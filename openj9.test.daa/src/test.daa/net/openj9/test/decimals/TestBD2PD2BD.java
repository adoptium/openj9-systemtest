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

package net.openj9.test.decimals;

import org.junit.Test;

import com.ibm.dataaccess.*;

import net.openj9.test.Utils;

import java.util.Arrays;
import java.util.Random;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;

/**
 * This class is intended to hold tests for com.ibm.dataaccess.DecimalData class
 * 
 * It specifically holds tests working around BigDecimal to packed data
 * (bytearray) bidirectional conversion validity
 */
public class TestBD2PD2BD
{
    static Random randomGen;

    static int ARRAY_SIZE = 64;

    static final int DOUBLESIZE = 8;
    static final int BYTESIZE = 8;

    static final long MANTISSA_MASK = 0x000FFFFFFFFFFFL;

    /* Attempting to increase the randomness */
    static int randomSeed = (int) (System.currentTimeMillis() % 160001);

    static final int numOfIters = 100;

    byte[] pdArray = new byte[ARRAY_SIZE];
    byte[] copy = new byte[ARRAY_SIZE];

    /* Static initializer (class wide) */
    static
    {
        randomGen = new Random(randomSeed);
    };

    static int getRandomInt()
    {
        return randomGen.nextInt(Integer.MAX_VALUE);
    }

    static int getRandomInt(int limit)
    {
        return randomGen.nextInt(limit);
    }

    public static long getRandomLong()
    {
        return (long) (randomGen.nextDouble() * 9223372036854775807L);
    }

    @Before
    public void setUp() throws Exception
    {

    }

    @After
    public void tearDown() throws Exception
    {
    }

    /**
     * test purpose : bidirectional conversion java native->packed->java native
     * using int and BigDecimal using pseudo random values
     * 
     * tested method : DecimalData.convertBigDecimalToPackedDecimal(BigDecimal,
     * byte[], int, int, boolean); tested method :
     * DecimalData.convertLongToPackedDecimal(int, byte[], int, int, boolean);
     * tested method : DecimalData.convertPackedDecimalToBigDecimal(byte[], int,
     * int, int, boolean); tested method :
     * DecimalData.convertPackedDecimalToInteger(byte[], int, int, int,
     * boolean);
     * 
     * init : gets a random POJO int value randomly, create a BigDecimal object
     * holding this value
     * 
     * test pass condition 1 : following conversion of int and BigDecimal in 2
     * PackedDecimal bytearrays, bytearrays holding the values must be equals to
     * succeed 
     * 
     * test pass condition 2 : following conversion of byteArrays to
     * long and BigDecimal , int values of both must be equals to succeed
     * 
     * test limits : int and BigDecimal to packed data conversion are tested one
     * against the other but they could both do the same error
     * 
     * test extensibility : check equality between original values and
     * bidirectionally converted values
     */
    @Test
    public void testSimpleInt()
    {
        for (int i = 0; i < numOfIters; i++)
        {
            testInt();
        }
    }

    /*
     * subroutine for testSimpleInt() test
     */
    private void testInt()
    {
        int intValue = getRandomInt();
        int length = String.valueOf(intValue).length();
        BigDecimal value = new BigDecimal(intValue);
        
        DecimalData.convertBigDecimalToPackedDecimal(value, pdArray, 0, length, true);
        DecimalData.convertIntegerToPackedDecimal(intValue, copy, 0, length, true);
        try
        {
            assertArrayEquals(copy, pdArray);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(String.format("BD2PD doesn't match I2PD, intValue: %d", intValue), copy, pdArray);
        }

        BigDecimal result = DecimalData.convertPackedDecimalToBigDecimal(pdArray, 0, length, 0, true);
        int resultInt = DecimalData.convertPackedDecimalToInteger(copy, 0, length, true);
        
        try
        {
            assertEquals(resultInt, result.intValue());
        }

        catch (AssertionError e)
        {
            assertEquals("Error", resultInt, result.intValue());
        }
    }

    /**
     * test purpose : bidirectional conversion java native->packed->java native
     * using long and BigDecimal using pseudo random values
     * 
     * tested method : DecimalData.convertBigDecimalToPackedDecimal(BigDecimal,
     * byte[], int, int, boolean); tested method :
     * DecimalData.convertLongToPackedDecimal(long, byte[], int, int, boolean);
     * tested method : DecimalData.convertPackedDecimalToBigDecimal(byte[], int,
     * int, int, boolean); tested method :
     * DecimalData.convertPackedDecimalToInteger(byte[], int, int, int,
     * boolean);
     * 
     * init : gets a POJO long value randomly, create a BigDecimal object
     * holding this value
     * 
     * test pass condition 1 : following conversion of long and BigDecimal in 2
     * PackedDecimal bytearrays, bytearrays holding the values must be equals to
     * succeed 
     * 
     * test pass condition 2 : following conversion of byteArrays to
     * long and BigDecimal , long values of both must be equals to succeed
     * 
     * test limits : long and BigDecimal to packed data conversion are tested
     * one against the other but they could both do the same error
     * 
     * test extensibility : check equality between original values and
     * bidirectionally converted values force test at max and min bounds
     */
    @Test
    public void testSimpleLongs()
    {
        for (int i = 0; i < numOfIters; i++)
            testLong();
    }

