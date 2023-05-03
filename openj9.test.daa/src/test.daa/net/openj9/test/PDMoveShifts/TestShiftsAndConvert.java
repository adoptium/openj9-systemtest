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

import com.ibm.dataaccess.*;

import net.openj9.test.*;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Random;

public class TestShiftsAndConvert
{
    static int ARRAY_SIZE = 64;

    byte[] original = new byte[ARRAY_SIZE];
    byte[] copy = new byte[ARRAY_SIZE];

    static Random randomGen = new Random((int) (System.currentTimeMillis() % 16001));

    static int getRandomInt()
    {
        return randomGen.nextInt(Integer.MAX_VALUE);
    }

    public void testRepeatedly()
    {

        testShifts();
        testShifts();

        testShiftsExceptions();
        testShiftsExceptions();

        testConvertPackedToExternalNormals();
        testConvertPackedToExternalNormals();

        testConvertPackedToExternalExceptions();
        testConvertPackedToExternalExceptions();
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
    public void testConvertPackedToExternalExceptions()
    {

        /*
         * test precision > byte array size test precision + offset > byte array
         * size test null value (source and destination)
         */

        final int offset0 = 0;
        final int precision31 = 31;
        final boolean errorChecking = false;
        long value = Utils.TestValue.SmallPositive.LongValue;

        final int decimalType1 = DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING;
        final int offset50 = 50;
        final int precision100 = 100;
        value = Utils.TestValue.SmallPositive.LongValue;
        byte[] longPackedDecimal = new byte[64];
        byte[] packedToExternal = new byte[64];
        byte[] externalToPacked = new byte[64];

        try
        {
            DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision100, errorChecking);
            DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision100, decimalType1);
            DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision100, decimalType1);

