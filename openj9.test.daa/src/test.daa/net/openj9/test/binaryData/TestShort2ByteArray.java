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

public class TestShort2ByteArray
{

    static String outputFile;
    static Random randomGen;
    static final int ARRAY_SIZE = 256;
    static final long ONE_DIGIT_MIN = 0x1L;
    static final long TWO_DIGIT_MIN = 0x10L;
    static final long THREE_DIGIT_MIN = 0x100L;
    static final long FOUR_DIGIT_MIN = 0x1000L;

    /* Attempting to increase the randomness */
    static int randomSeed = (int) (System.currentTimeMillis() % 6001);

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

    /* Returns positive shorts of desired length */
    private short getvalueWithNHexDigits(int n)
    {
        assertTrue(n > 0 && n <= 4);
        short value = 0;
        for (int k = 0; k < n; k++)
            value = (short) (value | (short) ((randomGen.nextInt() >> k * 4) & 0x0f) << (n - k - 1) * 4);
        return (short) (value < 0 ? value * -1 : value);
    }

    @Test
    public void testBoundaries()
    {
        short value = Short.MIN_VALUE;
        byte[] byteArray = new byte[3];

        ByteArrayMarshaller.writeShort(value, byteArray, 0, true);
        ByteArrayMarshaller.writeShort(value, byteArray, 0, false);

        ByteArrayMarshaller.writeShort(value, byteArray, 1, true);
        ByteArrayMarshaller.writeShort(value, byteArray, 1, false);

    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBLittleEndian1()
    {
        short value = Short.MIN_VALUE;
        byte[] byteArray = new byte[2];

        ByteArrayMarshaller.writeShort(value, byteArray, 1, false);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBLittleEndian2()
    {
        short value = Short.MIN_VALUE;
        byte[] byteArray = new byte[1];

        ByteArrayMarshaller.writeShort(value, byteArray, 0, false);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBBigEndian1()
    {
        short value = Short.MIN_VALUE;
        byte[] byteArray = new byte[2];

        ByteArrayMarshaller.writeShort(value, byteArray, 1, true);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBBigEndian2()
    {
        short value = Short.MIN_VALUE;
        byte[] byteArray = new byte[1];

        ByteArrayMarshaller.writeShort(value, byteArray, 0, true);
    }

    @Test
    public void testMinBoundaryValue()
    {

        short leftBoundary = (short) Short.MIN_VALUE;
        /* Big Endian */
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);
        Arrays.fill(byteArray, (byte) 0x0);
        resultArrayBigEndian[0] = (byte) 0x80;
        ByteArrayMarshaller.writeShort(leftBoundary, byteArray, 0, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);
        Arrays.fill(byteArray, (byte) 0x0);
        resultArrayLittleEndian[1] = (byte) 0x80;
        ByteArrayMarshaller.writeShort(leftBoundary, byteArray, 0, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testMaxBoundaryValue()
    {

        short rightBoundary = (short) Short.MAX_VALUE;
        /* Big Endian */
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);
        Arrays.fill(byteArray, (byte) 0x0);
        resultArrayBigEndian[0] = (byte) 0x7f;
        resultArrayBigEndian[1] = (byte) 0xff;
        ByteArrayMarshaller.writeShort(rightBoundary, byteArray, 0, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);
        Arrays.fill(byteArray, (byte) 0x0);
        resultArrayLittleEndian[1] = (byte) 0x7f;
        resultArrayLittleEndian[0] = (byte) 0xff;
        ByteArrayMarshaller.writeShort(rightBoundary, byteArray, 0, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testZero()
    {

        short zero = 0;
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);
        Arrays.fill(byteArray, (byte) 0x0);
        ByteArrayMarshaller.writeShort(zero, byteArray, 0, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);
        ByteArrayMarshaller.writeShort(zero, byteArray, 0, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomShortFourHexDigits()
    {

        short value;
        do
        {
            value = getvalueWithNHexDigits(4);
        } while (value < 0x1000);

        /* Big Endian */
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);
        Arrays.fill(byteArray, (byte) 0x0);
        resultArrayBigEndian[0] = (byte) ((value >> 8) & 0xff);
        resultArrayBigEndian[1] = (byte) (value & 0xff);
        ByteArrayMarshaller.writeShort(value, byteArray, 0, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);
        Arrays.fill(byteArray, (byte) 0x0);
        resultArrayLittleEndian[1] = (byte) ((value >> 8) & 0xff);
        resultArrayLittleEndian[0] = (byte) (value & 0xff);
        ByteArrayMarshaller.writeShort(value, byteArray, 0, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomShortThreeHexDigits()
    {
        // lp.addCallerName(Utils.getCallingMethod());

        short value;
        do
        {
            value = getvalueWithNHexDigits(3);
        } while (value >= 0x1000 || value < 0x100);

        /* Big Endian */
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);
        Arrays.fill(byteArray, (byte) 0x0);
        resultArrayBigEndian[0] = (byte) ((value >> 8) & 0xff);
        resultArrayBigEndian[1] = (byte) (value & 0xff);
        ByteArrayMarshaller.writeShort(value, byteArray, 0, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);
        Arrays.fill(byteArray, (byte) 0x0);
        resultArrayLittleEndian[1] = (byte) ((value >> 8) & 0xff);
        resultArrayLittleEndian[0] = (byte) (value & 0xff);
        ByteArrayMarshaller.writeShort(value, byteArray, 0, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomShortTwoHexDigits()
    {
        // lp.addCallerName(Utils.getCallingMethod());

        short value;
        do
        {
            value = getvalueWithNHexDigits(2);
        } while (value >= 0x100 || value < 0x10);

        /* Big Endian */
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);
        Arrays.fill(byteArray, (byte) 0x0);
        resultArrayBigEndian[0] = (byte) ((value >> 8) & 0xff);
        resultArrayBigEndian[1] = (byte) (value & 0xff);
        ByteArrayMarshaller.writeShort(value, byteArray, 0, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);
        Arrays.fill(byteArray, (byte) 0x0);
        resultArrayLittleEndian[1] = (byte) ((value >> 8) & 0xff);
        resultArrayLittleEndian[0] = (byte) (value & 0xff);
        ByteArrayMarshaller.writeShort(value, byteArray, 0, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomShortOneHexDigit()
    {
        // lp.addCallerName(Utils.getCallingMethod());

        short value;
        do
        {
            value = getvalueWithNHexDigits(1);
        } while (value >= 0x10 || value < 0x1);

        /* Big Endian */
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);
        Arrays.fill(byteArray, (byte) 0x0);
        resultArrayBigEndian[0] = (byte) ((value >> 8) & 0xff);
        resultArrayBigEndian[1] = (byte) (value & 0xff);
        ByteArrayMarshaller.writeShort(value, byteArray, 0, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);
        Arrays.fill(byteArray, (byte) 0x0);
        resultArrayLittleEndian[1] = (byte) ((value >> 8) & 0xff);
        resultArrayLittleEndian[0] = (byte) (value & 0xff);
        ByteArrayMarshaller.writeShort(value, byteArray, 0, isLittleEndian);
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
    public void testRandomNegativeShortFourHexDigits()
    {
        // lp.addCallerName(Utils.getCallingMethod());

        short value;
        do
        {
            value = getvalueWithNHexDigits(4);
        } while (value < FOUR_DIGIT_MIN);

        value *= -1;

        /* Big Endian */
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);
        Arrays.fill(byteArray, (byte) 0x0);
        resultArrayBigEndian[0] = signExtend((byte) ((value >> 8) & 0xff));
        resultArrayBigEndian[1] = (byte) (value & 0xff);
        ByteArrayMarshaller.writeShort(value, byteArray, 0, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);
        Arrays.fill(byteArray, (byte) 0x0);
        resultArrayLittleEndian[1] = signExtend((byte) ((value >> 8) & 0xff));
        resultArrayLittleEndian[0] = (byte) (value & 0xff);
        ByteArrayMarshaller.writeShort(value, byteArray, 0, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomNegativeShortThreeHexDigits()
    {
        // lp.addCallerName(Utils.getCallingMethod());

        short value;
        do
        {
            value = getvalueWithNHexDigits(3);
        } while (value >= FOUR_DIGIT_MIN || value < THREE_DIGIT_MIN);

        value *= -1;

        /* Big Endian */
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);
        Arrays.fill(byteArray, (byte) 0x0);
        resultArrayBigEndian[0] = signExtend((byte) ((value >> 8) & 0xff));
        resultArrayBigEndian[1] = (byte) (value & 0xff);
        ByteArrayMarshaller.writeShort(value, byteArray, 0, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);
        Arrays.fill(byteArray, (byte) 0x0);
        resultArrayLittleEndian[1] = signExtend((byte) ((value >> 8) & 0xff));
        resultArrayLittleEndian[0] = (byte) (value & 0xff);
        ByteArrayMarshaller.writeShort(value, byteArray, 0, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomNegativeShortTwoHexDigits()
    {
        // lp.addCallerName(Utils.getCallingMethod());

        short value;
        do
        {
            value = getvalueWithNHexDigits(2);
        } while (value >= 0x100 || value < 0x10);

        value *= -1;
        /* Big Endian */
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);
        Arrays.fill(byteArray, (byte) 0x0);
        resultArrayBigEndian[0] = signExtend((byte) ((value >> 8) & 0xff));
        resultArrayBigEndian[1] = (byte) (value & 0xff);
        ByteArrayMarshaller.writeShort(value, byteArray, 0, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);
        Arrays.fill(byteArray, (byte) 0x0);
        resultArrayLittleEndian[1] = signExtend((byte) ((value >> 8) & 0xff));
        resultArrayLittleEndian[0] = (byte) (value & 0xff);
        ByteArrayMarshaller.writeShort(value, byteArray, 0, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomNegativeShortOneHexDigit()
    {
        // lp.addCallerName(Utils.getCallingMethod());

        short value;
        do
        {
            value = getvalueWithNHexDigits(1);
        } while (value >= TWO_DIGIT_MIN || value < ONE_DIGIT_MIN);

        /* Big Endian */
        Arrays.fill(resultArrayBigEndian, (byte) 0x0);
        Arrays.fill(byteArray, (byte) 0x0);
        resultArrayBigEndian[0] = signExtend((byte) ((value >> 8) & 0xff));
        resultArrayBigEndian[1] = (byte) (value & 0xff);
        ByteArrayMarshaller.writeShort(value, byteArray, 0, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);
        Arrays.fill(byteArray, (byte) 0x0);
        resultArrayLittleEndian[1] = signExtend((byte) ((value >> 8) & 0xff));
        resultArrayLittleEndian[0] = (byte) (value & 0xff);
        ByteArrayMarshaller.writeShort(value, byteArray, 0, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testMaxOffset()
    {
        // lp.addCallerName(Utils.getCallingMethod());

        int maxOffset = byteArray.length - 2;
        short value;
        do
        {
            value = getvalueWithNHexDigits(4);
        } while (value < 0x1000);

        Arrays.fill(resultArrayBigEndian, (byte) 0x0);
        Arrays.fill(byteArray, (byte) 0x0);
        resultArrayBigEndian[maxOffset] = (byte) ((value >> 8) & 0xff);
        resultArrayBigEndian[maxOffset + 1] = (byte) (value & 0xff);
        ByteArrayMarshaller.writeShort(value, byteArray, maxOffset, isBigEndian);
        assertArrayEquals(resultArrayBigEndian, byteArray);

        /* Little Endian */
        Arrays.fill(resultArrayLittleEndian, (byte) 0x0);
        Arrays.fill(byteArray, (byte) 0x0);
        resultArrayLittleEndian[maxOffset + 1] = (byte) ((value >> 8) & 0xff);
        resultArrayLittleEndian[maxOffset] = (byte) (value & 0xff);
        ByteArrayMarshaller.writeShort(value, byteArray, maxOffset, isLittleEndian);
        assertArrayEquals(resultArrayLittleEndian, byteArray);
    }

    @Test
    public void testRandomNonZeroValidOffsets()
    {
        // lp.addCallerName(Utils.getCallingMethod());

        int offset;
        int count = 3;
        while (count != 0)
        {
            do
            {
                offset = randomGen.nextInt() % (ARRAY_SIZE - 3);
            } while (offset <= 0);

            short value;
            do
            {
                value = getvalueWithNHexDigits(4);
            } while (value < FOUR_DIGIT_MIN);

            Arrays.fill(resultArrayBigEndian, (byte) 0x0);
            Arrays.fill(byteArray, (byte) 0x0);
            resultArrayBigEndian[offset] = (byte) ((value >> 8) & 0xff);
            resultArrayBigEndian[offset + 1] = (byte) (value & 0xff);
            ByteArrayMarshaller.writeShort(value, byteArray, offset, isBigEndian);
            assertArrayEquals(resultArrayBigEndian, byteArray);

            /* Little Endian */
            Arrays.fill(resultArrayLittleEndian, (byte) 0x0);
            Arrays.fill(byteArray, (byte) 0x0);
            resultArrayLittleEndian[offset + 1] = (byte) ((value >> 8) & 0xff);
            resultArrayLittleEndian[offset] = (byte) (value & 0xff);
            ByteArrayMarshaller.writeShort(value, byteArray, offset, isLittleEndian);
            assertArrayEquals(resultArrayLittleEndian, byteArray);

            count--;
        }
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testRandomInvalidOffset()
    {
        // lp.addCallerName(Utils.getCallingMethod());

        short shortTest = (short) 0xabcd;
        int offset;
        do
        {
            offset = randomGen.nextInt() % 1000;
        } while (offset >= 0 && offset <= ARRAY_SIZE - 2);

        ByteArrayMarshaller.writeShort(shortTest, byteArray, offset, isBigEndian);
        ByteArrayMarshaller.writeShort(shortTest, byteArray, offset, isLittleEndian);
    }

    @Test(expected = NullPointerException.class)
    public void testNullByteArray()
    {
        // lp.addCallerName(Utils.getCallingMethod());

        short shortTest = (short) 0xabcd;
        byte[] nullByteArray = null;

        ByteArrayMarshaller.writeShort(shortTest, nullByteArray, 0, isBigEndian);
        ByteArrayMarshaller.writeShort(shortTest, nullByteArray, 0, isLittleEndian);
    }

}
