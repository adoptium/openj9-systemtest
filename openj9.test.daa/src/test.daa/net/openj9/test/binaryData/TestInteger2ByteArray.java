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

package net.openj9.test.binaryData;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ibm.dataaccess.ByteArrayMarshaller;

public class TestInteger2ByteArray
{
    static String outputFile;
    static Random randomGen;
    static final int ARRAY_SIZE = 256;
    static final int INTSIZE = 4; // number of bytes in a Java integer
    static final int BYTESIZE = 8; // number of bits in a byte!

    /* Minimum values for number of digits */
    static final long ONE_DIGIT_MIN = 0x1L;
    static final long TWO_DIGIT_MIN = 0x10L;
    static final long THREE_DIGIT_MIN = 0x100L;
    static final long FOUR_DIGIT_MIN = 0x1000L;
    static final long FIVE_DIGIT_MIN = 0x10000L;
    static final long SIX_DIGIT_MIN = 0x100000L;
    static final long SEVEN_DIGIT_MIN = 0x1000000L;
    static final long EIGHT_DIGIT_MIN = 0x10000000L;

    /* Attempting to increase the randomness */
    static int randomSeed = (int) (System.currentTimeMillis() % 16001);

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

    private int getRandomIntegerWithNHexDigits(int n)
    {
        assertTrue(n > 0 && n <= 8);
        int randomInt = 0;
        for (int k = 0; k < n; k++)
            randomInt = randomInt | (((randomGen.nextInt() >> k * 4) & 0x0f) << (n - k - 1) * 4);
        return randomInt < 0 ? randomInt * -1 : randomInt;
    }

