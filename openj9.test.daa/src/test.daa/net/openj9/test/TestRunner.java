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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class TestRunner {
	public static void printUsage()
	{
		System.out.println ("Usage: java net.openj9.test.TestRunner testClassName [options] [methods...]");
		System.out.println ();

		System.out.println ("Note: specify the options first, then methods, if any");
		System.out.println ("where options include:");
		System.out.println ("   testClassName");
		System.out.println ("      Name of the test class, e.g. net.openj9.test.arithmetics.TestArithmeticOperations");
		System.out.println ("   stackArg");
		System.out.println ("      If stackArg is '-stackTrace', the stack trace will be printed for failure, else, no stack trace will be printed");
		System.out.println ("   runAllArg");
		System.out.println ("      If runAllArg is '-runAll', all methods in the test class will be run. Note: if this option is enabled, [methods..] will not be checked");
		System.out.println ("   infoArg");
		System.out.println ("      -explicit for explicit diagnostic information of failures");
		System.out.println ("      -fast for fast diagnostic information of failures");
		System.out.println ("      default is simple information on failures");
		System.out.println ("   methods");
		System.out.println ("      List of method names to be run, separated by spaces. ie. test01 test02 test03");
		System.out.println ("   quiet");
		System.out.println ("      If -quiet is set, won't print out info for each iteration, (good for large iteration runs)");
		System.out.println ("   iterArg");
		System.out.println ("      Sets the number of iterations, -iter=nnn, where nnn is the number of iterations to be run");
	}

	enum DiagnosticMode{DEFAULT, FAST, EXPLICIT};
	public static void main(String[] args) throws ClassNotFoundException
	{
		if (args.length < 2)
		{
			printUsage();
			System.exit(0);
		}
		
		JUnitCore core = new JUnitCore();
		
		System.out.println ("JUnit version " + core.getVersion());
		System.out.println ();
		
		MyRunListener runListener = new MyRunListener();
		
		
		List<String> arguments = (List<String>) Arrays.asList(args);
		
		Iterator<String> argIter = arguments.iterator();
				
		String[] methods;
		String testClassName = argIter.next();
		
		String temp;

		boolean printStack = false;
		boolean runAll = false;
		boolean quietMode = false;
		DiagnosticMode diagMode = DiagnosticMode.DEFAULT;

		int index = 1;
		
		int iterations = 20;
		
		while(argIter.hasNext())
		{
			temp = argIter.next();
			if (temp.startsWith("-"))
			{
				index++;
			}
			else
			{
				break;
			}
			
			if (temp.equals("-stackTrace"))
				printStack = true;
			if (temp.equals("-runAll"))
				runAll = true;
			if (temp.equals("-explicit"))
				diagMode = DiagnosticMode.EXPLICIT;
			if (temp.equals("-fast"))
				diagMode = DiagnosticMode.FAST;
			if (temp.equals("-quiet"))
				quietMode = true;
			if (temp.startsWith("-iter"))
			{
				iterations = Integer.parseInt(temp.substring(6));
				if (iterations < 1)
					iterations = 1;
			}
		}
		
		Class<?> testClass = Class.forName(testClassName);
		
		Request myRequest = Request.aClass(testClass);
		
		if (runAll)
		{
			myRequest = myRequest.filterWith(Filter.ALL);
		}
		else
		{
			methods = new String[args.length-index];
			System.arraycopy(args, index, methods, 0, methods.length);
			MyFilter myFilter = new MyFilter(methods);
			myRequest = myRequest.filterWith(myFilter);
		}
		
		
		

		if (!quietMode)
		{
			core.addListener(runListener);
			System.out.println ("Running iteration " + 1 + ": ");
		}
		int totalRunTime = 0;
		int totalRunCount = 0;
		int totalFailCount = 0;
		Result result = core.run(myRequest);//JUnitCore.runClasses(testClass);
		totalRunTime += result.getRunTime();
		totalRunCount += result.getRunCount();
		totalFailCount += result.getFailureCount();
		for (int i=1;i<iterations;i++)
		{
			if (!quietMode)
				System.out.println ("Running iteration " + (i+1) + " : ");
			result = core.run(myRequest);
			totalRunTime += result.getRunTime();
			totalRunCount += result.getRunCount();
			totalRunCount += result.getFailureCount();
		}
		
		System.out.println ();
		System.out.println ("Total run time: " + totalRunTime + " ms");
		System.out.println ("Total run count: " + totalRunCount);
		System.out.println ("Total fail count: " + totalFailCount);
		System.out.println ("Last run time: " + result.getRunTime() + " ms");
		System.out.println ("Last run count: " + result.getRunCount());
		System.out.println ("Last fail count: " + result.getFailureCount());
		System.out.println ();
		
		List<Failure> listOfFailures = result.getFailures();
		
		Failure currFailure;
		
		System.out.println ("Failed tests: ");
		
		for (int i=0;i<listOfFailures.size();i++)
		{
			currFailure = listOfFailures.get(i);
			System.out.println ("" + (i+1) + ") " + currFailure.getTestHeader());
			if (printStack)
				System.out.println (toDiagnosticMode(currFailure.getTrace(), diagMode));
			else
				System.out.println (toDiagnosticMode(currFailure.getMessage(), diagMode));	
			System.out.println();
		}
		
		printInlineDetectorStats();
			/*
			 * get list of method names
			 * if first argument is "_runAll"
			 * will run all tests
			 * else, create list of methods
			 * convert list into junit descriptions
			 * 
			 * create junitcore object
			 * print junit version
			 * add listener to junitcore
			 * listeners will print function name when function is tested
			 * run tests with junit descriptions
			 * after tests are run
			 * result object is used to
			 * print time it took to run
			 * failure count
			 * each failure
			 * 
			 * failure printer prints
			 * function caller name
			 * assert error
			 * optional call stack
			 */
	}
	
	public static void printInlineDetectorStats()
	{
		System.out.println ("End of tests");
	}
	
	public static String toDiagnosticMode(String x, DiagnosticMode dm)
	{
		String[] split = x.split("\\|");
		
		if (split.length!=4)
		{
			return x;
		}
		
		switch (dm)
		{
		case EXPLICIT:
			return "" + split[1] + split[3];
		case FAST:
			return "" + split[2] + split[3];
		default:
			return "" + split[0] + split[3];
		}
	}
}

class MyFilter extends Filter
{
	String[] methods;
	
	public MyFilter(String[] x)
	{
		methods = new String[x.length];
		System.arraycopy(x, 0, methods, 0, x.length);
	}
	
	public String describe()
	{
		return "This filter only runs tests that are in a String array (instantiated by the contructor)";
	}

	public boolean shouldRun(Description desc) {
		for (int i=0;i<methods.length;i++)
		{
			if (desc.getMethodName().equals(methods[i]))
			{
				return true;
			}
		}
		return false;
	}
}

class MyRunListener extends RunListener
{
	public MyRunListener()
	{
		super();
	}
	
	public void testStarted(Description description)
	{
		System.out.println ("Test started: " + description.getMethodName());
	}
}