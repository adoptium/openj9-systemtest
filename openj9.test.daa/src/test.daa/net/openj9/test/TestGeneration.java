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

package net.openj9.test;

import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.junit.runner.RunWith;

@RunWith(Parameterized.class)
public class TestGeneration {
	
	static String expectedArgument = "-DexpectedFile=";
	static String vlogArgument = "-DvlogFile=";
	static String parseArgument = "-DparseFile=";
	static String[] params;
	
	@Test
	public void LogCompare()
	{
		@SuppressWarnings("unused") // Unused if fail(output) below is uncommented
		String output = "";
		String inlined = "";
		
		for (int i=0;i<expectedLines.length;i++)
		{
			if (parsedLines.length <= i || !expectedLines[i].equals(parsedLines[i]))
			{
				if (!expectedLines[i].startsWith("!"))
				{
					inlined = "BE inlined.";
				}
				else
				{
					inlined = "NOT be inlined.";
				}
				output += "\nFunction name: " + expectedLines[i];
				output += "\nCall number "+(i+1)+" in test function "+functionName+"() was expected to "+inlined;
				output += "\nNumber of calls expected to be inlined/not inlined: " + expectedLines.length;
				output += "\nNumber of calls inlined/not inlined: " + parsedLines.length;
				
				//fail(output);
			}
		}
	}
	
	/*
	 * fields for each test
	 */
	private String functionName;
	private String[] expectedLines;
	private String[] parsedLines;

    public TestGeneration(String input1, String[] input2, String[] input3) {
       functionName = input1;
       expectedLines = input2;
       parsedLines = input3;
    }
	
	@Parameters
	public static Collection<?> data() throws IOException
	{
		List<String> inputArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
		
		Iterator<String> inputArgsIterator = inputArgs.iterator();
		
		String temp = null;
		
		String expectedFile = null;
		String vlogFile = null;
		String parseFile = null;
		
		
		while(inputArgsIterator.hasNext())
		{
			temp = (String)inputArgsIterator.next();
			
			if (temp.startsWith(expectedArgument))
			{
				expectedFile = temp.substring(expectedArgument.length());
			}
			else if (temp.startsWith(vlogArgument))
			{
				vlogFile = temp.substring(vlogArgument.length());
			}
			else if (temp.startsWith(parseArgument))
			{
				parseFile = temp.substring(parseArgument.length());
			}
		}
		
		if (expectedFile == null || vlogFile == null || parseFile == null)
		{
			System.out.println ("All files must be specified");
			System.exit(0);
		}
		
		LogParser.parseLogFile(vlogFile, parseFile);
		
		String tempFuncName = null;
		
		ArrayList<String> tempExpecList = new ArrayList<String>();
		ArrayList<String> tempParseList = new ArrayList<String>();
		
		HashMap<String, Object[]> expecMap = new HashMap<String, Object[]>();
		HashMap<String, Object[]> parseMap = new HashMap<String, Object[]>();
		
		Object[] singleParam = new Object[3];
		
		ArrayList<Object[]> paramsList = new ArrayList<Object[]>();
		
		String[] stringArrayType = new String[0];
		
		BufferedReader br = new BufferedReader(new FileReader(expectedFile));
		
		temp = br.readLine();
		
		while (temp != null)
		{
			if (temp.startsWith("@"))
			{
				if (tempFuncName != null)
				{
					expecMap.put(tempFuncName, (String[]) tempExpecList.toArray(stringArrayType));
					tempExpecList.clear();
				}
				tempFuncName = temp.substring(1);
			}
			else
				tempExpecList.add(temp);
			temp = br.readLine();
		}
		
		br.close();
		
		if (!tempExpecList.isEmpty() && tempFuncName != null)
		{
			expecMap.put(tempFuncName, (String[]) tempExpecList.toArray(stringArrayType));
			tempExpecList.clear();
		}
		
		br = new BufferedReader(new FileReader(parseFile));
		
		temp = br.readLine();
		
		while (temp != null)
		{
			if (temp.startsWith("@"))
			{
				if (tempFuncName != null)
				{					
					parseMap.put(tempFuncName, (String[]) tempParseList.toArray(stringArrayType));
					tempParseList.clear();
				}
				
				tempFuncName = temp.substring(1);
			}
			else
				tempParseList.add(temp);
			temp = br.readLine();
		}
		
		if (!tempParseList.isEmpty() && tempFuncName != null)
		{
			parseMap.put(tempFuncName, (String[]) tempParseList.toArray(stringArrayType));
			tempParseList.clear();
		}
		
		Iterator<String> keyIter = expecMap.keySet().iterator();
		
		while (keyIter.hasNext())
		{
			tempFuncName = keyIter.next();
			singleParam = new Object[3];
			singleParam[0] = tempFuncName;
			singleParam[1] = (String[]) expecMap.get(tempFuncName);
			if (parseMap.containsKey(tempFuncName))
				singleParam[2] = (String[]) parseMap.get(tempFuncName);
			else
				singleParam[2] = new String[0];
			
			paramsList.add(singleParam);
		}
		
		return paramsList;
	}
}
