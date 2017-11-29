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

import net.adoptopenjdk.stf.StfException;
import net.adoptopenjdk.stf.extensions.core.StfCoreExtension;
import net.adoptopenjdk.stf.extensions.core.StfCoreExtension.Echo;
import net.adoptopenjdk.stf.plugin.interfaces.StfPluginInterface;
import net.adoptopenjdk.stf.processes.ExpectedOutcome;
import net.adoptopenjdk.stf.processes.definitions.JavaProcessDefinition;
import net.adoptopenjdk.stf.processes.definitions.LoadTestProcessDefinition;
import net.adoptopenjdk.stf.processes.definitions.SystemProcessDefinition;
import net.adoptopenjdk.stf.runner.modes.HelpTextGenerator;

/**
 * This is a test plugin for Java.util related tests, it runs a workload of 
 * test cases from the test.util project.
 */
public class IdleLoadTest implements StfPluginInterface {
	public void help(HelpTextGenerator help) throws StfException {
		help.outputSection("IdleLoadTest runs a workload of Java.util related tests.");
		help.outputText("");
	}

	public void pluginInit(StfCoreExtension test) throws StfException {
	}

	public void setUp(StfCoreExtension test) throws StfException {
	}

	public void execute(StfCoreExtension test) throws StfException {
		
		test.env().verifyUsingIBMJava();
		
		String inventoryFile = "/openj9.test.load/config/inventories/idle/idleTest.xml";
		int threadCount = 3;
		int numOfIterations = 12;
		int cpuCount = Runtime.getRuntime().availableProcessors();
		
		LoadTestProcessDefinition loadTestInvocation = test.createLoadTestSpecification()
				.addPrereqJarToClasspath(JavaProcessDefinition.JarId.JUNIT)
				.addPrereqJarToClasspath(JavaProcessDefinition.JarId.HAMCREST)
				.addProjectToClasspath("openj9.test.idle")
				.addSuite("idle")
				.setSuiteThreadCount(threadCount)
				.setSuiteNumTests(threadCount * numOfIterations)
				.setSuiteInventory(inventoryFile)
				.setSuiteSequentialSelection();
		
		// Copying the images folder to the results dir
		test.doCpDir("Copy pics to results dir", test.env().findTestDirectory("openj9.test.idle").childDirectory("src/test.idle/net/openj9/test/images"), test.env().getResultsDir().childDirectory("images"));

		test.doRunForegroundProcess("Run idle load test", "ILT", Echo.ECHO_ON,
				ExpectedOutcome.cleanRun().within("1h30m"), 
				loadTestInvocation);
				
		if ( test.getJavaArgs(test.env().primaryJvm()).contains("-verbose:gc") || test.env().getResultsDir().childFile("gc.verbose").asJavaFile().exists() ) {
			test.doCountFileMatches("Looking for string release free pages in gc logs", test.env().getResultsDir().childFile("gc.verbose"), numOfIterations, "type=\"release free pages\"");
		}
	}

	public void tearDown(StfCoreExtension test) throws StfException {
	}
}
