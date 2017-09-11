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

package net.openj9.test.binaryData;

import static org.junit.Assert.*;

import java.util.Arrays; 
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ibm.dataaccess.ByteArrayMarshaller;

public class TestLong2ByteArray
{
    static String outputFile;
    static Random randomGen;
    static final int ARRAY_SIZE = 256;
    static final int LONGSIZE = 8;
    static final int BYTESIZE = 8; // for elegance!
    static final long ONE_DIGIT_MIN = 0x1L;
    static final long TWO_DIGIT_MIN = 0x10L;
    static final long THREE_DIGIT_MIN = 0x100L;
    static final long FOUR_DIGIT_MIN = 0x1000L;
    static final long FIVE_DIGIT_MIN = 0x10000L;
    static final long SIX_DIGIT_MIN = 0x100000L;
    static final long SEVEN_DIGIT_MIN = 0x1000000L;
    static final long EIGHT_DIGIT_MIN = 0x10000000L;
    static final long NINE_DIGIT_MIN = 0x100000000L;
    static final long TEN_DIGIT_MIN = 0x1000000000L;
    static final long ELEVEN_DIGIT_MIN = 0x10000000000L;
    static final long TWELVE_DIGIT_MIN = 0x100000000000L;
    static final long THIRTEEN_DIGIT_MIN = 0x1000000000000L;
    static final long FOURTEEN_DIGIT_MIN = 0x10000000000000L;
    static final long FIFTEEN_DIGIT_MIN = 0x100000000000000L;
    static final long SIXTEEN_DIGIT_MIN = 0x1000000000000000L;

    /* Attempting to increase the randomness! */
    static int randomSeed = (int) (System.currentTimeMillis() % 160001);

    byte[] byteArray = new byte[ARRAY_SIZE];
    byte[] resultArrayBigEndian = new byte[ARRAY_SIZE];
    byte[] resultArrayLittleEndian = new byte[ARRAY_SIZE];

    static final boolean isBigEndian = true;
    static final boolean isLittleEndian = false;

    static
    {
        outputFile = "expected." + TestShort2ByteArray.class.getSimpleName() + ".txt";
        randomGen = new Random(randomSeed);
    };

    @Before
    public void setUp() throws Exception
    {

    }

    @After
    public void tearDown() throws Exception
    {

    }

    private long getRandomLongWithNHexDigits(int n)
    {
        assertTrue(n > 0 && n <= 16);
        long randomLong = 0;
        for (int k = 0; k < n; k++)
            randomLong = randomLong | (((randomGen.nextLong() >> k * 4) & 0x0f) << (n - k - 1) * 4);
        return randomLong < 0 ? randomLong * -1 : randomLong;
    }

