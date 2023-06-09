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

package net.openj9.test.decimals;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.junit.Test;

import com.ibm.dataaccess.DecimalData;

import net.openj9.test.Utils;

import org.junit.Assert;

public class TestED2PD extends DecimalTestBase
{
    static final int ARRAY_SIZE = 100;

    byte[] pdValue = new byte[ARRAY_SIZE];
    byte[] edValue = new byte[ARRAY_SIZE];

    static final byte separatePositiveSign = 0x4E;
    static final byte separateNegativeSign = 0x60;

    static final byte embeddedPositiveComponent = (byte) 0xC0;
    static final byte embeddedNegativeComponent = (byte) 0xD0;

    @Test
    public void testAlterSignsEmbeddedLeading()
    {
        byte[] ed = new byte[5];
        
        ed[0] = (byte) 0x01;
        ed[1] = (byte) 0xF2;

        try
        {
            DecimalData.convertExternalDecimalToInteger(ed, 0, 2, true, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);
        }

        catch (IllegalArgumentException iae)
        {
            fail();
        }
        
        ed[0] = (byte) 0x41;
        ed[1] = (byte) 0xF2;

        try
        {
            DecimalData.convertExternalDecimalToInteger(ed, 0, 2, true, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);
        }

        catch (IllegalArgumentException iae)
        {
            fail();
        }

        ed[0] = (byte) 0xA1;
        ed[1] = (byte) 0xF2;

        try
        {
            DecimalData.convertExternalDecimalToInteger(ed, 0, 2, true, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);
        }

        catch (IllegalArgumentException iae)
        {
            fail();
        }

        ed[0] = (byte) 0xB1;
        ed[1] = (byte) 0xF2;

        try
        {
            DecimalData.convertExternalDecimalToInteger(ed, 0, 2, true, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);
        }

        catch (IllegalArgumentException iae)
        {
            fail();
        }

        ed[0] = (byte) 0xC1;
        ed[1] = (byte) 0xF2;

        try
        {
            DecimalData.convertExternalDecimalToInteger(ed, 0, 2, true, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);
        }

        catch (IllegalArgumentException iae)
        {
            fail();
        }

        // D
        ed[0] = (byte) 0xD1;
        ed[1] = (byte) 0xF2;

        try
        {
            DecimalData.convertExternalDecimalToInteger(ed, 0, 2, true, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);
        }

        catch (IllegalArgumentException iae)
        {
            fail();
        }

        // E
        ed[0] = (byte) 0xE1;
        ed[1] = (byte) 0xF2;

        try
        {
            DecimalData.convertExternalDecimalToInteger(ed, 0, 2, true, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);
        }

        catch (IllegalArgumentException iae)
        {
            fail();
        }

        // F
        ed[0] = (byte) 0xF1;
        ed[1] = (byte) 0xF2;

        try
        {
            DecimalData.convertExternalDecimalToInteger(ed, 0, 2, true, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);
        }

        catch (IllegalArgumentException iae)
        {
            fail();
        }
    }

    @Test
    public void testValidSignsEmbeddedLeading()
    {
        byte[] ed = new byte[5];

        ed[0] = (byte) 0x01;
        ed[1] = (byte) 0xF2;

        int result;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 0, 2, true, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);
        
        Assert.assertEquals(result, 12);
        
