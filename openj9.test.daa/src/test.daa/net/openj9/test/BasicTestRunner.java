/*******************************************************************************
* Copyright (c) 2017 IBM Corp.
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

package net.openj9.test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.lang.Class;
import java.util.List;
import java.util.Iterator;

public class BasicTestRunner {

	/**
	 * Invokes JUnitCore.runClasses on the given class and prints the failures.
	 * 
	 * @param  c              the class to execute with JUnitCore.runClasses
	 * @return wasSuccessful  whether all tests were successful
	 */
	public static boolean runClass(Class<?> c) {
		Result res = JUnitCore.runClasses(c);
		System.out.println("\n" + c + " Total: " + res.getRunCount() + " Fail: "+ res.getFailureCount() + " Ignore: " + res.getIgnoreCount());

		if (!res.wasSuccessful()) {
			List<Failure> failures = res.getFailures();

			Iterator<Failure> it = failures.iterator();
			
			while (it.hasNext()) {
				Failure fail = it.next();
				System.err.println("\tFAILURE: " + fail.getDescription() + " - " + fail.getMessage());
			}
		}
		return res.wasSuccessful();
	}
}