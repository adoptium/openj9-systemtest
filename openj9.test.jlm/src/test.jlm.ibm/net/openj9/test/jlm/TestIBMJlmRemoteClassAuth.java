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

import static net.adoptopenjdk.stf.extensions.core.StfCoreExtension.Echo.ECHO_OFF;
import static net.adoptopenjdk.stf.extensions.core.StfCoreExtension.Echo.ECHO_ON;

import net.adoptopenjdk.stf.StfException;
import net.adoptopenjdk.stf.environment.DirectoryRef;
import net.adoptopenjdk.stf.environment.FileRef;
import net.adoptopenjdk.stf.extensions.core.StfCoreExtension;
import net.adoptopenjdk.stf.plugin.interfaces.StfPluginInterface;
import net.adoptopenjdk.stf.processes.ExpectedOutcome;
import net.adoptopenjdk.stf.processes.StfProcess;
import net.adoptopenjdk.stf.processes.definitions.JavaProcessDefinition;
import net.adoptopenjdk.stf.processes.definitions.JavaProcessDefinition.JarId;
import net.adoptopenjdk.stf.processes.definitions.LoadTestProcessDefinition;
import net.adoptopenjdk.stf.runner.modes.HelpTextGenerator;
import net.openj9.test.jlm.remote.ClassProfiler;

/**
 * STF automation to drive a java.lang.management system test scenario for class 
 * related beans where a 'client' JVM monitors a remote 'server' JVM via the server's 
 * platform MBeans with both password file based security and ssl based security enabled. 
 * 
 * The test is divided into two parts. The first part drives the remote class  
 * test configuration for secure proxy connections. The second part drives the 
 * test configuration for secure server connections.   
 */
public class TestIBMJlmRemoteClassAuth implements StfPluginInterface {
	
	FileRef keyStoreFile;
	FileRef passwordFile;
	
	public void help(HelpTextGenerator help) throws StfException {
		help.outputSection("IBM JLM Remote class test with security");
		help.outputText("These tests exercise com.ibm.lang.management APIs with (for class related beans) "
				+ "MBeans for a remote JVM using secure proxy and server connections");
	}

	public void pluginInit(StfCoreExtension test) throws StfException {
	}

	public void setUp(StfCoreExtension test) throws StfException {
		keyStoreFile = test.locateResourceFile("testkeys");
		passwordFile = test.doCp("Copying password file to temp", 
				test.locateResourceFile("jmxremote.password"), test.env().getTmpDir());
		
		// We need to set file permission of jmxremote.password to 600 as this is 
		// required by the password file based JMX remote monitoring mechanism.   
		// Since the password file contains passwords in clear text, for security reasons, 
		// the JMX implementation checks that the file is only readable by the owner and exits 
		// with an error if it is not. 
		test.doChmod("Restricting file permission of jmxremote.passsword to 600", passwordFile, "600");
	}

