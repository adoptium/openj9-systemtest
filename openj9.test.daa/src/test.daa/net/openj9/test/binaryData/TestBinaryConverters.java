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

import java.util.Arrays;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.dataaccess.*;

import net.openj9.test.Utils;
import net.openj9.test.Utils.TestValue;

public class TestBinaryConverters
{
    static String outputFile;

    static
    {
        outputFile = "expected." + TestBinaryConverters.class.getSimpleName() + ".txt";
    };

    public void testEverythingRepeatedly()
    {
        /*
         * Run tests twice to get around the count=1 command line option for JIT
         * De-activate and reactivate log recording to only record once
         */

        testDoubleNormals();
        testDoubleNormals();

        testDoubleExceptions();
        testDoubleExceptions();

        testFloatNormals();
        testFloatNormals();

        testFloatExceptions();
        testFloatExceptions();

        testIntegerNormals();
        testIntegerNormals();

        testIntegerExceptions();
        testIntegerExceptions();

        testShortNormals();
        testShortNormals();

        testShortExceptions();
        testShortExceptions();

        testLongNormals();
        testLongNormals();

        testLongExceptions();
        testLongExceptions();
    }

    @BeforeClass
    public static void setUp()
    {
    }

    @AfterClass
    public static void tearDown()
    {
    }

