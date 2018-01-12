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

package net.openj9.test.sc;

import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.openj9.test.sc.classes.Dummy;

import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.text.NumberFormat;

public class LoaderSlaveMultiCL
{

	public LoaderSlaveMultiCL()
	{
	}

	public static void main(String args[])
	{
		if(args.length == 0)
		{
			System.out.println("Usage: LoaderSlaveMultiCL <path of jar file eg. 10000med.jar>");
			return;
		}
		try
		{
			(new LoaderSlaveMultiCL()).run(args[0]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void run(String jarName)
		throws Exception
	{
		int cntr = 0;
		@SuppressWarnings("unused")
		int id = 0;
		@SuppressWarnings("unused")
		String name = "";
		JarFile file = new JarFile(jarName);
		File jarFile = new File(file.getName());
		URL myURL = new URL("file", null, 0, jarFile.getCanonicalPath().replace('\\', '/'));
		URL[] myURLS = {myURL};
		for(Enumeration<JarEntry> entries = file.entries(); entries.hasMoreElements();)
		{
			JarEntry entry = entries.nextElement();
			String className = entry.getName();
			if(className.endsWith(".class"))
			{
				URLClassLoader myCL = new URLClassLoader(myURLS);
				className = className.substring(0, className.length() - 6);
				className = className.replace('/', '.');
				
				// Need to load the init class each time so that the test classes can be loaded from the cache
				Class<?> initC = myCL.loadClass("net.openj9.sc.classes.Test_Init");
				Dummy initDummy = (Dummy)initC.newInstance();
				id = initDummy.getID();
				name = initDummy.getName();
				
				Class<?> myC = myCL.loadClass(className);
				Class<?> interfaces[] = myC.getInterfaces();
				if(interfaces != null && interfaces.length > 0 && interfaces[0].equals(net.openj9.test.sc.classes.Dummy.class))
				{
					cntr++;
					Dummy myDummy = (Dummy)myC.newInstance();
					if(cntr % 1000 == 0)
						logMessage("Loaded " + cntr + " classes...");
					id = myDummy.getID();
					name = myDummy.getName();
				}
				myCL.close();
			}
		}
		file.close();
		logMessage("Total classes loaded = " + cntr);
	}

	public void logMessage(String message)
	{
		NumberFormat formatter = NumberFormat.getIntegerInstance();
		formatter.setMinimumIntegerDigits(2);
		Calendar c = Calendar.getInstance();
		System.out.println(formatter.format(c.get(Calendar.HOUR_OF_DAY)) + ":" + formatter.format(c.get(Calendar.MINUTE)) + ":" + formatter.format(c.get(Calendar.SECOND)) + " >> " + message);
	}
	
	public String testClassDir;
}