    /*
     * subroutine for testSimpleLongs() test
     */
    private void testLong()
    {
        long longValue = getRandomLong();
        int length = String.valueOf(longValue).length();
        BigDecimal value = new BigDecimal(longValue);
        
        DecimalData.convertBigDecimalToPackedDecimal(value, pdArray, 0, length, true);
        DecimalData.convertLongToPackedDecimal(longValue, copy, 0, length, true);

        try
        {
            assertArrayEquals(copy, pdArray);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(String.format("BD2PD doesn't match L2PD, longValue: " + longValue), copy, pdArray);
        }

        BigDecimal result = DecimalData.convertPackedDecimalToBigDecimal(pdArray, 0, length, 0, true);
        long resultLong = DecimalData.convertPackedDecimalToLong(copy, 0, length, true);
        
        try
        {
            assertEquals(resultLong, result.longValue());
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("PD2BD doesn't match PD2L, longValue: %d, resultLong %d, result.longValue() %d.", longValue, resultLong, result.longValue()), resultLong, result.longValue());
        }
    }

    /**
     * test purpose : bidirectional conversion java-native BigDecimal -> packed
     * decimal -> java native BigDecimal using pseudo random values
     * 
     * tested method : DecimalData.convertBigDecimalToPackedDecimal(BigDecimal,
     * byte[], int, int, boolean); tested method :
     * DecimalData.convertPackedDecimalToBigDecimal(byte[], int, int, int,
     * boolean);
     * 
     * init : get a BigDecimal object randomly with an arbitrary 17 digit long
     * integer part scale it to a random value in [0,6] using downrounding
     * arbitrarily
     * 
     * test pass condition 1 : following conversion of a BigDecimal in a packed
     * decimal, then reconverting packed decimal in another Bigdecimal object,
     * initial and bidirectionally converted values must be equals
     * 
     * test limits : doesn't test the exception throwing in case of a bytearray
     * too short to hold the exact value
     * 
     * test extensibility : check exception throwing for each exception case
     */
    @Test
    public void testDFPPath()
    {
        for (int i = 0; i < numOfIters; i++)
            testDFP();
    }

    /*
     * subroutine for testDFPPath() test
     */
    private void testDFP()
    {
        BigDecimal value = generateRandomBigDecimal("100000000000000000");// 10e17
        int scale = getRandomInt(7);// test with random scaling too?
        value = value.setScale(scale, BigDecimal.ROUND_DOWN);// test with other
                                                             // rounding mode?
        int length = String.valueOf(value.unscaledValue()).length();
        DecimalData.convertBigDecimalToPackedDecimal(value, pdArray, 0, length, true);
        BigDecimal result = DecimalData.convertPackedDecimalToBigDecimal(pdArray, 0, length, scale, true);
        int IsEqual = value.compareTo(result);
        
        try
        {
            assertEquals(0, IsEqual);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("Failure with BD2PD2BD using DFPPath, BD: " + value.toString() + ", result:" + result.toString()), 0, IsEqual);
        }
        
        try
        {
            assertEquals(true, value.toString().equals(result.toString()));
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("Failure with BD2PD2BD using DFPPath, BD: " + value.toString() + ", result:" + result.toString()), true, value.toString().equals(result.toString()));
        }
    }

    /**
     * test purpose : bidirectional conversion java-native BigDecimal -> packed
     * decimal -> java native BigDecimal using pseudo random values
     * 
     * tested method : DecimalData.convertBigDecimalToPackedDecimal(BigDecimal,
     * byte[], int, int, boolean); tested method :
     * DecimalData.convertPackedDecimalToBigDecimal(byte[], int, int, int,
     * boolean);
     * 
     * init : get a BigDecimal object randomly with an arbitrary 17 digit long
     * integer part scale it to a random value in [0,6] using downrounding
     * arbitrarily
     * 
     * test pass condition 1 : following conversion of a BigDecimal in a packed
     * decimal, then reconverting packed decimal in another Bigdecimal object,
     * initial and bidirectionally converted values must be equals
     * 
     * test limits : doesn't test the exception throwing in case of a bytearray
     * too short to hold the exact value
     * 
     * test extensibility : check exception throwing for each exception case
     */
    @Test
    public void testSimpleString()
    {
        for (int i = 0; i < numOfIters; i++)
            testString();

    }

    private void testString()
    {
        BigDecimal value = generateRandomBigDecimal("10000000000000000000000000");// 10e25
        int scale = value.scale();
        int length = value.precision();// String.valueOf(value.unscaledValue()).length();
        DecimalData.convertBigDecimalToPackedDecimal(value, pdArray, 0, length, true);
        BigDecimal result = DecimalData.convertPackedDecimalToBigDecimal(pdArray, 0, length, scale, true);
        int IsEqual = value.compareTo(result);

        try
        {
            assertEquals(0, IsEqual);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("Failure with BD2PD2BD using String, BD: " + value.toString() + " result: " + result.toString()), 0, IsEqual);
        }
        
        try
        {
            assertEquals(true, value.toString().equals(result.toString()));
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("Failure with BD2PD2BD using String, BD: " + value.toString() + " result: " + result.toString()), true, value.toString().equals(result.toString()));
        }
    }

    @Test
    public void testDecimalUsingPrimitivesPath() // small decimal numbers using
                                                 // int and long path
    {
        for (int i = 0; i < numOfIters; i++)
        {
            BigDecimal value = generateRandomBigDecimal("10000000");
            int scale = getRandomInt(10);
            value = value.setScale(scale, BigDecimal.ROUND_DOWN);
            int length = String.valueOf(value.unscaledValue()).length();
            
            DecimalData.convertBigDecimalToPackedDecimal(value, pdArray, 0, length, true);
            BigDecimal result = DecimalData.convertPackedDecimalToBigDecimal(pdArray, 0, length, scale, true);
            
            int IsEqual = value.compareTo(result);

            try
            {
                assertEquals(0, IsEqual);
            }

            catch (AssertionError e)
            {
                assertEquals(String.format("Failure with BD2PD2BD using int with scale, BD: " + value.toString() + ", result: " + result.toString()), 0, IsEqual);
            }
            
            try
            {
                assertEquals(true, value.toString().equals(result.toString()));
            }

            catch (AssertionError e)
            {
                assertEquals(String.format("Failure with BD2PD2BD using int with scale, BD: " + value.toString() + ", result: " + result.toString()), true, value.toString().equals(result.toString()));
            }

            value = generateRandomBigDecimal("100000");
            
            if (value.intValue() == 0)
                value = new BigDecimal(1);
            
            scale = getRandomInt(3);
            value = value.setScale(scale, BigDecimal.ROUND_DOWN);
            length = String.valueOf(value.unscaledValue()).length();
            DecimalData.convertBigDecimalToPackedDecimal(value, pdArray, 0, length, true);
            result = DecimalData.convertPackedDecimalToBigDecimal(pdArray, 0, length, scale, true);
            
            IsEqual = value.compareTo(result);
            
            try
            {
                assertEquals(0, IsEqual);
            }

            catch (AssertionError e)
            {
                assertEquals(String.format("Failure with BD2PD2BD using long with scale, BD: " + value.toString() + ", result: " + result.toString()), 0, IsEqual);
            }
            
            try
            {
                assertEquals(true, value.toString().equals(result.toString()));
            }

            catch (AssertionError e)
            {
                assertEquals(String.format("Failure with BD2PD2BD using long with scale, BD: " + value.toString() + ", result: " + result.toString()), true, value.toString().equals(result.toString()));
            }
        }
    }

    @Test
    public void testDifferentOffset()
    {
        for (int i = 0; i < numOfIters; i++)
        {
            BigDecimal value = generateRandomBigDecimal("100000000");
            int scale = getRandomInt(10);
            value = value.setScale(scale, BigDecimal.ROUND_DOWN);
            
            int length = String.valueOf(value.unscaledValue()).length();
            int offset = getRandomInt(20);
            
            DecimalData.convertBigDecimalToPackedDecimal(value, pdArray, offset, length, true);
            BigDecimal result = DecimalData.convertPackedDecimalToBigDecimal(pdArray, offset, length, scale, true);
            
            int IsEqual = value.compareTo(result);
            
            try
            {
                assertEquals(0, IsEqual);
            }

            catch (AssertionError e)
            {
                assertEquals(String.format("Offset failure, BD: %s, offset: %d, result %s.", value.toString(), offset, result.toString()), 0, IsEqual);
            }
            
            try
            {
                assertEquals(true, value.toString().equals(result.toString()));
            }

            catch (AssertionError e)
            {
                assertEquals(String.format("Offset failure, BD: %s, offset: %d, result %s.", value.toString(), offset, result.toString()), true, value.toString().equals(result.toString()));
            }
        }
    }

    @Test(expected = ArithmeticException.class)
    public void testIncorrectPrecisionBD2PD()
    {
        BigDecimal value = generateRandomBigDecimal("100000000000000000000000000000000000000000000000");
        int scale = getRandomInt(10);
        value = value.setScale(scale, BigDecimal.ROUND_DOWN);
        
        int length = String.valueOf(value.unscaledValue()).length();
        int offset = getRandomInt(20);
        
        DecimalData.convertBigDecimalToPackedDecimal(value, pdArray, offset, length - 1, true);
    }

    @Test
    public void testBigInteger()
    {
        BigDecimal value = generateRandomBigDecimal("10000000000000000000000");
        BigInteger BIvalue = value.toBigInteger();
        
        int length = BIvalue.toString().length();
        
        DecimalData.convertBigIntegerToPackedDecimal(BIvalue, pdArray, 0, length, true);
        BigInteger BIresult = DecimalData.convertPackedDecimalToBigInteger(pdArray, 0, length, true);
        
        int IsEqual = BIvalue.compareTo(BIresult);
        
        try
        {
            assertEquals(0, IsEqual);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("BI Conversion failed BI: %s, Biresult: %s.", BIvalue.toString(), BIresult.toString()), 0, IsEqual);
        }
    }

    @Test
    public void testLowerThanZero()
    {
        BigDecimal value = generateRandomBigDecimal("1");
        
        if ((int) value.unscaledValue().intValue() == 0)
            value = value.add(new BigDecimal("0.1234"));
        
        int scale = value.scale();
        int length = String.valueOf(value.unscaledValue()).length();
        
        DecimalData.convertBigDecimalToPackedDecimal(value, pdArray, 0, length, true);
        BigDecimal result = DecimalData.convertPackedDecimalToBigDecimal(pdArray, 0, length, scale, true);
        
        int IsEqual = value.compareTo(result);
        
        try
        {
            assertEquals(0, IsEqual);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("Failure with BD2PD2BD for value < 0, BD: %s, result %s.", value.toString(), result.toString()), 0, IsEqual);
        }
        
        try
        {
            assertEquals(true, value.toString().equals(result.toString()));
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("Failure with BD2PD2BD for value < 0, BD: %s, result %s.", value.toString(), result.toString()), true, value.toString().equals(result.toString()));
        }
    }

    @Test
    public void testFifteenPrecNoOverflow()
    {
        BigDecimal value = new BigDecimal(Utils.getBigInteger(15));
        DecimalData.convertBigDecimalToPackedDecimal(value, pdArray, 0, 15, false);
        BigDecimal result = DecimalData.convertPackedDecimalToBigDecimal(pdArray, 0, 15, 0, false);
        
        int IsEqual = value.compareTo(result);
        
        try
        {
            assertEquals(0, IsEqual);
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("prec=15/noOverflow failure, BD: %s, result %s.", value.toString(), result.toString()), 0, IsEqual);
        }
        
        try
        {
            assertEquals(true, value.toString().equals(result.toString()));
        }

        catch (AssertionError e)
        {
            assertEquals(String.format("prec=15/noOverflow failure, BD: %s, result %s.", value.toString(), result.toString()), true, value.toString().equals(result.toString()));
        }
    }

    private BigDecimal generateRandomBigDecimal(String range)
    {
        BigDecimal max = new BigDecimal(range + ".0");
        BigDecimal randFromDouble = new BigDecimal(Math.random());
        BigDecimal randomValue = randFromDouble.multiply(max);
        
        int scale = getRandomInt(10);
        
        return randomValue.setScale(scale, BigDecimal.ROUND_DOWN);
    }

    @Test
    public void testBadPDtoBD()
    {
        // Test to see if JIT exception path of convertPackedDecimalToBigDecimal crashes.
        byte[] pdArray = { (byte) 0xFF, 0x12, 0x34, 0x56, 0x78, (byte) 0x9C };
        DecimalData.convertPackedDecimalToBigDecimal(pdArray, 0, 11, 0, false);
    }

    @Test
    public void testPadding()
    {
        byte[] pdArray = { (byte) 0xFF, (byte) 0x00, 0x12, 0x34, 0x56, 0x78, (byte) 0x9c, (byte) 0xFF };
        byte[] output = new byte[8];

        BigDecimal value = new BigDecimal("123456789");

        Arrays.fill(output, (byte) 0xFF);

        DecimalData.convertBigDecimalToPackedDecimal(value, output, 1, 11, true);

        try
        {
            assertArrayEquals(pdArray, output);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("BD2PD padding error.", pdArray, output);
        }
    }
}
