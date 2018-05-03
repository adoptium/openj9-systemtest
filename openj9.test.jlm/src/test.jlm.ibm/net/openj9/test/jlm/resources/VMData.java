/*******************************************************************************
* Copyright (c) 2017, 2018 IBM Corp. and others
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

/*
 * Description:
 *   This java source file contains the VMData class, the superclass
 *   for the ClassData, EnvironmentData, ThreadData and MemoryData classes
 */
package net.openj9.test.jlm.resources;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Assert;

import net.adoptopenjdk.test.jlm.remote.ServerConnector;
import net.adoptopenjdk.test.jlm.resources.Message;
import net.adoptopenjdk.test.jlm.resources.Report;
import net.openj9.test.extensions.GCExtensionCommand;
import net.openj9.test.extensions.IBMBeanExtension;
import net.openj9.test.extensions.MemoryExtensionCommand;
import net.openj9.test.extensions.MemoryPoolExtensionCommand;
import net.openj9.test.extensions.OperatingSystemExtensionCommand;

public abstract class VMData {
    protected PrintWriter out = null;
    protected boolean consoleOutput = true;
    protected   String logFile = null;
    protected ServerConnector profiler;
    protected boolean isLocal;
	
    protected VMData(String logFile, ServerConnector profiler, boolean isLocal) {

		Assert.assertFalse("logFile == null", logFile == null);
		Assert.assertFalse("logFile is empty", logFile == "");

       this.profiler = profiler;
       this.logFile = logFile; 
       this.isLocal = isLocal;  
    }

    // This method prints the title followed by the text
	// wrapped over multiple lines and indented to align 
	// with the first line of text ie
	// Title  text first part
	//        text second part
    protected void wrapString (String title, String text) {
    	int titleLength = title.length();
        String paddingString = "";

        for (int j = 0; j < titleLength; j++) {
            paddingString = paddingString + " ";
        }

        int i = 0;
        int lineLength = 85 - titleLength;

        out.print(title);

        for (i = 0; i < (text.length() - lineLength) ; i = i+lineLength ) {
            out.println(text.substring(i, (i+lineLength-1)));
            out.print(paddingString);
        }
        out.println(text.substring(i, text.length()));
    }

    // This method writes the banner text with highlighted with a line of stars
	// above and below the text ie - 
	// ************************
	//       BANNER TEXT
	// ************************
    public static void writeBanner ( String banner, PrintWriter out, int pad, int tab ) {
    	String padding = "";

        for (int j = 0; j < pad; j++) {
            padding += " ";
        }

        String indent = "";

        for (int k = 0; k < tab; k++) {
            indent += " ";
        }

        String stars = "";

        for (int i = 0; i < (banner.length() + (2 * pad)); i ++) {
            stars += "*";
        }

        out.println(indent + stars);
        out.println(indent + padding + banner + padding);
        out.println(indent + stars);
    }

    // This method writes a heading as a banner 100 or more characters wide
    public static void writeHeading (String fileName, boolean append, String heading) {
    	try {
            File log = new File(fileName);
            PrintWriter out = new PrintWriter(new FileWriter(log, append));
            int pad = 0;

            if (heading.length() < 100) {
                pad = (100 - heading.length()) / 2;
            }

            VMData.writeBanner( heading, out, pad, 0 );
            out.println("");
            out.close();

        } catch (IOException ioe) {
            Message.logOut("Problem opening/closing file");
            ioe.printStackTrace();
            Assert.fail("Problem opening/closing file");
        }
    }

    // This method opens the log if append is true the log is appended to 
	// otherwise the log is created overwriting existing logs
    public void openLogFile(boolean append) {
    	try {
    		File log = new File(logFile);
            this.out = new PrintWriter(new FileWriter(log, append));
         } catch (IOException ie) {
            Message.logOut("Unable to write to file " + logFile);
            ie.printStackTrace();
            Assert.fail("Unable to write to file " + logFile);
         }
         Report.initReport( this.out );
    }
    
    // This method closes the log opened by openLogFile()
    public void closeLogFile() {
      Report.endReport();
      out.close();
    }

    protected void invokeIBMGCBeanTest() {
    	IBMBeanExtension gcExtCmd = new GCExtensionCommand(out, 
    			isLocal? null : this.profiler.getNewConnection(), 
        		isLocal, 
        		isLocal? false : this.profiler.getUseProxy());
        gcExtCmd.execute();
	}

    protected void invokeIBMMemoryBeanTest() {
    	IBMBeanExtension memoryExtCmd = new MemoryExtensionCommand(out, 
        		isLocal? null : this.profiler.getNewConnection(), 
        		isLocal, 
        		isLocal? false : this.profiler.getUseProxy());
    	memoryExtCmd.execute();
	}
    
    protected void invokeIBMMemoryPoolBeanTest() {
    	IBMBeanExtension memoryPoolExtCmd = new MemoryPoolExtensionCommand(out, 
    			isLocal? null : this.profiler.getNewConnection(), 
        		isLocal, 
        		isLocal? false : this.profiler.getUseProxy());
    	memoryPoolExtCmd.execute();
	}
    
    protected void invokeIBMOSBeanTest() {
    	IBMBeanExtension osExtCmd = new OperatingSystemExtensionCommand(out, 
    			isLocal? null : this.profiler.getNewConnection(), 
        		isLocal, 
        		isLocal? false : this.profiler.getUseProxy());
    	osExtCmd.execute();
	}
}
