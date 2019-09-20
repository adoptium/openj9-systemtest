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

import static net.adoptopenjdk.stf.extensions.core.StfCoreExtension.Echo.ECHO_ON;

import java.util.ArrayList;

import net.adoptopenjdk.stf.environment.DirectoryRef;
import net.adoptopenjdk.stf.environment.FileRef;
import net.adoptopenjdk.stf.environment.StfTestArguments;
import net.adoptopenjdk.stf.extensions.core.StfCoreExtension;
import net.adoptopenjdk.stf.processes.ExpectedOutcome;
import net.adoptopenjdk.stf.runner.modes.HelpTextGenerator;
import net.openj9.stf.sharedClasses.SharedClassesPluginInterface;
import net.openj9.stf.sharedClasses.StfSharedClassesExtension;
import net.openj9.test.sc.JavaGen;
import net.openj9.test.sc.LoaderSlave;
import net.openj9.test.sc.LoaderSlaveMultiCL;
import net.openj9.test.sc.LoaderSlaveMultiJar;
import net.openj9.test.sc.LoaderSlaveMultiThread;
import net.openj9.test.sc.LoaderSlaveMultiThreadMultiCL;
import net.openj9.test.sc.SCSoftmxTestUtil;


/**
 * This is a SVT Shared Classes test plugin aimed at stress testing the shared Classes features of IBM's sdk.
 * It runs various extremes of classloading, whilst mixing -Xshareclasses options which are either diagnostic in nature 
 * or are likely options for customers. The tests and their aims are described below: 
 * 
 * Single Class Loader (SingleCL)
 * One classLoader, loads all classes (20000) in a single jar file. This is the simplest test in the suite, its aim is 
 * to populate the cache in the simplest and most obvious way without stressing the classpath caching.
 * 
 * Multiple Class Loader (MultiCL)
 * As above, however a separate classLoader is used to load each class from the jar file. This test introduces multiple 
 * classLoaders to stress the classpath caching. 
 * 
 * Multiple Thread Loader (MultiThread)
 *  As "Single Class Loader", however a thread is forked to load each class file from the jar. This test is
 *  aimed at stressing the concurrent access control without being too demanding on the rest of the caching code. 
 * 
 * Multiple Thread / Multiple Class Loader (MultiThreadMultiCL)
 * As "Multiple Class Loader", however each classLoader is created in it's own forked thread. The most stressful test for 
 * loading classes from a single jar - just a combination of the multiple classloaders and threads to make life difficult..
 * 
 * Multiple Jars (MultiJar)
 * Here we create a new classLoader to load two classes from every jar file in a given folder - one class loaded to lock 
 * the jar, another loaded into the cache. This tests makes heavy demands on the classpath caching code as it introduces 
 * a large number of classpath jars (10000 by default).
 */
public class SharedClasses implements SharedClassesPluginInterface {
	// Define the Shared Classes test and it's respective arguments.
	private enum Tests {
		//Test              Mnemonic  TestClass                             ClassArgs  UsesMultipleJars
		SingleCL(           "SCL",    LoaderSlave.class,                    "",        false),
		MultiCL(            "MCL",    LoaderSlaveMultiCL.class,             "",        false),
		MultiJar(           "MJ",     LoaderSlaveMultiJar.class,            "1000",    true),
		MultiThread(        "MT",     LoaderSlaveMultiThread.class,         "300",     false),
		MultiThreadMultiCL( "MTM",    LoaderSlaveMultiThreadMultiCL.class,  "120",     false);
		
		private String mnemonic;
		private Class<?> testClass;
		private String classArgs;
		private Boolean usesMultipleJars;
		
