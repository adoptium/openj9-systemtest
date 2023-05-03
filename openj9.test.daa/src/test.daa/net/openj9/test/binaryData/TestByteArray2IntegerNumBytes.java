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

public class TestByteArray2IntegerNumBytes
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
    public void testMinimumBoundaryValue()
    {
        int value = Integer.MIN_VALUE;
        int offset = 0;
        int resultInt;
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[0] = (byte) 0x80;
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, 4, signExtend);
        assertEquals(value, resultInt);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, 4, noSignExtend);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[3] = (byte) 0x80;
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, 4, signExtend);
        assertEquals(value, resultInt);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, 4, noSignExtend);
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
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, 3, signExtend);
        assertEquals(0x7fffff, resultInt);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, 4, noSignExtend);
        assertEquals(value, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[3] = (byte) 0x7f;
        byteArray[2] = (byte) 0xff;
        byteArray[1] = (byte) 0xff;
        byteArray[0] = (byte) 0xff;
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, 4, signExtend);
        assertEquals(value, resultInt);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, 4, noSignExtend);
        assertEquals(value, resultInt);
    }

    @Test
    public void testMaximumBoundaryValueThreeNumBytesSigned()
    {
        // int value = Integer.MAX_VALUE;
        int resultInt;
        int offset = genRandomOffset();
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 0] = (byte) 0xff;
        byteArray[offset + 1] = (byte) 0xff;
        byteArray[offset + 2] = (byte) 0xff;
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, 3, signExtend);
        assertEquals(-1, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[offset + 3] = (byte) 0x7f;
        byteArray[offset + 2] = (byte) 0xff;
        byteArray[offset + 1] = (byte) 0xff;
        byteArray[offset + 0] = (byte) 0xff;
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, 3, signExtend);
        assertEquals(-1, resultInt);
    }

    @Test
    public void testMaximumBoundaryValueThreeNumBytesUnsigned()
    {
        // int value = Integer.MAX_VALUE;
        int resultInt;
        int offset = genRandomOffset();
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 0] = (byte) 0xff;
        byteArray[offset + 1] = (byte) 0xff;
        byteArray[offset + 2] = (byte) 0xff;
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, 3, noSignExtend);
        assertEquals(0x0ffffff, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[offset + 3] = (byte) 0x7f;
        byteArray[offset + 2] = (byte) 0xff;
        byteArray[offset + 1] = (byte) 0xff;
        byteArray[offset + 0] = (byte) 0xff;
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, 3, noSignExtend);
        assertEquals(0x0ffffff, resultInt);
    }

    @Test
    public void testMaximumBoundaryValueTwoNumBytesSigned()
    {
        // int value = Integer.MAX_VALUE;
        int resultInt;
        int offset = genRandomOffset();
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 0] = (byte) 0xff;
        byteArray[offset + 1] = (byte) 0xff;
        byteArray[offset + 2] = (byte) 0xff;
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, 2, signExtend);
        assertEquals(-1, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[offset + 3] = (byte) 0x7f;
        byteArray[offset + 2] = (byte) 0xff;
        byteArray[offset + 1] = (byte) 0xff;
        byteArray[offset + 0] = (byte) 0xff;
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, 2, signExtend);
        assertEquals(-1, resultInt);
    }

    @Test
    public void testMaximumBoundaryValueTwoNumBytesUnsigned()
    {
        // int value = Integer.MAX_VALUE;
        int resultInt;
        int offset = genRandomOffset();
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        byteArray[offset + 0] = (byte) 0xff;
        byteArray[offset + 1] = (byte) 0xff;
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, 2, noSignExtend);
        assertEquals(0x0ffff, resultInt);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[offset + 3] = (byte) 0x7f;
        byteArray[offset + 2] = (byte) 0xff;
        byteArray[offset + 1] = (byte) 0xff;
        byteArray[offset + 0] = (byte) 0xff;
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, 2, noSignExtend);
        assertEquals(0x0ffff, resultInt);
    }

    /*
     * Take integers fitting within 2 and 3 bytes and create byte arrays. For
     * each, perform the following tests: 1. Unmarshall completely (4 numBytes)
     * - signed & unsigned 2. Unmarshall with the same number of numBytes -
     * signed & unsigned 3. Unmarshall with a lesser number of numBytes - signed
     * & unsigned 4. Negate and unmarshall with a lesser number of numBytes -
     * signed & unsigned
     */

    @Test
    public void testThreeByteIntegersUnmarshallCompletelySigned()
    {

        int offset = genRandomOffset();
        int value;
        int resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(6);
        } while (value >= SEVEN_DIGIT_MIN || value < SIX_DIGIT_MIN);

        /* Big Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, 4, signExtend);
        assertEquals(value, resultInt);

        /* Little Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, 4, signExtend);
        assertEquals(value, resultInt);
    }

    @Test
    public void testThreeByteIntegersUnmarshallCompletelyUnsigned()
    {

        int offset = genRandomOffset();
        int value;
        int resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(6);
        } while (value >= SEVEN_DIGIT_MIN || value < SIX_DIGIT_MIN);

        /* Big Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, 4, noSignExtend);
        assertEquals(value, resultInt);

        /* Little Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, 4, noSignExtend);
        assertEquals(value, resultInt);
    }

    @Test
    public void testThreeByteIntegersSameNumBytesSigned()
    {
        // initialization
        byte[] beByteArray = new byte[ARRAY_SIZE];
        byte[] leByteArray = new byte[ARRAY_SIZE];
        int offset = genRandomOffset();
        int value;
        int numBytes = 3;

        do
        {
            value = getRandomIntegerWithNHexDigits(6);
        } while (value >= SEVEN_DIGIT_MIN || value < SIX_DIGIT_MIN);

        /* Big Endian */
        for (int i = 0; i < INTSIZE; i++)
            beByteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        /* Little Endian */
        for (int i = 0; i < INTSIZE; i++)
            leByteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);
        // end of initialization

        // test execution
        int resultInt1 = ByteArrayUnmarshaller.readInt(beByteArray, offset, isBigEndian, numBytes, signExtend);
        int resultInt2 = ByteArrayUnmarshaller.readInt(leByteArray, offset, isLittleEndian, numBytes, signExtend);

        // verification
        assertEquals(value >> 8, resultInt1);
        assertEquals((value << 8) >> 8, resultInt2);
    }

    @Test
    public void testThreeByteIntegersSameNumBytesUnsigned()
    {

        int offset = genRandomOffset();
        int value;
        int resultInt;
        int numBytes = 3;

        do
        {
            value = getRandomIntegerWithNHexDigits(6);
        } while (value >= SEVEN_DIGIT_MIN || value < SIX_DIGIT_MIN);

        /* Big Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, numBytes, noSignExtend);

        assertEquals(value >> 8, resultInt);

        /* Little Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, numBytes, noSignExtend);
        assertEquals((value << 8) >>> 8, resultInt);
    }

    @Test
    public void testBoundaries()
    {
        byte[] byteArray = new byte[5];

        ByteArrayUnmarshaller.readInt(byteArray, 0, true, 4, true);
        ByteArrayUnmarshaller.readInt(byteArray, 0, false, 4, true);

        ByteArrayUnmarshaller.readInt(byteArray, 1, true, 4, false);
        ByteArrayUnmarshaller.readInt(byteArray, 1, false, 4, false);

    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBLittleEndian1()
    {
        byte[] byteArray = new byte[4];

        ByteArrayUnmarshaller.readInt(byteArray, 1, false, 4, true);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBLittleEndian2()
    {
        byte[] byteArray = new byte[3];

        ByteArrayUnmarshaller.readInt(byteArray, 0, false, 4, false);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBBigEndian1()
    {
        byte[] byteArray = new byte[4];

        ByteArrayUnmarshaller.readInt(byteArray, 1, true, 4, false);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBBigEndian2()
    {
        byte[] byteArray = new byte[3];

        ByteArrayUnmarshaller.readInt(byteArray, 0, true, 4, true);
    }

    @Test
    public void testThreeByteIntegersLesserNumBytesSigned()
    {

        int offset = genRandomOffset();
        int value;
        int resultInt;
        int numBytes = 2;

        do
        {
            value = getRandomIntegerWithNHexDigits(6);
        } while (value >= SEVEN_DIGIT_MIN || value < SIX_DIGIT_MIN);

        /* Big Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, numBytes, signExtend);

        assertEquals(value >> 16, resultInt);

        /* Little Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, numBytes, signExtend);
        assertEquals((value << 16) >> 16, resultInt);
    }

    @Test
    public void testThreeByteIntegersLesserNumBytesUnsigned()
    {

        int offset = genRandomOffset();
        int value;
        int resultInt;
        int numBytes = 2;

        do
        {
            value = getRandomIntegerWithNHexDigits(6);
        } while (value >= SEVEN_DIGIT_MIN || value < SIX_DIGIT_MIN);

        /* Big Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, numBytes, noSignExtend);

        assertEquals(value >>> 16, resultInt);

        /* Little Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, numBytes, noSignExtend);
        assertEquals((value << 16) >>> 16, resultInt);
    }

    @Test
    public void testThreeByteNegativeIntegersLesserNumBytesSigned()
    {

        int offset = genRandomOffset();
        int value;
        int resultInt;
        int numBytes = 2;

        do
        {
            value = getRandomIntegerWithNHexDigits(6);
        } while (value >= SEVEN_DIGIT_MIN || value < SIX_DIGIT_MIN);

        value *= -1;

        /* Big Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, numBytes, signExtend);

        assertEquals(value >> 16, resultInt);

        /* Little Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, numBytes, signExtend);
        assertEquals((value << 16) >> 16, resultInt);
    }

    @Test
    public void testThreeByteNegativeIntegersLesserNumBytesUnsigned()
    {

        int offset = genRandomOffset();
        int value;
        int resultInt;
        int numBytes = 2;

        do
        {
            value = getRandomIntegerWithNHexDigits(6);
        } while (value >= SEVEN_DIGIT_MIN || value < SIX_DIGIT_MIN);

        value *= -1;

        /* Big Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, numBytes, noSignExtend);

        assertEquals(value >>> 16, resultInt);

        /* Little Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, numBytes, noSignExtend);
        assertEquals((value << 16) >>> 16, resultInt);
    }

    @Test
    public void testTwoByteIntegersUnmarshallCompletelySigned()
    {

        int offset = genRandomOffset();
        int value;
        int resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(4);
        } while (value >= FIVE_DIGIT_MIN || value < FOUR_DIGIT_MIN);

        /* Big Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, 4, signExtend);
        assertEquals(value, resultInt);

        /* Little Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, 4, signExtend);
        assertEquals(value, resultInt);
    }

    @Test
    public void testTwoByteIntegersUnmarshallCompletelyUnsigned()
    {

        int offset = genRandomOffset();
        int value;
        int resultInt;
        do
        {
            value = getRandomIntegerWithNHexDigits(4);
        } while (value >= FIVE_DIGIT_MIN || value < FOUR_DIGIT_MIN);

        /* Big Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, 4, noSignExtend);
        assertEquals(value, resultInt);

        /* Little Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, 4, noSignExtend);
        assertEquals(value, resultInt);
    }

    @Test
    public void testTwoByteIntegersSameNumBytesSigned()
    {

        int offset = genRandomOffset();
        int value;
        int resultInt;
        int numBytes = 2;

        do
        {
            value = getRandomIntegerWithNHexDigits(4);
        } while (value >= FIVE_DIGIT_MIN || value < FOUR_DIGIT_MIN);

        /* Big Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, numBytes, signExtend);

        assertEquals(value >> 16, resultInt);

        /* Little Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, numBytes, signExtend);
        assertEquals((value << 16) >> 16, resultInt);
    }

    @Test
    public void testTwoByteIntegersSameNumBytesUnsigned()
    {

        int offset = genRandomOffset();
        int value;
        int resultInt;
        int numBytes = 2;

        do
        {
            value = getRandomIntegerWithNHexDigits(4);
        } while (value >= FIVE_DIGIT_MIN || value < FOUR_DIGIT_MIN);

        /* Big Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, numBytes, noSignExtend);

        assertEquals(value >> 16, resultInt);

        /* Little Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, numBytes, noSignExtend);
        assertEquals((value << 16) >>> 16, resultInt);
    }

    @Test
    public void testTwoByteIntegersLesserNumBytesSigned()
    {

        int offset = genRandomOffset();
        int value;
        int resultInt;
        int numBytes = 1;

        do
        {
            value = getRandomIntegerWithNHexDigits(6);
        } while (value >= FIVE_DIGIT_MIN || value < FOUR_DIGIT_MIN);

        /* Big Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, numBytes, signExtend);

        assertEquals(value >> 24, resultInt);
        /* Little Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, numBytes, signExtend);
        assertEquals((value << 24) >> 24, resultInt);
    }

    @Test
    public void testTwoByteIntegersLesserNumBytesUnsigned()
    {

        int offset = genRandomOffset();
        int value;
        int resultInt;
        int numBytes = 1;

        do
        {
            value = getRandomIntegerWithNHexDigits(6);
        } while (value >= FIVE_DIGIT_MIN || value < FOUR_DIGIT_MIN);

        /* Big Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, numBytes, noSignExtend);

        assertEquals(value >>> 24, resultInt);

        /* Little Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, numBytes, noSignExtend);
        assertEquals((value << 24) >>> 24, resultInt);
    }

    @Test
    public void testTwoByteNegativeIntegersLesserNumBytesSigned()
    {

        int offset = genRandomOffset();
        int value;
        int resultInt;
        int numBytes = 1;

        do
        {
            value = getRandomIntegerWithNHexDigits(4);
        } while (value >= FIVE_DIGIT_MIN || value < FOUR_DIGIT_MIN);

        value *= -1;

        /* Big Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, numBytes, signExtend);

        assertEquals(value >> 24, resultInt);

        /* Little Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, numBytes, signExtend);
        assertEquals((value << 24) >> 24, resultInt);
    }

    @Test
    public void testTwoByteNegativeIntegersLesserNumBytesUnsigned()
    {

        int offset = genRandomOffset();
        int value;
        int resultInt;
        int numBytes = 1;

        do
        {
            value = getRandomIntegerWithNHexDigits(4);
        } while (value >= FIVE_DIGIT_MIN || value < FOUR_DIGIT_MIN);

        value *= -1;

        /* Big Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, numBytes, noSignExtend);

        assertEquals(value >>> 24, resultInt);

        /* Little Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, numBytes, noSignExtend);
        assertEquals((value << 24) >>> 24, resultInt);
    }

    /*
     * Take a negative number and test for an offset valid for only a given
     * numBytes value
     */
    @Test
    public void testOffsetForNumByteSigned()
    {

        int offset = genRandomOffset() - 2;
        offset = offset > 0 ? offset : 0;
        int value;
        int resultInt;
        int numBytes = 2;

        do
        {
            value = getRandomIntegerWithNHexDigits(4);
        } while (value >= FIVE_DIGIT_MIN || value < FOUR_DIGIT_MIN);

        value *= -1;

        /* Big Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        offset = offset + 2;

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, numBytes, signExtend);
        assertEquals((value << 16) >> 16, resultInt);

        /* Little Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, numBytes, signExtend);
        assertEquals((value << 16) >> 16, resultInt);
    }

    @Test
    public void testOffsetForNumByteUnsigned()
    {

        int offset = genRandomOffset() - 2;
        offset = offset > 0 ? offset : 0;
        int value;
        int resultInt;
        int numBytes = 2;

        do
        {
            value = getRandomIntegerWithNHexDigits(4);
        } while (value >= FIVE_DIGIT_MIN || value < FOUR_DIGIT_MIN);

        value *= -1;

        /* Big Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (INTSIZE - 1 - i)) & 0x0ff);

        offset = offset + 2;

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, numBytes, noSignExtend);
        assertEquals((value << 16) >>> 16, resultInt);

        /* Little Endian */
        for (int i = 0; i < INTSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * i) & 0x0ff);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, numBytes, noSignExtend);
        assertEquals((value << 16) >>> 16, resultInt);
    }

    @Test
    public void testZero()
    {
        int value = 0;
        int resultInt;
        int offset = 0;

        Arrays.fill(byteArray, (byte) 0x0);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, 4, false);
        assertEquals(value, resultInt);
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, 4, false);
        assertArrayEquals(byteArray, byteArray);
    }

    /* Test for an invalid numBytes value */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidNumBytes()
    {
        int value = 0;
        int resultInt;
        int offset = 0;

        Arrays.fill(byteArray, (byte) 0x0);

        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isBigEndian, 5, false);
        assertEquals(value, resultInt);
        resultInt = ByteArrayUnmarshaller.readInt(byteArray, offset, isLittleEndian, -1, false);
        assertArrayEquals(byteArray, byteArray);
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

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testTwoBytesPastEndOfArray()
    {
        /*
         * Use a valid offset, but set numBytes to exceed read past end of array
         * - AIOB expected.
         */
        Arrays.fill(byteArray, (byte) 0x0);
        ByteArrayUnmarshaller.readInt(byteArray, byteArray.length - 1, isBigEndian, 2, false);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testThreeBytesPastEndOfArray()
    {
        /*
         * Use a valid offset, but set numBytes to exceed read past end of array
         * - AIOB expected.
         */
        Arrays.fill(byteArray, (byte) 0x0);
        ByteArrayUnmarshaller.readInt(byteArray, byteArray.length - 2, isLittleEndian, 3, false);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testFourBytesPastEndOfArray()
    {
        /*
         * Use a valid offset, but set numBytes to exceed read past end of array
         * - AIOB expected.
         */
        Arrays.fill(byteArray, (byte) 0x0);
        ByteArrayUnmarshaller.readInt(byteArray, byteArray.length - 3, isBigEndian, 4, false);
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

    @Test
    public void testUnsignedExtendOneByte()
    {
        // Populate bytearray with bytes 0 to 256.
        for (int i = 0; i < 256; i++)
        {
            byteArray[i] = (byte) i;
        }

        int resultInt;
        // Test load of integer from byteArray unsigned -> should match integer
        // i value.
        for (int i = 0; i < 256; i++)
        {
            resultInt = ByteArrayUnmarshaller.readInt(byteArray, i, isBigEndian, 1, false);
            assertEquals("testUnsignedExtendOneByte incorrect result.", i, resultInt);
        }
    }

    @Test
    public void testSignedExtendOneByte()
    {
        // Populate bytearray with bytes 0 to 256.
        for (int i = 0; i < 256; i++)
        {
            byteArray[i] = (byte) i;
        }

        int resultInt;
        // Test load of integer from byteArray signed -> Should range from 0 to
        // 127, then -128 to -1
        for (int i = 0; i < 256; i++)
        {
            resultInt = ByteArrayUnmarshaller.readInt(byteArray, i, isBigEndian, 1, true);
            assertEquals("testUnsignedExtendOneByte incorrect result.", (byte) i, resultInt);
        }
    }
}
