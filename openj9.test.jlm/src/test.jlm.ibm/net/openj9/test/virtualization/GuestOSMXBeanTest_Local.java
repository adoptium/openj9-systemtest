/*******************************************************************************
* Copyright (c) 2017, 2019 IBM Corp. and others
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

package net.openj9.test.virtualization;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.management.ManagementFactory;
import java.util.Scanner;
import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import com.ibm.virtualization.management.GuestOSInfoRetrievalException;
import com.ibm.virtualization.management.GuestOSMXBean;
import com.ibm.virtualization.management.GuestOSMemoryUsage;
import com.ibm.virtualization.management.GuestOSProcessorUsage;
import com.ibm.virtualization.management.HypervisorMXBean;

import junit.framework.TestCase;

/**
 * Class for testing the API that are provided as part of the GuestOSMXBean
 *
 */
public class GuestOSMXBeanTest_Local extends TestCase {
	// Strings that are looked for in exception messages to check if they are
	// actually a valid error or not
	private static final String VMWARE_ERROR = "VMWare Guest SDK Open failed";
	private static final String LPARCFG_ERROR = "-862";
	private static final String NOT_SUPPORTED_ERROR = "-856";
	private static final String NO_HYPERVISOR_ERROR = "-857";
	private static final String LPARCFG_FILE = "/proc/ppc64/lparcfg";

	private static MBeanServer mbeanServer = null;
	private static  ObjectName mxbeanName = null;
	private static  GuestOSMXBean mxbeanProxy = null;

	static String OSNAME = null; 
	static String OSARCH = null;

	static HypervisorMXBean bean = null;

