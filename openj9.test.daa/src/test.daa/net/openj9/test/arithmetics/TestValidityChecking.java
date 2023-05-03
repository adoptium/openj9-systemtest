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

package net.openj9.test.arithmetics;

import static org.junit.Assert.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import java.util.Arrays;

import com.ibm.dataaccess.DecimalData;
import com.ibm.dataaccess.PackedDecimal;

import org.junit.Test;
import org.junit.Assert;

public class TestValidityChecking extends TestArithmeticComparisonBase
{
    private byte[] pd = new byte[100];

    // there are several things we need to check:
    // 1. first 4 bits
    // 2. sign
    // 3. digits (1 and several)

    public void testFirst4Bits()
    {
        for (int i = 0; i < 5; ++i)
        {
            fillRandomPD(pd, i, 2);

            Assert.assertEquals(2, PackedDecimal.checkPackedDecimal(pd, i, 2, true, true));

            verifyFirst4Bits(pd, i);

            // add noise to the first 4 bits
            setNoiseAtFirst4Bits(pd, i, 2);
        }
    }

    @Test
    public void testCheckingPerformance()
    {
        // initialization
        fillRandomPD(pd, 0, 15);

        // test execution
        int rv = PackedDecimal.checkPackedDecimal(pd, 0, 15, true, true);

        // verification
        Assert.assertEquals(0, rv);
    }

    @Test
    public void testNormalCases()
    {
        for (int offset = 0; offset < 5; ++offset)
            for (int precision = 1; precision < 10; ++precision)
            {
                Arrays.fill(pd, (byte) 0);
                fillRandomPD(pd, offset, precision);
                Assert.assertEquals(0, PackedDecimal.checkPackedDecimal(pd, offset, precision, true, true));
                Assert.assertEquals(0, PackedDecimal.checkPackedDecimal(pd, offset, precision, true, false));
                Assert.assertEquals(0, PackedDecimal.checkPackedDecimal(pd, offset, precision, false, false));
                Assert.assertEquals(0, PackedDecimal.checkPackedDecimal(pd, offset, precision, false, true));
            }

        for (int precision = 10; precision < 50; ++precision)
        {
            Arrays.fill(pd, (byte) 0);
            fillRandomPD(pd, 0, precision);
            Assert.assertEquals(0, PackedDecimal.checkPackedDecimal(pd, 0, precision, true, true));
            Assert.assertEquals(0, PackedDecimal.checkPackedDecimal(pd, 0, precision, true, false));
            Assert.assertEquals(0, PackedDecimal.checkPackedDecimal(pd, 0, precision, false, false));
            Assert.assertEquals(0, PackedDecimal.checkPackedDecimal(pd, 0, precision, false, true));
        }
    }

    public void rv1Scenario()
    {
        for (int i = 0; i < 5; ++i)// offset
        {
            for (int precision = 1; precision < 10; ++precision)
            {

            }
        }
    }

    @Test
    public void illInputTest()
    {
        boolean catched = false;
        // ill precision
        try
        {
            PackedDecimal.checkPackedDecimal(null, 0, 0);
        }
        catch (IllegalArgumentException iae)
        {
            catched = true;
        }
        catch (NullPointerException e)
        {
            catched = true;
        }
        finally
        {
            if (catched != true)
            {
                fail("Ill-precision Exception not catched.");
            }
        }

        // ill offset
        try
        {
            catched = false;
            PackedDecimal.checkPackedDecimal(op1, -1, 1);
        }
        catch (IndexOutOfBoundsException e)
        {
            catched = true;
        }
        finally
        {
            if (catched != true)
                fail("Exception not catched.");
        }
    }

