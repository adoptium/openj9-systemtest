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

/*
 * Description:
 *   This java source file contains the GCExtensionCommand class, used
 *   to retreive information provided by the IBM GC Extensions
 */
package net.openj9.test.extensions;

import java.lang.management.*;
import java.io.*;

import javax.management.*;
import java.util.*;

import org.junit.Assert;

/**
 * JLM Bean test extension class that contains IBM specific API testing
 * for java.lang.management.GarbageCollectorMXBean.
 *
 * We only load this class if we already know we're running on an IBM JVM, so the
 * MemoryProfiler test should still run successfully with a non-IBM JVM.
 * However remember this class can only be compiled with an IBM JVM.
 */

public class GCExtensionCommand extends IBMBeanExtension {

	public GCExtensionCommand(PrintWriter out,
    		MBeanServerConnection mbs,
    		boolean isLocal, boolean isProxy) {
		super(out, mbs, isLocal, isProxy);
	}

	public void execute () {
		// If we are running locally, get the beans again
        if (isLocal) {
            List<java.lang.management.GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();

            out.println("  IBM-specific information:");
            out.println("");

            // Get the GC MXBeans, check the values returned by the MXBean
            // and print the values to the log
            for (java.lang.management.GarbageCollectorMXBean gcBean : gcBeans) {
                // for each bean, cast downwards so we can use the IBM-specific functions
                com.ibm.lang.management.GarbageCollectorMXBean ibmBean = (com.ibm.lang.management.GarbageCollectorMXBean) gcBean;
                String gc_name = ibmBean.getName();
                long gc_cnt[] = {0,0};
                gc_cnt[0] = ibmBean.getCollectionCount();
                long start_time = ibmBean.getLastCollectionStartTime();
                long end_time = ibmBean.getLastCollectionEndTime();
                gc_cnt[1] = ibmBean.getCollectionCount();
            	long memoryUsed = ibmBean.getMemoryUsed();
                long totalCompact = ibmBean.getTotalCompacts();
                long totalMemoryFreed = ibmBean.getTotalMemoryFreed();
                checkGCStats("local", gc_cnt, gc_name, start_time, end_time, memoryUsed, totalCompact, totalMemoryFreed);
            }
        } else {
        	// Find out if we're using a proxy or not - if yes, set up proxies again
            if (isProxy) {
            	// Use the proxy to the GC MXBean to get info about the GC,
            	// Check the values are valid and written them to the log 
                List<com.ibm.lang.management.GarbageCollectorMXBean> gcBeans = new ArrayList<com.ibm.lang.management.GarbageCollectorMXBean>();

                try {
                    Set<?> gcNames  = mbs.queryNames(new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",*"), null);
                    out.println("  IBM-specific information:");
                    out.println("");

                    // Get an MXBean proxy for each of the names returned,
                    // use the proxy to access the MXBeans
                    for (Object gcName : gcNames){
                    	ObjectName gc = (ObjectName) gcName;
						com.ibm.lang.management.GarbageCollectorMXBean gcBean = ManagementFactory.newPlatformMXBeanProxy(mbs, gc.toString(),
								com.ibm.lang.management.GarbageCollectorMXBean.class);
                        gcBeans.add(gcBean);
                    }

                    for (com.ibm.lang.management.GarbageCollectorMXBean gcBean : gcBeans) {
                    	String gc_name = gcBean.getName();
                        long gc_cnt[] = {0,0};
                        gc_cnt[0] = gcBean.getCollectionCount();
                        long start_time = gcBean.getLastCollectionStartTime();
                        long end_time = gcBean.getLastCollectionEndTime();
                        gc_cnt[1] = gcBean.getCollectionCount();
                        long memoryUsed = gcBean.getMemoryUsed();
                        long totalCompact = gcBean.getTotalCompacts();
                        long totalMemoryFreed = gcBean.getTotalMemoryFreed();
                        checkGCStats("proxy", gc_cnt, gc_name, start_time, end_time, memoryUsed, totalCompact, totalMemoryFreed);
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    Assert.fail("A communication problem occurred when accessing the MBeanServerConnection");

                } catch (MalformedObjectNameException mone) {
                    mone.printStackTrace();
                    Assert.fail("Problem with creating the ObjectName for a MXBean");
                }

            } else { // We must be going via the MBeanServerConnection
            	try {
            		// Use the server connection to the GC MXBean to get info about the GC,
            		// Check the values are valid and written them to the log
            		Set<?> srvGCNames = mbs.queryNames(new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",*"), null);

                    out.println("  IBM-specific information:");
                    out.println("");

                    for (Object gcbObj : srvGCNames) {
                        ObjectName gcb = (ObjectName) gcbObj;
                        String gc_name = ((String)(mbs.getAttribute(gcb, "Name")));
                        long gc_cnt[] = {0,0};
                        gc_cnt[0] = ((Long)mbs.getAttribute(gcb, "CollectionCount")).longValue();
                        long start_time = ((Long)mbs.getAttribute(gcb, "LastCollectionStartTime")).longValue();
                        long end_time = ((Long)mbs.getAttribute(gcb, "LastCollectionEndTime")).longValue();
                        gc_cnt[1] = ((Long)mbs.getAttribute(gcb, "CollectionCount")).longValue();
                        long memoryUsed = ((Long)mbs.getAttribute(gcb, "MemoryUsed")).longValue();
                        long totalCompact = ((Long)mbs.getAttribute(gcb, "TotalCompacts")).longValue();
                        long totalMemoryFreed = ((Long)mbs.getAttribute(gcb, "TotalMemoryFreed")).longValue();
                        checkGCStats("server", gc_cnt, gc_name, start_time, end_time, memoryUsed, totalCompact, totalMemoryFreed);
                    }
            	} catch (IOException ioe) {
                    ioe.printStackTrace();
                    Assert.fail("A communication problem occurred when accessing the MBeanServerConnection");
            	} catch (MalformedObjectNameException mone) {
                    mone.printStackTrace();
                    Assert.fail("Problem with creating the ObjectName for a MXBean");
            	} catch (MBeanException mbe) {
                    mbe.printStackTrace();
                    Assert.fail("Problem with the MBean access");
            	} catch (AttributeNotFoundException ae) {
                    ae.printStackTrace();
                    Assert.fail("Attribute does not exist on the MBean");
            	} catch (InstanceNotFoundException ie) {
                    ie.printStackTrace();
                    Assert.fail("MBean Instance not found");
            	} catch (ReflectionException re) {
                    re.printStackTrace();
                    Assert.fail("Problem with the reflection of the MBean");
                }
           }
        }
    }

    private void checkGCStats(String connect_type, long[] gc_cnt, String gc_name, 
    		long start_time, long end_time, long memoryUsed, long totalCompact, 
    		long totalMemoryFreed) {
    	// Print out the GC information in logfile
        out.println("    Name of GC manager:     " + gc_name);
        out.println("    Last GC start time:     " + start_time + " milliseconds");
        out.println("    Last GC finish time:    " + end_time + " milliseconds");
        out.println("    Memory Used:            " + memoryUsed);
        out.println("    Total Compacts:         " + totalCompact);
        out.println("    Total Memory Freed:     " + totalMemoryFreed);
        out.println("    Duration of last GC:    " + (end_time - start_time) + " milliseconds");
        out.println("");

    	/*The start time and end time are stored as second since epoc
    	 * check the start, end time is not negative and the end time is after the
    	 * start time if there has been a GC.  If there has not been a GC check
    	 * the start and end time is zero */
        Assert.assertTrue("Incorrected value for GC 1st count " + gc_cnt[0], gc_cnt[0] >= -1);
        Assert.assertTrue("Incorrected value for GC 2nd count " + gc_cnt[1], gc_cnt[1] >= -1);

    	if (gc_cnt[0] > 0) {
        	Assert.assertTrue("Incorrect value returned for " + gc_name + "'s last GC Start Time: " + start_time + " (" + connect_type + " test)", start_time >= 0);
        	Assert.assertTrue("Incorrect value returned for " + gc_name + "'s last GC End Time: " + end_time + " (" + connect_type + " test)", end_time >= 0);
        	Assert.assertTrue(gc_name + "'s GC Start Time: " + start_time + " is after its GC End Time: " + end_time + " after " + gc_cnt + " GCs (" + connect_type + " test)", start_time <= end_time);
        }
    }
}
