<!--
Copyright (c) 2017, 2023 IBM Corp. and others

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
[2] https://openjdk.org/legal/assembly-exception.html

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0 OR GPL-2.0 WITH Classpath-exception-2.0 OR LicenseRef-GPL-2.0 WITH Assembly-exception
-->
# openj9-systemtest

This repository contains the test cases which can be run against the OpenJ9
java implmentation but not OpenJDK.

The tests all run under the [STF System Test Framework](https://github.com/AdoptOpenJDK/stf).

* [Quick start (Unix)](#unix)
* [Quick start (Windows)](#windows)
* [More documentation](openj9.build/docs/build.md)

<a name="unix"></a>
## Quick start (Unix)

This quick start is for people who want to clone and build the
project.  To set up a development environment for creating new test
cases, refer to [this document](openj9.build/docs/build.md).

Before running the build for the first time make sure GNU make, ant
and wget are on your PATH.

wget is only required for the make configure step, which only needs
to be done once.

Either copy, paste and execute [this script](openj9.build/scripts/openj9-systemtest-clone-make.sh)
which runs the command below, or run the commands yourself.

```shell
# 1. Create a directory for the git clone
mkdir -p $HOME/git

# 2. Clone the STF repository
cd $HOME/git
git clone git@github.com:AdoptOpenJDK/stf.git stf

# 3. Clone the openjdk-systemtest repository
cd $HOME/git
git clone git@github.com:AdoptOpenJDK/openjdk-systemtest.git openjdk-systemtest

# 4. Clone the openj9-systemtest repository
cd $HOME/git
git clone git@github.com:eclipse-openj9/openj9-systemtest.git openj9-systemtest

# 5. Set JAVA_HOME to a Java 8 or later Java
export JAVA_HOME=<java-home>

# 6.Install the prereqs
# This requires wget be on the PATH
cd $HOME/git/openj9-systemtest/openj9.build
make configure

# 7. Build
cd $HOME/git/openj9-systemtest/openj9.build
make

# 8. Run the tests (the tests take some time to run (many minutes))
cd $HOME/git/openj9-systemtest/openj9.build
make test
echo See /tmp/stf to view the test results
```

<a name="windows"></a>
## Quick Start (Windows)

This quick start is for people who want to clone and build the
project.  To set up a development environment for creating new test
cases, refer to [this document](openj9.build/docs/build.md).

Before running the build for the first time make sure GNU make, ant
and wget are on your PATH.

wget is only required for the make configure step, which only needs
to be done once.

Either copy, paste and execute [this
script](openj9.build/scripts/openj9-systemtest-clone-make.bat) which
runs the command below, or run the commands yourself.

```dos
REM 1. Create a directory for the git clones
mkdir %USERPROFILE%\git

REM 2. Clone the STF repository
cd %USERPROFILE%\git
git clone git@github.com:AdoptOpenJDK/stf.git stf

REM 3. Clone the openjdk-systemtest repository
cd %USERPROFILE%\git
git clone git@github.com:AdoptOpenJDK/openjdk-systemtest.git openjdk-systemtest

REM 4. Clone the openj9-systemtest repository
cd %USERPROFILE%\git
git clone git@github.com:eclipse-openj9/openj9-systemtest.git openj9-systemtest

REM 5. Set JAVA_HOME to a Java 9 or later Java
SET JAVA_HOME=<java-home>

REM 6. Get the test case prereqs
cd %USERPROFILE%\git\openj9-systemtest\openj9.build
make configure

REM 7. Build
cd %USERPROFILE%\git\openj9-systemtest\openj9.build
make

REM 8. Run the tests (takes a long time (many minutes))
cd %USERPROFILE%\git\openj9-systemtest\openj9.build
make test
echo See c:\stf_temp to view the test results
```
