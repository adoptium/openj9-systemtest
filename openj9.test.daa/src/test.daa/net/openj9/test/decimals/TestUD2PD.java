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

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.dataaccess.DecimalData;

public class TestUD2PD extends DecimalTestBase
{
    @Test
    public void testValidSignsLeading()
    {
        char[] udString = new char[100];
        byte[] pdString = new byte[100];

        byte[] pdReference = new byte[100];

        udString[0] = '*';
        udString[1] = '1';
        
        pdReference[0] = 0x1C;

        DecimalData.convertUnicodeDecimalToPackedDecimal(udString, 0, pdString, 0, 1, DecimalData.UNICODE_SIGN_SEPARATE_LEADING);

        Assert.assertArrayEquals(pdReference, pdString);
        
        udString[0] = (char) 0x1234;
        udString[1] = '1';
        
        pdReference[0] = 0x1C;

        DecimalData.convertUnicodeDecimalToPackedDecimal(udString, 0, pdString, 0, 1, DecimalData.UNICODE_SIGN_SEPARATE_LEADING);

        Assert.assertArrayEquals(pdReference, pdString);
        
        udString[0] = '+';
        udString[1] = '4';
        
        pdReference[0] = 0x4C;

        DecimalData.convertUnicodeDecimalToPackedDecimal(udString, 0, pdString, 0, 1, DecimalData.UNICODE_SIGN_SEPARATE_LEADING);

        Assert.assertArrayEquals(pdReference, pdString);
        
        udString[0] = '-';
        udString[1] = '7';
        
        pdReference[0] = 0x7D;

        DecimalData.convertUnicodeDecimalToPackedDecimal(udString, 0, pdString, 0, 1, DecimalData.UNICODE_SIGN_SEPARATE_LEADING);

        Assert.assertArrayEquals(pdReference, pdString);
    }
    
    @Test
    public void testValidSignsLeadingOffset()
    {
        char[] udString = new char[100];
        byte[] pdString = new byte[100];

        byte[] pdReference = new byte[100];

        udString[4] = '*';
        udString[5] = '1';
        
        pdReference[4] = 0x1C;

        DecimalData.convertUnicodeDecimalToPackedDecimal(udString, 4, pdString, 4, 1, DecimalData.UNICODE_SIGN_SEPARATE_LEADING);

        Assert.assertArrayEquals(pdReference, pdString);
        
        udString[4] = (char) 0x1234;
        udString[5] = '1';
        
        pdReference[5] = 0x1C;

        DecimalData.convertUnicodeDecimalToPackedDecimal(udString, 4, pdString, 5, 1, DecimalData.UNICODE_SIGN_SEPARATE_LEADING);

        Assert.assertArrayEquals(pdReference, pdString);
        
        udString[4] = '+';
        udString[5] = '4';
        
        pdReference[6] = 0x4C;

        DecimalData.convertUnicodeDecimalToPackedDecimal(udString, 4, pdString, 6, 1, DecimalData.UNICODE_SIGN_SEPARATE_LEADING);

        Assert.assertArrayEquals(pdReference, pdString);
        
        udString[4] = '-';
        udString[5] = '7';
        
        pdReference[7] = 0x7D;

        DecimalData.convertUnicodeDecimalToPackedDecimal(udString, 4, pdString, 7, 1, DecimalData.UNICODE_SIGN_SEPARATE_LEADING);

        Assert.assertArrayEquals(pdReference, pdString);
    }
    
    @Test
    public void testValidSignsLeadingOffsetSize6()
    {
        int pdOffset = 8;
        int udOffset = 1;
        
        int precision = 6;
        
        int type = DecimalData.UNICODE_SIGN_SEPARATE_LEADING;
        
        byte[] pdValue = new byte[200];
        char[] udValue = constructRandomUD(udOffset, precision, type);
        
        com.ibm.dataaccess.DecimalData.convertUnicodeDecimalToPackedDecimal(udValue, udOffset, pdValue, pdOffset, precision, type);
        verify(udValue, udOffset, pdValue, pdOffset, precision, type);
    }
    
    @Test
    public void testValidSignsLeadingOffsetSize17()
    {
        int pdOffset = 8;
        int udOffset = 1;

        int precision = 17;
        
        int type = DecimalData.UNICODE_SIGN_SEPARATE_LEADING;
        
        byte[] pdValue = new byte[200];
        char[] udValue = constructRandomUD(udOffset, precision, type);
        
        com.ibm.dataaccess.DecimalData.convertUnicodeDecimalToPackedDecimal(udValue, udOffset, pdValue, pdOffset, precision, type);
        verify(udValue, udOffset, pdValue, pdOffset, precision, type);
    }
    
    @Test
    public void testValidSignsLeadingOffsetSize31()
    {
        int pdOffset = 8;
        int udOffset = 1;
        
        int precision = 31;
        
        int type = DecimalData.UNICODE_SIGN_SEPARATE_LEADING;
        
        byte[] pdValue = new byte[200];
        char[] udValue = constructRandomUD(udOffset, precision, type);
        
        com.ibm.dataaccess.DecimalData.convertUnicodeDecimalToPackedDecimal(udValue, udOffset, pdValue, pdOffset, precision, type);
        verify(udValue, udOffset, pdValue, pdOffset, precision, type);
    }

