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

package net.openj9.test.PD2Primitive;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ibm.dataaccess.*;

public class TestPD2Primitives2PD
{
    static Random randomGen;

    static int ARRAY_SIZE = 256;
    static final int DOUBLESIZE = 8;
    static final int BYTESIZE = 8;
    static final long MANTISSA_MASK = 0x000FFFFFFFFFFFL;

    byte[] byteArray = new byte[ARRAY_SIZE];

    static final boolean isBigEndian = true;
    static final boolean isLittleEndian = false;

    static int PDSize = 15;
    
    byte[] packedDecimal = new byte[PDSize];
    byte[] referenceArray = new byte[PDSize];

    static
    {
        randomGen = new Random((int) (System.currentTimeMillis() % 160001));
    };

    static int getRandomInt()
    {
        return randomGen.nextInt(Integer.MAX_VALUE);
    }

    static long getRandomLong()
    {
        return (long) (randomGen.nextDouble() * 9223372036854775807L);
    }

    byte[] MAX_INT_PLUS_1 = new byte[] { 0x02, 0x14, 0x74, (byte) 0x83, 0x64, (byte) 0x8C };
    byte[] MIN_INT_MINUS_1 = new byte[] { 0x02, 0x14, 0x74, (byte) 0x83, 0x64, (byte) 0x9D };
    
