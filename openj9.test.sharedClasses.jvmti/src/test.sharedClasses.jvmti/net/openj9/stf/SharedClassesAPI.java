/*******************************************************************************
* Copyright (c) 2016, 2024 IBM Corp. and others
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
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0 OR GPL-2.0-only WITH Classpath-exception-2.0 OR GPL-2.0-only WITH OpenJDK-assembly-exception-1.0
*******************************************************************************/

package net.openj9.stf;

import static net.adoptopenjdk.stf.extensions.core.StfCoreExtension.Echo.ECHO_OFF;
import static net.adoptopenjdk.stf.extensions.core.StfCoreExtension.Echo.ECHO_ON;

import java.util.ArrayList;
import java.util.Iterator;

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
		DefaultLocGrpAccessJava		(		 5,	    "java",    true,     false,    	true,	   false),
		DefaultLocUtilitiesJava		(		 5,	    "java",    true,	 true,     	false,	   false),		
		DefinedLocGrpAccessJava		(		 4,	    "java",    false,	 false,    	true,	   false),
		DefinedLocUtilitiesJava		(		 4,	    "java",    false,	 true,     	false,	   false),
		DefinedLocFromCmdLineJava	(	 	 5,	    "java",    false,	 false,    	true,	   true),    
		DefaultLocGrpAccessJVMTI	(	 	 5,	    "jvmti",   true,	 false,    	true,	   false),
		DefaultLocUtilitiesJVMTI	(		 4,	    "jvmti",   true,	 true,     	false,	   false),		
		DefinedLocGroupAccessJVMTI	(	 	 4,	    "jvmti",   false,	 false,    	true,	   false),
		DefinedLocUtilitiesJVMTI	(		 4,	    "jvmti",   false,	 true,     	false,	   false),
		DefinedLocFromCmdLineJVMTI	( 		 5,	    "jvmti",   false,	 false,    	true,	   true);

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
	
	private ArrayList<String> testCachesCreatedInDefaultLocation = new ArrayList<String>(); 
	private ArrayList<String> wlCacheNameRegistry = new ArrayList<String>(); 
	
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
		cacheDirLocation = test.env().getResultsDir();
		
		// Define the location for the configuration files.
		configDirLocation = test.env().getResultsDir().childDirectory("config");		
	}

	
	public void setUp(StfCoreExtension test, StfSharedClassesExtension sharedClasses) throws Exception {
		// Create the directories for the caches and configuration files.
		test.doMkdir("Create the cache directory", cacheDirLocation);
		test.doMkdir("Create the config directory", configDirLocation);
		
		// Destroy all test specific caches from the test specific cacheDir to begin with a clean slate
		sharedClasses.doDestroyAllPersistentCachesInCacheDir("Destroy all persistent caches in test cacheDir", cacheDirLocation.getSpec());
		sharedClasses.doDestroyAllNonPersistentCachesInCacheDir("Destroy all nonpersistent caches in test cacheDir", cacheDirLocation.getSpec());	
	}


	public void execute(StfCoreExtension test, StfSharedClassesExtension sharedClasses) throws Exception {
		// Abort if we are not running on IBM Java
		test.env().verifyUsingIBMJava();

		for (Tests apiTest : Tests.values()) {
			String commentPrefix = apiTest.name() + ": ";
			String cacheName = apiTest.name() + "Iterator";
			
			String cacheDir = "";	
			if (!apiTest.usesDefaultLocation) {
				cacheDir= "cacheDir=" + cacheDirLocation.toString();
			} else {
				testCachesCreatedInDefaultLocation.add(cacheName);
			}
			
			String sharedClassesOption = "-Xshareclasses";
			String configCacheLocation = cacheDir;
			if (apiTest.usesIteratorCache) {
				// When usesIteratorCache is true, an additional cache is created when the JVM runs during the
				// verification stage. In addition, the expected cache location for all caches is provided in the 
				// command line to the class, rather than going by the cacheDir parameter in the configuration file.
				configCacheLocation = "cacheDir=" + cacheDir; // This ensures the SharedClassesCacheChecker class is working as expected.
				sharedClassesOption += ":";
				sharedClassesOption += (apiTest.usesGroupAccess ? "groupAccess," : "");
				sharedClassesOption += (cacheDir.isEmpty()? "" : (cacheDir + ","));
				sharedClassesOption +=  "name=" +  cacheName;
			} else {
				cacheName = apiTest.name() + "NoIterator";
				if (apiTest.usesGroupAccess) {
					if (apiTest.usesUtilities) {
						sharedClassesOption = "-Xshareclasses:name=" + cacheName + ",groupAccess,utilities";
					} else {
						sharedClassesOption = "-Xshareclasses:name=" + cacheName + ",groupAccess";
					}
				} else {
					if (apiTest.usesUtilities) {
						sharedClassesOption = "-Xshareclasses:name=" + cacheName + ",utilities";
					} else {
						/* do nothing, sharedClassesOption is -Xshareclasses */
					}
				}
			}
			
			// Start a workload process for each cache.
			StfProcess wl1 = startWorkload(test, apiTest, "WL1", cacheDir, "persistent", sharedClasses);
			StfProcess wl2 = startWorkload(test, apiTest, "WL2", cacheDir, "persistent", sharedClasses);
			StfProcess wl3 = startWorkload(test, apiTest, "WL3", cacheDir, "nonpersistent", sharedClasses);
			StfProcess wl4 = startWorkload(test, apiTest, "WL4", cacheDir, "nonpersistent", sharedClasses);

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
							.addJvmOption(sharedClassesOption)
							.addJvmOption("-DconfigFile=" + configFile.getSpec())
							.addJvmOption("-DwlCacheList=" + wlCacheListToString())
							.addProjectToClasspath("openj9.test.sharedClasses.jvmti")
							.runClass(SharedClassesCacheChecker.class));
			} else {
				// Verify caches using a JVMTI native agent.
				String nativeExt;
				if (PlatformFinder.isOSX()) {
					nativeExt = ".dylib";
				} else if (PlatformFinder.isWindows()) {
					nativeExt = ".dll";
				} else {
					nativeExt = ".so";
				}
				String nativePrefix =  PlatformFinder.isWindows() ? "" : "lib";
				FileRef agent = test.env().findTestDirectory("openj9.test.sharedClasses.jvmti/bin/native")
						.childDirectory(test.env().getPlatformSimple())
						.childFile(nativePrefix + "sharedClasses" + nativeExt);
				
				if (!cacheDir.isEmpty()) {
					cacheDir = "," + cacheDir;
				}
				String agentOptions = "expectedCacheCount=" + apiTest.expectedCacheCount + ","
						+ "deleteCaches=true,"
						+ "cachePrefix=" + apiTest.name() + cacheDir;
				
				sharedClasses.doVerifyCachesUsingJVMTI(commentPrefix + "Verify caches using JVMTI",
						apiTest.name(), 
						sharedClassesOption,
						"-agentpath:" + agent + "=" + agentOptions);
			}
		}
	}

	// This method starts a workload for a specific cache
	private StfProcess startWorkload(StfCoreExtension test, Tests apiTest, String workloadName, String cacheDir, String persistantStatus, StfSharedClassesExtension sharedClasses) throws Exception {
		String groupAccess	= (apiTest.usesGroupAccess ? ",groupAccess" : "");
		String cacheName = apiTest.name() + workloadName;	
		this.wlCacheNameRegistry.add(cacheName); 
		String inventoryFile = "/openjdk.test.load/config/inventories/mix/mini-mix.xml";
		int totalTests = InventoryData.getNumberOfTests(test, inventoryFile);
		if (!cacheDir.isEmpty()) {
			cacheDir = cacheDir + ",";
		}
		LoadTestProcessDefinition loadTestSpecification = test.createLoadTestSpecification()
				.addJvmOption("-Xshareclasses:name=" + cacheName + "," + cacheDir + persistantStatus + groupAccess)
				.addPrereqJarToClasspath(JavaProcessDefinition.JarId.JUNIT)
				.addPrereqJarToClasspath(JavaProcessDefinition.JarId.HAMCREST)
				.addProjectToClasspath("openjdk.test.lang")  // For mini-mix inventory
				.addProjectToClasspath("openjdk.test.util")  // For mini-mix inventory
				.addProjectToClasspath("openjdk.test.math")  // For mini-mix inventory
				.generateCoreDumpAtFirstLoadTestFailure(false)
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
	
	// Creates a delimited list of workload cache names
	// to be passed to SharedClassesCacheChecker 
	private String wlCacheListToString() {
		String strList = "";
		Iterator<String> i = this.wlCacheNameRegistry.iterator();
		while(i.hasNext()) {
			String aCacheName = i.next(); 
			if (strList.length() == 0) {
				strList = aCacheName;
			} else {
				strList = strList + "--" + aCacheName;
			}
		}
		// clean up for next iteration 
		this.wlCacheNameRegistry.clear(); 
		return strList; 
	}

	
	public void tearDown(StfCoreExtension test, StfSharedClassesExtension sharedClasses) throws Exception {
		// Destroy all test specific persistent/non-persistent caches from the test cacheDir location which may
		// have been left behind by a failure. We don't care about caches left behind in results
		sharedClasses.doDestroyAllPersistentCachesInCacheDir("Destroy all persistent caches in test cacheDir", cacheDirLocation.getSpec());
		sharedClasses.doDestroyAllNonPersistentCachesInCacheDir("Destroy all nonpersistent caches in test cacheDir", cacheDirLocation.getSpec());
		
		// Destroy all test specific caches that may have been left in the default location 
		Iterator<String> i = testCachesCreatedInDefaultLocation.iterator(); 
		while (i.hasNext()) {
			String cacheName = i.next();
			sharedClasses.doDestroySpecificCache("Destroy test specific persistent cache from default location", "-Xshareclasses:name=" + cacheName + "${cacheOperation}", cacheName, "");
			sharedClasses.doDestroySpecificNonPersistentCache("Destroy test specific non-persistent cache from default location", "-Xshareclasses:name=" + cacheName + "${cacheOperation}", cacheName, "");
		}
	}
}
