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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import com.ibm.dataaccess.DecimalData;
import com.ibm.dataaccess.PackedDecimal;

import net.openj9.test.Utils;

import java.lang.reflect.Method;

import java.math.BigInteger;

import java.util.Arrays;
import java.util.Random;

public class TestPerformance
{
    BigInteger[] SmallPositives;
    BigInteger[] SmallNegatives;
    BigInteger[] LargePositives;
    BigInteger[] LargeNegatives;

    byte[][] resultArray;
    byte[][] referenceArray;
    
    boolean[] comparisionResultArray;

    int[] comparisionReferenceArray;
    
    byte[][] Array1;
    byte[][] Array2;
    
    BigInteger[] bigIntArray1;
    BigInteger[] bigIntArray2;

    static long randomSeed = 8002;

    static Random randomGenerator = new Random(System.currentTimeMillis());

    @Test
    public void testIntrinsicPerformance() throws Exception
    {
        int sampleSize = 1000;
        int multiplier = 100;
        try
        {
            {
                testAdd(sampleSize, multiplier);
                testSub(sampleSize, multiplier);
                testMul(sampleSize, multiplier);
                testDiv(sampleSize, multiplier);
                testRem(sampleSize, multiplier);
            }

            {
                testGT(sampleSize, multiplier);
                testGE(sampleSize, multiplier);
                testEQ(sampleSize, multiplier);
                testNE(sampleSize, multiplier);
                testLT(sampleSize, multiplier);
                testLE(sampleSize, multiplier);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public void testAdd(int sampleSize, int multiplier) throws Exception
    {
        setUpResultSpace(sampleSize, sampleSize);
        generateValues(sampleSize, sampleSize);

        getReferenceValues(10, sampleSize, "add");
        {
            long startTime, endTime;
            startTime = System.currentTimeMillis();
            for (int j = 0; j < multiplier; ++j)
            {
                for (int i = 0; i < sampleSize; ++i)
                {
                    PackedDecimal.addPackedDecimal(resultArray[i], 0, 20, Array1[i], 0, 10, Array2[i], 0, 10, false);
                }
            }
            endTime = System.currentTimeMillis();
            printElapsedTime(startTime, endTime);
        }
        verify(sampleSize, "addPackedDecimal");
    }

    public void testSub(int sampleSize, int multiplier) throws Exception
    {
        setUpResultSpace(sampleSize, sampleSize);
        generateValues(sampleSize, sampleSize);

        getReferenceValues(10, sampleSize, "subtract");
        {
            long startTime, endTime;
            startTime = System.currentTimeMillis();
            for (int j = 0; j < multiplier; ++j)
                for (int i = 0; i < sampleSize; ++i)
                {
                    PackedDecimal.subtractPackedDecimal(resultArray[i], 0, 20, Array1[i], 0, 10, Array2[i], 0, 10, false);
                }
            endTime = System.currentTimeMillis();
            printElapsedTime(startTime, endTime);

        }
        verify(sampleSize, "subtractPackedDecimal");
    }

    public void testMul(int sampleSize, int multiplier) throws Exception
    {
        setUpResultSpace(sampleSize, sampleSize);
        generateValues(sampleSize, sampleSize);

        getReferenceValues(10, sampleSize, "multiply");
        {
            long startTime, endTime;
            startTime = System.currentTimeMillis();
            for (int j = 0; j < multiplier; ++j)
                for (int i = 0; i < sampleSize; ++i)
                {
                    PackedDecimal.multiplyPackedDecimal(resultArray[i], 0, 20, Array1[i], 0, 10, Array2[i], 0, 10, false);
                }
            endTime = System.currentTimeMillis();
            printElapsedTime(startTime, endTime);

        }
        verify(sampleSize, "multiplyPackedDecimal");
    }

    public void testDiv(int sampleSize, int multiplier) throws Exception
    {
        setUpResultSpace(sampleSize, sampleSize);
        generateValues(sampleSize, sampleSize);

        getReferenceValues(10, sampleSize, "divide");
        {
            long startTime, endTime;
            startTime = System.currentTimeMillis();
            for (int j = 0; j < multiplier; ++j)
                for (int i = 0; i < sampleSize; ++i)
                {
                    PackedDecimal.dividePackedDecimal(resultArray[i], 0, 20, Array1[i], 0, 10, Array2[i], 0, 10, false);
                }
            endTime = System.currentTimeMillis();
            printElapsedTime(startTime, endTime);

        }
        verify(sampleSize, "dividePackedDecimal");
    }

    public void testRem(int sampleSize, int multiplier) throws Exception
    {
        setUpResultSpace(sampleSize, sampleSize);
        generateValues(sampleSize, sampleSize);

        getReferenceValues(10, sampleSize, "remainder");
        {
            long startTime, endTime;
            startTime = System.currentTimeMillis();
            for (int j = 0; j < multiplier; ++j)
                for (int i = 0; i < sampleSize; ++i)
                {
                    PackedDecimal.remainderPackedDecimal(resultArray[i], 0, 20, Array1[i], 0, 10, Array2[i], 0, 10, false);
                }
            endTime = System.currentTimeMillis();
            printElapsedTime(startTime, endTime);
        }
        verify(sampleSize, "remainderPackedDecimal");
    }

    public void testNE(int sampleSize, int multiplier) throws Exception
    {
        setUpResultSpace(sampleSize, sampleSize);
        generateValues(sampleSize, sampleSize);

        getComparisionReferenceValue(10, sampleSize, "compareTo");
        {
            long startTime, endTime;
            startTime = System.currentTimeMillis();
            for (int j = 0; j < multiplier; ++j)
                for (int i = 0; i < sampleSize; ++i)
                {
                    comparisionResultArray[i] = PackedDecimal.notEqualsPackedDecimal(Array1[i], 0, 10, Array2[i], 0, 10);
                }
            endTime = System.currentTimeMillis();
            printElapsedTime(startTime, endTime);
        }

        verifyComparison(sampleSize, NOT_EQUAL);
    }

    public void testLE(int sampleSize, int multiplier) throws Exception
    {
        setUpResultSpace(sampleSize, sampleSize);
        generateValues(sampleSize, sampleSize);

        getComparisionReferenceValue(10, sampleSize, "compareTo");
        {
            long startTime, endTime;
            startTime = System.currentTimeMillis();
            for (int j = 0; j < multiplier; ++j)
                for (int i = 0; i < sampleSize; ++i)
                {
                    comparisionResultArray[i] = PackedDecimal.lessThanOrEqualsPackedDecimal(Array1[i], 0, 10, Array2[i], 0, 10);
                }
            endTime = System.currentTimeMillis();
            printElapsedTime(startTime, endTime);
        }

        verifyComparison(sampleSize, LESS_EQUAL);
    }

    public void testLT(int sampleSize, int multiplier) throws Exception
    {

        generateValues(sampleSize, sampleSize);

        getComparisionReferenceValue(10, sampleSize, "compareTo");
        {
            long startTime, endTime;
            startTime = System.currentTimeMillis();
            for (int j = 0; j < multiplier; ++j)
                for (int i = 0; i < sampleSize; ++i)
                {
                    comparisionResultArray[i] = PackedDecimal.lessThanPackedDecimal(Array1[i], 0, 10, Array2[i], 0, 10);
                }
            endTime = System.currentTimeMillis();
            printElapsedTime(startTime, endTime);
        }

        verifyComparison(sampleSize, LESS_THAN);
    }

    public void testEQ(int sampleSize, int multiplier) throws Exception
    {

        generateValues(sampleSize, sampleSize);

        getComparisionReferenceValue(10, sampleSize, "compareTo");
        {
            long startTime, endTime;
            startTime = System.currentTimeMillis();
            for (int j = 0; j < multiplier; ++j)
                for (int i = 0; i < sampleSize; ++i)
                {
                    comparisionResultArray[i] = PackedDecimal.equalsPackedDecimal(Array1[i], 0, 10, Array2[i], 0, 10);
                }
            endTime = System.currentTimeMillis();
            printElapsedTime(startTime, endTime);
        }

        verifyComparison(sampleSize, EQUAL);
    }

    public void testGT(int sampleSize, int multiplier) throws Exception
    {

        generateValues(sampleSize, sampleSize);

        getComparisionReferenceValue(10, sampleSize, "compareTo");
        {
            long startTime, endTime;
            startTime = System.currentTimeMillis();
            for (int j = 0; j < multiplier; ++j)
                for (int i = 0; i < sampleSize; ++i)
                {
                    comparisionResultArray[i] = PackedDecimal.greaterThanPackedDecimal(Array1[i], 0, 10, Array2[i], 0, 10);
                }
            endTime = System.currentTimeMillis();
            printElapsedTime(startTime, endTime);
        }

        verifyComparison(sampleSize, GREATER_THAN);
    }

    public void testGE(int sampleSize, int multiplier) throws Exception
    {

        generateValues(sampleSize, sampleSize);

        getComparisionReferenceValue(10, sampleSize, "compareTo");
        {
            long startTime, endTime;
            startTime = System.currentTimeMillis();
            for (int j = 0; j < multiplier; ++j)
                for (int i = 0; i < sampleSize; ++i)
                {
                    comparisionResultArray[i] = PackedDecimal.greaterThanOrEqualsPackedDecimal(Array1[i], 0, 10, Array2[i], 0, 10);
                }
            endTime = System.currentTimeMillis();
            printElapsedTime(startTime, endTime);
        }

        verifyComparison(sampleSize, GREATER_EQUAL);
    }

    final private int GREATER_THAN = 0;
    final private int GREATER_EQUAL = 1;
    final private int EQUAL = 2;
    final private int NOT_EQUAL = 3;
    final private int LESS_THAN = 4;
    final private int LESS_EQUAL = 5;

    public boolean isThan(int compareMethod, int input)
    {
        switch (compareMethod)
        {
            case GREATER_THAN:
                return input > 0;
            case GREATER_EQUAL:
                return input >= 0;
            case EQUAL:
                return input == 0;
            case NOT_EQUAL:
                return input != 0;
            case LESS_THAN:
                return input < 0;
            case LESS_EQUAL:
                return input <= 0;
        }
        return false;
    }

    public void verifyComparison(int sampleSize, int compareMethod)
    {
        for (int i = 0; i < sampleSize; ++i)
            assertEquals(isThan(compareMethod, comparisionReferenceArray[i]), comparisionResultArray[i]);
    }

    public void getComparisionReferenceValue(int precision, int sampleSize, String methodName) throws Exception
    {
        comparisionResultArray = new boolean[sampleSize];
        comparisionReferenceArray = new int[sampleSize];

        Method method = null;
        Class<?> c = Class.forName("java.math.BigInteger");
        method = c.getDeclaredMethod(methodName, BigInteger.class);
        for (int i = 0; i < sampleSize; ++i)
        {
            BigInteger value1 = Utils.toPrecision(bigIntArray1[i], 10); // Precision 10 is the upper bound
            BigInteger value2 = Utils.toPrecision(bigIntArray2[i], 10);

            DecimalData.convertBigIntegerToPackedDecimal(value1, Array1[i], 0, precision, false);
            DecimalData.convertBigIntegerToPackedDecimal(value2, Array2[i], 0, precision, false);

            comparisionReferenceArray[i] = ((Integer) (method.invoke(value1, value2))).intValue();
        }
    }

    public void setUpResultSpace(int numBits, int sampleSize)
    {
        resultArray = new byte[sampleSize][numBits];
        referenceArray = new byte[sampleSize][numBits];

        for (int i = 0; i < sampleSize; ++i)
        {
            Arrays.fill(resultArray[i], (byte) 0x00);
            Arrays.fill(referenceArray[i], (byte) 0x00);
        }
    }

    public void generateValues(int numBits, int sampleSize)
    {

        Array1 = new byte[sampleSize][33];
        Array2 = new byte[sampleSize][33];
        bigIntArray1 = new BigInteger[sampleSize];
        bigIntArray2 = new BigInteger[sampleSize];

        for (int i = 0; i < sampleSize; ++i)
        {
            BigInteger temp;

            while ((temp = new BigInteger(numBits, randomGenerator)).equals(BigInteger.ZERO))
                ;
            if (randomGenerator.nextBoolean())
                temp = temp.negate();
            bigIntArray1[i] = temp;

            while ((temp = new BigInteger(numBits, randomGenerator)).equals(BigInteger.ZERO))
                ;
            if (randomGenerator.nextBoolean())
                temp = temp.negate();
            bigIntArray2[i] = temp;
        }
    }

    public void getReferenceValues(int precision, int sampleSize, String methodName) throws Exception
    {
        resultArray = new byte[sampleSize][20];
        referenceArray = new byte[sampleSize][20];

        Method method = null;
        Class<?> c = Class.forName("java.math.BigInteger");
        method = c.getDeclaredMethod(methodName, BigInteger.class);
        for (int i = 0; i < sampleSize; ++i)
        {
            BigInteger value1 = Utils.toPrecision(bigIntArray1[i], 10); // Precision 10 is the upper bound
            BigInteger value2 = Utils.toPrecision(bigIntArray2[i], 10);

            DecimalData.convertBigIntegerToPackedDecimal(value1, Array1[i], 0, precision, false);
            DecimalData.convertBigIntegerToPackedDecimal(value2, Array2[i], 0, precision, false);

            BigInteger resultValue = (BigInteger) method.invoke(value1, value2);
            DecimalData.convertBigIntegerToPackedDecimal(resultValue, referenceArray[i], 0, 20, false);
        }
    }

    private void printElapsedTime(long start, long end)
    {
    }

    public void doAddTest(int sampleSize, Method method)
    {
        final int precision = 10;
        setUpResultSpace(sampleSize * 2, sampleSize);
        // just do the arithmetic computation here.
        for (int i = 0; i < sampleSize; ++i)
        {
            PackedDecimal.addPackedDecimal(resultArray[i], 0, precision, Array1[i], 0, 10, Array2[i], 0, 10, false);
        }
    }

    public void verify(int sampleSize, String testName)
    {
        for (int i = 0; i < sampleSize; ++i)
        {
            // Trace.showBytes("ref", referenceArray[i], 0, 15 ) ;
            // Trace.showBytes("res", resultArray[i], 0, 15 ) ;
            if (!arrayEquals(referenceArray[i], resultArray[i]))
                assertArrayEquals(testName, referenceArray[i], resultArray[i]);

        }
    }

    public boolean arrayEquals(byte[] a1, byte[] a2)
    {
        // do not deal with different length.
        if (a1.length != a2.length)
            return false;

        boolean isZero = true;
        for (int i = 0; i < a1.length; ++i)
        {
            if (a1[i] != a2[i])
            {
                // test first 4 bits
                byte a = (byte) (a1[i] & 0xF0);
                byte b = (byte) (a2[i] & 0xF0);

                if (a != b)
                    return false;
                if ((int) a != 0)
                    isZero = false;

                // test second 4 bits, this might be sign value.
                a = (byte) (a1[i] & 0x0F);
                b = (byte) (a2[i] & 0x0F);
                if (a != b)
                {
                    // check if they are the same sign.
                    // first, check to see they are actually not a value.
                    if ((int) a <= 9 || (int) b <= 9)
                        return false;
                    // second, check if they are both negative
                    boolean aIsNeg = a == 0x0B || a == 0x0D;
                    boolean bIsNeg = b == 0x0B || b == 0x0D;
                    if (aIsNeg != bIsNeg) // signs are different.
                    { // if their signs are different:
                        if (isZero)
                            return true;
                        else
                            return false;
                    }
                }
            } else
            {
                if ((int) a1[i] != 0)
                    isZero = false;
            }
        }

        return true;
    }

    public static void main(String[] args)
    {
        Result result = JUnitCore.runClasses(TestPerformance.class);
        for (Failure failure : result.getFailures())
            System.out.println(failure.toString());
    }

}
