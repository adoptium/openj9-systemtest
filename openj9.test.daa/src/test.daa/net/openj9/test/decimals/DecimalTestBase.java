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

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Random;

import net.openj9.test.Utils;

public class DecimalTestBase
{
    static char udPositiveSign = '+';
    static char udNegativeSign = '-';
    
    static Random randomGenerator = new Random(System.currentTimeMillis());
    
    static int[] types = new int[] { com.ibm.dataaccess.DecimalData.UNICODE_SIGN_SEPARATE_LEADING, com.ibm.dataaccess.DecimalData.UNICODE_SIGN_SEPARATE_TRAILING, com.ibm.dataaccess.DecimalData.UNICODE_UNSIGNED };
    static int[] edTypes = new int[] { com.ibm.dataaccess.DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING, com.ibm.dataaccess.DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING, com.ibm.dataaccess.DecimalData.EBCDIC_SIGN_SEPARATE_LEADING, com.ibm.dataaccess.DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING };

    // only deals with positive value
    public static void StringToEBCDIC(byte[] pd, int pdOffset, String str)
    {
        char[] ch = str.toCharArray();
        int pos = pdOffset;

        for (int i = 0; i < ch.length; ++i)
        {
            switch (ch[i])
            {
                case '0':
                    pd[pos] = (byte) 0xF0;
                    break;
                case '1':
                    pd[pos] = (byte) 0xF1;
                    break;
                case '2':
                    pd[pos] = (byte) 0xF2;
                    break;
                case '3':
                    pd[pos] = (byte) 0xF3;
                    break;
                case '4':
                    pd[pos] = (byte) 0xF4;
                    break;
                case '5':
                    pd[pos] = (byte) 0xF5;
                    break;
                case '6':
                    pd[pos] = (byte) 0xF6;
                    break;
                case '7':
                    pd[pos] = (byte) 0xF7;
                    break;
                case '8':
                    pd[pos] = (byte) 0xF8;
                    break;
                case '9':
                    pd[pos] = (byte) 0xF9;
                    break;
            }
            pos++;
        }
    }

    public static byte[] constructRandomED(int offset, int precision, int decimalType) throws UnsupportedEncodingException
    {
        return constructED(offset, precision, Utils.getRandomPositiveBigInteger(precision), decimalType);
    }

    public static byte[] constructED(int offset, int precision, BigInteger bi, int decimalType) throws UnsupportedEncodingException
    {
        byte[] rv;

        byte posSign = (byte) (randomGenerator.nextInt() & 0xF0);
        byte negSign = (byte) 0xD0;

        int length = offset + precision;
        
        if (decimalType == com.ibm.dataaccess.DecimalData.EBCDIC_SIGN_SEPARATE_LEADING || decimalType == com.ibm.dataaccess.DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING)
            length += 1;

        rv = new byte[length];

        boolean isPositive = randomGenerator.nextBoolean();
        
        byte signVal = isPositive ? posSign : negSign;
        byte signVal2 = isPositive ? (byte) 0x4E : 0x60;
        
        switch (decimalType)
        {
            case com.ibm.dataaccess.DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING:
            {
                StringToEBCDIC(rv, offset, bi.toString());
                rv[offset] = (byte) ((rv[offset] & 0x0F) | signVal);
                break;
            }
            case com.ibm.dataaccess.DecimalData.EBCDIC_SIGN_SEPARATE_LEADING:
            {
                rv[offset] = signVal2;
                StringToEBCDIC(rv, offset + 1, bi.toString());
                break;
            }
            case com.ibm.dataaccess.DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING:
            {
                StringToEBCDIC(rv, offset, bi.toString());
                int pos = offset + precision - 1;
                rv[pos] = (byte) (rv[pos] & 0x0F | signVal);
                break;
            }
            case com.ibm.dataaccess.DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING:
            {
                StringToEBCDIC(rv, offset, bi.toString());
                rv[offset + precision] = signVal2;
                break;
            }
        }
        return rv;
    }

    public static char[] constructRandomUD(int offset, int precision, int decimalType)
    {
        return constructUD(offset, precision, Utils.getRandomPositiveBigInteger(precision), decimalType);
    }

