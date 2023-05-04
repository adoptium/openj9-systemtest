/*******************************************************************************
* Copyright (c) 2017, 2023 IBM Corp.
*
* This program and the accompanying materials are made available
* under the terms of the Eclipse Public License 2.0 which accompanies
* this distribution and is available at http://eclipse.org/legal/epl-2.0
* or the Apache License, Version 2.0 which accompanies this distribution
* and is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* This Source Code is also Distributed under one or more Secondary Licenses,
* as those terms are defined by the
* Eclipse Public License, v. 2.0: GNU General Public License, version 2
* with the GNU Classpath Exception [1] and GNU General Public License,
* version 2 with the OpenJDK Assembly Exception [2].
* 
*     [1] https://www.gnu.org/software/classpath/license.html
*     [2] https://openjdk.org/legal/assembly-exception.html
*******************************************************************************/

package net.openj9.test.simple;

import com.ibm.dataaccess.*;

public class ConvertDecimal {
	
	public static void invokeTest() {
		
		int      a_int         = -987654321;	
		int      b_int         = 0;
		
		byte     a_byteArray[] = new byte[12];
		
		
		for (int i=0; i<a_byteArray.length; ++i)
			a_byteArray[i]=0x00;
		
		DecimalData.convertIntegerToExternalDecimal(a_int, a_byteArray, 0, 11, true, DecimalData.EBCDIC_SIGN_SEPARATE_LEADING);

		b_int=DecimalData.convertExternalDecimalToInteger(a_byteArray, 0, 11, true, DecimalData.EBCDIC_SIGN_SEPARATE_LEADING);
		
		if (a_int != b_int)
			System.out.printf("Decimal external big_endian a_int=%d b_int=%d\n", a_int, b_int);

		
		for (int i=0; i<a_byteArray.length; ++i)
			a_byteArray[i]=0x00;
		
		DecimalData.convertIntegerToPackedDecimal(a_int, a_byteArray, 0, 11, true);
		
	
		b_int=DecimalData.convertPackedDecimalToInteger(a_byteArray, 0, 11, true);
		
		
		if (a_int != b_int)
			System.out.printf("Packed decimal a_int=%d b_int=%d\n", a_int, b_int);
	
		
		// So now we can insert an invalid packed nibble ... does it spot this ?
		
		a_byteArray[2] = 0x7A;  // Replace 76 with 7A
		
		boolean spotted_corrupt_packed = false;
		
		try {	
			b_int=DecimalData.convertPackedDecimalToInteger(a_byteArray, 0, 11, true);
		}
		catch (Exception e) {
			spotted_corrupt_packed = true;
		}
		
		if (spotted_corrupt_packed == false)
			System.out.printf("Corrupt Packed decimal slipped through: a_int=%d b_int=%d\n", a_int, b_int);
		
	}

}