	// Starting point for the test program. Invokes the memory and processor test routines and
	// indicates success or failure accordingly.
	static {
		try {	
			OSNAME = (System.getProperty("os.name")).toLowerCase();
			OSARCH = (System.getProperty("os.arch")).toLowerCase();
			MBeanServer mbs = null;
			ObjectName objName = null;
			objName = new ObjectName("com.ibm.virtualization.management:type=Hypervisor");
			mbs = ManagementFactory.getPlatformMBeanServer();

			if (mbs.isRegistered(objName) != true) {
				System.err.println("HypervisorMXBean is not registered with the PlatformMBeanServer, Cannot Proceed");
				System.exit(1);
			}

			bean = ManagementFactory.getPlatformMXBean(mbs, HypervisorMXBean.class);
			System.out.println("Running in a Virtualized environment?: " + bean.isEnvironmentVirtual());
			System.out.println("Vendor Name of the Hypervisor: " + bean.getVendor());
			if(!bean.isEnvironmentVirtual()){
				throw new RuntimeException("This is not Virtualized Environment. Existing ");
			}

			mxbeanName = new ObjectName("com.ibm.virtualization.management:type=GuestOS");
			mbeanServer = ManagementFactory.getPlatformMBeanServer();
			if(true != mbeanServer.isRegistered(mxbeanName)) {
				throw new RuntimeException("GuestOSMXBean is not registered. " +
						"Cannot Proceed with the test.");
			}
			mxbeanProxy =  JMX.newMXBeanProxy(mbeanServer, mxbeanName, GuestOSMXBean.class);
		} catch(Exception e){
			fail(e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Function tests the MemoryUsage retrieval functionality for sanity conditions as also prints
	 * out some basic memory usage statistics retrieved using the MBean passed to it.
	 *
	 */
	public void test_memoryInfo() {
		GuestOSMemoryUsage memUsage = null;
		long memUsed = 0;
		long maxMemLimit = 0;
		long timeStamp = 0;
		try {

			// Try obtaining the memory usage statistics at this time (timestamp).

			// PowerKVM does not support any of the Guest Memory Usage counters; disable the
			// validity checking part of the code on the platform, but enable as support for
			// each of these is added.
			if (bean.getVendor().contains("PowerKVM")) {
				//Guest memory counters not available on PowerKVM
			} else {	
				memUsage = mxbeanProxy.retrieveMemoryUsage();
				memUsed = memUsage.getMemUsed();
				maxMemLimit = memUsage.getMaxMemLimit();
				timeStamp = memUsage.getTimestamp();

				// Validate the statistics obtained.
				// - Memused cannot be 0.
				// - Memused cannot be greater than maxMemlimit.
				// - Timestamp cannot be 0.
				if ((-1!= maxMemLimit) && (-1 != memUsed)) {
				} else {
					if (-1 == memUsed) {
					} else if (memUsed < -1){
						fail("Invalid Current Used Memory by the Guest reported, Memory Used cannot be less than -1!!" + memUsed );
					} else {
						//ok
					}

					if (-1 == maxMemLimit) {
					} else if (maxMemLimit < -1){ //change based on CMVC#199419
						fail("Maximum Memory that can be used by the Guest:" +maxMemLimit);
					} else {
						//ok
					}
				}	

				if (timeStamp > 0) {
				} else {
					fail("Invalid timestamp received, timeStamp cannot be 0!! timeStamp:"+timeStamp+" ,memUsed:"+memUsed+" ,maxMemLimit:"+maxMemLimit);
				}
			}

		} catch(GuestOSInfoRetrievalException mu) {
			if (mu.getMessage().contains(VMWARE_ERROR)) {
				fail("Cannot Proceed with the test, check for the VMWare Guest SDK." + mu.getMessage());
			} else if (mu.getMessage().contains(LPARCFG_ERROR)) {
				// Validate the error condition i.e if the lparcfg version is
				// greater/equal to < 1.8 If version is < 1.8, ignore the error
				// else fail the test
				File file = new File(LPARCFG_FILE);
				try {
					// Use a Scanner to scan through the /proc/ppc64/lparcfg
					Scanner scanner = new Scanner(file);
					String str = "lparcfg ";
					while (scanner.hasNext("lparcfg")) {
						String line = scanner.nextLine();
						Float version = Float.valueOf(line.substring(str.length(), line.length()));
						if (version < 1.8) {
							fail("Cannot proceed with the test, the lparcfg version must be 1.8 or greater. Returned value is" + version  +  " mu.getMessage()");

						} else {
							fail("Invalid Guest Memory Usage statistics recieved!!" + mu.getMessage());
						}
					}
					scanner.close();
				} catch (FileNotFoundException e) {
					fail("/proc/ppc64/lparcfg file not found.. Exiting." + mu.getMessage());
				}
			} else if (mu.getMessage().contains(NO_HYPERVISOR_ERROR)) {
				fail("Not running on a Hypervisor, Guest Statistics cannot be retrieved!."+mu.getMessage());

			} else if (mu.getMessage().contains(NOT_SUPPORTED_ERROR)) {
				fail("GuestOSMXBean not supported on this Hypervisor!."+mu.getMessage());

			} else {
				// Received a valid exception, test failed 
				fail(mu.getMessage());
			}
		} catch(Exception e) {
			e.printStackTrace();
			fail("Unknown exception occurred:" + e.getMessage());

		}
	}

	/**
	 * Function tests the ProcessorUsage retrieval functionality for snaity conditions as also prints
	 * out processor usage statistics retrieved using the MBean passed to it.
	 *
	 */
	public void test_processorInfo() {
		GuestOSProcessorUsage procUsage = null;
		float cpuEnt = 0;
		long timeStamp = 0;
		long hostSpeed = 0;
		long cpuTime = 0;
		try{
			procUsage = mxbeanProxy.retrieveProcessorUsage();

			cpuTime = procUsage.getCpuTime();
			timeStamp = procUsage.getTimestamp();
			hostSpeed = procUsage.getHostCpuClockSpeed();
			cpuEnt = procUsage.getCpuEntitlement();
			/*
			 * Validate the statistics recived
			 * - hostCpuClockSpeed of Guest OS is not 0
			 * - timeStamp is not 0
			 * - cpuTime is not 0
			 * - cpuEntitlement is not 0
			 */
			if (cpuTime > 0 &&
					timeStamp > 0 &&
					cpuEnt > 0 &&
					hostSpeed > 0) {
				// ok. ignore
			} else {
				if ((cpuTime > 0 &&
						timeStamp > 0 &&
						hostSpeed > 0) && OSARCH.contains("s390")) {
					// CPU entitlement is not supported on System-z
				} else if((-1 == cpuEnt) && (bean.getVendor().contains("PowerKVM")) ){
					// The CPU Entitlement assigned for this Guest "PowerKVM"
				}else {
					fail("Invalid Guest Processor statistics recieved."+" cpuTime:"+cpuTime+" ,timeStamp:"+timeStamp+" ,hostSpeed:"+hostSpeed+" ,cpuEnt:"+cpuEnt);
				}
			}
		} catch(GuestOSInfoRetrievalException pu) {
			if (pu.getMessage().contains(VMWARE_ERROR)) {
				fail("Cannot Proceed with the test, check for the VMWare Guest SDK."+pu.getMessage());
			} else if (pu.getMessage().contains(NO_HYPERVISOR_ERROR)) {
				fail("Not running on a Hypervisor, Guest Statistics cannot be retrieved!."+pu.getMessage());

			} else if (pu.getMessage().contains(NOT_SUPPORTED_ERROR)) {
				fail("GuestOSMXBean not supported on this Hypervisor!." +pu.getMessage());

			} else {
				fail(pu.getMessage());
			}
		} catch(Exception e) {
			e.printStackTrace();
			fail("Unknown exception occurred:" + e.getMessage());

		}
	}
}
