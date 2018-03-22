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
 * This java source file contains the EnvironmentData class, used to retrieve 
 * information provided by the Runtime and OperatingSystem MXBeans
 */
package net.openj9.test.jlm.resources;

import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.UndeclaredThrowableException;
import java.rmi.ConnectException;
import java.rmi.UnmarshalException;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.LoggingMXBean;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.junit.Assert;

import net.adoptopenjdk.test.jlm.remote.ServerConnector;
import net.adoptopenjdk.test.jlm.resources.Message;

public class EnvironmentData extends VMData {

	public EnvironmentData(String logFile) {
		super(logFile, null, true);
	}

	public EnvironmentData(ServerConnector profiler, String logFile) {
		super(logFile, profiler, false);
	}

	public void writeData (RuntimeMXBean rb, OperatingSystemMXBean osb, LoggingMXBean lb, boolean append) 
			throws UndeclaredThrowableException {

		Assert.assertFalse("RuntimeMXBean == null", rb == null);
		Assert.assertFalse("OperatingSystemMXBean == null", osb == null);

		openLogFile(append);

		try {
			out.println("Information produced at " + DateFormat.getDateTimeInstance().format(new Date()));
			out.println("");
			out.println("");

			// Get OS info and write to file
			invokeIBMOSBeanTest();
			
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

	public void writeData (MBeanServerConnection mbs, ObjectName rb, ObjectName osb, ObjectName lb, boolean append) 
			throws ConnectException, UnmarshalException {

		Assert.assertFalse("MBeanServerConnection == null", mbs == null);
		Assert.assertFalse("RuntimeMXBean == null", rb == null);
		Assert.assertFalse("OperatingSystemMXBean == null", osb == null);

		openLogFile(append);

		try {
			out.println("Information produced at " + DateFormat.getDateTimeInstance().format(new Date()));
			out.println("");
			out.println("");

			// Get OS info and write to file

			// IBM specific OperatingSystemMXBean API tests: 
			invokeIBMOSBeanTest();

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

	public void checkLoggerLevel(String connect_type, String logger, String level) {
		// Check the logger level has a valid value 
		if(!((level.equals("PARENT LEVEL")) || (level.equals("SEVERE")) || (level.equals("WARNING")) || (level.equals("INFO")) || 
				(level.equals("CONFIG")) || (level.equals("FINE")) || 
				(level.equals("FINER")) ||(level.equals("FINEST")))) {
			Assert.fail("Invalid Value return for " + logger + "'s Logger Level(" + connect_type + "test)");
		}
	}
}
