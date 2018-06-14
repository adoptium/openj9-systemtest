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
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0 OR GPL-2.0 WITH Classpath-exception-2.0 OR LicenseRef-GPL-2.0 WITH Assembly-exception
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
 * SVT use case implementations for creating a soft limit on the contents of the shared cache
 * 
 * Use Case
 * ~~~~~~~~ 
 * Start 2 jvms that write to the cache. Increase cache size so they can 
 * complete execution. Decrease the cache size so that further attempts to 
 * write to cache fails.
 *       
 * Implementation steps
 * ~~~~~~~~~~~~~~~~~~~~~~
 * 1) Run 2 JVMs at the beginning. Both Jvm1 and Jvm2 run workload processes in background mode, 
 *    that create a shared classes cache using the initial soft limit and fill it up to 1-40% 
 *    of the limit.
 * 2) Add delay of 2 seconds so that jvm1 and jvm2 can write to the cache.
 * 3) Increase cache. 
 * 4) Wait for jvm1 and jvm2 to finish. 
 * 5) Decrease the cache size so that it becomes 100% full. 
 * 6) Start Jvm3 and make sure it can not write to the cache any more. 
 * 
 */
public class SharedClassesWorkloadTest_Softmx_IncreaseDecrease implements SharedClassesPluginInterface {
	
	private final int CACHESIZE_HARD_LIMIT_MB = 40;
	private final int CACHESIZE_SOFTLIMIT_INITIAL_MB = 10; 
	private final int CACHESIZE_SOFTLIMIT_INTERMIEDIATE_INCREASED_MB = 20;
	private final String CACHE_NAME = "workload_cache";
	
	private DirectoryRef cacheDirLocation;
	private String cacheSpecificGeneralOptions;
	private String initialCacheSizeOptions; 
	private String cacheSizeAdjustmentOptions_Intermediate_SoftmxIncreased; 
	private String cacheSizeAdjustmentOptions_Final_SoftmxDecreased; 
	private String cacheDir; 
	
	public void help(HelpTextGenerator help) {
		help.outputSection("Shared Classes Softmx Workload test with cache soft limit increase and decrease");
		help.outputText(   "The Shared Classes Workload test runs the following tests:\n");
		help.outputArgDesc("- Starts two workload processes in the background with shared classes, "
				+ "creating a named cache of a given hard limit and soft limit, in the results directory\n");
		help.outputArgDesc("- Checks that a cache was created\n");
		help.outputArgDesc("- Waits 2 seconds and and increases the cache soft limit of the cache\n");
		help.outputArgDesc("- Waits for the two workloads to complete, and then decreases the cache soft limit to "
				+ "half of its original limit\n");
		help.outputArgDesc("- Ensures the cache is 100% full\n");
		help.outputArgDesc("- Starts a third workload process and makes sure it fails to write to the cache\n");
		help.outputArgDesc("- Waits for the third workload to complete\n");
		help.outputArgDesc("- Deletes the cache\n");
	}

	public void pluginInit(StfCoreExtension test, StfSharedClassesExtension sharedClasses) throws Exception {  
	}