    @Test
    public void testBoundaries()
    {
        int value = Integer.MIN_VALUE;
        byte[] byteArray = new byte[5];

        ByteArrayMarshaller.writeInt(value, byteArray, 0, true);
        ByteArrayMarshaller.writeInt(value, byteArray, 0, false);

        ByteArrayMarshaller.writeInt(value, byteArray, 1, true);
        ByteArrayMarshaller.writeInt(value, byteArray, 1, false);

    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBLittleEndian1()
    {
        int value = Integer.MIN_VALUE;
        byte[] byteArray = new byte[4];

        ByteArrayMarshaller.writeInt(value, byteArray, 1, false);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBLittleEndian2()
    {
        int value = Integer.MIN_VALUE;
        byte[] byteArray = new byte[3];

        ByteArrayMarshaller.writeInt(value, byteArray, 0, false);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBBigEndian1()
    {
        int value = Integer.MIN_VALUE;
        byte[] byteArray = new byte[4];

        ByteArrayMarshaller.writeInt(value, byteArray, 1, true);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBBigEndian2()
    {
        int value = Integer.MIN_VALUE;
        byte[] byteArray = new byte[3];

        ByteArrayMarshaller.writeInt(value, byteArray, 0, true);
    }

    @Test
    public void testMinimumBoundaryValue()
    {
        int value = Integer.MIN_VALUE;
        int offset = 0;
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);
        resultArrayBigEndian[0] = (byte) 0x80;
        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);
        resultArrayLittleEndian[3] = (byte) 0x80;
        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testMaximumBoundaryValue()
    {
        int value = Integer.MAX_VALUE;
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);
        resultArrayBigEndian[0] = (byte) 0x7f;
        resultArrayBigEndian[1] = (byte) 0xff;
        resultArrayBigEndian[2] = (byte) 0xff;
        resultArrayBigEndian[3] = (byte) 0xff;
        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);
        resultArrayLittleEndian[3] = (byte) 0x7f;
        resultArrayLittleEndian[2] = (byte) 0xff;
        resultArrayLittleEndian[1] = (byte) 0xff;
        resultArrayLittleEndian[0] = (byte) 0xff;
        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testZero()
    {
        int value = 0;
        int offset = 0;

        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);
        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomIntegerEightHexDigits()
    {
        int NUMBER_OF_DIGITS = 8;
        int value;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < EIGHT_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomIntegerSevenHexDigits()
    {
        int NUMBER_OF_DIGITS = 7;
        int value;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < SEVEN_DIGIT_MIN || value >= EIGHT_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomIntegerSixHexDigits()
    {
        int NUMBER_OF_DIGITS = 6;
        int value;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < SIX_DIGIT_MIN || value >= SEVEN_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomIntegerFiveDigits()
    {
        int NUMBER_OF_DIGITS = 5;
        int value;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < FIVE_DIGIT_MIN || value >= SIX_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomIntegerWithFourHexDigits()
    {
        int NUMBER_OF_DIGITS = 4;
        int value;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < FOUR_DIGIT_MIN || value >= FIVE_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomIntegerWithThreeHexDigits()
    {
        int NUMBER_OF_DIGITS = 3;
        int value;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < THREE_DIGIT_MIN || value >= FOUR_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomIntegerWithTwoHexDigits()
    {
        int NUMBER_OF_DIGITS = 2;
        int value;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < TWO_DIGIT_MIN || value >= THREE_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomIntegerWithOneHexDigits()
    {
        int NUMBER_OF_DIGITS = 1;
        int value;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < ONE_DIGIT_MIN || value >= TWO_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);
        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian);
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
    public void testRandomNegativeIntegerEightHexDigits()
    {
        int NUMBER_OF_DIGITS = 8;
        int value;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < EIGHT_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        int numBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i < INTSIZE - numBytes - 1)
                resultArrayBigEndian[i] = (byte) 0xff;
            else if (i == INTSIZE - numBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i > numBytes)
                resultArrayLittleEndian[i] = (byte) 0xff;
            else if (i == numBytes)
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomNegativeIntegerSevenHexDigits()
    {
        int NUMBER_OF_DIGITS = 7;
        int value;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < SEVEN_DIGIT_MIN || value >= EIGHT_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        int numBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i < INTSIZE - numBytes - 1)
                resultArrayBigEndian[i] = (byte) 0xff;
            else if (i == INTSIZE - numBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i > numBytes)
                resultArrayLittleEndian[i] = (byte) 0xff;
            else if (i == numBytes)
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomNegativeIntegerSixHexDigits()
    {
        int NUMBER_OF_DIGITS = 6;
        int value;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < SIX_DIGIT_MIN || value >= SEVEN_DIGIT_MIN);
        int offset = 0;

        value *= -1;
        int numBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i < INTSIZE - numBytes - 1)
                resultArrayBigEndian[i] = (byte) 0xff;
            else if (i == INTSIZE - numBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i > numBytes)
                resultArrayLittleEndian[i] = (byte) 0xff;
            else if (i == numBytes)
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomNegativeIntegerFiveDigits()
    {
        int NUMBER_OF_DIGITS = 5;
        int value;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < FIVE_DIGIT_MIN || value >= SIX_DIGIT_MIN);
        int offset = 0;

        value *= -1;
        int numBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i < INTSIZE - numBytes - 1)
                resultArrayBigEndian[i] = (byte) 0xff;
            else if (i == INTSIZE - numBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i > numBytes)
                resultArrayLittleEndian[i] = (byte) 0xff;
            else if (i == numBytes)
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomNegativeIntegerWithFourHexDigits()
    {
        int NUMBER_OF_DIGITS = 4;
        int value;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < FOUR_DIGIT_MIN || value >= FIVE_DIGIT_MIN);
        int offset = 0;

        value *= -1;
        int numBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i < INTSIZE - numBytes - 1)
                resultArrayBigEndian[i] = (byte) 0xff;
            else if (i == INTSIZE - numBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i > numBytes)
                resultArrayLittleEndian[i] = (byte) 0xff;
            else if (i == numBytes)
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomNegativeIntegerWithThreeHexDigits()
    {
        int NUMBER_OF_DIGITS = 3;
        int value;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < THREE_DIGIT_MIN || value >= FOUR_DIGIT_MIN);
        int offset = 0;

        value *= -1;
        int numBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i < INTSIZE - numBytes - 1)
                resultArrayBigEndian[i] = (byte) 0xff;
            else if (i == INTSIZE - numBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i > numBytes)
                resultArrayLittleEndian[i] = (byte) 0xff;
            else if (i == numBytes)
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomNegativeIntegerWithTwoHexDigits()
    {
        int NUMBER_OF_DIGITS = 2;
        int value;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < TWO_DIGIT_MIN || value >= THREE_DIGIT_MIN);
        int offset = 0;

        value *= -1;
        int numBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i < INTSIZE - numBytes - 1)
                resultArrayBigEndian[i] = (byte) 0xff;
            else if (i == INTSIZE - numBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i > numBytes)
                resultArrayLittleEndian[i] = (byte) 0xff;
            else if (i == numBytes)
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomNegativeIntegerWithOneHexDigit()
    {
        int NUMBER_OF_DIGITS = 1;
        int value;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < ONE_DIGIT_MIN || value >= TWO_DIGIT_MIN);
        int offset = 0;

        value *= -1;
        int numBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i < INTSIZE - numBytes - 1)
                resultArrayBigEndian[i] = (byte) 0xff;
            else if (i == INTSIZE - numBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i > numBytes)
                resultArrayLittleEndian[i] = (byte) 0xff;
            else if (i == numBytes)
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);
        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testMaxValidOffset()
    {
        int NUMBER_OF_DIGITS = 8;
        int value;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < EIGHT_DIGIT_MIN);
        int offset = ARRAY_SIZE - 4;

        value *= -1;

        int numBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i < INTSIZE - numBytes - 1)
                resultArrayBigEndian[offset + i] = (byte) 0xff;
            else if (i == INTSIZE - numBytes - 1)
                resultArrayBigEndian[offset + i] = signExtend((byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i > numBytes)
                resultArrayLittleEndian[offset + i] = (byte) 0xff;
            else if (i == numBytes)
                resultArrayLittleEndian[offset + i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                resultArrayLittleEndian[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomValidOffset()
    {
        int value = Integer.MAX_VALUE;
        /* Testing for three random offsets */
        int count = 3;
        while (count != 0)
        {
            int offset;
            do
            {
                offset = getRandomIntegerWithNHexDigits(3);
            } while (offset < THREE_DIGIT_MIN || offset >= FOUR_DIGIT_MIN);

            offset = offset % (ARRAY_SIZE - 5);
            /* Big Endian */
            Arrays.fill(byteArray, (byte) 0x0);
            Arrays.fill(resultArrayBigEndian, (byte) 0x0);
            resultArrayBigEndian[offset] = (byte) 0x7f;
            resultArrayBigEndian[offset + 1] = (byte) 0xff;
            resultArrayBigEndian[offset + 2] = (byte) 0xff;
            resultArrayBigEndian[offset + 3] = (byte) 0xff;
            ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian);
            assertArrayEquals(resultArrayBigEndian, byteArray);

            /* Little Endian */
            Arrays.fill(byteArray, (byte) 0x0);
            Arrays.fill(resultArrayLittleEndian, (byte) 0x0);
            resultArrayLittleEndian[offset + 3] = (byte) 0x7f;
            resultArrayLittleEndian[offset + 2] = (byte) 0xff;
            resultArrayLittleEndian[offset + 1] = (byte) 0xff;
            resultArrayLittleEndian[offset] = (byte) 0xff;
            ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian);
            assertArrayEquals(resultArrayLittleEndian, byteArray);

            count--;
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testInvalidOffset()
    {
        int value;
        do
        {
            value = getRandomIntegerWithNHexDigits(5);
        } while (value < FIVE_DIGIT_MIN || value >= SIX_DIGIT_MIN);
        int offset = ARRAY_SIZE - 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);
        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);
        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test(expected = NullPointerException.class)
    public void testNullByteArrayBigEndian()
    {
        byte[] nullArray = null;
        int offset = 0;
        int value = Integer.MIN_VALUE; // anything
        ByteArrayMarshaller.writeInt(value, nullArray, offset, isBigEndian);
        ByteArrayMarshaller.writeInt(value, nullArray, offset, isLittleEndian);
    }

    @Test(expected = NullPointerException.class)
    public void testNullByteArrayLittleEndian()
    {
        byte[] nullArray = null;
        int offset = 0;
        int value = Integer.MIN_VALUE; // anything
        ByteArrayMarshaller.writeInt(value, nullArray, offset, isLittleEndian);
    }

}
