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

package net.openj9.stf.sharedClasses;

import static net.adoptopenjdk.stf.extensions.core.StfCoreExtension.Echo.ECHO_ON;

import net.adoptopenjdk.stf.StfException;
import net.adoptopenjdk.stf.codeGeneration.PerlCodeGenerator;
import net.adoptopenjdk.stf.environment.StfEnvironmentCore;
import net.adoptopenjdk.stf.environment.properties.Argument;
import net.adoptopenjdk.stf.extensions.StfExtensionBase;
import net.adoptopenjdk.stf.extensions.interfaces.StfExtension;
import net.adoptopenjdk.stf.processes.ExpectedOutcome;
import net.adoptopenjdk.stf.processes.StfProcess;
import net.adoptopenjdk.stf.processes.definitions.JavaProcessDefinition;
import net.adoptopenjdk.stf.runner.modes.HelpTextGenerator;
import net.adoptopenjdk.stf.util.StfDuration;

import java.util.Arrays;


/** 
 * This is a Shared Classes specific extension of STF that provides 
 * Shared Classes specific methods.
 */
public class StfSharedClassesExtension implements StfExtension {
    private StfEnvironmentCore environmentCore;
    private StfExtensionBase extensionBase;
    private PerlCodeGenerator generator;

    
	@Override
	public Argument[] getSupportedArguments() {
		return null;
	}
	

	@Override
	public void help(HelpTextGenerator help) {
	}

	
	public void initialise(StfEnvironmentCore environmentCore, StfExtensionBase extensionBase, PerlCodeGenerator generator) throws StfException {
		this.environmentCore = environmentCore;
		this.extensionBase = extensionBase;
		this.generator = generator;	
	}