	public void setUp(StfCoreExtension test, StfSharedClassesExtension sharedClasses) throws Exception {
		// Define the location for cache.
		cacheDirLocation = test.env().getResultsDir().childDirectory("cache");
		cacheDir = cacheDirLocation.toString();
		
		// Specify the cache specific variables.
		cacheSpecificGeneralOptions = "-Xshareclasses:" + "name=" + CACHE_NAME + "," + 
									  "cacheDir=" + cacheDirLocation + "," + "verboseIO";
		
		initialCacheSizeOptions = cacheSpecificGeneralOptions + " -XX:SharedCacheHardLimit=" + CACHESIZE_HARD_LIMIT_MB + "m " + 
								  "-Xscmx" + CACHESIZE_SOFTLIMIT_INITIAL_MB + "m";  
		
		cacheSizeAdjustmentOptions_Intermediate_SoftmxIncreased = cacheSpecificGeneralOptions + "," + 
								"adjustsoftmx=" + CACHESIZE_SOFTLIMIT_INTERMIEDIATE_INCREASED_MB + "m";
		
		cacheSizeAdjustmentOptions_Final_SoftmxDecreased = cacheSpecificGeneralOptions + "," + 
								"adjustsoftmx=" + (CACHESIZE_SOFTLIMIT_INTERMIEDIATE_INCREASED_MB /2) + "m";
				
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

		// Preprare the load test processes to be used at different phases of the test. 
		LoadTestProcessDefinition loadTestSpecificationVM1 = test.createLoadTestSpecification()
				.addJvmOption(initialCacheSizeOptions); 
		loadTestSpecificationVM1 = SCSoftmxTestUtil.getMiniMixLoadTestOptions(loadTestSpecificationVM1, 800); 
		
		LoadTestProcessDefinition loadTestSpecificationVM2 = test.createLoadTestSpecification()
				.addJvmOption(initialCacheSizeOptions); 
		loadTestSpecificationVM2 = SCSoftmxTestUtil.getMiniMixLoadTestOptions(loadTestSpecificationVM2, 800); 
		
		// Run 2 JVMs at the beginning. Both Jvm1 and Jvm2 run workload processes in background mode 
		// that create a shared classes cache using an initial soft limit and fill it up to 1-40% 
		// of the limit. 
		StfProcess jvm1 = test.doRunBackgroundProcess("Run Jvm1 workload process", 
				"jvm1", ECHO_OFF, ExpectedOutcome.exitValue(0,1).within("30m"), loadTestSpecificationVM1);
		
		StfProcess jvm2 = test.doRunBackgroundProcess("Run Jvm2 workload process", 
				"jvm2", ECHO_OFF, ExpectedOutcome.exitValue(0,1).within("30m"), loadTestSpecificationVM2);
		
		// Add delay of 2 seconds so that jvm1 and jvm2 can start writing to the cache 
		test.doRunForegroundProcess("Allow jvm1 and jvm2 to write to cache for 2 seconds",
				"SL", ECHO_OFF, ExpectedOutcome.exitValue(0).within("1m"), 
				test.createJavaProcessDefinition()
					.addJvmOption("-Dtask=" + SCSoftmxTestUtil.SLEEP)
					.addJvmOption("-Dtimeinmilis=2000")
					.addProjectToClasspath("stf.core")
					.addProjectToClasspath("openj9.test.sharedClasses")
					.runClass(net.openj9.test.sc.SCSoftmxTestUtil.class));

		// Increase the cache size while jvm1 and jvm2 are still running
		StfProcess cacheAdjuster = test.doRunForegroundProcess("Increase cache size to " + CACHESIZE_SOFTLIMIT_INTERMIEDIATE_INCREASED_MB + 
				"mb via -Xshareclasses:adjustsoftmx option", "AD1", ECHO_OFF, ExpectedOutcome.exitValue(1).within("1m"), 
				test.createJavaProcessDefinition()
					.addJvmOption(cacheSizeAdjustmentOptions_Intermediate_SoftmxIncreased)
					.runClass(""));

		// Make sure cache size adjustment happened properly
		test.doCountFileMatches("Make sure cache softmx was inreased to its correct limit", cacheAdjuster.getStderrFileRef(), 1, 
				"JVMSHRC789I The softmx bytes is set to " + (CACHESIZE_SOFTLIMIT_INTERMIEDIATE_INCREASED_MB * 1024 * 1024) + ".");
				
		// Wait for jvm1 and jvm2 to complete 
		test.doMonitorProcesses("Wait for processes to complete", jvm1, jvm2);
		
		// Ensure that, at this point that, the cache is more than 50% but less than 100% full. 
		String[] expectedMessages1 = {"Cache is (99%|[5-8][0-9]%|[9][0-8]%) soft full"}; 
		verifyAndPrintCache(sharedClasses, CACHE_NAME, cacheDir, CACHE_NAME, 1, expectedMessages1);
		
		// Attempt to decrease the cache size to half of its existing soft limit.
		// Since the cache is more than half full right now, adjustsoftmx should result in 
		// shrinking the softmx size to the amount of bytes that exist in the cache.  
		// The cache should be 100% soft-full after this adjustment has been made. 
		cacheAdjuster = test.doRunForegroundProcess("Decrease cache size to " + CACHESIZE_SOFTLIMIT_INTERMIEDIATE_INCREASED_MB / 2 + 
				"mb via -Xshareclasses:adjustsoftmx option", "AD2", ECHO_OFF, ExpectedOutcome.exitValue(1).within("1m"), 
				test.createJavaProcessDefinition()
					.addJvmOption(cacheSizeAdjustmentOptions_Final_SoftmxDecreased)
					.runClass(""));

		// Make sure we see an error message in the output indicating that the new decreased 
		// size is smaller than the number of bytes, x, in the cache. 
		// This guarantees that we have a 100% soft-full cache of size x mb at this point. 
		test.doCountFileMatches("Checking cache size", cacheAdjuster.getStderrFileRef(), 1, 
				"JVMSHRC776W The softmx limit for shared cache usage is smaller than the number of bytes");
		
		// Make sure that the cache is 100% soft full. 
		String[] expectedMessages2 = {"Cache is 100% soft full"};
		verifyAndPrintCache(sharedClasses, CACHE_NAME, cacheDir, CACHE_NAME, 1, expectedMessages2);
				
		// Run the third workload (jvm3) in background mode that drives a new workload. 
		// This should trigger a cache write failure.
		LoadTestProcessDefinition loadTestSpecificationWithDecreasedSoftmx = test.createLoadTestSpecification()
				.addJvmOption(cacheSpecificGeneralOptions);
		loadTestSpecificationWithDecreasedSoftmx = SCSoftmxTestUtil.getClassLoadingLoadTestOptions(
				test, loadTestSpecificationWithDecreasedSoftmx, 500); 
		
		StfProcess jvm3 = test.doRunBackgroundProcess("Run a third workload after the cache size has been decreased", 
				"jvm3", ECHO_OFF, ExpectedOutcome.exitValue(0,1).within("30m"), 
				loadTestSpecificationWithDecreasedSoftmx);	

		// Wait for jvm3 to complete 
		test.doMonitorProcesses("Wait for processes to complete", jvm3);

		// Check stderr output of jvm3 to ensure we get messages indicating failure to store classes in cache 
		test.doFindFileMatches("Checking for output indicating failure to write to cache", jvm3.getStderrFileRef(), 
				"Failed to store class");
		
		// Destroy the existing cache.
		sharedClasses.doDestroySpecificCache("Destroy cache", cacheSpecificGeneralOptions + "${cacheOperation}", CACHE_NAME, cacheDir);
		
		// Confirm that the deletion was successful.
		sharedClasses.doVerifySharedClassesCache("Verify caches", cacheSpecificGeneralOptions + "${cacheOperation}", CACHE_NAME, cacheDir, "", 0);
	}

	private void verifyAndPrintCache(StfSharedClassesExtension sharedClasses, String cacheName, String cacheDir, String expectedCacheName, int expectedCaches, String[] expectedMessages) throws Exception {
		// Verify cache and the number of expected caches
		sharedClasses.doVerifySharedClassesCache("List all caches", cacheSpecificGeneralOptions + "${cacheOperation}", cacheName, cacheDir, expectedCacheName, expectedCaches);
			
		// Print the status of the cache and check that the cache is full to the given expected percentage 
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