    @Test
    public void testOptions()
    {
        // first option
        // 1. when it is turned off, it can still pass when the first few bits
        // are tampered.
        // 2. when it is turned on, and the second operand
        for (int i = 0; i < 5; ++i) // offset
        {
            for (int j = 2; j < 10; ++j) // precision
            {
                Arrays.fill(pd, (byte) 0);
                fillRandomPD(pd, i, j);
                setNoiseAtFirst4Bits(pd, i, j);
                Assert.assertEquals(0, PackedDecimal.checkPackedDecimal(pd, i, j, false, true));
                Assert.assertEquals(0, PackedDecimal.checkPackedDecimal(pd, i, j, true, true));
                Assert.assertEquals(0, PackedDecimal.checkPackedDecimal(pd, i, j, false, false));
                Assert.assertEquals(0, PackedDecimal.checkPackedDecimal(pd, i, j, true, false));
            }
        }

        // second option
        for (int i = 0; i < 5; ++i)
        {
            for (int j = 1; j < 10; ++j)
            {
                Arrays.fill(pd, (byte) 0);
                fillRandomPD(pd, i, j);
                setNoiseAtFirst4Bits(pd, i, j);
                Assert.assertEquals(0, PackedDecimal.checkPackedDecimal(pd, i, j, false, true));
                Assert.assertEquals(0, PackedDecimal.checkPackedDecimal(pd, i, j, true, true));
                Assert.assertEquals(0, PackedDecimal.checkPackedDecimal(pd, i, j, false, false));
                Assert.assertEquals(0, PackedDecimal.checkPackedDecimal(pd, i, j, true, false));
            }
        }
    }

    @Test
    public void testSign()
    {
        // sign is not correct
        for (int i = 0; i < 5; ++i)
            // offset
            for (int j = 1; j < 10; ++j) // precision
            {
                Arrays.fill(pd, (byte) 0);
                fillRandomPD(pd, i, j);
                addSignNoise(pd, i, j);
                Assert.assertEquals(1, PackedDecimal.checkPackedDecimal(pd, i, j, true, true));
                Assert.assertEquals(1, PackedDecimal.checkPackedDecimal(pd, i, j, true, false));
                Assert.assertEquals(1, PackedDecimal.checkPackedDecimal(pd, i, j, false, true));
                Assert.assertEquals(1, PackedDecimal.checkPackedDecimal(pd, i, j, false, false));
            }

        // sign is not correct and at least one digit is not correct
        for (int i = 0; i < 5; ++i)
            // offset
            for (int j = 2; j < 5; ++j)
                // precision
                for (int k = 1; k < j; ++k) // num of wrong digits
                {
                    Arrays.fill(pd, (byte) 0);
                    getWrongFormatPDHelper2(pd, i, j, k, true, true);
                    Assert.assertEquals(3, PackedDecimal.checkPackedDecimal(pd, i, j, true, true));

                    Arrays.fill(pd, (byte) 0);
                    getWrongFormatPDHelper2(pd, i, j, k, true, true);
                    Assert.assertEquals(3, PackedDecimal.checkPackedDecimal(pd, i, j, true, false));

                    Arrays.fill(pd, (byte) 0);
                    getWrongFormatPDHelper2(pd, i, j, k, true, true);
                    Assert.assertEquals(3, PackedDecimal.checkPackedDecimal(pd, i, j, false, true));

                    Arrays.fill(pd, (byte) 0);
                    getWrongFormatPDHelper2(pd, i, j, k, true, true);
                    Assert.assertEquals(3, PackedDecimal.checkPackedDecimal(pd, i, j, false, false));
                }
    }

    @Test
    public void testDigits()
    {
        // sign is correct, digits are not correct
        for (int i = 0; i < 5; ++i)
            // offset
            for (int j = 1; j < 5; ++j)
                // precision
                for (int k = 1; k < j; ++k) // num of wrong digits
                {
                    Arrays.fill(pd, (byte) 0);
                    getWrongFormatPDHelper2(pd, i, j, k, true, false);
                    Assert.assertEquals(2, PackedDecimal.checkPackedDecimal(pd, i, j, true, false));
                    Assert.assertEquals(2, PackedDecimal.checkPackedDecimal(pd, i, j, true, false));
                    Assert.assertEquals(2, PackedDecimal.checkPackedDecimal(pd, i, j, true, false));
                    Assert.assertEquals(2, PackedDecimal.checkPackedDecimal(pd, i, j, true, false));
                }
    }

