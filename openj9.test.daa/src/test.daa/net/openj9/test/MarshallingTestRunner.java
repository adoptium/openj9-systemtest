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

import net.openj9.test.BasicTestRunner;
import net.openj9.test.binaryData.LongIntegerComparison;
import net.openj9.test.binaryData.TestBinaryConverters;
import net.openj9.test.binaryData.TestByteArray2Double;
import net.openj9.test.binaryData.TestByteArray2Float;
import net.openj9.test.binaryData.TestByteArray2Integer;
import net.openj9.test.binaryData.TestByteArray2IntegerNumBytes;
import net.openj9.test.binaryData.TestByteArray2Long;
import net.openj9.test.binaryData.TestByteArray2LongNumBytes;
import net.openj9.test.binaryData.TestByteArray2Short;
import net.openj9.test.binaryData.TestByteArray2ShortNumBytes;
import net.openj9.test.binaryData.TestDouble2ByteArray;
import net.openj9.test.binaryData.TestFloat2ByteArray;
import net.openj9.test.binaryData.TestInteger2ByteArray;
import net.openj9.test.binaryData.TestInteger2ByteArrayNumBytes;
import net.openj9.test.binaryData.TestLong2ByteArray;
import net.openj9.test.binaryData.TestLong2ByteArrayNumBytes;
import net.openj9.test.binaryData.TestOptimizer;
import net.openj9.test.binaryData.TestShort2ByteArray;
import net.openj9.test.binaryData.TestShort2ByteArrayNumBytes;

public class MarshallingTestRunner
{
    public static boolean runTests(int x)
    {
        boolean seenFailure = false;
        
        long startTime = System.nanoTime();

        for (int i = 0; i < x; ++i)
        {
            seenFailure |= !(BasicTestRunner.runClass(LongIntegerComparison.class));
            seenFailure |= !(BasicTestRunner.runClass(TestBinaryConverters.class));
            seenFailure |= !(BasicTestRunner.runClass(TestByteArray2Double.class));
            seenFailure |= !(BasicTestRunner.runClass(TestByteArray2Float.class));
            seenFailure |= !(BasicTestRunner.runClass(TestByteArray2Integer.class));
            seenFailure |= !(BasicTestRunner.runClass(TestByteArray2IntegerNumBytes.class));
            seenFailure |= !(BasicTestRunner.runClass(TestByteArray2Long.class));
            seenFailure |= !(BasicTestRunner.runClass(TestByteArray2LongNumBytes.class));
            seenFailure |= !(BasicTestRunner.runClass(TestByteArray2Short.class));
            seenFailure |= !(BasicTestRunner.runClass(TestByteArray2ShortNumBytes.class));
            seenFailure |= !(BasicTestRunner.runClass(TestDouble2ByteArray.class));
            seenFailure |= !(BasicTestRunner.runClass(TestFloat2ByteArray.class));
            seenFailure |= !(BasicTestRunner.runClass(TestInteger2ByteArray.class));
            seenFailure |= !(BasicTestRunner.runClass(TestInteger2ByteArrayNumBytes.class));
            seenFailure |= !(BasicTestRunner.runClass(TestLong2ByteArray.class));
            seenFailure |= !(BasicTestRunner.runClass(TestLong2ByteArrayNumBytes.class));
            seenFailure |= !(BasicTestRunner.runClass(TestOptimizer.class));
            seenFailure |= !(BasicTestRunner.runClass(TestShort2ByteArray.class));
            seenFailure |= !(BasicTestRunner.runClass(TestShort2ByteArrayNumBytes.class));
        }
        
        long endTime = System.nanoTime();
        
        System.out.printf("MarshallingTestRunner: Time taken for tests:%d \n", endTime - startTime);
        
        return seenFailure;
    }

    public static void invokeTest() {
        int x = 10;

        boolean seenFailure = runTests(x);

        if (seenFailure)
            System.exit(1);
    }
}
