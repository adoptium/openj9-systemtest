/*******************************************************************************
* Copyright (c) 2016, 2021 IBM Corp. and others
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
* [2] http://openjdk.java.net/legal/assembly-exception.html
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0 OR GPL-2.0 WITH Classpath-exception-2.0 OR LicenseRef-GPL-2.0 WITH Assembly-exception
*******************************************************************************/
package net.openj9.stf;

import java.util.ArrayList;

import net.adoptopenjdk.loadTest.InventoryData;
import net.adoptopenjdk.loadTest.TimeBasedLoadTest;
import net.adoptopenjdk.stf.StfException;
import net.adoptopenjdk.stf.environment.DirectoryRef;
import net.adoptopenjdk.stf.extensions.core.StfCoreExtension;
import net.adoptopenjdk.stf.extensions.core.StfCoreExtension.Echo;
import net.adoptopenjdk.stf.processes.ExpectedOutcome;
import net.adoptopenjdk.stf.processes.definitions.JavaProcessDefinition;
import net.adoptopenjdk.stf.processes.definitions.LoadTestProcessDefinition;
import net.adoptopenjdk.stf.runner.modes.HelpTextGenerator;
import net.adoptopenjdk.stf.util.TimeParser;

/**
 * This is a test plugin for ABBS (combination of DAA, ClassLoading, lambda, Math) tests.
 * 
 */
public class ABBSLoadTest extends TimeBasedLoadTest {
	// Define some parameters for the different workloads
	// The tests should take under 5 mins with no java options.
	// The timeout values are high because some java options (e.g. -Xint) can have a big performance impact.
	
	public void help(HelpTextGenerator help) throws StfException {
		help.outputSection("ABBS test");
		help.outputText("ABBSLoadTest runs a combination of DAA, ClassLoading, lambda, Math tests.");
	}

	public void execute(StfCoreExtension test) throws StfException {
		String inventory = "/openj9.test.load/config/inventories/abbs/abbs.xml";
		ArrayList<DirectoryRef> testRoots = test.env().getTestRoots();
		DirectoryRef notonclasspathDirRoot = DirectoryRef.findDirectoryRoot
		("openjdk.test.classloading/bin_notonclasspath/url1/net/adoptopenjdk/test/classloading/deadlock/package1/", testRoots);
		int numTests = InventoryData.getNumberOfTests(test, inventory);
		int cpuCount = Runtime.getRuntime().availableProcessors();
		int javaVersion = test.env().primaryJvm().getJavaVersion();
		
		String modulesAdd = "";
		if (javaVersion >= 9) {
			modulesAdd = "java.rmi";
			if (javaVersion < 11) {
				modulesAdd += ",java.transaction,java.corba";
			}
		}
		
		LoadTestProcessDefinition loadTestInvocation = test.createLoadTestSpecification()
				.addJvmOption("-Xss3192K") // TestLambdaRecursive needs a large stack 
				.addJvmOption("-Djava.classloading.dir=" + notonclasspathDirRoot)  // Expose the bin_notonclasspath root directory to the deadlock test
				.addJvmOption("-Djava.version.number=" + javaVersion)
				.addModules(modulesAdd)
				.addPrereqJarToClasspath(JavaProcessDefinition.JarId.JUNIT)
				.addPrereqJarToClasspath(JavaProcessDefinition.JarId.HAMCREST)
				.addProjectToClasspath("openjdk.test.classloading") 
				.addProjectToClasspath("openj9.test.daa")
				.addProjectToClasspath("openjdk.test.math")
				.addProjectToClasspath("openjdk.test.lambdasAndStreams");
		
		if (isTimeBasedLoadTest) { 
			loadTestInvocation = loadTestInvocation.setTimeLimit(timeLimit);	// If it's a time based test, stop execution after given time duration
		}	
		
		loadTestInvocation = loadTestInvocation.generateCoreDumpAtFirstLoadTestFailure(false)
				.addSuite("ABBS")
				.setSuiteThreadCount(cpuCount - 2, 2);   // Leave 1 CPU for the JIT, one for GC, but never less then two threads on machines with one or two CPUs 
		
		if (!isTimeBasedLoadTest) { 
			loadTestInvocation = loadTestInvocation.setSuiteNumTests(numTests * 500);   // About 5 minutes run time with no -X options
		}
				
		loadTestInvocation = loadTestInvocation.setSuiteInventory(inventory)
				.setSuiteRandomSelection();
		
		test.doRunForegroundProcess("Run ABBS load test", "ABBS", Echo.ECHO_ON,
				ExpectedOutcome.cleanRun().within(finalTimeout), 
				loadTestInvocation);
	}
}
