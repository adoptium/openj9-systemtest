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

package net.openj9.test.decimals;

import java.math.BigDecimal;
import java.math.BigInteger;

import java.util.Arrays;
import java.util.Random;

import com.ibm.dataaccess.*;

import net.openj9.test.Utils;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import static org.junit.Assert.*;

public class TestDecimalData
{
    static String outputFile;

    static Random randomGenerator;

    byte[] externalDecimal = new byte[64];

    final int offset0 = 0;
    final int offset5 = 5;
    final int offset10 = 10;
    final int offset25 = 25;
    final int offset50 = 50;

    final int precision1 = 1;
    final int precision2 = 2;
    final int precision15 = 15;
    final int precision16 = 16;
    final int precision30 = 30;
    final int precision31 = 31;
    final int precision50 = 50;
    final int precision100 = 100;

    final boolean errorChecking = false;
    final boolean errorCheckingFalse = false;

    final int decimalType1 = DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING;
    final int decimalType2 = DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING;
    final int decimalType3 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
    final int decimalType4 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
    final int decimalType5 = DecimalData.UNICODE_UNSIGNED;
    final int decimalType6 = DecimalData.UNICODE_SIGN_SEPARATE_LEADING;
    final int decimalType7 = DecimalData.UNICODE_SIGN_SEPARATE_TRAILING;

    long value = Utils.TestValue.SmallPositive.LongValue;

    byte[] packedDecimal = new byte[64];
    char[] unicodeDecimal = new char[128];
    byte[] finalDecimal = new byte[64];

    static
    {
        outputFile = "expected." + TestDecimalData.class.getSimpleName() + ".txt";
    };

    @Test
    public void testBDToPD()
    {
        BigDecimal bd = new BigDecimal("123456789012345678901234567890");
        byte[] byteArray = new byte[1024];

        DecimalData.convertBigDecimalToPackedDecimal(bd, byteArray, 0, 30, true);
    }

    @Test
    public void testNonExceptions()
    {
        testConvertLongNormals();

        testConvertIntegerNormals();

        testConvertBigDecimalNormals();

        testConvertBigIntegerNormals();

        testOtherConverters();
    }

