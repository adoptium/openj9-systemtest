/*******************************************************************************
* Copyright (c) 2016, 2023 IBM Corp. and others
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License 2.0 which accompanies this distribution
* and is available at http://eclipse.org/legal/epl-2.0 or the Apache License, 
* Version 2.0 which accompanies this distribution and is available at 
* https://www.apache.org/licenses/LICENSE-2.0.
* 
* This Source Code may also be made available under the following Secondary
* Licenses when the conditions for such availability set forth in the 
* Eclipse Public License, v. 2.0 are satisfied: GNU General Public License,
* version 2 with the GNU Classpath Exception [1] and GNU General Public License,
* version 2 with the OpenJDK Assembly Exception [2].
* 
* [1] https://www.gnu.org/software/classpath/license.html
* [2] https://openjdk.org/legal/assembly-exception.html
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0 OR GPL-2.0 WITH Classpath-exception-2.0 OR LicenseRef-GPL-2.0 WITH Assembly-exception
*******************************************************************************/
package net.openj9.stf;

import net.adoptopenjdk.loadTest.InventoryData;
import net.adoptopenjdk.loadTest.TimeBasedLoadTest;
import net.adoptopenjdk.stf.StfException;
import net.adoptopenjdk.stf.extensions.core.StfCoreExtension;
import net.adoptopenjdk.stf.extensions.core.StfCoreExtension.Echo;
import net.adoptopenjdk.stf.processes.ExpectedOutcome;
import net.adoptopenjdk.stf.processes.definitions.JavaProcessDefinition;
import net.adoptopenjdk.stf.processes.definitions.LoadTestProcessDefinition;
import net.adoptopenjdk.stf.runner.modes.HelpTextGenerator;
import net.adoptopenjdk.stf.util.TimeParser;

/**
 * This is a test plugin for DAA (Data Access Accelerator) related tests.
 *  
 * DAA is a Math's library that comes with IBM's sdk, its aim is to boost
 * Math's performance, specially for z/OS based systems. 
 * This library is particularly useful to allow JIT optimisation on legacy 
 * programs such as COBOL that contain native data structures (e.g. Packed 
 * Decimal) that wouldn't otherwise be exploited by the JIT.
 * 
 */
public class DaaLoadTest extends TimeBasedLoadTest {
	// Define some parameters for the different workloads
	// The tests should take under 5 mins with no java options.
	// The timeout values are high because some java options (e.g. -Xint) can have a big performance impact.
	private enum Workloads {
		//Workload   Multiplier  Timeout  InventoryFile
		daa1( 		   20, 	      "30m", 	"daa1.xml"),
		daa2( 		   50,        "30m", 	"daa2.xml"),
		daa3( 		   150, 	  "30m", 	"daa3.xml"),
		daaAll( 	   15,   	  "30m", 	"daaAll.xml");	
	
		int multiplier;
		String timeout;
		String inventoryFile;
		
		private Workloads(int multiplier, String timeout, String inventoryFile) {
			this.multiplier 	= multiplier;
			this.timeout		= timeout;
			this.inventoryFile 	= inventoryFile;
		}
	}
	
	// This workload is calibrated for slow running load tests executed under special JIT modes such as -Xjit:count=0
	private enum WorkloadsSpecial {
		//Workload   Multiplier  Timeout  InventoryFile
		daa1 (  10,        "1h", 	"daa1.xml"),
		daa2 (  25,        "1h", 	"daa2.xml"),
		daa3 (  50,        "1h", 	"daa3.xml"),
		daaAll( 10,        "1h", 	"daaAll.xml");
		
		int multiplier;
		String timeout;
		String inventoryFile;
		
		private WorkloadsSpecial(int multiplier, String timeout, String inventoryFile) {
			this.multiplier 	= multiplier;
			this.timeout		= timeout;
			this.inventoryFile 	= inventoryFile;
		}
	}
	
	Workloads workload;
	WorkloadsSpecial workloadSpecial; 
	boolean specialTest = false; 
	
	public void help(HelpTextGenerator help) throws StfException {
		help.outputSection("DaaLoadTest test");
		help.outputText("DaaLoadTest runs DAA (Data Access Accelerator) related tests.");
		help.outputText("DAA is a Math's library that comes with IBM's sdk, its aim is to "
				+ "boost Math's performance, specially for z/OS based systems.");

		help.outputSection("DaaLoadTest test options");

		help.outputArgName("workload", "NAME");
		help.outputArgDesc("This is the name of the workload to run, it supports 5 workloads: "
				+ "daa1, daa2, daa3, daa4 and daaAll. "
				+ "This argument is optional, if not provided it will run all workloads.");
	}

	public void execute(StfCoreExtension test) throws StfException {
		
		String jvmOptionsInUse = test.getJavaArgs(test.env().primaryJvm()); 
		
		// If we are using JVM options that contains slow running JIT options, use reduced workload 
		if (jvmOptionsInUse.contains("-Xjit:count") ||
			jvmOptionsInUse.contains("Xjit:enableOSR")) {
			specialTest = true;
		}
		
		if(specialTest) {
			workloadSpecial = testArgs.decodeEnum("workload", WorkloadsSpecial.class);
		} else {
			workload = testArgs.decodeEnum("workload", Workloads.class);
		}
		
		// Abort if we are not running on IBM Java
		test.env().verifyUsingIBMJava();
		String inventory = null;
		int cpuCount = Runtime.getRuntime().availableProcessors();
		int multiplier = 1; 
		String timeout = null; 
		
		if(specialTest) {
			multiplier = workloadSpecial.multiplier;
			timeout = workloadSpecial.timeout; 
			inventory = "/openj9.test.load/config/inventories/daa/" + workloadSpecial.inventoryFile;
		} else {
			multiplier = workload.multiplier;
			timeout = workload.timeout; 
			inventory = "/openj9.test.load/config/inventories/daa/" + workload.inventoryFile;
		}
		
		int numDaaTests = InventoryData.getNumberOfTests(test, inventory);
		
		LoadTestProcessDefinition loadTestInvocation = test.createLoadTestSpecification()
				.addPrereqJarToClasspath(JavaProcessDefinition.JarId.JUNIT)
				.addPrereqJarToClasspath(JavaProcessDefinition.JarId.HAMCREST)
				.addProjectToClasspath("openj9.test.daa"); 
		
		if (isTimeBasedLoadTest) { 
			loadTestInvocation = loadTestInvocation.setTimeLimit(timeLimit);	// If it's a time based test, stop execution after given time duration
		}
			
		loadTestInvocation = loadTestInvocation.generateCoreDumpAtFirstLoadTestFailure(false)
				.addSuite("daa")
				.setSuiteThreadCount(cpuCount - 2, 2);
		
		if (!isTimeBasedLoadTest) { 
			loadTestInvocation = loadTestInvocation.setSuiteNumTests(numDaaTests * multiplier);
		}
		
		finalTimeout = timeout; 
		
		if (isTimeBasedLoadTest) { 
			long finalTimeoutInSeconds = TimeParser.parseTimeSpecification(timeout).getSeconds() + 
				TimeParser.parseTimeSpecification(timeLimit).getSeconds();
			finalTimeout = finalTimeoutInSeconds + "s";
		}
		
		loadTestInvocation = loadTestInvocation.setSuiteInventory(inventory)
				.setSuiteRandomSelection();
		
		test.doRunForegroundProcess("Run daa load test", "DLT", Echo.ECHO_ON,
				ExpectedOutcome.cleanRun().within(finalTimeout), 
				loadTestInvocation);
	}
}
