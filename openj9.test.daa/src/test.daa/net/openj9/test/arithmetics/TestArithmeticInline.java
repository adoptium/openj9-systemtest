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

import java.util.Arrays;
import java.util.Random;


public class TestArithmeticInline 
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
		outputFile = "expected."+TestArithmeticInline.class.getSimpleName()+".txt";
		randomGenerator = new Random(randomSeed);
	};

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
	public void testNotEqualsPackedDecimal()
	{
		generateValues();
		
		final int offset0 = 0;
		final int precision5 = 5;
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
			assertEquals(Utils.makeTestNameEquivalency("Normal", "testNotEqualsPackedDecimal", value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
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
			assertEquals(Utils.makeTestNameEquivalency("Normal", "testNotEqualsPackedDecimal", value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
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
			assertEquals(Utils.makeTestNameEquivalency("Normal", "testNotEqualsPackedDecimal", value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
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
			assertEquals(Utils.makeTestNameEquivalency("Normal", "testNotEqualsPackedDecimal", value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
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
			assertEquals(Utils.makeTestNameEquivalency("Normal", "testNotEqualsPackedDecimal", value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
		}
	}

	
	@Test
	public void testGreaterThanPackedDecimal()
	{
		generateValues();
		
		final int offset0 = 0;
		final int precision5 = 5;
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
			assertEquals(Utils.makeTestNameEquivalency("Normal", "testGreaterThanPackedDecimal", value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
		}
	}
	
	@Test
	public void testGreaterThanOrEqualsPackedDecimal()
	{
		generateValues();
		
		final int offset0 = 0;
		final int precision5 = 5;
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
			assertEquals(Utils.makeTestNameEquivalency("Normal", "testGreaterThanOrEqualsPackedDecimal", value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
		}
	}
	
	@Test
	public void testLessThanOrEqualsPackedDecimal()
	{
		generateValues();
		
		final int offset0 = 0;
		final int precision5 = 5;
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

		correct = value1.compareTo(value2) <= 0;
		
		try 
		{
			assertEquals(correct, result);
		}
		
		catch (AssertionError e)
		{
			assertEquals(Utils.makeTestNameEquivalency("Normal", "testLessThanOrEqualsPackedDecimal", value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
		}
	}
	
	@Test
	public void testLessThanPackedDecimal()
	{
		generateValues();
		
		final int offset0 = 0;
		final int precision5 = 5;
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
			assertEquals(Utils.makeTestNameEquivalency("Normal", "testLessThanPackedDecimal", value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
		}
	}
	
	@Test
	public void testSubtractPackedDecimal()
	{
		generateValues();
		
		final int offset0 = 0;
		final int precision5 = 5;
		final int precision10 = 10;
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
			assertArrayEquals(Utils.makeTestNameArithmetic("Normal", "testSubtractPackedDecimal", value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
		}
	}
	
	@Test
	public void testRemainderPackedDecimal()
	{
		generateValues();
		
		final int offset0 = 0;
		final int precision5 = 5;
		final int precision10 = 10;
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
			assertArrayEquals(Utils.makeTestNameArithmetic("Normal", "testRemainderPackedDecimal", value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
		}
	}
	
	@Test
	public void testDividePackedDecimal()
	{
		generateValues();
		
		final int offset0 = 0;
		final int precision5 = 5;
		final int precision10 = 10;
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
			assertArrayEquals(Utils.makeTestNameArithmetic("Normal", "testDividePackedDecimal", value1, value2, op1, offset0, precision10, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
		}
	}
	
	@Test
	public void testMultiplyPackedDecimal()
	{
		generateValues();
		
		final int offset0 = 0;
		final int precision5 = 5;
		final int precision10 = 10;
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
			assertArrayEquals(Utils.makeTestNameArithmetic("Normal", "testMultiplyPackedDecimal", value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
		}
	}
	
	@Test
	public void testAddPackedDecimal()
	{
		generateValues();
		
		final int offset0 = 0;
		final int precision5 = 5;
		final int precision10 = 10;
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
			assertArrayEquals(Utils.makeTestNameArithmetic("Normal", "testAddPackedDecimal", value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
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
			assertArrayEquals(Utils.makeTestNameArithmetic("Normal", "testAddPackedDecimal", value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
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
			assertArrayEquals(Utils.makeTestNameArithmetic("Normal", "testAddPackedDecimal", value1, value2, op1, offset0, precision5, op2, offset0, precision5, resultant, offset0, precision10, resultValue, correct, offset0, precision10, errorChecking), correct, resultant);
		}
	}
	
	@Test
	public void testEqualsPackedDecimal()
	{
		generateValues();
		
		final int offset0 = 0;
		final int precision5 = 5;
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
			assertEquals(Utils.makeTestNameEquivalency("Functional", "testEqualsPackedDecimal", value1, value2, op1, offset0, precision5, op2, offset0, precision5), correct, result);
		}
	}
	
	public static void main(String[] args)
	{
		System.out.println ("Run me");
		JUnitCore core = new JUnitCore();
		Result result = core.run(TestArithmeticInline.class);
		System.out.println (result.getRunCount());
		JUnitCore.main("net.openj9.test.arithmetics.TestArithmeticInline");
	}
}
