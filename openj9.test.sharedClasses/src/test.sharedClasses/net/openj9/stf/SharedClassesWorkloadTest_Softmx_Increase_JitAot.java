/*******************************************************************************
* Copyright (c) 2016, 2019 IBM Corp. and others
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
import net.adoptopenjdk.stf.processes.definitions.JavaProcessDefinition;
import net.adoptopenjdk.stf.processes.definitions.LoadTestProcessDefinition;
import net.adoptopenjdk.stf.runner.modes.HelpTextGenerator;
import net.openj9.stf.sharedClasses.SharedClassesPluginInterface;
import net.openj9.stf.sharedClasses.StfSharedClassesExtension;
import net.openj9.test.sc.SCSoftmxTestUtil;

/**
 *  SVT use case implementations for "Create a soft limit on the contents of the shared cache"
 *  
 *  Usecase
 *  ~~~~~~~~
 *  Test if we can write AOT and JIT data even after cache is soft full, as long as we have reserved spaces 
 *  for JIT and AOT data in the cache
 *  
 *  Have two Jvms generating AOT and JIT data at the same time. 
 *  Have max limits for AOT and JIT data in the shared classes cache. 
 *  Make sure they max out. Increase the limits. Have a third jvm producing 
 *  AOT and JIT cache data. Ensure it can write AOT and JIT data in the cache.
 *  
 *  Implementation steps
 *  ~~~~~~~~~~~~~~~~~~~~
 *  1) Start 2 Jvms that run workload processes with options "-XX:SharedCacheHardLimit=20m, 
 *     -Xshareclasses:addtestjithints,verbose -Xscmaxaot10k -Xscmaxjitdata10k -Xjit:count=0". 
 *     
 *     Both Jvms run mini-mix load tests, generate aot and jit data, and fill up the aot 
 *     and jit space in the cache. 
 *     
 *  2) Once Jvm1 and Jvm2 finishes, check that the AOT and JIT space is full. 
 *     Also, record the cache stats at this point, which should give us the number of bytes
 *     written in AOT and JIT data space. 
 *     
 *  3) Start Jvm3 with with options to increase softlimit of the aot and jit data spaces: 
 *  	 "-Xshareclasses:adjustminaot=50k,adjustmaxaot=60k,adjustminjit=50k,adjustmaxjit=60k". 
 *      Check cache stats to ensure cache's limits has been increased.
 *     
 *  4) Start Jvm4 that runs the third load test. When it finishes, make sure the aot and jitdata space has 
 *     been exhausted again. Obtain the cache stats again and compare the AOT and JIT data space now with 
 *     the values obtained in step 2. Make sure the latter values larger-- indicating AOT and JIT data were 
 *     written after the softmx were increased. 
 */
public class SharedClassesWorkloadTest_Softmx_Increase_JitAot implements SharedClassesPluginInterface {

	private final String CACHESIZE_HARD_LIMIT = "20m";
	
	private final String MAX_JITDATA = "10k"; 

	private final String MAX_AOT = "10k";

	private final String ADJUSTED_MIN_JITDATA = "50k";
	private final String ADJUSTED_MAX_JITDATA = "60k";

	private final String ADJUSTED_MIN_AOT = "50k";
	private final String ADJUSTED_MAX_AOT = "60k";
	
	private final long ADJUSTED_MINJIT_IN_BYTES = toBytes(ADJUSTED_MIN_JITDATA);
	private final long ADJUSTED_MAXJIT_IN_BYTES = toBytes(ADJUSTED_MAX_JITDATA);
	private final long ADJUSTED_MINAOT_IN_BYTES = toBytes(ADJUSTED_MIN_AOT);
	private final long ADJUSTED_MAXAOT_IN_BYTES = toBytes(ADJUSTED_MAX_AOT);
	
	private final String AOT_SPACE_FULL_MESSAGE = "JVMSHRC773I The space for AOT data in shared cache \"" + SCSoftmxTestUtil.CACHE_NAME + "\" is full.";
	private final String JIT_SPACE_FULL_MESSAGE = "JVMSHRC774I The space for JIT data in shared cache \"" + SCSoftmxTestUtil.CACHE_NAME + "\" is full.";
	private final String COMBINED_ERR_FILE_NAME = "stderr_combined.txt"; 