		private Tests(String mnemonic, Class<?> testClass, String classArgs, Boolean usesMultipleJars) {
			this.mnemonic = mnemonic;
			this.testClass = testClass;
			this.classArgs = classArgs;
			this.usesMultipleJars = usesMultipleJars; 
		}
	}
	
	
	// Define the Shared Classes test modes that can be run.
	// Not all these options may be being used in a given test plan (e.g. as defined as makefile targets in a 'make test' run.
	// It may be that some options listed here are not compatible with recent versions of java.
	private enum Modes {
        SCM01("-Xshareclasses:name=${cacheName},cacheDir=${cacheDir}${cacheOperation}"), // default option
        SCM02("-Xshareclasses:name=${cacheName},cacheDir=${cacheDir}${cacheOperation},noClasspathCacheing"), 
        SCM03("-Xshareclasses:name=${cacheName},cacheDir=${cacheDir}${cacheOperation},noIncrementalUpdates"), // No longer in use as noIncrementalUpdates is deprecated
        SCM04("-Xshareclasses:name=${cacheName},cacheDir=${cacheDir}${cacheOperation},safemode"), // This mode required a pre-populated cache - no longer a valid option
        SCM05("-Xshareclasses:name=${cacheName},cacheDir=${cacheDir}${cacheOperation},noaot"), 
        SCM06("-Xshareclasses:name=${cacheName},cacheDir=${cacheDir}${cacheOperation} -Xaot:forceAoT,count=0"), 
        SCM07("-Xshareclasses:name=${cacheName},cacheDir=${cacheDir}${cacheOperation} -Xits0"), // Java6 only, as -Xits has been replaced with -Xitsn and -Xitn for the 2.6 J9 JVM
        SCM08("-Xshareclasses:name=${cacheName},cacheDir=${cacheDir}${cacheOperation} -Xits2000"), // Java6 only, as -Xits has been replaced with -Xitsn and -Xitn for the 2.6 J9 VM
        SCM09("-Xshareclasses:name=${cacheName},cacheDir=${cacheDir}${cacheOperation} -Xits50000"), // Java6 only, as -Xits has been replaced with -Xitsn and -Xitn for the 2.6 J9 VM
        SCM10("-Xshareclasses:name=${cacheName},cacheDir=${cacheDir}${cacheOperation},noReduceStoreContention"), 
        SCM11("-Xshareclasses:name=${cacheName},cacheDir=${cacheDir}${cacheOperation},modified=context1"), 
        SCM12("-Xshareclasses:name=${cacheName},cacheDir=${cacheDir}${cacheOperation} -Xtrace:maximal=all,buffers=64k -XX:fatalassert"), 
        SCM13("-Xshareclasses:name=${cacheName},cacheDir=${cacheDir}${cacheOperation} -Xint"), // -Xint on the command line should override -Xjit from the IBM_JAVA_OPTIONS env variable
        SCM14("-Xshareclasses:name=${cacheName},cacheDir=${cacheDir}${cacheOperation} -Xnojit"), 
        SCM15("-Xshareclasses:name=${cacheName},cacheDir=${cacheDir}${cacheOperation},nonpersistent"), 
        SCM16("-Xshareclasses:name=${cacheName},cacheDir=${cacheDir}${cacheOperation} -Xquickstart"),
        SCM17("-Xshareclasses:name=${cacheName},cacheDir=${cacheDir}${cacheOperation} -Xscdmx0m"), 
        SCM18("-Xshareclasses:name=${cacheName},cacheDir=${cacheDir}${cacheOperation} -Xitn0 -Xitsn0"), 
        SCM19("-Xshareclasses:name=${cacheName},cacheDir=${cacheDir}${cacheOperation} -Xitn0 -Xitsn29179"), // 29179 was the highest supported value at Nov 2010
        SCM20("-Xshareclasses:name=${cacheName},cacheDir=${cacheDir}${cacheOperation} -Xitn5000 -Xitsn0"), // default value for itn was 2000 at Nov 2010
        SCM21("-Xshareclasses:name=${cacheName},cacheDir=${cacheDir}${cacheOperation},nojitdata"), 
        SCM22("-Xshareclasses:name=${cacheName},cacheDir=${cacheDir}${cacheOperation} -Xscdmx18m"),
        SCM23("-Xshareclasses:name=${cacheName},cacheDir=${cacheDir}${cacheOperation} -Xaot:forceAoT,count=1"), 
        SCM24("-Xshareclasses:name=${cacheName},cacheDir=${cacheDir}${cacheOperation},mprotect=onfind"), // Was used to test that the last partially filled pages in the shared cache are kept write protected
        noSC(""); 
		
        private String sharedClassesModeStr;
	
        private Modes(String sharedClassesModeStr) {
        	this.sharedClassesModeStr = sharedClassesModeStr;
        }
	}
	
	
	private Tests scTest;
	private Modes mode;

	private String scOptions;
	private String[] defaultScOptions;
	
	private String cacheDir;
	
