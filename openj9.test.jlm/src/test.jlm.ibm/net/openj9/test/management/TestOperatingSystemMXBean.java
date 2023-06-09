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

package net.openj9.test.management;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import javax.management.MalformedObjectNameException;
import junit.framework.TestCase;

/**
 * Class for testing the APIs that are provided as part of OperatingSystemMXBean
 * Interface */
public class TestOperatingSystemMXBean extends TestCase {
	private static final int SLEEPINTERVAL = 5;
	private static final int NTHREADS = 1;
	private static final int NITERATIONS = 20;
	public static volatile long SHARED_RESULT = 0;
	private static com.ibm.lang.management.OperatingSystemMXBean osmxbean = 
			(com.ibm.lang.management.OperatingSystemMXBean) 
			ManagementFactory.getOperatingSystemMXBean();

	private static double processLoadFirstCall = 0;
	private static double systemCpuLoadFirstCall = 0;

	static String OSNAME = (System.getProperty("os.name")).toLowerCase();

	static {
		/* Start with obtaining a first snapshot of system and process
		 * CPU loads.  These should be tested and found as -1.
		 */
		processLoadFirstCall = osmxbean.getProcessCpuLoad();
		systemCpuLoadFirstCall = osmxbean.getSystemCpuLoad();
	}

	/**
	 * Test the getProcessCpuLoad() API of OperatingSystemMXBean
	 */
	public  void testGetProcessCpuLoad() throws MalformedObjectNameException, IOException {
		int i = 0;
		double processLoad = 0;

		// First call to getProcessCpuLoad() should have returned -1 
		assertEquals(-1.0, processLoadFirstCall);

		// Generate some load before reading up Process CPU Time yet again.
		try {
			Thread[] busyObj = new Thread[NTHREADS];
			long counter = 0;

			for (; counter < NITERATIONS; counter++) {
				for (i = 0; i < NTHREADS; i++) {
					busyObj[i] = new Thread(new BusyThread( SLEEPINTERVAL * 500));
					busyObj[i].start();
				}
				Thread.sleep(1000);
				for (i = 0; i < NTHREADS; i++) {
					busyObj[i].join();
				}
			}
		} catch(InterruptedException e) {
			fail("Test Failed" + e);
		}

		// Now obtain the process's CPU load; ensure that load has increased. 
		processLoad = osmxbean.getProcessCpuLoad();

		// Process CPU Load should have increased and hence, in the [0.0, 1.0] range.
		if (processLoad >= 0 && processLoad <= 1.0 ) {
			// Do nothing
		} else {
			fail("processLoad should have been between 0 to 1.0 but " + "the value is: " + processLoad);
		}
	}

	/**
	 * Test the getSystemCpuLoad() API of OperatingSystemMXBean
	 */
	public void testGetSystemCpuLoad() throws MalformedObjectNameException, IOException {
		/* The API is unsupported on z/OS; remove this test and return
		 * once support is available. */
		if ((null != OSNAME) && (OSNAME.equalsIgnoreCase("z/OS"))) { 
			// Check for unsupported return code and just return
			assertEquals(-3.0, systemCpuLoadFirstCall);
			return;
		}

		// First call to getSystemCpuLoad() should have returned -1 
		assertEquals(-1.0, systemCpuLoadFirstCall);

		// Perform some CPU intensive work to ensure CPU cycles have 
		// been spent before obtaining CPU load.
		try {
			RandomNumber random = new RandomNumber();

			long limit = random.generateRandomNumber(1, Long.MAX_VALUE);

			// Form fibonacci series until the limit is reached.
			Thread thread1 = new Thread(new FibonacciSeriesOSMXBean(limit));
			thread1.start();

			thread1.join();
			System.out.println(SHARED_RESULT);

			// Measuring the system CPU load after starting the work load.
			double systemCpuLoad = osmxbean.getSystemCpuLoad();

			assertTrue("SystemCpuLoad should have been between 0.0 " + "to 1.0 but the value is " + systemCpuLoad, 
					(systemCpuLoad >= 0 && systemCpuLoad <= 1.0));

		} catch(InterruptedException ie) {
			fail("Test Failed" + ie);
		}
	}
}

class FibonacciSeriesOSMXBean implements Runnable {
	long limit;
	long result;

	public FibonacciSeriesOSMXBean(long limit) {
		this.limit = limit;
	}

	public void run() {

		long start = 1;
		long next = 1;
		long result = start;

		while( result < (limit*0.75) ) {
			long output = start + next;
			result = output;			
			start = next;
			next = output;
		}

		TestOperatingSystemMXBean.SHARED_RESULT = result;
	}
}
