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
 * This java source file contains the ClassProfiler class. 
 * This class creates a profile of the classes loaded, and    
 * retrieves class related information regularly from a remote VM
 */
package net.openj9.test.jlm.remote;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.UndeclaredThrowableException;
import java.rmi.ConnectException;
import java.rmi.UnmarshalException;
import java.util.logging.LogManager;
import java.util.logging.LoggingMXBean;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.junit.Assert;

import net.adoptopenjdk.test.jlm.remote.ServerConnector;
import net.adoptopenjdk.test.jlm.remote.TestArgs;
import net.adoptopenjdk.test.jlm.resources.Message;
import net.openj9.test.jlm.resources.EnvironmentData;

public class ClassProfiler extends ServerConnector {
	private PrintWriter csv;
	private EnvironmentData envData;

	private ClassProfiler(boolean useProxy, String logFile, String 
			csvFile, String hostName, int portNumber) {
		// Create the Data resources needed to collect the stats.
		super(useProxy, hostName, portNumber);
		this.envData = new EnvironmentData(this, logFile);
		createCSVFile(csvFile);
	}

	private ClassProfiler(boolean useProxy, String logFile, String csvFile, 
			String user, String pw, String hostName,
			int portNumber) {
		// Create the Data resources needed to collect the stats.
		super(useProxy, user, pw, hostName, portNumber);
		this.envData = new EnvironmentData(this, logFile);
		createCSVFile(csvFile);
	}

	private static void usage() {
		TestArgs.usageProfiler("ClassProfiler");
		Assert.fail("Usage error");
	}

	public static void main(String[] args) {
		ClassProfiler profiler = null;
		if (TestArgs.parseArgs(args)) {
			if (TestArgs.isUseAuthorisation()) {
				profiler = new ClassProfiler(TestArgs.isUseProxy(), TestArgs.getLogPathname(),
						TestArgs.getCSVPathname(), TestArgs.getUsername(), TestArgs.getPassword(), TestArgs.getHost(),
						TestArgs.getPort());
			} else {
				profiler = new ClassProfiler(TestArgs.isUseProxy(), TestArgs.getLogPathname(),
						TestArgs.getCSVPathname(), TestArgs.getHost(), TestArgs.getPort());
			}
		} else {
			usage();
		}

		// Collect Stats differently depending on whether 'proxy' or 'server' has been chosen
		Assert.assertTrue("ClassProfiler is null", profiler != null);
		
		if (TestArgs.isUseProxy()) {
			profiler.getStatsViaProxy();
		} else {
			profiler.getStatsViaServer();
		}
	}

	private void getStatsViaProxy() {
		RuntimeMXBean runtimeBean = null;
		OperatingSystemMXBean osBean = null;
		LoggingMXBean logBean = null;

		// Get the proxies for the runtime, os, log and class MXBeans
		try {
			runtimeBean = ManagementFactory.newPlatformMXBeanProxy(this.mbs, ManagementFactory.RUNTIME_MXBEAN_NAME,
					RuntimeMXBean.class);

			osBean = ManagementFactory.newPlatformMXBeanProxy(this.mbs, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME,
					OperatingSystemMXBean.class);

			logBean = ManagementFactory.newPlatformMXBeanProxy(this.mbs, LogManager.LOGGING_MXBEAN_NAME,
					LoggingMXBean.class);
		} catch (IOException ioe) {
			Message.logOut("A communication problem occurred when accessing the MBeanServerConnection");
			ioe.printStackTrace();
			Assert.fail("A communication problem occurred when accessing the MBeanServerConnection");
		}

		try {
			Message.logOut("Starting to write data");
			// Record the environment data in the log
			this.envData.writeData(runtimeBean, osBean, logBean, false);
		} catch (UndeclaredThrowableException ue) {
			// If the exception was caused by a Connect or Unmarshal Exception
			// assume the monitored JVM has finished.
			Throwable cause = ue.getCause();
			Class<ConnectException> connectExcept = ConnectException.class;
			Class<UnmarshalException> unmarshalExcept = UnmarshalException.class;
			String msg = "";
			if (connectExcept.isInstance(cause)) {
				msg = "Exiting as ConnectException thrown receiving data from the connected JVM.  This may mean the JVM we are connected to has finished. ";
			}
			else if (unmarshalExcept.isInstance(cause)) {
				msg = "Exiting as UnmarshalException thrown receiving data from the connected JVM.  This may mean the JVM we are connected to has finished. ";
			}
			msg += ue.getMessage();
			this.closeCSVFile();
			Message.logOut(msg);
			ue.printStackTrace();
			Assert.fail(msg);
		} finally {
			this.closeCSVFile();
		}
	}

	private void getStatsViaServer() {
		ObjectName srvRuntimeName = null;
		ObjectName srvOSName = null;
		ObjectName srvLogName = null;
	
		try {
			// Set up the ObjectName needed to reference the ThreadMXBean,
			// RuntimeMXBean and OperatingSystemMXBean
			srvRuntimeName = new ObjectName(ManagementFactory.RUNTIME_MXBEAN_NAME);
			srvOSName = new ObjectName(ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME);
			srvLogName = new ObjectName(LogManager.LOGGING_MXBEAN_NAME);
		} catch (MalformedObjectNameException mone) {
			Message.logOut("Problem with creating the ObjectName for a MXBean");
			mone.printStackTrace();
			Assert.fail("Problem with creating the ObjectName for a MXBean");
		} 

		// The first time we write to the file, 'append' needs to be false.
		// From the second time onwards, it needs to be true*/
		try {
			this.envData.writeData(this.mbs, srvRuntimeName, srvOSName, srvLogName, false);
		// If the exception is a Connect or Unmarshal Exception, assume the
		// monitored JVM has finished 
		} catch (ConnectException ce) {
			this.closeCSVFile();
			String msg = "Exiting as ConnectException thrown receiving data from the connected JVM.  This may mean the JVM we are connected to has finished. ";
			msg += ce.getMessage();
			Message.logOut(msg);
			ce.printStackTrace();
			Assert.fail(msg);
		} catch (UnmarshalException ue) {
			this.closeCSVFile();
			String msg = "Exiting as UnmarshalException thrown receiving data from the connected JVM.  This may mean the JVM we are connected to has finished. ";
			msg += ue.getMessage();
			Message.logOut(msg);
			ue.printStackTrace();
			Assert.fail(msg);
		} finally {
			this.closeCSVFile();
		}
	}

	private void createCSVFile(String csvFile) {
		try {
			// Open the cvs file
			this.csv = new PrintWriter(new FileWriter(new File(csvFile), false));
			// write headings to file
			this.csv.println("Time (seconds), Loaded Class Count, Total Loaded Class Count, Unloaded Class Count");
		} catch (IOException ie) {
			Message.logOut("Unable to write to file " + csvFile);
			ie.printStackTrace();
			Assert.fail("Unable to write to file " + csvFile);			
		}
	}

	private void closeCSVFile() {
		this.csv.close();
	}
}
