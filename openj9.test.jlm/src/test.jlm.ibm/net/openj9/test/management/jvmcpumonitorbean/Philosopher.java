/*******************************************************************************
* Copyright (c) 2017, 2023 IBM Corp. and others
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

package net.openj9.test.management.jvmcpumonitorbean;

import java.util.*;

public class Philosopher implements Runnable {
	String person;
	Fork rightFork, leftFork;
	int arraySize;
	int iterationCount;
	
	Philosopher(String person, Fork rightFork, Fork leftFork, int arraySize) {
		this.person = person;
		this.rightFork = rightFork;
		this.leftFork = leftFork;
		this.arraySize = arraySize;
		iterationCount = 0;
	}
	
	public void run() {
		try {
			while(iterationCount < 100) {
				synchronized(rightFork)  {
					rightFork.inUse = true;
					if(!leftFork.inUse) {
						synchronized(leftFork){
							leftFork.inUse = true;
							rightFork.usedCount++;
							leftFork.usedCount++;
							iterationCount++;				
							
							//Generating some load for measuring cpu load time
							Random rand = new Random();
							Integer[] list = new Integer[arraySize];
							Integer totalVal = new Integer(0);
							
							for(int i=0; i < arraySize; i++) {
								list[i] = new Integer(rand.nextInt());
							}
										
							Arrays.sort(list);
										
							for(int i=0; i < arraySize; i++) {
								totalVal = new Integer(totalVal.intValue() + list[i].intValue());
							}
							
							if(list[999] == list[666]) {
								// The values should never match, however this forces the JVM to evaluate the Array values
								// The idea is to prevent the JVM from optimising this code too much and induce more GC/JIT/CPU activity
								System.out.println("Impossible - the values matched!");
								
							}
							
						}
						leftFork.inUse = false;
					}
				}
				rightFork.inUse = false;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}

