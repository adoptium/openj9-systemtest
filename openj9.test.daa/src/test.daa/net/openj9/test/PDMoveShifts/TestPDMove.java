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
package net.openj9.test.PDMoveShifts;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Random;

import com.ibm.dataaccess.DecimalData;
import com.ibm.dataaccess.PackedDecimal;

public class TestPDMove
{
    static int ARRAY_SIZE = 64;
    byte[] original = new byte[ARRAY_SIZE];
    byte[] copy = new byte[ARRAY_SIZE];

    static Random randomGen = new Random((int) (System.currentTimeMillis() % 16001));

    private int getRandomIntegerWithNDigits(int n)
    {
        int randomInt = 0;

        for (int k = 0; k < n; k++)
            randomInt = randomInt | (((randomGen.nextInt() >> k * 4) & 0x0f) << (n - k - 1) * 4);

        return randomInt < 0 ? randomInt * -1 : randomInt;
    }

    private int genRandomOffset()
    {
        int offset = randomGen.nextInt() % (ARRAY_SIZE - 4);
        return offset > 0 ? offset : -offset;
    }

    @Test
    public void testPDMovesimpleMove()
    {
        Arrays.fill(original, (byte) 0x00);
        Arrays.fill(copy, (byte) 0x00);

        int value, length, resultInt;

        value = getRandomIntegerWithNDigits(randomGen.nextInt(8));
        length = String.valueOf(value).length();
        DecimalData.convertIntegerToPackedDecimal(value, original, 0, length, false);
        PackedDecimal.movePackedDecimal(copy, 0, length, original, 0, length, true);
        resultInt = DecimalData.convertPackedDecimalToInteger(copy, 0, length, false);

        try
        {
            assertEquals(value, resultInt);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, length: %d, resultInt: %d\n", value, length, resultInt), value, resultInt);
        }

        Arrays.fill(original, (byte) 0x00);
        Arrays.fill(copy, (byte) 0x00);

        value = getRandomIntegerWithNDigits(randomGen.nextInt(8));
        length = String.valueOf(value).length();
        value = value * (-1);

        DecimalData.convertIntegerToPackedDecimal(value, original, 0, length, false);
        PackedDecimal.movePackedDecimal(copy, 0, length, original, 0, length, true);
        resultInt = DecimalData.convertPackedDecimalToInteger(copy, 0, length, false);

        try
        {
            assertEquals(value, resultInt);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, length: %d, resultInt: %d\n", value, length, resultInt), value, resultInt);
        }
    }

    @Test
    public void testPDMoveWithOffset()
    {
        Arrays.fill(original, (byte) 0x00);
        Arrays.fill(copy, (byte) 0x00);

        int value, length, offset, resultInt, offsetorg, offsetcopy;

        value = getRandomIntegerWithNDigits(randomGen.nextInt(8));
        length = String.valueOf(value).length();
        offset = genRandomOffset();

        DecimalData.convertIntegerToPackedDecimal(value, original, offset, length, false);
        PackedDecimal.movePackedDecimal(copy, 0, length, original, offset, length, true);
        resultInt = DecimalData.convertPackedDecimalToInteger(copy, 0, length, false);

        try
        {
            assertEquals(value, resultInt);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, length: %d, resultInt: %d, offset%d\n", value, length, resultInt, offset), value, resultInt);
        }

        Arrays.fill(original, (byte) 0x00);
        Arrays.fill(copy, (byte) 0x00);

        value = getRandomIntegerWithNDigits(randomGen.nextInt(8));
        length = String.valueOf(value).length();
        value = value * (-1);
        offset = genRandomOffset();

        DecimalData.convertIntegerToPackedDecimal(value, original, 0, length, false);
        PackedDecimal.movePackedDecimal(copy, offset, length, original, 0, length, true);
        resultInt = DecimalData.convertPackedDecimalToInteger(copy, offset, length, false);

        try
        {
            assertEquals(value, resultInt);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, length: %d, resultInt: %d,offset:%d\n", value, length, resultInt, offset), value, resultInt);
        }

        Arrays.fill(original, (byte) 0x00);
        Arrays.fill(copy, (byte) 0x00);

        value = getRandomIntegerWithNDigits(randomGen.nextInt(8));
        length = String.valueOf(value).length();

        offsetorg = genRandomOffset();
        offsetcopy = genRandomOffset();

        DecimalData.convertIntegerToPackedDecimal(value, original, offsetorg, length, false);
        PackedDecimal.movePackedDecimal(copy, offsetcopy, length, original, offsetorg, length, true);
        resultInt = DecimalData.convertPackedDecimalToInteger(copy, offsetcopy, length, false);

        try
        {
            assertEquals(value, resultInt);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, length: %d, resultInt: %d, offset:%d\n", value, length, resultInt, offset), value, resultInt);
        }
    }

