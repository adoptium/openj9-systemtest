/*******************************************************************************
* Copyright (c) 2017, 2023 IBM Corp. and others
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
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0 OR GPL-2.0 WITH Classpath-exception-2.0 OR LicenseRef-GPL-2.0 WITH Assembly-exception
*******************************************************************************/

package net.openj9.test.jlm.local;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.UndeclaredThrowableException;
import java.rmi.ConnectException;
import java.rmi.UnmarshalException;
import java.util.List;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.logging.LoggingMXBean;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import org.junit.Assert;
import net.adoptopenjdk.test.jlm.resources.Message;
import net.openj9.test.jlm.resources.EnvironmentData;
import net.openj9.test.jlm.resources.MemoryData;
import net.openj9.test.jlm.resources.VMData;

/*
 * This class retrieves information about the local VM
 */
public class VMLogger {

	private String logFile;
	
	// MBeanServerConnection
    private MBeanServerConnection mbs;
	
	// Proxis
    private RuntimeMXBean         runtimeBean;
    private OperatingSystemMXBean osBean;
    private MemoryMXBean          memBean;
    private LoggingMXBean         logBean;
    
    private List<MemoryPoolMXBean>        memPoolBeans;
    private List<MemoryManagerMXBean>     memMgrBeans;
    private List<GarbageCollectorMXBean>  gcBeans;    
    
    // Server ObjectNames 
    private ObjectName srvRuntimeName;
    private ObjectName srvOSName;
    private ObjectName srvClassName;
    private ObjectName srvThrdName;
    private ObjectName srvMemName;
    private ObjectName srvLogName;

    private Set<?> srvMemMgrNames;
    private Set<?> srvMemPoolNames;
    private Set<?> srvGCNames;
    
    // Data loggers
    private EnvironmentData envData;   
    private MemoryData memoryData;      
    
    public VMLogger(String logFile) {
    	// Store the log file
    	this.logFile = logFile;
    }
    
    public static void main (String[] args) {
        if (args.length != 1) {
            Message.logOut("Usage: net.adoptopenjdk.test.jlm.local.VMLogger <logFile>");
            Assert.fail("Usage: java net.adoptopenjdk.test.jlm.local.VMLogger <logFile>");
        }

        // Create a VMLogger object to run the test
        VMLogger theLogger = new VMLogger(args[0]);

        // Initialize logger 
        theLogger.initialise();

        // Write the VM Data to the files using the MXBean Proxies
        theLogger.writeProxyData("VM DATA RETRIEVED USING BEANS DIRECTLY");
        
        // Write the VM Data to the files using the MBeanServerConnection
        theLogger.writeServerData("VM DATA RETRIEVED USING THE MBEAN SERVER");

        // Enable some of the optional functionality and then rewrite the
        // VM Data using the MBeanServerConnection
        theLogger.enableOptionalFunctionality();
        theLogger.writeServerData("VM DATA RETRIEVED USING THE MBEAN SERVER - OPTIONAL FUNCTIONALITY ENABLED");
        
        // Disable the functionality and then rewrite the VM Data through proxies
        theLogger.disableOptionalFunctionality();
        theLogger.writeServerData("VM DATA RETRIEVED USING THE BEANS DIRECTLY - OPTIONAL FUNCTIONALITY DISABLED");
    } 

    public void initialise() {
        // Initialize the logger
        mbs = ManagementFactory.getPlatformMBeanServer();

        // Initialize the proxies
        initialiseProxies();
        
        // Initialize server name objects;
        initialiseServerNames();
    
        // Initialize each of the data loggers
        initialiseDataLoggers();
        
        VMData.writeHeading(logFile, false, "START TESTING");
    }
   
    private void initialiseProxies() {
        runtimeBean  = ManagementFactory.getRuntimeMXBean();
        osBean       = ManagementFactory.getOperatingSystemMXBean();

        memBean      = ManagementFactory.getMemoryMXBean();
        logBean      = LogManager.getLoggingMXBean();

        memPoolBeans = ManagementFactory.getMemoryPoolMXBeans();
        memMgrBeans  = ManagementFactory.getMemoryManagerMXBeans();
        gcBeans      = ManagementFactory.getGarbageCollectorMXBeans();
    }
    