	private String localSharedClassesResources;

	
	public void help(HelpTextGenerator help) {
		help.outputSection("Shared Classes test options");

		help.outputArgName("sharedClassTest", "TEST");
		help.outputArgDesc("This is the Shared Classes test, e.g. :\n"
				+ "'SingleCL' - Single ClassLoader test that loads 20000 classes from a single jar file \n"
				+ "'MultiCL' - Multiple ClassLoader is a repeat of the above but uses one class loader per class \n"
				+ "'MultiJar' - Multiple jar test that uses 10000 jars, each with 2 classes, rather than 1 jar file with 20000 classes \n"
				+ "'MultiThread' - Multiple thread test that uses a single jar on a single class loader on multiple threads \n"
				+ "'MultiThreadMultiCL' - Multiple thread / Multi Classloader does similarly but uses multiple class "
				+ "loaders on multiple threads ");
		
		help.outputArgName("sharedClassMode", "MODE");
		help.outputArgDesc("This is the mode of the Shared Classes test, each mode uses a combination of Shared Classes options to run. "
				+ "The modes include a noSC mode (with no shared classes options) and range from SCM01 to SCM24, e.g. :\n"
				+ "'SCM01' - -Xshareclasses:name=<name>,cacheDir=<dir> \n"
				+ "'SCM05' - -Xshareclasses:name=<name>,cacheDir=<dir>,noaot \n"				
				+ "etc... See SharedClasses.java for the full list of modes and their respective options.");
	}

	
	public void pluginInit(StfCoreExtension test, StfSharedClassesExtension sharedClasses) throws Exception {
		// Find out which shared classes test and mode have been supplied.
		StfTestArguments testArgs = test.env().getTestProperties("sharedClassTest", "sharedClassMode");
		
		// Get the shared classes options from the supplied args.	
		scTest = testArgs.decodeEnum("sharedClassTest", Tests.class);
		mode = testArgs.decodeEnum("sharedClassMode", Modes.class);
		
		cacheDir = test.env().getResultsDir().childDirectory("caches").toString();
		String cacheOperation = "";
        
        // Get the shared classes JVM options from the mode.
		scOptions = mode.sharedClassesModeStr;
		
		// And the (default) shared classes JVM options without the cache operation.
		defaultScOptions = sharedClasses.resolveSharedClassesOptions(scOptions, SCSoftmxTestUtil.CACHE_NAME, cacheDir, cacheOperation);
	}

	
	public void setUp(StfCoreExtension test, StfSharedClassesExtension sharedClasses) throws Exception {
		
		// See whether the test data (20000 classes and jars) already exist.
		// If not, create them in the first -systemtest-prereqs directory (or the default location if no -systemtest-prereqs were supplied).
		DirectoryRef sharedClassesDataDir = null;   // This will be initialised when we find it.
		String dataSubdir = "sharedClassesTestData/v1";
		ArrayList<DirectoryRef> prereqRoots = test.env().getPrereqRoots();
		int found = 0;
		for (int i = 0 ; (i < prereqRoots.size()) && ( found == 0 ); i++ ) {
			sharedClassesDataDir = prereqRoots.get(i).childDirectory(dataSubdir);
			if (!sharedClassesDataDir.exists()) {
				System.out.println(sharedClassesDataDir.getSpec() + " does not exist");
			}
			else {
				System.out.println(sharedClassesDataDir.getSpec() + " exists");
				found = 1;
			}
		}

		if ( found == 0 ) {
			sharedClassesDataDir = prereqRoots.get(0).childDirectory(dataSubdir);
			test.doRunForegroundProcess("Create Shared Classes jars", 
					"CSC",
					ECHO_ON,
					ExpectedOutcome.cleanRun().within("30m"), 
					test.createJavaProcessDefinition()
						.addProjectToClasspath("stf.core")
						.addProjectToClasspath("openj9.test.sharedClasses")
						.runClass(JavaGen.class)
						.addArg(sharedClassesDataDir.getSpec())
						.addArg("10000"));
		}
		
		// Copy the shared classes jar/s from the systemtest_prereqs directory to /tmp.
		// This is in case the prereqs directory is on a mount which would cause the tests to run much more slowly.
		if (scTest.usesMultipleJars) {	
			DirectoryRef appsSharedClassesJarsDir = sharedClassesDataDir.childDirectory("jars");
			DirectoryRef localSharedClassesJarsDir = test.doCpDir("Copy sharedClasses jars", appsSharedClassesJarsDir, test.env().getTmpDir().childDirectory("jars"));
			localSharedClassesResources = localSharedClassesJarsDir.getSpec();
		} else {
			FileRef sharedClassesJar = sharedClassesDataDir.childFile("classes.jar");
			FileRef localSharedClassesJar = test.doCp("Copy sharedClasses jar", sharedClassesJar, test.env().getTmpDir());
			localSharedClassesResources = localSharedClassesJar.getSpec();
		}
		
		// To ensure we run from a clean state, attempt to destroy all test related persistent/non-persistent caches 
		// from the default cache location which may have been left behind by a previous failed test.
		sharedClasses.doDestroySpecificCache("Destroy cache", "-Xshareclasses:name=" + SCSoftmxTestUtil.CACHE_NAME + ",cacheDir=" + cacheDir + "${cacheOperation}", SCSoftmxTestUtil.CACHE_NAME, cacheDir);
		sharedClasses.doDestroySpecificNonPersistentCache("Destroy cache", "-Xshareclasses:name=" + SCSoftmxTestUtil.CACHE_NAME + ",cacheDir=" + cacheDir + "${cacheOperation}", SCSoftmxTestUtil.CACHE_NAME, cacheDir);
	}


