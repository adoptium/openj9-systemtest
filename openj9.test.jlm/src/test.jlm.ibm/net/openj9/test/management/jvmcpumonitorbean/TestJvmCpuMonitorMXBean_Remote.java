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

import java.sql.Timestamp;
import java.util.Date;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import com.ibm.lang.management.JvmCpuMonitorInfo;

/**
 * Class for testing the APIs that are provided as part of JvmCpuMonitorMXBean Interface
 */
public class TestJvmCpuMonitorMXBean_Remote extends junit.framework.TestCase {

	private final int NPERSON = 5;
	
	//This array size is arrived at considering 50 threads 
	// running in parallel with 100MB as XmX value.  
	// This value has to be tuned if the number of threads 
	// in the workload definition increases.
	private final int ARRAYSIZE = 75000; 
	private JMXServiceURL urlForRemoteMachine = null;
	private JMXConnector connector = null;
	private MBeanServerConnection mbeanConnection = null;
	private com.ibm.lang.management.JvmCpuMonitorMXBean monitorMXBean = null;
	private String vmInfo = null;
	private JvmCpuMonitorInfo[] cpuInfo = new JvmCpuMonitorInfo[5];
	
	protected void setUp() throws Exception {
		vmInfo = System.getProperty("java.vm.info").replace("JRE 9","JRE 1.9");
		urlForRemoteMachine = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9999/jmxrmi");

		// Connect to remote host and create a proxy to 
		// be used to get an JVMCpuMonitorMXBean instance.
		connector = JMXConnectorFactory.connect(urlForRemoteMachine, null);
		mbeanConnection = connector.getMBeanServerConnection();

		// Obtain an mxbean object using the connection and the object name for the class 
		// JVMCpuMonitorMXBean. 
		ObjectName objName = new ObjectName("com.ibm.lang.management:type=JvmCpuMonitor");
		monitorMXBean = JMX.newMXBeanProxy(mbeanConnection, objName, 
				com.ibm.lang.management.JvmCpuMonitorMXBean.class);
	}
	
	protected void tearDown() throws Exception{
		connector.close();
	}
	
    /**
     * Test the cpu consumption by System/Appication threads
	 *
	 * - Initial jvmcpuinfo snapshot is taken
	 * - Array sort workload is done
	 * - Values are not updated by the OS immediately. So we sleep for sometime before measuring.
	 * - Second jvmcpuinfo snapshot is taken. In case of platforms like windows and z/os,
	 * 
	 * Tested api's:
	 * - getThreadsCpuUsage(JvmCpuMonitorInfo obj)
	 *
	 * - getTimestamp(): Expected to raise across the measurements
	 * - getGcCpuTime() : Expected to increase across the measurements
	 * - getJitCpuTime() : Expected to increase across the measurements
	 * - getApplicationCpuTime() : Expected to increase across the measurements
	 * 
     */
	public  void testGetThreadsCpuUsage() {
		try {
			long threadId = Thread.currentThread().getId();
			
			Timestamp t1 = new Timestamp(new Date().getTime());
			
			//Take the first snapshot of cpu information
			cpuInfo[0] = monitorMXBean.getThreadsCpuUsage(new JvmCpuMonitorInfo());
					
			//Workload
			Fork[] forks = new Fork[NPERSON];
			Thread[] threads = new Thread[NPERSON];
			int size = ARRAYSIZE;
			System.out.println("Setting array size as "+ size);
			
			for(int i=0; i < NPERSON ; i++) { 
				forks[i] = new Fork(i+1,0, false);
			}

			for(int i=0; i < NPERSON; i++) {
				Fork rightFork, leftFork;
				String name = "P"+(i+1);
				
				if(NPERSON-i == 1) {
					rightFork = forks[i];
					leftFork = forks[i-i];
				} else {
					rightFork = forks[i];
					leftFork = forks[i+1];
				}
								
				threads[i] = new Thread(new Philosopher(name, rightFork, leftFork, size));
			}
			
			for(int i=0; i < NPERSON; i++) {
				threads[i].start();
			}
			
			for(int i=0; i < NPERSON; i++) {
				threads[i].join();
			}
			
			// Calling System.GC to force garbage collection
			System.gc();
	
			// Sleep for 60 secs
			Thread.sleep(90000);

			Timestamp t2 = new Timestamp(new Date().getTime());
			
			// Take the second snapshot of cpu information
			cpuInfo[1] = monitorMXBean.getThreadsCpuUsage(new JvmCpuMonitorInfo());

			System.out.println("TIME STAMP:" + cpuInfo[0].getTimestamp() + "," + cpuInfo[1].getTimestamp());
			System.out.println("GC TIME:" + cpuInfo[0].getGcCpuTime() + "(" + t1 + ")" + "," + cpuInfo[1].getGcCpuTime() + "(" + t2 + ")" );
			System.out.println("JIT TIME:" + cpuInfo[0].getJitCpuTime() + "(" + t1 + ")" + "," + cpuInfo[1].getJitCpuTime() + "(" + t2 + ")" );
			System.out.println("APPLICATION TIME:" + cpuInfo[0].getApplicationCpuTime() + "," + cpuInfo[1].getApplicationCpuTime());
			
			
			// Do the validation of data collected
			// Check for increase of time stamp
			assertTrue("Thread#"+ threadId + ":Snapshot timestamp is not increasing: value1 = " + cpuInfo[0].getTimestamp() + "(" + t1 + ")" + 
					", value2 = " + cpuInfo[1].getTimestamp() + "(" + t2 + ")", (cpuInfo[0].getTimestamp() < cpuInfo[1].getTimestamp()));
			
			// Please note all the measured time durations are in micro seconds
			// Check for gc cpu time
			assertTrue("Thread#"+ threadId + ":GC cpu time is not increasing: value1 = " + cpuInfo[0].getGcCpuTime() + "(" + t1 + ")"  + 
					", value2 = " + cpuInfo[1].getGcCpuTime() + "(" + t2 + ")", ( cpuInfo[0].getGcCpuTime() < cpuInfo[1].getGcCpuTime() ));
							
			//Check for jit cpu time only if jit is enabled
			if(vmInfo.contains("JIT enabled")) {
						
			assertTrue("Thread#"+ threadId + ":JIT cpu time is not increasing: value1 = " + cpuInfo[0].getJitCpuTime() + "(" + t1 + ")" + 
					", value2 = " + cpuInfo[1].getJitCpuTime() + "(" + t2 + ")", ( cpuInfo[0].getJitCpuTime() < cpuInfo[1].getJitCpuTime() ));
			}
			
			// Check for System jvm cpu time
			assertTrue("Thread#"+ threadId + ":SYSTEM JVM THREADS cpu time is not increasing: value1 = " + cpuInfo[0].getSystemJvmCpuTime() + "(" + t1 + ")" +
					", value2 = " + cpuInfo[1].getSystemJvmCpuTime() + "(" + t2 + ")", (cpuInfo[0].getSystemJvmCpuTime() < cpuInfo[1].getSystemJvmCpuTime()));
					
			// Check for application cpu time
			assertTrue("Thread#"+ threadId + ":APPLICATION cpu time is not increasing: value1 = " + cpuInfo[0].getApplicationCpuTime() + "(" + t1 + ")" +
					", value2 = " + cpuInfo[1].getApplicationCpuTime() + "(" + t2 + ")", (cpuInfo[0].getApplicationCpuTime() < cpuInfo[1].getApplicationCpuTime()));

		} catch(Exception exception) {
			exception.printStackTrace();
			fail("Exception thrown: " + exception.getMessage());
		}
	}
}