    byte[] MAX_LONG_PLUS_1 = new byte[] { (byte) 0x92, 0x23, 0x37, 0x20, 0x36, (byte) 0x85, 0x47, 0x75, (byte) 0x80, (byte) 0x8C };
    byte[] MIN_LONG_MINUS_1 = new byte[] { (byte) 0x92, 0x23, 0x37, 0x20, 0x36, (byte) 0x85, 0x47, 0x75, (byte) 0x80, (byte) 0x9D };

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
    }

    private int getRandomIntegerWithNDigits(int n)
    {
        n--;
        int randomInt = 0;
        
        for (int k = 0; k < n; k++)
            randomInt = randomInt | (((randomGen.nextInt() >> k * 4) & 0x0f) << (n - k - 1) * 4);
        
        return randomInt < 0 ? randomInt * -1 : randomInt;
    }

    private long getRandomLongWithNDigits(int n)
    {
        n -= 2;
        long randomLong = 0;
        
        for (int k = 0; k < n; k++)
            randomLong = randomLong | (((randomGen.nextLong() >> k * 4) & 0x0f) << (n - k - 1) * 4);
        
        return randomLong < 0 ? randomLong * -1 : randomLong;
    }

    @Test
    public void intBoundaryTests()
    {
        testInt(0, false);
        testInt(1, false);
        testInt(-1, false);
        testInt(Integer.MAX_VALUE, false);
        testInt(Integer.MIN_VALUE, false);
        testInt(Integer.MIN_VALUE + 1, false);
        testInt(Integer.MIN_VALUE - 1, false);

    }

    @Test(expected = ArithmeticException.class)
    public void testMaxIntPlus1()
    {
        // this shall not throw an exception, though it returns garbage.
        DecimalData.convertPackedDecimalToInteger(MAX_INT_PLUS_1, 0, 10, false);
        DecimalData.convertPackedDecimalToInteger(MAX_INT_PLUS_1, 0, 10, true);
    }

    @Test(expected = ArithmeticException.class)
    public void testMinIntMinus1()
    {
        // this shall not throw an exception, though it returns garbage.
        DecimalData.convertPackedDecimalToInteger(MIN_INT_MINUS_1, 0, 10, false);
        DecimalData.convertPackedDecimalToInteger(MIN_INT_MINUS_1, 0, 10, true);

    }

    @Test
    public void testI2PDMinInt()
    {
        int precision = randomGen.nextInt(10);
        if (precision == 0)
            precision = 1;
        int offset = randomGen.nextInt(precision);
        intOverflowHelper(Integer.MIN_VALUE, precision, offset);
        precision--;
        intOverflowHelper(Integer.MIN_VALUE, precision, offset);
    }

    @Test
    public void testI2PDMaxInt()
    {
        int precision = randomGen.nextInt(10);
        if (precision == 0)
            precision = 1;
        int offset = randomGen.nextInt(precision);
        intOverflowHelper(Integer.MAX_VALUE, precision, offset);
        precision--;
        intOverflowHelper(Integer.MAX_VALUE, precision, offset);
    }

    private void intOverflowHelper(int value, int precision, int offset)
    {
        byte[] array = new byte[precision / 2 + 1 + offset];

        try
        {
            DecimalData.convertIntegerToPackedDecimal(value, array, offset, precision, false);
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
            DecimalData.convertIntegerToPackedDecimal(value, array, offset, precision, true);
        }
        
        catch (Exception e)
        {
            return;
        }

        fail();
    }

    @Test
    public void testOddPrec()
    {
        int value = 0, length = 1;
        
        while (length % 2 == 0)
        {
            value = getRandomInt();
            length = String.valueOf(value).length();
        }

        testInt(value, false);
        value = value * (-1);
        testInt(value, false);
    }

    @Test
    public void testEvenPrec()
    {
        int value = 0, length = 0;
        
        while (length % 2 != 0)
        {
            value = getRandomInt();
            length = String.valueOf(value).length();
        }

        testInt(value, false);
        value = value * (-1);
        testInt(value, false);
    }

    @Test
    public void testOffset()
    {
        int offset = randomGen.nextInt(PDSize - 6);
        int value = getRandomInt();
        int length = String.valueOf(value).length();
        
        DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, offset, length, false);
        int resultInt = DecimalData.convertPackedDecimalToInteger(packedDecimal, offset, length, false);

        try 
        {
            assertEquals(value, resultInt);
        }
        
        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, offset: %d\n", value, offset), value, resultInt);
        }
    }

    @Test
    public void randomTest()
    {
        for (int i = 0; i < 100; ++i)
            testInt(getRandomInt(), true);
    }

    @Test
    public void specialTest()
    {
        testInt(2118395439, true);
    }

    @Test
    public void intPerformanceTest()
    {
        // initialization
        byte[] testArray = new byte[] { 0x12, 0x34, 0x56, 0x78, (byte) 0x9C, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
        Arrays.fill(packedDecimal, (byte) 0);
        // test execution
        int rv = DecimalData.convertPackedDecimalToInteger(testArray, 0, 9, true);
        DecimalData.convertIntegerToPackedDecimal(rv, packedDecimal, 0, 9, true);
        // verification
        assertArrayEquals(testArray, packedDecimal);
    }

    @Test
    public void intLeadingZeros()
    {
        int value = getRandomInt();
        int length = String.valueOf(value).length();
        int precision = length + randomGen.nextInt(PDSize - (length + 2) / 2 + 1);
        DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, 0, precision, true);
        int resultInt = DecimalData.convertPackedDecimalToInteger(packedDecimal, 0, precision, true);
        assertEquals(value, resultInt);

    }

    private void testInt(int value, boolean isRandom)
    {
        // get precision:
        int temp = Math.abs(value);
        int precision = String.valueOf(temp).length();

        if (precision == 0)
            precision = 1;

        if (isRandom && randomGen.nextBoolean())
            value = -value;

        DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, 0, precision, true);
        int resultInt = DecimalData.convertPackedDecimalToInteger(packedDecimal, 0, precision, true);
        assertEquals("testInt received different int after I2PD -> PD2I : " + value, value, resultInt);
    }

    @Test
    public void longBoundaryTests()
    {
        testInt(0, false);
        testInt(1, false);
        testInt(-1, false);
        testLong(Long.MAX_VALUE, false);
        testLong(Long.MIN_VALUE, false);
        testLong(Long.MAX_VALUE - 1, false);
        testLong(Long.MIN_VALUE + 1, false);

    }

    @Test(expected = ArithmeticException.class)
    public void testMaxLongPlus1()
    {
        // this shall not throw an exception, though it returns garbage.
        DecimalData.convertPackedDecimalToInteger(MAX_LONG_PLUS_1, 0, 19, false);
        DecimalData.convertPackedDecimalToInteger(MAX_LONG_PLUS_1, 0, 19, true);
    }

    @Test(expected = ArithmeticException.class)
    public void testMinLongMinus1()
    {
        // this shall not throw an exception, though it returns garbage.
        DecimalData.convertPackedDecimalToInteger(MIN_LONG_MINUS_1, 0, 19, false);
        DecimalData.convertPackedDecimalToInteger(MIN_LONG_MINUS_1, 0, 19, true);

    }

    @Test(expected = ArithmeticException.class)
    public void testL2PDMinLong()
    {
        int precision = randomGen.nextInt(10);
        if (precision == 0)
            precision = 1;
        int offset = randomGen.nextInt(precision);
        DecimalData.convertIntegerToPackedDecimal(Integer.MAX_VALUE, new byte[precision / 2 + 1 + offset], offset, precision, true);
        precision--;
        DecimalData.convertIntegerToPackedDecimal(Integer.MAX_VALUE, new byte[precision / 2 + 1 + offset], offset, precision, true);
    }

    @Test(expected = ArithmeticException.class)
    public void testL2PDMaxLong()
    {
        int precision = randomGen.nextInt(10);
        if (precision == 0)
            precision = 1;
        int offset = randomGen.nextInt(precision);
        DecimalData.convertIntegerToPackedDecimal(Integer.MAX_VALUE, new byte[precision / 2 + 1 + offset], offset, precision, true);
        precision--;
        DecimalData.convertIntegerToPackedDecimal(Integer.MAX_VALUE, new byte[precision / 2 + 1 + offset], offset, precision, true);
    }

    @Test
    public void testL2PDMinLongRandom()
    {
        int precision = randomGen.nextInt(10);
        if (precision == 0)
            precision = 1;
        int offset = randomGen.nextInt(precision);
        longOverflowHelper(Long.MIN_VALUE, precision, offset);
        precision--;
        longOverflowHelper(Long.MIN_VALUE, precision, offset);
    }

    @Test
    public void testL2PDMaxLongRandom()
    {
        int precision = randomGen.nextInt(10);
        if (precision == 0)
            precision = 1;
        int offset = randomGen.nextInt(precision);
        longOverflowHelper(Long.MAX_VALUE, precision, offset);
        precision--;
        longOverflowHelper(Long.MAX_VALUE, precision, offset);
    }

    private void longOverflowHelper(long value, int precision, int offset)
    {
        byte[] array = new byte[precision / 2 + 1 + offset];

        // this should not throw an ArithmeticException
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
            // do nothing as we might have it.
        }

        // this should throw an exception
        try
        {
            DecimalData.convertLongToPackedDecimal(value, array, offset, precision, true);
        }
        catch (Exception e)
        {
            return;
        }
        // should never get to this point.
        fail();
    }

    @Test
    public void testOddPrecLong()
    {
        long value = 0, length = 1;
        while (length % 2 == 0)
        {
            value = getRandomLong();
            length = String.valueOf(value).length();
        }

        testLong(value, false);
        value = value * (-1);
        testLong(value, false);
    }

    @Test
    public void testEvenPrecLong()
    {
        long value = 0, length = 0;
        while (length % 2 != 0)
        {
            value = getRandomLong();
            length = String.valueOf(value).length();
        }

        testLong(value, false);
        value = value * (-1);
        testLong(value, false);
    }

    @Test
    public void testOffsetLong()
    {
        int offset = randomGen.nextInt(PDSize - 10); // 10 is max bytes for long
        long value = getRandomLong();
        int length = String.valueOf(value).length();
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset, length, false);
        long resultInt = DecimalData.convertPackedDecimalToLong(packedDecimal, offset, length, false);
        assertEquals(value, resultInt);
    }

    @Test
    public void randomTestLong()
    {
        for (int i = 0; i < 100; ++i)
            testLong(getRandomLong(), true);
    }

    @Test
    public void longPerformanceTest()
    {
        // initialization
        byte[] testArray = new byte[] { 0x12, 0x34, 0x56, 0x78, (byte) 0x90, 0x12, 0x34, 0x56, 0x78, (byte) 0x9C, 0x00, 0x00, 0x00, 0x00, 0x00 };
        Arrays.fill(packedDecimal, (byte) 0);
        // test execution
        long rv = DecimalData.convertPackedDecimalToLong(testArray, 0, 19, true);
        DecimalData.convertLongToPackedDecimal(rv, packedDecimal, 0, 19, true);

        // verification
        assertArrayEquals(testArray, packedDecimal);

    }

    @Test
    public void longLeadingZeros()
    {
        long value = getRandomLong();
        int length = String.valueOf(value).length();
        int precision = length + randomGen.nextInt(PDSize - (length + 2) / 2 + 1);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, 0, precision, true);
        long resultInt = DecimalData.convertPackedDecimalToLong(packedDecimal, 0, precision, true);
        assertEquals(value, resultInt);

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

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, 0, precision, true);
        long resultInt = DecimalData.convertPackedDecimalToLong(packedDecimal, 0, precision, true);
        assertEquals("testLong received different long after L2PD -> PD2L : " + value, value, resultInt);
    }

    @Test
    public void testPD2LongWithSetPrecisionSetOffset()
    {

        byte[] pd = new byte[128];

        int offset0 = 0;
        int offset1 = 1;
        int offset5 = 5;
        int offset10 = 10;
        int offset15 = 15;

        int prec1 = 1;
        int prec5 = 5;
        int prec10 = 10;
        int prec20 = 20;
        int prec30 = 30;
        int prec40 = 40;
        int prec50 = 50;
        int prec60 = 60;

        long value;

        // offset 0, precision 1
        value = getRandomLongWithNDigits(prec1);
        DecimalData.convertLongToPackedDecimal(value, pd, offset0, prec1, false);
        long resultLong = DecimalData.convertPackedDecimalToLong(pd, offset0, prec1, false);
        assertEquals(value, resultLong);

        // offset 0, precision 5
        value = getRandomLongWithNDigits(prec5);
        DecimalData.convertLongToPackedDecimal(value, pd, offset0, prec5, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset0, prec5, false);
        assertEquals(value, resultLong);

        // offset 0, precision 10
        value = getRandomLongWithNDigits(prec10);
        DecimalData.convertLongToPackedDecimal(value, pd, offset0, prec10, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset0, prec10, false);
        assertEquals(value, resultLong);

        // offset 0, precision 20
        value = getRandomLongWithNDigits(prec20);
        DecimalData.convertLongToPackedDecimal(value, pd, offset0, prec20, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset0, prec20, false);
        assertEquals(value, resultLong);

        // offset 0, precision 30
        value = getRandomLongWithNDigits(prec30);
        DecimalData.convertLongToPackedDecimal(value, pd, offset0, prec30, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset0, prec30, false);
        assertEquals(value, resultLong);

        // offset 0, precision 40
        value = getRandomLongWithNDigits(prec40);
        DecimalData.convertLongToPackedDecimal(value, pd, offset0, prec40, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset0, prec40, false);
        assertEquals(value, resultLong);

        // offset 0, precision 50
        value = getRandomLongWithNDigits(prec50);
        DecimalData.convertLongToPackedDecimal(value, pd, offset0, prec50, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset0, prec50, false);
        assertEquals(value, resultLong);

        // offset 0, precision 60
        value = getRandomLongWithNDigits(prec60);
        DecimalData.convertLongToPackedDecimal(value, pd, offset0, prec60, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset0, prec60, false);
        assertEquals(value, resultLong);

        // offset 1, precision 1
        value = getRandomLongWithNDigits(prec1);
        DecimalData.convertLongToPackedDecimal(value, pd, offset1, prec1, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset1, prec1, false);
        assertEquals(value, resultLong);

        // offset 1, precision 5
        value = getRandomLongWithNDigits(prec5);
        DecimalData.convertLongToPackedDecimal(value, pd, offset1, prec5, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset1, prec5, false);
        assertEquals(value, resultLong);

        // offset 1, precision 10
        value = getRandomLongWithNDigits(prec10);
        DecimalData.convertLongToPackedDecimal(value, pd, offset1, prec10, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset1, prec10, false);
        assertEquals(value, resultLong);

        // offset 1, precision 20
        value = getRandomLongWithNDigits(prec20);
        DecimalData.convertLongToPackedDecimal(value, pd, offset1, prec20, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset1, prec20, false);
        assertEquals(value, resultLong);

        // offset 1, precision 30
        value = getRandomLongWithNDigits(prec30);
        DecimalData.convertLongToPackedDecimal(value, pd, offset1, prec30, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset1, prec30, false);
        assertEquals(value, resultLong);

        // offset 1, precision 40
        value = getRandomLongWithNDigits(prec40);
        DecimalData.convertLongToPackedDecimal(value, pd, offset1, prec40, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset1, prec40, false);
        assertEquals(value, resultLong);

        // offset 1, precision 50
        value = getRandomLongWithNDigits(prec50);
        DecimalData.convertLongToPackedDecimal(value, pd, offset1, prec50, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset1, prec50, false);
        assertEquals(value, resultLong);

        // offset 1, precision 60
        value = getRandomLongWithNDigits(prec60);
        DecimalData.convertLongToPackedDecimal(value, pd, offset1, prec60, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset1, prec60, false);
        assertEquals(value, resultLong);

        // offset 5, precision 1
        value = getRandomLongWithNDigits(prec1);
        DecimalData.convertLongToPackedDecimal(value, pd, offset5, prec1, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset5, prec1, false);
        assertEquals(value, resultLong);

        // offset 5, precision 5
        value = getRandomLongWithNDigits(prec5);
        DecimalData.convertLongToPackedDecimal(value, pd, offset5, prec5, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset5, prec5, false);
        assertEquals(value, resultLong);

        // offset 5, precision 10
        value = getRandomLongWithNDigits(prec10);
        DecimalData.convertLongToPackedDecimal(value, pd, offset5, prec10, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset5, prec10, false);
        assertEquals(value, resultLong);

        // offset 5, precision 20
        value = getRandomLongWithNDigits(prec20);
        DecimalData.convertLongToPackedDecimal(value, pd, offset5, prec20, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset5, prec20, false);
        assertEquals(value, resultLong);

        // offset 5, precision 30
        value = getRandomLongWithNDigits(prec30);
        DecimalData.convertLongToPackedDecimal(value, pd, offset5, prec30, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset5, prec30, false);
        assertEquals(value, resultLong);

        // offset 5, precision 40
        value = getRandomLongWithNDigits(prec40);
        DecimalData.convertLongToPackedDecimal(value, pd, offset5, prec40, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset5, prec40, false);
        assertEquals(value, resultLong);

        // offset 5, precision 50
        value = getRandomLongWithNDigits(prec50);
        DecimalData.convertLongToPackedDecimal(value, pd, offset5, prec50, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset5, prec50, false);
        assertEquals(value, resultLong);

        // offset 5, precision 60
        value = getRandomLongWithNDigits(prec60);
        DecimalData.convertLongToPackedDecimal(value, pd, offset5, prec60, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset5, prec60, false);
        assertEquals(value, resultLong);

        // offset 10, precision 1
        value = getRandomLongWithNDigits(prec1);
        DecimalData.convertLongToPackedDecimal(value, pd, offset10, prec1, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset10, prec1, false);
        assertEquals(value, resultLong);

        // offset 10, precision 5
        value = getRandomLongWithNDigits(prec5);
        DecimalData.convertLongToPackedDecimal(value, pd, offset10, prec5, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset10, prec5, false);
        assertEquals(value, resultLong);

        // offset 10, precision 10
        value = getRandomLongWithNDigits(prec10);
        DecimalData.convertLongToPackedDecimal(value, pd, offset10, prec10, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset10, prec10, false);
        assertEquals(value, resultLong);

        // offset 10, precision 20
        value = getRandomLongWithNDigits(prec20);
        DecimalData.convertLongToPackedDecimal(value, pd, offset10, prec20, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset10, prec20, false);
        assertEquals(value, resultLong);

        // offset 10, precision 30
        value = getRandomLongWithNDigits(prec30);
        DecimalData.convertLongToPackedDecimal(value, pd, offset10, prec30, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset10, prec30, false);
        assertEquals(value, resultLong);

        // offset 10, precision 40
        value = getRandomLongWithNDigits(prec40);
        DecimalData.convertLongToPackedDecimal(value, pd, offset10, prec40, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset10, prec40, false);
        assertEquals(value, resultLong);

        // offset 10, precision 50
        value = getRandomLongWithNDigits(prec50);
        DecimalData.convertLongToPackedDecimal(value, pd, offset10, prec50, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset10, prec50, false);
        assertEquals(value, resultLong);

        // offset 10, precision 60
        value = getRandomLongWithNDigits(prec60);
        DecimalData.convertLongToPackedDecimal(value, pd, offset10, prec60, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset10, prec60, false);
        assertEquals(value, resultLong);

        // offset 15, precision 1
        value = getRandomLongWithNDigits(prec1);
        DecimalData.convertLongToPackedDecimal(value, pd, offset15, prec1, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset15, prec1, false);
        assertEquals(value, resultLong);

        // offset 15, precision 5
        value = getRandomLongWithNDigits(prec5);
        DecimalData.convertLongToPackedDecimal(value, pd, offset15, prec5, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset15, prec5, false);
        assertEquals(value, resultLong);

        // offset 15, precision 10
        value = getRandomLongWithNDigits(prec10);
        DecimalData.convertLongToPackedDecimal(value, pd, offset15, prec10, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset15, prec10, false);
        assertEquals(value, resultLong);

        // offset 15, precision 20
        value = getRandomLongWithNDigits(prec20);
        DecimalData.convertLongToPackedDecimal(value, pd, offset15, prec20, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset15, prec20, false);
        assertEquals(value, resultLong);

        // offset 15, precision 30
        value = getRandomLongWithNDigits(prec30);
        DecimalData.convertLongToPackedDecimal(value, pd, offset15, prec30, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset15, prec30, false);
        assertEquals(value, resultLong);

        // offset 15, precision 40
        value = getRandomLongWithNDigits(prec40);
        DecimalData.convertLongToPackedDecimal(value, pd, offset15, prec40, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset15, prec40, false);
        assertEquals(value, resultLong);

        // offset 15, precision 50
        value = getRandomLongWithNDigits(prec50);
        DecimalData.convertLongToPackedDecimal(value, pd, offset15, prec50, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset15, prec50, false);
        assertEquals(value, resultLong);

        // offset 15, precision 60
        value = getRandomLongWithNDigits(prec60);
        DecimalData.convertLongToPackedDecimal(value, pd, offset15, prec60, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset15, prec60, false);
        assertEquals(value, resultLong);

    }

    @Test
    public void testPD2IntWithSetPrecisionSetOffset()
    {

        byte[] pd = new byte[128];

        int offset0 = 0;
        int offset1 = 1;
        int offset5 = 5;
        int offset10 = 10;
        int offset15 = 15;

        int prec1 = 1;
        int prec5 = 5;
        int prec10 = 10;
        int prec20 = 20;
        int prec30 = 30;

        int value;

        // offset 0, precision 1
        value = getRandomIntegerWithNDigits(prec1);
        DecimalData.convertLongToPackedDecimal(value, pd, offset0, prec1, false);
        long resultLong = DecimalData.convertPackedDecimalToLong(pd, offset0, prec1, false);
        assertEquals("Case: offset 0, precision 1", value, resultLong);

        // offset 0, precision 5
        value = getRandomIntegerWithNDigits(prec5);
        DecimalData.convertLongToPackedDecimal(value, pd, offset0, prec5, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset0, prec5, false);
        assertEquals("Case: offset 0, precision 5", value, resultLong);

        // offset 0, precision 10
        value = getRandomIntegerWithNDigits(prec10);
        DecimalData.convertLongToPackedDecimal(value, pd, offset0, prec10, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset0, prec10, false);
        assertEquals("Case: offset 0, precision 10", value, resultLong);

        // offset 0, precision 20
        value = getRandomIntegerWithNDigits(prec20);
        DecimalData.convertLongToPackedDecimal(value, pd, offset0, prec20, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset0, prec20, false);
        assertEquals("Case: offset 0, precision 20", value, resultLong);

        // offset 0, precision 30
        value = getRandomIntegerWithNDigits(prec30);
        DecimalData.convertLongToPackedDecimal(value, pd, offset0, prec30, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset0, prec30, false);
        assertEquals("Case: offset 0, precision 30", value, resultLong);

        // offset 1, precision 1
        value = getRandomIntegerWithNDigits(prec1);
        DecimalData.convertLongToPackedDecimal(value, pd, offset1, prec1, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset1, prec1, false);
        assertEquals("Case: offset 1, precision 1", value, resultLong);

        // offset 1, precision 5
        value = getRandomIntegerWithNDigits(prec5);
        DecimalData.convertLongToPackedDecimal(value, pd, offset1, prec5, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset1, prec5, false);
        assertEquals("Case: offset 1, precision 5", value, resultLong);

        // offset 1, precision 10
        value = getRandomIntegerWithNDigits(prec10);
        DecimalData.convertLongToPackedDecimal(value, pd, offset1, prec10, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset1, prec10, false);
        assertEquals("Case: offset 1, precision 10", value, resultLong);

        // offset 1, precision 20
        value = getRandomIntegerWithNDigits(prec20);
        DecimalData.convertLongToPackedDecimal(value, pd, offset1, prec20, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset1, prec20, false);
        assertEquals("Case: offset 1, precision 20", value, resultLong);

        // offset 1, precision 30
        value = getRandomIntegerWithNDigits(prec30);
        DecimalData.convertLongToPackedDecimal(value, pd, offset1, prec30, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset1, prec30, false);
        assertEquals("Case: offset 1, precision 30", value, resultLong);

        // offset 5, precision 1
        value = getRandomIntegerWithNDigits(prec1);
        DecimalData.convertLongToPackedDecimal(value, pd, offset5, prec1, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset5, prec1, false);
        assertEquals("Case: offset 5, precision 1", value, resultLong);

        // offset 5, precision 5
        value = getRandomIntegerWithNDigits(prec5);
        DecimalData.convertLongToPackedDecimal(value, pd, offset5, prec5, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset5, prec5, false);
        assertEquals("Case: offset 5, precision 5", value, resultLong);

        // offset 5, precision 10
        value = getRandomIntegerWithNDigits(prec10);
        DecimalData.convertLongToPackedDecimal(value, pd, offset5, prec10, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset5, prec10, false);
        assertEquals("Case: offset 5, precision 10", value, resultLong);

        // offset 5, precision 20
        value = getRandomIntegerWithNDigits(prec20);
        DecimalData.convertLongToPackedDecimal(value, pd, offset5, prec20, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset5, prec20, false);
        assertEquals("Case: offset 5, precision 20", value, resultLong);

        // offset 5, precision 30
        value = getRandomIntegerWithNDigits(prec30);
        DecimalData.convertLongToPackedDecimal(value, pd, offset5, prec30, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset5, prec30, false);
        assertEquals("Case: offset 5, precision 30", value, resultLong);

        // offset 10, precision 1
        value = getRandomIntegerWithNDigits(prec1);
        DecimalData.convertLongToPackedDecimal(value, pd, offset10, prec1, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset10, prec1, false);
        assertEquals("Case: offset 10, precision 1", value, resultLong);

        // offset 10, precision 5
        value = getRandomIntegerWithNDigits(prec5);
        DecimalData.convertLongToPackedDecimal(value, pd, offset10, prec5, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset10, prec5, false);
        assertEquals("Case: offset 10, precision 5", value, resultLong);

        // offset 10, precision 10
        value = getRandomIntegerWithNDigits(prec10);
        DecimalData.convertLongToPackedDecimal(value, pd, offset10, prec10, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset10, prec10, false);
        assertEquals("Case: offset 10, precision 10", value, resultLong);

        // offset 10, precision 20
        value = getRandomIntegerWithNDigits(prec20);
        DecimalData.convertLongToPackedDecimal(value, pd, offset10, prec20, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset10, prec20, false);
        assertEquals("Case: offset 10, precision 20", value, resultLong);

        // offset 10, precision 30
        value = getRandomIntegerWithNDigits(prec30);
        DecimalData.convertLongToPackedDecimal(value, pd, offset10, prec30, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset10, prec30, false);
        assertEquals("Case: offset 10, precision 30", value, resultLong);

        // offset 15, precision 1
        value = getRandomIntegerWithNDigits(prec1);
        DecimalData.convertLongToPackedDecimal(value, pd, offset15, prec1, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset15, prec1, false);
        assertEquals("Case: offset 15, precision 1", value, resultLong);

        // offset 15, precision 5
        value = getRandomIntegerWithNDigits(prec5);
        DecimalData.convertLongToPackedDecimal(value, pd, offset15, prec5, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset15, prec5, false);
        assertEquals("Case: offset 15, precision 5", value, resultLong);

        // offset 15, precision 10
        value = getRandomIntegerWithNDigits(prec10);
        DecimalData.convertLongToPackedDecimal(value, pd, offset15, prec10, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset15, prec10, false);
        assertEquals("Case: offset 15, precision 10", value, resultLong);

        // offset 15, precision 20
        value = getRandomIntegerWithNDigits(prec20);
        DecimalData.convertLongToPackedDecimal(value, pd, offset15, prec20, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset15, prec20, false);
        assertEquals("Case: offset 15, precision 20", value, resultLong);

        // offset 15, precision 30
        value = getRandomIntegerWithNDigits(prec30);
        DecimalData.convertLongToPackedDecimal(value, pd, offset15, prec30, false);
        resultLong = DecimalData.convertPackedDecimalToLong(pd, offset15, prec30, false);
        assertEquals("Case: offset 15, precision 30", value, resultLong);
    }
}
