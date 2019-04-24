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
/*
 * Description:
 *   This java source file contains the MemoryExtensionCommand class, used
 *   to retrieve information provided by the Memory Extensions
 */
package net.openj9.test.extensions;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.junit.Assert;

/** 
 * JLM Bean test extension class that contains IBM specific API testing
 * for java.lang.management.MemoryMXBean. 
 *  
 * We only load this class if we already know we're running on an IBM JVM, so the 
 * MemoryProfiler test should still run successfully with a non-IBM JVM.
 * However remember this class can only be compiled with an IBM JVM.
 */

public class MemoryExtensionCommand extends IBMBeanExtension {

	public MemoryExtensionCommand(PrintWriter out, 
			MBeanServerConnection mbs,
			boolean isLocal, boolean isProxy) {
		super(out, mbs, isLocal, isProxy);
	}

	public void execute() {
		// If running locally, get the bean again so we can use the IBM-specific functions
        if (isLocal) {
            java.lang.management.MemoryMXBean mb = ManagementFactory.getMemoryMXBean();

            // Cast downwards so that we can use the IBM-specific functions
            com.ibm.lang.management.MemoryMXBean ibmBean = (com.ibm.lang.management.MemoryMXBean) mb;
            
            long min_size = ibmBean.getMinHeapSize();
            long max_size = ibmBean.getMaxHeapSize();
            long max_limit = ibmBean.getMaxHeapSizeLimit();
            
            // Check the heap information is reasonable then write the values to the log
            checkHeapInfo("local", max_size, max_limit, min_size, ibmBean.getGCMode());

            out.print("    Runtime resetting of \n    max heap size supported?: ");
            if (ibmBean.isSetMaxHeapSizeSupported()) {
                out.println("Yes");
            } else {
                out.println("No");
            }
            
            long cache_size = ibmBean.getSharedClassCacheSize();          	
            long free_space = ibmBean.getSharedClassCacheFreeSpace();
            
            // Check the shared class information is reasonable, then record the values in the log
            checkSharedClassInfo("local", cache_size, free_space);

        } else {
        	// Find out if we're using a proxy or not - if yes, set up proxies again
            if (isProxy) {
            	try {
                	com.ibm.lang.management.MemoryMXBean mb = 
                        ManagementFactory.newPlatformMXBeanProxy(mbs, ManagementFactory.MEMORY_MXBEAN_NAME, 
                                                                 com.ibm.lang.management.MemoryMXBean.class);
                	long min_size = mb.getMinHeapSize();
                    long max_size = mb.getMaxHeapSize();
                    long max_limit = mb.getMaxHeapSizeLimit();
                    
                    // Check the heap information is reasonable 
                    // then write the values to the log
                    checkHeapInfo("proxy", max_size, max_limit, min_size, mb.getGCMode());
                    
                    out.print("    Runtime resetting of \n    max heap size supported?: ");
                    if (mb.isSetMaxHeapSizeSupported()) {
                        out.println("Yes");
                    } else {
                        out.println("No");
                    }

                    long cache_size = mb.getSharedClassCacheSize();          	
                    long free_space = mb.getSharedClassCacheFreeSpace();
                    
                    // Check the shared class information is reasonable, 
                    // then record the values in the log
                    checkSharedClassInfo("proxy", cache_size, free_space);
                    	
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    Assert.fail("A communication problem occurred when accessing the MBeanServerConnection");
                }
                
            } else { // We are going through the MBeanServerConnection

                try {
                    // Get the ObjectName that the MemoryMXBean is registered by
                    ObjectName srvMemName = new ObjectName(ManagementFactory.MEMORY_MXBEAN_NAME);
                    
                    Long min_size = (Long)mbs.getAttribute(srvMemName, "MinHeapSize");
                    Long max_size = (Long)mbs.getAttribute(srvMemName, "MaxHeapSize");
                    Long max_limit = (Long)mbs.getAttribute(srvMemName, "MaxHeapSizeLimit");
                    String gcMode = (String)mbs.getAttribute(srvMemName, "GCMode");
                    
                    // Check the heap information is reasonable 
                    // then write the values to the log
                    checkHeapInfo("server connection", max_size.longValue(), max_limit.longValue(), min_size.longValue(), gcMode);            
                    
                    // Check if resetting of max heap size supported
                    out.print("    Runtime resetting of \n    max heap size supported?: ");
                    if (((Boolean)(mbs.getAttribute(srvMemName, "SetMaxHeapSizeSupported"))).booleanValue()) {
                        out.println("Yes");
                    } else {
                        out.println("No");
                    }
                    
                    Long cache_size = (Long)mbs.getAttribute(srvMemName, "SharedClassCacheSize");          	
                    Long free_space = (Long)mbs.getAttribute(srvMemName, "SharedClassCacheFreeSpace");
                    
                    // Check the shared class information is reasonable, then record the values in the log
                    checkSharedClassInfo("server connection", cache_size.longValue(), free_space.longValue());
                } catch (InstanceNotFoundException ie) {
                    ie.printStackTrace();
                    Assert.fail("MBean instance not found");
                } catch (ReflectionException re) {
                    re.printStackTrace();
                    Assert.fail("Problem with the reflection of the MBean");
                } catch (IOException ie) {
                    ie.printStackTrace();
                    Assert.fail("Problem with server connection");
                } catch (MBeanException mbe) {
                    mbe.printStackTrace();
                    Assert.fail("Problem with the MBean access");
                } catch (AttributeNotFoundException ae) {
                    ae.printStackTrace();
                    Assert.fail("Attribute does not exist on the MBean");
                } catch (MalformedObjectNameException mone) {
                    mone.printStackTrace();
                    Assert.fail("Problem with creating the ObjectName for a MXBean");
                }
            }
        }
    }
    
    public void checkHeapInfo(String connect_type, long max_size, long max_limit, long min_size, String gcMode) {
        // Print out the heap information in logfile
        out.println("  IBM-specific information:");
        out.println("    Current GC mode:        " + gcMode);
        out.println("    Minimum heap size:      " + min_size + " bytes");
        out.println("    Current max heap size:  " + max_size + " bytes");
        out.println("    Max heap size limit:    " + max_limit + " bytes");
        
    	// Check the heap's maximum and minimum sizes and limits are not negative
        Assert.assertTrue("Negative value returned for Min Heap Size. Value was retrieved using ibm extensions (" + connect_type + " test)", min_size >= 0);
        Assert.assertTrue("Max Heap Size returned was less then the Min Heap Size value.  Values were retrieved using ibm extensions (" + connect_type + " test)", max_size >= min_size);
        Assert.assertTrue("Max Heap Size Limit returned was less then the Max Heap Size value.  Values were retrieved using ibm extensions (" + connect_type + " test)", max_limit >= max_size);
    }     	

    public void checkSharedClassInfo(String connect_type, long cache_size, long free_space) {
    	out.println("    Shared class cache size:" + cache_size + " bytes");
        out.println("    Cache free space:       " + free_space + " bytes");

        // Check the shared class cache size and free space are not negative. 
        Assert.assertTrue("Negative value returned for Shared Class Cache Size. Value was retrieved using ibm extensions (" + connect_type + " test)", cache_size >= 0);
        
        // Check the free space in the cache is not bigger then the cache
        Assert.assertTrue("Negative value returned for Size of Free Space in the Shared Class Cache, Value was retrieved using ibm extensions (" + connect_type + " test)", free_space >= 0);
        Assert.assertTrue("Error: Free Space in Class Cache returned was greater then the Cache Size value.  Values were retrieved using ibm extensions (" + connect_type + " test)", free_space <= cache_size);
    }
}
