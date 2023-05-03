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

package net.openj9.test.decimals;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import org.junit.Test;

import com.ibm.dataaccess.*;

import net.openj9.test.Utils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import java.util.Random;

import static org.junit.Assert.*;

public class TestDecimalData2 {
	
	static String outputFile;
	static Random randomGenerator;
	
	byte[] externalDecimal = new byte[64];
	final int offset0 = 0;
	final int offset5 = 5;
	final int offset10 = 10;
	final int offset25 = 25;
	final int offset50 = 50;
	final int precision1 = 1;
	final int precision2 = 2;
	final int precision15 = 15;
	final int precision16 = 16;
	final int precision30 = 30;
	final int precision31 = 31;
	final int precision50 = 50;
	final int precision100 = 100;
	final boolean errorChecking = false;
	final boolean errorCheckingFalse = false;
	
	final int decimalType1 = DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING;
	final int decimalType2 = DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING;
	final int decimalType3 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
	final int decimalType4 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
	final int decimalType5 = DecimalData.UNICODE_UNSIGNED;
	final int decimalType6 = DecimalData.UNICODE_SIGN_SEPARATE_LEADING;
	final int decimalType7 = DecimalData.UNICODE_SIGN_SEPARATE_TRAILING;
	
	long value = Utils.TestValue.SmallPositive.LongValue;
	String dataName = "PackedDecimalToUnicodeDecimal";
	String typeName = "UNICODE_UNSIGNED";
	String testType = "Normal Value";
	String testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
	
	byte[] packedDecimal = new byte[64];
	char[] unicodeDecimal = new char[128];
	byte[] finalDecimal = new byte[64];	

	static {
		outputFile = "expected."+TestDecimalData2.class.getSimpleName()+".txt";
	};
	
	@Test
	public void testBDToPD()
	{
		BigDecimal bd = new BigDecimal("123456789012345678901234567890");
		byte[] byteArray = new byte[1024];
		
		DecimalData.convertBigDecimalToPackedDecimal(bd, byteArray, 0, 30, true);
		
		
		
	}
	
	
	@Test
	public void testNonExceptions()
	{
		testConvertLongNormals();

		testConvertIntegerNormals();
	
		testConvertBigDecimalNormals();

		testConvertBigIntegerNormals();

		testOtherConverters();
	}
	
