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
package net.openj9.test.sc;

import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.openj9.test.sc.classes.Dummy;

import java.io.File;
import java.net.URL;
import java.lang.Runnable;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Calendar;
import java.text.NumberFormat;

public class LoaderSlaveMultiThread
{
	
	public LoaderSlaveMultiThread()
	{
	}

	public static void main(String args[])
	{
		if(args.length == 0)
		{
			System.out.println("Usage: LoaderSlaveMultiThread <path of jar file eg. 10000med.jar> <MAX No. Threads>");
			return;
		}
		try
		{
			(new LoaderSlaveMultiThread()).run(args[0],Integer.parseInt(args[1]));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void run(String jarName, int threadsMAX)
		throws Exception
	{
		Counter cntr = new Counter(0);
		JarFile file = new JarFile(jarName);
		File jarFile = new File(file.getName());
		URL myURL = new URL("file", null, 0, jarFile.getCanonicalPath().replace('\\', '/'));
		URL[] myURLS = {myURL};
		URLClassLoader myCL = new URLClassLoader(myURLS);
		
		int start = 0, finish = 0;
		LinkedList<Thread> threads = new LinkedList<Thread>();
		
		for(Enumeration<JarEntry> entries = file.entries(); entries.hasMoreElements();)
		{
			JarEntry entry = (JarEntry)entries.nextElement();
			String className = entry.getName();
			if(className.endsWith(".class"))
			{
				className = className.substring(0, className.length() - 6);
				className = className.replace('/', '.');
				
				LoaderThread lT = new LoaderThread(myCL, className, cntr);
				Thread t = new Thread(lT);
				finish++;
				threads.add(t);
				t.start();
			}
			if (finish - start == threadsMAX)
			{
				logMessage("--> Threads " + start + " to " + finish + " <--");
				start = finish;
				Iterator<Thread> threadsJoinI = threads.iterator();
				while (threadsJoinI.hasNext())
				{
					threadsJoinI.next().join();
				}
				threads = new LinkedList<Thread>();
			}
		}
		
		file.close();
	}
	
	public void logMessage(String message)
	{
		NumberFormat formatter = NumberFormat.getIntegerInstance();
		formatter.setMinimumIntegerDigits(2);
		Calendar c = Calendar.getInstance();
		System.out.println(formatter.format(c.get(Calendar.HOUR_OF_DAY)) + ":" + formatter.format(c.get(Calendar.MINUTE)) + ":" + formatter.format(c.get(Calendar.SECOND)) + " >> " + message);
	} 

	public String testClassDir;
	
	private class Counter
	{
		private int val;
		
		public Counter(int value)
		{
			val = value;
		}
		
		public void inc()
		{
			val++;
		}
		
		public int getVal()
		{
			return val;
		}
	}
	
	private class LoaderThread implements Runnable
	{
		private URLClassLoader cLoader;
		private String cName;
		private Counter cntr;
		
		public LoaderThread(URLClassLoader classLoader, String className, Counter counter)
		{
			cLoader = classLoader;
			cName = className;
			cntr = counter;
		}
		
		public void run()
		{
			try 
			{
				Class<?> myC;
				synchronized (cLoader)
				{
					myC = cLoader.loadClass(cName);
				}
				Class<?> interfaces[] = myC.getInterfaces();
				if(interfaces != null && interfaces.length > 0 && interfaces[0].equals(net.openj9.test.sc.classes.Dummy.class))
				{
					synchronized (cntr)
					{
						cntr.inc();						
						if(cntr.getVal() % 1000 == 0)
							logMessage("Loaded " + cntr.getVal() + " classes...");
					}
					Dummy myDummy = (Dummy)myC.newInstance();
					@SuppressWarnings("unused")
					int id = myDummy.getID();
					@SuppressWarnings("unused")
					String name = myDummy.getName();
				}
			} catch (IllegalAccessException iae)
			{
				String msg= "ERROR: Incorrect access for instantiating class: " + cName;
				throw new RuntimeException(msg);
				
			} catch (ClassNotFoundException cnfe)
			{
				String msg = "ERROR: Unable to find class " + cName + " to load";
				throw new RuntimeException(msg);
			} catch (InstantiationException ie)
			{
				String msg= "ERROR: Unable to instantiate class: " + cName;
				throw new RuntimeException(msg);
			}
			
		}
	}
}