    public static void verifyDigits(byte[] pd, int offset, int precision)
    {
        boolean high4Bits = true;
        if (precision % 2 == 0)
        {
            high4Bits = false;
        }

        for (int i = offset; i < offset + precision / 2 + 1;)
        {
            if (high4Bits)
            {
                int comp = pd[i] & 0xF0;
                comp >>= 4;
                Assert.assertTrue(comp >= 0 && comp <= 9);
                high4Bits = !high4Bits;
            } else
            // !high4Bits
            {
                int comp = pd[i] & 0x0F;
                i++;
                Assert.assertTrue(comp >= 0 && comp <= 9);
                high4Bits = !high4Bits;
            }
        }
    }

    public static void verifySign(byte[] pd, int offset, int precision)
    {
        int position = offset + precision / 2;
        int comp = pd[position] & 0x0F;
        Assert.assertTrue(comp >= 10 && comp <= 15);
    }

    public static void verifyFirst4Bits(byte[] pd, int offset)
    {
        // caution: only works when precision % 2 == 0
        int comp = (pd[offset] & 0xF0) >> 4;
        Assert.assertTrue(comp == 0x00);
    }

    @Test
    public void testCheckPackedDecimal4Parameters()
    {
        byte[] pd = new byte[10];
        DecimalData.convertIntegerToPackedDecimal(999, pd, 0, 3, false);
        Assert.assertEquals(0, PackedDecimal.checkPackedDecimal(pd, 0, 3, true));

        DecimalData.convertIntegerToPackedDecimal(999, pd, 0, 3, false);
        Assert.assertEquals(0, PackedDecimal.checkPackedDecimal(pd, 0, 3, false));
    }

    @Test
    public void testCheckPackedDecimal3Parameters()
    {
        byte[] pd = new byte[10];
        DecimalData.convertIntegerToPackedDecimal(999, pd, 0, 3, false);
        Assert.assertEquals(0, PackedDecimal.checkPackedDecimal(pd, 0, 3));

    }