    @Test
    public void testBoundaries()
    {
        long value = Long.MIN_VALUE;
        byte[] byteArray = new byte[9];

        ByteArrayMarshaller.writeLong(value, byteArray, 0, true);
        ByteArrayMarshaller.writeLong(value, byteArray, 0, false);

        ByteArrayMarshaller.writeLong(value, byteArray, 1, true);
        ByteArrayMarshaller.writeLong(value, byteArray, 1, false);

    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBLittleEndian1()
    {
        long value = Long.MIN_VALUE;
        byte[] byteArray = new byte[8];

        ByteArrayMarshaller.writeLong(value, byteArray, 1, false);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBLittleEndian2()
    {
        long value = Long.MIN_VALUE;
        byte[] byteArray = new byte[7];

        ByteArrayMarshaller.writeLong(value, byteArray, 0, false);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBBigEndian1()
    {
        long value = Long.MIN_VALUE;
        byte[] byteArray = new byte[8];

        ByteArrayMarshaller.writeLong(value, byteArray, 1, true);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBBigEndian2()
    {
        long value = Long.MIN_VALUE;
        byte[] byteArray = new byte[7];

        ByteArrayMarshaller.writeLong(value, byteArray, 0, true);
    }

    @Test
    public void testMinimumBoundaryValue()
    {
        long value = Long.MIN_VALUE;
        int offset = 0;
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);
        resultArrayBigEndian[0] = (byte) 0x80;
        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);
        resultArrayLittleEndian[7] = (byte) 0x80;
        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testMaximumBoundaryValue()
    {
        long value = Long.MAX_VALUE;
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);
        resultArrayBigEndian[0] = (byte) 0x7f;
        for (int i = 1; i <= 7; i++)
            resultArrayBigEndian[i] = (byte) 0xff;
        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        resultArrayLittleEndian[7] = (byte) 0x7f;
        for (int i = 6; i >= 0; i--)
            resultArrayLittleEndian[i] = (byte) 0xff;

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testZero()
    {
        long value = 0;
        int offset = 0;

        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);
        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomLongSixteenHexDigits()
    {
        int NUMBER_OF_DIGITS = 16;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < SIXTEEN_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);
        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomLongFifteenHexDigits()
    {
        int NUMBER_OF_DIGITS = 15;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= SIXTEEN_DIGIT_MIN || value < FIFTEEN_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayBigEndian[i] = (byte) ((value >> 8 * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (8 * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomLongFourteenHexDigits()
    {
        int NUMBER_OF_DIGITS = 14;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FIFTEEN_DIGIT_MIN || value < FOURTEEN_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (8 * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomLongThirteenHexDigits()
    {
        int NUMBER_OF_DIGITS = 13;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FOURTEEN_DIGIT_MIN || value < THIRTEEN_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomLongTwelveHexDigits()
    {
        int NUMBER_OF_DIGITS = 12;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= THIRTEEN_DIGIT_MIN || value < TWELVE_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomLongElevenHexDigits()
    {
        int NUMBER_OF_DIGITS = 11;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= TWELVE_DIGIT_MIN || value < ELEVEN_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomLongTenHexDigits()
    {
        int NUMBER_OF_DIGITS = 10;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= ELEVEN_DIGIT_MIN || value < TEN_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomLongNineHexDigits()
    {
        int NUMBER_OF_DIGITS = 9;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= TEN_DIGIT_MIN || value < NINE_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomLongEightHexDigits()
    {
        int NUMBER_OF_DIGITS = 8;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= NINE_DIGIT_MIN || value < EIGHT_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomLongSevenHexDigits()
    {
        int NUMBER_OF_DIGITS = 7;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= EIGHT_DIGIT_MIN || value < SEVEN_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomLongSixHexDigits()
    {
        int NUMBER_OF_DIGITS = 6;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= SEVEN_DIGIT_MIN || value < SIX_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomLongFiveHexDigits()
    {
        int NUMBER_OF_DIGITS = 5;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= SIX_DIGIT_MIN || value < FIVE_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomLongFourHexDigits()
    {
        int NUMBER_OF_DIGITS = 4;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FIVE_DIGIT_MIN || value < FOUR_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomLongThreeHexDigits()
    {
        int NUMBER_OF_DIGITS = 3;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FOUR_DIGIT_MIN || value < THREE_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomLongTwoHexDigits()
    {
        int NUMBER_OF_DIGITS = 2;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= THREE_DIGIT_MIN || value < TWO_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomLongOneHexDigit()
    {
        int NUMBER_OF_DIGITS = 1;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= TWO_DIGIT_MIN || value < ONE_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    /*
     * Testing for negatives: When we say N digits here, we mean that we take a
     * positive long with N digits and use its negative in our test. Sign
     * extension would make all negatives have the same number of hex digits.
     */

    private byte signExtend(byte b)
    {
        /* Sign extension of bytes */
        if (b > 0)
            return (byte) (b * -1);
        return b;
    }

    @Test
    public void testRandomNegativeLongSixteenHexDigits()
    {
        int NUMBER_OF_DIGITS = 16;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < SIXTEEN_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomNegativeLongFifteenHexDigits()
    {
        int NUMBER_OF_DIGITS = 15;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= SIXTEEN_DIGIT_MIN || value < FIFTEEN_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomNegativeLongFourteenHexDigits()
    {
        int NUMBER_OF_DIGITS = 14;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FIFTEEN_DIGIT_MIN || value < FOURTEEN_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomNegativeLongThirteenHexDigits()
    {
        int NUMBER_OF_DIGITS = 13;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FOURTEEN_DIGIT_MIN || value < THIRTEEN_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomNegativeLongTwelveHexDigits()
    {
        int NUMBER_OF_DIGITS = 12;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= THIRTEEN_DIGIT_MIN || value < TWELVE_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomNegativeLongElevenHexDigits()
    {
        int NUMBER_OF_DIGITS = 11;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= TWELVE_DIGIT_MIN || value < ELEVEN_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomNegativeLongTenHexDigits()
    {
        int NUMBER_OF_DIGITS = 10;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= ELEVEN_DIGIT_MIN || value < TEN_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomNegativeLongNineHexDigits()
    {
        int NUMBER_OF_DIGITS = 9;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= TEN_DIGIT_MIN || value < NINE_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomNegativeLongEightHexDigits()
    {
        int NUMBER_OF_DIGITS = 8;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= NINE_DIGIT_MIN || value < EIGHT_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomNegativeLongSevenHexDigits()
    {
        int NUMBER_OF_DIGITS = 7;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= EIGHT_DIGIT_MIN || value < SEVEN_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomNegativeLongSixHexDigits()
    {
        int NUMBER_OF_DIGITS = 6;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= SEVEN_DIGIT_MIN || value < SIX_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomNegativeLongFiveHexDigits()
    {
        int NUMBER_OF_DIGITS = 5;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= SIX_DIGIT_MIN || value < FIVE_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomNegativeLongFourHexDigits()
    {
        int NUMBER_OF_DIGITS = 4;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FIVE_DIGIT_MIN || value < FOUR_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomNegativeLongThreeHexDigits()
    {
        int NUMBER_OF_DIGITS = 3;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FOUR_DIGIT_MIN || value < THREE_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomNegativeLongTwoHexDigits()
    {
        int NUMBER_OF_DIGITS = 2;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= THREE_DIGIT_MIN || value < TWO_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomNegativeLongOneHexDigit()
    {
        int NUMBER_OF_DIGITS = 1;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= TWO_DIGIT_MIN || value < ONE_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testMaxOffset()
    {
        int NUMBER_OF_DIGITS = 4;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FIVE_DIGIT_MIN || value < FOUR_DIGIT_MIN);
        int offset = ARRAY_SIZE - 8;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                resultArrayBigEndian[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                resultArrayBigEndian[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                resultArrayLittleEndian[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                resultArrayLittleEndian[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomValidOffsets()
    {
        int count = 3;
        int offset;

        while (count != 0)
        {
            do
            {
                offset = randomGen.nextInt() % (ARRAY_SIZE - 9);
            } while (offset <= 0);

            int NUMBER_OF_DIGITS = 5;
            long value;
            do
            {
                value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
            } while (value >= SIX_DIGIT_MIN || value < FIVE_DIGIT_MIN);

            /* Big Endian */
            Arrays.fill(byteArray, (byte) 0x0);
            Arrays.fill(resultArrayBigEndian, (byte) 0x0);

            for (int i = 0; i < LONGSIZE; i++)
                resultArrayBigEndian[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

            ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);
            assertArrayEquals(resultArrayBigEndian, byteArray);

            /* Little Endian */
            Arrays.fill(byteArray, (byte) 0x0);
            Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

            for (int i = 0; i < LONGSIZE; i++)
                resultArrayLittleEndian[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

            ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
            assertArrayEquals(resultArrayLittleEndian, byteArray);

            count--;
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testInvalidOffset()
    {
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(5);
        } while (value < FIVE_DIGIT_MIN || value >= SIX_DIGIT_MIN);
        int offset = ARRAY_SIZE + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (8 * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test(expected = NullPointerException.class)
    public void testNullByteArray()
    {
        byte[] nullArray = null;
        int offset = 0;
        long value = Long.MIN_VALUE; // anything
        ByteArrayMarshaller.writeLong(value, nullArray, offset, isBigEndian);
        ByteArrayMarshaller.writeLong(value, nullArray, offset, isLittleEndian);

    }

}
