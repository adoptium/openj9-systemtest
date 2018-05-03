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
package net.openj9.test.extensions;

public class Constants {
	public static final String GCPOLICY_GENCON = "-Xgcpolicy:gencon";
	public static final String GCPOLICY_BALANCED = "-Xgcpolicy:balanced";
	public static final String GCPOLICY_OPTTHRUPUT = "-Xgcpolicy:optthruput";
	public static final String GCPOLICY_OPTAVGPAUSE = "-Xgcpolicy:optavgpause";
	public static final String GCPOLICY_METRONOME = "-Xgcpolicy:metronome";

	public static final String POOLNAME_NURSERY_SURVIVOR = "nursery-survivor";
	public static final String POOLNAME_NURSERY_ALLOCATE = "nursery-allocate";
	public static final String POOLNAME_TENURED_LOA = "tenured-LOA";
	public static final String POOLNAME_TENURED_SOA = "tenured-SOA";
	public static final String POOLNAME_TENURED = "tenured";
	public static final String POOLNAME_BALANCED_RESERVED = "balanced-reserved";
	public static final String POOLNAME_BALANCED_EDEN = "balanced-eden";
	public static final String POOLNAME_BALANCED_SURVIVOR = "balanced-survivor";
	public static final String POOLNAME_BALANCED_OLD = "balanced-old";
	public static final String POOLNAME_JAVAHEAP = "JavaHeap";
}

