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
import static net.adoptopenjdk.stf.extensions.core.StfCoreExtension.Echo.ECHO_ON;

import net.adoptopenjdk.loadTest.InventoryData;
import net.openj9.sc.api.SharedClassesCacheChecker;
import net.adoptopenjdk.stf.environment.DirectoryRef;
import net.adoptopenjdk.stf.environment.FileRef;
import net.adoptopenjdk.stf.environment.PlatformFinder;
import net.adoptopenjdk.stf.extensions.core.StfCoreExtension;
import net.adoptopenjdk.stf.processes.ExpectedOutcome;
import net.adoptopenjdk.stf.processes.StfProcess;
import net.adoptopenjdk.stf.processes.definitions.JavaProcessDefinition;
import net.adoptopenjdk.stf.processes.definitions.LoadTestProcessDefinition;
import net.adoptopenjdk.stf.runner.modes.HelpTextGenerator;
import net.openj9.stf.sharedClasses.SharedClassesPluginInterface;
import net.openj9.stf.sharedClasses.StfSharedClassesExtension;

/**
 * This test plugin is aimed at stress testing the IBM Shared Classes API which allows querying the status of shared 
 * classes caches.
 * 
 * The test plugin runs a set of tests that use various combinations of shared classes options and cache locations, 
 * the tests are then repeated for each API type (Java or JVMTI).
 * The Java API is accessed through the package com.ibm.oti.shared via the SharedClassesCacheChecker class
 * and the native API is available as JVMTI extension functions. 
 * 
 * Each test starts multiple JVMs running a small mini-mix workload connecting to multiple shared classes caches and later starts 
 * another JVM to query/delete the caches using either the Java API or the JVMTI API. 
 */
public class SharedClassesAPI implements SharedClassesPluginInterface {
	// Define the internal tests and their respective arguments.
	private enum Tests {
		// 							     expected  	        usesDefault  uses 		usesGroup  uses
		// Test						    CacheCount   api     Location    Utilities  Access	   IteratorCache
		DefaultLocationGroupAccessJava(		 4,	    "java",    true,     false,    	true,	   false),
		DefaultLocationUtilitiesJava(		 4,	    "java",    true,	 true,     	false,	   false),		
		DefinedLocationGroupAccessJava(		 4,	    "java",    false,	 false,    	true,	   false),
		DefinedLocationUtilitiesJava(		 4,	    "java",    false,	 true,     	false,	   false),
		DefinedLocationFromCommandLineJava(	 5,	    "java",    false,	 false,    	true,	   true),    
		DefaultLocationGroupAccessJVMTI(	 4,	    "jvmti",   true,	 false,    	true,	   false),
		DefaultLocationUtilitiesJVMTI(		 4,	    "jvmti",   true,	 true,     	false,	   false),		
		DefinedLocationGroupAccessJVMTI(	 4,	    "jvmti",   false,	 false,    	true,	   false),
		DefinedLocationUtilitiesJVMTI(		 4,	    "jvmti",   false,	 true,     	false,	   false),
		DefinedLocationFromCommandLineJVMTI( 5,	    "jvmti",   false,	 false,    	true,	   true);

		private int expectedCacheCount;
		private String api;
		private boolean usesDefaultLocation;
		private boolean usesUtilities;
		private boolean usesGroupAccess;
		private boolean usesIteratorCache;
		
		private Tests(int expectedCacheCount, String api, boolean usesDefaultLocation, boolean usesUtilities, boolean usesGroupAccess, boolean usesIteratorCache) {
			this.expectedCacheCount = expectedCacheCount;
			this.api = api;
			this.usesDefaultLocation = usesDefaultLocation; 
			this.usesUtilities = usesUtilities;
			this.usesGroupAccess = usesGroupAccess;
			this.usesIteratorCache = usesIteratorCache;
		}
	}
	

