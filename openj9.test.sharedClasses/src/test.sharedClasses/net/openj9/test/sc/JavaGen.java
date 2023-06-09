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

package net.openj9.test.sc;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

// This class generates jar files to be tested.
// A sample generated file is shown at the bottom of this file.

public class JavaGen
{
	static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
	
	List<String> fileNames = new ArrayList<String>();
	
	static final int COMPILE_BATCH_SIZE = 100;

	public static void main(String[] args)
	{
		// arg 1 path to folder to place files
		// arg 2 number of files
		String path = args[0];
		String num  = args[1];
		
		log("Generating jar files in " + path);
		
		int numArg = 0;
		try
		{
			numArg = Integer.parseInt(num);
		} catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException("parseInt failed to parse " + num);
		}

		File destination = new File(path);
		
		if (!destination.exists())
		{
			boolean b = destination.mkdirs();
			if (!b)
			{
				throw new RuntimeException("Failed to make destination directory " + path + " for generating sharedclasses jar test jar files.");
			}
		} else
		{
			if (!destination.isDirectory())
			{
				throw new RuntimeException(path + " must be a preexisting directory.");
			}
		}


		JavaGen generator = new JavaGen();
		generator.go(path, numArg);
	 
	}
	
	void go(String path, int numArg) {
		log("Creating source files");
		makeJavas(path, numArg); //Make java source files
		log("Compiling java files");
		compileJavas(path); //Compile Source to .class files
		log("Source files compiled, creating jar files");
		makeJars(path, numArg); //Create jar files
		log("Jar files created");
		return;
	}
	
	/**
	 * Prints a message to stdout with a timestamp
	 */
	public static void log(String message) {
		System.out.println(dateFormatter.format(new Date()) + ": " + message);
	}
	
	/**
	 * Creates a large number of java files.
	 * 
	 * @param directory String, the directory to write them into.
	 * @param count int, the number of tests to generate.
	 */
	private void makeJavas(String dir, int count)
	{
		String sl = System.getProperty("file.separator");
		String directory = dir + sl + "net" + sl + "openj9" + sl + "sc" + sl + "classes";
		
		File fileDir = new File(directory);
		boolean mkDirBool = fileDir.mkdirs();
		
		if (!mkDirBool)
		{
			throw new RuntimeException("Failed to create destination directory for source files");
		}
		 
		int nameLen;
		
		if (("" + (count-1)).length() < ("" + count).length())
		{
			nameLen = ("" + (count-1)).length();
		} else
		{
			nameLen = ("" + count).length();
		}
		
		String format = new String("");
		while (nameLen > 0)
		{
			format = format + "0";
			nameLen--;
		}
		
		// Create Init class for locking the large jar files
		try
		{
			String bName = directory + sl + "Test_Init";
			fileNames.add(bName);
			String fName = bName + ".java";
			//log("Creating " + fName);
			BufferedWriter bWrite = new BufferedWriter(new FileWriter(fName));
			bWrite.write("package net.openj9.sc.classes;");
			bWrite.newLine();
			bWrite.write("public class Test_Init" + " implements net.openj9.test.sc.classes.Dummy{");
			bWrite.newLine();
			bWrite.write("int id;");
			bWrite.newLine();
			bWrite.write("String name;");
			bWrite.newLine();
			bWrite.write("public Test_Init" + "(){");
			bWrite.newLine();
			bWrite.write("id = -1;");
			bWrite.newLine();
			bWrite.write("name = new String(\"Init\");");
			bWrite.newLine();
			bWrite.write("}");
			bWrite.newLine();

		
			for (int j = 0; j < 100; j++)
			{
				writeFunc(j,bWrite);
			}
		
			bWrite.write("public int getID(){");
			bWrite.newLine();
			bWrite.write("return id;");
			bWrite.newLine();
			bWrite.write("}");
			bWrite.newLine();
			
			bWrite.write("public String getName(){");
			bWrite.newLine();
			bWrite.write("return name;");
			bWrite.newLine();
			bWrite.write("}");
			bWrite.newLine();
			
			bWrite.write("}");
			bWrite.flush();
			bWrite.close();
		} catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}

		DecimalFormat nameFormat = new DecimalFormat(format);
		
		try
		{
			for (int i = 0; i < count; i++)
			{
				String bName = directory + sl + "Test_Init_" + nameFormat.format(i);
				fileNames.add(bName);
				String fName = bName + ".java";
				//log("Creating " + fName);
				BufferedWriter bWrite = new BufferedWriter(new FileWriter(fName));
				bWrite.write("package net.openj9.sc.classes;");
				bWrite.newLine();				
				
				// *****************************************************
				// This writes code which generates a random string when the code is executed.
				// This increases the length of the test case execution dramatically, so
				// we have opted not to do that but use a fixed string instead.
				// Left the code in in case anyone wants to revisit it.
				//
//				bWrite.write("public class Test_Init_" + nameFormat.format(i) + " implements net.openj9.test.sc.classes.Dummy{");
//				bWrite.newLine();
//				bWrite.write("private String myString = \"\";");
//				bWrite.newLine();
//				bWrite.write("public Test_Init_" + nameFormat.format(i) + "(){}");
//				bWrite.newLine();
//				bWrite.write("public int getID(){return " + (0-i) + ";}");
//				bWrite.newLine();
//				bWrite.write("public String getName(){");
//				bWrite.newLine();
//				bWrite.write("myString = makeString(500);");
//				bWrite.newLine();
				// *************************************************
				
				// *****************************************************
				// Use the same string each time.
				// The string length has been chosen so that we can fit more classes into the shared class cache.
				// To use longer strings (and therefore larger classes) adjust the commented out lines below.
				//
				bWrite.write("public class Test_Init_" + nameFormat.format(i) + " implements net.openj9.test.sc.classes.Dummy{");
				bWrite.newLine();
				bWrite.write("private String myString = \"4p9Tr3we7TVH6GPZkfIbxCYs1Pxr95e0B09G6POfW64t2606G6ITrHx16dBH6S5C3eOBu2vB4Rv7y397RBn5IwR42zJUyTjW6GOBpuYU4OX2ohal1VF04DPdwXxIX\";");
				//bWrite.newLine();
				//bWrite.write("\"jiwe1LYUX4MmhRXQpjkQfyJXTU7EzJgqx863OQL4XG76H11sHv167PJD2B7De7qY5o4asc9C0IYK919y42hehB1ml73dn56k9QEmOMVfh77LFM11u2Bb9IqqR9nss4B13k21176K14oqpl6T\" +");
				//bWrite.newLine();
				//bWrite.write("\"w1UrYHQ149753246J3tNgm9hNOsY8cbn6s12N74s7KGknU7C9071531IbQblk9bsYnQ1ZbKP2hk9A0NT19dn03r8K0jX6xr76HamH4X9gQ1uwusEllk9sN57R8rbiIt2F2t1jr254q4e7Nq0\" +");
				//bWrite.newLine();
				//bWrite.write("\"ZRp5rYkSXnt3btmW16e8pV6tPHt1f44Bem486PpS3XksQ8QK4127E4831odDLoZWZh8FH3621gy7O20JRDrOMLTmMd9gIb593zgGw6W12yRlgvbd724M5tfpZlF897qoVwC1J8ilLn1DqZ8u\";");
				//bWrite.newLine();			
				bWrite.write("public Test_Init_" + nameFormat.format(i) + "(){}");
				bWrite.newLine();
				bWrite.write("public int getID(){return " + (0-i) + ";}");
				bWrite.newLine();
				bWrite.write("public String getName(){");
				bWrite.newLine();
				// *************************************************
				
				// This writes the rest of the class out
				writeStringFunctions(bWrite);
				
				bWrite.flush();
				bWrite.close();
			}
		} catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		try
		{
			for (int i = 0; i < count; i++)
			{
				String bName = directory + sl + "TestClass_" + nameFormat.format(i);
				fileNames.add(bName);
				String fName = bName + ".java";
				//log("Creating " + fName);
				BufferedWriter bWrite = new BufferedWriter(new FileWriter(fName));
				bWrite.write("package net.openj9.sc.classes;");
				bWrite.newLine();
				
				// *****************************************************
				// This writes code which generates a random string when the code is executed.
				// This increases the length of the test case execution dramatically, so
				// we have opted not to do that but use a fixed string instead.
				// Left the code in in case anyone wants to revisit it.
				//
//				bWrite.write("public class TestClass_" + nameFormat.format(i) + " implements net.openj9.test.sc.classes.Dummy{");
//				bWrite.newLine();
//				bWrite.write("private String myString = \"\";");
//				bWrite.newLine();
//				bWrite.write("public TestClass_" + nameFormat.format(i) + "(){}");
//				bWrite.newLine();
//				bWrite.write("public int getID(){return " + (0-i) + ";}");
//				bWrite.newLine();
//				bWrite.write("public String getName(){");
//				bWrite.newLine();
//				bWrite.write("myString = makeString(50);");
//				bWrite.newLine();
				// *************************************************
				
				// *****************************************************
				// Use the same string each time
				bWrite.write("public class TestClass_" + nameFormat.format(i) + " implements net.openj9.test.sc.classes.Dummy{");
				bWrite.newLine();
				bWrite.write("private String myString = \"ZRp5rYkSXnt3btmW16e8pV6tPHt1f44Bem486PpS3XksQ8QK4127E4831odDLoZWZh8FH3621gy7O20JRDrOMLTmMd9gIb593zgGw6W12yRlgvbd724M5tfpZlF897qoVwC1J8ilLn1DqZ8u\";");
				bWrite.newLine();			
				bWrite.write("public TestClass_" + nameFormat.format(i) + "(){}");
				bWrite.newLine();
				bWrite.write("public int getID(){return " + (0-i) + ";}");
				bWrite.newLine();
				bWrite.write("public String getName(){");
				bWrite.newLine();
				//
				// *************************************************
				
				// this writes the rest of the class out
				writeStringFunctions(bWrite);
				
				bWrite.flush();
				bWrite.close();
			}
		} catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	private void compileJavas(String dir)
	{

		Iterator<String> iFnames = fileNames.iterator();
		
		int count = 0;
		int logCount = 0;
		while (iFnames.hasNext())
		{
			if( (logCount > 0) && ( (logCount % 1000) == 0 ) ) {
				log("Compiled " + logCount + " source files");
			}
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
			
			List<File> sourceFiles = new ArrayList<File>();
			
			while ((count < COMPILE_BATCH_SIZE) && iFnames.hasNext())
			{
				File f = new File(iFnames.next() + ".java");
				f.deleteOnExit();
				sourceFiles.add(f);
				count++;
				logCount++;
			}
			
			ArrayList<String> javacOptions = new ArrayList<String>();
			String javacOutDirOption = "-d";
			javacOptions.add(javacOutDirOption);
			if (isWindows())
			{
				javacOptions.add(dir.replace('\\', '/'));
			} else
			{
				javacOptions.add(dir);
			}

			
			Iterable<? extends JavaFileObject> compileList = fileManager.getJavaFileObjectsFromFiles(sourceFiles);
			CompilationTask compileTask = compiler.getTask(null, fileManager, null, javacOptions, null, compileList);
			boolean result = compileTask.call();
			
			if (!result)
			{
				System.out.println("Compilation failed");
				
				try 
				{
					fileManager.close();
				} catch (IOException e)
				{
					e.printStackTrace();
					System.exit(1);
				}
				
				System.exit(1);
			}
			
			try 
			{
				fileManager.close();
			} catch (IOException e)
			{
				System.out.println("Failed to close filemanager");
				e.printStackTrace();
				System.exit(1);
			}

			count = 0;
		}
		
		
	}
	
	private void makeJars(String directory, int numJars)
	{
		String sl = System.getProperty("file.separator");
		Iterator<String> fNomIt = fileNames.iterator();
		
		boolean isWindows = (System.getProperty("os.name").toLowerCase().indexOf("win")) > -1;
		
		// Make one big jar file
		try 
		{
			log("Creating one large jar file");
			String jarFileName = directory + sl + "classes.jar";
			FileOutputStream fOutStr = new FileOutputStream(jarFileName);
			JarOutputStream jOutStr = new JarOutputStream(fOutStr);

			while (fNomIt.hasNext())
			{
				String name = fNomIt.next();

				//log("Adding " + name + " to " + jarFileName);
				String entryName = name.substring(name.indexOf("net"));  // Start of net/openj9 or net\openj9

				if (isWindows)
				{
					entryName = entryName.replace('\\', '/');
				}
				
				JarEntry clasJE = new JarEntry(entryName + ".class");
				jOutStr.putNextEntry(clasJE);
				
				FileInputStream fIn = new FileInputStream(name + ".class");
				BufferedInputStream bIn = new BufferedInputStream(fIn);
				
				byte[] buffer = new byte[8192];
				int dataLength = 0;
				while ((dataLength = bIn.read(buffer)) != -1)
				{
					jOutStr.write(buffer, 0, dataLength);
				}
				
				jOutStr.flush();
				jOutStr.closeEntry();
				bIn.close();
			}
			
			jOutStr.close();
			
		} catch (IOException e)
		{
			System.out.println("Failed making one big jar");
			e.printStackTrace();
			System.exit(1);
		}
		

		
		// Make individual jar files
		try 
		{
			File jarDir = new File(directory + sl + "jars");
			boolean mkDirBool = jarDir.mkdirs();
		
			if (!mkDirBool)
			{
				System.out.println("Failed to create destination directory for jar files");
				System.exit(1);
			}
			
			int nameLen;
			
			if (("" + (numJars-1)).length() < ("" + numJars).length())
			{
				nameLen = ("" + (numJars-1)).length();
			} else
			{
				nameLen = ("" + numJars).length();
			}
			
			String format = new String("");
			while (nameLen > 0)
			{
				format = format + "0";
				nameLen--;
			}
			
			DecimalFormat nameFormat = new DecimalFormat(format);
		
			int count = 0;
			fNomIt = fileNames.iterator();
			int total = 0;
			
			while (fNomIt.hasNext())
			{
				count = 0;
				int myCount = 0;
				while ((count < 100) && fNomIt.hasNext())
				{
					String name = fNomIt.next();
					
					if (name.contains("Init"))
					{
						continue;
					}

					if ( (myCount > 0) && ((myCount % 1000) == 0) ) {
						log("Created " + myCount + " individual jar files");
					}
					myCount++;
					
					String jarFileName = jarDir + sl + name.substring(name.lastIndexOf(sl)) + ".jar";
					//log("Adding " + name + " to " + jarFileName);
					File jarFile = new File(jarFileName);
					FileOutputStream fOutStr = new FileOutputStream(jarFile);
					JarOutputStream jOutStr = new JarOutputStream(fOutStr);

					String entryName = name.substring(name.indexOf("net"));  // Start of net/openj9 or net\openj9
					
					if (isWindows)
					{
						entryName = entryName.replace('\\', '/');
					}
				
					// Insert Named Class
					JarEntry clasJE = new JarEntry(entryName + ".class");
					jOutStr.putNextEntry(clasJE);
			
					File clsFile = new File(name + ".class");
					clsFile.deleteOnExit();
					FileInputStream fIn = new FileInputStream(clsFile);
					BufferedInputStream bIn = new BufferedInputStream(fIn);
			
					byte[] buffer = new byte[8192];
					int dataLength = 0;
					while ((dataLength = bIn.read(buffer)) != -1)
					{
						jOutStr.write(buffer, 0, dataLength);
					}
				
					jOutStr.flush();
					jOutStr.closeEntry();
					fIn.close();
				
					// Insert Init class
					JarEntry clasJInit = new JarEntry(entryName.substring(0, entryName.lastIndexOf('/') + 1) + "Test_Init_" + nameFormat.format(total) + ".class");
					jOutStr.putNextEntry(clasJInit);
				
					File initClass = new File(name.substring(0, name.lastIndexOf(sl) + 1) + "Test_Init_" + nameFormat.format(total) + ".class");
					initClass.deleteOnExit();
					FileInputStream fInInit = new FileInputStream(initClass);
					BufferedInputStream bInInit = new BufferedInputStream(fInInit);

					while ((dataLength = bInInit.read(buffer)) != -1)
					{
						jOutStr.write(buffer, 0, dataLength);
					}
				
					jOutStr.flush();
					jOutStr.closeEntry();
					fInInit.close();
					bInInit.close();
			
					jOutStr.close();
					fOutStr.close();
					bIn.close();
					
					total++;
 
				}
			}
			
		} catch (IOException e)
		{
			System.out.println("Failed making individual jar files");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void writeStringFunctions(BufferedWriter bWrite) throws IOException
	{
		bWrite.write("if(Math.random() > 0.5){stringCopyAndChangeTest();}");
		bWrite.newLine();
		// Left the next two lines commented out in case they are useful for debugging one day.
		// bWrite.write("System.out.println(\"StringTester : Starting string operations test.\");");
		// bWrite.newLine();

		// The classes should not be so big that not many will fit into the sharedclass cache.
		// Adjusting the parameters will create classes of different sizes.
		// The parameters are : number of single character concatenations, number of small (1 to 20char) string concats, number of large (1 to 100) string concats
		bWrite.write("stringOperations(10, 5, 2);");
		bWrite.newLine();
		bWrite.write("return \"Passed\";}");
		bWrite.newLine();
		bWrite.write("private void stringCopyAndChangeTest(){");
		bWrite.newLine();
		bWrite.write("String copy1 = myString; String copy2 = myString;");
		bWrite.newLine();
		bWrite.write("if(!myString.equals(copy1) && !myString.equals(copy2)){System.err.println(\"stringCopyAndChangeTest : ERROR >> Copied strings were not equal to source string!\");}");
		bWrite.newLine();
		bWrite.write("copy1 = makeString(myString.length());");
		bWrite.newLine();
		bWrite.write("if(myString.equals(copy1) || !myString.equals(copy2)){System.err.println(\"stringCopyAndChangeTest : ERROR >> Copy and change test failed!\");}");
		bWrite.newLine();
		bWrite.write("}");
		bWrite.newLine();
		bWrite.write("private void stringOperations(int charConcats, int smallStringConcats, int selfConcats){");
		bWrite.newLine();
		bWrite.write("String myStringCopy1 = myString;");
		bWrite.newLine();
		bWrite.write("for(int i = 0; i < charConcats; i++){");
		bWrite.newLine();
		bWrite.write("int rand = (int)(Math.random() * 3.0);");
		bWrite.newLine();
		bWrite.write("if(rand % 3 == 0){myStringCopy1 = myStringCopy1.concat(\"\" + selectUppercaseCharacter());}");
		bWrite.newLine();
		bWrite.write("else if(rand % 3 == 1){myStringCopy1 = myStringCopy1.concat(\"\" + selectLowercaseCharacter());}");
		bWrite.newLine();
		bWrite.write("else{myStringCopy1 = myStringCopy1.concat(\"\" + selectDigit());}}");
		bWrite.newLine();
		bWrite.write("if(myString.equals(myStringCopy1)){System.err.println(\"String was identical after character concats!\");}");
		bWrite.newLine();
		bWrite.write("myStringCopy1 = myString;");
		bWrite.newLine();
		bWrite.write("for(int i = 0; i < smallStringConcats; i++){myStringCopy1 = myStringCopy1.concat(makeString((int)(Math.random() * 20.0 + 1.0)));}");
		bWrite.newLine();
		bWrite.write("if(myString.equals(myStringCopy1)){System.err.println(\"String was identical after small string concats!\");}");
		bWrite.newLine();
		bWrite.write("myStringCopy1 = myString;");
		bWrite.newLine();
		bWrite.write("for(int i = 0; i < selfConcats; i++){myStringCopy1 += myString;}");
		bWrite.newLine();
		bWrite.write("if(myString.equals(myStringCopy1)){System.err.println(\"String was identical after \" + selfConcats + \" concats!\");}");
		bWrite.newLine();
		bWrite.write("}");
		bWrite.newLine();
		bWrite.write("private char selectUppercaseCharacter(){return (char)(Math.random() * 26 + 65);}");
		bWrite.newLine();
		bWrite.write("private char selectLowercaseCharacter(){return (char)(Math.random() * 26 + 97);}");
		bWrite.newLine();
		bWrite.write("private char selectDigit(){return (char)(Math.random() * 10 + 48);}"); 
		bWrite.newLine();
		bWrite.write("private String makeString(int length){");
		bWrite.newLine();
		bWrite.write("String result = \"\";");
		bWrite.newLine();
		bWrite.write("for(int i = 0; i < length; i++){");
		bWrite.newLine();
		bWrite.write("int rand = (int)(Math.random() * 3.0);");
		bWrite.newLine();
		bWrite.write("if(rand % 3 == 0){result += selectUppercaseCharacter();}");
		bWrite.newLine();
		bWrite.write("else if(rand % 3 == 1){result += selectLowercaseCharacter();}");
		bWrite.newLine();
		bWrite.write("else{result += selectDigit();}}");
		bWrite.newLine();
		bWrite.write("return result; }");
		bWrite.newLine();
		bWrite.write("}");
	}
	
	private void writeFunc(int num, BufferedWriter bWriter)
	{
		try 
		{
			bWriter.write("public int getMe" + num + "(){");
			bWriter.newLine();
			bWriter.write("return " + num + ";");
			bWriter.newLine();
			bWriter.write("}");
			bWriter.newLine();
		} catch (IOException e)
		{
			System.err.println("ERROR: Problem while writing the getMe method to the java file being generated");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	static boolean isWindows() {
		return ((System.getProperty("os.name").toLowerCase().indexOf("win")) > -1);
	}

}

/* Sample generated class file
 **************************************
 *
package net.openj9.sc.classes;
public class TestClass_9999 implements net.openj9.test.sc.classes.Dummy {
    private String myString = "ZRp5rYkSXnt3btmW16e8pV6tPHt1f44Bem486PpS3XksQ8QK4127E4831odDLoZWZh8FH3621gy7O20JRDrOMLTmMd9gIb593zgGw6W12yRlgvbd724M5tfpZlF897qoVwC1J8ilLn1DqZ8u";
    public TestClass_9999() {
    }
    public int getID() {
        return -9999;
    }
    public String getName() {
        if (Math.random() > 0.5) {
            stringCopyAndChangeTest();
        }
        stringOperations(10, 5, 2);
        return "Passed";
    }
    private void stringCopyAndChangeTest() {
        String copy1 = myString; String copy2 = myString;
        if (!myString.equals(copy1) && !myString.equals(copy2)) {
            System.err.println("stringCopyAndChangeTest : ERROR >> Copied strings were not equal to source string!");
        }
        copy1 = makeString(myString.length());
        if (myString.equals(copy1) || !myString.equals(copy2)) {
            System.err.println("stringCopyAndChangeTest : ERROR >> Copy and change test failed!");
        }
    }
    private void stringOperations(int charConcats, int smallStringConcats, int selfConcats) {
        String myStringCopy1 = myString;
        for (int i = 0; i < charConcats; i++) {
            int rand = (int)(Math.random() * 3.0);
            if (rand % 3 == 0) {
                myStringCopy1 = myStringCopy1.concat("" + selectUppercaseCharacter());
            }
            else if (rand % 3 == 1) {
                myStringCopy1 = myStringCopy1.concat("" + selectLowercaseCharacter());
            }
            else {
                myStringCopy1 = myStringCopy1.concat("" + selectDigit());
            }
        }
        if (myString.equals(myStringCopy1)) {
            System.err.println("String was identical after character concats!");
        }
        myStringCopy1 = myString;
        for (int i = 0; i < smallStringConcats; i++) {
            myStringCopy1 = myStringCopy1.concat(makeString((int)(Math.random() * 20.0 + 1.0)));
        }
        if (myString.equals(myStringCopy1)) {
            System.err.println("String was identical after small string concats!");
        }
        myStringCopy1 = myString;
        for (int i = 0; i < selfConcats; i++) {
            myStringCopy1 += myString;
        }
        if (myString.equals(myStringCopy1)) {
            System.err.println("String was identical after " + selfConcats + " concats!");
        }
    }
    private char selectUppercaseCharacter() {
        return(char)(Math.random() * 26 + 65);
    }
    private char selectLowercaseCharacter() {
        return(char)(Math.random() * 26 + 97);
    }
    private char selectDigit() {
        return(char)(Math.random() * 10 + 48);
    }
    private String makeString(int length) {
        String result = "";
        for (int i = 0; i < length; i++) {
            int rand = (int)(Math.random() * 3.0);
            if (rand % 3 == 0) {
                result += selectUppercaseCharacter();
            }
            else if (rand % 3 == 1) {
                result += selectLowercaseCharacter();
            }
            else {
                result += selectDigit();
            }
        }
        return result;
    }
}
 */
