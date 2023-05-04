/*******************************************************************************
* Copyright (c) 2016, 2023 IBM Corp. and others
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

package net.openj9.sc.api;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.ibm.oti.shared.SharedClassCacheInfo;
import com.ibm.oti.shared.SharedClassUtilities;

/**
 * A class that runs some of the SharedClasses util methods
 * provided by the JVM and checks that the results are
 * sensible.
 * 
 * Can also read in data from properties files describing what
 * caches are expected to exist, and verifying that the utility
 * methods find the same caches.
 * 
 * The main configuration file is looked for in a system property
 * 
 *  java -DconfigFile=/bob/main.props SharedClassesCacheChecker
 *  
 * This file will be searched for the following properties:
 *   cacheDir : The directory to search for caches. This will be ignored if commandLineValues prop is true
 *   commandLineValues : Use values from the command line to search for caches. Overrides the cacheDir property
 *   delete   : If 'true', then on exit, attempt to delete all caches, by using the utility APIs 
 *   expectedCacheCount : The number of caches that should be found
 *   cacheFiles : A space separated list of properties file names, which should
 *                containing detail about each of the caches that are expected
 *                to be found 
 *                
 * The properties files describing the individual caches will be searched for
 * the following properties:
 * 
 *   name : The name of the cache
 *   persistence: if 'true', the cache is expected to be persistent
 *
 */
public class SharedClassesCacheChecker {

	
	private static final String CACHE_DIR_PROP = "cacheDir";

	private static final String CONFIG_FILE_PROP = "configFile";
	
	private static final String WORKLOAD_CACHE_LIST_PROP = "wlCacheList";

	private static final String EXPECTED_CACHE_COUNT_PROP = "expectedCacheCount";
	
	private static final String COMMAND_LINE_VALUES_PROP = "commandLineValues";
	
	private Logger logger = Logger.getLogger("net.openj9.sc.api");
	
	/** if not null, this will be the directory to search for caches */
	private String cacheDir;
	
	/** If true, then attempt to delete caches as the last step */
	private boolean delete = false; 
	
	/** If true, the command line value for the cache directory will be use (-Xshareclasses:cacheDir=....) */
	private boolean commandLineValues = false;
	
	// All properties from the main config file
	private Properties config;
	
	// Details of the caches we expect
	private HashMap<String, ExpectedCacheData> expectedData;

	// The retrieved list of shared classes caches
	List<SharedClassCacheInfo> caches;
	
	// This list is to keep track of caches that have already successfully been deleted
	// So that we don't attempt to delete it more than once 
	ArrayList<String> alreadySuccesfullyDeletedCaches;
	
	// Name of the Shared Classes Cache 
	private List<String> workloadCacheList; 
	
	public SharedClassesCacheChecker(Properties config, List<String> wlCacheList) {
		this.config = config;
		this.workloadCacheList = wlCacheList; 
		cacheDir = config.getProperty(CACHE_DIR_PROP);
		if (cacheDir == null || cacheDir.equals("default")) {
			logger.info("Using default cache directory");
			cacheDir = null;
		} else {
			logger.info("Using cache directory '" + cacheDir + "'");
		}
		
		String deleteString = config.getProperty("delete");
		if (deleteString != null) {
			logger.info("Found delete property in config file with value: " + deleteString);
			this.delete = Boolean.parseBoolean(deleteString); // parseBoolean returns false on failure which is what we want			
		}
		if (this.delete) {
			logger.info("Will attempt to delete caches after iteration/verification");
		} else {
			logger.info("Caches will be _not_ be deleted after iteration/verification");
		}
		
		String commandLineValuesString = config.getProperty(COMMAND_LINE_VALUES_PROP);
		if (commandLineValuesString != null) {
			logger.info("Found " + COMMAND_LINE_VALUES_PROP + " in config file with value: " + commandLineValuesString);
			this.commandLineValues = Boolean.parseBoolean(commandLineValuesString);
		}
		if (this.commandLineValues) {
			logger.info("Will use command line values to search for shared classes caches");
		} else {
			logger.info("Command line value for shared classes directory will be ignored during search for caches");
		}
		
		this.caches = SharedClassUtilities.getSharedCacheInfo(cacheDir, SharedClassUtilities.NO_FLAGS, commandLineValues);		
		this.alreadySuccesfullyDeletedCaches = new ArrayList<String>(); 

	}

