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

import static net.adoptopenjdk.stf.extensions.core.StfCoreExtension.Echo.ECHO_OFF;

import net.adoptopenjdk.stf.environment.DirectoryRef;
import net.adoptopenjdk.stf.extensions.core.StfCoreExtension;
import net.adoptopenjdk.stf.processes.ExpectedOutcome;
import net.adoptopenjdk.stf.processes.StfProcess;
import net.adoptopenjdk.stf.processes.definitions.LoadTestProcessDefinition;
import net.adoptopenjdk.stf.runner.modes.HelpTextGenerator;
import net.openj9.stf.sharedClasses.SharedClassesPluginInterface;
import net.openj9.stf.sharedClasses.StfSharedClassesExtension;
import net.openj9.test.sc.SCSoftmxTestUtil;

/**
 *  SVT use case implementations for creating a soft limit on the contents of the shared cache.
 *  
 *  Usecase
 *  ~~~~~~~
 *  Start Jvm1 that sets softmx to say 1mb and shared cache hard limit to say 20mb. 
 *  Jvm1 fills up cache, Jvm2 will check if cache's usage has reached softmx, if yes, 
 *  then Jvm2 will adjust softmx to increased value of e.g. 2mb. Start Jvm3 and 
 *  continue loading the cache.
 *  
 *  Implementation steps
 *  ~~~~~~~~~~~~~~~~~~~~~~
 *  1) Start Jvm1 with options: "-XX:SharedCacheHardLimit=20m, -Xscmxsoftmx=1m". It should 
 *     create the shared classes cache and run a mini-mix load test and fill up the shared 
 *     classes cache.
 *  2) Once Jvm1 finishes its work, check cache stats to ensure cache is soft full. Fail if it isn't.
 *  3) Start Jvm2 that does -Xshareclasses:adjustsoftmx=2m. Check cache stats to ensure 
 *     cache's softmx limit has been increased to 2m.
 *  4) Start Jvm3 that runs the second mini-mix load. Make sure code is being written to 
 *     the increased space of the shared classes cache.
 */
public class SharedClassesWorkloadTest_Softmx_Increase implements SharedClassesPluginInterface {
	
	private final int CACHESIZE_HARD_LIMIT = 20;
	private final int CACHESIZE_SOFTLIMIT_1MB = 1; 
	private final int CACHESIZE_SOFTLIMIT_2MB = 2; 
	private final String CACHE_NAME = "workload_cache";
	private final String[] CACHE_FULL_MESSAGE = {"Cache is 100% soft full"}; 
	
	private DirectoryRef cacheDirLocation;
	private String cacheSpecificGeneralOptions;
	private String initialCacheSizeOptions; 
	private String cacheSizeAdjustmentOptions; 
	private String cacheDir; 
	
	public void help(HelpTextGenerator help) {
		help.outputSection("Shared Classes Workload test with cache soft limit increase");
		help.outputText(   "The Shared Classes Softmx Workload test runs the following tests:\n");
		help.outputArgDesc("- Runs a workload with shared classes creating a named cache "
				+ "with a specific hard limit and soft limit in the results directory. The workload fills up the cache.\n");
		help.outputArgDesc("- Checks that a cache was created and that it was filled up.\n");
		help.outputArgDesc("- Increases the softlimit of the cache.\n");
		help.outputArgDesc("- Does another workload run using this named cache\n");
		help.outputArgDesc("- Checks no other caches exist and that the cache was filled up to its new soft limit\n");
		help.outputArgDesc("- Deletes the cache\n");
	}

	public void pluginInit(StfCoreExtension test, StfSharedClassesExtension sharedClasses) throws Exception {
	}

	public void setUp(StfCoreExtension test, StfSharedClassesExtension sharedClasses) throws Exception {
		// Define the location for cache.
		cacheDirLocation = test.env().getResultsDir().childDirectory("cache");
		cacheDir = cacheDirLocation.toString();
		
		// Specify the cache specific variables.
		cacheSpecificGeneralOptions = "-Xshareclasses:" + "name=" + CACHE_NAME + "," + "cacheDir=" + cacheDirLocation;

		// Create the directories for the cache.
		test.doMkdir("Create the cache directory", cacheDirLocation);
		
		// Set Cache specific options for the test 
		initialCacheSizeOptions = cacheSpecificGeneralOptions +  
					" -XX:SharedCacheHardLimit=" + CACHESIZE_HARD_LIMIT + "m" + 
					" -Xscmx" + CACHESIZE_SOFTLIMIT_1MB + "m";  

		cacheSizeAdjustmentOptions = cacheSpecificGeneralOptions + "," + 
                   "adjustsoftmx=" + CACHESIZE_SOFTLIMIT_2MB + "m";

		// To ensure we run from a clean state, attempt to destroy all persistent/non-persistent caches 
		// from the default cache location which may have been left behind by a previous failed test.
		sharedClasses.doDestroyAllPersistentCaches("Destroy Persistent Shared Classes Caches");
		sharedClasses.doDestroyAllNonPersistentCaches("Destroy Non-Persistent Shared Classes Caches");
	}
	