	public void execute(StfCoreExtension test) throws StfException {
		
		// Check to make sure we are using an OpenJ9 SDK
		test.env().verifyUsingIBMJava();
		
		/****************
		 *  Part 1) Drive the test configuration for secure proxy connection 
		 *****************/
		
		// Process definition for the monitored server JVM
		String inventoryFile = "/openjdk.test.load/config/inventories/mix/mini-mix.xml";
		
		// setSuiteNumTests and setSuiteThreadCount need to be big enough to ensure the workload does not
		// end within the setTimeLimit time.
		LoadTestProcessDefinition serverLoadTestInvocation = test.createLoadTestSpecification()
			.addJvmOption("-Dcom.sun.management.jmxremote.port=1234")
			.addJvmOption("-Dcom.sun.management.jmxremote.ssl.need.client.auth=true")
			.addJvmOption("-Djavax.net.ssl.keyStore=" + keyStoreFile.getSpec())
			.addJvmOption("-Djavax.net.ssl.trustStore=" + keyStoreFile.getSpec())
			.addJvmOption("-Djavax.net.ssl.keyStoreType=JKS")
			.addJvmOption("-Djavax.net.ssl.trustStoreType=JKS")
			.addJvmOption("-Djavax.net.ssl.keyStorePassword=passphrase")
			.addJvmOption("-Djavax.net.ssl.trustStorePassword=passphrase")
			.addJvmOption("-Dcom.sun.management.jmxremote.password.file=" + passwordFile.getSpec())
			.addPrereqJarToClasspath(JavaProcessDefinition.JarId.JUNIT)
			.addPrereqJarToClasspath(JavaProcessDefinition.JarId.HAMCREST)
			.addProjectToClasspath("openjdk.test.lang")  // For mini-mix inventory
			.addProjectToClasspath("openjdk.test.util")  // For mini-mix inventory
			.addProjectToClasspath("openjdk.test.math")  // For mini-mix inventory
			.setTimeLimit("30m")
			.setAbortAtFailureLimit(-1)
			.addSuite("mini-mix")
			.setSuiteNumTests(20000000)
			.setSuiteInventory(inventoryFile)
			.setSuiteThreadCount(30)
		   	.setSuiteRandomSelection();
		
		// Process definition for the client JVM that will monitor the server using proxy connection
		DirectoryRef resultsDir = test.env().getResultsDir();
		FileRef logFile	= resultsDir.childFile("scls_proxy.log");
		FileRef statsFile = resultsDir.childFile("scls_proxy.csv");
		FileRef dumpFile = resultsDir.childFile("javacore_scls_proxy.%Y%m%d.%H%M%S.%pid.%seq.txt,filter=java.lang.IllegalArgumentException");
		
		JavaProcessDefinition clientJavaInvocationProxy =  test.createJavaProcessDefinition()
			.addJvmOptionIfIBMJava("-Xdump:java:events=throw,file=" + dumpFile.getSpec())
	 		.addJvmOption("-Djavax.net.ssl.keyStore=" + keyStoreFile.getSpec())
			.addJvmOption("-Djavax.net.ssl.trustStore=" + keyStoreFile.getSpec())
			.addJvmOption("-Djavax.net.ssl.keyStoreType=JKS")
			.addJvmOption("-Djavax.net.ssl.trustStoreType=JKS")
			.addJvmOption("-Djavax.net.ssl.keyStorePassword=passphrase")
			.addJvmOption("-Djavax.net.ssl.trustStorePassword=passphrase")
			.addJvmOption("-Dcom.sun.management.jmxremote.password.file=" + passwordFile.getSpec())
			.addProjectToClasspath("openj9.test.jlm")
			.addProjectToClasspath("openjdk.test.jlm")
			.addPrereqJarToClasspath(JarId.JUNIT)
			.runClass(ClassProfiler.class)
			.addArg("proxy")
			.addArg(logFile.getSpec())
			.addArg(statsFile.getSpec())
			.addArg("auth")
			.addArg("controlRole")
			.addArg("control1")
			.addArg("localhost")
			.addArg("1234");
		
		// Start the background server process
		StfProcess serverProxy = test.doRunBackgroundProcess("Running ClassProfiler Proxy "
				+ "test Server Process(with security)", "LT1", ECHO_OFF, 
				ExpectedOutcome.neverCompletes(), serverLoadTestInvocation);
		
		// Start the background client process
		StfProcess clientProxy = test.doRunBackgroundProcess("Running Monitoring "
				+ "Client with proxy connection(with security)", 
				"CL1", ECHO_ON,  
				ExpectedOutcome.cleanRun().within("10m"), 
				clientJavaInvocationProxy);
		
		// Wait for the processes to complete
		test.doMonitorProcesses("Wait for the processes to complete", serverProxy, clientProxy);
		test.doKillProcesses("Stop LT1 process", serverProxy);
		
		/****************
		 *  Part 2) Drive the test configuration for secure server connection 
		 *****************/
		
		// Process definition for the client JVM that connects with the server via server-connection
		logFile	= resultsDir.childFile("scls_server.log");
		statsFile = resultsDir.childFile("scls_server.csv");
		dumpFile = resultsDir.childFile("javacore_scls_server.%Y%m%d.%H%M%S.%pid.%seq.txt,filter=java.lang.IllegalArgumentException");
		JavaProcessDefinition clientJavaInvocationS = test.createJavaProcessDefinition()
			.addJvmOptionIfIBMJava("-Xdump:java:events=throw,file=" + dumpFile.getSpec())
			.addJvmOption("-Djavax.net.ssl.keyStore=" + keyStoreFile.getSpec())
			.addJvmOption("-Djavax.net.ssl.trustStore=" + keyStoreFile.getSpec())
			.addJvmOption("-Djavax.net.ssl.keyStoreType=JKS")
			.addJvmOption("-Djavax.net.ssl.trustStoreType=JKS")
			.addJvmOption("-Djavax.net.ssl.keyStorePassword=passphrase")
			.addJvmOption("-Djavax.net.ssl.trustStorePassword=passphrase")
			.addJvmOption("-Dcom.sun.management.jmxremote.password.file=" + passwordFile.getSpec())
			.addProjectToClasspath("openj9.test.jlm")
			.addProjectToClasspath("openjdk.test.jlm")
			.addPrereqJarToClasspath(JarId.JUNIT)
			.runClass(ClassProfiler.class)
			.addArg("server")
			.addArg(logFile.getSpec())
			.addArg(statsFile.getSpec())
			.addArg("auth")
			.addArg("controlRole")
			.addArg("control1")
			.addArg("localhost")
			.addArg("1234");

		// Start the server process
		StfProcess serverS= test.doRunBackgroundProcess("Running ClassProfiler Server test Server Process(with security)", "LT2", ECHO_OFF, 
				ExpectedOutcome.neverCompletes(), serverLoadTestInvocation);
		
		// Start the background client process
		StfProcess clientS = test.doRunBackgroundProcess("Run the Monitoring Client with server-connection(with security)", 
				"CL2", ECHO_ON,  
				ExpectedOutcome.cleanRun().within("10m"), 
				clientJavaInvocationS);
		
		// Wait for background processes to complete 
		test.doMonitorProcesses("Wait for processes to complete", serverS, clientS);
		test.doKillProcesses("Stop LT2 process", serverS);
	}

	public void tearDown(StfCoreExtension test) throws StfException {
	}
}