	/*
	 * This method runs a shared classes command and generates an if statement to
	 * scan the output for an expected message if the return code is not 0. 
	 * It does this because several shared classes commands always returns code 1.
	 */
	private void runSharedClassesCacheCommand(String comment, StfDuration duration, String[] expectedMessages, String... scOptions) throws StfException {
		// Build the command to be run
		JavaProcessDefinition javaDefinition = new JavaProcessDefinition(environmentCore)
				.addJvmOption(scOptions)
				.runClass("");
		
		StfProcess process = extensionBase.runForegroundProcess(comment, "SCC", ECHO_ON, ExpectedOutcome.exitValue(0,1).within("5m"), javaDefinition);

		// Verify that expected message has been written to stderr
		generator.outputEmptyLine();
		generator.outputLine("# Scan the stderr of the shared caches command to");
		generator.outputLine("# ensure the right message is printed out");
		extensionBase.outputCountFileMatches("$cache_message_count", process.getStderrFileRef(), expectedMessages);
		
		// Check the result of the scan
		extensionBase.outputFailIfTrue("java", comment, "$cache_message_count", "==", 0);
	}
	
	
	/**
	 * This method sets the cacheOperation property and resolves the variables in the shared classes options
	 * mode.
	 * 
	 * @param scOptions are the shared classes options defined in a shared classes mode.
	 * @param cacheName is the name to be used in scOptions for '${cacheName}'.
	 * @param cacheDir is the cache location, to be used in scOptions for '${cacheDir}'.
	 * @param cacheOperation is the shared classes sub-option that drives the operation, eg printStats, reset, destroy, etc. 
	 * Replaces the '${cacheOperation}' in scOptions. 
	 * @throws StfException if anything goes wrong.
	 */
	public String[] resolveSharedClassesOptions(String scOptions, String cacheName, String cacheDir, String cacheOperation) throws StfException {
		String scModeArguments = scOptions
				.replace("${cacheName}", cacheName)
				.replace("${cacheDir}", cacheDir)
				.replace("${cacheOperation}", cacheOperation);
			
		String[] scModeArray = scModeArguments.split(" ");

		return scModeArray;
	}
	
	
	/**
	 * Destroys a test-specific persistent shared classes cache and checks the output 
	 * for "shared cache <cacheName> has been destroyed" if the return code is not 0.
	 * It does this validation because the cache is expected to be destroyed and
	 * the destroy shared classes command always returns code 1.
	 *  
	 * @param comment is a comment from the test summarising why it's running a child process.
	 * @param scOptions are the shared classes options defined in a shared classes mode.
	 * @param cacheName is the name of the cache.
	 * @param cacheDir is the directory holding the classes cache.
	 * @throws StfException if anything goes wrong.
	 */
	public void doDestroySpecificCache(String comment, String scOptions, String cacheName, String cacheDir) throws StfException {
		String cacheOperation =  ",destroyAll";
		
		generator.startNewCommand(comment, "java", "Destroy specific shared classes cache",
						"Options:", scOptions,
						"CacheName:", cacheName,
						"CacheDir:", cacheDir,
						"CacheOperation:", cacheOperation); 

		String[] expectedMessages = {"shared cache \"" + cacheName + "\" has been destroyed", "cache \"" + cacheName + "\" is destroyed"};
		
		runSharedClassesCacheCommand(comment, StfDuration.ofMinutes(1), expectedMessages, resolveSharedClassesOptions(scOptions, cacheName, cacheDir, cacheOperation));
	}
	
	
	/**
	 * Destroys a test-specific non-persistent shared classes cache and checks the output 
	 * for "shared cache <cacheName> has been destroyed" if the return code is not 0.
	 * It does this validation because the cache is expected to be destroyed and
	 * the destroy shared classes command always returns code 1.
	 * 
	 * @param comment is a comment from the test summarising why it's running a child process.
	 * @param scOptions are the shared classes options defined in a shared classes mode.
	 * @param cacheName is the name of the cache.
	 * @param cacheDir is the directory holding the classes cache.
	 * @throws StfException if anything goes wrong.
	 */
	public void doDestroySpecificNonPersistentCache(String comment, String scOptions, String cacheName, String cacheDir) throws StfException {
		String cacheOperation =  ",destroyAll,nonpersistent";
		
		generator.startNewCommand(comment, "java", "Destroy specific non-persistent shared classes cache",
				"Options:", scOptions,
				"CacheName:", cacheName,
				"CacheDir:", cacheDir,
				"CacheOperation:", cacheOperation); 
		
		String[] expectedMessages = {"shared cache \"" + cacheName + "\" has been destroyed", "cache \"" + cacheName + "\" is destroyed"};		
		
		runSharedClassesCacheCommand(comment, StfDuration.ofMinutes(1), expectedMessages, resolveSharedClassesOptions(scOptions, cacheName, cacheDir, cacheOperation));
	}
	
	
	/**
	 * Destroys all the persistent shared classes caches from the default shared classes 
	 * cache location, and checks the output for "No shared class caches available" 
	 * or "shared cache (.*) has been destroyed" if the return code is not 0.
	 * It does this validation because it is unknown if a cache exists and the destroy 
	 * shared classes command always returns code 1.
	 * 
	 * @param comment is a comment from the test summarising why it's running a child process.
	 * @throws StfException if anything goes wrong.
	 */
	public void doDestroyAllPersistentCaches(String comment) throws StfException {
		generator.startNewCommand(comment, "java", "Destroy all persistent caches");

		String defaultJavaOptions =  "-Xshareclasses:destroyAll";
		String[] expectedMessages = {"No shared class caches available", "Cache does not exist", "shared cache (.*) has been destroyed", "cache (.*) is destroyed"};
		
		runSharedClassesCacheCommand(comment, StfDuration.ofMinutes(1), expectedMessages, defaultJavaOptions);
	}


