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

/*
 * Description:
 *   This java source file contains the MemoryPoolExtensionCommand class, used
 *   to retrieve information provided by the Memory Pool Extensions
*/
package net.openj9.test.extensions;

import java.lang.management.*;
import org.junit.Assert;
import java.io.*;
import javax.management.*;
import java.util.*;
import javax.management.openmbean.*;

/** 
 * JLM Bean test extension class that contains IBM specific API testing
 * for java.lang.management.MemoryPoolMXBean. 
 *  
 * We only load this class if we already know we're running on an IBM JVM, so the 
 * MemoryProfiler test should still run successfully with a non-IBM JVM.
 * However remember this class can only be compiled with an IBM JVM.
 */

public class MemoryPoolExtensionCommand extends IBMBeanExtension {

	public MemoryPoolExtensionCommand(PrintWriter out, MBeanServerConnection mbs,
			boolean isLocal, boolean isProxy) {
		super(out, mbs, isLocal, isProxy);
	}

	public void execute () {
		// If running locally, get the beans again so we can use the IBM-specific functions
        if (isLocal) {
            List<java.lang.management.MemoryPoolMXBean> mpBeans = ManagementFactory.getMemoryPoolMXBeans();

            out.println("  IBM-specific information:");
            out.println("");

            for (java.lang.management.MemoryPoolMXBean mpb : mpBeans) {
                // For each one, cast downwards so that we can use the IBM-specific functions
                com.ibm.lang.management.MemoryPoolMXBean ibmBean = (com.ibm.lang.management.MemoryPoolMXBean) mpb;

                out.println("    Memory Pool name:       " + ibmBean.getName());
                MemoryUsage usage = ibmBean.getPreCollectionUsage();

                // May return null if the functionality is not supported
                if (usage != null) {
                    checkMemoryUsage (usage, ibmBean.getName() + " PreCollectionUsage", "local");
                } else {
                    out.println("     This VM does not support the pre-collection memory usage functionality for this pool.");
                }
                out.println("");
            }
        } else {
        	// If we're not running locally, get a new MBeanServerConnection
            if (isProxy) {
            	try {
					List<com.ibm.lang.management.MemoryPoolMXBean> mpBeans = new ArrayList<com.ibm.lang.management.MemoryPoolMXBean>();
					Set<?> mpNames = mbs.queryNames(new ObjectName(ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE + ",*"), null);

                    // Get an MXBean proxy for each of the names 
                    // returned, use the proxy to access the MXBeans
                    for (Object mpName : mpNames) {
                    	ObjectName mp = (ObjectName) mpName;
                    	com.ibm.lang.management.MemoryPoolMXBean mpBean = 
                    			ManagementFactory.newPlatformMXBeanProxy(mbs, mp.toString(),
                    					com.ibm.lang.management.MemoryPoolMXBean.class);
                        mpBeans.add(mpBean);
                    }

                    out.println("  IBM-specific information:");
                    out.println("");

                    for (com.ibm.lang.management.MemoryPoolMXBean mpBean : mpBeans) {
                    	MemoryUsage usage = mpBean.getPreCollectionUsage();
                    	out.println("    Memory Pool name:       " + mpBean.getName());

                        // May return null if the functionality is not supported
                        if (usage != null) {
                            checkMemoryUsage (usage, mpBean.getName() + " PreCollectionUsage", "proxy");
                        } else {
                            out.println("     This VM does not support the pre-collection memory usage functionality for this pool.");
                        }
                        out.println("");
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
                   	Set<?> srvMPNames = mbs.queryNames(new ObjectName(
                   			ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE + ",*"), 
                   			null);

                    out.println("  IBM-specific information:");
                    out.println("");

                    for (Object mpbObj : srvMPNames) {
                        ObjectName mpb = (ObjectName) mpbObj; 
                        CompositeData cd = (CompositeData)(mbs.getAttribute(mpb, "PreCollectionUsage"));
                        String pool_name = (String)(mbs.getAttribute(mpb, "Name"));
                        out.println("    Memory Pool name:       " + pool_name);

                        // May be null if this functionality is not supported
                        if (cd != null) {
                            MemoryUsage usage = MemoryUsage.from(cd);
                            checkMemoryUsage (usage, pool_name + " PreCollectionUsage", "server connection");
                        }else {
                            out.println("     This VM does not support the pre-collection memory usage functionality for this pool.");
                        }
                        out.println("");
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

	private void checkMemoryUsage (MemoryUsage memUsage, String usage_type, String connect_type) {
		long init   = memUsage.getInit();
		long used   = memUsage.getUsed();
		long commit = memUsage.getCommitted();
		long max    = memUsage.getMax();
		
		out.println("     Usage before last GC:  " + memUsage.toString()); 
		checkMemoryUsageElements(connect_type, usage_type, init, max, commit, used);
	}
	
    public void checkMemoryUsageElements(String connect_type, String usage_type, long init, long max, long commit, long used) {
    	
    	Assert.assertTrue("Invalid value returned for " + usage_type + " Init value (" + connect_type + " test)", init >= -1);
    	Assert.assertTrue("Invalid value returned for " + usage_type + " Used value (" + connect_type + " test)", used >= 0);    	
    	Assert.assertTrue(usage_type + " Commited value returned was less then the Used value (" + connect_type + " test)", commit >= used);
    	Assert.assertTrue(usage_type + " Max value returned was less then the Commited value (" + connect_type + " test)", max >= commit || max == -1);
    }
}
