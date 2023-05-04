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

import java.math.BigInteger;

import java.util.Arrays;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ibm.dataaccess.*;

public class TestI2PD
{
    static Random randomGen;

    static int ARRAY_SIZE = 256;
    static final int DOUBLESIZE = 8;
    static final int BYTESIZE = 8;
    static final long MANTISSA_MASK = 0x000FFFFFFFFFFFL;

    byte[] byteArray = new byte[ARRAY_SIZE];

    static final boolean isBigEndian = true;
    static final boolean isLittleEndian = false;

    byte[] packedDecimal = new byte[15];
    byte[] referenceArray = new byte[15];

    static
    {
        randomGen = new Random((int) (System.currentTimeMillis() % 160001));
    };

    byte[] MAX_INT = new byte[] { 0x02, 0x14, 0x74, (byte) 0x83, 0x64, 0x7C };
    byte[] MIN_INT = new byte[] { 0x02, 0x14, 0x74, (byte) 0x83, 0x64, (byte) 0x8D };

    byte[] MAX_LONG = new byte[] { (byte) 0x92, 0x23, 0x37, 0x20, 0x36, (byte) 0x85, 0x47, 0x75, (byte) 0x80, 0x7c };
    byte[] MIN_LONG = new byte[] { (byte) 0x92, 0x23, 0x37, 0x20, 0x36, (byte) 0x85, 0x47, 0x75, (byte) 0x80, (byte) 0x8D };

    byte[] MAX_INT_MINUS_1 = new byte[] { 0x02, 0x14, 0x74, (byte) 0x83, 0x64, (byte) 0x6C };
    byte[] MIN_INT_PLUS_1 = new byte[] { 0x02, 0x14, 0x74, (byte) 0x83, 0x64, (byte) 0x7D };

    byte[] MAX_LONG_MINUS_1 = new byte[] { (byte) 0x92, 0x23, 0x37, 0x20, 0x36, (byte) 0x85, 0x47, 0x75, (byte) 0x80, 0x6c };
    byte[] MIN_LONG_PLUS_1 = new byte[] { (byte) 0x92, 0x23, 0x37, 0x20, 0x36, (byte) 0x85, 0x47, 0x75, (byte) 0x80, (byte) 0x7D };

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void boundaryTests()
    {
        test(0, false);
        test(1, false);
        test(-1, false);

        testIntegerBoundaryHelper(Integer.MAX_VALUE, MAX_INT, 10);
        testIntegerBoundaryHelper(Integer.MIN_VALUE, MIN_INT, 10);

        testIntegerBoundaryHelper(Integer.MAX_VALUE - 1, MAX_INT_MINUS_1, 10);
        testIntegerBoundaryHelper(Integer.MIN_VALUE + 1, MIN_INT_PLUS_1, 10);
    }

    @Test
    public void randomTest()
    {
        for (int i = 0; i < 100; ++i)
        {
            test(randomGen.nextInt(2147483647), true);
        }
    }

    @Test
    public void specialTest()
    {
        test(2118395439, true);
    }

    private void test(int value, boolean isRandom)
    {
        // get precision:
        int temp = Math.abs(value);
        int precision = String.valueOf(temp).length();

        if (precision == 0)
            precision = 1;

        if (isRandom && randomGen.nextBoolean())
            value = -value;

        // create reference array value
        BigInteger refVal = new BigInteger((new Integer(value).toString()));

        DecimalData.convertBigIntegerToPackedDecimal(refVal, referenceArray, 0, precision, true);
        DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, 0, precision, true);

        try
        {
            assertArrayEquals(referenceArray, packedDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(String.format("value: %d, precision: %d\n", value, precision), referenceArray, packedDecimal);
        }
    }

    public void testLongBoundaryHelper(long value, byte[] reference, int precision)
    {
        byte[] packedDecimal = new byte[reference.length];
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, 0, precision, true);

        try
        {
            assertArrayEquals(reference, packedDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(String.format("value: %d, precision: %d\n", value, precision), reference, packedDecimal);
        }
    }

    public void testIntegerBoundaryHelper(int value, byte[] reference, int precision)
    {
        byte[] packedDecimal = new byte[reference.length];
        DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, 0, precision, true);

        try
        {
            assertArrayEquals(reference, packedDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(String.format("value: %d, precision: %d\n", value, precision), reference, packedDecimal);
        }
    }

    @Test
    public void randomTestLong()
    {
        long range = 9223372036854775807L;
        for (int i = 0; i < 100; ++i)
        {
            testLong((long) (randomGen.nextDouble() * range), true);
        }
    }

