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
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*******************************************************************************/

package net.openj9.test.decimals;

import static org.junit.Assert.*;

import java.math.BigInteger;

import java.util.Arrays;

import org.junit.Test;

import com.ibm.dataaccess.DecimalData;

public class TestPD2UD extends DecimalTestBase
{
    char[] udString = new char[100];
    byte[] pdString = new byte[100];

    @Test
    public void TestZeros()
    {

        for (int offset = 0; offset < 10; ++offset)
        {
            Arrays.fill(udString, (char) 0x00);
            Arrays.fill(pdString, (byte) 0x00);

            for (int decimalType = 0; decimalType < types.length; ++decimalType)
            {
                for (int j = 0x0A; j < 0x10; ++j)
                {
                    pdString[offset] = (byte) j;

                    DecimalData.convertPackedDecimalToUnicodeDecimal(pdString, offset, udString, offset, 1, types[decimalType]);
                    verify(udString, offset, pdString, offset, 1, types[decimalType]);
                }
            }
        }
    }

    @Test
    public void illegalArgumentTests()
    {
        // precision
        boolean catched = false;

        try
        {
            DecimalData.convertPackedDecimalToUnicodeDecimal(pdString, 0, udString, 0, 0, 0);
        }

        catch (IllegalArgumentException e)
        {
            catched = true;
        }

        if (!catched)
            fail();

        catched = false;

        try
        {
            DecimalData.convertPackedDecimalToUnicodeDecimal(pdString, 0, udString, 0, 1, 8);
        }

        catch (IllegalArgumentException e)
        {
            catched = true;
        }

        if (!catched)
            fail();

        catched = false;

        try
        {
            DecimalData.convertPackedDecimalToUnicodeDecimal(pdString, 0, udString, 0, 1, 12);
        }

        catch (IllegalArgumentException e)
        {
            catched = true;
        }

        if (!catched)
            fail();
    }

    @Test
    public void randomTests()
    {
        for (int pdOffset = 0; pdOffset < 5; ++pdOffset)
            for (int udOffset = 0; udOffset < 5; ++udOffset)
                for (int prec = 1; prec < 10; ++prec)
                    randomTestHelper(pdOffset, udOffset, prec);

        for (int prec = 1; prec < 50; ++prec)
            randomTestHelper(0, 0, prec);
    }

    public static void randomTestHelper(int pdOffset, int udOffset, int precision)
    {
        BigInteger bi;

        byte[] pdValue;
        char[] udValue;

        for (int i = 0; i < types.length; ++i)
        {
            bi = net.openj9.test.Utils.getRandomBigInteger(precision);

            pdValue = net.openj9.test.Utils.getRandomInput(pdOffset, precision, bi);
            udValue = new char[precision * 2 + 2 + udOffset];

            com.ibm.dataaccess.DecimalData.convertPackedDecimalToUnicodeDecimal(pdValue, pdOffset, udValue, udOffset, precision, types[i]);
            verify(udValue, udOffset, pdValue, pdOffset, precision, types[i]);
        }

    }

    @Test
    public void testSmallValues()
    {
        byte[] pdValue = new byte[1];
        char[] udValue = new char[4];

        for (int i = 0; i < types.length; ++i)
        {
            for (int j = 0; j < 10; j++)
            {
                pdValue[0] = (byte) (j * 10 | 0x0C);

                DecimalData.convertPackedDecimalToUnicodeDecimal(pdValue, 0, udValue, 0, 1, types[i]);
                verify(udValue, 0, pdValue, 0, 1, types[i]);
            }
        }
    }