    @Test
    public void testValidSignsTrailing()
    {
        char[] udString = new char[100];
        byte[] pdString = new byte[100];

        byte[] pdReference = new byte[100];

        udString[0] = '1';
        udString[1] = 'G';
        
        pdReference[0] = 0x1C;

        DecimalData.convertUnicodeDecimalToPackedDecimal(udString, 0, pdString, 0, 1, DecimalData.UNICODE_SIGN_SEPARATE_TRAILING);

        Assert.assertArrayEquals(pdReference, pdString);
        
        udString[0] = '1';
        udString[1] = (char) 0x9B5A;
        
        pdReference[0] = 0x1C;

        DecimalData.convertUnicodeDecimalToPackedDecimal(udString, 0, pdString, 0, 1, DecimalData.UNICODE_SIGN_SEPARATE_TRAILING);

        Assert.assertArrayEquals(pdReference, pdString);
        
        udString[0] = '5';
        udString[1] = '+';
        
        pdReference[0] = 0x5C;

        DecimalData.convertUnicodeDecimalToPackedDecimal(udString, 0, pdString, 0, 1, DecimalData.UNICODE_SIGN_SEPARATE_TRAILING);

        Assert.assertArrayEquals(pdReference, pdString);
        
        udString[0] = '9';
        udString[1] = '-';
        
        pdReference[0] = (byte)0x9D;

        DecimalData.convertUnicodeDecimalToPackedDecimal(udString, 0, pdString, 0, 1, DecimalData.UNICODE_SIGN_SEPARATE_TRAILING);

        Assert.assertArrayEquals(pdReference, pdString);
    }
    
    @Test
    public void testValidSignsTrailingOffset()
    {
        char[] udString = new char[100];
        byte[] pdString = new byte[100];

        byte[] pdReference = new byte[100];

        udString[8] = '1';
        udString[9] = 'G';
        
        pdReference[4] = 0x1C;

        DecimalData.convertUnicodeDecimalToPackedDecimal(udString, 8, pdString, 4, 1, DecimalData.UNICODE_SIGN_SEPARATE_TRAILING);

        Assert.assertArrayEquals(pdReference, pdString);
        
        udString[8] = '1';
        udString[9] = (char) 0x9B5A;
        
        pdReference[3] = 0x1C;

        DecimalData.convertUnicodeDecimalToPackedDecimal(udString, 8, pdString, 3, 1, DecimalData.UNICODE_SIGN_SEPARATE_TRAILING);

        Assert.assertArrayEquals(pdReference, pdString);
        
        udString[8] = '5';
        udString[9] = '+';
        
        pdReference[2] = 0x5C;
        
        DecimalData.convertUnicodeDecimalToPackedDecimal(udString, 8, pdString, 2, 1, DecimalData.UNICODE_SIGN_SEPARATE_TRAILING);

        Assert.assertArrayEquals(pdReference, pdString);
        
        udString[8] = '9';
        udString[9] = '-';
        
        pdReference[1] = (byte)0x9D;

        DecimalData.convertUnicodeDecimalToPackedDecimal(udString, 8, pdString, 1, 1, DecimalData.UNICODE_SIGN_SEPARATE_TRAILING);

        Assert.assertArrayEquals(pdReference, pdString);
    }
    
    @Test
    public void testValidSignsTrailingOffsetSize6()
    {
        int pdOffset = 8;
        int udOffset = 1;
        
        int precision = 6;
        
        int type = DecimalData.UNICODE_SIGN_SEPARATE_TRAILING;
        
        byte[] pdValue = new byte[200];
        char[] udValue = constructRandomUD(udOffset, precision, type);
        
        com.ibm.dataaccess.DecimalData.convertUnicodeDecimalToPackedDecimal(udValue, udOffset, pdValue, pdOffset, precision, type);
        verify(udValue, udOffset, pdValue, pdOffset, precision, type);
    }
    
    @Test
    public void testValidSignsTrailingOffsetSize17()
    {
        int pdOffset = 8;
        int udOffset = 1;

        int precision = 17;
        
        int type = DecimalData.UNICODE_SIGN_SEPARATE_TRAILING;
        
        byte[] pdValue = new byte[200];
        char[] udValue = constructRandomUD(udOffset, precision, type);
        
        com.ibm.dataaccess.DecimalData.convertUnicodeDecimalToPackedDecimal(udValue, udOffset, pdValue, pdOffset, precision, type);
        verify(udValue, udOffset, pdValue, pdOffset, precision, type);
    }
    
    @Test
    public void testValidSignsTrailingOffsetSize31()
    {
        int pdOffset = 8;
        int udOffset = 1;
        
        int precision = 31;
        
        int type = DecimalData.UNICODE_SIGN_SEPARATE_TRAILING;
        
        byte[] pdValue = new byte[200];
        char[] udValue = constructRandomUD(udOffset, precision, type);
        
        com.ibm.dataaccess.DecimalData.convertUnicodeDecimalToPackedDecimal(udValue, udOffset, pdValue, pdOffset, precision, type);
        verify(udValue, udOffset, pdValue, pdOffset, precision, type);
    }