    @Test
    public void boundaryTestLong()
    {
        testLong(0, false);
        testLong(1, false);
        testLong(-1, false);

        testLongBoundaryHelper(Long.MAX_VALUE, MAX_LONG, 19);
        testLongBoundaryHelper(Long.MIN_VALUE, MIN_LONG, 19);

        testLongBoundaryHelper(Long.MAX_VALUE - 1, MAX_LONG_MINUS_1, 19);
        testLongBoundaryHelper(Long.MIN_VALUE + 1, MIN_LONG_PLUS_1, 19);
    }

    @Test
    public void testIntOverflow()
    {
        for (int precision = 1; precision < 10; ++precision)
        {
            for (int offset = 0; offset < 10; ++offset)
            {
                intOverflowHelper(Integer.MAX_VALUE, precision, offset);
                intOverflowHelper(Integer.MIN_VALUE, precision - 1, offset);
                intOverflowHelper(Integer.MAX_VALUE, precision, offset);
                intOverflowHelper(Integer.MIN_VALUE, precision - 1, offset);
            }
        }

        // TODO: add more test cases
    }

    @Test
    public void testLongOverflow()
    {
        for (int precision = 1; precision < 18; ++precision)
        {
            for (int offset = 0; offset < 10; ++offset)
            {
                longOverflowHelper(Long.MAX_VALUE, precision, offset);
                longOverflowHelper(Long.MIN_VALUE, precision - 1, offset);
                longOverflowHelper(Long.MAX_VALUE, precision, offset);
                longOverflowHelper(Long.MIN_VALUE, precision - 1, offset);
            }
        }
    }

    // @Test
    public void testLongOverflow1()
    {
        longOverflowHelper(Long.MAX_VALUE, 18, 0);
    }

    private void intOverflowHelper(int value, int precision, int offset)
    {
        int valueSize = precision / 2 + 1;
        byte[] array = new byte[valueSize + offset];

        try
        {
            DecimalData.convertIntegerToPackedDecimal(value, array, offset, precision, true);
        }

        catch (Exception e)
        {
            return;
        }

        fail();
    }

    private void longOverflowHelper(long value, int precision, int offset)
    {
        int valueSize = precision / 2 + 1;
        byte[] array = new byte[valueSize + offset];

        try
        {
            DecimalData.convertLongToPackedDecimal(value, array, offset, precision, false);
        }

        catch (ArithmeticException ae)
        {
            fail();
        }

        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            DecimalData.convertLongToPackedDecimal(value, array, offset, precision, true);
        }

        catch (Exception e)
        {
            return;
        }

        fail();
    }

    private void testLong(long value, boolean isRandom)
    {
        // get precision:
        long temp = Math.abs(value);
        int precision = String.valueOf(temp).length();

        if (precision == 0)
            precision = 1;

        if (isRandom && randomGen.nextBoolean())
            value = -value;

        // create reference array value
        BigInteger refVal = new BigInteger((new Long(value).toString()));

        DecimalData.convertBigIntegerToPackedDecimal(refVal, referenceArray, 0, precision, true);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, 0, precision, true);

        assertArrayEquals(referenceArray, packedDecimal);
    }

    @Test
    public void intPerformanceTest()
    {
        int value = 2147483647;

        Arrays.fill(referenceArray, (byte) 0);
        Arrays.fill(packedDecimal, (byte) 0);

        BigInteger refVal = new BigInteger((new Integer(value).toString()));

        DecimalData.convertBigIntegerToPackedDecimal(refVal, referenceArray, 0, 10, true);
        DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, 0, 10, true);

        assertArrayEquals(referenceArray, packedDecimal);
    }

    @Test
    public void longPerformanceTest()
    {
        long value = 7223372036854775807L;

        Arrays.fill(referenceArray, (byte) 0);
        Arrays.fill(packedDecimal, (byte) 0);

        BigInteger refVal = new BigInteger((new Long(value).toString()));

        DecimalData.convertBigIntegerToPackedDecimal(refVal, referenceArray, 0, 19, true);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, 0, 19, true);

        assertArrayEquals(referenceArray, packedDecimal);
    }

    @Test
    public void testInvalidPrecision()
    {
        Arrays.fill(packedDecimal, (byte) 0);

        boolean catched = false;

        try
        {
            DecimalData.convertIntegerToPackedDecimal(randomGen.nextInt(20000), packedDecimal, 0, 0, true);
        }

        catch (ArithmeticException e)
        {
            catched = true;
        }

        if (!catched)
            fail();
    }
}
