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

package net.openj9.test.PD2Primitive;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ibm.dataaccess.ByteArrayMarshaller;
import com.ibm.dataaccess.ByteArrayUnmarshaller;
import com.ibm.dataaccess.DecimalData;

//Tests testing the bugs JZOS found
public class RandomTests
{
    static Random randomGen;

    static
    {
        randomGen = new Random(System.currentTimeMillis());
    }

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void testSevenNumBytes()
    {
        byte[] buf = new byte[7];
        long in = 0x12345678987654L;
        
        ByteArrayMarshaller.writeLong(in, buf, 0, true, 7);
        long out = ByteArrayUnmarshaller.readLong(buf, 0, true, 7, true);
        
        assertEquals(in, out);
    }

    @Test
    public void testBigDecimalToPackedDDecimal()
    {
        byte[] buf = new byte[10];
        byte[] out = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 29 };
        
        DecimalData.convertBigDecimalToPackedDecimal(new BigDecimal(-1), buf, 0, 18, false);

        assertArrayEquals(buf, out);
    }

    @Test
    public void LongToExternalDecimal()
    {
        byte[] buf = new byte[18];
        byte[] out = { -7, -7, -7, -7, -7, -7, -7, -7, -7, -7, -7, -7, -7, -7, -7, -7, -7, -55 };
        
        DecimalData.convertLongToExternalDecimal(999999999999999999L, buf, 0, 18, false, 1);
        
        assertArrayEquals(buf, out);
    }

    @Test
    public void ExternalDecimalToLong()
    {
        byte[] buf = { -7, -7, -7, -7, -7, -7, -7, -7, -7, -7, -7, -7, -7, -7, -7, -7, -7, -64 };

        long in = 999999999999999990L;
        long out = DecimalData.convertExternalDecimalToLong(buf, 0, 18, false, 1);
        
        assertEquals(out, in);
    }
}