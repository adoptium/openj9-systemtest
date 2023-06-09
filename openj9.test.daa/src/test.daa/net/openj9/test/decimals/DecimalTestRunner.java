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

import net.openj9.test.BasicTestRunner;
import net.openj9.test.decimals.TestBD2PD2BD;
import net.openj9.test.decimals.TestDecimalData;
import net.openj9.test.decimals.TestED2PD;
import net.openj9.test.decimals.TestPD2ED;
import net.openj9.test.decimals.TestPD2UD;
import net.openj9.test.decimals.TestUD2PD;

public class DecimalTestRunner
{
    public static void invokeTest() {
        int x = 10;

        if (runTests(x))
            System.exit(1);
    }

    public static boolean runTests(int x)
    {
        boolean seenFailure = false;
        
        long startTime = System.nanoTime();
        
        for (int i = 0; i < x; ++i)
        {
            seenFailure |= !(BasicTestRunner.runClass(TestED2PD.class));
            seenFailure |= !(BasicTestRunner.runClass(TestPD2ED.class));
            seenFailure |= !(BasicTestRunner.runClass(TestPD2UD.class));
            seenFailure |= !(BasicTestRunner.runClass(TestUD2PD.class));
            seenFailure |= !(BasicTestRunner.runClass(TestBD2PD2BD.class));
            seenFailure |= !(BasicTestRunner.runClass(TestDecimalData.class));
         // seenFailure |= !(BasicTestRunner.runClass(TestI2PD.class));

        }
        
        long endTime = System.nanoTime();
        
        System.out.printf("DecimalTestRunner: Time taken for tests:%d \n", endTime - startTime);

        return seenFailure;
    }
}
