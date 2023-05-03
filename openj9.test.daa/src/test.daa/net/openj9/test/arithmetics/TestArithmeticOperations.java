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

import java.math.BigInteger;
import com.ibm.dataaccess.DecimalData;
import com.ibm.dataaccess.PackedDecimal;

import net.openj9.test.Utils;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.internal.TextListener;

import java.util.Arrays;
import java.util.Random;

public class TestArithmeticOperations 
{
    BigInteger[] SmallPositives;
    BigInteger[] SmallNegatives;
    BigInteger[] LargePositives;
    BigInteger[] LargeNegatives;
    
    static int sampleSize = 20;
    
    static int SmallPositiveMax = 10000;
    static int SmallNegativesMax = 10000;
    static long LargePositivesMin = Long.MAX_VALUE;
    static long LargeNegativesMin = Long.MAX_VALUE;
    
    static long randomSeed = System.currentTimeMillis();
    
    static Random randomGenerator;
    
    static String outputFile;
    
    static 
    {
        outputFile = "expected."+TestArithmeticOperations.class.getSimpleName()+".txt";
        randomGenerator = new Random(randomSeed);
    };
    
    byte [] randomNumberPacked1 = new byte[64];
    byte [] randomNumberPacked2 = new byte[64];
    
    byte [] resultPacked = new byte[64];
    byte [] answerPacked = new byte[64];

    @BeforeClass
    public static void setUp() 
    {
    }
    
    @AfterClass
    public static void tearDown()
    {
    }
    
    public void generateValues()
    {
       SmallPositives = new BigInteger[sampleSize];
       SmallNegatives = new BigInteger[sampleSize];
       LargePositives = new BigInteger[sampleSize];
       LargeNegatives = new BigInteger[sampleSize];
    
       for (int i = 0; i < sampleSize; ++i)
       {
          BigInteger temp;

          while ((temp = new BigInteger(5, randomGenerator)).equals(BigInteger.ZERO))
             ;
          SmallPositives[i] = temp;

          while ((temp = new BigInteger(5, randomGenerator)).equals(BigInteger.ZERO))
             ;
          SmallNegatives[i] = temp.negate();

          while ((temp = new BigInteger(256, randomGenerator)).equals(BigInteger.ZERO))
             ;
          LargePositives[i] = temp;

          while ((temp = new BigInteger(256, randomGenerator)).equals(BigInteger.ZERO))
             ;
          LargeNegatives[i] = temp.negate();
       }
    } 
    
    public void testRepeatedly()
    {
        testAddPackedDecimal();
        testAddPackedDecimal();
        
        testDividePackedDecimal();
        testDividePackedDecimal();
        
        testEqualsPackedDecimal();
        testEqualsPackedDecimal();
        
        testGreaterThanOrEqualsPackedDecimal();
        testGreaterThanOrEqualsPackedDecimal();
        
        testGreaterThanPackedDecimal();
        testGreaterThanPackedDecimal();
        
        testLessThanOrEqualsPackedDecimal();
        testLessThanOrEqualsPackedDecimal();
        
        testLessThanPackedDecimal();
        testLessThanPackedDecimal();
        
        testMultiplyPackedDecimal();
        testMultiplyPackedDecimal();
        
        testNotEqualsPackedDecimal();
        testNotEqualsPackedDecimal();
                
        testRemainderPackedDecimal();
        testRemainderPackedDecimal();
        
        testSubtractPackedDecimal();
        testSubtractPackedDecimal();
    }
    