    @Test
    public void testZeros()
    {
        for (int pdOffset = 0; pdOffset < 5; ++pdOffset)
            for (int udOffset = 0; udOffset < 5; ++udOffset)
            {
                char[] udString = new char[100];
                byte[] pdString = new byte[100];

                byte[] pdReference = new byte[100];

                // unsigned:
                udString[udOffset] = '0';
                com.ibm.dataaccess.DecimalData.convertUnicodeDecimalToPackedDecimal(udString, udOffset, pdString, pdOffset, 1, com.ibm.dataaccess.DecimalData.UNICODE_UNSIGNED);
                pdReference[pdOffset] = (byte) 0x0C;
                
                assertArrayEquals(pdString, pdReference);

                // trailing
                udString[udOffset] = '0';
                udString[udOffset + 1] = '-';
                com.ibm.dataaccess.DecimalData.convertUnicodeDecimalToPackedDecimal(udString, udOffset, pdString, pdOffset, 1, com.ibm.dataaccess.DecimalData.UNICODE_SIGN_SEPARATE_TRAILING);
                pdReference[pdOffset] = (byte) 0x0D;
                
                assertArrayEquals(pdString, pdReference);

                // leading
                udString[udOffset + 1] = '0';
                udString[udOffset] = '-';
                com.ibm.dataaccess.DecimalData.convertUnicodeDecimalToPackedDecimal(udString, udOffset, pdString, pdOffset, 1, com.ibm.dataaccess.DecimalData.UNICODE_SIGN_SEPARATE_LEADING);
                pdReference[pdOffset] = (byte) 0x0D;
                
                assertArrayEquals(pdString, pdReference);
            }
    }

    @Test
    public void illegalArgumentTests()
    {
        char[] udString = new char[100];
        byte[] pdString = new byte[100];

        byte[] pdReference = new byte[100];
        
        boolean caught = false;
        
        // Illegal precision
        try
        {
            DecimalData.convertUnicodeDecimalToPackedDecimal(udString, 0, pdString, 0, -1, 8);
        }
        
        catch (Exception e)
        {
            caught = true;
        }
        
        if (!caught)
            fail();

        caught = false;
        
        // Illegal decimal type (lower)
        try
        {
            DecimalData.convertUnicodeDecimalToPackedDecimal(udString, 0, pdString, 0, 1, 8);
        }
        
        catch (Exception e)
        {
            caught = true;
        }
        
        if (!caught)
            fail();

        caught = false;
        
        // Illegal decimal type (higher)
        try
        {
            DecimalData.convertUnicodeDecimalToPackedDecimal(udString, 0, pdString, 0, 1, 12);
        }
        
        catch (Exception e)
        {
            caught = true;
        }
        
        if (!caught)
            fail();

        caught = false;
        
        Arrays.fill(pdString, (byte) 0);
        Arrays.fill(udString, (char) 0x00);
        
        udString[0] = '1';
        udString[1] = '2';
        udString[2] = 'F';
        
        pdReference[0] = 0x01;
        pdReference[1] = 0x2C;
        
        DecimalData.convertUnicodeDecimalToPackedDecimal(udString, 0, pdString, 0, 2, DecimalData.UNICODE_SIGN_SEPARATE_TRAILING);
        
        Assert.assertArrayEquals(pdReference, pdString);
        
        Arrays.fill(pdString, (byte) 0);
        Arrays.fill(udString, (char) 0x00);
        
        udString[0] = '-';
        udString[1] = '1';
        udString[2] = '2';

        pdReference[0] = 0x01;
        pdReference[1] = 0x2D;
        
        DecimalData.convertUnicodeDecimalToPackedDecimal(udString, 0, pdString, 0, 2, DecimalData.UNICODE_SIGN_SEPARATE_LEADING);
        
        Assert.assertArrayEquals(pdReference, pdString);
    }

    @Test
    public void testRandomNumber()
    {

        for (int pdOffset = 0; pdOffset < 5; ++pdOffset)
            for (int udOffset = 0; udOffset < 5; ++udOffset)
                for (int precision = 2; precision < 9; ++precision)
                    testRandomNumberHelper(pdOffset, udOffset, precision);
    }

    public void testRandomNumberHelper(int pdOffset, int udOffset, int precision)
    {
        byte[] pdValue = new byte[200];

        for (int decimalType = 0; decimalType < types.length; ++decimalType)
        {
            Arrays.fill(pdValue, (byte) 0);
            
            char[] udValue = constructRandomUD(udOffset, precision, types[decimalType]);
            
            com.ibm.dataaccess.DecimalData.convertUnicodeDecimalToPackedDecimal(udValue, udOffset, pdValue, pdOffset, precision, types[decimalType]);
            verify(udValue, udOffset, pdValue, pdOffset, precision, types[decimalType]);

        }
    }

}
