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

import static net.adoptopenjdk.stf.extensions.core.StfCoreExtension.Echo.ECHO_OFF;

import net.adoptopenjdk.stf.environment.DirectoryRef;
import net.adoptopenjdk.stf.extensions.core.StfCoreExtension;
import net.adoptopenjdk.stf.processes.ExpectedOutcome;
import net.adoptopenjdk.stf.processes.definitions.JavaProcessDefinition;
import net.adoptopenjdk.stf.processes.definitions.LoadTestProcessDefinition;
import net.adoptopenjdk.stf.runner.modes.HelpTextGenerator;
import net.openj9.stf.sharedClasses.SharedClassesPluginInterface;
import net.openj9.stf.sharedClasses.StfSharedClassesExtension;

/**
 * 
 *  This Shared Classes test plugin runs the following tests:
 *  
 *  Runs a workload with shared classes creating a named cache in the results directory
 *  Checks that a cache was created
 *  Does another workload run using this named cache
 *  Checks no other caches exist
 *  Deletes the cache
 *  Does two sequential workload runs, with multiple JVM instances in each run, creating and using a common cache
 *  Checks that only one cache was created
 *  Deletes the cache
 *
 */
public class SharedClassesWorkload implements SharedClassesPluginInterface {
	private DirectoryRef cacheDirLocation;
	private String cacheName;
	private String cacheDir;
	private String jvmOptions;
	
	
	public void help(HelpTextGenerator help) {
		help.outputSection("Shared Classes Workload test");
		help.outputText(   "The Shared Classes Workload test runs the following tests:\n");
	
		help.outputArgDesc("- Runs a workload with shared classes creating a named cache in the results directory\n");
		help.outputArgDesc("- Checks that a cache was created\n");
		help.outputArgDesc("- Does another workload run using this named cache\n");
		help.outputArgDesc("- Checks no other caches exist\n");
		help.outputArgDesc("- Deletes the cache\n");
		help.outputArgDesc("- Does two sequential workload runs, with multiple JVM instances in each run, creating and using a common cache\n");
		help.outputArgDesc("- Checks that only one cache was created\n");
		help.outputArgDesc("- Deletes the cache\n");
	}

	
	public void pluginInit(StfCoreExtension test, StfSharedClassesExtension sharedClasses) throws Exception {
		// Define the location for cache.
		cacheDirLocation = test.env().getResultsDir().childDirectory("cache");
		cacheDir = cacheDirLocation.toString();
		
		// Specify the cache specific variables.
		cacheName = "workload_cache";
		jvmOptions = "-Xshareclasses:" + "name=" + cacheName + "," + "cacheDir=" + cacheDirLocation;
	}

	
	public void setUp(StfCoreExtension test, StfSharedClassesExtension sharedClasses) throws Exception {
		// Create the directories for the cache.
		test.doMkdir("Create the cache directory", cacheDirLocation);
		
		// To ensure we run from a clean state, attempt to destroy all persistent/non-persistent caches 
		// from the default cache location which may have been left behind by a previous failed test.
		sharedClasses.doDestroyAllPersistentCaches("Destroy Persistent Shared Classes Caches");
		sharedClasses.doDestroyAllNonPersistentCaches("Destroy Non-Persistent Shared Classes Caches");
	}