    @Test
    public void testShortNormals()
    {
        /*
         * Testing all normal values. All should be expected. All should be
         * generated (since they use final constants for parameters).
         */
        short value = Utils.TestValue.SmallPositive.ShortValue;
        byte[] byte_array = new byte[16];
        final int offset0 = 0;
        final boolean bigEndian = true;
        int result;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.LargePositive.ShortValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.SmallNegative.ShortValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.LargeNegative.ShortValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.LargestPossible.ShortValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.SmallestPossible.ShortValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.Zero.ShortValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        /*
         * Testing normal values with Small Endian. Should all be expected. Should
         * all be generated.
         */

        final boolean smallEndian = false;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, smallEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.LargePositive.ShortValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, smallEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.SmallNegative.ShortValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, smallEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.LargeNegative.ShortValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, smallEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.LargestPossible.ShortValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, smallEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.SmallestPossible.ShortValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, smallEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.Zero.ShortValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, smallEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        /*
         * Testing different offsets. Should all be generated since all offsets
         * are final constant parameters.
         */

        final int offset1 = 1;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset1, bigEndian);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset1, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset1, bigEndian, result, offset1, bigEndian), value, result);
        }

        value = Utils.TestValue.LargePositive.ShortValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        final int offset2 = 2;

        value = Utils.TestValue.SmallNegative.ShortValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset2, bigEndian);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset2, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset2, bigEndian, result, offset2, bigEndian), value, result);
        }

        value = Utils.TestValue.LargeNegative.ShortValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset2, bigEndian);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset2, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset2, bigEndian, result, offset2, bigEndian), value, result);
        }

        value = Utils.TestValue.LargestPossible.ShortValue;

        final int offset4 = 4;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset4, bigEndian);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset4, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset4, bigEndian, result, offset4, bigEndian), value, result);
        }

        value = Utils.TestValue.SmallestPossible.ShortValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset4, bigEndian);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset4, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset4, bigEndian, result, offset4, bigEndian), value, result);
        }

        value = Utils.TestValue.Zero.ShortValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset4, bigEndian);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset4, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset4, bigEndian, result, offset4, bigEndian), value, result);
        }

        /*
         * Testing byte lengths and sign extension. Use Utils modify length function.
         */

        value = TestValue.LargePositive.ShortValue;

        final int length1 = 1;
        final boolean noSign = false;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, bigEndian, length1);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, bigEndian, length1, noSign);

        // ShortValue = 0x61a8
        short correct = 0x00a8;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length1, result, offset0, bigEndian, length1, noSign), correct, result);
        }

        value = TestValue.SmallestPossible.ShortValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, bigEndian, length1);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, bigEndian, length1, noSign);

        // ShortValue = 0x8000
        correct = 0x0000;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length1, result, offset0, bigEndian, length1, noSign), correct, result);
        }

        final int length2 = 2;

        value = TestValue.SmallNegative.ShortValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, bigEndian, length2);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, bigEndian, length2, noSign);

        // ShortValue = 0xfff7
        correct = (short) 0xfff7;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length2, result, offset0, bigEndian, length2, noSign), correct, result);
        }

        value = TestValue.LargestPossible.ShortValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, bigEndian, length2);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, bigEndian, length2, noSign);

        // ShortValue = 0x7fff
        correct = 0x7fff;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length2, result, offset0, bigEndian, length2, noSign), correct, result);
        }

        /*
         * Testing byte lengths and sign extension. Use Utils modify length function.
         */

        value = TestValue.LargePositive.ShortValue;
        final boolean yesSign = true;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, bigEndian, length1);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, bigEndian, length1, yesSign);

        // ShortValue = 0x61a8
        correct = (short) 0xffa8;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length1, result, offset0, bigEndian, length1, yesSign), correct, result);
        }

        value = TestValue.SmallestPossible.ShortValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, bigEndian, length1);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, bigEndian, length1, yesSign);

        // ShortValue = 0x8000
        correct = 0x0000;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length1, result, offset0, bigEndian, length1, yesSign), correct, result);
        }

        value = TestValue.SmallNegative.ShortValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, bigEndian, length2);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, bigEndian, length2, yesSign);

        // ShortValue = fff7;
        correct = (short) 0xfff7;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length2, result, offset0, bigEndian, length2, yesSign), correct, result);
        }

        value = TestValue.LargestPossible.ShortValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, bigEndian, length2);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, bigEndian, length2, yesSign);

        // ShortValue = 0x7fff
        correct = (short) 0x7fff;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length2, result, offset0, bigEndian, length2, yesSign), correct, result);
        }

        /*
         * Testing non constants. Should all be not expected. Should all be not
         * generated. Parameters are not constants (use Utils.destroyConstant).
         * Although, the optimizer might not pick up simple constant propagation
         * due to optimization scheduling.
         */

        boolean variableEndian;
        int temp = Utils.destroyConstant(1);
        variableEndian = (temp == 1);
        // variableEndian should be a non constant true;
        int variableLength = Utils.destroyConstant(2);

        // should all be not expected

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, variableEndian);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, variableEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, variableEndian, result, offset0, variableEndian), value, result);
        }

        value = Utils.TestValue.LargePositive.ShortValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, variableEndian);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, variableEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, variableEndian, result, offset0, variableEndian), value, result);
        }

        // should all be not expected

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, bigEndian, variableLength);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, bigEndian, variableLength, noSign);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, variableLength, result, offset0, bigEndian, variableLength, noSign), value, result);
        }

        value = Utils.TestValue.LargePositive.ShortValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeShort(value, byte_array, offset0, bigEndian, variableLength);

        result = ByteArrayUnmarshaller.readShort(byte_array, offset0, bigEndian, variableLength, noSign);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, variableLength, result, offset0, bigEndian, variableLength, noSign), value, result);
        }
    }

    @Test
    public void testShortExceptions()
    {
        

        /*
         * Test negative offsets. Test offset out of bounds. Test null byte array.
         * Test offset and length out of bounds. Test illegal lengths.
         */

        short value = Utils.TestValue.SmallPositive.ShortValue;
        byte[] byte_array = new byte[4];
        final int negativeOffset1 = -1;
        final int offset0 = 0;
        final boolean bigEndian = true;
        float result = 0;

        Arrays.fill(byte_array, (byte) 0x00);

        try
        {
            ByteArrayMarshaller.writeShort(value, byte_array, negativeOffset1, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testShortExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, negativeOffset1, bigEndian, result, negativeOffset1, bigEndian));
        }

        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readShort(byte_array, negativeOffset1, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testShortExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, negativeOffset1, bigEndian, result, negativeOffset1, bigEndian));
        }

        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        final int largeOffset50 = 50;
        value = Utils.TestValue.LargeNegative.ShortValue;

        Arrays.fill(byte_array, (byte) 0x00);

        try
        {
            ByteArrayMarshaller.writeShort(value, byte_array, largeOffset50, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testShortExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, largeOffset50, bigEndian, result, largeOffset50, bigEndian));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readShort(byte_array, largeOffset50, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testShortExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, largeOffset50, bigEndian, result, largeOffset50, bigEndian));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        final int offset3 = 3;
        final int length2 = 2;
        final boolean noSign = false;
        final boolean yesSign = true;

        try
        {
            byte_array = new byte[4];
            ByteArrayMarshaller.writeShort(value, byte_array, offset3, bigEndian, length2);

            fail(Utils.makeTestNameBinaryConvert("", "testShortExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset3, bigEndian, length2, result, offset3, bigEndian, length2, false));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readShort(byte_array, offset3, bigEndian, length2, noSign);

            fail(Utils.makeTestNameBinaryConvert("", "testShortExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset3, bigEndian, length2, result, offset3, bigEndian, length2, noSign));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.LargeNegative.ShortValue;

        try
        {
            byte_array = new byte[4];
            ByteArrayMarshaller.writeShort(value, byte_array, offset3, bigEndian, length2);

            fail(Utils.makeTestNameBinaryConvert("", "testShortExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset3, bigEndian, length2, result, offset3, bigEndian, length2, noSign));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readShort(byte_array, offset3, bigEndian, length2, yesSign);

            fail(Utils.makeTestNameBinaryConvert("", "testShortExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset3, bigEndian, length2, result, offset3, bigEndian, length2, yesSign));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        byte_array = null;

        try
        {
            ByteArrayMarshaller.writeShort(value, byte_array, offset0, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testShortExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian));
        }
        catch (NullPointerException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readShort(byte_array, offset0, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testShortExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian));
        }
        catch (NullPointerException e)
        {

        }

        final int length60 = 60;
        final int negativeLength1 = -1;

        try
        {
            byte_array = new byte[8];
            ByteArrayMarshaller.writeShort(value, byte_array, offset0, bigEndian, length60);

            fail(Utils.makeTestNameBinaryConvert("", "testShortExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length60, result, offset0, bigEndian, length60, false));
        }
        catch (IllegalArgumentException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readShort(byte_array, offset0, bigEndian, length60, noSign);

            fail(Utils.makeTestNameBinaryConvert("", "testShortExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length60, result, offset0, bigEndian, length60, noSign));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            byte_array = new byte[8];
            ByteArrayMarshaller.writeShort(value, byte_array, offset0, bigEndian, negativeLength1);

            fail(Utils.makeTestNameBinaryConvert("", "testShortExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, negativeLength1, result, offset0, bigEndian, negativeLength1, noSign));
        }
        catch (IllegalArgumentException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readShort(byte_array, offset0, bigEndian, negativeLength1, noSign);

            fail(Utils.makeTestNameBinaryConvert("", "testShortExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, negativeLength1, result, offset0, bigEndian, negativeLength1, noSign));
        }
        catch (IllegalArgumentException e)
        {

        }

    }

    @Test
    public void testLongNormals()
    {
        /*
         * Testing all normal values. All should be expected. All should be
         * generated (since they use final constants for parameters).
         */
        long value = Utils.TestValue.SmallPositive.LongValue;
        byte[] byte_array = new byte[16];
        final int offset0 = 0;
        final boolean bigEndian = true;
        long result;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.LargePositive.LongValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.SmallNegative.LongValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.LargeNegative.LongValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.LargestPossible.LongValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian);

        try {
            assertEquals(value, result);
        }
        catch (AssertionError e)
        {
        	assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }
        
        value = Utils.TestValue.SmallestPossible.LongValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.Zero.LongValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        /*
         * Testing normal values with Small Endian. Should all be expected. Should all be generated
         */

        final boolean smallEndian = false;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, smallEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.LargePositive.LongValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, smallEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.SmallNegative.LongValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, smallEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.LargeNegative.LongValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, smallEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.LargestPossible.LongValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, smallEndian);

        Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian);
        
        try 
        {
            assertEquals(value, result);
        }
        catch (AssertionError e)
        {
        	assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.SmallestPossible.LongValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, smallEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.Zero.LongValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, smallEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        /*
         * Testing different offsets. Should all be generated since all offsets are final constant parameters.
         */

        final int offset1 = 1;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset1, bigEndian);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset1, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset1, bigEndian, result, offset1, bigEndian), value, result);
        }

        value = Utils.TestValue.LargePositive.LongValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        final int offset2 = 2;

        value = Utils.TestValue.SmallNegative.LongValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset2, bigEndian);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset2, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset2, bigEndian, result, offset2, bigEndian), value, result);
        }

        value = Utils.TestValue.LargeNegative.LongValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset2, bigEndian);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset2, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset2, bigEndian, result, offset2, bigEndian), value, result);
        }

        value = Utils.TestValue.LargestPossible.LongValue;

        final int offset4 = 4;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset4, bigEndian);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset4, bigEndian);

        try
        {
            assertEquals(value, result);
        }
        catch (AssertionError e)
        {
        	assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset4, bigEndian, result, offset4, bigEndian), value, result);
        }

        value = Utils.TestValue.SmallestPossible.LongValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset4, bigEndian);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset4, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset4, bigEndian, result, offset4, bigEndian), value, result);
        }

        value = Utils.TestValue.Zero.LongValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset4, bigEndian);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset4, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset4, bigEndian, result, offset4, bigEndian), value, result);
        }

        /*
         * Testing byte lengths and sign extension. Use Utils modify length function.
         */

        value = TestValue.LargePositive.LongValue;
        final int length1 = 1;
        final boolean noSign = false;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian, length1);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian, length1, noSign);

        // LongValue = 0x35a4e900
        long correct = 0x0000000000000000l;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length1, result, offset0, bigEndian, length1, noSign), correct, result);
        }

        value = TestValue.SmallestPossible.LongValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian, length1);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian, length1, noSign);

        // LongValue = 0x8000000000000000
        correct = 0x0000000000000000l;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length1, result, offset0, bigEndian, length1, noSign), correct, result);
        }

        final int length2 = 2;

        value = TestValue.SmallNegative.LongValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian, length2);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian, length2, noSign);

        // LongValue = 0xfffffffffffffff7
        correct = 0x00000000000000fff7l;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length2, result, offset0, bigEndian, length2, noSign), correct, result);
        }

        value = TestValue.LargestPossible.LongValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian, length2);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian, length2, noSign);

        correct = 0x00000000000000ffffl;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length2, result, offset0, bigEndian, length2, noSign), correct, result);
        }

        final int length3 = 3;
        {
            value = TestValue.SmallNegative.LongValue;
            Arrays.fill(byte_array, (byte) 0x00);
            ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian, length3);

            result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian, length3, noSign);

            // LongValue = 0xfffffffffffffff7
            correct = 0x000000000000fffff7l;
            try
            {
                assertEquals(correct, result);
            }

            catch (AssertionError e)
            {
                assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length3, result, offset0, bigEndian, length2, noSign), correct, result);
            }

            value = TestValue.LargestPossible.LongValue;
            Arrays.fill(byte_array, (byte) 0x00);
            ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian, length3);

            result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian, length3, noSign);

            correct = 0x000000000000ffffffl;
            try
            {
                assertEquals(correct, result);
            }

            catch (AssertionError e)
            {
                assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length3, result, offset0, bigEndian, length2, noSign), correct, result);
            }
        }

        final int length4 = 4;

        value = TestValue.SmallNegative.LongValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian, length4);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian, length4, noSign);

        // LongValue = 0xfffffffffffffff7
        correct = 0x00000000fffffff7l;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length4, result, offset0, bigEndian, length4, noSign), correct, result);
        }

        value = TestValue.LargestPossible.LongValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian, length4);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian, length4, noSign);

        // LongValue = 0x7fffffffffffffff
        correct = 0x00000000ffffffffl;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length4, result, offset0, bigEndian, length4, noSign), correct, result);
        }

        final int length5 = 5;
        {
            value = TestValue.SmallNegative.LongValue;
            Arrays.fill(byte_array, (byte) 0x00);
            ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian, length5);

            result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian, length5, noSign);

            // LongValue = 0xfffffffffffffff7
            correct = 0x00000000fffffffff7l;
            try
            {
                assertEquals(correct, result);
            }

            catch (AssertionError e)
            {
                assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length5, result, offset0, bigEndian, length2, noSign), correct, result);
            }

            value = TestValue.LargestPossible.LongValue;
            Arrays.fill(byte_array, (byte) 0x00);
            ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian, length5);

            result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian, length5, noSign);

            correct = 0x00000000ffffffffffl;
            try
            {
                assertEquals(correct, result);
            }

            catch (AssertionError e)
            {
                assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length5, result, offset0, bigEndian, length2, noSign), correct, result);
            }
        }

        final int length6 = 6;
        {
            value = TestValue.SmallNegative.LongValue;
            Arrays.fill(byte_array, (byte) 0x00);
            ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian, length6);

            result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian, length6, noSign);

            // LongValue = 0xfffffffffffffff7
            correct = 0x000000fffffffffff7l;
            try
            {
                assertEquals(correct, result);
            }

            catch (AssertionError e)
            {
                assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length6, result, offset0, bigEndian, length2, noSign), correct, result);
            }

            value = TestValue.LargestPossible.LongValue;
            Arrays.fill(byte_array, (byte) 0x00);
            ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian, length6);

            result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian, length6, noSign);

            correct = 0x000000ffffffffffffl;
            try
            {
                assertEquals(correct, result);
            }

            catch (AssertionError e)
            {
                assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length6, result, offset0, bigEndian, length2, noSign), correct, result);
            }
        }

        final int length7 = 7;
        {
            value = TestValue.SmallNegative.LongValue;
            Arrays.fill(byte_array, (byte) 0x00);
            ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian, length7);

            result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian, length7, noSign);

            // LongValue = 0xfffffffffffffff7
            correct = 0x0000fffffffffffff7l;
            try
            {
                assertEquals(correct, result);
            }

            catch (AssertionError e)
            {
                assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length7, result, offset0, bigEndian, length2, noSign), correct, result);
            }

            value = TestValue.LargestPossible.LongValue;
            Arrays.fill(byte_array, (byte) 0x00);
            ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian, length7);

            result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian, length7, noSign);

            correct = 0x0000ffffffffffffffl;
            try
            {
                assertEquals(correct, result);
            }

            catch (AssertionError e)
            {
                assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length7, result, offset0, bigEndian, length2, noSign), correct, result);
            }
        }

        final int length8 = 8;

        value = TestValue.SmallestPossible.LongValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian, length8);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian, length8, noSign);

        // LongValue = 0x8000000000000000
        correct = 0x8000000000000000l;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length8, result, offset0, bigEndian, length8, noSign), correct, result);
        }

        value = TestValue.SmallPositive.LongValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian, length8);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian, length8, noSign);

        // LongValue = 0x9
        correct = 0x0000000000000009l;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length8, result, offset0, bigEndian, length8, noSign), correct, result);
        }

        /*
         * Testing byte lengths and sign extension. Use Utils modify length function
         */

        value = TestValue.LargePositive.LongValue;
        final boolean yesSign = true;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian, length1);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian, length1, yesSign);

        // LongValue = 0x35a4e900
        correct = 0x0000000000000000l;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length1, result, offset0, bigEndian, length1, yesSign), correct, result);
        }

        value = TestValue.SmallestPossible.LongValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian, length1);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian, length1, yesSign);

        // LongValue = 0x8000000000000000
        correct = 0x0000000000000000l;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length1, result, offset0, bigEndian, length1, yesSign), correct, result);
        }

        value = TestValue.SmallNegative.LongValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian, length2);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian, length2, yesSign);

        // LongValue = fffffffffffffff7;
        correct = 0xfffffffffffffff7l;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length2, result, offset0, bigEndian, length2, yesSign), correct, result);
        }

        value = TestValue.LargestPossible.LongValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian, length2);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian, length2, yesSign);

        // LongValue = 0x7fffffffffffffff
        correct = 0xffffffffffffffffl;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length2, result, offset0, bigEndian, length2, yesSign), correct, result);
        }

        value = TestValue.SmallNegative.LongValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian, length4);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian, length4, yesSign);

        // LongValue = 0xfffffff7
        correct = 0xfffffffffffffff7l;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length4, result, offset0, bigEndian, length4, yesSign), correct, result);
        }

        value = TestValue.LargestPossible.LongValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian, length4);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian, length4, yesSign);

        // LongValue = 0x7fffffffffffffff;
        correct = 0xffffffffffffffffl;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length4, result, offset0, bigEndian, length4, yesSign), correct, result);
        }

        /*
         * Testing non constants. Should all be not expected. Should all be not
         * generated. Parameters are not constants (use Utils.destroyConstant).
         * Although, the optimizer might not pick up simple constant propagation
         * due to optimization scheduling.
         */

        boolean variableEndian;
        int temp = Utils.destroyConstant(1);
        variableEndian = (temp == 1);
        // variableEndian should be a non constant true;
        int variableLength = Utils.destroyConstant(4);

        value = Utils.TestValue.SmallPositive.LongValue;

        // should all be not expected

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, variableEndian);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, variableEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, variableEndian, result, offset0, variableEndian), value, result);
        }

        value = Utils.TestValue.LargePositive.LongValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, variableEndian);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, variableEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, variableEndian, result, offset0, variableEndian), value, result);
        }

        // should all be not expected

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian, variableLength);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian, variableLength, noSign);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, variableLength, result, offset0, bigEndian, variableLength, noSign), value, result);
        }

        value = Utils.TestValue.LargePositive.LongValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian, variableLength);

        result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian, variableLength, noSign);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, variableLength, result, offset0, bigEndian, variableLength, noSign), value, result);
        }
    }

    @Test
    public void testLongExceptions()
    {
        long value = Utils.TestValue.SmallPositive.LongValue;
        byte[] byte_array = new byte[8];
        final int negativeOffset1 = -1;
        final int offset0 = 0;
        final boolean bigEndian = true;
        long result = 0;

        try
        {
            byte_array = new byte[8];
            ByteArrayMarshaller.writeLong(value, byte_array, negativeOffset1, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testLongExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, negativeOffset1, bigEndian, result, negativeOffset1, bigEndian));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readLong(byte_array, negativeOffset1, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testLongExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, negativeOffset1, bigEndian, result, negativeOffset1, bigEndian));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        final int largeOffset50 = 50;
        value = Utils.TestValue.LargeNegative.LongValue;

        try
        {
            byte_array = new byte[8];
            ByteArrayMarshaller.writeLong(value, byte_array, largeOffset50, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testLongExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, largeOffset50, bigEndian, result, largeOffset50, bigEndian));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readLong(byte_array, largeOffset50, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testLongExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, largeOffset50, bigEndian, result, largeOffset50, bigEndian));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        final int offset5 = 5;
        final int length4 = 4;
        final boolean noSign = false;
        final boolean yesSign = true;

        try
        {
            byte_array = new byte[8];
            ByteArrayMarshaller.writeLong(value, byte_array, offset5, bigEndian, length4);

            fail(Utils.makeTestNameBinaryConvert("", "testLongExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset5, bigEndian, length4, result, offset5, bigEndian, length4, false));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readLong(byte_array, offset5, bigEndian, length4, noSign);

            fail(Utils.makeTestNameBinaryConvert("", "testLongExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset5, bigEndian, length4, result, offset5, bigEndian, length4, noSign));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.LargeNegative.LongValue;

        try
        {
            byte_array = new byte[8];
            ByteArrayMarshaller.writeLong(value, byte_array, offset5, bigEndian, length4);

            fail(Utils.makeTestNameBinaryConvert("", "testLongExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset5, bigEndian, length4, result, offset5, bigEndian, length4, noSign));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readLong(byte_array, offset5, bigEndian, length4, yesSign);

            fail(Utils.makeTestNameBinaryConvert("", "testLongExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset5, bigEndian, length4, result, offset5, bigEndian, length4, yesSign));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        byte_array = null;

        try
        {
            ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testLongExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian));
        }
        catch (NullPointerException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testLongExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian));
        }
        catch (NullPointerException e)
        {

        }

        final int length60 = 60;
        final int negativeLength1 = -1;

        try
        {
            byte_array = new byte[64];
            ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian, length60);

            fail(Utils.makeTestNameBinaryConvert("", "testLongExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length60, result, offset0, bigEndian, length60, false));
        }
        catch (IllegalArgumentException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian, length60, noSign);

            fail(Utils.makeTestNameBinaryConvert("", "testLongExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length60, result, offset0, bigEndian, length60, noSign));
        }
        catch (IllegalArgumentException e)
        {

        }

        try
        {
            byte_array = new byte[64];
            ByteArrayMarshaller.writeLong(value, byte_array, offset0, bigEndian, negativeLength1);

            fail(Utils.makeTestNameBinaryConvert("", "testLongExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, negativeLength1, result, offset0, bigEndian, negativeLength1, noSign));
        }
        catch (IllegalArgumentException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readLong(byte_array, offset0, bigEndian, negativeLength1, noSign);

            fail(Utils.makeTestNameBinaryConvert("", "testLongExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, negativeLength1, result, offset0, bigEndian, negativeLength1, noSign));
        }
        catch (IllegalArgumentException e)
        {

        }
    }

    @Test
    public void testIntegerNormals()
    {
        /*
         * Testing all normal values. All should be expected. All should be
         * generated (since they use final constants for parameters).
         */
        int value = Utils.TestValue.SmallPositive.IntValue;
        byte[] byte_array = new byte[16];
        final int offset0 = 0;
        final boolean bigEndian = true;
        int result;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.LargePositive.IntValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.SmallNegative.IntValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.LargeNegative.IntValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.LargestPossible.IntValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.SmallestPossible.IntValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.Zero.IntValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        /*
         * Testing normal values with Small Endian.. Should all be expected. Should all be generated.
         */

        final boolean smallEndian = false;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, smallEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.LargePositive.IntValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, smallEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.SmallNegative.IntValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, smallEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.LargeNegative.IntValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, smallEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.LargestPossible.IntValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, smallEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.SmallestPossible.IntValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, smallEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.Zero.IntValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, smallEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        /*
         * Testing different offsets. Should all be generated since all offsets are final constant parameters.
         */

        final int offset1 = 1;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset1, bigEndian);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset1, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset1, bigEndian, result, offset1, bigEndian), value, result);
        }

        value = Utils.TestValue.LargePositive.IntValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        final int offset2 = 2;

        value = Utils.TestValue.SmallNegative.IntValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset2, bigEndian);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset2, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset2, bigEndian, result, offset2, bigEndian), value, result);
        }

        value = Utils.TestValue.LargeNegative.IntValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset2, bigEndian);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset2, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset2, bigEndian, result, offset2, bigEndian), value, result);
        }

        value = Utils.TestValue.LargestPossible.IntValue;

        final int offset4 = 4;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset4, bigEndian);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset4, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset4, bigEndian, result, offset4, bigEndian), value, result);
        }

        value = Utils.TestValue.SmallestPossible.IntValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset4, bigEndian);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset4, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset4, bigEndian, result, offset4, bigEndian), value, result);
        }

        value = Utils.TestValue.Zero.IntValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset4, bigEndian);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset4, bigEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset4, bigEndian, result, offset4, bigEndian), value, result);
        }

        /*
         * Testing byte lengths and sign extension. Use Utils modify length function
         */

        value = TestValue.LargePositive.IntValue;
        final int length1 = 1;
        final boolean noSign = false;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian, length1);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian, length1, noSign);

        // intValue = 0x895440
        int correct = 0x00000040;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length1, result, offset0, bigEndian, length1, noSign), correct, result);
        }

        value = TestValue.SmallestPossible.IntValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian, length1);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian, length1, noSign);

        // intValue = 0x80000000
        correct = 0x00000000;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length1, result, offset0, bigEndian, length1, noSign), correct, result);
        }

        final int length2 = 2;

        value = TestValue.SmallNegative.IntValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian, length2);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian, length2, noSign);

        // intValue = 0xfffffff7
        correct = 0x0000fff7;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length1, result, offset0, bigEndian, length1, noSign), correct, result);
        }

        value = TestValue.LargestPossible.IntValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian, length2);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian, length2, noSign);

        // intValue = 0x7fffffff
        correct = 0x0000ffff;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length1, result, offset0, bigEndian, length1, noSign), correct, result);
        }

        value = TestValue.LargestPossible.IntValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian, length2);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian, length2, noSign);

        // intValue = 0x7fffffff
        correct = 0x0000ffff;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length1, result, offset0, bigEndian, length1, noSign), correct, result);
        }

        final int length3 = 3;
        {
            value = TestValue.SmallNegative.IntValue;
            Arrays.fill(byte_array, (byte) 0x00);
            ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian, length3);

            result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian, length3, noSign);

            // intValue = 0xfffffff7
            correct = 0x00fffff7;
            try
            {
                assertEquals(correct, result);
            }

            catch (AssertionError e)
            {
                assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length1, result, offset0, bigEndian, length1, noSign), correct, result);
            }

            value = TestValue.LargestPossible.IntValue;
            Arrays.fill(byte_array, (byte) 0x00);
            ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian, length3);

            result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian, length3, noSign);

            // intValue = 0x7fffffff
            correct = 0x00ffffff;
            try
            {
                assertEquals(correct, result);
            }

            catch (AssertionError e)
            {
                assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length1, result, offset0, bigEndian, length1, noSign), correct, result);
            }

            value = TestValue.LargestPossible.IntValue;
            Arrays.fill(byte_array, (byte) 0x00);
            ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian, length3);

            result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian, length3, noSign);

            // intValue = 0x7fffffff
            correct = 0x00ffffff;
            try
            {
                assertEquals(correct, result);
            }

            catch (AssertionError e)
            {
                assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length1, result, offset0, bigEndian, length1, noSign), correct, result);
            }
        }

        final int length4 = 4;

        value = TestValue.SmallNegative.IntValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian, length4);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian, length4, noSign);

        // intValue = 0xfffffff7
        correct = 0xfffffff7;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length4, result, offset0, bigEndian, length4, noSign), correct, result);
        }

        value = TestValue.LargestPossible.IntValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian, length4);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian, length4, noSign);

        // intValue = 0x7fffffff
        correct = 0x7fffffff;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length4, result, offset0, bigEndian, length4, noSign), correct, result);
        }

        /*
         * Testing byte lengths and sign extension. Use Utils modify length function
         */

        value = TestValue.LargePositive.IntValue;
        final boolean yesSign = true;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian, length1);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian, length1, yesSign);

        // intValue = 0x895440
        correct = 0x00000040;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length1, result, offset0, bigEndian, length1, yesSign), correct, result);
        }

        value = TestValue.SmallestPossible.IntValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian, length1);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian, length1, yesSign);

        // intValue = 0x80000000
        correct = 0x00000000;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length1, result, offset0, bigEndian, length1, yesSign), correct, result);
        }

        value = TestValue.SmallNegative.IntValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian, length2);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian, length2, yesSign);

        // intValue = fffffff7;
        correct = 0xfffffff7;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length2, result, offset0, bigEndian, length2, yesSign), correct, result);
        }

        value = TestValue.LargestPossible.IntValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian, length2);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian, length2, yesSign);

        // intValue = 0x7fffffff
        correct = 0xffffffff;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length2, result, offset0, bigEndian, length2, yesSign), correct, result);
        }

        {
            value = TestValue.SmallNegative.IntValue;
            Arrays.fill(byte_array, (byte) 0x00);
            ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian, length3);

            result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian, length3, yesSign);

            // intValue = fffffff7;
            correct = 0xfffffff7;
            try
            {
                assertEquals(correct, result);
            }

            catch (AssertionError e)
            {
                assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length3, result, offset0, bigEndian, length3, yesSign), correct, result);
            }

            value = TestValue.LargestPossible.IntValue;
            Arrays.fill(byte_array, (byte) 0x00);
            ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian, length3);

            result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian, length3, yesSign);

            // intValue = 0x7fffffff
            correct = 0xffffffff;
            try
            {
                assertEquals(correct, result);
            }

            catch (AssertionError e)
            {
                assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length3, result, offset0, bigEndian, length3, yesSign), correct, result);
            }
        }

        value = TestValue.SmallNegative.IntValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian, length4);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian, length4, yesSign);

        // intValue = 0xfffffff7
        correct = 0xfffffff7;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length4, result, offset0, bigEndian, length4, yesSign), correct, result);
        }

        value = TestValue.LargestPossible.IntValue;
        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian, length4);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian, length4, yesSign);

        // intValue = 0x7fffffff;
        correct = 0x7fffffff;
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length4, result, offset0, bigEndian, length4, yesSign), correct, result);
        }

        /*
         * Testing non constants. Should all be not expected. Should all be not
         * generated. Parameters are not constants (use Utils.destroyConstant).
         * Although, the optimizer might not pick up simple constant propagation
         * due to optimization scheduling.
         */

        boolean variableEndian;
        int temp = Utils.destroyConstant(1);
        variableEndian = (temp == 1);
        // variableEndian should be a non constant true;
        int variableLength = Utils.destroyConstant(4);

        // should all be not expected

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, variableEndian);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, variableEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, variableEndian, result, offset0, variableEndian), value, result);
        }

        value = Utils.TestValue.LargePositive.IntValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, variableEndian);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, variableEndian);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, variableEndian, result, offset0, variableEndian), value, result);
        }

        // should all be not expected

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian, variableLength);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian, variableLength, noSign);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, variableLength, result, offset0, bigEndian, variableLength, noSign), value, result);
        }

        value = Utils.TestValue.LargePositive.IntValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian, variableLength);

        result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian, variableLength, noSign);

        try
        {
            assertEquals(value, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntegerNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, variableLength, result, offset0, bigEndian, variableLength, noSign), value, result);
        }
    }

    @Test
    public void testIntegerExceptions()
    {
        int value = Utils.TestValue.SmallPositive.IntValue;
        byte[] byte_array = new byte[8];
        final int negativeOffset1 = -1;
        final int offset0 = 0;
        final boolean bigEndian = true;
        float result = 0;

        try
        {
            Arrays.fill(byte_array, (byte) 0x00);
            ByteArrayMarshaller.writeInt(value, byte_array, negativeOffset1, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testIntegerExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, negativeOffset1, bigEndian, result, negativeOffset1, bigEndian));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readInt(byte_array, negativeOffset1, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testIntegerExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, negativeOffset1, bigEndian, result, negativeOffset1, bigEndian));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        final int largeOffset50 = 50;
        value = Utils.TestValue.LargeNegative.IntValue;

        try
        {
            Arrays.fill(byte_array, (byte) 0x00);
            ByteArrayMarshaller.writeInt(value, byte_array, largeOffset50, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testIntegerExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, largeOffset50, bigEndian, result, largeOffset50, bigEndian));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readInt(byte_array, largeOffset50, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testIntegerExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, largeOffset50, bigEndian, result, largeOffset50, bigEndian));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        final int offset5 = 5;
        final int length4 = 4;
        final boolean noSign = false;
        final boolean yesSign = true;

        try
        {
            byte_array = new byte[8];
            ByteArrayMarshaller.writeInt(value, byte_array, offset5, bigEndian, length4);

            fail(Utils.makeTestNameBinaryConvert("", "testIntegerExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset5, bigEndian, length4, result, offset5, bigEndian, length4, false));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readInt(byte_array, offset5, bigEndian, length4, noSign);

            fail(Utils.makeTestNameBinaryConvert("", "testIntegerExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset5, bigEndian, length4, result, offset5, bigEndian, length4, noSign));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.LargeNegative.IntValue;

        try
        {
            byte_array = new byte[8];
            ByteArrayMarshaller.writeInt(value, byte_array, offset5, bigEndian, length4);

            fail(Utils.makeTestNameBinaryConvert("", "testIntegerExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset5, bigEndian, length4, result, offset5, bigEndian, length4, noSign));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readInt(byte_array, offset5, bigEndian, length4, yesSign);

            fail(Utils.makeTestNameBinaryConvert("", "testIntegerExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset5, bigEndian, length4, result, offset5, bigEndian, length4, yesSign));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        byte_array = null;

        try
        {
            ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testIntegerExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian));
        }
        catch (NullPointerException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testIntegerExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian));
        }
        catch (NullPointerException e)
        {

        }

        final int length60 = 60;
        final int negativeLength1 = -1;

        try
        {
            byte_array = new byte[8];
            ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian, length60);

            fail(Utils.makeTestNameBinaryConvert("", "testIntegerExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length60, result, offset0, bigEndian, length60, false));
        }
        catch (IllegalArgumentException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian, length60, noSign);

            fail(Utils.makeTestNameBinaryConvert("", "testIntegerExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, length60, result, offset0, bigEndian, length60, noSign));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            byte_array = new byte[8];
            ByteArrayMarshaller.writeInt(value, byte_array, offset0, bigEndian, negativeLength1);

            fail(Utils.makeTestNameBinaryConvert("", "testIntegerExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, negativeLength1, result, offset0, bigEndian, negativeLength1, noSign));
        }
        catch (IllegalArgumentException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readInt(byte_array, offset0, bigEndian, negativeLength1, noSign);

            fail(Utils.makeTestNameBinaryConvert("", "testIntegerExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, negativeLength1, result, offset0, bigEndian, negativeLength1, noSign));
        }
        catch (IllegalArgumentException e)
        {

        }
    }

    @Test
    public void testFloatNormals()
    {
        /*
         * Testing all normal values. All should be expected. All should be
         * generated (since they use final constants for parameters)
         */
        float value = Utils.TestValue.SmallPositive.FloatValue;
        byte[] byte_array = new byte[16];
        final int offset0 = 0;
        final boolean bigEndian = true;
        float result;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeFloat(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readFloat(byte_array, offset0, bigEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testFloatNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.LargePositive.FloatValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeFloat(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readFloat(byte_array, offset0, bigEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testFloatNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.SmallNegative.FloatValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeFloat(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readFloat(byte_array, offset0, bigEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testFloatNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.LargeNegative.FloatValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeFloat(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readFloat(byte_array, offset0, bigEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testFloatNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.LargestPossible.FloatValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeFloat(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readFloat(byte_array, offset0, bigEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testFloatNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.SmallestPossible.FloatValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeFloat(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readFloat(byte_array, offset0, bigEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testFloatNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.Zero.FloatValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeFloat(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readFloat(byte_array, offset0, bigEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testFloatNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        /*
         * Testing normal values with Small Endian. Should all be not expected.
         * Should all be not generated.
         */

        final boolean smallEndian = false;

        // should all be not expected

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeFloat(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readFloat(byte_array, offset0, smallEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testFloatNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.LargePositive.FloatValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeFloat(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readFloat(byte_array, offset0, smallEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testFloatNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.SmallNegative.FloatValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeFloat(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readFloat(byte_array, offset0, smallEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testFloatNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.LargeNegative.FloatValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeFloat(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readFloat(byte_array, offset0, smallEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testFloatNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.LargestPossible.FloatValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeFloat(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readFloat(byte_array, offset0, smallEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testFloatNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.SmallestPossible.FloatValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeFloat(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readFloat(byte_array, offset0, smallEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testFloatNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.Zero.FloatValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeFloat(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readFloat(byte_array, offset0, smallEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testFloatNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        /*
         * Testing different offsets. Should all be generated since all offsets are final constant parameters.
         */

        final int offset1 = 1;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeFloat(value, byte_array, offset1, bigEndian);

        result = ByteArrayUnmarshaller.readFloat(byte_array, offset1, bigEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testFloatNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset1, bigEndian, result, offset1, bigEndian), value, result);
        }

        value = Utils.TestValue.LargePositive.FloatValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeFloat(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readFloat(byte_array, offset0, bigEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testFloatNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        final int offset2 = 2;

        value = Utils.TestValue.SmallNegative.FloatValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeFloat(value, byte_array, offset2, bigEndian);

        result = ByteArrayUnmarshaller.readFloat(byte_array, offset2, bigEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testFloatNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset2, bigEndian, result, offset2, bigEndian), value, result);
        }

        value = Utils.TestValue.LargeNegative.FloatValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeFloat(value, byte_array, offset2, bigEndian);

        result = ByteArrayUnmarshaller.readFloat(byte_array, offset2, bigEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testFloatNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset2, bigEndian, result, offset2, bigEndian), value, result);
        }

        value = Utils.TestValue.LargestPossible.FloatValue;

        final int offset4 = 4;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeFloat(value, byte_array, offset4, bigEndian);

        result = ByteArrayUnmarshaller.readFloat(byte_array, offset4, bigEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testFloatNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset4, bigEndian, result, offset4, bigEndian), value, result);
        }

        value = Utils.TestValue.SmallestPossible.FloatValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeFloat(value, byte_array, offset4, bigEndian);

        result = ByteArrayUnmarshaller.readFloat(byte_array, offset4, bigEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testFloatNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset4, bigEndian, result, offset4, bigEndian), value, result);
        }

        value = Utils.TestValue.Zero.FloatValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeFloat(value, byte_array, offset4, bigEndian);

        result = ByteArrayUnmarshaller.readFloat(byte_array, offset4, bigEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testFloatNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset4, bigEndian, result, offset4, bigEndian), value, result);
        }

        /*
         * Testing non constants. Should all be not expected. Should all be not
         * generated. Parameters are not constants (use Utils.destroyConstant).
         * Although, the optimizer might not pick up simple constant propagation
         * due to optimization scheduling.
         */

        boolean variableEndian;
        int temp = Utils.destroyConstant(1);
        variableEndian = (temp == 1);
        // variableEndian should be a non constant true;

        // should all be not expected

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeFloat(value, byte_array, offset0, variableEndian);

        result = ByteArrayUnmarshaller.readFloat(byte_array, offset0, variableEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testFloatNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, variableEndian, result, offset0, variableEndian), value, result);
        }

        value = Utils.TestValue.LargePositive.FloatValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeFloat(value, byte_array, offset0, variableEndian);

        result = ByteArrayUnmarshaller.readFloat(byte_array, offset0, variableEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testFloatNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, variableEndian, result, offset0, variableEndian), value, result);
        }
    }

    @Test
    public void testFloatExceptions()
    {
        float value = Utils.TestValue.SmallPositive.FloatValue;
        byte[] byte_array = new byte[16];
        final int negativeOffset1 = -1;
        final int offset0 = 0;
        final boolean bigEndian = true;
        float result = 0;

        try
        {
            Arrays.fill(byte_array, (byte) 0x00);
            ByteArrayMarshaller.writeFloat(value, byte_array, negativeOffset1, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testFloatExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, negativeOffset1, bigEndian, result, negativeOffset1, bigEndian));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readFloat(byte_array, negativeOffset1, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testFloatExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, negativeOffset1, bigEndian, result, negativeOffset1, bigEndian));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        final int largeOffset50 = 50;
        value = Utils.TestValue.LargeNegative.FloatValue;

        try
        {
            Arrays.fill(byte_array, (byte) 0x00);
            ByteArrayMarshaller.writeFloat(value, byte_array, largeOffset50, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testFloatExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, largeOffset50, bigEndian, result, largeOffset50, bigEndian));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readFloat(byte_array, largeOffset50, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testFloatExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, largeOffset50, bigEndian, result, largeOffset50, bigEndian));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        byte_array = null;

        try
        {
            ByteArrayMarshaller.writeFloat(value, byte_array, offset0, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testFloatExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian));
        }
        catch (NullPointerException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readFloat(byte_array, offset0, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testFloatExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian));
        }
        catch (NullPointerException e)
        {

        }
    }

    @Test
    public void testDoubleNormals()
    {
        /*
         * Testing all normal values. All should be expected. All should be
         * generated (since they use final constants for parameters)
         */
        double value = Utils.TestValue.SmallPositive.DoubleValue;
        byte[] byte_array = new byte[16];
        final int offset0 = 0;
        final boolean bigEndian = true;
        double result = 0;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeDouble(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readDouble(byte_array, offset0, bigEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testDoubleNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.LargePositive.DoubleValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeDouble(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readDouble(byte_array, offset0, bigEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testDoubleNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.SmallNegative.DoubleValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeDouble(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readDouble(byte_array, offset0, bigEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testDoubleNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.LargeNegative.DoubleValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeDouble(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readDouble(byte_array, offset0, bigEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testDoubleNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.LargestPossible.DoubleValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeDouble(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readDouble(byte_array, offset0, bigEndian);

        assertEqualsDouble(value, result);

        value = Utils.TestValue.SmallestPossible.DoubleValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeDouble(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readDouble(byte_array, offset0, bigEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testDoubleNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        value = Utils.TestValue.Zero.DoubleValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeDouble(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readDouble(byte_array, offset0, bigEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testDoubleNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        /*
         * Testing normal values with Small Endian. Should all be not expected.
         * Should all be not generated.
         */

        final boolean smallEndian = false;

        // should all be not expected

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeDouble(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readDouble(byte_array, offset0, smallEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testDoubleNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.LargePositive.DoubleValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeDouble(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readDouble(byte_array, offset0, smallEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testDoubleNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.SmallNegative.DoubleValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeDouble(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readDouble(byte_array, offset0, smallEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testDoubleNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.LargeNegative.DoubleValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeDouble(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readDouble(byte_array, offset0, smallEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testDoubleNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.LargestPossible.DoubleValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeDouble(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readDouble(byte_array, offset0, smallEndian);

        assertEqualsDouble(value, result);

        value = Utils.TestValue.SmallestPossible.DoubleValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeDouble(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readDouble(byte_array, offset0, smallEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testDoubleNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        value = Utils.TestValue.Zero.DoubleValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeDouble(value, byte_array, offset0, smallEndian);

        result = ByteArrayUnmarshaller.readDouble(byte_array, offset0, smallEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testDoubleNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, smallEndian, result, offset0, smallEndian), value, result);
        }

        /*
         * Testing different offsets. Should all be generated since all offsets
         * are final constant parameters.
         */

        final int offset1 = 1;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeDouble(value, byte_array, offset1, bigEndian);

        result = ByteArrayUnmarshaller.readDouble(byte_array, offset1, bigEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testDoubleNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset1, bigEndian, result, offset1, bigEndian), value, result);
        }

        value = Utils.TestValue.LargePositive.DoubleValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeDouble(value, byte_array, offset0, bigEndian);

        result = ByteArrayUnmarshaller.readDouble(byte_array, offset0, bigEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testDoubleNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }

        final int offset2 = 2;

        value = Utils.TestValue.SmallNegative.DoubleValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeDouble(value, byte_array, offset2, bigEndian);

        result = ByteArrayUnmarshaller.readDouble(byte_array, offset2, bigEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testDoubleNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset2, bigEndian, result, offset2, bigEndian), value, result);
        }

        value = Utils.TestValue.LargeNegative.DoubleValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeDouble(value, byte_array, offset2, bigEndian);

        result = ByteArrayUnmarshaller.readDouble(byte_array, offset2, bigEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testDoubleNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset2, bigEndian, result, offset2, bigEndian), value, result);
        }

        value = Utils.TestValue.LargestPossible.DoubleValue;

        final int offset4 = 4;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeDouble(value, byte_array, offset4, bigEndian);

        result = ByteArrayUnmarshaller.readDouble(byte_array, offset4, bigEndian);

        assertEqualsDouble(value, result);

        value = Utils.TestValue.SmallestPossible.DoubleValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeDouble(value, byte_array, offset4, bigEndian);

        result = ByteArrayUnmarshaller.readDouble(byte_array, offset4, bigEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testDoubleNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset4, bigEndian, result, offset4, bigEndian), value, result);
        }

        value = Utils.TestValue.Zero.DoubleValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeDouble(value, byte_array, offset4, bigEndian);

        result = ByteArrayUnmarshaller.readDouble(byte_array, offset4, bigEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testDoubleNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset4, bigEndian, result, offset4, bigEndian), value, result);
        }

        /*
         * Testing non constants. Should all be not expected. Should all be not
         * generated. Parameters are not constants (use Utils.destroyConstant).
         * Although, the optimizer might not pick up simple constant propagation
         * due to optimization scheduling.
         */

        boolean variableEndian;
        int temp = Utils.destroyConstant(1);
        variableEndian = (temp == 1);
        // variableEndian should be a non constant true;

        // should all be not expected

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeDouble(value, byte_array, offset0, variableEndian);

        result = ByteArrayUnmarshaller.readDouble(byte_array, offset0, variableEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testDoubleNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, variableEndian, result, offset0, variableEndian), value, result);
        }

        value = Utils.TestValue.LargePositive.DoubleValue;

        Arrays.fill(byte_array, (byte) 0x00);
        ByteArrayMarshaller.writeDouble(value, byte_array, offset0, variableEndian);

        result = ByteArrayUnmarshaller.readDouble(byte_array, offset0, variableEndian);

        try
        {
            assertEqualsDouble(value, result);
        }

        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testDoubleNormals #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, variableEndian, result, offset0, variableEndian), value, result);
        }
    }

    @Test
    public void testDoubleExceptions()
    {
        double value = Utils.TestValue.SmallPositive.DoubleValue;
        byte[] byte_array = new byte[16];
        final int negativeOffset1 = -1;
        final int offset0 = 0;
        final boolean bigEndian = true;
        double result = 0;

        try
        {
            Arrays.fill(byte_array, (byte) 0x00);
            ByteArrayMarshaller.writeDouble(value, byte_array, negativeOffset1, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testDoubleExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, negativeOffset1, bigEndian, result, negativeOffset1, bigEndian));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readDouble(byte_array, negativeOffset1, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testDoubleExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, negativeOffset1, bigEndian, result, negativeOffset1, bigEndian));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        final int largeOffset50 = 50;
        value = Utils.TestValue.LargeNegative.DoubleValue;

        try
        {
            Arrays.fill(byte_array, (byte) 0x00);
            ByteArrayMarshaller.writeDouble(value, byte_array, largeOffset50, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testDoubleExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, largeOffset50, bigEndian, result, largeOffset50, bigEndian));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readDouble(byte_array, largeOffset50, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testDoubleExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, largeOffset50, bigEndian, result, largeOffset50, bigEndian));
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        byte_array = null;

        try
        {
            ByteArrayMarshaller.writeDouble(value, byte_array, offset0, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testDoubleExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian));
        }
        catch (NullPointerException e)
        {

        }

        try
        {
            result = ByteArrayUnmarshaller.readDouble(byte_array, offset0, bigEndian);

            fail(Utils.makeTestNameBinaryConvert("", "testDoubleExceptions #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value, byte_array, offset0, bigEndian, result, offset0, bigEndian));
        }
        catch (NullPointerException e)
        {

        }
    }

    private void assertEqualsDouble(double x, double y)
    {
        long xLong = Double.doubleToLongBits(x);
        long yLong = Double.doubleToLongBits(y);

        assertEquals(xLong, yLong);
    }

    private void assertEqualsDouble(String n, double x, double y)
    {
        long xLong = Double.doubleToLongBits(x);
        long yLong = Double.doubleToLongBits(y);

        assertEquals(n + " expected(as double):<" + x + "> result(as double):<" + y + "> ", xLong, yLong);
    }

    @SuppressWarnings("unused")
    private void assertEqualsFloat(String n, float x, float y)
    {
        assertEqualsDouble(n, (double) x, (double) y);
    }
}
