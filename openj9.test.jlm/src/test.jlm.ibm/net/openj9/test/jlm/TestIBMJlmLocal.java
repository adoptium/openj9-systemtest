/*******************************************************************************
* Copyright (c) 2017, 2023 IBM Corp. and others
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

package net.openj9.test.jlm;

import static net.adoptopenjdk.stf.extensions.core.StfCoreExtension.Echo.ECHO_ON;

import net.adoptopenjdk.stf.StfException;
import net.adoptopenjdk.stf.extensions.core.StfCoreExtension;
import net.adoptopenjdk.stf.plugin.interfaces.StfPluginInterface;
import net.adoptopenjdk.stf.processes.ExpectedOutcome;
import net.adoptopenjdk.stf.processes.definitions.JavaProcessDefinition;
import net.adoptopenjdk.stf.processes.definitions.JavaProcessDefinition.JarId;
import net.adoptopenjdk.stf.runner.modes.HelpTextGenerator;
import net.openj9.test.jlm.local.VMLogger;

/**
 * STF automation to drive a java.lang.management system test scenario 
 * where a JVM monitors itself via its platform MBeans using a local connection.
 */
public class TestIBMJlmLocal implements StfPluginInterface {
	
	public void help(HelpTextGenerator help) {
		help.outputSection("IBM JLM Local test");
		help.outputText("These tests exercise com.ibm.lang.management APIs "
				+ "with MBeans for the local JVM");
	}

	public void pluginInit(StfCoreExtension test) throws StfException {
	}

	public void setUp(StfCoreExtension test) throws StfException {
	}

	public void execute(StfCoreExtension test) throws StfException {

		// Check to make sure we are using an OpenJ9 SDK
		test.env().verifyUsingIBMJava();
		
		JavaProcessDefinition localTestProcessDef = test.createJavaProcessDefinition()
			.addProjectToClasspath("openjdk.test.jlm")
			.addProjectToClasspath("openj9.test.jlm")
			.addPrereqJarToClasspath(JarId.JUNIT)
			.runClass(VMLogger.class)
			.addArg("jlmtestlocal.log");
		
		// Start a synchronous JVM process to drive the local JLM test 
		test.doRunForegroundProcess("Run JLM IBM Local test", "TST", 
				ECHO_ON, ExpectedOutcome.cleanRun().within("5m"),
				localTestProcessDef);
	}
	
	public void tearDown(StfCoreExtension test) throws StfException {
	}
}
