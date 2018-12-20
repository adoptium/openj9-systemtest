/*******************************************************************************
* Copyright (c) 2017, 2018 IBM Corp.
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
 * This is a test plugin that runs the ObjectTree test class along side a Mauve workload.
 * 
 * The ObjectTree test class creates a large object tree and fills up the memory over time.
 */
public class ObjectTreeLoadTest implements StfPluginInterface {	
	public void help(HelpTextGenerator help) throws StfException {
		String testName = ObjectTreeLoadTest.class.getSimpleName();
		
		help.outputSection(testName + " test");
		help.outputText(testName + " runs the ObjectTree test class along side a workload.");
		help.outputText("This test is primarily aimed at stressing the Garbage Collector.");
	}

	public void pluginInit(StfCoreExtension test) throws StfException {
	}

	public void setUp(StfCoreExtension test) throws Exception {
		// Ensure we are running on IBM Java because this test is 
		// expected to be invoked with IBM specific JVM options
		test.env().verifyUsingIBMJava();
		
		// Ensure the test is invoked with the expected JVM option
		test.verifyJavaArgsContains(Stage.EXECUTE, "-Xnoclassgc");
	}

	public void execute(StfCoreExtension test) throws StfException {
		String inventory = "/openj9.test.load/config/inventories/gc/objectTree.xml";
		int numTests = InventoryData.getNumberOfTests(test, inventory);
		int cpuCount = Runtime.getRuntime().availableProcessors();
		
		LoadTestProcessDefinition loadTestInvocation = test.createLoadTestSpecification()
				.addPrereqJarToClasspath(JavaProcessDefinition.JarId.JUNIT)
				.addPrereqJarToClasspath(JavaProcessDefinition.JarId.HAMCREST)
				.addProjectToClasspath("openjdk.test.gc")
				.addProjectToClasspath("openjdk.test.lang")    // For mini-mix inventory
				.addProjectToClasspath("openjdk.test.util")    // For mini-mix inventory
				.addProjectToClasspath("openjdk.test.math")    // For mini-mix inventory
				.generateCoreDumpAtFirstLoadTestFailure(true)
				.addSuite("ObjectTree")
				.setSuiteThreadCount(cpuCount - 2, 2)   // Leave 1 CPU for the JIT, one for GC, but never less then two threads on machines with one or two CPUs 
				.setSuiteNumTests(numTests * 150)   // About 5 minutes run time with no -X options
				.setSuiteInventory(inventory)
				.setSuiteRandomSelection();
		
		test.doRunForegroundProcess("Run Object Tree load test", "OLT", Echo.ECHO_ON,
				ExpectedOutcome.cleanRun().within("2h"), 
				loadTestInvocation);
	}

	public void tearDown(StfCoreExtension test) throws StfException {
	}
}
