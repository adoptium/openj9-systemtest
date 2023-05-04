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

import com.ibm.dataaccess.ByteArrayUnmarshaller;

public class TestByteArray2Integer
{
    static String outputFile;
    static Random randomGen;
    static int ARRAY_SIZE = 256;
    static int INTSIZE = 4; // number of bytes in a Java integer
    static int BYTESIZE = 8; // number of bits in a byte!

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

    private int genRandomOffset()
    {
        int offset = randomGen.nextInt() % (ARRAY_SIZE - 4);
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
        byte[] byteArray = new byte[5];

        ByteArrayUnmarshaller.readInt(byteArray, 0, true);
        ByteArrayUnmarshaller.readInt(byteArray, 0, false);

        ByteArrayUnmarshaller.readInt(byteArray, 1, true);
        ByteArrayUnmarshaller.readInt(byteArray, 1, false);

    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBLittleEndian1()
    {
        byte[] byteArray = new byte[4];

        ByteArrayUnmarshaller.readInt(byteArray, 1, false);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBLittleEndian2()
    {
        byte[] byteArray = new byte[3];

        ByteArrayUnmarshaller.readInt(byteArray, 0, false);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBBigEndian1()
    {
        byte[] byteArray = new byte[4];

        ByteArrayUnmarshaller.readInt(byteArray, 1, true);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBBigEndian2()
    {
        byte[] byteArray = new byte[3];

        ByteArrayUnmarshaller.readInt(byteArray, 0, true);
    }

    @Test
    public void testMinimumBoundaryValue()
    {
        int value = Integer.MIN_VALUE;
        int offset = 0;
        int resultInt;
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[0] = (byte) 0x80;
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);

        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[3] = (byte) 0x80;
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testMinimumBoundaryValueValidOffset()
    {
        int value = Integer.MIN_VALUE;
        int offset = genRandomOffset();
        int resultInt;
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[offset + 0] = (byte) 0x80;
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);

        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[offset + 3] = (byte) 0x80;
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testMaximumBoundaryValue()
    {
        int value = Integer.MAX_VALUE;
        int resultInt;
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[0] = (byte) 0x7f;
        byteArray[1] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[3] = (byte) 0x7f;
        byteArray[2] = (byte) 0xff;
        byteArray[1] = (byte) 0xff;
        byteArray[0] = (byte) 0xff;
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testMaximumBoundaryValueValidOffset()
    {
        int value = Integer.MAX_VALUE;
        int resultInt;
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[offset + 0] = (byte) 0x7f;
        byteArray[offset + 1] = (byte) 0xff;
        byteArray[offset + 2] = (byte) 0xff;
        byteArray[offset + 3] = (byte) 0xff;
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[offset + 3] = (byte) 0x7f;
        byteArray[offset + 2] = (byte) 0xff;
        byteArray[offset + 1] = (byte) 0xff;
        byteArray[offset + 0] = (byte) 0xff;
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testZero()
    {
        int value = 0;
        int resultInt;
        int offset = 0;

        Arrays.fill(byteArray, (byte) 0x0);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertArrayEquals(byteArray, byteArray);
    }

    @Test
    public void testZeroValidOffset()
    {
        int value = 0;
        int resultInt;
        int offset = genRandomOffset();

        Arrays.fill(byteArray, (byte) 0x0);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertArrayEquals(byteArray, byteArray);
    }

    @Test
    public void testRandomIntegerEightHexDigits()
    {
        // initialization
        int NUMBER_OF_DIGITS = 8;
        int value, resultInt1, resultInt2;

        byte[] leByteArray = new byte[ARRAY_SIZE];
        byte[] beByteArray = new byte[ARRAY_SIZE];

        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < EIGHT_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(beByteArray, (byte) 0x0);
        for (int i = 0; i < INTSIZE; i++)
            beByteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        /* Little Endian */
        Arrays.fill(leByteArray, (byte) 0x0);
        for (int i = 0; i < INTSIZE; i++)
            leByteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);
        // end of initialization

        // test execution
        resultInt1 = ByteArrayUnmarshaller.readInt(beByteArray, offset, isBigEndian);
        resultInt2 = ByteArrayUnmarshaller.readInt(leByteArray, offset, isLittleEndian);

        // verification
        assertEquals(value, resultInt1);
        assertEquals(value, resultInt2);
    }

    @Test
    public void testRandomIntegerEightHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 8;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < EIGHT_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomIntegerSevenHexDigits()
    {
        int NUMBER_OF_DIGITS = 7;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < SEVEN_DIGIT_MIN || value >= EIGHT_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomIntegerSevenHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 7;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < SEVEN_DIGIT_MIN || value >= EIGHT_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomIntegerSixHexDigits()
    {
        int NUMBER_OF_DIGITS = 6;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < SIX_DIGIT_MIN || value >= SEVEN_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomIntegerSixHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 6;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < SIX_DIGIT_MIN || value >= SEVEN_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomIntegerFiveDigits()
    {
        int NUMBER_OF_DIGITS = 5;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < FIVE_DIGIT_MIN || value >= SIX_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomIntegerFiveDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 5;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < FIVE_DIGIT_MIN || value >= SIX_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomIntegerWithFourHexDigits()
    {
        int NUMBER_OF_DIGITS = 4;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < FOUR_DIGIT_MIN || value >= FIVE_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomIntegerWithFourHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 4;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < FOUR_DIGIT_MIN || value >= FIVE_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomIntegerWithThreeHexDigits()
    {
        int NUMBER_OF_DIGITS = 3;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < THREE_DIGIT_MIN || value >= FOUR_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomIntegerWithThreeHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 3;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < THREE_DIGIT_MIN || value >= FOUR_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomIntegerWithTwoHexDigits()
    {
        int NUMBER_OF_DIGITS = 2;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < TWO_DIGIT_MIN || value >= THREE_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomIntegerWithTwoHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 2;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < TWO_DIGIT_MIN || value >= THREE_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomIntegerWithOneHexDigits()
    {
        int NUMBER_OF_DIGITS = 1;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < ONE_DIGIT_MIN || value >= TWO_DIGIT_MIN);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomIntegerWithOneHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 1;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < ONE_DIGIT_MIN || value >= TWO_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
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
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < EIGHT_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        int numBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i < INTSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == INTSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i > numBytes)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == numBytes)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomNegativeIntegerEightHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 8;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < EIGHT_DIGIT_MIN);
        int offset = genRandomOffset();

        value *= -1;

        int numBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i < INTSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == INTSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i > numBytes)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == numBytes)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomNegativeIntegerSevenHexDigits()
    {
        int NUMBER_OF_DIGITS = 7;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < SEVEN_DIGIT_MIN || value >= EIGHT_DIGIT_MIN);
        int offset = 0;

        value *= -1;

        int numBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i < INTSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == INTSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i > numBytes)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == numBytes)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomNegativeIntegerSevenHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 7;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < SEVEN_DIGIT_MIN || value >= EIGHT_DIGIT_MIN);
        int offset = genRandomOffset();

        value *= -1;

        int numBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i < INTSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == INTSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i > numBytes)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == numBytes)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomNegativeIntegerSixHexDigits()
    {
        int NUMBER_OF_DIGITS = 6;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < SIX_DIGIT_MIN || value >= SEVEN_DIGIT_MIN);
        int offset = 0;

        value *= -1;
        int numBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i < INTSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == INTSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i > numBytes)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == numBytes)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomNegativeIntegerSixHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 6;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < SIX_DIGIT_MIN || value >= SEVEN_DIGIT_MIN);
        int offset = genRandomOffset();

        value *= -1;
        int numBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i < INTSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == INTSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i > numBytes)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == numBytes)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomNegativeIntegerFiveDigits()
    {
        int NUMBER_OF_DIGITS = 5;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < FIVE_DIGIT_MIN || value >= SIX_DIGIT_MIN);
        int offset = 0;

        value *= -1;
        int numBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i < INTSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == INTSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i > numBytes)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == numBytes)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomNegativeIntegerFiveDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 5;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < FIVE_DIGIT_MIN || value >= SIX_DIGIT_MIN);
        int offset = genRandomOffset();

        value *= -1;
        int numBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i < INTSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == INTSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i > numBytes)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == numBytes)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomNegativeIntegerWithFourHexDigits()
    {
        int NUMBER_OF_DIGITS = 4;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < FOUR_DIGIT_MIN || value >= FIVE_DIGIT_MIN);
        int offset = 0;

        value *= -1;
        int numBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i < INTSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == INTSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i > numBytes)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == numBytes)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomNegativeIntegerWithFourHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 4;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < FOUR_DIGIT_MIN || value >= FIVE_DIGIT_MIN);
        int offset = genRandomOffset();

        value *= -1;
        int numBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i < INTSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == INTSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i > numBytes)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == numBytes)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomNegativeIntegerWithThreeHexDigits()
    {
        int NUMBER_OF_DIGITS = 3;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < THREE_DIGIT_MIN || value >= FOUR_DIGIT_MIN);
        int offset = 0;

        value *= -1;
        int numBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i < INTSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == INTSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i > numBytes)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == numBytes)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomNegativeIntegerWithThreeHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 3;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < THREE_DIGIT_MIN || value >= FOUR_DIGIT_MIN);
        int offset = genRandomOffset();

        value *= -1;
        int numBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i < INTSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == INTSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i > numBytes)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == numBytes)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomNegativeIntegerWithTwoHexDigits()
    {
        int NUMBER_OF_DIGITS = 2;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < TWO_DIGIT_MIN || value >= THREE_DIGIT_MIN);
        int offset = 0;

        value *= -1;
        int numBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i < INTSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == INTSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i > numBytes)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == numBytes)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomNegativeIntegerWithTwoHexDigitsValidOffset()
    {
        int NUMBER_OF_DIGITS = 2;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < TWO_DIGIT_MIN || value >= THREE_DIGIT_MIN);
        int offset = genRandomOffset();

        value *= -1;
        int numBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i < INTSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == INTSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i > numBytes)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == numBytes)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomNegativeIntegerWithOneHexDigit()
    {
        int NUMBER_OF_DIGITS = 1;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < ONE_DIGIT_MIN || value >= TWO_DIGIT_MIN);
        int offset = 0;

        value *= -1;
        int numBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i < INTSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == INTSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i > numBytes)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == numBytes)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testRandomNegativeIntegerWithOneHexDigitValidOffset()
    {
        int NUMBER_OF_DIGITS = 1;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < ONE_DIGIT_MIN || value >= TWO_DIGIT_MIN);
        int offset = genRandomOffset();

        value *= -1;
        int numBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i < INTSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == INTSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i > numBytes)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == numBytes)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test
    public void testMaxValidOffset()
    {
        int NUMBER_OF_DIGITS = 8;
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value < EIGHT_DIGIT_MIN);
        int offset = ARRAY_SIZE - 4;

        value *= -1;

        int numBytes = NUMBER_OF_DIGITS / 2 == 0 ? NUMBER_OF_DIGITS / 2 : NUMBER_OF_DIGITS / 2 + 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i < INTSIZE - numBytes - 1)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == INTSIZE - numBytes - 1)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < INTSIZE; i++)
            if (i > numBytes)
                byteArray[offset + i] = (byte) 0xff;
            else if (i == numBytes)
                byteArray[offset + i] = signExtend((byte) ((value >> BYTESIZE * i) & 0x0ff));
            else
                byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testInvalidOffset()
    {
        int value, resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(5);
        } while (value < FIVE_DIGIT_MIN || value >= SIX_DIGIT_MIN);
        int offset = genRandomOutofBoundsOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian);
        assertEquals(value, resultInt);
    }

    @Test(expected = NullPointerException.class)
    public void testNullByteArray()
    {
        byte[] nullArray = null;
        int offset = 0;
        ByteArrayUnmarshaller.readInt(nullArray, offset, isBigEndian);
        ByteArrayUnmarshaller.readInt(nullArray, offset, isLittleEndian);

    }

    @Test
    public void testIncrementalOnebyte()
    {
        int value, resultInt;

        for (int i = 0; i < ARRAY_SIZE; i++)
        {
            byteArray[i] = (byte) (i > ARRAY_SIZE / 2 ? -(i - ARRAY_SIZE / 2) : i);
        }

        for (int i = 0; i < ARRAY_SIZE / 2; i++)
        {
            value = i > ARRAY_SIZE / 2 ? -(i - ARRAY_SIZE / 2) : i;
            resultInt = ByteArrayUnmarshaller.readInt(byteArray, i, isBigEndian, 1, false);
            assertEquals(value, resultInt);
        }
    }
}
