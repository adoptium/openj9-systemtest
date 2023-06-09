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
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0 OR GPL-2.0-only WITH Classpath-exception-2.0 OR GPL-2.0-only WITH OpenJDK-assembly-exception-1.0
*******************************************************************************/

package net.openj9.test.management.jvmcpumonitorbean;

import com.ibm.lang.management.*;
import java.util.Random;

public class ThreadCategory implements Runnable {

	private JvmCpuMonitorMXBean mxbean;
	private long threadId;
	private String newCategory;
	private Boolean startWorkLoad;
	private Boolean isInterrupted;
		
	ThreadCategory(JvmCpuMonitorMXBean mxbean, long threadId, String newCategory, Boolean startWorkLoad) {
		this.mxbean = mxbean;
		this.threadId = threadId;
		this.newCategory = newCategory;
		this.startWorkLoad = startWorkLoad;
	}
	
	public void run() {
		try {
			if(!startWorkLoad) {
				if( threadId == 0 ) {
					Thread.sleep(2500); // 2.5 secs
				} else {
					long startTime = System.nanoTime();
					long busyTime = 1500 * 1000000L; // 1.5 secs
					while ((System.nanoTime() - startTime) < busyTime) {
						mxbean.setThreadCategory(threadId, newCategory);
					}
				}
			} else {
				threadId = Thread.currentThread().getId();
				try {
					mxbean.setThreadCategory(threadId, newCategory);
					
					if( (mxbean.getThreadCategory(threadId)).equalsIgnoreCase("Application-User0") ) {
						System.err.println("Setting to Application-User0 category is expected to fail..");
						return;
					}
				} catch(IllegalArgumentException iae) {
					if(newCategory.equalsIgnoreCase("Application-User0")) {
						mxbean.setThreadCategory(threadId, "Application");
					}
					else {
						iae.printStackTrace();
						return;
					}
				}
				
				Random rand = new Random();
				@SuppressWarnings("unused")
				double result = 0;
								
				long startTime = System.nanoTime();
				long busyTime = 1000 * 1000000L;
				
				if(newCategory.equalsIgnoreCase("Application-User1")) {
					while ((System.nanoTime() - startTime) < busyTime) {
						double value = rand.nextDouble();
						result = java.lang.Math.log(value);
					}
					mxbean.setThreadCategory(threadId, "Application");
				} else if(newCategory.equalsIgnoreCase("Application-User2")) {
					while ((System.nanoTime() - startTime) < busyTime) {	
						double value = rand.nextDouble();
						result = java.lang.Math.cos(value);
					}
					mxbean.setThreadCategory(threadId, "Application");
				} else if(newCategory.equalsIgnoreCase("Application-User3")) {
					while ((System.nanoTime() - startTime) < busyTime) {
						double value = rand.nextDouble();
						result = java.lang.Math.tan(value);
					}
					mxbean.setThreadCategory(threadId, "Application");
				} else if(newCategory.equalsIgnoreCase("Application-User4")) {
					while ((System.nanoTime() - startTime) < busyTime) {
						double value = rand.nextDouble();
						result = java.lang.Math.sin(value);
					}
					mxbean.setThreadCategory(threadId, "Application");
				} else if(newCategory.equalsIgnoreCase("Application-User5")) {
					while ((System.nanoTime() - startTime) < busyTime) {
						double value = rand.nextDouble();
						result = java.lang.Math.exp(value);
					}
					mxbean.setThreadCategory(threadId, "Application");
				} else if(newCategory.equalsIgnoreCase("Resource-Monitor")) {
					while ( !isInterrupted ) {
						JvmCpuMonitorInfo jcmInfo = mxbean.getThreadsCpuUsage(new JvmCpuMonitorInfo());
						@SuppressWarnings("unused")
						long appCpuTime = jcmInfo.getApplicationCpuTime();
					}		
				} else {			
					while ((System.nanoTime() - startTime) < busyTime) {
						double value = rand.nextDouble();
						result = java.lang.Math.exp(value);
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setResourceMonitorInterruptedStatus(Boolean b) {
		isInterrupted = b;
	}
}