	public void execute(StfCoreExtension test, StfSharedClassesExtension sharedClasses) throws Exception {
		// Abort if we are not running on IBM Java
		test.env().verifyUsingIBMJava();
		
		// Reset/create test-specific cache.
		sharedClasses.doResetSharedClassesCache("Reset Shared Classes Cache", scOptions, SCSoftmxTestUtil.CACHE_NAME, cacheDir);

		// Launch 5 Java processes concurrently to populate the Shared Classes cache.
		String comment = "Start java processes using " + scTest.testClass.getSimpleName();
		test.doRunForegroundProcesses(comment, scTest.mnemonic, 5, ECHO_ON, ExpectedOutcome.cleanRun().within("2h"), 
				test.createJavaProcessDefinition()
					.addJvmOption(defaultScOptions)
					.addProjectToClasspath("openj9.test.sharedClasses")
					.runClass(scTest.testClass)
					.addArg(localSharedClassesResources)
					.addArg(scTest.classArgs));

		// Launch 5 Java processes concurrently to load from the Shared Classes cache.
		test.doRunForegroundProcesses(comment, scTest.mnemonic, 5, ECHO_ON, ExpectedOutcome.cleanRun().within("2h"), 
				test.createJavaProcessDefinition()
					.addJvmOption(defaultScOptions)
					.addProjectToClasspath("openj9.test.sharedClasses")
					.runClass(scTest.testClass)
					.addArg(localSharedClassesResources)
					.addArg(scTest.classArgs));
		
		// Ensure no cache is found if the noSC (no shared classes) mode is used 
		// else print the cache and check the output to ensure the cache has been created/populated.
		if (mode == Modes.noSC) {
			sharedClasses.doVerifySharedClassesCache("Ensure no cache is found", "-Xshareclasses" + "${cacheOperation}", SCSoftmxTestUtil.CACHE_NAME, cacheDir, "", 0);
		} else {
			String[] expectedMessages = {"Cache is (100%|[1-9][0-9]%|[1-9]%) (soft )*full"}; // Ensure the cache is 1-100% 'full' or 'soft full' (in case of Java 10 and up)
			sharedClasses.doPrintAndVerifyCache("Print Shared Classes Cache Stats", scOptions, SCSoftmxTestUtil.CACHE_NAME, cacheDir, expectedMessages);
		}
	}

	
	public void tearDown(StfCoreExtension test, StfSharedClassesExtension sharedClasses) throws Exception {
		// Destroy all caches created by the test 
		sharedClasses.doDestroySpecificCache("Destroy cache", "-Xshareclasses:name=" + SCSoftmxTestUtil.CACHE_NAME + ",cacheDir=" + cacheDir + "${cacheOperation}", SCSoftmxTestUtil.CACHE_NAME, cacheDir);
		sharedClasses.doDestroySpecificNonPersistentCache("Destroy cache", "-Xshareclasses:name=" + SCSoftmxTestUtil.CACHE_NAME + ",cacheDir=" + cacheDir + "${cacheOperation}", SCSoftmxTestUtil.CACHE_NAME, cacheDir);
	}
}