	private DirectoryRef cacheDirLocation;
	private DirectoryRef configDirLocation;
	
	
	public void help(HelpTextGenerator help) {
		help.outputSection("Shared Classes API test");
		help.outputText("The Shared Classes API test runs a set of tests aimed at stress testing the API which allows querying the"
				+ " status of shared classes caches and their deletion.\n");
	
		help.outputText("What does each test do?");
		help.outputArgDesc("- Uses a combination of shared classes options with a pre-defined cache location (default or defined).\n"
				+ "- Starts up multiple JVMs running a mini-mix work load, connecting to multiple shared classes caches.\n"
				+ "- Starts up another JVM to query/delete the caches, using either the Java API or the JVMTI extension functions.\n");
	}

	
	public void pluginInit(StfCoreExtension test, StfSharedClassesExtension sharedClasses) throws Exception {
		// Define the location for caches.
		cacheDirLocation = test.env().getResultsDir().childDirectory("caches");
		
		// Define the location for the configuration files.
		configDirLocation = test.env().getResultsDir().childDirectory("config");		
	}

	
	public void setUp(StfCoreExtension test, StfSharedClassesExtension sharedClasses) throws Exception {
		// Create the directories for the caches and configuration files.
		test.doMkdir("Create the cache directory", cacheDirLocation);
		test.doMkdir("Create the config directory", configDirLocation);
		
		// To ensure we run from a clean state, attempt to destroy all persistent/non-persistent caches 
		// from the default cache location which may have been left behind by a previous failed test.
		sharedClasses.doDestroyAllPersistentCaches("Destroy Persistent Shared Classes Caches");
		sharedClasses.doDestroyAllNonPersistentCaches("Destroy Non-Persistent Shared Classes Caches");
	}


	public void execute(StfCoreExtension test, StfSharedClassesExtension sharedClasses) throws Exception {
		// Abort if we are not running on IBM Java
		test.env().verifyUsingIBMJava();

		for (Tests apiTest : Tests.values()) {
			String commentPrefix = apiTest.name() + ": ";
			
			String cacheDir = "";	
			if (!apiTest.usesDefaultLocation) {
				cacheDir= "cacheDir=" + cacheDirLocation;
			}
			
			String iteratorCache = "";
			String configCacheLocation = cacheDir;
			if (apiTest.usesIteratorCache) {
				// When usesIteratorCache is true, an additional cache is created when the JVM runs during the
				// verification stage. In addition, the expected cache location for all caches is provided in the 
				// command line to the class, rather than going by the cacheDir parameter in the configuration file.
				configCacheLocation = "cacheDir="; // This ensures the SharedClassesCacheChecker class is working as expected.
				iteratorCache = "-Xshareclasses:" + cacheDir + ",name=" +  apiTest.name() + "Iterator";
			}		

			String utilities = (apiTest.usesUtilities ? "-Xshareclasses:utilities" : "");
			
			// Start a workload process for each cache.
			StfProcess wl1 = startWorkload(test, apiTest, "WL1", cacheDir, ",persistent");
			StfProcess wl2 = startWorkload(test, apiTest, "WL2", cacheDir, ",persistent");
			StfProcess wl3 = startWorkload(test, apiTest, "WL3", cacheDir, ",nonpersistent");
			StfProcess wl4 = startWorkload(test, apiTest, "WL4", cacheDir, ",nonpersistent");

			// Monitor the workload processes to completion.
			test.doMonitorProcesses(commentPrefix + "Monitor workload processes", wl1, wl2, wl3, wl4);

			if (apiTest.api.equals("java")) {	
				// Now start verification of caches.
				// Firstly, create a config file for each cache.
				FileRef config1 = createConfigFile(test, apiTest, "WL1", true);
				FileRef config2 = createConfigFile(test, apiTest, "WL2", true);
				FileRef config3 = createConfigFile(test, apiTest, "WL3", false);
				FileRef config4 = createConfigFile(test, apiTest, "WL4", false);
				
				// Define the configuration file content required for the SharedClassesCacheChecker class.
				String configContent	= 
						  "expectedCacheCount=" + apiTest.expectedCacheCount + "\n"
						+ "cacheFiles=" + config1 + " " + config2 + " " + config3 + " " + config4 + "\n"
						+ "delete=true\n"
						+ "commandLineValues=" + apiTest.usesIteratorCache + "\n"
						+ configCacheLocation + "\n";
	
				// Create the configuration file required for the SharedClassesCacheChecker class.
				FileRef configFile = configDirLocation.childFile(apiTest.name() + "_config.props");
				test.doWriteFile(commentPrefix + "Create_config.props", configFile , configContent);
	
				// Run the SharedClassesCacheChecker class to validate the sharedClasses caches and delete them.
				test.doRunForegroundProcess(commentPrefix + "Check Shared Classes Caches", 
						"SCC",
						ECHO_ON,
						ExpectedOutcome.cleanRun().within("10m"), 
						test.createJavaProcessDefinition()
							.addJvmOption(utilities)
							.addJvmOption(iteratorCache)
							.addJvmOption("-DconfigFile=" + configFile.getSpec())
							.addProjectToClasspath("openj9.test.sharedClasses.jvmti")
							.runClass(SharedClassesCacheChecker.class));
			} else {
				// Verify caches using a JVMTI native agent
				String nativeExt    =  PlatformFinder.isWindows() ? ".dll" : ".so";
				String nativePrefix =  PlatformFinder.isWindows() ? "" : "lib";
				FileRef agent = test.env().findTestDirectory("openj9.test.sharedClasses.jvmti/bin/native")
						.childDirectory(test.env().getPlatform())
						.childFile(nativePrefix + "sharedClasses" + nativeExt);
				
				if (!cacheDir.isEmpty()) {
					cacheDir = "," + cacheDir;
				}
				String agentOptions = "expectedCacheCount=" + apiTest.expectedCacheCount + ","
						+ "deleteCaches=true,"
						+ "cachePrefix=" + apiTest.name() + cacheDir;
				
				sharedClasses.doVerifyCachesUsingJVMTI(commentPrefix + "Verify caches using JVMTI",
						apiTest.name(), 
						utilities,
						iteratorCache,
						"-agentpath:" + agent + "=" + agentOptions);
			}
		}
	}