    @Test
    public void testConvert1234Unsigned()
    {
        // Convert 1234 from PD to ED
        final byte[] positive1234PD = { (byte) 0x01, (byte) 0x23, (byte) 0x4C };
        final byte[] unsigned1234PD = { (byte) 0x01, (byte) 0x23, (byte) 0x4F };
        final byte[] negative1234PD = { (byte) 0x01, (byte) 0x23, (byte) 0x4D };

        char[] result1234UD = new char[4];

        // Unsigned,
        // +1234 => 0031 0032 0033 0034
        char[] expected1234UD = { (char) 0x0031, (char) 0x0032, (char) 0x0033, (char) 0x0034 };
        DecimalData.convertPackedDecimalToUnicodeDecimal(positive1234PD, 0, result1234UD, 0, 4, DecimalData.UNICODE_UNSIGNED);

        assertArrayEquals("Failed to convert +1234: UNICODE_UNSIGNED", result1234UD, expected1234UD);

        DecimalData.convertPackedDecimalToUnicodeDecimal(unsigned1234PD, 0, result1234UD, 0, 4, DecimalData.UNICODE_UNSIGNED);

        assertArrayEquals("Failed to convert 1234: UNICODE_UNSIGNED", result1234UD, expected1234UD);

        DecimalData.convertIntegerToUnicodeDecimal(1234, result1234UD, 0, 4, false, DecimalData.UNICODE_UNSIGNED);

        assertArrayEquals("Failed to convert int 1234: UNICODE_UNSIGNED", result1234UD, expected1234UD);

        DecimalData.convertPackedDecimalToUnicodeDecimal(negative1234PD, 0, result1234UD, 0, 4, DecimalData.UNICODE_UNSIGNED);

        assertArrayEquals("Failed to convert +1234: UNICODE_UNSIGNED", result1234UD, expected1234UD);

        DecimalData.convertIntegerToUnicodeDecimal(-1234, result1234UD, 0, 4, false, DecimalData.UNICODE_UNSIGNED);

        assertArrayEquals("Failed to convert int -1234: UNICODE_UNSIGNED", result1234UD, expected1234UD);
    }
    
    @Test
    public void testConvert1234SeparateLeading()
    {
        // Convert 1234 from PD to ED
        final byte[] positive1234PD = { (byte) 0x01, (byte) 0x23, (byte) 0x4C };
        final byte[] unsigned1234PD = { (byte) 0x01, (byte) 0x23, (byte) 0x4F };
        final byte[] negative1234PD = { (byte) 0x01, (byte) 0x23, (byte) 0x4D };

        char[] result1234UD = new char[5];

        // Separate Leading,
        // +1234 => 002B 0031 0032 0033 0034
        char[] expected1234UD = new char[] { (char) 0x002B, (char) 0x0031, (char) 0x0032, (char) 0x0033, (char) 0x0034 };
        DecimalData.convertPackedDecimalToUnicodeDecimal(positive1234PD, 0, result1234UD, 0, 4, DecimalData.UNICODE_SIGN_SEPARATE_LEADING);

        assertArrayEquals("Failed to convert +1234: UNICODE_SIGN_SEPARATE_LEADING", result1234UD, expected1234UD);

        DecimalData.convertPackedDecimalToUnicodeDecimal(unsigned1234PD, 0, result1234UD, 0, 4, DecimalData.UNICODE_SIGN_SEPARATE_LEADING);

        assertArrayEquals("Failed to convert 1234: UNICODE_SIGN_SEPARATE_LEADING", result1234UD, expected1234UD);

        DecimalData.convertIntegerToUnicodeDecimal(1234, result1234UD, 0, 4, false, DecimalData.UNICODE_SIGN_SEPARATE_LEADING);

        assertArrayEquals("Failed to convert int 1234: UNICODE_SIGN_SEPARATE_LEADING", result1234UD, expected1234UD);

        // -1234 => 002D 0031 0032 0033 0034
        expected1234UD = new char[] { (char) 0x002D, (char) 0x0031, (char) 0x0032, (char) 0x0033, (char) 0x0034 };
        DecimalData.convertPackedDecimalToUnicodeDecimal(negative1234PD, 0, result1234UD, 0, 4, DecimalData.UNICODE_SIGN_SEPARATE_LEADING);

        assertArrayEquals("Failed to convert +1234: UNICODE_SIGN_SEPARATE_LEADING", result1234UD, expected1234UD);

        DecimalData.convertIntegerToUnicodeDecimal(-1234, result1234UD, 0, 4, false, DecimalData.UNICODE_SIGN_SEPARATE_LEADING);

        assertArrayEquals("Failed to convert int -1234: UNICODE_SIGN_SEPARATE_LEADING", result1234UD, expected1234UD);
    }
    
