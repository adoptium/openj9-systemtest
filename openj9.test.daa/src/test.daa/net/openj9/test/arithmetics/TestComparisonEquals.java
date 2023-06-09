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
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0 OR GPL-2.0-only WITH Classpath-exception-2.0 OR GPL-2.0-only WITH OpenJDK-assembly-exception-1.0
*******************************************************************************/

package net.openj9.test.arithmetics;

import org.junit.Test;

public class TestComparisonEquals extends TestArithmeticComparisonBase
{

	@Test
	public void testZeros()
	{
		// similiar to TestAddPD's testZeros

	}

	@Test
	public void testDifferentOffsets()
	{
		for (int i = 0; i < 4; ++i)
		{
			testOffset(i);
		}
	}

	public void testOffset(int offset)
	{
		// construct test cases for precision 1

		// precision 2

		// precision 3

		// precision 4
	}

	@Test
	public void randomTest()
	{
		for (int i = 0; i < 50; ++i)
		{

		}
	}

	@Test
	public void testDifferentPrecisions()
	{

	}

	// please implement the following according to the corresponding
	// test methods in TestAddPD.java
	@Test
	public void testLongPrecisions()
	{

	}
}
