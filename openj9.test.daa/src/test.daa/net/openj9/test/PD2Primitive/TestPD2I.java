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

package net.openj9.test.PD2Primitive;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

import com.ibm.dataaccess.*;

public class TestPD2I
{
    static Random randomGen = new Random((int) (System.currentTimeMillis() % 160001));

    static final int ARRAY_SIZE = 256;
    static final int DOUBLESIZE = 8;
    static final int BYTESIZE = 8;
    static final long MANTISSA_MASK = 0x000FFFFFFFFFFFL;

    byte[] byteArray = new byte[ARRAY_SIZE];

    static final boolean isBigEndian = true;
    static final boolean isLittleEndian = false;

    byte[] MAX_INT = new byte[] { 0x02, 0x14, 0x74, (byte) 0x83, 0x64, 0x7C };
    byte[] MIN_INT = new byte[] { 0x02, 0x14, 0x74, (byte) 0x83, 0x64, (byte) 0x8D };
    
    byte[] MAX_INT_ALTERNATE_SIGN = new byte[] { 0x02, 0x14, 0x74, (byte) 0x83, 0x64, 0x70 };
    byte[] MIN_INT_ALTERNATE_SIGN = new byte[] { 0x02, 0x14, 0x74, (byte) 0x83, 0x64, (byte) 0x8B };

    byte[] MAX_LONG = new byte[] { (byte) 0x92, 0x23, 0x37, 0x20, 0x36, (byte) 0x85, 0x47, 0x75, (byte) 0x80, 0x7C };
    byte[] MIN_LONG = new byte[] { (byte) 0x92, 0x23, 0x37, 0x20, 0x36, (byte) 0x85, 0x47, 0x75, (byte) 0x80, (byte) 0x8D };

    byte[] MAX_LONG_ALTERNATE_SIGN = new byte[] { (byte) 0x92, 0x23, 0x37, 0x20, 0x36, (byte) 0x85, 0x47, 0x75, (byte) 0x80, 0x79 };
    byte[] MIN_LONG_ALTERNATE_SIGN = new byte[] { (byte) 0x92, 0x23, 0x37, 0x20, 0x36, (byte) 0x85, 0x47, 0x75, (byte) 0x80, (byte) 0x8B };
    
    byte[] MAX_INT_PLUS_1 = new byte[] { 0x02, 0x14, 0x74, (byte) 0x83, 0x64, (byte) 0x8C };
    byte[] MIN_INT_MINUS_1 = new byte[] { 0x02, 0x14, 0x74, (byte) 0x83, 0x64, (byte) 0x9D };

    byte[] MAX_LONG_PLUS_1 = new byte[] { (byte) 0x92, 0x23, 0x37, 0x20, 0x36, (byte) 0x85, 0x47, 0x75, (byte) 0x80, (byte) 0x8C };
    byte[] MIN_LONG_MINUS_1 = new byte[] { (byte) 0x92, 0x23, 0x37, 0x20, 0x36, (byte) 0x85, 0x47, 0x75, (byte) 0x80, (byte) 0x9D };

    @Test
    public void testMaxInt()
    {
        int result = 0;

        result = DecimalData.convertPackedDecimalToInteger(MAX_INT, 0, 10, true);

        assertEquals(Integer.MAX_VALUE, result);

        result = 0;
        result = DecimalData.convertPackedDecimalToInteger(MAX_INT, 0, 10, false);

        assertEquals(Integer.MAX_VALUE, result);
    }
    
    @Test
    public void testMaxIntAlternateSign()
    {
        int result = 0;

        result = DecimalData.convertPackedDecimalToInteger(MAX_INT_ALTERNATE_SIGN, 0, 10, true);

        assertEquals(Integer.MAX_VALUE, result);

        result = 0;
        result = DecimalData.convertPackedDecimalToInteger(MAX_INT_ALTERNATE_SIGN, 0, 10, false);

        assertEquals(Integer.MAX_VALUE, result);
    }

    @Test
    public void testMinInt()
    {

        int result = 0;
        result = DecimalData.convertPackedDecimalToInteger(MIN_INT, 0, 10, true);

        assertEquals(Integer.MIN_VALUE, result);

        result = 0;
        result = DecimalData.convertPackedDecimalToInteger(MIN_INT, 0, 10, false);

        assertEquals(Integer.MIN_VALUE, result);
    }
    
    @Test
    public void testMinIntAlternateSign()
    {

        int result = 0;
        result = DecimalData.convertPackedDecimalToInteger(MIN_INT_ALTERNATE_SIGN, 0, 10, true);

        assertEquals(Integer.MIN_VALUE, result);

        result = 0;
        result = DecimalData.convertPackedDecimalToInteger(MIN_INT_ALTERNATE_SIGN, 0, 10, false);

        assertEquals(Integer.MIN_VALUE, result);
    }