	/**
	 * Try and delete all the caches we know about from our query.
	 */
	@SuppressWarnings("deprecation")
	boolean delete() {
		if (!delete) {
			return true;
		}
		
		boolean rv = true;

		for (SharedClassCacheInfo info: this.caches) {		
			// We want to only delete the workload caches here.
			// Any cache that might be listed in the default location - created 
			// or owned by other processes should be ignored. 
			// Cache created and used by the SharedClassesCacheChecker process 
			// can not be deleted by itself either, so must be ignored here. 
			// Those are deleted in SharedClassesAPI tearDown() method 
			String cacheName = info.getCacheName();
			if (cacheName == null) {
				continue; 
			} else if (!workloadCacheList.contains(cacheName)) {
				continue; 
			} else if (this.alreadySuccesfullyDeletedCaches.contains(cacheName)) {
				continue; 			
			} else { 
				int persistence = SharedClassUtilities.NONPERSISTENT;
				if (info.isCachePersistent()) {
					persistence = SharedClassUtilities.PERSISTENT;
				}
				int answer = SharedClassUtilities.destroySharedCache(this.cacheDir,
						persistence, cacheName, false);
				logger.info("Attempting to delete cache: " + cacheName
						+ " and return value from delete call was: " + answer);
				
				switch (answer) {
				case SharedClassUtilities.DESTROYED_ALL_CACHE:
					logger.info("Return value means destroyed all caches");
					break;
				case SharedClassUtilities.DESTROYED_NONE:
					logger.info("Return value means no caches destroyed");
					rv = false;
					break;
				case SharedClassUtilities.DESTROY_FAILED_CURRENT_GEN_CACHE:
					logger.info("Return value means DESTROY_FAILED_CURRENT_GEN_CACHE");
					rv = false;
					break;
				case SharedClassUtilities.DESTROY_FAILED_OLDER_GEN_CACHE:
					logger.info("Return value means DESTROY_FAILED_OLDER_GEN_CACHE");
					rv = false;
					break;
				default:
					logger.info("Return value is unknown");
					rv = false;
					break;
				}
				this.alreadySuccesfullyDeletedCaches.add(cacheName); 
				break; 
			}
			
		}
		return rv;
	}
	
