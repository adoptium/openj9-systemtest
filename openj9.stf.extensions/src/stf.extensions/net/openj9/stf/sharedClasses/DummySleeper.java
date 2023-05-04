/*******************************************************************************
* Copyright (c) 2017, 2023 IBM Corp.
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

package net.openj9.stf.sharedClasses;

/**
 * A class to do nothing but sleep. Intended to allow a JVMTI agent to run
 *
 */
public class DummySleeper {

	public static void main(String[] args) {
		System.out.println("Entered DummySleeper main method");
		try {
			Thread.sleep(1 * 1000);
		} catch (InterruptedException e) {
			System.out.println("Caught an interrupted exception");
		}
		System.out.println("Finished with the sleep");
		System.out.println("DummySleeper COMPLETED SUCCESSFULLY");

	}
	
}
