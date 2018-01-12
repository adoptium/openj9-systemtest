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
import com.ibm.dataaccess.ByteArrayUnmarshaller;

public class TestByteArray2LongNumBytes
{
    static String outputFile;
    static Random randomGen;
    static final int ARRAY_SIZE = 256;
    static final int LONGSIZE = 8;
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

    static final boolean isBigEndian = true;
    static final boolean isLittleEndian = false;

    static final boolean signExtend = true;
    static final boolean noSignExtend = false;

    long _64BIT_MASK = 0xFFFFFFFFFFFFFFFFL;

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

    private int genRandomOffset()
    {
        int offset = randomGen.nextInt() % (ARRAY_SIZE - 8);
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

    /* Return numBytes >= 0 and < 8 */
    private int genRandomNumBytes()
    {
        int numBytes = randomGen.nextInt() % LONGSIZE;
        return numBytes > 0 ? numBytes : -numBytes;
    }

    @Test
    public void testBoundaries()
    {
        byte[] byteArray = new byte[9];

        ByteArrayUnmarshaller.readLong(byteArray, 0, true, 8, true);
        ByteArrayUnmarshaller.readLong(byteArray, 0, false, 8, true);

        ByteArrayUnmarshaller.readLong(byteArray, 1, true, 8, false);
        ByteArrayUnmarshaller.readLong(byteArray, 1, false, 8, false);

    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBLittleEndian1()
    {
        byte[] byteArray = new byte[8];

        ByteArrayUnmarshaller.readLong(byteArray, 1, false, 8, true);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBLittleEndian2()
    {
        byte[] byteArray = new byte[7];

        ByteArrayUnmarshaller.readLong(byteArray, 0, false, 8, true);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBBigEndian1()
    {
        byte[] byteArray = new byte[8];

        ByteArrayUnmarshaller.readLong(byteArray, 1, true, 8, false);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAIOBBigEndian2()
    {
        byte[] byteArray = new byte[7];

        ByteArrayUnmarshaller.readLong(byteArray, 0, true, 8, false);
    }

    @Test
    public void testMinimumBoundaryValue()
    {
        long value = Long.MIN_VALUE;
        int offset = 0;
        int numBytes = 8;
        long resultLong;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[0] = (byte) 0x80;
        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, numBytes, signExtend);
        assertEquals(value, resultLong);
        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, numBytes, noSignExtend);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[7] = (byte) 0x80;
        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, numBytes, signExtend);
        assertEquals(value, resultLong);
        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, numBytes, noSignExtend);
        assertEquals(value, resultLong);
    }

    /* For Long.MIN_VALUE, numBytes < 8 should produce a 0 */
    @Test
    public void testMinimumBoundaryValueRandomNumBytes()
    {
        // long value = Long.MIN_VALUE;
        int offset = 0;
        int numBytes = genRandomNumBytes();
        long resultLong;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, numBytes, signExtend);
        assertEquals(0, resultLong);
        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, numBytes, noSignExtend);
        assertEquals(0, resultLong);