	// This method starts a workload for a specific cache
	private StfProcess startWorkload(StfCoreExtension test, Tests apiTest, String workloadName, String cacheDir, String persistantStatus) throws Exception {
		String groupAccess	= (apiTest.usesGroupAccess ? ",groupAccess" : "");

		String cacheName = apiTest.name() + workloadName;
		
		String inventoryFile = "/openjdk.test.load/config/inventories/mix/mini-mix.xml";
		int totalTests = InventoryData.getNumberOfTests(test, inventoryFile);
		LoadTestProcessDefinition loadTestSpecification = test.createLoadTestSpecification()
				.addJvmOption("-Xshareclasses:name=" + cacheName + "," + cacheDir + persistantStatus + groupAccess)
				.addPrereqJarToClasspath(JavaProcessDefinition.JarId.JUNIT)
				.addPrereqJarToClasspath(JavaProcessDefinition.JarId.HAMCREST)
				.addProjectToClasspath("openjdk.test.lang")  // For mini-mix inventory
				.addProjectToClasspath("openjdk.test.util")  // For mini-mix inventory
				.addProjectToClasspath("openjdk.test.math")  // For mini-mix inventory
				.addSuite("mini-mix")
				.setSuiteInventory(inventoryFile)
				.setSuiteThreadCount(3)
				.setSuiteNumTests(totalTests)   // Should mean most of the classes are used
			   	.setSuiteRandomSelection();
		
		return test.doRunBackgroundProcess(apiTest.name() + ": Start workload for " + cacheName,
				workloadName, 
				ECHO_OFF,
				ExpectedOutcome.cleanRun().within("20m"), 
				loadTestSpecification); 
	}

	// This method creates a configuration file with basic info about a cache generated by a workload
	private FileRef createConfigFile(StfCoreExtension test, Tests apiTest, String workloadName, boolean isPersistant) 
				throws Exception {
		String cacheFileName = apiTest.name() + workloadName + ".props";
		
		// Define the configuration file content for the cache.
		String cacheConfigContent	= 
				  "name=" + cacheFileName + "\n"
				+ "persistence=" + isPersistant + "\n";
		
		// Create the cache specific configuration file
		FileRef configCacheFile = configDirLocation.childFile(cacheFileName);
		test.doWriteFile(apiTest.name() + ": Create " + cacheFileName, configCacheFile, cacheConfigContent);
		
		return configCacheFile;
	}

	
	public void tearDown(StfCoreExtension test, StfSharedClassesExtension sharedClasses) throws Exception {
		// Destroy all persistent/non-persistent caches from the default cache location which may
		// have been left behind by a failure. We don't care about caches left behind in results
		// as those will get deleted together with results.
		sharedClasses.doDestroyAllPersistentCaches("Destroy Persistent Shared Classes Caches");
		sharedClasses.doDestroyAllNonPersistentCaches("Destroy Non-Persistent Shared Classes Caches");
	}
}