	private DirectoryRef cacheDirLocation;
	private String cacheSpecificGeneralOptions;
	private String initialCacheSizeOptions; 
	private String cacheSizeAdjustmentOptions; 
	private String finalCacheSizeOptions; 
	private String cacheDir; 

	public void help(HelpTextGenerator help) {
		help.outputSection("Shared Classes Workload test with cache soft limit, aot space limit and jitdata space limit increase");
		help.outputText(   "The Shared Classes Softmx Workload test runs the following tests:\n");
		help.outputArgDesc("- Runs two workload processes with shared classes creating a named cache, "
				+ "with a specific hard limit, soft limit, aot limit and jitdata limit, "
				+ "in the results directory. The workloads fill up the softlimit, aot and jitdata limits "
				+ "in the cache.\n");
		help.outputArgDesc("- Checks that a cache was created and that it is 100% soft full as expected.\n");
		help.outputArgDesc("- Increases the softlimit, aot and jitdata limit of the cache.\n");
		help.outputArgDesc("- Does another workload run using this named cache\n");
		help.outputArgDesc("- Checks no other caches exist and that the cache is 100% soft full again, "
				+ "and that the aot and jitdata space also are 100% full\n");
		help.outputArgDesc("- Deletes the cache\n");
	}

	public void pluginInit(StfCoreExtension test, StfSharedClassesExtension sharedClasses) throws Exception {
	}

	public void setUp(StfCoreExtension test, StfSharedClassesExtension sharedClasses) throws Exception {
		// Define the location for cache.
		cacheDirLocation = test.env().getResultsDir().childDirectory("cache");
		cacheDir = cacheDirLocation.toString();

		// Specify the cache specific variables.
		cacheSpecificGeneralOptions = "-Xshareclasses:addtestjithints,verbose," + "name=" + SCSoftmxTestUtil.CACHE_NAME + "," + "cacheDir=" + cacheDirLocation;

		// Create the directories for the cache.
		test.doMkdir("Create the cache directory", cacheDirLocation);

		initialCacheSizeOptions = "-XX:SharedCacheHardLimit=" + CACHESIZE_HARD_LIMIT + " " 
				+ cacheSpecificGeneralOptions  
				+ " -Xscmaxaot" + MAX_AOT
				+ " -Xscmaxjitdata" + MAX_JITDATA
				+ " -Xjit:count=1";

		cacheSizeAdjustmentOptions = cacheSpecificGeneralOptions + "," 
				+ "adjustminaot=" + ADJUSTED_MIN_AOT + ","
				+ "adjustmaxaot=" + ADJUSTED_MAX_AOT + ","
				+ "adjustminjitdata=" + ADJUSTED_MIN_JITDATA + ","
				+ "adjustmaxjitdata=" + ADJUSTED_MAX_JITDATA;

		finalCacheSizeOptions = cacheSpecificGeneralOptions  
				+ " -Xaot:forceAot,disableAsyncCompilation,count=0";

		// To ensure we run from a clean state, attempt to destroy the test related persistent/non-persistent caches 
		// from the default cache location which may have been left behind by a previous failed test.
		sharedClasses.doDestroySpecificCache("Destroy cache", cacheSpecificGeneralOptions + "${cacheOperation}", SCSoftmxTestUtil.CACHE_NAME, cacheDir);
		sharedClasses.doDestroySpecificNonPersistentCache("Destroy cache", cacheSpecificGeneralOptions + "${cacheOperation}", SCSoftmxTestUtil.CACHE_NAME, cacheDir);
	}