    @Test
    public void testConvert1230SeparateLeadingAlternateSign()
    {
        // Convert 1230 from PD to ED
        final byte[] positive1230PD = { (byte) 0x01, (byte) 0x23, (byte) 0x00 };
        final byte[] unsigned1230PD = { (byte) 0x01, (byte) 0x23, (byte) 0x0A };
        final byte[] negative1230PD = { (byte) 0x01, (byte) 0x23, (byte) 0x0B };

        char[] result1230UD = new char[5];

        // Separate Leading,
        // +1230 => 002B 0031 0032 0033 0030
        char[] expected1230UD = new char[] { (char) 0x002B, (char) 0x0031, (char) 0x0032, (char) 0x0033, (char) 0x0030 };
        DecimalData.convertPackedDecimalToUnicodeDecimal(positive1230PD, 0, result1230UD, 0, 4, DecimalData.UNICODE_SIGN_SEPARATE_LEADING);

        assertArrayEquals("Failed to convert +1230: UNICODE_SIGN_SEPARATE_LEADING", result1230UD, expected1230UD);

        DecimalData.convertPackedDecimalToUnicodeDecimal(unsigned1230PD, 0, result1230UD, 0, 4, DecimalData.UNICODE_SIGN_SEPARATE_LEADING);

        assertArrayEquals("Failed to convert 1230: UNICODE_SIGN_SEPARATE_LEADING", result1230UD, expected1230UD);

        DecimalData.convertIntegerToUnicodeDecimal(1230, result1230UD, 0, 4, false, DecimalData.UNICODE_SIGN_SEPARATE_LEADING);

        assertArrayEquals("Failed to convert int 1230: UNICODE_SIGN_SEPARATE_LEADING", result1230UD, expected1230UD);

        // -1230 => 002D 0031 0032 0033 0030
        expected1230UD = new char[] { (char) 0x002D, (char) 0x0031, (char) 0x0032, (char) 0x0033, (char) 0x0030 };
        DecimalData.convertPackedDecimalToUnicodeDecimal(negative1230PD, 0, result1230UD, 0, 4, DecimalData.UNICODE_SIGN_SEPARATE_LEADING);

        assertArrayEquals("Failed to convert +1230: UNICODE_SIGN_SEPARATE_LEADING", result1230UD, expected1230UD);

        DecimalData.convertIntegerToUnicodeDecimal(-1230, result1230UD, 0, 4, false, DecimalData.UNICODE_SIGN_SEPARATE_LEADING);

        assertArrayEquals("Failed to convert int -1230: UNICODE_SIGN_SEPARATE_LEADING", result1230UD, expected1230UD);
    }
    
