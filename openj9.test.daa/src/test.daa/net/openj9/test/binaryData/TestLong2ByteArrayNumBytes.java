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

package net.openj9.test.binaryData;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ibm.dataaccess.ByteArrayMarshaller;

public class TestLong2ByteArrayNumBytes
{
    static String outputFile;
    static Random randomGen;
    static final int ARRAY_SIZE = 256;
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

    private int getRandomNumBytes()
    {
        int rn = randomGen.nextInt() % 9;
        return (rn < 0) ? rn * -1 : rn;
    }

    @Test
    public void testMinimumBoundaryValue()
    {
        long value = Long.MIN_VALUE;
        int offset = 0;
        int numBytes = getRandomNumBytes();
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);
        if (numBytes == 8)
            resultArrayBigEndian[0] = (byte) 0x80;
        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);
        if (numBytes == 8)
            resultArrayLittleEndian[7] = (byte) 0x80;
        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testBoundaries()
    {
        long value = Long.MIN_VALUE;
        byte[] byteArray = new byte[9];

        ByteArrayMarshaller.writeLong(value, byteArray, 0, true, 8);
        ByteArrayMarshaller.writeLong(value, byteArray, 0, false, 8);

        ByteArrayMarshaller.writeLong(value, byteArray, 1, true, 8);
        ByteArrayMarshaller.writeLong(value, byteArray, 1, false, 8);

    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBLittleEndian1()
    {
        long value = Long.MIN_VALUE;
        byte[] byteArray = new byte[8];

        ByteArrayMarshaller.writeLong(value, byteArray, 1, false, 8);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBLittleEndian2()
    {
        long value = Long.MIN_VALUE;
        byte[] byteArray = new byte[7];

        ByteArrayMarshaller.writeLong(value, byteArray, 0, false, 8);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBBigEndian1()
    {
        long value = Long.MIN_VALUE;
        byte[] byteArray = new byte[8];

        ByteArrayMarshaller.writeLong(value, byteArray, 1, true, 8);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBBigEndian2()
    {
        long value = Long.MIN_VALUE;
        byte[] byteArray = new byte[7];

        ByteArrayMarshaller.writeLong(value, byteArray, 0, true, 8);
    }

    @Test
    public void testMaximumBoundaryValue()
    {
        long value = Long.MAX_VALUE;
        int offset = 0;
        int numBytes = getRandomNumBytes();
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = numBytes - 1; i >= 0; i--)
        {
            resultArrayBigEndian[i] = (byte) 0xff;
            if (i == 0 && numBytes == 8)
                resultArrayBigEndian[i] = (byte) 0x7f;
            else
                resultArrayBigEndian[i] = (byte) 0xff;
        }
        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        int i = 0;
        for (; i < Math.min(numBytes, 7); i++)
            resultArrayLittleEndian[i] = (byte) 0xff;
        if (i < numBytes)
            resultArrayLittleEndian[i] = (byte) 0x7f;

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testZero()
    {
        long value = 0;
        int offset = 0;
        int numBytes = getRandomNumBytes();

        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);
        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);
        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayBigEndian[i] = (byte) ((value >> 8 * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (8 * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (8 * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int nBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == nBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int nBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == nBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int nBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == nBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int nBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == nBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int nBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == nBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int nBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == nBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int nBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == nBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int nBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == nBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int nBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == nBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int nBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == nBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int nBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == nBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int nBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == nBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int nBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == nBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int nBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == nBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int nBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == nBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int nBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[i] = (byte) (0x0ff);
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes) // sign extension
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == nBytes) // sign extension
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int nBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[offset + i] = (byte) (0x0ff);
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[offset + i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[offset + i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes) // sign extension
                resultArrayLittleEndian[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == nBytes) // sign extension
                resultArrayLittleEndian[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
            int numBytes = getRandomNumBytes();

            /* Big Endian */
            Arrays.fill(byteArray, (byte) 0x0);
            Arrays.fill(resultArrayBigEndian, (byte) 0x0);

            for (int i = 0; i < numBytes; i++)
                resultArrayBigEndian[offset + i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

            ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
            assertArrayEquals(resultArrayBigEndian, byteArray);

            /* Little Endian */
            Arrays.fill(byteArray, (byte) 0x0);
            Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

            for (int i = 0; i < numBytes; i++)
                resultArrayLittleEndian[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

            ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes(); // must be non-zero
        if (numBytes == 0)
            numBytes = 1;
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> (8 * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test(expected = NullPointerException.class)
    public void testNullByteArray()
    {
        byte[] nullArray = null;
        int offset = 0;
        int numBytes = getRandomNumBytes(); // anything non-zero
        if (numBytes == 0)
            numBytes = 1;
        long value = Long.MIN_VALUE; // anything
        ByteArrayMarshaller.writeLong(value, nullArray, offset, isBigEndian, numBytes);
        ByteArrayMarshaller.writeLong(value, nullArray, offset, isLittleEndian, numBytes);

    }

    @Test
    public void testValidIndexForSevenNumBytes()
    {
        int NUMBER_OF_DIGITS = 4;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FIVE_DIGIT_MIN || value < FOUR_DIGIT_MIN);
        int offset = ARRAY_SIZE - 7;
        int numBytes = 7;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int nBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[offset + i] = (byte) (0x0ff);
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[offset + i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[offset + i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes) // sign extension
                resultArrayLittleEndian[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == nBytes) // sign extension
                resultArrayLittleEndian[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testValidIndexForSixNumBytes()
    {
        int NUMBER_OF_DIGITS = 4;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FIVE_DIGIT_MIN || value < FOUR_DIGIT_MIN);
        int offset = ARRAY_SIZE - 6;
        int numBytes = 6;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int nBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[offset + i] = (byte) (0x0ff);
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[offset + i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[offset + i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes) // sign extension
                resultArrayLittleEndian[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == nBytes) // sign extension
                resultArrayLittleEndian[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testValidIndexForFiveNumBytes()
    {
        int NUMBER_OF_DIGITS = 4;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FIVE_DIGIT_MIN || value < FOUR_DIGIT_MIN);
        int offset = ARRAY_SIZE - 5;
        int numBytes = 5;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int nBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[offset + i] = (byte) (0x0ff);
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[offset + i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[offset + i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes) // sign extension
                resultArrayLittleEndian[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == nBytes) // sign extension
                resultArrayLittleEndian[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testValidIndexForFourNumBytes()
    {
        int NUMBER_OF_DIGITS = 4;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FIVE_DIGIT_MIN || value < FOUR_DIGIT_MIN);
        int offset = ARRAY_SIZE - 4;
        int numBytes = 4;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int nBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[offset + i] = (byte) (0x0ff);
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[offset + i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[offset + i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes) // sign extension
                resultArrayLittleEndian[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == nBytes) // sign extension
                resultArrayLittleEndian[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testValidIndexForThreeNumBytes()
    {
        int NUMBER_OF_DIGITS = 4;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FIVE_DIGIT_MIN || value < FOUR_DIGIT_MIN);
        int offset = ARRAY_SIZE - 3;
        int numBytes = 3;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int nBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[offset + i] = (byte) (0x0ff);
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[offset + i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[offset + i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes) // sign extension
                resultArrayLittleEndian[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == nBytes) // sign extension
                resultArrayLittleEndian[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testValidIndexForTwoNumBytes()
    {
        int NUMBER_OF_DIGITS = 4;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FIVE_DIGIT_MIN || value < FOUR_DIGIT_MIN);
        int offset = ARRAY_SIZE - 2;
        int numBytes = 2;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int nBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[offset + i] = (byte) (0x0ff);
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[offset + i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[offset + i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes) // sign extension
                resultArrayLittleEndian[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == nBytes) // sign extension
                resultArrayLittleEndian[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testValidIndexForOneNumByte()
    {
        int NUMBER_OF_DIGITS = 4;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= FIVE_DIGIT_MIN || value < FOUR_DIGIT_MIN);
        int offset = ARRAY_SIZE - 1;
        int numBytes = 1;

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        int nBytes = NUMBER_OF_DIGITS % 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[offset + i] = (byte) (0x0ff);
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[offset + i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[offset + i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes) // sign extension
                resultArrayLittleEndian[offset + i] = (byte) ((value >> (BYTESIZE * i)) | 0x0ff);
            else if (i == nBytes) // sign extension
                resultArrayLittleEndian[offset + i] = signExtend((byte) ((value >> (BYTESIZE * i)) | 0x0ff));
            else
                resultArrayLittleEndian[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayMarshaller.writeLong(value, byteArray, offset, isLittleEndian, numBytes);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

}
