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
import com.ibm.dataaccess.ByteArrayUnmarshaller;

public class TestByteArray2Short
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
        byte[] byteArray = new byte[3];

        ByteArrayUnmarshaller.readShort(byteArray, 0, true);
        ByteArrayUnmarshaller.readShort(byteArray, 0, false);

        ByteArrayUnmarshaller.readShort(byteArray, 1, true);
        ByteArrayUnmarshaller.readShort(byteArray, 1, false);

    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBLittleEndian1()
    {
        byte[] byteArray = new byte[2];

        ByteArrayUnmarshaller.readShort(byteArray, 1, false);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBLittleEndian2()
    {
        byte[] byteArray = new byte[1];

        ByteArrayUnmarshaller.readShort(byteArray, 0, false);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBBigEndian1()
    {
        byte[] byteArray = new byte[2];

        ByteArrayUnmarshaller.readShort(byteArray, 1, true);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBBigEndian2()
    {
        byte[] byteArray = new byte[1];

        ByteArrayUnmarshaller.readShort(byteArray, 0, true);
    }

    @Test
    public void testMinBoundaryValue()
    {
        short value = (short) Short.MIN_VALUE;
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[offset + 0] = (byte) 0x80;
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian);

        assertEquals(value, resultShort);

        /* Little Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = (byte) 0x80;

        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian);
        assertEquals(value, resultShort);
    }

    @Test
    public void testMinBoundaryValueValidOffset()
    {

        short value = (short) Short.MIN_VALUE;
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[offset + 0] = (byte) 0x80;
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian);
        assertEquals(value, resultShort);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = (byte) 0x80;
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian);
        assertEquals(value, resultShort);
    }

    @Test
    public void testMaxBoundaryValue()
    {

        int offset = 0;
        short value = (short) Short.MAX_VALUE;
        /* Big Endian */

        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[offset + 0] = (byte) 0x7f;
        byteArray[offset + 1] = (byte) 0xff;
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian);
        assertEquals(value, resultShort);

        /* Little Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = (byte) 0x7f;
        byteArray[offset + 0] = (byte) 0xff;
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian);
        assertEquals(value, resultShort);
    }

    @Test
    public void testMaxBoundaryValueValidOffset()
    {

        int offset = genRandomOffset();
        short value = (short) Short.MAX_VALUE;
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[offset + 0] = (byte) 0x7f;
        byteArray[offset + 1] = (byte) 0xff;
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian);
        assertEquals(value, resultShort);

        /* Little Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = (byte) 0x7f;
        byteArray[offset + 0] = (byte) 0xff;
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian);
        assertEquals(value, resultShort);
    }

    @Test
    public void testZero()
    {
        int offset = 0;
        short zero = 0;
        Arrays.fill(byteArray, (byte) 0x0);
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian);
        assertEquals(zero, resultShort);
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian);
        assertEquals(zero, resultShort);
    }

    @Test
    public void testRandomShortFourHexDigits()
    {

        short value;
        int offset = 0;
        do
        {
            value = getvalueWithNHexDigits(4);
        } while (value < 0x1000);

        /* Big Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 0] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 1] = (byte) (value & 0xff);
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian);
        assertEquals(value, resultShort);

        /* Little Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 0] = (byte) (value & 0xff);
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian);
        assertEquals(value, resultShort);
    }

    @Test
    public void testRandomShortFourHexDigitsValidOffset()
    {
        short value;
        int offset = genRandomOffset();
        do
        {
            value = getvalueWithNHexDigits(4);
        } while (value < 0x1000);

        /* Big Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 0] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 1] = (byte) (value & 0xff);
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian);
        assertEquals(value, resultShort);

        /* Little Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 0] = (byte) (value & 0xff);
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian);
        assertEquals(value, resultShort);
    }

    @Test
    public void testRandomShortThreeHexDigits()
    {

        int offset = 0;
        short value;
        do
        {
            value = getvalueWithNHexDigits(3);
        } while (value >= 0x1000 || value < 0x100);

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 0] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 1] = (byte) (value & 0xff);
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian);
        assertEquals(value, resultShort);

        /* Little Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 0] = (byte) (value & 0xff);
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian);
        assertEquals(value, resultShort);
    }

    @Test
    public void testRandomShortThreeHexDigitsValidOffset()
    {

        int offset = genRandomOffset();
        short value;
        do
        {
            value = getvalueWithNHexDigits(3);
        } while (value >= 0x1000 || value < 0x100);

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 0] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 1] = (byte) (value & 0xff);
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian);
        assertEquals(value, resultShort);

        /* Little Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 0] = (byte) (value & 0xff);
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian);
        assertEquals(value, resultShort);
    }

    @Test
    public void testRandomShortTwoHexDigits()
    {
        int offset = 0;
        short value;
        do
        {
            value = getvalueWithNHexDigits(2);
        } while (value >= 0x100 || value < 0x10);

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 0] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 1] = (byte) (value & 0xff);
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian);
        assertEquals(value, resultShort);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 0] = (byte) (value & 0xff);
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian);
        assertEquals(value, resultShort);
    }

    @Test
    public void testRandomShortTwoHexDigitsValidOffset()
    {
        int offset = genRandomOffset();
        short value;
        do
        {
            value = getvalueWithNHexDigits(2);
        } while (value >= 0x100 || value < 0x10);

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 0] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 1] = (byte) (value & 0xff);
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian);
        assertEquals(value, resultShort);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 0] = (byte) (value & 0xff);
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian);
        assertEquals(value, resultShort);
    }

    @Test
    public void testRandomShortOneHexDigit()
    {
        int offset = 0;
        short value;
        do
        {
            value = getvalueWithNHexDigits(1);
        } while (value >= 0x10 || value < 0x1);

        /* Big Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 0] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 1] = (byte) (value & 0xff);
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian);
        assertEquals(value, resultShort);

        /* Little Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 0] = (byte) (value & 0xff);
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian);
        assertEquals(value, resultShort);
    }

    @Test
    public void testRandomShortOneHexDigitValidOffset()
    {
        int offset = genRandomOffset();
        short value;
        do
        {
            value = getvalueWithNHexDigits(1);
        } while (value >= 0x10 || value < 0x1);

        /* Big Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 0] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 1] = (byte) (value & 0xff);
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian);
        assertEquals(value, resultShort);

        /* Little Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 0] = (byte) (value & 0xff);
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian);
        assertEquals(value, resultShort);
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
        int offset = 0;
        short value;
        do
        {
            value = getvalueWithNHexDigits(4);
        } while (value < FOUR_DIGIT_MIN);

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 0] = signExtend((byte) ((value >> 8) & 0xff));
        byteArray[offset + 1] = (byte) (value & 0xff);
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian);
        assertEquals(value, resultShort);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = signExtend((byte) ((value >> 8) & 0xff));
        byteArray[offset + 0] = (byte) (value & 0xff);
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian);
        assertEquals(value, resultShort);
    }

    @Test
    public void testRandomNegativeShortFourHexDigitsValidOffset()
    {
        int offset = genRandomOffset();
        short value;
        do
        {
            value = getvalueWithNHexDigits(4);
        } while (value < FOUR_DIGIT_MIN);

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 0] = signExtend((byte) ((value >> 8) & 0xff));
        byteArray[offset + 1] = (byte) (value & 0xff);
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian);
        assertEquals(value, resultShort);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = signExtend((byte) ((value >> 8) & 0xff));
        byteArray[offset + 0] = (byte) (value & 0xff);
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian);
        assertEquals(value, resultShort);
    }

    @Test
    public void testRandomNegativeShortThreeHexDigits()
    {
        int offset = 0;
        short value;
        do
        {
            value = getvalueWithNHexDigits(3);
        } while (value >= FOUR_DIGIT_MIN || value < THREE_DIGIT_MIN);

        value *= -1;

        /* Big Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 0] = signExtend((byte) ((value >> 8) & 0xff));
        byteArray[offset + 1] = (byte) (value & 0xff);
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian);
        assertEquals(value, resultShort);

        /* Little Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = signExtend((byte) ((value >> 8) & 0xff));
        byteArray[offset + 0] = (byte) (value & 0xff);
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian);
        assertEquals(value, resultShort);
    }

    @Test
    public void testRandomNegativeShortThreeHexDigitsValidOffset()
    {
        int offset = genRandomOffset();
        short value;
        do
        {
            value = getvalueWithNHexDigits(3);
        } while (value >= FOUR_DIGIT_MIN || value < THREE_DIGIT_MIN);

        value *= -1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 0] = signExtend((byte) ((value >> 8) & 0xff));
        byteArray[offset + 1] = (byte) (value & 0xff);
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian);
        assertEquals(value, resultShort);

        /* Little Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = signExtend((byte) ((value >> 8) & 0xff));
        byteArray[offset + 0] = (byte) (value & 0xff);
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian);
        assertEquals(value, resultShort);
    }

    @Test
    public void testRandomNegativeShortTwoHexDigits()
    {
        int offset = 0;
        short value;
        do
        {
            value = getvalueWithNHexDigits(2);
        } while (value >= 0x100 || value < 0x10);

        value *= -1;
        /* Big Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 0] = signExtend((byte) ((value >> 8) & 0xff));
        byteArray[offset + 1] = (byte) (value & 0xff);
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian);
        assertEquals(value, resultShort);

        /* Little Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = signExtend((byte) ((value >> 8) & 0xff));
        byteArray[offset + 0] = (byte) (value & 0xff);
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian);
        assertEquals(value, resultShort);
    }

    @Test
    public void testRandomNegativeShortTwoHexDigitsValidOffset()
    {
        int offset = genRandomOffset();
        short value;
        do
        {
            value = getvalueWithNHexDigits(2);
        } while (value >= 0x100 || value < 0x10);

        value *= -1;
        /* Big Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 0] = signExtend((byte) ((value >> 8) & 0xff));
        byteArray[offset + 1] = (byte) (value & 0xff);
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian);
        assertEquals(value, resultShort);

        /* Little Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = signExtend((byte) ((value >> 8) & 0xff));
        byteArray[offset + 0] = (byte) (value & 0xff);
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian);
        assertEquals(value, resultShort);
    }

    @Test
    public void testRandomNegativeShortOneHexDigit()
    {
        int offset = 0;
        short value;
        do
        {
            value = getvalueWithNHexDigits(1);
        } while (value >= TWO_DIGIT_MIN || value < ONE_DIGIT_MIN);

        /* Big Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 0] = signExtend((byte) ((value >> 8) & 0xff));
        byteArray[offset + 1] = (byte) (value & 0xff);
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian);
        assertEquals(value, resultShort);

        /* Little Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = signExtend((byte) ((value >> 8) & 0xff));
        byteArray[offset + 0] = (byte) (value & 0xff);
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian);
        assertEquals(value, resultShort);
    }

    @Test
    public void testRandomNegativeShortOneHexDigitValidOffset()
    {
        int offset = genRandomOffset();
        short value;
        do
        {
            value = getvalueWithNHexDigits(1);
        } while (value >= TWO_DIGIT_MIN || value < ONE_DIGIT_MIN);

        /* Big Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 0] = signExtend((byte) ((value >> 8) & 0xff));
        byteArray[offset + 1] = (byte) (value & 0xff);
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian);
        assertEquals(value, resultShort);

        /* Little Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = signExtend((byte) ((value >> 8) & 0xff));
        byteArray[offset + 0] = (byte) (value & 0xff);
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian);
        assertEquals(value, resultShort);
    }

    @Test
    public void testoffset()
    {
        int offset = ARRAY_SIZE - 2;
        short value;
        do
        {
            value = getvalueWithNHexDigits(4);
        } while (value < 0x1000);

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 1] = (byte) (value & 0xff);
        short resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian);
        assertEquals(value, resultShort);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = (byte) ((value >> 8) & 0xff);
        byteArray[offset] = (byte) (value & 0xff);
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian);
        assertEquals(value, resultShort);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testRandomInvalidOffset()
    {
        int offset;
        do
        {
            offset = randomGen.nextInt() % 1000;
        } while (offset >= 0 && offset <= ARRAY_SIZE - 2);

        ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian);
        ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian);
    }

    @Test(expected = NullPointerException.class)
    public void testNullByteArray()
    {

        short shortTest = (short) 0x1234;
        byte[] nullByteArray = null;

        ByteArrayMarshaller.writeShort(shortTest, nullByteArray, 0, isBigEndian);
        ByteArrayMarshaller.writeShort(shortTest, nullByteArray, 0, isLittleEndian);
    }

}
