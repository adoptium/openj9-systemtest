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

/*
 * Description:
 *   This java source file contains the MemoryPoolExtensionCommand class, used
 *   to retrieve information provided by the Memory Pool Extensions
 */
package net.openj9.test.extensions;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

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
 * for java.lang.management.MemoryPoolMXBean. 
 *  
 * We only load this class if we already know we're running on an IBM JVM, so the 
 * MemoryProfiler test should still run successfully with a non-IBM JVM.
 * However remember this class can only be compiled with an IBM JVM.
 */

public class OperatingSystemExtensionCommand extends IBMBeanExtension {

    public OperatingSystemExtensionCommand(PrintWriter out, 
    		MBeanServerConnection mbs, boolean isLocal,
			boolean isProxy) {
		super(out, mbs, isLocal, isProxy);
	}

	public void execute () {
        out.println("  IBM-specific information:");
        out.println("");

        if (isLocal) {
        	// If running locally, get the beans again so we can use the IBM-specific functions
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            
            // Cast downwards so that we can use the IBM-specific functions
            com.ibm.lang.management.OperatingSystemMXBean ibmOsb = (com.ibm.lang.management.OperatingSystemMXBean) osBean;
            
            long free_physical_memory =  ibmOsb.getFreePhysicalMemorySize();
            long process_virtual_memory =  ibmOsb.getCommittedVirtualMemorySize();
            long process_private_memory =  ibmOsb.getProcessPrivateMemorySize();
            long process_physical_memory =  ibmOsb.getProcessPhysicalMemorySize();
            long process_cpu_time =  ibmOsb.getProcessCpuTime();
            checkOsInfo (free_physical_memory, process_virtual_memory,
            		process_private_memory, process_physical_memory, process_cpu_time);
            
            out.println("");
            
        } else {
        	// Find out if we're using a proxy or not - if yes, set up proxies again
            if (isProxy) {
				try {
					com.ibm.lang.management.OperatingSystemMXBean ibmOsb = 
							ManagementFactory.newPlatformMXBeanProxy(mbs, 
									ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, 
					                com.ibm.lang.management.OperatingSystemMXBean.class);
					
	                long free_physical_memory_size =  ibmOsb.getFreePhysicalMemorySize();
	                long process_virtual_memory_size =  ibmOsb.getCommittedVirtualMemorySize();
	                long process_private_memory_size =  ibmOsb.getProcessPrivateMemorySize();
	                long process_physical_memory_size =  ibmOsb.getProcessPhysicalMemorySize();
	                long process_cpu_time =  ibmOsb.getProcessCpuTime();
	                checkOsInfo(free_physical_memory_size, process_virtual_memory_size,
	                		process_private_memory_size, process_physical_memory_size, process_cpu_time);
				} catch (IOException e) {
					e.printStackTrace();
                    Assert.fail("A communication problem occurred when "
                    		+ "accessing the Proxy connetion for the OperatingSystemMXBean");
				}
				
            } else { // We must be going via the MBeanServerConnection 
            	try {
            		ObjectName srvOSBeanName = new ObjectName(ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME);
            		long free_physical_memory_size =  (long)mbs.getAttribute(srvOSBeanName, "FreePhysicalMemorySize");
                    long process_virtual_memory_size =  (long)mbs.getAttribute(srvOSBeanName, "CommittedVirtualMemorySize");
                    long process_private_memory_size =  (long)mbs.getAttribute(srvOSBeanName, "ProcessPrivateMemorySize");
                    long process_physical_memory_size =  (long)mbs.getAttribute(srvOSBeanName, "ProcessPhysicalMemorySize");
                    long process_cpu_time =  (long)mbs.getAttribute(srvOSBeanName, "ProcessCpuTime");
                    checkOsInfo(free_physical_memory_size, process_virtual_memory_size,
                    		process_private_memory_size, process_physical_memory_size, process_cpu_time);
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

    private void checkOsInfo(long free_physical_memory_size, long process_virtual_memory_size,
    		long process_private_memory_size, long process_physical_memory_size, long process_cpu_time) {
    	out.println("  Free Physical Memory Size:   " + free_physical_memory_size);
    	out.println("  Process Virtual Memory Size: " + process_virtual_memory_size);
    	out.println("  Process Private Memory Size: " + process_private_memory_size);
    	out.println("  Process Physical Memory Size:" + process_physical_memory_size);
    	out.println("  Process CPU time:  			" + process_cpu_time);
    	out.println("");
    	out.println("");
    }
}