    @Test
    public void testMaxIntPlus1()
    {
        // this shall not throw an exception, thou it returns garbage.
        DecimalData.convertPackedDecimalToInteger(MAX_INT_PLUS_1, 0, 10, false);

        try
        {
            DecimalData.convertPackedDecimalToInteger(MAX_INT_PLUS_1, 0, 10, true);
        }

        catch (ArithmeticException ae)
        {
            return;
        }

        fail();
    }

    @Test
    public void testMinIntMinus1()
    {
        // this shall not throw an exception, thou it returns garbage.
        DecimalData.convertPackedDecimalToInteger(MIN_INT_MINUS_1, 0, 10, false);

        try
        {
            DecimalData.convertPackedDecimalToInteger(MIN_INT_MINUS_1, 0, 10, true);
        }

        catch (ArithmeticException ae)
        {
            return;
        }

        fail();
    }

    @Test
    public void testOddPrec()
    {
        byte[] testArray = new byte[] { 0x12, 0x3C };
        int rv = DecimalData.convertPackedDecimalToInteger(testArray, 0, 3, true);

        assertEquals(123, rv);

        rv = DecimalData.convertPackedDecimalToInteger(testArray, 0, 3, false);

        assertEquals(123, rv);

        // test negative
        testArray = new byte[] { 0x12, 0x3D };
        rv = DecimalData.convertPackedDecimalToInteger(testArray, 0, 3, true);

        assertEquals(-123, rv);

        rv = DecimalData.convertPackedDecimalToInteger(testArray, 0, 3, false);

        assertEquals(-123, rv);
    }

    @Test
    public void testEvenPrec()
    {
        // test positive
        byte[] testArray = new byte[] { 0x01, 0x23, 0x4C };
        int rv = DecimalData.convertPackedDecimalToInteger(testArray, 0, 4, true);

        assertEquals(1234, rv);

        rv = DecimalData.convertPackedDecimalToInteger(testArray, 0, 4, false);

        assertEquals(1234, rv);

        // test negative
        testArray = new byte[] { 0x01, 0x23, (byte) 0x4D };
        rv = DecimalData.convertPackedDecimalToInteger(testArray, 0, 4, true);

        assertEquals(-1234, rv);

        rv = DecimalData.convertPackedDecimalToInteger(testArray, 0, 4, false);

        assertEquals(-1234, rv);

    }

    @Test
    public void testOffset()
    {
        byte[] testArray = new byte[] { 0x00, 0x00, 0x01, 0x23, 0x4C };
        int rv = DecimalData.convertPackedDecimalToInteger(testArray, 2, 4, true);

        assertEquals(1234, rv);

        rv = DecimalData.convertPackedDecimalToInteger(testArray, 2, 4, false);

        assertEquals(1234, rv);

        // also test leading zeros
        rv = DecimalData.convertPackedDecimalToInteger(testArray, 1, 6, true);

        assertEquals(1234, rv);

        rv = DecimalData.convertPackedDecimalToInteger(testArray, 1, 6, false);

        assertEquals(1234, rv);

        testArray = new byte[] { 0x00, 0x00, 0x12, 0x3C, 0x00 };
        rv = DecimalData.convertPackedDecimalToInteger(testArray, 2, 3, true);

        assertEquals(123, rv);

        rv = DecimalData.convertPackedDecimalToInteger(testArray, 2, 3, false);

        assertEquals(123, rv);
    }

    @Test
    public void testMaxLong()
    {
        long result = 0;

        result = DecimalData.convertPackedDecimalToLong(MAX_LONG, 0, 19, true);

        assertEquals(Long.MAX_VALUE, result);

        result = 0;
        result = DecimalData.convertPackedDecimalToLong(MAX_LONG, 0, 19, false);

        assertEquals(Long.MAX_VALUE, result);
    }
    
    @Test
    public void testMaxLongAlternateSign()
    {
        long result = 0;

        result = DecimalData.convertPackedDecimalToLong(MAX_LONG_ALTERNATE_SIGN, 0, 19, true);

        assertEquals(Long.MAX_VALUE, result);

        result = 0;
        result = DecimalData.convertPackedDecimalToLong(MAX_LONG_ALTERNATE_SIGN, 0, 19, false);

        assertEquals(Long.MAX_VALUE, result);
    }

    @Test
    public void testMinLong()
    {
        long result = 0;

        result = DecimalData.convertPackedDecimalToLong(MIN_LONG, 0, 19, true);

        assertEquals(Long.MIN_VALUE, result);

        result = 0;
        result = DecimalData.convertPackedDecimalToLong(MIN_LONG, 0, 19, false);

        assertEquals(Long.MIN_VALUE, result);
    }
    
