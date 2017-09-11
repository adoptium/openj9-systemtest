/*******************************************************************************
* Copyright (c) 2017 IBM Corp.
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
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*******************************************************************************/
package net.openj9.stf;

import net.adoptopenjdk.loadTest.InventoryData;
import net.adoptopenjdk.stf.StfException;
import net.adoptopenjdk.stf.codeGeneration.Stage;
import net.adoptopenjdk.stf.extensions.core.StfCoreExtension;
import net.adoptopenjdk.stf.extensions.core.StfCoreExtension.Echo;
import net.adoptopenjdk.stf.plugin.interfaces.StfPluginInterface;
import net.adoptopenjdk.stf.processes.ExpectedOutcome;
import net.adoptopenjdk.stf.processes.definitions.JavaProcessDefinition;
import net.adoptopenjdk.stf.processes.definitions.LoadTestProcessDefinition;
import net.adoptopenjdk.stf.runner.modes.HelpTextGenerator;

/**
 * This is a test plugin that runs the ObjectTree2 test class along side a daa workload.
 * 
 * ObjectTree2 has been designed to fill the heap but leave a specified space, rather than 
 * fill a specified space. Should allow the heap to be quickly filled to allow the testing
 * of large heaps.
 */
public class HeapHogLoadTest implements StfPluginInterface {	
	public void help(HelpTextGenerator help) throws StfException {
		String testName = HeapHogLoadTest.class.getSimpleName();
		
		help.outputSection(testName + " test");
		help.outputText(testName + " runs the ObjectTree2 test class along side a daa workload.");
		help.outputText("This test is primarily aimed at stressing the Garbage Collector.");
	}

	public void pluginInit(StfCoreExtension test) throws StfException {
	}

	public void setUp(StfCoreExtension test) throws Exception {
		// Ensure we are running on IBM Java because this test is 
		// expected to be invoked with IBM specific JVM options
		test.env().verifyUsingIBMJava();
		
		// Ensure the test is invoked with the expected JVM option
		test.verifyJavaArgsContains(Stage.EXECUTE, "-Xdisableexcessivegc");
	}

	public void execute(StfCoreExtension test) throws StfException {
		String inventory = "/openj9.test.load/config/inventories/gc/heapHog.xml";
		int numTests = InventoryData.getNumberOfTests(test, inventory);
		int cpuCount = Runtime.getRuntime().availableProcessors();
		
		LoadTestProcessDefinition loadTestInvocation = test.createLoadTestSpecification()
				.addPrereqJarToClasspath(JavaProcessDefinition.JarId.JUNIT)
				.addPrereqJarToClasspath(JavaProcessDefinition.JarId.HAMCREST)
				.addProjectToClasspath("openjdk.test.gc")
				.addProjectToClasspath("openjdk.test.lang")    // For mini-mix inventory
				.addProjectToClasspath("openjdk.test.util")    // For mini-mix inventory
				.addProjectToClasspath("openjdk.test.math")    // For mini-mix inventory
				.addSuite("HeapHog")
				.setSuiteThreadCount(cpuCount - 2, 2)   // Leave 1 CPU for the JIT, one for GC, but never less then two threads on machines with one or two CPUs 
				.setSuiteNumTests(numTests * 150)   // About 5 minutes run time with no -X options
				.setSuiteInventory(inventory)
				.setSuiteRandomSelection();
		
		test.doRunForegroundProcess("Run heap hog load test", "HHLT", Echo.ECHO_ON,
				ExpectedOutcome.cleanRun().within("1h"), 
				loadTestInvocation);
	}

	public void tearDown(StfCoreExtension test) throws StfException {
	}
}