    @Test
    public void testAddPackedDecimalPerformance()
    {
        byte[] op1 = new byte[5];
        byte[] op2 = new byte[5];
        byte[] res = new byte[5];
        byte[] ref = new byte[5];
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(res, (byte) 0x00);
        
        BigInteger value1 = new BigInteger("1234567");
        BigInteger value2 = new BigInteger("8765432");

        BigInteger resVal = new BigInteger("9999999");
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, 0, 7, true);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, 0, 7, true);
        DecimalData.convertBigIntegerToPackedDecimal(resVal, ref, 0, 7, true);

        PackedDecimal.addPackedDecimal(res, 0, 7, op1, 0, 7, op2, 0, 7, true);
         
		try 
		{
			assertArrayEquals(ref, res);
		}
		
		catch (AssertionError e)
		{
			assertArrayEquals("testAddPackedDecimalPerformance", ref, res);
		}
    }
    
    @Test
    public void testSubtractPackedDecimalPerformance()
    {
        byte[] op1 = new byte[20];
        byte[] op2 = new byte[20];
        byte[] res = new byte[20];
        byte[] ref = new byte[20];
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(res, (byte) 0x00);
        
        BigInteger value1 = new BigInteger("999999999999999");
        BigInteger value2 = new BigInteger("123456787654321");

        BigInteger refInt = new BigInteger("876543212345678");
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, 0, 15, true);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, 0, 15, true);
        DecimalData.convertBigIntegerToPackedDecimal(refInt, ref, 0, 15, true);

        PackedDecimal.subtractPackedDecimal(res, 0, 15, op1, 0, 15, op2, 0, 15, true);
        
        try
        {
            assertArrayEquals(ref, res);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testSubtractPackedDecimalPerformance", ref, res);
        }
    }
    
    @Test
    public void testMultiplyPackedDecimalPerformance()
    {
        byte[] op1 = new byte[20];
        byte[] op2 = new byte[20];
        byte[] res = new byte[20];
        byte[] ref = new byte[20];
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(res, (byte) 0x00);
        
        BigInteger value1 = new BigInteger("12345");
        BigInteger value2 = new BigInteger("12345");

        BigInteger refInt = new BigInteger("152399025");
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, 0, 5, true);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, 0, 5, true);
        DecimalData.convertBigIntegerToPackedDecimal(refInt, ref, 0, 9, true);

        PackedDecimal.multiplyPackedDecimal(res, 0, 9, op1, 0, 5, op2, 0, 5, true);

        try
        {
            assertArrayEquals(ref, res);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testMultiplyPackedDecimalPerformance", ref, res);
        }
    }

    @Test
    public void testDividePackedDecimalPerformance()
    {
   
        byte[] op1 = new byte[20];
        byte[] op2 = new byte[20];
        byte[] res = new byte[20];
        byte[] ref = new byte[20];
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(res, (byte) 0x00);
        
        BigInteger value1 = new BigInteger("152399025");
        BigInteger value2 = new BigInteger("12345");

        BigInteger refInt = new BigInteger("12345");
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, 0, 9, true);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, 0, 5, true);
        DecimalData.convertBigIntegerToPackedDecimal(refInt, ref, 0, 5, true);

        PackedDecimal.dividePackedDecimal(res, 0, 5, op1, 0, 9, op2, 0, 5, true);

        try
        {
            assertArrayEquals(ref, res);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testDividePackedDecimalPerformance", ref, res);
        }
    }
    
    @Test
    public void testRemainderPackedDecimalPerformance()
    {
        byte[] op1 = new byte[20];
        byte[] op2 = new byte[20];
        byte[] res = new byte[20];
        byte[] ref = new byte[20];

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(res, (byte) 0x00);

        BigInteger value1 = new BigInteger("152411369");
        BigInteger value2 = new BigInteger("12345");

        BigInteger refInt = new BigInteger("12344");

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, 0, 9, true);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, 0, 5, true);
        DecimalData.convertBigIntegerToPackedDecimal(refInt, ref, 0, 5, true);

        PackedDecimal.remainderPackedDecimal(res, 0, 5, op1, 0, 9, op2, 0, 5, true);

        try
        {
            assertArrayEquals(ref, res);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testRemainderPackedDecimalPerformance", ref, res);
        }
    }
    
    @Test
    public void testCompareEQPerformance()
    {
        byte[] op1 = new byte[20];
        byte[] op2 = new byte[20];
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        BigInteger value1 = new BigInteger("999999999999999");
        BigInteger value2 = new BigInteger("999999999999999");

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, 0, 15, true);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, 0, 15, true);

        boolean result = PackedDecimal.equalsPackedDecimal(op1, 0, 15, op2, 0, 15);

		try 
		{
			assertTrue(result);
		}
		
		catch (AssertionError e)
		{
			assertTrue("testCompareEQPerformance", result);
		}
    }
    
    @Test
    public void testCompareGTPerformance()
    {
        byte[] op1 = new byte[20];
        byte[] op2 = new byte[20];
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        BigInteger value1 = new BigInteger("999999999999999");
        BigInteger value2 = new BigInteger("999999999999998");

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, 0, 15, true);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, 0, 15, true);

        boolean result = PackedDecimal.greaterThanPackedDecimal(op1, 0, 15, op2, 0, 15);

		try 
		{
			assertTrue(result);
		}
		
		catch (AssertionError e)
		{
			assertTrue("testCompareGTPerformance", result);
		}
    }

    @Test
    public void testCompareGEPerformance()
    {
        byte[] op1 = new byte[20];
        byte[] op2 = new byte[20];
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        BigInteger value1 = new BigInteger("999999999999999");
        BigInteger value2 = new BigInteger("999999999999999");

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, 0, 15, true);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, 0, 15, true);

        boolean result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, 0, 15, op2, 0, 15);

		try 
		{
			assertTrue(result);
		}
		
		catch (AssertionError e)
		{
			assertTrue("testCompareGEPerformance", result);
		}
    }
    
    @Test 
    public void testCompareLTPerformance()
    {
        byte[] op1 = new byte[20];
        byte[] op2 = new byte[20];
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        BigInteger value1 = new BigInteger("999999999999998");
        BigInteger value2 = new BigInteger("999999999999999");

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, 0, 15, true);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, 0, 15, true);

        boolean result = PackedDecimal.lessThanPackedDecimal(op1, 0, 15, op2, 0, 15);

		try 
		{
			assertTrue(result);
		}
		
		catch (AssertionError e)
		{
			assertTrue("testCompareLTPerformance", result);
		}
    }
    
    @Test 
    public void testCompareLEPerformance()
    {
        byte[] op1 = new byte[20];
        byte[] op2 = new byte[20];
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        BigInteger value1 = new BigInteger("999999999999998");
        BigInteger value2 = new BigInteger("999999999999999");

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, 0, 15, true);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, 0, 15, true);

        boolean result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, 0, 15, op2, 0, 15);

		try 
		{
			assertTrue(result);
		}
		
		catch (AssertionError e)
		{
			assertTrue("testCompareLEPerformance", result);
		}
    }
    
    @Test 
    public void testCompareNEPerformance()
    {
        byte[] op1 = new byte[20];
        byte[] op2 = new byte[20];

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        BigInteger value1 = new BigInteger("999999999999998");
        BigInteger value2 = new BigInteger("999999999999999");

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, 0, 15, true);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, 0, 15, true);

        boolean result = PackedDecimal.notEqualsPackedDecimal(op1, 0, 15, op2, 0, 15);

		try 
		{
			assertTrue(result);
		}
		
		catch (AssertionError e)
		{
			assertTrue("testCompareNEPerformance", result);
		}
    }

    @Test
    public void testNotEqualsPackedDecimal()
    {
        generateValues();
        
        final int offset0 = 0;
        final int offset5 = 5;
        final int offset10 = 10;
        final int offset25 = 25;
        
        final int precision5 = 5;
        final int precision16 = 16;
        final int precision256 = 256;
        
        final boolean errorChecking = false;
        
        BigInteger value1;
        BigInteger value2;
        
        boolean result;
        boolean correct;
        
        int randomElement;
        int randomElement2;
        
        byte[] op1 = new byte[64];
        byte[] op2 = new byte[64];
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);
        
        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);
        
        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);
        
        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);
        
        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);
        
        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);
        
        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset5, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset5, precision5, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset5, precision5, op2, offset5, precision5);

        correct = value1.compareTo(value2)!=0;
        
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset5, precision5, op2, offset5, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset10, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset10, precision5, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset10, precision5, op2, offset10, precision5);

        correct = value1.compareTo(value2)!=0;
        
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset10, precision5, op2, offset10, precision5), correct, result);
        }
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset25, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset25, precision5, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset25, precision5, op2, offset25, precision5);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset25, precision5, op2, offset25, precision5), correct, result);
        }
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset5, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset5, precision16, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset5, precision16, op2, offset5, precision16);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset5, precision16, op2, offset5, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset10, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset10, precision16, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset10, precision16, op2, offset10, precision16);

        correct = value1.compareTo(value2)!=0;
        
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset10, precision16, op2, offset10, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset25, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset25, precision16, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset25, precision16, op2, offset25, precision16);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset25, precision16, op2, offset25, precision16), correct, result);
        }
        
        op1 = new byte[300];
        op2 = new byte[300];
        
        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);

        correct = value1.compareTo(value2)!=0;
        
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);

        correct = value1.compareTo(value2)!=0;
        
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);

        correct = value1.compareTo(value2)!=0;
        
        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset5, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset5, precision256, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset5, precision256, op2, offset5, precision256);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset5, precision256, op2, offset5, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset10, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset10, precision256, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset10, precision256, op2, offset10, precision256);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset10, precision256, op2, offset10, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset25, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset25, precision256, errorChecking);
        
        result = PackedDecimal.notEqualsPackedDecimal(op1, offset25, precision256, op2, offset25, precision256);

        correct = value1.compareTo(value2)!=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testNotEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset25, precision256, op2, offset25, precision256), correct, result);
        }

    }

    @Test
    public void testGreaterThanPackedDecimal()
    {
        generateValues();
        
        final int offset0 = 0;
        final int offset5 = 5;
        final int offset10 = 10;
        final int offset25 = 25;
        
        final int precision5 = 5;
        final int precision16 = 16;
        final int precision256 = 256;
        
        final boolean errorChecking = false;
        
        BigInteger value1;
        BigInteger value2;
        
        boolean result;
        boolean correct;
        
        int randomElement;
        int randomElement2;
        
        byte[] op1 = new byte[64];
        byte[] op2 = new byte[64];       

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], 6);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], 6);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset5, 6, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset5, 6, errorChecking);

        result = PackedDecimal.greaterThanPackedDecimal(op2, offset5, 6, op1, offset5, 6);
        System.gc();
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset5, 6, op2, offset5, 6), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset10, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset10, precision5, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset10, precision5, op2, offset10, precision5);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset10, precision5, op2, offset10, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset25, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset25, precision5, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset25, precision5, op2, offset25, precision5);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset25, precision5, op2, offset25, precision5), correct, result);
        }
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset5, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset5, precision16, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset5, precision16, op2, offset5, precision16);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset5, precision16, op2, offset5, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset10, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset10, precision16, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset10, precision16, op2, offset10, precision16);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset10, precision16, op2, offset10, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset25, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset25, precision16, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset25, precision16, op2, offset25, precision16);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset25, precision16, op2, offset25, precision16), correct, result);
        }
        
        op1 = new byte[300];
        op2 = new byte[300];
        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset5, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset5, precision256, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset5, precision256, op2, offset5, precision256);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset5, precision256, op2, offset5, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset10, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset10, precision256, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset10, precision256, op2, offset10, precision256);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset10, precision256, op2, offset10, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset25, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset25, precision256, errorChecking);
        
        result = PackedDecimal.greaterThanPackedDecimal(op1, offset25, precision256, op2, offset25, precision256);
        
        correct = value1.compareTo(value2)>0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset25, precision256, op2, offset25, precision256), correct, result);
        }

    }

    @Test
    public void testGreaterThanOrEqualsPackedDecimal()
    {
        generateValues();
        
        final int offset0 = 0;
        final int offset5 = 5;
        final int offset10 = 10;
        final int offset25 = 25;
        
        final int precision5 = 5;
        final int precision16 = 16;
        final int precision256 = 256;
        
        final boolean errorChecking = false;
        
        BigInteger value1;
        BigInteger value2;
        
        boolean result;
        boolean correct;
        
        int randomElement;
        int randomElement2;
        
        byte[] op1 = new byte[64];
        byte[] op2 = new byte[64];
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset5, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset5, precision5, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset5, precision5, op2, offset5, precision5);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset5, precision5, op2, offset5, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset10, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset10, precision5, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset10, precision5, op2, offset10, precision5);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset10, precision5, op2, offset10, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset25, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset25, precision5, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset25, precision5, op2, offset25, precision5);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset25, precision5, op2, offset25, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset5, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset5, precision16, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset5, precision16, op2, offset5, precision16);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset5, precision16, op2, offset5, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset10, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset10, precision16, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset10, precision16, op2, offset10, precision16);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset10, precision16, op2, offset10, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset25, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset25, precision16, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset25, precision16, op2, offset25, precision16);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset25, precision16, op2, offset25, precision16), correct, result);
        }
        
        op1 = new byte[300];
        op2 = new byte[300];
        
        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset5, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset5, precision256, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset5, precision256, op2, offset5, precision256);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset5, precision256, op2, offset5, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset10, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset10, precision256, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset10, precision256, op2, offset10, precision256);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset10, precision256, op2, offset10, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset25, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset25, precision256, errorChecking);
        
        result = PackedDecimal.greaterThanOrEqualsPackedDecimal(op1, offset25, precision256, op2, offset25, precision256);
        
        correct = value1.compareTo(value2)>=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testGreaterThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset25, precision256, op2, offset25, precision256), correct, result);
        }
    }
    
    @Test
    public void testLessThanOrEqualsPackedDecimal()
    {
        generateValues();
        
        final int offset0 = 0;
        final int offset5 = 5;
        final int offset10 = 10;
        final int offset25 = 25;
        
        final int precision5 = 5;
        final int precision16 = 16;
        final int precision256 = 256;
        
        final boolean errorChecking = false;
        
        BigInteger value1;
        BigInteger value2;
        
        boolean result;
        boolean correct;
        
        int randomElement;
        int randomElement2;
        
        byte[] op1 = new byte[64];
        byte[] op2 = new byte[64];
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset5, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset5, precision5, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset5, precision5, op2, offset5, precision5);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset5, precision5, op2, offset5, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset10, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset10, precision5, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset10, precision5, op2, offset10, precision5);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset10, precision5, op2, offset10, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset25, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset25, precision5, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset25, precision5, op2, offset25, precision5);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset25, precision5, op2, offset25, precision5), correct, result);
        }
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset5, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset5, precision16, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset5, precision16, op2, offset5, precision16);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset5, precision16, op2, offset5, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset10, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset10, precision16, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset10, precision16, op2, offset10, precision16);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset10, precision16, op2, offset10, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset25, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset25, precision16, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset25, precision16, op2, offset25, precision16);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset25, precision16, op2, offset25, precision16), correct, result);
        }
        
        op1 = new byte[300];
        op2 = new byte[300];
        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset5, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset5, precision256, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset5, precision256, op2, offset5, precision256);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset5, precision256, op2, offset5, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset10, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset10, precision256, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset10, precision256, op2, offset10, precision256);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset10, precision256, op2, offset10, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset25, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset25, precision256, errorChecking);
        
        result = PackedDecimal.lessThanOrEqualsPackedDecimal(op1, offset25, precision256, op2, offset25, precision256);
        
        correct = value1.compareTo(value2)<=0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanOrEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset25, precision256, op2, offset25, precision256), correct, result);
        }
    }

    @Test
    public void testLessThanPackedDecimal()
    {
        generateValues();
        
        final int offset0 = 0;
        final int offset5 = 5;
        final int offset10 = 10;
        final int offset25 = 25;
        
        final int precision5 = 5;
        final int precision16 = 16;
        final int precision256 = 256;
        
        final boolean errorChecking = false;
        
        BigInteger value1;
        BigInteger value2;
        
        boolean result;
        boolean correct;
        
        int randomElement;
        int randomElement2;
        
        byte[] op1 = new byte[64];
        byte[] op2 = new byte[64];
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset5, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset5, precision5, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset5, precision5, op2, offset5, precision5);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset5, precision5, op2, offset5, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset10, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset10, precision5, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset10, precision5, op2, offset10, precision5);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset10, precision5, op2, offset10, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset25, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset25, precision5, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset25, precision5, op2, offset25, precision5);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset25, precision5, op2, offset25, precision5), correct, result);
        }
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset5, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset5, precision16, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset5, precision16, op2, offset5, precision16);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset5, precision16, op2, offset5, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset10, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset10, precision16, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset10, precision16, op2, offset10, precision16);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset10, precision16, op2, offset10, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset25, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset25, precision16, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset25, precision16, op2, offset25, precision16);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset25, precision16, op2, offset25, precision16), correct, result);
        }
        
        op1 = new byte[300];
        op2 = new byte[300];
        
        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset5, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset5, precision256, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset5, precision256, op2, offset5, precision256);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset5, precision256, op2, offset5, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset10, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset10, precision256, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset10, precision256, op2, offset10, precision256);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset10, precision256, op2, offset10, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset25, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset25, precision256, errorChecking);
        
        result = PackedDecimal.lessThanPackedDecimal(op1, offset25, precision256, op2, offset25, precision256);
        
        correct = value1.compareTo(value2)<0;

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testLessThanPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset25, precision256, op2, offset25, precision256), correct, result);
        }
    }

    @Test
    public void testSubtractPackedDecimal()
    {
        generateValues();
        
        final int offset0 = 0;
        
        final int precision5 = 5;
        final int precision10 = 10;
        final int precision16 = 16;
        final int precision253 = 253;
        
        final boolean errorChecking = false;
        
        BigInteger value1;
        BigInteger value2;
        
        int randomElement;
        int randomElement2;
        
        BigInteger resultValue;

        byte[] op1 = new byte[64];
        byte[] op2 = new byte[64];
        
        byte[] resultant = new byte[128];
        byte[] correct = new byte[128];
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision5);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);
        
        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision5);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision5);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision5);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision5);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision5);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision5);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }
        
        op1 = new byte[64];
        op2 = new byte[64];
        
        resultant = new byte[512];
        correct = new byte[512];
        
        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision16);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision16);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision16);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision16);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision16);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision16);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision16);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        resultValue = value1.subtract(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.subtractPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testSubtractPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        
    }

    @Test
    public void testRemainderPackedDecimal()
    {
        generateValues();
        
        final int offset0 = 0;
        
        final int precision5 = 5;
        final int precision10 = 10;
        final int precision16 = 16;
        final int precision254 = 254;
        
        final boolean errorChecking = false;
        
        BigInteger value1;
        BigInteger value2;
        
        int randomElement;
        int randomElement2;
        
        BigInteger resultValue;

        byte[] op1 = new byte[64];
        byte[] op2 = new byte[64];
        
        byte[] resultant = new byte[128];
        byte[] correct = new byte[128];
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision5);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision5);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision5);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision5);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision5);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision5);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision5);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision16);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision16);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision16);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision16);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision16);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision16);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        resultValue = value1.remainder(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.remainderPackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testRemainderPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }
    }

    @Test
    public void testDividePackedDecimal()
    {
        generateValues();
        
        final int offset0 = 0;
        
        final int precision5 = 5;
        final int precision10 = 10;
        final int precision16 = 16;
        final int precision254 = 254;
        
        final boolean errorChecking = false;
        
        BigInteger value1;
        BigInteger value2;
        
        int randomElement;
        int randomElement2;
        
        BigInteger resultValue;

        byte[] op1 = new byte[64];
        byte[] op2 = new byte[64];
        
        byte[] resultant = new byte[128];
        byte[] correct = new byte[128];

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision5);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision5);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision5);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision5);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("Normal", "testDividePackedDecimal", value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision5);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision5);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision5);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);
        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision10, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision10, op1, offset0, precision10, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }
        
        op1 = new byte[64];
        op2 = new byte[64];
        
        resultant = new byte[512];
        correct = new byte[512];
        
        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision16);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision16);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision16);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision16);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision16);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision16);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision16);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        resultValue = value1.divide(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.dividePackedDecimal(resultant, offset0, precision254, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision254, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision254);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testDividePackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision254, resultValue, correct, offset0, precision254, errorChecking), correct, resultant);
        }
    }

    @Test
    public void testMultiplyPackedDecimal()
    {
        generateValues();
        
        final int offset0 = 0;
        
        final int precision5 = 5;
        final int precision10 = 10;
        final int precision16 = 16;
        final int precision256 = 256;
        final int precision512 = 512;
        
        final boolean errorChecking = false;
        
        BigInteger value1;
        BigInteger value2;
        
        int randomElement;
        int randomElement2;
        
        BigInteger resultValue;

        byte[] op1 = new byte[64];
        byte[] op2 = new byte[64];
        
        byte[] resultant = new byte[128];
        byte[] correct = new byte[128];

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision5);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision5);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision5);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision5);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision5);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision5);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision5);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision10);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }
        
        op1 = new byte[64];
        op2 = new byte[64];
        
        resultant = new byte[512];
        correct = new byte[512];
        
        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision16);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision256, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision256, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision256);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision256, resultValue, correct, offset0, precision256, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision16);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision256, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision256, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision256);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision256, resultValue, correct, offset0, precision256, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision16);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision256, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision256, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision256);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision256, resultValue, correct, offset0, precision256, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision16);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision256, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision256, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision256);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision256, resultValue, correct, offset0, precision256, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision256, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision256, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision256);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision256, resultValue, correct, offset0, precision256, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision16);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision256, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision256, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision256);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision256, resultValue, correct, offset0, precision256, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision256, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision256, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision256);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision256, resultValue, correct, offset0, precision256, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision16);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision256, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision256, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision256);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision256, resultValue, correct, offset0, precision256, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision256, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision256, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision256);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision256, resultValue, correct, offset0, precision256, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision256, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision256, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision256);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision256, resultValue, correct, offset0, precision256, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision16);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision256, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision256, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision256);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision256, resultValue, correct, offset0, precision256, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision256, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision256, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision256);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision256, resultValue, correct, offset0, precision256, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision256, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision256, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision256);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision256, resultValue, correct, offset0, precision256, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision256, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision256, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision256);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision256, resultValue, correct, offset0, precision256, errorChecking), correct, resultant);
        }
        
        op1 = new byte[300];
        op2 = new byte[300];
        
        resultant = new byte[512];
        correct = new byte[512];
        
        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision256);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision512, op1, offset0, precision256, op2, offset0, precision256, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision512, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision512);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256, resultant, offset0, precision512, resultValue, correct, offset0, precision512, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision256);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision512, op1, offset0, precision256, op2, offset0, precision256, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision512, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision512);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256, resultant, offset0, precision512, resultValue, correct, offset0, precision512, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision256);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision512, op1, offset0, precision256, op2, offset0, precision256, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision512, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision512);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256, resultant, offset0, precision512, resultValue, correct, offset0, precision512, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision256);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision512, op1, offset0, precision256, op2, offset0, precision256, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision512, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision512);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256, resultant, offset0, precision512, resultValue, correct, offset0, precision512, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision256);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision512, op1, offset0, precision256, op2, offset0, precision256, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision512, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision512);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256, resultant, offset0, precision512, resultValue, correct, offset0, precision512, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision256);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision512, op1, offset0, precision256, op2, offset0, precision256, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision512, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision512);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256, resultant, offset0, precision512, resultValue, correct, offset0, precision512, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision512, op1, offset0, precision256, op2, offset0, precision256, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision512, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision512);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256, resultant, offset0, precision512, resultValue, correct, offset0, precision512, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision256);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision512, op1, offset0, precision256, op2, offset0, precision256, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision512, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision512);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256, resultant, offset0, precision512, resultValue, correct, offset0, precision512, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision256);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision512, op1, offset0, precision256, op2, offset0, precision256, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision512, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision512);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256, resultant, offset0, precision512, resultValue, correct, offset0, precision512, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision512, op1, offset0, precision256, op2, offset0, precision256, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision512, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision512);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256, resultant, offset0, precision512, resultValue, correct, offset0, precision512, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision256);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision512, op1, offset0, precision256, op2, offset0, precision256, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision512, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision512);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256, resultant, offset0, precision512, resultValue, correct, offset0, precision512, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision512, op1, offset0, precision256, op2, offset0, precision256, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision512, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision512);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256, resultant, offset0, precision512, resultValue, correct, offset0, precision512, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision512, op1, offset0, precision256, op2, offset0, precision256, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision512, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision512);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256, resultant, offset0, precision512, resultValue, correct, offset0, precision512, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision256);

        resultValue = value1.multiply(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        PackedDecimal.multiplyPackedDecimal(resultant, offset0, precision512, op1, offset0, precision256, op2, offset0, precision256, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision512, errorChecking);

        if (resultValue.equals(BigInteger.ZERO))
            Utils.changeSignToC(resultant, offset0, precision512);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testMultiplyPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256, resultant, offset0, precision512, resultValue, correct, offset0, precision512, errorChecking), correct, resultant);
        }
    }

    @Test
    public void testAddPackedDecimal()
    {        
        generateValues();
        
        final int offset0 = 0;
        final int offset5 = 5;
        
        final int precision5 = 5;
        final int precision10 = 10;
        final int precision16 = 16;
        final int precision253 = 253;
        
        final boolean errorChecking = false;
        
        BigInteger value1;
        BigInteger value2;
        
        int randomElement;
        int randomElement2;
        
        BigInteger resultValue;
        
        byte[] op1 = new byte[64];
        byte[] op2 = new byte[64];
        
        byte[] resultant = new byte[128];
        byte[] correct = new byte[128];
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision5);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision5);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision5);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision5);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision5);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision5);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision5);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset0, precision10, op1, offset0, precision5, op2, offset0, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision10, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset5, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset5, precision5, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset5, precision10, op1, offset5, precision5, op2, offset5, precision5, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset5, precision10, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset5, precision5, op2, offset5, precision5, resultant, offset5, precision10, resultValue, correct, offset5, precision10, errorChecking), correct, resultant);
        }
        
        op1 = new byte[64];
        op2 = new byte[64];
        
        resultant = new byte[512];
        correct = new byte[512];
        
        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision16);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision16);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision16);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision16);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision16);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision16);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision16);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        Arrays.fill(resultant, (byte) 0x00);
        Arrays.fill(correct, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        resultValue = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        PackedDecimal.addPackedDecimal(resultant, offset0, precision253, op1, offset0, precision16, op2, offset0, precision16, errorChecking);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultValue, correct, offset0, precision253, errorChecking);

        try
        {
            assertArrayEquals(correct, resultant);
        }

        catch (AssertionError e)
        {
            assertArrayEquals(Utils.makeTestNameArithmetic("", "testAddPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16, resultant, offset0, precision253, resultValue, correct, offset0, precision253, errorChecking), correct, resultant);
        }
    }

    @Test
    public void testEqualsPackedDecimal()
    {
        // Here we test all comparison operations on Packed Decimals
        
        generateValues();
        
        final int offset0 = 0;
        final int offset5 = 5;
        final int offset10 = 10;
        final int offset25 = 25;
        
        final int precision5 = 5;
        final int precision16 = 16;
        final int precision256 = 256;
        
        final boolean errorChecking = false;
        
        BigInteger value1;
        BigInteger value2;
        
        boolean result;
        boolean correct;
        
        int randomElement;
        int randomElement2;

        byte[] op1 = new byte[64];
        byte[] op2 = new byte[64];
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);

        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision5, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision5, op2, offset0, precision5);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset5, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset5, precision5, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset5, precision5, op2, offset5, precision5);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset5, precision5, op2, offset5, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset10, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset10, precision5, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset10, precision5, op2, offset10, precision5);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset10, precision5, op2, offset10, precision5), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision5);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision5);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset25, precision5, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset25, precision5, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset25, precision5, op2, offset25, precision5);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset25, precision5, op2, offset25, precision5), correct, result);
        }
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision16, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision16, op2, offset0, precision16);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision16, op2, offset0, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset5, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset5, precision16, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset5, precision16, op2, offset5, precision16);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset5, precision16, op2, offset5, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset10, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset10, precision16, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset10, precision16, op2, offset10, precision16);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset10, precision16, op2, offset10, precision16), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision16);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision16);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset25, precision16, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset25, precision16, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset25, precision16, op2, offset25, precision16);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset25, precision16, op2, offset25, precision16), correct, result);
        }
        
        op1 = new byte[300];
        op2 = new byte[300];
        
        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallPositives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallPositives[randomElement], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallNegatives[randomElement], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargePositives[randomElement], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargeNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargePositives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallNegatives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset0, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset0, precision256, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset0, precision256, op2, offset0, precision256);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset0, precision256, op2, offset0, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(SmallNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset5, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset5, precision256, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset5, precision256, op2, offset5, precision256);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset5, precision256, op2, offset5, precision256), correct, result);
        }

        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(SmallPositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset10, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset10, precision256, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset10, precision256, op2, offset10, precision256);
        
        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset10, precision256, op2, offset10, precision256), correct, result);
        }
        
        Arrays.fill(op1, (byte) 0x00);
        Arrays.fill(op2, (byte) 0x00);

        randomElement = randomGenerator.nextInt(20);
        randomElement2 = randomGenerator.nextInt(20);

        value1 = Utils.toPrecision(LargePositives[randomElement], precision256);
        value2 = Utils.toPrecision(LargeNegatives[randomElement2], precision256);

        DecimalData.convertBigIntegerToPackedDecimal(value1, op1, offset25, precision256, errorChecking);
        DecimalData.convertBigIntegerToPackedDecimal(value2, op2, offset25, precision256, errorChecking);
        
        result = PackedDecimal.equalsPackedDecimal(op1, offset25, precision256, op2, offset25, precision256);

        correct = value1.equals(value2);

        try
        {
            assertEquals(correct, result);
        }

        catch (AssertionError e)
        {
            assertEquals(Utils.makeTestNameEquivalency("", "testEqualsPackedDecimal #" + Thread.currentThread().getStackTrace()[2].getLineNumber(), value1, value2, op1, offset25, precision256, op2, offset25, precision256), correct, result);
        }
    }
            
    @Test
    public void testAddPackedDecimalRandom()
    {
        int randomNumber1;
        int randomNumber2;
        
        int randomNumber1Length;
        int randomNumber2Length;
        
        int result;
        int resultLength;
        int resultAddedPacked;
        
        for (int i = 0; i < 1000; i++)
        {
            randomNumber1 = randomGenerator.nextInt(Integer.MAX_VALUE / 2);
            randomNumber2 = randomGenerator.nextInt(Integer.MAX_VALUE / 2);
            
            result = randomNumber1 + randomNumber2;
            
            randomNumber1Length = String.valueOf(randomNumber1).length();
            randomNumber2Length = String.valueOf(randomNumber2).length();
            
            resultLength = String.valueOf(result).length();

            DecimalData.convertIntegerToPackedDecimal(randomNumber1, randomNumberPacked1, 0, randomNumber1Length, false);
            DecimalData.convertIntegerToPackedDecimal(randomNumber2, randomNumberPacked2, 0, randomNumber2Length, false);
            
            PackedDecimal.addPackedDecimal(resultPacked, 0, resultLength, randomNumberPacked1, 0, randomNumber1Length, randomNumberPacked2, 0, randomNumber2Length, false);
            
            resultAddedPacked = DecimalData.convertPackedDecimalToInteger(resultPacked, 0, resultLength, false);
            
    		try 
    		{
    			assertEquals(result, resultAddedPacked);
    		}
    		
    		catch (AssertionError e)
    		{
    			assertEquals("testAddPackedDecimalRandom", result, resultAddedPacked);
    		}
        }
    }

    @Test
    public void testAddPackedDecimalLeadingZeros()
    {
        int randomNumber1;
        int randomNumber2;
        
        int randomNumber1Length;
        int randomNumber2Length;
        
        int result;
        int resultLength;
        int resultAddPacked;
        
        for (int i = 0; i < 1000; i++)
        {
            randomNumber1 = randomGenerator.nextInt(Integer.MAX_VALUE / 2);
            randomNumber2 = randomGenerator.nextInt(Integer.MAX_VALUE / 2);

            result = randomNumber1 + randomNumber2;
            
            // Increasing precision for same number which means leading zeros
            randomNumber1Length = String.valueOf(randomNumber1).length() + randomGenerator.nextInt(5);
            randomNumber2Length = String.valueOf(randomNumber2).length() + randomGenerator.nextInt(5);
            
            resultLength = String.valueOf(result).length() + randomGenerator.nextInt(5);

            DecimalData.convertIntegerToPackedDecimal(randomNumber1, randomNumberPacked1, 0, randomNumber1Length, false);
            DecimalData.convertIntegerToPackedDecimal(randomNumber2, randomNumberPacked2, 0, randomNumber2Length, false);
            
            PackedDecimal.addPackedDecimal(resultPacked, 0, resultLength, randomNumberPacked1, 0, randomNumber1Length, randomNumberPacked2, 0, randomNumber2Length, false);
            
            resultAddPacked = DecimalData.convertPackedDecimalToInteger(resultPacked, 0, resultLength, false);
            
    		try 
    		{
    			assertEquals(result, resultAddPacked);
    		}
    		
    		catch (AssertionError e)
    		{
    			assertEquals("testAddPackedDecimalLeadingZeros", result, resultAddPacked);
    		}
        }
    }
    
    @Test
    public void testAddPackedDecimalPossibleNegativeNumbers()
    {
        int randomNumber1;
        int randomNumber2;
        
        int randomNumber1Length;
        int randomNumber2Length;
        
        int result;
        int resultLength;
        int resultAddPacked;
        
        for (int i = 0; i < 1000; i++)
        {
            randomNumber1 = randomGenerator.nextInt(Integer.MAX_VALUE / 2);
            randomNumber2 = randomGenerator.nextInt(Integer.MAX_VALUE / 2);
            
            if (randomGenerator.nextBoolean())
                randomNumber1 = -randomNumber1;
            
            if (randomGenerator.nextBoolean())
                randomNumber2 = -randomNumber2;
            
            result = randomNumber1 + randomNumber2;
            
            // Increasing precision for same number which means leading zeros
            randomNumber1Length = String.valueOf(randomNumber1).length() + randomGenerator.nextInt(5);
            randomNumber2Length = String.valueOf(randomNumber2).length() + randomGenerator.nextInt(5);
            
            resultLength = String.valueOf(result).length() + randomGenerator.nextInt(5);

            DecimalData.convertIntegerToPackedDecimal(randomNumber1, randomNumberPacked1, 0, randomNumber1Length, false);
            DecimalData.convertIntegerToPackedDecimal(randomNumber2, randomNumberPacked2, 0, randomNumber2Length, false);
            
            PackedDecimal.addPackedDecimal(resultPacked, 0, resultLength, randomNumberPacked1, 0, randomNumber1Length, randomNumberPacked2, 0, randomNumber2Length, false);
            
            resultAddPacked = DecimalData.convertPackedDecimalToInteger(resultPacked, 0, resultLength, false);
            
            try 
    		{
            	assertEquals(result, resultAddPacked);
    		}
    		
    		catch (AssertionError e)
    		{
    			assertEquals("testAddPackedDecimalPossibleNegativeNumbers", result, resultAddPacked);
    		}
        }
    }

    @Test
    public void testSubtractPackedDecimalRandom()
    {
        int randomNumber1;
        int randomNumber2;

        int randomNumber1Length;
        int randomNumber2Length;
        
        int result;
        int resultLength;
        int resultSubtractedPacked;
        
        for (int i = 0; i < 1000; i++)
        {
            randomNumber1 = randomGenerator.nextInt(Integer.MAX_VALUE / 2);
            randomNumber2 = randomGenerator.nextInt(Integer.MAX_VALUE / 2);
            
            result = randomNumber1 - randomNumber2;
            
            randomNumber1Length = String.valueOf(randomNumber1).length();
            randomNumber2Length = String.valueOf(randomNumber2).length();
            
            resultLength = String.valueOf(result).length();

            DecimalData.convertIntegerToPackedDecimal(randomNumber1, randomNumberPacked1, 0, randomNumber1Length, false);
            DecimalData.convertIntegerToPackedDecimal(randomNumber2, randomNumberPacked2, 0, randomNumber2Length, false);
            
            PackedDecimal.subtractPackedDecimal(resultPacked, 0, resultLength, randomNumberPacked1, 0, randomNumber1Length, randomNumberPacked2, 0, randomNumber2Length, false);
            
            resultSubtractedPacked = DecimalData.convertPackedDecimalToInteger(resultPacked, 0, resultLength, false);
            
            try 
    		{
            	assertEquals(result,resultSubtractedPacked);
    		}
    		
    		catch (AssertionError e)
    		{
    			assertEquals("testSubtractPackedDecimalRandom", result,resultSubtractedPacked);
    		}
        }
    }
    
    @Test
    public void testSubtractPackedDecimalLeadingZeros()
    {
        int randomNumber1;
        int randomNumber2;

        int randomNumber1Length;
        int randomNumber2Length;
        
        int result;
        int resultLength;
        int resultSubtractPacked;
        
        for (int i = 0; i < 1000; i++)
        {
            randomNumber1 = randomGenerator.nextInt(Integer.MAX_VALUE / 2);
            randomNumber2 = randomGenerator.nextInt(Integer.MAX_VALUE / 2);
            
            result = randomNumber1 - randomNumber2;
            
            // Increasing precision for same number which means leading zeros
            randomNumber1Length = String.valueOf(randomNumber1).length() + randomGenerator.nextInt(5);
            randomNumber2Length = String.valueOf(randomNumber2).length() + randomGenerator.nextInt(5);
            
            resultLength = String.valueOf(result).length() + randomGenerator.nextInt(5);

            DecimalData.convertIntegerToPackedDecimal(randomNumber1, randomNumberPacked1, 0, randomNumber1Length, false);
            DecimalData.convertIntegerToPackedDecimal(randomNumber2, randomNumberPacked2, 0, randomNumber2Length, false);
            
            PackedDecimal.subtractPackedDecimal(resultPacked, 0, resultLength, randomNumberPacked1, 0, randomNumber1Length, randomNumberPacked2, 0, randomNumber2Length, false);
            
            resultSubtractPacked = DecimalData.convertPackedDecimalToInteger(resultPacked, 0, resultLength, false);
            
            try 
    		{
            	assertEquals(result,resultSubtractPacked);
    		}
    		
    		catch (AssertionError e)
    		{
    			assertEquals("testSubtractPackedDecimalLeadingZeros", result,resultSubtractPacked);
    		}
        }
    }
    
    @Test
    public void testSubtractPackedDecimalRandomPossibleNegativeNumbers()
    {
        int randomNumber1;
        int randomNumber2;

        int randomNumber1Length;
        int randomNumber2Length;
        
        int result;
        int resultLength;
        int resultSubtractedPacked;
        
        for (int i = 0; i < 1000; i++)
        {
            randomNumber1 = randomGenerator.nextInt(Integer.MAX_VALUE / 2);
            randomNumber2 = randomGenerator.nextInt(Integer.MAX_VALUE / 2);
            
            if (randomGenerator.nextBoolean())
                randomNumber1 = -randomNumber1;
            
            if (randomGenerator.nextBoolean())
                randomNumber2 = -randomNumber2;
            
            result = randomNumber1 - randomNumber2;
            
            randomNumber1Length = String.valueOf(randomNumber1).length();
            randomNumber2Length = String.valueOf(randomNumber2).length();
            
            resultLength = String.valueOf(result).length();

            DecimalData.convertIntegerToPackedDecimal(randomNumber1, randomNumberPacked1, 0, randomNumber1Length, false);
            DecimalData.convertIntegerToPackedDecimal(randomNumber2, randomNumberPacked2, 0, randomNumber2Length, false);
            
            PackedDecimal.subtractPackedDecimal(resultPacked, 0, resultLength, randomNumberPacked1, 0, randomNumber1Length, randomNumberPacked2, 0, randomNumber2Length, false);
            
            resultSubtractedPacked = DecimalData.convertPackedDecimalToInteger(resultPacked, 0, resultLength, false);
            
            try 
    		{
            	assertEquals(result, resultSubtractedPacked);
    		}
    		
    		catch (AssertionError e)
    		{
    			assertEquals("testSubtractPackedDecimalRandomPossibleNegativeNumbers", result, resultSubtractedPacked);
    		}
        }
    }

    @Test
    public void testMultiplyPackedDecimalRandom()
    {
        int randomNumber1;
        int randomNumber2;

        int randomNumber1Length;
        int randomNumber2Length;
        
        int result;
        int resultLength;
        int resultMultipliedPacked;
        
        for (int i = 0; i < 1000; i++)
        {
            randomNumber1 = randomGenerator.nextInt((int) Math.sqrt(Integer.MAX_VALUE));
            randomNumber2 = randomGenerator.nextInt((int) Math.sqrt(Integer.MAX_VALUE));
            
            result = randomNumber1 * randomNumber2;
            
            randomNumber1Length = String.valueOf(randomNumber1).length();
            randomNumber2Length = String.valueOf(randomNumber2).length();
            
            resultLength = String.valueOf(result).length();

            DecimalData.convertIntegerToPackedDecimal(randomNumber1, randomNumberPacked1, 0, randomNumber1Length, false);
            DecimalData.convertIntegerToPackedDecimal(randomNumber2, randomNumberPacked2, 0, randomNumber2Length, false);
            
            PackedDecimal.multiplyPackedDecimal(resultPacked, 0, resultLength, randomNumberPacked1, 0, randomNumber1Length, randomNumberPacked2, 0, randomNumber2Length, false);
            
            resultMultipliedPacked = DecimalData.convertPackedDecimalToInteger(resultPacked, 0, resultLength, false);
            
            try 
    		{
            	assertEquals(result, resultMultipliedPacked);
    		}
    		
    		catch (AssertionError e)
    		{
    			assertEquals("testMultiplyPackedDecimalRandom", result, resultMultipliedPacked);
    		}
        }
    }
    
    @Test
    public void testMultiplyPackedDecimalLeadingZeros()
    { 
        int randomNumber1;
        int randomNumber2;

        int randomNumber1Length;
        int randomNumber2Length;
        
        int result;
        int resultLength;
        int resultMultipliedPacked;
        
        for (int i = 0; i < 1000; i++)
        {
            randomNumber1 = randomGenerator.nextInt((int) Math.sqrt(Integer.MAX_VALUE));
            randomNumber2 = randomGenerator.nextInt((int) Math.sqrt(Integer.MAX_VALUE));
            
            result = randomNumber1 * randomNumber2;
            
            // Increasing precision for same number which means leading zeros
            randomNumber1Length = String.valueOf(randomNumber1).length() + randomGenerator.nextInt(5);
            randomNumber2Length = String.valueOf(randomNumber2).length() + randomGenerator.nextInt(5);
            
            resultLength = String.valueOf(result).length() + randomGenerator.nextInt(5);

            DecimalData.convertIntegerToPackedDecimal(randomNumber1, randomNumberPacked1, 0, randomNumber1Length, false);
            DecimalData.convertIntegerToPackedDecimal(randomNumber2, randomNumberPacked2, 0, randomNumber2Length, false);
            
            PackedDecimal.multiplyPackedDecimal(resultPacked, 0, resultLength, randomNumberPacked1, 0, randomNumber1Length, randomNumberPacked2, 0, randomNumber2Length, false);
            
            resultMultipliedPacked = DecimalData.convertPackedDecimalToInteger(resultPacked, 0, resultLength, false);
            
            try 
    		{
            	assertEquals(result, resultMultipliedPacked);
    		}
    		
    		catch (AssertionError e)
    		{
    			assertEquals("testMultiplyPackedDecimalLeadingZeros", result, resultMultipliedPacked);
    		}
        }
    }
    
    @Test
    public void testDividePackedDecimalRandom()
    {
        int randomNumber1;
        int randomNumber2;

        int randomNumber1Length;
        int randomNumber2Length;
        
        int result;
        int resultLength;
        int resultDividedPacked;
        
        for (int i = 0; i < 1000; i++)
        {
            randomNumber1 = randomGenerator.nextInt(Integer.MAX_VALUE);
            randomNumber2 = randomGenerator.nextInt(Integer.MAX_VALUE) + 1;
            
            result = randomNumber1 / randomNumber2;
            
            randomNumber1Length = String.valueOf(randomNumber1).length();
            randomNumber2Length = String.valueOf(randomNumber2).length();
            
            resultLength = String.valueOf(result).length();

            DecimalData.convertIntegerToPackedDecimal(randomNumber1, randomNumberPacked1, 0, randomNumber1Length, false);
            DecimalData.convertIntegerToPackedDecimal(randomNumber2, randomNumberPacked2, 0, randomNumber2Length, false);
            
            PackedDecimal.dividePackedDecimal(resultPacked, 0, resultLength, randomNumberPacked1, 0, randomNumber1Length, randomNumberPacked2, 0, randomNumber2Length, false);
            
            resultDividedPacked = DecimalData.convertPackedDecimalToInteger(resultPacked, 0, resultLength, false);
            
            try 
    		{
            	assertEquals(result, resultDividedPacked);
    		}
    		
    		catch (AssertionError e)
    		{
    			assertEquals("testDividePackedDecimalRandom", result, resultDividedPacked);
    		}
        }
    }
    
    @Test
    public void testDividePackedDecimalLeadingZeros()
    {
        int randomNumber1;
        int randomNumber2;

        int randomNumber1Length;
        int randomNumber2Length;
        
        int result;
        int resultLength;
        int resultDividePacked;
        
        for (int i = 0; i < 1000; i++)
        {
            randomNumber1 = randomGenerator.nextInt(Integer.MAX_VALUE);
            randomNumber2 = randomGenerator.nextInt(Integer.MAX_VALUE) + 1;
            
            result = randomNumber1 / randomNumber2;
            
            // Increasing precision for same number which means leading zeros
            randomNumber1Length = String.valueOf(randomNumber1).length() + randomGenerator.nextInt(5);
            randomNumber2Length = String.valueOf(randomNumber2).length() + randomGenerator.nextInt(5);
            
            resultLength = String.valueOf(result).length() + randomGenerator.nextInt(5);

            DecimalData.convertIntegerToPackedDecimal(randomNumber1, randomNumberPacked1, 0, randomNumber1Length, false);
            DecimalData.convertIntegerToPackedDecimal(randomNumber2, randomNumberPacked2, 0, randomNumber2Length, false);
            
            PackedDecimal.dividePackedDecimal(resultPacked, 0, resultLength, randomNumberPacked1, 0, randomNumber1Length, randomNumberPacked2, 0, randomNumber2Length, false);
            
            resultDividePacked = DecimalData.convertPackedDecimalToInteger(resultPacked, 0, resultLength, false);
            
            try 
    		{
            	assertEquals(result, resultDividePacked);
    		}
    		
    		catch (AssertionError e)
    		{
    			assertEquals("testDividePackedDecimalLeadingZeros", result, resultDividePacked);
    		}
        }
    }
    
    @Test
    public void testRemainderPackedDecimalRandom()
    {
        int randomNumber1;
        int randomNumber2;

        int randomNumber1Length;
        int randomNumber2Length;
        
        int result;
        int resultLength;
        int resultRemainderPacked;
        
        for (int i = 0; i < 1000; i++)
        {
            randomNumber1 = randomGenerator.nextInt(Integer.MAX_VALUE);
            randomNumber2 = randomGenerator.nextInt(Integer.MAX_VALUE) + 1;
            
            result = randomNumber1 % randomNumber2;
            
            randomNumber1Length = String.valueOf(randomNumber1).length();
            randomNumber2Length = String.valueOf(randomNumber2).length();
            
            resultLength = String.valueOf(result).length();

            DecimalData.convertIntegerToPackedDecimal(randomNumber1, randomNumberPacked1, 0, randomNumber1Length, false);
            DecimalData.convertIntegerToPackedDecimal(randomNumber2, randomNumberPacked2, 0, randomNumber2Length, false);
            
            PackedDecimal.remainderPackedDecimal(resultPacked, 0, resultLength, randomNumberPacked1, 0, randomNumber1Length, randomNumberPacked2, 0, randomNumber2Length, false);
            
            resultRemainderPacked = DecimalData.convertPackedDecimalToInteger(resultPacked, 0, resultLength, false);
            
            try 
    		{
            	assertEquals(result, resultRemainderPacked);
    		}
    		
    		catch (AssertionError e)
    		{
    			assertEquals("testRemainderPackedDecimalRandom", result, resultRemainderPacked);
    		}
        }
    }
    
    @Test
    public void testRemainderPackedDecimalLeadingZeros()
    {
        int randomNumber1;
        int randomNumber2;

        int randomNumber1Length;
        int randomNumber2Length;
        
        int result;
        int resultLength;
        int resultRemainderPacked;
        
        for (int i = 0; i < 1000; i++)
        {
            randomNumber1 = randomGenerator.nextInt(Integer.MAX_VALUE);
            randomNumber2 = randomGenerator.nextInt(Integer.MAX_VALUE) + 1;
            
            result = randomNumber1 % randomNumber2;
            
            // Increasing precision for same number which means leading zeros
            randomNumber1Length = String.valueOf(randomNumber1).length() + randomGenerator.nextInt(5);
            randomNumber2Length = String.valueOf(randomNumber2).length() + randomGenerator.nextInt(5);
            
            resultLength = String.valueOf(result).length() + randomGenerator.nextInt(5);

            DecimalData.convertIntegerToPackedDecimal(randomNumber1, randomNumberPacked1, 0, randomNumber1Length, false);
            DecimalData.convertIntegerToPackedDecimal(randomNumber2, randomNumberPacked2, 0, randomNumber2Length, false);
            
            PackedDecimal.remainderPackedDecimal(resultPacked, 0, resultLength, randomNumberPacked1, 0, randomNumber1Length, randomNumberPacked2, 0, randomNumber2Length, false);
            
            resultRemainderPacked = DecimalData.convertPackedDecimalToInteger(resultPacked, 0, resultLength, false);

            try 
    		{
            	assertEquals(result, resultRemainderPacked);
    		}
    		
    		catch (AssertionError e)
    		{
    			assertEquals("testRemainderPackedDecimalLeadingZeros", result, resultRemainderPacked);
    		}
        }
    }
    
    @Test
    public void testAddPackedByteArrayTooSmall()
    {
        byte [] numberPacked1 = new byte[2];
        byte [] numberPacked2 = new byte[2];
        
        byte [] resultPacked = new byte[2];
        
        boolean catched = false;
        
        int number1 = 999;
        int number2 = 999;
        
        int result = number1 + number2;

        int number1Length = String.valueOf(number1).length();
        int number2Length = String.valueOf(number2).length();
        
        int resultLength = String.valueOf(result).length();

        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.addPackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArrayIndexOutOfBoundsException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testSubtractPackedByteArrayTooSmall()
    {
        byte [] numberPacked1 = new byte[2];
        byte [] numberPacked2 = new byte[2];
        
        byte [] resultPacked = new byte[2];
        
        boolean catched = false;
        
        int number1 = -999;
        int number2 = 999;
        
        int number1Length = 3;
        int number2Length = 3;
        
        int resultLength = 4;

        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.subtractPackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArrayIndexOutOfBoundsException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testMultiplyPackedByteArrayTooSmall()
    {
        byte [] numberPacked1 = new byte[2];
        byte [] numberPacked2 = new byte[2];
        
        byte [] resultPacked = new byte[3];
        
        boolean catched = false;
        
        int number1 = 999;
        int number2 = 999;
        
        // Increasing precision for same number which means leading zeros
        int number1Length = 3;
        int number2Length = 3;
        
        int resultLength = 6;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.multiplyPackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArrayIndexOutOfBoundsException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testDividePackedByteArrayTooSmall()
    {
        byte [] numberPacked1 = new byte[2];
        byte [] numberPacked2 = new byte[1];
        
        byte [] resultPacked = new byte[1];
        
        boolean catched = false;
        
        int number1 = 999;
        int number2 = 1;
        
        // Increasing precision for same number which means leading zeros
        int number1Length = 3;
        int number2Length = 1;
        int resultLength = 3;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try {
            PackedDecimal.dividePackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } catch (ArrayIndexOutOfBoundsException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testRemainderPackedByteArrayTooSmall()
    {
        byte [] numberPacked1 = new byte[2];
        byte [] numberPacked2 = new byte[2];
        
        byte [] resultPacked = new byte[1];
        
        boolean catched = false;
        
        int number1 = 987;
        int number2 = 25;
        
        // Increasing precision for same number which means leading zeros
        int number1Length = 3;
        int number2Length = 2;
        
        int resultLength = 2;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.remainderPackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArrayIndexOutOfBoundsException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testAddPackedPrecsionTooSmallOverflowSetAsTrueEvenToOdd()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        
        boolean catched = false;
        
        int number1 = 999;
        int number2 = 999;
        
        // Increasing precision for same number which means leading zeros
        int number1Length = 3;
        int number2Length = 3;
        
        int resultLength = 3;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.addPackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testAddPackedPrecsionTooSmallOverflowSetAsTrueOddToEvenSameByte()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        
        boolean catched = false;
        
        int number1 = 99;
        int number2 = 99;

        int number1Length = 2;
        int number2Length = 2;
        
        int resultLength = 2;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.addPackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testAddPackedPrecsionTooSmallOverflowSetAsTrueOddToEvenNotSameByte()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        
        boolean catched = false;
        
        int number1 = 9999;
        int number2 = 999;

        int number1Length = 4;
        int number2Length = 3;
        
        int resultLength = 2;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.addPackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testAddPackedPrecsionTooSmallOverflowSetAsTrueOddToOdd()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        
        boolean catched = false;
        
        int number1 = 99;
        int number2 = 99;

        int number1Length = 2;
        int number2Length = 2;
        
        int resultLength = 1;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.addPackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testAddPackedPrecsionTooSmallOverflowSetAsTrueEvenToEven()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        
        boolean catched = false;
        
        int number1 = 999;
        int number2 = 999;

        int number1Length = 3;
        int number2Length = 3;
        int resultLength = 2;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.addPackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testSubtractPrecisionTooSmallOverflowSetAsTrueEvenToOdd()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        
        boolean catched = false;
        
        int number1 = 9999;
        int number2 = 1;
        
        int number1Length = 4;
        int number2Length = 1;
        
        int resultLength = 3;

        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.subtractPackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testSubtractPrecisionTooSmallOverflowSetAsTrueOddToEvenSameByte()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        
        boolean catched = false;
        
        int number1 = 999;
        int number2 = 1;
        
        int number1Length = 3;
        int number2Length = 1;
        
        int resultLength = 2;

        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.subtractPackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testSubtractPrecisionTooSmallOverflowSetAsTrueOddToEvenNotSameByte()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        
        boolean catched = false;
        
        int number1 = 99999;
        int number2 = 1;
        
        int number1Length = 5;
        int number2Length = 1;
        
        int resultLength = 2;

        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.subtractPackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testSubtractPrecisionTooSmallOverflowSetAsTrueEvenToEven()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        
        boolean catched = false;
        
        int number1 = 9999;
        int number2 = 1;
        
        int number1Length = 4;
        int number2Length = 1;
        
        int resultLength = 2;

        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.subtractPackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testSubtractPrecisionTooSmallOverflowSetAsTrueOddToOdd()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        
        boolean catched = false;
        
        int number1 = 99999;
        int number2 = 1;
        
        int number1Length = 5;
        int number2Length = 1;
        
        int resultLength = 3;

        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.subtractPackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testMultiplyPrecisionTooSmallOverflowSetAsTrueEvenToOdd()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        
        boolean catched = false;
        
        int number1 = 100;
        int number2 = 10;
        
        // Increasing precision for same number which means leading zeros
        int number1Length = 3;
        int number2Length = 2;
        
        int resultLength = 3;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.multiplyPackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testMultiplyPrecisionTooSmallOverflowSetAsTrueOddToEvenSameByte()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        
        boolean catched = false;
        
        int number1 = 12;
        int number2 = 10;
        
        // Increasing precision for same number which means leading zeros
        int number1Length = 2;
        int number2Length = 2;
        
        int resultLength = 2;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.multiplyPackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testMultiplyPrecisionTooSmallOverflowSetAsTrueOddToEvenNotSameByte()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        
        boolean catched = false;
        
        int number1 = 1234;
        int number2 = 10;
        
        // Increasing precision for same number which means leading zeros
        int number1Length = 4;
        int number2Length = 2;
        
        int resultLength = 3;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.multiplyPackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testMultiplyPrecisionTooSmallOverflowSetAsTrueOddToOdd()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        
        boolean catched = false;
        
        int number1 = 10;
        int number2 = 10;
        
        // Increasing precision for same number which means leading zeros
        int number1Length = 2;
        int number2Length = 2;
        
        int resultLength = 1;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.multiplyPackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testMultiplyPrecisionTooSmallOverflowSetAsTrueEvenToEven()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        
        boolean catched = false;
        
        int number1 = 100;
        int number2 = 10;
        
        // Increasing precision for same number which means leading zeros
        int number1Length = 3;
        int number2Length = 2;
        
        int resultLength = 2;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.multiplyPackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testDividePrecisionTooSmallOverflowSetAsTrueEvenToOdd()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        
        boolean catched = false;
        
        int number1 = 9999;
        int number2 = 1;
        
        int number1Length = 4;
        int number2Length = 1;
        
        int resultLength = 3;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.dividePackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testDividePrecisionTooSmallOverflowSetAsTrueOddtoEvenSameByte()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        
        boolean catched = false;
        
        int number1 = 999;
        int number2 = 1;
        
        int number1Length = 3;
        int number2Length = 1;
        
        int resultLength = 2;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.dividePackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testDividePrecisionTooSmallOverflowSetAsTrueOddtoEvenNotSameByte()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        
        boolean catched = false;
        
        int number1 = 99999;
        int number2 = 1;
        
        int number1Length = 5;
        int number2Length = 1;
        
        int resultLength = 2;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.dividePackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testDividePrecisionTooSmallOverflowSetAsTrueOddtoOdd()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        
        boolean catched = false;
        
        int number1 = 999;
        int number2 = 1;
        
        int number1Length = 3;
        int number2Length = 1;
        
        int resultLength = 1;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.dividePackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testDividePrecisionTooSmallOverflowSetAsTrueEvenToEven()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        
        boolean catched = false;
        
        int number1 = 10000;
        int number2 = 10;
        
        int number1Length = 5;
        int number2Length = 2;
        
        int resultLength = 2;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.dividePackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testRemainderPrecisionTooSmallOverflowSetAsTrueEvenToOdd()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        
        boolean catched = false;
        
        int number1 = 1000;
        int number2 = 11;

        int number1Length = 4;
        int number2Length = 2;
        
        int resultLength = 1;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.remainderPackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testRemainderPrecisionTooSmallOverflowSetAsTrueOddtoEvenSameByte()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        
        boolean catched = false;
        
        int number1 = 9711;
        int number2 = 113;

        int number1Length = 4;
        int number2Length = 3;
        
        int resultLength = 2;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.remainderPackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testRemainderPrecisionTooSmallOverflowSetAsTrueNotOddtoEvenSameByte()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        boolean catched = false;
        
        int number1 = 345353;
        int number2 = 23524;

        int number1Length = 6;
        int number2Length = 5;
        
        int resultLength = 2;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.remainderPackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testRemainderPrecisionTooSmallOverflowSetAsTrueOddtoOdd()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        
        boolean catched = false;
        
        int number1 = 9711;
        int number2 = 113;

        int number1Length = 4;
        int number2Length = 3;
        
        int resultLength = 1;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.remainderPackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testRemainderPrecisionTooSmallOverflowSetAsTrueEvenToEven()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        
        boolean catched = false;
        
        int number1 = 1233345;
        int number2 = 13345;

        int number1Length = 7;
        int number2Length = 5;
        
        int resultLength = 2;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);

        try 
        {
            PackedDecimal.remainderPackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, true);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail("Did not catch illegal argument.");
    }
    
    @Test
    public void testAddPackedPrecsionTooSmallOverflowSetAsFalseEvenToOdd()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        
        byte [] answer = new byte[10];

        int number1 = 999;
        int number2 = 999;

        int number1Length = 3;
        int number2Length = 3;
        
        int resultLength = 3;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);
        
        PackedDecimal.addPackedDecimal(resultPacked, 1, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, false);
        
        answer[0]=(byte)0x00;
        answer[1]=(byte)0x99;
        answer[2]=(byte)0x8C;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testAddPackedPrecsionTooSmallOverflowSetAsFalseEvenToOdd", answer, resultPacked);
        }
    }
    
    @Test
    public void testAddPackedPrecsionTooSmallOverflowSetAsFalseOddToEvenSameByte()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        
        byte [] answer = new byte[10];
        
        int number1 = 99;
        int number2 = 99;

        int number1Length = 2;
        int number2Length = 2;
        
        int resultLength = 2;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);
        
        PackedDecimal.addPackedDecimal(resultPacked, 1, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, false);

        answer[0]=(byte) 0x00;
        answer[1]=(byte) 0x09;
        answer[2]=(byte) 0x8C;
        answer[3]=(byte) 0x00;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testAddPackedPrecsionTooSmallOverflowSetAsFalseOddToEvenSameByte", answer, resultPacked);
        }
    }
    
    @Test
    public void testAddPackedPrecsionTooSmallOverflowSetAsFalseOddToEvenNotSameByte()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        byte [] answer = new byte[10];
        
        int number1 = 9999;
        int number2 = 999;

        int number1Length = 4;
        int number2Length = 3;
        
        int resultLength = 2;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);
        
        PackedDecimal.addPackedDecimal(resultPacked, 1, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, false);

        answer[0]=(byte) 0x00;
        answer[1]=(byte) 0x09;
        answer[2]=(byte) 0x8C;
        answer[3]=(byte) 0x00;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testAddPackedPrecsionTooSmallOverflowSetAsFalseOddToEvenNotSameByte", answer, resultPacked);
        }
    }
    
    @Test
    public void testAddPackedPrecsionTooSmallOverflowSetAsFalseEvenToEven()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        byte [] answer = new byte[10];
        
        int number1 = 999;
        int number2 = 999;

        int number1Length = 3;
        int number2Length = 3;
        
        int resultLength = 2;

        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);
        
        PackedDecimal.addPackedDecimal(resultPacked, 1, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, false);

        answer[0]=(byte) 0x00;
        answer[1]=(byte) 0x09;
        answer[2]=(byte) 0x8C;
        answer[3]=(byte) 0x00;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testAddPackedPrecsionTooSmallOverflowSetAsFalseEvenToEven", answer, resultPacked);
        }
    }
    
    @Test
    public void testAddPackedPrecsionTooSmallOverflowSetAsFalseOddToOdd()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        byte [] answer = new byte[10];

        int number1 = 99;
        int number2 = 99;

        int number1Length = 2;
        int number2Length = 2;
        
        int resultLength = 1;

        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);
        
        PackedDecimal.addPackedDecimal(resultPacked, 1, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, false);

        answer[0]=(byte) 0x00;
        answer[1]=(byte) 0x8C;
        answer[2]=(byte) 0x00;
        answer[3]=(byte) 0x00;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testAddPackedPrecsionTooSmallOverflowSetAsFalseOddToOdd", answer, resultPacked);
        }
    }
    
    @Test
    public void testSubtractPackedPrecsionTooSmallOverflowSetAsFalseEvenToOdd()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        byte [] answer = new byte[10];
        
        int number1 = 9999;
        int number2 = 1;
        
        int number1Length = 4;
        int number2Length = 1;
        
        int resultLength = 3;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);
        
        PackedDecimal.subtractPackedDecimal(resultPacked, 1, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, false);

        answer[0]=(byte) 0x00;
        answer[1]=(byte) 0x99;
        answer[2]=(byte) 0x8C;
        answer[3]=(byte) 0x00;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testSubtractPackedPrecsionTooSmallOverflowSetAsFalseEvenToOdd", answer, resultPacked);
        }
    }
    
    @Test
    public void testSubtractPackedPrecsionTooSmallOverflowSetAsFalseOddToEvenSameByte()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        byte [] answer = new byte[10];
        
        int number1 = 999;
        int number2 = 1;
        
        int number1Length = 3;
        int number2Length = 1;
        
        int resultLength = 2;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);
        
        PackedDecimal.subtractPackedDecimal(resultPacked, 1, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, false);
        
        answer[0]=(byte) 0x00;
        answer[1]=(byte) 0x09;
        answer[2]=(byte) 0x8C;
        answer[3]=(byte) 0x00;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testSubtractPackedPrecsionTooSmallOverflowSetAsFalseOddToEvenSameByte", answer, resultPacked);
        }
    }
    
    @Test
    public void testSubtractPackedPrecsionTooSmallOverflowSetAsFalseOddToEvenNotSameByte()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        byte [] answer = new byte[10];

        int number1 = 99999;
        int number2 = 1;
        
        int number1Length = 5;
        int number2Length = 1;
        
        int resultLength = 2;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);
        
        PackedDecimal.subtractPackedDecimal(resultPacked, 1, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, false);
        
        answer[0]=(byte) 0x00;
        answer[1]=(byte) 0x09;
        answer[2]=(byte) 0x8C;
        answer[3]=(byte) 0x00;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testSubtractPackedPrecsionTooSmallOverflowSetAsFalseOddToEvenNotSameByte", answer, resultPacked);
        }
    }
    
    @Test
    public void testSubtractPackedPrecsionTooSmallOverflowSetAsFalseEvenToEven()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        byte [] answer = new byte[10];
        
        int number1 = 9999;
        int number2 = 1;
        
        int number1Length = 4;
        int number2Length = 1;
        
        int resultLength = 2;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);
        
        PackedDecimal.subtractPackedDecimal(resultPacked, 1, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, false);

        answer[0]=(byte) 0x00;
        answer[1]=(byte) 0x09;
        answer[2]=(byte) 0x8C;
        answer[3]=(byte) 0x00;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testSubtractPackedPrecsionTooSmallOverflowSetAsFalseEvenToEven", answer, resultPacked);
        }
    }
    
    @Test
    public void testSubtractPackedPrecsionTooSmallOverflowSetAsFalseOddToOdd()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        byte [] answer = new byte[10];
        
        int number1 = 99999;
        int number2 = 1;
        
        int number1Length = 5;
        int number2Length = 1;
        
        int resultLength = 3;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);
        
        PackedDecimal.subtractPackedDecimal(resultPacked, 1, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, false);

        answer[0]=(byte) 0x00;
        answer[1]=(byte) 0x99;
        answer[2]=(byte) 0x8C;
        answer[3]=(byte) 0x00;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testSubtractPackedPrecsionTooSmallOverflowSetAsFalseOddToOdd", answer, resultPacked);
        }
    }
    
    @Test
    public void testMultiplyPrecisionTooSmallOverflowSetAsFalseOddToEven()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        byte [] answer = new byte[10];
        
        int number1 = 1234;
        int number2 = 10;

        int number1Length = 4;
        int number2Length = 2;
        
        int resultLength = 4;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);
        
        PackedDecimal.multiplyPackedDecimal(resultPacked, 1, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, false);

        answer[0]=(byte) 0x00;
        answer[1]=(byte) 0x02;
        answer[2]=(byte) 0x34;
        answer[3]=(byte) 0x0C;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testMultiplyPrecisionTooSmallOverflowSetAsFalseOddToEven", answer, resultPacked);
        }
    }
    
    @Test
    public void testMultiplyPrecisionTooSmallOverflowSetAsFalseEvenToOdd()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        byte [] answer = new byte[10];
        
        int number1 = 123;
        int number2 = 10;

        int number1Length = 3;
        int number2Length = 2;
        
        int resultLength = 3;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);
        
        PackedDecimal.multiplyPackedDecimal(resultPacked, 1, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, false);

        answer[0]=(byte) 0x00;
        answer[1]=(byte) 0x23;
        answer[2]=(byte) 0x0C;
        answer[3]=(byte) 0x00;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testMultiplyPrecisionTooSmallOverflowSetAsFalseEvenToOdd", answer, resultPacked);
        }
    }
    
    @Test
    public void testDividePrecisionTooSmallOverflowSetAsFalseEvenToOdd()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        byte [] answer = new byte [10];
        
        int number1 = 9999;
        int number2 = 1;
        
        int number1Length = 4;
        int number2Length = 1;
        
        int resultLength = 3;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);
        
        PackedDecimal.dividePackedDecimal(resultPacked, 1, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, false);
        
        answer[0]=(byte) 0x00;
        answer[1]=(byte) 0x99;
        answer[2]=(byte) 0x9C;
        answer[3]=(byte) 0x00;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testDividePrecisionTooSmallOverflowSetAsFalseEvenToOdd", answer, resultPacked);
        }
    }
    
    @Test
    public void testDividePrecisionTooSmallOverflowSetAsFalseOddToEven()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        byte [] answer = new byte [10];
        
        int number1 = 999;
        int number2 = 1;
        
        int number1Length = 3;
        int number2Length = 1;
        
        int resultLength = 2;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);
        
        PackedDecimal.dividePackedDecimal(resultPacked, 1, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, false);
        
        answer[0]=(byte) 0x00;
        answer[1]=(byte) 0x09;
        answer[2]=(byte) 0x9C;
        answer[3]=(byte) 0x00;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testDividePrecisionTooSmallOverflowSetAsFalseOddToEven", answer, resultPacked);
        }
    }
    
    @Test
    public void testRemainderPrecisionTooSmallOverflowSetAsFalseEvenToOdd()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        byte [] answer = new byte[10];
        
        int number1 = 987;
        int number2 = 25;

        int number1Length = 3;
        int number2Length = 2;
        
        int resultLength = 1;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);
        
        PackedDecimal.remainderPackedDecimal(resultPacked, 1, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, false);
        
        answer[0]=(byte) 0x00;
        answer[1]=(byte) 0x2C;
        answer[2]=(byte) 0x00;
        answer[3]=(byte) 0x00;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testRemainderPrecisionTooSmallOverflowSetAsFalseEvenToOdd", answer, resultPacked);
        }

    }
    
    @Test
    public void testRemainderPrecisionTooSmallOverflowSetAsFalseOddToEven()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        byte [] answer = new byte[10];
        
        int number1 = 1233;
        int number2 = 113;

        int number1Length = 4;
        int number2Length = 3;
        
        int resultLength = 2;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);
        
        PackedDecimal.remainderPackedDecimal(resultPacked, 1, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, false);
        
        answer[0]=(byte) 0x00;
        answer[1]=(byte) 0x00;
        answer[2]=(byte) 0x3C;
        answer[3]=(byte) 0x00;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testRemainderPrecisionTooSmallOverflowSetAsFalseOddToEven", answer, resultPacked);
        }
    }
    
    @Test
    public void testAddPackedDecimalRandomLeadingZerosSum()
    {
        int randomNumber1;
        int randomNumber2;
        
        int randomNumber1Length;
        int randomNumber2Length;
        
        int result;
        int resultLength;
        
        for (int i = 0; i < 1000; i++)
        {
            randomNumber1 = randomGenerator.nextInt(Integer.MAX_VALUE / 2);
            randomNumber2 = randomGenerator.nextInt(Integer.MAX_VALUE / 2);
            
            result = randomNumber1 + randomNumber2;
            
            randomNumber1Length = String.valueOf(randomNumber1).length();
            randomNumber2Length = String.valueOf(randomNumber2).length();
            resultLength = String.valueOf(result).length() + randomGenerator.nextInt(5);

            DecimalData.convertIntegerToPackedDecimal(randomNumber1, randomNumberPacked1, 0, randomNumber1Length, false);
            DecimalData.convertIntegerToPackedDecimal(randomNumber2, randomNumberPacked2, 0, randomNumber2Length, false);
            
            PackedDecimal.addPackedDecimal(resultPacked, 0, resultLength, randomNumberPacked1, 0, randomNumber1Length, randomNumberPacked2, 0, randomNumber2Length, false);
            DecimalData.convertLongToPackedDecimal(result, answerPacked, 0, resultLength, false);
                    
            try 
            {
                assertArrayEquals(resultPacked ,answerPacked);
            }
            
            catch (AssertionError e)
            {
                assertArrayEquals("testAddPackedDecimalRandomLeadingZerosSum", resultPacked ,answerPacked);
            }
        }
    }
    
    @Test
    public void testSubtractPackedDecimalRandomLeadingZerosSum()
    {
        int randomNumber1;
        int randomNumber2;

        int randomNumber1Length;
        int randomNumber2Length;
        
        int result;
        int resultLength;
        
        for (int i = 0; i < 1000; i++)
        {
            randomNumber1 = randomGenerator.nextInt(Integer.MAX_VALUE / 2);
            randomNumber2 = randomGenerator.nextInt(Integer.MAX_VALUE / 2);
            
            result = randomNumber1 - randomNumber2;
            
            randomNumber1Length = String.valueOf(randomNumber1).length();
            randomNumber2Length = String.valueOf(randomNumber2).length();
            
            resultLength = String.valueOf(result).length() + randomGenerator.nextInt(5);

            DecimalData.convertIntegerToPackedDecimal(randomNumber1, randomNumberPacked1, 0, randomNumber1Length, false);
            DecimalData.convertIntegerToPackedDecimal(randomNumber2, randomNumberPacked2, 0, randomNumber2Length, false);
            
            PackedDecimal.subtractPackedDecimal(resultPacked, 0, resultLength, randomNumberPacked1, 0, randomNumber1Length, randomNumberPacked2, 0, randomNumber2Length, false);
            DecimalData.convertLongToPackedDecimal(result, answerPacked, 0, resultLength, false);
                    
            try 
            {
                assertArrayEquals(resultPacked ,answerPacked);
            }
            
            catch (AssertionError e)
            {
                assertArrayEquals("testSubtractPackedDecimalRandomLeadingZerosSum", resultPacked ,answerPacked);
            }
        }
    }
    
    @Test
    public void testMultiplyPackedDecimalOnTheThanLongRange()
    {
        byte [] numberPacked1 = new byte[128];
        byte [] numberPacked2 = new byte[128];
        
        byte [] resultPacked = new byte[128];
        byte [] answer = new byte[128];
        
        // 9,223,372,036,854,775,807 + 1 = 9,223,372,036,854,775,808
        numberPacked1[0] = (byte) 0x92;
        numberPacked1[1] = 0x23;      
        numberPacked1[2] = 0x37;
        numberPacked1[3]=0x20;
        numberPacked1[4]=0x36;
        numberPacked1[5]=(byte) 0x85;
        numberPacked1[6]=0x47;
        numberPacked1[7]=0x75;
        numberPacked1[8]=(byte) 0x80;
        numberPacked1[9]=(byte) 0x7C;
        
        int number2 =1;

        DecimalData.convertIntegerToPackedDecimal(number2,numberPacked2,0,1,false);
        
        PackedDecimal.multiplyPackedDecimal(resultPacked, 0, 19, numberPacked1, 0, 19, numberPacked2, 0, 1, false);
        
        answer[0]=(byte) 0x92;
        answer[1]=0x23;     
        answer[2]=0x37;
        answer[3]=0x20;
        answer[4]=0x36;
        answer[5]=(byte) 0x85;
        answer[6]=0x47;
        answer[7]=0x75;
        answer[8]=(byte) 0x80;
        answer[9]=(byte) 0x7C;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testMultiplyPackedDecimalOnTheThanLongRange", answer, resultPacked);
        }
    }
    
    @Test
    public void testMultiplyPackedDecimalOneBigInteger()
    {
        byte [] numberPacked1 = new byte[128];
        byte [] numberPacked2 = new byte[128];
        
        byte [] resultPacked = new byte[128];
        byte [] answer = new byte[128];
        
        // 9,223,372,036,854,775,808 + 1 = 9,223,372,036,854,775,808
        numberPacked1[0] = (byte) 0x92;
        numberPacked1[1] = 0x23;      
        numberPacked1[2] = 0x37;
        numberPacked1[3]=0x20;
        numberPacked1[4]=0x36;
        numberPacked1[5]=(byte) 0x85;
        numberPacked1[6]=0x47;
        numberPacked1[7]=0x75;
        numberPacked1[8]=(byte) 0x80;
        numberPacked1[9]=(byte) 0x8C;
        
        int number2 =1;

        DecimalData.convertIntegerToPackedDecimal(number2,numberPacked2,0,1,false);
        
        PackedDecimal.multiplyPackedDecimal(resultPacked, 0, 19, numberPacked1, 0, 19, numberPacked2, 0, 1, false);
        
        answer[0]=(byte) 0x92;
        answer[1]=0x23;     
        answer[2]=0x37;
        answer[3]=0x20;
        answer[4]=0x36;
        answer[5]=(byte) 0x85;
        answer[6]=0x47;
        answer[7]=0x75;
        answer[8]=(byte) 0x80;
        answer[9]=(byte) 0x8C;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testMultiplyPackedDecimalOneBigInteger", answer, resultPacked);
        }

    }
    
    @Test
    public void testMultiplyPackedDecimalTwoBigInteger()
    {
        byte [] numberPacked1 = new byte[128];
        byte [] numberPacked2 = new byte[128];
        
        byte [] resultPacked = new byte[128];
        byte [] answer = new byte[128];
        
        // 9,223,372,036,854,775,808 * 9,223,372,036,854,775,808 = 8 50 70 59 17 30 23 46 15 86 58 43 65 18 57 94 20 52 86 4
        numberPacked1[0] = (byte) 0x92;
        numberPacked1[1] = 0x23;      
        numberPacked1[2] = 0x37;
        numberPacked1[3]=0x20;
        numberPacked1[4]=0x36;
        numberPacked1[5]=(byte) 0x85;
        numberPacked1[6]=0x47;
        numberPacked1[7]=0x75;
        numberPacked1[8]=(byte) 0x80;
        numberPacked1[9]=(byte) 0x8C;       
        numberPacked2[0] = (byte) 0x92;
        numberPacked2[1] = 0x23;      
        numberPacked2[2]=0x37;
        numberPacked2[3]=0x20;
        numberPacked2[4]=0x36;
        numberPacked2[5]=(byte) 0x85;
        numberPacked2[6]=0x47;
        numberPacked2[7]=0x75;
        numberPacked2[8]=(byte) 0x80;
        numberPacked2[9]=(byte) 0x8C;
        
        PackedDecimal.multiplyPackedDecimal(resultPacked, 0, 38, numberPacked1, 0, 19, numberPacked2, 0, 19, false);
        
        answer[0]=(byte) 0x08;
        answer[1]=(byte) 0x50;
        answer[2]=(byte) 0x70;
        answer[3]=(byte) 0x59;
        answer[4]=(byte) 0x17;
        answer[5]=(byte) 0x30;
        answer[6]=(byte) 0x23;
        answer[7]=(byte) 0x46;
        answer[8]=(byte) 0x15;
        answer[9]=(byte) 0x86;
        answer[10]=(byte) 0x58;
        answer[11]=(byte) 0x43;
        answer[12]=(byte) 0x65;
        answer[13]=(byte) 0x18;
        answer[14]=(byte) 0x57;
        answer[15]=(byte) 0x94;
        answer[16]=(byte) 0x20;
        answer[17]=(byte) 0x52;
        answer[18]=(byte) 0x86;
        answer[19]=(byte) 0x4C;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testMultiplyPackedDecimalTwoBigInteger", answer, resultPacked);
        }

    }
    
    @Test
    public void testMultiplyPackedDecimalTwoBigIntegeLossOfPrecision()
    {
        byte [] numberPacked1 = new byte[128];
        byte [] numberPacked2 = new byte[128];
        
        byte [] resultPacked = new byte[128];
        byte [] answer = new byte[128];
        
        // 9,223,372,036,854,775,808 * 9,223,372,036,854,775,808 = 8 50 70 59 17 30 23 46 15 86 58 43 65 18 57 94 20 52 86 4
        numberPacked1[0] = (byte) 0x92;
        numberPacked1[1] = 0x23;      
        numberPacked1[2] = 0x37;
        numberPacked1[3]=0x20;
        numberPacked1[4]=0x36;
        numberPacked1[5]=(byte) 0x85;
        numberPacked1[6]=0x47;
        numberPacked1[7]=0x75;
        numberPacked1[8]=(byte) 0x80;
        numberPacked1[9]=(byte) 0x8C;       
        numberPacked2[0] = (byte) 0x92;
        numberPacked2[1] = 0x23;      
        numberPacked2[2]=0x37;
        numberPacked2[3]=0x20;
        numberPacked2[4]=0x36;
        numberPacked2[5]=(byte) 0x85;
        numberPacked2[6]=0x47;
        numberPacked2[7]=0x75;
        numberPacked2[8]=(byte) 0x80;
        numberPacked2[9]=(byte) 0x8C;
        
        PackedDecimal.multiplyPackedDecimal(resultPacked, 0, 37, numberPacked1, 0, 19, numberPacked2, 0, 19, false);

        answer[0]=(byte) 0x50;
        answer[1]=(byte) 0x70;
        answer[2]=(byte) 0x59;
        answer[3]=(byte) 0x17;
        answer[4]=(byte) 0x30;
        answer[5]=(byte) 0x23;
        answer[6]=(byte) 0x46;
        answer[7]=(byte) 0x15;
        answer[8]=(byte) 0x86;
        answer[9]=(byte) 0x58;
        answer[10]=(byte) 0x43;
        answer[11]=(byte) 0x65;
        answer[12]=(byte) 0x18;
        answer[13]=(byte) 0x57;
        answer[14]=(byte) 0x94;
        answer[15]=(byte) 0x20;
        answer[16]=(byte) 0x52;
        answer[17]=(byte) 0x86;
        answer[18]=(byte) 0x4C;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testMultiplyPackedDecimalTwoBigIntegeLossOfPrecision", answer, resultPacked);
        }
    }
    
    @Test
    public void testMultiplyPackedDecimalTwoLongsEqualToBigDecimal()
    {
        byte [] numberPacked1 = new byte[128];
        byte [] numberPacked2 = new byte[128];
        
        byte [] resultPacked = new byte[128];
        byte [] answer = new byte[128];
        
        // 3037000500 * 3037000500 = 9223372037000250000
        numberPacked1[0] = 0x03;
        numberPacked1[1] = (byte) 0x03;
        numberPacked1[2] = 0x70;
        numberPacked1[3]=0x00;
        numberPacked1[4]=(byte) 0x50;
        numberPacked1[5]=(byte) 0x0C;
        
        numberPacked2[0] = 0x03;
        numberPacked2[1] = (byte) 0x03;
        numberPacked2[2]=0x70;
        numberPacked2[3]=0x00;
        numberPacked2[4]=(byte) 0x50;
        numberPacked2[5]=(byte) 0x0C;
        
        PackedDecimal.multiplyPackedDecimal(resultPacked, 0, 19, numberPacked1, 0, 10, numberPacked2, 0, 10, false);
        
        answer[0]=(byte) 0x92;
        answer[1]=0x23;     
        answer[2]=0x37;
        answer[3]=0x20;
        answer[4]=0x37;
        answer[5]=(byte) 0x00;
        answer[6]=0x02;
        answer[7]=0x50;
        answer[8]=(byte) 0x00;
        answer[9]=(byte) 0x0C;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testMultiplyPackedDecimalTwoLongsEqualToBigDecimal", answer, resultPacked);
        }

    }
    
    @Test
    public void testDividePackedDecimalOnTheThanLongRange()
    {
        byte [] numberPacked1 = new byte[128];
        byte [] numberPacked2 = new byte[128];
        
        byte [] resultPacked = new byte[128];
        byte [] answer = new byte[128];
        
        // 9,223,372,036,854,775,807 / 1 = 9,223,372,036,854,775,807
        numberPacked1[0] = (byte) 0x92;
        numberPacked1[1] = 0x23;      
        numberPacked1[2] = 0x37;
        numberPacked1[3]=0x20;
        numberPacked1[4]=0x36;
        numberPacked1[5]=(byte) 0x85;
        numberPacked1[6]=0x47;
        numberPacked1[7]=0x75;
        numberPacked1[8]=(byte) 0x80;
        numberPacked1[9]=(byte) 0x7C;
        
        int number2 = 1;

        DecimalData.convertIntegerToPackedDecimal(number2,numberPacked2,0,1,false);
        
        PackedDecimal.dividePackedDecimal(resultPacked, 0, 19, numberPacked1, 0, 19, numberPacked2, 0, 1, false);
        
        answer[0]=(byte) 0x92;
        answer[1]=0x23;     
        answer[2]=0x37;
        answer[3]=0x20;
        answer[4]=0x36;
        answer[5]=(byte) 0x85;
        answer[6]=0x47;
        answer[7]=0x75;
        answer[8]=(byte) 0x80;
        answer[9]=(byte) 0x7C;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testDividePackedDecimalOnTheThanLongRange", answer, resultPacked);
        }

    }
    
    @Test
    public void testDividePackedDecimalOneBigInteger()
    {
        byte [] numberPacked1 = new byte[128];
        byte [] numberPacked2 = new byte[128];
        
        byte [] resultPacked = new byte[128];
        byte [] answer = new byte[128];
        
        // 9,223,372,036,854,775,807 / 1 = 9,223,372,036,854,775,807
        numberPacked1[0] = (byte) 0x92;
        numberPacked1[1] = 0x23;      
        numberPacked1[2] = 0x37;
        numberPacked1[3]=0x20;
        numberPacked1[4]=0x36;
        numberPacked1[5]=(byte) 0x85;
        numberPacked1[6]=0x47;
        numberPacked1[7]=0x75;
        numberPacked1[8]=(byte) 0x80;
        numberPacked1[9]=(byte) 0x8C;
        
        int number2 = 1;

        DecimalData.convertIntegerToPackedDecimal(number2,numberPacked2,0,1,false);
        
        PackedDecimal.dividePackedDecimal(resultPacked, 0, 19, numberPacked1, 0, 19, numberPacked2, 0, 1, false);
        
        answer[0]=(byte) 0x92;
        answer[1]=0x23;     
        answer[2]=0x37;
        answer[3]=0x20;
        answer[4]=0x36;
        answer[5]=(byte) 0x85;
        answer[6]=0x47;
        answer[7]=0x75;
        answer[8]=(byte) 0x80;
        answer[9]=(byte) 0x8C;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testDividePackedDecimalOneBigInteger", answer, resultPacked);
        }

    }

    @Test
    public void testDividePackedDecimalTwoBigInteger()
    {
        
        byte [] numberPacked1 = new byte[128];
        byte [] numberPacked2 = new byte[128];
        
        byte [] resultPacked = new byte[128];
        byte [] answer = new byte[128];
        
        // 9,223,372,036,854,775,808 / 9,223,372,036,854,775,808 = 1
        numberPacked1[0] = (byte) 0x92;
        numberPacked1[1] = 0x23;      
        numberPacked1[2] = 0x37;
        numberPacked1[3]=0x20;
        numberPacked1[4]=0x36;
        numberPacked1[5]=(byte) 0x85;
        numberPacked1[6]=0x47;
        numberPacked1[7]=0x75;
        numberPacked1[8]=(byte) 0x80;
        numberPacked1[9]=(byte) 0x8C;
        
        numberPacked2[0] = (byte) 0x92;
        numberPacked2[1] = 0x23;      
        numberPacked2[2]=0x37;
        numberPacked2[3]=0x20;
        numberPacked2[4]=0x36;
        numberPacked2[5]=(byte) 0x85;
        numberPacked2[6]=0x47;
        numberPacked2[7]=0x75;
        numberPacked2[8]=(byte) 0x80;
        numberPacked2[9]=(byte) 0x8C;

        PackedDecimal.dividePackedDecimal(resultPacked, 0, 1, numberPacked1, 0, 19, numberPacked2, 0, 19, false);
        
        answer[0] = 0x1C;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testDividePackedDecimalTwoBigInteger", answer, resultPacked);
        }
    }
    
    @Test
    public void testRemainderPackedDecimalOnTheLongRange()
    {
        
        byte [] numberPacked1 = new byte[128];
        byte [] numberPacked2 = new byte[128];
        
        byte [] resultPacked = new byte[128];
        byte [] answer = new byte[128];
        
        // 9,223,372,036,854,775,808 % 1 = 9,223,372,036,854,775,808 = 1
        numberPacked1[0] = (byte) 0x92;
        numberPacked1[1] = 0x23;      
        numberPacked1[2] = 0x37;
        numberPacked1[3]=0x20;
        numberPacked1[4]=0x36;
        numberPacked1[5]=(byte) 0x85;
        numberPacked1[6]=0x47;
        numberPacked1[7]=0x75;
        numberPacked1[8]=(byte) 0x80;
        numberPacked1[9]=(byte) 0x7C;
        
        int number2 =1;

        DecimalData.convertIntegerToPackedDecimal(number2,numberPacked2,0,1,false);

        PackedDecimal.remainderPackedDecimal(resultPacked, 0, 1, numberPacked1, 0, 19, numberPacked2, 0, 1, false);
        
        answer[0] = 0x0C;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testRemainderPackedDecimalOnTheLongRange", answer, resultPacked);
        }
    }
    
    @Test
    public void testRemainderPackedDecimalOneBigInteger()
    {    
        byte [] numberPacked1 = new byte[128];
        byte [] numberPacked2 = new byte[128];
        
        byte [] resultPacked = new byte[128];
        byte [] answer = new byte[128];
        
        // 9,223,372,036,854,775,808 % 1 = 9,223,372,036,854,775,808 = 1
        numberPacked1[0] = (byte) 0x92;
        numberPacked1[1] = 0x23;      
        numberPacked1[2] = 0x37;
        numberPacked1[3]=0x20;
        numberPacked1[4]=0x36;
        numberPacked1[5]=(byte) 0x85;
        numberPacked1[6]=0x47;
        numberPacked1[7]=0x75;
        numberPacked1[8]=(byte) 0x80;
        numberPacked1[9]=(byte) 0x8C;
        
        int number2 =1;

        DecimalData.convertIntegerToPackedDecimal(number2,numberPacked2,0,1,false);

        PackedDecimal.remainderPackedDecimal(resultPacked, 0, 1, numberPacked1, 0, 19, numberPacked2, 0, 1, false);
        
        answer[0] = 0x0C;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testRemainderPackedDecimalOneBigInteger", answer, resultPacked);
        }
    }
    
    @Test
    public void testRemainderPackedDecimalTwoBigInteger()
    {
        byte [] numberPacked1 = new byte[128];
        byte [] numberPacked2 = new byte[128];
        
        byte [] resultPacked = new byte[128];
        byte [] answer = new byte[128];
        
        // 9,223,372,036,854,775,808 % 1 = 9,223,372,036,854,775,808 = 1
        numberPacked1[0] = (byte) 0x92;
        numberPacked1[1] = 0x23;      
        numberPacked1[2] = 0x37;
        numberPacked1[3]=0x20;
        numberPacked1[4]=0x36;
        numberPacked1[5]=(byte) 0x85;
        numberPacked1[6]=0x47;
        numberPacked1[7]=0x75;
        numberPacked1[8]=(byte) 0x80;
        numberPacked1[9]=(byte) 0x8C;
        
        numberPacked2[0] = (byte) 0x92;
        numberPacked2[1] = 0x23;      
        numberPacked2[2]=0x37;
        numberPacked2[3]=0x20;
        numberPacked2[4]=0x36;
        numberPacked2[5]=(byte) 0x85;
        numberPacked2[6]=0x47;
        numberPacked2[7]=0x75;
        numberPacked2[8]=(byte) 0x80;
        numberPacked2[9]=(byte) 0x8C;

        PackedDecimal.remainderPackedDecimal(resultPacked, 0, 1, numberPacked1, 0, 19, numberPacked2, 0, 19, false);
        
        answer[0]=0x0C;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testRemainderPackedDecimalTwoBigInteger", answer, resultPacked);
        }
    }

    @Test
    public void multiplyResultisZero()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        byte [] answer = new byte [10];
        
        int number1 = 1;
        int number2 = 0;
        
        int number1Length = 1;
        int number2Length = 1;
        
        int resultLength = 1;
        
        DecimalData.convertIntegerToPackedDecimal(number1, numberPacked1, 0, number1Length, false);
        DecimalData.convertIntegerToPackedDecimal(number2, numberPacked2, 0, number2Length, false);
        
        PackedDecimal.multiplyPackedDecimal(resultPacked, 0, resultLength, numberPacked1, 0, number1Length, numberPacked2, 0, number2Length, false);
        
        answer[0] = (byte)0x0C;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("multiplyResultisZero", answer, resultPacked);
        }
            
    }
    
    @Test
    public void invalidPackedDecimalSignOperations()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        byte [] answer = new byte [10];

        numberPacked1[0] = 0x11;
        numberPacked1[1] = 0x24;      
        
        numberPacked2[0] = 0x00;
        numberPacked2[1] = 0x50;
        
        PackedDecimal.multiplyPackedDecimal(resultPacked, 0, 3, numberPacked1, 0, 3, numberPacked2, 0, 3, false);
  
        answer[0] = 0x56;
        answer[1] = 0x0C;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("invalidPackedDecimalSignOperations", answer, resultPacked);
        }
        
        PackedDecimal.remainderPackedDecimal(resultPacked, 0, 3, numberPacked1, 0, 3, numberPacked2, 0, 3, false);

        answer[0] = 0x00;
        answer[1] = 0x2C;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("invalidPackedDecimalSignOperations", answer, resultPacked);
        }
        
        PackedDecimal.dividePackedDecimal(resultPacked, 0, 3, numberPacked1, 0, 3, numberPacked2, 0, 3, false);

        answer[0] = 0x02;
        answer[1] = 0x2C;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("invalidPackedDecimalSignOperations", answer, resultPacked);
        }
    }
    
    @Test
    public void testDivideByZero()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];

        numberPacked1[0] = 0x12;
        numberPacked1[1] = 0x3C;      
        
        numberPacked1[2] = 0x00;
        numberPacked1[3] = 0x0C;
        
        boolean catched = false;
        
        try
        {
            PackedDecimal.dividePackedDecimal(resultPacked, 0, 3, numberPacked1, 0, 3, numberPacked2, 0, 3, false);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail();
    }
    
    @Test
    public void testRemainderByZero()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];

        numberPacked1[0] = 0x12;
        numberPacked1[1] = 0x3C;      
        
        numberPacked1[2] = 0x00;
        numberPacked1[3] = 0x0C;
        
        boolean catched = false;
        
        try
        {
            PackedDecimal.remainderPackedDecimal(resultPacked, 0, 3, numberPacked1, 0, 3, numberPacked2, 0, 3, false);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail();
    }
    
    @Test
    public void testDivideByZeroByZero()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];

        numberPacked1[0] = 0x00;
        numberPacked1[1] = 0x0C;      
        
        numberPacked1[2] = 0x00;
        numberPacked1[3]=0x0C;
        
        boolean catched = false;
        try
        {
            PackedDecimal.dividePackedDecimal(resultPacked, 0, 3, numberPacked1, 0, 3, numberPacked2, 0, 3, false);
        } catch (ArithmeticException e)
        {
            catched = true;
        }
        if (!catched)
            fail();
        
    }
    
    @Test
    public void testRemainderByZeroByZero()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];

        numberPacked1[0] = 0x00;
        numberPacked1[1] = 0x0C;      
        
        numberPacked1[2] = 0x00;
        numberPacked1[3] = 0x0C;
        
        boolean catched = false;
        
        try
        {
            PackedDecimal.remainderPackedDecimal(resultPacked, 0, 3, numberPacked1, 0, 3, numberPacked2, 0, 3, false);
        } 
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail();
    }
    
    @Test
    public void testZeroDividesNumber()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        byte [] answer = new byte[10];

        numberPacked1[0] = 0x00;
        numberPacked1[1] = 0x00;      
        numberPacked1[2] = 0x0C;

        numberPacked2[0] = 0x12;
        numberPacked2[1] = 0x3C;      

        PackedDecimal.dividePackedDecimal(resultPacked, 0, 1, numberPacked1, 0, 5, numberPacked2, 0, 3, false);
        
        answer[0] = 0x0C;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("", answer, resultPacked);
        }
    }
    
    @Test
    public void testZeroRemainderNumber()
    {
        byte [] numberPacked1 = new byte[10];
        byte [] numberPacked2 = new byte[10];
        
        byte [] resultPacked = new byte[10];
        byte [] answer = new byte[10];

        numberPacked1[0] = 0x00;
        numberPacked1[1] = 0x00;      
        numberPacked1[2] = 0x0C;

        numberPacked2[0] = 0x12;
        numberPacked2[1] = 0x3C;      

        PackedDecimal.remainderPackedDecimal(resultPacked, 0, 1, numberPacked1, 0, 5, numberPacked2, 0, 3, false);
        
        answer[0] = 0x0C;
        
        try
        {
            assertArrayEquals(answer, resultPacked);
        }

        catch (AssertionError e)
        {
            assertArrayEquals("testZeroRemainderNumber", answer, resultPacked);
        }
    }
   
    public static void main(String[] args)
    {
        System.out.println("Running TestArithmeticOperations.Main");
        JUnitCore core = new JUnitCore();
        core.addListener(new TextListener(System.out));
        long startTime = System.nanoTime();
        Result res = core.run(TestArithmeticOperations.class);
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        System.out.printf("Arithmetics: Time taken for tests:%d \n", duration);
        System.out.println("Total: " + res.getRunCount() + " Fail: "+ res.getFailureCount() + " Ignore: " + res.getIgnoreCount());
        JUnitCore.main("net.openj9.test.arithmetics.TestArithmeticOperations");
    }
}