	/**
	 * Destroys all the non-persistent shared classes caches from the default shared classes 
	 * cache location, and checks the output for "No shared class caches available" 
	 * or "shared cache (.*) has been destroyed" if the return code is not 0.
	 * It does this validation because it is unknown if a cache exists and the destroy 
	 * shared classes command always returns code 1.
	 * 
	 * @param comment is a comment from the test summarising why it's running a child process.
	 * @throws StfException if anything goes wrong.
	 */
	public void doDestroyAllNonPersistentCaches(String comment) throws StfException {
		generator.startNewCommand(comment, "java", "Destroy all non-persistent caches");

		String defaultJavaOptions = "-Xshareclasses:destroyAll,nonpersistent";
		String[] expectedMessages = {"No shared class caches available", "Cache does not exist", "shared cache (.*) has been destroyed", "cache (.*) is destroyed"};

		runSharedClassesCacheCommand(comment, StfDuration.ofMinutes(1), expectedMessages, defaultJavaOptions);
	}
	
	
	/**
	 * Prints the Shared Classes cache status and checks the output for "Cache is 100% full"
	 * if the return code is not 0. 	 
	 * It does this validation because the shared classes cache is expected to get full and 
	 * the print shared classes command always returns code 1.
	 * 
	 * @param comment is a comment from the test summarizing why it's running a child process.
	 * @param scOptions are the shared classes options defined in a shared classes mode.
	 * @param cacheName is the name of the cache.
	 * @param cacheDir is the directory holding the classes cache.
	 * @param cacheDir is the directory holding the classes cache.
	 * @param expectedMessages is the set of messages to check for in the output 
	 */
	public void doPrintAndVerifyCache(String comment, String scOptions, String cacheName, String cacheDir, String[] expectedMessages) throws StfException {
		String cacheOperation =  ",printStats";
		
		generator.startNewCommand(comment, "java", "Print shared classes cache stats", 
					"Options:", scOptions,
					"CacheName:", cacheName,
					"CacheDir:", cacheDir,
					"CacheOperation:", cacheOperation,
					"ExpectedMessages:", Arrays.toString(expectedMessages)); 
		
		runSharedClassesCacheCommand(comment, StfDuration.ofMinutes(1), expectedMessages, resolveSharedClassesOptions(scOptions, cacheName, cacheDir, cacheOperation));		
	}
	
	
	/**
	 * Lists the Shared Classes caches and verifies that the expected cache is listed. It also, checks for 
	 * an expected number of caches.
	 * 
	 * @param comment is a comment from the test summarising why it's running a child process.
	 * @param scOptions are the shared classes options.
	 * @param cacheName is the name of the cache.
	 * @param cacheDir is the directory holding the classes cache.
	 * @param expectedCacheName is the name of the cache it checks for it's existence.
	 * @param noOfExpectedCaches is the number of expected caches.
	 * @throws StfException if anything goes wrong.
	 */
	public void doVerifySharedClassesCache(String comment, String scOptions, String cacheName, String cacheDir, String expectedCacheName, int noOfExpectedCaches) throws StfException {
		String cacheOperation =  scOptions.contains(":") ? ",listAllCaches" : ":listAllCaches";

		generator.startNewCommand(comment, "java", "Verify contents of shared classes cache",
						"Options:", scOptions,
						"CacheName:", cacheName,
						"CacheDir:", cacheDir,
						"CacheOperation:", cacheOperation,
						"ExpectedCacheName:", expectedCacheName,
						"ExpectedNum:", Integer.toString(noOfExpectedCaches));
		
		
		String cacheRegex  = "Java[" + environmentCore.primaryJvm().getJavaVersion() + "]"; // This is a common pattern for all caches
		
		// Build the command to be run
		JavaProcessDefinition javaDefinition = new JavaProcessDefinition(environmentCore)
				.addJvmOption(resolveSharedClassesOptions(scOptions, cacheName, cacheDir, cacheOperation))
				.runClass("");
		
		StfProcess process = extensionBase.runForegroundProcess(comment, "SCV", ECHO_ON, ExpectedOutcome.exitValue(0,1).within("5m"), javaDefinition);

		// Verify that the expected cache is listed if the expectedCacheName is not empty
		if (!expectedCacheName.isEmpty()) {
			generator.outputEmptyLine();
			generator.outputLine("# Scan the stderr of the shared caches command to");
			generator.outputLine("# ensure the right cache is printed.");
			extensionBase.outputCountFileMatches("$cache_count", process.getStderrFileRef(), expectedCacheName);
			extensionBase.outputFailIfTrue("java", comment, "$cache_count", "==", 0);	
		}
		
		// Verify that the expected number of caches is found
		generator.outputEmptyLine();
		generator.outputLine("# Scan the stderr of the shared caches command to");
		generator.outputLine("# ensure the right number of caches are printed.");
		extensionBase.outputCountFileMatches("$actual_cache_count", process.getStderrFileRef(), cacheRegex);
		/* comment out the following line for now, needs to be re-enable when fixed. See https://github.com/eclipse/openj9-systemtest/issues/47 */
		/* extensionBase.outputFailIfTrue("java", comment, "$actual_cache_count", "!=", noOfExpectedCaches); */
	}

	
	/**
	 * Resets the Shared Classes Cache and calls -version, it then checks that the return code is 0.
	 * 
	 * @param comment is a comment from the test summarising why it's running a child process.
	 * @param scOptions are the shared classes options defined in a shared classes mode.
	 * @param cacheName is the name of the cache.
	 * @param cacheDir is the directory holding the classes cache.
	 * @throws StfException if anything goes wrong.
	 */
	public void doResetSharedClassesCache(String comment, String scOptions, String cacheName, String cacheDir) throws StfException {
		String cacheOperation =  ",reset";
		
		generator.startNewCommand(comment, "java", "Reset shared classes cache", 
				"Options:", scOptions,
				"CacheName:", cacheName,
				"CacheDir:", cacheDir,
				"CacheOperation:", cacheOperation); 
		
		String[] javaArgs = resolveSharedClassesOptions(scOptions, cacheName, cacheDir, cacheOperation);

		// Build the command to be run
		JavaProcessDefinition javaDefinition = new JavaProcessDefinition(environmentCore)
				.addJvmOption(javaArgs)
				.runClass("")
				.addArg("-version");

		extensionBase.runForegroundProcess(comment, "SCC", ECHO_ON, ExpectedOutcome.exitValue(0).within("5m"), javaDefinition);
	}
	
	
	/**
	 * Verifies the shared classes cache using a native JVMTI agent.
	 *  
	 * @param comment is a comment from the test summarising why it's running a child process.
	 * @param testName is the test that is running. 
	 * @param utilities is the utilities JVM option.
	 * @param iteratorCache is the iteratorCache JVM option.
	 * @param agentOptions are the native agent JVM options.
	 * @throws StfException if anything goes wrong.
	 */
	public void doVerifyCachesUsingJVMTI(String comment, String testName, String sharedClassesOption, String agentOptions) throws StfException {
		generator.startNewCommand(comment, "java", "Verify caches using JVMTI", 
				"TestName:", testName,
				"sharedClassesOption:", sharedClassesOption,
				"AgentOptions:", agentOptions); 
		
		// Start a JVM with a dummy class and a native agent
		StfProcess process = extensionBase.runForegroundProcess(testName + " - Check Shared Classes Caches", "JVMT", ECHO_ON,
				ExpectedOutcome.cleanRun().within("10m"), 
					new JavaProcessDefinition(environmentCore)
						.addJvmOption(sharedClassesOption)
						.addJvmOption(agentOptions)
						.addProjectToClasspath("openj9.stf.extensions")
						.runClass(DummySleeper.class));
		
		// Verify that the JVMTI output does not contain error messages
		generator.outputEmptyLine();
		generator.outputLine("# Scan the stdout of the Java command to");
		generator.outputLine("# ensure no error messages are printed.");
		extensionBase.outputCountFileMatches("$error_count", process.getStdoutFileRef(), "ERROR");
		extensionBase.outputFailIfTrue("java", "Verify Classes Caches using JVMTI", "$error_count", "!=", 0);	
	}
}