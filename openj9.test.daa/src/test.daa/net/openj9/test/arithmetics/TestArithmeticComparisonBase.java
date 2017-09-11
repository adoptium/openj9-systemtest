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

import java.math.BigInteger;

import java.util.Arrays;
import java.util.Random;

import net.openj9.test.Utils;

public class TestArithmeticComparisonBase
{
    byte[] op1 = new byte[100];
    byte[] op2 = new byte[100];

    static int sampleSize = 20;

    static int SmallPositiveMax = 10000;
    static int SmallNegativesMax = 10000;
    
    static long LargePositivesMin = Long.MAX_VALUE;
    static long LargeNegativesMin = Long.MAX_VALUE;

    static Random randomGenerator = new Random(System.currentTimeMillis());

    /**
     * clear the 4 most significant bits of a packed decimal
     */
    public static void resetFirst4Bits(byte[] pd, int offset, int precision)
    {
        if (precision % 2 != 0)
            return;

        pd[offset] &= 0x0F;
    }

    /**
     * helper methods that fills the input byte array with a random packed
     * decimal, which has designated precision and offset.
     */
    public static void fillRandomPD(byte[] rv, int offset, int precision)
    {
        BigInteger bi = Utils.getBigInteger(precision, randomGenerator.nextBoolean());
        Utils.setNumber(rv, offset, precision, bi);
        // System.arraycopy(value, 0, rv, offset, precision/2 + 1);
    }

    /**
     * serve a pdByte array, with offset and precision, and the position of
     * wrong digit, it will randomly insert an invalid digit to the position
     */
    public static void addNoiseAt(byte[] pdByte, int offset, int precision, int wrongPos)
    {
        if (wrongPos < 0 || wrongPos >= precision)
            return; // simply don't do it.

        int randomNum = randomGenerator.nextInt(6) + 10;// guarantee that it is
                                                        // higher than 10
        if (precision % 2 == 0)
            wrongPos++;
        int position = offset + wrongPos / 2;

        if (wrongPos % 2 != 0)
        {
            byte randomByte = (byte) (((pdByte[position] & (byte) 0xF0) << 4) | randomNum);
            pdByte[position] = randomByte;
        } else
        {
            byte randomByte = (byte) ((randomNum << 4) | (pdByte[position] & 0x0F));
            pdByte[position] = randomByte;
        }
    }

    /**
     * change the sign to any value other than sign values.
     */
    public static void addSignNoise(byte[] pd, int offset, int precision)
    {
        int position = offset + (precision) / 2;

        int noise = randomGenerator.nextInt(10);

        pd[position] &= 0xF0;
        pd[position] |= noise;
    }

    /**
     * used on packed decimal types, tries to ruin the 4 most significant bits
     */
    public static void setNoiseAtFirst4Bits(byte[] pd, int offset, int precision)
    {
        if (precision % 2 != 0)
            return; // can't do anything.

        int noise = randomGenerator.nextInt(15) + 1;

        pd[offset] &= 0x0F;
        pd[offset] |= noise << 4;
    }

    /**
     * given a packed decimal, and given the number of invalid digits you want,
     * and the option to ruin most significant bits(for even precision) and ruin
     * sign or not, it will
     */
    public static void getWrongFormatPDHelper2(byte[] pd, int offset, int precision, int numNoises, boolean ruinHigher4Bits, boolean ruinSign)
    {
        if (numNoises > precision)
            numNoises = precision;

        BigInteger bi = Utils.getBigInteger(precision);
        Utils.setNumber(pd, offset, precision, bi);

        boolean[] pits = new boolean[precision];
        Arrays.fill(pits, false);

        for (int i = 0; i < numNoises; ++i)
        {
            int position;
            while (pits[position = randomGenerator.nextInt(precision)] != false)
                ;
            pits[position] = true;
            addNoiseAt(pd, offset, precision, position);
        }

        if (ruinHigher4Bits)
        {
            setNoiseAtFirst4Bits(pd, offset, precision);
        }

        if (ruinSign)
        {
            addSignNoise(pd, offset, precision);
        }
    }

    public static byte[] getWrongFormatPDHelper(int offset, int precision, int numNoises, boolean ruinHigher4Bits, boolean ruinSign)
    {
        if (numNoises > precision)
            numNoises = precision;

        BigInteger bi = Utils.getBigInteger(precision);
        byte[] rv = Utils.getRandomInput(offset, precision, bi);

        boolean[] pits = new boolean[precision];
        Arrays.fill(pits, false);

        for (int i = 0; i < numNoises; ++i)
        {
            int position;
            while (pits[position = randomGenerator.nextInt(precision)] != false)
            {
                pits[position] = true;
                addNoiseAt(rv, offset, precision, position);
            }
        }

        if (ruinHigher4Bits)
        {
            setNoiseAtFirst4Bits(rv, offset, precision);
        }

        if (ruinSign)
        {
            addSignNoise(rv, offset, precision);
        }

        return rv;
    }
}
