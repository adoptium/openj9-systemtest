#!/bin/sh
# -------------------------------------------------------------------------------
# Copyright (c) 2017, 2023 IBM Corp. and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0 which accompanies this distribution
# and is available at http://eclipse.org/legal/epl-2.0 or the Apache License, 
# Version 2.0 which accompanies this distribution and is available at 
# https://www.apache.org/licenses/LICENSE-2.0.
#
# This Source Code may also be made available under the following Secondary
# Licenses when the conditions for such availability set forth in the 
# Eclipse Public License, v. 2.0 are satisfied: GNU General Public License,
# version 2 with the GNU Classpath Exception [1] and GNU General Public License,
# version 2 with the OpenJDK Assembly Exception [2].
#
# [1] https://www.gnu.org/software/classpath/license.html
# [2] https://openjdk.org/legal/assembly-exception.html
#
# SPDX-License-Identifier: EPL-2.0 OR Apache-2.0 OR GPL-2.0 WITH Classpath-exception-2.0 OR LicenseRef-GPL-2.0 WITH Assembly-exception
# -------------------------------------------------------------------------------
# Clone stf
mkdir -p $HOME/git && cd $HOME/git && rm -rf stf && mkdir stf && git clone https://github.com/AdoptOpenJDK/stf.git stf
if [ "$?" != "0" ]; then
        echo "Error cloning stf" 1>&2
        exit 1
fi
# Clone aqa-systemtest
mkdir -p $HOME/git && cd $HOME/git && rm -rf aqa-systemtest && mkdir aqa-systemtest && git clone https://github.com/adoptium/aqa-systemtest.git aqa-systemtest
if [ "$?" != "0" ]; then
        echo "Error cloning aqa-systemtest" 1>&2
        exit 1
fi
# Clone openj9-systemtest
mkdir -p $HOME/git && cd $HOME/git && rm -rf openj9-systemtest && mkdir openj9-systemtest && git clone https://github.com/eclipse-openj9/openj9/openj9-systemtest.git openj9-systemtest
if [ "$?" != "0" ]; then
        echo "Error cloning openj9-systemtest" 1>&2
        exit 1
fi
# Configure (get prereqs)
cd $HOME/git/openj9-systemtest/openj9.build/ && make configure
if [ "$?" != "0" ]; then
        echo "Error configuring openj9-systemtest - see build output" 1>&2
        exit 1
fi
# Build
cd $HOME/git/openj9-systemtest/openj9.build/ && make
if [ "$?" != "0" ]; then
        echo "Error building openj9-systemtest - see build output" 1>&2
        exit 1
fi
echo "openj9-systemtest repository build successful - to run the tests"
echo "make -f $HOME/git/openj9-systemtest/openj9.build/makefile test"
exit 0