    public static char[] constructUD(int offset, int precision, BigInteger bi, int decimalType)
    {
        char[] rv;
        int length = offset + precision;
        if (decimalType != com.ibm.dataaccess.DecimalData.UNICODE_UNSIGNED)
            length += 1;

        rv = new char[length];

        switch (decimalType)
        {
            case com.ibm.dataaccess.DecimalData.UNICODE_SIGN_SEPARATE_LEADING:
            {

                StringBuilder sb = new StringBuilder(length);

                // append zeros:
                for (int i = 0; i < offset; ++i)
                    sb.append("0");

                if (randomGenerator.nextBoolean())
                {
                    sb.append((char)randomGenerator.nextInt());
                } else
                {
                    sb.append('-');
                }

                sb.append(bi.toString());
                rv = sb.toString().toCharArray();

                break;
            }
            case com.ibm.dataaccess.DecimalData.UNICODE_SIGN_SEPARATE_TRAILING:
            {

                StringBuilder sb = new StringBuilder(length);

                // append zeros:
                for (int i = 0; i < offset; ++i)
                    sb.append("0");
                sb.append(bi.toString());

                if (randomGenerator.nextBoolean())
                {
                    sb.append((char)randomGenerator.nextInt());
                } else
                {
                    sb.append('-');
                }

                rv = sb.toString().toCharArray();
                break;
            }
            case com.ibm.dataaccess.DecimalData.UNICODE_UNSIGNED:
            {
                StringBuilder sb = new StringBuilder(length);
                for (int i = 0; i < offset; ++i)
                    sb.append("0");
                sb.append(bi.toString());
                rv = sb.toString().toCharArray();
                break;
            }

        }

        return rv;
    }

    // ud is a char array ... which means that each ud element takes up 2 bytes,
    // it is not the same as byte array.
    public static void verify(char[] ud, int udOffset, byte[] pd, int pdOffset, int precision, int decimalType)
    {
        // test decimalType

        switch (decimalType)
        {
            case com.ibm.dataaccess.DecimalData.UNICODE_SIGN_SEPARATE_LEADING:
            {
                // test if is valid PDSign
                if (!Utils.isValidPDSign(pd, pdOffset, precision))

                    if (Utils.isPDPositiveSign(pd, pdOffset, precision))
                        assertEquals(ud[udOffset], udPositiveSign);
                    else
                        assertEquals(ud[udOffset], udNegativeSign);

                matchPDUDDigits(ud, udOffset + 1, pd, pdOffset, precision);
                break;
            }
            case com.ibm.dataaccess.DecimalData.UNICODE_SIGN_SEPARATE_TRAILING:
            {
                if (!Utils.isValidPDSign(pd, pdOffset, precision))

                    if (Utils.isPDPositiveSign(pd, pdOffset, precision))
                        assertEquals(ud[udOffset + precision], udPositiveSign);
                    else
                        assertEquals(ud[udOffset + precision], udNegativeSign);

                matchPDUDDigits(ud, udOffset, pd, pdOffset, precision);
                break;
            }
            case com.ibm.dataaccess.DecimalData.UNICODE_UNSIGNED:
            {
                matchPDUDDigits(ud, udOffset, pd, pdOffset, precision);
                break;
            }
            default:
                throw new IllegalArgumentException("Wrong Decimal Type");
        }
    }

    private static void matchPDUDDigits(char[] ud, int udOffset, byte[] pd, int pdOffset, int precision)
    {

        boolean isHighHalfByte = true;
        if (precision % 2 == 0)
            isHighHalfByte = false;
        for (int i = 0; i < precision; ++i)
        {

            byte pdByte = Utils.getByteValueAt(pd, pdOffset, precision, i);
            char compareByte = 0;
            switch (pdByte)
            {
                case 0:
                    compareByte = 0x0030;
                    break;
                case 1:
                    compareByte = 0x0031;
                    break;
                case 2:
                    compareByte = 0x0032;
                    break;
                case 3:
                    compareByte = 0x0033;
                    break;
                case 4:
                    compareByte = 0x0034;
                    break;
                case 5:
                    compareByte = 0x0035;
                    break;
                case 6:
                    compareByte = 0x0036;
                    break;
                case 7:
                    compareByte = 0x0037;
                    break;
                case 8:
                    compareByte = 0x0038;
                    break;
                case 9:
                    compareByte = 0x0039;
                    break;

                default:
                    break;
            }
            assertEquals(ud[udOffset + i], compareByte);

            isHighHalfByte = !isHighHalfByte;
        }
    }

}
