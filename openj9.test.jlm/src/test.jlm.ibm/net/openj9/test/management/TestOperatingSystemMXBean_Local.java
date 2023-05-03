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

package net.openj9.test.management;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;

import com.ibm.lang.management.MemoryUsage;
import com.ibm.lang.management.MemoryUsageRetrievalException;
import com.ibm.lang.management.ProcessorUsage;
import com.ibm.lang.management.ProcessorUsageRetrievalException;
import com.ibm.virtualization.management.HypervisorMXBean;

/**
 * Class for testing the APIs that are provided as part of OperatingSystemMXBean
 * Interface*/
public class TestOperatingSystemMXBean_Local extends junit.framework.TestCase {
	private static final int SLEEPINTERVAL = 5;
	private static final int NTHREADS = 1;
	private static final int NITERATIONS = 1;
	ObjectName objName = null;
	JMXServiceURL urlForRemoteMachine = null;
	JMXConnector connector = null;
	MBeanServerConnection mbeanConnection = null;
	static com.ibm.lang.management.OperatingSystemMXBean osmxbean = null;
	static String OSNAME = null;
	static String OSARCH = null;
	static {
		try {
			MBeanServer mbs = null;
			ObjectName objName = null;
			HypervisorMXBean bean = null;
			objName = new ObjectName("com.ibm.virtualization.management:type=Hypervisor");

			mbs = ManagementFactory.getPlatformMBeanServer();

			if (mbs.isRegistered(objName) != true) {
				System.err.println("HypervisorMXBean is not registered with the PlatformMBeanServer, Cannot Proceed");
				System.exit(1);
			}

			bean = ManagementFactory.getPlatformMXBean(mbs, HypervisorMXBean.class);
			System.out.println("Running in a Virtualized environment?: " + bean.isEnvironmentVirtual());
			System.out.println("Vendor Name of the Hypervisor: " + bean.getVendor());

			OSNAME = (System.getProperty("os.name")).toLowerCase();
			OSARCH = (System.getProperty("os.arch")).toLowerCase();
			osmxbean = (com.ibm.lang.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
			osmxbean.getProcessCpuLoad();//first call returns -1//cannot handle in 300 threads hence ignoring check. FV should be covered
		} catch (Exception e){
			fail(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Test the getTotalSwapSpaceSize() & getFreeSwapSpaceSize() APIs of OperatingSystemMXBean
	 */
	public void test_SwapSpaceSizeAPIs() {
		long totalSwap = 0;
		long freeSwap = 0;

		totalSwap = osmxbean.getTotalSwapSpaceSize();
		freeSwap = osmxbean.getFreeSwapSpaceSize();
		if ((-1 != totalSwap) && (-1 != freeSwap)) {
			if (freeSwap <= totalSwap) {
				//ok
			} else {
				fail("Invalid Swap Memory Info retrieved , Free Swap size cannot be" +
						"greater than total Swap Size." +
						" getTotalSwapSpaceSize() & getFreeSwapSpaceSize() API's failed "+"osmxbean.getFreeSwapSpaceSize()="+freeSwap+" osmxbean.getTotalSwapSpaceSize is "+ totalSwap);

			}
		} else {
			if (-1 == totalSwap) {
				/*
				 * An error has occurred since getTotalSwapSpaceSize() has returned -1.
				 * -1 can also mean it is not supported, but we exclude these tests on
				 * non-supported platforms(i.e System-Z). */
				fail("Error: getTotalSwapSpaceSize() API returned -1, test failed"+ "totalSwap is "+totalSwap);
			} else {
				//ok
			}

			if (-1 == freeSwap) {
				/*
				 * An error has occurred since getFreeSwapSpaceSize() has returned -1.
				 * -1 can also mean it is not supported, but we exclude these tests on
				 * non-supported platforms(i.e System-Z).*/
				fail("getFreeSwapSpaceSize() API returned -1, test failed"+"freeSwap is:"+ freeSwap);
			} else {
				//ok
			}
		}
	}

	/**
	 * Test the getTotalPhysicalMemorySize() & getFreePhysicalMemorySize() APIs of OperatingSystemMXBean
	 */
	public void test_PhysicalMemoryAPIs() {
		long totalMemory = osmxbean.getTotalPhysicalMemorySize();
		long freeMemory = osmxbean.getFreePhysicalMemorySize();

		if ((-1 != totalMemory) && (-1 != freeMemory)) {
			// Check for the validity of the values from the API
			if ((totalMemory > 0) && (freeMemory <= totalMemory)) {
				//ok
			} else {
				fail("Invalid Physical Memory Info retrieved , Total Physical " +
						"Memory cannot be 0 bytes and Free Physical Memory size cannot be" +
						" greater than total Physical Memory Size." +
						"\nError: getTotalPhysicalMemorySize() & getFreePhysicalMemorySize() API failed!!"+"totalMemory is:"+ totalMemory+ "freeMemory is:"+freeMemory);
			}
		} else {
			if (-1 == totalMemory) {
				/*
				 * An error has occurred since getTotalPhysicalMemorySize() has returned -1.
				 * -1 can also mean it is not supported, but we exclude these tests on
				 * non-supported platforms(i.e System-Z).
				 */
				fail("getTotalPhysicalMemorySize() API returned -1, test failed"+"totalMemory is:"+totalMemory);
			} else if (totalMemory < -1){
				fail("getTotalPhysicalMemorySize() API returned "+ totalMemory + ", test failed");
			} else {
				//ok
			}

			if (-1 == freeMemory) {
				/*
				 * An error has occurred since getFreePhysicalMemorySize() has returned -1.
				 * -1 can also mean it is not supported, but we exclude these tests on
				 * non-supported platforms(i.e System-Z).
				 */
				if(OSNAME.equalsIgnoreCase("z/OS")){ //CMVC#199325 Not supported on z/OS
					//ok
				} else {
					fail("getFreePhysicalMemorySize() API returned -1, test failed"+ "freeMemory is:"+ freeMemory);
				}
			} else if (freeMemory < -1){
				fail("getFreePhysicalMemorySize() API returned " + freeMemory + ", test failed");

			} else {
				//ok
			}
		}
	}

	/**
	 * Test the getCommittedVirtualMemorySize() APIs OperatingSystemMXBean
	 */
	public void  test_getCommittedVirtualMemorySize() {
		long processVirtualMem = osmxbean.getCommittedVirtualMemorySize();
		String osname = osmxbean.getName();

		if (-1 != processVirtualMem) {
			if(processVirtualMem > 0) {
				//ok
			} else {
				fail("Invalid Process Virtual Memory Size retrieved," +
						"ProcessVirtualMemory cannot be less than 0" +
						"\nError: getCommittedVirtualMemorySize() API failed!!"+"processVirtualMem is: "+processVirtualMem);

			}
		} else if ((-1 == processVirtualMem) && ((true == osname.equals("AIX")) || (true == osname.equalsIgnoreCase("z/OS")))  ) {//CMVC#199325
			/* API not supported on AIX for now, so we ignore the -1 */
		} else {
			/*
			 * An error has occurred since getCommittedVirtualMemorySize() has returned -1.
			 * -1 can also mean it is not supported, but we exclude these tests on
			 * non-supported platforms(i.e System-Z).
			 */
			fail("Error: getCommittedVirtualMemorySize() returned -1, API failed!!"+ "processVirtualMem is:"+ processVirtualMem);
		}
	}

	/**
	 * Test the getProcessCpuTime() API of OperatingSystemMXBean
	 */
	public void test_getProcessCpuTime() {
		int i = 0;
		long processCpuTime_old = 0;
		long processCpuTime_new = 0;
		processCpuTime_old = osmxbean.getProcessCpuTime();

		if (-1 != processCpuTime_old) {
			try {
				Thread[] busyObj = new Thread[NTHREADS];
				long counter = 0;

				for(; counter < NITERATIONS; counter++) {
					for (i = 0; i < NTHREADS; i++) {
						busyObj[i] = new Thread(new BusyThread(SLEEPINTERVAL * 500));
						//busyObj[i] = new Thread(new BusyThread(1 * 10));// api needs fix
						busyObj[i].start();
					}
					Thread.sleep(50);
					for (i = 0; i < NTHREADS; i++) {
						busyObj[i].join();
					}
				}
				processCpuTime_new = osmxbean.getProcessCpuTime();
				if (-1 == processCpuTime_new) {
					fail("Error: getProcessCpuTime() returned -1, API failed!!");
				}

			} catch(InterruptedException e) {
				fail("Sleep Interrupted..Exiting the test");
			}

			if (processCpuTime_new == processCpuTime_old) {
				if(OSNAME.equalsIgnoreCase("z/OS")){
					//ok
				}else{
					fail("Processor load did not increase. Reiterating ..."+"processCpuTime_new is:"+processCpuTime_new +" processCpuTime_old is"+ processCpuTime_old);
				}
			} else if (processCpuTime_new > processCpuTime_old) {
				//ok
			}
		} else {
			/*
			 * An error has occurred since getProcessCpuTime() has returned -1.
			 * -1 can also mean it is not supported, but we exclude these tests on
			 * non-supported platforms(i.e System-Z).
			 */
			fail("Error: getProcessCpuTime() returned -1, API failed!!"+ "processCpuTime_old is:"+processCpuTime_old);
		}
	}

	/**
	 * Function tests the MemoryUsage retrieval functionality for sanity conditions as also prints
	 * out some basic memory usage statistics retrieved using the MXBean.
	 *
	 */
	public void test_memoryInfo() {
		long total;
		long free;
		long totalSwap;
		long freeSwap;
		long cached;
		long buffered;
		long timestamp;

		MemoryUsage memUsage = null;

		try {
			// Try obtaining the memory usage statistics at this time (timestamp).
			memUsage = osmxbean.retrieveMemoryUsage();

			total = memUsage.getTotal();
			free = memUsage.getFree();
			if ((-1 != total) && (-1 != free)) {
				if ((total > 0) && (free <= total)) {
					//ok
				} else {
					fail("Invalid memory usage statistics retrieved."+"memUsage.getTotal() is:"+total+"memUsage.getFree() is:"+ free);
				}
			} else {
				if (-1 == total) {
					fail("Total physical memory: <undefined for platform>"+"memUsage.getTotal() is:"+total);
				} else {
					//ok
				}
				if (-1 == free) {
					fail("Available physical: <undefined for platform>"+"memUsage.getFree() is:"+ free);
				} else {
					//ok
				}
			}

			totalSwap = memUsage.getSwapTotal();
			freeSwap= memUsage.getSwapFree();
			if ((-1 != totalSwap) && (-1 != freeSwap)) {
				if (freeSwap <= totalSwap) {
					//ok
				} else {
					fail("Invalid memory usage statistics retrieved."+"memUsage.getSwapFree() is: "+freeSwap +" memUsage.getSwapTotal() is: "+totalSwap);
				}
			} else {
				if (-1 == totalSwap) {
					fail("Total swap space configured: <undefined for platform>"+" memUsage.getSwapTotal() is: "+totalSwap);
				} else {
					//ok
				}
				if (-1 == freeSwap) {
					fail("Available swap: <undefined for platform>"+"memUsage.getSwapFree() is: "+freeSwap );
				} else {
					//ok
				}
			}

			cached = memUsage.getCached();

			if (-1 == cached) {
				fail("Size of cached memory: <undefined for platform>"+"memUsage.getCached() : "+cached);
			} else {
				//ok
			}

			buffered = memUsage.getBuffered();
			if (-1 == buffered) {

				if(OSNAME.contains("windows") || OSNAME.contains("aix")){
					//ok
				} else {
					fail("Size of buffered memory: <undefined for platform>"+" memUsage.getBuffered() : "+ buffered);
				}
			} else {
				//ok
			}

			timestamp = memUsage.getTimestamp();
			if (timestamp > 0) {
				//ok
			} else {
				fail("Invalid timestamp received!"+"memUsage.getTimestamp() : "+ timestamp);
			}

		} catch(MemoryUsageRetrievalException mu) {
			fail("Exception occurred while retrieving memory usage:" + mu.getMessage());
		} catch(Exception e) {
			e.printStackTrace();
			fail("Unknown exception occurred:" + e.getMessage());

		}
	}

	/**
	 * Function tests the ProcessorUsage retrieval functionality for sanity conditions as also prints
	 * out processor usage statistics retrieved using the MBean.
	 */
	public void test_processorInfo()
	{
		if(OSNAME.equalsIgnoreCase("z/OS")){
			//ok
		} else {
			ProcessorUsage procUsage = null;
			ProcessorUsage[] procUsageArr = null;

			int MaxIterations = 5;
			long deltaTotalIdleTime = 0;
			long deltaTotalBusyTime = 0;
			@SuppressWarnings("unused")
			long busy = 0;
			@SuppressWarnings("unused")
			long user = 0;
			@SuppressWarnings("unused")
			long system = 0;
			@SuppressWarnings("unused")
			long idle = 0;
			@SuppressWarnings("unused")
			long wait = 0;
			long busyTotal[] = null;
			long userTotal[] = null;
			long systemTotal[] = null;
			long idleTotal[] = null;
			long waitTotal[] = null;
			long timestamp[] = null;

			try {
				busyTotal = new long[MaxIterations];
				userTotal = new long[MaxIterations];
				systemTotal = new long[MaxIterations];
				idleTotal = new long[MaxIterations];
				waitTotal = new long[MaxIterations];
				timestamp = new long[MaxIterations];

				for (int iter = 0; iter < MaxIterations; iter++) {
					procUsage = osmxbean.retrieveTotalProcessorUsage();

					busyTotal[iter] = procUsage.getBusy();
					userTotal[iter] = procUsage.getUser();
					systemTotal[iter] = procUsage.getSystem();
					idleTotal[iter] = procUsage.getIdle();
					waitTotal[iter] = procUsage.getWait();
					timestamp[iter] = procUsage.getTimestamp();

					/* Check for the number of records in the array; this is equal to the number of CPUs
					 * configured on the machine. Iterate through the records printing the processor usage
					 * data and dumping the same.
					 */
					procUsageArr = osmxbean.retrieveProcessorUsage();
					int nrecs = procUsageArr.length;
					int n_onln = onlineProcessorCount(procUsageArr);

					if ((nrecs > 0) && (n_onln > 0) && (nrecs >= n_onln)) {
						/* We can deal with deltas only the second iteration onwards. */
						if (iter > 0) {
							deltaTotalIdleTime = idleTotal[iter] - idleTotal[iter - 1];
							deltaTotalBusyTime = busyTotal[iter] - busyTotal[iter - 1];

							if (0 < (deltaTotalBusyTime + deltaTotalIdleTime)) {
								//ok
							} else {
								fail("Unexpected change in processor time deltas!"+ "deltaTotalBusyTime:"+deltaTotalBusyTime +"deltaTotalIdleTime: "+ deltaTotalIdleTime);
							}
						}
					} else {
						fail("Invalid processor usage statistics retrieved."+"procUsageArr.length:"+nrecs+"nlineProcessorCount(procUsageArr):"+n_onln);
					}

					for (int i = 0; i < nrecs; i++) {
						@SuppressWarnings("unused")
						int online = procUsageArr[i].getOnline();
						busy = procUsageArr[i].getBusy();
						user = procUsageArr[i].getUser();
						system = procUsageArr[i].getSystem();
						idle = procUsageArr[i].getIdle();
						wait = procUsageArr[i].getWait();
						@SuppressWarnings("unused")
						int id = procUsageArr[i].getId();
					}
					Thread.sleep(300);
				}
			} catch(ProcessorUsageRetrievalException pu) {
				fail("Exception occurred retrieving processor usage: " + pu.getMessage());
			} catch(java.lang.InterruptedException ie) {
				fail("Exception occurred while sleeping thread: " + ie.getMessage());
			} catch(Exception e) {
				e.printStackTrace();
				fail("Unknown exception occurred:" + e.getMessage());

			}
		}
	}

	/**
	 * Internal function: Computes and returns the number of processors currently online.
	 *
	 * @param[in] procUsageArr An array of processor usage objects.
	 *
	 * @return Online processor count.
	 */
	private static int onlineProcessorCount(ProcessorUsage[] procUsageArr) {
		int n_onln = 0;

		for (int cntr = 0; cntr < procUsageArr.length; cntr++) {
			if (1 == procUsageArr[cntr].getOnline()) {
				n_onln++;
			}
		}
		return n_onln;
	}
}