	public void execute(StfCoreExtension test, StfSharedClassesExtension sharedClasses) throws Exception {
		// Abort if we are not running on IBM Java
		test.env().verifyUsingIBMJava();
		
		// Specify the Process definition for the workload processes.
		String inventoryFile = "/openjdk.test.load/config/inventories/mix/mini-mix.xml";
		LoadTestProcessDefinition loadTestSpecification = test.createLoadTestSpecification()
				.addJvmOption(jvmOptions)
				.addPrereqJarToClasspath(JavaProcessDefinition.JarId.JUNIT)
				.addPrereqJarToClasspath(JavaProcessDefinition.JarId.HAMCREST)
				.addProjectToClasspath("openjdk.test.lang")    // For mini-mix inventory
				.addProjectToClasspath("openjdk.test.util")    // For mini-mix inventory
				.addProjectToClasspath("openjdk.test.math")    // For mini-mix inventory
				.generateCoreDumpAtFirstLoadTestFailure(false)
				.addSuite("mini-mix")
				.setSuiteInventory(inventoryFile)
				.setSuiteThreadCount(3)
				.setSuiteNumTests(600)
			   	.setSuiteSequentialSelection();
		
		///////////////////////////////////////// Run the tests /////////////////////////////////////////
		
		// Create a cache using a single workload process.
		test.doRunForegroundProcess("Run a single workload process", 
				"SCL1", 
				ECHO_OFF, 
				ExpectedOutcome.cleanRun().within("5m"), 
				loadTestSpecification);
		
		// Check that the expected cache was created and no other caches exist.
		verifyAndPrintCache(sharedClasses, cacheName, cacheDir, cacheName, 1);
		
		// Access the existing cache using another workload process.
		test.doRunForegroundProcess("Access cache using another workload process", 
				"SCL2", 
				ECHO_OFF, 
				ExpectedOutcome.cleanRun().within("5m"), 
				loadTestSpecification);	

		// Confirm that only the expected cache exists and no other caches were 
		// created by the "SCL2" workload process.
		verifyAndPrintCache(sharedClasses, cacheName, cacheDir, cacheName, 1);
		
		// Destroy the existing cache.
		sharedClasses.doDestroySpecificCache("Destroy cache", jvmOptions + "${cacheOperation}", cacheName, cacheDir);
		
		// Confirm that the deletion was successful.
		sharedClasses.doVerifySharedClassesCache("Verify caches", jvmOptions + "${cacheOperation}", cacheName, cacheDir, "", 0);
		
		// Create a new cache using multiple workload processes.
		test.doRunForegroundProcesses("Run multiple workload processes", 
				"SCL",
				2,
				ECHO_OFF, 
				ExpectedOutcome.cleanRun().within("5m"), 
				loadTestSpecification);
		
		// Check that the expected cache was created and no other caches exist
		verifyAndPrintCache(sharedClasses, cacheName, cacheDir, cacheName, 1);
		
		// Access the existing cache using more workload processes
		test.doRunForegroundProcesses("Access cache using more workload processes", 
				"SCL",
				2,
				ECHO_OFF, 
				ExpectedOutcome.cleanRun().within("20m"), 
				loadTestSpecification);
		
		// Confirm that only the expected cache exists and no other caches were 
		// created by the last run of "SCL" workload processes.
		verifyAndPrintCache(sharedClasses, cacheName, cacheDir, cacheName, 1);
		
		// Destroy the existing cache.
		sharedClasses.doDestroySpecificCache("Destroy cache", jvmOptions + "${cacheOperation}", cacheName, cacheDir);
		
		// Confirm that the deletion was successful 
		sharedClasses.doVerifySharedClassesCache("List all caches", jvmOptions + "${cacheOperation}", cacheName, cacheDir, "", 0);	
	}
		
	private void verifyAndPrintCache(StfSharedClassesExtension sharedClasses, String cacheName, String cacheDir, String expectedCacheName, int expectedCaches) throws Exception {
		// Verify cache and the number of expected caches
		sharedClasses.doVerifySharedClassesCache("List all caches", jvmOptions + "${cacheOperation}", cacheName, cacheDir, expectedCacheName, expectedCaches);
			
		// Print the status of the cache and check that the cache is 1-100% full
		String[] expectedMessages = {"Cache is (100%|[1-9][0-9]%|[1-9]%) (soft )*full"}; // Ensure the cache is 1-100% 'full' or 'soft full' (in case of Java 10 and up)
		sharedClasses.doPrintAndVerifyCache("Print Shared Classes Cache Stats", jvmOptions + "${cacheOperation}", cacheName, cacheDir, expectedMessages);
	}

	
	public void tearDown(StfCoreExtension test, StfSharedClassesExtension sharedClasses) throws Exception {
		// Destroy all persistent/non-persistent caches from the default cache location which may
		// have been left behind by a failure. We don't care about caches left behind in results
		// as those will get deleted together with results.
		sharedClasses.doDestroyAllPersistentCaches("Destroy Persistent Shared Classes Caches");
		sharedClasses.doDestroyAllNonPersistentCaches("Destroy Non-Persistent Shared Classes Caches");
	}
}