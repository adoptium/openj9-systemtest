/*******************************************************************************
* (c) Copyright IBM Corp. 2017, 2017
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
*     [2] http://openjdk.java.net/legal/assembly-exception.html
*******************************************************************************/

package net.openj9.test.simple;

import java.util.Random;
// import java.util.concurrent.ThreadLocalRandom;

import com.ibm.dataaccess.*;

public class MarshalUnmarshalBinary {

	long   seed;
	Random rnd;
	
	public MarshalUnmarshalBinary(long myseed) {

		// NB we do NOT use ThreadLocalRandom, as we don't want other code in this thread altering the seed (we don't want real random numbers)
		// here we use a per object random number generator so the sequence is repeatable.
		seed            = myseed;
		rnd  		    = new Random(seed);  // Take care One instance per object so (effectively) unlocked
	}
	
	public MarshalUnmarshalBinary() {}
	
	void do_all() {


		for (int i=0; i<100; ++i)
		{
			do_short((short) rnd.nextInt(Short.MAX_VALUE));
			do_int  (        rnd.nextInt(Integer.MAX_VALUE));
			
		}
	}

	
	
	byte     a_byteArray[] = new byte[6];
	int      offset        = 0;
	
	public static void invokeTest() {
		long seed = 0;

		System.out.printf("MarshallUnmarshall test:\n");

		seed = System.nanoTime();

		System.out.printf("Seed used is (%d) you will need this if you want to rerun\n", seed);
		
		MarshalUnmarshalBinary me = new MarshalUnmarshalBinary(seed);
		
		me.do_all();
	}
	

	
	// These methods were named like this (got refactored before 1st ship)
	//public static void marshallShortIntoByteArray(short value,
    // byte[] byteArray,
    // int offset,
    // boolean bigEndian,
    // int numBytes)
	
	
	
	private void do_short(short a_short) {

		short    b_short       = 0;

		
		ByteArrayMarshaller.writeShort(a_short, a_byteArray, offset, true);
				
		b_short = ByteArrayUnmarshaller.readShort(a_byteArray, 0, true);
		
		if (a_short != b_short)
			throw new ArithmeticException("short big_endian a_short="+a_short+" b_short="+b_short);
		

		
		ByteArrayMarshaller.writeShort(a_short, a_byteArray, offset, false);
		

		b_short = ByteArrayUnmarshaller.readShort(a_byteArray, 0, false);
		
		if (a_short != b_short)
			throw new ArithmeticException("short little_endian a_short="+a_short+" b_short="+b_short);
	}
		
	
	private void do_int(int a_int) {

		int      b_int         = 0;

		
		ByteArrayMarshaller.writeInt(a_int, a_byteArray, offset, true);
				
		b_int = ByteArrayUnmarshaller.readInt(a_byteArray, 0, true);
		
		if (a_int != b_int)
			throw new ArithmeticException("Integer big_endian a_int="+a_int+" b_int="+b_int);
		
		ByteArrayMarshaller.writeInt(a_int, a_byteArray, offset, false);
				
		b_int = ByteArrayUnmarshaller.readInt(a_byteArray, 0, false);
		
		if (a_int != b_int)
			throw new ArithmeticException("Integer little_endian a_int="+a_int+" b_int="+b_int);
	}
}
