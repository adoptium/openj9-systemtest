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

import com.ibm.dataaccess.ByteArrayUnmarshaller;

public class TestByteArray2Double
{
    static String outputFile;
    static Random randomGen;
    static final int ARRAY_SIZE = 256;
    static final int DOUBLESIZE = 8;
    static final int BYTESIZE = 8;
    static final long MANTISSA_MASK = 0x000FFFFFFFFFFFL;

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
    public void testMaxValue()
    {
        double value = Double.MAX_VALUE;
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test
    public void testMaxValueWithRandomValidOffset()
    {
        double value = Double.MAX_VALUE;
        long memoryWord = Double.doubleToLongBits(value);
        int offset = randomGen.nextInt() % (ARRAY_SIZE - 8);
        offset = offset > 0 ? offset : -offset;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testMaxValueWithInvalidOffset()
    {
        double value = Double.MAX_VALUE;
        long memoryWord = Double.doubleToLongBits(value);
        int offset = ARRAY_SIZE - 7;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test
    public void testBoundaries()
    {
        byte[] byteArray = new byte[8];

        ByteArrayUnmarshaller.readDouble(byteArray, 0, true);
        ByteArrayUnmarshaller.readDouble(byteArray, 0, false);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBLittleEndian1()
    {
        byte[] byteArray = new byte[8];

        ByteArrayUnmarshaller.readDouble(byteArray, 1, false);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBLittleEndian2()
    {
        byte[] byteArray = new byte[7];

        ByteArrayUnmarshaller.readDouble(byteArray, 0, false);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBBigEndian1()
    {
        byte[] byteArray = new byte[8];

        ByteArrayUnmarshaller.readDouble(byteArray, 1, true);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBBigEndian2()
    {
        byte[] byteArray = new byte[7];

        ByteArrayUnmarshaller.readDouble(byteArray, 0, true);
    }

    @Test
    public void testMinValue()
    {
        double value = Double.MIN_VALUE;
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test
    public void testMinValueRandomValidOffset()
    {
        double value = Double.MIN_VALUE;
        long memoryWord = Double.doubleToLongBits(value);
        int offset = randomGen.nextInt() % (ARRAY_SIZE - 8);
        offset = offset > 0 ? offset : -offset;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testMinValueInvalidOffset()
    {
        double value = Double.MIN_VALUE;
        long memoryWord = Double.doubleToLongBits(value);
        int offset = ARRAY_SIZE - 300;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test
    public void testPositiveInfinity()
    {
        double value = Double.POSITIVE_INFINITY;
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test
    public void testPositiveInfinityValidOffset()
    {
        double value = Double.POSITIVE_INFINITY;
        long memoryWord = Double.doubleToLongBits(value);

        int offset = randomGen.nextInt() % (ARRAY_SIZE - 8);
        offset = offset > 0 ? offset : -offset;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test
    public void testNegativeInfinity()
    {
        double value = Double.NEGATIVE_INFINITY;
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test
    public void testNegativeInfinityValidOffset()
    {
        double value = Double.NEGATIVE_INFINITY;
        long memoryWord = Double.doubleToLongBits(value);
        int offset = randomGen.nextInt() % (ARRAY_SIZE - 8);
        offset = offset > 0 ? offset : -offset;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test
    public void testNAN()
    {
        double value = Double.NaN;
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test
    public void testNANValidOffset()
    {
        double value = Double.NaN;
        long memoryWord = Double.doubleToLongBits(value);
        int offset = randomGen.nextInt() % (ARRAY_SIZE - 8);
        offset = offset > 0 ? offset : -offset;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test
    public void testZero()
    {
        double value = 0;
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test
    public void testPositiveDoubleMaxExponent()
    {
        double value = generateRandomDoubleWithExponentAndSign(Double.MAX_EXPONENT, false);
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test
    public void testPositiveDoubleMaxExponentValidOffset()
    {
        double value = generateRandomDoubleWithExponentAndSign(Double.MAX_EXPONENT, false);
        long memoryWord = Double.doubleToLongBits(value);
        int offset = randomGen.nextInt() % (ARRAY_SIZE - 8);
        offset = offset > 0 ? offset : -offset;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test
    public void testNegativeDoubleMaxExponent()
    {
        double value = generateRandomDoubleWithExponentAndSign(Double.MAX_EXPONENT, true);
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test
    public void testNegativeDoubleMaxExponentValidOffset()
    {
        double value = generateRandomDoubleWithExponentAndSign(Double.MAX_EXPONENT, true);
        long memoryWord = Double.doubleToLongBits(value);
        int offset = randomGen.nextInt() % (ARRAY_SIZE - 8);
        offset = offset > 0 ? offset : -offset;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test
    public void testPositiveDoubleMinExponent()
    {
        double value = generateRandomDoubleWithExponentAndSign(Double.MIN_EXPONENT, false);
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test
    public void testPositiveDoubleMinExponentValidOffset()
    {
        double value = generateRandomDoubleWithExponentAndSign(Double.MIN_EXPONENT, false);
        long memoryWord = Double.doubleToLongBits(value);
        int offset = randomGen.nextInt() % (ARRAY_SIZE - 8);
        offset = offset > 0 ? offset : -offset;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test
    public void testNegativeDoubleMinExponent()
    {
        double value = generateRandomDoubleWithExponentAndSign(Double.MIN_EXPONENT, true);
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test
    public void testNegativeDoubleMinExponentValidOffset()
    {
        double value = generateRandomDoubleWithExponentAndSign(Double.MIN_EXPONENT, true);
        long memoryWord = Double.doubleToLongBits(value);
        int offset = randomGen.nextInt() % (ARRAY_SIZE - 8);
        offset = offset > 0 ? offset : -offset;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test
    public void testZeroExponent()
    {
        double value = generateRandomDoubleWithExponentAndSign(0, true);
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test
    public void testZeroExponentValidOffset()
    {
        double value = generateRandomDoubleWithExponentAndSign(0, true);
        long memoryWord = Double.doubleToLongBits(value);
        int offset = randomGen.nextInt() % (ARRAY_SIZE - 8);
        offset = offset > 0 ? offset : -offset;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    /*
     * We have covered all possible boundary cases. We'll now generate doubles
     * with a few random exponents and test their reverse conversions. Two
     * positve and two negative exponents.
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

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);
        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test
    public void testRandomPositiveDoublePositveExponentValidOffset()
    {
        int exponent = randomGen.nextInt() % Double.MAX_EXPONENT;
        if (exponent > 0)
            exponent *= -1;
        double value = generateRandomDoubleWithExponentAndSign(exponent, false);
        long memoryWord = Double.doubleToLongBits(value);

        int offset = randomGen.nextInt() % (ARRAY_SIZE - 8);
        offset = offset > 0 ? offset : -offset;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test
    public void testRandomPositiveDoubleNegativeExponent()
    {
        // initialization
        int exponent = randomGen.nextInt() % Double.MAX_EXPONENT;
        byte[] beByteArray = new byte[ARRAY_SIZE];
        byte[] leByteArray = new byte[ARRAY_SIZE];
        if (exponent > 0)
            exponent *= -1;

        double value = generateRandomDoubleWithExponentAndSign(exponent, false);
        long memoryWord = Double.doubleToLongBits(value);
        int offset = 0;

        /* Big Endian */
        Arrays.fill(beByteArray, (byte) 0x0);
        for (int i = 0; i < DOUBLESIZE; i++)
            beByteArray[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        /* Little Endian */
        Arrays.fill(leByteArray, (byte) 0x0);
        for (int i = 0; i < DOUBLESIZE; i++)
            leByteArray[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);
        // end of initialization

        // test execution
        double resultDouble = ByteArrayUnmarshaller.readDouble(beByteArray, offset, isBigEndian);
        double resultDouble2 = ByteArrayUnmarshaller.readDouble(leByteArray, offset, isLittleEndian);

        // verification

        assertEquals(value, resultDouble, 0.0);
        assertEquals(value, resultDouble2, 0.0);
    }

    @Test
    public void testRandomPositiveDoubleNegativeExponentValidOffset()
    {
        int exponent = randomGen.nextInt() % Double.MAX_EXPONENT;
        if (exponent > 0)
            exponent *= -1;

        double value = generateRandomDoubleWithExponentAndSign(exponent, false);
        long memoryWord = Double.doubleToLongBits(value);
        int offset = randomGen.nextInt() % (ARRAY_SIZE - 8);
        offset = offset > 0 ? offset : -offset;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
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

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test
    public void testRandomNegativeDoublePositiveExponentValidOffset()
    {
        int exponent = randomGen.nextInt() % Double.MAX_EXPONENT;
        if (exponent < 0)
            exponent *= -1;

        double value = generateRandomDoubleWithExponentAndSign(exponent, true);
        long memoryWord = Double.doubleToLongBits(value);
        int offset = randomGen.nextInt() % (ARRAY_SIZE - 8);
        offset = offset > 0 ? offset : -offset;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
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

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test
    public void testRandomNegativeDoubleNegativeExponentValidOffset()
    {
        int exponent = randomGen.nextInt() % Double.MAX_EXPONENT;
        if (exponent > 0)
            exponent *= -1;
        double value = generateRandomDoubleWithExponentAndSign(exponent, true);
        long memoryWord = Double.doubleToLongBits(value);
        int offset = randomGen.nextInt() % (ARRAY_SIZE - 8);
        offset = offset > 0 ? offset : -offset;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

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

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * (DOUBLESIZE - 1 - i)) & 0x0ff);

        double resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);

        assertEquals(value, resultDouble, 0.0);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < DOUBLESIZE; i++)
            byteArray[offset + i] = (byte) ((memoryWord >> BYTESIZE * i) & 0x0ff);

        resultDouble = ByteArrayUnmarshaller.readDouble(byteArray, offset, isLittleEndian);

        assertEquals(value, resultDouble, 0.0);
    }

    @Test(expected = NullPointerException.class)
    public void testNullByteArray()
    {
        int offset = 0;
        byte[] nullByteArray = null;

        ByteArrayUnmarshaller.readDouble(nullByteArray, offset, isBigEndian);
        ByteArrayUnmarshaller.readDouble(nullByteArray, offset, isLittleEndian);
    }

    @Test
    public void testIncrementalOnebyte()
    {
        int offset = 0;
        for (int i = 0; i < ARRAY_SIZE; i++)
        {
            byteArray[i] = (byte) (double) (i > ARRAY_SIZE / 2 ? -(i - ARRAY_SIZE / 2) : i);
        }

        for (int i = 0; i < ARRAY_SIZE; i++)
        {
            ByteArrayUnmarshaller.readDouble(byteArray, offset, isBigEndian);
        }
    }
}
