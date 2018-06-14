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
 * This java source file contains the MemoryData class, used
 * to retrieve information provided by the Memory, MemoryManager
 * and MemoryPool MXBeans
 */
package net.openj9.test.jlm.resources;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.reflect.UndeclaredThrowableException;
import java.rmi.ConnectException;
import java.rmi.UnmarshalException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

//import java.lang.management.*;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.junit.Assert;

import net.adoptopenjdk.test.jlm.remote.ServerConnector;
import net.adoptopenjdk.test.jlm.resources.Message;

public class MemoryData extends VMData {

	public MemoryData(String logFile) {
		super(logFile, null, true);
	}

	public MemoryData (ServerConnector profiler, String logFile) {
		super(logFile, profiler, false);
	}

	public void writeData(MemoryMXBean mb, List<MemoryManagerMXBean> mmbList, List<MemoryPoolMXBean> mpbList,
			List<GarbageCollectorMXBean> gcbList, boolean append)
					throws UndeclaredThrowableException {
		Assert.assertFalse("MemoryMXBean == null", mb == null);
		Assert.assertFalse("List<MemoryManagerMXBean> == null", mmbList == null);
		Assert.assertFalse("List<MemoryPoolMXBean> == null", mpbList == null);
		Assert.assertFalse("List<GarbageCollectorMXBean> gcbList == null", gcbList == null);

		openLogFile(append);

		try {
			// Record the time and whether the verbose output is enabled
			out.println("Memory Information Retrieved at " + 
					DateFormat.getDateTimeInstance().format(new Date()));

			String state = "disabled";
			
			if (mb.isVerbose()) {
				state = "enabled";
			}
			
			out.println("Verbose output is " + state + " for the memory system"); 
			out.println("");
			out.println("");

			// Get stats for the VM's Memory and write to file
			out.println("MEMORY STATS");

			// IBM-specific functions to be tested if we're running with an IBM JVM
			invokeIBMMemoryBeanTest();
			
			out.println("");
			out.println("");

			// Get the info for the Garbage Collector and write to file
			out.println("GARBAGE COLLECTOR INFORMATION");

			// IBM-specific functions to be tested if we're running with an IBM JVM
			invokeIBMGCBeanTest();

			out.println("");
			out.println("");

			// Get the info for the memory managers and write to file
			out.println("MEMORY MANAGER INFORMATION");

			// IBM-specific functions to be tested if we're running with an IBM JVM
			invokeIBMMemoryPoolBeanTest();

			out.println("");
			out.println("");

			closeLogFile();
		} catch (UnsupportedOperationException uoe) {
			Message.logOut("One of the operations you tried is not supported");
			uoe.printStackTrace();
			Assert.fail("One of the operations you tried is not supported");
		} catch (SecurityException se) {
			Message.logOut("Insufficient privileges to access OS info");
			se.printStackTrace();
			Assert.fail("Insufficient privileges to access OS info");
		}
	}

	public void writeData (MBeanServerConnection mbs, ObjectName mb, Set<?> mmbList, Set<?> mpbList,  
			Set<?> gcbList, boolean append) throws ConnectException, UnmarshalException {
		Assert.assertFalse("MBeanServerConnection == null", mbs == null);
		Assert.assertFalse("MemoryMXBean == null", mb == null);
		Assert.assertFalse("List<MemoryManagerMXBean> == null", mmbList == null);
		Assert.assertFalse("List<MemoryPoolMXBean> == null", mpbList == null);
		Assert.assertFalse("List<GarbageCollectorMXBean> gcbList == null", gcbList == null);

		openLogFile(append);

		try {
			// Record the time and whether the verbose output is enabled
			out.println("Memory Information Retrieved at " + DateFormat.getDateTimeInstance().format(new Date()));

			String state = "disabled";
			if (((Boolean)(mbs.getAttribute(mb, "Verbose"))).booleanValue()) {
				state = "enabled";
			}
			out.println("Verbose output is " + state + " for the memory system"); 
			out.println("");
			out.println("");

			// Get Stats for the VMs Memory and write to file
			out.println("MEMORY STATS"); 
			invokeIBMMemoryBeanTest();

			out.println("");
			out.println("");

			// Get the info for the Garbage Collector and write to file
			invokeIBMGCBeanTest();

			out.println("");

			// Get the info for the memory pool and write to file
			out.println("MEMORY POOLS INFORMATION");
			invokeIBMMemoryPoolBeanTest();

			out.println("");
			out.println("");

			closeLogFile();

		} catch (UnsupportedOperationException uoe) {
			Message.logOut("One of the operations you tried is not supported");
			uoe.printStackTrace();
			Assert.fail("One of the operations you tried is not supported");
		} catch (UnmarshalException ue) {
			Assert.fail("UnmarshalException: \n" + ue.getMessage());
		} catch (ConnectException ce) {
			Assert.fail("ConnectException: \n" + ce.getMessage());
		} catch (IOException ie) {
			Message.logOut("Problem with server connection");
			ie.printStackTrace();
			Assert.fail("Problem with server connection");
		} catch (SecurityException se) {
			Message.logOut("Insufficient privileges to access OS info");
			se.printStackTrace();
			Assert.fail("Insufficient privileges to access OS info");
		} catch (MBeanException mbe) {
			Message.logOut("Problem with the MBean access");
			mbe.printStackTrace();
			Assert.fail("Problem with the MBean access");
		} catch (AttributeNotFoundException ae) {
			Message.logOut("Attribute does not exist on the MBean");
			ae.printStackTrace();
			Assert.fail("Attribute does not exist on the MBean");
		} catch (InstanceNotFoundException ie) {
			Message.logOut("MBean Instance not found");
			ie.printStackTrace();
			Assert.fail("MBean Instance not found");
		} catch (ReflectionException re) {
			Message.logOut("Problem with the reflection of the MBean");
			re.printStackTrace();
			Assert.fail("Problem with the reflection of the MBean");
		}
	}
}