	public void execute(StfCoreExtension test, StfSharedClassesExtension sharedClasses) throws Exception {
		// Abort if we are not running on IBM Java.
		test.env().verifyUsingIBMJava();

		// Specify the Process definition for the workload processes (jvm1).
		LoadTestProcessDefinition loadTestSpecificationVM1 = test.createLoadTestSpecification() 
				.addJvmOption(initialCacheSizeOptions); 
		loadTestSpecificationVM1 = SCSoftmxTestUtil.getMiniMixLoadTestOptions(loadTestSpecificationVM1, 600); 
		
		LoadTestProcessDefinition loadTestSpecificationVM2 = test.createLoadTestSpecification()
				.addJvmOption(cacheSpecificGeneralOptions);
		loadTestSpecificationVM2 = SCSoftmxTestUtil.getMiniMixLoadTestOptions(loadTestSpecificationVM2, 1000); 
		
		// Start JVM1: Run a single workload process that creates a shared classes cache
		// using the initial soft limit and fills it up.
		test.doRunForegroundProcess("Run a single workload process to fill up the initial cache", 
				"Jvm1", ECHO_OFF, ExpectedOutcome.exitValue(0,1).within("15m"), loadTestSpecificationVM1);
		
		// Check that the expected cache was created, caches exist, and the cache is 100% soft fullt.
		verifyAndPrintCache(sharedClasses, CACHE_NAME, cacheDir, CACHE_NAME, 1, CACHE_FULL_MESSAGE);
		
		// Increase cache size via softmx and load more classes to fill it up again. 
		StfProcess p = test.doRunForegroundProcess("Increasing cache size to " + CACHESIZE_SOFTLIMIT_2MB + 
				"mb via -Xshareclasses:adjustsoftmx option", "SL2", ECHO_OFF, ExpectedOutcome.exitValue(1).within("1m"), 
				test.createJavaProcessDefinition()
					.addJvmOption(cacheSizeAdjustmentOptions)
					.runClass(""));
		
		// Make sure cache size adjustment happened properly.
		test.doCountFileMatches("Check output", p.getStderrFileRef(), 1, 
				"JVMSHRC789I The softmx bytes is set to " + (CACHESIZE_SOFTLIMIT_2MB * 1024 * 1024) + ".");
		
		// Start JVM2 : Run a second workload to fill the cache up again, after the cache size has been increased.  
		test.doRunForegroundProcess("Run a second workload after the cache size has been increased", 
				"Jvm2", ECHO_OFF, ExpectedOutcome.exitValue(0,1).within("30m"), loadTestSpecificationVM2);	

		// Confirm that only the expected cache exists and no other caches were created 
		// Also, confirm that increasing the cache size via softmx allowed new code to be written. 
		verifyAndPrintCache(sharedClasses, CACHE_NAME, cacheDir, CACHE_NAME, 1, CACHE_FULL_MESSAGE);
		
		// Destroy the existing cache.
		sharedClasses.doDestroySpecificCache("Destroy cache", cacheSpecificGeneralOptions + "${cacheOperation}", CACHE_NAME, cacheDir);
		
		// Confirm that the deletion was successful.
		sharedClasses.doVerifySharedClassesCache("Verify caches", cacheSpecificGeneralOptions + "${cacheOperation}", CACHE_NAME, cacheDir, "", 0);
	}
	
	private void verifyAndPrintCache(StfSharedClassesExtension sharedClasses, String cacheName, String cacheDir, String expectedCacheName, int expectedCaches, String[] expectedMessages) throws Exception {
		// Verify cache and the number of expected caches
		sharedClasses.doVerifySharedClassesCache("List all caches", cacheSpecificGeneralOptions + "${cacheOperation}", cacheName, cacheDir, expectedCacheName, expectedCaches);
			
		// Print the status of the cache and check that the cache is 1-100% full
		sharedClasses.doPrintAndVerifyCache("Print Shared Classes Cache Stats", cacheSpecificGeneralOptions + "${cacheOperation}", cacheName, cacheDir, expectedMessages);
	}
	
	public void tearDown(StfCoreExtension test, StfSharedClassesExtension sharedClasses) throws Exception {
		// Destroy all persistent/non-persistent caches from the default cache location which may
		// have been left behind by a failure. We don't care about caches left behind in results
		// as those will get deleted together with results.
		sharedClasses.doDestroyAllPersistentCaches("Destroy Persistent Shared Classes Caches");
		sharedClasses.doDestroyAllNonPersistentCaches("Destroy Non-Persistent Shared Classes Caches");
	}
}