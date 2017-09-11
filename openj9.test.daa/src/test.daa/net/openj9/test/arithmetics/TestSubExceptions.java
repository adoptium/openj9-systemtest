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

import static org.junit.Assert.fail;
import        org.junit.Test;

import com.ibm.dataaccess.PackedDecimal;

public class TestSubExceptions extends TestArithmeticComparisonBase
{
    @Test
    public void testSubExceptions()
    {
    }

    public void testWrongOp1(int offset1, int precision1, int offset2, int precision2)
    {
        byte[] resultant = new byte[101];

        for (int i = 0; i < precision1 - 1; ++i)
        {
            // i is the number of wrong Pos;
            byte[] op1 = getWrongFormatPDHelper(offset1, precision1, i, false, false);
            fillRandomPD(op2, offset2, precision2);

            testSubWrongFormatPackedDecimalHelper(resultant, op1, offset1, precision1, op2, offset2, precision2);
        }
    }

    private static void testSubWrongFormatPackedDecimalHelper(byte[] resultant, byte[] op1, int offset1, int precision1, byte[] op2, int offset2, int precision2)
    {
        boolean catched = false;

        try
        {
            PackedDecimal.subtractPackedDecimal(resultant, 0, precision1 >= precision2 ? precision1 + 1 : precision2 + 1, op1, offset1, precision1, op2, offset2, precision2, true);
        }
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail();

        setNoiseAtFirst4Bits(op1, offset1, precision1);

        catched = false;
        
        try
        { 
            PackedDecimal.subtractPackedDecimal(resultant, 0, precision1 >= precision2 ? precision1 + 1 : precision2 + 1, op1, offset1, precision1, op2, offset2, precision2, true);
        }
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail();

        // recover First4Bits Noise
        resetFirst4Bits(op1, offset1, precision1);
        addSignNoise(op1, offset1, precision1);
        
        catched = false;

        try
        {
            PackedDecimal.subtractPackedDecimal(op1, 0, precision1 >= precision2 ? precision1 + 1 : precision2 + 1, op1, offset1, precision1, op2, offset2, precision2, true);
        }
        
        catch (ArithmeticException e)
        {
            catched = true;
        }
        
        if (!catched)
            fail();
    }

    public void testWrongOp2(int offset1, int precision1, int offset2, int precision2)
    {
        byte[] resultant = new byte[101];

        for (int i = 0; i < precision2 - 1; ++i)
        {
            // i is the number of wrong Pos;
            fillRandomPD(op1, offset1, precision1);
            byte[] op2 = getWrongFormatPDHelper(offset2, precision2, i, false, false);

            testSubWrongFormatPackedDecimalHelper(resultant, op1, offset1, precision1, op2, offset2, precision2);
        }
    }

    public static void testOverflow()
    {
    }
}
