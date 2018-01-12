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

public class TestDouble2ByteArray
{
    static String outputFile;
    static Random randomGen;
    static final int ARRAY_SIZE = 256;
    static final int DOUBLESIZE = 8;
    static final int BYTESIZE = 8;
    static final long MANTISSA_MASK = 0x000FFFFFFFFFFFL;

    /* Attempting to increase the randomness */
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

    private double generateRandomDoubleWithExponentAndSign(int exponent, boolean negative)
    {
        assert (exponent >= Double.MIN_EXPONENT && exponent <= Double.MAX_EXPONENT);
        exponent += 1023;
        exponent <<= 52;
        long rawBits = (randomGen.nextLong() & MANTISSA_MASK) | exponent;
        if (negative)
            return Double.longBitsToDouble(0x80000000000000L | rawBits);
        else
            return Double.longBitsToDouble(rawBits);
    }

    @Test
    public void testBoundaries()
    {
        double value = Double.MIN_VALUE;
        byte[] byteArray = new byte[9];

        ByteArrayMarshaller.writeDouble(value, byteArray, 0, true);
        ByteArrayMarshaller.writeDouble(value, byteArray, 0, false);

        ByteArrayMarshaller.writeDouble(value, byteArray, 1, true);
        ByteArrayMarshaller.writeDouble(value, byteArray, 1, false);

    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBLittleEndian1()
    {
        double value = Double.MIN_VALUE;
        byte[] byteArray = new byte[8];

        ByteArrayMarshaller.writeDouble(value, byteArray, 1, false);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBLittleEndian2()
    {
        double value = Double.MIN_VALUE;
        byte[] byteArray = new byte[7];

        ByteArrayMarshaller.writeDouble(value, byteArray, 0, false);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBBigEndian1()
    {
        double value = Double.MIN_VALUE;
        byte[] byteArray = new byte[8];

        ByteArrayMarshaller.writeDouble(value, byteArray, 1, true);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBBigEndian2()
    {
        double value = Double.MIN_VALUE;
        byte[] byteArray = new byte[7];

        ByteArrayMarshaller.writeDouble(value, byteArray, 0, true);
    }

    /* Test for MAX_VALUE */
    @Test
    public void testMaxValue()
    {
        double value = Double.MAX_VALUE;
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayBigEndian[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);

    }

    /* Test for MIN_VALUE */
    @Test
    public void testMinValue()
    {
        double value = Double.MIN_VALUE;
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayBigEndian[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    /* Test for Positive Infinity */
    @Test
    public void testPositiveInfinity()
    {
        double value = Double.POSITIVE_INFINITY;
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayBigEndian[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    /* Test for Negative Infinity */
    @Test
    public void testNegativeInfinity()
    {
        double value = Double.NEGATIVE_INFINITY;
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayBigEndian[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    /* Test for NAN */
    @Test
    public void testNAN()
    {
        double value = Double.NaN;
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayBigEndian[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    /* Test for Zero */
    @Test
    public void testZero()
    {
        double value = 0;
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayBigEndian[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    /* Test for random positive double with MAX_EXPONENT. */
    @Test
    public void testPositiveDoubleMaxExponent()
    {
        double value = generateRandomDoubleWithExponentAndSign(Double.MAX_EXPONENT, false);
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayBigEndian[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);

    }

    /* Test for random positive double with MAX_EXPONENT. */
    @Test
    public void testNegativeDoubleMaxExponent()
    {
        double value = generateRandomDoubleWithExponentAndSign(Double.MAX_EXPONENT, true);
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayBigEndian[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);

    }

    /* Test for random positive double with MIN_EXPONENT */
    @Test
    public void testPositiveDoubleMinExponent()
    {
        double value = generateRandomDoubleWithExponentAndSign(Double.MIN_EXPONENT, false);
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayBigEndian[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);

    }

    /* Test for random negative double with MIN_EXPONENT */
    @Test
    public void testNegativeDoubleMinExponent()
    {
        double value = generateRandomDoubleWithExponentAndSign(Double.MIN_EXPONENT, true);
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayBigEndian[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    /* Test for zero exponent */
    @Test
    public void testZeroExponent()
    {
        double value = generateRandomDoubleWithExponentAndSign(0, true);
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayBigEndian[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    /*
     * We have covered all possible boundary cases. We'll now generate doubles
     * with a few random exponents. Two positve and two negative exponents.
     */

    @Test
    public void testRandomPositiveDoublePositveExponent()
    {
        int exponent = randomGen.nextInt() % Double.MAX_EXPONENT;
        if (exponent > 0)
            exponent *= -1;
        double value = generateRandomDoubleWithExponentAndSign(exponent, false);
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayBigEndian[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);

    }

    @Test
    public void testRandomPositiveNegativeExponent()
    {
        int exponent = randomGen.nextInt() % Double.MAX_EXPONENT;
        if (exponent > 0)
            exponent *= -1;

        double value = generateRandomDoubleWithExponentAndSign(exponent, false);
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayBigEndian[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);

    }

    @Test
    public void testRandomNegativeDoublePositiveExponent()
    {
        int exponent = randomGen.nextInt() % Double.MAX_EXPONENT;
        if (exponent < 0)
            exponent *= -1;

        double value = generateRandomDoubleWithExponentAndSign(exponent, true);
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayBigEndian[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);

    }

    @Test
    public void testRandomNegativeDoubleNegativeExponent()
    {
        int exponent = randomGen.nextInt() % Double.MAX_EXPONENT;
        if (exponent > 0)
            exponent *= -1;
        double value = generateRandomDoubleWithExponentAndSign(exponent, true);
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayBigEndian[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayLittleEndian[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);

    }

    /* Testing for the maximum valid offset */
    @Test
    public void testMaximumValidOffset()
    {
        int exponent = randomGen.nextInt() % Double.MAX_EXPONENT;
        if (exponent < 0)
            exponent *= -1;
        double value = generateRandomDoubleWithExponentAndSign(exponent, true);
        long memoryWord = Double.doubleToLongBits(value);
        int offset = ARRAY_SIZE - 8;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayBigEndian[offset + i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayLittleEndian[offset + i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);

    }

    @Test
    public void testRandomValidOffset()
    {
        int exponent = randomGen.nextInt() % Double.MAX_EXPONENT;
        if (exponent < 0)
            exponent *= -1;
        double value = generateRandomDoubleWithExponentAndSign(exponent, true);
        long memoryWord = Double.doubleToLongBits(value);

        /* Testing for three random offsets */
        int count = 3;
        while (count != 0)
        {

            int offset = randomGen.nextInt() % (ARRAY_SIZE - 8);
            offset = offset < 0 ? -1 * offset : offset;

            /* Big Endian */
            Arrays.fill(byteArray, (byte) 0x0);
            Arrays.fill(resultArrayBigEndian, (byte) 0x0);

            for (int i = 0; i < DOUBLESIZE; i++)
                resultArrayBigEndian[offset + i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

            ByteArrayMarshaller.writeDouble(value, byteArray, offset, isBigEndian);
            assertArrayEquals(resultArrayBigEndian, byteArray);

            /* Little Endian */
            Arrays.fill(byteArray, (byte) 0x0);
            Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

            for (int i = 0; i < DOUBLESIZE; i++)
                resultArrayLittleEndian[offset + i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

            ByteArrayMarshaller.writeDouble(value, byteArray, offset, isLittleEndian);
            assertArrayEquals(resultArrayLittleEndian, byteArray);

            count--;
        }
    }

    /* Testing for an invalid offset */
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testInvalidOffset()
    {
        int exponent = randomGen.nextInt() % Double.MAX_EXPONENT;
        if (exponent < 0)
            exponent *= -1;
        double value = generateRandomDoubleWithExponentAndSign(exponent, true);
        long memoryWord = Double.doubleToLongBits(value);
        int offset = ARRAY_SIZE - 3;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayBigEndian[offset + i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            resultArrayLittleEndian[offset + i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        ByteArrayMarshaller.writeDouble(value, byteArray, offset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    /* Test for null byte array */
    @Test(expected = NullPointerException.class)
    public void testNullByteArray()
    {
        double value = 0;
        int offset = 0;
        byte[] nullByteArray = null;

        ByteArrayMarshaller.writeDouble(value, nullByteArray, offset, isBigEndian);
        ByteArrayMarshaller.writeDouble(value, nullByteArray, offset, isLittleEndian);
    }

}
