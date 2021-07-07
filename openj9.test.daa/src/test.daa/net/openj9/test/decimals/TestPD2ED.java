/*******************************************************************************
* Copyright (c) 2017, 2021 IBM Corp.
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

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import com.ibm.dataaccess.DecimalData;

public class TestPD2ED extends TestED2PD
{
    static final int ARRAY_SIZE = 100;

    byte[] pdValue = new byte[ARRAY_SIZE];
    byte[] edValue = new byte[ARRAY_SIZE];

    static Random randomGen;

    static
    {
        randomGen = new Random((int) (System.currentTimeMillis() % 160001));
    };

    static int getRandomInt()
    {
        return randomGen.nextInt(Integer.MAX_VALUE);
    }

    static int getRandomInt(int limit)
    {
        return randomGen.nextInt(limit);
    }

    // temporarily disable this test becase it is not correct.
    // @Test
    public void testZeros()
    {
        for (int offset = 0; offset < 3; ++offset)
        {

            Arrays.fill(pdValue, (byte) 0x00);
            Arrays.fill(edValue, (byte) 0);

            for (int i = 0; i < edTypes.length; ++i)
            {
                for (int j = 0x0A; j < 0x10; ++j)
                {
                    pdValue[offset] = (byte) j;
                    DecimalData.convertPackedDecimalToExternalDecimal(pdValue, offset, edValue, offset, 1, edTypes[i]);
                    verify(pdValue, offset, edValue, offset, 1, edTypes[i]);
                }
            }
        }
    }

    @Test
    public void testIllegalArgument()
    {
        // illegal precision
        boolean catched = false;

        try
        {
            DecimalData.convertPackedDecimalToExternalDecimal(pdValue, 0, edValue, 0, 0, edTypes[0]);
        }

        catch (IllegalArgumentException e)
        {
            catched = true;
        }

        if (!catched)
            fail("Did not catch illegal argument.");

        // illegal decimal type
        catched = false;

        try
        {
            DecimalData.convertPackedDecimalToExternalDecimal(pdValue, 0, edValue, 0, 1, 0);
        }

        catch (IllegalArgumentException e)
        {
            catched = true;
        }

        if (!catched)
            fail("Did not catch illegal argument.");

        catched = false;

        try
        {
            DecimalData.convertPackedDecimalToExternalDecimal(pdValue, 0, edValue, 0, 1, 12);
        }

        catch (IllegalArgumentException e)
        {
            catched = true;
        }

        if (!catched)
            fail("Did not catch illegal argument.");
    }

    @Test
    public void testArrayIndexOutOfBounds()
    {
        boolean catched = false;

        // test for each edtype
        for (int i = 0; i < edTypes.length; i++)
        {
            // out of bound access for pdArray
            try
            {
                DecimalData.convertPackedDecimalToExternalDecimal(pdValue, ARRAY_SIZE, edValue, ARRAY_SIZE - 1, 1, edTypes[i]);
            }

            catch (ArrayIndexOutOfBoundsException e)
            {
                catched = true;
            }

            if (!catched)
                fail("Did not catch AIOOB exception for pdArray");

            catched = false;

            // out of bound access for edArray
            try
            {
                DecimalData.convertPackedDecimalToExternalDecimal(pdValue, ARRAY_SIZE - 1, edValue, ARRAY_SIZE, 1, edTypes[i]);
            }

            catch (ArrayIndexOutOfBoundsException e)
            {
                catched = true;
            }

            if (!catched)
                fail("Did not catch AIOOB exception for edArray");

            catched = false;
        }

    }

    public void test(int pdOffset, int edOffset, int limit)
    {
        int precision, value, edType;

        if (limit == 0)
            value = getRandomInt();
        else
            value = getRandomInt(limit);

        precision = String.valueOf(value).length();

        if (randomGen.nextBoolean())
            value = -value;

        DecimalData.convertIntegerToPackedDecimal(value, pdValue, pdOffset, precision, true);

        edType = getRandomInt(4);

        if (edType == 0)
            edType = 1;

        DecimalData.convertPackedDecimalToExternalDecimal(pdValue, pdOffset, edValue, edOffset, precision, edType);
        int result = DecimalData.convertExternalDecimalToInteger(edValue, edOffset, precision, true, edType);

        verify(pdValue, pdOffset, edValue, edOffset, precision, edType);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("pdOffset : %d, edOffset: %d, ed Types : %d, value: %d resultint: %d \n ", pdOffset, edOffset, edType, value, result), value, result);
        }
    }

    @Test
    public void testSmallValues()
    {
        for (int i = 0; i < 10; i++)
            test(0, 0, 10);

        DecimalData.convertIntegerToPackedDecimal(-1, pdValue, 0, 1, true);

        for (int i = 1; i < 5; i++)
        {
            DecimalData.convertPackedDecimalToExternalDecimal(pdValue, 0, edValue, 0, 1, i);
            verify(pdValue, 0, edValue, 0, 1, i);
        }

        DecimalData.convertIntegerToPackedDecimal(1, pdValue, 0, 1, true);

        for (int i = 1; i < 5; i++)
        {
            DecimalData.convertPackedDecimalToExternalDecimal(pdValue, 0, edValue, 0, 1, i);
            verify(pdValue, 0, edValue, 0, 1, i);
        }
    }

    @Test
    public void testRandom()
    {
        int pdOffset, edOffset;

        for (int i = 0; i < 1000; ++i)
        {
            pdOffset = getRandomInt(ARRAY_SIZE - 6);
            edOffset = getRandomInt(ARRAY_SIZE - 11);
            test(pdOffset, edOffset, 0);
        }
    }

    @Test
    public void testPDwithLeadingZeros()
    {
        int value = getRandomInt();
        int length = String.valueOf(value).length();
        int precision = length + getRandomInt(4);

        int edType = getRandomInt(4);

        if (edType == 0)
            edType = 1;

        DecimalData.convertIntegerToPackedDecimal(value, pdValue, 0, precision, true);
        DecimalData.convertPackedDecimalToExternalDecimal(pdValue, 0, edValue, 0, precision, edType);

        verify(pdValue, 0, edValue, 0, precision, edType);
    }

    @Test
    public void testConvert1234EmbeddedLeading()
    {
        // Convert 1234 from PD to ED
        final byte[] positive1234PD = { (byte) 0x01, (byte) 0x23, (byte) 0x4C };
        final byte[] unsigned1234PD = { (byte) 0x01, (byte) 0x23, (byte) 0x4F };
        final byte[] negative1234PD = { (byte) 0x01, (byte) 0x23, (byte) 0x4D };

        byte[] result1234ED = new byte[4];

        // Embedded Leading,
        // +1234 => C1 F2 F3 F4
        byte[] expected1234ED = { (byte) 0xC1, (byte) 0xF2, (byte) 0xF3, (byte) 0xF4 };
        DecimalData.convertPackedDecimalToExternalDecimal(positive1234PD, 0, result1234ED, 0, 4, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);

        assertArrayEquals("Failed to convert +1234: EBCDIC_SIGN_EMBEDDED_LEADING", result1234ED, expected1234ED);

        DecimalData.convertPackedDecimalToExternalDecimal(unsigned1234PD, 0, result1234ED, 0, 4, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);

        assertArrayEquals("Failed to convert 1234: EBCDIC_SIGN_EMBEDDED_LEADING", result1234ED, expected1234ED);

        DecimalData.convertIntegerToExternalDecimal(1234, result1234ED, 0, 4, false, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);

        assertArrayEquals("Failed to convert int 1234: EBCDIC_SIGN_EMBEDDED_LEADING", result1234ED, expected1234ED);

        // -1234 => D1 F2 F3 F4
        expected1234ED = new byte[] { (byte) 0xD1, (byte) 0xF2, (byte) 0xF3, (byte) 0xF4 };
        DecimalData.convertPackedDecimalToExternalDecimal(negative1234PD, 0, result1234ED, 0, 4, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);

        assertArrayEquals("Failed to convert +1234: EBCDIC_SIGN_EMBEDDED_LEADING", result1234ED, expected1234ED);

        DecimalData.convertIntegerToExternalDecimal(-1234, result1234ED, 0, 4, false, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);

        assertArrayEquals("Failed to convert int -1234: EBCDIC_SIGN_EMBEDDED_LEADING", result1234ED, expected1234ED);
    }
    
    @Test
    public void testConvert1230EmbeddedLeadingAlternateSign()
    {
        // Convert 1230 from PD to ED
        final byte[] positive1230PD = { (byte) 0x01, (byte) 0x23, (byte) 0x00 };
        final byte[] unsigned1230PD = { (byte) 0x01, (byte) 0x23, (byte) 0x0A };
        final byte[] negative1230PD = { (byte) 0x01, (byte) 0x23, (byte) 0x0B };

        byte[] result1230ED = new byte[4];

        // Embedded Leading,
        // +1230 => C1 F2 F3 F0
        byte[] expected1234ED = { (byte) 0xC1, (byte) 0xF2, (byte) 0xF3, (byte) 0xF0 };
        DecimalData.convertPackedDecimalToExternalDecimal(positive1230PD, 0, result1230ED, 0, 4, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);

        assertArrayEquals("Failed to convert +1230: EBCDIC_SIGN_EMBEDDED_LEADING", result1230ED, expected1234ED);

        DecimalData.convertPackedDecimalToExternalDecimal(unsigned1230PD, 0, result1230ED, 0, 4, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);

        assertArrayEquals("Failed to convert 1230: EBCDIC_SIGN_EMBEDDED_LEADING", result1230ED, expected1234ED);

        DecimalData.convertIntegerToExternalDecimal(1230, result1230ED, 0, 4, false, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);

        assertArrayEquals("Failed to convert int 1230: EBCDIC_SIGN_EMBEDDED_LEADING", result1230ED, expected1234ED);

        // -1234 => D1 F2 F3 F0
        expected1234ED = new byte[] { (byte) 0xD1, (byte) 0xF2, (byte) 0xF3, (byte) 0xF0 };
        DecimalData.convertPackedDecimalToExternalDecimal(negative1230PD, 0, result1230ED, 0, 4, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);

        assertArrayEquals("Failed to convert +1230: EBCDIC_SIGN_EMBEDDED_LEADING", result1230ED, expected1234ED);

        DecimalData.convertIntegerToExternalDecimal(-1230, result1230ED, 0, 4, false, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);

        assertArrayEquals("Failed to convert int -1230: EBCDIC_SIGN_EMBEDDED_LEADING", result1230ED, expected1234ED);
    }
    
    @Test
    public void testConvert1234EmbeddedTrailing()
    {
        // Convert 1234 from PD to ED
        final byte[] positive1234PD = { (byte) 0x01, (byte) 0x23, (byte) 0x4C };
        final byte[] unsigned1234PD = { (byte) 0x01, (byte) 0x23, (byte) 0x4F };
        final byte[] negative1234PD = { (byte) 0x01, (byte) 0x23, (byte) 0x4D };

        byte[] result1234ED = new byte[4];

        // Embedded Trailing,
        // +1234 => F1 F2 F3 C4
        byte[] expected1234ED = new byte[] { (byte) 0xF1, (byte) 0xF2, (byte) 0xF3, (byte) 0xC4 };
        DecimalData.convertPackedDecimalToExternalDecimal(positive1234PD, 0, result1234ED, 0, 4, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);

        assertArrayEquals("Failed to convert +1234: EBCDIC_SIGN_EMBEDDED_TRAILING", result1234ED, expected1234ED);

        DecimalData.convertPackedDecimalToExternalDecimal(unsigned1234PD, 0, result1234ED, 0, 4, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);

        assertArrayEquals("Failed to convert 1234: EBCDIC_SIGN_EMBEDDED_TRAILING", result1234ED, expected1234ED);

        DecimalData.convertIntegerToExternalDecimal(1234, result1234ED, 0, 4, false, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);

        assertArrayEquals("Failed to convert int 1234: EBCDIC_SIGN_EMBEDDED_TRAILING", result1234ED, expected1234ED);

        // -1234 => F1 F2 F3 D4
        expected1234ED = new byte[] { (byte) 0xF1, (byte) 0xF2, (byte) 0xF3, (byte) 0xD4 };
        DecimalData.convertPackedDecimalToExternalDecimal(negative1234PD, 0, result1234ED, 0, 4, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);

        assertArrayEquals("Failed to convert -1234: EBCDIC_SIGN_EMBEDDED_TRAILING", result1234ED, expected1234ED);

        DecimalData.convertIntegerToExternalDecimal(-1234, result1234ED, 0, 4, false, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);

        assertArrayEquals("Failed to convert int -1234: EBCDIC_SIGN_EMBEDDED_TRAILING", result1234ED, expected1234ED);

        result1234ED = new byte[5];
    }

    @Test
    public void testConvertEmbeddedTrailingMaxPercision()
    {
        // 16 bytes in low and 15 in high vector registers
        final byte[] positive = new byte[] { (byte) 0x91, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x13, (byte) 0x14, (byte) 0x15, (byte) 0x16, (byte) 0x17, (byte) 0x18, (byte) 0x19, (byte) 0x4C };
        final byte[] unsigned = new byte[] { (byte) 0x91, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x13, (byte) 0x14, (byte) 0x15, (byte) 0x16, (byte) 0x17, (byte) 0x18, (byte) 0x19, (byte) 0x4F };
        final byte[] negative = new byte[] { (byte) 0x91, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x13, (byte) 0x14, (byte) 0x15, (byte) 0x16, (byte) 0x17, (byte) 0x18, (byte) 0x19, (byte) 0x4D };

        byte[] result = new byte[31];

        // Positive
        byte[] expected = new byte[] { (byte) 0xF9, (byte) 0xF1, (byte) 0xF2, (byte) 0xF3, (byte) 0xF4, (byte) 0xF5, (byte) 0xF6, (byte) 0xF7, (byte) 0xF8, (byte) 0xF9, (byte) 0xF1, (byte) 0xF0, (byte) 0xF1, (byte) 0xF1, (byte) 0xF1, (byte) 0xF2, (byte) 0xF1, (byte) 0xF3, (byte) 0xF1, (byte) 0xF4, (byte) 0xF1, (byte) 0xF5, (byte) 0xF1, (byte) 0xF6, (byte) 0xF1, (byte) 0xF7, (byte) 0xF1, (byte) 0xF8, (byte) 0xF1, (byte) 0xF9, (byte) 0xC4 };

        DecimalData.convertPackedDecimalToExternalDecimal(positive, 0, result, 0, 31, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);
        assertArrayEquals("Failed to convert positive packed decimal with max percision: EBCDIC_SIGN_EMBEDDED_TRAILING", expected, result);

        DecimalData.convertPackedDecimalToExternalDecimal(unsigned, 0, result, 0, 31, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);
        assertArrayEquals("Failed to convert unsigned packed decimal with max percision: EBCDIC_SIGN_EMBEDDED_TRAILING", expected, result);

        // Negative
        expected = new byte[] { (byte) 0xF9, (byte) 0xF1, (byte) 0xF2, (byte) 0xF3, (byte) 0xF4, (byte) 0xF5, (byte) 0xF6, (byte) 0xF7, (byte) 0xF8, (byte) 0xF9, (byte) 0xF1, (byte) 0xF0, (byte) 0xF1, (byte) 0xF1, (byte) 0xF1, (byte) 0xF2, (byte) 0xF1, (byte) 0xF3, (byte) 0xF1, (byte) 0xF4, (byte) 0xF1, (byte) 0xF5, (byte) 0xF1, (byte) 0xF6, (byte) 0xF1, (byte) 0xF7, (byte) 0xF1, (byte) 0xF8, (byte) 0xF1, (byte) 0xF9, (byte) 0xD4 };

        DecimalData.convertPackedDecimalToExternalDecimal(negative, 0, result, 0, 31, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);
        assertArrayEquals("Failed to convert negative packed decimal with max percision: EBCDIC_SIGN_EMBEDDED_TRAILING", expected, result);
    }

    @Test
    public void testConvertEmbeddedTrailingMaxPercisionAlternateSign()
    {
        // 16 bytes in low and 15 in high vector registers
        final byte[] positive = new byte[] { (byte) 0x91, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x13, (byte) 0x14, (byte) 0x15, (byte) 0x16, (byte) 0x17, (byte) 0x18, (byte) 0x19, (byte) 0x4E };
        final byte[] unsigned = new byte[] { (byte) 0x91, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x13, (byte) 0x14, (byte) 0x15, (byte) 0x16, (byte) 0x17, (byte) 0x18, (byte) 0x19, (byte) 0x4A };
        final byte[] negative = new byte[] { (byte) 0x91, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x13, (byte) 0x14, (byte) 0x15, (byte) 0x16, (byte) 0x17, (byte) 0x18, (byte) 0x19, (byte) 0x4B };

        byte[] result = new byte[31];

        // Positive
        byte[] expected = new byte[] { (byte) 0xF9, (byte) 0xF1, (byte) 0xF2, (byte) 0xF3, (byte) 0xF4, (byte) 0xF5, (byte) 0xF6, (byte) 0xF7, (byte) 0xF8, (byte) 0xF9, (byte) 0xF1, (byte) 0xF0, (byte) 0xF1, (byte) 0xF1, (byte) 0xF1, (byte) 0xF2, (byte) 0xF1, (byte) 0xF3, (byte) 0xF1, (byte) 0xF4, (byte) 0xF1, (byte) 0xF5, (byte) 0xF1, (byte) 0xF6, (byte) 0xF1, (byte) 0xF7, (byte) 0xF1, (byte) 0xF8, (byte) 0xF1, (byte) 0xF9, (byte) 0xC4 };

        DecimalData.convertPackedDecimalToExternalDecimal(positive, 0, result, 0, 31, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);
        assertArrayEquals("Failed to convert positive packed decimal with max percision: EBCDIC_SIGN_EMBEDDED_TRAILING", expected, result);

        DecimalData.convertPackedDecimalToExternalDecimal(unsigned, 0, result, 0, 31, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);
        assertArrayEquals("Failed to convert unsigned packed decimal with max percision: EBCDIC_SIGN_EMBEDDED_TRAILING", expected, result);

        // Negative
        expected = new byte[] { (byte) 0xF9, (byte) 0xF1, (byte) 0xF2, (byte) 0xF3, (byte) 0xF4, (byte) 0xF5, (byte) 0xF6, (byte) 0xF7, (byte) 0xF8, (byte) 0xF9, (byte) 0xF1, (byte) 0xF0, (byte) 0xF1, (byte) 0xF1, (byte) 0xF1, (byte) 0xF2, (byte) 0xF1, (byte) 0xF3, (byte) 0xF1, (byte) 0xF4, (byte) 0xF1, (byte) 0xF5, (byte) 0xF1, (byte) 0xF6, (byte) 0xF1, (byte) 0xF7, (byte) 0xF1, (byte) 0xF8, (byte) 0xF1, (byte) 0xF9, (byte) 0xD4 };

        DecimalData.convertPackedDecimalToExternalDecimal(negative, 0, result, 0, 31, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);
        assertArrayEquals("Failed to convert negative packed decimal with max percision: EBCDIC_SIGN_EMBEDDED_TRAILING", expected, result);
    }
    
    @Test
    public void testConvert1230EmbeddedTrailingAlternateSign()
    {
        // Convert 1230 from PD to ED
        final byte[] positive1230PD = { (byte) 0x01, (byte) 0x23, (byte) 0x00 };
        final byte[] unsigned1230PD = { (byte) 0x01, (byte) 0x23, (byte) 0x0A };
        final byte[] negative1230PD = { (byte) 0x01, (byte) 0x23, (byte) 0x0B };

        byte[] result1230ED = new byte[4];

        // Embedded Trailing,
        // +1230 => F1 F2 F3 C0
        byte[] expected1234ED = new byte[] { (byte) 0xF1, (byte) 0xF2, (byte) 0xF3, (byte) 0xC0 };
        DecimalData.convertPackedDecimalToExternalDecimal(positive1230PD, 0, result1230ED, 0, 4, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);

        assertArrayEquals("Failed to convert +1230: EBCDIC_SIGN_EMBEDDED_TRAILING", result1230ED, expected1234ED);

        DecimalData.convertPackedDecimalToExternalDecimal(unsigned1230PD, 0, result1230ED, 0, 4, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);

        assertArrayEquals("Failed to convert 1230: EBCDIC_SIGN_EMBEDDED_TRAILING", result1230ED, expected1234ED);

        DecimalData.convertIntegerToExternalDecimal(1230, result1230ED, 0, 4, false, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);

        assertArrayEquals("Failed to convert int 1230: EBCDIC_SIGN_EMBEDDED_TRAILING", result1230ED, expected1234ED);

        // -1230 => F1 F2 F3 D0
        expected1234ED = new byte[] { (byte) 0xF1, (byte) 0xF2, (byte) 0xF3, (byte) 0xD0 };
        DecimalData.convertPackedDecimalToExternalDecimal(negative1230PD, 0, result1230ED, 0, 4, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);

        assertArrayEquals("Failed to convert -1230: EBCDIC_SIGN_EMBEDDED_TRAILING", result1230ED, expected1234ED);

        DecimalData.convertIntegerToExternalDecimal(-1230, result1230ED, 0, 4, false, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);

        assertArrayEquals("Failed to convert int -1230: EBCDIC_SIGN_EMBEDDED_TRAILING", result1230ED, expected1234ED);

        result1230ED = new byte[5];
    }
    
    // Non zero packed decimal offset
    public static void testConvertEmbeddedTrailingNonZeroPackedDecimalOffset() {
        final byte[] input = { (byte) 0x91, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x13, (byte) 0x4D };

        // Non zero offset ending on the sign byte.
        byte[] result = new byte[15];
        byte[] expected = new byte[] { (byte) 0xF4, (byte) 0xF5, (byte) 0xF6, (byte) 0xF7, (byte) 0xF8, (byte) 0xF9, (byte) 0xF1, (byte) 0xF0, (byte) 0xF1, (byte) 0xF1, (byte) 0xF1, (byte) 0xF2, (byte) 0xF1, (byte) 0xF3, (byte) 0xD4 };

        DecimalData.convertPackedDecimalToExternalDecimal(input, 2, result, 0, 15, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);
        assertArrayEquals("Failed to convert packed decimal with non zero offset ending on sign byte: EBCDIC_SIGN_EMBEDDED_TRAILING", expected, result);

        // Non zero offset ending on non-sign byte.
        // Illegal instruction exception would be thrown if hardware sign validation is turned on. This is because the sign representation
        // is neither prefered nor alternative.
        expected = new byte[] { (byte) 0xF4, (byte) 0xF5, (byte) 0xF6, (byte) 0xF7, (byte) 0xF8, (byte) 0xF9, (byte) 0xF1, (byte) 0xF0, (byte) 0xF1, (byte) 0xF1, (byte) 0xF1, (byte) 0xF2, (byte) 0xC1 };

        result = new byte[13];
        DecimalData.convertPackedDecimalToExternalDecimal(input, 2, result, 0, 13, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);
        assertArrayEquals("Failed to convert packed decimal with non zero offset ending on non-sign byte: EBCDIC_SIGN_EMBEDDED_TRAILING", expected, result);
    }

    @Test
    public void testConvert1234SeparateLeading()
    {
        // Convert 1234 from PD to ED
        final byte[] positive1234PD = { (byte) 0x01, (byte) 0x23, (byte) 0x4C };
        final byte[] unsigned1234PD = { (byte) 0x01, (byte) 0x23, (byte) 0x4F };
        final byte[] negative1234PD = { (byte) 0x01, (byte) 0x23, (byte) 0x4D };

        byte[] result1234ED = new byte[5];

        // Separate Leading,
        // +1234 => 4E F1 F2 F3 F4
        byte[] expected1234ED = new byte[] { (byte) 0x4E, (byte) 0xF1, (byte) 0xF2, (byte) 0xF3, (byte) 0xF4 };
        DecimalData.convertPackedDecimalToExternalDecimal(positive1234PD, 0, result1234ED, 0, 4, DecimalData.EBCDIC_SIGN_SEPARATE_LEADING);

        assertArrayEquals("Failed to convert +1234: EBCDIC_SIGN_SEPARATE_LEADING", result1234ED, expected1234ED);

        DecimalData.convertPackedDecimalToExternalDecimal(unsigned1234PD, 0, result1234ED, 0, 4, DecimalData.EBCDIC_SIGN_SEPARATE_LEADING);

        assertArrayEquals("Failed to convert 1234: EBCDIC_SIGN_SEPARATE_LEADING", result1234ED, expected1234ED);

        DecimalData.convertIntegerToExternalDecimal(1234, result1234ED, 0, 4, false, DecimalData.EBCDIC_SIGN_SEPARATE_LEADING);

        assertArrayEquals("Failed to convert int 1234: EBCDIC_SIGN_SEPARATE_LEADING", result1234ED, expected1234ED);

        // -1234 => 60 F1 F2 F3 F4
        expected1234ED = new byte[] { (byte) 0x60, (byte) 0xF1, (byte) 0xF2, (byte) 0xF3, (byte) 0xF4 };
        DecimalData.convertPackedDecimalToExternalDecimal(negative1234PD, 0, result1234ED, 0, 4, DecimalData.EBCDIC_SIGN_SEPARATE_LEADING);

        assertArrayEquals("Failed to convert +1234: EBCDIC_SIGN_SEPARATE_LEADING", result1234ED, expected1234ED);

        DecimalData.convertIntegerToExternalDecimal(-1234, result1234ED, 0, 4, false, DecimalData.EBCDIC_SIGN_SEPARATE_LEADING);

        assertArrayEquals("Failed to convert int -1234: EBCDIC_SIGN_SEPARATE_LEADING", result1234ED, expected1234ED);
    }
    
    @Test
    public void testConvert1230SeparateLeadingAlternateSign()
    {
        // Convert 1230 from PD to ED
        final byte[] positive1230PD = { (byte) 0x01, (byte) 0x23, (byte) 0x00 };
        final byte[] unsigned1230PD = { (byte) 0x01, (byte) 0x23, (byte) 0x0A };
        final byte[] negative1230PD = { (byte) 0x01, (byte) 0x23, (byte) 0x0B };

        byte[] result1230ED = new byte[5];

        // Separate Leading,
        // +1230 => 4E F1 F2 F3 F0
        byte[] expected1230ED = new byte[] { (byte) 0x4E, (byte) 0xF1, (byte) 0xF2, (byte) 0xF3, (byte) 0xF0 };
        DecimalData.convertPackedDecimalToExternalDecimal(positive1230PD, 0, result1230ED, 0, 4, DecimalData.EBCDIC_SIGN_SEPARATE_LEADING);

        assertArrayEquals("Failed to convert +1230: EBCDIC_SIGN_SEPARATE_LEADING", result1230ED, expected1230ED);

        DecimalData.convertPackedDecimalToExternalDecimal(unsigned1230PD, 0, result1230ED, 0, 4, DecimalData.EBCDIC_SIGN_SEPARATE_LEADING);

        assertArrayEquals("Failed to convert 1230: EBCDIC_SIGN_SEPARATE_LEADING", result1230ED, expected1230ED);

        DecimalData.convertIntegerToExternalDecimal(1230, result1230ED, 0, 4, false, DecimalData.EBCDIC_SIGN_SEPARATE_LEADING);

        assertArrayEquals("Failed to convert int 1230: EBCDIC_SIGN_SEPARATE_LEADING", result1230ED, expected1230ED);

        // -1230 => 60 F1 F2 F3 F0
        expected1230ED = new byte[] { (byte) 0x60, (byte) 0xF1, (byte) 0xF2, (byte) 0xF3, (byte) 0xF0 };
        DecimalData.convertPackedDecimalToExternalDecimal(negative1230PD, 0, result1230ED, 0, 4, DecimalData.EBCDIC_SIGN_SEPARATE_LEADING);

        assertArrayEquals("Failed to convert +1230: EBCDIC_SIGN_SEPARATE_LEADING", result1230ED, expected1230ED);

        DecimalData.convertIntegerToExternalDecimal(-1230, result1230ED, 0, 4, false, DecimalData.EBCDIC_SIGN_SEPARATE_LEADING);

        assertArrayEquals("Failed to convert int -1230: EBCDIC_SIGN_SEPARATE_LEADING", result1230ED, expected1230ED);
    }
    
    @Test
    public void testConvert1234SeparateTrailing()
    {
        // Convert 1234 from PD to ED
        final byte[] positive1234PD = { (byte) 0x01, (byte) 0x23, (byte) 0x4C };
        final byte[] unsigned1234PD = { (byte) 0x01, (byte) 0x23, (byte) 0x4F };
        final byte[] negative1234PD = { (byte) 0x01, (byte) 0x23, (byte) 0x4D };

        byte[] result1234ED = new byte[5];

        // Separate Trailing,
        // +1234 => F1 F2 F3 F4 4E
        byte[] expected1234ED = new byte[] { (byte) 0xF1, (byte) 0xF2, (byte) 0xF3, (byte) 0xF4, (byte) 0x4E };
        DecimalData.convertPackedDecimalToExternalDecimal(positive1234PD, 0, result1234ED, 0, 4, DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING);

        assertArrayEquals("Failed to convert +1234: EBCDIC_SIGN_SEPARATE_TRAILING", result1234ED, expected1234ED);

        DecimalData.convertPackedDecimalToExternalDecimal(unsigned1234PD, 0, result1234ED, 0, 4, DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING);

        assertArrayEquals("Failed to convert 1234: EBCDIC_SIGN_SEPARATE_TRAILING", result1234ED, expected1234ED);

        DecimalData.convertIntegerToExternalDecimal(1234, result1234ED, 0, 4, false, DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING);

        assertArrayEquals("Failed to convert int 1234: EBCDIC_SIGN_SEPARATE_TRAILING", result1234ED, expected1234ED);

        // -1234 => F1 F2 F3 F4 60
        expected1234ED = new byte[] { (byte) 0xF1, (byte) 0xF2, (byte) 0xF3, (byte) 0xF4, (byte) 0x60 };
        DecimalData.convertPackedDecimalToExternalDecimal(negative1234PD, 0, result1234ED, 0, 4, DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING);

        assertArrayEquals("Failed to convert +1234: EBCDIC_SIGN_SEPARATE_TRAILING", result1234ED, expected1234ED);

        DecimalData.convertIntegerToExternalDecimal(-1234, result1234ED, 0, 4, false, DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING);

        assertArrayEquals("Failed to convert int -1234: EBCDIC_SIGN_SEPARATE_TRAILING", result1234ED, expected1234ED);
    }
    
    @Test
    public void testConvert1230SeparateTrailingAlternateSign()
    {
        // Convert 1230 from PD to ED
        final byte[] positive1230PD = { (byte) 0x01, (byte) 0x23, (byte) 0x00 };
        final byte[] unsigned1230PD = { (byte) 0x01, (byte) 0x23, (byte) 0x0A };
        final byte[] negative1230PD = { (byte) 0x01, (byte) 0x23, (byte) 0x0B };

        byte[] result1230ED = new byte[5];

        // Separate Trailing,
        // +1230 => F1 F2 F3 F0 4E
        byte[] expected1230ED = new byte[] { (byte) 0xF1, (byte) 0xF2, (byte) 0xF3, (byte) 0xF0, (byte) 0x4E };
        DecimalData.convertPackedDecimalToExternalDecimal(positive1230PD, 0, result1230ED, 0, 4, DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING);

        assertArrayEquals("Failed to convert +1230: EBCDIC_SIGN_SEPARATE_TRAILING", result1230ED, expected1230ED);

        DecimalData.convertPackedDecimalToExternalDecimal(unsigned1230PD, 0, result1230ED, 0, 4, DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING);

        assertArrayEquals("Failed to convert 1230: EBCDIC_SIGN_SEPARATE_TRAILING", result1230ED, expected1230ED);

        DecimalData.convertIntegerToExternalDecimal(1230, result1230ED, 0, 4, false, DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING);

        assertArrayEquals("Failed to convert int 1230: EBCDIC_SIGN_SEPARATE_TRAILING", result1230ED, expected1230ED);

        // -1230 => F1 F2 F3 F0 60
        expected1230ED = new byte[] { (byte) 0xF1, (byte) 0xF2, (byte) 0xF3, (byte) 0xF0, (byte) 0x60 };
        DecimalData.convertPackedDecimalToExternalDecimal(negative1230PD, 0, result1230ED, 0, 4, DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING);

        assertArrayEquals("Failed to convert +1230: EBCDIC_SIGN_SEPARATE_TRAILING", result1230ED, expected1230ED);

        DecimalData.convertIntegerToExternalDecimal(-1230, result1230ED, 0, 4, false, DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING);

        assertArrayEquals("Failed to convert int -1230: EBCDIC_SIGN_SEPARATE_TRAILING", result1230ED, expected1230ED);
    }
}