    // Get the ObjectNames need to access the MXBeans through a MBeanServerConnection
    private void initialiseServerNames() {
        try {
        	srvRuntimeName  = new ObjectName(ManagementFactory.RUNTIME_MXBEAN_NAME);
            srvOSName       = new ObjectName(ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME);
            srvClassName    = new ObjectName(ManagementFactory.CLASS_LOADING_MXBEAN_NAME);     
            srvThrdName     = new ObjectName(ManagementFactory.THREAD_MXBEAN_NAME);
            srvMemName      = new ObjectName(ManagementFactory.MEMORY_MXBEAN_NAME);
            srvLogName      = new ObjectName("java.util.logging:type=Logging");
            srvMemMgrNames  = mbs.queryNames(new ObjectName(ManagementFactory.MEMORY_MANAGER_MXBEAN_DOMAIN_TYPE + ",*"), null);
            srvMemPoolNames = mbs.queryNames(new ObjectName(ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE + ",*"), null);
            srvGCNames      = mbs.queryNames(new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",*"), null);
        } catch ( MalformedObjectNameException me) {
            Message.logOut("Got a MalformedObjectNameException");
            me.printStackTrace();
            Assert.fail("Got a MalformedObjectNameException");
        } catch ( IOException ie ) {
            Message.logOut("Caught an IOException:");
            ie.printStackTrace();
            Assert.fail("Caught an IOException: \n" + ie.getMessage());
        }	
    }
    
    private void initialiseDataLoggers() {
        envData    = new EnvironmentData(logFile);
        memoryData = new MemoryData(logFile);
    }
    
    public void writeProxyData(String msg) {
        VMData.writeHeading(logFile, true, msg); 
        try {
            envData.writeData (runtimeBean, osBean, logBean, true);
            memoryData.writeData (memBean, memMgrBeans, memPoolBeans, gcBeans, true);
        } catch ( UndeclaredThrowableException ue) {
            Message.logOut("UndeclaredThrowableException when trying to access the Platform MBean Server");
            ue.printStackTrace();
            Assert.fail("UndeclaredThrowableException when trying to access the Platform MBean Server");           
        } 
    }
    
    public void writeServerData(String msg) {
        VMData.writeHeading(logFile, true, msg); 
        try {
            envData.writeData(mbs, srvRuntimeName, srvOSName, srvLogName, true);
            memoryData.writeData(mbs, srvMemName, srvMemMgrNames, srvMemPoolNames, srvGCNames, true);
        } catch ( ConnectException ce) {
            Message.logOut("ConnectException when trying to access the Platform MBean Server");
            ce.printStackTrace();
            Assert.fail("ConnectException when trying to access the Platform MBean Server");            
        } catch ( UnmarshalException ue) {
            Message.logOut("UnmarshelException when trying to access the Platform MBean Server");
            ue.printStackTrace();
            Assert.fail("UnmarshelException when trying to access the Platform MBean Server");           
        } 
    }

    public void disableOptionalFunctionality() {
        try {
            // ClassLoadingMXBean operations
            mbs.setAttribute(srvClassName, new Attribute("Verbose", new Boolean(false)));

            // MemoryMXBean operations
            mbs.setAttribute(srvMemName, new Attribute("Verbose", new Boolean(false)));

            // MemoryPoolMXBean operations
            for (Object srvMemPoolObj: srvMemPoolNames) {
                ObjectName srvMemPoolName = (ObjectName) srvMemPoolObj;

                mbs.invoke(srvMemPoolName, "resetPeakUsage", new Object[] { }, new String[] { });

                if (((Boolean)(mbs.getAttribute(srvMemPoolName, "CollectionUsageThresholdSupported"))).booleanValue()) {
                    mbs.setAttribute(srvMemPoolName, new Attribute("CollectionUsageThreshold", new Long(0)));            }

                if (((Boolean)(mbs.getAttribute(srvMemPoolName, "UsageThresholdSupported"))).booleanValue()) {
                    mbs.setAttribute(srvMemPoolName, new Attribute("UsageThreshold", new Long(0)));
                }            
            }

            // ThreadMXBean operations
            mbs.invoke(srvThrdName, "resetPeakThreadCount", new Object[] { }, new String[] { });

            if (((Boolean)(mbs.getAttribute(srvThrdName, "ThreadContentionMonitoringSupported"))).booleanValue()) {
                mbs.setAttribute(srvThrdName, new Attribute("ThreadContentionMonitoringEnabled", new Boolean(false))); 
            }


            if (((Boolean)(mbs.getAttribute(srvThrdName, "ThreadCpuTimeSupported"))).booleanValue()) {
                mbs.setAttribute(srvThrdName, new Attribute("ThreadCpuTimeEnabled", new Boolean(false)));           
            }

            // LoggingMXBean operations
            String[] loggers = (String[]) mbs.getAttribute(srvLogName, "LoggerNames"); 
            
            String[] levels = {"FINEST", "FINER", "FINE", "CONFIG", "INFO", "WARNING", "SEVERE"};
            int i = 0;
            
            for( String logger : loggers) {               	
                if (i > 6) {
                	i = i - 7;
                }
                
                // LoggingMXBean operations
                logBean.setLoggerLevel(logger, levels[i]);
                mbs.invoke(srvLogName, "setLoggerLevel", new Object[] {logger, levels[i]}, new String[] {"java.lang.String", "java.lang.String"});
                
                i++;                
            }
       } catch (UnsupportedOperationException uoe) {
            Message.logOut("One of the operations you tried is not supported");
            uoe.printStackTrace();
        } catch (MBeanException mbe) {
            Message.logOut("Problem with the MBean access");
            mbe.printStackTrace();
        } catch (AttributeNotFoundException ae) {
            Message.logOut("Attribute does not exist on the MBean");
            ae.printStackTrace();
        } catch (InstanceNotFoundException ie) {
            Message.logOut("MBean Instance not found");
            ie.printStackTrace();
        } catch (ReflectionException re) {
            Message.logOut("Problem with the reflection of the MBean");
            re.printStackTrace();
        } catch (InvalidAttributeValueException iave) {
            Message.logOut("Problem with the value you attempted to set an attribute to");
            iave.printStackTrace();
        } catch (IOException ioe) {
            Message.logOut("A communication problem occurred when talking to the MBean server");
            ioe.printStackTrace();
        }
    }
    
    public void enableOptionalFunctionality() {
        // Turn on the optional functionality, reset thresholds etc - using the beans directly
        try {
            // MemoryMXBean operations
            memBean.setVerbose(true);

            // MemoryPoolMXBean operations
            for (MemoryPoolMXBean memPoolBean: memPoolBeans) {
                memPoolBean.resetPeakUsage();

                if (memPoolBean.isCollectionUsageThresholdSupported()) {
                    memPoolBean.setCollectionUsageThreshold(10000);
                }

                if (memPoolBean.isUsageThresholdSupported()) {
                    memPoolBean.setUsageThreshold(200000);
                }            
            }
            
            // LoggingMXBean operations
            List<String> loggers = logBean.getLoggerNames();
            
            String[] levels = {"SEVERE", "WARNING", "INFO", "CONFIG", "FINE", "FINER", "FINEST"};
            int i = 0;
            
            for( String logger : loggers) {               	
                if (i > 6) {
                	i = i - 7;
                }
                
                // There's a chance the logger no longer exists
                String parent = logBean.getParentLoggerName(logger);
                if (parent != null) {
                	logBean.setLoggerLevel(logger, levels[i]);
                }
                
                i++;                
            }
            
        } catch (UnsupportedOperationException uoe) {
            Message.logOut("One of the operations you tried is not supported");
            uoe.printStackTrace();
        }
    }
}