            fail("Assertion in TestShiftsAndConvert on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        try
        {
            DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset50, precision31, errorChecking);
            DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset50, packedToExternal, offset50, precision31, decimalType1);
            DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset50, externalToPacked, offset50, precision31, decimalType1);
            fail("Assertion in TestShiftsAndConvert on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = Utils.TestValue.SmallPositive.LongValue;
        byte[] packedDecimalNull = null;
        byte[] externalDecimalNull = null;

        try
        {
            DecimalData.convertPackedDecimalToExternalDecimal(packedDecimalNull, offset0, packedToExternal, offset0, precision31, decimalType1);

            fail("Assertion in TestShiftsAndConvert on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }

        try
        {
            DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalDecimalNull, offset0, precision31, decimalType1);
            fail("Assertion in TestShiftsAndConvert on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }
    }

    @Test
    public void testConvertPackedToExternalNormals()
    {

        byte[] longPackedDecimal = new byte[64];
        byte[] packedToExternal = new byte[64];
        byte[] externalToPacked = new byte[64];
        final int offset0 = 0;
        final int precision31 = 31;
        final int offset5 = 5;
        final int precision15 = 15;
        final int precision16 = 16;
        final int offset10 = 10;
        final int precision2 = 2;
        final boolean errorChecking = false;

        final int decimalType1 = DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING;
        final int decimalType2 = DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING;
        final int decimalType3 = DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING;
        final int decimalType4 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
        long value = Utils.TestValue.SmallPositive.LongValue;
        String functionName = "convertPackedDecimal";
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision31, decimalType1);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision31, decimalType1);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision31, packedToExternal, offset0, precision31, decimalType1, externalToPacked, offset0, precision31, decimalType1, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision31, decimalType1);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision31, decimalType1);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision31, packedToExternal, offset0, precision31, decimalType1, externalToPacked, offset0, precision31, decimalType1, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.SmallNegative.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision31, decimalType1);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision31, decimalType1);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision31, packedToExternal, offset0, precision31, decimalType1, externalToPacked, offset0, precision31, decimalType1, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargeNegative.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision31, decimalType1);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision31, decimalType1);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision31, packedToExternal, offset0, precision31, decimalType1, externalToPacked, offset0, precision31, decimalType1, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargestPossible.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision31, decimalType1);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision31, decimalType1);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision31, packedToExternal, offset0, precision31, decimalType1, externalToPacked, offset0, precision31, decimalType1, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.Zero.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision31, decimalType1);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision31, decimalType1);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision31, packedToExternal, offset0, precision31, decimalType1, externalToPacked, offset0, precision31, decimalType1, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset5, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset5, packedToExternal, offset5, precision31, decimalType1);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset5, externalToPacked, offset5, precision31, decimalType1);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset5, precision31, packedToExternal, offset5, precision31, decimalType1, externalToPacked, offset5, precision31, decimalType1, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset10, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset10, packedToExternal, offset10, precision31, decimalType1);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset10, externalToPacked, offset10, precision31, decimalType1);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset10, precision31, packedToExternal, offset10, precision31, decimalType1, externalToPacked, offset10, precision31, decimalType1, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.SmallNegative.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision2, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision2, decimalType1);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision2, decimalType1);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision2, packedToExternal, offset0, precision2, decimalType1, externalToPacked, offset0, precision2, decimalType1, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset5, precision15, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset5, packedToExternal, offset5, precision15, decimalType1);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset5, externalToPacked, offset5, precision15, decimalType1);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset5, precision15, packedToExternal, offset5, precision15, decimalType1, externalToPacked, offset5, precision15, decimalType1, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset10, precision16, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset10, packedToExternal, offset10, precision16, decimalType1);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset10, externalToPacked, offset10, precision16, decimalType1);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset10, precision16, packedToExternal, offset10, precision16, decimalType1, externalToPacked, offset10, precision16, decimalType1, errorChecking), longPackedDecimal, externalToPacked);
        }

        /*
         * testing EBCDIC_SIGN_EMBEDDED_LEADING
         */

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision31, decimalType2);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision31, decimalType2);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision31, packedToExternal, offset0, precision31, decimalType2, externalToPacked, offset0, precision31, decimalType2, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision31, decimalType2);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision31, decimalType2);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision31, packedToExternal, offset0, precision31, decimalType2, externalToPacked, offset0, precision31, decimalType2, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.SmallNegative.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision31, decimalType2);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision31, decimalType2);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision31, packedToExternal, offset0, precision31, decimalType2, externalToPacked, offset0, precision31, decimalType2, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargeNegative.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision31, decimalType2);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision31, decimalType2);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision31, packedToExternal, offset0, precision31, decimalType2, externalToPacked, offset0, precision31, decimalType2, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargestPossible.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision31, decimalType2);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision31, decimalType2);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision31, packedToExternal, offset0, precision31, decimalType2, externalToPacked, offset0, precision31, decimalType2, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.Zero.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision31, decimalType2);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision31, decimalType2);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision31, packedToExternal, offset0, precision31, decimalType2, externalToPacked, offset0, precision31, decimalType2, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset5, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset5, packedToExternal, offset5, precision31, decimalType2);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset5, externalToPacked, offset5, precision31, decimalType2);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset5, precision31, packedToExternal, offset5, precision31, decimalType2, externalToPacked, offset5, precision31, decimalType2, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset10, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset10, packedToExternal, offset10, precision31, decimalType2);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset10, externalToPacked, offset10, precision31, decimalType2);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset10, precision31, packedToExternal, offset10, precision31, decimalType2, externalToPacked, offset10, precision31, decimalType2, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.SmallNegative.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision2, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision2, decimalType2);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision2, decimalType2);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision2, packedToExternal, offset0, precision2, decimalType2, externalToPacked, offset0, precision2, decimalType2, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset5, precision15, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset5, packedToExternal, offset5, precision15, decimalType2);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset5, externalToPacked, offset5, precision15, decimalType2);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset5, precision15, packedToExternal, offset5, precision15, decimalType2, externalToPacked, offset5, precision15, decimalType2, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset10, precision16, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset10, packedToExternal, offset10, precision16, decimalType2);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset10, externalToPacked, offset10, precision16, decimalType2);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset10, precision16, packedToExternal, offset10, precision16, decimalType2, externalToPacked, offset10, precision16, decimalType2, errorChecking), longPackedDecimal, externalToPacked);
        }

        /*
         * testing EBCDIC_SIGN_SEPARATE_TRAILING
         */

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision31, decimalType3);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision31, decimalType3);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision31, packedToExternal, offset0, precision31, decimalType3, externalToPacked, offset0, precision31, decimalType3, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision31, decimalType3);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision31, decimalType3);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision31, packedToExternal, offset0, precision31, decimalType3, externalToPacked, offset0, precision31, decimalType3, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.SmallNegative.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision31, decimalType3);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision31, decimalType3);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision31, packedToExternal, offset0, precision31, decimalType3, externalToPacked, offset0, precision31, decimalType3, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargeNegative.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision31, decimalType3);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision31, decimalType3);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision31, packedToExternal, offset0, precision31, decimalType3, externalToPacked, offset0, precision31, decimalType3, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargestPossible.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision31, decimalType3);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision31, decimalType3);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision31, packedToExternal, offset0, precision31, decimalType3, externalToPacked, offset0, precision31, decimalType3, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.Zero.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision31, decimalType3);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision31, decimalType3);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision31, packedToExternal, offset0, precision31, decimalType3, externalToPacked, offset0, precision31, decimalType3, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset5, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset5, packedToExternal, offset5, precision31, decimalType3);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset5, externalToPacked, offset5, precision31, decimalType3);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset5, precision31, packedToExternal, offset5, precision31, decimalType3, externalToPacked, offset5, precision31, decimalType3, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset10, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset10, packedToExternal, offset10, precision31, decimalType3);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset10, externalToPacked, offset10, precision31, decimalType3);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset10, precision31, packedToExternal, offset10, precision31, decimalType3, externalToPacked, offset10, precision31, decimalType3, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.SmallNegative.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision2, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision2, decimalType3);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision2, decimalType3);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision2, packedToExternal, offset0, precision2, decimalType3, externalToPacked, offset0, precision2, decimalType3, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset5, precision15, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset5, packedToExternal, offset5, precision15, decimalType3);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset5, externalToPacked, offset5, precision15, decimalType3);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset5, precision15, packedToExternal, offset5, precision15, decimalType3, externalToPacked, offset5, precision15, decimalType3, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset10, precision16, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset10, packedToExternal, offset10, precision16, decimalType3);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset10, externalToPacked, offset10, precision16, decimalType3);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset10, precision16, packedToExternal, offset10, precision16, decimalType3, externalToPacked, offset10, precision16, decimalType3, errorChecking), longPackedDecimal, externalToPacked);
        }

        /*
         * testing EBCDIC_SIGN_SEPARATE_LEADING
         */

        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision31, decimalType4);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision31, decimalType4);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision31, packedToExternal, offset0, precision31, decimalType4, externalToPacked, offset0, precision31, decimalType4, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision31, decimalType4);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision31, decimalType4);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision31, packedToExternal, offset0, precision31, decimalType4, externalToPacked, offset0, precision31, decimalType4, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.SmallNegative.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision31, decimalType4);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision31, decimalType4);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision31, packedToExternal, offset0, precision31, decimalType4, externalToPacked, offset0, precision31, decimalType4, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargeNegative.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision31, decimalType4);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision31, decimalType4);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision31, packedToExternal, offset0, precision31, decimalType4, externalToPacked, offset0, precision31, decimalType4, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargestPossible.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision31, decimalType4);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision31, decimalType4);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision31, packedToExternal, offset0, precision31, decimalType4, externalToPacked, offset0, precision31, decimalType4, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.Zero.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision31, decimalType4);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision31, decimalType4);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision31, packedToExternal, offset0, precision31, decimalType4, externalToPacked, offset0, precision31, decimalType4, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset5, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset5, packedToExternal, offset5, precision31, decimalType4);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset5, externalToPacked, offset5, precision31, decimalType4);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset5, precision31, packedToExternal, offset5, precision31, decimalType4, externalToPacked, offset5, precision31, decimalType4, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset10, precision31, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset10, packedToExternal, offset10, precision31, decimalType4);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset10, externalToPacked, offset10, precision31, decimalType4);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset10, precision31, packedToExternal, offset10, precision31, decimalType4, externalToPacked, offset10, precision31, decimalType4, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.SmallNegative.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, precision2, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset0, packedToExternal, offset0, precision2, decimalType4);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset0, externalToPacked, offset0, precision2, decimalType4);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset0, precision2, packedToExternal, offset0, precision2, decimalType4, externalToPacked, offset0, precision2, decimalType4, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset5, precision15, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset5, packedToExternal, offset5, precision15, decimalType4);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset5, externalToPacked, offset5, precision15, decimalType4);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset5, precision15, packedToExternal, offset5, precision15, decimalType4, externalToPacked, offset5, precision15, decimalType4, errorChecking), longPackedDecimal, externalToPacked);
        }

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(longPackedDecimal, (byte) 0x00);
        Arrays.fill(packedToExternal, (byte) 0x00);
        Arrays.fill(externalToPacked, (byte) 0x00);

        DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset10, precision16, errorChecking);
        DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal, offset10, packedToExternal, offset10, precision16, decimalType4);
        DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal, offset10, externalToPacked, offset10, precision16, decimalType4);

        try
        {
            assertArrayEquals(longPackedDecimal, externalToPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameConverterArray("", functionName, value, longPackedDecimal, offset10, precision16, packedToExternal, offset10, precision16, decimalType4, externalToPacked, offset10, precision16, decimalType4, errorChecking), longPackedDecimal, externalToPacked);
        }

        /*
         * testing UNICODE_UNSIGNED
         */

        /*
         * value = Utils.TestValue.SmallPositive.LongValue; dataName =
         * "PackedDecimal to ExternalDecimal"; typeName = "UNICODE_UNSIGNED";
         * testName = dataName + " " + Utils.TestValue.SmallPositive.TestName +
         * " " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset0, precision31, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset0, packedToExternal, offset0, precision31, decimalType4);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset0, externalToPacked, offset0, precision31, decimalType4);
         * 
         * //Utils.changeSignToF(longPackedDecimal, offset0, precision31);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset0, precision31, packedToExternal, offset0,
         * precision31, decimalType5, externalToPacked, offset0, precision31,
         * decimalType5, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.LargePositive.LongValue; testName = dataName
         * + " " + Utils.TestValue.LargePositive.TestName + " " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset0, precision31, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset0, packedToExternal, offset0, precision31, decimalType5);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset0, externalToPacked, offset0, precision31, decimalType5);
         * //Utils.changeSignToF(longPackedDecimal, offset0, precision31);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset0, precision31, packedToExternal, offset0,
         * precision31, decimalType5, externalToPacked, offset0, precision31,
         * decimalType5, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.SmallNegative.LongValue; testName = dataName
         * + " " + Utils.TestValue.SmallNegative.TestName + " " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset0, precision31, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset0, packedToExternal, offset0, precision31, decimalType5);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset0, externalToPacked, offset0, precision31, decimalType5);
         * //Utils.changeSignToF(longPackedDecimal, offset0, precision31);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset0, precision31, packedToExternal, offset0,
         * precision31, decimalType5, externalToPacked, offset0, precision31,
         * decimalType5, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.LargeNegative.LongValue; testName = dataName
         * + " " + Utils.TestValue.LargeNegative.TestName + " " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset0, precision31, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset0, packedToExternal, offset0, precision31, decimalType5);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset0, externalToPacked, offset0, precision31, decimalType5);
         * //Utils.changeSignToF(longPackedDecimal, offset0, precision31);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset0, precision31, packedToExternal, offset0,
         * precision31, decimalType5, externalToPacked, offset0, precision31,
         * decimalType5, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.LargestPossible.LongValue; testName =
         * dataName + " " + Utils.TestValue.LargestPossible.TestName + " " +
         * typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset0, precision31, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset0, packedToExternal, offset0, precision31, decimalType5);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset0, externalToPacked, offset0, precision31, decimalType5);
         * //Utils.changeSignToF(longPackedDecimal, offset0, precision31);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset0, precision31, packedToExternal, offset0,
         * precision31, decimalType5, externalToPacked, offset0, precision31,
         * decimalType5, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.Zero.LongValue; testName = dataName + " " +
         * Utils.TestValue.Zero.TestName + " " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset0, precision31, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset0, packedToExternal, offset0, precision31, decimalType5);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset0, externalToPacked, offset0, precision31, decimalType5);
         * //Utils.changeSignToF(longPackedDecimal, offset0, precision31);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset0, precision31, packedToExternal, offset0,
         * precision31, decimalType5, externalToPacked, offset0, precision31,
         * decimalType5, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.LargePositive.LongValue; testName = dataName
         * + " " + Utils.TestValue.LargePositive.TestName +
         * " Offset and Precision " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset5, precision15, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset5, packedToExternal, offset5, precision15, decimalType5);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset5, externalToPacked, offset5, precision15, decimalType5);
         * //Utils.changeSignToF(longPackedDecimal, offset5, precision15);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset5, precision15, packedToExternal, offset5,
         * precision15, decimalType5, externalToPacked, offset5, precision15,
         * decimalType5, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.LargePositive.LongValue; testName = dataName
         * + " " + Utils.TestValue.LargePositive.TestName +
         * " Offset and Precision " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset10, precision15, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset10, packedToExternal, offset10, precision15, decimalType5);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset10, externalToPacked, offset10, precision15, decimalType5);
         * 
         * //Utils.changeSignToF(longPackedDecimal, offset10, precision15);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset10, precision15, packedToExternal, offset10,
         * precision15, decimalType5, externalToPacked, offset10, precision15,
         * decimalType5, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.SmallNegative.LongValue; testName = dataName
         * + " " + Utils.TestValue.SmallNegative.TestName + " " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset0, precision2, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset0, packedToExternal, offset0, precision2, decimalType5);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset0, externalToPacked, offset0, precision2, decimalType5);
         * 
         * //Utils.changeSignToF(longPackedDecimal, offset0, precision2);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset0, precision2, packedToExternal, offset0,
         * precision2, decimalType5, externalToPacked, offset0, precision2,
         * decimalType5, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.LargePositive.LongValue; testName = dataName
         * + " " + Utils.TestValue.LargePositive.TestName +
         * " Offset and Precision " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset5, precision15, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset5, packedToExternal, offset5, precision15, decimalType5);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset5, externalToPacked, offset5, precision15, decimalType5);
         * //Utils.changeSignToF(longPackedDecimal, offset5, precision15);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset5, precision15, packedToExternal, offset5,
         * precision15, decimalType5, externalToPacked, offset5, precision15,
         * decimalType5, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.LargePositive.LongValue; testName = dataName
         * + " " + Utils.TestValue.LargePositive.TestName +
         * " Offset and Precision " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset10, precision16, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset10, packedToExternal, offset10, precision16, decimalType5);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset10, externalToPacked, offset10, precision16, decimalType5);
         * //Utils.changeSignToF(longPackedDecimal, offset10, precision16);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset10, precision16, packedToExternal, offset10,
         * precision16, decimalType5, externalToPacked, offset10, precision16,
         * decimalType5, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         */

        /*
         * testing UNICODE_SIGN_EMBEDDED_LEADING
         */

        /*
         * value = Utils.TestValue.SmallPositive.LongValue; dataName =
         * "PackedDecimal to ExternalDecimal"; typeName =
         * "UNICODE_SIGN_EMBEDDED_LEADING"; testName = dataName + " " +
         * Utils.TestValue.SmallPositive.TestName + " " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset0, precision31, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset0, packedToExternal, offset0, precision31, decimalType6);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset0, externalToPacked, offset0, precision31, decimalType6);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset0, precision31, packedToExternal, offset0,
         * precision31, decimalType6, externalToPacked, offset0, precision31,
         * decimalType6, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.LargePositive.LongValue; testName = dataName
         * + " " + Utils.TestValue.LargePositive.TestName + " " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset0, precision31, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset0, packedToExternal, offset0, precision31, decimalType6);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset0, externalToPacked, offset0, precision31, decimalType6);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset0, precision31, packedToExternal, offset0,
         * precision31, decimalType6, externalToPacked, offset0, precision31,
         * decimalType6, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.SmallNegative.LongValue; testName = dataName
         * + " " + Utils.TestValue.SmallNegative.TestName + " " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset0, precision31, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset0, packedToExternal, offset0, precision31, decimalType6);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset0, externalToPacked, offset0, precision31, decimalType6);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset0, precision31, packedToExternal, offset0,
         * precision31, decimalType6, externalToPacked, offset0, precision31,
         * decimalType6, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.LargeNegative.LongValue; testName = dataName
         * + " " + Utils.TestValue.LargeNegative.TestName + " " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset0, precision31, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset0, packedToExternal, offset0, precision31, decimalType6);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset0, externalToPacked, offset0, precision31, decimalType6);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset0, precision31, packedToExternal, offset0,
         * precision31, decimalType6, externalToPacked, offset0, precision31,
         * decimalType6, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.LargestPossible.LongValue; testName =
         * dataName + " " + Utils.TestValue.LargestPossible.TestName + " " +
         * typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset0, precision31, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset0, packedToExternal, offset0, precision31, decimalType6);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset0, externalToPacked, offset0, precision31, decimalType6);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset0, precision31, packedToExternal, offset0,
         * precision31, decimalType6, externalToPacked, offset0, precision31,
         * decimalType6, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.Zero.LongValue; testName = dataName + " " +
         * Utils.TestValue.Zero.TestName + " " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset0, precision31, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset0, packedToExternal, offset0, precision31, decimalType6);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset0, externalToPacked, offset0, precision31, decimalType6);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset0, precision31, packedToExternal, offset0,
         * precision31, decimalType6, externalToPacked, offset0, precision31,
         * decimalType6, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.LargePositive.LongValue; testName = dataName
         * + " " + Utils.TestValue.LargePositive.TestName +
         * " Offset and Precision " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00); packedToExternal = new
         * byte[100]; Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset5, precision31, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset5, packedToExternal, offset5, precision31, decimalType6);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset5, externalToPacked, offset5, precision31, decimalType6);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset5, precision31, packedToExternal, offset5,
         * precision31, decimalType6, externalToPacked, offset5, precision31,
         * decimalType6, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.LargePositive.LongValue; testName = dataName
         * + " " + Utils.TestValue.LargePositive.TestName +
         * " Offset and Precision " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00); packedToExternal = new
         * byte[100]; Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset10, precision31, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset10, packedToExternal, offset10, precision31, decimalType6);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset10, externalToPacked, offset10, precision31, decimalType6);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset10, precision31, packedToExternal, offset10,
         * precision31, decimalType6, externalToPacked, offset10, precision31,
         * decimalType6, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.SmallNegative.LongValue; testName = dataName
         * + " " + Utils.TestValue.SmallNegative.TestName + " " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset0, precision2, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset0, packedToExternal, offset0, precision2, decimalType6);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset0, externalToPacked, offset0, precision2, decimalType6);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset0, precision2, packedToExternal, offset0,
         * precision2, decimalType6, externalToPacked, offset0, precision2,
         * decimalType6, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.LargePositive.LongValue; testName = dataName
         * + " " + Utils.TestValue.LargePositive.TestName +
         * " Offset and Precision " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset5, precision15, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset5, packedToExternal, offset5, precision15, decimalType6);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset5, externalToPacked, offset5, precision15, decimalType6);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset5, precision15, packedToExternal, offset5,
         * precision15, decimalType6, externalToPacked, offset5, precision15,
         * decimalType6, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.LargePositive.LongValue; testName = dataName
         * + " " + Utils.TestValue.LargePositive.TestName +
         * " Offset and Precision " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset10, precision16, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset10, packedToExternal, offset10, precision16, decimalType6);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset10, externalToPacked, offset10, precision16, decimalType6);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset10, precision16, packedToExternal, offset10,
         * precision16, decimalType6, externalToPacked, offset10, precision16,
         * decimalType6, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         */

        /*
         * testing UNICODE_SIGN_EMBEDDED_TRAILING
         */

        /*
         * value = Utils.TestValue.SmallPositive.LongValue; dataName =
         * "PackedDecimal to ExternalDecimal"; typeName =
         * "UNICODE_SIGN_EMBEDDED_TRAILING"; testName = dataName + " " +
         * Utils.TestValue.SmallPositive.TestName + " " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset0, precision31, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset0, packedToExternal, offset0, precision31, decimalType7);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset0, externalToPacked, offset0, precision31, decimalType7);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset0, precision31, packedToExternal, offset0,
         * precision31, decimalType7, externalToPacked, offset0, precision31,
         * decimalType7, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.LargePositive.LongValue; testName = dataName
         * + " " + Utils.TestValue.LargePositive.TestName + " " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset0, precision31, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset0, packedToExternal, offset0, precision31, decimalType7);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset0, externalToPacked, offset0, precision31, decimalType7);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset0, precision31, packedToExternal, offset0,
         * precision31, decimalType7, externalToPacked, offset0, precision31,
         * decimalType7, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.SmallNegative.LongValue; testName = dataName
         * + " " + Utils.TestValue.SmallNegative.TestName + " " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset0, precision31, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset0, packedToExternal, offset0, precision31, decimalType7);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset0, externalToPacked, offset0, precision31, decimalType7);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset0, precision31, packedToExternal, offset0,
         * precision31, decimalType7, externalToPacked, offset0, precision31,
         * decimalType7, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.LargeNegative.LongValue; testName = dataName
         * + " " + Utils.TestValue.LargeNegative.TestName + " " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset0, precision31, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset0, packedToExternal, offset0, precision31, decimalType7);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset0, externalToPacked, offset0, precision31, decimalType7);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset0, precision31, packedToExternal, offset0,
         * precision31, decimalType7, externalToPacked, offset0, precision31,
         * decimalType7, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.LargestPossible.LongValue; testName =
         * dataName + " " + Utils.TestValue.LargestPossible.TestName + " " +
         * typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset0, precision31, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset0, packedToExternal, offset0, precision31, decimalType7);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset0, externalToPacked, offset0, precision31, decimalType7);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset0, precision31, packedToExternal, offset0,
         * precision31, decimalType7, externalToPacked, offset0, precision31,
         * decimalType7, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.Zero.LongValue; testName = dataName + " " +
         * Utils.TestValue.Zero.TestName + " " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset0, precision31, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset0, packedToExternal, offset0, precision31, decimalType7);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset0, externalToPacked, offset0, precision31, decimalType7);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset0, precision31, packedToExternal, offset0,
         * precision31, decimalType7, externalToPacked, offset0, precision31,
         * decimalType7, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.LargePositive.LongValue; testName = dataName
         * + " " + Utils.TestValue.LargePositive.TestName +
         * " Offset and Precision " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset5, precision16, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset5, packedToExternal, offset5, precision16, decimalType7);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset5, externalToPacked, offset5, precision16, decimalType7);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset5, precision16, packedToExternal, offset5,
         * precision16, decimalType7, externalToPacked, offset5, precision16,
         * decimalType7, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.LargePositive.LongValue; testName = dataName
         * + " " + Utils.TestValue.LargePositive.TestName +
         * " Offset and Precision " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset10, precision16, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset10, packedToExternal, offset10, precision16, decimalType7);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset10, externalToPacked, offset10, precision16, decimalType7);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset10, precision16, packedToExternal, offset10,
         * precision16, decimalType7, externalToPacked, offset10, precision16,
         * decimalType7, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.SmallNegative.LongValue; testName = dataName
         * + " " + Utils.TestValue.SmallNegative.TestName + " " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset0, precision2, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset0, packedToExternal, offset0, precision2, decimalType7);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset0, externalToPacked, offset0, precision2, decimalType7);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset0, precision2, packedToExternal, offset0,
         * precision2, decimalType7, externalToPacked, offset0, precision2,
         * decimalType7, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.LargePositive.LongValue; testName = dataName
         * + " " + Utils.TestValue.LargePositive.TestName +
         * " Offset and Precision " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset5, precision15, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset5, packedToExternal, offset5, precision15, decimalType7);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset5, externalToPacked, offset5, precision15, decimalType7);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset5, precision15, packedToExternal, offset5,
         * precision15, decimalType7, externalToPacked, offset5, precision15,
         * decimalType7, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         * 
         * 
         * value = Utils.TestValue.LargePositive.LongValue; testName = dataName
         * + " " + Utils.TestValue.LargePositive.TestName +
         * " Offset and Precision " + typeName;
         * 
         * Arrays.fill(longPackedDecimal, (byte) 0x00);
         * Arrays.fill(packedToExternal, (byte) 0x00);
         * Arrays.fill(externalToPacked, (byte) 0x00);
         * 
         * DecimalData.convertLongToPackedDecimal(value, longPackedDecimal,
         * offset10, precision16, errorChecking);
         * DecimalData.convertPackedDecimalToExternalDecimal(longPackedDecimal,
         * offset10, packedToExternal, offset10, precision16, decimalType7);
         * DecimalData.convertExternalDecimalToPackedDecimal(packedToExternal,
         * offset10, externalToPacked, offset10, precision16, decimalType7);
         * Utils.makeTestNameConverterArray("", functionName, value,
         * longPackedDecimal, offset10, precision16, packedToExternal, offset10,
         * precision16, decimalType7, externalToPacked, offset10, precision16,
         * decimalType7, errorChecking); assertArrayEquals(testName,
         * longPackedDecimal, externalToPacked);
         */
    }

    @Test
    public void testLeftEvenShift()
    {
        byte[] pd = new byte[16];
        byte[] sd = new byte[16];
        byte[] answer = new byte[16];

        // Even Shift for 9C
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(9, pd, 0, 3, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 3, pd, 0, 3, 2, false);
        answer[0] = (byte) 0x90;
        answer[1] = (byte) 0x0C;
        assertArrayEquals("00 9C shifted left to 90 0C ", answer, sd);

        // Even Shift for 01 0C
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(10, pd, 0, 5, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 5, pd, 0, 5, 2, false);

        answer[0] = (byte) 0x01;
        answer[1] = (byte) 0x00;
        answer[2] = (byte) 0x0C;
        assertArrayEquals("00 01 0C shifted left to 01 00 0C ", answer, sd);

        // Even Shift for 12 3C
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(123, pd, 0, 5, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 5, pd, 0, 5, 2, false);

        answer[0] = (byte) 0x12;
        answer[1] = (byte) 0x30;
        answer[2] = (byte) 0x0C;
        assertArrayEquals("00 12 3C shifted left to 12 30 0C", answer, sd);

    }

    @Test
    public void testLeftOddShift()
    {
        byte[] pd = new byte[16];
        byte[] sd = new byte[16];
        byte[] answer = new byte[16];

        // 0dd Shift for 9C
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(9, pd, 0, 5, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 5, pd, 0, 5, 1, false);

        answer[0] = (byte) 0x00;
        answer[1] = (byte) 0x09;
        answer[2] = (byte) 0x0C;
        assertArrayEquals("00 00 9C shifted to 00 09 0C", answer, sd);

        // Odd Shift for 12 3C
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(123, pd, 0, 5, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 5, pd, 0, 5, 1, false);

        answer[0] = (byte) 0x01;
        answer[1] = (byte) 0x23;
        answer[2] = (byte) 0x0C;
        assertArrayEquals("00 12 3C shifted by 1 to 01 23 0C", answer, sd);

        // 0dd Shift for 01 0C
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(10, pd, 0, 5, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 5, pd, 0, 5, 1, false);

        answer[0] = (byte) 0x00;
        answer[1] = (byte) 0x10;
        answer[2] = (byte) 0x0C;
        assertArrayEquals("00 01 0C shifted by 1 to 00 10 0C", answer, sd);

        // 0dd Shift for 01 23 4C no 0's in front after shift
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(1234, pd, 0, 5, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 5, pd, 0, 5, 1, false);

        answer[0] = (byte) 0x12;
        answer[1] = (byte) 0x34;
        answer[2] = (byte) 0x0C;
        assertArrayEquals("01 23 4C shifted to 12 34 0C", answer, sd);

        // 0dd Shift for 01 23 4C
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(1234, pd, 0, 7, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 7, pd, 0, 7, 1, false);

        answer[0] = (byte) 0x00;
        answer[1] = (byte) 0x12;
        answer[2] = (byte) 0x34;
        answer[3] = (byte) 0x0C;
        assertArrayEquals("00 01 23 4C shifted to 00 12 34 0C", answer, sd);
    }

    @Test
    public void testLeftEvenShiftOffset()
    {
        byte[] pd = new byte[16];
        byte[] sd = new byte[16];
        byte[] answer = new byte[16];

        // Even shift with 1 offset for 9C
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(9, pd, 1, 5, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 1, 5, pd, 1, 5, 2, false);

        answer[0] = (byte) 0xFF;
        answer[1] = (byte) 0x00;
        answer[2] = (byte) 0x90;
        answer[3] = (byte) 0x0C;
        assertArrayEquals("shift 00 00 9C by 2 with an offset of 1", answer, sd);

        // Even shift with 1 offset for 01 0C
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(10, pd, 1, 5, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 1, 5, pd, 1, 5, 2, false);

        answer[0] = (byte) 0xFF;
        answer[1] = (byte) 0x01;
        answer[2] = (byte) 0x00;
        answer[3] = (byte) 0x0C;
        assertArrayEquals("00 01 0C shifted by 2 with an offset of 1", answer, sd);
    }

    @Test
    public void testLeftOddShiftOffset()
    {
        byte[] pd = new byte[16];
        byte[] sd = new byte[16];
        byte[] answer = new byte[16];

        // Odd shift with 1 offset for 9C
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(9, pd, 1, 5, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 1, 5, pd, 1, 5, 1, false);

        answer[0] = (byte) 0xFF;
        answer[1] = (byte) 0x00;
        answer[2] = (byte) 0x09;
        answer[3] = (byte) 0x0C;
        assertArrayEquals("00 00 9C shifted by 1 with an offset of 1 to 00 09 0C", answer, sd);

        // Odd shift with 1 offset for 01 0C
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(10, pd, 1, 5, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 1, 5, pd, 1, 5, 1, false);

        answer[0] = (byte) 0xFF;
        answer[1] = (byte) 0x00;
        answer[2] = (byte) 0x10;
        answer[3] = (byte) 0x0C;
        assertArrayEquals("00 01 0C shifted by 1 with an offset of 1 to 00 10 0C", answer, sd);
    }

    @Test
    public void testLeftEvenShiftOverflow()
    {
        boolean catched = false;
        byte[] pd = new byte[16];
        byte[] sd = new byte[16];
        byte[] answer = new byte[16];

        // Overflow set as true with 0 offset even shift
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(10, pd, 0, 5, false);

        try
        {
            PackedDecimal.shiftLeftPackedDecimal(sd, 0, 5, pd, 0, 5, 4, true);
        }
        catch (ArithmeticException e)
        {
            catched = true;
        }

        if (!catched)
            fail("Did not catch arithmetic exception.");
    }

    @Test
    public void testLeftOddShiftOverflow()
    {
        boolean catched = false;
        byte[] pd = new byte[16];
        byte[] sd = new byte[16];
        byte[] answer = new byte[16];

        // Overflow set as true with 0 offset odd shift
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(10, pd, 0, 5, false);

        catched = false;
        try
        {
            PackedDecimal.shiftLeftPackedDecimal(sd, 0, 5, pd, 0, 5, 5, true);
        }
        catch (ArithmeticException e)
        {
            catched = true;
        }

        if (!catched)
            fail("Did not catch arithmetic exception.");
    }

    @Test
    public void testLeftEvenShiftOverflowOffset()
    {
        boolean catched = false;
        byte[] pd = new byte[16];
        byte[] sd = new byte[16];
        byte[] answer = new byte[16];

        // Overflow set as true with 1 offset even shift space in the array but
        // not with offset
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(10, pd, 1, 5, false);

        catched = false;
        try
        {
            PackedDecimal.shiftLeftPackedDecimal(sd, 1, 5, pd, 1, 5, 4, true);
        }
        catch (ArithmeticException e)
        {
            catched = true;
        }

        if (!catched)
            fail("Did not catch arithmetic exception.");

    }

    @Test
    public void testLeftShiftOverflowCuttoff()
    {
        byte[] pd = new byte[16];
        byte[] sd = new byte[16];
        byte[] answer = new byte[16];

        // Overflow set as false but it does occur for odd shift with no offset
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(123, pd, 0, 3, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 3, pd, 0, 3, 1, false);

        answer[0] = (byte) 0x23;
        answer[1] = (byte) 0x0C;
        assertArrayEquals("12 3C shifted to the left by 1 to 23 0C", answer, sd);

        // Overflow set as false but it does occur for even shift with no offset
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(123, pd, 0, 3, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 3, pd, 0, 3, 2, false);

        answer[0] = (byte) 0x30;
        answer[1] = (byte) 0x0C;
        assertArrayEquals("12 3C shifted to the left by 2 to 30 0C", answer, sd);

    }

    @Test
    public void testLeftOddShiftDifferentOffsetsSourceDestination()
    {
        byte[] pd = new byte[16];
        byte[] sd = new byte[16];
        byte[] answer = new byte[16];

        // Odd shift with different offsets for source and destination
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(123, pd, 1, 4, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 4, pd, 1, 4, 1, true);
        answer[0] = (byte) 0x01;
        answer[1] = (byte) 0x23;
        answer[2] = (byte) 0x0C;
        assertArrayEquals(" 00 12 3C with offset of 1 shifted to the left by 1 to 01 23 0C with no offset", answer, sd);

        // Even shift with different offsets for source and destination with
        // overflow occuring even though it is set as false
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(123, pd, 1, 4, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 4, pd, 1, 4, 2, false);
        answer[0] = (byte) 0x02;
        answer[1] = (byte) 0x30;
        answer[2] = (byte) 0x0C;
        assertArrayEquals("00 12 3C with offset of 1 shifted to the left by 2 to 02 30 0C with no offset", answer, sd);
    }

    @Test
    public void testLeftOddShiftIncreasingPrecision()
    {
        byte[] pd = new byte[16];
        byte[] sd = new byte[16];
        byte[] answer = new byte[16];

        // Increasing precision from source to destination 0 shift
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(9, pd, 0, 3, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 5, pd, 0, 3, 0, false);
        answer[0] = (byte) 0x00;
        answer[1] = (byte) 0x00;
        answer[2] = (byte) 0x9C;
        assertArrayEquals("00 9C shifted by 0 to the left with increasing precision to 00 00 9C", answer, sd);

        // Increasing precision from source to destination 2 shift
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(9, pd, 0, 3, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 5, pd, 0, 3, 2, false);
        answer[0] = (byte) 0x00;
        answer[1] = (byte) 0x90;
        answer[2] = (byte) 0x0C;
        assertArrayEquals("00 9C shifted by 2 to the left and increased precision to 5: 00 90 0C", answer, sd);

        // Increasing precision from source to destination 1 shift
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(9, pd, 0, 3, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 5, pd, 0, 3, 1, false);
        answer[0] = (byte) 0x00;
        answer[1] = (byte) 0x09;
        answer[2] = (byte) 0x0C;
        assertArrayEquals("00 9C shifted by 1 to the left and increased precision to 5: 00 09 0C", answer, sd);
    }

    @Test
    public void testLeftOddShiftDecreasingPrecision()
    {
        boolean catched = false;
        byte[] pd = new byte[16];
        byte[] sd = new byte[16];
        byte[] answer = new byte[16];

        // Decreasing precision from source to destination with 1 shift
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(9, pd, 0, 7, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 5, pd, 0, 7, 1, false);

        answer[0] = (byte) 0x00;
        answer[1] = (byte) 0x09;
        answer[2] = (byte) 0x0C;
        assertArrayEquals("00 00 00 9C shifted by 1 to the left and decreased precision to 5:00 09 0C", answer, sd);

        // Decreasing precision from source to destination with 2 shift
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(9, pd, 0, 7, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 5, pd, 0, 7, 2, false);

        answer[0] = (byte) 0x00;
        answer[1] = (byte) 0x90;
        answer[2] = (byte) 0x0C;
        assertArrayEquals("00 00 00 9C shifted b 2 to the left and decreased precision to 5 : 00 90 0C", answer, sd);

        // Decreasing precision from source to destination with 1 shift with 01
        // 0C
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(10, pd, 0, 7, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 5, pd, 0, 7, 1, false);

        answer[0] = (byte) 0x00;
        answer[1] = (byte) 0x10;
        answer[2] = (byte) 0x0C;
        assertArrayEquals("00 00 01 0C shifted by 1 to the left and decreased precision to 5 : 00 10 0C", answer, sd);

        // Decreasing precision from source to destination with 2 shift with 01
        // 0C
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(10, pd, 0, 7, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 5, pd, 0, 7, 2, false);

        answer[0] = (byte) 0x01;
        answer[1] = (byte) 0x00;
        answer[2] = (byte) 0x0C;
        assertArrayEquals("00 00 01 0C shifted by 2 to the left and decreased precision to 5: 01 00 0C", answer, sd);

        // Decreasing precision from source to destination with no overflow
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(9, pd, 0, 7, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 5, pd, 0, 7, 0, true);

        answer[0] = (byte) 0x00;
        answer[1] = (byte) 0x00;
        answer[2] = (byte) 0x9C;
        assertArrayEquals("00 00 00 9C shifted by 0 to the left and decreased precision to 5 : 00 00 9C", answer, sd);

        // Decreasing precision from source to destination with overflow but set
        // as false
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(123, pd, 0, 3, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 1, pd, 0, 3, 0, false);

        answer[0] = (byte) 0x3C;
        answer[1] = (byte) 0xFF;
        answer[2] = (byte) 0xFF;
        assertArrayEquals("12 3C shifted by 0 to the left and decreased precision to 1 : 3C ", answer, sd);

        // Decreasing precision from source to destination with overflow but set
        // as true
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(123, pd, 0, 3, false);

        catched = false;
        try
        {
            PackedDecimal.shiftLeftPackedDecimal(sd, 0, 1, pd, 0, 3, 0, true);
        }
        catch (ArithmeticException e)
        {
            catched = true;
        }

        if (!catched)
            fail("Did not catch arithmetic exception.");

        // Decreasing precision from source to destination with overflow but set
        // as true
        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(123, pd, 0, 3, false);

        catched = false;
        try
        {
            PackedDecimal.shiftLeftPackedDecimal(sd, 0, 1, pd, 0, 3, 0, true);
        }
        catch (ArithmeticException e)
        {
            catched = true;
        }

        if (!catched)
            fail("Did not catch arithmetic exception.");
    }

    @Test
    // Test Even/Odd Precisions for destination and source
    public void testLeftOddEvenPrecision()
    {
        byte[] pd = new byte[16];
        byte[] sd = new byte[16];
        byte[] answer = new byte[16];

        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(1234, pd, 0, 4, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 3, pd, 0, 4, 0, false);

        answer[0] = (byte) 0x23;
        answer[1] = (byte) 0x4C;
        assertArrayEquals(" 01 23 4C descreased precision to 23 4C ", answer, sd);

        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(123, pd, 0, 3, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 4, pd, 0, 3, 0, false);

        answer[0] = (byte) 0x00;
        answer[1] = (byte) 0x12;
        answer[2] = (byte) 0x3C;
        assertArrayEquals("12 3C to 00 12 3C ", answer, sd);

    }

    @Test
    // Test Even/Odd Precisions for destination and source
    public void testLeftOddEvenPrecisionOffsetOne()
    {
        byte[] pd = new byte[16];
        byte[] sd = new byte[16];
        byte[] answer = new byte[16];

        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(1234, pd, 0, 4, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 3, pd, 0, 4, 1, false);

        answer[0] = (byte) 0x34;
        answer[1] = (byte) 0x0C;
        assertArrayEquals("01 23 4C shifted to 34 0C", answer, sd);

        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(123, pd, 0, 3, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 4, pd, 0, 3, 1, false);

        answer[0] = (byte) 0x01;
        answer[1] = (byte) 0x23;
        answer[2] = (byte) 0x0C;
        assertArrayEquals("12 3C shifted to 01 23 0C", answer, sd);

    }

    @Test
    // Test Even/Odd Precisions for destination and source
    public void testLeftOddEvenPrecisionOffsetTwo()
    {
        byte[] pd = new byte[16];
        byte[] sd = new byte[16];
        byte[] answer = new byte[16];

        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(1234, pd, 0, 4, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 3, pd, 0, 4, 2, false);

        answer[0] = (byte) 0x40;
        answer[1] = (byte) 0x0C;
        assertArrayEquals("01 23 4C", answer, sd);

        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(123, pd, 0, 3, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 4, pd, 0, 3, 2, false);

        answer[0] = (byte) 0x02;
        answer[1] = (byte) 0x30;
        answer[2] = (byte) 0x0C;
        assertArrayEquals("12 3C shifted by 2 to 02 30 0C", answer, sd);

    }

    @Test
    // Test Even Shift for Right shift
    public void testRightEvenShift()
    {
        byte[] pd = new byte[16];
        byte[] sd = new byte[16];
        byte[] answer = new byte[16];

        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(123000, pd, 0, 6, false);
        PackedDecimal.shiftRightPackedDecimal(sd, 0, 6, pd, 0, 6, 2, false, false);

        answer[0] = (byte) 0x00;
        answer[1] = (byte) 0x01;
        answer[2] = (byte) 0x23;
        answer[3] = (byte) 0x0C;
        assertArrayEquals("  01 23 00 0C shifted by 2 00 01 23 0C", answer, sd);

    }

    @Test
    // Test Even Shift for Right shift with offset
    public void testRightEvenShiftOffset()
    {
        byte[] pd = new byte[16];
        byte[] sd = new byte[16];
        byte[] answer = new byte[16];

        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(123000, pd, 1, 6, false);
        PackedDecimal.shiftRightPackedDecimal(sd, 1, 6, pd, 1, 6, 2, false, false);

        answer[0] = (byte) 0xFF;
        answer[1] = (byte) 0x00;
        answer[2] = (byte) 0x01;
        answer[3] = (byte) 0x23;
        answer[4] = (byte) 0x0C;
        assertArrayEquals("1 23 00 0C shifted by 2 to 00 01 23 0C", answer, sd);

    }

    @Test
    // Test Shift for right shift
    public void testRightOddShift()
    {
        byte[] pd = new byte[16];
        byte[] sd = new byte[16];
        byte[] answer = new byte[16];

        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(1230, pd, 0, 5, false);
        PackedDecimal.shiftRightPackedDecimal(sd, 0, 5, pd, 0, 5, 1, false, false);

        answer[0] = (byte) 0x00;
        answer[1] = (byte) 0x12;
        answer[2] = (byte) 0x3C;
        assertArrayEquals("01 23 0C shifted by 1 to 00 12 3C", answer, sd);

    }

    @Test
    // Test Odd Shift for Right shift with offset
    public void testRightOddShiftOffset()
    {
        byte[] pd = new byte[16];
        byte[] sd = new byte[16];
        byte[] answer = new byte[16];

        Arrays.fill(pd, (byte) 0xFF);
        Arrays.fill(sd, (byte) 0xFF);
        Arrays.fill(answer, (byte) 0xFF);
        DecimalData.convertLongToPackedDecimal(123000, pd, 1, 6, false);
        PackedDecimal.shiftRightPackedDecimal(sd, 1, 6, pd, 1, 6, 2, false, false);

        answer[0] = (byte) 0xFF;
        answer[1] = (byte) 0x00;
        answer[2] = (byte) 0x01;
        answer[3] = (byte) 0x23;
        answer[4] = (byte) 0x0C;
        assertArrayEquals(answer, sd);

    }

    @Test
    public void testShiftLeftSimpleRandom()
    {
        for (int i = 0; i < 1000; i++)
        {
            int length;
            int value;
            do
            {
                value = getRandomInt();
                length = String.valueOf(value).length();
            } while (length <= 1);
            int shiftAmount = randomGen.nextInt(length - 1);

            int prec2 = length - shiftAmount;
            if (randomGen.nextBoolean())
                value = -value;
            testShiftLeft(value, length, shiftAmount, prec2);
        }
    }

    @Test
    public void testShiftLeftExample()
    {
        byte[] pd = new byte[16];
        byte[] sd = new byte[16];
        byte[] answer = new byte[16];

        DecimalData.convertLongToPackedDecimal(123, pd, 0, 3, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 3, pd, 0, 3, 10, false);

        answer[0] = (byte) 0x00;
        answer[1] = (byte) 0x0C;
        assertArrayEquals("12 3C shifted by 10", answer, sd);

        DecimalData.convertLongToPackedDecimal(123, pd, 0, 3, false);
        PackedDecimal.shiftLeftPackedDecimal(sd, 0, 3, pd, 0, 3, 9, false);

        answer[0] = (byte) 0x00;
        answer[1] = (byte) 0x0C;
        assertArrayEquals("12 3C shifted by 9", answer, sd);
    }

    @Test
    public void testMalformedPackedDecimalOfZeros()
    {
        byte[] pd = new byte[16];
        byte[] sd = new byte[16];

        Arrays.fill(pd, (byte) 0x00);
        try
        {
        	PackedDecimal.shiftLeftPackedDecimal(sd, 0, 3, pd, 0, 3, 0, false);
        }
        catch (IllegalArgumentException e)
        {
        	// Java 9 only: new DAA API throws IllegalArgumentException on malformed inputs
        }

    }

    private void testShiftLeft(int value, int prec1, int shiftAmount, int prec2)
    {

        DecimalData.convertLongToPackedDecimal(value, original, 0, prec1, false);
        PackedDecimal.shiftLeftPackedDecimal(copy, 0, prec2, original, 0, prec1, shiftAmount, false);
        long result = (long) DecimalData.convertPackedDecimalToLong(copy, 0, prec2, false);
        long newvalue = (long) (value * (Math.pow(10, shiftAmount)));
        newvalue = newvalue % ((long) (Math.pow(10, prec2)));
        String str = String.format("value: %d, sA: %d, prec1:%d prec2: %d, result: %d, newvalue: %d \n", value, shiftAmount, prec1, prec2, result, newvalue);
        assertEquals(str, newvalue, result);
    }

    @Test
    public void testShiftLeftInvalidPrecision()
    {
        byte[] pd = new byte[16];
        byte[] sd = new byte[16];

        DecimalData.convertLongToPackedDecimal(123, pd, 0, 3, false);

        boolean catched = false;
        try
        {
            PackedDecimal.shiftLeftPackedDecimal(sd, 0, 0, pd, 0, 3, 0, true);
        }
        catch (IllegalArgumentException e)
        {
            catched = true;
        }

        if (!catched)
            fail("Did not catch illegal argument.");

        DecimalData.convertLongToPackedDecimal(123, pd, 0, 3, false);

        catched = false;
        try
        {
            PackedDecimal.shiftLeftPackedDecimal(sd, 0, 1, pd, 0, 0, 0, true);
        }
        catch (IllegalArgumentException e)
        {
            catched = true;
        }

        if (!catched)
            fail("Did not catch illegal argument.");
    }

    @Test
    public void testLeftOffsetOne()
    {
        /* Both expectedArray and expectedArray2 are needed because
         * there was a behavioral change for VSRP instruction on z15
         * and up. On z15 and up, if the result following the shift
         * is 0, it is treated as positive. However, on prior
         * hardware the 0 would retain the sign of the input packed
         * decimal. For example: doing a left shift by 1 on 5D (-5)
         * would result in 0D (-0) on z14; and 0C (+0) on z15 and above.
         */
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];
        byte[] expectedArray2 = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);
        Arrays.fill(expectedArray2, (byte) 0xff);

        // Test precision of 1 digit, shifted left by 1
        inputArray[1] = 0x5D;
        expectedArray[2] = 0x0D;
        expectedArray2[2] = 0x0C;
        PackedDecimal.shiftLeftPackedDecimal(outputArray, 2, 1, inputArray, 1, 1, 1, false);
        boolean isExpectedResult = Arrays.equals(expectedArray, outputArray) || Arrays.equals(expectedArray2, outputArray);
        assertTrue("testLeftOffsetOne - Test precision of 1 << 1 to precision 1", isExpectedResult);
    }

    @Test
    public void testShifts()
    {

        final int offset0 = 0;
        final int precision31 = 31;
        final boolean errorChecking = false;

        long value = Utils.TestValue.SmallPositive.LongValue;
        byte[] packedDecimal = new byte[64];
        byte[] shiftedDecimal = new byte[64];
        final int shift5 = 5;
        final boolean roundedFalse = false;
        final boolean roundedTrue = true;

        value = Utils.TestValue.SmallPositive.LongValue;
        String functionName = "shift";

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftLeftPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, errorChecking);
        PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking);

        try
        {
            assertArrayEquals(packedDecimal, shiftedDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking), packedDecimal, shiftedDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftLeftPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, errorChecking);
        long result = DecimalData.convertPackedDecimalToLong(shiftedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(shiftLeft(value, shift5), result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking), shiftLeft(value, shift5), result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking);
        result = DecimalData.convertPackedDecimalToLong(shiftedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(shiftRight(value, shift5, roundedFalse), result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking), shiftRight(value, shift5, roundedFalse), result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, roundedTrue, errorChecking);
        result = DecimalData.convertPackedDecimalToLong(shiftedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(shiftRight(value, shift5, roundedTrue), result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedTrue, errorChecking), shiftRight(value, shift5, roundedTrue), result);
        }

        value = Utils.TestValue.LargePositive.LongValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftLeftPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, errorChecking);
        PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking);

        try
        {
            assertArrayEquals(packedDecimal, shiftedDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking), packedDecimal, shiftedDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftLeftPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, errorChecking);
        result = DecimalData.convertPackedDecimalToLong(shiftedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(shiftLeft(value, shift5), result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking), shiftLeft(value, shift5), result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking);
        result = DecimalData.convertPackedDecimalToLong(shiftedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(shiftRight(value, shift5, roundedFalse), result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking), shiftRight(value, shift5, roundedFalse), result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, roundedTrue, errorChecking);
        result = DecimalData.convertPackedDecimalToLong(shiftedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(shiftRight(value, shift5, roundedTrue), result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedTrue, errorChecking), shiftRight(value, shift5, roundedTrue), result);
        }

        value = Utils.TestValue.SmallNegative.LongValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftLeftPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, errorChecking);
        PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking);

        try
        {
            assertArrayEquals(packedDecimal, shiftedDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking), packedDecimal, shiftedDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftLeftPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, errorChecking);
        result = DecimalData.convertPackedDecimalToLong(shiftedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(shiftLeft(value, shift5), result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking), shiftLeft(value, shift5), result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking);
        result = DecimalData.convertPackedDecimalToLong(shiftedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(shiftRight(value, shift5, roundedFalse), result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking), shiftRight(value, shift5, roundedFalse), result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, roundedTrue, errorChecking);
        result = DecimalData.convertPackedDecimalToLong(shiftedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(shiftRight(value, shift5, roundedTrue), result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedTrue, errorChecking), shiftRight(value, shift5, roundedTrue), result);
        }

        value = Utils.TestValue.LargeNegative.LongValue;

        // TODO: add a test to test for 0xC/0xD when negative numbers are shifted to zero
        // ie, -9 shift right 5

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftLeftPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, errorChecking);
        PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking);

        try
        {
            assertArrayEquals(packedDecimal, shiftedDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking), packedDecimal, shiftedDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftLeftPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, errorChecking);
        result = DecimalData.convertPackedDecimalToLong(shiftedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(shiftLeft(value, shift5), result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking), shiftLeft(value, shift5), result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking);
        result = DecimalData.convertPackedDecimalToLong(shiftedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(shiftRight(value, shift5, roundedFalse), result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking), shiftRight(value, shift5, roundedFalse), result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, roundedTrue, errorChecking);
        result = DecimalData.convertPackedDecimalToLong(shiftedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(shiftRight(value, shift5, roundedTrue), result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedTrue, errorChecking), shiftRight(value, shift5, roundedTrue), result);
        }

        value = Utils.TestValue.LargestPossible.LongValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftLeftPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, errorChecking);
        PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking);

        try
        {
            assertArrayEquals(packedDecimal, shiftedDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking), packedDecimal, shiftedDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftLeftPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, errorChecking);
        // result = DecimalData.convertPackedDecimalToLong(shiftedDecimal, offset0, precision31, errorChecking);
        
        /*
         * The following result is too big for long, don't test
         */
        // Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0,
        // precision31, shift5, roundedFalse, errorChecking);
        // assertEquals(testName, shiftLeft(value, shift5), result);

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking);
        result = DecimalData.convertPackedDecimalToLong(shiftedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(shiftRight(value, shift5, roundedFalse), result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking), shiftRight(value, shift5, roundedFalse), result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, roundedTrue, errorChecking);
        result = DecimalData.convertPackedDecimalToLong(shiftedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(shiftRight(value, shift5, roundedTrue), result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedTrue, errorChecking), shiftRight(value, shift5, roundedTrue), result);
        }

        value = Utils.TestValue.SmallestPossible.LongValue;

        /*
         * Don't test because SmallestPossible * -1 is too big for Long
         */

        value = Utils.TestValue.Zero.LongValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftLeftPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, errorChecking);
        PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking);

        try
        {
            assertArrayEquals(packedDecimal, shiftedDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking), packedDecimal, shiftedDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftLeftPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, errorChecking);
        result = DecimalData.convertPackedDecimalToLong(shiftedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(shiftLeft(value, shift5), result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking), shiftLeft(value, shift5), result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking);
        result = DecimalData.convertPackedDecimalToLong(shiftedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(shiftRight(value, shift5, roundedFalse), result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking), shiftRight(value, shift5, roundedFalse), result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, roundedTrue, errorChecking);
        result = DecimalData.convertPackedDecimalToLong(shiftedDecimal, offset0, precision31, errorChecking);

        try
        {
            assertEquals(shiftRight(value, shift5, roundedTrue), result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedTrue, errorChecking), shiftRight(value, shift5, roundedTrue), result);
        }

        /*
         * Testing shift right rounded/unrounded with doubles
         */

        value = Utils.TestValue.Zero.LongValue;
        double doubleValue = 555555.0;
        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        BigDecimal bigDecimalValue = new BigDecimal(doubleValue);
        DecimalData.convertBigDecimalToPackedDecimal(bigDecimalValue, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftLeftPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, errorChecking);
        PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking);

        try
        {
            assertArrayEquals(packedDecimal, shiftedDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking), packedDecimal, shiftedDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        byte[] shiftedDecimalCorrect = new byte[64];
        DecimalData.convertBigDecimalToPackedDecimal(bigDecimalValue, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftLeftPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, errorChecking);
        DecimalData.convertBigDecimalToPackedDecimal(new BigDecimal(shiftLeft(doubleValue, shift5)), shiftedDecimalCorrect, offset0, precision31, errorChecking);

        try
        {
            assertArrayEquals(shiftedDecimalCorrect, shiftedDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking), shiftedDecimalCorrect, shiftedDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        shiftedDecimalCorrect = new byte[64];
        DecimalData.convertBigDecimalToPackedDecimal(bigDecimalValue, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking);
        DecimalData.convertBigDecimalToPackedDecimal(new BigDecimal(shiftRight(doubleValue, shift5, roundedFalse)), shiftedDecimalCorrect, offset0, precision31, errorChecking);

        try
        {
            assertArrayEquals(shiftedDecimalCorrect, shiftedDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedFalse, errorChecking), shiftedDecimalCorrect, shiftedDecimal);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        shiftedDecimalCorrect = new byte[64];
        DecimalData.convertBigDecimalToPackedDecimal(bigDecimalValue, packedDecimal, offset0, precision31, errorChecking);
        PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5, roundedTrue, errorChecking);
        DecimalData.convertBigDecimalToPackedDecimal(new BigDecimal(shiftRight(doubleValue, shift5, roundedTrue)), shiftedDecimalCorrect, offset0, precision31, errorChecking);

        try
        {
            assertArrayEquals(shiftedDecimalCorrect, shiftedDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift5, roundedTrue, errorChecking), shiftedDecimalCorrect, shiftedDecimal);
        }

        value = Utils.TestValue.SmallPositive.LongValue;
        final int offset5 = 5;
        final int offset10 = 10;
        final int precision2 = 2;
        final int precision15 = 15;
        final int precision16 = 16;
        final int precision30 = 30;
        final int shift1 = 1;

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision2, errorChecking);
        PackedDecimal.shiftLeftPackedDecimal(shiftedDecimal, offset0, precision2, packedDecimal, offset0, precision2, shift1, errorChecking);
        result = DecimalData.convertPackedDecimalToLong(shiftedDecimal, offset0, precision2, errorChecking);

        try
        {
            assertEquals(shiftLeft(value, shift1), result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision2, shiftedDecimal, offset0, precision2, shift1, roundedTrue, errorChecking), shiftLeft(value, shift1), result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset10, precision15, errorChecking);
        PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset10, precision15, packedDecimal, offset10, precision15, shift5, roundedFalse, errorChecking);
        result = DecimalData.convertPackedDecimalToLong(shiftedDecimal, offset10, precision15, errorChecking);

        try
        {
            assertEquals(shiftRight(value, shift5, roundedFalse), result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset10, precision15, shiftedDecimal, offset10, precision15, shift5, roundedFalse, errorChecking), shiftRight(value, shift5, roundedFalse), result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision16, errorChecking);
        PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset0, precision16, packedDecimal, offset0, precision16, shift5, roundedTrue, errorChecking);
        result = DecimalData.convertPackedDecimalToLong(shiftedDecimal, offset0, precision16, errorChecking);

        try
        {
            assertEquals(shiftRight(value, shift5, roundedTrue), result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset0, precision16, shiftedDecimal, offset0, precision16, shift5, roundedTrue, errorChecking), shiftRight(value, shift5, roundedTrue), result);
        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset5, precision30, errorChecking);
        PackedDecimal.shiftLeftPackedDecimal(shiftedDecimal, offset5, precision30, packedDecimal, offset5, precision30, shift5, errorChecking);
        PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset5, precision30, shiftedDecimal, offset5, precision30, shift5, roundedFalse, errorChecking);

        try
        {
            assertArrayEquals(packedDecimal, shiftedDecimal);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameShift("", functionName, value, packedDecimal, offset5, precision30, shiftedDecimal, offset5, precision30, shift5, roundedFalse, errorChecking), packedDecimal, shiftedDecimal);
        }
    }

    @Test
    public void testShiftsExceptions()
    {

        final int offset0 = 0;
        final int precision31 = 31;
        final boolean errorChecking = true;
        final boolean errorCheckingFalse = false;

        long value = Utils.TestValue.SmallPositive.LongValue;
        byte[] packedDecimal = new byte[64];
        byte[] shiftedDecimal = new byte[64];
        final int shift5 = 5;
        final int shift25 = 25;
        final int shift55 = 55;
        final int shift85 = 85;
        final int offset25 = 25;
        final int offset50 = 50;
        final int precision100 = 100;
        final int precision5 = 5;
        final boolean roundedFalse = false;
        final boolean roundedTrue = true;
        value = Utils.TestValue.SmallPositive.LongValue;
        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        try
        {
            DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
            PackedDecimal.shiftLeftPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift55, errorChecking);
            PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset0, precision31, shiftedDecimal, offset0, precision31, shift55, roundedFalse, errorChecking);

            fail("Assertion in TestShiftsAndConvert on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        try
        {
            DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset25, precision31, errorChecking);
            PackedDecimal.shiftLeftPackedDecimal(shiftedDecimal, offset25, precision31, packedDecimal, offset25, precision31, shift25, errorChecking);
            DecimalData.convertPackedDecimalToLong(shiftedDecimal, offset25, precision31, errorChecking);
            fail("Assertion in TestShiftsAndConvert on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }

        packedDecimal = new byte[32];
        shiftedDecimal = new byte[32];
        try
        {
            DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision100, errorChecking);
            PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset0, precision100, packedDecimal, offset0, precision100, shift85, roundedFalse, errorChecking);
            DecimalData.convertPackedDecimalToLong(shiftedDecimal, offset0, precision100, errorChecking);

            fail("Assertion in TestShiftsAndConvert on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        try
        {
            DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset50, precision100, errorChecking);
            PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset50, precision100, packedDecimal, offset0, precision100, shift55, roundedTrue, errorChecking);
            DecimalData.convertPackedDecimalToLong(shiftedDecimal, offset50, precision100, errorChecking);
            fail("Assertion in TestShiftsAndConvert on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {

        }

        value = 50000;
        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        try
        {
            DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision5, errorChecking);
            PackedDecimal.shiftLeftPackedDecimal(shiftedDecimal, offset0, precision5, packedDecimal, offset0, precision5, shift5, errorChecking);
            PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset0, precision5, shiftedDecimal, offset0, precision5, shift5, roundedFalse, errorChecking);
            fail("Assertion in TestShiftsAndConvert on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (ArithmeticException e)
        {

        }

        Arrays.fill(packedDecimal, (byte) 0x00);
        shiftedDecimal = new byte[9];
        byte[] brokenPackedDecimal = new byte[] { 0x12, 0x23, (byte) 0xff, (byte) 0xcb, (byte) 0xb2, (byte) 0xa2, (byte) 0xd9, (byte) 0xaa, (byte) 0xbc };

        final int precision7 = 7;
        final int shift2 = 2;
        try
        {
            PackedDecimal.shiftLeftPackedDecimal(shiftedDecimal, offset0, precision7, brokenPackedDecimal, offset0, precision7, shift2, errorChecking);
            fail("Assertion in TestShiftsAndConvert on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (IllegalArgumentException e)
        {

        }
        catch (ArithmeticException e2)
        {
        }
       
        try
        {
            PackedDecimal.shiftLeftPackedDecimal(shiftedDecimal, offset0, 17, brokenPackedDecimal, offset0, 17, shift2, errorCheckingFalse);
        }
        catch (IllegalArgumentException e)
        {
        }        

       

        Arrays.fill(packedDecimal, (byte) 0x00);
        Arrays.fill(shiftedDecimal, (byte) 0x00);
        byte[] packedDecimalNull = null;
        byte[] shiftedDecimalNull = null;

        try
        {
            DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision5, errorChecking);
            PackedDecimal.shiftLeftPackedDecimal(shiftedDecimal, offset0, precision5, packedDecimalNull, offset0, precision5, shift5, errorChecking);
            fail("Assertion in TestShiftsAndConvert on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }

        try
        {
            DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision5, errorChecking);
            PackedDecimal.shiftLeftPackedDecimal(shiftedDecimalNull, offset0, precision5, packedDecimal, offset0, 10, shift5, errorCheckingFalse);
            fail("Assertion in TestShiftsAndConvert on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }

        try
        {
            DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision5, errorChecking);
            PackedDecimal.shiftRightPackedDecimal(shiftedDecimal, offset0, precision5, packedDecimalNull, offset0, 10, shift5, roundedTrue, errorCheckingFalse);
            fail("Assertion in TestShiftsAndConvert on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }

        try
        {
            DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision5, errorChecking);
            PackedDecimal.shiftRightPackedDecimal(shiftedDecimalNull, offset0, precision5, packedDecimal, offset0, 10, shift5, roundedTrue, errorCheckingFalse);
            fail("Assertion in TestShiftsAndConvert on line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (NullPointerException e)
        {

        }

        packedDecimal = new byte[64];
        shiftedDecimal = new byte[64];
        final int shift5n = -5;

        try
        {
            DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
            PackedDecimal.shiftLeftPackedDecimal(shiftedDecimal, offset0, precision31, packedDecimal, offset0, precision31, shift5n, errorChecking);
           
            fail("Assertion in TestShiftsAndConvert on line " +
              Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        catch (IllegalArgumentException e)
        {

        }
    }

    private void testShiftRight(int value, int prec1, int shiftAmount, int prec2, boolean round)
    {
        int rounding_digit = 0;
        DecimalData.convertIntegerToPackedDecimal(value, original, 0, prec1, false);
        PackedDecimal.shiftRightPackedDecimal(copy, 0, prec2, original, 0, prec1, shiftAmount, round, false);
        int result = DecimalData.convertPackedDecimalToInteger(copy, 0, prec2, false);
        if (round && shiftAmount > 0)
        {
            rounding_digit = (int) (value / (int) (Math.pow(10, shiftAmount - 1)));
            rounding_digit = Math.abs((int) (rounding_digit % 10));
        }
        int newvalue = (int) (value / (int) (Math.pow(10, shiftAmount)));
        if (rounding_digit >= 5)
        {
            if (newvalue > 0)
                newvalue++;
            else
                newvalue--;
        }
        if (prec2 < prec1)
            newvalue = newvalue % ((int) (Math.pow(10, prec2)));
        String str = String.format("value: %d, sA: %d, prec2: %d, result: %d, newvalue: %d \n", value, shiftAmount, prec2, result, newvalue);
        // System.out.printf("value: %d, sA: %d, prec2:%d, result:%d, newvalue:%d \n", value, shiftAmount, prec2, result, newvalue);
        assertEquals(str, newvalue, result);
    }

    @Test
    public void testShiftRightSimple()
    {
        for (int i = 0; i < 1000; i++)
        {
            int value = getRandomInt();
            int length = String.valueOf(value).length();
            int shiftAmount = randomGen.nextInt(length);
            int prec2 = length - shiftAmount;
            if (randomGen.nextBoolean())
                value = -value;
            // System.out.printf("Simple value: %d, sA: %d, prec2:%d \n", value, shiftAmount, prec2);
            testShiftRight(value, length, shiftAmount, prec2, false);
        }
    }

    @Test
    public void testShiftRightLowerDstPrec() // decrease precision from front
    {
        for (int i = 0; i < 1000; i++)
        {
            int value = getRandomInt();
            int length = String.valueOf(value).length();
            int shiftAmount = randomGen.nextInt(length);
            int prec2 = length - shiftAmount - randomGen.nextInt(3);
            if (randomGen.nextBoolean())
                value = -value;
            if (prec2 <= 0)
                prec2 = 1;
            // System.out.printf("LDP value: %d, sA: %d, prec2:%d \n", value, shiftAmount, prec2);
            testShiftRight(value, length, shiftAmount, prec2, false);
        }
    }

    @Test
    public void testShiftRightHigherDstPrec() // should have leading zeros
    {
        for (int i = 0; i < 1000; i++)
        {
            int value = getRandomInt();
            int length = String.valueOf(value).length();
            int shiftAmount = randomGen.nextInt(length);
            int prec2 = length - shiftAmount + randomGen.nextInt(3);
            if (randomGen.nextBoolean())
                value = -value;
            // System.out.printf("HDP value: %d, sA: %d, prec2:%d \n", value, shiftAmount, prec2);
            Arrays.fill(copy, (byte) 0xFF);
            testShiftRight(value, length, shiftAmount, prec2, false);
        }

    }

    @Test
    public void testShiftRightSimpleRound()
    {
        for (int i = 0; i < 1000; i++)
        {
            int value = getRandomInt();
            int length = String.valueOf(value).length();
            int shiftAmount = randomGen.nextInt(length);
            int prec2 = length - shiftAmount + 1;
            if (randomGen.nextBoolean())
                value = -value;
            // System.out.printf("Rounding value: %d, sA: %d, prec2:%d \n", value, shiftAmount, prec2);
            testShiftRight(value, length, shiftAmount, prec2, true);
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void testShiftRightNegativeShiftAmountException()
    {
        int value = getRandomInt();
        int length = String.valueOf(value).length();
        int shiftAmount = (-1) * randomGen.nextInt(length);
        if (shiftAmount == 0)
            shiftAmount = -1;
        int prec2 = length - shiftAmount + 1;
        testShiftRight(value, length, shiftAmount, prec2, false);

    }

    @Test
    public void testShiftRightNoRounding()
    {
        int value = getRandomInt();
        int length = String.valueOf(value).length();
        int shiftAmount = 0;
        int prec2 = length - randomGen.nextInt(3);
        testShiftRight(value, length, shiftAmount, prec2, true);

    }

    public long shiftLeft(long x, int shift)
    {
        return (long) (Math.pow(10, shift) * x);
    }

    public long shiftRight(long x, int shift, boolean round)
    {
        double result = x / (Math.pow(10, shift));
        if (round)
        {
            return (long) Math.round(result);
        } else
        {
            return (long) result;
        }
    }

    public double shiftLeft(double x, int shift)
    {
        return (Math.pow(10, shift) * x);
    }

    public double shiftRight(double x, int shift, boolean round)
    {
        double result = x / (Math.pow(10, shift));
        if (round)
        {
            return Math.round(result);
        } else
        {
            return Math.floor(result);
        }
    }

    @Test
    public void testShiftLeftUnitCases()
    {
        /* Both expectedArray and expectedArray2 are needed because
         * there was a behavioral change for VSRP instruction on z15
         * and up. On z15 and up, if the result following the shift
         * is 0, it is treated as positive. However, on prior
         * hardware the 0 would retain the sign of the input packed
         * decimal. For example: doing a left shift by 1 on 5D (-5)
         * would result in 0D (-0) on z14; and 0C (+0) on z15 and above.
         */
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];
        byte[] expectedArray2 = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);
        Arrays.fill(expectedArray2, (byte) 0xff);

        // Test precision of 1 digit, no shift
        inputArray[1] = 0x5D;
        expectedArray[2] = 0x5D;
        PackedDecimal.shiftLeftPackedDecimal(outputArray, 2, 1, inputArray, 1, 1, 0, false);
        assertArrayEquals("testShiftLeftUnitCases - Test precision of 1 << 0 to precision 1", expectedArray, outputArray);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);
        Arrays.fill(expectedArray2, (byte) 0xff);

        // Test precision of 1 digit, shifted by 1
        inputArray[1] = 0x5D;
        expectedArray[2] = 0x0D;
        expectedArray2[2] = 0x0C;
        PackedDecimal.shiftLeftPackedDecimal(outputArray, 2, 1, inputArray, 1, 1, 1, false);
        boolean isExpectedResult = Arrays.equals(expectedArray, outputArray) || Arrays.equals(expectedArray2, outputArray);
        assertTrue("testShiftLeftUnitCases - Test precision of 1 << 1 to precision 1", isExpectedResult);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 1 digit, shifted by 2 to precision 2.
        inputArray[1] = 0x5C;
        expectedArray[2] = 0x00;
        expectedArray[3] = 0x0C;

        PackedDecimal.shiftLeftPackedDecimal(outputArray, 2, 2, inputArray, 1, 1, 2, false);
        assertArrayEquals("testShiftLeftUnitCases - Test precision of 1 << 2 to precision 2", expectedArray, outputArray);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 1 digit, shifted by 2, to precision 3.
        inputArray[1] = 0x5C;
        expectedArray[2] = 0x50;
        expectedArray[3] = 0x0C;

        PackedDecimal.shiftLeftPackedDecimal(outputArray, 2, 3, inputArray, 1, 1, 2, false);
        assertArrayEquals("testShiftLeftUnitCases - Test precision of 1 << 2 to precision 3", expectedArray, outputArray);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 1 digit, shifted by 1 to precision 2.
        inputArray[1] = 0x5C;
        expectedArray[2] = 0x05;
        expectedArray[3] = 0x0C;

        PackedDecimal.shiftLeftPackedDecimal(outputArray, 2, 2, inputArray, 1, 1, 1, false);
        assertArrayEquals("testShiftLeftUnitCases - Test precision of 1 << 1 to precision 2", expectedArray, outputArray);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 2 digit (08 5D), no shift to precision 2 (08 5D).
        inputArray[1] = 0x08;
        inputArray[2] = 0x5D;
        expectedArray[2] = 0x08;
        expectedArray[3] = 0x5D;
        PackedDecimal.shiftLeftPackedDecimal(outputArray, 2, 2, inputArray, 1, 2, 0, false);
        assertArrayEquals("testShiftLeftUnitCases - Test precision of 2 << 0 to precision 2", expectedArray, outputArray);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 2 digit (08 5D), shifted by 1 to precision 2 (05
        // 0D).
        inputArray[1] = 0x08;
        inputArray[2] = 0x5D;
        expectedArray[2] = 0x05;
        expectedArray[3] = 0x0D;
        PackedDecimal.shiftLeftPackedDecimal(outputArray, 2, 2, inputArray, 1, 2, 1, false);
        assertArrayEquals("testShiftLeftUnitCases - Test precision of 2 << 1 to precision 2", expectedArray, outputArray);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);
        Arrays.fill(expectedArray2, (byte) 0xff);

        // Test precision of 2 digit (08 5D), shifted by 2 to precision 2 (00
        // 0D).
        inputArray[1] = 0x08;
        inputArray[2] = 0x5D;
        expectedArray[2] = 0x00;
        expectedArray[3] = 0x0D;
        expectedArray2[2] = 0x00;
        expectedArray2[3] = 0x0C;
        PackedDecimal.shiftLeftPackedDecimal(outputArray, 2, 2, inputArray, 1, 2, 2, false);
        isExpectedResult = Arrays.equals(expectedArray, outputArray) || Arrays.equals(expectedArray2, outputArray);
        assertTrue("testShiftLeftUnitCases - Test precision of 2 << 2 to precision 2", isExpectedResult);
    }

    @Test
    public void testShiftRightUnitCases()
    {
        /* Both expectedArray and expectedArray2 are needed because
         * there was a behavioral change for VSRP instruction on z15
         * and up. On z15 and up, if the result following the shift
         * is 0, it is treated as positive. However, on prior
         * hardware the 0 would retain the sign of the input packed
         * decimal. For example: doing a left shift by 1 on 5D (-5)
         * would result in 0D (-0) on z14; and 0C (+0) on z15 and above.
         */
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];
        byte[] expectedArray2 = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 1 digit, shifted left by 0, no rounding
        inputArray[1] = 0x5D;
        expectedArray[2] = 0x5D;
        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 1, inputArray, 1, 1, 0, false, false);
        assertArrayEquals("testShiftRightUnitCases - Test precision of 1 >> 0 to precision 1", expectedArray, outputArray);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 1 digit, shifted left by 0, no rounding - checkOverflow
        inputArray[1] = 0x5D;
        expectedArray[2] = 0x5D;
        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 1, inputArray, 1, 1, 0, false, true);
        assertArrayEquals("testShiftRightUnitCases - Test precision of 1 >> 0 to precision 1 check overflow", expectedArray, outputArray);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);
        Arrays.fill(expectedArray2, (byte) 0xff);

        // Test precision of 1 digit, shifted right by 1, no rounding
        inputArray[1] = 0x5D;
        expectedArray[2] = 0x0D;
        expectedArray2[2] = 0x0C;
        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 1, inputArray, 1, 1, 1, false, false);
        boolean isExpectedResult = Arrays.equals(expectedArray, outputArray) || Arrays.equals(expectedArray2, outputArray);
        assertTrue("testShiftRightUnitCases - Test precision of 1 >> 1 to precision 1 no rounding", isExpectedResult);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);
        Arrays.fill(expectedArray2, (byte) 0xff);

        // Test precision of 1 digit, shifted right by 1, no rounding - checkOverflow
        inputArray[1] = 0x5D;
        expectedArray[2] = 0x0D;
        expectedArray2[2] = 0x0C;
        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 1, inputArray, 1, 1, 1, false, true);
        isExpectedResult = Arrays.equals(expectedArray, outputArray) || Arrays.equals(expectedArray2, outputArray);
        assertTrue("testShiftRightUnitCases - Test precision of 1 >> 1 to precision 1 no rounding check overflow", isExpectedResult);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 1 digit, shifted right by 1, rounding
        inputArray[1] = 0x4D;
        expectedArray[2] = 0x0C;
        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 1, inputArray, 1, 1, 1, true, false);
        assertArrayEquals("testShiftRightUnitCases - Test precision of 1 >> 1 to precision 1 rounding", expectedArray, outputArray);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        /*
         * // Test precision of 1 digit, shifted right by 1, rounding -
         * checkOverflow inputArray[1] = 0x4D; expectedArray[2] = 0x0C;
         * PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 1, inputArray,
         * 1, 1, 1, true, true); assertArrayEquals(
         * "testShiftRightUnitCases - Test precision of 1 >> 1 to precision 1 rounding check overflow"
         * , expectedArray, outputArray);
         */

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 1 digit, shifted right by 1, rounding to 1
        inputArray[1] = (byte) 0x9C;
        expectedArray[2] = 0x1C;
        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 1, inputArray, 1, 1, 1, true, false);
        assertArrayEquals("testShiftRightUnitCases - Test precision of 1 >> 1 to precision 1 rounding to 1", expectedArray, outputArray);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 1 digit, shifted right by 1, rounding to 1 - checkOverflow
        inputArray[1] = (byte) 0x9C;
        expectedArray[2] = 0x1C;
        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 1, inputArray, 1, 1, 1, true, true);
        assertArrayEquals("testShiftRightUnitCases - Test precision of 1 >> 1 to precision 1 rounding to 1 checkOverflow", expectedArray, outputArray);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 1 digit, shifted by 2 to precision 2, no rounding
        inputArray[1] = 0x5C;
        expectedArray[2] = 0x00;
        expectedArray[3] = 0x0C;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 2, inputArray, 1, 1, 2, false, false);
        assertArrayEquals("testShiftRightUnitCases - Test precision of 1 >> 2 to precision 2 no rounding", expectedArray, outputArray);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 1 digit, shifted by 2 to precision 2, no rounding - checkOverflow
        inputArray[1] = 0x5C;
        expectedArray[2] = 0x00;
        expectedArray[3] = 0x0C;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 2, inputArray, 1, 1, 2, false, true);
        assertArrayEquals("testShiftRightUnitCases - Test precision of 1 >> 2 to precision 2 no rounding checkOverflow", expectedArray, outputArray);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 1 digit, shifted by 2 to precision 2, rounding
        inputArray[1] = 0x7C;
        expectedArray[2] = 0x00;
        expectedArray[3] = 0x0C;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 2, inputArray, 1, 1, 2, true, false);
        assertArrayEquals("testShiftRightUnitCases - Test precision of 1 >> 2 to precision 2 rounding", expectedArray, outputArray);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 1 digit, shifted by 2 to precision 2, rounding - checkOverflow
        inputArray[1] = 0x7C;
        expectedArray[2] = 0x00;
        expectedArray[3] = 0x0C;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 2, inputArray, 1, 1, 2, true, true);
        assertArrayEquals("testShiftRightUnitCases - Test precision of 1 >> 2 to precision 2 rounding checkOverflow", expectedArray, outputArray);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);
        Arrays.fill(expectedArray2, (byte) 0xff);

        // Test precision of 2 digit, shifted by 2, to precision 3.
        inputArray[1] = 0x08;
        inputArray[2] = 0x5D;
        expectedArray[2] = 0x00;
        expectedArray[3] = 0x0D;
        expectedArray2[2] = 0x00;
        expectedArray2[3] = 0x0C;
        
        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 3, inputArray, 1, 2, 2, false, false);
        isExpectedResult = Arrays.equals(expectedArray, outputArray) || Arrays.equals(expectedArray2, outputArray);
        assertTrue("testShiftRightUnitCases - Test precision of 2 >> 2 to precision 3 no rounding", isExpectedResult);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);
        Arrays.fill(expectedArray2, (byte) 0xff);

        // Test precision of 2 digit, shifted by 2, to precision 3 - checkOverflow
        inputArray[1] = 0x08;
        inputArray[2] = 0x5D;
        expectedArray[2] = 0x00;
        expectedArray[3] = 0x0D;
        expectedArray2[2] = 0x00;
        expectedArray2[3] = 0x0C;
        
        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 3, inputArray, 1, 2, 2, false, true);
        isExpectedResult = Arrays.equals(expectedArray, outputArray) || Arrays.equals(expectedArray2, outputArray);
        assertTrue("testShiftRightUnitCases - Test precision of 2 >> 2 to precision 3 no rounding checkOverflow", isExpectedResult);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 2 digit, shifted by 1, to precision 1 with no rounding
        inputArray[1] = 0x09;
        inputArray[2] = 0x5D;
        expectedArray[2] = (byte) 0x9D;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 1, inputArray, 1, 2, 1, false, false);
        assertArrayEquals("testShiftRightUnitCases - Test precision of 2 >> 1 to precision 1 no rounding", expectedArray, outputArray);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 2 digit, shifted by 1, to precision 1 with no rounding - checkOverflow
        inputArray[1] = 0x09;
        inputArray[2] = 0x5D;
        expectedArray[2] = (byte) 0x9D;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 1, inputArray, 1, 2, 1, false, true);
        assertArrayEquals("testShiftRightUnitCases - Test precision of 2 >> 1 to precision 1 no rounding checkOverflow", expectedArray, outputArray);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 2 digit, shifted by 2, to precision 3.
        inputArray[1] = 0x08;
        inputArray[2] = 0x5D;
        expectedArray[2] = 0x00;
        expectedArray[3] = 0x1D;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 3, inputArray, 1, 2, 2, true, false);
        assertArrayEquals("testShiftRightUnitCases - Test precision of 2 >> 2 to precision 3 rounding", expectedArray, outputArray);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 2 digit, shifted by 2, to precision 3 - checkOverflow
        inputArray[1] = 0x08;
        inputArray[2] = 0x5D;
        expectedArray[2] = 0x00;
        expectedArray[3] = 0x1D;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 3, inputArray, 1, 2, 2, true, true);
        assertArrayEquals("testShiftRightUnitCases - Test precision of 2 >> 2 to precision 3 rounding checkOverflow", expectedArray, outputArray);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 3 digit, shifted by 2, to precision 3.
        inputArray[1] = 0x18;
        inputArray[2] = 0x5C;
        expectedArray[2] = 0x00;
        expectedArray[3] = 0x1C;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 3, inputArray, 1, 3, 2, false, false);
        assertArrayEquals("testShiftRightUnitCases - Test precision of 3 >> 2 to precision 3 no rounding", expectedArray, outputArray);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 3 digit, shifted by 2, to precision 3 - checkOverflow
        inputArray[1] = 0x18;
        inputArray[2] = 0x5C;
        expectedArray[2] = 0x00;
        expectedArray[3] = 0x1C;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 3, inputArray, 1, 3, 2, false, true);
        assertArrayEquals("testShiftRightUnitCases - Test precision of 3 >> 2 to precision 3 no rounding checkOverflow", expectedArray, outputArray);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 2 digit, shifted by 2, to precision 3.
        inputArray[1] = 0x18;
        inputArray[2] = 0x5D;
        expectedArray[2] = 0x00;
        expectedArray[3] = 0x2D;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 3, inputArray, 1, 3, 2, true, false);
        assertArrayEquals("testShiftRightUnitCases - Test precision of 3 >> 2 to precision 3 rounding", expectedArray, outputArray);

        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 2 digit, shifted by 2, to precision 3.
        inputArray[1] = 0x18;
        inputArray[2] = 0x5D;
        expectedArray[2] = 0x00;
        expectedArray[3] = 0x2D;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 3, inputArray, 1, 3, 2, true, true);
        assertArrayEquals("testShiftRightUnitCases - Test precision of 3 >> 2 to precision 3 rounding - checkOverflow", expectedArray, outputArray);
    }

    @Test
    public void testShiftRightOverflowRounding()
    {
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 2 digit, shifted by 1, to precision 1 with rounding
        inputArray[1] = 0x09;
        inputArray[2] = 0x5C;
        expectedArray[2] = 0x0C;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 1, inputArray, 1, 2, 1, true, false);
        assertArrayEquals("testShiftRightOverflowRounding - Test precision of 2 >> 1 to precision 1 rounding", expectedArray, outputArray);

    }

    @Test(expected = ArithmeticException.class)
    public void testShiftRightOverflowRoundingException()
    {
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 2 digit, shifted by 1, to precision 1 with rounding
        inputArray[1] = 0x09;
        inputArray[2] = 0x5C;
        expectedArray[2] = 0x0C;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 1, inputArray, 1, 2, 1, true, true);
        assertArrayEquals("testShiftRightOverflowRoundingException - Test precision of 2 >> 1 to precision 1 rounding checkOverflow", expectedArray, outputArray);
    }

    @Test(expected = ArithmeticException.class)
    public void testShiftRightOverflowLossOfPrecision()
    {
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 3 digit (123), shifted by 1, to precision 1
        inputArray[1] = 0x12;
        inputArray[2] = 0x5C;
        expectedArray[2] = 0x2C;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 1, inputArray, 1, 3, 1, false, true);
        assertArrayEquals("testShiftRightOverflowLossOfPrecision - Test precision of 3 >> 1 to precision 1 checkOverflow", expectedArray, outputArray);
    }

    @Test
    public void testShiftRightNoOverflow()
    {
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 3 digit (023C), shifted by 1 (002C), to precision 1 
        // 2C)
        inputArray[1] = 0x02;
        inputArray[2] = 0x5C;
        expectedArray[2] = 0x2C;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 1, inputArray, 1, 3, 1, false, true);
        assertArrayEquals("testShiftRightNoOverflow - Test precision of 3 >> 1 to precision 1 checkOverflow", expectedArray, outputArray);
    }

    @Test
    public void testShiftRightNoOverflow2()
    {
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 5 digit (023C), shifted by 2 (002C), to precision 1
        // 2C)
        inputArray[0] = 0x00;
        inputArray[1] = 0x12;
        inputArray[2] = 0x5C;
        expectedArray[2] = 0x1C;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 1, inputArray, 0, 5, 2, false, true);
        assertArrayEquals("testShiftRightNoOverflow2 - Test precision of 3 >> 1 to precision 1 checkOverflow", expectedArray, outputArray);
    }

    @Test(expected = ArithmeticException.class)
    public void testShiftRightOverflow()
    {
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 3 digit (023C), shifted by 1 (002C), to precision 1
        // 2C)]
        inputArray[0] = 0x00;
        inputArray[1] = 0x10;
        inputArray[2] = 0x12;
        inputArray[3] = 0x5C;
        expectedArray[2] = 0x1C;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 1, inputArray, 0, 6, 2, false, true);
        assertArrayEquals("testShiftRightOverflow - Test precision of 3 >> 1 to precision 1 checkOverflow", expectedArray, outputArray);
    }

    @Test
    public void testShiftRightNoOverflowOddToEvenLossOfPrecision()
    {
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 3 digit (012C), shifted by 1 (1C), to precision 2
        // (01C)
        inputArray[1] = 0x01;
        inputArray[2] = 0x2C;
        expectedArray[2] = 0x00;
        expectedArray[3] = 0x1C;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 2, inputArray, 1, 3, 1, false, true);
        assertArrayEquals("testShiftRightNoOverflowOddToEvenLossOfPrecision - Test precision of 3 << 1 to precision 2 checkOverflow", expectedArray, outputArray);
    }

    @Test
    public void testShiftRightNoOverflowOddToOddLossOfPrecision()
    {
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Odd to Odd
        // Test precision of 5 digits (00 11 5C) shifted by 2 (00 1C) to
        // precision 3 (1C)
        inputArray[1] = 0x00;
        inputArray[2] = 0x11;
        inputArray[3] = 0x5C;
        expectedArray[2] = 0x1C;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 1, inputArray, 1, 5, 2, false, true);
        assertArrayEquals("testShiftRightNoOverflowOddToOddLossOfPrecision - Test precision of 5 << 2 to precision 3 checkOverflow", expectedArray, outputArray);
    }

    @Test
    public void testShiftRightNoOverflowEvenToOddLossOfPrecision()
    {
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Even to Odd
        // Test precision of 4 digits (00 01 5C) shifted by 2 to precision 3 (50
        // 0C)
        inputArray[1] = 0x00;
        inputArray[2] = 0x10;
        inputArray[3] = 0x5C;
        expectedArray[2] = 0x00;
        expectedArray[3] = 0x1C;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 3, inputArray, 1, 4, 2, false, true);
        assertArrayEquals("testShiftRightNoOverflowEvenToOddLossOfPrecision - Test precision of 4 << 2 to precision 3 checkOverflow", expectedArray, outputArray);
    }

    @Test
    public void testShiftRightNoOverflowEvenToEvenLossOfPrecision()
    {
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Even to Even
        // Test precision of 4 digits (00 01 5C) shifted by 1 to precision 2 (05
        // 0C)
        inputArray[1] = 0x00;
        inputArray[2] = 0x01;
        inputArray[3] = 0x5C;
        expectedArray[2] = 0x00;
        expectedArray[3] = 0x1C;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 2, inputArray, 1, 5, 1, false, true);
        assertArrayEquals("testShiftRightNoOverflowEvenToEvenLossOfPrecision - Test precision of 4 << 1 to precision 2 checkOverflow", expectedArray, outputArray);
    }

    @Test
    public void testShiftRightNoOverflowEvenToEvenSamePrecision()
    {
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Even to Even
        // Test precision of 4 digits (00 01 5C) shifted by 1 to precision 4 (05
        // 0C)
        inputArray[1] = 0x01;
        inputArray[2] = 0x23;
        inputArray[3] = 0x5C;
        expectedArray[2] = 0x00;
        expectedArray[3] = 0x12;
        expectedArray[4] = 0x3C;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 4, inputArray, 1, 4, 1, false, true);
        assertArrayEquals("testShiftRightNoOverflowEvenToEvenSamePrecision - Test precision of 4 << 1 to precision 4 checkOverflow", expectedArray, outputArray);
    }

    @Test(expected = ArithmeticException.class)
    public void testShiftRightOverflowOddToEvenLossOfPrecision()
    {
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 5 digit (0x10 11 2C), shifted by 1 (1C), to precision 2
        // (0xFF FF 00 1C) with an offset of 2 bytes
        inputArray[0] = 0x10;
        inputArray[1] = 0x11;
        inputArray[2] = 0x2C;
        
        expectedArray[2] = 0x00;
        expectedArray[3] = 0x1C;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 2, inputArray, 0, 5, 1, false, true);
        assertArrayEquals("testShiftRightOverflowOddToEvenLossOfPrecision - Test precision of 3 << 1 to precision 2 checkOverflow", expectedArray, outputArray);
    }

    @Test(expected = ArithmeticException.class)
    public void testShiftRightOverflowOddToOddLossOfPrecision()
    {
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Odd to Odd
        // Test precision of 5 digits (00 11 5C) shifted by 2 (00 1C) to
        // precision 3 (1C)
        inputArray[1] = 0x10;
        inputArray[2] = 0x11;
        inputArray[3] = 0x5C;
        expectedArray[2] = 0x1C;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 1, inputArray, 1, 5, 2, false, true);
        assertArrayEquals("testShiftRightOverflowOddToOddLossOfPrecision - Test precision of 5 << 2 to precision 3 checkOverflow", expectedArray, outputArray);
    }

    @Test(expected = ArithmeticException.class)
    public void testShiftRightOverflowEvenToOddLossOfPrecision()
    {
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Even to Odd
        // Test precision of 4 digits (00 01 5C) shifted by 2 to precision 3 (50
        // 0C)
        inputArray[1] = 0x01;
        inputArray[2] = 0x10;
        inputArray[3] = 0x5C;
        expectedArray[2] = 0x1C;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 1, inputArray, 1, 4, 2, false, true);
        assertArrayEquals("testShiftRightOverflowEvenToOddLossOfPrecision - Test precision of 4 << 2 to precision 3 checkOverflow", expectedArray, outputArray);
    }

    @Test(expected = ArithmeticException.class)
    public void testShiftRightOverflowEvenToEvenLossOfPrecision()
    {
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Even to Even
        // Test precision of 4 digits (00 01 5C) shifted by 1 to precision 2 (05
        // 0C)
        inputArray[1] = 0x02;
        inputArray[2] = 0x01;
        inputArray[3] = 0x5C;
        expectedArray[2] = 0x00;
        expectedArray[3] = 0x1C;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 2, inputArray, 1, 5, 1, false, true);
        assertArrayEquals("testShiftRightOverflowEvenToEvenLossOfPrecision - Test precision of 4 << 1 to precision 2 checkOverflow", expectedArray, outputArray);
    }

    @Test(expected = ArithmeticException.class)
    public void testShiftRightOverflowEvenToEvenSamePrecision()
    {
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Even to Even
        // Test precision of 6 digits (00 01 5C) shifted by 1 to precision 6 (05
        // 0C)
        inputArray[1] = 0x01;
        inputArray[2] = 0x23;
        inputArray[3] = 0x50;
        inputArray[4] = 0x1C;
        expectedArray[2] = 0x00;
        expectedArray[3] = 0x12;
        expectedArray[4] = 0x35;
        expectedArray[5] = 0x0C;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 4, inputArray, 1, 6, 1, false, true);
        assertArrayEquals("testShiftRightOverflowEvenToEvenSamePrecision - Test precision of 4 << 1 to precision 4 checkOverflow", expectedArray, outputArray);
    }

    @Test
    public void testShiftLeftNoOverflow()
    {
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Odd to Even
        // Test precision of 3 digit (002C), shifted by 1, to precision 2 (20C)
        inputArray[1] = 0x00;
        inputArray[2] = 0x2C;
        expectedArray[2] = 0x02;
        expectedArray[3] = 0x0C;

        PackedDecimal.shiftLeftPackedDecimal(outputArray, 2, 2, inputArray, 1, 3, 1, true);
        assertArrayEquals("testShiftLeftNoOverflow - Test precision of 3 << 1 to precision 2 checkOverflow", expectedArray, outputArray);

        // Odd to Odd
        // Test precision of 5 digits (00 00 5C) shifted by 2 to precision 3 (50
        // 0C)
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        inputArray[1] = 0x00;
        inputArray[2] = 0x00;
        inputArray[3] = 0x5C;
        expectedArray[2] = 0x50;
        expectedArray[3] = 0x0C;

        PackedDecimal.shiftLeftPackedDecimal(outputArray, 2, 3, inputArray, 1, 5, 2, true);
        assertArrayEquals("testShiftLeftNoOverflow - Test precision of 5 << 2 to precision 3 checkOverflow", expectedArray, outputArray);

        // Even to Odd
        // Test precision of 4 digits (00 00 5C) shifted by 2 to precision 3 (50
        // 0C)
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        inputArray[1] = 0x00;
        inputArray[2] = 0x00;
        inputArray[3] = 0x5C;
        expectedArray[2] = 0x50;
        expectedArray[3] = 0x0C;

        PackedDecimal.shiftLeftPackedDecimal(outputArray, 2, 3, inputArray, 1, 4, 2, true);
        assertArrayEquals("testShiftLeftNoOverflow - Test precision of 4 << 2 to precision 3 checkOverflow", expectedArray, outputArray);

        // Even to Even
        // Test precision of 4 digits (00 00 5C) shifted by 1 to precision 2 (05
        // 0C)
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        inputArray[1] = 0x00;
        inputArray[2] = 0x00;
        inputArray[3] = 0x5C;
        expectedArray[2] = 0x05;
        expectedArray[3] = 0x0C;

        PackedDecimal.shiftLeftPackedDecimal(outputArray, 2, 2, inputArray, 1, 4, 1, true);
        assertArrayEquals("testShiftLeftNoOverflow - Test precision of 4 << 1 to precision 2 checkOverflow", expectedArray, outputArray);

        // Even to Even - Same Precision
        // Test precision of 4 digits (00 23 5C) shifted by 1 to precision 4 (02
        // 35 0C)
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        inputArray[1] = 0x00;
        inputArray[2] = 0x23;
        inputArray[3] = 0x5C;
        expectedArray[2] = 0x02;
        expectedArray[3] = 0x35;
        expectedArray[4] = 0x0C;

        PackedDecimal.shiftLeftPackedDecimal(outputArray, 2, 4, inputArray, 1, 4, 1, true);
        assertArrayEquals("testShiftLeftNoOverflow - Test precision of 4 << 1 to precision 4 checkOverflow", expectedArray, outputArray);
    }

    @Test(expected = ArithmeticException.class)
    public void testShiftLeftOverflowOddToEvenLossOfPrecision()
    {
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Test precision of 3 digit (012C), shifted by 1 (120C), to precision 2
        // (20C)
        inputArray[1] = 0x01;
        inputArray[2] = 0x2C;
        expectedArray[2] = 0x02;
        expectedArray[3] = 0x0C;

        PackedDecimal.shiftLeftPackedDecimal(outputArray, 2, 2, inputArray, 1, 3, 1, true);
        assertArrayEquals("testShiftLeftOverflowOddToEvenLossOfPrecision - Test precision of 3 << 1 to precision 2 checkOverflow", expectedArray, outputArray);
    }

    @Test(expected = ArithmeticException.class)
    public void testShiftLeftOverflowOddToOddLossOfPrecision()
    {
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Odd to Odd
        // Test precision of 5 digits (00 01 5C) shifted by 2 to precision 3 (50
        // 0C)
        inputArray[1] = 0x00;
        inputArray[2] = 0x01;
        inputArray[3] = 0x5C;
        expectedArray[2] = 0x50;
        expectedArray[3] = 0x0C;

        PackedDecimal.shiftLeftPackedDecimal(outputArray, 2, 3, inputArray, 1, 5, 2, true);
        assertArrayEquals("testShiftLeftOverflowOddToOddLossOfPrecision - Test precision of 5 << 2 to precision 3 checkOverflow", expectedArray, outputArray);
    }

    @Test(expected = ArithmeticException.class)
    public void testShiftLeftOverflowEvenToOddLossOfPrecision()
    {
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Even to Odd
        // Test precision of 4 digits (00 01 5C) shifted by 2 to precision 3 (50
        // 0C)
        inputArray[1] = 0x00;
        inputArray[2] = 0x01;
        inputArray[3] = 0x5C;
        expectedArray[2] = 0x50;
        expectedArray[3] = 0x0C;

        PackedDecimal.shiftLeftPackedDecimal(outputArray, 2, 3, inputArray, 1, 4, 2, true);
        assertArrayEquals("testShiftLeftOverflowEvenToOddLossOfPrecision - Test precision of 4 << 2 to precision 3 checkOverflow", expectedArray, outputArray);
    }

    @Test(expected = ArithmeticException.class)
    public void testShiftLeftOverflowEvenToEvenLossOfPrecision()
    {
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Even to Even
        // Test precision of 4 digits (00 01 5C) shifted by 1 to precision 2 (05
        // 0C)
        inputArray[1] = 0x00;
        inputArray[2] = 0x01;
        inputArray[3] = 0x5C;
        expectedArray[2] = 0x05;
        expectedArray[3] = 0x0C;

        PackedDecimal.shiftLeftPackedDecimal(outputArray, 2, 2, inputArray, 1, 5, 1, true);
        assertArrayEquals("testShiftLeftOverflowEvenToEvenLossOfPrecision - Test precision of 4 << 1 to precision 2 checkOverflow", expectedArray, outputArray);
    }

    @Test(expected = ArithmeticException.class)
    public void testShiftLeftOverflowEvenToEvenSamePrecision()
    {
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Even to Even
        // Test precision of 4 digits (00 01 5C) shifted by 1 to precision 4 (05
        // 0C)
        inputArray[1] = 0x01;
        inputArray[2] = 0x23;
        inputArray[3] = 0x5C;
        expectedArray[2] = 0x02;
        expectedArray[3] = 0x35;
        expectedArray[4] = 0x0C;

        PackedDecimal.shiftLeftPackedDecimal(outputArray, 2, 4, inputArray, 1, 4, 1, true);
        assertArrayEquals("testShiftLeftOverflowEvenToEvenSamePrecision - Test precision of 4 << 1 to precision 4 checkOverflow", expectedArray, outputArray);
    }

    @Test(expected = ArithmeticException.class)
    public void testShiftLeftOverflowEvenToEvenSamePrecisionTopNibbleIgnored()
    {
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Even to Even
        // Test precision of 4 digits (00 01 5C) shifted by 1 to precision 4 (05
        // 0C)
        inputArray[0] = 0x00;
        inputArray[1] = 0x23;
        inputArray[2] = 0x4C;
        expectedArray[0] = 0x03;
        expectedArray[1] = 0x40;
        expectedArray[2] = 0x0C;

        PackedDecimal.shiftLeftPackedDecimal(outputArray, 0, 4, inputArray, 0, 4, 2, true);
        assertArrayEquals("testShiftLeftOverflowEvenToEvenSamePrecisionTopNibbleIgnored - Test precision of 4 << 1 to precision 4 checkOverflow", expectedArray, outputArray);
    }

    @Test(expected = ArithmeticException.class)
    public void testShiftLeftOverflowEvenToEvenSamePrecisionTopNibbleGarbage()
    {
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        // Even to Even
        // Test precision of 4 digits (00 01 5C) shifted by 1 to precision 4 (05
        // 0C)
        inputArray[0] = 0x30;
        inputArray[1] = 0x23;
        inputArray[2] = 0x4C;
        expectedArray[0] = 0x03;
        expectedArray[1] = 0x40;
        expectedArray[2] = 0x0C;

        PackedDecimal.shiftLeftPackedDecimal(outputArray, 0, 4, inputArray, 0, 4, 2, true);
        assertArrayEquals("testShiftLeftOverflowEvenToEvenSamePrecisionTopNibbleGarbage - Test precision of 4 << 1 to precision 4 checkOverflow", expectedArray, outputArray);
    }

    @Test
    public void testRightShiftTopNibbleGarbage()
    {
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);

        inputArray[0] = (byte) 0xB1;
        inputArray[1] = 0x23;
        inputArray[2] = 0x4C;
        expectedArray[0] = (byte) 0x00;
        expectedArray[1] = 0x12;
        expectedArray[2] = 0x3C;

        PackedDecimal.shiftRightPackedDecimal(outputArray, 0, 4, inputArray, 0, 4, 1, false, false);
        assertArrayEquals(expectedArray, outputArray);
    }

    @Test
    public void testZeroShifts1()
    {
        /* Both expectedArray and expectedArray2 are needed because
         * there was a behavioral change for VSRP instruction on z15
         * and up. On z15 and up, if the result following the shift
         * is 0, it is treated as positive. However, on prior
         * hardware the 0 would retain the sign of the input packed
         * decimal. For example: doing a left shift by 1 on 5D (-5)
         * would result in 0D (-0) on z14; and 0C (+0) on z15 and above.
         */
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];
        byte[] expectedArray2 = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);
        Arrays.fill(expectedArray2, (byte) 0xff);

        // Test precision of 1 digit, no shift
        inputArray[1] = 0x00;
        inputArray[2] = 0x0D;
        expectedArray[2] = 0x00;
        expectedArray[3] = 0x0D;
        expectedArray2[2] = 0x00;
        expectedArray2[3] = 0x0C;
        
        PackedDecimal.shiftLeftPackedDecimal(outputArray, 2, 2, inputArray, 1, 2, 1, false);
        boolean compResult = Arrays.equals(outputArray, expectedArray) ||  Arrays.equals(outputArray, expectedArray2);
        assertTrue("testZeroShifts1 - Test precision of 1 << 0 to precision 1", compResult);
    }

    @Test
    public void testZeroShifts2()
    {
        /* Both expectedArray and expectedArray2 are needed because
         * there was a behavioral change for VSRP instruction on z15
         * and up. On z15 and up, if the result following the shift
         * is 0, it is treated as positive. However, on prior
         * hardware the 0 would retain the sign of the input packed
         * decimal. For example: doing a left shift by 1 on 5D (-5)
         * would result in 0D (-0) on z14; and 0C (+0) on z15 and above.
         */
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];
        byte[] expectedArray2 = new byte[10];
        
        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);
        Arrays.fill(expectedArray2, (byte) 0xff);
        
        // Test precision of 1 digit, shifted by 1
        inputArray[1] = 0x5D;
        expectedArray[2] = 0x0D;
        expectedArray2[2] = 0x0C;
        PackedDecimal.shiftLeftPackedDecimal(outputArray, 2, 1, inputArray, 1, 1, 1, false);
        
        boolean compResult = Arrays.equals(outputArray, expectedArray) ||  Arrays.equals(outputArray, expectedArray2);
        assertTrue("testZeroShifts2 - Test precision of 1 << 1 to precision 1", compResult);
    }

    @Test
    public void testZeroShifts3()
    {
        /* Both expectedArray and expectedArray2 are needed because
         * there was a behavioral change for VSRP instruction on z15
         * and up. On z15 and up, if the result following the shift
         * is 0, it is treated as positive. However, on prior
         * hardware the 0 would retain the sign of the input packed
         * decimal. For example: doing a left shift by 1 on 5D (-5)
         * would result in 0D (-0) on z14; and 0C (+0) on z15 and above.
         */
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];
        byte[] expectedArray2 = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);
        Arrays.fill(expectedArray2, (byte) 0xff);

        // Test precision of 1 digit, no shift
        inputArray[1] = 0x00;
        inputArray[2] = 0x0D;
        expectedArray[2] = 0x00;
        expectedArray[3] = 0x0D;
        
        expectedArray2[2] = 0x00;
        expectedArray2[3] = 0x0C;
        
        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 2, inputArray, 1, 3, 1, false, false);
        boolean compResult = Arrays.equals(outputArray, expectedArray) ||  Arrays.equals(outputArray, expectedArray2);
        
        assertTrue("testZeroShifts3 - Test precision of 1 << 0 to precision 1", compResult);
    }

    @Test
    public void testZeroShifts4()
    {
        /* Both expectedArray and expectedArray2 are needed because
         * there was a behavioral change for VSRP instruction on z15
         * and up. On z15 and up, if the result following the shift
         * is 0, it is treated as positive. However, on prior
         * hardware the 0 would retain the sign of the input packed
         * decimal. For example: doing a left shift by 1 on 5D (-5)
         * would result in 0D (-0) on z14; and 0C (+0) on z15 and above.
         */
        byte[] inputArray = new byte[10];
        byte[] outputArray = new byte[10];
        byte[] expectedArray = new byte[10];
        byte[] expectedArray2 = new byte[10];

        // Fill with garbage characters
        Arrays.fill(inputArray, (byte) 0xff);
        Arrays.fill(outputArray, (byte) 0xff);
        Arrays.fill(expectedArray, (byte) 0xff);
        Arrays.fill(expectedArray2, (byte) 0xff);

        // Test precision of 1 digit, shifted by 1
        inputArray[1] = 0x5D;
        expectedArray[2] = 0x0D;
        expectedArray2[2] = 0x0C;
        
        PackedDecimal.shiftRightPackedDecimal(outputArray, 2, 1, inputArray, 1, 1, 1, false, false);
        
        boolean compResult = Arrays.equals(outputArray, expectedArray) ||  Arrays.equals(outputArray, expectedArray2);
        assertTrue("testZeroShifts4 - Test precision of 1 << 1 to precision 1", compResult);
    }
}
