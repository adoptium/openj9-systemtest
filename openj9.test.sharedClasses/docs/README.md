<!--
Copyright (c) 2017, 2021 IBM Corp. and others

This program and the accompanying materials are made available under
the terms of the Eclipse Public License 2.0 which accompanies this
distribution and is available at https://www.eclipse.org/legal/epl-2.0/
or the Apache License, Version 2.0 which accompanies this distribution and
is available at https://www.apache.org/licenses/LICENSE-2.0.

This Source Code may also be made available under the following
Secondary Licenses when the conditions for such availability set
forth in the Eclipse Public License, v. 2.0 are satisfied: GNU
General Public License, version 2 with the GNU Classpath
Exception [1] and GNU General Public License, version 2 with the
OpenJDK Assembly Exception [2].

[1] https://www.gnu.org/software/classpath/license.html
[2] http://openjdk.java.net/legal/assembly-exception.html

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0 OR GPL-2.0 WITH Classpath-exception-2.0 OR LicenseRef-GPL-2.0 WITH Assembly-exception
-->

# openj9.test.sharedClasses

The openj9.test.sharedclasses project targets load testing of the shared classes feature of IBM Java.  This was originally developed as part of IBM's Java 6 implementation.

There are three flavours of automated (stf) tests:
## SharedClasses
- These tests ensure that many classes can be loaded into the sharedclasses cache from a single or multiple classloaders and on a single or multiple threads.
- The classes and jars used (10000 classes) are generated at run time and placed in the systemtest_prereqs directory (default $HOME/systemtest_prereqs (Unix), %USERPROFILE%\systemtest_prereqs (Windows).
- The classes and jars used are not regenerated if they are already present in the systemtest_prereqs directory.
- The tests use various combinations of java -X options, some of which are representative of normal customer usage whereas others instruct the jvm to perform additional run time diagnostic checks or increase the likelihood of certain code JVM code paths to occur.
- See the test source code for the actual options available.
 
## SharedClassesWorkload
- This test runs test.load tests with a shared classes cache.
- The tests verify that a cache created by one java process can be used successfully by a subsequent java process.

## SharedClassesWorkloadSoftmx_xxxx
- These tests verify the shared classes softmx feature.  The value is adjusted while a java process is running and the tests check that the instruction to alter the cache size is honoured.

## Running the tests
To see all the valid tests, run

```
perl <stf-root>/stf.core/scripts/stf.pl -list
```

To run the SharedClassesWorkload test, run

```
perl <stf-root>/stf.core/scripts/stf.pl -test-root="<openj9-systemtest-root>" -test="SharedClassesWorkload"
```

To run the SharedClasses test, select the subtest from SingleCL, MultiCL, MultiThread, MultiThreadMultiCL or MultiJar, combined with one of the test modes.  Examples:

```
perl <stf-root>/stf.core/scripts/stf.pl -test-root="<openj9-systemtest-root>" -test="SharedClasses"  -test-args="sharedClassMode=SCM01,sharedClassTest=SingleCL"
perl <stf-root>/stf.core/scripts/stf.pl -test-root="<openj9-systemtest-root>" -test="SharedClasses"  -test-args="sharedClassMode=SCM23,sharedClassTest=MultiCL"
```
