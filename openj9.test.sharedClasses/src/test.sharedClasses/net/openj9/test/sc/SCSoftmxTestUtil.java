/*******************************************************************************
* Copyright (c) 2016, 2020 IBM Corp. and others
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

package net.openj9.test.sc;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

import net.adoptopenjdk.stf.StfException;
import net.adoptopenjdk.stf.extensions.core.StfCoreExtension;
import net.adoptopenjdk.stf.processes.definitions.JavaProcessDefinition;
import net.adoptopenjdk.stf.processes.definitions.LoadTestProcessDefinition;

// This class hosts some utility programs that are commonly used by the shared classes softmx tests 

public class SCSoftmxTestUtil { 
	
	public static final String SLEEP = "sleep";
    public static final String MERGE_FILE = "MergeFile";
    public static final String COMPARE_AOT_JIT_DATA_SPACE = "CompareAOTJITData";
    public static final String CACHE_NAME = "SCC_SysTest_Workload_Cache";
	
	public static void main (String [] args) throws Exception {
		String task = System.getProperty("task");
		 
		if (task.equals(SLEEP)) {
			Thread.sleep(Integer.parseInt(System.getProperty("timeinmilis")));
		}
		
		else if (task.equals(MERGE_FILE)) {
			StringBuffer output = new StringBuffer();
			Scanner scanner1 = new Scanner(new File(System.getProperty("file1")));
			Scanner scanner2 = new Scanner(new File(System.getProperty("file2")));
			File combinedFile = new File(System.getProperty("final.file")); 

			while(scanner1.hasNextLine()){
				output.append(scanner1.nextLine() + "\n");
			}

			while(scanner2.hasNextLine()){
				output.append(scanner2.nextLine() + "\n");
			}

			System.out.println(output.toString());

			PrintWriter pw = new PrintWriter(combinedFile); 
			pw.write(output.toString());
			pw.flush();
			pw.close();
			scanner1.close();
			scanner2.close();
		}
		
		else if (task.equals(COMPARE_AOT_JIT_DATA_SPACE)) {
			Scanner scanner1 = new Scanner(new File(System.getProperty("file1")));
			Scanner scanner2 = new Scanner(new File(System.getProperty("file2")));
	
			long aotValue1 = 0, aotValue2 = 0, jitdataValue1 = 0, jitdataValue2 = 0; 
	
			while(scanner1.hasNextLine()){
				String nextLine = scanner1.nextLine(); 
				if (nextLine.startsWith("AOT bytes")) {
					aotValue1 = Long.parseLong(nextLine.split("=")[1].trim()); 
				} else if (nextLine.startsWith("JIT data bytes")) {
					jitdataValue1 = Long.parseLong(nextLine.split("=")[1].trim()); 
				} else {
					continue;
				}
			}
	
			while(scanner2.hasNextLine()){
				String nextLine = scanner2.nextLine(); 
				if (nextLine.startsWith("AOT bytes")) {
					aotValue2 = Long.parseLong(nextLine.split("=")[1].trim()); 
				} else if (nextLine.startsWith("JIT data bytes")) {
					jitdataValue2 = Long.parseLong(nextLine.split("=")[1].trim()); 
				} else {
					continue;
				}
			}
	
			scanner1.close();
			scanner2.close();

			if (aotValue2 > aotValue1) {
				System.out.println("New AOT data was written to the AOT space after softmx increase");
			} else if (aotValue2 == aotValue1){
				System.out.println("AOT data remained the same after softmx increase");
			} else {
				throw new StfException("Incorrect AOT bytes value after softmx increase.\n"
						+ "Before Softmx increase, AOT data = " + aotValue1 + "\n"
								+ "After Softmx increase, AOT data = " + aotValue2);
			}
	
			if (jitdataValue2 > jitdataValue1) {
				System.out.println("New JIT data was written to the JIT space after softmx increase");
			} else if (jitdataValue2 == jitdataValue1){
				System.out.println("JIT data remained the same after softmx increase");
			} else {
				throw new StfException("Incorrect JIT bytes value after softmx increase.\n"
						+ "Before Softmx increase, JIT data = " + jitdataValue1 + "\n"
								+ "After Softmx increase, JIT data = " + jitdataValue2);
			}
		} else {
			System.out.println("Wrong task type provided");
		}
	}
	
	public static LoadTestProcessDefinition getMiniMixLoadTestOptions(LoadTestProcessDefinition initialSpec, int numberOfTests) throws StfException {
		String inventoryFile = "/openj9.test.load/config/inventories/mix/sharedclasses-mix.xml";
		int cpuCount = Runtime.getRuntime().availableProcessors();
		
		return initialSpec
			.addPrereqJarToClasspath(JavaProcessDefinition.JarId.JUNIT)
			.addPrereqJarToClasspath(JavaProcessDefinition.JarId.HAMCREST)
			.addProjectToClasspath("openjdk.test.lang")          // For mini-mix inventory
			.addProjectToClasspath("openjdk.test.util")          // For mini-mix inventory
			.addProjectToClasspath("openjdk.test.math")          // For mini-mix inventory
			.addProjectToClasspath("openjdk.test.classloading")  // For sharedclasses-mix inventory
			.addProjectToClasspath("openjdk.test.nio")           // For sharedclasses-mix inventory
			.addProjectToClasspath("openj9.test.daa")         // For sharedclasses-mix inventory
		   	.generateCoreDumpAtFirstLoadTestFailure(false)
			.addSuite("sharedclasses-mix")
			.setSuiteInventory(inventoryFile)
			.setSuiteThreadCount(cpuCount - 1, 2)
			.setSuiteNumTests(numberOfTests)
		   	.setSuiteSequentialSelection();
	}
}
