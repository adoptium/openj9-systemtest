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
import org.junit.Assert;

import com.ibm.dataaccess.ByteArrayUnmarshaller;

public class TestByteArray2ShortNumBytes
{

    static String outputFile;
    static Random randomGen;
    static final int ARRAY_SIZE = 256;
    static final int BYTESIZE = 8; // number of bits in a byte!

    static final long ONE_DIGIT_MIN = 0x1L;
    static final long TWO_DIGIT_MIN = 0x10L;
    static final long THREE_DIGIT_MIN = 0x100L;
    static final long FOUR_DIGIT_MIN = 0x1000L;

    /* Attempting to increase the randomness */
    static int randomSeed = (int) (System.currentTimeMillis() % 1601);

    byte[] byteArray = new byte[ARRAY_SIZE];

    static final boolean isBigEndian = true;
    static final boolean isLittleEndian = false;

    static final boolean signExtend = true;
    static final boolean noSignExtend = false;

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

        ByteArrayUnmarshaller.readShort(byteArray, 0, true, 2, true);
        ByteArrayUnmarshaller.readShort(byteArray, 0, false, 2, true);

        ByteArrayUnmarshaller.readShort(byteArray, 1, true, 2, false);
        ByteArrayUnmarshaller.readShort(byteArray, 1, false, 2, false);

    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBLittleEndian1()
    {
        byte[] byteArray = new byte[2];

        ByteArrayUnmarshaller.readShort(byteArray, 1, false, 2, false);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBLittleEndian2()
    {
        byte[] byteArray = new byte[1];

        ByteArrayUnmarshaller.readShort(byteArray, 0, false, 2, true);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBBigEndian1()
    {
        byte[] byteArray = new byte[2];

        ByteArrayUnmarshaller.readShort(byteArray, 1, true, 2, false);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBBigEndian2()
    {
        byte[] byteArray = new byte[1];

        ByteArrayUnmarshaller.readShort(byteArray, 0, true, 2, true);
    }

    @Test
    public void testMinBoundaryValue()
    {
        short value = (short) Short.MIN_VALUE;
        int offset = 0;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[offset + 0] = (byte) 0x80;
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian, 2, signExtend);

        assertEquals(value, resultShort);

        /* Little Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = (byte) 0x80;

        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian, 2, signExtend);
        assertEquals(value, resultShort);
    }

    @Test
    public void testMaximumBoundaryValueSigned()
    {
        int offset = 0;
        short value = (short) Short.MAX_VALUE;
        /* Big Endian */

        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[offset + 0] = (byte) 0x7f;
        byteArray[offset + 1] = (byte) 0xff;
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian, 2, signExtend);
        assertEquals(value, resultShort);

        /* Little Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = (byte) 0x7f;
        byteArray[offset + 0] = (byte) 0xff;
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian, 2, signExtend);
        assertEquals(value, resultShort);

    }

    @Test
    public void testMaximumBoundaryValueUnsigned()
    {
        int offset = 0;
        short value = (short) Short.MAX_VALUE;
        /* Big Endian */

        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[offset + 0] = (byte) 0x7f;
        byteArray[offset + 1] = (byte) 0xff;
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian, 2, noSignExtend);
        assertEquals(value, resultShort);

        /* Little Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = (byte) 0x7f;
        byteArray[offset + 0] = (byte) 0xff;
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian, 2, noSignExtend);
        assertEquals(value, resultShort);
    }

    @Test
    public void testMaximumBoundaryValueOneNumByteUnsigned()
    {
        int offset = 0;
        // short value = (short)Short.MAX_VALUE;
        /* Big Endian */

        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[offset + 0] = (byte) 0x7f;
        byteArray[offset + 1] = (byte) 0xff;
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian, 1, noSignExtend);
        assertEquals(0x7f, resultShort);