        /* Little Endian */
        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, numBytes, signExtend);
        assertEquals(0, resultLong);
        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, numBytes, noSignExtend);
        assertEquals(0, resultLong);
    }

    /*
     * Testing for sign extension.
     * 
     * Using Long.MAX_VALUE = 0x7fffffffffffffff is a good option.
     * 
     * With numBytes lesser than 8, the reverse conversion should yield a long
     * with the top bytes sign extended. For MAX_VALUE, we should always receive
     * a -1 when numBytes < 8.
     * 
     * The next seven tests are with signExtend = true
     */

    @Test
    public void testMaximumBoundaryValueSevenNumBytes()
    {
        // long value = Long.MAX_VALUE;
        int offset = 0;
        long resultLong;
        int numBytes = 7;

        /* Little Endian */
        byteArray[7] = (byte) 0x7f;
        byteArray[6] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[1] = (byte) 0xff;
        byteArray[0] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, numBytes, signExtend);
        assertEquals(-1, resultLong);

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[0] = 0x7f;
        byteArray[1] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[6] = (byte) 0xff;
        byteArray[7] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, numBytes, signExtend);
        assertEquals(0x7fffffffffffffL, resultLong);

    }

    @Test
    public void testMaximumBoundaryValueSixNumBytes()
    {
        // long value = Long.MAX_VALUE;
        int offset = 0;
        long resultLong;
        int numBytes = 6;

        /* Little Endian */
        byteArray[7] = (byte) 0x7f;
        byteArray[6] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[1] = (byte) 0xff;
        byteArray[0] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, numBytes, signExtend);
        assertEquals(-1, resultLong);

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[0] = (byte) 0x7f;
        byteArray[1] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[6] = (byte) 0xff;
        byteArray[7] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, numBytes, signExtend);
        assertEquals(0x7fffffffffffL, resultLong);

    }

    @Test
    public void testMaximumBoundaryValueFiveNumBytes()
    {
        // long value = Long.MAX_VALUE;
        int offset = 0;
        long resultLong;
        int numBytes = 5;
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[0] = (byte) 0x7f;
        byteArray[1] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[6] = (byte) 0xff;
        byteArray[7] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, numBytes, signExtend);
        assertEquals(0x7fffffffffL, resultLong);

    }

    @Test
    public void testMaximumBoundaryValueFourNumBytes()
    {
        // long value = Long.MAX_VALUE;
        int offset = 0;
        long resultLong;
        int numBytes = 4;

        /* Little Endian */
        byteArray[7] = (byte) 0x7f;
        byteArray[6] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[1] = (byte) 0xff;
        byteArray[0] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, numBytes, signExtend);
        assertEquals(-1, resultLong);

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[0] = (byte) 0x7f;
        byteArray[1] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[6] = (byte) 0xff;
        byteArray[7] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, numBytes, signExtend);
        assertEquals(0x7fffffffL, resultLong);

        /* Little Endian */
        byteArray[7] = (byte) 0x7f;
        byteArray[6] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[1] = (byte) 0xff;
        byteArray[0] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, numBytes, signExtend);
        assertEquals(-1, resultLong);

    }

    @Test
    public void testMaximumBoundaryValueThreeNumBytes()
    {
        // long value = Long.MAX_VALUE;
        int offset = 0;
        long resultLong;
        int numBytes = 3;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[0] = (byte) 0x7f;
        byteArray[1] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[6] = (byte) 0xff;
        byteArray[7] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, numBytes, signExtend);
        assertEquals(0x7fffffL, resultLong);

        /* Little Endian */
        byteArray[7] = (byte) 0x7f;
        byteArray[6] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[1] = (byte) 0xff;
        byteArray[0] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, numBytes, signExtend);
        assertEquals(-1, resultLong);
    }

    @Test
    public void testMaximumBoundaryValueTwoNumBytes()
    {
        // long value = Long.MAX_VALUE;
        int offset = 0;
        long resultLong;
        int numBytes = 2;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[0] = (byte) 0x7f;
        byteArray[1] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[6] = (byte) 0xff;
        byteArray[7] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, numBytes, signExtend);
        assertEquals(0x7fffL, resultLong);

        /* Little Endian */
        byteArray[7] = (byte) 0x7f;
        byteArray[6] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[1] = (byte) 0xff;
        byteArray[0] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, numBytes, signExtend);
        assertEquals(-1, resultLong);

    }

    @Test
    public void testMaximumBoundaryValueOneNumBytes()
    {
        // long value = Long.MAX_VALUE;
        int offset = 0;
        long resultLong;
        int numBytes = 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[0] = (byte) 0x7f;
        byteArray[1] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[6] = (byte) 0xff;
        byteArray[7] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, numBytes, signExtend);
        assertEquals(0x7fL, resultLong);

        /* Little Endian */

        byteArray[7] = (byte) 0x7f;
        byteArray[6] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[1] = (byte) 0xff;
        byteArray[0] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, numBytes, signExtend);
        assertEquals(-1, resultLong);
    }

    /* Repeating all the above seven tests without sign extension */

    @Test
    public void testMaximumBoundaryValueSevenNumBytesUnsigned()
    {
        // long value = Long.MAX_VALUE;
        int offset = 0;
        long resultLong;
        int numBytes = 7;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[0] = (byte) 0x7f;
        byteArray[1] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[6] = (byte) 0xff;
        byteArray[7] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, numBytes, noSignExtend);
        assertEquals(0x7fffffffffffffL, resultLong);

        /* Little Endian */
        byteArray[7] = (byte) 0x7f;
        byteArray[6] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[1] = (byte) 0xff;
        byteArray[0] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, numBytes, noSignExtend);
        assertEquals(0x00ffffffffffffffL, resultLong);
    }

    @Test
    public void testMaximumBoundaryValueSixNumBytesUnsigned()
    {
        // long value = Long.MAX_VALUE;
        int offset = 0;
        long resultLong;
        int numBytes = 6;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[0] = (byte) 0x7f;
        byteArray[1] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[6] = (byte) 0xff;
        byteArray[7] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, numBytes, noSignExtend);

        assertEquals(0x007fffffffffffL, resultLong);

        /* Little Endian */
        byteArray[7] = (byte) 0x7f;
        byteArray[6] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[1] = (byte) 0xff;
        byteArray[0] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, numBytes, noSignExtend);
        assertEquals(0x00ffffffffffffL, resultLong);

    }

    @Test
    public void testMaximumBoundaryValueFiveNumBytesUnsigned()
    {
        // long value = Long.MAX_VALUE;
        int offset = 0;
        long resultLong;
        int numBytes = 5;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[0] = (byte) 0x7f;
        byteArray[1] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[6] = (byte) 0xff;
        byteArray[7] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, numBytes, noSignExtend);
        assertEquals(0x7fffffffffL, resultLong);

        /* Little Endian */
        byteArray[7] = (byte) 0x7f;
        byteArray[6] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[1] = (byte) 0xff;
        byteArray[0] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, numBytes, noSignExtend);
        assertEquals(0x00ffffffffffL, resultLong);
    }

    @Test
    public void testMaximumBoundaryValueFourNumBytesUnsigned()
    {
        // long value = Long.MAX_VALUE;
        int offset = 0;
        long resultLong;
        int numBytes = 4;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[0] = (byte) 0x7f;
        byteArray[1] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[6] = (byte) 0xff;
        byteArray[7] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, numBytes, noSignExtend);
        assertEquals(0x7fffffffL, resultLong);

        /* Little Endian */
        byteArray[7] = (byte) 0x7f;
        byteArray[6] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[1] = (byte) 0xff;
        byteArray[0] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, numBytes, noSignExtend);
        assertEquals(0x00ffffffffL, resultLong);
    }

    @Test
    public void testMaximumBoundaryValueThreeNumBytesUnsigned()
    {
        // long value = Long.MAX_VALUE;
        int offset = 0;
        long resultLong;
        int numBytes = 3;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[0] = (byte) 0x7f;
        byteArray[1] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[6] = (byte) 0xff;
        byteArray[7] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, numBytes, noSignExtend);
        assertEquals(0x7fffffL, resultLong);

        /* Little Endian */

        byteArray[7] = (byte) 0x7f;
        byteArray[6] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[1] = (byte) 0xff;
        byteArray[0] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, numBytes, noSignExtend);
        assertEquals(0x00ffffffL, resultLong);
    }

    @Test
    public void testMaximumBoundaryValueTwoNumBytesUnsigned()
    {
        // long value = Long.MAX_VALUE;
        int offset = 0;
        long resultLong;
        int numBytes = 2;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[0] = (byte) 0x7f;
        byteArray[1] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[6] = (byte) 0xff;
        byteArray[7] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, numBytes, noSignExtend);
        assertEquals(0x7fffL, resultLong);

        /* Little Endian */
        byteArray[7] = (byte) 0x7f;
        byteArray[6] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[1] = (byte) 0xff;
        byteArray[0] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, numBytes, noSignExtend);
        assertEquals(0x00ffffL, resultLong);
    }

    @Test
    public void testMaximumBoundaryValueOneNumByteUnsigned()
    {
        // long value = Long.MAX_VALUE;
        int offset = 0;
        long resultLong;
        int numBytes = 1;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[0] = (byte) 0x7f;
        byteArray[1] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[6] = (byte) 0xff;
        byteArray[7] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, numBytes, noSignExtend);
        assertEquals(0x7fL, resultLong);

        byteArray[7] = (byte) 0x7f;
        byteArray[6] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[1] = (byte) 0xff;
        byteArray[0] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, numBytes, noSignExtend);
        assertEquals(0x00ffL, resultLong);
    }

    /* Sign extension should not happen when numBytes = 8 */
    @Test
    public void testMaximumBoundaryValueEightNumBytes()
    {
        // long value = Long.MAX_VALUE;
        int offset = 0;
        long resultLong;
        int numBytes = 8;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[0] = (byte) 0x7f;
        byteArray[1] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[6] = (byte) 0xff;
        byteArray[7] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, numBytes, signExtend);
        assertEquals(0x7fffffffffffffffL, resultLong);

        /* Little Endian */
        byteArray[7] = (byte) 0x7f;
        byteArray[6] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[1] = (byte) 0xff;
        byteArray[0] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, numBytes, signExtend);
        assertEquals(0x7fffffffffffffffL, resultLong);

    }

    /* Simple test for Long.MAX_VALUE */
    @Test
    public void testMaximumBoundaryValueEightNumBytesUnsigned()
    {
        // long value = Long.MAX_VALUE;
        int offset = 0;
        long resultLong;
        int numBytes = 8;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        byteArray[0] = (byte) 0x7f;
        byteArray[1] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[6] = (byte) 0xff;
        byteArray[7] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, numBytes, noSignExtend);
        assertEquals(0x7fffffffffffffffL, resultLong);

        /* Little Endian */

        byteArray[7] = (byte) 0x7f;
        byteArray[6] = (byte) 0xff;
        byteArray[5] = (byte) 0xff;
        byteArray[4] = (byte) 0xff;
        byteArray[3] = (byte) 0xff;
        byteArray[2] = (byte) 0xff;
        byteArray[1] = (byte) 0xff;
        byteArray[0] = (byte) 0xff;

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, numBytes, noSignExtend);
        assertEquals(0x7fffffffffffffffL, resultLong);

    }

    @Test
    public void testZero()
    {
        long value = 0;
        int offset = 0;
        long resultLong;

        Arrays.fill(byteArray, (byte) 0x0);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, 0, noSignExtend);
        assertEquals(value, resultLong);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, 0, noSignExtend);
        assertEquals(value, resultLong);
    }

    /*
     * Take longs fitting within 3, 4 and 6 bytes and create byte arrays. For
     * each, perform the following tests: 1. Unmarshall completely (8 numBytes)
     * - signed & unsigned 2. Unmarshall with the same number of numBytes -
     * signed & unsigned 3. Unmarshall with a lesser number of numBytes - signed
     * & unsigned 4. Negate and unmarshall with a lesser number of numBytes -
     * signed & unsigned
     */

    @Test
    public void testThreeByteLongUnmarshallCompleteSigned()
    {
        int NUMBER_OF_DIGITS = 6;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= SEVEN_DIGIT_MIN || value < SIX_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, 8, signExtend);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, 8, signExtend);
        assertEquals(value, resultLong);
    }

    @Test
    public void testThreeByteLongUnmarshallCompleteUnsigned()
    {
        int NUMBER_OF_DIGITS = 6;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= SEVEN_DIGIT_MIN || value < SIX_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, 8, noSignExtend);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, 8, noSignExtend);
        assertEquals(value, resultLong);
    }

    @Test
    public void testThreeByteLongUnmarshallThreeNumBytesSigned()
    {
        int NUMBER_OF_DIGITS = 6;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= SEVEN_DIGIT_MIN || value < SIX_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, 3, signExtend);
        assertEquals(0, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, 3, signExtend);
        assertEquals((value << 40) >> 40, resultLong);
    }

    @Test
    public void testThreeByteLongUnmarshallThreeNumBytesUnsigned()
    {
        int NUMBER_OF_DIGITS = 6;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= SEVEN_DIGIT_MIN || value < SIX_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, 3, noSignExtend);
        assertEquals(value >>> 40, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, 3, noSignExtend);
        assertEquals((value << 40) >>> 40, resultLong);
    }

    @Test
    public void testThreeByteLongUnmarshallTwoNumBytesSigned()
    {
        int NUMBER_OF_DIGITS = 6;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= SEVEN_DIGIT_MIN || value < SIX_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, 2, signExtend);
        assertEquals(0, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, 2, signExtend);
        assertEquals((value << 48) >> 48, resultLong);
    }

    @Test
    public void testThreeByteLongUnmarshallTwoNumBytesUnsigned()
    {
        int NUMBER_OF_DIGITS = 6;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= SEVEN_DIGIT_MIN || value < SIX_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, 2, noSignExtend);
        assertEquals(0, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, 2, noSignExtend);
        assertEquals((value << 48) >>> 48, resultLong);
    }

    @Test
    public void testNegativeLongUnmarshallFourNumBytesSigned()
    {
        int NUMBER_OF_DIGITS = 6;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= SEVEN_DIGIT_MIN || value < SIX_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, 4, signExtend);
        assertEquals(0, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, 4, signExtend);
        assertEquals((value << 32) >> 32, resultLong);
    }

    @Test
    public void testNegativeLongUnmarshallFourNumBytesUnsigned()
    {
        int NUMBER_OF_DIGITS = 6;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= SEVEN_DIGIT_MIN || value < SIX_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, 4, noSignExtend);
        assertEquals(0, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, 4, noSignExtend);
        assertEquals((value << 32) >>> 32, resultLong);
    }

    @Test
    public void testFourByteLongUnmarshallCompleteSigned()
    {
        // initialization
        byte[] beByteArray = new byte[ARRAY_SIZE];
        byte[] leByteArray = new byte[ARRAY_SIZE];
        int NUMBER_OF_DIGITS = 8;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= NINE_DIGIT_MIN || value < EIGHT_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(beByteArray, (byte) 0x0);
        for (int i = 0; i < LONGSIZE; i++)
            beByteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        for (int i = 0; i < LONGSIZE; i++)
            leByteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);
        // end of invocation

        // test execution
        long resultLong1 = ByteArrayUnmarshaller.readLong(beByteArray, offset, isBigEndian, 8, signExtend);
        long resultLong2 = ByteArrayUnmarshaller.readLong(leByteArray, offset, isLittleEndian, 8, signExtend);

        // verification
        assertEquals(value, resultLong1);
        assertEquals(value, resultLong2);
    }

    @Test
    public void testFourByteLongUnmarshallCompleteUnsigned()
    {
        int NUMBER_OF_DIGITS = 8;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= NINE_DIGIT_MIN || value < EIGHT_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, 8, noSignExtend);
        assertEquals(value, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, 8, noSignExtend);
        assertEquals(value, resultLong);
    }

    @Test
    public void testFourByteLongUnmarshallFourNumBytesSigned()
    {
        int NUMBER_OF_DIGITS = 8;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= NINE_DIGIT_MIN || value < EIGHT_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, 4, signExtend);
        assertEquals(0, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, 4, signExtend);
        assertEquals((value << 32) >> 32, resultLong);
    }

    @Test
    public void testFourByteLongUnmarshallFourNumBytesUnsigned()
    {
        int NUMBER_OF_DIGITS = 8;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= NINE_DIGIT_MIN || value < EIGHT_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, 4, noSignExtend);
        assertEquals(0, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, 4, noSignExtend);
        assertEquals((value << 32) >>> 32, resultLong);
    }

    @Test
    public void testFourByteLongUnmarshallTwoNumBytesSigned()
    {
        int NUMBER_OF_DIGITS = 8;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= NINE_DIGIT_MIN || value < EIGHT_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, 2, signExtend);
        assertEquals(0, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, 2, signExtend);
        assertEquals((value << 48) >> 48, resultLong);
    }

    @Test
    public void testFourByteLongUnmarshallTwoNumBytesUnsigned()
    {
        int NUMBER_OF_DIGITS = 8;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= NINE_DIGIT_MIN || value < EIGHT_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, 2, noSignExtend);
        assertEquals(0, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, 2, noSignExtend);
        assertEquals((value << 48) >>> 48, resultLong);
    }

    @Test
    public void testFourByteNegativeLongUnmarshallFourNumBytesSigned()
    {
        int NUMBER_OF_DIGITS = 8;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= NINE_DIGIT_MIN || value < EIGHT_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, 4, signExtend);
        assertEquals(0, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, 4, signExtend);
        assertEquals((value << 32) >> 32, resultLong);
    }

    @Test
    public void testFourByteNegativeLongUnmarshallFourNumBytesUnsigned()
    {
        int NUMBER_OF_DIGITS = 8;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= NINE_DIGIT_MIN || value < EIGHT_DIGIT_MIN);
        int offset = genRandomOffset();

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, 4, noSignExtend);
        assertEquals(0, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, 4, noSignExtend);
        assertEquals((value << 32) >>> 32, resultLong);
    }

    // Test for checking roundtrip from long->byteArray->long for seven numBytes
    @Test
    public void testLongToByteArrayToLongSevenNumBytes()
    {
        byte[] buf = new byte[7];
        long in = 0x12345678987654L;
        ByteArrayMarshaller.writeLong(in, buf, 0, true, 7);
        long out = ByteArrayUnmarshaller.readLong(buf, 0, true, 7, true);
        assertEquals(in, out);
    }

    /* Test for an offset value which is valid only for a given numBytes */
    @Test
    public void testValidOffsetForGivenNumBytesSigned()
    {

        int NUMBER_OF_DIGITS = 8;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= NINE_DIGIT_MIN || value < EIGHT_DIGIT_MIN);
        int offset = ARRAY_SIZE - 8;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        offset += 4;
        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, 4, signExtend);
        assertEquals((value << 32) >> 32, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        offset = ARRAY_SIZE - 8;
        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        offset += 4;
        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, 4, signExtend);
        assertEquals(value >> 32, resultLong);
    }

    @Test
    public void testValidOffsetForGivenNumBytesUnsigned()
    {
        int NUMBER_OF_DIGITS = 8;
        long value, resultLong;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= NINE_DIGIT_MIN || value < EIGHT_DIGIT_MIN);
        int offset = ARRAY_SIZE - 8;

        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (LONGSIZE - 1 - i)) & 0x0ff);

        offset += 4;
        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, 4, noSignExtend);
        assertEquals((value << 32) >>> 32, resultLong);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);
        offset = ARRAY_SIZE - 8;
        for (int i = 0; i < LONGSIZE; i++)
            byteArray[offset + i] = (byte) ((value >> (LONGSIZE * i)) & 0x0ff);

        offset += 4;
        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, 4, noSignExtend);
        assertEquals(value >>> 32, resultLong);
    }

    /* Test for invalid offsets */
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testInvalidOffsets()
    {
        int NUMBER_OF_DIGITS = 10;
        long value;
        do
        {
            value = getRandomLongWithNHexDigits(NUMBER_OF_DIGITS);
        } while (value >= ELEVEN_DIGIT_MIN || value < TEN_DIGIT_MIN);
        int offset = genRandomOutofBoundsOffset();
        int numBytes = 3;

        value *= -1;
        /* Big Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            byteArray[offset + i] = (byte) ((value >> BYTESIZE * (numBytes - 1 - i)) & 0x0ff);

        ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, numBytes, noSignExtend);

        /* Little Endian */
        Arrays.fill(byteArray, (byte) 0x0);

        for (int i = 0; i < numBytes; i++)
            byteArray[offset + i] = (byte) ((value >> (BYTESIZE * i)) & 0x0ff);

        ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, numBytes, noSignExtend);

    }

    @Test(expected = NullPointerException.class)
    public void testNullByteArray()
    {
        long value = 0;
        int offset = 0;
        long resultLong;

        byte[] nullByteArray = null;

        resultLong = ByteArrayUnmarshaller.readLong(nullByteArray, offset, isBigEndian, 1, noSignExtend);
        assertEquals(value, resultLong);

        resultLong = ByteArrayUnmarshaller.readLong(nullByteArray, offset, isLittleEndian, 1, noSignExtend);
        assertEquals(value, resultLong);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidNumBytes()
    {
        long value = 0;
        int offset = 0;
        long resultLong;

        Arrays.fill(byteArray, (byte) 0x0);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isBigEndian, 9, noSignExtend);
        assertEquals(value, resultLong);

        resultLong = ByteArrayUnmarshaller.readLong(byteArray, offset, isLittleEndian, -1, noSignExtend);
        assertEquals(value, resultLong);
    }

    @Test
    public void testUnsignedExtendOneByte()
    {
        // Populate bytearray with bytes 0 to 256.
        for (int i = 0; i < 256; i++)
        {
            byteArray[i] = (byte) i;
        }

        long resultInt;
        // Test load of integer from byteArray unsigned -> should match integer
        // i value.
        for (int i = 0; i < 256; i++)
        {
            resultInt = ByteArrayUnmarshaller.readLong(byteArray, i, isBigEndian, 1, noSignExtend);
            assertEquals("testUnsignedExtendOneByte incorrect result.", (long) i, resultInt);
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

        long resultInt;
        // Test load of integer from byteArray signed -> Should range from 0 to
        // 127, then -128 to -1
        for (int i = 0; i < 256; i++)
        {
            resultInt = ByteArrayUnmarshaller.readLong(byteArray, i, isBigEndian, 1, signExtend);
            assertEquals("testUnsignedExtendOneByte incorrect result.", (byte) i, resultInt);
        }
    }

}