	@Test
	public void testLong2ED2Long(){
		
		
		long result;
	   
		byte[] edValue = new byte[100];
		for(int i = 0; i<1000;i++){
			long value = randomGenerator.nextLong();
			int length = String.valueOf(value).length();
			for(int decimalType = 1; decimalType<=4;decimalType++){
				DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, false,decimalType);
				result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, false,decimalType);
				assertEquals(value,result);
			}
			
		}
		
	}
	
	@Test
	public void testOverflowED2Long(){
		long result;

		//ed is 10e18, just over the maximum for long
		byte[] edValue = new byte[] {(byte) 0xc1, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0};
		try
		{
			result = DecimalData.convertExternalDecimalToLong(edValue, 0, 20, true, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);
			fail("Missed exception! input: 10e18 result: " + result);
		}
		catch (ArithmeticException e)
		{
		}


		//ed is 20e18, just over double the maximum for long
		edValue = new byte[] {(byte) 0xc2, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0};
		try
		{
			result = DecimalData.convertExternalDecimalToLong(edValue, 0, 20, true, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);
			fail("Missed exception! input: 20e18 result: " + result);
		}
		catch (ArithmeticException e)
		{
		}
	}
	
	@Test
	public void testOverflowED2Integer(){
		int result;

		//ed is 3e9, just over the maximum for int
		byte[] edValue = new byte[] {(byte) 0xc3, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0};
		try
		{
			result = DecimalData.convertExternalDecimalToInteger(edValue, 0, 10, true, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);
			fail("Missed exception! input: 3e9 result: " + result);
		}
		catch (ArithmeticException e)
		{
		}

		//ed is 5e9, just over double the maximum for int
		edValue = new byte[] {(byte) 0xc5, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0};
		try
		{
			result = DecimalData.convertExternalDecimalToInteger(edValue, 0, 10, true, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);
			fail("Missed exception! input: 5e9 result: " + result);
		}
		catch (ArithmeticException e)
		{
		}
	}

	@Test
	public void testInteger2ED2Integer(){
		
		
		int result;
	   
		byte[] edValue = new byte[100];
		for(int i = 0; i<1000;i++){
			int value = randomGenerator.nextInt();
			int length = String.valueOf(value).length();
			for(int decimalType = 1; decimalType<=4;decimalType++){
				DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, false,decimalType);
				result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, false,decimalType);
				assertEquals(value,result);
			}
			
		}
		
	}
	
	@Test
	public void testBiggestLong2ED2Long(){
		
		long result;
		   
		byte[] edValue = new byte[100];
		
		long value = Long.MAX_VALUE;
		int length = 19;
		
			DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, false,2);
			result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, false,2);
			assertEquals(value,result);
			
			DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, false,3);
			result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, false,3);
			assertEquals(value,result);
			
			DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, false,4);
			result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, false,4);
			assertEquals(value,result);
			
			DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, false,1);
			result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, false,1);
			assertEquals(value,result);
			
			
			//overflow
			DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, true,1);
			result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, true,1);
			assertEquals(value,result);
			
			DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, true,2);
			result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, true,2);
			assertEquals(value,result);
			
			DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, true,3);
			result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, true,3);
			assertEquals(value,result);
			
			DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, true,4);
			result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, true,4);
			assertEquals(value,result);
		}
	@Test
	public void testBiggestInt2ED2Int(){
		
		int result;
		   
		byte[] edValue = new byte[100];
		
		int value = Integer.MAX_VALUE;
		int length = 10;

		DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, false,1);
		result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, false,1);
		assertEquals(value,result);
		
		DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, false,2);
		result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, false,2);
		assertEquals(value,result);
		
		DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, false,3);
		result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, false,3);
		assertEquals(value,result);
		
		DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, false,4);
		result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, false,4);
		assertEquals(value,result);
 
		//overflow
		DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, true,1);
		result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, true,1);
		assertEquals(value,result);
		
		DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, true,2);
		result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, true,2);
		assertEquals(value,result);
		
		DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, true,3);
		result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, true,3);
		assertEquals(value,result);
		
		DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, true,4);
		result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, true,4);
		assertEquals(value,result);
	}
	
	@Test
	public void testSmallestLong2ED2Long(){

		long result;

		byte[] edValue = new byte[100];

		long value = Long.MIN_VALUE;
		int length = 19;

		
		DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, false,1);
		result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, false,1);
		assertEquals(value,result);
		
		DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, false,2);
		result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, false,2);
		assertEquals(value,result);
		
		DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, false,3);
		result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, false,3);
		assertEquals(value,result);
		
		DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, false,4);
		result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, false,4);
		assertEquals(value,result);
	
		//overflow
		DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, true,1);
		result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, true,1);
		assertEquals(value,result);
		
		DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, true,2);
		result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, true,2);
		assertEquals(value,result);
		
		DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, true,3);
		result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, true,3);
		assertEquals(value,result);
		
		DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, true,4);
		result = DecimalData.convertExternalDecimalToLong(edValue, 0, length, true,4);
		assertEquals(value,result);	
	
	}
	
	
	
	@Test
	public void testSmallestInt2ED2Int(){

		int result;

		byte[] edValue = new byte[100];

		long value = Integer.MIN_VALUE;
		int length = 10;
		
		DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, false,1);
		result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, false,1);
		assertEquals(value,result);
		
		DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, false,2);
		result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, false,2);
		assertEquals(value,result);
		
		DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, false,3);
		result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, false,3);
		assertEquals(value,result);
		
		DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, false,4);
		result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, false,4);
		assertEquals(value,result);
		
		
		//overflow
		DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, true,1);
		result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, true,1);
		assertEquals(value,result);
		
		DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, true,2);
		result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, true,2);
		assertEquals(value,result);
		
		DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, true,3);
		result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, true,3);
		assertEquals(value,result);
		
		DecimalData.convertLongToExternalDecimal(value, edValue, 0, length, true,4);
		result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, true,4);
		assertEquals(value,result);
	}
	
	   @Test
		public void testBiggestInteger2ED2Integer(){
			
			int result;
			   
			byte[] edValue = new byte[100];
			
			int value = Integer.MAX_VALUE;
			int length = 10;

		 
			DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, false,1);
			result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, false,1);
			assertEquals(value,result);
				
			DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, false,2);
			result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, false,2);
			assertEquals(value,result);
				
			DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, false,3);
			result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, false,3);
			assertEquals(value,result);
				
			DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, false,4);
			result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, false,4);
			assertEquals(value,result);
			  
			// overflow
			DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, true,1);
			result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, true,1);
			assertEquals(value,result);
				
			DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, true,2);
			result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, true,2);
			assertEquals(value,result);
				
			DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, true,3);
			result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, true,3);
			assertEquals(value,result);
				
			DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, length, true,4);
			result = DecimalData.convertExternalDecimalToInteger(edValue, 0, length, true,4);
			assertEquals(value,result);				
		}
	   
	   @Test
	   public void testInteger2ED2IntegerDecreasingPrecision(){
		   
		  int result;
		   byte[] edValue = new byte[100];
		   
		   int value = 12345;

		   DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, 4, false,1);
		   result = DecimalData.convertExternalDecimalToInteger(edValue, 0, 4, false,1);
		   assertEquals(2345,result);
		   
		   DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, 4, false,2);
		   result = DecimalData.convertExternalDecimalToInteger(edValue, 0, 4, false,2);
		   assertEquals(2345,result);
		   
		   DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, 4, false,3);
		   result = DecimalData.convertExternalDecimalToInteger(edValue, 0, 4, false,3);
		   assertEquals(2345,result);
		   
		   DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, 4, false,4);
		   result = DecimalData.convertExternalDecimalToInteger(edValue, 0, 4, false,4);
		   assertEquals(2345,result);
		   
		   //overflow
		   try{
		   DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, 4, true,1);
		   result = DecimalData.convertExternalDecimalToInteger(edValue, 0, 4, true,1);
		   assertEquals(2345,result);
		   } catch (Exception e)
		   {}
		   
		   try {
		   DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, 4, true,2);
		   result = DecimalData.convertExternalDecimalToInteger(edValue, 0, 4, true,2);
		   assertEquals(2345,result);
		   } catch (Exception e)
		   {}
		   
		   try {
		   DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, 4, true,3);
		   result = DecimalData.convertExternalDecimalToInteger(edValue, 0, 4, true,3);
		   assertEquals(2345,result);
		   } catch (Exception e)
		   {}
		   
		   try {
		   DecimalData.convertIntegerToExternalDecimal(value, edValue, 0, 4, true,4);
		   result = DecimalData.convertExternalDecimalToInteger(edValue, 0, 4, true,4);
		   assertEquals(2345,result);
		   } catch (Exception e)
		   {}
	   }
	   
	   @Test
	   public void testLong2ED2LongDecreasingPrecision(){
		   
		  long result;
		   byte[] edValue = new byte[100];
		   
		   long value = 12345;

			   DecimalData.convertLongToExternalDecimal(value, edValue, 0, 4, false,1);
			   result = DecimalData.convertExternalDecimalToLong(edValue, 0, 4, false,1);
			   assertEquals(2345,result);
			   
			   DecimalData.convertLongToExternalDecimal(value, edValue, 0, 4, false,2);
			   result = DecimalData.convertExternalDecimalToLong(edValue, 0, 4, false,2);
			   assertEquals(2345,result);
			   
			   DecimalData.convertLongToExternalDecimal(value, edValue, 0, 4, false,3);
			   result = DecimalData.convertExternalDecimalToLong(edValue, 0, 4, false,3);
			   assertEquals(2345,result);
			   
			   DecimalData.convertLongToExternalDecimal(value, edValue, 0, 4, false,4);
			   result = DecimalData.convertExternalDecimalToLong(edValue, 0, 4, false,4);
			   assertEquals(2345,result);
	   }
	  
	
	   @Test
		public void testBiggestLong2UD2Long(){
			
			long result;
			   
		   long value = Long.MAX_VALUE;
			
			char[] udValue = new char[100];
			int length = String.valueOf(value).length();
				for(int decimalType = 6; decimalType<=7;decimalType++){
					DecimalData.convertLongToUnicodeDecimal(value, udValue, 0, length, false,decimalType);
					result = DecimalData.convertUnicodeDecimalToLong(udValue, 0, length, false,decimalType);
					assertEquals(value,result);
				}
				
			}
		
	
	
	   @Test
		public void testLong2UD2Long(){
			
			
			long result;
		   
			char[] udValue = new char[100];
			for(int i = 0; i<1000;i++){
				long value = randomGenerator.nextLong();
				int length = String.valueOf(value).length();
				for(int decimalType = 6; decimalType<=7;decimalType++){
					DecimalData.convertLongToUnicodeDecimal(value, udValue, 0, length, false,decimalType);
					result = DecimalData.convertUnicodeDecimalToLong(udValue, 0, length, false,decimalType);
					assertEquals(value,result);
				}
				
			}
			
		}
	   

	   
	   @Test
	   public void testInteger2UD2Integer(){
		   
		   
		   int result;
		  
		   char[] udValue = new char[100];
		   for(int i = 0; i<1000;i++){
			   int value = randomGenerator.nextInt();
			   int length = String.valueOf(value).length();
			   for(int decimalType = 6; decimalType<=7;decimalType++){
				   DecimalData.convertIntegerToUnicodeDecimal(value, udValue, 0, length, false,decimalType);
				   result = DecimalData.convertUnicodeDecimalToInteger(udValue, 0, length, false,decimalType);
				   assertEquals(value,result);
			   }
			   
		   }
		   
	   }
	   
	   @Test
	   public void testBiggestInteger2UD2Integer(){
		   
		   int result;
			  
		  int value = Integer.MAX_VALUE;
		   
		   char[] udValue = new char[100];
		   int length = String.valueOf(value).length();
			   for(int decimalType = 6; decimalType<=7;decimalType++){
				   DecimalData.convertIntegerToUnicodeDecimal(value, udValue, 0, length, false,decimalType);
				   result = DecimalData.convertUnicodeDecimalToInteger(udValue, 0, length, false,decimalType);
				   assertEquals(value,result);
			   }
			   
		   }
	
	@Test
	public void testi2PD()
	{
		testi2PDHelper(12345);
		testi2PDHelper_p10(2147483647);
		testi2PDHelper_p10(-2147483648);
		
		//TODO: test more precisions
		randomGenerator.setSeed(1024);
		for (int i = 0; i < 10; ++i)
		{
			int value = randomGenerator.nextInt();
			testi2PDHelper_p10(value);
		}
	}
	
	@Test
	public void testi2PDExceptions()
	{
		testi2PDExceptionsHelper(123456);
		//TODO: add more tests
	}
	
	public void testi2PDExceptionsHelper(final int value)
	{
		final int offset = 0;
		final int precison = 5;
		
		byte[] packedDecimal = new byte[64];
		try
		{
			DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, offset, precison, true);
		}
		catch (ArithmeticException e)
		{
			return;
		}
		
		fail("Could not catch exception.");
		
	}
	
	//TODO: test more precisions
	public void testi2PDHelper_p10(final int value)
	{
		int result;
		String dataName, typeName, testType, testName;
		
		final int offset = 0;
		final int precision = 10;

		dataName = "IntegerToPackedDecimal";
		typeName = "PackedDecimal";
		testType = "Normal Value";

		@SuppressWarnings("unused") // Useful to know what we're testing
		String functionName = "convertIntegerToPackedDecimal";

		byte[] packedDecimal = new byte[64];
		Arrays.fill(packedDecimal, (byte) 0x00);
		
		{
			testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
			DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, offset, precision, true);
			result = DecimalData.convertPackedDecimalToInteger(packedDecimal, offset, precision, false);
			testName = "Line: " + " no line " + ", "  + "no name";
			assertEquals(testName, value, result);
		}
	}
	
	public void testi2PDHelper(final int value)
	{
		int result;
		String dataName, typeName, testType, testName;
		
		final int offset = 0;
		final int precision = 5;

		dataName = "IntegerToPackedDecimal";
		typeName = "PackedDecimal";
		testType = "Normal Value";

		@SuppressWarnings("unused") // Useful to know what we're testing
		String functionName = "convertIntegerToPackedDecimal";

		byte[] packedDecimal = new byte[64];
		Arrays.fill(packedDecimal, (byte) 0x00);
		
		{
			testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
			DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, offset, precision, true);
			result = DecimalData.convertPackedDecimalToInteger(packedDecimal, offset, precision, false);
			testName = "Line: " + " no line " + ", "  + "no name";
			assertEquals(testName, value, result);
		}
	}
	
	@Test
	public void testPD2i()
	{
		final int precision = 5;
		final int offset0 = 0;

		@SuppressWarnings("unused") // Useful to know what we're testing
		final String functionName = "converPackedDecimalToInteger"; 

		int value = 12345;
		String dataName = "IntegerToPackedDecimal";
		String typeName = "PackedDecimal";
		String testType = "Normal Value";
		String testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		final boolean errorChecking = true;
		
		byte[] packedDecimal = new byte[64];
		BigInteger bi = new BigInteger("12345");
		DecimalData.convertBigIntegerToPackedDecimal(bi, packedDecimal, 0, 5, errorChecking);
		int result = DecimalData.convertPackedDecimalToInteger(packedDecimal, offset0, precision, errorChecking);
		testName = "Line: " + " no line " + ", "  + "no name";
		assertEquals(testName, value, result);
	}
	
	public void testRepeatedly()
	{
		//lp.deactivate();
		testConvertLongNormals();
		//lp.activate();
		testConvertLongNormals();
		//lp.deactivate();
		
		//lp.deactivate();
		testConvertLongExceptions();
		//lp.activate();
		testConvertLongExceptions();
		//lp.deactivate();
		
		//lp.deactivate();
		testConvertIntegerNormals();
		//lp.activate();
		testConvertIntegerNormals();
		//lp.deactivate();
		
		//lp.deactivate();
		testConvertIntegerExceptions();
		//lp.activate();
		testConvertIntegerExceptions();
		//lp.deactivate();
		
		//lp.deactivate();
		testConvertBigDecimalNormals();
		//lp.activate();
		testConvertBigDecimalNormals();
		//lp.deactivate();
		
		//lp.deactivate();
		testConvertBigDecimalExceptions();
		//lp.activate();
		testConvertBigDecimalExceptions();
		//lp.deactivate();
		
		//lp.deactivate();
		testConvertBigIntegerNormals();
		//lp.activate();
		testConvertBigIntegerNormals();
		//lp.deactivate();
		
		//lp.deactivate();
		testConvertBigIntegerExceptions();
		//lp.activate();
		testConvertBigIntegerExceptions();
		//lp.deactivate();
		
		//lp.deactivate();
		testOtherConverters();
		//lp.activate();
		testOtherConverters();
		//lp.deactivate();
	}
	
	@BeforeClass
	public static void setUp() 
	{
		randomGenerator = new Random(System.currentTimeMillis());
	}
	
	@AfterClass
	public static void tearDown()
	{
	}
	

	/*
	@Test
	public static void singleDebuggingTest()
	{
		byte[] packedDecimal = new byte[64];
		char[] unicodeDecimal = new char[128];
		byte[] finalDecimal = new byte[64];
		
		final int offset0 = 0;
		final int offset5 = 5;
		final int offset10 = 10;
		final int offset25 = 25;
		final int offset50 = 50;
		final int precision1 = 1;
		final int precision2 = 2;
		final int precision15 = 15;
		final int precision16 = 16;
		final int precision30 = 30;
		final int precision31 = 31;
		final int precision50 = 50;
		final int precision100 = 100;
		final boolean errorChecking = false;
		final boolean errorCheckingFalse = false;
		
		final int decimalType1 = DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING;
		final int decimalType2 = DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING;
		final int decimalType3 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
		final int decimalType4 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
		final int decimalType5 = DecimalData.UNICODE_UNSIGNED;
		final int decimalType6 = DecimalData.UNICODE_SIGN_SEPARATE_LEADING;
		final int decimalType7 = DecimalData.UNICODE_SIGN_SEPARATE_TRAILING;
		
		long value = Utils.TestValue.SmallPositive.LongValue;
		String testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;

		Arrays.fill(unicodeDecimal, (char)0);
		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset25, precision15, errorChecking);

		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset25, unicodeDecimal, offset25, precision15, decimalType5);
		
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset25, finalDecimal, offset25, precision15, decimalType5);
	
		assertArrayEquals(testName, packedDecimal, finalDecimal);
	}
	*/
	
	
	@Test
	public void testOtherConverters_pd()
	{
		@SuppressWarnings("unused") // Useful to know what we're testing
		String functionName = "convertPackedDecimal";
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType5);
		//lp.addExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType5);
		//lp.addExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType5);
		//lp.addExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType5);
		//lp.addExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;
		
		/*
		 * Negative number, dont test
		 * DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType9);
		//lp.addExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType9);
		//lp.addExpected("convertUnicodeDecimalToPackedDecimal");*/
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.Zero.LongValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType5);
		//lp.addExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision1, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision1, decimalType5);
		//lp.addExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision1, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		/*
		 * Don't test, negative
		 * DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset5, precision2, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset5, unicodeDecimal, offset5, precision2, decimalType9);
		//lp.addExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset5, finalDecimal, offset5, precision2, decimalType9);
		//lp.addExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name";
		assertArrayEquals(testName, packedDecimal, finalDecimal);*/

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;

		Arrays.fill(unicodeDecimal, (char)0);
		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset25, precision15, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset25, unicodeDecimal, offset25, precision15, decimalType5);
		//lp.addExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset25, finalDecimal, offset25, precision15, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;

		Arrays.fill(unicodeDecimal, (char)0);
		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		/*
		 * Negative, unsigned, dont test
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset5, precision16, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset5, unicodeDecimal, offset5, precision16, decimalType9);
		//lp.addExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset5, finalDecimal, offset5, precision16, decimalType9);
		//lp.addExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name";
		assertArrayEquals(testName, packedDecimal, finalDecimal);*/

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;

		Arrays.fill(unicodeDecimal, (char)0);
		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset50, precision2, errorChecking);
		//lp.addNotExpected("convertLongToPackedDecimal");
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset50, unicodeDecimal, offset50, precision2, decimalType5);
		//lp.addNotExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset50, finalDecimal, offset50, precision2, decimalType5);
		//lp.addNotExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		/* Negative, unsigned, dont test
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision16, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision16, decimalType9);
		//lp.addExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision16, decimalType9);
		//lp.addExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name";
		assertArrayEquals(testName, packedDecimal, finalDecimal);*/

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;

		Arrays.fill(unicodeDecimal, (char)0);
		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision30, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision30, decimalType5);
		//lp.addExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision30, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		/* negative, unsigned, don't test
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision50, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision50, decimalType9);
		//lp.addExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision50, decimalType9);
		//lp.addExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name";
		assertArrayEquals(testName, packedDecimal, finalDecimal);*/

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);
	}
	
	@Test
	public void testOtherConvertes_UDSL()
	{
		/*
		 * UNICODE_SIGN_EMBEDDED_LEADING
		 */
		
		@SuppressWarnings("unused") // Useful to know what we're testing
		String functionName = "PD2UD";
		
		value = Utils.TestValue.SmallPositive.LongValue;
		dataName = "PackedDecimalToUnicodeDecimal";
		typeName = "UNICODE_SIGN_EMBEDDED_LEADING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);
		Arrays.fill(finalDecimal, (byte) 0x00);
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType6);
		//lp.addNotExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType6);
		//lp.addNotExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType6);
		//lp.addNotExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType6);
		//lp.addNotExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType6);
		//lp.addNotExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType6);
		//lp.addNotExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.Zero.LongValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType6);
		//lp.addNotExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision1, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision1, decimalType6);
		//lp.addNotExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision1, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset5, precision2, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset5, unicodeDecimal, offset5, precision2, decimalType6);
		//lp.addExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset5, finalDecimal, offset5, precision2, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset25, precision15, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset25, unicodeDecimal, offset25, precision15, decimalType6);
		//lp.addExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset25, finalDecimal, offset25, precision15, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset5, precision16, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset5, unicodeDecimal, offset5, precision16, decimalType6);
		//lp.addExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset5, finalDecimal, offset5, precision16, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset50, precision2, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset50, unicodeDecimal, offset50, precision2, decimalType6);
		//lp.addExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset50, finalDecimal, offset50, precision2, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision16, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision16, decimalType6);
		//lp.addExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision16, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision30, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision30, decimalType6);
		//lp.addExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision30, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision50, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision50, decimalType6);
		//lp.addExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision50, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);
	
	}
	
	@Test
	public void testOtherConverters()
	{
		//lp.addCallerName(Utils.getCallingMethod());

		/*
		 * UNICODE_SIGN_EMBEDDED_TRAILING
		 */
		@SuppressWarnings("unused") // Useful to know what we're testing
		String functionName = "UDST Tests";
		
		value = Utils.TestValue.SmallPositive.LongValue;
		dataName = "PackedDecimalToUnicodeDecimal";
		typeName = "UNICODE_SIGN_EMBEDDED_TRAILING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);
		Arrays.fill(finalDecimal, (byte) 0x00);
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType7);
		//lp.addNotExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType7);
		//lp.addNotExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType7);
		//lp.addNotExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType7);
		//lp.addNotExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType7);
		//lp.addNotExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType7);
		//lp.addNotExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.Zero.LongValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision31, decimalType7);
		//lp.addNotExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision31, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision1, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision1, decimalType7);
		//lp.addNotExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision1, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset5, precision2, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset5, unicodeDecimal, offset5, precision2, decimalType7);
		//lp.addNotExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset5, finalDecimal, offset5, precision2, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset25, precision15, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset25, unicodeDecimal, offset25, precision15, decimalType7);
		//lp.addNotExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset25, finalDecimal, offset25, precision15, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset5, precision16, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset5, unicodeDecimal, offset5, precision16, decimalType7);
		//lp.addNotExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset5, finalDecimal, offset5, precision16, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset50, precision2, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset50, unicodeDecimal, offset50, precision2, decimalType7);
		//lp.addNotExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset50, finalDecimal, offset50, precision2, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision16, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision16, decimalType7);
		//lp.addNotExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision16, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision30, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision30, decimalType7);
		//lp.addNotExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision30, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision50, errorChecking);
		//lp.addNotExpected("convertLongToPackedDecimal");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertPackedDecimalToUnicodeDecimal(packedDecimal, offset0, unicodeDecimal, offset0, precision50, decimalType7);
		//lp.addNotExpected("convertPackedDecimalToUnicodeDecimal");
		DecimalData.convertUnicodeDecimalToPackedDecimal(unicodeDecimal, offset0, finalDecimal, offset0, precision50, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToPackedDecimal");
		testName = "Line: " + " no  line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertArrayEquals(testName, packedDecimal, finalDecimal);

		Arrays.fill(packedDecimal, (byte) 0x00);
		Arrays.fill(finalDecimal, (byte) 0x00);
		Arrays.fill(unicodeDecimal, (char)0);
		Arrays.fill(unicodeDecimal, (char)0);

		
		
	}

	@Test
	public void testConvertBigIntegerExceptions()
	{
		//lp.addCallerName(Utils.getCallingMethod());
		final int offset0 = 0;
		final int precision31 = 31;
		final boolean errorChecking = true;
		final boolean errorCheckingFalse = false;
		
		BigInteger value = Utils.TestValue.SmallPositive.BigIntegerValue;
		
		final int decimalType1 = DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING;
		final int decimalType6 = DecimalData.UNICODE_SIGN_SEPARATE_LEADING;
		
		byte[] packedDecimal = new byte[64];
		final int offset25 = 25;
		final int offset50 = 50;
		final int precision100 = 100;
		final int precision50 = 50;
		final int precision5 = 5;
		
		String namePrecisionGreaterThanByteArray = " Precision greater than byte array";
		String namePrecisionAndOffsetGreaterThanByteArray = " Precision and offset greater than byte array";
		String nameErrorChecking = " Error checking";
		String nameNull = " Null";
		String nameInvalidDecimalTypes = " Invalid decimal type";
		String nameOverflow = " Overflow";
		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		String dataName = "BigIntegerToPackedDecimal";
		String typeName = namePrecisionGreaterThanByteArray;
		String testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		byte[] longPackedDecimal = new byte[49];
		byte[] externalDecimal = new byte[49];
		
		try
		{
			DecimalData.convertBigIntegerToPackedDecimal(value, longPackedDecimal, offset0, precision100, errorChecking);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		try
		{
			DecimalData.convertPackedDecimalToBigInteger(longPackedDecimal, offset0, precision100, errorChecking);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		Arrays.fill(longPackedDecimal, (byte) 0x00);
		Arrays.fill(externalDecimal, (byte) 0x00);

		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		typeName = namePrecisionAndOffsetGreaterThanByteArray;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(longPackedDecimal, (byte) 0x00);
		
		try
		{
			DecimalData.convertBigIntegerToPackedDecimal(value, longPackedDecimal, offset50, precision31, errorChecking);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		try
		{
			DecimalData.convertPackedDecimalToBigInteger(longPackedDecimal, offset50, precision31, errorChecking);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}


		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		typeName = nameNull;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		byte[] packedDecimalNull = null;
		
		try
		{
			DecimalData.convertBigIntegerToPackedDecimal(value, packedDecimalNull, offset0, precision31, errorChecking);
			fail(testName);
		}
		catch(NullPointerException e)
		{
			
		}
		try
		{
			DecimalData.convertPackedDecimalToBigInteger(packedDecimalNull, offset0, precision31, errorChecking);
			fail(testName);
		}
		catch(NullPointerException e)
		{
			
		}
		
		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		typeName = nameOverflow;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(packedDecimal, (byte) 0x00);
		try
		{
			DecimalData.convertBigIntegerToPackedDecimal(value, packedDecimal, offset0, precision5, errorChecking);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}
		// Big Integer has huge range
		// probably won't overflow
		/*
		byte[] packedDecimalBiggerThanLong = new byte[64];
		BigInteger biggerThanLong = new BigInteger("99999999999999999999");
		DecimalData.convertBigIntegerToPackedDecimal(biggerThanLong, packedDecimalBiggerThanLong, offset0, precision31, errorChecking);
		try
		{
			DecimalData.convertPackedDecimalToBigInteger(packedDecimalBiggerThanLong, offset0, precision31, errorChecking);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}*/
		
		value = Utils.TestValue.LargePositive.BigIntegerValue;
		typeName = nameErrorChecking;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertBigIntegerToPackedDecimal(value, packedDecimal, offset0, precision5, errorCheckingFalse);
		byte[] correct = new byte[64];
		DecimalData.convertBigIntegerToPackedDecimal(Utils.TestValue.Zero.BigIntegerValue, correct, offset0, precision5, errorCheckingFalse);
		assertArrayEquals(testName, correct, packedDecimal);
		

		/*Arrays.fill(packedDecimal, (byte) 0x00);
		// originally, used overflowing of long/int to test for errorChecking validity, but biginteger probably wont overflow, so dont test
		BigInteger oneMoreThanLongRange = new BigInteger("9223372036854775808"); 
		packedDecimalBiggerThanLong = new byte[64];
		DecimalData.convertBigIntegerToPackedDecimal(oneMoreThanLongRange, packedDecimalBiggerThanLong, offset0, precision31, errorCheckingFalse);
		result = DecimalData.convertPackedDecimalToBigInteger(packedDecimalBiggerThanLong, offset0, precision31, errorCheckingFalse);
		assertEquals(testName, Long.MIN_VALUE, result);*/
		
		/*
		 * convertBigIntegerToExternalDecimal
		 */
		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		dataName = "BigIntegerToExternalDecimal";
		typeName = namePrecisionGreaterThanByteArray;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(longPackedDecimal, (byte) 0x00);
		Arrays.fill(externalDecimal, (byte) 0x00);
		
		try
		{
			DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision100, errorChecking, decimalType1);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		try
		{
			
			DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision100, errorChecking, decimalType1);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		

		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		typeName = namePrecisionAndOffsetGreaterThanByteArray;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		
		try
		{
			DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset25, precision50, errorChecking, decimalType1);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		try
		{
			DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset25, precision50, errorChecking, decimalType1);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}


		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		typeName = nameNull;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		byte[] externalDecimalNull = null;
		
		try
		{
			DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimalNull, offset0, precision31, errorChecking, decimalType1);
			fail(testName);
		}
		catch(NullPointerException e)
		{
			
		}
		
		try
		{
			DecimalData.convertExternalDecimalToBigInteger(externalDecimalNull, offset0, precision31, errorChecking, decimalType1);
			fail(testName);
		}
		catch(NullPointerException e)
		{
			
		}
		
		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		typeName = nameOverflow;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		
		try
		{
			DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision5, errorChecking, decimalType1);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}

		/*biggerThanLong = new BigInteger("99999999999999999999");
		Arrays.fill(externalDecimal, (byte) 0x00);
		DecimalData.convertBigIntegerToExternalDecimal(biggerThanLong, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		try
		{
			DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}*/
		
		value = Utils.TestValue.LargePositive.BigIntegerValue;
		typeName = nameErrorChecking;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + typeName+ " line:"+  " no  line ";

		externalDecimal = new byte[64];
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision5, errorCheckingFalse, decimalType1);
		correct = new byte[64];
		DecimalData.convertBigIntegerToExternalDecimal(Utils.TestValue.Zero.BigIntegerValue, correct, offset0, precision5, errorCheckingFalse, decimalType1);
		assertArrayEquals(testName, correct, externalDecimal);
		

		// this is one larger than Long.MAX_VALUE
		// when converted to packed, then packed to long
		// should overflow into Long.MIN_Value
		/*oneMoreThanLongRange = new BigInteger("9223372036854775808"); 
		Arrays.fill(externalDecimal, (byte) 0x00);
		DecimalData.convertBigIntegerToExternalDecimal(oneMoreThanLongRange, externalDecimal, offset0, precision31, errorCheckingFalse, decimalType1);
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorCheckingFalse, decimalType1);
		assertEquals(testName, Long.MIN_VALUE, result);*/
		
		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		typeName = nameInvalidDecimalTypes;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		
		try
		{
			DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, 50);
			fail(testName);
		}
		catch (IllegalArgumentException e)
		{
			
		}
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		try
		{
			DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, 50);
			fail(testName);
		}
		catch (IllegalArgumentException e)
		{
			
		}
		
		/*
		 * convertBigIntegerToUnicodeDecimal
		 */
		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		dataName = "BigIntegerToUnicodeDecimal";
		typeName = namePrecisionGreaterThanByteArray;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		char[] unicodeDecimal = new char[64];
		
		try
		{
			DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision100, errorChecking, decimalType6);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		try
		{
			DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision100, errorChecking, decimalType6);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		

		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		typeName = namePrecisionAndOffsetGreaterThanByteArray;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(unicodeDecimal, (char)0);
		
		try
		{
			DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset25, precision50, errorChecking, decimalType6);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		try
		{
			DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset25, precision50, errorChecking, decimalType6);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}


		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		typeName = nameNull;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		char[] unicodeDecimalNull = null;
		
		try
		{
			DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimalNull, offset0, precision31, errorChecking, decimalType6);
			fail(testName);
		}
		catch(NullPointerException e)
		{
			
		}
		
		try
		{
			DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimalNull, offset0, precision31, errorChecking, decimalType6);
			fail(testName);
		}
		catch(NullPointerException e)
		{
			
		}
		
		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		typeName = nameOverflow;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + typeName+ " line:"+  " no  line ";

		Arrays.fill(unicodeDecimal, (char)0);
		
		try
		{
			DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision5, errorChecking, decimalType6);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}

		/*biggerThanLong = new BigInteger("99999999999999999999");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(biggerThanLong, unicodeDecimal, offset0, precision31, errorChecking, decimalType9);
		try
		{
			DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType9);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}*/
		
		
		value = Utils.TestValue.LargePositive.BigIntegerValue;
		typeName = nameErrorChecking;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + typeName+ " line:"+  " no  line ";

		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision5, errorCheckingFalse, decimalType6);
		char[] correctUnicode = new char[64];
		DecimalData.convertBigIntegerToUnicodeDecimal(Utils.TestValue.Zero.BigIntegerValue, correctUnicode, offset0, precision5, errorCheckingFalse, decimalType6);
		assertArrayEquals(testName, correctUnicode, unicodeDecimal);
		
		
		// this is one larger than Long.MAX_VALUE
		// when converted to packed, then packed to long
		// should overflow into Long.MIN_Value
		/*oneMoreThanLongRange = new BigInteger("9223372036854775808"); 
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(oneMoreThanLongRange, unicodeDecimal, offset0, precision31, errorCheckingFalse, decimalType9);
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorCheckingFalse, decimalType9);
		assertEquals(testName, Long.MIN_VALUE, result);*/
		
		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		typeName = nameInvalidDecimalTypes;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + typeName+ " line:"+  " no  line ";

		Arrays.fill(unicodeDecimal, (char)0);
		
		try
		{
			DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType1);
			fail(testName);
		}
		catch (IllegalArgumentException e)
		{
			
		}
		
		try
		{
			DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType1);
			fail(testName);
		}
		catch (IllegalArgumentException e)
		{
			
		}
	}
	
	@Test
	public void testConvertBigIntegerNormals()
	{
		//lp.addCallerName(Utils.getCallingMethod());
		/*
		 * this function will test
		 * convertLong to xxxx
		 * convertFromxxx back to long, and assert both equals
		 * 
		 * the values to test will be small positive, large positive, largest possible, smallest possible, small negative, large negative, zero
		 * do two tests for offset/precision
		 * test every single decimal type
		 */
		byte[] externalDecimal = new byte[64];
		final int offset0 = 0;
		final int offset5 = 5;
		final int offset25 = 25;
		final int offset50 = 50;
		final int precision1 = 1;
		final int precision2 = 2;
		final int precision15 = 15;
		final int precision16 = 16;
		final int precision30 = 30;
		final int precision31 = 31;
		final int precision50 = 50;
		final boolean errorChecking = false;
		
		final int decimalType1 = DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING;
		final int decimalType2 = DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING;
		final int decimalType3 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
		final int decimalType4 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
		final int decimalType5 = DecimalData.UNICODE_UNSIGNED;
		final int decimalType6 = DecimalData.UNICODE_SIGN_SEPARATE_LEADING;
		final int decimalType7 = DecimalData.UNICODE_SIGN_SEPARATE_TRAILING;
		
		BigInteger value = Utils.TestValue.SmallPositive.BigIntegerValue;
		String dataName = "BigIntegerToExternalDecimal";
		String typeName = "EBCDIC_SIGN_EMBEDDED_TRAILING";
		String testType = "Normal Value";
		String testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		@SuppressWarnings("unused") // Useful to know what we're testing
		String functionName = "convertBigInteger";
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		BigInteger result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargePositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargestPossible.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallestPossible.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.Zero.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType1);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision1, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		/*DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType1);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset5, precision2, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);*/

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType1);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset25, precision15, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		/*
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType1);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset5, precision16, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);*/

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		/*DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType1);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset50, precision2, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);*/

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType1);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision16, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType1);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision30, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType1);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision50, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		/*
		 * EBCDIC_SIGN_EMBEDDED_LEADING
		 */
		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		typeName = "EBCDIC_SIGN_EMBEDDED_LEADING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargePositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargestPossible.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallestPossible.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.Zero.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType2);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision1, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		/*DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType2);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset5, precision2, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);*/

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		/*DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType2);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset25, precision15, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);*/

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		/*DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType2);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset5, precision16, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);*/

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		/*DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType2);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset50, precision2, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);*/

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType2);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision16, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType2);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision30, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType2);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision50, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		/*
		 * EBCDIC_SIGN_SEPARATE_TRAILING
		 */

		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		typeName = "EBCDIC_SIGN_SEPARATE_TRAILING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargePositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargestPossible.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallestPossible.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.Zero.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType3);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision1, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		/*DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType3);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset5, precision2, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);*/

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		/*DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType3);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset25, precision15, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);*/

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		/*DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType3);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset5, precision16, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);*/

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		/*DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType3);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset50, precision2, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);*/

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType3);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision16, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType3);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision30, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType3);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision50, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		/*
		 * EBCDIC_SIGN_SEPARATE_LEADING
		 */

		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		typeName = "EBCDIC_SIGN_SEPARATE_LEADING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargePositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargestPossible.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallestPossible.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.Zero.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType4);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision1, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		/*DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType4);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset5, precision2, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);*/

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		/*DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType4);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset25, precision15, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);*/

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		/*DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType4);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset5, precision16, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);*/

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		/*DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType4);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset50, precision2, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);*/

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType4);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision16, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType4);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision30, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType4);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigInteger(externalDecimal, offset0, precision50, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		/*
		 * UNICODE_UNSIGNED
		 */
		char[] UnicodeDecimal = new char[128];

		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		typeName = "UNICODE_UNSIGNED";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargePositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		//Unsigned, dont test negatives
		//testName = "Line: " + " no line " + ", " +  "no name";
		//assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		//Unsigned, dont test negatives
		//testName = "Line: " + " no line " + ", " +  "no name";
		//assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargestPossible.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallestPossible.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		
		// negative, unsigned, dont test
		//testName = "Line: " + " no line " + ", " +  "no name";
		//assertEquals(testName, value, result);
		
		value = Utils.TestValue.Zero.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision1, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision1, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset5, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset5, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset25, precision15, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset25, precision15, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargePositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset5, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset5, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset50, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset50, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargePositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision30, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision30, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(UnicodeDecimal, (char)'0');
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision50, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision50, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		/*
		 * UNICODE_SIGN_EMBEDDED_LEADING
		 */
		

		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		typeName = "UNICODE_SIGN_EMBEDDED_LEADING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargePositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargestPossible.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallestPossible.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.Zero.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision1, errorChecking, decimalType6);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision1, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		/*DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset5, precision2, errorChecking, decimalType10);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset5, precision2, errorChecking, decimalType10);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset25, precision15, errorChecking, decimalType10);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset25, precision15, errorChecking, decimalType10);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset5, precision16, errorChecking, decimalType10);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset5, precision16, errorChecking, decimalType10);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset50, precision2, errorChecking, decimalType10);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset50, precision2, errorChecking, decimalType10);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision16, errorChecking, decimalType10);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision16, errorChecking, decimalType10);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision30, errorChecking, decimalType10);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision30, errorChecking, decimalType10);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision50, errorChecking, decimalType10);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision50, errorChecking, decimalType10);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);*/

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		/*
		 * UNICODE_SIGN_EMBEDDED_TRAILING
		 */
		

		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		typeName = "UNICODE_SIGN_EMBEDDED_TRAILING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(UnicodeDecimal, (char)'0');
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargePositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargestPossible.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallestPossible.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.Zero.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision1, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision1, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset5, precision2, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset5, precision2, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset25, precision15, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset25, precision15, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset5, precision16, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset5, precision16, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset50, precision2, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset50, precision2, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision16, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision16, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision30, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision30, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(UnicodeDecimal, (char)'0');
		DecimalData.convertBigIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision50, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(UnicodeDecimal, offset0, precision50, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		
		/*
		 * convertBigIntegerToPackedDecimal
		 */

		

		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		dataName = "BigIntegerToPackedDecimal";
		typeName = "PackedDecimal";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		byte[] packedDecimal = new byte[64];
		DecimalData.convertBigIntegerToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertBigIntegerToPackedDecimal");
		result = DecimalData.convertPackedDecimalToBigInteger(packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertPackedDecimalToBigInteger");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.LargePositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertBigIntegerToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertBigIntegerToExternalDecimal");
		result = DecimalData.convertPackedDecimalToBigInteger(packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertPackedDecimalToBigInteger");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertBigIntegerToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertBigIntegerToPackedDecimal");
		result = DecimalData.convertPackedDecimalToBigInteger(packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertPackedDecimalToBigInteger");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertBigIntegerToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertBigIntegerToPackedDecimal");
		result = DecimalData.convertPackedDecimalToBigInteger(packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertPackedDecimalToBigInteger");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.LargestPossible.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertBigIntegerToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertBigIntegerToPackedDecimal");
		result = DecimalData.convertPackedDecimalToBigInteger(packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertPackedDecimalToBigInteger");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallestPossible.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertBigIntegerToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertBigIntegerToPackedDecimal");
		result = DecimalData.convertPackedDecimalToBigInteger(packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertPackedDecimalToBigInteger");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.Zero.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertBigIntegerToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertBigIntegerToPackedDecimal");
		result = DecimalData.convertPackedDecimalToBigInteger(packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertPackedDecimalToBigInteger");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertBigIntegerToPackedDecimal(value, packedDecimal, offset0, precision1, errorChecking);
		//lp.addExpected("convertBigIntegerToPackedDecimal");
		result = DecimalData.convertPackedDecimalToBigInteger(packedDecimal, offset0, precision1, errorChecking);
		//lp.addExpected("convertPackedDecimalToBigInteger");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertBigIntegerToPackedDecimal(value, packedDecimal, offset5, precision2, errorChecking);
		//lp.addExpected("convertBigIntegerToPackedDecimal");
		result = DecimalData.convertPackedDecimalToBigInteger(packedDecimal, offset5, precision2, errorChecking);
		//lp.addExpected("convertPackedDecimalToBigInteger");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertBigIntegerToPackedDecimal(value, packedDecimal, offset25, precision15, errorChecking);
		//lp.addExpected("convertBigIntegerToPackedDecimal");
		result = DecimalData.convertPackedDecimalToBigInteger(packedDecimal, offset25, precision15, errorChecking);
		//lp.addExpected("convertPackedDecimalToBigInteger");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);

		
		/*
		 * convertBigIntegerToUnicodeDecimal
		 */
		
		/*
		 * UNICODE_UNSIGNED
		 */

		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		typeName = "UNICODE_UNSIGNED";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		char[] unicodeDecimal = new char[128];
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		//Unsigned, dont test negatives
		//testName = "Line: " + " no line " + ", " +  "no name";
		//assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		//Unsigned, dont test negatives
		//testName = "Line: " + " no line " + ", " +  "no name";
		//assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargestPossible.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallestPossible.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		//negative, unsigned, dont test
		//testName = "Line: " + " no line " + ", " +  "no name";
		//assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.Zero.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision1, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision1, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset5, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset5, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset25, precision15, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset25, precision15, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset5, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset5, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset50, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset50, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision30, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision30, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType5);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision50, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		/*
		 * UNICODE_SIGN_EMBEDDED_LEADING
		 */

		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		typeName = "UNICODE_SIGN_EMBEDDED_LEADING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargestPossible.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType6);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision50, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallestPossible.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.Zero.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision1, errorChecking, decimalType6);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision1, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset5, precision2, errorChecking, decimalType6);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset5, precision2, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset25, precision15, errorChecking, decimalType6);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset25, precision15, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset5, precision16, errorChecking, decimalType6);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset5, precision16, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset50, precision2, errorChecking, decimalType6);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset50, precision2, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision16, errorChecking, decimalType6);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision16, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision30, errorChecking, decimalType6);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision30, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType6);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision50, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		/*
		 * UNICODE_SIGN_EMBEDDED_TRAILING
		 */


		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		typeName = "UNICODE_SIGN_EMBEDDED_TRAILING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");

		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");

		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargeNegative.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargestPossible.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision50, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallestPossible.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.Zero.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision1, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision1, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset5, precision2, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset5, precision2, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset25, precision15, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset25, precision15, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset5, precision16, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset5, precision16, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset50, precision2, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset50, precision2, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision16, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision16, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision30, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision30, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallPositive.BigIntegerValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType7);
		//lp.addExpected("convertBigIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigInteger(unicodeDecimal, offset0, precision50, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigInteger");
		
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

	}


	@Test
	public void testConvertBigDecimalExceptions()
	{
		//lp.addCallerName(Utils.getCallingMethod());
		final int offset0 = 0;
		final int precision31 = 31;
		final boolean errorChecking = true;
		final boolean errorCheckingFalse = false;
		
		BigDecimal value = Utils.TestValue.SmallPositive.BigDecimalValue;
		
		final int decimalType1 = DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING;
		final int decimalType6 = DecimalData.UNICODE_SIGN_SEPARATE_LEADING;
		
		byte[] packedDecimal = new byte[64];
		final int offset25 = 25;
		final int offset50 = 50;
		final int precision100 = 100;
		final int precision50 = 50;
		final int precision5 = 5;
		
		final int scale0 = 0;
				
		String namePrecisionGreaterThanByteArray = " Precision greater than byte array";
		String namePrecisionAndOffsetGreaterThanByteArray = " Precision and offset greater than byte array";
		String nameErrorChecking = " Error checking";
		String nameNull = " Null";
		String nameInvalidDecimalTypes = " Invalid decimal type";
		String nameOverflow = " Overflow";
		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		String dataName = "BigDecimalToPackedDecimal";
		String typeName = namePrecisionGreaterThanByteArray;
		String testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		byte[] longPackedDecimal = new byte[49];
		byte[] externalDecimal = new byte[49];
		
		try
		{
			DecimalData.convertBigDecimalToPackedDecimal(value, longPackedDecimal, offset0, precision100, errorChecking);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		try
		{
			DecimalData.convertPackedDecimalToBigDecimal(longPackedDecimal, offset0, precision100, scale0, errorChecking);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		Arrays.fill(longPackedDecimal, (byte) 0x00);
		Arrays.fill(longPackedDecimal, (byte) 0x00);

		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		typeName = namePrecisionAndOffsetGreaterThanByteArray;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(longPackedDecimal, (byte) 0x00);
		
		try
		{
			DecimalData.convertBigDecimalToPackedDecimal(value, longPackedDecimal, offset50, precision31, errorChecking);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		try
		{
			DecimalData.convertPackedDecimalToBigDecimal(longPackedDecimal, offset50, precision31, scale0, errorChecking);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}


		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		typeName = nameNull;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		byte[] packedDecimalNull = null;
		
		try
		{
			DecimalData.convertBigDecimalToPackedDecimal(value, packedDecimalNull, offset0, precision31, errorChecking);
			fail(testName);
		}
		catch(NullPointerException e)
		{
			
		}
		try
		{
			DecimalData.convertPackedDecimalToBigDecimal(packedDecimalNull, offset0, precision31, scale0, errorChecking);
			fail(testName);
		}
		catch(NullPointerException e)
		{
			
		}
		
		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		typeName = nameOverflow;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(packedDecimal, (byte) 0x00);
		try
		{
			DecimalData.convertBigDecimalToPackedDecimal(value, packedDecimal, offset0, precision5, errorChecking);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}
		// Big Integer has huge range
		// probably won't overflow
		/*
		byte[] packedDecimalBiggerThanLong = new byte[64];
		BigInteger biggerThanLong = new BigInteger("99999999999999999999");
		DecimalData.convertBigIntegerToPackedDecimal(biggerThanLong, packedDecimalBiggerThanLong, offset0, precision31, errorChecking);
		try
		{
			DecimalData.convertPackedDecimalToBigDecimal(packedDecimalBiggerThanLong, offset0, precision31, scale0, errorChecking);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}*/
		
		value = Utils.TestValue.LargePositive.BigDecimalValue;
		typeName = nameErrorChecking;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertBigDecimalToPackedDecimal(value, packedDecimal, offset0, precision5, errorCheckingFalse);
		byte[] correct = new byte[64];
		DecimalData.convertBigDecimalToPackedDecimal(Utils.TestValue.Zero.BigDecimalValue, correct, offset0, precision5, errorCheckingFalse);
		assertArrayEquals(testName, correct, packedDecimal);
		

		/*Arrays.fill(packedDecimal, (byte) 0x00);
		// this is one larger than Long.MAX_VALUE
		// when converted to packed, then packed to long
		// should overflow into Long.MIN_Value
		BigInteger oneMoreThanLongRange = new BigInteger("9223372036854775808"); 
		packedDecimalBiggerThanLong = new byte[64];
		DecimalData.convertBigIntegerToPackedDecimal(oneMoreThanLongRange, packedDecimalBiggerThanLong, offset0, precision31, errorCheckingFalse);
		result = DecimalData.convertPackedDecimalToBigDecimal(packedDecimalBiggerThanLong, offset0, precision31, scale0, errorCheckingFalse);
		assertEquals(testName, Long.MIN_VALUE, result);*/
		
		/*
		 * convertBigDecimalToExternalDecimal
		 */
		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		dataName = "BigDecimalToExternalDecimal";
		typeName = namePrecisionGreaterThanByteArray;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(longPackedDecimal, (byte) 0x00);
		Arrays.fill(externalDecimal, (byte) 0x00);
		
		try
		{
			DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision100, errorChecking, decimalType1);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		try
		{
			
			DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision100, scale0, errorChecking, decimalType1);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		

		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		typeName = namePrecisionAndOffsetGreaterThanByteArray;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		
		try
		{
			DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset25, precision50, errorChecking, decimalType1);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		try
		{
			DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset25, precision50, scale0, errorChecking, decimalType1);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}


		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		typeName = nameNull;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		byte[] externalDecimalNull = null;
		
		try
		{
			DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimalNull, offset0, precision31, errorChecking, decimalType1);
			fail(testName);
		}
		catch(NullPointerException e)
		{
			
		}
		
		try
		{
			DecimalData.convertExternalDecimalToBigDecimal(externalDecimalNull, offset0, precision31, scale0, errorChecking, decimalType1);
			fail(testName);
		}
		catch(NullPointerException e)
		{
			
		}
		
		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		typeName = nameOverflow;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		
		try
		{
			DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision5, errorChecking, decimalType1);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}

		/*biggerThanLong = new BigInteger("99999999999999999999");
		Arrays.fill(externalDecimal, (byte) 0x00);
		DecimalData.convertBigIntegerToExternalDecimal(biggerThanLong, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		try
		{
			DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType1);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}*/
		
		value = Utils.TestValue.LargePositive.BigDecimalValue;
		typeName = nameErrorChecking;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + typeName+ " line:"+  " no  line ";

		externalDecimal = new byte[64];
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision5, errorCheckingFalse, decimalType1);
		correct = new byte[64];
		DecimalData.convertBigDecimalToExternalDecimal(Utils.TestValue.Zero.BigDecimalValue, correct, offset0, precision5, errorCheckingFalse, decimalType1);
		assertArrayEquals(testName, correct, externalDecimal);
		

		// this is one larger than Long.MAX_VALUE
		// when converted to packed, then packed to long
		// should overflow into Long.MIN_Value
		/*oneMoreThanLongRange = new BigInteger("9223372036854775808"); 
		Arrays.fill(externalDecimal, (byte) 0x00);
		DecimalData.convertBigIntegerToExternalDecimal(oneMoreThanLongRange, externalDecimal, offset0, precision31, errorCheckingFalse, decimalType1);
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorCheckingFalse, decimalType1);
		assertEquals(testName, Long.MIN_VALUE, result);*/
		
		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		typeName = nameInvalidDecimalTypes;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		
		try
		{
			DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, 50);
			fail(testName);
		}
		catch (IllegalArgumentException e)
		{
			
		}
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		try
		{
			DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, 50);
			fail(testName);
		}
		catch (IllegalArgumentException e)
		{
			
		}
		
		/*
		 * convertBigDecimalToUnicodeDecimal
		 */
		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		dataName = "BigDecimalToUnicodeDecimal";
		typeName = namePrecisionGreaterThanByteArray;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		char[] unicodeDecimal = new char[64];
		
		try
		{
			DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision100, errorChecking, decimalType6);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		try
		{
			DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision100, scale0, errorChecking, decimalType6);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		

		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		typeName = namePrecisionAndOffsetGreaterThanByteArray;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(unicodeDecimal, (char)0);
		
		try
		{
			DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset25, precision50, errorChecking, decimalType6);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		try
		{
			DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset25, precision50, scale0, errorChecking, decimalType6);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}


		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		typeName = nameNull;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		char[] unicodeDecimalNull = null;
		
		try
		{
			DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimalNull, offset0, precision31, errorChecking, decimalType6);
			fail(testName);
		}
		catch(NullPointerException e)
		{
			
		}
		
		try
		{
			DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimalNull, offset0, precision31, scale0, errorChecking, decimalType6);
			fail(testName);
		}
		catch(NullPointerException e)
		{
			
		}
		
		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		typeName = nameOverflow;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + typeName+ " line:"+  " no  line ";

		Arrays.fill(unicodeDecimal, (char)0);
		
		try
		{
			DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision5, errorChecking, decimalType6);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}

		/*biggerThanLong = new BigInteger("99999999999999999999");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(biggerThanLong, unicodeDecimal, offset0, precision31, errorChecking, decimalType9);
		try
		{
			DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType9);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}*/
		
		
		value = Utils.TestValue.LargePositive.BigDecimalValue;
		typeName = nameErrorChecking;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + typeName+ " line:"+  " no  line ";

		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision5, errorCheckingFalse, decimalType6);
		char[] correctUnicode = new char[64];
		DecimalData.convertBigDecimalToUnicodeDecimal(Utils.TestValue.Zero.BigDecimalValue, correctUnicode, offset0, precision5, errorCheckingFalse, decimalType6);
		assertArrayEquals(testName, correctUnicode, unicodeDecimal);
		
		
		// this is one larger than Long.MAX_VALUE
		// when converted to packed, then packed to long
		// should overflow into Long.MIN_Value
		/*oneMoreThanLongRange = new BigInteger("9223372036854775808"); 
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(oneMoreThanLongRange, unicodeDecimal, offset0, precision31, errorCheckingFalse, decimalType9);
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorCheckingFalse, decimalType9);
		assertEquals(testName, Long.MIN_VALUE, result);*/
		
		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		typeName = nameInvalidDecimalTypes;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + typeName+ " line:"+  " no  line ";

		Arrays.fill(unicodeDecimal, (char)0);
		
		try
		{
			DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType1);
			fail(testName);
		}
		catch (IllegalArgumentException e)
		{
			
		}
		
		try
		{
			DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType1);
			fail(testName);
		}
		catch (IllegalArgumentException e)
		{
			
		}
	}
	
	@Test
	public void testConvertBigDecimalNormals()
	{
		//lp.addCallerName(Utils.getCallingMethod());
		/*
		 * this function will test
		 * convertLong to xxxx
		 * convertFromxxx back to long, and assert both equals
		 * 
		 * the values to test will be small positive, large positive, largest possible, smallest possible, small negative, large negative, zero
		 * do two tests for offset/precision
		 * test every single decimal type
		 */
		byte[] externalDecimal = new byte[64];
		final int offset0 = 0;
		final int offset5 = 5;
		final int offset25 = 25;
		final int offset50 = 50;
		final int precision1 = 1;
		final int precision2 = 2;
		final int precision15 = 15;
		final int precision16 = 16;
		final int precision30 = 30;
		final int precision31 = 31;
		final int precision50 = 50;
		final boolean errorChecking = false;
		
		final int scale0 = 0;
		
		final int decimalType1 = DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING;
		final int decimalType2 = DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING;
		final int decimalType3 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
		final int decimalType4 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
		final int decimalType5 = DecimalData.UNICODE_UNSIGNED;
		final int decimalType6 = DecimalData.UNICODE_SIGN_SEPARATE_LEADING;
		final int decimalType7 = DecimalData.UNICODE_SIGN_SEPARATE_TRAILING;
		
		BigDecimal value = Utils.TestValue.SmallPositive.BigDecimalValue;
		String dataName = "BigDecimalToExternalDecimal";
		String typeName = "EBCDIC_SIGN_EMBEDDED_TRAILING";
		String testType = "Normal Value";
		String testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		@SuppressWarnings("unused") // Useful to know what we're testing
		String functionName = "convertBigDecimalToExternalDecimal/convertExternalDecimalToBigDecimal";
				
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		BigDecimal result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargePositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargestPossible.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallestPossible.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.Zero.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType1);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision1, scale0, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType1);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset5, precision2, scale0, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType1);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset25, precision15, scale0, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType1);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset5, precision16, scale0, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType1);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset50, precision2, scale0, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType1);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision16, scale0, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType1);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision30, scale0, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType1);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision50, scale0, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		/*
		 * EBCDIC_SIGN_EMBEDDED_LEADING
		 */
		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		typeName = "EBCDIC_SIGN_EMBEDDED_LEADING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargePositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargestPossible.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallestPossible.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.Zero.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType2);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision1, scale0, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType2);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset5, precision2, scale0, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType2);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset25, precision15, scale0, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType2);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset5, precision16, scale0, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType2);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset50, precision2, scale0, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType2);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision16, scale0, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType2);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision30, scale0, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType2);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision50, scale0, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		/*
		 * EBCDIC_SIGN_SEPARATE_TRAILING
		 */

		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		typeName = "EBCDIC_SIGN_SEPARATE_TRAILING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargePositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargestPossible.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallestPossible.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.Zero.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType3);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision1, scale0, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType3);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset5, precision2, scale0, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType3);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset25, precision15, scale0, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType3);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset5, precision16, scale0, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType3);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset50, precision2, scale0, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType3);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision16, scale0, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType3);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision30, scale0, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType3);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision50, scale0, errorChecking, decimalType3);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		/*
		 * EBCDIC_SIGN_SEPARATE_LEADING
		 */

		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		typeName = "EBCDIC_SIGN_SEPARATE_LEADING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargePositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargestPossible.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallestPossible.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.Zero.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision31, scale0, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType4);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision1, scale0, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType4);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset5, precision2, scale0, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType4);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset25, precision15, scale0, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType4);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset5, precision16, scale0, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType4);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset50, precision2, scale0, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType4);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision16, scale0, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType4);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision30, scale0, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType4);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertExternalDecimalToBigDecimal(externalDecimal, offset0, precision50, scale0, errorChecking, decimalType4);
		//lp.addExpected("convertExternalDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		/*
		 * UNICODE_UNSIGNED
		 */
		char[] UnicodeDecimal = new char[100];

		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		typeName = "UNICODE_UNSIGNED";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargePositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		//Unsigned, dont test negatives
		//testName = "Line: " + " no line " + ", " +  "no name";
		//assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		//Unsigned, dont test negatives
		//testName = "Line: " + " no line " + ", " +  "no name";
		//assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargestPossible.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallestPossible.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		//assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.Zero.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision1, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision1, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset5, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset5, precision2, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset25, precision15, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset25, precision15, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargePositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset5, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset5, precision16, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset50, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset50, precision2, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargePositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision16, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision30, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision30, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(UnicodeDecimal, (char)'0');
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision50, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision50, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		/*
		 * UNICODE_SIGN_EMBEDDED_LEADING
		 */
		

		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		typeName = "UNICODE_SIGN_EMBEDDED_LEADING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargePositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargestPossible.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallestPossible.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.Zero.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision1, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision1, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset5, precision2, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset5, precision2, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset25, precision15, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset25, precision15, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset5, precision16, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset5, precision16, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset50, precision2, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset50, precision2, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision16, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision16, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision30, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision30, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision50, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision50, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		/*
		 * UNICODE_SIGN_EMBEDDED_TRAILING
		 */
		

		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		typeName = "UNICODE_SIGN_EMBEDDED_TRAILING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(UnicodeDecimal, (char)'0');
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargePositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargestPossible.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallestPossible.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.Zero.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision1, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision1, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset5, precision2, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset5, precision2, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset25, precision15, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset25, precision15, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset5, precision16, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset5, precision16, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset50, precision2, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset50, precision2, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision16, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision16, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision30, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision30, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(UnicodeDecimal, (char)'0');
		DecimalData.convertBigDecimalToUnicodeDecimal(value, UnicodeDecimal, offset0, precision50, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(UnicodeDecimal, offset0, precision50, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		
		/*
		 * convertBigDecimalToPackedDecimal
		 */

		

		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		dataName = "BigDecimalToPackedDecimal";
		typeName = "PackedDecimal";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		byte[] packedDecimal = new byte[64];
		DecimalData.convertBigDecimalToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertBigDecimalToPackedDecimal");
		result = DecimalData.convertPackedDecimalToBigDecimal(packedDecimal, offset0, precision31, scale0, errorChecking);
		//lp.addExpected("convertPackedDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.LargePositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertBigDecimalToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertBigDecimalToExternalDecimal");
		result = DecimalData.convertPackedDecimalToBigDecimal(packedDecimal, offset0, precision31, scale0, errorChecking);
		//lp.addExpected("convertPackedDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertBigDecimalToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertBigDecimalToPackedDecimal");
		result = DecimalData.convertPackedDecimalToBigDecimal(packedDecimal, offset0, precision31, scale0, errorChecking);
		//lp.addExpected("convertPackedDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertBigDecimalToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertBigDecimalToPackedDecimal");
		result = DecimalData.convertPackedDecimalToBigDecimal(packedDecimal, offset0, precision31, scale0, errorChecking);
		//lp.addExpected("convertPackedDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.LargestPossible.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertBigDecimalToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertBigDecimalToPackedDecimal");
		result = DecimalData.convertPackedDecimalToBigDecimal(packedDecimal, offset0, precision31, scale0, errorChecking);
		//lp.addExpected("convertPackedDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallestPossible.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertBigDecimalToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertBigDecimalToPackedDecimal");
		result = DecimalData.convertPackedDecimalToBigDecimal(packedDecimal, offset0, precision31, scale0, errorChecking);
		//lp.addExpected("convertPackedDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.Zero.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertBigDecimalToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertBigDecimalToPackedDecimal");
		result = DecimalData.convertPackedDecimalToBigDecimal(packedDecimal, offset0, precision31, scale0, errorChecking);
		//lp.addExpected("convertPackedDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertBigDecimalToPackedDecimal(value, packedDecimal, offset0, precision1, errorChecking);
		//lp.addExpected("convertBigDecimalToPackedDecimal");
		result = DecimalData.convertPackedDecimalToBigDecimal(packedDecimal, offset0, precision1, scale0, errorChecking);
		//lp.addExpected("convertPackedDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertBigDecimalToPackedDecimal(value, packedDecimal, offset5, precision2, errorChecking);
		//lp.addExpected("convertBigDecimalToPackedDecimal");
		result = DecimalData.convertPackedDecimalToBigDecimal(packedDecimal, offset5, precision2, scale0, errorChecking);
		//lp.addExpected("convertPackedDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertBigDecimalToPackedDecimal(value, packedDecimal, offset25, precision15, errorChecking);
		//lp.addExpected("convertBigDecimalToPackedDecimal");
		result = DecimalData.convertPackedDecimalToBigDecimal(packedDecimal, offset25, precision15, scale0, errorChecking);
		//lp.addExpected("convertPackedDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);

		
		/*
		 * convertBigDecimalToUnicodeDecimal
		 */
		
		/*
		 * UNICODE_UNSIGNED
		 */

		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		typeName = "UNICODE_UNSIGNED";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		char[] unicodeDecimal = new char[128];
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		//Unsigned, dont test negatives
		//testName = "Line: " + " no line " + ", " +  "no name";
		//assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		//Unsigned, dont test negatives
		//testName = "Line: " + " no line " + ", " +  "no name";
		//assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargestPossible.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallestPossible.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		//assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.Zero.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision1, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision1, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset5, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset5, precision2, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset25, precision15, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset25, precision15, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset5, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset5, precision16, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset50, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset50, precision2, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision16, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision30, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision30, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType5);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision50, scale0, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		/*
		 * UNICODE_SIGN_EMBEDDED_LEADING
		 */

		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		typeName = "UNICODE_SIGN_EMBEDDED_LEADING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargestPossible.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision50, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallestPossible.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.Zero.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision1, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision1, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset5, precision2, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset5, precision2, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset25, precision15, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset25, precision15, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset5, precision16, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset5, precision16, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset50, precision2, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset50, precision2, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision16, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision16, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision30, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision30, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType6);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision50, scale0, errorChecking, decimalType6);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		/*
		 * UNICODE_SIGN_EMBEDDED_TRAILING
		 */


		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		typeName = "UNICODE_SIGN_EMBEDDED_TRAILING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargeNegative.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargestPossible.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision50, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallestPossible.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.Zero.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision31, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision1, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision1, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset5, precision2, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset5, precision2, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset25, precision15, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset25, precision15, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset5, precision16, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset5, precision16, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset50, precision2, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset50, precision2, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision16, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision16, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision30, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision30, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallPositive.BigDecimalValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigDecimalToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType7);
		//lp.addExpected("convertBigDecimalToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToBigDecimal(unicodeDecimal, offset0, precision50, scale0, errorChecking, decimalType7);
		//lp.addExpected("convertUnicodeDecimalToBigDecimal");
		
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

	}


	@Test
	public void testConvertIntegerNormals()
	{
		//lp.addCallerName(Utils.getCallingMethod());
		/*
		 * this function will test
		 * convertInteger to xxxx
		 * convertFromxxx back to long, and assert both equals
		 * 
		 * the values to test will be small positive, large positive, largest possible, smallest possible, small negative, large negative, zero
		 * do two tests for offset/precision
		 * test every single decimal type
		 */
		byte[] externalDecimal = new byte[64];
		final int offset0 = 0;
		final int offset5 = 5;
		final int offset25 = 25;
		final int offset50 = 50;
		final int precision1 = 1;
		final int precision2 = 2;
		final int precision15 = 15;
		final int precision16 = 16;
		final int precision30 = 30;
		final int precision31 = 31;
		final int precision50 = 50;
		final boolean errorChecking = false;
		
		final int decimalType1 = DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING;
		final int decimalType2 = DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING;
		final int decimalType3 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
		final int decimalType4 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
		final int decimalType5 = DecimalData.UNICODE_UNSIGNED;
		final int decimalType6 = DecimalData.UNICODE_SIGN_SEPARATE_LEADING;
		final int decimalType7 = DecimalData.UNICODE_SIGN_SEPARATE_TRAILING;
		
		int value = Utils.TestValue.SmallPositive.IntValue;
		String dataName = "IntegerToExternalDecimal";
		String typeName = "EBCDIC_SIGN_EMBEDDED_TRAILING";
		String testType = "Normal Value";
		String testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		@SuppressWarnings("unused") // Useful to know what we're testing
		String functionName = "convertInteger";
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertIntegerToExternalDecimal");
		int result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargePositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargestPossible.IntValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallestPossible.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.Zero.IntValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType1);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision1, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType1);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset5, precision2, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType1);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset25, precision15, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType1);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset5, precision16, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType1);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset50, precision2, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType1);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision16, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType1);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision30, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType1);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision50, errorChecking, decimalType1);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		/*
		 * EBCDIC_SIGN_EMBEDDED_LEADING
		 */
		
		value = Utils.TestValue.SmallPositive.IntValue;
		typeName = "EBCDIC_SIGN_EMBEDDED_LEADING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargePositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);
		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargestPossible.IntValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallestPossible.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.Zero.IntValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType2);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision1, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType2);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset5, precision2, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType2);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset25, precision15, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType2);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset5, precision16, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType2);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset50, precision2, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType2);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision16, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType2);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision30, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType2);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision50, errorChecking, decimalType2);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		/*
		 * EBCDIC_SIGN_SEPARATE_TRAILING
		 */

		value = Utils.TestValue.SmallPositive.IntValue;
		typeName = "EBCDIC_SIGN_SEPARATE_TRAILING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargePositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargestPossible.IntValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallestPossible.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.Zero.IntValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType3);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision1, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType3);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset5, precision2, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType3);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset25, precision15, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType3);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset5, precision16, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType3);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset50, precision2, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType3);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision16, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType3);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision30, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType3);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision50, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		/*
		 * EBCDIC_SIGN_SEPARATE_LEADING
		 */

		value = Utils.TestValue.SmallPositive.IntValue;
		typeName = "EBCDIC_SIGN_SEPARATE_LEADING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargePositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargestPossible.IntValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallestPossible.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.Zero.IntValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType4);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision1, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType4);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset5, precision2, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType4);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset25, precision15, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType4);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset5, precision16, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType4);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset50, precision2, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType4);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision16, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType4);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision30, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType4);
		//lp.addNotExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision50, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		
		/*
		 * UNICODE_UNSIGNED
		 */
		char[] UnicodeDecimal = new char[100];

		value = Utils.TestValue.SmallPositive.IntValue;
		typeName = "UNICODE_UNSIGNED";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargePositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		//Unsigned, dont test negatives
		//testName = "Line: " + " no line " + ", " +  "no name";
		//assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		//Unsigned, dont test negatives
		//testName = "Line: " + " no line " + ", " +  "no name";
		//assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargestPossible.IntValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallestPossible.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		// don't test, negative value
		/*DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType9);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType9);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);*/

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.Zero.IntValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision1, errorChecking, decimalType5);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision1, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(UnicodeDecimal, (char)'0');
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset5, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset5, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset25, precision15, errorChecking, decimalType5);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset25, precision15, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargePositive.IntValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset5, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset5, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset50, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset50, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargePositive.IntValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision30, errorChecking, decimalType5);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision30, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision50, errorChecking, decimalType5);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision50, errorChecking, decimalType5);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		
		/*
		 * UNICODE_SIGN_EMBEDDED_LEADING
		 */
		

		value = Utils.TestValue.SmallPositive.IntValue;
		typeName = "UNICODE_SIGN_EMBEDDED_LEADING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(UnicodeDecimal, (char)'0');
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargePositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargestPossible.IntValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallestPossible.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.Zero.IntValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision1, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision1, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset5, precision2, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset5, precision2, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset25, precision15, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset25, precision15, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset5, precision16, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset5, precision16, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset50, precision2, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset50, precision2, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision16, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision16, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision30, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision30, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(UnicodeDecimal, (char)'0');
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision50, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision50, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		/*
		 * UNICODE_SIGN_EMBEDDED_TRAILING
		 */
		

		value = Utils.TestValue.SmallPositive.IntValue;
		typeName = "UNICODE_SIGN_EMBEDDED_TRAILING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(UnicodeDecimal, (char)'0');
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargePositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargestPossible.IntValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallestPossible.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.Zero.IntValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision1, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision1, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset5, precision2, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset5, precision2, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset25, precision15, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset25, precision15, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset5, precision16, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset5, precision16, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset50, precision2, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset50, precision2, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision16, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision16, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision30, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision30, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(UnicodeDecimal, (char)'0');
		DecimalData.convertIntegerToUnicodeDecimal(value, UnicodeDecimal, offset0, precision50, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(UnicodeDecimal, offset0, precision50, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		
		/*
		 * convertIntegerToPackedDecimal
		 */

		final int precision = 6;
		

		//value = Utils.TestValue.SmallPositive.IntValue;
		value = 12345;
		dataName = "IntegerToPackedDecimal";
		typeName = "PackedDecimal";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		byte[] packedDecimal = new byte[64];
		DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, offset0, precision, true);
		//lp.addExpected("convertIntegerToPackedDecimal");
		result = DecimalData.convertPackedDecimalToInteger(packedDecimal, offset0, precision, errorChecking);
		//lp.addExpected("convertPackedDecimalToInteger");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(packedDecimal, (byte)0x00);


		/*
		value = Utils.TestValue.LargePositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(packedDecimal, (char)'0');
		DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertIntegerToExternalDecimal");
		result = DecimalData.convertPackedDecimalToInteger(packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertPackedDecimalToInteger");
		testName = "Line: " + " no line " + ", "  + "no name";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertIntegerToPackedDecimal");
		result = DecimalData.convertPackedDecimalToInteger(packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertPackedDecimalToInteger");
		testName = "Line: " + " no line " + ", "  + "no name";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertIntegerToPackedDecimal");
		result = DecimalData.convertPackedDecimalToInteger(packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertPackedDecimalToInteger");
		testName = "Line: " + " no line " + ", "  + "no name";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.LargestPossible.IntValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertIntegerToPackedDecimal");
		result = DecimalData.convertPackedDecimalToInteger(packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertPackedDecimalToInteger");
		testName = "Line: " + " no line " + ", "  + "no name";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallestPossible.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertIntegerToPackedDecimal");
		result = DecimalData.convertPackedDecimalToInteger(packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertPackedDecimalToInteger");
		testName = "Line: " + " no line " + ", "  + "no name";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.Zero.IntValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertIntegerToPackedDecimal");
		result = DecimalData.convertPackedDecimalToInteger(packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertPackedDecimalToInteger");
		testName = "Line: " + " no line " + ", "  + "no name";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, offset0, precision1, errorChecking);
		//lp.addExpected("convertIntegerToPackedDecimal");
		result = DecimalData.convertPackedDecimalToInteger(packedDecimal, offset0, precision1, errorChecking);
		//lp.addExpected("convertPackedDecimalToInteger");
		testName = "Line: " + " no line " + ", "  + "no name";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, offset5, precision2, errorChecking);
		//lp.addExpected("convertIntegerToPackedDecimal");
		result = DecimalData.convertPackedDecimalToInteger(packedDecimal, offset5, precision2, errorChecking);
		//lp.addExpected("convertPackedDecimalToInteger");
		testName = "Line: " + " no line " + ", "  + "no name";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, offset25, precision15, errorChecking);
		//lp.addExpected("convertIntegerToPackedDecimal");
		result = DecimalData.convertPackedDecimalToInteger(packedDecimal, offset25, precision15, errorChecking);
		//lp.addExpected("convertPackedDecimalToInteger");
		testName = "Line: " + " no line " + ", "  + "no name";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);
		*/

		
		/*
		 * convertIntegerToUnicodeDecimal
		 */
		
		/*
		 * UNICODE_UNSIGNED
		 */

		
		value = Utils.TestValue.SmallPositive.IntValue;
		typeName = "UNICODE_UNSIGNED";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		char[] unicodeDecimal = new char[128];
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		//Unsigned, dont test negatives
		//testName = "Line: " + " no line " + ", " +  "no name";
		//assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		//Unsigned, dont test negatives
		//testName = "Line: " + " no line " + ", " +  "no name";
		//assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargestPossible.IntValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallestPossible.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;
		// Don't test negative unsigned
		/*
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType9);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType9);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);*/

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.Zero.IntValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision1, errorChecking, decimalType5);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision1, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset5, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset5, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset25, precision15, errorChecking, decimalType5);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset25, precision15, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.IntValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset5, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset5, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset50, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset50, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.IntValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision30, errorChecking, decimalType5);
		//lp.addExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision30, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType5);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision50, errorChecking, decimalType5);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		/*
		 * UNICODE_SIGN_EMBEDDED_LEADING
		 */

		value = Utils.TestValue.SmallPositive.IntValue;
		typeName = "UNICODE_SIGN_EMBEDDED_LEADING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargestPossible.IntValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision50, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallestPossible.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.Zero.IntValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision1, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision1, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset5, precision2, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset5, precision2, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset25, precision15, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset25, precision15, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.IntValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset5, precision16, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset5, precision16, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset50, precision2, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset50, precision2, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.IntValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision16, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision16, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision30, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision30, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType6);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision50, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		/*
		 * UNICODE_SIGN_EMBEDDED_TRAILING
		 */


		value = Utils.TestValue.SmallPositive.IntValue;
		typeName = "UNICODE_SIGN_EMBEDDED_TRAILING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargeNegative.IntValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargestPossible.IntValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision50, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallestPossible.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.Zero.IntValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision1, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision1, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset5, precision2, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset5, precision2, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset25, precision15, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset25, precision15, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.IntValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset5, precision16, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset5, precision16, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset50, precision2, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset50, precision2, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.IntValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision16, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision16, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision30, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision30, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallPositive.IntValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType7);
		//lp.addNotExpected("convertIntegerToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision50, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToInteger");
		
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

	}


	@Test
	public void testConvertIntegerExceptions()
	{
		//lp.addCallerName(Utils.getCallingMethod());

		/*
		 * test offset, precision out of bounds
		 * test overflow
		 * test error checking
		 * test null array
		 * test invalid decimal type
		 * test invalid data
		 */

		final int offset0 = 0;
		final int precision31 = 31;
		final boolean errorChecking = true;
		final boolean errorCheckingFalse = false;
		
		int value = Utils.TestValue.SmallPositive.IntValue;
		
		final int decimalType1 = DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING;
		final int decimalType2 = DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING;
		final int decimalType3 = DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING;
		final int decimalType4 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
		final int decimalType5 = DecimalData.UNICODE_UNSIGNED;
		final int decimalType6 = DecimalData.UNICODE_SIGN_SEPARATE_LEADING;
		final int decimalType7 = DecimalData.UNICODE_SIGN_SEPARATE_TRAILING;
		
		final int offset25 = 25;
		final int precision5 = 5;
		int result;
				
		String namePrecisionGreaterThanByteArray = " Precision greater than byte array";
		String namePrecisionAndOffsetGreaterThanByteArray = " Precision and offset greater than byte array";
		String namePrecisionOffsetAndDecTypeGreaterThanByteArray = " Precision, offset and decimal type greater than byte array";
		String nameErrorChecking = " Error checking";
		String nameNull = " Null";
		String nameInvalidDecimalTypes = " Invalid decimal type";
		String nameOverflow = " Overflow";

		value = Utils.TestValue.SmallPositive.IntValue;
		String dataName = "IntegerToPackedDecimal";
		String typeName = namePrecisionGreaterThanByteArray;
		String testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		int arrSize = 49;
		byte[] longPackedDecimal = new byte[arrSize];
		byte[] externalDecimal = new byte[arrSize];
		byte[] packedDecimal = new byte[arrSize];
		
		try
		{
			DecimalData.convertIntegerToPackedDecimal(value, longPackedDecimal, offset0, arrSize*2, errorChecking);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		try
		{
			DecimalData.convertPackedDecimalToInteger(longPackedDecimal, offset0, arrSize*2, errorChecking);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		Arrays.fill(longPackedDecimal, (byte) 0x00);
		Arrays.fill(externalDecimal, (byte) 0x00);

		value = Utils.TestValue.SmallPositive.IntValue;
		typeName = namePrecisionAndOffsetGreaterThanByteArray;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(longPackedDecimal, (byte) 0x00);
		
		try
		{
			DecimalData.convertIntegerToPackedDecimal(value, longPackedDecimal, offset25, (arrSize - offset25)*2, errorChecking);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		try
		{
			DecimalData.convertPackedDecimalToInteger(longPackedDecimal, offset25, (arrSize - offset25)*2, errorChecking);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}


		value = Utils.TestValue.SmallPositive.IntValue;
		typeName = nameNull;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		byte[] packedDecimalNull = null;
		
		try
		{
			DecimalData.convertIntegerToPackedDecimal(value, packedDecimalNull, offset0, precision31, errorChecking);
			fail(testName);
		}
		catch(NullPointerException e)
		{
			
		}
		try
		{
			DecimalData.convertPackedDecimalToInteger(packedDecimalNull, offset0, precision31, errorChecking);
			fail(testName);
		}
		catch(NullPointerException e)
		{
			
		}
		
		value = Utils.TestValue.LargeNegative.IntValue;
		typeName = nameOverflow;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(packedDecimal, (byte) 0x00);
		try
		{
			DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, offset0, String.valueOf(value).length()-2, errorChecking);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}
		byte[] packedDecimalBiggerThanLong = new byte[arrSize];
		BigInteger biggerThanLong = new BigInteger("99999999999999999999");
		DecimalData.convertBigIntegerToPackedDecimal(biggerThanLong, packedDecimalBiggerThanLong, offset0, precision31, errorChecking);
		try
		{
			DecimalData.convertPackedDecimalToInteger(packedDecimalBiggerThanLong, offset0, precision31, errorChecking);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}
		byte[] packedDecimalSmallerThanLong = new byte[arrSize];
		BigInteger smallerThanLong = new BigInteger("-99999999999999999999");
		DecimalData.convertBigIntegerToPackedDecimal(smallerThanLong, packedDecimalSmallerThanLong, offset0, precision31, errorChecking);
		try
		{
			DecimalData.convertPackedDecimalToInteger(packedDecimalSmallerThanLong, offset0, precision31, errorChecking);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}
		
		value = Utils.TestValue.LargePositive.IntValue;
		typeName = nameErrorChecking;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertIntegerToPackedDecimal(value, packedDecimal, offset0, precision5, errorCheckingFalse);
		byte[] correct = new byte[arrSize];
		DecimalData.convertIntegerToPackedDecimal(0, correct, offset0, precision5, errorCheckingFalse);
		assertArrayEquals(testName, correct, packedDecimal);
		
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		// this is one larger than Integer.MAX_VALUE
		// when converted to packed, then packed to int
		// should overflow into Integer.MIN_VALUE
		BigInteger oneMoreThanLongRange = new BigInteger("2147483648"); 
		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertBigIntegerToPackedDecimal(oneMoreThanLongRange, packedDecimal, offset0, precision31, errorCheckingFalse);
		result = DecimalData.convertPackedDecimalToInteger(packedDecimal, offset0, precision31, errorCheckingFalse);
		assertEquals(testName, Integer.MIN_VALUE, result);
		
		// this is one smaller than Integer.MIN_VALUE
		// when converted to packed, then packed to int
		// should underflow into Integer.MAX_VALUE
		BigInteger oneLessThanLongRange = new BigInteger("-2147483649"); 
		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertBigIntegerToPackedDecimal(oneLessThanLongRange, packedDecimal, offset0, precision31, errorCheckingFalse);
		result = DecimalData.convertPackedDecimalToInteger(packedDecimal, offset0, precision31, errorCheckingFalse);
		assertEquals(testName, Integer.MAX_VALUE, result);
		
		/*
		 * convertIntegerToExternalDecimal
		 */
		
		value = Utils.TestValue.SmallPositive.IntValue;
		dataName = "IntegerToExternalDecimal";
		typeName = namePrecisionGreaterThanByteArray;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(longPackedDecimal, (byte) 0x00);
		Arrays.fill(externalDecimal, (byte) 0x00);
		
		try
		{
			DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, arrSize + 1, errorChecking, decimalType1);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		try
		{
			
			DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, arrSize + 1, errorChecking, decimalType1);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		

		value = Utils.TestValue.SmallPositive.IntValue;
		typeName = namePrecisionAndOffsetGreaterThanByteArray;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		
		try
		{
			DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset25, arrSize + 1 - offset25, errorChecking, decimalType1);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		try
		{
			DecimalData.convertExternalDecimalToInteger(externalDecimal, offset25, arrSize + 1 - offset25, errorChecking, decimalType1);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		value = Utils.TestValue.SmallPositive.IntValue;
		typeName = namePrecisionOffsetAndDecTypeGreaterThanByteArray;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		
		try
		{
			DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset25, arrSize - offset25, errorChecking, decimalType3);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		try
		{
			DecimalData.convertExternalDecimalToInteger(externalDecimal, offset25, arrSize - offset25, errorChecking, decimalType3);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}


		value = Utils.TestValue.SmallPositive.IntValue;
		typeName = nameNull;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		byte[] externalDecimalNull = null;
		
		try
		{
			DecimalData.convertIntegerToExternalDecimal(value, externalDecimalNull, offset0, precision31, errorChecking, decimalType1);
			fail(testName);
		}
		catch(NullPointerException e)
		{
			
		}
		
		try
		{
			DecimalData.convertExternalDecimalToInteger(externalDecimalNull, offset0, precision31, errorChecking, decimalType1);
			fail(testName);
		}
		catch(NullPointerException e)
		{
			
		}
		
		value = Utils.TestValue.LargeNegative.IntValue;
		typeName = nameOverflow;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		
		try
		{
			DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, String.valueOf(value).length() - 2, errorChecking, decimalType1);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}

		biggerThanLong = new BigInteger("99999999999999999999");
		Arrays.fill(externalDecimal, (byte) 0x00);
		DecimalData.convertBigIntegerToExternalDecimal(biggerThanLong, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		try
		{
			DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}
		
		smallerThanLong = new BigInteger("-99999999999999999999");
		Arrays.fill(externalDecimal, (byte) 0x00);
		DecimalData.convertBigIntegerToExternalDecimal(smallerThanLong, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		try
		{
			DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, decimalType1);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}
		
		value = Utils.TestValue.LargePositive.IntValue;
		typeName = nameErrorChecking;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision5, errorCheckingFalse, decimalType1);
		Arrays.fill(correct, (byte) 0x00);
		DecimalData.convertIntegerToExternalDecimal(0, correct, offset0, precision5, errorCheckingFalse, decimalType1);
		assertArrayEquals(testName, correct, externalDecimal);
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision5, errorCheckingFalse, decimalType2);
		Arrays.fill(correct, (byte) 0x00);
		DecimalData.convertIntegerToExternalDecimal(0, correct, offset0, precision5, errorCheckingFalse, decimalType2);
		assertArrayEquals(testName, correct, externalDecimal);
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision5, errorCheckingFalse, decimalType3);
		Arrays.fill(correct, (byte) 0x00);
		DecimalData.convertIntegerToExternalDecimal(0, correct, offset0, precision5, errorCheckingFalse, decimalType3);
		assertArrayEquals(testName, correct, externalDecimal);
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision5, errorCheckingFalse, decimalType4);
		Arrays.fill(correct, (byte) 0x00);
		DecimalData.convertIntegerToExternalDecimal(0, correct, offset0, precision5, errorCheckingFalse, decimalType4);
		assertArrayEquals(testName, correct, externalDecimal);
		
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		// this is one larger than Integer.MAX_VALUE
		// when converted to packed, then packed to long
		// should overflow into Long.MIN_VALUE
		oneMoreThanLongRange = new BigInteger("2147483648"); 
		Arrays.fill(externalDecimal, (byte) 0x00);
		DecimalData.convertBigIntegerToExternalDecimal(oneMoreThanLongRange, externalDecimal, offset0, precision31, errorCheckingFalse, decimalType1);
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorCheckingFalse, decimalType1);
		assertEquals(testName, Integer.MIN_VALUE, result);
		
		// this is one smaller than Integer.MIN_VALUE
		// when converted to packed, then packed to long
		// should underflow into Long.MAX_VALUE
		oneLessThanLongRange = new BigInteger("-2147483649"); 
		Arrays.fill(externalDecimal, (byte) 0x00);
		DecimalData.convertBigIntegerToExternalDecimal(oneLessThanLongRange, externalDecimal, offset0, precision31, errorCheckingFalse, decimalType1);
		result = DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorCheckingFalse, decimalType1);
		assertEquals(testName, Integer.MAX_VALUE, result);
		
		value = Utils.TestValue.LargeNegative.IntValue;
		typeName = nameInvalidDecimalTypes;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		
		try
		{
			DecimalData.convertIntegerToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, 50);
			fail(testName);
		}
		catch (IllegalArgumentException e)
		{
			
		}
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		try
		{
			DecimalData.convertExternalDecimalToInteger(externalDecimal, offset0, precision31, errorChecking, 50);
			fail(testName);
		}
		catch (IllegalArgumentException e)
		{
			
		}
		
		/*
		 * convertIntegerToUnicodeDecimal
		 */
		
		value = Utils.TestValue.SmallPositive.IntValue;
		dataName = "IntegerToUnicodeDecimal";
		typeName = namePrecisionGreaterThanByteArray;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		char[] unicodeDecimal = new char[arrSize];
		
		try
		{
			DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, arrSize, errorChecking, decimalType6);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		try
		{
			DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, arrSize, errorChecking, decimalType6);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		

		value = Utils.TestValue.SmallPositive.IntValue;
		typeName = namePrecisionAndOffsetGreaterThanByteArray;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(unicodeDecimal, (char)0);
		
		try
		{
			DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset25, arrSize - offset25, errorChecking, decimalType6);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		try
		{
			DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset25, arrSize - offset25, errorChecking, decimalType6);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}


		value = Utils.TestValue.SmallPositive.IntValue;
		typeName = nameNull;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		char[] unicodeDecimalNull = null;
		
		try
		{
			DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimalNull, offset0, precision31, errorChecking, decimalType6);
			fail(testName);
		}
		catch(NullPointerException e)
		{
			
		}
		
		try
		{
			DecimalData.convertUnicodeDecimalToInteger(unicodeDecimalNull, offset0, precision31, errorChecking, decimalType6);
			fail(testName);
		}
		catch(NullPointerException e)
		{
			
		}
		
		value = Utils.TestValue.LargeNegative.IntValue;
		typeName = nameOverflow;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + typeName+ " line:"+  " no  line ";

		Arrays.fill(unicodeDecimal, (char)0);
		
		try
		{
			DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, String.valueOf(value).length() - 2, errorChecking, decimalType6);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}

		biggerThanLong = new BigInteger("99999999999999999999");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(biggerThanLong, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		try
		{
			DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}
		smallerThanLong = new BigInteger("-99999999999999999999");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(smallerThanLong, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		try
		{
			DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}
		
		value = Utils.TestValue.LargePositive.IntValue;
		typeName = nameErrorChecking;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + typeName+ " line:"+  " no  line ";

		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision5, errorCheckingFalse, decimalType7);
		char[] correctUnicode = new char[arrSize];
		DecimalData.convertIntegerToUnicodeDecimal(0, correctUnicode, offset0, precision5, errorCheckingFalse, decimalType7);
		assertArrayEquals(testName, correctUnicode, unicodeDecimal);
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision5, errorCheckingFalse, decimalType6);
		Arrays.fill(correctUnicode, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(0, correctUnicode, offset0, precision5, errorCheckingFalse, decimalType6);
		assertArrayEquals(testName, correctUnicode, unicodeDecimal);
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision5, errorCheckingFalse, decimalType5);
		Arrays.fill(correctUnicode, (char)0);
		DecimalData.convertIntegerToUnicodeDecimal(0, correctUnicode, offset0, precision5, errorCheckingFalse, decimalType5);
		assertArrayEquals(testName, correctUnicode, unicodeDecimal);
		
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		// this is one larger than Long.MAX_VALUE
		// when converted to packed, then packed to long
		// should overflow into Long.MIN_VALUE
		oneMoreThanLongRange = new BigInteger("2147483648"); 
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(oneMoreThanLongRange, unicodeDecimal, offset0, precision31, errorCheckingFalse, decimalType5);
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorCheckingFalse, decimalType5);
		assertEquals(testName, Integer.MIN_VALUE, result);
		
		// this is one smaller than Long.MIN_VALUE
		// when converted to packed, then packed to long
		// should convert into Long.MIN_VALUE+1
		oneLessThanLongRange = new BigInteger("-2147483649"); 
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(oneLessThanLongRange, unicodeDecimal, offset0, precision31, errorCheckingFalse, decimalType5);
		result = DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorCheckingFalse, decimalType5);
		assertEquals(testName, Integer.MIN_VALUE+1, result);
		
		value = Utils.TestValue.LargeNegative.IntValue;
		typeName = nameInvalidDecimalTypes;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + typeName+ " line:"+  " no  line ";

		Arrays.fill(unicodeDecimal, (char)0);
		
		try
		{
			DecimalData.convertIntegerToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType1);
			fail(testName);
		}
		catch (IllegalArgumentException e)
		{
			
		}
		
		try
		{
			DecimalData.convertUnicodeDecimalToInteger(unicodeDecimal, offset0, precision31, errorChecking, decimalType1);
			fail(testName);
		}
		catch (IllegalArgumentException e)
		{
			
		}
		
	}
	
	@Test
	public void testConvertLongExceptions()
	{
		//lp.addCallerName(Utils.getCallingMethod());
		/*
		 * test offset, precision out of bounds
		 * test overflow
		 * test error checking
		 * test null array
		 * test invalid decimal type
		 * test invalid data
		 */

		final int offset0 = 0;
		final int precision31 = 31;
		final boolean errorChecking = true;
		final boolean errorCheckingFalse = false;
		
		long value = Utils.TestValue.SmallPositive.LongValue;
		
		final int decimalType1 = DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING;
		final int decimalType2 = DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING;
		final int decimalType3 = DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING;
		final int decimalType4 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
		final int decimalType5 = DecimalData.UNICODE_UNSIGNED;
		final int decimalType6 = DecimalData.UNICODE_SIGN_SEPARATE_LEADING;
		final int decimalType7 = DecimalData.UNICODE_SIGN_SEPARATE_TRAILING;
		
		final int offset25 = 25;
		final int precision5 = 5;
		long result;
				
		String namePrecisionGreaterThanByteArray = " Precision greater than byte array";
		String namePrecisionAndOffsetGreaterThanByteArray = " Precision and offset greater than byte array";
		String namePrecisionOffsetAndDecTypeGreaterThanByteArray = " Precision, offset and decimal type greater than byte array";
		String nameErrorChecking = " Error checking";
		String nameNull = " Null";
		String nameInvalidDecimalTypes = " Invalid decimal type";
		String nameOverflow = " Overflow";
		
		value = Utils.TestValue.SmallPositive.LongValue;
		String dataName = "LongToPackedDecimal";
		String typeName = namePrecisionGreaterThanByteArray;
		String testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		int arrSize = 49; 
		byte[] longPackedDecimal = new byte[arrSize];
		byte[] externalDecimal = new byte[arrSize];
		byte[] packedDecimal = new byte[arrSize];
		
		try
		{
			DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset0, arrSize*2, errorChecking);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		try
		{
			DecimalData.convertPackedDecimalToLong(longPackedDecimal, offset0, arrSize*2, errorChecking);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		Arrays.fill(longPackedDecimal, (byte) 0x00);
		Arrays.fill(externalDecimal, (byte) 0x00);
		

		value = Utils.TestValue.SmallPositive.LongValue;
		typeName = namePrecisionAndOffsetGreaterThanByteArray;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(longPackedDecimal, (byte) 0x00);
		
		try
		{
			DecimalData.convertLongToPackedDecimal(value, longPackedDecimal, offset25, (arrSize - offset25)*2, errorChecking);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		try
		{
			DecimalData.convertPackedDecimalToLong(longPackedDecimal, offset25, (arrSize - offset25)*2, errorChecking);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}


		value = Utils.TestValue.SmallPositive.LongValue;
		typeName = nameNull;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		byte[] packedDecimalNull = null;
		
		try
		{
			DecimalData.convertLongToPackedDecimal(value, packedDecimalNull, offset0, precision31, errorChecking);
			fail(testName);
		}
		catch(NullPointerException e)
		{
			
		}
		try
		{
			DecimalData.convertPackedDecimalToLong(packedDecimalNull, offset0, precision31, errorChecking);
			fail(testName);
		}
		catch(NullPointerException e)
		{
			
		}
		
		value = Utils.TestValue.LargeNegative.LongValue;
		typeName = nameOverflow;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(packedDecimal, (byte) 0x00);
		try
		{
			DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, String.valueOf(value).length() - 2, errorChecking);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}
		byte[] packedDecimalBiggerThanLong = new byte[arrSize];
		BigInteger biggerThanLong = new BigInteger("99999999999999999999");
		DecimalData.convertBigIntegerToPackedDecimal(biggerThanLong, packedDecimalBiggerThanLong, offset0, precision31, errorChecking);
		try
		{
			DecimalData.convertPackedDecimalToLong(packedDecimalBiggerThanLong, offset0, precision31, errorChecking);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}
		byte[] packedDecimalSmallerThanLong = new byte[arrSize];
		BigInteger smallerThanLong = new BigInteger("-99999999999999999999");
		DecimalData.convertBigIntegerToPackedDecimal(smallerThanLong, packedDecimalSmallerThanLong, offset0, precision31, errorChecking);
		try
		{
			DecimalData.convertPackedDecimalToLong(packedDecimalSmallerThanLong, offset0, precision31, errorChecking);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}
		
		value = Utils.TestValue.LargePositive.LongValue;
		typeName = nameErrorChecking;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision5, errorCheckingFalse);
		byte[] correct = new byte[arrSize];
		DecimalData.convertLongToPackedDecimal(0, correct, offset0, precision5, errorCheckingFalse);
		assertArrayEquals(testName, correct, packedDecimal);
		
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		// this is one larger than Long.MAX_VALUE
		// when converted to packed, then packed to long
		// should overflow into Long.MIN_VALUE
		BigInteger oneMoreThanLongRange = new BigInteger("9223372036854775808"); 
		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertBigIntegerToPackedDecimal(oneMoreThanLongRange, packedDecimal, offset0, precision31, errorCheckingFalse);
		result = DecimalData.convertPackedDecimalToLong(packedDecimal, offset0, precision31, errorCheckingFalse);
		assertEquals(testName, Long.MIN_VALUE, result);
		
		// this is one smaller than Long.MIN_VALUE
		// when converted to packed, then packed to long
		// should underflow into Long.MAX_VALUE
		BigInteger oneLessThanLongRange = new BigInteger("-9223372036854775809"); 
		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertBigIntegerToPackedDecimal(oneLessThanLongRange, packedDecimal, offset0, precision31, errorCheckingFalse);
		result = DecimalData.convertPackedDecimalToLong(packedDecimal, offset0, precision31, errorCheckingFalse);
		assertEquals(testName, Long.MAX_VALUE, result);
		
		/*
		 * convertLongToExternalDecimal
		 */
		
		value = Utils.TestValue.SmallPositive.LongValue;
		dataName = "LongToExternalDecimal";
		typeName = namePrecisionGreaterThanByteArray;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(longPackedDecimal, (byte) 0x00);
		Arrays.fill(externalDecimal, (byte) 0x00);
		
		try
		{
			DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, arrSize + 1, errorChecking, decimalType1);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		try
		{
			
			DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, arrSize + 1, errorChecking, decimalType1);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		

		value = Utils.TestValue.SmallPositive.LongValue;
		typeName = namePrecisionAndOffsetGreaterThanByteArray;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		
		try
		{
			DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset25, arrSize + 1 - offset25, errorChecking, decimalType1);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		try
		{
			DecimalData.convertExternalDecimalToLong(externalDecimal, offset25, arrSize + 1 - offset25, errorChecking, decimalType1);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		value = Utils.TestValue.SmallPositive.LongValue;
		typeName = namePrecisionOffsetAndDecTypeGreaterThanByteArray;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		
		try
		{
			DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset25, arrSize - offset25, errorChecking, decimalType3);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		try
		{
			DecimalData.convertExternalDecimalToLong(externalDecimal, offset25, arrSize - offset25, errorChecking, decimalType3);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		


		value = Utils.TestValue.SmallPositive.LongValue;
		typeName = nameNull;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		byte[] externalDecimalNull = null;
		
		try
		{
			DecimalData.convertLongToExternalDecimal(value, externalDecimalNull, offset0, precision31, errorChecking, decimalType1);
			fail(testName);
		}
		catch(NullPointerException e)
		{
			
		}
		
		try
		{
			DecimalData.convertExternalDecimalToLong(externalDecimalNull, offset0, precision31, errorChecking, decimalType1);
			fail(testName);
		}
		catch(NullPointerException e)
		{
			
		}
		
		value = Utils.TestValue.LargeNegative.LongValue;
		typeName = nameOverflow;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		
		try
		{
			DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, String.valueOf(value).length()-2, errorChecking, decimalType1);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}

		biggerThanLong = new BigInteger("99999999999999999999");
		Arrays.fill(externalDecimal, (byte) 0x00);
		DecimalData.convertBigIntegerToExternalDecimal(biggerThanLong, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		try
		{
			DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType1);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}
		smallerThanLong = new BigInteger("-99999999999999999999");
		Arrays.fill(externalDecimal, (byte) 0x00);
		DecimalData.convertBigIntegerToExternalDecimal(smallerThanLong, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		try
		{
			DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType1);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}
		
		value = Utils.TestValue.LargePositive.LongValue;
		typeName = nameErrorChecking;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision5, errorCheckingFalse, decimalType1);
		Arrays.fill(correct, (byte) 0x00);
		DecimalData.convertLongToExternalDecimal(0, correct, offset0, precision5, errorCheckingFalse, decimalType1);
		assertArrayEquals(testName, correct, externalDecimal);
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision5, errorCheckingFalse, decimalType2);
		Arrays.fill(correct, (byte) 0x00);
		DecimalData.convertLongToExternalDecimal(0, correct, offset0, precision5, errorCheckingFalse, decimalType2);
		assertArrayEquals(testName, correct, externalDecimal);
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision5, errorCheckingFalse, decimalType3);
		Arrays.fill(correct, (byte) 0x00);
		DecimalData.convertLongToExternalDecimal(0, correct, offset0, precision5, errorCheckingFalse, decimalType3);
		assertArrayEquals(testName, correct, externalDecimal);
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision5, errorCheckingFalse, decimalType4);
		Arrays.fill(correct, (byte) 0x00);
		DecimalData.convertLongToExternalDecimal(0, correct, offset0, precision5, errorCheckingFalse, decimalType4);
		assertArrayEquals(testName, correct, externalDecimal);
		
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		// this is one larger than Long.MAX_VALUE
		// when converted to packed, then packed to long
		// should overflow into Long.MIN_VALUE
		oneMoreThanLongRange = new BigInteger("9223372036854775808"); 
		Arrays.fill(externalDecimal, (byte) 0x00);
		DecimalData.convertBigIntegerToExternalDecimal(oneMoreThanLongRange, externalDecimal, offset0, precision31, errorCheckingFalse, decimalType1);
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorCheckingFalse, decimalType1);
		assertEquals(testName, Long.MIN_VALUE, result);
		
		// this is one smaller than Long.MIN_VALUE
		// when converted to packed, then packed to long
		// should underflow into Long.MAX_VALUE
		oneLessThanLongRange = new BigInteger("-9223372036854775809"); 
		Arrays.fill(externalDecimal, (byte) 0x00);
		DecimalData.convertBigIntegerToExternalDecimal(oneLessThanLongRange, externalDecimal, offset0, precision31, errorCheckingFalse, decimalType1);
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorCheckingFalse, decimalType1);
		assertEquals(testName, Long.MAX_VALUE, result);
		
		value = Utils.TestValue.LargeNegative.LongValue;
		typeName = nameInvalidDecimalTypes;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		
		try
		{
			DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, 50);
			fail(testName);
		}
		catch (IllegalArgumentException e)
		{
			
		}
		
		Arrays.fill(externalDecimal, (byte) 0x00);
		try
		{
			DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, 50);
			fail(testName);
		}
		catch (IllegalArgumentException e)
		{
			
		}
		
		/*
		 * convertLongToUnicodeDecimal
		 */
		
		value = Utils.TestValue.SmallPositive.LongValue;
		dataName = "LongToUnicodeDecimal";
		typeName = namePrecisionGreaterThanByteArray;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		char[] unicodeDecimal = new char[arrSize];
		
		try
		{
			DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, arrSize, errorChecking, decimalType6);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		try
		{
			DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, arrSize, errorChecking, decimalType6);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		

		value = Utils.TestValue.SmallPositive.LongValue;
		typeName = namePrecisionAndOffsetGreaterThanByteArray;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(unicodeDecimal, (char)0);
		
		try
		{
			DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset25, arrSize - offset25, errorChecking, decimalType6);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		try
		{
			DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset25, arrSize - offset25, errorChecking, decimalType6);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		value = Utils.TestValue.SmallPositive.LongValue;
		typeName = namePrecisionOffsetAndDecTypeGreaterThanByteArray;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(unicodeDecimal, (char)0);
		
		try
		{
			DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset25, arrSize + 1 - offset25, errorChecking, decimalType5);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		try
		{
			DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset25, arrSize + 1 - offset25, errorChecking, decimalType5);
			fail(testName);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}


		value = Utils.TestValue.SmallPositive.LongValue;
		typeName = nameNull;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		char[] unicodeDecimalNull = null;
		
		try
		{
			DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimalNull, offset0, precision31, errorChecking, decimalType6);
			fail(testName);
		}
		catch(NullPointerException e)
		{
			
		}
		
		try
		{
			DecimalData.convertUnicodeDecimalToLong(unicodeDecimalNull, offset0, precision31, errorChecking, decimalType6);
			fail(testName);
		}
		catch(NullPointerException e)
		{
			
		}
		
		value = Utils.TestValue.LargeNegative.LongValue;
		typeName = nameOverflow;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + typeName+ " line:"+  " no  line ";

		Arrays.fill(unicodeDecimal, (char)0);
		
		try
		{
			DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, String.valueOf(value).length() -2, errorChecking, decimalType6);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}

		biggerThanLong = new BigInteger("99999999999999999999");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(biggerThanLong, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		try
		{
			DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}
		
		smallerThanLong = new BigInteger("-99999999999999999999");
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(smallerThanLong, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		try
		{
			DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
			fail(testName);
		}
		catch (ArithmeticException e)
		{
			
		}
		
		
		value = Utils.TestValue.LargePositive.LongValue;
		typeName = nameErrorChecking;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + typeName+ " line:"+  " no  line ";

		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision5, errorCheckingFalse, decimalType7);
		char[] correctUnicode = new char[arrSize];
		DecimalData.convertLongToUnicodeDecimal(0, correctUnicode, offset0, precision5, errorCheckingFalse, decimalType7);
		assertArrayEquals(testName, correctUnicode, unicodeDecimal);
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision5, errorCheckingFalse, decimalType6);
		Arrays.fill(correctUnicode, (char)0);
		DecimalData.convertLongToUnicodeDecimal(0, correctUnicode, offset0, precision5, errorCheckingFalse, decimalType6);
		assertArrayEquals(testName, correctUnicode, unicodeDecimal);
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision5, errorCheckingFalse, decimalType5);
		Arrays.fill(correctUnicode, (char)0);
		DecimalData.convertLongToUnicodeDecimal(0, correctUnicode, offset0, precision5, errorCheckingFalse, decimalType5);
		assertArrayEquals(testName, correctUnicode, unicodeDecimal);
		
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + typeName+ " line:"+  " no  line ";
		
		// this is one larger than Long.MAX_VALUE
		// when converted to packed, then packed to long
		// should overflow into Long.MIN_VALUE
		oneMoreThanLongRange = new BigInteger("9223372036854775808"); 
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(oneMoreThanLongRange, unicodeDecimal, offset0, precision31, errorCheckingFalse, decimalType5);
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorCheckingFalse, decimalType5);
		assertEquals(testName, Long.MIN_VALUE, result);
		
		// this is one smaller than Long.MIN_VALUE
		// when converted to packed, then packed to long
		// should convert into Long.MIN_VALUE+1
		oneLessThanLongRange = new BigInteger("-9223372036854775809"); 
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertBigIntegerToUnicodeDecimal(oneLessThanLongRange, unicodeDecimal, offset0, precision31, errorCheckingFalse, decimalType5);
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorCheckingFalse, decimalType5);
		assertEquals(testName, Long.MIN_VALUE+1, result);
		
		value = Utils.TestValue.LargeNegative.LongValue;
		typeName = nameInvalidDecimalTypes;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + typeName+ " line:"+  " no  line ";

		Arrays.fill(unicodeDecimal, (char)0);
		
		try
		{
			DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType1);
			fail(testName);
		}
		catch (IllegalArgumentException e)
		{
			
		}
		
		try
		{
			DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType1);
			fail(testName);
		}
		catch (IllegalArgumentException e)
		{
			
		}
		
		
	}
	
	@Test
	public void testConvertLongNormals()
	{
		//lp.addCallerName(Utils.getCallingMethod());
		/*
		 * this function will test
		 * convertLong to xxxx
		 * convertFromxxx back to long, and assert both equals
		 * 
		 * the values to test will be small positive, large positive, largest possible, smallest possible, small negative, large negative, zero
		 * do two tests for offset/precision
		 * test every single decimal type
		 */
		byte[] externalDecimal = new byte[64];
		
		final int offset0 = 0;
		final int offset5 = 5;
		final int offset25 = 25;
		final int offset50 = 50;
		final int precision1 = 1;
		final int precision2 = 2;
		final int precision15 = 15;
		final int precision16 = 16;
		final int precision30 = 30;
		final int precision31 = 31;
		final int precision50 = 50;
		final boolean errorChecking = false;
		
		final int decimalType1 = DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING;
		final int decimalType2 = DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING;
		final int decimalType3 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
		final int decimalType4 = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
		final int decimalType5 = DecimalData.UNICODE_UNSIGNED;
		final int decimalType6 = DecimalData.UNICODE_SIGN_SEPARATE_LEADING;
		final int decimalType7 = DecimalData.UNICODE_SIGN_SEPARATE_TRAILING;
		
		long value = Utils.TestValue.SmallPositive.LongValue;
		String dataName = "LongToExternalDecimal";
		String typeName = "EBCDIC_SIGN_EMBEDDED_TRAILING";
		String testType = "Normal Value";
		String testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertLongToExternalDecimal");
		long result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargePositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.Zero.LongValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType1);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision1, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType1);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset5, precision2, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType1);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset25, precision15, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType1);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset5, precision16, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType1);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset50, precision2, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType1);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision16, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType1);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision30, errorChecking, decimalType1);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType1);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision50, errorChecking, decimalType1);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		/*
		 * EBCDIC_SIGN_EMBEDDED_LEADING
		 */
		
		value = Utils.TestValue.SmallPositive.LongValue;
		typeName = "EBCDIC_SIGN_EMBEDDED_LEADING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargePositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.Zero.LongValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType2);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision1, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType2);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset5, precision2, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType2);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset25, precision15, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType2);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset5, precision16, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType2);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset50, precision2, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType2);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision16, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType2);
		//lp.addExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision30, errorChecking, decimalType2);
		//lp.addExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType2);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision50, errorChecking, decimalType2);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		/*
		 * EBCDIC_SIGN_SEPARATE_TRAILING
		 */

		value = Utils.TestValue.SmallPositive.LongValue;
		typeName = "EBCDIC_SIGN_SEPARATE_TRAILING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargePositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.Zero.LongValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType3);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision1, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType3);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset5, precision2, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType3);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset25, precision15, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType3);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset5, precision16, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType3);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset50, precision2, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType3);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision16, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType3);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision30, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType3);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision50, errorChecking, decimalType3);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		/*
		 * EBCDIC_SIGN_SEPARATE_LEADING
		 */

		value = Utils.TestValue.SmallPositive.LongValue;
		typeName = "EBCDIC_SIGN_SEPARATE_LEADING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargePositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.Zero.LongValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision31, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision1, errorChecking, decimalType4);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision1, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset5, precision2, errorChecking, decimalType4);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset5, precision2, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset25, precision15, errorChecking, decimalType4);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset25, precision15, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset5, precision16, errorChecking, decimalType4);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset5, precision16, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset50, precision2, errorChecking, decimalType4);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset50, precision2, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision16, errorChecking, decimalType4);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision16, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision30, errorChecking, decimalType4);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision30, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToExternalDecimal(value, externalDecimal, offset0, precision50, errorChecking, decimalType4);
		//lp.addNotExpected("convertLongToExternalDecimal");
		result = DecimalData.convertExternalDecimalToLong(externalDecimal, offset0, precision50, errorChecking, decimalType4);
		//lp.addNotExpected("convertExternalDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(externalDecimal, (byte) 0x00);



			
		/*
		 * UNICODE_UNSIGNED
		 */

		char[] UnicodeDecimal = new char[100];
		
		value = Utils.TestValue.SmallPositive.LongValue;
		typeName = "UNICODE_UNSIGNED";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargePositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToLong");
		//Unsigned, dont test negatives
		//testName = "Line: " + " no line " + ", " +  "no name";
		//assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToLong");
		//Unsigned, dont test negatives
		//testName = "Line: " + " no line " + ", " +  "no name";
		//assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		// dont test, negative unsigned
		/*DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType9);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType9);
		//lp.addExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);*/

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.Zero.LongValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision1, errorChecking, decimalType5);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision1, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset5, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset5, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset25, precision15, errorChecking, decimalType5);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset25, precision15, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargePositive.LongValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset5, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset5, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset50, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset50, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargePositive.LongValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision30, errorChecking, decimalType5);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision30, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(UnicodeDecimal, (char)'0');
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision50, errorChecking, decimalType5);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision50, errorChecking, decimalType5);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		/*
		 * UNICODE_SIGN_EMBEDDED_LEADING
		 */

		value = Utils.TestValue.SmallPositive.LongValue;
		typeName = "UNICODE_SIGN_EMBEDDED_LEADING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeUnicodeToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargePositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		/*DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType10);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType10);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);*/

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.Zero.LongValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision1, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision1, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset5, precision2, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset5, precision2, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset25, precision15, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset25, precision15, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset5, precision16, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset5, precision16, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset50, precision2, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset50, precision2, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision16, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision16, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision30, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision30, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		/*DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision50, errorChecking, decimalType10);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision50, errorChecking, decimalType10);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);*/

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		/*
		 * UNICODE_SIGN_EMBEDDED_TRAILING
		 */
		

		value = Utils.TestValue.SmallPositive.LongValue;
		typeName = "UNICODE_SIGN_EMBEDDED_TRAILING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(UnicodeDecimal, (char)'0');
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargePositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.Zero.LongValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision1, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision1, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.SmallNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset5, precision2, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset5, precision2, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset25, precision15, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset25, precision15, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset5, precision16, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset5, precision16, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset50, precision2, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset50, precision2, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision16, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision16, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision30, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision30, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(UnicodeDecimal, (char)'0');
		DecimalData.convertLongToUnicodeDecimal(value, UnicodeDecimal, offset0, precision50, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(UnicodeDecimal, offset0, precision50, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(UnicodeDecimal, (char)'0');

		
		
		/*
		 * convertLongToPackedDecimal
		 */

		

		value = Utils.TestValue.SmallPositive.LongValue;
		dataName = "LongToPackedDecimal";
		typeName = "PackedDecimal";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		byte[] packedDecimal = new byte[64];
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		result = DecimalData.convertPackedDecimalToLong(packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertPackedDecimalToLong");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

		Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.LargePositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		result = DecimalData.convertPackedDecimalToLong(packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertPackedDecimalToLong");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		result = DecimalData.convertPackedDecimalToLong(packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertPackedDecimalToLong");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		result = DecimalData.convertPackedDecimalToLong(packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertPackedDecimalToLong");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.LargestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		result = DecimalData.convertPackedDecimalToLong(packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertPackedDecimalToLong");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		result = DecimalData.convertPackedDecimalToLong(packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertPackedDecimalToLong");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.Zero.LongValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		result = DecimalData.convertPackedDecimalToLong(packedDecimal, offset0, precision31, errorChecking);
		//lp.addExpected("convertPackedDecimalToLong");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset0, precision1, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		result = DecimalData.convertPackedDecimalToLong(packedDecimal, offset0, precision1, errorChecking);
		//lp.addExpected("convertPackedDecimalToLong");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);


		value = Utils.TestValue.SmallNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset5, precision2, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		result = DecimalData.convertPackedDecimalToLong(packedDecimal, offset5, precision2, errorChecking);
		//lp.addExpected("convertPackedDecimalToLong");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;

		Arrays.fill(packedDecimal, (byte) 0x00);
		DecimalData.convertLongToPackedDecimal(value, packedDecimal, offset25, precision15, errorChecking);
		//lp.addExpected("convertLongToPackedDecimal");
		result = DecimalData.convertPackedDecimalToLong(packedDecimal, offset25, precision15, errorChecking);
		//lp.addExpected("convertPackedDecimalToLong");
		testName = "Line: " + " no line " + ", "  + "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(packedDecimal, (byte) 0x00);

		
		/*
		 * convertLongToUnicodeDecimal
		 */
		
		/*
		 * UNICODE_UNSIGNED
		 */

		value = Utils.TestValue.SmallPositive.LongValue;
		typeName = "UNICODE_UNSIGNED";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		char[] unicodeDecimal = new char[128];
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToLong");
		//Unsigned, dont test negatives
		//testName = "Line: " + " no line " + ", " +  "no name";
		//assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToLong");
		//Unsigned, dont test negatives
		//testName = "Line: " + " no line " + ", " +  "no name";
		//assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		// dont test, negative unsigned
		/*
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType9);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType9);
		//lp.addExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name";
		assertEquals(testName, value, result);*/

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.Zero.LongValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision1, errorChecking, decimalType5);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision1, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset5, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset5, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset25, precision15, errorChecking, decimalType5);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset25, precision15, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.LongValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset5, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset5, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset50, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset50, precision2, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.LongValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision16, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision30, errorChecking, decimalType5);
		//lp.addExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision30, errorChecking, decimalType5);
		//lp.addExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType5);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision50, errorChecking, decimalType5);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		/*
		 * UNICODE_SIGN_EMBEDDED_LEADING
		 */

		value = Utils.TestValue.SmallPositive.LongValue;
		typeName = "UNICODE_SIGN_EMBEDDED_LEADING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");

		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision50, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.Zero.LongValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision1, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision1, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset5, precision2, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset5, precision2, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset25, precision15, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset25, precision15, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.LongValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset5, precision16, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset5, precision16, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset50, precision2, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset50, precision2, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.LongValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision16, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision16, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision30, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision30, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType6);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision50, errorChecking, decimalType6);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		/*
		 * UNICODE_SIGN_EMBEDDED_TRAILING
		 */


		value = Utils.TestValue.SmallPositive.LongValue;
		typeName = "UNICODE_SIGN_EMBEDDED_TRAILING";
		testType = "Normal Value";
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");

		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargeNegative.LongValue;
		testName = dataName + " " + Utils.TestValue.LargeNegative.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.LargestPossible.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision50, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallestPossible.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallestPossible.TestName + " " + testType +  " " + typeName;

		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.Zero.LongValue;
		testName = dataName + " " + Utils.TestValue.Zero.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision31, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		testType = "Precision and Offset";
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision1, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision1, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset5, precision2, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset5, precision2, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset25, precision15, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset25, precision15, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.LongValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset5, precision16, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset5, precision16, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset50, precision2, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset50, precision2, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.LargePositive.LongValue;
		testName = dataName + " " + Utils.TestValue.LargePositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision16, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision16, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

		
		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName;
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision30, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision30, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);


		value = Utils.TestValue.SmallPositive.LongValue;
		testName = dataName + " " + Utils.TestValue.SmallPositive.TestName + " " + testType +  " " + typeName+ " line:"+  " no  line ";
		
		Arrays.fill(unicodeDecimal, (char)0);
		DecimalData.convertLongToUnicodeDecimal(value, unicodeDecimal, offset0, precision50, errorChecking, decimalType7);
		//lp.addNotExpected("convertLongToUnicodeDecimal");
		result = DecimalData.convertUnicodeDecimalToLong(unicodeDecimal, offset0, precision50, errorChecking, decimalType7);
		//lp.addNotExpected("convertUnicodeDecimalToLong");
		
		testName = "Line: " + " no line " + ", " +  "no name"+ " line:"+  " no  line ";
		assertEquals(testName, value, result);

			Arrays.fill(unicodeDecimal, (char)0);

	}


}