    @Test
    public void testCheckPackedDecimalDifferentSignValues()
    {
        byte[] pd = new byte[10];

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x1A;
        int result = PackedDecimal.checkPackedDecimal(pd, 0, 1, false, true);
        Assert.assertEquals("Test CheckPackedDecimal for 1A with overwrite set as true", 0, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x1B;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 1, false, true);
        Assert.assertEquals("Test CheckPackedDecimal for 1B with overwrite set as true", 0, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x1C;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 1, false, true);
        Assert.assertEquals("Test CheckPackedDecimal for 1C with overwrite set as true", 0, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x1D;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 1, false, true);
        Assert.assertEquals("Test CheckPackedDecimal for 1D with overwrite set as true", 0, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x1E;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 1, false, true);
        Assert.assertEquals("Test CheckPackedDecimal for 1E with overwrite set as true", 0, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x1F;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 1, false, true);
        Assert.assertEquals("Test CheckPackedDecimal for 1F with overwrite set as true", 0, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x1A;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 1, true, true);
        Assert.assertEquals("Test CheckPackedDecimal for 1A with overwrite set as true and ignoreEvenNibble set as true", 0, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x1B;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 1, true, true);
        Assert.assertEquals("Test CheckPackedDecimal for 1B with overwrite set as true and ignoreEvenNibble set as true", 0, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x1C;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 1, true, true);
        Assert.assertEquals("Test CheckPackedDecimal for 1C with overwrite set as true and ignoreEvenNibble set as true", 0, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x1D;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 1, true, true);
        Assert.assertEquals("Test CheckPackedDecimal for 1D with overwrite set as true and ignoreEvenNibble set as true", 0, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x1E;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 1, true, true);
        Assert.assertEquals("Test CheckPackedDecimal for 1E with overwrite set as true and ignoreEvenNibble set as true", 0, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x1F;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 1, true, true);
        Assert.assertEquals("Test CheckPackedDecimal for 1F with overwrite set as true and ignoreEvenNibble set as true", 0, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x1A;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 1, true, false);
        Assert.assertEquals("Test CheckPackedDecimal for 1A with ignoreEvenNibble set as true", 0, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x1B;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 1, true, false);
        Assert.assertEquals("Test CheckPackedDecimal for 1B with ignoreEvenNibble set as true", 0, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x1C;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 1, true, false);
        Assert.assertEquals("Test CheckPackedDecimal for 1C with ignoreEvenNibble set as true", 0, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x1D;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 1, true, false);
        Assert.assertEquals("Test CheckPackedDecimal for 1D with ignoreEvenNibble set as true", 0, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x1E;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 1, true, false);
        Assert.assertEquals("Test CheckPackedDecimal for 1E with ignoreEvenNibble set as true", 0, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x1F;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 1, true, false);
        Assert.assertEquals("Test CheckPackedDecimal for 1F with ignoreEvenNibble set as true", 0, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x1A;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 1, false, false);
        Assert.assertEquals("Test CheckPackedDecimal for 1A", 0, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x1B;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 1, false, false);
        Assert.assertEquals("Test CheckPackedDecimal for 1B", 0, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x1C;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 1, false, false);
        Assert.assertEquals("Test CheckPackedDecimal for 1C", 0, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x1D;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 1, false, false);
        Assert.assertEquals("Test CheckPackedDecimal for 1D", 0, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x1E;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 1, false, false);
        Assert.assertEquals("Test CheckPackedDecimal for 1E", 0, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x1F;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 1, false, false);
        Assert.assertEquals("Test CheckPackedDecimal for 1F", 0, result);

    }

    @Test
    public void testInvalidDigits()
    {
        byte[] pd = new byte[10];

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x2A;
        pd[1] = 0x1C;
        int result = PackedDecimal.checkPackedDecimal(pd, 0, 3, false, false);
        Assert.assertEquals("Test CheckPackedDecimal for improper digit in the middle", 2, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x21;
        pd[1] = (byte) 0xAC;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 3, false, false);
        Assert.assertEquals("Test CheckPackedDecimal for improper digit in lowest nibble", 2, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = (byte) 0xAA;
        pd[1] = (byte) 0xAC;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 3, false, false);
        Assert.assertEquals("Test CheckPackedDecimal for improper digit in all digits", 2, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = (byte) 0xA1;
        pd[1] = (byte) 0x2D;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 3, false, false);
        Assert.assertEquals("Test CheckPackedDecimal for improper digit highest nibble", 2, result);
    }

    @Test
    public void testInvalidSignDigits()
    {
        byte[] pd = new byte[10];

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x21;
        pd[1] = (byte) 0x11;
        int result = PackedDecimal.checkPackedDecimal(pd, 0, 3, false, false);
        Assert.assertEquals("Test CheckPackedDecimal for improper sign digit", 1, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = (byte) 0x99;
        pd[1] = (byte) 0x90;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 3, false, false);
        Assert.assertEquals("Test CheckPackedDecimal for for 0 sign digit", 1, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = (byte) 0x11;
        pd[1] = (byte) 0x29;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 3, false, false);
        Assert.assertEquals("Test CheckPackedDecimal for 9 sign digit", 1, result);

    }

