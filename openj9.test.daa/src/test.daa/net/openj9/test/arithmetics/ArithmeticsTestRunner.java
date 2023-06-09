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

import net.openj9.test.BasicTestRunner;
import net.openj9.test.arithmetics.TestArithmeticInline;
import net.openj9.test.arithmetics.TestArithmetics;
import net.openj9.test.arithmetics.TestComparisonEquals;
import net.openj9.test.arithmetics.TestPerformance;
import net.openj9.test.arithmetics.TestSubExceptions;
import net.openj9.test.arithmetics.TestValidityChecking;

public class ArithmeticsTestRunner
{
    public static void main(String[] args)
    {
        int x = 10;
        if (args.length > 0)
        {
            x = new Integer(args[0]).intValue();
        }

        if (runTests(x))
            System.exit(1);
    }

    public static boolean runTests(int x)
    {
        boolean seenFailure = false;
        
        long startTime = System.nanoTime();
        
        for (int i = 0; i < x; ++i)
        {
            seenFailure |= !(BasicTestRunner.runClass(TestArithmeticInline.class));
            seenFailure |= !(BasicTestRunner.runClass(TestArithmetics.class));
            seenFailure |= !(BasicTestRunner.runClass(TestArithmeticOperations.class));
            seenFailure |= !(BasicTestRunner.runClass(TestComparisonEquals.class));
            seenFailure |= !(BasicTestRunner.runClass(TestPerformance.class));
            seenFailure |= !(BasicTestRunner.runClass(TestSubExceptions.class));
            seenFailure |= !(BasicTestRunner.runClass(TestValidityChecking.class));
            seenFailure |= !(BasicTestRunner.runClass(TestPDComparisons.class));
        }
        
        long endTime = System.nanoTime();
        
        System.out.printf("ArithmeticsTestRunner: Time taken for tests:%d \n", endTime - startTime);
        
        return seenFailure;
    }
}
