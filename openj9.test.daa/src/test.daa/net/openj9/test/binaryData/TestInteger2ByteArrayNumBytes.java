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

public class TestInteger2ByteArrayNumBytes
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

    private int getRandomNumBytes()
    {
        int rn = randomGen.nextInt() % 5;
        return (rn < 0) ? rn * -1 : rn;
    }

    @Test
    public void testBoundaries()
    {
        int value = Integer.MIN_VALUE;
        byte[] byteArray = new byte[5];

        ByteArrayMarshaller.writeInt(value, byteArray, 0, true, 4);
        ByteArrayMarshaller.writeInt(value, byteArray, 0, false, 4);

        ByteArrayMarshaller.writeInt(value, byteArray, 1, true, 4);
        ByteArrayMarshaller.writeInt(value, byteArray, 1, false, 4);

    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBLittleEndian1()
    {
        int value = Integer.MIN_VALUE;
        byte[] byteArray = new byte[4];

        ByteArrayMarshaller.writeInt(value, byteArray, 1, false, 4);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBLittleEndian2()
    {
        int value = Integer.MIN_VALUE;
        byte[] byteArray = new byte[3];

        ByteArrayMarshaller.writeInt(value, byteArray, 0, false, 4);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBBigEndian1()
    {
        int value = Integer.MIN_VALUE;
        byte[] byteArray = new byte[4];

        ByteArrayMarshaller.writeInt(value, byteArray, 1, true, 4);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBBigEndian2()
    {
        int value = Integer.MIN_VALUE;
        byte[] byteArray = new byte[3];

        ByteArrayMarshaller.writeInt(value, byteArray, 0, true, 4);
    }

    @Test
    public void testMinimumBoundaryValue()
    {
        int value = Integer.MIN_VALUE;
        int offset = 0;

        int numBytes = getRandomNumBytes();
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);
        if (numBytes == 4)
            resultArrayBigEndian[0] = (byte) 0x80;
        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);
        if (numBytes == 4)
            resultArrayLittleEndian[3] = (byte) 0x80;
        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian, numBytes);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testMaximumBoundaryValue()
    {
        int value = Integer.MAX_VALUE;
        int offset = 0;

        int numBytes = getRandomNumBytes();
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);
        int idx = 0;
        switch (numBytes)
        {
            case 4:
                resultArrayBigEndian[idx++] = (byte) 0x7f;
            case 3:
                resultArrayBigEndian[idx++] = (byte) 0xff;
            case 2:
                resultArrayBigEndian[idx++] = (byte) 0xff;
            case 1:
                resultArrayBigEndian[idx] = (byte) 0xff;
        }
        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);
        idx = numBytes - 1;
        switch (numBytes)
        {
            case 4:
                resultArrayLittleEndian[idx--] = (byte) 0x7f;
            case 3:
                resultArrayLittleEndian[idx--] = (byte) 0xff;
            case 2:
                resultArrayLittleEndian[idx--] = (byte) 0xff;
            case 1:
                resultArrayLittleEndian[idx] = (byte) 0xff;
        }
        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian, numBytes);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testZero()
    {
        int value = 0;
        int offset = 0;

        int numBytes = getRandomNumBytes();
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);
        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian, numBytes);
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

        int numBytes = getRandomNumBytes();
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian, numBytes);
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

        int numBytes = getRandomNumBytes();
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian, numBytes);
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

        int numBytes = getRandomNumBytes();
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);
        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        value *= -1;

        int nBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[i] = (byte) 0xff;
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes)
                resultArrayLittleEndian[i] = (byte) 0xff;
            else if (i == nBytes)
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        value *= -1;

        int nBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[i] = (byte) 0xff;
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes)
                resultArrayLittleEndian[i] = (byte) 0xff;
            else if (i == nBytes)
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        value *= -1;
        int nBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[i] = (byte) 0xff;
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes)
                resultArrayLittleEndian[i] = (byte) 0xff;
            else if (i == nBytes)
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        value *= -1;
        int nBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[i] = (byte) 0xff;
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes)
                resultArrayLittleEndian[i] = (byte) 0xff;
            else if (i == nBytes)
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        value *= -1;
        int nBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[i] = (byte) 0xff;
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes)
                resultArrayLittleEndian[i] = (byte) 0xff;
            else if (i == nBytes)
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        value *= -1;
        int nBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[i] = (byte) 0xff;
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes)
                resultArrayLittleEndian[i] = (byte) 0xff;
            else if (i == nBytes)
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        value *= -1;
        int nBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[i] = (byte) 0xff;
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes)
                resultArrayLittleEndian[i] = (byte) 0xff;
            else if (i == nBytes)
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        value *= -1;
        int nBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[i] = (byte) 0xff;
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes)
                resultArrayLittleEndian[i] = (byte) 0xff;
            else if (i == nBytes)
                resultArrayLittleEndian[i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                resultArrayLittleEndian[i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);
        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian, numBytes);
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
        int numBytes = getRandomNumBytes();

        value *= -1;
        int nBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[offset + i] = (byte) 0xff;
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[offset + i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[offset + i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes)
                resultArrayLittleEndian[offset + i] = (byte) 0xff;
            else if (i == nBytes)
                resultArrayLittleEndian[offset + i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                resultArrayLittleEndian[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian, numBytes);
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

            int numBytes = getRandomNumBytes();

            offset = offset % (ARRAY_SIZE - 5);
            /* Big Endian */
            Arrays.fill(byteArray, (byte) 0x0);
            Arrays.fill(resultArrayBigEndian, (byte) 0x0);
            int idx = offset;
            switch (numBytes)
            {
                case 4:
                    resultArrayBigEndian[idx++] = (byte) 0x7f;
                case 3:
                    resultArrayBigEndian[idx++] = (byte) 0xff;
                case 2:
                    resultArrayBigEndian[idx++] = (byte) 0xff;
                case 1:
                    resultArrayBigEndian[idx] = (byte) 0xff;
            }
            ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian, numBytes);
            assertArrayEquals(resultArrayBigEndian, byteArray);

            /* Little Endian */
            Arrays.fill(byteArray, (byte) 0x0);
            Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

            switch (numBytes)
            {
                case 4:
                    resultArrayLittleEndian[offset + 3] = (byte) 0x7f;
                case 3:
                    resultArrayLittleEndian[offset + 2] = (byte) 0xff;
                case 2:
                    resultArrayLittleEndian[offset + 1] = (byte) 0xff;
                case 1:
                    resultArrayLittleEndian[offset] = (byte) 0xff;
            }
            ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian, numBytes);
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
        int offset = ARRAY_SIZE;
        int numBytes = 3;
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);
        resultArrayBigEndian[0] = (byte) ((value >> 16) & 0x0ff);
        resultArrayBigEndian[1] = (byte) ((value >> 8) & 0x0ff);
        resultArrayBigEndian[2] = (byte) (value & 0x0ff);
        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian, numBytes);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);
        resultArrayLittleEndian[2] = (byte) ((value >> 16) & 0x0ff);
        resultArrayLittleEndian[1] = (byte) ((value >> 8) & 0x0ff);
        resultArrayLittleEndian[0] = (byte) (value & 0x0ff);
        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian, numBytes);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testWritePastEndOfArrayTwoBytes()
    {
        /*
         * Use a valid offset, but set numBytes to exceed write past end of
         * array - AIOB expected.
         */
        ByteArrayMarshaller.writeInt(678, byteArray, byteArray.length - 1, isBigEndian, 2);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testWritePastEndOfArrayThreeBytes()
    {
        /*
         * Use a valid offset, but set numBytes to exceed write past end of
         * array - AIOB expected.
         */
        ByteArrayMarshaller.writeInt(1234, byteArray, byteArray.length - 2, isLittleEndian, 3);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testWritePastEndOfArrayFourBytes()
    {
        /*
         * Use a valid offset, but set numBytes to exceed write past end of
         * array - AIOB expected.
         */
        ByteArrayMarshaller.writeInt(123456, byteArray, byteArray.length - 3, isBigEndian, 4);
    }

    @Test(expected = NullPointerException.class)
    public void testNullByteArray()
    {
        byte[] nullArray = null;
        int offset = 0; // anything
        int numBytes = 1; // non-zero
        int value = Integer.MIN_VALUE; // anything
        ByteArrayMarshaller.writeInt(value, nullArray, offset, isBigEndian, numBytes);
        ByteArrayMarshaller.writeInt(value, nullArray, offset, isLittleEndian, numBytes);
    }

    /*
     * Test with indices which are valid only with numBytes other than the
     * default
     */
    @Test
    public void testValidIndexForTwoNumBytes()
    {
        int NUMBER_OF_DIGITS = 8;
        int value;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < EIGHT_DIGIT_MIN);
        int offset = ARRAY_SIZE - 2;
        int numBytes = 2;

        value *= -1;
        int nBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[offset + i] = (byte) 0xff;
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[offset + i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[offset + i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes)
                resultArrayLittleEndian[offset + i] = (byte) 0xff;
            else if (i == nBytes)
                resultArrayLittleEndian[offset + i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                resultArrayLittleEndian[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian, numBytes);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testValidIndexForOneNumBytes()
    {
        int NUMBER_OF_DIGITS = 8;
        int value;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < EIGHT_DIGIT_MIN);
        int offset = ARRAY_SIZE - 1;
        int numBytes = 1;

        value *= -1;
        int nBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[offset + i] = (byte) 0xff;
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[offset + i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[offset + i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes)
                resultArrayLittleEndian[offset + i] = (byte) 0xff;
            else if (i == nBytes)
                resultArrayLittleEndian[offset + i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                resultArrayLittleEndian[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian, numBytes);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testValidIndexForZeroNumBytes()
    {
        int NUMBER_OF_DIGITS = 8;
        int value;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < EIGHT_DIGIT_MIN);
        int offset = ARRAY_SIZE;
        int numBytes = 0;

        value *= -1;
        int nBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i < numBytes - nBytes - 1)
                resultArrayBigEndian[offset + i] = (byte) 0xff;
            else if (i == numBytes - nBytes - 1)
                resultArrayBigEndian[offset + i] = signExtend((byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff));
            else
                resultArrayBigEndian[offset + i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isBigEndian, numBytes);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            if (i > nBytes)
                resultArrayLittleEndian[offset + i] = (byte) 0xff;
            else if (i == nBytes)
                resultArrayLittleEndian[offset + i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                resultArrayLittleEndian[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeInt(value, byteArray, offset, isLittleEndian, numBytes);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    /* Try some invalid numBytes */
    @Test(expected = NullPointerException.class)
    public void testNegativeNumBytesBigEndian()
    {
        byte[] nullArray = null;
        int offset = 0; // anything
        int numBytes = -1;
        int value = Integer.MIN_VALUE; // anything
        ByteArrayMarshaller.writeInt(value, nullArray, offset, isBigEndian, numBytes);
    }

    @Test(expected = NullPointerException.class)
    public void testNegativeNumBytesLittleEndian()
    {
        byte[] nullArray = null;
        int offset = 0; // anything
        int numBytes = -1;
        int value = Integer.MIN_VALUE; // anything
        ByteArrayMarshaller.writeInt(value, nullArray, offset, isLittleEndian, numBytes);
    }

    @Test(expected = NullPointerException.class)
    public void testExcessiveNumBytesBigEndian()
    {
        byte[] nullArray = null;
        int offset = 0; // anything
        int numBytes = INTSIZE + 1;
        int value = Integer.MIN_VALUE; // anything
        ByteArrayMarshaller.writeInt(value, nullArray, offset, isBigEndian, numBytes);
    }

    @Test(expected = NullPointerException.class)
    public void testExcessiveNumBytesLittleEndian()
    {
        byte[] nullArray = null;
        int offset = 0; // anything
        int numBytes = INTSIZE + 1;
        int value = Integer.MIN_VALUE; // anything
        ByteArrayMarshaller.writeInt(value, nullArray, offset, isLittleEndian, numBytes);
    }
}