    @Test
    public void testMinLongAlternateSign()
    {
        long result = 0;

        result = DecimalData.convertPackedDecimalToLong(MIN_LONG_ALTERNATE_SIGN, 0, 19, true);

        assertEquals(Long.MIN_VALUE, result);

        result = 0;
        result = DecimalData.convertPackedDecimalToLong(MIN_LONG_ALTERNATE_SIGN, 0, 19, false);

        assertEquals(Long.MIN_VALUE, result);
    }

    @Test
    public void testMaxLongPlus1()
    {
        // this shall not throw an exception, thou it returns garbage.
        DecimalData.convertPackedDecimalToLong(MAX_LONG_PLUS_1, 0, 19, false);
        try
        {
            DecimalData.convertPackedDecimalToLong(MAX_LONG_PLUS_1, 0, 19, true);
        }

        catch (ArithmeticException ae)
        {
            return;
        }

        fail();
    }

    @Test
    public void testMinLongMinus1()
    {
        // this shall not throw an exception, thou it returns garbage.
        DecimalData.convertPackedDecimalToLong(MIN_LONG_MINUS_1, 0, 19, false);

        try
        {
            DecimalData.convertPackedDecimalToLong(MIN_LONG_MINUS_1, 0, 19, true);
        }

        catch (ArithmeticException ae)
        {
            return;
        }

        fail();

    }

    @Test
    public void testOddPrecLong()
    {
        byte[] testArray = new byte[] { 0x12, 0x3C };
        long rv = DecimalData.convertPackedDecimalToLong(testArray, 0, 3, true);

        assertEquals(123, rv);

        rv = DecimalData.convertPackedDecimalToInteger(testArray, 0, 3, false);

        assertEquals(123, rv);

        // test negative
        testArray = new byte[] { 0x12, 0x3D };
        rv = DecimalData.convertPackedDecimalToInteger(testArray, 0, 3, true);

        assertEquals(-123, rv);

        rv = DecimalData.convertPackedDecimalToInteger(testArray, 0, 3, false);

        assertEquals(-123, rv);
    }

    @Test
    public void testEvenPrecLong()
    {
        // test positive
        byte[] testArray = new byte[] { 0x01, 0x23, 0x4C };
        long rv = DecimalData.convertPackedDecimalToLong(testArray, 0, 4, true);

        assertEquals(1234, rv);

        rv = DecimalData.convertPackedDecimalToLong(testArray, 0, 4, false);

        assertEquals(1234, rv);

        // test negative
        testArray = new byte[] { 0x01, 0x23, 0x4D };
        rv = DecimalData.convertPackedDecimalToLong(testArray, 0, 4, true);

        assertEquals(-1234, rv);

        rv = DecimalData.convertPackedDecimalToLong(testArray, 0, 4, false);

        assertEquals(-1234, rv);
    }

    @Test
    public void testOffsetLong()
    {
        byte[] testArray = new byte[] { 0x00, 0x00, 0x01, 0x23, 0x4C };
        long rv = DecimalData.convertPackedDecimalToLong(testArray, 2, 4, true);

        assertEquals(1234, rv);

        rv = DecimalData.convertPackedDecimalToLong(testArray, 2, 4, false);

        assertEquals(1234, rv);

        // also test leading zeros
        rv = DecimalData.convertPackedDecimalToLong(testArray, 1, 6, true);

        assertEquals(1234, rv);

        rv = DecimalData.convertPackedDecimalToLong(testArray, 1, 6, false);

        assertEquals(1234, rv);

        testArray = new byte[] { 0x00, 0x00, 0x12, 0x3C, 0x00 };
        rv = DecimalData.convertPackedDecimalToLong(testArray, 1, 5, true);

        assertEquals(123, rv);

        rv = DecimalData.convertPackedDecimalToLong(testArray, 1, 5, false);

        assertEquals(123, rv);
    }

    @Test
    public void intPerformanceTest()
    {
        byte[] testArray = new byte[] { 0x12, 0x34, 0x56, 0x78, (byte) 0x9C };
        long rv = DecimalData.convertPackedDecimalToInteger(testArray, 0, 9, true);

        assertEquals(123456789, rv);
    }

    @Test
    public void longPerformanceTest()
    {
        byte[] testArray = new byte[] { 0x12, 0x34, 0x56, 0x78, (byte) 0x90, 0x12, 0x34, 0x56, 0x78, (byte) 0x9C };
        long rv = DecimalData.convertPackedDecimalToLong(testArray, 0, 19, true);

        assertEquals(1234567890123456789L, rv);
    }
}