        ed[0] = (byte) 0xC1;
        ed[1] = (byte) 0xF2;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 0, 2, true, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);
        
        Assert.assertEquals(result, 12);
        
        ed[0] = (byte) 0xD1;
        ed[1] = (byte) 0xF2;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 0, 2, true, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);
        
        Assert.assertEquals(result, -12);
        
        ed[0] = (byte) 0xB1;
        ed[1] = (byte) 0xF2;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 0, 2, true, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);
        
        Assert.assertEquals(result, -12);
    }
    
    @Test
    public void testValidSignsEmbeddedLeadingOffset()
    {
        byte[] ed = new byte[5];

        ed[2] = (byte) 0x41;
        ed[3] = (byte) 0xF2;

        int result;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 2, 2, true, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);
        
        Assert.assertEquals(result, 12);
        
        ed[2] = (byte) 0xA1;
        ed[3] = (byte) 0xF2;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 2, 2, true, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);
        
        Assert.assertEquals(result, 12);
        
        ed[2] = (byte) 0xB1;
        ed[3] = (byte) 0xF2;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 2, 2, true, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);
        
        Assert.assertEquals(result, -12);
        
        ed[2] = (byte) 0xD1;
        ed[3] = (byte) 0xF2;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 2, 2, true, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);
        
        Assert.assertEquals(result, -12);
    }
    
    @Test
    public void testValidSignsEmbeddedLeadingOffsetSize6() throws UnsupportedEncodingException
    {
        int pdOffset = 8;
        int edOffset = 1;
        
        int precision = 6;
        
        int type = DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING;
        
        byte[] pdValue = new byte[pdOffset + precision / 2 + 1];
        byte[] edValue = constructRandomED(edOffset, precision, type);

        DecimalData.convertExternalDecimalToPackedDecimal(edValue, edOffset, pdValue, pdOffset, precision, type);

        verify(pdValue, pdOffset, edValue, edOffset, precision, type);
    }
    
    @Test
    public void testValidSignsEmbeddedLeadingOffsetSize17() throws UnsupportedEncodingException
    {
        int pdOffset = 7;
        int edOffset = 15;
        
        int precision = 17;
        
        int type = DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING;
        
        byte[] pdValue = new byte[pdOffset + precision / 2 + 1];
        byte[] edValue = constructRandomED(edOffset, precision, type);

        DecimalData.convertExternalDecimalToPackedDecimal(edValue, edOffset, pdValue, pdOffset, precision, type);

        verify(pdValue, pdOffset, edValue, edOffset, precision, type);
    }
    
    @Test
    public void testValidSignsEmbeddedLeadingOffsetSize31() throws UnsupportedEncodingException
    {
        int pdOffset = 4;
        int edOffset = 6;
        
        int precision = 31;
        
        int type = DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING;
        
        byte[] pdValue = new byte[pdOffset + precision / 2 + 1];
        byte[] edValue = constructRandomED(edOffset, precision, type);

        DecimalData.convertExternalDecimalToPackedDecimal(edValue, edOffset, pdValue, pdOffset, precision, type);

        verify(pdValue, pdOffset, edValue, edOffset, precision, type);
    }

    @Test
    public void testValidSignsSeparateLeading()
    {
        byte[] ed = new byte[5];

        ed[0] = (byte) 0xFF;
        ed[1] = (byte) 0xF1;
        ed[2] = (byte) 0xF2;

        int result;

        result = DecimalData.convertExternalDecimalToInteger(ed, 0, 2, true, DecimalData.EBCDIC_SIGN_SEPARATE_LEADING);
        
        Assert.assertEquals(result, 12);
        
        ed[0] = (byte) 0x00;
        ed[1] = (byte) 0xF1;
        ed[2] = (byte) 0xF2;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 0, 2, true, DecimalData.EBCDIC_SIGN_SEPARATE_LEADING);
        
        Assert.assertEquals(result, 12);
        
        ed[0] = (byte) 0x4E;
        ed[1] = (byte) 0xF1;
        ed[2] = (byte) 0xF2;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 0, 2, true, DecimalData.EBCDIC_SIGN_SEPARATE_LEADING);
        
        Assert.assertEquals(result, 12);
        
        ed[0] = (byte) 0x60;
        ed[1] = (byte) 0xF1;
        ed[2] = (byte) 0xF2;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 0, 2, true, DecimalData.EBCDIC_SIGN_SEPARATE_LEADING);
        
        Assert.assertEquals(result, -12);
    }
    
    @Test
    public void testValidSignsSeparateLeadingOffset()
    {
        byte[] ed = new byte[5];

        ed[1] = (byte) 0xFF;
        ed[2] = (byte) 0xF1;
        ed[3] = (byte) 0xF2;

        int result;

        result = DecimalData.convertExternalDecimalToInteger(ed, 1, 2, true, DecimalData.EBCDIC_SIGN_SEPARATE_LEADING);
        
        Assert.assertEquals(result, 12);
        
        ed[1] = (byte) 0x00;
        ed[2] = (byte) 0xF1;
        ed[3] = (byte) 0xF2;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 1, 2, true, DecimalData.EBCDIC_SIGN_SEPARATE_LEADING);
        
        Assert.assertEquals(result, 12);
        
        ed[1] = (byte) 0x4E;
        ed[2] = (byte) 0xF1;
        ed[3] = (byte) 0xF2;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 1, 2, true, DecimalData.EBCDIC_SIGN_SEPARATE_LEADING);
        
        Assert.assertEquals(result, 12);
        
        ed[1] = (byte) 0x60;
        ed[2] = (byte) 0xF1;
        ed[3] = (byte) 0xF2;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 1, 2, true, DecimalData.EBCDIC_SIGN_SEPARATE_LEADING);
        
        Assert.assertEquals(result, -12);
    }
    
    @Test
    public void testValidSignsSeparateLeadingOffsetSize6() throws UnsupportedEncodingException
    {
        int pdOffset = 8;
        int edOffset = 1;
        
        int precision = 6;
        
        int type = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
        
        byte[] pdValue = new byte[pdOffset + precision / 2 + 1];
        byte[] edValue = constructRandomED(edOffset, precision, type);

        DecimalData.convertExternalDecimalToPackedDecimal(edValue, edOffset, pdValue, pdOffset, precision, type);

        verify(pdValue, pdOffset, edValue, edOffset, precision, type);
    }
    
    @Test
    public void testValidSignsSeparateLeadingOffsetSize17() throws UnsupportedEncodingException
    {
        int pdOffset = 7;
        int edOffset = 15;
        
        int precision = 17;
        
        int type = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
        
        byte[] pdValue = new byte[pdOffset + precision / 2 + 1];
        byte[] edValue = constructRandomED(edOffset, precision, type);

        DecimalData.convertExternalDecimalToPackedDecimal(edValue, edOffset, pdValue, pdOffset, precision, type);

        verify(pdValue, pdOffset, edValue, edOffset, precision, type);
    }
    
    @Test
    public void testValidSignsSeparateLeadingOffsetSize31() throws UnsupportedEncodingException
    {
        int pdOffset = 4;
        int edOffset = 6;
        
        int precision = 31;
        
        int type = DecimalData.EBCDIC_SIGN_SEPARATE_LEADING;
        
        byte[] pdValue = new byte[pdOffset + precision / 2 + 1];
        byte[] edValue = constructRandomED(edOffset, precision, type);

        DecimalData.convertExternalDecimalToPackedDecimal(edValue, edOffset, pdValue, pdOffset, precision, type);

        verify(pdValue, pdOffset, edValue, edOffset, precision, type);
    }

    @Test
    public void testValidSignsEmbeddedTrailing()
    {
        byte[] ed = new byte[5];

        ed[0] = (byte) 0xF1;
        ed[1] = (byte) 0x02;

        int result;

        result = DecimalData.convertExternalDecimalToInteger(ed, 0, 2, true, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);

        Assert.assertEquals(result, 12);
        
        ed[0] = (byte) 0xF1;
        ed[1] = (byte) 0xF2;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 0, 2, true, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);

        Assert.assertEquals(result, 12);
        
        ed[0] = (byte) 0xF1;
        ed[1] = (byte) 0xA2;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 0, 2, true, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);

        Assert.assertEquals(result, 12);
        
        ed[0] = (byte) 0xF1;
        ed[1] = (byte) 0xD2;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 0, 2, true, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);

        Assert.assertEquals(result, -12);
        
        ed[0] = (byte) 0xF1;
        ed[1] = (byte) 0xB2;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 0, 2, true, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);

        Assert.assertEquals(result, -12);
    }
    
    @Test
    public void testValidSignsEmbeddedTrailingOffset()
    {
        byte[] ed = new byte[5];

        ed[3] = (byte) 0xF1;
        ed[4] = (byte) 0x02;

        int result;

        result = DecimalData.convertExternalDecimalToInteger(ed, 3, 2, true, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);

        Assert.assertEquals(result, 12);
        
        ed[3] = (byte) 0xF1;
        ed[4] = (byte) 0xF2;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 3, 2, true, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);

        Assert.assertEquals(result, 12);
        
        ed[3] = (byte) 0xF1;
        ed[4] = (byte) 0xA2;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 3, 2, true, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);

        Assert.assertEquals(result, 12);
        
        ed[3] = (byte) 0xF1;
        ed[4] = (byte) 0xD2;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 3, 2, true, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);

        Assert.assertEquals(result, -12);
        
        ed[3] = (byte) 0xF1;
        ed[4] = (byte) 0xB2;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 3, 2, true, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);

        Assert.assertEquals(result, -12);
    }
    
    @Test
    public void testValidSignsEmbeddedTrailingOffsetSize6() throws UnsupportedEncodingException
    {
        int pdOffset = 8;
        int edOffset = 1;
        
        int precision = 6;
        
        int type = DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING;
        
        byte[] pdValue = new byte[pdOffset + precision / 2 + 1];
        byte[] edValue = constructRandomED(edOffset, precision, type);

        DecimalData.convertExternalDecimalToPackedDecimal(edValue, edOffset, pdValue, pdOffset, precision, type);

        verify(pdValue, pdOffset, edValue, edOffset, precision, type);
    }
    
    @Test
    public void testValidSignsEmbeddedTrailingOffsetSize17() throws UnsupportedEncodingException
    {
        int pdOffset = 7;
        int edOffset = 15;
        
        int precision = 17;
        
        int type = DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING;
        
        byte[] pdValue = new byte[pdOffset + precision / 2 + 1];
        byte[] edValue = constructRandomED(edOffset, precision, type);

        DecimalData.convertExternalDecimalToPackedDecimal(edValue, edOffset, pdValue, pdOffset, precision, type);

        verify(pdValue, pdOffset, edValue, edOffset, precision, type);
    }
    
    @Test
    public void testValidSignsEmbeddedTrailingOffsetSize31() throws UnsupportedEncodingException
    {
        int pdOffset = 4;
        int edOffset = 6;
        
        int precision = 31;
        
        int type = DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING;
        
        byte[] pdValue = new byte[pdOffset + precision / 2 + 1];
        byte[] edValue = constructRandomED(edOffset, precision, type);

        DecimalData.convertExternalDecimalToPackedDecimal(edValue, edOffset, pdValue, pdOffset, precision, type);

        verify(pdValue, pdOffset, edValue, edOffset, precision, type);
    }

    @Test
    public void testValidSignsSeparateTrailing()
    {
        byte[] ed = new byte[5];

        ed[0] = (byte) 0xF1;
        ed[1] = (byte) 0xF2;
        ed[2] = (byte) 0x4E;

        int result;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 0, 2, true, DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING);
        
        Assert.assertEquals(result, 12);
        
        ed[0] = (byte) 0xF1;
        ed[1] = (byte) 0xF2;
        ed[2] = (byte) 0x00;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 0, 2, true, DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING);
        
        Assert.assertEquals(result, 12);
        
        ed[0] = (byte) 0xF1;
        ed[1] = (byte) 0xF2;
        ed[2] = (byte) 0xFF;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 0, 2, true, DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING);
        
        Assert.assertEquals(result, 12);
        
        ed[0] = (byte) 0xF1;
        ed[1] = (byte) 0xF2;
        ed[2] = (byte) 0x60;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 0, 2, true, DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING);
        
        Assert.assertEquals(result, -12);
    }
    
    @Test
    public void testValidSignsSeparateTrailingOffset()
    {
        byte[] ed = new byte[5];

        ed[2] = (byte) 0xF1;
        ed[3] = (byte) 0xF2;
        ed[4] = (byte) 0x4E;

        int result;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 2, 2, true, DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING);
        
        Assert.assertEquals(result, 12);
        
        ed[2] = (byte) 0xF1;
        ed[3] = (byte) 0xF2;
        ed[4] = (byte) 0x00;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 2, 2, true, DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING);
        
        Assert.assertEquals(result, 12);
        
        ed[2] = (byte) 0xF1;
        ed[3] = (byte) 0xF2;
        ed[4] = (byte) 0xFF;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 2, 2, true, DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING);
        
        Assert.assertEquals(result, 12);
        
        ed[2] = (byte) 0xF1;
        ed[3] = (byte) 0xF2;
        ed[4] = (byte) 0x60;
        
        result = DecimalData.convertExternalDecimalToInteger(ed, 2, 2, true, DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING);
        
        Assert.assertEquals(result, -12);
    }
    
    @Test
    public void testValidSignsSeparateTrailingOffsetSize6() throws UnsupportedEncodingException
    {
        int pdOffset = 8;
        int edOffset = 1;
        
        int precision = 6;
        
        int type = DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING;
        
        byte[] pdValue = new byte[pdOffset + precision / 2 + 1];
        byte[] edValue = constructRandomED(edOffset, precision, type);

        DecimalData.convertExternalDecimalToPackedDecimal(edValue, edOffset, pdValue, pdOffset, precision, type);

        verify(pdValue, pdOffset, edValue, edOffset, precision, type);
    }
    
    @Test
    public void testValidSignsSeparateTrailingOffsetSize17() throws UnsupportedEncodingException
    {
        int pdOffset = 7;
        int edOffset = 15;
        
        int precision = 17;
        
        int type = DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING;
        
        byte[] pdValue = new byte[pdOffset + precision / 2 + 1];
        byte[] edValue = constructRandomED(edOffset, precision, type);

        DecimalData.convertExternalDecimalToPackedDecimal(edValue, edOffset, pdValue, pdOffset, precision, type);

        verify(pdValue, pdOffset, edValue, edOffset, precision, type);
    }
    
    @Test
    public void testValidSignsSeparateTrailingOffsetSize31() throws UnsupportedEncodingException
    {
        int pdOffset = 4;
        int edOffset = 6;
        
        int precision = 31;
        
        int type = DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING;
        
        byte[] pdValue = new byte[pdOffset + precision / 2 + 1];
        byte[] edValue = constructRandomED(edOffset, precision, type);

        DecimalData.convertExternalDecimalToPackedDecimal(edValue, edOffset, pdValue, pdOffset, precision, type);

        verify(pdValue, pdOffset, edValue, edOffset, precision, type);
    }
    
    @Test
    public void testIllegalArgument()
    {
        boolean caught = false;

        // Illegal precision
        try
        {
            DecimalData.convertExternalDecimalToPackedDecimal(edValue, 0, pdValue, 0, 0, edTypes[0]);
        }
        catch (IllegalArgumentException e)
        {
            caught = true;
        }

        if (!caught)
            fail("Did not catch illegal argument precision.");

        // Illegal decimal type (lower)
        caught = false;
        try
        {
            DecimalData.convertPackedDecimalToExternalDecimal(edValue, 0, pdValue, 0, 1, 0);
        }
        catch (IllegalArgumentException e)
        {
            caught = true;
        }

        if (!caught)
            fail("Did not catch illegal argument decimal type lower.");

        // Illegal decimal type (higher)
        caught = false;
        try
        {
            DecimalData.convertPackedDecimalToExternalDecimal(edValue, 0, pdValue, 0, 1, 12);
        }
        catch (IllegalArgumentException e)
        {
            caught = true;
        }

        if (!caught)
            fail("Did not catch illegal argument decimal type higher.");
    }


    @Test
    public void testZeros()
    {
        for (int offset = 0; offset < 2; ++offset)
        {
            Arrays.fill(pdValue, (byte) 0x00);
            Arrays.fill(edValue, (byte) 0);

            edValue[offset] = separatePositiveSign; // separate leading
            edValue[offset + 1] = (byte) 0xF0;
            DecimalData.convertExternalDecimalToPackedDecimal(edValue, offset, pdValue, offset, 1, DecimalData.EBCDIC_SIGN_SEPARATE_LEADING);
            verify(pdValue, offset, edValue, offset, 1, DecimalData.EBCDIC_SIGN_SEPARATE_LEADING);

            Arrays.fill(pdValue, (byte) 0);
            Arrays.fill(edValue, (byte) 0);
            edValue[offset] = separateNegativeSign; // separate leading
            edValue[offset + 1] = (byte) 0xF0;
            DecimalData.convertExternalDecimalToPackedDecimal(edValue, offset, pdValue, offset, 1, DecimalData.EBCDIC_SIGN_SEPARATE_LEADING);
            verify(pdValue, offset, edValue, offset, 1, DecimalData.EBCDIC_SIGN_SEPARATE_LEADING);

            // embedded leading/trailing
            Arrays.fill(pdValue, (byte) 0);
            Arrays.fill(edValue, (byte) 0);
            edValue[offset] |= embeddedPositiveComponent; // embedded
            DecimalData.convertExternalDecimalToPackedDecimal(edValue, offset, pdValue, offset, 1, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);
            verify(pdValue, offset, edValue, offset, 1, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);

            Arrays.fill(pdValue, (byte) 0);
            Arrays.fill(edValue, (byte) 0);
            edValue[offset] |= embeddedNegativeComponent; // embedded
            DecimalData.convertExternalDecimalToPackedDecimal(edValue, offset, pdValue, offset, 1, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);
            verify(pdValue, offset, edValue, offset, 1, DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING);

            Arrays.fill(pdValue, (byte) 0);
            Arrays.fill(edValue, (byte) 0);
            edValue[offset] |= embeddedPositiveComponent; // embedded
            DecimalData.convertExternalDecimalToPackedDecimal(edValue, offset, pdValue, offset, 1, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);
            verify(pdValue, offset, edValue, offset, 1, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);

            Arrays.fill(pdValue, (byte) 0);
            Arrays.fill(edValue, (byte) 0);
            edValue[offset] |= embeddedNegativeComponent; // embedded
            DecimalData.convertExternalDecimalToPackedDecimal(edValue, offset, pdValue, offset, 1, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);
            verify(pdValue, offset, edValue, offset, 1, DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING);

            // separate trailing
            Arrays.fill(pdValue, (byte) 0);
            Arrays.fill(edValue, (byte) 0);
            edValue[offset + 1] = separatePositiveSign;
            edValue[offset] = (byte) 0xF0;
            DecimalData.convertExternalDecimalToPackedDecimal(edValue, offset, pdValue, offset, 1, DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING);
            verify(pdValue, offset, edValue, offset, 1, DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING);

            Arrays.fill(pdValue, (byte) 0);
            Arrays.fill(edValue, (byte) 0);
            edValue[offset] = (byte) 0xF0;
            edValue[offset + 1] = separateNegativeSign;
            DecimalData.convertExternalDecimalToPackedDecimal(edValue, offset, pdValue, offset, 1, DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING);
            verify(pdValue, offset, edValue, offset, 1, DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING);

        }
    }

    @Test
    public void testRandomNumber()
    {
        try
        {
            for (int i = 0; i < 2; ++i)
            {
                for (int k = 5; k < 10; ++k)
                {
                    testRandomNumberHelper(i, i, k);
                }
            }

            for (int k = 20; k < 50; ++k)
            {
                testRandomNumberHelper(0, 0, k);// test for different precisions
            }
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            fail("exceptions thrown:" + e);
        }
    }

    public static void testRandomNumberHelper(int edOffset, int pdOffset, int precision) throws UnsupportedEncodingException
    {
        byte[] pdValue = new byte[pdOffset + precision / 2 + 1];
        for (int i = 0; i < edTypes.length; ++i)
        {
            byte[] edValue = constructRandomED(edOffset, precision, edTypes[i]);

            DecimalData.convertExternalDecimalToPackedDecimal(edValue, edOffset, pdValue, pdOffset, precision, edTypes[i]);

            verify(pdValue, pdOffset, edValue, edOffset, precision, edTypes[i]);
        }
    }

    public static boolean isValidEDDigit(byte value, boolean isEmbeddedSign)
    {
        byte digitComponent = (byte) (value & 0x0F);

        Assert.assertTrue(digitComponent < 0x0A || digitComponent >= 0x00);

        return true;
    }

    // it may be destructive
    public static void verify(byte[] pdValue, int pdOffset, byte[] edValue, int edOffset, int precision, int decimalType)
    {
        boolean isEDPositive = Utils.isEDPositiveSign(edValue, edOffset, precision, decimalType);
        boolean isPDPositive = Utils.isPDPositiveSign(pdValue, pdOffset, precision);

        org.junit.Assert.assertTrue(isPDPositive == isEDPositive);

        int start = edOffset;
        int end = edOffset + precision; // should be exclusive after the swich
                                        // below
        int digitStart = start;
        int digitEnd = end;

        switch (decimalType)
        {
            case DecimalData.EBCDIC_SIGN_EMBEDDED_LEADING:
            {
                // check first digit
                isValidEDDigit(edValue[start], true);

                // destructive test ...
                edValue[start] = (byte) ((edValue[start] & 0x0F) | 0xF0);

                break;
            }
            case DecimalData.EBCDIC_SIGN_SEPARATE_LEADING:
            {
                Assert.assertTrue((edValue[start] == separatePositiveSign) || (edValue[start] == separateNegativeSign));
                start++;
                digitStart++;
                digitEnd++;
                break;
            }
            case DecimalData.EBCDIC_SIGN_EMBEDDED_TRAILING:
            {
                isValidEDDigit(edValue[end - 1], true);
                edValue[end - 1] = (byte) ((edValue[end - 1] & 0x0F) | 0xF0);
                end--;
                break;
            }
            case DecimalData.EBCDIC_SIGN_SEPARATE_TRAILING:
            {
                Assert.assertTrue((edValue[end] == separatePositiveSign) || (edValue[end] == separateNegativeSign));
                break;
            }
        }

        // start and end are used for significant 4bits checking
        for (int i = start; i < end; ++i)
        {
            Assert.assertTrue((edValue[i] & 0xF0) == 0xF0);
        }

        // make sure each digit matches, from left to right
        int pdPos = pdOffset;
        boolean higher4Bits = precision % 2 != 0;
        for (int i = digitStart; i < digitEnd; ++i)
        {
            int ed = (int) (edValue[i] & 0x0F);
            if (higher4Bits)
            {
                int pd = (pdValue[pdPos] >> 4) & 0X0F;
                Assert.assertEquals(pd, ed);
                higher4Bits = false;
            } else
            {
                int pd = pdValue[pdPos] & 0x0F;
                Assert.assertEquals(pd, ed);
                higher4Bits = true;
                pdPos++;
            }
        }
    }
}