	public void execute(StfCoreExtension test, StfSharedClassesExtension sharedClasses) throws Exception {
		// Abort if we are not running on IBM Java
		test.env().verifyUsingIBMJava();

		// Prepare the JVMs to run at different phases of the test 
		LoadTestProcessDefinition loadTestSpecificationVM1 = SCSoftmxTestUtil.getMiniMixLoadTestOptions(
				test.createLoadTestSpecification() 
				.addJvmOption(initialCacheSizeOptions), 200); 

		LoadTestProcessDefinition loadTestSpecificationVM2 = SCSoftmxTestUtil.getMiniMixLoadTestOptions(
				test.createLoadTestSpecification()
				.addJvmOption(initialCacheSizeOptions), 200); 

		LoadTestProcessDefinition loadTestSpecificationVM3 = SCSoftmxTestUtil.getMiniMixLoadTestOptions(
				test.createLoadTestSpecification()
				.addJvmOption(finalCacheSizeOptions), 500); 

		JavaProcessDefinition cacheStatsVM = test.createJavaProcessDefinition()
				.addJvmOption("-Xshareclasses:name=" + SCSoftmxTestUtil.CACHE_NAME + ",cacheDir=" + cacheDir + ",verboseIO,printStats")
				.runClass("");

		// Run 2 JVMs at the beginning. Both Jvm1 and Jvm2 run workload processes in background mode 
		// that create a shared classes cache using the initial soft-limit and fill it up 100% of the soft-limit. 
		StfProcess jvm1 = test.doRunBackgroundProcess("Run Jvm1 workload process", 
				"jvm1", ECHO_OFF, ExpectedOutcome.exitValue(0,1).within("30m"), loadTestSpecificationVM1);

		StfProcess jvm2 = test.doRunBackgroundProcess("Run Jvm2 workload process", 
				"jvm2", ECHO_OFF, ExpectedOutcome.exitValue(0,1).within("30m"), loadTestSpecificationVM2);

		// Wait for jvm1 and jvm2 to complete 
		test.doMonitorProcesses("Waiting for jvm1 and jvm2 to finish", jvm1, jvm2);

		// The message that indicates AOT and JIT space is full may get printed in 
		// stderr of either jvm1 or jvm2. So, we must check both the files. 
		test.doRunForegroundProcess("Combine stderr files of jvm1 and jvm2", "CO", 
				ECHO_OFF, ExpectedOutcome.exitValue(0).within("30s"), 
				test.createJavaProcessDefinition()
				.addJvmOption("-Dtask=" + SCSoftmxTestUtil.MERGE_FILE)
				.addJvmOption("-Dfile1=" + jvm1.getStderrFileRef())
				.addJvmOption("-Dfile2=" + jvm2.getStderrFileRef())
				.addJvmOption("-Dfinal.file=" + test.env().getResultsDir() + System.getProperty("file.separator") + COMBINED_ERR_FILE_NAME)
				.addProjectToClasspath("stf.core")
				.addProjectToClasspath("openj9.test.sharedClasses")
				.runClass(SCSoftmxTestUtil.class));

		// Make sure the space for AOT data in shared cache is full
		test.doCountFileMatches("Make sure the space for AOT data in shared cache is full", 
				test.env().getResultsDir().childFile(COMBINED_ERR_FILE_NAME), 1, AOT_SPACE_FULL_MESSAGE);

		// Make sure the space for JIT data in shared cache is full
		test.doCountFileMatches("Make sure the space for JIT data in shared cache is full", 
				test.env().getResultsDir().childFile(COMBINED_ERR_FILE_NAME), 1, JIT_SPACE_FULL_MESSAGE);

		// Save the cache stats prior to increasing the soft mx size 
		StfProcess cacheStatsVMProcess1 = test.doRunForegroundProcess("Run printStats", 
				"PS1", ECHO_OFF, ExpectedOutcome.exitValue(1).within("1m"), cacheStatsVM);

		// Run jvm3 to increase cache size via softmx, aot and jitdata space and load more classes to fill it up again 
		StfProcess jvm3 = test.doRunForegroundProcess("Run Jvm3 to increasing cache size, aot and jitdata space", "jvm3", 
				ECHO_OFF, ExpectedOutcome.exitValue(1).within("1m"), 
				test.createJavaProcessDefinition()
				.addJvmOption(cacheSizeAdjustmentOptions)
				.runClass(""));

		// Make sure cache size adjustment happened properly
		test.doCountFileMatches("Check output to make sure cache minaot size adjustment happened properly", jvm3.getStderrFileRef(), 1, 
				"JVMSHRC785I The minimum reserved AOT bytes is set to " + ADJUSTED_MINAOT_IN_BYTES + ".");
		test.doCountFileMatches("Check output to make sure cache maxaot size adjustment happened properly", jvm3.getStderrFileRef(), 1, 
				"JVMSHRC786I The maximum allowed AOT bytes is set to " + ADJUSTED_MAXAOT_IN_BYTES + ".");
		test.doCountFileMatches("Check output to make sure cache min jitdata size adjustment happened properly", jvm3.getStderrFileRef(), 1, 
				"JVMSHRC787I The minimum reserved JIT data bytes is set to " + ADJUSTED_MINJIT_IN_BYTES + ".");
		test.doCountFileMatches("Check output to make sure cache max jitdata size adjustment happened properly", jvm3.getStderrFileRef(), 1, 
				"JVMSHRC788I The maximum allowed JIT data bytes is set to " + ADJUSTED_MAXJIT_IN_BYTES + ".");

		// Start Jvm4 : Run a third jvm that to fill the cache up again.
		test.doRunForegroundProcess("Run Jvm4 workload process", 
				"jvm4", ECHO_OFF, ExpectedOutcome.exitValue(0,1).within("30m"), loadTestSpecificationVM3);	

		// Save the cache stats after the softmx size was increased and the third workload(jvm4) was run 
		StfProcess cacheStatsVMProcess2 = test.doRunForegroundProcess("Run printStats", 
				"PS2", ECHO_OFF, ExpectedOutcome.exitValue(1).within("1m"), cacheStatsVM);

		// Compare the AOT and JIT data bytes written to the cache before and after the softmx increase
		// and make sure the latter is a larger or equal value  
		test.doRunForegroundProcess("Compare AOT and JITdata bytes before and after softmx was increased. "
				+ "This process will return a non-zero value if AOT and / or JITdata space "
				+ "after the softmx was increased is not greater than or equal to what they were before the increase "
				+ "was made", "SL0", 
				ECHO_OFF, ExpectedOutcome.exitValue(0).within("1m"), 
				test.createJavaProcessDefinition()
				.addJvmOption("-Dtask=" + SCSoftmxTestUtil.COMPARE_AOT_JIT_DATA_SPACE)
				.addJvmOption("-Dfile1=" + cacheStatsVMProcess1.getStderrFileRef())
				.addJvmOption("-Dfile2=" + cacheStatsVMProcess2.getStderrFileRef())
				.addProjectToClasspath("openj9.test.sharedClasses")
				.addProjectToClasspath("stf.core")
				.runClass(net.openj9.test.sc.SCSoftmxTestUtil.class));

		// Destroy the existing cache.
		sharedClasses.doDestroySpecificCache("Destroy cache", cacheSpecificGeneralOptions + "${cacheOperation}", SCSoftmxTestUtil.CACHE_NAME, cacheDir);

		// Confirm that the deletion was successful.
		sharedClasses.doVerifySharedClassesCache("Verify caches", cacheSpecificGeneralOptions + "${cacheOperation}", SCSoftmxTestUtil.CACHE_NAME, cacheDir, "", 0);
	}

	public void tearDown(StfCoreExtension test, StfSharedClassesExtension sharedClasses) throws Exception {
		// Destroy all test related persistent/non-persistent caches from the default cache location which may
		// have been left behind by a failure. We don't care about caches left behind in results
		// as those will get deleted together with results.
		sharedClasses.doDestroySpecificCache("Destroy cache", cacheSpecificGeneralOptions + "${cacheOperation}", SCSoftmxTestUtil.CACHE_NAME, cacheDir);
		sharedClasses.doDestroySpecificNonPersistentCache("Destroy cache", cacheSpecificGeneralOptions + "${cacheOperation}", SCSoftmxTestUtil.CACHE_NAME, cacheDir);
	}
	
	private long toBytes(String size) {
		if (size.endsWith("m")) {
			return Long.parseLong(size.split("m")[0]) * 1024 * 1024; 
		} else if (size.endsWith("k")) {
			return Long.parseLong(size.split("k")[0]) * 1024;
		} else {
			System.out.println("Invalid unit provided. Supported units: \"k\", \"m\"");
			return -1;
		}
	}
}