        /* Little Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = (byte) 0x7f;
        byteArray[offset + 0] = (byte) 0xff;
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian, 1, noSignExtend);
        assertEquals(0x0ff, resultShort);
    }

    @Test
    public void testMaximumBoundaryValueOneNumByteSigned()
    {
        int offset = 0;
        // short value = (short)Short.MAX_VALUE;
        /* Big Endian */

        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[offset + 0] = (byte) 0x7f;
        byteArray[offset + 1] = (byte) 0xff;
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian, 1, signExtend);
        assertEquals(0x7f, resultShort);

        /* Little Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = (byte) 0x7f;
        byteArray[offset + 0] = (byte) 0xff;
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian, 1, signExtend);
        assertEquals(-1, resultShort);
    }

    @Test
    public void testRandomShortFourHexDigitsTwoNumBytesSigned()
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
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian, 2, signExtend);
        assertEquals(value, resultShort);

        /* Little Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 0] = (byte) (value & 0xff);
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian, 2, signExtend);
        assertEquals(value, resultShort);
    }

    @Test
    public void testRandomShortFourHexDigitsTwoNumBytesUnsigned()
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
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian, 2, noSignExtend);
        assertEquals(value, resultShort);

        /* Little Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 0] = (byte) (value & 0xff);
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian, 2, noSignExtend);
        assertEquals(value, resultShort);
    }

    @Test
    public void testRandomShortFourHexDigitsOneNumBytesSigned()
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
        short resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian, 1, signExtend);
        assertEquals(value >> 8, resultShort);

        /* Little Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 0] = (byte) (value & 0xff);
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian, 1, signExtend);
        Assert.assertEquals(((short) (value << 8)) >> 8, resultShort);
    }

    @Test
    public void testRandomShortFourHexDigitsOneNumBytesUnigned()
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
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian, 1, noSignExtend);
        assertEquals(value >>> 8, resultShort);

        /* Little Endian */

        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 0] = (byte) (value & 0xff);
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian, 1, noSignExtend);
        assertEquals(value & 0xff, resultShort);
    }

    @Test
    public void testNegativeRandomShortTwoHexDigitsOneNumByteSigned()
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
        byteArray[offset + 0] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 1] = (byte) (value & 0xff);
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian, 1, signExtend);
        assertEquals(value >> 8, resultShort);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 0] = (byte) (value & 0xff);
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian, 1, signExtend);
        assertEquals(((short) (value << 8)) >> 8, resultShort);
    }

    @Test
    public void testNegativeRandomShortTwoHexDigitsOneNumByteUnsigned()
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
        byteArray[offset + 0] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 1] = (byte) (value & 0xff);
        short resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian, 1, noSignExtend);
        assertEquals((value & 0xff00) >> 8, (short) resultShort);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 1] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 0] = (byte) (value & 0xff);
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian, 1, noSignExtend);
        assertEquals(value & 0x0ff, resultShort);
    }

    /* Test offset valid for only a given numByte value */
    @Test
    public void testValidOffsetForGivenNumBytesUnsigned()
    {
        int offset = ARRAY_SIZE - 2;
        short value;
        do
        {
            value = getvalueWithNHexDigits(2);
        } while (value >= 0x100 || value < 0x10);

        value *= -1;
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 0] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 1] = (byte) (value & 0xff);
        offset += 1;
        short resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian, 1, noSignExtend);
        assertEquals(value & 0x0ff, (short) resultShort);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        offset = ARRAY_SIZE - 2;
        byteArray[offset + 1] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 0] = (byte) (value & 0xff);
        offset += 1;
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian, 1, noSignExtend);
        assertEquals(((value >> 8) & 0xff), resultShort);
    }

    @Test
    public void testValidOffsetForGivenNumBytesSigned()
    {
        int offset = ARRAY_SIZE - 2;
        short value;
        do
        {
            value = getvalueWithNHexDigits(2);
        } while (value >= 0x100 || value < 0x10);

        value *= -1;
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 0] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 1] = (byte) (value & 0xff);
        offset += 1;
        short resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian, 1, signExtend);
        assertEquals(((short) (value << 8)) >> 8, (short) resultShort);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        offset = ARRAY_SIZE - 2;
        byteArray[offset + 1] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 0] = (byte) (value & 0xff);
        offset += 1;
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian, 1, noSignExtend);
        assertEquals(((value >> 8) & 0xff), resultShort);
    }

    @Test
    public void testZero()
    {
        int offset = 0;
        short zero = 0;
        Arrays.fill(byteArray, (byte) 0x0);
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian, 0, false);
        assertEquals(zero, resultShort);
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian, 0, false);
        assertEquals(zero, resultShort);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalNumBytes()
    {
        int offset = 0;
        short zero = 0;
        Arrays.fill(byteArray, (byte) 0x0);
        int resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian, 3, false);
        assertEquals(zero, resultShort);
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian, -1, false);
        assertEquals(zero, resultShort);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testInvalidOffset()
    {
        int offset = ARRAY_SIZE - 2;
        short value;
        do
        {
            value = getvalueWithNHexDigits(2);
        } while (value >= 0x100 || value < 0x10);

        value *= -1;
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 0] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 1] = (byte) (value & 0xff);
        offset += 2;
        short resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isBigEndian, 1, signExtend);
        assertEquals(((short) (value << 8)) >> 8, (short) resultShort);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        offset = ARRAY_SIZE - 2;
        byteArray[offset + 1] = (byte) ((value >> 8) & 0xff);
        byteArray[offset + 0] = (byte) (value & 0xff);
        offset += 2;
        resultShort = ByteArrayUnmarshaller.readShort(byteArray, offset, isLittleEndian, 1, noSignExtend);
        assertEquals(((value >> 8) & 0xff), resultShort);
    }

    @Test(expected = NullPointerException.class)
    public void testNullByteArray()
    {
        int offset = 0;
        byte[] nullByteArray = null;
        ByteArrayUnmarshaller.readShort(nullByteArray, offset, isBigEndian, 2, false);
        ByteArrayUnmarshaller.readShort(nullByteArray, offset, isBigEndian, 2, false);
    }

}
