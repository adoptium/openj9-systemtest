java.lang.management Test Suite for IBM specific JMX Bean extensions
=====================================================================

## What does it test? 
This project contains tests specific to IBM extensions of the java.lang.management Beans. There are a list of interfaces, defined by Oracle, of the java.lang.management JMX Beans to inspect the JVM's state and modify its properties via a set of managed beans, known as Platform MBeans. Along with the implementations of these interfaces, IBM also extends some of the interfaces to accommodate some additional IBM specific APIs. This test project test those IBM specific APIs.  

**Note:** 'openj9.test.jlm' has a project dependency on 'javasvt.test.jlm' project, since it uses the same test framework to drive the IBM bean tests. Please ensure 'javasvt.test.jlm' in the project classpath before attempting to compile this project. 
    