    @Test
    public void testLong2ED2Long()
    {
        long result;

        byte[] edValue = new byte[100];

        for (int i = 0; i < 1000; i++)
        {
            long value = randomGenerator.nextLong();
            int length = String.valueOf(value).length();
            for (int decimalType = 1; decimalType <= 4; decimalType++)
            {
                DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, false, decimalType);
                result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, false, decimalType);

                assertEquals(value, result);
            }

        }

    }

    @Test
    public void testOverflowED2Long()
    {
        long result;

        // ed is 10e18, just over the maximum for long
        byte[] edValue = new byte[] { (byte) 0xc1, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0 };
        try
        {
            result = DecimalData.convertExternalDecimalToLong(edValue, 0, 20, true, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);
            fail("Missed exception! input: 10e18 result: " + result);
        }
        catch (ArithmeticException e)
        {
        }

        // ed is 20e18, just over double the maximum for long
        edValue = new byte[] { (byte) 0xc2, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0 };
        try
        {
            result = DecimalData.convertExternalDecimalToLong(edValue, 0, 20, true, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);
            fail("Missed exception! input: 20e18 result: " + result);
        }
        catch (ArithmeticException e)
        {
        }
    }

    @Test
    public void testOverflowED2Integer()
    {
        int result;

        // ed is 3e9, just over the maximum for int
        byte[] edValue = new byte[] { (byte) 0xc3, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0 };
        try
        {
            result = DecimalData.convertExternalDecimalToInteger(edValue, 0, 10, true, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);
            fail("Missed exception! input: 3e9 result: " + result);
        }
        catch (ArithmeticException e)
        {
        }

        // ed is 5e9, just over double the maximum for int
        edValue = new byte[] { (byte) 0xc5, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0 };
        try
        {
            result = DecimalData.convertExternalDecimalToInteger(edValue, 0, 10, true, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);
            fail("Missed exception! input: 5e9 result: " + result);
        }
        catch (ArithmeticException e)
        {
        }
    }

    @Test
    public void testInteger2ED2Integer()
    {

        int result;

        byte[] edValue = new byte[100];
        for (int i = 0; i < 1000; i++)
        {
            int value = randomGenerator.nextInt();
            int length = String.valueOf(value).length();
            for (int decimalType = 1; decimalType <= 4; decimalType++)
            {
                DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, false, decimalType);
                result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, false, decimalType);
                assertEquals(value, result);
            }

        }

    }

    @Test
    public void testBiggestLong2ED2Long()
    {

        long result;

        byte[] edValue = new byte[100];

        long value = Long.MAX_VALUE;
        int length = 19;

        DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, false, 2);
        result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, false, 2);
        assertEquals(value, result);

        DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, false, 3);
        result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, false, 3);
        assertEquals(value, result);

        DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, false, 4);
        result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, false, 4);
        assertEquals(value, result);

        DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, false, 1);
        result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, false, 1);
        assertEquals(value, result);

        // overflow
        DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, true, 1);
        result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, true, 1);
        assertEquals(value, result);

        DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, true, 2);
        result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, true, 2);
        assertEquals(value, result);

        DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, true, 3);
        result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, true, 3);
        assertEquals(value, result);

        DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, true, 4);
        result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, true, 4);
        assertEquals(value, result);
    }

    @Test
    public void testBiggestInt2ED2Int()
    {

        int result;

        byte[] edValue = new byte[100];

        int value = Integer.MAX_VALUE;
        int length = 10;

        DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, false, 1);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, false, 1);
        assertEquals(value, result);

        DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, false, 2);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, false, 2);
        assertEquals(value, result);

        DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, false, 3);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, false, 3);
        assertEquals(value, result);

        DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, false, 4);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, false, 4);
        assertEquals(value, result);

        // overflow
        DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, true, 1);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, true, 1);
        assertEquals(value, result);

        DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, true, 2);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, true, 2);
        assertEquals(value, result);

        DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, true, 3);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, true, 3);
        assertEquals(value, result);

        DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, true, 4);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, true, 4);
        assertEquals(value, result);
    }

    @Test
    public void testSmallestLong2ED2Long()
    {

        long result;

        byte[] edValue = new byte[100];

        long value = Long.MIN_VALUE;
        int length = 19;

        DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, false, 1);
        result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, false, 1);
        assertEquals(value, result);

        DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, false, 2);
        result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, false, 2);
        assertEquals(value, result);

        DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, false, 3);
        result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, false, 3);
        assertEquals(value, result);

        DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, false, 4);
        result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, false, 4);
        assertEquals(value, result);

        // overflow
        DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, true, 1);
        result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, true, 1);
        assertEquals(value, result);

        DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, true, 2);
        result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, true, 2);
        assertEquals(value, result);

        DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, true, 3);
        result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, true, 3);
        assertEquals(value, result);

        DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, true, 4);
        result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, true, 4);
        assertEquals(value, result);

    }

    @Test
    public void testSmallestInt2ED2Int()
    {

        int result;

        byte[] edValue = new byte[100];

        long value = Integer.MIN_VALUE;
        int length = 10;

        DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, false, 1);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, false, 1);
        assertEquals(value, result);

        DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, false, 2);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, false, 2);
        assertEquals(value, result);

        DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, false, 3);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, false, 3);
        assertEquals(value, result);

        DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, false, 4);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, false, 4);
        assertEquals(value, result);

        // overflow
        DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, true, 1);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, true, 1);
        assertEquals(value, result);

        DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, true, 2);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, true, 2);
        assertEquals(value, result);

        DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, true, 3);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, true, 3);
        assertEquals(value, result);

        DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, true, 4);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, true, 4);
        assertEquals(value, result);
    }

    @Test
    public void testBiggestInteger2ED2Integer()
    {

        int result;

        byte[] edValue = new byte[100];

        int value = Integer.MAX_VALUE;
        int length = 10;

        DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, false, 1);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, false, 1);
        assertEquals(value, result);

        DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, false, 2);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, false, 2);
        assertEquals(value, result);

        DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, false, 3);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, false, 3);
        assertEquals(value, result);

        DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, false, 4);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, false, 4);
        assertEquals(value, result);

        // overflow
        DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, true, 1);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, true, 1);
        assertEquals(value, result);

        DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, true, 2);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, true, 2);
        assertEquals(value, result);

        DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, true, 3);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, true, 3);
        assertEquals(value, result);

        DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, true, 4);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, true, 4);
        assertEquals(value, result);
    }

    @Test
    public void testInteger2ED2IntegerDecreasingPrecision()
    {

        int result;
        byte[] edValue = new byte[100];

        int value = 12345;

        DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, 4, false, 1);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, 4, false, 1);
        assertEquals(2345, result);

        DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, 4, false, 2);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, 4, false, 2);
        assertEquals(2345, result);

        DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, 4, false, 3);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, 4, false, 3);
        assertEquals(2345, result);

        DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, 4, false, 4);
        result = DecimalData.convertExternalDecimalToInteger(edValue, 0, 4, false, 4);
        assertEquals(2345, result);

        // overflow
        try
        {
            DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, 4, true, 1);
            result = DecimalData.convertExternalDecimalToInteger(edValue, 0, 4, true, 1);
            assertEquals(2345, result);
        }
        catch (Exception e)
        {
        }

        try
        {
            DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, 4, true, 2);
            result = DecimalData.convertExternalDecimalToInteger(edValue, 0, 4, true, 2);
            assertEquals(2345, result);
        }
        catch (Exception e)
        {
        }

        try
        {
            DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, 4, true, 3);
            result = DecimalData.convertExternalDecimalToInteger(edValue, 0, 4, true, 3);
            assertEquals(2345, result);
        }
        catch (Exception e)
        {
        }

        try
        {
            DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, 4, true, 4);
            result = DecimalData.convertExternalDecimalToInteger(edValue, 0, 4, true, 4);
            assertEquals(2345, result);
        }
        catch (Exception e)
        {
        }
    }

    @Test
    public void testLong2ED2LongDecreasingPrecision()
    {

        long result;
        byte[] edValue = new byte[100];

        long value = 12345;

        DecimalData.convertLongToExternalDecimal(value, edValue, 0, 4, false, 1);
        result = DecimalData.convertExternalDecimalToLong(edValue, 0, 4, false, 1);
        assertEquals(2345, result);

        DecimalData.convertLongToExternalDecimal(value, edValue, 0, 4, false, 2);
        result = DecimalData.convertExternalDecimalToLong(edValue, 0, 4, false, 2);
        assertEquals(2345, result);

        DecimalData.convertLongToExternalDecimal(value, edValue, 0, 4, false, 3);
        result = DecimalData.convertExternalDecimalToLong(edValue, 0, 4, false, 3);
        assertEquals(2345, result);

        DecimalData.convertLongToExternalDecimal(value, edValue, 0, 4, false, 4);
        result = DecimalData.convertExternalDecimalToLong(edValue, 0, 4, false, 4);
        assertEquals(2345, result);
    }

    @Test
    public void testBiggestLong2UD2Long()
    {

        long result;

        long value = Long.MAX_VALUE;

        char[] udValue = new char[100];
        int length = String.valueOf(value).length();
        for (int decimalType = 6; decimalType <= 7; decimalType++)
        {
            DecimalData.convertLongToUnicodeDecimal(value, udValue, 0, length, false, decimalType);
            result = DecimalData.convertUnicodeDecimalToLong(udValue, 0, length, false, decimalType);
            assertEquals(value, result);
        }

    }

    @Test
    public void testLong2UD2Long()
    {

        long result;

        char[] udValue = new char[100];
        for (int i = 0; i < 1000; i++)
        {
            long value = randomGenerator.nextLong();
            int length = String.valueOf(value).length();
            for (int decimalType = 6; decimalType <= 7; decimalType++)
            {
                DecimalData.convertLongToUnicodeDecimal(value, udValue, 0, length, false, decimalType);
                result = DecimalData.convertUnicodeDecimalToLong(udValue, 0, length, false, decimalType);
                assertEquals(value, result);
            }

        }

    }

    @Test
    public void testInteger2UD2Integer()
    {

        int result;

        char[] udValue = new char[100];
        for (int i = 0; i < 1000; i++)
        {
            int value = randomGenerator.nextInt();
            int length = String.valueOf(value).length();
            for (int decimalType = 6; decimalType <= 7; decimalType++)
            {
                DecimalData.convertIntegerToUnicodeDecimal(value, udValue, 0, length, false, decimalType);
                result = DecimalData.convertUnicodeDecimalToInteger(udValue, 0, length, false, decimalType);
                assertEquals(value, result);
            }

        }

    }

    @Test
    public void testBiggestInteger2UD2Integer()
    {

        int result;

        int value = Integer.MAX_VALUE;

        char[] udValue = new char[100];
        int length = String.valueOf(value).length();
        for (int decimalType = 6; decimalType <= 7; decimalType++)
        {
            DecimalData.convertIntegerToUnicodeDecimal(value, udValue, 0, length, false, decimalType);
            result = DecimalData.convertUnicodeDecimalToInteger(udValue, 0, length, false, decimalType);
            assertEquals(value, result);
        }

    }

    @Test
    public void testi2PD()
    {
        testi2PDHelper(12345);
        testi2PDHelper_p10(2147483647);
        testi2PDHelper_p10(-2147483648);

        // TODO: test more precisions
        randomGenerator.setSeed(1024);
        for (int i = 0; i < 10; ++i)
        {
            int value = randomGenerator.nextInt();
            testi2PDHelper_p10(value);
        }
    }

    @Test
    public void testi2PDExceptions()
    {
        testi2PDExceptionsHelper(123456);
        // TODO: add more tests
    }

    public void testi2PDExceptionsHelper(final int value)
    {
        final int offset = 0;
        final int precison = 5;

        byte[] packedDecimal = new byte[64];
        try
        {
            DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, offset, precison, true);
        }
        catch (ArithmeticException e)
        {
            return;
        }

        fail("Could not catch exception.");

    }

    // TODO: test more precisions
    public void testi2PDHelper_p10(final int value)
    {
        int result;
        final int offset = 0;
        final int precision = 10;

        byte[] packedDecimal = new byte[64];
        Arrays.fill(packedDecimal, (byte) 0x00);

        {
            DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, offset, precision, true);
            result = DecimalData.convertPackedDecimalToInteger(packedDecimal, offset, precision, false);
            try
            {
                assertEquals(value, result);
            }

            catch (AssertionError e)
            {
                assertEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, result);
            }
        }
    }

    public void testi2PDHelper(final int value)
    {
        int result;
        final int offset = 0;
        final int precision = 5;

        byte[] packedDecimal = new byte[64];
        Arrays.fill(packedDecimal, (byte) 0x00);

        {
            DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, offset, precision, true);
            result = DecimalData.convertPackedDecimalToInteger(packedDecimal, offset, precision, false);
            try
            {
                assertEquals(value, result);
            }

            catch (AssertionError e)
            {
                assertEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, result);
            }
        }
    }

    @Test
    public void testPD2i()
    {
        final int precision = 5;
        final int offset0 = 0;
        int value = 12345;
        final boolean errorChecking = true;

        byte[] packedDecimal = new byte[64];
        BigInteger bi = new BigInteger("12345");
        DecimalData.convertBigIntegerToPackedDecimal(bi, packedDecimal, 0, 5, errorChecking);
        int result = DecimalData.convertPackedDecimalToInteger(packedDecimal, offset0, precision, errorChecking);
        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, result);
        }
    }

    public void testRepeatedly()
    {

        testConvertLongNormals();

        testConvertLongNormals();

        testConvertLongExceptions();

        testConvertLongExceptions();

        testConvertIntegerNormals();

        testConvertIntegerNormals();

        testConvertIntegerExceptions();

        testConvertIntegerExceptions();

        testConvertBigDecimalNormals();

        testConvertBigDecimalNormals();

        testConvertBigDecimalExceptions();

        testConvertBigDecimalExceptions();

        testConvertBigIntegerNormals();

        testConvertBigIntegerNormals();

        testConvertBigIntegerExceptions();

        testConvertBigIntegerExceptions();

        testOtherConverters();

        testOtherConverters();

    }

    @BeforeClass
    public static void setUp()
    {
        randomGenerator = new Random(System.currentTimeMillis());
    }

    @AfterClass
    public static void tearDown()
    {
    }

    @Test
    public void testOtherConverters_pd()
    {
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType5);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType5);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConverters_pd #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, unicodeDecimal, offset0, precision31, decimalType5, finalDecimal, offset0, precision31, decimalType5, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType5);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType5);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConverters_pd #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, unicodeDecimal, offset0, precision31, decimalType5, finalDecimal, offset0, precision31, decimalType5, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType5);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType5);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConverters_pd #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, unicodeDecimal, offset0, precision31, decimalType5, finalDecimal, offset0, precision31, decimalType5, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargestPossible.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType5);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType5);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConverters_pd #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, unicodeDecimal, offset0, precision31, decimalType5, finalDecimal, offset0, precision31, decimalType5, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallestPossible.LongValue;

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConverters_pd #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, unicodeDecimal, offset0, precision31, decimalType5, finalDecimal, offset0, precision31, decimalType5, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.Zero.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType5);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType5);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConverters_pd #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, unicodeDecimal, offset0, precision31, decimalType5, finalDecimal, offset0, precision31, decimalType5, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision1, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision1, decimalType5);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision1, decimalType5);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConverters_pd #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision1, unicodeDecimal, offset0, precision1, decimalType5, finalDecimal, offset0, precision1, decimalType5, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallNegative.LongValue;

        /*
         * Don't test, negative DecimalData.convertLongToPackedDecimal(value,
         * packedDecimal, offset5, precision2, errorChecking);
         * 
         * 
         * 
         * Arrays.fill(unicodeDecimal, (char)0);
         * DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal,
         * offset5, unicodeDecimal, offset5, precision2, decimalType9);
         * 
         * 
         * 
         * DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal,
         * offset5, finalDecimal, offset5, precision2, decimalType9);
         * 
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterArray("", functionName, value,
         * packedDecimal, offset5, precision2, unicodeDecimal, offset5,
         * precision2, decimalType9, finalDecimal, offset5, precision2,
         * decimalType9, errorChecking); assertArrayEquals(testName,
         * packedDecimal, finalDecimal);
         */

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;

        Arrays.fill(unicodeDecimal, (char) 0);
        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset25, precision15, errorChecking);

        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset25, unicodeDecimal, offset25, precision15, decimalType5);
        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset25, finalDecimal, offset25, precision15, decimalType5);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConverters_pd #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset25, precision15, unicodeDecimal, offset25, precision15, decimalType5, finalDecimal, offset25, precision15, decimalType5, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargeNegative.LongValue;

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;

        Arrays.fill(unicodeDecimal, (char) 0);
        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset50, precision2, errorChecking);

        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset50, unicodeDecimal, offset50, precision2, decimalType5);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset50, finalDecimal, offset50, precision2, decimalType5);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConverters_pd #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset50, precision2, unicodeDecimal, offset50, precision2, decimalType5, finalDecimal, offset50, precision2, decimalType5, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargeNegative.LongValue;

        /*
         * Negative, unsigned, dont test
         * DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0,
         * precision16, errorChecking);
         * 
         * 
         * 
         * Arrays.fill(unicodeDecimal, (char)0);
         * DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal,
         * offset0, unicodeDecimal, offset0, precision16, decimalType9);
         * 
         * 
         * 
         * DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal,
         * offset0, finalDecimal, offset0, precision16, decimalType9);
         * 
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterArray("", functionName, value,
         * packedDecimal, offset0, precision16, unicodeDecimal, offset0,
         * precision16, decimalType9, finalDecimal, offset0, precision16,
         * decimalType9, errorChecking); assertArrayEquals(testName,
         * packedDecimal, finalDecimal);
         */

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;

        Arrays.fill(unicodeDecimal, (char) 0);
        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision30, errorChecking);

        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision30, decimalType5);
        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision30, decimalType5);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConverters_pd #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision30, unicodeDecimal, offset0, precision30, decimalType5, finalDecimal, offset0, precision30, decimalType5, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargeNegative.LongValue;

        /*
         * negative, unsigned, don't test
         * DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0,
         * precision50, errorChecking);
         * 
         * 
         * 
         * Arrays.fill(unicodeDecimal, (char)0);
         * DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal,
         * offset0, unicodeDecimal, offset0, precision50, decimalType9);
         * 
         * 
         * 
         * DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal,
         * offset0, finalDecimal, offset0, precision50, decimalType9);
         * 
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterArray("", functionName, value,
         * packedDecimal, offset0, precision50, unicodeDecimal, offset0,
         * precision50, decimalType9, finalDecimal, offset0, precision50,
         * decimalType9, errorChecking); assertArrayEquals(testName,
         * packedDecimal, finalDecimal);
         */

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);
    }

    @Test
    public void testOtherConvertes_UDSL()
    {
        /*
         * UNICODE_SIGN_EMBEDDED_LEADING
         */

        value = Utils.TestValue.SmallPositive.LongValue;

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);
        Arrays.fill(finalDecimal, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType6);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType6);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConvertes_UDSL #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, unicodeDecimal, offset0, precision31, decimalType6, finalDecimal, offset0, precision31, decimalType6, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType6);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType6);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConvertes_UDSL #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, unicodeDecimal, offset0, precision31, decimalType6, finalDecimal, offset0, precision31, decimalType6, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallNegative.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);

        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType6);
        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType6);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConvertes_UDSL #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, unicodeDecimal, offset0, precision31, decimalType6, finalDecimal, offset0, precision31, decimalType6, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargeNegative.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);

        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType6);
        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType6);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConvertes_UDSL #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, unicodeDecimal, offset0, precision31, decimalType6, finalDecimal, offset0, precision31, decimalType6, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargestPossible.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType6);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType6);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConvertes_UDSL #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, unicodeDecimal, offset0, precision31, decimalType6, finalDecimal, offset0, precision31, decimalType6, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallestPossible.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType6);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType6);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConvertes_UDSL #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, unicodeDecimal, offset0, precision31, decimalType6, finalDecimal, offset0, precision31, decimalType6, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.Zero.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType6);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType6);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConvertes_UDSL #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, unicodeDecimal, offset0, precision31, decimalType6, finalDecimal, offset0, precision31, decimalType6, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision1, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision1, decimalType6);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision1, decimalType6);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConvertes_UDSL #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision1, unicodeDecimal, offset0, precision1, decimalType6, finalDecimal, offset0, precision1, decimalType6, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallNegative.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset5, precision2, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset5, unicodeDecimal, offset5, precision2, decimalType6);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset5, finalDecimal, offset5, precision2, decimalType6);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConvertes_UDSL #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset5, precision2, unicodeDecimal, offset5, precision2, decimalType6, finalDecimal, offset5, precision2, decimalType6, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset25, precision15, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset25, unicodeDecimal, offset25, precision15, decimalType6);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset25, finalDecimal, offset25, precision15, decimalType6);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConvertes_UDSL #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset25, precision15, unicodeDecimal, offset25, precision15, decimalType6, finalDecimal, offset25, precision15, decimalType6, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargeNegative.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset5, precision16, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset5, unicodeDecimal, offset5, precision16, decimalType6);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset5, finalDecimal, offset5, precision16, decimalType6);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConvertes_UDSL #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset5, precision16, unicodeDecimal, offset5, precision16, decimalType6, finalDecimal, offset5, precision16, decimalType6, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset50, precision2, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset50, unicodeDecimal, offset50, precision2, decimalType6);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset50, finalDecimal, offset50, precision2, decimalType6);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConvertes_UDSL #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset50, precision2, unicodeDecimal, offset50, precision2, decimalType6, finalDecimal, offset50, precision2, decimalType6, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargeNegative.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision16, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision16, decimalType6);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision16, decimalType6);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConvertes_UDSL #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision16, unicodeDecimal, offset0, precision16, decimalType6, finalDecimal, offset0, precision16, decimalType6, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision30, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision30, decimalType6);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision30, decimalType6);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConvertes_UDSL #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision30, unicodeDecimal, offset0, precision30, decimalType6, finalDecimal, offset0, precision30, decimalType6, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargeNegative.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision50, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision50, decimalType6);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision50, decimalType6);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConvertes_UDSL #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision50, unicodeDecimal, offset0, precision50, decimalType6, finalDecimal, offset0, precision50, decimalType6, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

    }

    @Test
    public void testOtherConverters()
    {

        /*
         * UNICODE_SIGN_EMBEDDED_TRAILING
         */
        value = Utils.TestValue.SmallPositive.LongValue;

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);
        Arrays.fill(finalDecimal, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType7);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType7);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConverters #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, unicodeDecimal, offset0, precision31, decimalType7, finalDecimal, offset0, precision31, decimalType7, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType7);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType7);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConverters #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, unicodeDecimal, offset0, precision31, decimalType7, finalDecimal, offset0, precision31, decimalType7, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallNegative.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType7);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType7);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConverters #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, unicodeDecimal, offset0, precision31, decimalType7, finalDecimal, offset0, precision31, decimalType7, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargeNegative.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType7);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType7);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConverters #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, unicodeDecimal, offset0, precision31, decimalType7, finalDecimal, offset0, precision31, decimalType7, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargestPossible.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType7);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType7);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConverters #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, unicodeDecimal, offset0, precision31, decimalType7, finalDecimal, offset0, precision31, decimalType7, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallestPossible.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType7);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType7);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConverters #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, unicodeDecimal, offset0, precision31, decimalType7, finalDecimal, offset0, precision31, decimalType7, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.Zero.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType7);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType7);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConverters #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, unicodeDecimal, offset0, precision31, decimalType7, finalDecimal, offset0, precision31, decimalType7, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision1, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision1, decimalType7);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision1, decimalType7);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConverters #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision1, unicodeDecimal, offset0, precision1, decimalType7, finalDecimal, offset0, precision1, decimalType7, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallNegative.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset5, precision2, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset5, unicodeDecimal, offset5, precision2, decimalType7);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset5, finalDecimal, offset5, precision2, decimalType7);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConverters #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset5, precision2, unicodeDecimal, offset5, precision2, decimalType7, finalDecimal, offset5, precision2, decimalType7, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset25, precision15, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset25, unicodeDecimal, offset25, precision15, decimalType7);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset25, finalDecimal, offset25, precision15, decimalType7);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConverters #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset25, precision15, unicodeDecimal, offset25, precision15, decimalType7, finalDecimal, offset25, precision15, decimalType7, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargeNegative.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset5, precision16, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset5, unicodeDecimal, offset5, precision16, decimalType7);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset5, finalDecimal, offset5, precision16, decimalType7);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConverters #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset5, precision16, unicodeDecimal, offset5, precision16, decimalType7, finalDecimal, offset5, precision16, decimalType7, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset50, precision2, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset50, unicodeDecimal, offset50, precision2, decimalType7);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset50, finalDecimal, offset50, precision2, decimalType7);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConverters #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset50, precision2, unicodeDecimal, offset50, precision2, decimalType7, finalDecimal, offset50, precision2, decimalType7, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargeNegative.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision16, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision16, decimalType7);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision16, decimalType7);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConverters #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision16, unicodeDecimal, offset0, precision16, decimalType7, finalDecimal, offset0, precision16, decimalType7, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision30, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision30, decimalType7);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision30, decimalType7);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConverters #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision30, unicodeDecimal, offset0, precision30, decimalType7, finalDecimal, offset0, precision30, decimalType7, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargeNegative.LongValue;

        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision50, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision50, decimalType7);

        DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision50, decimalType7);

        try
        {
            assertArrayEquals(packedDecimal, finalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", "testOtherConverters #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision50, unicodeDecimal, offset0, precision50, decimalType7, finalDecimal, offset0, precision50, decimalType7, errorChecking), packedDecimal, finalDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(finalDecimal, (byte) 0x00);
        Arrays.fill(unicodeDecimal, (char) 0);
        Arrays.fill(unicodeDecimal, (char) 0);

    }

    @Test
    public void testConvertBigIntegerExceptions()
    {
        final int offset0 = 0;
        final int precision31 = 31;
        final boolean errorChecking = true;
        final boolean errorCheckingFalse = false;

        BigInteger value = Utils.TestValue.SmallPositive.BigIntegerValue;

        final int decimalType1 = DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING;
        final int decimalType6 = DecimalData.UNICODE_SIGN_SEPARATE_LEADING;
        byte[] packedDecimal = new byte[64];
        final int offset25 = 25;
        final int offset50 = 50;
        final int precision100 = 100;
        final int precision50 = 50;
        final int precision5 = 5;
        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        byte[] longPackedDecimal = new byte[49];
        byte[] externalDecimal = new byte[49];

        try
        {
            DecimalData.convertBigIntegerToPackedDecimal(value, longPackedDecimal, offset0, precision100, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            DecimalData.convertPackedDecimalToBigInteger(longPackedDecimal, offset0, precision100, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);

        try
        {
            DecimalData.convertBigIntegerToPackedDecimal(value, longPackedDecimal, offset50, precision31, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }
        try
        {
            DecimalData.convertPackedDecimalToBigInteger(longPackedDecimal, offset50, precision31, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        byte[] packedDecimalNull = null;

        try
        {
            DecimalData.convertBigIntegerToPackedDecimal(value, packedDecimalNull, offset0, precision31, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }
        try
        {
            DecimalData.convertPackedDecimalToBigInteger(packedDecimalNull, offset0, precision31, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        try
        {
            DecimalData.convertBigIntegerToPackedDecimal(value, packedDecimal, offset0, precision5, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }
        // Big Integer has huge range
        // probably won't overflow
        /*
         * byte[] packedDecimalBiggerThanLong = new byte[64]; BigInteger
         * biggerThanLong = new BigInteger("99999999999999999999");
         * DecimalData.convertBigIntegerToPackedDecimal(biggerThanLong,
         * packedDecimalBiggerThanLong, offset0, precision31, errorChecking);
         * try {
         * DecimalData.convertPackedDecimalToBigInteger(packedDecimalBiggerThanLong
         * , offset0, precision31, errorChecking);
         * fail("Assertion in TestDecimalData on line " +
         * Thread.currentThread().getStackTrace()[2].getLineNumber()); } catch
         * (ArithmeticException e) {
         * 
         * }
         */

        value = Utils.TestValue.LargePositive.BigIntegerValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertBigIntegerToPackedDecimal(value, packedDecimal, offset0, precision5, errorCheckingFalse);
        byte[] correct = new byte[64];
        DecimalData.convertBigIntegerToPackedDecimal(Utils.TestValue.Zero.BigIntegerValue, correct, offset0, precision5, errorCheckingFalse);
        try
        {
            assertArrayEquals(correct, packedDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), correct, packedDecimal);
        }

        /*
         * Arrays.fill(packedDecimal, (byte) 0x00); // originally, used
         * overflowing of long/int to test for errorChecking validity, but
         * biginteger probably wont overflow, so dont test BigInteger
         * oneMoreThanLongRange = new BigInteger("9223372036854775808");
         * packedDecimalBiggerThanLong = new byte[64];
         * DecimalData.convertBigIntegerToPackedDecimal(oneMoreThanLongRange,
         * packedDecimalBiggerThanLong, offset0, precision31,
         * errorCheckingFalse); result =
         * DecimalData.convertPackedDecimalToBigInteger
         * (packedDecimalBiggerThanLong, offset0, precision31,
         * errorCheckingFalse); assertEquals(testName, Long.MIN_VALUE, result);
         */

        /*
         * convertBigIntegerToExternalDecimal
         */

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(externalDecimal, (byte) 0x00);

        try
        {
            DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision100, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {

            DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision100, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(externalDecimal, (byte) 0x00);

        try
        {
            DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset25, precision50, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset25, precision50, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        byte[] externalDecimalNull = null;

        try
        {
            DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimalNull, offset0, precision31, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }

        try
        {
            DecimalData.convertExternalDecimalToBigInteger(externalDecimalNull, offset0, precision31, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        Arrays.fill(externalDecimal, (byte) 0x00);

        try
        {
            DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision5, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }

        /*
         * biggerThanLong = new BigInteger("99999999999999999999");
         * Arrays.fill(externalDecimal, (byte) 0x00);
         * DecimalData.convertBigIntegerToExternalDecimal(biggerThanLong,
         * externalDecimal, offset0, precision31, errorChecking, decimalType1);
         * try { DecimalData.convertExternalDecimalToBigInteger(externalDecimal,
         * offset0, precision31, errorChecking, decimalType1);
         * fail("Assertion in TestDecimalData on line " +
         * Thread.currentThread().getStackTrace()[2].getLineNumber()); } catch
         * (ArithmeticException e) {
         * 
         * }
         */

        value = Utils.TestValue.LargePositive.BigIntegerValue;
        externalDecimal = new byte[64];
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision5, errorCheckingFalse, decimalType1);
        correct = new byte[64];
        DecimalData.convertBigIntegerToExternalDecimal(Utils.TestValue.Zero.BigIntegerValue, correct, offset0, precision5, errorCheckingFalse, decimalType1);
        try
        {
            assertArrayEquals(correct, externalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), correct, externalDecimal);
        }

        // this is one larger than Long.MAX_VALUE
        // when converted to packed, then packed to long
        // should overflow into Long.MIN_Value
        /*
         * oneMoreThanLongRange = new BigInteger("9223372036854775808");
         * Arrays.fill(externalDecimal, (byte) 0x00);
         * DecimalData.convertBigIntegerToExternalDecimal(oneMoreThanLongRange,
         * externalDecimal, offset0, precision31, errorCheckingFalse,
         * decimalType1); result =
         * DecimalData.convertExternalDecimalToBigInteger(externalDecimal,
         * offset0, precision31, errorCheckingFalse, decimalType1);
         * assertEquals(testName, Long.MIN_VALUE, result);
         */

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        Arrays.fill(externalDecimal, (byte) 0x00);

        try
        {
            DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, 50);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (IllegalArgumentException e)
        {

        }

        Arrays.fill(externalDecimal, (byte) 0x00);
        try
        {
            DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, 50);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (IllegalArgumentException e)
        {

        }

        /*
         * convertBigIntegerToUnicodeDecimal
         */

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        char[] unicodeDecimal = new char[64];

        try
        {
            DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision100, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision100, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);

        try
        {
            DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset25, precision50, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset25, precision50, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        char[] unicodeDecimalNull = null;

        try
        {
            DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimalNull, offset0, precision31, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }

        try
        {
            DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimalNull, offset0, precision31, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);

        try
        {
            DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision5, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }

        /*
         * biggerThanLong = new BigInteger("99999999999999999999");
         * Arrays.fill(unicodeDecimal, (char)0);
         * DecimalData.convertBigIntegerToUnicodeDecimal(biggerThanLong,
         * unicodeDecimal, offset0, precision31, errorChecking, decimalType9);
         * try { DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal,
         * offset0, precision31, errorChecking, decimalType9);
         * fail("Assertion in TestDecimalData on line " +
         * Thread.currentThread().getStackTrace()[2].getLineNumber()); } catch
         * (ArithmeticException e) {
         * 
         * }
         */

        value = Utils.TestValue.LargePositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision5, errorCheckingFalse, decimalType6);
        char[] correctUnicode = new char[64];
        DecimalData.convertBigIntegerToUnicodeDecimal(Utils.TestValue.Zero.BigIntegerValue, correctUnicode, offset0, precision5, errorCheckingFalse, decimalType6);
        try
        {
            assertArrayEquals(correctUnicode, unicodeDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), correctUnicode, unicodeDecimal);
        }

        // this is one larger than Long.MAX_VALUE
        // when converted to packed, then packed to long
        // should overflow into Long.MIN_Value
        /*
         * oneMoreThanLongRange = new BigInteger("9223372036854775808");
         * Arrays.fill(unicodeDecimal, (char)0);
         * DecimalData.convertBigIntegerToUnicodeDecimal(oneMoreThanLongRange,
         * unicodeDecimal, offset0, precision31, errorCheckingFalse,
         * decimalType9); result =
         * DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal,
         * offset0, precision31, errorCheckingFalse, decimalType9);
         * assertEquals(testName, Long.MIN_VALUE, result);
         */

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);

        try
        {
            DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (IllegalArgumentException e)
        {

        }

        try
        {
            DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (IllegalArgumentException e)
        {

        }
    }

    @Test
    public void testConvertBigIntegerNormals()
    {

        /*
         * this function will test convertLong to xxxx convertFromxxx back to
         * long, and assert both equals
         * 
         * the values to test will be small positive, large positive, largest
         * possible, smallest possible, small negative, large negative, zero do
         * two tests for offset/precision test every single decimal type
         */
        byte[] externalDecimal = new byte[64];
        final int offset0 = 0;
        final int offset5 = 5;
        final int offset25 = 25;
        final int offset50 = 50;
        final int precision1 = 1;
        final int precision2 = 2;
        final int precision15 = 15;
        final int precision16 = 16;
        final int precision30 = 30;
        final int precision31 = 31;
        final int precision50 = 50;
        final boolean errorChecking = false;
        final int decimalType1 = DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING;
        final int decimalType2 = DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING;
        final int decimalType3 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
        final int decimalType4 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
        final int decimalType5 = DecimalData.UNICODE_UNSIGNED;
        final int decimalType6 = DecimalData.UNICODE_SIGN_SEPARATE_LEADING;
        final int decimalType7 = DecimalData.UNICODE_SIGN_SEPARATE_TRAILING;

        BigInteger value = Utils.TestValue.SmallPositive.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        BigInteger result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargePositive.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargestPossible.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallestPossible.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.Zero.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision1, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision1, decimalType1, result, offset0, precision1, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.BigIntegerValue;
        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset25, precision15, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset25, precision15, decimalType1, result, offset25, precision15, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigIntegerValue;

        /*
         * DecimalData.convertBigIntegerToExternalDecimal(value,
         * externalDecimal, offset5, precision16, errorChecking, decimalType1);
         * 
         * 
         * 
         * DecimalData.convertExternalDecimalToBigInteger(externalDecimal,
         * offset5, precision16, errorChecking, decimalType1);
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterValue("", functionName, value,
         * externalDecimal, offset5, precision16, decimalType1, result, offset5,
         * precision16, decimalType1, errorChecking); assertEquals(testName,
         * value, result);
         */

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;

        /*
         * DecimalData.convertBigIntegerToExternalDecimal(value,
         * externalDecimal, offset50, precision2, errorChecking, decimalType1);
         * 
         * 
         * 
         * DecimalData.convertExternalDecimalToBigInteger(externalDecimal,
         * offset50, precision2, errorChecking, decimalType1);
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterValue("", functionName, value,
         * externalDecimal, offset50, precision2, decimalType1, result,
         * offset50, precision2, decimalType1, errorChecking);
         * assertEquals(testName, value, result);
         */

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision16, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision16, decimalType1, result, offset0, precision16, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision30, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision30, decimalType1, result, offset0, precision30, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision50, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision50, decimalType1, result, offset0, precision50, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        /*
         * EBCDIC_SIGN_EMBEDDED_LEADING
         */

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargePositive.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargestPossible.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallestPossible.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.Zero.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision1, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision1, decimalType2, result, offset0, precision1, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.BigIntegerValue;

        /*
         * DecimalData.convertBigIntegerToExternalDecimal(value,
         * externalDecimal, offset5, precision2, errorChecking, decimalType2);
         * 
         * 
         * 
         * DecimalData.convertExternalDecimalToBigInteger(externalDecimal,
         * offset5, precision2, errorChecking, decimalType2);
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterValue("", functionName, value,
         * externalDecimal, offset5, precision2, decimalType2, result, offset5,
         * precision2, decimalType2, errorChecking); assertEquals(testName,
         * value, result);
         */

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;

        /*
         * DecimalData.convertBigIntegerToExternalDecimal(value,
         * externalDecimal, offset25, precision15, errorChecking, decimalType2);
         * 
         * 
         * 
         * DecimalData.convertExternalDecimalToBigInteger(externalDecimal,
         * offset25, precision15, errorChecking, decimalType2);
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterValue("", functionName, value,
         * externalDecimal, offset25, precision15, decimalType2, result,
         * offset25, precision15, decimalType2, errorChecking);
         * assertEquals(testName, value, result);
         */

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigIntegerValue;

        /*
         * DecimalData.convertBigIntegerToExternalDecimal(value,
         * externalDecimal, offset5, precision16, errorChecking, decimalType2);
         * 
         * 
         * 
         * DecimalData.convertExternalDecimalToBigInteger(externalDecimal,
         * offset5, precision16, errorChecking, decimalType2);
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterValue("", functionName, value,
         * externalDecimal, offset5, precision16, decimalType2, result, offset5,
         * precision16, decimalType2, errorChecking); assertEquals(testName,
         * value, result);
         */

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;

        /*
         * DecimalData.convertBigIntegerToExternalDecimal(value,
         * externalDecimal, offset50, precision2, errorChecking, decimalType2);
         * 
         * 
         * 
         * DecimalData.convertExternalDecimalToBigInteger(externalDecimal,
         * offset50, precision2, errorChecking, decimalType2);
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterValue("", functionName, value,
         * externalDecimal, offset50, precision2, decimalType2, result,
         * offset50, precision2, decimalType2, errorChecking);
         * assertEquals(testName, value, result);
         */

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision16, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision16, decimalType2, result, offset0, precision16, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision30, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision30, decimalType2, result, offset0, precision30, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision50, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision50, decimalType2, result, offset0, precision50, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        /*
         * EBCDIC_SIGN_SEPARATE_TRAILING
         */

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargePositive.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargestPossible.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallestPossible.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.Zero.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision1, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision1, decimalType3, result, offset0, precision1, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.BigIntegerValue;

        /*
         * DecimalData.convertBigIntegerToExternalDecimal(value,
         * externalDecimal, offset5, precision2, errorChecking, decimalType3);
         * 
         * 
         * 
         * DecimalData.convertExternalDecimalToBigInteger(externalDecimal,
         * offset5, precision2, errorChecking, decimalType3);
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterValue("", functionName, value,
         * externalDecimal, offset5, precision2, decimalType3, result, offset5,
         * precision2, decimalType3, errorChecking); assertEquals(testName,
         * value, result);
         */

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;

        /*
         * DecimalData.convertBigIntegerToExternalDecimal(value,
         * externalDecimal, offset25, precision15, errorChecking, decimalType3);
         * 
         * 
         * 
         * DecimalData.convertExternalDecimalToBigInteger(externalDecimal,
         * offset25, precision15, errorChecking, decimalType3);
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterValue("", functionName, value,
         * externalDecimal, offset25, precision15, decimalType3, result,
         * offset25, precision15, decimalType3, errorChecking);
         * assertEquals(testName, value, result);
         */

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigIntegerValue;

        /*
         * DecimalData.convertBigIntegerToExternalDecimal(value,
         * externalDecimal, offset5, precision16, errorChecking, decimalType3);
         * 
         * 
         * 
         * DecimalData.convertExternalDecimalToBigInteger(externalDecimal,
         * offset5, precision16, errorChecking, decimalType3);
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterValue("", functionName, value,
         * externalDecimal, offset5, precision16, decimalType3, result, offset5,
         * precision16, decimalType3, errorChecking); assertEquals(testName,
         * value, result);
         */

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;

        /*
         * DecimalData.convertBigIntegerToExternalDecimal(value,
         * externalDecimal, offset50, precision2, errorChecking, decimalType3);
         * 
         * 
         * 
         * DecimalData.convertExternalDecimalToBigInteger(externalDecimal,
         * offset50, precision2, errorChecking, decimalType3);
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterValue("", functionName, value,
         * externalDecimal, offset50, precision2, decimalType3, result,
         * offset50, precision2, decimalType3, errorChecking);
         * assertEquals(testName, value, result);
         */

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision16, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision16, decimalType3, result, offset0, precision16, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision30, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision30, decimalType3, result, offset0, precision30, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision50, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision50, decimalType3, result, offset0, precision50, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        /*
         * EBCDIC_SIGN_SEPARATE_LEADING
         */

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargePositive.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargestPossible.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallestPossible.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.Zero.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision1, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision1, decimalType4, result, offset0, precision1, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.BigIntegerValue;

        /*
         * DecimalData.convertBigIntegerToExternalDecimal(value,
         * externalDecimal, offset5, precision2, errorChecking, decimalType4);
         * 
         * 
         * 
         * DecimalData.convertExternalDecimalToBigInteger(externalDecimal,
         * offset5, precision2, errorChecking, decimalType4);
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterValue("", functionName, value,
         * externalDecimal, offset5, precision2, decimalType4, result, offset5,
         * precision2, decimalType4, errorChecking); assertEquals(testName,
         * value, result);
         */

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;

        /*
         * DecimalData.convertBigIntegerToExternalDecimal(value,
         * externalDecimal, offset25, precision15, errorChecking, decimalType4);
         * 
         * 
         * 
         * DecimalData.convertExternalDecimalToBigInteger(externalDecimal,
         * offset25, precision15, errorChecking, decimalType4);
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterValue("", functionName, value,
         * externalDecimal, offset25, precision15, decimalType4, result,
         * offset25, precision15, decimalType4, errorChecking);
         * assertEquals(testName, value, result);
         */

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigIntegerValue;

        /*
         * DecimalData.convertBigIntegerToExternalDecimal(value,
         * externalDecimal, offset5, precision16, errorChecking, decimalType4);
         * 
         * 
         * 
         * DecimalData.convertExternalDecimalToBigInteger(externalDecimal,
         * offset5, precision16, errorChecking, decimalType4);
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterValue("", functionName, value,
         * externalDecimal, offset5, precision16, decimalType4, result, offset5,
         * precision16, decimalType4, errorChecking); assertEquals(testName,
         * value, result);
         */

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;

        /*
         * DecimalData.convertBigIntegerToExternalDecimal(value,
         * externalDecimal, offset50, precision2, errorChecking, decimalType4);
         * 
         * 
         * 
         * DecimalData.convertExternalDecimalToBigInteger(externalDecimal,
         * offset50, precision2, errorChecking, decimalType4);
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterValue("", functionName, value,
         * externalDecimal, offset50, precision2, decimalType4, result,
         * offset50, precision2, decimalType4, errorChecking);
         * assertEquals(testName, value, result);
         */

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision16, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision16, decimalType4, result, offset0, precision16, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision30, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision30, decimalType4, result, offset0, precision30, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision50, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision50, decimalType4, result, offset0, precision50, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        /*
         * UNICODE_UNSIGNED
         */
        char[] UnicodeDecimal = new char[128];

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargePositive.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallNegative.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        // Unsigned, dont test negatives
        // testName = "Line: " +
        // Thread.currentThread().getStackTrace()[2].getLineNumber() + ", " +
        // Utils.makeTestNameConverterValue("", functionName, value,
        // UnicodeDecimal, offset0, precision31, decimalType9, result, offset0,
        // precision31, decimalType9, errorChecking);
        // assertEquals(testName, value, result);

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        // Unsigned, dont test negatives
        // testName = "Line: " +
        // Thread.currentThread().getStackTrace()[2].getLineNumber() + ", " +
        // Utils.makeTestNameConverterValue("", functionName, value,
        // UnicodeDecimal, offset0, precision31, decimalType9, result, offset0,
        // precision31, decimalType9, errorChecking);
        // assertEquals(testName, value, result);

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargestPossible.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallestPossible.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        // negative, unsigned, dont test
        // testName = "Line: " +
        // Thread.currentThread().getStackTrace()[2].getLineNumber() + ", " +
        // Utils.makeTestNameConverterValue("", functionName, value,
        // UnicodeDecimal, offset0, precision31, decimalType9, result, offset0,
        // precision31, decimalType9, errorChecking);
        // assertEquals(testName, value, result);

        value = Utils.TestValue.Zero.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision1, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision1, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision1, decimalType5, result, offset0, precision1, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset5, precision2, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset5, precision2, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset5, precision2, decimalType5, result, offset5, precision2, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset25, precision15, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset25, precision15, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset25, precision15, decimalType5, result, offset25, precision15, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargePositive.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset5, precision16, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset5, precision16, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset5, precision16, decimalType5, result, offset5, precision16, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset50, precision2, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset50, precision2, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset50, precision2, decimalType5, result, offset50, precision2, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargePositive.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision16, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision16, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision16, decimalType5, result, offset0, precision16, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision30, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision30, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision30, decimalType5, result, offset0, precision30, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(UnicodeDecimal, (char) '0');
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision50, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision50, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision50, decimalType5, result, offset0, precision50, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        /*
         * UNICODE_SIGN_EMBEDDED_LEADING
         */

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargePositive.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallNegative.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargestPossible.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallestPossible.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.Zero.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision1, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision1, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision1, decimalType6, result, offset0, precision1, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallNegative.BigIntegerValue;

        /*
         * DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal,
         * offset5, precision2, errorChecking, decimalType10);
         * 
         * 
         * DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal,
         * offset5, precision2, errorChecking, decimalType10);
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterValue("", functionName, value,
         * UnicodeDecimal, offset5, precision2, decimalType10, result, offset5,
         * precision2, decimalType10, errorChecking); assertEquals(testName,
         * value, result);
         * 
         * Arrays.fill(UnicodeDecimal, (char)'0');
         * 
         * 
         * value = Utils.TestValue.SmallPositive.BigIntegerValue; testName =
         * dataName + " " + Utils.TestValue.SmallPositive.TestName + " " +
         * testType + " " + typeName;
         * 
         * DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal,
         * offset25, precision15, errorChecking, decimalType10);
         * 
         * 
         * DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal,
         * offset25, precision15, errorChecking, decimalType10);
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterValue("", functionName, value,
         * UnicodeDecimal, offset25, precision15, decimalType10, result,
         * offset25, precision15, decimalType10, errorChecking);
         * assertEquals(testName, value, result);
         * 
         * Arrays.fill(UnicodeDecimal, (char)'0');
         * 
         * 
         * value = Utils.TestValue.LargeNegative.BigIntegerValue; testName =
         * dataName + " " + Utils.TestValue.LargeNegative.TestName + " " +
         * testType + " " + typeName;
         * 
         * DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal,
         * offset5, precision16, errorChecking, decimalType10);
         * 
         * 
         * DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal,
         * offset5, precision16, errorChecking, decimalType10);
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterValue("", functionName, value,
         * UnicodeDecimal, offset5, precision16, decimalType10, result, offset5,
         * precision16, decimalType10, errorChecking); assertEquals(testName,
         * value, result);
         * 
         * Arrays.fill(UnicodeDecimal, (char)'0');
         * 
         * 
         * value = Utils.TestValue.SmallPositive.BigIntegerValue; testName =
         * dataName + " " + Utils.TestValue.SmallPositive.TestName + " " +
         * testType + " " + typeName;
         * 
         * DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal,
         * offset50, precision2, errorChecking, decimalType10);
         * 
         * 
         * DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal,
         * offset50, precision2, errorChecking, decimalType10);
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterValue("", functionName, value,
         * UnicodeDecimal, offset50, precision2, decimalType10, result,
         * offset50, precision2, decimalType10, errorChecking);
         * assertEquals(testName, value, result);
         * 
         * Arrays.fill(UnicodeDecimal, (char)'0');
         * 
         * 
         * value = Utils.TestValue.LargeNegative.BigIntegerValue; testName =
         * dataName + " " + Utils.TestValue.LargeNegative.TestName + " " +
         * testType + " " + typeName;
         * 
         * DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal,
         * offset0, precision16, errorChecking, decimalType10);
         * 
         * 
         * DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal,
         * offset0, precision16, errorChecking, decimalType10);
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterValue("", functionName, value,
         * UnicodeDecimal, offset0, precision16, decimalType10, result, offset0,
         * precision16, decimalType10, errorChecking); assertEquals(testName,
         * value, result);
         * 
         * Arrays.fill(UnicodeDecimal, (char)'0');
         * 
         * 
         * value = Utils.TestValue.SmallPositive.BigIntegerValue; testName =
         * dataName + " " + Utils.TestValue.SmallPositive.TestName + " " +
         * testType + " " + typeName;
         * 
         * DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal,
         * offset0, precision30, errorChecking, decimalType10);
         * 
         * 
         * DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal,
         * offset0, precision30, errorChecking, decimalType10);
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterValue("", functionName, value,
         * UnicodeDecimal, offset0, precision30, decimalType10, result, offset0,
         * precision30, decimalType10, errorChecking); assertEquals(testName,
         * value, result);
         * 
         * Arrays.fill(UnicodeDecimal, (char)'0');
         * 
         * 
         * value = Utils.TestValue.LargeNegative.BigIntegerValue; testName =
         * dataName + " " + Utils.TestValue.LargeNegative.TestName + " " +
         * testType + " " + typeName;
         * 
         * DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal,
         * offset0, precision50, errorChecking, decimalType10);
         * 
         * 
         * DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal,
         * offset0, precision50, errorChecking, decimalType10);
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterValue("", functionName, value,
         * UnicodeDecimal, offset0, precision50, decimalType10, result, offset0,
         * precision50, decimalType10, errorChecking); assertEquals(testName,
         * value, result);
         */

        Arrays.fill(UnicodeDecimal, (char) '0');

        /*
         * UNICODE_SIGN_EMBEDDED_TRAILING
         */

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(UnicodeDecimal, (char) '0');
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargePositive.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallNegative.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargestPossible.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallestPossible.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.Zero.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision1, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision1, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision1, decimalType7, result, offset0, precision1, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallNegative.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset5, precision2, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset5, precision2, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset5, precision2, decimalType7, result, offset5, precision2, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset25, precision15, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset25, precision15, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset25, precision15, decimalType7, result, offset25, precision15, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset5, precision16, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset5, precision16, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset5, precision16, decimalType7, result, offset5, precision16, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset50, precision2, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset50, precision2, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset50, precision2, decimalType7, result, offset50, precision2, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision16, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision16, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision16, decimalType7, result, offset0, precision16, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision30, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision30, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision30, decimalType7, result, offset0, precision30, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        Arrays.fill(UnicodeDecimal, (char) '0');
        DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision50, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision50, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision50, decimalType7, result, offset0, precision50, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        /*
         * convertBigIntegerToPackedDecimal
         */

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        byte[] packedDecimal = new byte[64];
        DecimalData.convertBigIntegerToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        result = DecimalData.convertPackedDecimalToBigInteger(packedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, result, offset0, precision31, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.LargePositive.BigIntegerValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertBigIntegerToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        result = DecimalData.convertPackedDecimalToBigInteger(packedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, result, offset0, precision31, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.BigIntegerValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertBigIntegerToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        result = DecimalData.convertPackedDecimalToBigInteger(packedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, result, offset0, precision31, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertBigIntegerToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        result = DecimalData.convertPackedDecimalToBigInteger(packedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, result, offset0, precision31, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.LargestPossible.BigIntegerValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertBigIntegerToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        result = DecimalData.convertPackedDecimalToBigInteger(packedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, result, offset0, precision31, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallestPossible.BigIntegerValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertBigIntegerToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        result = DecimalData.convertPackedDecimalToBigInteger(packedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, result, offset0, precision31, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.Zero.BigIntegerValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertBigIntegerToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        result = DecimalData.convertPackedDecimalToBigInteger(packedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, result, offset0, precision31, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertBigIntegerToPackedDecimal(value, packedDecimal, offset0, precision1, errorChecking);

        result = DecimalData.convertPackedDecimalToBigInteger(packedDecimal, offset0, precision1, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision1, result, offset0, precision1, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.BigIntegerValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertBigIntegerToPackedDecimal(value, packedDecimal, offset5, precision2, errorChecking);

        result = DecimalData.convertPackedDecimalToBigInteger(packedDecimal, offset5, precision2, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset5, precision2, result, offset5, precision2, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertBigIntegerToPackedDecimal(value, packedDecimal, offset25, precision15, errorChecking);

        result = DecimalData.convertPackedDecimalToBigInteger(packedDecimal, offset25, precision15, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset25, precision15, result, offset25, precision15, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        /*
         * convertBigIntegerToUnicodeDecimal
         */

        /*
         * UNICODE_UNSIGNED
         */

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        char[] unicodeDecimal = new char[128];

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallNegative.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        // Unsigned, dont test negatives
        // testName = "Line: " +
        // Thread.currentThread().getStackTrace()[2].getLineNumber() + ", " +
        // Utils.makeTestNameConverterValue("", functionName, value,
        // unicodeDecimal, offset0, precision31, decimalType9, result, offset0,
        // precision31, decimalType9, errorChecking);
        // assertEquals(testName, value, result);

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        // Unsigned, dont test negatives
        // testName = "Line: " +
        // Thread.currentThread().getStackTrace()[2].getLineNumber() + ", " +
        // Utils.makeTestNameConverterValue("", functionName, value,
        // unicodeDecimal, offset0, precision31, decimalType9, result, offset0,
        // precision31, decimalType9, errorChecking);
        // assertEquals(testName, value, result);

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargestPossible.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallestPossible.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        // negative, unsigned, dont test
        // testName = "Line: " +
        // Thread.currentThread().getStackTrace()[2].getLineNumber() + ", " +
        // Utils.makeTestNameConverterValue("", functionName, value,
        // unicodeDecimal, offset0, precision31, decimalType9, result, offset0,
        // precision31, decimalType9, errorChecking);
        // assertEquals(testName, value, result);

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.Zero.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision1, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision1, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision1, decimalType5, result, offset0, precision1, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset5, precision2, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset5, precision2, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset5, precision2, decimalType5, result, offset5, precision2, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset25, precision15, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset25, precision15, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset25, precision15, decimalType5, result, offset25, precision15, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset5, precision16, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset5, precision16, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset5, precision16, decimalType5, result, offset5, precision16, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset50, precision2, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset50, precision2, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset50, precision2, decimalType5, result, offset50, precision2, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision16, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision16, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision16, decimalType5, result, offset0, precision16, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision30, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision30, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision30, decimalType5, result, offset0, precision30, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision50, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision50, decimalType5, result, offset0, precision50, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        /*
         * UNICODE_SIGN_EMBEDDED_LEADING
         */

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallNegative.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargestPossible.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision50, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision50, decimalType6, result, offset0, precision50, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallestPossible.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.Zero.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision1, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision1, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision1, decimalType6, result, offset0, precision1, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset5, precision2, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset5, precision2, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset5, precision2, decimalType6, result, offset5, precision2, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset25, precision15, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset25, precision15, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset25, precision15, decimalType6, result, offset25, precision15, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset5, precision16, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset5, precision16, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset5, precision16, decimalType6, result, offset5, precision16, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset50, precision2, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset50, precision2, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset50, precision2, decimalType6, result, offset50, precision2, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision16, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision16, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision16, decimalType6, result, offset0, precision16, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision30, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision30, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision30, decimalType6, result, offset0, precision30, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision50, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision50, decimalType6, result, offset0, precision50, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        /*
         * UNICODE_SIGN_EMBEDDED_TRAILING
         */

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallNegative.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargeNegative.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargestPossible.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision50, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision50, decimalType7, result, offset0, precision50, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallestPossible.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.Zero.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision1, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision1, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision1, decimalType7, result, offset0, precision1, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset5, precision2, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset5, precision2, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset5, precision2, decimalType7, result, offset5, precision2, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset25, precision15, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset25, precision15, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset25, precision15, decimalType7, result, offset25, precision15, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset5, precision16, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset5, precision16, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset5, precision16, decimalType7, result, offset5, precision16, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset50, precision2, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset50, precision2, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset50, precision2, decimalType7, result, offset50, precision2, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision16, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision16, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision16, decimalType7, result, offset0, precision16, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision30, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision30, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision30, decimalType7, result, offset0, precision30, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigIntegerValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision50, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision50, decimalType7, result, offset0, precision50, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

    }

    @Test
    public void testConvertBigDecimalExceptions()
    {
        final int offset0 = 0;
        final int precision31 = 31;
        final boolean errorChecking = true;
        final boolean errorCheckingFalse = false;

        BigDecimal value = Utils.TestValue.SmallPositive.BigDecimalValue;

        final int decimalType1 = DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING;
        final int decimalType6 = DecimalData.UNICODE_SIGN_SEPARATE_LEADING;
        byte[] packedDecimal = new byte[64];
        final int offset25 = 25;
        final int offset50 = 50;
        final int precision100 = 100;
        final int precision50 = 50;
        final int precision5 = 5;
        final int scale0 = 0;
        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        byte[] longPackedDecimal = new byte[49];
        byte[] externalDecimal = new byte[49];

        try
        {
            DecimalData.convertBigDecimalToPackedDecimal(value, longPackedDecimal, offset0, precision100, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            DecimalData.convertPackedDecimalToBigDecimal(longPackedDecimal, offset0, precision100, scale0, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(longPackedDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);

        try
        {
            DecimalData.convertBigDecimalToPackedDecimal(value, longPackedDecimal, offset50, precision31, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }
        try
        {
            DecimalData.convertPackedDecimalToBigDecimal(longPackedDecimal, offset50, precision31, scale0, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        byte[] packedDecimalNull = null;

        try
        {
            DecimalData.convertBigDecimalToPackedDecimal(value, packedDecimalNull, offset0, precision31, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }
        try
        {
            DecimalData.convertPackedDecimalToBigDecimal(packedDecimalNull, offset0, precision31, scale0, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        try
        {
            DecimalData.convertBigDecimalToPackedDecimal(value, packedDecimal, offset0, precision5, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }
        // Big Integer has huge range
        // probably won't overflow
        /*
         * byte[] packedDecimalBiggerThanLong = new byte[64]; BigInteger
         * biggerThanLong = new BigInteger("99999999999999999999");
         * DecimalData.convertBigIntegerToPackedDecimal(biggerThanLong,
         * packedDecimalBiggerThanLong, offset0, precision31, errorChecking);
         * try {
         * DecimalData.convertPackedDecimalToBigDecimal(packedDecimalBiggerThanLong
         * , offset0, precision31, scale0, errorChecking);
         * fail("Assertion in TestDecimalData on line " +
         * Thread.currentThread().getStackTrace()[2].getLineNumber()); } catch
         * (ArithmeticException e) {
         * 
         * }
         */

        value = Utils.TestValue.LargePositive.BigDecimalValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertBigDecimalToPackedDecimal(value, packedDecimal, offset0, precision5, errorCheckingFalse);
        byte[] correct = new byte[64];
        DecimalData.convertBigDecimalToPackedDecimal(Utils.TestValue.Zero.BigDecimalValue, correct, offset0, precision5, errorCheckingFalse);
        try
        {
            assertArrayEquals(correct, packedDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), correct, packedDecimal);
        }

        /*
         * Arrays.fill(packedDecimal, (byte) 0x00); // this is one larger than
         * Long.MAX_VALUE // when converted to packed, then packed to long
         * should overflow into Long.MIN_Value BigInteger oneMoreThanLongRange =
         * new BigInteger("9223372036854775808"); packedDecimalBiggerThanLong =
         * new byte[64];
         * DecimalData.convertBigIntegerToPackedDecimal(oneMoreThanLongRange,
         * packedDecimalBiggerThanLong, offset0, precision31,
         * errorCheckingFalse); result =
         * DecimalData.convertPackedDecimalToBigDecimal
         * (packedDecimalBiggerThanLong, offset0, precision31, scale0,
         * errorCheckingFalse); assertEquals(testName, Long.MIN_VALUE, result);
         */

        /*
         * convertBigDecimalToExternalDecimal
         */

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(externalDecimal, (byte) 0x00);

        try
        {
            DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision100, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {

            DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision100, scale0, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(externalDecimal, (byte) 0x00);

        try
        {
            DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset25, precision50, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset25, precision50, scale0, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        byte[] externalDecimalNull = null;

        try
        {
            DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimalNull, offset0, precision31, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }

        try
        {
            DecimalData.convertExternalDecimalToBigDecimal(externalDecimalNull, offset0, precision31, scale0, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        Arrays.fill(externalDecimal, (byte) 0x00);

        try
        {
            DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision5, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }

        /*
         * biggerThanLong = new BigInteger("99999999999999999999");
         * Arrays.fill(externalDecimal, (byte) 0x00);
         * DecimalData.convertBigIntegerToExternalDecimal(biggerThanLong,
         * externalDecimal, offset0, precision31, errorChecking, decimalType1);
         * try { DecimalData.convertExternalDecimalToBigDecimal(externalDecimal,
         * offset0, precision31, scale0, errorChecking, decimalType1);
         * fail("Assertion in TestDecimalData on line " +
         * Thread.currentThread().getStackTrace()[2].getLineNumber()); } catch
         * (ArithmeticException e) {
         * 
         * }
         */

        value = Utils.TestValue.LargePositive.BigDecimalValue;
        externalDecimal = new byte[64];
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision5, errorCheckingFalse, decimalType1);
        correct = new byte[64];
        DecimalData.convertBigDecimalToExternalDecimal(Utils.TestValue.Zero.BigDecimalValue, correct, offset0, precision5, errorCheckingFalse, decimalType1);
        try
        {
            assertArrayEquals(correct, externalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), correct, externalDecimal);
        }

        // this is one larger than Long.MAX_VALUE
        // when converted to packed, then packed to long
        // should overflow into Long.MIN_Value
        /*
         * oneMoreThanLongRange = new BigInteger("9223372036854775808");
         * Arrays.fill(externalDecimal, (byte) 0x00);
         * DecimalData.convertBigIntegerToExternalDecimal(oneMoreThanLongRange,
         * externalDecimal, offset0, precision31, errorCheckingFalse,
         * decimalType1); result =
         * DecimalData.convertExternalDecimalToBigDecimal(externalDecimal,
         * offset0, precision31, scale0, errorCheckingFalse, decimalType1);
         * assertEquals(testName, Long.MIN_VALUE, result);
         */

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        Arrays.fill(externalDecimal, (byte) 0x00);

        try
        {
            DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, 50);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (IllegalArgumentException e)
        {

        }

        Arrays.fill(externalDecimal, (byte) 0x00);
        try
        {
            DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, 50);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (IllegalArgumentException e)
        {

        }

        /*
         * convertBigDecimalToUnicodeDecimal
         */

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        char[] unicodeDecimal = new char[64];

        try
        {
            DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision100, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision100, scale0, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);

        try
        {
            DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset25, precision50, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset25, precision50, scale0, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        char[] unicodeDecimalNull = null;

        try
        {
            DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimalNull, offset0, precision31, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }

        try
        {
            DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimalNull, offset0, precision31, scale0, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);

        try
        {
            DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision5, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }

        /*
         * biggerThanLong = new BigInteger("99999999999999999999");
         * Arrays.fill(unicodeDecimal, (char)0);
         * DecimalData.convertBigIntegerToUnicodeDecimal(biggerThanLong,
         * unicodeDecimal, offset0, precision31, errorChecking, decimalType9);
         * try { DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal,
         * offset0, precision31, scale0, errorChecking, decimalType9);
         * fail("Assertion in TestDecimalData on line " +
         * Thread.currentThread().getStackTrace()[2].getLineNumber()); } catch
         * (ArithmeticException e) {
         * 
         * }
         */

        value = Utils.TestValue.LargePositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision5, errorCheckingFalse, decimalType6);
        char[] correctUnicode = new char[64];
        DecimalData.convertBigDecimalToUnicodeDecimal(Utils.TestValue.Zero.BigDecimalValue, correctUnicode, offset0, precision5, errorCheckingFalse, decimalType6);
        try
        {
            assertArrayEquals(correctUnicode, unicodeDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), correctUnicode, unicodeDecimal);
        }

        // this is one larger than Long.MAX_VALUE
        // when converted to packed, then packed to long
        // should overflow into Long.MIN_Value
        /*
         * oneMoreThanLongRange = new BigInteger("9223372036854775808");
         * Arrays.fill(unicodeDecimal, (char)0);
         * DecimalData.convertBigIntegerToUnicodeDecimal(oneMoreThanLongRange,
         * unicodeDecimal, offset0, precision31, errorCheckingFalse,
         * decimalType9); result =
         * DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal,
         * offset0, precision31, scale0, errorCheckingFalse, decimalType9);
         * assertEquals(testName, Long.MIN_VALUE, result);
         */

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);

        try
        {
            DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (IllegalArgumentException e)
        {

        }

        try
        {
            DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (IllegalArgumentException e)
        {

        }
    }

    @Test
    public void testConvertBigDecimalNormals()
    {

        /*
         * this function will test convertLong to xxxx convertFromxxx back to
         * long, and assert both equals
         * 
         * the values to test will be small positive, large positive, largest
         * possible, smallest possible, small negative, large negative, zero do
         * two tests for offset/precision test every single decimal type
         */
        byte[] externalDecimal = new byte[64];
        final int offset0 = 0;
        final int offset5 = 5;
        final int offset25 = 25;
        final int offset50 = 50;
        final int precision1 = 1;
        final int precision2 = 2;
        final int precision15 = 15;
        final int precision16 = 16;
        final int precision30 = 30;
        final int precision31 = 31;
        final int precision50 = 50;
        final boolean errorChecking = false;
        final int scale0 = 0;

        final int decimalType1 = DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING;
        final int decimalType2 = DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING;
        final int decimalType3 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
        final int decimalType4 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
        final int decimalType5 = DecimalData.UNICODE_UNSIGNED;
        final int decimalType6 = DecimalData.UNICODE_SIGN_SEPARATE_LEADING;
        final int decimalType7 = DecimalData.UNICODE_SIGN_SEPARATE_TRAILING;

        BigDecimal value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        BigDecimal result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargePositive.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargestPossible.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallestPossible.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.Zero.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision1, scale0, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision1, decimalType1, result, offset0, precision1, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset5, precision2, scale0, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset5, precision2, decimalType1, result, offset5, precision2, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset25, precision15, scale0, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset25, precision15, decimalType1, result, offset25, precision15, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset5, precision16, scale0, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset5, precision16, decimalType1, result, offset5, precision16, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset50, precision2, scale0, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset50, precision2, decimalType1, result, offset50, precision2, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision16, scale0, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision16, decimalType1, result, offset0, precision16, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision30, scale0, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision30, decimalType1, result, offset0, precision30, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision50, scale0, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision50, decimalType1, result, offset0, precision50, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        /*
         * EBCDIC_SIGN_EMBEDDED_LEADING
         */

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargePositive.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargestPossible.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallestPossible.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.Zero.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision1, scale0, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision1, decimalType2, result, offset0, precision1, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset5, precision2, scale0, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset5, precision2, decimalType2, result, offset5, precision2, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset25, precision15, scale0, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset25, precision15, decimalType2, result, offset25, precision15, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset5, precision16, scale0, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset5, precision16, decimalType2, result, offset5, precision16, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset50, precision2, scale0, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset50, precision2, decimalType2, result, offset50, precision2, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision16, scale0, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision16, decimalType2, result, offset0, precision16, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision30, scale0, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision30, decimalType2, result, offset0, precision30, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision50, scale0, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision50, decimalType2, result, offset0, precision50, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        /*
         * EBCDIC_SIGN_SEPARATE_TRAILING
         */

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargePositive.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargestPossible.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallestPossible.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.Zero.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision1, scale0, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision1, decimalType3, result, offset0, precision1, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset5, precision2, scale0, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset5, precision2, decimalType3, result, offset5, precision2, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset25, precision15, scale0, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset25, precision15, decimalType3, result, offset25, precision15, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset5, precision16, scale0, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset5, precision16, decimalType3, result, offset5, precision16, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset50, precision2, scale0, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset50, precision2, decimalType3, result, offset50, precision2, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision16, scale0, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision16, decimalType3, result, offset0, precision16, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision30, scale0, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision30, decimalType3, result, offset0, precision30, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision50, scale0, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision50, decimalType3, result, offset0, precision50, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        /*
         * EBCDIC_SIGN_SEPARATE_LEADING
         */

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargePositive.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargestPossible.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallestPossible.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.Zero.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision1, scale0, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision1, decimalType4, result, offset0, precision1, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset5, precision2, scale0, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset5, precision2, decimalType4, result, offset5, precision2, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset25, precision15, scale0, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset25, precision15, decimalType4, result, offset25, precision15, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset5, precision16, scale0, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset5, precision16, decimalType4, result, offset5, precision16, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset50, precision2, scale0, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset50, precision2, decimalType4, result, offset50, precision2, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision16, scale0, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision16, decimalType4, result, offset0, precision16, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision30, scale0, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision30, decimalType4, result, offset0, precision30, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision50, scale0, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision50, decimalType4, result, offset0, precision50, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        /*
         * UNICODE_UNSIGNED
         */
        char[] UnicodeDecimal = new char[100];

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargePositive.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);

        // Unsigned, dont test negatives
        // testName = "Line: " +
        // Thread.currentThread().getStackTrace()[2].getLineNumber() + ", " +
        // Utils.makeTestNameConverterValue("", functionName, value,
        // UnicodeDecimal, offset0, precision31, decimalType9, result, offset0,
        // precision31, decimalType9, errorChecking);
        // assertEquals(testName, value, result);

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);

        // Unsigned, dont test negatives
        // testName = "Line: " +
        // Thread.currentThread().getStackTrace()[2].getLineNumber() + ", " +
        // Utils.makeTestNameConverterValue("", functionName, value,
        // UnicodeDecimal, offset0, precision31, decimalType9, result, offset0,
        // precision31, decimalType9, errorChecking);
        // assertEquals(testName, value, result);

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargestPossible.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallestPossible.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);

        Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking);

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.Zero.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision1, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision1, scale0, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision1, decimalType5, result, offset0, precision1, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset5, precision2, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset5, precision2, scale0, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset5, precision2, decimalType5, result, offset5, precision2, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset25, precision15, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset25, precision15, scale0, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset25, precision15, decimalType5, result, offset25, precision15, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargePositive.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset5, precision16, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset5, precision16, scale0, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset5, precision16, decimalType5, result, offset5, precision16, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset50, precision2, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset50, precision2, scale0, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset50, precision2, decimalType5, result, offset50, precision2, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargePositive.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision16, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision16, scale0, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision16, decimalType5, result, offset0, precision16, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision30, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision30, scale0, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision30, decimalType5, result, offset0, precision30, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(UnicodeDecimal, (char) '0');
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision50, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision50, scale0, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision50, decimalType5, result, offset0, precision50, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        /*
         * UNICODE_SIGN_EMBEDDED_LEADING
         */

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargePositive.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargestPossible.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallestPossible.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.Zero.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision1, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision1, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision1, decimalType6, result, offset0, precision1, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset5, precision2, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset5, precision2, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset5, precision2, decimalType6, result, offset5, precision2, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset25, precision15, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset25, precision15, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset25, precision15, decimalType6, result, offset25, precision15, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset5, precision16, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset5, precision16, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset5, precision16, decimalType6, result, offset5, precision16, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset50, precision2, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset50, precision2, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset50, precision2, decimalType6, result, offset50, precision2, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision16, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision16, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision16, decimalType6, result, offset0, precision16, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision30, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision30, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision30, decimalType6, result, offset0, precision30, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision50, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision50, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision50, decimalType6, result, offset0, precision50, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        /*
         * UNICODE_SIGN_EMBEDDED_TRAILING
         */

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(UnicodeDecimal, (char) '0');
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargePositive.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargestPossible.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallestPossible.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.Zero.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision1, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision1, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision1, decimalType7, result, offset0, precision1, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset5, precision2, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset5, precision2, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset5, precision2, decimalType7, result, offset5, precision2, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset25, precision15, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset25, precision15, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset25, precision15, decimalType7, result, offset25, precision15, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset5, precision16, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset5, precision16, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset5, precision16, decimalType7, result, offset5, precision16, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset50, precision2, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset50, precision2, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset50, precision2, decimalType7, result, offset50, precision2, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision16, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision16, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision16, decimalType7, result, offset0, precision16, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision30, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision30, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision30, decimalType7, result, offset0, precision30, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        Arrays.fill(UnicodeDecimal, (char) '0');
        DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision50, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision50, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision50, decimalType7, result, offset0, precision50, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        /*
         * convertBigDecimalToPackedDecimal
         */

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        byte[] packedDecimal = new byte[64];
        DecimalData.convertBigDecimalToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        result = DecimalData.convertPackedDecimalToBigDecimal(packedDecimal, offset0, precision31, scale0, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, result, offset0, precision31, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.LargePositive.BigDecimalValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertBigDecimalToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        result = DecimalData.convertPackedDecimalToBigDecimal(packedDecimal, offset0, precision31, scale0, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, result, offset0, precision31, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.BigDecimalValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertBigDecimalToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        result = DecimalData.convertPackedDecimalToBigDecimal(packedDecimal, offset0, precision31, scale0, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, result, offset0, precision31, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertBigDecimalToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        result = DecimalData.convertPackedDecimalToBigDecimal(packedDecimal, offset0, precision31, scale0, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, result, offset0, precision31, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.LargestPossible.BigDecimalValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertBigDecimalToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        result = DecimalData.convertPackedDecimalToBigDecimal(packedDecimal, offset0, precision31, scale0, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, result, offset0, precision31, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallestPossible.BigDecimalValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertBigDecimalToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        result = DecimalData.convertPackedDecimalToBigDecimal(packedDecimal, offset0, precision31, scale0, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, result, offset0, precision31, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.Zero.BigDecimalValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertBigDecimalToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        result = DecimalData.convertPackedDecimalToBigDecimal(packedDecimal, offset0, precision31, scale0, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, result, offset0, precision31, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertBigDecimalToPackedDecimal(value, packedDecimal, offset0, precision1, errorChecking);

        result = DecimalData.convertPackedDecimalToBigDecimal(packedDecimal, offset0, precision1, scale0, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision1, result, offset0, precision1, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.BigDecimalValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertBigDecimalToPackedDecimal(value, packedDecimal, offset5, precision2, errorChecking);

        result = DecimalData.convertPackedDecimalToBigDecimal(packedDecimal, offset5, precision2, scale0, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset5, precision2, result, offset5, precision2, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertBigDecimalToPackedDecimal(value, packedDecimal, offset25, precision15, errorChecking);

        result = DecimalData.convertPackedDecimalToBigDecimal(packedDecimal, offset25, precision15, scale0, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset25, precision15, result, offset25, precision15, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        /*
         * convertBigDecimalToUnicodeDecimal
         */

        /*
         * UNICODE_UNSIGNED
         */

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        char[] unicodeDecimal = new char[128];

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallNegative.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);

        // Unsigned, dont test negatives
        // testName = "Line: " +
        // Thread.currentThread().getStackTrace()[2].getLineNumber() + ", " +
        // Utils.makeTestNameConverterValue("", functionName, value,
        // unicodeDecimal, offset0, precision31, decimalType9, result, offset0,
        // precision31, decimalType9, errorChecking);
        // assertEquals(testName, value, result);

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);

        // Unsigned, dont test negatives
        // testName = "Line: " +
        // Thread.currentThread().getStackTrace()[2].getLineNumber() + ", " +
        // Utils.makeTestNameConverterValue("", functionName, value,
        // unicodeDecimal, offset0, precision31, decimalType9, result, offset0,
        // precision31, decimalType9, errorChecking);
        // assertEquals(testName, value, result);

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargestPossible.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallestPossible.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);

        Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking);

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.Zero.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision1, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision1, scale0, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision1, decimalType5, result, offset0, precision1, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset5, precision2, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset5, precision2, scale0, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset5, precision2, decimalType5, result, offset5, precision2, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset25, precision15, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset25, precision15, scale0, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset25, precision15, decimalType5, result, offset25, precision15, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset5, precision16, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset5, precision16, scale0, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset5, precision16, decimalType5, result, offset5, precision16, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset50, precision2, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset50, precision2, scale0, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset50, precision2, decimalType5, result, offset50, precision2, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision16, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision16, scale0, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision16, decimalType5, result, offset0, precision16, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision30, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision30, scale0, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision30, decimalType5, result, offset0, precision30, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision50, scale0, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision50, decimalType5, result, offset0, precision50, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        /*
         * UNICODE_SIGN_EMBEDDED_LEADING
         */

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallNegative.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargestPossible.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision50, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision50, decimalType6, result, offset0, precision50, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallestPossible.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.Zero.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision1, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision1, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision1, decimalType6, result, offset0, precision1, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset5, precision2, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset5, precision2, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset5, precision2, decimalType6, result, offset5, precision2, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset25, precision15, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset25, precision15, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset25, precision15, decimalType6, result, offset25, precision15, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset5, precision16, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset5, precision16, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset5, precision16, decimalType6, result, offset5, precision16, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset50, precision2, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset50, precision2, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset50, precision2, decimalType6, result, offset50, precision2, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision16, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision16, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision16, decimalType6, result, offset0, precision16, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision30, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision30, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision30, decimalType6, result, offset0, precision30, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision50, scale0, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision50, decimalType6, result, offset0, precision50, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        /*
         * UNICODE_SIGN_EMBEDDED_TRAILING
         */

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallNegative.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargeNegative.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargestPossible.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision50, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision50, decimalType7, result, offset0, precision50, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallestPossible.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.Zero.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision1, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision1, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision1, decimalType7, result, offset0, precision1, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset5, precision2, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset5, precision2, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset5, precision2, decimalType7, result, offset5, precision2, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset25, precision15, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset25, precision15, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset25, precision15, decimalType7, result, offset25, precision15, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset5, precision16, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset5, precision16, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset5, precision16, decimalType7, result, offset5, precision16, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset50, precision2, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset50, precision2, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset50, precision2, decimalType7, result, offset50, precision2, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision16, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision16, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision16, decimalType7, result, offset0, precision16, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision30, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision30, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision30, decimalType7, result, offset0, precision30, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.BigDecimalValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision50, scale0, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertBigDecimalNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision50, decimalType7, result, offset0, precision50, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

    }

    @Test
    public void testConvertIntegerNormals()
    {

        /*
         * this function will test convertInteger to xxxx convertFromxxx back to
         * long, and assert both equals
         * 
         * the values to test will be small positive, large positive, largest
         * possible, smallest possible, small negative, large negative, zero do
         * two tests for offset/precision test every single decimal type
         */
        byte[] externalDecimal = new byte[64];
        final int offset0 = 0;
        final int offset5 = 5;
        final int offset25 = 25;
        final int offset50 = 50;
        final int precision1 = 1;
        final int precision2 = 2;
        final int precision15 = 15;
        final int precision16 = 16;
        final int precision30 = 30;
        final int precision31 = 31;
        final int precision50 = 50;
        final boolean errorChecking = false;
        final int decimalType1 = DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING;
        final int decimalType2 = DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING;
        final int decimalType3 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
        final int decimalType4 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
        final int decimalType5 = DecimalData.UNICODE_UNSIGNED;
        final int decimalType6 = DecimalData.UNICODE_SIGN_SEPARATE_LEADING;
        final int decimalType7 = DecimalData.UNICODE_SIGN_SEPARATE_TRAILING;

        int value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        int result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargePositive.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargestPossible.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallestPossible.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.Zero.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision1, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision1, decimalType1, result, offset0, precision1, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset5, precision2, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset5, precision2, decimalType1, result, offset5, precision2, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset25, precision15, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset25, precision15, decimalType1, result, offset25, precision15, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset5, precision16, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset5, precision16, decimalType1, result, offset5, precision16, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset50, precision2, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset50, precision2, decimalType1, result, offset50, precision2, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision16, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision16, decimalType1, result, offset0, precision16, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision30, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision30, decimalType1, result, offset0, precision30, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision50, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision50, decimalType1, result, offset0, precision50, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        /*
         * EBCDIC_SIGN_EMBEDDED_LEADING
         */

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(externalDecimal, (byte) 0x00);
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargePositive.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);
        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargestPossible.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallestPossible.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.Zero.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision1, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision1, decimalType2, result, offset0, precision1, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset5, precision2, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset5, precision2, decimalType2, result, offset5, precision2, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset25, precision15, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset25, precision15, decimalType2, result, offset25, precision15, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset5, precision16, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset5, precision16, decimalType2, result, offset5, precision16, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset50, precision2, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset50, precision2, decimalType2, result, offset50, precision2, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision16, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision16, decimalType2, result, offset0, precision16, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision30, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision30, decimalType2, result, offset0, precision30, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision50, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision50, decimalType2, result, offset0, precision50, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        /*
         * EBCDIC_SIGN_SEPARATE_TRAILING
         */

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargePositive.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargestPossible.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallestPossible.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.Zero.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision1, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision1, decimalType3, result, offset0, precision1, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset5, precision2, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset5, precision2, decimalType3, result, offset5, precision2, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset25, precision15, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset25, precision15, decimalType3, result, offset25, precision15, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset5, precision16, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset5, precision16, decimalType3, result, offset5, precision16, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset50, precision2, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset50, precision2, decimalType3, result, offset50, precision2, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision16, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision16, decimalType3, result, offset0, precision16, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision30, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision30, decimalType3, result, offset0, precision30, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision50, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision50, decimalType3, result, offset0, precision50, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        /*
         * EBCDIC_SIGN_SEPARATE_LEADING
         */

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargePositive.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargestPossible.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallestPossible.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.Zero.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision1, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision1, decimalType4, result, offset0, precision1, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset5, precision2, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset5, precision2, decimalType4, result, offset5, precision2, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset25, precision15, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset25, precision15, decimalType4, result, offset25, precision15, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset5, precision16, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset5, precision16, decimalType4, result, offset5, precision16, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset50, precision2, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset50, precision2, decimalType4, result, offset50, precision2, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision16, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision16, decimalType4, result, offset0, precision16, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision30, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision30, decimalType4, result, offset0, precision30, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.IntValue;
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision50, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision50, decimalType4, result, offset0, precision50, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        /*
         * UNICODE_UNSIGNED
         */
        char[] UnicodeDecimal = new char[100];

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargePositive.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallNegative.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        // Unsigned, dont test negatives
        // testName = "Line: " +
        // Thread.currentThread().getStackTrace()[2].getLineNumber() + ", " +
        // Utils.makeTestNameConverterValue("", functionName, value,
        // UnicodeDecimal, offset0, precision31, decimalType9, result, offset0,
        // precision31, decimalType9, errorChecking);
        // assertEquals(testName, value, result);

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        // Unsigned, dont test negatives
        // testName = "Line: " +
        // Thread.currentThread().getStackTrace()[2].getLineNumber() + ", " +
        // Utils.makeTestNameConverterValue("", functionName, value,
        // UnicodeDecimal, offset0, precision31, decimalType9, result, offset0,
        // precision31, decimalType9, errorChecking);
        // assertEquals(testName, value, result);

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargestPossible.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallestPossible.IntValue;

        // don't test, negative value
        /*
         * DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal,
         * offset0, precision31, errorChecking, decimalType9);
         * 
         * 
         * 
         * DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0,
         * precision31, errorChecking, decimalType9);
         * 
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterValue("", functionName, value,
         * UnicodeDecimal, offset0, precision31, decimalType9, result, offset0,
         * precision31, decimalType9, errorChecking); assertEquals(testName,
         * value, result);
         */

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.Zero.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision1, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision1, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision1, decimalType5, result, offset0, precision1, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(UnicodeDecimal, (char) '0');
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset5, precision2, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset5, precision2, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset5, precision2, decimalType5, result, offset5, precision2, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset25, precision15, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset25, precision15, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset25, precision15, decimalType5, result, offset25, precision15, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargePositive.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset5, precision16, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset5, precision16, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset5, precision16, decimalType5, result, offset5, precision16, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset50, precision2, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset50, precision2, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset50, precision2, decimalType5, result, offset50, precision2, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargePositive.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision16, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision16, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision16, decimalType5, result, offset0, precision16, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision30, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision30, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision30, decimalType5, result, offset0, precision30, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision50, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision50, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision50, decimalType5, result, offset0, precision50, decimalType5, errorChecking), value, result);
        }

        /*
         * UNICODE_SIGN_EMBEDDED_LEADING
         */

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(UnicodeDecimal, (char) '0');
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargePositive.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallNegative.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargestPossible.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallestPossible.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.Zero.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision1, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision1, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision1, decimalType6, result, offset0, precision1, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallNegative.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset5, precision2, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset5, precision2, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset5, precision2, decimalType6, result, offset5, precision2, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset25, precision15, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset25, precision15, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset25, precision15, decimalType6, result, offset25, precision15, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset5, precision16, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset5, precision16, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset5, precision16, decimalType6, result, offset5, precision16, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset50, precision2, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset50, precision2, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset50, precision2, decimalType6, result, offset50, precision2, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision16, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision16, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision16, decimalType6, result, offset0, precision16, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision30, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision30, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision30, decimalType6, result, offset0, precision30, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.IntValue;
        Arrays.fill(UnicodeDecimal, (char) '0');
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision50, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision50, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision50, decimalType6, result, offset0, precision50, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        /*
         * UNICODE_SIGN_EMBEDDED_TRAILING
         */

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(UnicodeDecimal, (char) '0');
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargePositive.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallNegative.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargestPossible.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallestPossible.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.Zero.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision1, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision1, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision1, decimalType7, result, offset0, precision1, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallNegative.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset5, precision2, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset5, precision2, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset5, precision2, decimalType7, result, offset5, precision2, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset25, precision15, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset25, precision15, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset25, precision15, decimalType7, result, offset25, precision15, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset5, precision16, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset5, precision16, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset5, precision16, decimalType7, result, offset5, precision16, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset50, precision2, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset50, precision2, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset50, precision2, decimalType7, result, offset50, precision2, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision16, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision16, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision16, decimalType7, result, offset0, precision16, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.IntValue;
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision30, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision30, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision30, decimalType7, result, offset0, precision30, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.IntValue;
        Arrays.fill(UnicodeDecimal, (char) '0');
        DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision50, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision50, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision50, decimalType7, result, offset0, precision50, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        /*
         * convertIntegerToPackedDecimal
         */

        final int precision = 6;

        // value = Utils.TestValue.SmallPositive.IntValue;
        value = 12345;
        byte[] packedDecimal = new byte[64];
        DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, offset0, precision, true);

        result = DecimalData.convertPackedDecimalToInteger(packedDecimal, offset0, precision, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision, result, offset0, precision, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        /*
         * value = Utils.TestValue.LargePositive.IntValue; testName = dataName +
         * " " + Utils.TestValue.SmallPositive.TestName + " " + testType + " " +
         * typeName;
         * 
         * Arrays.fill(packedDecimal, (char)'0');
         * DecimalData.convertIntegerToPackedDecimal(value, packedDecimal,
         * offset0, precision31, errorChecking);
         * 
         * 
         * 
         * DecimalData.convertPackedDecimalToInteger(packedDecimal, offset0,
         * precision31, errorChecking);
         * 
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterPacked("", functionName, value,
         * packedDecimal, offset0, precision31, result, offset0, precision31,
         * errorChecking); assertEquals(testName, value, result);
         * 
         * Arrays.fill(packedDecimal, (byte) 0x00);
         * 
         * 
         * value = Utils.TestValue.SmallNegative.IntValue; testName = dataName +
         * " " + Utils.TestValue.SmallNegative.TestName + " " + testType + " " +
         * typeName;
         * 
         * Arrays.fill(packedDecimal, (byte) 0x00);
         * DecimalData.convertIntegerToPackedDecimal(value, packedDecimal,
         * offset0, precision31, errorChecking);
         * 
         * 
         * 
         * DecimalData.convertPackedDecimalToInteger(packedDecimal, offset0,
         * precision31, errorChecking);
         * 
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterPacked("", functionName, value,
         * packedDecimal, offset0, precision31, result, offset0, precision31,
         * errorChecking); assertEquals(testName, value, result);
         * 
         * Arrays.fill(packedDecimal, (byte) 0x00);
         * 
         * 
         * value = Utils.TestValue.LargeNegative.IntValue; testName = dataName +
         * " " + Utils.TestValue.LargeNegative.TestName + " " + testType + " " +
         * typeName;
         * 
         * Arrays.fill(packedDecimal, (byte) 0x00);
         * DecimalData.convertIntegerToPackedDecimal(value, packedDecimal,
         * offset0, precision31, errorChecking);
         * 
         * 
         * 
         * DecimalData.convertPackedDecimalToInteger(packedDecimal, offset0,
         * precision31, errorChecking);
         * 
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterPacked("", functionName, value,
         * packedDecimal, offset0, precision31, result, offset0, precision31,
         * errorChecking); assertEquals(testName, value, result);
         * 
         * Arrays.fill(packedDecimal, (byte) 0x00);
         * 
         * 
         * value = Utils.TestValue.LargestPossible.IntValue; testName = dataName
         * + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +
         * " " + typeName;
         * 
         * Arrays.fill(packedDecimal, (byte) 0x00);
         * DecimalData.convertIntegerToPackedDecimal(value, packedDecimal,
         * offset0, precision31, errorChecking);
         * 
         * 
         * 
         * DecimalData.convertPackedDecimalToInteger(packedDecimal, offset0,
         * precision31, errorChecking);
         * 
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterPacked("", functionName, value,
         * packedDecimal, offset0, precision31, result, offset0, precision31,
         * errorChecking); assertEquals(testName, value, result);
         * 
         * Arrays.fill(packedDecimal, (byte) 0x00);
         * 
         * 
         * value = Utils.TestValue.SmallestPossible.IntValue; testName =
         * dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " +
         * testType + " " + typeName;
         * 
         * Arrays.fill(packedDecimal, (byte) 0x00);
         * DecimalData.convertIntegerToPackedDecimal(value, packedDecimal,
         * offset0, precision31, errorChecking);
         * 
         * 
         * 
         * DecimalData.convertPackedDecimalToInteger(packedDecimal, offset0,
         * precision31, errorChecking);
         * 
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterPacked("", functionName, value,
         * packedDecimal, offset0, precision31, result, offset0, precision31,
         * errorChecking); assertEquals(testName, value, result);
         * 
         * Arrays.fill(packedDecimal, (byte) 0x00);
         * 
         * 
         * value = Utils.TestValue.Zero.IntValue; testName = dataName + " " +
         * Utils.TestValue.Zero.TestName + " " + testType + " " + typeName;
         * 
         * Arrays.fill(packedDecimal, (byte) 0x00);
         * DecimalData.convertIntegerToPackedDecimal(value, packedDecimal,
         * offset0, precision31, errorChecking);
         * 
         * 
         * 
         * DecimalData.convertPackedDecimalToInteger(packedDecimal, offset0,
         * precision31, errorChecking);
         * 
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterPacked("", functionName, value,
         * packedDecimal, offset0, precision31, result, offset0, precision31,
         * errorChecking); assertEquals(testName, value, result);
         * 
         * Arrays.fill(packedDecimal, (byte) 0x00);
         * 
         * 
         * testType = "Precision and Offset"; value =
         * Utils.TestValue.SmallPositive.IntValue; testName = dataName + " " +
         * Utils.TestValue.SmallPositive.TestName + " " + testType + " " +
         * typeName;
         * 
         * Arrays.fill(packedDecimal, (byte) 0x00);
         * DecimalData.convertIntegerToPackedDecimal(value, packedDecimal,
         * offset0, precision1, errorChecking);
         * 
         * 
         * 
         * DecimalData.convertPackedDecimalToInteger(packedDecimal, offset0,
         * precision1, errorChecking);
         * 
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterPacked("", functionName, value,
         * packedDecimal, offset0, precision1, result, offset0, precision1,
         * errorChecking); assertEquals(testName, value, result);
         * 
         * Arrays.fill(packedDecimal, (byte) 0x00);
         * 
         * 
         * value = Utils.TestValue.SmallNegative.IntValue; testName = dataName +
         * " " + Utils.TestValue.SmallNegative.TestName + " " + testType + " " +
         * typeName;
         * 
         * Arrays.fill(packedDecimal, (byte) 0x00);
         * DecimalData.convertIntegerToPackedDecimal(value, packedDecimal,
         * offset5, precision2, errorChecking);
         * 
         * 
         * 
         * DecimalData.convertPackedDecimalToInteger(packedDecimal, offset5,
         * precision2, errorChecking);
         * 
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterPacked("", functionName, value,
         * packedDecimal, offset5, precision2, result, offset5, precision2,
         * errorChecking); assertEquals(testName, value, result);
         * 
         * Arrays.fill(packedDecimal, (byte) 0x00);
         * 
         * 
         * value = Utils.TestValue.SmallPositive.IntValue; testName = dataName +
         * " " + Utils.TestValue.SmallPositive.TestName + " " + testType + " " +
         * typeName;
         * 
         * Arrays.fill(packedDecimal, (byte) 0x00);
         * DecimalData.convertIntegerToPackedDecimal(value, packedDecimal,
         * offset25, precision15, errorChecking);
         * 
         * 
         * 
         * DecimalData.convertPackedDecimalToInteger(packedDecimal, offset25,
         * precision15, errorChecking);
         * 
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterPacked("", functionName, value,
         * packedDecimal, offset25, precision15, result, offset25, precision15,
         * errorChecking); assertEquals(testName, value, result);
         * 
         * Arrays.fill(packedDecimal, (byte) 0x00);
         */

        /*
         * convertIntegerToUnicodeDecimal
         */

        /*
         * UNICODE_UNSIGNED
         */

        value = Utils.TestValue.SmallPositive.IntValue;
        char[] unicodeDecimal = new char[128];

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallNegative.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        // Unsigned, dont test negatives
        // testName = "Line: " +
        // Thread.currentThread().getStackTrace()[2].getLineNumber() + ", " +
        // Utils.makeTestNameConverterValue("", functionName, value,
        // unicodeDecimal, offset0, precision31, decimalType9, result, offset0,
        // precision31, decimalType9, errorChecking);
        // assertEquals(testName, value, result);

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargeNegative.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        // Unsigned, dont test negatives
        // testName = "Line: " +
        // Thread.currentThread().getStackTrace()[2].getLineNumber() + ", " +
        // Utils.makeTestNameConverterValue("", functionName, value,
        // unicodeDecimal, offset0, precision31, decimalType9, result, offset0,
        // precision31, decimalType9, errorChecking);
        // assertEquals(testName, value, result);

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargestPossible.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallestPossible.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.Zero.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision1, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision1, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision1, decimalType5, result, offset0, precision1, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset5, precision2, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset5, precision2, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset5, precision2, decimalType5, result, offset5, precision2, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset25, precision15, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset25, precision15, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset25, precision15, decimalType5, result, offset25, precision15, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset5, precision16, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset5, precision16, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset5, precision16, decimalType5, result, offset5, precision16, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset50, precision2, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset50, precision2, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset50, precision2, decimalType5, result, offset50, precision2, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision16, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision16, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision16, decimalType5, result, offset0, precision16, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision30, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision30, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision30, decimalType5, result, offset0, precision30, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision50, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision50, decimalType5, result, offset0, precision50, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        /*
         * UNICODE_SIGN_EMBEDDED_LEADING
         */

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallNegative.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargeNegative.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargestPossible.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision50, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision50, decimalType6, result, offset0, precision50, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallestPossible.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.Zero.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision1, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision1, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision1, decimalType6, result, offset0, precision1, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset5, precision2, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset5, precision2, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset5, precision2, decimalType6, result, offset5, precision2, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset25, precision15, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset25, precision15, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset25, precision15, decimalType6, result, offset25, precision15, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset5, precision16, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset5, precision16, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset5, precision16, decimalType6, result, offset5, precision16, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset50, precision2, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset50, precision2, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset50, precision2, decimalType6, result, offset50, precision2, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision16, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision16, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision16, decimalType6, result, offset0, precision16, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision30, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision30, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision30, decimalType6, result, offset0, precision30, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision50, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision50, decimalType6, result, offset0, precision50, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        /*
         * UNICODE_SIGN_EMBEDDED_TRAILING
         */

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallNegative.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargeNegative.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargestPossible.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision50, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision50, decimalType7, result, offset0, precision50, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallestPossible.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.Zero.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision1, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision1, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision1, decimalType7, result, offset0, precision1, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset5, precision2, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset5, precision2, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset5, precision2, decimalType7, result, offset5, precision2, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset25, precision15, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset25, precision15, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset25, precision15, decimalType7, result, offset25, precision15, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset5, precision16, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset5, precision16, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset5, precision16, decimalType7, result, offset5, precision16, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset50, precision2, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset50, precision2, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset50, precision2, decimalType7, result, offset50, precision2, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision16, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision16, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision16, decimalType7, result, offset0, precision16, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision30, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision30, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision30, decimalType7, result, offset0, precision30, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision50, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision50, decimalType7, result, offset0, precision50, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

    }

    @Test
    public void testConvertIntegerExceptions()
    {

        /*
         * test offset, precision out of bounds test overflow test error
         * checking test null array test invalid decimal type test invalid data
         */

        final int offset0 = 0;
        final int precision31 = 31;
        final boolean errorChecking = true;
        final boolean errorCheckingFalse = false;

        int value = Utils.TestValue.SmallPositive.IntValue;

        final int decimalType1 = DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING;
        final int decimalType2 = DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING;
        final int decimalType3 = DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING;
        final int decimalType4 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
        final int decimalType5 = DecimalData.UNICODE_UNSIGNED;
        final int decimalType6 = DecimalData.UNICODE_SIGN_SEPARATE_LEADING;
        final int decimalType7 = DecimalData.UNICODE_SIGN_SEPARATE_TRAILING;

        final int offset25 = 25;
        final int precision5 = 5;
        int result;

        value = Utils.TestValue.SmallPositive.IntValue;
        int arrSize = 49;
        byte[] longPackedDecimal = new byte[arrSize];
        byte[] externalDecimal = new byte[arrSize];
        byte[] packedDecimal = new byte[arrSize];
        try
        {
            DecimalData.convertIntegerToPackedDecimal(value, longPackedDecimal, offset0, arrSize * 2, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            DecimalData.convertPackedDecimalToInteger(longPackedDecimal, offset0, arrSize * 2, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);

        try
        {
            DecimalData.convertIntegerToPackedDecimal(value, longPackedDecimal, offset25, (arrSize - offset25) * 2, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }
        try
        {
            DecimalData.convertPackedDecimalToInteger(longPackedDecimal, offset25, (arrSize - offset25) * 2, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.SmallPositive.IntValue;
        byte[] packedDecimalNull = null;

        try
        {
            DecimalData.convertIntegerToPackedDecimal(value, packedDecimalNull, offset0, precision31, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }
        try
        {
            DecimalData.convertPackedDecimalToInteger(packedDecimalNull, offset0, precision31, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }

        value = Utils.TestValue.LargeNegative.IntValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        try
        {
            DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, offset0, String.valueOf(value).length() - 2, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }
        byte[] packedDecimalBiggerThanLong = new byte[arrSize];
        BigInteger biggerThanLong = new BigInteger("99999999999999999999");
        DecimalData.convertBigIntegerToPackedDecimal(biggerThanLong, packedDecimalBiggerThanLong, offset0, precision31, errorChecking);
        try
        {
            DecimalData.convertPackedDecimalToInteger(packedDecimalBiggerThanLong, offset0, precision31, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }
        byte[] packedDecimalSmallerThanLong = new byte[arrSize];
        BigInteger smallerThanLong = new BigInteger("-99999999999999999999");
        DecimalData.convertBigIntegerToPackedDecimal(smallerThanLong, packedDecimalSmallerThanLong, offset0, precision31, errorChecking);
        try
        {
            DecimalData.convertPackedDecimalToInteger(packedDecimalSmallerThanLong, offset0, precision31, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }

        value = Utils.TestValue.LargePositive.IntValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, offset0, precision5, errorCheckingFalse);
        byte[] correct = new byte[arrSize];
        DecimalData.convertIntegerToPackedDecimal(0, correct, offset0, precision5, errorCheckingFalse);
        try
        {
            assertArrayEquals(correct, packedDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), correct, packedDecimal);
        }

        // this is one larger than Integer.MAX_VALUE
        // when converted to packed, then packed to int
        // should overflow into Integer.MIN_VALUE
        BigInteger oneMoreThanLongRange = new BigInteger("2147483648");
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertBigIntegerToPackedDecimal(oneMoreThanLongRange, packedDecimal, offset0, precision31, errorCheckingFalse);
        result = DecimalData.convertPackedDecimalToInteger(packedDecimal, offset0, precision31, errorCheckingFalse);
        try
        {
            assertEquals(Integer.MIN_VALUE, result);
        }

        catch (AssertionError e)
        {
            assertEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), Integer.MIN_VALUE, result);
        }

        // this is one smaller than Integer.MIN_VALUE
        // when converted to packed, then packed to int
        // should underflow into Integer.MAX_VALUE
        BigInteger oneLessThanLongRange = new BigInteger("-2147483649");
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertBigIntegerToPackedDecimal(oneLessThanLongRange, packedDecimal, offset0, precision31, errorCheckingFalse);
        result = DecimalData.convertPackedDecimalToInteger(packedDecimal, offset0, precision31, errorCheckingFalse);
        try
        {
            assertEquals(Integer.MAX_VALUE, result);
        }

        catch (AssertionError e)
        {
            assertEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), Integer.MAX_VALUE, result);
        }

        /*
         * convertIntegerToExternalDecimal
         */

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(externalDecimal, (byte) 0x00);

        try
        {
            DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, arrSize + 1, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {

            DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, arrSize + 1, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(externalDecimal, (byte) 0x00);

        try
        {
            DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset25, arrSize + 1 - offset25, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            DecimalData.convertExternalDecimalToInteger(externalDecimal, offset25, arrSize + 1 - offset25, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(externalDecimal, (byte) 0x00);

        try
        {
            DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset25, arrSize - offset25, errorChecking, decimalType3);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            DecimalData.convertExternalDecimalToInteger(externalDecimal, offset25, arrSize - offset25, errorChecking, decimalType3);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.SmallPositive.IntValue;
        byte[] externalDecimalNull = null;

        try
        {
            DecimalData.convertIntegerToExternalDecimal(value, externalDecimalNull, offset0, precision31, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }

        try
        {
            DecimalData.convertExternalDecimalToInteger(externalDecimalNull, offset0, precision31, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }

        value = Utils.TestValue.LargeNegative.IntValue;
        Arrays.fill(externalDecimal, (byte) 0x00);

        try
        {
            DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, String.valueOf(value).length() - 2, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }

        biggerThanLong = new BigInteger("99999999999999999999");
        Arrays.fill(externalDecimal, (byte) 0x00);
        DecimalData.convertBigIntegerToExternalDecimal(biggerThanLong, externalDecimal, offset0, precision31, errorChecking, decimalType1);
        try
        {
            DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }

        smallerThanLong = new BigInteger("-99999999999999999999");
        Arrays.fill(externalDecimal, (byte) 0x00);
        DecimalData.convertBigIntegerToExternalDecimal(smallerThanLong, externalDecimal, offset0, precision31, errorChecking, decimalType1);
        try
        {
            DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }

        value = Utils.TestValue.LargePositive.IntValue;
        Arrays.fill(externalDecimal, (byte) 0x00);
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision5, errorCheckingFalse, decimalType1);
        Arrays.fill(correct, (byte) 0x00);
        DecimalData.convertIntegerToExternalDecimal(0, correct, offset0, precision5, errorCheckingFalse, decimalType1);
        try
        {
            assertArrayEquals(correct, externalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), correct, externalDecimal);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision5, errorCheckingFalse, decimalType2);
        Arrays.fill(correct, (byte) 0x00);
        DecimalData.convertIntegerToExternalDecimal(0, correct, offset0, precision5, errorCheckingFalse, decimalType2);
        try
        {
            assertArrayEquals(correct, externalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), correct, externalDecimal);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision5, errorCheckingFalse, decimalType3);
        Arrays.fill(correct, (byte) 0x00);
        DecimalData.convertIntegerToExternalDecimal(0, correct, offset0, precision5, errorCheckingFalse, decimalType3);
        try
        {
            assertArrayEquals(correct, externalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), correct, externalDecimal);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);
        DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision5, errorCheckingFalse, decimalType4);
        Arrays.fill(correct, (byte) 0x00);
        DecimalData.convertIntegerToExternalDecimal(0, correct, offset0, precision5, errorCheckingFalse, decimalType4);
        try
        {
            assertArrayEquals(correct, externalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), correct, externalDecimal);
        }

        // this is one larger than Integer.MAX_VALUE
        // when converted to packed, then packed to long
        // should overflow into Long.MIN_VALUE
        oneMoreThanLongRange = new BigInteger("2147483648");
        Arrays.fill(externalDecimal, (byte) 0x00);
        DecimalData.convertBigIntegerToExternalDecimal(oneMoreThanLongRange, externalDecimal, offset0, precision31, errorCheckingFalse, decimalType1);
        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorCheckingFalse, decimalType1);
        try
        {
            assertEquals(Integer.MIN_VALUE, result);
        }

        catch (AssertionError e)
        {
            assertEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), Integer.MIN_VALUE, result);
        }

        // this is one smaller than Integer.MIN_VALUE
        // when converted to packed, then packed to long
        // should underflow into Long.MAX_VALUE
        oneLessThanLongRange = new BigInteger("-2147483649");
        Arrays.fill(externalDecimal, (byte) 0x00);
        DecimalData.convertBigIntegerToExternalDecimal(oneLessThanLongRange, externalDecimal, offset0, precision31, errorCheckingFalse, decimalType1);
        result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorCheckingFalse, decimalType1);
        try
        {
            assertEquals(Integer.MAX_VALUE, result);
        }

        catch (AssertionError e)
        {
            assertEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), Integer.MAX_VALUE, result);
        }

        value = Utils.TestValue.LargeNegative.IntValue;
        Arrays.fill(externalDecimal, (byte) 0x00);

        try
        {
            DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, 50);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (IllegalArgumentException e)
        {

        }

        Arrays.fill(externalDecimal, (byte) 0x00);
        try
        {
            DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, 50);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (IllegalArgumentException e)
        {

        }

        /*
         * convertIntegerToUnicodeDecimal
         */

        value = Utils.TestValue.SmallPositive.IntValue;
        char[] unicodeDecimal = new char[arrSize];

        try
        {
            DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, arrSize, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, arrSize, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.SmallPositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);

        try
        {
            DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset25, arrSize - offset25, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset25, arrSize - offset25, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.SmallPositive.IntValue;
        char[] unicodeDecimalNull = null;

        try
        {
            DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimalNull, offset0, precision31, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }

        try
        {
            DecimalData.convertUnicodeDecimalToInteger(unicodeDecimalNull, offset0, precision31, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }

        value = Utils.TestValue.LargeNegative.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);

        try
        {
            DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, String.valueOf(value).length() - 2, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }

        biggerThanLong = new BigInteger("99999999999999999999");
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(biggerThanLong, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
        try
        {
            DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }
        smallerThanLong = new BigInteger("-99999999999999999999");
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(smallerThanLong, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
        try
        {
            DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }

        value = Utils.TestValue.LargePositive.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision5, errorCheckingFalse, decimalType7);
        char[] correctUnicode = new char[arrSize];
        DecimalData.convertIntegerToUnicodeDecimal(0, correctUnicode, offset0, precision5, errorCheckingFalse, decimalType7);
        try
        {
            assertArrayEquals(correctUnicode, unicodeDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), correctUnicode, unicodeDecimal);
        }

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision5, errorCheckingFalse, decimalType6);
        Arrays.fill(correctUnicode, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(0, correctUnicode, offset0, precision5, errorCheckingFalse, decimalType6);
        try
        {
            assertArrayEquals(correctUnicode, unicodeDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), correctUnicode, unicodeDecimal);
        }

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision5, errorCheckingFalse, decimalType5);
        Arrays.fill(correctUnicode, (char) 0);
        DecimalData.convertIntegerToUnicodeDecimal(0, correctUnicode, offset0, precision5, errorCheckingFalse, decimalType5);
        try
        {
            assertArrayEquals(correctUnicode, unicodeDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), correctUnicode, unicodeDecimal);
        }

        // this is one larger than Long.MAX_VALUE
        // when converted to packed, then packed to long
        // should overflow into Long.MIN_VALUE
        oneMoreThanLongRange = new BigInteger("2147483648");
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(oneMoreThanLongRange, unicodeDecimal, offset0, precision31, errorCheckingFalse, decimalType5);
        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorCheckingFalse, decimalType5);
        try
        {
            assertEquals(Integer.MIN_VALUE, result);
        }

        catch (AssertionError e)
        {
            assertEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), Integer.MIN_VALUE, result);
        }

        // this is one smaller than Long.MIN_VALUE
        // when converted to packed, then packed to long
        // should convert into Long.MIN_VALUE+1
        oneLessThanLongRange = new BigInteger("-2147483649");
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(oneLessThanLongRange, unicodeDecimal, offset0, precision31, errorCheckingFalse, decimalType5);
        result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorCheckingFalse, decimalType5);
        try
        {
            assertEquals(Integer.MIN_VALUE + 1, result);
        }

        catch (AssertionError e)
        {
            assertEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), Integer.MIN_VALUE + 1, result);
        }

        value = Utils.TestValue.LargeNegative.IntValue;
        Arrays.fill(unicodeDecimal, (char) 0);

        try
        {
            DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (IllegalArgumentException e)
        {

        }

        try
        {
            DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (IllegalArgumentException e)
        {

        }

    }

    @Test
    public void testConvertLongExceptions()
    {

        /*
         * test offset, precision out of bounds test overflow test error
         * checking test null array test invalid decimal type test invalid data
         */

        final int offset0 = 0;
        final int precision31 = 31;
        final boolean errorChecking = true;
        final boolean errorCheckingFalse = false;

        long value = Utils.TestValue.SmallPositive.LongValue;

        final int decimalType1 = DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING;
        final int decimalType2 = DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING;
        final int decimalType3 = DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING;
        final int decimalType4 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
        final int decimalType5 = DecimalData.UNICODE_UNSIGNED;
        final int decimalType6 = DecimalData.UNICODE_SIGN_SEPARATE_LEADING;
        final int decimalType7 = DecimalData.UNICODE_SIGN_SEPARATE_TRAILING;

        final int offset25 = 25;
        final int precision5 = 5;
        long result;

        value = Utils.TestValue.SmallPositive.LongValue;
        int arrSize = 49;
        byte[] longPackedDecimal = new byte[arrSize];
        byte[] externalDecimal = new byte[arrSize];
        byte[] packedDecimal = new byte[arrSize];
        try
        {
            DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, arrSize * 2, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            DecimalData.convertPackedDecimalToLong(longPackedDecimal, offset0, arrSize * 2, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);

        try
        {
            DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset25, (arrSize - offset25) * 2, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }
        try
        {
            DecimalData.convertPackedDecimalToLong(longPackedDecimal, offset25, (arrSize - offset25) * 2, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.SmallPositive.LongValue;
        byte[] packedDecimalNull = null;

        try
        {
            DecimalData.convertLongToPackedDecimal(value, packedDecimalNull, offset0, precision31, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }
        try
        {
            DecimalData.convertPackedDecimalToLong(packedDecimalNull, offset0, precision31, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }

        value = Utils.TestValue.LargeNegative.LongValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        try
        {
            DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, String.valueOf(value).length() - 2, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }
        byte[] packedDecimalBiggerThanLong = new byte[arrSize];
        BigInteger biggerThanLong = new BigInteger("99999999999999999999");
        DecimalData.convertBigIntegerToPackedDecimal(biggerThanLong, packedDecimalBiggerThanLong, offset0, precision31, errorChecking);
        try
        {
            DecimalData.convertPackedDecimalToLong(packedDecimalBiggerThanLong, offset0, precision31, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }
        byte[] packedDecimalSmallerThanLong = new byte[arrSize];
        BigInteger smallerThanLong = new BigInteger("-99999999999999999999");
        DecimalData.convertBigIntegerToPackedDecimal(smallerThanLong, packedDecimalSmallerThanLong, offset0, precision31, errorChecking);
        try
        {
            DecimalData.convertPackedDecimalToLong(packedDecimalSmallerThanLong, offset0, precision31, errorChecking);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision5, errorCheckingFalse);
        byte[] correct = new byte[arrSize];
        DecimalData.convertLongToPackedDecimal(0, correct, offset0, precision5, errorCheckingFalse);
        try
        {
            assertArrayEquals(correct, packedDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), correct, packedDecimal);
        }

        // this is one larger than Long.MAX_VALUE
        // when converted to packed, then packed to long
        // should overflow into Long.MIN_VALUE
        BigInteger oneMoreThanLongRange = new BigInteger("9223372036854775808");
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertBigIntegerToPackedDecimal(oneMoreThanLongRange, packedDecimal, offset0, precision31, errorCheckingFalse);
        result = DecimalData.convertPackedDecimalToLong(packedDecimal, offset0, precision31, errorCheckingFalse);
        try
        {
            assertEquals(Long.MIN_VALUE, result);
        }

        catch (AssertionError e)
        {
            assertEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), Long.MIN_VALUE, result);
        }

        // this is one smaller than Long.MIN_VALUE
        // when converted to packed, then packed to long
        // should underflow into Long.MAX_VALUE
        BigInteger oneLessThanLongRange = new BigInteger("-9223372036854775809");
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertBigIntegerToPackedDecimal(oneLessThanLongRange, packedDecimal, offset0, precision31, errorCheckingFalse);
        result = DecimalData.convertPackedDecimalToLong(packedDecimal, offset0, precision31, errorCheckingFalse);
        try
        {
            assertEquals(Long.MAX_VALUE, result);
        }

        catch (AssertionError e)
        {
            assertEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), Long.MAX_VALUE, result);
        }

        /*
         * convertLongToExternalDecimal
         */

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(externalDecimal, (byte) 0x00);

        try
        {
            DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, arrSize + 1, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {

            DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, arrSize + 1, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(externalDecimal, (byte) 0x00);

        try
        {
            DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset25, arrSize + 1 - offset25, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            DecimalData.convertExternalDecimalToLong(externalDecimal, offset25, arrSize + 1 - offset25, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(externalDecimal, (byte) 0x00);

        try
        {
            DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset25, arrSize - offset25, errorChecking, decimalType3);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            DecimalData.convertExternalDecimalToLong(externalDecimal, offset25, arrSize - offset25, errorChecking, decimalType3);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.SmallPositive.LongValue;
        byte[] externalDecimalNull = null;

        try
        {
            DecimalData.convertLongToExternalDecimal(value, externalDecimalNull, offset0, precision31, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }

        try
        {
            DecimalData.convertExternalDecimalToLong(externalDecimalNull, offset0, precision31, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }

        value = Utils.TestValue.LargeNegative.LongValue;
        Arrays.fill(externalDecimal, (byte) 0x00);

        try
        {
            DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, String.valueOf(value).length() - 2, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }

        biggerThanLong = new BigInteger("99999999999999999999");
        Arrays.fill(externalDecimal, (byte) 0x00);
        DecimalData.convertBigIntegerToExternalDecimal(biggerThanLong, externalDecimal, offset0, precision31, errorChecking, decimalType1);
        try
        {
            DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }
        smallerThanLong = new BigInteger("-99999999999999999999");
        Arrays.fill(externalDecimal, (byte) 0x00);
        DecimalData.convertBigIntegerToExternalDecimal(smallerThanLong, externalDecimal, offset0, precision31, errorChecking, decimalType1);
        try
        {
            DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(externalDecimal, (byte) 0x00);
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision5, errorCheckingFalse, decimalType1);
        Arrays.fill(correct, (byte) 0x00);
        DecimalData.convertLongToExternalDecimal(0, correct, offset0, precision5, errorCheckingFalse, decimalType1);
        try
        {
            assertArrayEquals(correct, externalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), correct, externalDecimal);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision5, errorCheckingFalse, decimalType2);
        Arrays.fill(correct, (byte) 0x00);
        DecimalData.convertLongToExternalDecimal(0, correct, offset0, precision5, errorCheckingFalse, decimalType2);
        try
        {
            assertArrayEquals(correct, externalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), correct, externalDecimal);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision5, errorCheckingFalse, decimalType3);
        Arrays.fill(correct, (byte) 0x00);
        DecimalData.convertLongToExternalDecimal(0, correct, offset0, precision5, errorCheckingFalse, decimalType3);
        try
        {
            assertArrayEquals(correct, externalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), correct, externalDecimal);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision5, errorCheckingFalse, decimalType4);
        Arrays.fill(correct, (byte) 0x00);
        DecimalData.convertLongToExternalDecimal(0, correct, offset0, precision5, errorCheckingFalse, decimalType4);
        try
        {
            assertArrayEquals(correct, externalDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), correct, externalDecimal);
        }

        // this is one larger than Long.MAX_VALUE
        // when converted to packed, then packed to long
        // should overflow into Long.MIN_VALUE
        oneMoreThanLongRange = new BigInteger("9223372036854775808");
        Arrays.fill(externalDecimal, (byte) 0x00);
        DecimalData.convertBigIntegerToExternalDecimal(oneMoreThanLongRange, externalDecimal, offset0, precision31, errorCheckingFalse, decimalType1);
        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorCheckingFalse, decimalType1);
        try
        {
            assertEquals(Long.MIN_VALUE, result);
        }

        catch (AssertionError e)
        {
            assertEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), Long.MIN_VALUE, result);
        }

        // this is one smaller than Long.MIN_VALUE
        // when converted to packed, then packed to long
        // should underflow into Long.MAX_VALUE
        oneLessThanLongRange = new BigInteger("-9223372036854775809");
        Arrays.fill(externalDecimal, (byte) 0x00);
        DecimalData.convertBigIntegerToExternalDecimal(oneLessThanLongRange, externalDecimal, offset0, precision31, errorCheckingFalse, decimalType1);
        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorCheckingFalse, decimalType1);
        try
        {
            assertEquals(Long.MAX_VALUE, result);
        }

        catch (AssertionError e)
        {
            assertEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), Long.MAX_VALUE, result);
        }

        value = Utils.TestValue.LargeNegative.LongValue;
        Arrays.fill(externalDecimal, (byte) 0x00);

        try
        {
            DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, 50);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (IllegalArgumentException e)
        {

        }

        Arrays.fill(externalDecimal, (byte) 0x00);
        try
        {
            DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, 50);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (IllegalArgumentException e)
        {

        }

        /*
         * convertLongToUnicodeDecimal
         */

        value = Utils.TestValue.SmallPositive.LongValue;
        char[] unicodeDecimal = new char[arrSize];

        try
        {
            DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, arrSize, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, arrSize, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);

        try
        {
            DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset25, arrSize - offset25, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset25, arrSize - offset25, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);

        try
        {
            DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset25, arrSize + 1 - offset25, errorChecking, decimalType5);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset25, arrSize + 1 - offset25, errorChecking, decimalType5);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.SmallPositive.LongValue;
        char[] unicodeDecimalNull = null;

        try
        {
            DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimalNull, offset0, precision31, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }

        try
        {
            DecimalData.convertUnicodeDecimalToLong(unicodeDecimalNull, offset0, precision31, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }

        value = Utils.TestValue.LargeNegative.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);

        try
        {
            DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, String.valueOf(value).length() - 2, errorChecking, decimalType6);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }

        biggerThanLong = new BigInteger("99999999999999999999");
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(biggerThanLong, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
        try
        {
            DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }

        smallerThanLong = new BigInteger("-99999999999999999999");
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(smallerThanLong, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
        try
        {
            DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision5, errorCheckingFalse, decimalType7);
        char[] correctUnicode = new char[arrSize];
        DecimalData.convertLongToUnicodeDecimal(0, correctUnicode, offset0, precision5, errorCheckingFalse, decimalType7);
        try
        {
            assertArrayEquals(correctUnicode, unicodeDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), correctUnicode, unicodeDecimal);
        }

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision5, errorCheckingFalse, decimalType6);
        Arrays.fill(correctUnicode, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(0, correctUnicode, offset0, precision5, errorCheckingFalse, decimalType6);
        try
        {
            assertArrayEquals(correctUnicode, unicodeDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), correctUnicode, unicodeDecimal);
        }

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision5, errorCheckingFalse, decimalType5);
        Arrays.fill(correctUnicode, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(0, correctUnicode, offset0, precision5, errorCheckingFalse, decimalType5);
        try
        {
            assertArrayEquals(correctUnicode, unicodeDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), correctUnicode, unicodeDecimal);
        }

        // this is one larger than Long.MAX_VALUE
        // when converted to packed, then packed to long
        // should overflow into Long.MIN_VALUE
        oneMoreThanLongRange = new BigInteger("9223372036854775808");
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(oneMoreThanLongRange, unicodeDecimal, offset0, precision31, errorCheckingFalse, decimalType5);
        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorCheckingFalse, decimalType5);
        try
        {
            assertEquals(Long.MIN_VALUE, result);
        }

        catch (AssertionError e)
        {
            assertEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), Long.MIN_VALUE, result);
        }

        // this is one smaller than Long.MIN_VALUE
        // when converted to packed, then packed to long
        // should convert into Long.MIN_VALUE+1
        oneLessThanLongRange = new BigInteger("-9223372036854775809");
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertBigIntegerToUnicodeDecimal(oneLessThanLongRange, unicodeDecimal, offset0, precision31, errorCheckingFalse, decimalType5);
        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorCheckingFalse, decimalType5);
        try
        {
            assertEquals(Long.MIN_VALUE + 1, result);
        }

        catch (AssertionError e)
        {
            assertEquals("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber(), Long.MIN_VALUE + 1, result);
        }

        value = Utils.TestValue.LargeNegative.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);

        try
        {
            DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (IllegalArgumentException e)
        {

        }

        try
        {
            DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType1);
            fail("Assertion in TestDecimalData on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (IllegalArgumentException e)
        {

        }

    }

    @Test
    public void testConvertLongNormals()
    {

        /*
         * this function will test convertLong to xxxx convertFromxxx back to
         * long, and assert both equals
         * 
         * the values to test will be small positive, large positive, largest
         * possible, smallest possible, small negative, large negative, zero do
         * two tests for offset/precision test every single decimal type
         */
        byte[] externalDecimal = new byte[64];

        final int offset0 = 0;
        final int offset5 = 5;
        final int offset25 = 25;
        final int offset50 = 50;
        final int precision1 = 1;
        final int precision2 = 2;
        final int precision15 = 15;
        final int precision16 = 16;
        final int precision30 = 30;
        final int precision31 = 31;
        final int precision50 = 50;
        final boolean errorChecking = false;
        final int decimalType1 = DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING;
        final int decimalType2 = DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING;
        final int decimalType3 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
        final int decimalType4 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
        final int decimalType5 = DecimalData.UNICODE_UNSIGNED;
        final int decimalType6 = DecimalData.UNICODE_SIGN_SEPARATE_LEADING;
        final int decimalType7 = DecimalData.UNICODE_SIGN_SEPARATE_TRAILING;

        long value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        long result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargePositive.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargestPossible.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallestPossible.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.Zero.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType1, result, offset0, precision31, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision1, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision1, decimalType1, result, offset0, precision1, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset5, precision2, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset5, precision2, decimalType1, result, offset5, precision2, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset25, precision15, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset25, precision15, decimalType1, result, offset25, precision15, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset5, precision16, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset5, precision16, decimalType1, result, offset5, precision16, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset50, precision2, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset50, precision2, decimalType1, result, offset50, precision2, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision16, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision16, decimalType1, result, offset0, precision16, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision30, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision30, decimalType1, result, offset0, precision30, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType1);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision50, errorChecking, decimalType1);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision50, decimalType1, result, offset0, precision50, decimalType1, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        /*
         * EBCDIC_SIGN_EMBEDDED_LEADING
         */

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargePositive.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargestPossible.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallestPossible.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.Zero.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType2, result, offset0, precision31, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision1, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision1, decimalType2, result, offset0, precision1, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset5, precision2, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset5, precision2, decimalType2, result, offset5, precision2, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset25, precision15, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset25, precision15, decimalType2, result, offset25, precision15, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset5, precision16, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset5, precision16, decimalType2, result, offset5, precision16, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset50, precision2, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset50, precision2, decimalType2, result, offset50, precision2, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision16, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision16, decimalType2, result, offset0, precision16, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision30, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision30, decimalType2, result, offset0, precision30, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType2);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision50, errorChecking, decimalType2);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision50, decimalType2, result, offset0, precision50, decimalType2, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        /*
         * EBCDIC_SIGN_SEPARATE_TRAILING
         */

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargePositive.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargestPossible.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallestPossible.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.Zero.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType3, result, offset0, precision31, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision1, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision1, decimalType3, result, offset0, precision1, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset5, precision2, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset5, precision2, decimalType3, result, offset5, precision2, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset25, precision15, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset25, precision15, decimalType3, result, offset25, precision15, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset5, precision16, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset5, precision16, decimalType3, result, offset5, precision16, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset50, precision2, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset50, precision2, decimalType3, result, offset50, precision2, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision16, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision16, decimalType3, result, offset0, precision16, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision30, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision30, decimalType3, result, offset0, precision30, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType3);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision50, errorChecking, decimalType3);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision50, decimalType3, result, offset0, precision50, decimalType3, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        /*
         * EBCDIC_SIGN_SEPARATE_LEADING
         */

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargePositive.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargestPossible.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallestPossible.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.Zero.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision31, decimalType4, result, offset0, precision31, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision1, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision1, decimalType4, result, offset0, precision1, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset5, precision2, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset5, precision2, decimalType4, result, offset5, precision2, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset25, precision15, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset25, precision15, decimalType4, result, offset25, precision15, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset5, precision16, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset5, precision16, decimalType4, result, offset5, precision16, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset50, precision2, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset50, precision2, decimalType4, result, offset50, precision2, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision16, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision16, decimalType4, result, offset0, precision16, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision30, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision30, decimalType4, result, offset0, precision30, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.LongValue;
        DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType4);

        result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision50, errorChecking, decimalType4);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, externalDecimal, offset0, precision50, decimalType4, result, offset0, precision50, decimalType4, errorChecking), value, result);
        }

        Arrays.fill(externalDecimal, (byte) 0x00);

        /*
         * UNICODE_UNSIGNED
         */

        char[] UnicodeDecimal = new char[100];

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargePositive.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallNegative.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        // Unsigned, dont test negatives
        // testName = "Line: " +
        // Thread.currentThread().getStackTrace()[2].getLineNumber() + ", " +
        // Utils.makeTestNameConverterValue("", functionName, value,
        // UnicodeDecimal, offset0, precision31, decimalType9, result, offset0,
        // precision31, decimalType9, errorChecking);
        // assertEquals(testName, value, result);

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        // Unsigned, dont test negatives
        // testName = "Line: " +
        // Thread.currentThread().getStackTrace()[2].getLineNumber() + ", " +
        // Utils.makeTestNameConverterValue("", functionName, value,
        // UnicodeDecimal, offset0, precision31, decimalType9, result, offset0,
        // precision31, decimalType9, errorChecking);
        // assertEquals(testName, value, result);

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargestPossible.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallestPossible.LongValue;

        // dont test, negative unsigned
        /*
         * DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal,
         * offset0, precision31, errorChecking, decimalType9);
         * 
         * 
         * 
         * DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0,
         * precision31, errorChecking, decimalType9);
         * 
         * 
         * 
         * + Thread.currentThread().getStackTrace()[2].getLineNumber() + ", " +
         * Utils.makeTestNameConverterValue("", functionName, value,
         * UnicodeDecimal, offset0, precision31, decimalType9, result, offset0,
         * precision31, decimalType9, errorChecking); assertEquals(testName,
         * value, result);
         */

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.Zero.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision1, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision1, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision1, decimalType5, result, offset0, precision1, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset5, precision2, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset5, precision2, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset5, precision2, decimalType5, result, offset5, precision2, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset25, precision15, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset25, precision15, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset25, precision15, decimalType5, result, offset25, precision15, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargePositive.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset5, precision16, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset5, precision16, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset5, precision16, decimalType5, result, offset5, precision16, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset50, precision2, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset50, precision2, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset50, precision2, decimalType5, result, offset50, precision2, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargePositive.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision16, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision16, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision16, decimalType5, result, offset0, precision16, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision30, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision30, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision30, decimalType5, result, offset0, precision30, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(UnicodeDecimal, (char) '0');
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision50, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision50, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision50, decimalType5, result, offset0, precision50, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        /*
         * UNICODE_SIGN_EMBEDDED_LEADING
         */

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargePositive.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallNegative.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargestPossible.LongValue;

        /*
         * DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal,
         * offset0, precision31, errorChecking, decimalType10);
         * 
         * 
         * 
         * DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0,
         * precision31, errorChecking, decimalType10);
         * 
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterValue("", functionName, value,
         * UnicodeDecimal, offset0, precision31, decimalType10, result, offset0,
         * precision31, decimalType10, errorChecking); assertEquals(testName,
         * value, result);
         */

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallestPossible.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.Zero.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision1, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision1, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision1, decimalType6, result, offset0, precision1, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallNegative.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset5, precision2, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset5, precision2, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset5, precision2, decimalType6, result, offset5, precision2, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset25, precision15, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset25, precision15, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset25, precision15, decimalType6, result, offset25, precision15, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset5, precision16, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset5, precision16, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset5, precision16, decimalType6, result, offset5, precision16, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset50, precision2, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset50, precision2, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset50, precision2, decimalType6, result, offset50, precision2, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision16, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision16, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision16, decimalType6, result, offset0, precision16, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision30, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision30, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision30, decimalType6, result, offset0, precision30, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.LongValue;

        /*
         * DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal,
         * offset0, precision50, errorChecking, decimalType10);
         * 
         * 
         * 
         * DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0,
         * precision50, errorChecking, decimalType10);
         * 
         * 
         * 
         * "Line: " + Thread.currentThread().getStackTrace()[2].getLineNumber()
         * + ", " + Utils.makeTestNameConverterValue("", functionName, value,
         * UnicodeDecimal, offset0, precision50, decimalType10, result, offset0,
         * precision50, decimalType10, errorChecking); assertEquals(testName,
         * value, result);
         */

        Arrays.fill(UnicodeDecimal, (char) '0');

        /*
         * UNICODE_SIGN_EMBEDDED_TRAILING
         */

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(UnicodeDecimal, (char) '0');
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargePositive.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallNegative.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargestPossible.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallestPossible.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.Zero.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision1, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision1, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision1, decimalType7, result, offset0, precision1, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallNegative.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset5, precision2, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset5, precision2, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset5, precision2, decimalType7, result, offset5, precision2, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset25, precision15, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset25, precision15, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset25, precision15, decimalType7, result, offset25, precision15, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset5, precision16, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset5, precision16, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset5, precision16, decimalType7, result, offset5, precision16, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset50, precision2, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset50, precision2, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset50, precision2, decimalType7, result, offset50, precision2, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision16, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision16, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision16, decimalType7, result, offset0, precision16, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.SmallPositive.LongValue;
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision30, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision30, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision30, decimalType7, result, offset0, precision30, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        value = Utils.TestValue.LargeNegative.LongValue;
        Arrays.fill(UnicodeDecimal, (char) '0');
        DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision50, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision50, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, UnicodeDecimal, offset0, precision50, decimalType7, result, offset0, precision50, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(UnicodeDecimal, (char) '0');

        /*
         * convertLongToPackedDecimal
         */

        value = Utils.TestValue.SmallPositive.LongValue;
        byte[] packedDecimal = new byte[64];
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        result = DecimalData.convertPackedDecimalToLong(packedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, result, offset0, precision31, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        result = DecimalData.convertPackedDecimalToLong(packedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, result, offset0, precision31, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.LongValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        result = DecimalData.convertPackedDecimalToLong(packedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, result, offset0, precision31, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.LargeNegative.LongValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        result = DecimalData.convertPackedDecimalToLong(packedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, result, offset0, precision31, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.LargestPossible.LongValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        result = DecimalData.convertPackedDecimalToLong(packedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, result, offset0, precision31, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallestPossible.LongValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        result = DecimalData.convertPackedDecimalToLong(packedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, result, offset0, precision31, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.Zero.LongValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);

        result = DecimalData.convertPackedDecimalToLong(packedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision31, result, offset0, precision31, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision1, errorChecking);

        result = DecimalData.convertPackedDecimalToLong(packedDecimal, offset0, precision1, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset0, precision1, result, offset0, precision1, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallNegative.LongValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset5, precision2, errorChecking);

        result = DecimalData.convertPackedDecimalToLong(packedDecimal, offset5, precision2, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset5, precision2, result, offset5, precision2, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset25, precision15, errorChecking);

        result = DecimalData.convertPackedDecimalToLong(packedDecimal, offset25, precision15, errorChecking);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterPacked("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, packedDecimal, offset25, precision15, result, offset25, precision15, errorChecking), value, result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);

        /*
         * convertLongToUnicodeDecimal
         */

        /*
         * UNICODE_UNSIGNED
         */

        value = Utils.TestValue.SmallPositive.LongValue;
        char[] unicodeDecimal = new char[128];

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallNegative.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        // Unsigned, dont test negatives
        // testName = "Line: " +
        // Thread.currentThread().getStackTrace()[2].getLineNumber() + ", " +
        // Utils.makeTestNameConverterValue("", functionName, value,
        // unicodeDecimal, offset0, precision31, decimalType9, result, offset0,
        // precision31, decimalType9, errorChecking);
        // assertEquals(testName, value, result);

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargeNegative.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        // Unsigned, dont test negatives
        // testName = "Line: " +
        // Thread.currentThread().getStackTrace()[2].getLineNumber() + ", " +
        // Utils.makeTestNameConverterValue("", functionName, value,
        // unicodeDecimal, offset0, precision31, decimalType9, result, offset0,
        // precision31, decimalType9, errorChecking);
        // assertEquals(testName, value, result);

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargestPossible.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallestPossible.LongValue;

        // dont test, negative unsigned
        /*
         * Arrays.fill(unicodeDecimal, (char)0);
         * DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal,
         * offset0, precision31, errorChecking, decimalType9);
         * 
         * 
         * 
         * DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0,
         * precision31, errorChecking, decimalType9);
         * 
         * 
         * 
         * + Thread.currentThread().getStackTrace()[2].getLineNumber() + ", " +
         * Utils.makeTestNameConverterValue("", functionName, value,
         * unicodeDecimal, offset0, precision31, decimalType9, result, offset0,
         * precision31, decimalType9, errorChecking); assertEquals(testName,
         * value, result);
         */

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.Zero.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType5, result, offset0, precision31, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision1, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision1, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision1, decimalType5, result, offset0, precision1, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset5, precision2, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset5, precision2, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset5, precision2, decimalType5, result, offset5, precision2, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset25, precision15, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset25, precision15, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset25, precision15, decimalType5, result, offset25, precision15, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset5, precision16, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset5, precision16, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset5, precision16, decimalType5, result, offset5, precision16, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset50, precision2, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset50, precision2, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset50, precision2, decimalType5, result, offset50, precision2, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision16, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision16, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision16, decimalType5, result, offset0, precision16, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision30, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision30, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision30, decimalType5, result, offset0, precision30, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType5);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision50, errorChecking, decimalType5);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision50, decimalType5, result, offset0, precision50, decimalType5, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        /*
         * UNICODE_SIGN_EMBEDDED_LEADING
         */

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallNegative.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargeNegative.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargestPossible.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision50, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision50, decimalType6, result, offset0, precision50, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallestPossible.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.Zero.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType6, result, offset0, precision31, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision1, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision1, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision1, decimalType6, result, offset0, precision1, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset5, precision2, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset5, precision2, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset5, precision2, decimalType6, result, offset5, precision2, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset25, precision15, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset25, precision15, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset25, precision15, decimalType6, result, offset25, precision15, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset5, precision16, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset5, precision16, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset5, precision16, decimalType6, result, offset5, precision16, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset50, precision2, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset50, precision2, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset50, precision2, decimalType6, result, offset50, precision2, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision16, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision16, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision16, decimalType6, result, offset0, precision16, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision30, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision30, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision30, decimalType6, result, offset0, precision30, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType6);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision50, errorChecking, decimalType6);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision50, decimalType6, result, offset0, precision50, decimalType6, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        /*
         * UNICODE_SIGN_EMBEDDED_TRAILING
         */

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallNegative.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargeNegative.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargestPossible.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision50, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision50, decimalType7, result, offset0, precision50, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallestPossible.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.Zero.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision31, decimalType7, result, offset0, precision31, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision1, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision1, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision1, decimalType7, result, offset0, precision1, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset5, precision2, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset5, precision2, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset5, precision2, decimalType7, result, offset5, precision2, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset25, precision15, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset25, precision15, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset25, precision15, decimalType7, result, offset25, precision15, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset5, precision16, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset5, precision16, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset5, precision16, decimalType7, result, offset5, precision16, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset50, precision2, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset50, precision2, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset50, precision2, decimalType7, result, offset50, precision2, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision16, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision16, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision16, decimalType7, result, offset0, precision16, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision30, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision30, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision30, decimalType7, result, offset0, precision30, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);

        value = Utils.TestValue.SmallPositive.LongValue;

        Arrays.fill(unicodeDecimal, (char) 0);
        DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType7);

        result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision50, errorChecking, decimalType7);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameConverterValue("", "testConvertLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, unicodeDecimal, offset0, precision50, decimalType7, result, offset0, precision50, decimalType7, errorChecking), value, result);
        }

        Arrays.fill(unicodeDecimal, (char) 0);
    }
}