	public String verify() {
		String rv = "";		
		
		logger.info("Found " + caches.size() + " caches");
		
		String expectedCacheCount = config.getProperty(EXPECTED_CACHE_COUNT_PROP);
		logger.info("Checking cache count matches expected value (" + expectedCacheCount + ")");
		
		int expectedCacheCountInt = -1;
		try {
			expectedCacheCountInt = Integer.parseInt(expectedCacheCount);
		} catch (NumberFormatException e) {
			logger.info("WARNING: Can't check number of caches as expectedCacheCount in config file is not numeric. Was: "
					+ expectedCacheCount);
		}
		
		if (expectedCacheCountInt != -1){
			if (caches.size() != expectedCacheCountInt) {
				/* comment out the following lines for now, needs to be re-enable when fixed. See https://github.com/eclipse-openj9/openj9-systemtest/issues/47 */
				/* String error = "ERROR: found " + caches.size() + " caches. Expected " + expectedCacheCount + " caches";
				logger.severe(error);
				rv = rv + error + "\n"; */
			} else {
				logger.info("Expected number of caches found (" + expectedCacheCount + ")");
			}
		}
		
		int i = 0;
		for (SharedClassCacheInfo info: caches) {
			
			// We only want to verify the workload caches 
			// Any cache that might be listed in the default location - created 
			// or owned by other processes should be ignored. 
			if (info.getCacheName() == null) {
				continue; 
			} else if (!workloadCacheList.contains(info.getCacheName())) {
				continue; 
			}
			
			i++;
			logger.info("\nDetails for Cache " + i + "\n");
			String name = info.getCacheName();
			logger.info("cache name is " + name);
			
			if (expectedData != null) {
				ExpectedCacheData currentCacheData = expectedData.get(name);
				if (currentCacheData != null) {
					boolean result = verifyAgainstExpectedData(info, currentCacheData);
					if (!result) {
						rv = "Verifying cache " + name + " against expected data from properties file failed\n";
					}
				}
			}

			
			int mode = info.getCacheAddressMode();
			if (mode == SharedClassCacheInfo.ADDRESS_MODE_32) {
				logger.info("address Mode is ADDRESS_MODE_32 (" + mode + ")");
			} else if (mode == SharedClassCacheInfo.ADDRESS_MODE_64){
				logger.info("address Mode is ADDRESS_MODE_64 (" + mode + ")");
			} else {
				String error = "ERROR: addressing mode is unknown: " + mode; 
				logger.severe(error);
				rv = rv + error + "\n";
			}
			
			
			long freeBytes = info.getCacheFreeBytes();
			logger.info("free bytes is " + freeBytes);
			
			int jvmLevel = info.getCacheJVMLevel();

			/* Just print out the JVM level to avoid compile failures if compiled against
			 * earlier versions of java.
			 */
			logger.info("jvmLevel is " + jvmLevel);

			/*
			if (jvmLevel == SharedClassCacheInfo.JVMLEVEL_JAVA5) {
				logger.info("jvm level is JVMLEVEL_JAVA5: " + jvmLevel);
			} else if (jvmLevel == SharedClassCacheInfo.JVMLEVEL_JAVA6) {
				logger.info("jvm level is JVMLEVEL_JAVA6: " + jvmLevel);
			} else if (jvmLevel == SharedClassCacheInfo.JVMLEVEL_JAVA7) {
				logger.info("jvm level is JVMLEVEL_JAVA7: " + jvmLevel);
			} else if (jvmLevel == SharedClassCacheInfo.JVMLEVEL_JAVA8) {
				logger.info("jvm level is JVMLEVEL_JAVA8: " + jvmLevel);
			} else if (jvmLevel == SharedClassCacheInfo.JVMLEVEL_JAVA9) {
				logger.info("jvm level is JVMLEVEL_JAVA9: " + jvmLevel);
			} else {
				String error = "ERROR: jvm level is unknown: " + jvmLevel;
				logger.severe(error);
				rv = rv + error + "\n";
			}
			*/
			
			long longSize =  info.getCacheSize();
			logger.info("cache size is " + longSize);
			
			Date detachDate = info.getLastDetach();
			if(detachDate == null){
				logger.info("LastDetach date unavailable hence detach date is null");
			}
			else {
				logger.info("detach date is " + detachDate.toString());
			}
			
			int semaphore = info.getOSsemid();
			logger.info("sempahore id is " + semaphore);
			
			int sharedMem = info.getOSshmid();
			logger.info("shared memory id is " + sharedMem);
			
			boolean compatible = info.isCacheCompatible();
			logger.info("is cache compatible? " + compatible);
			
			boolean corrupt = info.isCacheCorrupt();
			logger.info("is cache corrupt? " + corrupt);
			
			@SuppressWarnings("deprecation")
			boolean persistent = info.isCachePersistent();
			logger.info("is cache persistent? " + persistent);
			
		}
		if (rv.equals("")) {
			return null;
		}
		return rv;
	}

