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

package net.openj9.test.arithmetics;

import static org.junit.Assert.*;

import com.ibm.dataaccess.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

public class TestPDComparisons
{
    static String outputFile;

    static Random randomGen;

    static int ARRAY_SIZE = 64;

    byte[] pdArray1 = new byte[ARRAY_SIZE];
    byte[] pdArray2 = new byte[ARRAY_SIZE];

    static int randomSeed = (int) (System.currentTimeMillis() % 160001);

    static
    {
        randomGen = new Random(randomSeed);
    };

    static
    {
        outputFile = "expected." + TestArithmeticOperations.class.getSimpleName() + ".txt";
    };

    static int getRandomInt()
    {
        return randomGen.nextInt(Integer.MAX_VALUE);
    }

    static int getRandomInt(int limit)
    {
        return randomGen.nextInt(limit);
    }

    static long getRandomLong()
    {
        return (long) (randomGen.nextDouble() * 9223372036854775807L);
    }

    static long getRandomLong(int limit)
    {
        return (long) (randomGen.nextDouble() * limit);
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
    public void testEquals()
    {
        for (int i = 0; i < 1000; i++)
            testEqualValues();
    }

    private void testEqualValues()
    {
        Arrays.fill(pdArray1, (byte) 0);
        Arrays.fill(pdArray2, (byte) 0);

        int value = getRandomInt();

        int length = String.valueOf(value).length();

        int extraLength1 = getRandomInt(5);
        int extraLength2 = getRandomInt(7);

        int offset1 = getRandomInt(20);
        int offset2 = getRandomInt(20);

        DecimalData.convertIntegerToPackedDecimal(value, pdArray1, offset1, length + extraLength1, false);
        DecimalData.convertIntegerToPackedDecimal(value, pdArray2, offset2, length + extraLength2, false);

        boolean isEqual = PackedDecimal.equalsPackedDecimal(pdArray1, offset1, length + extraLength1, pdArray2, offset2, length + extraLength2);

        try
        {
            assertEquals(true, isEqual);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, extraLen1: %d, extraLen2: %d, offset1: %d, offset2: %d", value, extraLength1, extraLength2, offset1, offset2), true, isEqual);
        }
    }

    @Test
    public void testNotEquals()
    {
        for (int i = 0; i < 1000; i++)
            testNotEqualValues();
    }

    private void testNotEqualValues()
    {
        int value = getRandomInt();
        int value2 = getRandomInt();

        if (value == value2)
            value2--;

        int length = String.valueOf(value).length();
        int length2 = String.valueOf(value2).length();

        int extraLength1 = getRandomInt(5);
        int extraLength2 = getRandomInt(7);

        int offset1 = getRandomInt(20);
        int offset2 = getRandomInt(20);

        DecimalData.convertIntegerToPackedDecimal(value, pdArray1, offset1, length + extraLength1, false);
        DecimalData.convertIntegerToPackedDecimal(value2, pdArray2, offset2, length2 + extraLength2, false);

        boolean isEqual = PackedDecimal.equalsPackedDecimal(pdArray1, offset1, length + extraLength1, pdArray2, offset2, length2 + extraLength2);

        try
        {
            assertEquals(false, isEqual);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, extraLen1: %d, extraLen2: %d, offset1: %d, offset2: %d", value, extraLength1, extraLength2, offset1, offset2), false, isEqual);
        }
    }

    @Test
    public void testEqualLongerNumbs()
    {
        for (int i = 0; i < 1000; i++)
            testEqualLongNumber();
    }

    private void testEqualLongNumber()
    {
        Arrays.fill(pdArray1, (byte) 0);
        Arrays.fill(pdArray2, (byte) 0);

        long value = getRandomLong(1000000);
        long valueLonger = value + 1000000000;

        int length = String.valueOf(value).length();
        int length2 = String.valueOf(valueLonger).length();

        int extraLength1 = getRandomInt(5);
        int extraLength2 = getRandomInt(7);

        int offset1 = getRandomInt(20);
        int offset2 = getRandomInt(20);

        DecimalData.convertLongToPackedDecimal(value, pdArray1, offset1, length + extraLength1, false);
        DecimalData.convertLongToPackedDecimal(valueLonger, pdArray2, offset2, length2 + extraLength2, false);

        boolean isEqual = PackedDecimal.equalsPackedDecimal(pdArray1, offset1, length + extraLength1, pdArray2, offset2, length2 + extraLength2);

        try
        {
            assertEquals(false, isEqual);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, extraLen1: %d, extraLen2: %d, offset1: %d, offset2: %d", value, extraLength1, extraLength2, offset1, offset2), false, isEqual);
        }

    }

    @Test
    public void testEqualNegativeCompPositive()
    {
        int value = getRandomInt();
        int value2 = value * (-1);

        int length = String.valueOf(value).length();

        DecimalData.convertLongToPackedDecimal(value, pdArray1, 0, length, false);
        DecimalData.convertLongToPackedDecimal(value2, pdArray2, 0, length, false);

        boolean isEqual = PackedDecimal.equalsPackedDecimal(pdArray1, 0, length, pdArray2, 0, length);

        try
        {
            assertEquals(false, isEqual);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d", value), false, isEqual);
        }

    }

    @Test
    public void testEqualTwoNegatives()
    {
        int value = getRandomInt();
        int value2 = value;

        int length = String.valueOf(value).length();

        value = -value;
        value2 = -value2;

        DecimalData.convertLongToPackedDecimal(value, pdArray1, 0, length, false);
        DecimalData.convertLongToPackedDecimal(value2, pdArray2, 0, length, false);

        boolean isEqual = PackedDecimal.equalsPackedDecimal(pdArray1, 0, length, pdArray2, 0, length);

        try
        {
            assertEquals(true, isEqual);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d", value), true, isEqual);
        }
    }

    @Test
    public void testEqualPrecOne()
    {
        int value = getRandomInt(9);
        int value2 = value;

        int length = String.valueOf(value).length();

        DecimalData.convertLongToPackedDecimal(value, pdArray1, 0, length, false);
        DecimalData.convertLongToPackedDecimal(value2, pdArray2, 0, length, false);

        boolean isEqual = PackedDecimal.equalsPackedDecimal(pdArray1, 0, length, pdArray2, 0, length);

        try
        {
            assertEquals(true, isEqual);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d", value), true, isEqual);
        }

        value = getRandomInt(9);
        value2 = getRandomInt();

        int length2 = String.valueOf(value2).length();

        DecimalData.convertLongToPackedDecimal(value, pdArray1, 0, 1, false);
        DecimalData.convertLongToPackedDecimal(value2, pdArray2, 0, length2, false);

        isEqual = PackedDecimal.equalsPackedDecimal(pdArray1, 0, 1, pdArray2, 0, length2);

        try
        {
            assertEquals(false, isEqual);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, value2: %d ", value, value2), false, isEqual);
        }

        value = getRandomInt(9);
        value2 = 100000 + value;

        length2 = String.valueOf(value2).length();

        DecimalData.convertLongToPackedDecimal(value, pdArray1, 0, 1, false);
        DecimalData.convertLongToPackedDecimal(value2, pdArray2, 0, length2, false);

        isEqual = PackedDecimal.equalsPackedDecimal(pdArray1, 0, 1, pdArray2, 0, length2);

        try
        {
            assertEquals(false, isEqual);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, value2: %d ", value, value2), false, isEqual);
        }
    }

    @Test
    public void testEqualLowestDigitDiff()
    {
        int value = getRandomInt();
        int value2;

        if (value % 10 != 0)
            value2 = value - 1;
        else
            value2 = value + 1;

        int length = String.valueOf(value).length();

        DecimalData.convertLongToPackedDecimal(value, pdArray1, 0, length, false);
        DecimalData.convertLongToPackedDecimal(value2, pdArray2, 0, length, false);

        boolean isEqual = PackedDecimal.equalsPackedDecimal(pdArray1, 0, length, pdArray2, 0, length);

        try
        {
            assertEquals(false, isEqual);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d", value), false, isEqual);
        }

    }

    @Test
    public void testEqualLongNumberConcat()
    {
        long value = getRandomLong(1000000);
        long valueLonger = value + 1000000000;

        int length = String.valueOf(value).length();
        int length2 = String.valueOf(valueLonger).length();

        int extraLength1 = getRandomInt(5);
        int extraLength2 = getRandomInt(7);

        int offset1 = getRandomInt(20);
        int offset2 = getRandomInt(20);

        DecimalData.convertLongToPackedDecimal(value, pdArray1, offset1, length + extraLength1, false);
        DecimalData.convertLongToPackedDecimal(valueLonger, pdArray2, offset2, length2 + extraLength2, false);

        boolean isEqual = PackedDecimal.equalsPackedDecimal(pdArray1, offset1, length + extraLength1, pdArray2, offset2, length2 + extraLength2);

        try
        {
            assertEquals(false, isEqual);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, extraLen1: %d, extraLen2: %d, offset1: %d, offset2: %d", value, extraLength1, extraLength2, offset1, offset2), false, isEqual);
        }
    }

    @Test
    public void testZeroDiffSigns()
    {
        pdArray1[0] = 0x00;
        pdArray1[1] = 0x0D;
        pdArray2[0] = 0x00;
        pdArray2[1] = 0x0C;

        int length = 3, length2 = 3;

        boolean isEqual = PackedDecimal.equalsPackedDecimal(pdArray1, 0, length, pdArray2, 0, length2);

        try
        {
            assertEquals(true, isEqual);
        }

        catch (AssertionError e)
        {
            assertEquals("not equal for prec1=3, prec2=3", true, isEqual);
        }

        boolean isLessThan = PackedDecimal.lessThanPackedDecimal(pdArray1, 0, length, pdArray2, 0, length2);

        try
        {
            assertEquals(false, isLessThan);
        }

        catch (AssertionError e)
        {
            assertEquals("lessThan for prec1=3, prec2=3", false, isLessThan);
        }

        boolean isGreaterThan = PackedDecimal.greaterThanPackedDecimal(pdArray1, 0, length, pdArray2, 0, length2);

        try
        {
            assertEquals(false, isGreaterThan);
        }

        catch (AssertionError e)
        {
            assertEquals("greaterThan for prec1=3, prec2=3", false, isGreaterThan);
        }

        isLessThan = PackedDecimal.lessThanPackedDecimal(pdArray2, 0, length, pdArray1, 0, length2);

        try
        {
            assertEquals(false, isLessThan);
        }

        catch (AssertionError e)
        {
            assertEquals("lessThan for prec1=3, prec2=3", false, isLessThan);
        }

        isGreaterThan = PackedDecimal.greaterThanPackedDecimal(pdArray2, 0, length, pdArray1, 0, length2);

        try
        {
            assertEquals(false, isGreaterThan);
        }

        catch (AssertionError e)
        {
            assertEquals("greaterThan for prec1=3, prec2=3", false, isGreaterThan);
        }

        pdArray1[0] = 0x00;
        pdArray1[1] = 0x00;
        pdArray1[2] = 0x0D;
        pdArray2[0] = 0x00;
        pdArray2[1] = 0x00;
        pdArray2[2] = 0x0C;

        length = 2;
        length2 = 3;

        isEqual = PackedDecimal.equalsPackedDecimal(pdArray1, 1, length, pdArray2, 1, length2);

        try
        {
            assertEquals(true, isEqual);
        }

        catch (AssertionError e)
        {
            assertEquals("not equal for prec1=2, prec2=3", true, isEqual);
        }

        length = 2;
        length2 = 4;

        isEqual = PackedDecimal.equalsPackedDecimal(pdArray1, 1, length, pdArray2, 0, length2);

        try
        {
            assertEquals(true, isEqual);
        }

        catch (AssertionError e)
        {
            assertEquals("not equal for prec1=2, prec2=4", true, isEqual);
        }

        length = 2;
        length2 = 5;

        isEqual = PackedDecimal.equalsPackedDecimal(pdArray1, 1, length, pdArray2, 0, length2);

        try
        {
            assertEquals(true, isEqual);
        }

        catch (AssertionError e)
        {
            assertEquals("not equal for prec1=2, prec2=5", true, isEqual);
        }

        length = 3;
        length2 = 4;

        isEqual = PackedDecimal.equalsPackedDecimal(pdArray1, 1, length, pdArray2, 0, length2);

        try
        {
            assertEquals(true, isEqual);
        }

        catch (AssertionError e)
        {
            assertEquals("not equal for prec1=3, prec2=4", true, isEqual);
        }

        length = 3;
        length2 = 5;

        isEqual = PackedDecimal.equalsPackedDecimal(pdArray1, 1, length, pdArray2, 0, length2);

        try
        {
            assertEquals(true, isEqual);
        }

        catch (AssertionError e)
        {
            assertEquals("not equal for prec1=3, prec2=5", true, isEqual);
        }

        length = 4;
        length2 = 2;

        isEqual = PackedDecimal.equalsPackedDecimal(pdArray1, 0, length, pdArray2, 1, length2);

        try
        {
            assertEquals(true, isEqual);
        }

        catch (AssertionError e)
        {
            assertEquals("not equal for prec1=4, prec2=2", true, isEqual);
        }

        length = 5;
        length2 = 2;

        isEqual = PackedDecimal.equalsPackedDecimal(pdArray1, 0, length, pdArray2, 1, length2);

        try
        {
            assertEquals(true, isEqual);
        }

        catch (AssertionError e)
        {
            assertEquals("not equal for prec1=5, prec2=2", true, isEqual);
        }

        length = 4;
        length2 = 4;

        isEqual = PackedDecimal.equalsPackedDecimal(pdArray1, 0, length, pdArray2, 0, length2);

        try
        {
            assertEquals(true, isEqual);
        }

        catch (AssertionError e)
        {
            assertEquals("not equal for prec1=4, prec2=4", true, isEqual);
        }

        length = 5;
        length2 = 5;

        isEqual = PackedDecimal.equalsPackedDecimal(pdArray1, 0, length, pdArray2, 0, length2);

        try
        {
            assertEquals(true, isEqual);
        }

        catch (AssertionError e)
        {
            assertEquals("not equal for prec1=5, prec2=5", true, isEqual);
        }

        length = 5;
        length2 = 3;

        isEqual = PackedDecimal.equalsPackedDecimal(pdArray1, 0, length, pdArray2, 1, length2);

        try
        {
            assertEquals(true, isEqual);
        }

        catch (AssertionError e)
        {
            assertEquals("not equal for prec1=5, prec2=3", true, isEqual);
        }

        length = 5;
        length2 = 4;

        isEqual = PackedDecimal.equalsPackedDecimal(pdArray1, 0, length, pdArray2, 0, length2);

        try
        {
            assertEquals(true, isEqual);
        }

        catch (AssertionError e)
        {
            assertEquals("not equal for prec1=5, prec2=4", true, isEqual);
        }
    }

    @Test
    public void testEqualPrec1()
    {
        Arrays.fill(pdArray1, (byte) 0);
        Arrays.fill(pdArray2, (byte) 0);

        pdArray1[0] = (byte) 0x3C;
        pdArray2[0] = (byte) 0x11;
        pdArray2[1] = (byte) 0x3C;

        boolean isEqual = PackedDecimal.equalsPackedDecimal(pdArray1, 0, 1, pdArray2, 0, 3);

        try
        {
            assertEquals(false, isEqual);
        }

        catch (AssertionError e)
        {
            assertEquals("Equal for value1 = 3, value2 = 113", false, isEqual);
        }
    }

    @Test
    public void testEqualHighNibble()
    {
        Arrays.fill(pdArray1, (byte) 0);
        Arrays.fill(pdArray2, (byte) 0);

        pdArray1[0] = (byte) 0x11;
        pdArray1[1] = (byte) 0x2C;
        pdArray2[0] = (byte) 0x01;
        pdArray2[1] = (byte) 0x2C;

        boolean isEqual = PackedDecimal.equalsPackedDecimal(pdArray1, 0, 3, pdArray2, 0, 2);

        try
        {
            assertEquals(false, isEqual);
        }

        catch (AssertionError e)
        {
            assertEquals("Equal for value1 = 112, value2 = 12", false, isEqual);
        }
    }

    @Test
    public void testLeadZeroGreater()
    {
        for (int i = 0; i < 1000; i++)
            testLeadZerosGreaterThan();
    }

    private void testLeadZerosGreaterThan()
    {
        int value = getRandomInt();
        int value2 = getRandomInt();

        boolean shouldbeGreater = true;

        if (value <= value2)
            shouldbeGreater = false;

        Arrays.fill(pdArray1, (byte) 0);
        Arrays.fill(pdArray2, (byte) 0);

        int length = String.valueOf(value).length();
        int length2 = String.valueOf(value2).length();

        int extraLength1 = getRandomInt(5);
        int extraLength2 = getRandomInt(7);

        DecimalData.convertLongToPackedDecimal(value, pdArray1, 0, length + extraLength1, false);
        DecimalData.convertLongToPackedDecimal(value2, pdArray2, 0, length2 + extraLength2, false);

        boolean isGreater = PackedDecimal.greaterThanPackedDecimal(pdArray1, 0, length + extraLength1, pdArray2, 0, length2 + extraLength2);

        try
        {
            assertEquals(shouldbeGreater, isGreater);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, value2: %d", value, value2), shouldbeGreater, isGreater);
        }

    }

    @Test
    public void testGreaterNegatives()
    {
        for (int i = 0; i < 1000; i++)
            testGreaterNegative2Negative();
    }

    private void testGreaterNegative2Negative()
    {
        Arrays.fill(pdArray1, (byte) 0);
        Arrays.fill(pdArray2, (byte) 0);

        int value = getRandomInt();
        int value2 = getRandomInt();

        int length = String.valueOf(value).length();
        int length2 = String.valueOf(value2).length();

        value *= -1;
        value2 *= -1;

        boolean shouldbeGreater = true;

        if (value <= value2)
            shouldbeGreater = false;

        DecimalData.convertLongToPackedDecimal(value, pdArray1, 0, length, false);
        DecimalData.convertLongToPackedDecimal(value2, pdArray2, 0, length2, false);

        boolean isGreater = PackedDecimal.greaterThanPackedDecimal(pdArray1, 0, length, pdArray2, 0, length2);

        try
        {
            assertEquals(shouldbeGreater, isGreater);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, value2: %d", value, value2), shouldbeGreater, isGreater);
        }
    }

    @Test
    public void testGreaterNegatives2Positives()
    {
        for (int i = 0; i < 1000; i++)
            testGreaterPositive2Negative();
    }

    private void testGreaterPositive2Negative()
    {
        Arrays.fill(pdArray1, (byte) 0);
        Arrays.fill(pdArray2, (byte) 0);

        int value = getRandomInt();
        int value2 = getRandomInt();

        int length = String.valueOf(value).length();
        int length2 = String.valueOf(value2).length();

        if (randomGen.nextBoolean())
            value *= -1;
        else
            value2 *= -1;

        boolean shouldbeGreater = true;

        if (value <= value2)
            shouldbeGreater = false;

        DecimalData.convertLongToPackedDecimal(value, pdArray1, 0, length, false);
        DecimalData.convertLongToPackedDecimal(value2, pdArray2, 0, length2, false);

        boolean isGreater = PackedDecimal.greaterThanPackedDecimal(pdArray1, 0, length, pdArray2, 0, length2);

        try
        {
            assertEquals(shouldbeGreater, isGreater);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, value2: %d", value, value2), shouldbeGreater, isGreater);
        }
    }

    @Test
    public void testGreater()
    {
        for (int i = 0; i < 1000; i++)
            testGreaterThan();
    }

    private void testGreaterThan()
    {
        Arrays.fill(pdArray1, (byte) 0);
        Arrays.fill(pdArray2, (byte) 0);

        int value = getRandomInt();
        int value2 = getRandomInt();

        boolean shouldbeGreater = true;

        if (value <= value2)
            shouldbeGreater = false;

        int length = String.valueOf(value).length();
        int length2 = String.valueOf(value2).length();

        DecimalData.convertLongToPackedDecimal(value, pdArray1, 0, length, false);
        DecimalData.convertLongToPackedDecimal(value2, pdArray2, 0, length2, false);

        boolean isGreater = PackedDecimal.greaterThanPackedDecimal(pdArray1, 0, length, pdArray2, 0, length2);

        try
        {
            assertEquals(shouldbeGreater, isGreater);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, value2: %d", value, value2), shouldbeGreater, isGreater);
        }

    }

    @Test
    public void testGreaterThanSmallValues()
    {
        for (int i = 0; i < 1000; i++)
            testGreaterSmallValues();
    }

    private void testGreaterSmallValues()
    {
        Arrays.fill(pdArray1, (byte) 0);
        Arrays.fill(pdArray2, (byte) 0);

        int value = getRandomInt(9);
        int value2 = getRandomInt(9);

        if (randomGen.nextBoolean())
        {
            value *= -1;
            value2 *= -1;
        }

        boolean shouldbeGreater = true;

        if (value <= value2)
            shouldbeGreater = false;

        int length = String.valueOf(value).length();
        int length2 = String.valueOf(value2).length();

        DecimalData.convertLongToPackedDecimal(value, pdArray1, 0, length, false);
        DecimalData.convertLongToPackedDecimal(value2, pdArray2, 0, length2, false);

        boolean isGreater = PackedDecimal.greaterThanPackedDecimal(pdArray1, 0, length, pdArray2, 0, length2);

        try
        {
            assertEquals(shouldbeGreater, isGreater);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, value2: %d", value, value2), shouldbeGreater, isGreater);
        }
    }

    @Test
    public void testGreaterThanOneBigger()
    {
        for (int i = 0; i < 1000; i++)
            testGreaterOneBigger();
    }

    private void testGreaterOneBigger()
    {
        Arrays.fill(pdArray1, (byte) 0);
        Arrays.fill(pdArray2, (byte) 0);

        int value = getRandomInt() * 10 + getRandomInt(9);
        int value2 = value + 1;

        boolean shouldbeGreater = true;

        if (value <= value2)
            shouldbeGreater = false;

        int length = String.valueOf(value).length();
        int length2 = String.valueOf(value2).length();

        DecimalData.convertLongToPackedDecimal(value, pdArray1, 0, length, false);
        DecimalData.convertLongToPackedDecimal(value2, pdArray2, 0, length2, false);

        boolean isGreater = PackedDecimal.greaterThanPackedDecimal(pdArray1, 0, length, pdArray2, 0, length2);

        try
        {
            assertEquals(shouldbeGreater, isGreater);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, value2: %d", value, value2), shouldbeGreater, isGreater);
        }
    }

    @Test
    public void testGreaterThanOneLesser()
    {
        for (int i = 0; i < 1000; i++)
            testGreaterOneLess();
    }

    private void testGreaterOneLess()
    {
        Arrays.fill(pdArray1, (byte) 0);
        Arrays.fill(pdArray2, (byte) 0);

        int value = getRandomInt() * 10 + getRandomInt(9);
        int value2 = value - 1;

        boolean shouldbeGreater = true;

        if (value <= value2)
            shouldbeGreater = false;

        int length = String.valueOf(value).length();
        int length2 = String.valueOf(value2).length();

        DecimalData.convertLongToPackedDecimal(value, pdArray1, 0, length, false);
        DecimalData.convertLongToPackedDecimal(value2, pdArray2, 0, length2, false);

        boolean isGreater = PackedDecimal.greaterThanPackedDecimal(pdArray1, 0, length, pdArray2, 0, length2);

        try
        {
            assertEquals(shouldbeGreater, isGreater);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, value2: %d", value, value2), shouldbeGreater, isGreater);
        }
    }

    @Test
    public void testGreaterWhenEqual()
    {
        Arrays.fill(pdArray1, (byte) 0);
        Arrays.fill(pdArray2, (byte) 0);

        int value = getRandomInt();
        int value2 = value;

        boolean shouldbeGreater = false;

        int length = String.valueOf(value).length();

        DecimalData.convertIntegerToPackedDecimal(value, pdArray1, 0, length, false);
        DecimalData.convertIntegerToPackedDecimal(value2, pdArray2, 0, length, false);

        boolean isGreater = PackedDecimal.greaterThanPackedDecimal(pdArray1, 0, length, pdArray2, 0, length);

        try
        {
            assertEquals(shouldbeGreater, isGreater);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, value2: %d", value, value2), shouldbeGreater, isGreater);
        }

    }

    @Test
    public void testLessThan()
    {
        for (int i = 0; i < 1000; i++)
            testLess();
    }

    private void testLess()
    {
        Arrays.fill(pdArray1, (byte) 0);
        Arrays.fill(pdArray2, (byte) 0);

        int value = getRandomInt();
        int value2 = getRandomInt();

        boolean shouldbeLess = true;

        if (value >= value2)
            shouldbeLess = false;

        int length = String.valueOf(value).length();
        int length2 = String.valueOf(value2).length();

        DecimalData.convertLongToPackedDecimal(value, pdArray1, 0, length, false);
        DecimalData.convertLongToPackedDecimal(value2, pdArray2, 0, length2, false);

        boolean isLess = PackedDecimal.lessThanPackedDecimal(pdArray1, 0, length, pdArray2, 0, length2);

        try
        {
            assertEquals(shouldbeLess, isLess);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, value2: %d", value, value2), shouldbeLess, isLess);
        }

    }

    @Test
    public void testLeadZeroLessThan()
    {
        for (int i = 0; i < 1000; i++)
            testLeadZerosLessThan();
    }

    private void testLeadZerosLessThan()
    {
        Arrays.fill(pdArray1, (byte) 0);
        Arrays.fill(pdArray2, (byte) 0);

        int value = getRandomInt();
        int value2 = getRandomInt();

        boolean shouldbeLess = true;

        if (value >= value2)
            shouldbeLess = false;

        int length = String.valueOf(value).length();
        int length2 = String.valueOf(value2).length();

        int extraLength1 = getRandomInt(5);
        int extraLength2 = getRandomInt(7);

        DecimalData.convertLongToPackedDecimal(value, pdArray1, 0, length + extraLength1, false);
        DecimalData.convertLongToPackedDecimal(value2, pdArray2, 0, length2 + extraLength2, false);

        boolean isLess = PackedDecimal.lessThanPackedDecimal(pdArray1, 0, length + extraLength1, pdArray2, 0, length2 + extraLength2);

        try
        {
            assertEquals(shouldbeLess, isLess);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, value2: %d", value, value2), shouldbeLess, isLess);
        }
    }

    @Test
    public void testLessNegatives2Positives()
    {
        for (int i = 0; i < 1000; i++)
            testLessPositive2Negative();
    }

    private void testLessPositive2Negative()
    {
        Arrays.fill(pdArray1, (byte) 0);
        Arrays.fill(pdArray2, (byte) 0);

        int value = getRandomInt();
        int value2 = getRandomInt();

        int length = String.valueOf(value).length();
        int length2 = String.valueOf(value2).length();

        if (randomGen.nextBoolean())
            value *= -1;
        else
            value2 *= -1;

        boolean shouldbeLess = true;

        if (value >= value2)
            shouldbeLess = false;

        DecimalData.convertLongToPackedDecimal(value, pdArray1, 0, length, false);
        DecimalData.convertLongToPackedDecimal(value2, pdArray2, 0, length2, false);

        boolean isLess = PackedDecimal.lessThanPackedDecimal(pdArray1, 0, length, pdArray2, 0, length2);

        try
        {
            assertEquals(shouldbeLess, isLess);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, value2: %d", value, value2), shouldbeLess, isLess);
        }
    }

    @Test
    public void testLessNegatives()
    {
        for (int i = 0; i < 1000; i++)
            testLessNegative2Negative();
    }

    private void testLessNegative2Negative()
    {
        Arrays.fill(pdArray1, (byte) 0);
        Arrays.fill(pdArray2, (byte) 0);

        int value = getRandomInt();
        int value2 = getRandomInt();

        int length = String.valueOf(value).length();
        int length2 = String.valueOf(value2).length();

        value *= -1;
        value2 *= -1;

        boolean shouldbeLess = true;

        if (value >= value2)
            shouldbeLess = false;

        DecimalData.convertLongToPackedDecimal(value, pdArray1, 0, length, false);
        DecimalData.convertLongToPackedDecimal(value2, pdArray2, 0, length2, false);

        boolean isLess = PackedDecimal.lessThanPackedDecimal(pdArray1, 0, length, pdArray2, 0, length2);

        try
        {
            assertEquals(shouldbeLess, isLess);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, value2: %d", value, value2), shouldbeLess, isLess);
        }
    }

    public void testLessThanSmallValues()
    {
        for (int i = 0; i < 1000; i++)
            testLessSmallValues();
    }

    private void testLessSmallValues()
    {
        Arrays.fill(pdArray1, (byte) 0);
        Arrays.fill(pdArray2, (byte) 0);

        int value = getRandomInt(9);
        int value2 = getRandomInt(9);

        if (randomGen.nextBoolean())
        {
            value *= -1;
            value2 *= -1;
        }

        boolean shouldbeLess = true;

        if (value >= value2)
            shouldbeLess = false;

        int length = String.valueOf(value).length();
        int length2 = String.valueOf(value2).length();

        DecimalData.convertLongToPackedDecimal(value, pdArray1, 0, length, false);
        DecimalData.convertLongToPackedDecimal(value2, pdArray2, 0, length2, false);

        boolean isLess = PackedDecimal.lessThanPackedDecimal(pdArray1, 0, length, pdArray2, 0, length2);

        try
        {
            assertEquals(shouldbeLess, isLess);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, value2: %d", value, value2), shouldbeLess, isLess);
        }
    }

    @Test
    public void testLessThanOneBigger()
    {
        for (int i = 0; i < 1000; i++)
            testLessOneBigger();
    }

    private void testLessOneBigger()
    {
        Arrays.fill(pdArray1, (byte) 0);
        Arrays.fill(pdArray2, (byte) 0);

        int value = getRandomInt() * 10 + getRandomInt(9);
        int value2 = value + 1;

        boolean shouldbeLess = true;

        if (value >= value2)
            shouldbeLess = false;

        int length = String.valueOf(value).length();
        int length2 = String.valueOf(value2).length();

        DecimalData.convertLongToPackedDecimal(value, pdArray1, 0, length, false);
        DecimalData.convertLongToPackedDecimal(value2, pdArray2, 0, length2, false);

        boolean isLess = PackedDecimal.lessThanPackedDecimal(pdArray1, 0, length, pdArray2, 0, length2);

        try
        {
            assertEquals(shouldbeLess, isLess);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, value2: %d", value, value2), shouldbeLess, isLess);
        }
    }

    @Test
    public void testLessThanOneSmaller()
    {
        for (int i = 0; i < 1000; i++)
            testLessOneSmaller();
    }

    private void testLessOneSmaller()
    {
        Arrays.fill(pdArray1, (byte) 0);
        Arrays.fill(pdArray2, (byte) 0);

        int value = getRandomInt() * 10 + getRandomInt(9);
        int value2 = value - 1;

        boolean shouldbeLess = true;

        if (value >= value2)
            shouldbeLess = false;

        int length = String.valueOf(value).length();
        int length2 = String.valueOf(value2).length();

        DecimalData.convertLongToPackedDecimal(value, pdArray1, 0, length, false);
        DecimalData.convertLongToPackedDecimal(value2, pdArray2, 0, length2, false);

        boolean isLess = PackedDecimal.lessThanPackedDecimal(pdArray1, 0, length, pdArray2, 0, length2);

        try
        {
            assertEquals(shouldbeLess, isLess);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, value2: %d", value, value2), shouldbeLess, isLess);
        }
    }

    @Test
    public void testLessWhenEqual()
    {
        Arrays.fill(pdArray1, (byte) 0);
        Arrays.fill(pdArray2, (byte) 0);

        int value = getRandomInt();
        int value2 = value;

        boolean shouldbeLess = false;

        int length = String.valueOf(value).length();

        DecimalData.convertIntegerToPackedDecimal(value, pdArray1, 0, length, false);
        DecimalData.convertIntegerToPackedDecimal(value2, pdArray2, 0, length, false);

        boolean isLess = PackedDecimal.lessThanPackedDecimal(pdArray1, 0, length, pdArray2, 0, length);

        try
        {
            assertEquals(shouldbeLess, isLess);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, value2: %d", value, value2), shouldbeLess, isLess);
        }
    }
}
