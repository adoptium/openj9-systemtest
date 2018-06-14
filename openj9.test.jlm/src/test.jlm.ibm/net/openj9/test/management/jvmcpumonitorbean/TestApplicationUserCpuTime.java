/*******************************************************************************
* Copyright (c) 2017, 2018 IBM Corp. and others
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

package net.openj9.test.management.jvmcpumonitorbean;

import java.lang.management.ManagementFactory;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import com.ibm.lang.management.JvmCpuMonitorInfo;

public class TestApplicationUserCpuTime extends junit.framework.TestCase {

	private final int MAX_USER_CATEGORY = 5;
	private final int THREADPOOL_SIZE = 15;
	private final int TASK_COUNT = 50;
	
	private com.ibm.lang.management.JvmCpuMonitorMXBean monitorMXBean = null;
	
	//This is added to check if more than one same application user type thread is running
	//public static volatile int userThreadCounter[] = new int[]{0,0,0,0,0};
	protected void setUp() throws Exception {
		MBeanServer mbs = null;
		ObjectName objName = null;
		objName = new ObjectName("com.ibm.lang.management:type=JvmCpuMonitor");
		
		mbs = ManagementFactory.getPlatformMBeanServer();

		if (mbs.isRegistered(objName) != true) {
			fail("JvmCpuMonitorMXBean is not registered with the PlatformMBeanServer, Cannot Proceed");
			throw new Exception("JvmCpuMonitorMXBean is not registered with the PlatformMBeanServer");
		}
	
		monitorMXBean = JMX.newMXBeanProxy(mbs, objName, com.ibm.lang.management.JvmCpuMonitorMXBean.class);
	}
	
	protected void tearDown() { }

    /** 
	* testMultipleAccessScenario() method intend to test the scenario when many threads attempt 
	* to set the category of the same thread into different category values. 
	* A new thread is started and is put to sleep for 5 secs. Its thread id is given to
	* a pool of threads which try to set the thread category to Application-User1 to Application-User5
	* in parallel for 3 secs. 
	*/
	public  void testMultipleAccessScenario() {
		try {
			
			Thread t = new Thread(new ThreadCategory(monitorMXBean, 0, "", Boolean.FALSE));
			t.start();
			
			long threadId = t.getId();
			
			String category = monitorMXBean.getThreadCategory(threadId);
			System.out.println(category);
			
			Thread[] threads = new Thread[MAX_USER_CATEGORY];
			for(int i=0; i < MAX_USER_CATEGORY; i++) {
				String newCategory = "Application-User" + (i+1);
				threads[i] = new Thread(new ThreadCategory(monitorMXBean, threadId, newCategory, Boolean.FALSE));
			}
			
			for(int i=0; i < MAX_USER_CATEGORY; i++) {
				threads[i].start();
			}
			
			for(int i=0; i < MAX_USER_CATEGORY; i++) {
				threads[i].join();
			}
			
			t.join();

		} catch(Exception exception) {
			exception.printStackTrace();
			fail("Exception thrown: " + exception.getMessage());
		}
	}
	
	/** 
	* testThreadPoolScenario() method intend to test the getResourceMonitorCpuTime() and getApplicationUserCpuTime() after executing some load using a threadpool
	* 
	* A snapshot of cpu usage is measured before starting anything.
	* A new thread is created and set to 'Resource-Monitor' category which calls the getApplicationCpuTime() continuously until asked to stop.
	* A fixed pool of 'n' threads are created and are given 'm' load to execute.
	* Each load when execute set the currently executing thread's category to one of Application-User0 to Application-User5.
	* Application-User0 is a invalid category and hence we reset it to 'Application' when the exception is thrown.
	* Each thread category performs different function for a defined period and when done their category is reset to 'Application' (default).
	* After all the created loads are complete, we measure the cpu usage again and compare it with the previously taken value.
	* 
	*/
	
	public  void testThreadPoolScenario() {
		try {
			long threadId = Thread.currentThread().getId();
			Timestamp t1 = new Timestamp(new Date().getTime());
			JvmCpuMonitorInfo jcmInfoStart = monitorMXBean.getThreadsCpuUsage(new JvmCpuMonitorInfo());
			
			ThreadCategory tc = new ThreadCategory(monitorMXBean, -1, "Resource-Monitor", Boolean.TRUE);
			tc.setResourceMonitorInterruptedStatus(Boolean.FALSE);
			Thread t = new Thread(tc);
			t.start();
			
			ExecutorService executor = Executors.newFixedThreadPool(THREADPOOL_SIZE);
			
			Random rand = new Random();
		
			int categoryList[] = new int[]{0,0,0,0,0,0,0};
			Future<?> taskStatus[] = new Future[TASK_COUNT];
			
			for (int i = 0; i < TASK_COUNT; i++) {
				int number = rand.nextInt(MAX_USER_CATEGORY+1);
				categoryList[number]++;
				String newCategory = "Application-User" + number;
				Runnable worker = new ThreadCategory(monitorMXBean, -1, newCategory, Boolean.TRUE);
				taskStatus[i] = executor.submit(worker);
			}
			
			executor.shutdown();
			
			while (!executor.isTerminated()) {
				for (int i = 0; i < TASK_COUNT; i++) {
					if(taskStatus[i].get() != null)
						System.out.println("Task"+(i+1)+" is not done..");
				}	
			}
			
			tc.setResourceMonitorInterruptedStatus(Boolean.TRUE);
			t.join();
			
			System.out.println("Finished all threads");
			
			Timestamp t2 = new Timestamp(new Date().getTime());
			
			JvmCpuMonitorInfo jcmInfoEnd = monitorMXBean.getThreadsCpuUsage(new JvmCpuMonitorInfo());
			
			long appUserInfoStart[] = jcmInfoStart.getApplicationUserCpuTime();
			long appUserInfoEnd[] = jcmInfoEnd.getApplicationUserCpuTime();
			
			long appUserSumEnd = 0;
			
			for(int i=0; i < appUserInfoStart.length ; i++) {
				appUserSumEnd += appUserInfoEnd[i];
				
				if( categoryList[i+1] > 0 )
					assertTrue("Thread#"+ threadId + "Application user"+(i+1)+" cpu usage is not increasing: value1 = " + appUserInfoStart[i] + "(" + t1 + ")" + 
								", value2 = " + appUserInfoEnd[i] + "(" + t2 + ")", (appUserInfoStart[i] < appUserInfoEnd[i]) );
			}
			
			if( categoryList[0] > 0 )
				assertTrue("Thread#"+ threadId + "Total cpu usage of all application threads is expected to be more than sum of user defined application threads usage: ApplicationCpuTime = "+ jcmInfoEnd.getApplicationCpuTime() +  
								", ApplicationUserCpuSum = " + appUserSumEnd , (jcmInfoEnd.getApplicationCpuTime() > appUserSumEnd) );
			
			assertTrue("Thread#"+ threadId + "Resource-Monitor cpu usage is not increasing: value1 = "+ jcmInfoStart.getResourceMonitorCpuTime() + "(" + t1 + ")" + 
								", value2 = " + jcmInfoEnd.getResourceMonitorCpuTime() + "(" + t2 + ")", ( jcmInfoStart.getResourceMonitorCpuTime() < jcmInfoEnd.getResourceMonitorCpuTime() ) );
			
		} catch(Exception exception) {
			exception.printStackTrace();
			fail("Exception thrown: " + exception.getMessage());
		}
	}
}