	/**
	 * Read the list of cache files from the main configuration properties
	 * Load the named files, and store in expectedData
	 * @throws IOException
	 */
	public void loadExpectedCacheData() throws IOException {
		String cacheFiles = config.getProperty("cacheFiles");
		if (cacheFiles == null) {
			logger.info("No data given to verify caches. Data will just be listed");
			return;
		} else {
			logger.info("Found list of properties files with cache details: " + cacheFiles);
		}
		
		StringTokenizer fileNameTokenizer = new StringTokenizer(cacheFiles);
		int fileCount = fileNameTokenizer.countTokens();
		expectedData = new HashMap<String,ExpectedCacheData>(fileCount);
		while (fileNameTokenizer.hasMoreTokens()) {
			ExpectedCacheData data = new ExpectedCacheData(fileNameTokenizer.nextToken());
			expectedData.put(data.getName(), data);
		}
	}
	

	/**
	 * Check that the cache data in info is the same as the expectedData 
	 */
	@SuppressWarnings("deprecation")
	private boolean verifyAgainstExpectedData(SharedClassCacheInfo info, ExpectedCacheData expectedData) {
		String name = info.getCacheName();
		if (info.isCachePersistent() != expectedData.isPersistent()) {
			logger.severe(
					"persistence of cache " + name + " is " + info.isCachePersistent() + "\n"
					+ "expected: " + expectedData.isPersistent());
			return false;
		}
		
		logger.info("cache " + name + " verified correctly against data from file " + expectedData.getFileName());
		
		return true;
	}

	
	
	
	public static void main(String[] args) {
		
		String configFile = System.getProperty(CONFIG_FILE_PROP);
		if (configFile == null) {
			System.out.println("No config file name supplied via system property 'configFile'");
			System.exit(1);
		}
		
		List<String> wlCacheList = new ArrayList<String>(); 
		String listAsString = System.getProperty(WORKLOAD_CACHE_LIST_PROP);
		if (listAsString == null) {
			System.out.println("No workload cache list supplied via system property '"+ WORKLOAD_CACHE_LIST_PROP +"'");
			System.exit(1);
		} else {
			wlCacheList = Arrays.asList(listAsString.split("--")); 
		}

		Properties config = new Properties();
		try {
			config.load(new FileInputStream(configFile));
		} catch (IOException e) {
			System.out.println("Couldn't load config file " + configFile);
			System.out.println(e.getMessage());
			System.exit(1);
		}
		
		
		SharedClassesCacheChecker checker = new SharedClassesCacheChecker(config, wlCacheList);
		try {
			checker.loadExpectedCacheData();
		} catch (IOException e) {
			System.out.println("Couldn't load expected cache data: " + e.getMessage());
			System.exit(1);
		}
		
		
		String errors = checker.verify();

		if (errors != null) {
			System.out.println("************\nVERIFICATION FAILED\n************");
			System.out.println(errors);
			System.exit(1);
		}

		boolean deleteDone = checker.delete();
		if (!deleteDone) {
			System.out.println("************\nDELETION FAILED\n************");
			System.exit(1);
		}

		System.out.println("************\nCacheAPIChecker COMPLETED SUCCESSFULLY\n************");
		System.exit(0);
	}

	
	
	/**
	 * Represents data read from a properties file describing what
	 * we expect to find in a shared classes cache.
	 */
	private static class ExpectedCacheData {
		private boolean persistence;
		private String name;
		private String fileName;

		// The names of the various properties that we expect to find in the properties file
		public static final String PERSISTENCE_PROP = "persistence";
		public static final String NAME_PROP = "name";
		
		public ExpectedCacheData (String dataFile) throws IOException {
			Properties config = new Properties();
			config.load(new FileInputStream(dataFile));

			fileName = dataFile;
			persistence = Boolean.parseBoolean(config.getProperty(PERSISTENCE_PROP));			
			name = config.getProperty(NAME_PROP);

		}
		
		public boolean isPersistent() {
			return persistence;
		}
		
		public String getName() {
			return name;
		}
		
		public String getFileName() {
			return fileName;
		}
		
	}

}