    @Test
    public void testInvalidDigitsAndSignDigit()
    {
        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x21;
        pd[1] = 0x11;
        int result = PackedDecimal.checkPackedDecimal(pd, 0, 4, false, false);
        Assert.assertEquals("Test CheckPackedDecimal for no space for sign digit", 1, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x21;
        pd[1] = 0x11;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 2, false, false);
        Assert.assertEquals("Test CheckPackedDecimal for invalid sign digit and invalid digit outside of precision", 1, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = 0x2C;
        pd[1] = 0x11;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 3, false, false);
        Assert.assertEquals("Test CheckPackedDecimal for invalid sign digit and invalid digit in the middle", 3, result);

        Arrays.fill(pd, (byte) 0);
        pd[0] = (byte) 0xA0;
        pd[1] = 0x00;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 3, false, false);
        Assert.assertEquals("Test CheckPackedDecimal for invalid sign digit and invalid digit in the highest nibble", 3, result);
    }

    @Test
    public void testIgnoreEvenNibble()
    {
        byte[] pd = new byte[10];
        byte[] resultArray = new byte[10];

        Arrays.fill(pd, (byte) 0);
        pd[0] = (byte) 0xA1;
        pd[1] = (byte) 0x2D;
        resultArray[0] = (byte) 0xA1;
        resultArray[1] = (byte) 0x2D;

        int result = PackedDecimal.checkPackedDecimal(pd, 0, 2, true, false);
        Assert.assertEquals("Test CheckPackedDecimal for top nibble out of precision, set ignore nibble as true", 0, result);
        assertArrayEquals("Test CheckPackedDecimal for top nibble out of precision, set ignore nibble as true", resultArray, pd);

        Arrays.fill(pd, (byte) 0);
        pd[0] = (byte) 0xA1;
        pd[1] = (byte) 0x21;
        resultArray[0] = (byte) 0xA1;
        resultArray[1] = (byte) 0x21;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 2, true, false);
        Assert.assertEquals("Test CheckPackedDecimal for top nibble out of precision, set ignore nibble as true, sign invalid", 1, result);
        assertArrayEquals("Test CheckPackedDecimal for top nibble out of precision, set ignore nibble as true, sign invalid", resultArray, pd);

        Arrays.fill(pd, (byte) 0);
        pd[0] = (byte) 0xAA;
        pd[1] = (byte) 0x2C;
        resultArray[0] = (byte) 0xAA;
        resultArray[1] = (byte) 0x2C;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 2, true, false);
        Assert.assertEquals("Test CheckPackedDecimal for top nibble out of precision, set ignore nibble as true, digits invalid", 2, result);
        assertArrayEquals("Test CheckPackedDecimal for top nibble out of precision, set ignore nibble as true, digits invalid", resultArray, pd);

        Arrays.fill(pd, (byte) 0);
        pd[0] = (byte) 0xAA;
        pd[1] = (byte) 0x21;
        resultArray[0] = (byte) 0xAA;
        resultArray[1] = (byte) 0x21;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 2, true, false);
        Assert.assertEquals("Test CheckPackedDecimal for top nibble out of precision, set ignore nibble as true, digits and sign invalid", 3, result);
        assertArrayEquals("Test CheckPackedDecimal for top nibble out of precision, set ignore nibble as true, digits and sign invalid", resultArray, pd);

    }

    @Test
    public void testOverwriteEvenNibble()
    {

        byte[] pd = new byte[10];
        byte[] resultArray = new byte[10];

        Arrays.fill(pd, (byte) 0);
        pd[0] = (byte) 0xA1;
        pd[1] = (byte) 0x2D;
        resultArray[0] = (byte) 0x01;
        resultArray[1] = (byte) 0x2D;

        int result = PackedDecimal.checkPackedDecimal(pd, 0, 2, false, true);
        Assert.assertEquals("Test CheckPackedDecimal for top nibble out of precision, set overwrite as true", 0, result);
        assertArrayEquals("Test CheckPackedDecimal for top nibble out of precision, set overwrite as true", resultArray, pd);

        Arrays.fill(pd, (byte) 0);
        pd[0] = (byte) 0xA1;
        pd[1] = (byte) 0x21;
        resultArray[0] = (byte) 0x01;
        resultArray[1] = (byte) 0x21;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 2, false, true);
        Assert.assertEquals("Test CheckPackedDecimal for top nibble out of precision, set overwrite as true, sign invalid", 1, result);
        assertArrayEquals("Test CheckPackedDecimal for top nibble out of precision, set overwrite as true, sign invalid", resultArray, pd);

        Arrays.fill(pd, (byte) 0);
        pd[0] = (byte) 0xAA;
        pd[1] = (byte) 0x2C;
        resultArray[0] = (byte) 0x0A;
        resultArray[1] = (byte) 0x2C;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 2, false, true);
        Assert.assertEquals("Test CheckPackedDecimal for top nibble out of precision, set overwrite as true, digit invalid", 2, result);
        assertArrayEquals("Test CheckPackedDecimal for top nibble out of precision, set overwrite as true, digit invalid", resultArray, pd);

        Arrays.fill(pd, (byte) 0);
        pd[0] = (byte) 0xAA;
        pd[1] = (byte) 0x21;
        resultArray[0] = (byte) 0x0A;
        resultArray[1] = (byte) 0x21;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 2, false, true);
        Assert.assertEquals("Test CheckPackedDecimal for top nibble out of precision, set overwrite as true, digit and sign invalid", 3, result);
        assertArrayEquals("Test CheckPackedDecimal for top nibble out of precision, set overwrite as true, digit and sign invalid", resultArray, pd);

        Arrays.fill(pd, (byte) 0);
        pd[0] = (byte) 0x12;
        pd[1] = (byte) 0x2C;
        resultArray[0] = (byte) 0x12;
        resultArray[1] = (byte) 0x2C;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 3, false, true);
        Assert.assertEquals("Test CheckPackedDecimal for top nibble out of precision, set overwrite as true, digit and sign invalid", 0, result);
        assertArrayEquals("Test CheckPackedDecimal for everything valid overwrite set as true", resultArray, pd);

        Arrays.fill(pd, (byte) 0);
        pd[0] = (byte) 0x12;
        pd[1] = (byte) 0xA1;
        pd[2] = (byte) 0x21;

        resultArray[0] = (byte) 0x02;
        resultArray[1] = (byte) 0xA1;
        resultArray[2] = (byte) 0x21;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 4, false, true);
        Assert.assertEquals("Test CheckPackedDecimal for top nibble out of precision, set overwrite as true, digit and sign invalid", 3, result);
        assertArrayEquals("Test CheckPackedDecimal for everything valid overwrite set as true", resultArray, pd);

    }

    @Test
    public void testOverwriteEvenNibbleAndtestIgnoreEvenNibble()
    {

        byte[] pd = new byte[10];
        byte[] resultArray = new byte[10];

        Arrays.fill(pd, (byte) 0);
        pd[0] = (byte) 0xA1;
        pd[1] = (byte) 0x2D;
        resultArray[0] = (byte) 0x01;
        resultArray[1] = (byte) 0x2D;

        int result = PackedDecimal.checkPackedDecimal(pd, 0, 2, true, true);
        Assert.assertEquals("Test CheckPackedDecimal for top nibble out of precision, set overwrite and ignorehighnible as true", 0, result);
        assertArrayEquals("Test CheckPackedDecimal for top nibble out of precision, set overwrite and ignorehighnible as true", resultArray, pd);

        Arrays.fill(pd, (byte) 0);
        pd[0] = (byte) 0xA1;
        pd[1] = (byte) 0x21;
        resultArray[0] = (byte) 0x01;
        resultArray[1] = (byte) 0x21;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 2, true, true);
        Assert.assertEquals("Test CheckPackedDecimal for top nibble out of precision, set overwrite and ignorehighnible as true, sign invalid", 1, result);
        assertArrayEquals("Test CheckPackedDecimal for top nibble out of precision, set overwrite and ignorehighnible as true, sign invalid", resultArray, pd);

        Arrays.fill(pd, (byte) 0);
        pd[0] = (byte) 0xAA;
        pd[1] = (byte) 0x2C;
        resultArray[0] = (byte) 0x0A;
        resultArray[1] = (byte) 0x2C;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 2, true, true);
        Assert.assertEquals("Test CheckPackedDecimal for top nibble out of precision, set overwrite and ignorehighnible as true, digit invalid", 2, result);
        assertArrayEquals("Test CheckPackedDecimal for top nibble out of precision, set overwrite and ignorehighnible as true, digit invalid", resultArray, pd);

        Arrays.fill(pd, (byte) 0);
        pd[0] = (byte) 0xAA;
        pd[1] = (byte) 0x21;
        resultArray[0] = (byte) 0x0A;
        resultArray[1] = (byte) 0x21;
        result = PackedDecimal.checkPackedDecimal(pd, 0, 2, true, true);
        Assert.assertEquals("Test CheckPackedDecimal for top nibble out of precision, set overwrite and ignorehighnible as true, sign and digit invalid", 3, result);
        assertArrayEquals("Test CheckPackedDecimal for top nibble out of precision, set overwrite and ignorehighnible as true, sign and digit invalid", resultArray, pd);

    }

    @Test
    public void testCheckPackedWithOffset()
    {
        byte[] pd = new byte[10];
        byte[] resultArray = new byte[10];

        Arrays.fill(pd, (byte) 0);
        pd[0] = (byte) 0x11;
        pd[1] = 0x12;
        pd[2] = 0x3D;

        resultArray[0] = (byte) 0x11;
        resultArray[1] = 0x02;
        resultArray[2] = 0x3D;

        int result = PackedDecimal.checkPackedDecimal(pd, 1, 2, true, true);
        Assert.assertEquals("Test CheckPackedDecimal with offset, set overwrite and ignorehighnibble as true", 0, result);
        assertArrayEquals("Test CheckPackedDecimal with offset, set overwrite and ignorehighnibble as true", resultArray, pd);

        Arrays.fill(pd, (byte) 0);
        pd[0] = (byte) 0x11;
        pd[1] = 0x12;
        pd[2] = 0x3D;

        resultArray[0] = (byte) 0x11;
        resultArray[1] = 0x12;
        resultArray[2] = 0x3D;

        result = PackedDecimal.checkPackedDecimal(pd, 1, 2, true, false);
        Assert.assertEquals("Test CheckPackedDecimal with offset, set ignorehighnibble as true", 0, result);
        assertArrayEquals("Test CheckPackedDecimal with offset, set ignorehighnibble as true", resultArray, pd);

        Arrays.fill(pd, (byte) 0);
        pd[0] = (byte) 0x11;
        pd[1] = 0x12;
        pd[2] = 0x3D;

        resultArray[0] = (byte) 0x11;
        resultArray[1] = 0x02;
        resultArray[2] = 0x3D;

        result = PackedDecimal.checkPackedDecimal(pd, 1, 2, false, true);
        Assert.assertEquals("Test CheckPackedDecimal with offset, set overwrite as true", 0, result);
        assertArrayEquals("Test CheckPackedDecimal with offset, set overwrite as true", resultArray, pd);

        Arrays.fill(pd, (byte) 0);
        pd[0] = (byte) 0x11;
        pd[1] = 0x12;
        pd[2] = 0x3D;

        resultArray[0] = (byte) 0x11;
        resultArray[1] = 0x12;
        resultArray[2] = 0x3D;

        result = PackedDecimal.checkPackedDecimal(pd, 1, 2, false, false);
        Assert.assertEquals("Test CheckPackedDecimal with offset", 0, result);
        assertArrayEquals("Test CheckPackedDecimal with offset", resultArray, pd);

    }

    @Test
    public void testEmptyArrayForCheckPacked()
    {

        byte[] pd = new byte[10];

        Arrays.fill(pd, (byte) 0);

        int result = PackedDecimal.checkPackedDecimal(pd, 0, 19, false, false);
        Assert.assertEquals("Test CheckPackedDecimal with empty array", 1, result);

    }

    public static void main(String[] args)
    {
        Result result = new Result();
        result = JUnitCore.runClasses(TestValidityChecking.class);
        System.out.println(result.getRunCount());
    }

}
