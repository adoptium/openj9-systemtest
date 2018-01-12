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

package net.openj9.test.binaryData;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ibm.dataaccess.ByteArrayMarshaller;
import com.ibm.dataaccess.ByteArrayUnmarshaller;

public class TestByteArray2Long
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

    int genRandomOffset()
    {
        int offset = randomGen.nextInt() % (ARRAY_SIZE - 8);
        return offset > 0 ? offset : -offset;
    }

    private int genRandomOutofBoundsOffset()
    {
        int offset;

        do
        {
            offset = randomGen.nextInt();
        } while (offset <= ARRAY_SIZE || offset < 0);
        return offset;
    }

    @Test
    public void testMinimumBoundaryValue()
    {
        long value = Long.MIN_VALUE;
        int offset = 0;
        long resultLong;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[0] = (byte) 0x80;
        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[7] = (byte) 0x80;
        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testBoundaries()
    {
        byte[] byteArray = new byte[9];

        ByteArrayUnmarshaller.readLong(byteArray, 0, true);
        ByteArrayUnmarshaller.readLong(byteArray, 0, false);

        ByteArrayUnmarshaller.readLong(byteArray, 1, true);
        ByteArrayUnmarshaller.readLong(byteArray, 1, false);

    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBLittleEndian1()
    {
        byte[] byteArray = new byte[8];

        ByteArrayUnmarshaller.readLong(byteArray, 1, false);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBLittleEndian2()
    {
        byte[] byteArray = new byte[7];

        ByteArrayUnmarshaller.readLong(byteArray, 0, false);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBBigEndian1()
    {
        byte[] byteArray = new byte[8];

        ByteArrayUnmarshaller.readLong(byteArray, 1, true);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBBigEndian2()
    {
        byte[] byteArray = new byte[7];

        ByteArrayUnmarshaller.readLong(byteArray, 0, true);
    }

    @Test
    public void testMinimumBoundaryValueValidOffset()
    {
        long value = Long.MIN_VALUE;
        int offset = 0;
        long resultLong;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[offset + 0] = (byte) 0x80;
        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[offset + 7] = (byte) 0x80;
        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testMaximumBoundaryValue()
    {
        long value = Long.MAX_VALUE;
        int offset = 0;
        long resultLong;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[offset + 0] = (byte) 0x7f;
        for (int i = 1; i <= 7; i++)
            byteArray[offset + i] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[offset + 7] = (byte) 0x7f;
        for (int i = 6; i >= 0; i--)
            byteArray[offset + i] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);

    }

    @Test
    public void testMaximumBoundaryValueValidOffset()
    {
        long value = Long.MAX_VALUE;
        int offset = genRandomOffset();
        long resultLong;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[offset + 0] = (byte) 0x7f;
        for (int i = 1; i <= 7; i++)
            byteArray[offset + i] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[offset + 7] = (byte) 0x7f;
        for (int i = 6; i >= 0; i--)
            byteArray[offset + i] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);

    }

    @Test
    public void testZero()
    {
        long value = 0;
        int offset = 0;
        long resultLong;

        Arrays.fill(byteArray, (byte) 0x0);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomLongSixteenHexDigits()
    {
        // initialization
        byte[] beByteArray = new byte[ARRAY_SIZE];
        byte[] leByteArray = new byte[ARRAY_SIZE];

        int NUMBER_OF_DIGITS = 16;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < SIXTEEN_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(beByteArray, (byte) 0x0);
        for (int i = 0; i < LONGSIZE; i++)
            beByteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        /* Little Endian */
        Arrays.fill(leByteArray, (byte) 0x0);
        for (int i = 0; i < LONGSIZE; i++)
            leByteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);
        // end of initialization

        // test execution
        long resultLong1 = ByteArrayUnmarshaller.readLong(beByteArray, offset, isBigEndian);
        long resultLong2 = ByteArrayUnmarshaller.readLong(leByteArray, offset, isLittleEndian);

        // verification
        assertEquals(value, resultLong1);
        assertEquals(value, resultLong2);
    }

    @Test
    public void testRandomLongSixteenHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 16;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < SIXTEEN_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomLongFifteenHexDigits()
    {
        int NUMBER_OF_DIGITS = 15;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= SIXTEEN_DIGIT_MIN || value < FIFTEEN_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> 8 * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (8 * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomLongFifteenHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 15;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= SIXTEEN_DIGIT_MIN || value < FIFTEEN_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> 8 * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (8 * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomLongFourteenHexDigits()
    {
        int NUMBER_OF_DIGITS = 14;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FIFTEEN_DIGIT_MIN || value < FOURTEEN_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (8 * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);

    }

    @Test
    public void testRandomLongFourteenHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 14;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FIFTEEN_DIGIT_MIN || value < FOURTEEN_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (8 * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);

    }

    @Test
    public void testRandomLongThirteenHexDigits()
    {
        int NUMBER_OF_DIGITS = 13;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FOURTEEN_DIGIT_MIN || value < THIRTEEN_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomLongThirteenHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 13;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FOURTEEN_DIGIT_MIN || value < THIRTEEN_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomLongTwelveHexDigits()
    {
        int NUMBER_OF_DIGITS = 12;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= THIRTEEN_DIGIT_MIN || value < TWELVE_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomLongTwelveHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 12;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= THIRTEEN_DIGIT_MIN || value < TWELVE_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomLongElevenHexDigits()
    {
        int NUMBER_OF_DIGITS = 11;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= TWELVE_DIGIT_MIN || value < ELEVEN_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);

    }

    @Test
    public void testRandomLongElevenHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 11;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= TWELVE_DIGIT_MIN || value < ELEVEN_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);

    }

    @Test
    public void testRandomLongTenHexDigits()
    {
        int NUMBER_OF_DIGITS = 10;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= ELEVEN_DIGIT_MIN || value < TEN_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);

    }

    @Test
    public void testRandomLongTenHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 10;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= ELEVEN_DIGIT_MIN || value < TEN_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);

    }

    @Test
    public void testRandomLongNineHexDigits()
    {
        int NUMBER_OF_DIGITS = 9;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= TEN_DIGIT_MIN || value < NINE_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomLongNineHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 9;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= TEN_DIGIT_MIN || value < NINE_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomLongEightHexDigits()
    {
        int NUMBER_OF_DIGITS = 8;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= NINE_DIGIT_MIN || value < EIGHT_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);

    }

    @Test
    public void testRandomLongEightHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 8;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= NINE_DIGIT_MIN || value < EIGHT_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);

    }

    @Test
    public void testRandomLongSevenHexDigits()
    {
        int NUMBER_OF_DIGITS = 7;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= EIGHT_DIGIT_MIN || value < SEVEN_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomLongSevenHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 7;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= EIGHT_DIGIT_MIN || value < SEVEN_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomLongSixHexDigits()
    {
        int NUMBER_OF_DIGITS = 6;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= SEVEN_DIGIT_MIN || value < SIX_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomLongSixHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 6;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= SEVEN_DIGIT_MIN || value < SIX_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomLongFiveHexDigits()
    {
        int NUMBER_OF_DIGITS = 5;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= SIX_DIGIT_MIN || value < FIVE_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomLongFiveHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 5;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= SIX_DIGIT_MIN || value < FIVE_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomLongFourHexDigits()
    {
        int NUMBER_OF_DIGITS = 4;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FIVE_DIGIT_MIN || value < FOUR_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomLongFourHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 4;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FIVE_DIGIT_MIN || value < FOUR_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomLongThreeHexDigits()
    {
        int NUMBER_OF_DIGITS = 3;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FOUR_DIGIT_MIN || value < THREE_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomLongThreeHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 3;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FOUR_DIGIT_MIN || value < THREE_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomLongTwoHexDigits()
    {
        int NUMBER_OF_DIGITS = 2;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= THREE_DIGIT_MIN || value < TWO_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);

    }

    @Test
    public void testRandomLongTwoHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 2;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= THREE_DIGIT_MIN || value < TWO_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);

    }

    @Test
    public void testRandomLongOneHexDigit()
    {
        int NUMBER_OF_DIGITS = 1;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= TWO_DIGIT_MIN || value < ONE_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomLongOneHexDigitValidOffset()
    {
        int NUMBER_OF_DIGITS = 1;
        long value, resultLong;

        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= TWO_DIGIT_MIN || value < ONE_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
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
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < SIXTEEN_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);

    }

    @Test
    public void testRandomNegativeLongSixteenHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 16;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < SIXTEEN_DIGIT_MIN);
        int offset = genRandomOffset();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);

    }

    @Test
    public void testRandomNegativeLongFifteenHexDigits()
    {
        int NUMBER_OF_DIGITS = 15;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= SIXTEEN_DIGIT_MIN || value < FIFTEEN_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongFifteenHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 15;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= SIXTEEN_DIGIT_MIN || value < FIFTEEN_DIGIT_MIN);
        int offset = genRandomOffset();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongFourteenHexDigits()
    {
        int NUMBER_OF_DIGITS = 14;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FIFTEEN_DIGIT_MIN || value < FOURTEEN_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongFourteenHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 14;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FIFTEEN_DIGIT_MIN || value < FOURTEEN_DIGIT_MIN);
        int offset = genRandomOffset();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongThirteenHexDigits()
    {
        int NUMBER_OF_DIGITS = 13;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FOURTEEN_DIGIT_MIN || value < THIRTEEN_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongThirteenHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 13;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FOURTEEN_DIGIT_MIN || value < THIRTEEN_DIGIT_MIN);
        int offset = genRandomOffset();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongTwelveHexDigits()
    {
        int NUMBER_OF_DIGITS = 12;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= THIRTEEN_DIGIT_MIN || value < TWELVE_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);

    }

    @Test
    public void testRandomNegativeLongTwelveHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 12;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= THIRTEEN_DIGIT_MIN || value < TWELVE_DIGIT_MIN);
        int offset = genRandomOffset();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);

    }

    @Test
    public void testRandomNegativeLongElevenHexDigits()
    {
        int NUMBER_OF_DIGITS = 11;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= TWELVE_DIGIT_MIN || value < ELEVEN_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongElevenHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 11;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= TWELVE_DIGIT_MIN || value < ELEVEN_DIGIT_MIN);
        int offset = genRandomOffset();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongTenHexDigits()
    {
        int NUMBER_OF_DIGITS = 10;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= ELEVEN_DIGIT_MIN || value < TEN_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongTenHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 10;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= ELEVEN_DIGIT_MIN || value < TEN_DIGIT_MIN);
        int offset = genRandomOffset();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongNineHexDigits()
    {
        int NUMBER_OF_DIGITS = 9;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= TEN_DIGIT_MIN || value < NINE_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongNineHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 9;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= TEN_DIGIT_MIN || value < NINE_DIGIT_MIN);
        int offset = genRandomOffset();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongEightHexDigits()
    {
        int NUMBER_OF_DIGITS = 8;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= NINE_DIGIT_MIN || value < EIGHT_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongEightHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 8;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= NINE_DIGIT_MIN || value < EIGHT_DIGIT_MIN);
        int offset = genRandomOffset();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongSevenHexDigits()
    {
        int NUMBER_OF_DIGITS = 7;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= EIGHT_DIGIT_MIN || value < SEVEN_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongSevenHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 7;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= EIGHT_DIGIT_MIN || value < SEVEN_DIGIT_MIN);
        int offset = genRandomOffset();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongSixHexDigits()
    {
        int NUMBER_OF_DIGITS = 6;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= SEVEN_DIGIT_MIN || value < SIX_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongSixHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 6;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= SEVEN_DIGIT_MIN || value < SIX_DIGIT_MIN);
        int offset = genRandomOffset();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongFiveHexDigits()
    {
        int NUMBER_OF_DIGITS = 5;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= SIX_DIGIT_MIN || value < FIVE_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongFiveHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 5;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= SIX_DIGIT_MIN || value < FIVE_DIGIT_MIN);
        int offset = genRandomOffset();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongFourHexDigits()
    {
        int NUMBER_OF_DIGITS = 4;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FIVE_DIGIT_MIN || value < FOUR_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongFourHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 4;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FIVE_DIGIT_MIN || value < FOUR_DIGIT_MIN);
        int offset = genRandomOffset();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongThreeHexDigits()
    {
        int NUMBER_OF_DIGITS = 3;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FOUR_DIGIT_MIN || value < THREE_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongThreeHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 3;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FOUR_DIGIT_MIN || value < THREE_DIGIT_MIN);
        int offset = genRandomOffset();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongTwoHexDigits()
    {
        int NUMBER_OF_DIGITS = 2;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= THREE_DIGIT_MIN || value < TWO_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongTwoHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 2;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= THREE_DIGIT_MIN || value < TWO_DIGIT_MIN);
        int offset = genRandomOffset();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongOneHexDigit()
    {
        int NUMBER_OF_DIGITS = 1;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= TWO_DIGIT_MIN || value < ONE_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testRandomNegativeLongOneHexDigitValidOffset()
    {
        int NUMBER_OF_DIGITS = 1;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= TWO_DIGIT_MIN || value < ONE_DIGIT_MIN);
        int offset = genRandomOffset();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test
    public void testMaxOffset()
    {
        int NUMBER_OF_DIGITS = 4;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FIVE_DIGIT_MIN || value < FOUR_DIGIT_MIN);
        int offset = ARRAY_SIZE - 8;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        int numBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < LONGSIZE; i++)
            if (i < LONGSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) (0x0ff);
            else if (i == LONGSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            if (i > numBytes) // sign extension
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == numBytes) // sign extension
                byteArray[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian);
        assertEquals(value, resultLong);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testInvalidOffset()
    {
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(5);
        } while (value < FIVE_DIGIT_MIN || value >= SIX_DIGIT_MIN);
        int offset = genRandomOutofBoundsOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (8 * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian);
        assertEquals(value, resultLong);
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