    @Test
    public void testPDMoveWithDifferentPrecision()
    {
        Arrays.fill(original, (byte) 0x00);
        Arrays.fill(copy, (byte) 0x00);

        int value, length, copyprec, resultInt, newvalue, orgprec;

        value = getRandomIntegerWithNDigits(randomGen.nextInt(8));
        length = String.valueOf(value).length();
        copyprec = randomGen.nextInt(length);

        if (copyprec == 0)
            copyprec = 1;

        DecimalData.convertIntegerToPackedDecimal(value, original, 0, length, false);
        PackedDecimal.movePackedDecimal(copy, 0, copyprec, original, 0, length, false);
        resultInt = DecimalData.convertPackedDecimalToInteger(copy, 0, copyprec, false);

        newvalue = value % (int) (Math.pow(10, (double) copyprec));

        try
        {
            assertEquals(newvalue, resultInt);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, length: %d, copyprec: %d, resultInt: %d, newvalue: %d \n", value, length, copyprec, resultInt, newvalue), newvalue, resultInt);
        }

        Arrays.fill(original, (byte) 0x00);
        Arrays.fill(copy, (byte) 0x00);

        value = getRandomIntegerWithNDigits(randomGen.nextInt(8));
        length = String.valueOf(value).length();
        orgprec = randomGen.nextInt(length);

        if (orgprec == 0)
            orgprec = 1;

        DecimalData.convertIntegerToPackedDecimal(value, original, 0, orgprec, false);
        PackedDecimal.movePackedDecimal(copy, 0, length, original, 0, orgprec, false);
        resultInt = DecimalData.convertPackedDecimalToInteger(copy, 0, length, false);

        newvalue = value % (int) (Math.pow(10, (double) orgprec));

        try
        {
            assertEquals(newvalue, resultInt);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, length: %d, orgprec: %d, resultInt: %d, newvalue: %d \n", value, length, orgprec, resultInt, newvalue), newvalue, resultInt);
        }
    }

    @Test(expected = Exception.class)
    // Could be either Arithmetic exception or illegal argument exeception
    public void testPDMoveArithmeticException()
    {
        Arrays.fill(original, (byte) 0x00);
        Arrays.fill(copy, (byte) 0x00);

        int value, length, copyprec, resultInt, newvalue;

        value = 0;
        while (value == 0)
            value = getRandomIntegerWithNDigits(randomGen.nextInt(8));

        length = String.valueOf(value).length();
        copyprec = length - 1;

        DecimalData.convertIntegerToPackedDecimal(value, original, 0, length, false);
        PackedDecimal.movePackedDecimal(copy, 0, copyprec, original, 0, length, true);
        resultInt = DecimalData.convertPackedDecimalToInteger(copy, 0, copyprec, false);

        newvalue = value % (int) (Math.pow(10, (double) copyprec));

        try
        {
            assertEquals(newvalue, resultInt);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("value: %d, length: %d, resultInt: %d, newvalue: %d \n", value, length, resultInt, newvalue), newvalue, resultInt);
        }
    }
}
