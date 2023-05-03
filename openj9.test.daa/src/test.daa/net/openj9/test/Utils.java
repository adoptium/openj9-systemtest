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

package net.openj9.test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

import com.ibm.dataaccess.DecimalData;

public class Utils {
	
	private static Random randomGenerator = new Random(System.currentTimeMillis());
	
	public static BigInteger getBigInteger(int prec, boolean isNegative)
	{
		//construct a string with digits.
		StringBuilder sb = new StringBuilder();
		if (isNegative)
			sb.append("-");
		//the first digit cannot be zero
		int rndDig = randomGenerator.nextInt(9) + 1;
		sb.append(rndDig);
		for (int i = 1; i < prec; ++i)
		{
			rndDig = randomGenerator.nextInt(10);
			sb.append(rndDig);
		}
		BigInteger bi = new BigInteger(sb.toString());
		
		return bi;
		
	}
	
	public static BigInteger getBigInteger(int prec)
	{
		//construct a string with digits.
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < prec; ++i)
		{
			int rndDig = randomGenerator.nextInt(10);
			sb.append(rndDig);
		}
		BigInteger bi = new BigInteger(sb.toString());
		
		return bi;
		
	}
	
	
	
	public static BigInteger getRandomPositiveBigInteger(int prec)
	{
		return getBigInteger(prec, false);
	}
	
	public static BigInteger getRandomBigInteger(int prec)
	{
		//construct a string with digits.
		return getBigInteger(prec, randomGenerator.nextBoolean());
	}
	
	//add bytes to the preceding digits
	public static byte[] extendZerosPD(byte[] input, int offset, int oldPrec, int newPrec)
	{
		if (oldPrec >= newPrec)
			return input;
		
		
		int oldLength = getByteArraySizeFromPrecision(oldPrec);
		int extendedBytes = getByteArraySizeFromPrecision(newPrec) - oldLength;
		
		byte[] rv = new byte[offset + getByteArraySizeFromPrecision(oldPrec + newPrec)];
		Arrays.fill(rv, (byte) 0);
		System.arraycopy(input, offset, rv, offset + extendedBytes, oldLength);
		return rv;
	}
	
	/**
	 * returns true if the packed decimal is positive. 
	 */
	public static boolean isPDPositiveSign(byte[] pd, int pdOffset,  int precision)
	{
		byte signByte = getPDSign(pd, pdOffset, precision);
		
		switch (signByte)
		{
		case 0x0B:
		case 0x0D:
			return false;
		default:
			return true;
		}
	}
	
	public static boolean isEDPositiveSign(byte[] ed, int edOffset, int precision, int decimalType)
	{
		switch (decimalType)
		{
		case DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING:
		{
			return (ed[edOffset] & 0xF0) != 0xD0 && (ed[edOffset] & 0xF0) != 0xB0;
		}
		case DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING:
		{
			return (ed[edOffset + precision -1] & 0xF0) != 0xD0 && (ed[edOffset + precision -1] & 0xF0) != 0xB0;
		}
		case DecimalData.EBCDIC_SIGN_SEPARATE_LEADING:
		{
			return ed[edOffset] == 0x4E;
		}
		case DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING:
		{
			return ed[edOffset + precision] == 0x4E;
		}
		}
		return false;
	}
	
	public static boolean isValidPDSign(byte[] pd, int pdOffset, int precision)
	{
		return getPDSign(pd, pdOffset, precision) < 0x10;
	}
	
	public static byte getPDSign(byte[] pd, int pdOffset, int precision)
	{
		return (byte) (pd[pdOffset + precision/2] & 0x0F);
	}
	
	public static int getByteArraySizeFromPrecision(int prec)
	{
		return (prec+2)/2;
	}
	
	public static byte getByteValueAt(byte[] pd, int pdOffset, int precision, int nthDigit)
	{
		byte byteValue;
		if (precision %2 == 0) //double precision
		{
			if (nthDigit % 2 == 0)
				byteValue = (byte) (pd[pdOffset + (nthDigit+1)/2]& 0x0F);
			else
				byteValue = (byte) ((pd[pdOffset + (nthDigit+1)/2]& 0xF0)>>4);
		}
		else
		{
			if (nthDigit % 2 == 0)
				byteValue = (byte) ((pd[pdOffset + nthDigit/2]& 0xF0)>>4);
			else
				byteValue = (byte) (pd[pdOffset + nthDigit/2]& 0x0F);
		}
		return byteValue;
	}
	
	public static byte[] getRandomInput(int offset, int prec, BigInteger bi)
	{
		int size = prec/2 + 1;
		size += offset;
		byte[] rv = new byte[size];
		
		DecimalData.convertBigIntegerToPackedDecimal(bi, rv, offset, prec, true);
		
		return rv;
	}
	
	public static void setNumber(byte[] rv, int offset, int prec, BigInteger bi)
	{
		DecimalData.convertBigIntegerToPackedDecimal(bi, rv, offset, prec, true);
	}
	
	public static String printByteArray(byte[] input)
	{
		StringBuffer sb = new StringBuffer(input.length * 2);
		for (int i = 0; i < input.length; i++) {
			int v = input[i] & 0xff;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase();		
	}

	public static String getCallingMethod() {
		return trace(Thread.currentThread().getStackTrace(), 3);
	}

	public static String getCallingMethod(int level) {
		return trace(Thread.currentThread().getStackTrace(), 2 + level);
	}
	
	private static String trace(StackTraceElement e[], int level) {
		if(e != null && e.length >= level) {
			StackTraceElement s = e[level];
			if(s != null) {
				return s.getMethodName();
			}
		}
		return null;
	}	
	
	public static void changeSignToF(byte[] array, int offset, int precision)
	{
		int pos = precision / 2;
		pos = pos+offset;

		array[pos] = (byte) (array[pos] & 0xF0 | 0x0F);
	}
	
	public static void changeSignToC(byte[] array, int offset, int precision)
	{
		int pos = precision / 2;
		pos = pos+offset;

		array[pos] = (byte) (array[pos] & 0xF0 | 0x0C);
	}
	
	public static long lengthTruncateFromRight(long source, int length)
	{
		int distance = length * 8;
		
		long target = source >> distance;
		target = target << distance;
		
		return target;
	}
	
	public static long modifyLength(long value, int oldLength, int newLength, boolean extend)
	{
		int diff = Math.abs(oldLength - newLength)*8;
		long target;
		boolean negative = (value < 0);
		if (newLength < oldLength)
		{
			target = value << diff;
			if (extend)
				target = target >> diff;
			else
				target = target >>> diff;
			return target;
		}
		else if (oldLength == newLength)
		{
			return value;
		}
		else
		{
			if (negative && extend)
			{
				target = value << diff;
				target = value >>> diff;
				return target;
			}
			else
			{
				return value;
			}
		}
	}
	
	public static int modifyLength(int value, int oldLength, int newLength, boolean extend)
	{
		int diff = Math.abs(oldLength - newLength)*8;
		int target;
		boolean negative = (value < 0);
		if (newLength < oldLength)
		{
			target = value << diff;
			if (extend)
				target = target >> diff;
			else
				target = target >>> diff;
			return target;
		}
		else if (oldLength == newLength)
		{
			return value;
		}
		else
		{
			if (negative && extend)
			{
				target = value << diff;
				target = value >>> diff;
				return target;
			}
			else
			{
				return value;
			}
		}
	}
	
	
	public static String toLength(String x, int length)
	{
		int minLength = Math.max(0, x.length()-length*2);
		String newString = x.substring(minLength, x.length());
		
		return newString;
	}
	
	public static long modifyLength(long value, int newLength, boolean extend)
	{
		String shortStr = toLength(Long.toHexString(value), 8);
		int charLength = newLength * 2;
		if (charLength < shortStr.length())
		{
			shortStr = shortStr.substring(shortStr.length()-charLength, shortStr.length());
		}
		else if (extend && charLength > shortStr.length() && value < 0)
		{
			String temp = "";
			for (int i=0;i<charLength-shortStr.length();i++)
			{
				temp+="f";
			}
			shortStr = temp + shortStr;
			
			shortStr = "-" + shortStr;
		}
		
		return Long.parseLong(shortStr, 16);
	}
	
	public static long modifyLength(int value, int newLength, boolean extend)
	{
		/*
		 * take value
		 * convert it to hex string
		 * truncate length to new length
		 * 
		 * if negative and extend, 
		 * padd with ff's
		 */
		
		String shortStr = toLength(Long.toHexString(value), newLength);
		
		boolean leftMostNegative = false;
		if (shortStr.charAt(0) == '8' || shortStr.charAt(0) == '9' || shortStr.charAt(0) == 'a' || shortStr.charAt(0) == 'b' || shortStr.charAt(0) == 'c' || shortStr.charAt(0) == 'd' || shortStr.charAt(0) == 'e' || shortStr.charAt(0) == 'f')
			leftMostNegative = true;
		
		if (leftMostNegative && extend)
		{
			String temp = "";

			for (int i=0;i<8*2-shortStr.length();i++)
			{
				temp+="f";
			}
			shortStr = "-" + temp + shortStr;
		}
	
		long a = Long.parseLong(shortStr, 16);
		
		return a;
	}
	
	public static short cutToShort(long x)
	{
		/* will return a short
		 * that is the right most bytes of the long
		 * does no sign extension
		 */
		short target = 0x0000;
		target = (short) (target | (x & 0x0000000000FFFF));
		
		return target;
	}
	
	public static int cutToInt(long x)
	{
		/* will return a int
		 * that is the right most bytes of the long
		 * does no sign extension
		 */
		int target = 0x00000000;
		target = (int) (target | (x & 0x000000FFFFFFFF));
		
		return target;
	}
	
	public static byte cutToByte(long x)
	{
		/* will return a byte
		 * that is the right most bytes of the long
		 * does no sign extension
		 */
		byte target = 0x00;
		target = (byte) (target | (x & 0x000000000000FF));
		
		return target;
	}
	
	
	public static long modifyLength(short value, int newLength, boolean extend)
	{
		/*
		 * take in a short
		 * convert it to string
		 * if not extend
		 * adjust to new length
		 * if extend and new length larger
		 * adjust to new length with FF's
		 */
		
		String shortStr = toLength(Long.toHexString(value), 2);
		int charLength = newLength * 2;
		if (charLength < shortStr.length())
		{
			shortStr = shortStr.substring(shortStr.length()-charLength, shortStr.length());
		}
		else if (extend && charLength > shortStr.length())
		{
			String temp = "";
			for (int i=0;i<charLength-shortStr.length();i++)
			{
				temp+="f";
			}
			shortStr = temp + shortStr;
			
			if (value < 0 && extend)
				shortStr = "-" + shortStr;
		}
		
		return Long.parseLong(shortStr, 16);
	}
	
	public static short modifyLength(short value, int oldLength, int newLength, boolean extend)
	{
		int diff = Math.abs(oldLength - newLength)*8;
		short target;
		short orTarget = 0x0000;
		boolean negative = (value < 0);
		if (newLength < oldLength)
		{
			target = (short)(value << diff);
			if (extend)
				target = (short)(target >> diff);
			else
			{
				target = (short)(target >>> diff);
				orTarget = (short) (target | orTarget);
			}
			return orTarget;
		}
		else if (oldLength == newLength)
		{
			return value;
		}
		else
		{
			if (negative && extend)
			{
				target = (short)(value << diff);
				target = (short)(value >>> diff);
				return (short)target;
			}
			else
			{
				return (short)value;
			}
		}
	}
	
	
	public static BigInteger toPrecision(BigInteger x, int precision)
	{
		String xStr = x.toString();
		
		if (precision < xStr.length())
		{
			xStr = xStr.substring(0, precision);
		}
		else
			return x;
		
		return new BigInteger(xStr);
	}
	
	public static BigDecimal toDecimalDigits(BigDecimal x, int decimalDigits) throws IllegalArgumentException
	{
		String xStr = x.toPlainString();
		
		int indexOfDecimal = xStr.indexOf('.');
		
		
		if (indexOfDecimal != -1)
		{
			int newEnd = Math.min(indexOfDecimal + decimalDigits + 1, xStr.length());
			xStr = xStr.substring(0, newEnd);
			return new BigDecimal(xStr);
		}
		else
		{
			throw new IllegalArgumentException("no decimal point");
		}
	}
		
	public static long signExtend(long value, int length, boolean extend)
	{
		long target;
		
		value = lengthTruncateFromRight(value, length);
		
		if (value >= 0)
			return value;
		
		if (value < 0 && extend)
			return value;
		
		else
			target = value;
		
		int shift = 64 - length*8;
		
		target = target << shift;
		target = target >>> shift;
		
		return target;
	}
	
	public static String makeTestNameEquivalency(String testType, String functionName, Number value1, Number value2, byte[] op1, int offset1, int precision1, byte[] op2, int offset2, int precision2)
	{
		/*
		 *    Explicit version
		 *    Function: [functionName]
		 *    Type: [testType]
		 *    Value1: [Value1]
		 *       Offset1: [Offset1]
		 *       Precision1: [Precision1]
		 *       Operand1: [Op1.0] [Op1.1] ...
		 *    Value2: [Value2]
		 *       Offset2: [Offset2]
		 *       Precision2: [Precision2]
		 *       Operand2: [Op2.0] [Op2.1] ...
		 *       
		 *    Fast version
		 *    Function: [functionName]
		 *    Type: [testType]
		 *    Value1: [Value1]
		 *    Value2: [Value2]
		 *       Operand1: [Op1.0] [Op1.1] ...
		 *       Operand2: [Op2.0] [Op2.1] ...  
		 *       
		 *    Simple version
		 *    Type: [testType], Function: [functionName], Value1: [Value1], Value2: [Value2]
		 */
		String simpleInfo = "";
		simpleInfo += "Function: " + functionName;
		simpleInfo += ", Type: " + testType;
		simpleInfo += ", Value1: " + value1;
		simpleInfo += ", Value2: " + value2;
		
		String explicitInfo = "";
		explicitInfo += "   Function: " + functionName;
		explicitInfo += "\n   Type: " + testType;
		explicitInfo += "\n   Value1: " + value1;
		explicitInfo += "\n      Offset1: " + offset1;
		explicitInfo += "\n      Precision1: " + precision1;
		explicitInfo += "\n      Operand1: " + byteArrayToString(op1);
		explicitInfo += "\n   Value2: " + value2;
		explicitInfo += "\n      Offset2: " + offset2;
		explicitInfo += "\n      Precision2: " + precision2;
		explicitInfo += "\n      Operand2: " + byteArrayToString(op2);
		
		String fastInfo = "";
		fastInfo += "   Function: " + functionName;
		fastInfo += "\n   Type: " + testType;
		fastInfo += "\n   Value1: " + value1;
		fastInfo += "\n   Value2: " + value2;
		fastInfo += "\n      Operand1: " + byteArrayToString(op1);
		fastInfo += "\n      Operand2: " + byteArrayToString(op2);
		
		String combinedInfos = simpleInfo + "|" + explicitInfo + "|" + fastInfo + "|";
		combinedInfos += "\nReason";
		return combinedInfos;
	}
	
	public static String makeTestNameShift(String testType, String functionName, Number value, byte[] packedDecimal, int offset1, int precision1, byte[] shiftedDecimal, int offset2, int precision2, int shift, boolean rounded, boolean errorChecking)
	{
		/*
		 *    Explicit version
		 *    Function: [functionName]
		 *    Type: [testType]
		 *    Shift: [shift]
		 *    Rounded: [rounded]
		 *    ErrorCheck: [errorChecking]
		 *    Value: [value]
		 *       Offset1: [Offset1]
		 *       Precision1: [Precision1]
		 *       Value  Array1: [packedDecimal.0] [packedDecimal.1] ...
		 *    Shifted: 
		 *       Offset2: [Offset2]
		 *       Precision2: [Precision2]
		 *       ShiftedArray2: [shiftedDecimal.0] [shiftedDecimal.1] ...
		 *       
		 *    Fast version
		 *    Function: [functionName]
		 *    Type: [testType]
		 *    Shift: [shift]
		 *    Value: [value]
		 *    Shifted: 
		 *       Value  Array1: [packedDecimal.0] [packedDecimal.1] ...
		 *       ShiftedArray2: [shiftedDecimal.0] [shiftedDecimal.1] ...
		 *       
		 *    Simple version
		 *    Function: [functionName], Type: [testType], Value: [value], Shift: [shift]
		 */
		String simpleInfo = "";
		simpleInfo += "Function: " + functionName;
		simpleInfo += ", Type: " + testType;
		simpleInfo += ", Value: " + value;
		simpleInfo += ", Shift: " + shift;
		
		String explicitInfo = "";
		explicitInfo += "   Function: " + functionName;
		explicitInfo += "\n   Type: " + testType;
		explicitInfo += "\n   Shift: " + shift;
		if (rounded)
			explicitInfo += "\n   Rounded: " + rounded;
		else
			explicitInfo += "\n   Rounded: " + rounded + " (or none)";
		explicitInfo += "\n   ErrorCheck: " + errorChecking;
		explicitInfo += "\n   Value: " + value;
		explicitInfo += "\n      Offset1: " + offset1;
		explicitInfo += "\n      Precision1: " + precision1;
		explicitInfo += "\n      Value  Array1: " + byteArrayToString(packedDecimal);
		explicitInfo += "\n   Shifted: ";
		explicitInfo += "\n      Offset2: " + offset2;
		explicitInfo += "\n      Precision2: " + precision2;
		explicitInfo += "\n      ShiftedArray2: " + byteArrayToString(shiftedDecimal);
		
		String fastInfo = "";
		fastInfo += "   Function: " + functionName;
		fastInfo += "\n   Type: " + testType;
		fastInfo += "\n   Shift: " + shift;
		fastInfo += "\n   Value: " + value;
		fastInfo += "\n   Shifted: ";
		fastInfo += "\n      Value  Array1: " + byteArrayToString(packedDecimal);
		fastInfo += "\n      ShiftedArray2: " + byteArrayToString(shiftedDecimal);
		
		String combinedInfos = simpleInfo + "|" + explicitInfo + "|" + fastInfo + "|";
		combinedInfos += "\nReason";
		return combinedInfos;
	}
	
	public static String makeTestNameShift(String testType, String functionName, Number value, byte[] packedDecimal, int offset1, int precision1, byte[] shiftedDecimal, int offset2, int precision2, int shift,  boolean errorChecking)
	{
		/*
		 * Identical to the other makeTestNameShift function, except that this is for shiftLefts, where there is no rounding parameter
		 * This will call the other function with a rounding parameter of false,
		 * In the output, it is specified that false can mean either a parameter of false, or no rounding parameter
		 */
		
		return makeTestNameShift(testType, functionName, value, packedDecimal, offset1, precision1, shiftedDecimal, offset2, precision2, shift, errorChecking);
	}
	
	public static String makeTestNameBinaryConvert(String testType, String functionName, Number value, byte[] byte_array, int offset1, boolean endian1, Number result, int offset2, boolean endian2)
	{
		return makeTestNameBinaryConvert(testType, functionName, value, byte_array, offset1, endian1, -1, result, offset2, endian2, -1, false);
	}
	
	public static String makeTestNameBinaryConvert(String testType, String functionName, Number value, byte[] byte_array, int offset1, boolean endian1, int length1, Number result, int offset2, boolean endian2, int length2, boolean sign)
	{
		/*
		 *    Explicit version
		 *    Function: [functionName]
		 *    Type: [testType]
		 *    Value: [Value]
		 *       Offset1: [Offset1]
		 *       BigEndian1: [endian1]
		 *       Length1: [length1]
		 *       Value  Array1: [byte_array.0] [byte_array.1] ...
		 *    Result: [result]
		 *       Offset2: [Offset2]
		 *       BigEndian2: [endian2]
		 *       Length2: [length2]
		 *       SignExtend2: [sign]
		 *       
		 *    Fast version
		 *    Function: [functionName]
		 *    Type: [testType]
		 *    Value: [Value]
		 *    Result: [result]
		 *       Value  Array1: [byte_array.0] [byte_array.1] ...
		 *       
		 *    Simple version
		 *    Function: [functionName], Type: [testType], Value: [Value], Result: [result]
		 */
		
		String simpleInfo = "";
		simpleInfo += "Function: " + functionName;
		simpleInfo += ", Type: " + testType;
		simpleInfo += ", Value: " + value;
		simpleInfo += ", Result: " + result;
		
		String explicitInfo = "";
		explicitInfo += "   Function: " + functionName;
		explicitInfo += "\n   Type: " + testType;
		explicitInfo += "\n   Value: " + value;
		explicitInfo += "\n      Offset1: " + offset1;
		explicitInfo += "\n      BigEndian1: " + endian1;
		explicitInfo += "\n      Length1: " + length1;
		explicitInfo += "\n      Value  Array1: " + byteArrayToString(byte_array);
		explicitInfo += "\n   Result: " + result;
		explicitInfo += "\n      Offset2: " + offset2;
		explicitInfo += "\n      BigEndian2: " + endian2;
		explicitInfo += "\n      Length2: " + length2;
		explicitInfo += "\n      SignExtend2: " + sign;
		
		String fastInfo = "";
		fastInfo += "   Function: " + functionName;
		fastInfo += "\n   Type: " + testType;
		fastInfo += "\n   Value: " + value;
		fastInfo += "\n   Result: " + result;
		fastInfo += "\n      Value  Array1: " + byteArrayToString(byte_array);
		
		String combinedInfos = simpleInfo + "|" + explicitInfo + "|" + fastInfo + "|";
		combinedInfos += "\nReason";
		return combinedInfos;
	}
	
	public static String makeTestNameConverterValue(String testType, String functionName, Number value, char[] externalDecimal, int offset1, int precision1, int decimalType1, Number result, int offset2, int precision2, int decimalType2, boolean errorChecking)
	{		
		
		/*
		 *    Same as the other function, except accepts char arrays instead
		 *    
		 *    Explicit version
		 *    Function: [functionName]
		 *    Type: [testType]
		 *    ErrorCheck: [errorChecking]
		 *    Value: [Value]
		 *       DecimalType1: [decimalType1]
		 *       Offset1: [Offset1]
		 *       Precision1: [Precision1]
		 *       Value  Array1: [Op1.0] [Op1.1] ...
		 *    Result: [result]
		 *       DecimalType2: [decimalType2]
		 *       Offset2: [Offset2]
		 *       Precision2: [Precision2]
		 *       
		 *    Fast version
		 *    Function: [functionName]
		 *    Type: [testType]
		 *    Value: [Value]
		 *    Result: [result]
		 *       DecimalType1: [decimalType1]
		 *       DecimalType2: [decimalType2]
		 *       Value  Array1: [Op1.0] [Op1.1] ...
		 *       
		 *    Simple version
		 *    Function: [functionName], Type: [testType], Value: [Value], DecimalType1: [decimalType1], Result: [result]
		 */
		
		String simpleInfo = "";
		simpleInfo += "Function: " + functionName;
		simpleInfo += ", Type: " + testType;
		simpleInfo += ", Value: " + value;
		simpleInfo += ", DecimalType1: " + decimalTypeToString(decimalType1);
		simpleInfo += ", Result: " + result;
		
		String explicitInfo = "";
		explicitInfo += "   Function: " + functionName;
		explicitInfo += "\n   Type: " + testType;
		explicitInfo += "\n   ErrorCheck: " + errorChecking;
		explicitInfo += "\n   Value: " + value;
		explicitInfo += "\n      DecimalType1: " + decimalTypeToString(decimalType1);
		explicitInfo += "\n      Offset1: " + offset1;
		explicitInfo += "\n      Precision1: " + precision1;
		explicitInfo += "\n      Value  Array1: " + charArrayToString(externalDecimal);
		explicitInfo += "\n   Result: " + result;
		explicitInfo += "\n      DecimalType2: " + decimalTypeToString(decimalType2);
		explicitInfo += "\n      Offset2: " + offset2;
		explicitInfo += "\n      Precision2: " + precision2;
		
		String fastInfo = "";
		fastInfo += "   Function: " + functionName;
		fastInfo += "\n   Type: " + testType;
		fastInfo += "\n   Value: " + value;
		fastInfo += "\n   Result: " + result;
		fastInfo += "\n      DecimalType1: " + decimalTypeToString(decimalType1);
		fastInfo += "\n      DecimalType2: " + decimalTypeToString(decimalType2);
		fastInfo += "\n      Value  Array1: " + charArrayToString(externalDecimal);
		
		String combinedInfos = simpleInfo + "|" + explicitInfo + "|" + fastInfo + "|";
		combinedInfos += "\nReason";
		return combinedInfos;
	}
	
	public static String makeTestNameConverterValue(String testType, String functionName, Number value, byte[] externalDecimal, int offset1, int precision1, int decimalType1, Number result, int offset2, int precision2, int decimalType2, boolean errorChecking)
	{		
		/*
		 *    Explicit version
		 *    Function: [functionName]
		 *    Type: [testType]
		 *    ErrorCheck: [errorChecking]
		 *    Value: [Value]
		 *       DecimalType1: [decimalType1]
		 *       Offset1: [Offset1]
		 *       Precision1: [Precision1]
		 *       Value  Array1: [Op1.0] [Op1.1] ...
		 *    Result: [result]
		 *       DecimalType2: [decimalType2]
		 *       Offset2: [Offset2]
		 *       Precision2: [Precision2]
		 *       
		 *    Fast version
		 *    Function: [functionName]
		 *    Type: [testType]
		 *    Value: [Value]
		 *    Result: [result]
		 *       DecimalType1: [decimalType1]
		 *       DecimalType2: [decimalType2]
		 *       Value  Array1: [Op1.0] [Op1.1] ...
		 *       
		 *    Simple version
		 *    Function: [functionName], Type: [testType], Value: [Value], DecimalType1: [decimalType1], Result: [result]
		 */
		
		String simpleInfo = "";
		simpleInfo += "Function: " + functionName;
		simpleInfo += ", Type: " + testType;
		simpleInfo += ", Value: " + value;
		simpleInfo += ", DecimalType1: " + decimalTypeToString(decimalType1);
		simpleInfo += ", Result: " + result;
		
		String explicitInfo = "";
		explicitInfo += "   Function: " + functionName;
		explicitInfo += "\n   Type: " + testType;
		explicitInfo += "\n   ErrorCheck: " + errorChecking;
		explicitInfo += "\n   Value: " + value;
		explicitInfo += "\n      DecimalType1: " + decimalTypeToString(decimalType1);
		explicitInfo += "\n      Offset1: " + offset1;
		explicitInfo += "\n      Precision1: " + precision1;
		explicitInfo += "\n      Value  Array1: " + byteArrayToString(externalDecimal);
		explicitInfo += "\n   Result: " + result;
		explicitInfo += "\n      DecimalType2: " + decimalTypeToString(decimalType2);
		explicitInfo += "\n      Offset2: " + offset2;
		explicitInfo += "\n      Precision2: " + precision2;
		
		String fastInfo = "";
		fastInfo += "   Function: " + functionName;
		fastInfo += "\n   Type: " + testType;
		fastInfo += "\n   Value: " + value;
		fastInfo += "\n   Result: " + result;
		fastInfo += "\n      DecimalType1: " + decimalTypeToString(decimalType1);
		fastInfo += "\n      DecimalType2: " + decimalTypeToString(decimalType2);
		fastInfo += "\n      Value  Array1: " + byteArrayToString(externalDecimal);
		
		String combinedInfos = simpleInfo + "|" + explicitInfo + "|" + fastInfo + "|";
		combinedInfos += "\nReason";
		return combinedInfos;
	}
	
	public static String makeTestNameConverterPacked(String testType, String functionName, Number value, byte[] packedDecimal, int offset1, int precision1, Number result, int offset2, int precision2, boolean errorChecking)
	{
		/*
		 *    Explicit version
		 *    Function: [functionName]
		 *    Type: [testType]
		 *    ErrorCheck: [errorChecking]
		 *    Value: [Value]
		 *       Offset1: [Offset1]
		 *       Precision1: [Precision1]
		 *       Value  Array1: [Op1.0] [Op1.1] ...
		 *    Result: [result]
		 *       Offset2: [Offset2]
		 *       Precision2: [Precision2]
		 *       
		 *    Fast version
		 *    Function: [functionName]
		 *    Type: [testType]
		 *    Value: [Value1]
		 *    Result: [result]
		 *       Value  Array1: [Op1.0] [Op1.1] ...
		 *       
		 *    Simple version
		 *    Function: [functionName], Type: [testType], Value: [Value1], Result: [result]
		 */
		
		String simpleInfo = "";
		simpleInfo += "Function: " + functionName;
		simpleInfo += ", Type: " + testType;
		simpleInfo += ", Value: " + value;
		simpleInfo += ", Result: " + result;
		
		String explicitInfo = "";
		explicitInfo += "   Function: " + functionName;
		explicitInfo += "\n   Type: " + testType;
		explicitInfo += "\n   ErrorCheck: " + errorChecking;
		explicitInfo += "\n   Value: " + value;
		explicitInfo += "\n      Offset1: " + offset1;
		explicitInfo += "\n      Precision1: " + precision1;
		explicitInfo += "\n      Value  Array1: " + byteArrayToString(packedDecimal);
		explicitInfo += "\n   Result: " + result;
		explicitInfo += "\n      Offset2: " + offset2;
		explicitInfo += "\n      Precision2: " + precision2;
		
		String fastInfo = "";
		fastInfo += "   Function: " + functionName;
		fastInfo += "\n   Type: " + testType;
		fastInfo += "\n   Value: " + value;
		fastInfo += "\n   Result: " + result;
		
		String combinedInfos = simpleInfo + "|" + explicitInfo + "|" + fastInfo + "|";
		combinedInfos += "\nReason";
		return combinedInfos;
	}
	
	public static String makeTestNameConverterArray(String testType, String functionName, Number value, byte[] packedDecimal, int offset1, int precision1, char[] convert2, int offset2, int precision2, int decimalType2, byte[] convert3, int offset3, int precision3, int decimalType3, boolean errorChecking)
	{
		/*
		 * 	  Same as other function, except accepts chars
		 * 
		 *    Explicit version
		 *    Function: [functionName]
		 *    Type: [testType]
		 *    ErrorCheck: [errorChecking]
		 *    Value: [Value]
		 *       Offset1: [Offset1]
		 *       Precision1: [Precision1]
		 *       Value  Array1: [Op1.0] [Op1.1] ...
		 *    Convert1: 
		 *    	 DecimalType2: [decimalType2]
		 *       Offset2: [Offset2]
		 *       Precision2: [Precision2]
		 *       ConvertArray2: [Op2.0] [Op2.1] ...
		 *    Convert2: 
		 *    	 DecimalType3: [decimalType3]
		 *       Offset3: [Offset3]
		 *       Precision3: [Precision3]
		 *       ConvertArray3: [resultantArray.0] [resultantArray.1] ...
		 *       
		 *    Fast version
		 *    Function: [functionName]
		 *    Type: [testType]
		 *    Value: [Value1]
		 *    	 DecimalType2: [decimalType2]
		 *    	 DecimalType3: [decimalType3]
		 *       Value  Array1: [Op1.0] [Op1.1] ...
		 *       ConvertArray2: [Op2.0] [Op2.1] ...
		 *       ConvertArray3: [resultantArray.0] [resultantArray.1] ... 
		 *       
		 *    Simple version
		 *    Function: [functionName], Type: [testType], Value: [Value1], DecimalType2: [decimalType2], DecimalType3: [decimalType3]
		 */
		
		String simpleInfo = "";
		simpleInfo += "Function: " + functionName;
		simpleInfo += ", Type: " + testType;
		simpleInfo += ", Value: " + value;
		simpleInfo += ", DecimalType2: " + decimalTypeToString(decimalType2);
		simpleInfo += ", DecimalType3: " + decimalTypeToString(decimalType3);
		
		String explicitInfo = "";
		explicitInfo += "   Function: " + functionName;
		explicitInfo += "\n   Type: " + testType;
		explicitInfo += "\n   ErrorCheck: " + errorChecking;
		explicitInfo += "\n   Value: " + value;
		explicitInfo += "\n      Offset1: " + offset1;
		explicitInfo += "\n      Precision1: " + precision1;
		explicitInfo += "\n      Value  Array1: " + byteArrayToString(packedDecimal);
		explicitInfo += "\n   Convert1: ";
		explicitInfo += "\n      DecimalType2: " + decimalTypeToString(decimalType2);
		explicitInfo += "\n      Offset2: " + offset2;
		explicitInfo += "\n      Precision2: " + precision2;
		explicitInfo += "\n      ConvertArray2: " + charArrayToString(convert2);
		explicitInfo += "\n   Convert2: ";
		explicitInfo += "\n      DecimalType3: " + decimalTypeToString(decimalType3);
		explicitInfo += "\n      Offset3: " + offset3;
		explicitInfo += "\n      Precision3: " + precision3;
		explicitInfo += "\n      ConvertArray3: " + byteArrayToString(convert3);
		
		String fastInfo = "";
		fastInfo += "   Function: " + functionName;
		fastInfo += "\n   Type: " + testType;
		fastInfo += "\n   Value: " + value;
		fastInfo += "\n      DecimalType2: " + decimalTypeToString(decimalType2);
		fastInfo += "\n      DecimalType3: " + decimalTypeToString(decimalType3);
		fastInfo += "\n      Value  Array1: " + byteArrayToString(packedDecimal);
		fastInfo += "\n      ConvertArray2: " + charArrayToString(convert2);
		fastInfo += "\n      ConvertArray3: " + byteArrayToString(convert3);
		
		String combinedInfos = simpleInfo + "|" + explicitInfo + "|" + fastInfo + "|";
		combinedInfos += "\nReason";
		return combinedInfos;
	}
	
	public static String makeTestNameConverterArray(String testType, String functionName, Number value, byte[] packedDecimal, int offset1, int precision1, byte[] convert2, int offset2, int precision2, int decimalType2, byte[] convert3, int offset3, int precision3, int decimalType3, boolean errorChecking)
	{
		/*
		 *    Explicit version
		 *    Function: [functionName]
		 *    Type: [testType]
		 *    ErrorCheck: [errorChecking]
		 *    Value: [Value]
		 *       Offset1: [Offset1]
		 *       Precision1: [Precision1]
		 *       Value  Array1: [Op1.0] [Op1.1] ...
		 *    Convert1: 
		 *    	 DecimalType2: [decimalType2]
		 *       Offset2: [Offset2]
		 *       Precision2: [Precision2]
		 *       ConvertArray2: [Op2.0] [Op2.1] ...
		 *    Convert2: 
		 *    	 DecimalType3: [decimalType3]
		 *       Offset3: [Offset3]
		 *       Precision3: [Precision3]
		 *       ConvertArray3: [resultantArray.0] [resultantArray.1] ...
		 *       
		 *    Fast version
		 *    Function: [functionName]
		 *    Type: [testType]
		 *    Value: [Value1]
		 *    	 DecimalType2: [decimalType2]
		 *    	 DecimalType3: [decimalType3]
		 *       Value  Array1: [Op1.0] [Op1.1] ...
		 *       ConvertArray2: [Op2.0] [Op2.1] ...
		 *       ConvertArray3: [resultantArray.0] [resultantArray.1] ... 
		 *       
		 *    Simple version
		 *    Function: [functionName], Type: [testType], Value: [Value1], DecimalType2: [decimalType2], DecimalType3: [decimalType3]
		 */
		
		String simpleInfo = "";
		simpleInfo += "Function: " + functionName;
		simpleInfo += ", Type: " + testType;
		simpleInfo += ", Value: " + value;
		simpleInfo += ", DecimalType2: " + decimalTypeToString(decimalType2);
		simpleInfo += ", DecimalType3: " + decimalTypeToString(decimalType3);
		
		String explicitInfo = "";
		explicitInfo += "   Function: " + functionName;
		explicitInfo += "\n   Type: " + testType;
		explicitInfo += "\n   ErrorCheck: " + errorChecking;
		explicitInfo += "\n   Value: " + value;
		explicitInfo += "\n      Offset1: " + offset1;
		explicitInfo += "\n      Precision1: " + precision1;
		explicitInfo += "\n      Value  Array1: " + byteArrayToString(packedDecimal);
		explicitInfo += "\n   Convert1: ";
		explicitInfo += "\n      DecimalType2: " + decimalTypeToString(decimalType2);
		explicitInfo += "\n      Offset2: " + offset2;
		explicitInfo += "\n      Precision2: " + precision2;
		explicitInfo += "\n      ConvertArray2: " + byteArrayToString(convert2);
		explicitInfo += "\n   Convert2: ";
		explicitInfo += "\n      DecimalType3: " + decimalTypeToString(decimalType3);
		explicitInfo += "\n      Offset3: " + offset3;
		explicitInfo += "\n      Precision3: " + precision3;
		explicitInfo += "\n      ConvertArray3: " + byteArrayToString(convert3);
		
		String fastInfo = "";
		fastInfo += "   Function: " + functionName;
		fastInfo += "\n   Type: " + testType;
		fastInfo += "\n   Value: " + value;
		fastInfo += "\n      DecimalType2: " + decimalTypeToString(decimalType2);
		fastInfo += "\n      DecimalType3: " + decimalTypeToString(decimalType3);
		fastInfo += "\n      Value  Array1: " + byteArrayToString(packedDecimal);
		fastInfo += "\n      ConvertArray2: " + byteArrayToString(convert2);
		fastInfo += "\n      ConvertArray3: " + byteArrayToString(convert3);
		
		String combinedInfos = simpleInfo + "|" + explicitInfo + "|" + fastInfo + "|";
		combinedInfos += "\nReason";
		return combinedInfos;
	}
	
	public static String decimalTypeToString(int decimalType)
	{
		switch (decimalType)
		{
			case DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING:
				return "EBCDIC_SIGN_EMBEDDED_TRAILING";
			case DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING:
				return "EBCDIC_SIGN_EMBEDDED_LEADING";
			case DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING:
				return "EBCDIC_SIGN_SEPARATE_TRAILING";
			case DecimalData.EBCDIC_SIGN_SEPARATE_LEADING:
				return "EBCDIC_SIGN_SEPARATE_LEADING";
			case DecimalData.UNICODE_UNSIGNED:
				return "UNICODE_UNSIGNED";
			case DecimalData.UNICODE_SIGN_SEPARATE_LEADING:
				return "UNICODE_SIGN_SEPARATE_LEADING";
			case DecimalData.UNICODE_SIGN_SEPARATE_TRAILING:
				return "UNICODE_SIGN_SEPARATE_TRAILING";
			default:
				return "UNKNOWN_DATA_TYPE";
		}
	}
	
	public static String makeTestNameArithmetic(String testType, String functionName, Number value1, Number value2, byte[] op1, int offset1, int precision1, byte[] op2, int offset2, int precision2, byte[] resultantArray, int offset3, int precision3, Number correct, byte[] correctArray, int offset4, int precision4, boolean errorChecking)
	{
		/*
		 *    Explicit version
		 *    Function: [functionName]
		 *    Type: [testType]
		 *    ErrorCheck: [errorChecking]
		 *    Value1: [Value1]
		 *       Offset1: [Offset1]
		 *       Precision1: [Precision1]
		 *       Operand1: [Op1.0] [Op1.1] ...
		 *    Value2: [Value2]
		 *       Offset2: [Offset2]
		 *       Precision2: [Precision2]
		 *       Operand2: [Op2.0] [Op2.1] ...
		 *    Result: 
		 *       Offset3: [Offset3]
		 *       Precision3: [Precision3]
		 *       ResultantArray: [resultantArray.0] [resultantArray.1] ...
		 *    CorrectValue: [correct]
		 *       Offset4: [Offset4]
		 *       Precision4: [Precision4]
		 *       Correct  Array: [correctArray.0] [correctArray.1] ...
		 *       
		 *    Fast version
		 *    Function: [functionName]
		 *    Type: [testType]
		 *    Value1: [Value1]
		 *    Value2: [Value2]
		 *    Result: 
		 *    CorrectValue: [correct]
		 *       Operand1: [Op1.0] [Op1.1] ...
		 *       Operand2: [Op2.0] [Op2.1] ...
		 *       ResultantArray: [resultantArray.0] [resultantArray.1] ...
		 *       Correct  Array: [correctArray.0] [correctArray.1] ...    
		 *       
		 *    Simple version
		 *    Function: [functionName], Type: [testType], Value1: [Value1], Value2: [Value2], Correct: [correct]
		 */
		String simpleInfo = "";
		simpleInfo += "Function: " + functionName;
		simpleInfo += ", Type: " + testType;
		simpleInfo += ", Value1: " + value1;
		simpleInfo += ", Value2: " + value2;
		simpleInfo += ", Correct: " + correct;
		
		String explicitInfo = "";
		explicitInfo += "   Function: " + functionName;
		explicitInfo += "\n   Type: " + testType;
		explicitInfo += "\n   ErrorCheck: " + errorChecking;
		explicitInfo += "\n   Value1: " + value1;
		explicitInfo += "\n      Offset1: " + offset1;
		explicitInfo += "\n      Precision1: " + precision1;
		explicitInfo += "\n      Operand1: " + byteArrayToString(op1);
		explicitInfo += "\n   Value2: " + value2;
		explicitInfo += "\n      Offset2: " + offset2;
		explicitInfo += "\n      Precision2: " + precision2;
		explicitInfo += "\n      Operand2: " + byteArrayToString(op2);
		explicitInfo += "\n   Result: ";
		explicitInfo += "\n      Offset3: " + offset3;
		explicitInfo += "\n      Precision3: " + precision3;
		explicitInfo += "\n      ResultantArray: " + byteArrayToString(resultantArray);
		explicitInfo += "\n   CorrectValue: " + correct;
		explicitInfo += "\n      Offset4: " + offset4;
		explicitInfo += "\n      Precision4: " + precision4;
		explicitInfo += "\n      Correct  Array: " + byteArrayToString(correctArray);
		
		String fastInfo = "";
		fastInfo += "   Function: " + functionName;
		fastInfo += "\n   Type: " + testType;
		fastInfo += "\n   Value1: " + value1;
		fastInfo += "\n   Value2: " + value2;
		fastInfo += "\n   CorrectValue: " + correct;
		fastInfo += "\n      Operand1: " + byteArrayToString(op1);
		fastInfo += "\n      Operand2: " + byteArrayToString(op2);
		fastInfo += "\n      ResultantArray: " + byteArrayToString(resultantArray);
		fastInfo += "\n      Correct  Array: " + byteArrayToString(correctArray);
		
		String combinedInfos = simpleInfo + "|" + explicitInfo + "|" + fastInfo + "|";
		combinedInfos += "\nReason";
		return combinedInfos;
	}
	
	public static String charArrayToString(char[] op)
	{
		String arrayString = "";
		for (int i=0;i<op.length;i++)
		{
			arrayString += "[" + toHex((byte)op[i]) + "]";
		}
		return arrayString;
	}
	
	public static String byteArrayToString(byte[] op)
	{
		String arrayString = "";
		for (int i=0;i<op.length;i++)
		{
			arrayString += "[" + toHex(op[i]) + "]";
		}
		return arrayString;
	}
	
	public static String toHex(byte x)
	{
		long xl = 0;
		xl = x&0x0F | x&0xF0;
		String hex = Long.toHexString(xl);
		if (hex.length() < 2)
			hex = "0"+hex;
		return hex.toUpperCase();
	}
	
	public static byte[] reverseByteOrder(byte[] byte_array, int numBytes)
	{
		byte[] reversed = new byte[byte_array.length];
		
		if (!divideInto(byte_array, numBytes))
			throw new IllegalArgumentException();
		else
		{
			int iterations = byte_array.length / numBytes;
			
			int oldIndex = 0;
			int newIndex;
			
			for (int k=0;k<iterations;k++)
			{				
				newIndex = k*numBytes + numBytes - 1;
				for (int i=0;i<numBytes;i++)
				{
					reversed[newIndex] = byte_array[oldIndex];
					oldIndex++;
					newIndex--;
				}
			}
		}
		
		return reversed;
	}
	
	/* Function should return the number
	 * Optimizer should not be able to propagate the constant
	 */
	public static int destroyConstant(int x)
	{
		int x1 = x-10;
		int x2 = x+10;
		for (int i=x1;i<x2;i++)
		{
			if (i==x)
			{
				return i;
			}
		}
		return x;
	}
	
	private static boolean divideInto (byte[] byte_array, int numBytes)
	{
		if (byte_array.length % numBytes == 0)
			return true;
		return false;
	}
	
	public static class TestValue
	{
		public static class SmallPositive
		{
			public static double DoubleValue = 9.0;
			public static float FloatValue = 9.0f;
			public static int IntValue = 9;
			public static short ShortValue = (short)9;
			public static long LongValue = 9l;
			public static BigDecimal BigDecimalValue = BigDecimal.valueOf(LongValue);
			public static BigInteger BigIntegerValue = BigInteger.valueOf(LongValue);
			
			public static String TestName = "Small Positive";
		}
		public static class LargePositive
		{
			public static double DoubleValue = 9000000.0;
			public static float FloatValue = 9000000.0f;
			public static int IntValue = 9000000;
			public static short ShortValue = (short)25000;
			public static long LongValue = 900000000l;
			public static BigDecimal BigDecimalValue = BigDecimal.valueOf(LongValue);
			public static BigInteger BigIntegerValue = BigInteger.valueOf(LongValue);
			
			public static String TestName = "Large Positive";
		}
		public static class SmallNegative
		{
			public static double DoubleValue = -9.0;
			public static float FloatValue = -9.0f;
			public static int IntValue = -9;
			public static short ShortValue = (short)-9;
			public static long LongValue = -9l;
			public static BigDecimal BigDecimalValue = BigDecimal.valueOf(LongValue);
			public static BigInteger BigIntegerValue = BigInteger.valueOf(LongValue);
			
			public static String TestName = "Small Negative";
		}
		public static class LargeNegative
		{
			public static double DoubleValue = -9000000.0;
			public static float FloatValue = -9000000.0f;
			public static int IntValue = -9000000;
			public static short ShortValue = (short)-25000;
			public static long LongValue = -900000000l;
			public static BigDecimal BigDecimalValue = BigDecimal.valueOf(LongValue);
			public static BigInteger BigIntegerValue = BigInteger.valueOf(LongValue);
			
			public static String TestName = "Large Negative";
		}
		public static class Zero
		{
			public static double DoubleValue = 0.0;
			public static float FloatValue = 0.0f;
			public static int IntValue = 0;
			public static short ShortValue = (short)0;
			public static long LongValue = 0l;
			public static BigDecimal BigDecimalValue = BigDecimal.valueOf(LongValue);
			public static BigInteger BigIntegerValue = BigInteger.valueOf(LongValue);
			
			public static String TestName = "Zero";
		}
		public static class LargestPossible
		{
			public static double DoubleValue = Double.MAX_VALUE;
			public static float FloatValue = Float.MAX_VALUE;
			public static int IntValue = Integer.MAX_VALUE;
			public static short ShortValue = Short.MAX_VALUE;
			public static long LongValue = Long.MAX_VALUE;
			public static BigDecimal BigDecimalValue = BigDecimal.valueOf(LongValue);
			public static BigInteger BigIntegerValue = BigInteger.valueOf(LongValue);
			
			public static String TestName = "Largest Possible";
		}
		public static class SmallestPossible
		{
			public static double DoubleValue = Double.MIN_VALUE;
			public static float FloatValue = Float.MIN_VALUE;              
			public static int IntValue = Integer.MIN_VALUE;
			public static short ShortValue = Short.MIN_VALUE;
			public static long LongValue = Long.MIN_VALUE;
			public static BigDecimal BigDecimalValue = BigDecimal.valueOf(LongValue);
			public static BigInteger BigIntegerValue = BigInteger.valueOf(LongValue);
			
			public static String TestName = "Smallest Possible";
		}
		public static class NegativeOffset
		{
			public static int Offset = -1;
		}
	}
}


