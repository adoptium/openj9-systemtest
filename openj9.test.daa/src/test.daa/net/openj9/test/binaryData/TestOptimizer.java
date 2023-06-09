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

import org.junit.Test;

import com.ibm.dataaccess.ByteArrayMarshaller;
import com.ibm.dataaccess.ByteArrayUnmarshaller;

import net.openj9.test.Utils;
import net.openj9.test.Utils.TestValue;

public class TestOptimizer
{
    @Test
    public void testLongOptimizers()
    {
        boolean bigEndian = true;

        byte[] byte_array = new byte[10];
        
        int value = 25;
        int offset = 0;
        
        Arrays.fill(byte_array, (byte) 0x00);
        
        ByteArrayMarshaller.writeLong(value, byte_array, offset, bigEndian);
        long result = ByteArrayUnmarshaller.readLong(byte_array, offset, bigEndian);
        
        try 
        {
            assertEquals(value, result);
        }
        
        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLongOptimizers", value, byte_array, offset, bigEndian, result, offset, bigEndian), value, result);
        }
    }

    @Test
    public void testIntOptimizers()
    {
        boolean bigEndian = true;
        
        byte[] byte_array = new byte[10];
        
        int value = 25;
        int offset = 0;
        
        Arrays.fill(byte_array, (byte) 0x00);
        
        ByteArrayMarshaller.writeInt(value, byte_array, offset, bigEndian);
        int result = ByteArrayUnmarshaller.readInt(byte_array, offset, bigEndian);
        
        try 
        {
            assertEquals(value, result);
        }
        
        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testIntOptimizers", value, byte_array, offset, bigEndian, result, offset, bigEndian), value, result);
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

        assertEquals(n + " exptected(as double):<" + x + "> result(as double):<" + y + "> ", xLong, yLong);
    }

    @Test
    public void testDouble()
    {
        boolean bigEndian = true;
        
        byte[] byte_array = new byte[16];
        
        double value = Utils.TestValue.SmallPositive.DoubleValue;
        final int offset0 = 0;

        Arrays.fill(byte_array, (byte) 0x00);
        
        ByteArrayMarshaller.writeDouble(value, byte_array, offset0, bigEndian);
        double result = ByteArrayUnmarshaller.readDouble(byte_array, offset0, bigEndian);
        
        try 
        {
            assertEqualsDouble(value, result);
        }
        
        catch (AssertionError e)
        {
            assertEqualsDouble(Utils.makeTestNameBinaryConvert("", "testDouble", value, byte_array, offset0, bigEndian, result, offset0, bigEndian), value, result);
        }
    }

    @Test
    public void testLengthOptimizers()
    {
        int value;

        byte[] byte_array = new byte[10];
        
        int result;
        int correct;

        int offset0 = 0;
        
        boolean bigEndian = false;
        
        int length1 = 1;
        int length2 = 2;
        int length4 = 4;
        
        value = TestValue.LargePositive.IntValue;
        
        boolean yesSign = true;
        
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
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLengthOptimizers", value, byte_array, offset0, bigEndian, length1, result, offset0, bigEndian, length1, yesSign), correct, result);
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
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLengthOptimizers", value, byte_array, offset0, bigEndian, length1, result, offset0, bigEndian, length1, yesSign), correct, result);
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
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLengthOptimizers", value, byte_array, offset0, bigEndian, length2, result, offset0, bigEndian, length2, yesSign), correct, result);
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
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLengthOptimizers", value, byte_array, offset0, bigEndian, length2, result, offset0, bigEndian, length2, yesSign), correct, result);
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
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLengthOptimizers", value, byte_array, offset0, bigEndian, length4, result, offset0, bigEndian, length4, yesSign), correct, result);
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
            assertEquals(Utils.makeTestNameBinaryConvert("", "testLengthOptimizers", value, byte_array, offset0, bigEndian, length4, result, offset0, bigEndian, length4, yesSign), correct, result);
        }
    }

    @Test
    public void testShortOptimizers()
    {
        boolean bigEndian = true;

        byte[] byte_array = { 0x00, 0x11, 0x22, 0x33, 0x44, 0x55 };
        
        short value = 25;
        int offset = 0;
        
        ByteArrayMarshaller.writeShort(value, byte_array, offset, bigEndian);
        short result = ByteArrayUnmarshaller.readShort(byte_array, offset, bigEndian);
        
        try 
        {
            assertEquals(value, result);
        }
        
        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameBinaryConvert("", "testShortOptimizers", value, byte_array, offset, bigEndian, result, offset, bigEndian), value, result);
        }
    }
}