    @Test
    public void testConvert1234SeparateTrailing()
    {
        // Convert 1234 from PD to ED
        final byte[] positive1234PD = { (byte) 0x01, (byte) 0x23, (byte) 0x4C };
        final byte[] unsigned1234PD = { (byte) 0x01, (byte) 0x23, (byte) 0x4F };
        final byte[] negative1234PD = { (byte) 0x01, (byte) 0x23, (byte) 0x4D };

        char[] result1234UD = new char[5];

        // Separate Trailing,
        // +1234 => 0031 0032 0033 0034 002B
        char[] expected1234UD = new char[] { (char) 0x0031, (char) 0x0032, (char) 0x0033, (char) 0x0034, (char) 0x002B };
        DecimalData.convertPackedDecimalToUnicodeDecimal(positive1234PD, 0, result1234UD, 0, 4, DecimalData.UNICODE_SIGN_SEPARATE_TRAILING);

        assertArrayEquals("Failed to convert +1234: UNICODE_SIGN_SEPARATE_TRAILING", result1234UD, expected1234UD);

        DecimalData.convertPackedDecimalToUnicodeDecimal(unsigned1234PD, 0, result1234UD, 0, 4, DecimalData.UNICODE_SIGN_SEPARATE_TRAILING);

        assertArrayEquals("Failed to convert 1234: UNICODE_SIGN_SEPARATE_TRAILING", result1234UD, expected1234UD);

        DecimalData.convertIntegerToUnicodeDecimal(1234, result1234UD, 0, 4, false, DecimalData.UNICODE_SIGN_SEPARATE_TRAILING);

        assertArrayEquals("Failed to convert int 1234: UNICODE_SIGN_SEPARATE_TRAILING", result1234UD, expected1234UD);

        // -1234 => 0031 0032 0033 0034 002D
        expected1234UD = new char[] { (char) 0x0031, (char) 0x0032, (char) 0x0033, (char) 0x0034, (char) 0x002D };
        DecimalData.convertPackedDecimalToUnicodeDecimal(negative1234PD, 0, result1234UD, 0, 4, DecimalData.UNICODE_SIGN_SEPARATE_TRAILING);

        assertArrayEquals("Failed to convert -1234: UNICODE_SIGN_SEPARATE_TRAILING", result1234UD, expected1234UD);

        DecimalData.convertIntegerToUnicodeDecimal(-1234, result1234UD, 0, 4, false, DecimalData.UNICODE_SIGN_SEPARATE_TRAILING);

        assertArrayEquals("Failed to convert int -1234: UNICODE_SIGN_SEPARATE_TRAILING", result1234UD, expected1234UD);
    }
    
    @Test
    public void testConvert1230SeparateTrailingAlternateSign()
    {
        // Convert 1230 from PD to ED
        final byte[] positive1230PD = { (byte) 0x01, (byte) 0x23, (byte) 0x00 };
        final byte[] unsigned1230PD = { (byte) 0x01, (byte) 0x23, (byte) 0x0A };
        final byte[] negative1230PD = { (byte) 0x01, (byte) 0x23, (byte) 0x0B };

        char[] result1230UD = new char[5];

        // Separate Trailing,
        // +1230 => 0031 0032 0033 0030 002B
        char[] expected1230UD = new char[] { (char) 0x0031, (char) 0x0032, (char) 0x0033, (char) 0x0030, (char) 0x002B };
        DecimalData.convertPackedDecimalToUnicodeDecimal(positive1230PD, 0, result1230UD, 0, 4, DecimalData.UNICODE_SIGN_SEPARATE_TRAILING);

        assertArrayEquals("Failed to convert +1230: UNICODE_SIGN_SEPARATE_TRAILING", result1230UD, expected1230UD);

        DecimalData.convertPackedDecimalToUnicodeDecimal(unsigned1230PD, 0, result1230UD, 0, 4, DecimalData.UNICODE_SIGN_SEPARATE_TRAILING);

        assertArrayEquals("Failed to convert 1230: UNICODE_SIGN_SEPARATE_TRAILING", result1230UD, expected1230UD);

        DecimalData.convertIntegerToUnicodeDecimal(1230, result1230UD, 0, 4, false, DecimalData.UNICODE_SIGN_SEPARATE_TRAILING);

        assertArrayEquals("Failed to convert int 1230: UNICODE_SIGN_SEPARATE_TRAILING", result1230UD, expected1230UD);

        // -1230 => 0031 0032 0033 0030 002D
        expected1230UD = new char[] { (char) 0x0031, (char) 0x0032, (char) 0x0033, (char) 0x0030, (char) 0x002D };
        DecimalData.convertPackedDecimalToUnicodeDecimal(negative1230PD, 0, result1230UD, 0, 4, DecimalData.UNICODE_SIGN_SEPARATE_TRAILING);

        assertArrayEquals("Failed to convert -1230: UNICODE_SIGN_SEPARATE_TRAILING", result1230UD, expected1230UD);

        DecimalData.convertIntegerToUnicodeDecimal(-1230, result1230UD, 0, 4, false, DecimalData.UNICODE_SIGN_SEPARATE_TRAILING);

        assertArrayEquals("Failed to convert int -1230: UNICODE_SIGN_SEPARATE_TRAILING", result1230UD, expected1230UD);
    }
}
