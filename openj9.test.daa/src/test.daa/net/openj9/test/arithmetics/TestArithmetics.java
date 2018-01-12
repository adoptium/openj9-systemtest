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

package net.openj9.test.arithmetics;

import static org.junit.Assert.*;

import java.math.BigInteger;

import com.ibm.dataaccess.DecimalData;
import com.ibm.dataaccess.PackedDecimal;

import net.openj9.test.Utils;

import org.junit.Test;

import java.util.Random;

public class TestArithmetics
{
	static Random randomGenerator = new Random(System.currentTimeMillis());

    @Test
    public void randomTest()
    {
        for (int i = 0; i < 10; ++i) // offset
        {
            for (int j = 1; j < 16; ++j) // precision
            {
                testWithOffsetAndPrec(i, j + 1, i, j);
            }
        }
    }

    private BigInteger getRandomBigInteger(int prec)
    {
        // Construct a string with digits.
        StringBuilder sb = new StringBuilder();
        
        if (randomGenerator.nextBoolean())
        {
            sb.append("-");
        }
        
        for (int i = 0; i < prec; ++i)
        {
            int rndDig = randomGenerator.nextInt(10);
            sb.append(rndDig);
        }
        
        BigInteger bi = new BigInteger(sb.toString());

        return bi;
    }

    private void testWithOffsetAndPrec(int resoffset, int resprec, int opoffset, int opprec)
    {
        BigInteger value1 = getRandomBigInteger(opprec);
        BigInteger value2 = getRandomBigInteger(opprec);
        
        byte[] input1 = getRandomInput(opoffset, opprec, value1);
        byte[] input2 = getRandomInput(opoffset, opprec, value2);

        byte[] reference = new byte[resoffset + resprec / 2 + 1];
        byte[] resultant = new byte[resoffset + resprec / 2 + 1];
        byte[] resultant2 = new byte[resoffset + resprec / 2 + 1];

        BigInteger resultBI = value1.add(value2);
        
        DecimalData.convertBigIntegerToPackedDecimal(resultBI, reference, resoffset, resprec, true);
        
        PackedDecimal.addPackedDecimal(resultant, resoffset, resprec, input1, opoffset, opprec, input2, opoffset, opprec, false);
        
        try 
        {
            assertArrayEquals(reference, resultant);
        }
        
        catch (AssertionError e)
        {
            assertArrayEquals(String.format("Reference: %s, Result: %s \n", Utils.byteArrayToString(reference), Utils.byteArrayToString(resultant2)), reference, resultant);
        }
        
        PackedDecimal.addPackedDecimal(resultant2, resoffset, resprec, input1, opoffset, opprec, input2, opoffset, opprec, true);
        
        try 
        {
            assertArrayEquals(reference, resultant2);
        }
        
        catch (AssertionError e)
        {
            assertArrayEquals(String.format("Reference: %s, Result: %s \n", Utils.byteArrayToString(reference), Utils.byteArrayToString(resultant2)), reference, resultant2);
        }
    }

    private byte[] getRandomInput(int offset, int prec, BigInteger bi)
    {
        int size = prec / 2 + 1;
        size += offset;
        byte[] rv = new byte[size];

        DecimalData.convertBigIntegerToPackedDecimal(bi, rv, offset, prec, true);

        return rv;
    }

}
