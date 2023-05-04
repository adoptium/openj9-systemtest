#################################################################################
# Copyright (c) 2017, 2023 IBM Corp.
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
#################################################################################

EMPTY=
SPACE=$(EMPTY) $(EMPTY)

# Environment variable OSTYPE is set to cygwin if running under cygwin.
# Set our own macro to indicate we're running under cygwin.
# Also Windows commands will need to be prefixed with cmd /c to run them in a Windows shell.
ifndef CYGWIN
  OSTYPE?=$(shell echo $$OSTYPE)
  ifeq ($(OSTYPE),cygwin)
    CYGWIN:=1
    CMD_PREFIX:=cmd /c
  else
    CYGWIN:=0
  endif
endif
#$(warning CYGWIN is $(CYGWIN))

# Use a macro to say whether we are running under Windows.
# Don't set it under cygwin yet, first we need to resolve any unix style paths.
ifeq ($(OS),Windows_NT)
  ifneq ($(CYGWIN),1)
    WINDOWS:=1
  endif
else
  UNIX:=1
endif

# Set a macro for the directory delimiter.
ifeq ($(WINDOWS),1)
  D:=\\
else
  D:=/
endif

# Work out the root directory name of the repository from the current makefile.

# Assumes directory structure is
#
# git repository is checked out to <testing_root>$(D)git$(D)openj9-systemtest
# So
# <testing_root>$(D)git$(D)openj9-systemtest$(D)test_project1
# <testing_root>$(D)git$(D)openj9-systemtest$(D)test_project2
#
# stf repository is built into <testing_root>$(D)git$(D)stf
# openjdk-systemtest repository is built into <testing_root>$(D)git$(D)openjdk-systemtest
#
# prereqs are installed to <testing_root>$(D)systemtest_prereqs
# So
# <testing_root>$(D)systemtest_prereqs$(D)prereq1
# <testing_root>$(D)systemtest_prereqs$(D)prereq2
#
# To change override OPENJ9_SYSTEMTEST_ROOT, STF_ROOT and / or PREREQS_ROOT on the make command line
# make OPENJ9_SYSTEMTEST_ROOT=<my_OPENJ9_SYSTEMTEST_ROOT> STF_ROOT=<my_STF_ROOT> PREREQS_ROOT=<my_prereqs_root>

# The current makefile is the last makefile in MAKEFILE_LIST

#$(warning MAKEFILE_LIST is $(MAKEFILE_LIST))
ifeq ($(MAKEFILE_LIST),)
  $(warning The MAKEFILE_LIST variable is not supported, this inbuilt variable is available on gnu make 3.80+)
  $(warning Expecting the OPENJ9_SYSTEMTEST_ROOT variable set instead) 
  ifeq ($(OPENJ9_SYSTEMTEST_ROOT),)
    $(error The OPENJ9_SYSTEMTEST_ROOT variable is not set)
  endif
else
  CURRENT_MAKEFILE:=$(word $(words $(MAKEFILE_LIST)),$(MAKEFILE_LIST))
  #$(warning CURRENT_MAKEFILE is $(CURRENT_MAKEFILE))
  MAKEFILE_ROOT_DIR:=$(dir $(CURRENT_MAKEFILE))$(D)..
  #$(warning MAKEFILE_ROOT_DIR is $(MAKEFILE_ROOT_DIR))
  # Running pwd or dir will resolve any relative path (./ or ../) references.
  ifneq ($(WINDOWS),1)
    OPENJ9_SYSTEMTEST_ROOT:=$(shell cd "$(MAKEFILE_ROOT_DIR)"; pwd)
  else
    OPENJ9_SYSTEMTEST_ROOT:=$(strip $(subst Directory of,,$(shell $(CMD_PREFIX) dir "$(subst $(D),\,$(MAKEFILE_ROOT_DIR))" | findstr $(D)C:"Directory of")))
  endif
  # If we are running under cygwin, turn a /cygdrive/c style path into the real Windows path and then set the shell to cmd.exe.
  # The tests all expect to be running under real Windows.
  ifeq ($(CYGWIN),1)
    #$(warning Running under cygwin)
    #$(warning Before cygpath OPENJ9_SYSTEMTEST_ROOT set to $(OPENJ9_SYSTEMTEST_ROOT))
    OPENJ9_SYSTEMTEST_ROOT:=$(shell $(CMD_PREFIX) cygpath -m '$(OPENJ9_SYSTEMTEST_ROOT)')
  endif
endif
ifeq (,$(wildcard $(OPENJ9_SYSTEMTEST_ROOT)))
  $(error ERROR: OPENJ9_SYSTEMTEST_ROOT $(OPENJ9_SYSTEMTEST_ROOT) does not exist)
endif
$(warning OPENJ9_SYSTEMTEST_ROOT is $(OPENJ9_SYSTEMTEST_ROOT))

ifeq (,$(JAVA_HOME))
  $(error ERROR: JAVA_HOME not set)
endif
ifeq ($(CYGWIN),1)
  #$(warning Running $(CMD_PREFIX) cygpath -m '$(JAVA_HOME)')
  JAVA_HOME:=$(shell $(CMD_PREFIX) cygpath -m '$(JAVA_HOME)')
  #$(warning JAVA_HOME is $(JAVA_HOME))
endif
ifeq (,$(wildcard $(JAVA_HOME)))
  $(error ERROR: JAVA_HOME $(JAVA_HOME) does not exist)
endif

STF_ROOT:=$(OPENJ9_SYSTEMTEST_ROOT)$(D)..$(D)stf
TEST_STF_ROOT:=$(STF_ROOT)
#$(warning TEST_STF_ROOT is $(TEST_STF_ROOT))
ifeq ($(CYGWIN),1)
  #$(warning Running $(CMD_PREFIX) cygpath -m '$(TEST_STF_ROOT)')
  TEST_STF_ROOT:=$(shell $(CMD_PREFIX) cygpath -m '$(TEST_STF_ROOT)')
  #$(warning TEST_STF_ROOT is $(TEST_STF_ROOT))
else
  ifneq ($(WINDOWS),1)
    #$(warning Running cd "$(TEST_STF_ROOT)"; pwd)
    TEST_STF_ROOT:=$(shell cd "$(TEST_STF_ROOT)"; pwd)
  else
    # On Windows 'dir' won't work with double back slashes
    TEST_STF_ROOT:=$(subst $(D),\,$(TEST_STF_ROOT))
    #$(warning Running dir "$(TEST_STF_ROOT)")
    TEST_STF_ROOT:=$(subst \,$(D),$(strip $(subst Directory of,,$(shell $(CMD_PREFIX) dir "$(subst $(D),\,$(TEST_STF_ROOT))" | findstr $(D)C:"Directory of"))))
    #$(warning TEST_STF_ROOT is $(TEST_STF_ROOT))
  endif
endif
ifeq (,$(wildcard $(STF_ROOT)))
  $(error ERROR: STF directory $(STF_ROOT) does not exist.  Either setup the directory $(STF_ROOT) or use STF_ROOT=xxxx on the make command line to point to a different location.)
else
  STF_ROOT:=$(TEST_STF_ROOT)
endif
$(warning STF_ROOT is $(STF_ROOT))

OPENJDK_SYSTEMTEST_ROOT:=$(OPENJ9_SYSTEMTEST_ROOT)$(D)..$(D)openjdk-systemtest
TEST_OPENJDK_SYSTEMTEST_ROOT:=$(OPENJDK_SYSTEMTEST_ROOT)
#$(warning TEST_OPENJDK_SYSTEMTEST_ROOT is $(TEST_OPENJDK_SYSTEMTEST_ROOT))
ifeq ($(CYGWIN),1)
  #$(warning Running cygpath -m '$(TEST_OPENJDK_SYSTEMTEST_ROOT)')
  TEST_OPENJDK_SYSTEMTEST_ROOT:=$(shell $(CMD_PREFIX) cygpath -m '$(TEST_OPENJDK_SYSTEMTEST_ROOT)')
  #$(warning TEST_OPENJDK_SYSTEMTEST_ROOT is $(TEST_OPENJDK_SYSTEMTEST_ROOT))
else
  ifneq ($(WINDOWS),1)
    #$(warning Running cd "$(TEST_OPENJDK_SYSTEMTEST_ROOT)"; pwd)
    TEST_OPENJDK_SYSTEMTEST_ROOT:=$(shell cd "$(TEST_OPENJDK_SYSTEMTEST_ROOT)"; pwd)
  else
    # On Windows 'dir' won't work with double back slashes
    TEST_OPENJDK_SYSTEMTEST_ROOT:=$(subst $(D),\,$(TEST_OPENJDK_SYSTEMTEST_ROOT))
    #$(warning Running dir "$(TEST_OPENJDK_SYSTEMTEST_ROOT)")
    TEST_OPENJDK_SYSTEMTEST_ROOT:=$(subst \,$(D),$(strip $(subst Directory of,,$(shell $(CMD_PREFIX) dir "$(subst $(D),\,$(TEST_OPENJDK_SYSTEMTEST_ROOT))" | findstr $(D)C:"Directory of"))))
    #$(warning TEST_OPENJDK_SYSTEMTEST_ROOT is $(TEST_OPENJDK_SYSTEMTEST_ROOT))
  endif
endif
ifeq (,$(wildcard $(OPENJDK_SYSTEMTEST_ROOT)))
  $(error ERROR: openjdk-systemtest directory $(OPENJDK_SYSTEMTEST_ROOT) does not exist.  Either setup the directory $(OPENJDK_SYSTEMTEST_ROOT) or use OPENJDK_SYSTEMTEST_ROOT=xxxx on the make command line to point to a different location.)
else
  OPENJDK_SYSTEMTEST_ROOT:=$(TEST_OPENJDK_SYSTEMTEST_ROOT)
endif
$(warning OPENJDK_SYSTEMTEST_ROOT is $(OPENJDK_SYSTEMTEST_ROOT))

# Check any directories supplied via PREREQS_ROOT are valid
# Note that on Windows 'dir' doesn't work with double back slashes
# Macro assignments within a foreach loop need to be eval'ed to execute as intended

# Set a default PREREQS_ROOT if not provided on the command line
PREREQS_ROOT=$(OPENJ9_SYSTEMTEST_ROOT)$(D)..$(D)..$(D)systemtest_prereqs
$(warning PREREQS_ROOT is $(PREREQS_ROOT))

# Change the semi-colon separated list of directories into a whitespace separated list
ABSOLUTE_PREREQS_ROOT_LIST:=$(subst ;, ,$(PREREQS_ROOT))
#$(warning ABSOLUTE_PREREQS_ROOT_LIST is $(ABSOLUTE_PREREQS_ROOT_LIST))

# On cygwin convert the PREREQS_ROOT paths to windows style path names
ifeq ($(CYGWIN),1)
  ABSOLUTE_PREREQS_ROOT_LIST:=$(foreach PREREQS_ROOT,$(ABSOLUTE_PREREQS_ROOT_LIST),$(shell $(CMD_PREFIX) cygpath -m '$(PREREQS_ROOT)'))
endif

# If make configure is being run, only one prereq directory can be supplied (or none when the default location will be used).
# Otherwise check that the directories exists (make configure will create the directory if necessary).
ifneq (,$(findstring configure,$(MAKECMDGOALS)))
  ifneq (1,$(words $(ABSOLUTE_PREREQS_ROOT_LIST)))
    $(error ERROR: Only one PREREQS_ROOT directory can be specified for make configure)
  endif
else
  # Use wildcard and filter-out to find any directories which don't exist
  EXISTING_PREREQS_ROOT_LIST:=$(wildcard $(ABSOLUTE_PREREQS_ROOT_LIST))
  #$(warning EXISTING_PREREQS_ROOT_LIST is $(EXISTING_PREREQS_ROOT_LIST))
  ERROR_PREREQS_ROOT_LIST:=$(filter-out $(EXISTING_PREREQS_ROOT_LIST),$(ABSOLUTE_PREREQS_ROOT_LIST))
  $(foreach ERROR_PREREQS_ROOT,$(ERROR_PREREQS_ROOT_LIST),$(warning ERROR: PREREQS_ROOT directory $(ERROR_PREREQS_ROOT) does not exist. Either setup the directory $(ERROR_PREREQS_ROOT) or use PREREQS_ROOT=xxxx on the make command line to point to a different location.   Running make configure will get most of the prereqs.  See openjdk.build/docs/build.md for more details.))
  ifneq (,$(ERROR_PREREQS_ROOT_LIST))
    $(error )
  endif
endif

# Put the semi colons back into the list of prereq directories for passing to ant or stf.
RESOLVED_PREREQS_ROOT:=$(subst $(SPACE),;,$(ABSOLUTE_PREREQS_ROOT_LIST))
$(warning RESOLVED_PREREQS_ROOT is $(RESOLVED_PREREQS_ROOT))

# Now we have finished converting cygwin paths, we can mostly use the same behaviour as Windows.
ifeq ($(CYGWIN),1)
  UNIX:=0
  WINDOWS:=1
endif

# If we are running under cygwin we have resolved ant unix style paths and set the shell to cmd.exe.
# So now set more macros for Windows / Unix differences.
ifeq ($(WINDOWS),1)
  ESC:=^
  ESC_DQ:="""
  D:=\\
  P:=;
  EXE:=.exe
  BAT:=.bat
  TEMP?=C:\\TEMP
  CAT:=type
  WHICH:=where
  NULL:=nul
  EXPORT:=set
  OS_DATE_WITH_SLASHES = $(strip $(shell cmd.exe /C DATE /T))
  OS_DATE_WITHOUT_SLASHES = $(subst /,-,$(OS_DATE_WITH_SLASHES))
  OS_DATE = $(subst $(space),,$(OS_DATE_WITHOUT_SLASHES))
  OS_TIME_WITH_COLONS = $(strip $(shell cmd.exe /C TIME /T))
  OS_TIME_WITHOUT_COLONS = $(subst :,-,$(OS_TIME_WITH_COLONS))
  OS_TIME = $(subst $(space),,$(OS_TIME_WITHOUT_COLONS))
  ifeq ($(CYGWIN),1)
    EXPORT:=export
    # If we are running under cygwin, the tests need to run with a Windows perl port (e.g. Strawberry perl) rather
    # than the cygwin perl port. This assumes that version will be in a directory ending /perl/bin directory
    # and the cygwin version will not. Once found, that version of perl is added ahead of cygwin perl in the PATH.
    PERL:=$(shell which -a perl.exe | grep /perl/bin | sort | uniq)
    ifeq (,$(PERL))
      $(error Unable to find Windows perl e.g. Strawberry perl in a /perl/bin subdirectory on PATH.  Install perl or add to PATH and retry)
    endif
    PERL:=$(dir $(PERL))
    export PATH:=$(PERL):$(PATH)
  endif
else
  ESC:=\$(EMPTY)
  ESC_DQ:=\"
  D:=/
  P:=:
  EXE:=
  BAT:=
  TMP?=$(D)tmp
  TEMP?=$(TMP)
  CAT:=cat
  WHICH=which
  NULL=$(D)dev$(D)null
  EXPORT=export
  OS_DATE = $(shell date +%d-%m-%y)
  OS_TIME = $(shell date +%H-%M-%S)
  
endif

# Make sure that we have the correct directory delimiters for Windows vs Unix.
# On windows set double back slashes in case a shell treats backslash as an escape character.
# First set any double back slashes to a single backslash, then set single backslashes to double backslashes.
# This avoids setting any double backslashes to four backslashes.
ifeq ($(WINDOWS),1)
  RESOLVED_PREREQS_ROOT:=$(subst /,\,$(RESOLVED_PREREQS_ROOT))
  RESOLVED_PREREQS_ROOT:=$(subst \\,\,$(RESOLVED_PREREQS_ROOT))
  RESOLVED_PREREQS_ROOT:=$(subst \,\\,$(RESOLVED_PREREQS_ROOT))
endif

# To build to a different location specify OPENJ9_SYSTEMTEST_TARGET_ROOT=<a directory> on the make command line
OPENJ9_SYSTEMTEST_TARGET_ROOT:=$(OPENJ9_SYSTEMTEST_ROOT)

ANT_JAVA_HOME:=$(JAVA_HOME)
ANT_LAUNCHER=$(ANT_HOME)$(D)lib$(D)ant-launcher.jar

ifeq (,$(ANT_JAVA_HOME))
  $(warning ANT_JAVA_HOME not set, will use $(JAVA_HOME) to run ant)
endif

# See if there is a apache-ant directory present in one of the $(PREREQS_ROOT) directories.
ANT_SUBDIR:=apache-ant
ANT_DIRS:=$(foreach PREREQS_ROOT,$(ABSOLUTE_PREREQS_ROOT_LIST),$(wildcard $(PREREQS_ROOT)$(D)$(ANT_SUBDIR)))
ANT_DIR:=$(firstword $(ANT_DIRS))
#$(warning ANT_DIRS is $(ANT_DIRS))
#$(warning ANT_DIR is $(ANT_DIR))
ifeq (,$(ANT_DIR))
  $(warning No ant dir found in $(PREREQS_ROOT))
endif

ifndef ANT_HOME
  # If we haven't been told which ant to use via ANT_HOME, see if there is an ant present in one of the $(PREREQS_ROOT) directories?
  # If not, try to find ant on the path and use that one.
  ifndef ANT_DIR
    $(warning ANT_HOME not set, and apache-ant was not found in $(PREREQS_ROOT), looking for ant on the PATH)
    ANT_NOT_FOUND:=1
  else
    ANT_HOME:=$(ANT_DIR)
    ifneq (,$(wildcard $(ANT_LAUNCHER)))
      $(warning Found $(ANT_LAUNCHER))
    else
      $(warning Cannot find $(ANT_LAUNCHER), looking for ant on the PATH)
      ANT_NOT_FOUND:=1
    endif
  endif
  ifeq (1,$(ANT_NOT_FOUND))
    # On Windows run where ant.bat
    # On Unix run which ant, and use realpath to resolve symlinks (ant is likely to be symlinked e.g. to usr$(D)bin$(D)ant).
    ifeq ($(WINDOWS),1)
      ANT_BINDIR:=$(dir $(firstword $(shell $(CMD_PREFIX) $(WHICH) ant$(BAT) 2>$(NULL))))
      $(warning $(WHICH) ant$(BAT) 2>$(NULL) returned)
      $(warning $(ANT_BINDIR))
      ifneq (,$(ANT_BINDIR))
        ANT_HOME:=$(ANT_BINDIR)$(D)..
        $(warning ANT_HOME set to $(ANT_HOME))
        $(warning Found $(ANT_BINDIR), will start build with $(ANT_LAUNCHER).  Run make configure to install the required ant version 1.10.1 or follow the prereq install instructions in build$(D)build.md)
      else
        $(error Unable to locate ant to start the build. Either add ant to PATH, set ANT_HOME or follow the prereq install instructions in openjdk.build$(D)docs$(D)build$(D)build.md)
      endif
    else  # Unix
      ANT_BINDIR:=$(dir $(firstword $(shell $(WHICH) ant$(BAT) 2>$(NULL))))
      $(warning $(WHICH) ant$(BAT) 2>$(NULL) returned)
      $(warning $(ANT_BINDIR))
      ifneq (,$(ANT_BINDIR))
        ANT_BINDIR:=$(realpath $(ANT_BINDIR)$(D)ant$(BAT))
        ANT_HOME:=$(abspath $(ANT_BINDIR)$(D)..$(D)..)
        $(warning ANT_HOME set to $(ANT_HOME))
        $(warning Found $(ANT_BINDIR), will start build with $(ANT_LAUNCHER).  Run make configure to install the required ant version 1.10.1 or follow the prereq install instructions in build$(D)build.md)
      else
        $(error Unable to locate ant to start the build. Either add ant to PATH, set ANT_HOME or follow the prereq install instructions in build$(D)build.md)
      endif
    endif
  endif
endif


ifeq (1,$(ERROR))
  $(error Fix above errors and retry)
endif

$(warning JAVA_HOME is $(JAVA_HOME))
JAVA:=$(JAVA_HOME)$(D)bin$(D)java$(EXE)
JAVAC:=$(JAVA_HOME)$(D)bin$(D)javac$(EXE)
JAVA_OUTPUT:=$(shell $(CMD_PREFIX) $(JAVA) -fullversion 2>&1)
$(warning $(CMD_PREFIX) $(JAVA) -fullversion returned)
$(warning $(JAVA_OUTPUT))

BUILD_COMMAND:=$(CMD_PREFIX) $(ANT_JAVA_HOME)$(D)bin$(D)java$(EXE) -classpath $(ANT_LAUNCHER) org.apache.tools.ant.launch.Launcher -buildfile $(OPENJ9_SYSTEMTEST_TARGET_ROOT)$(D)openj9.build$(D)build.xml -Dprereqs_root="$(RESOLVED_PREREQS_ROOT)" -Dstf_root="$(STF_ROOT)" -Dopenjdk_systemtest_root="$(OPENJDK_SYSTEMTEST_ROOT)"
BUILD_COMMAND:=$(BUILD_COMMAND) -Djava_home=$(JAVA_HOME)

# Provide a means for users to pass -java-args to the STF tests
# Java arguments may contain imbedded quotes.  These need to be escaped on the STF command line.
# An IBM (test / debugging) arguments example:
# java -Xjit:counts="- - - - - - 1 1 1 1000 250 250 - - - 10000 100000 10000" -Xgcpolicy:optthruput
# requires on Windows
# stf.pl -java-args="-Xjit:counts="""- - - - - - 1 1 1 1000 250 250 - - - 10000 100000 10000""" -Xgcpolicy:optthruput"
# and on unix
# stf.pl -java-args="-Xjit:counts=\"- - - - - - 1 1 1 1000 250 250 - - - 10000 100000 10000\" -Xgcpolicy:optthruput"

ifneq (,$(JAVA_ARGS))
  JAVA_ARGS_ARG:=-java-args="$(subst ",$(ESC_DQ),$(JAVA_ARGS))"
endif

STF_COMMAND:=$(CMD_PREFIX) perl $(STF_ROOT)$(D)stf.core$(D)scripts$(D)stf.pl $(JAVA_ARGS_ARG) -test-root="$(STF_ROOT);$(OPENJDK_SYSTEMTEST_ROOT);$(OPENJ9_SYSTEMTEST_TARGET_ROOT)" -systemtest-prereqs="$(RESOLVED_PREREQS_ROOT)"

# Macros to allow the tests to run to completion with a log of what passed and failed.
# To turn on run 
# make test LOGDIR=/home/user/logs
# or similar on the command line.
# To carry on running even after failures use the make -k option
# make -k test LOGDIR=/home/user/logs

ifneq (,$(LOGDIR))
  LOG=&& echo $(OS_DATE) $(OS_TIME) $@ Passed >>$(LOGDIR)/openj9.results.log || echo $(OS_DATE) $(OS_TIME) $@ Failed >>$(LOGDIR)/openj9.results.log
endif

# Targets to run the tests
# Allow the user to exclude tests from the command line via EXCLUDE="test.target1 test.target2"
DAA_TESTS:=test.DaaLoadTest_daa1 \
test.DaaLoadTest_daa2 \
test.DaaLoadTest_daa3 \
test.DaaLoadTest_daaAll
DAA_TESTS:=$(filter-out $(EXCLUDE),$(DAA_TESTS))

GC_TESTS:=test.HeapHogLoadTest \
test.ObjectTreeLoadTest
GC_TESTS:=$(filter-out $(EXCLUDE),$(GC_TESTS))

SHARED_CLASSES_TESTS:=test.SharedClassesWorkload \
test.SharedClassesAPI \
test.SharedClassesWorkloadTest_Softmx_Increase \
test.SharedClassesWorkloadTest_Softmx_IncreaseDecrease \
test.SharedClassesWorkloadTest_Softmx_Increase_JitAot \
test.SharedClasses.SCM23.SingleCL \
test.SharedClasses.SCM23.MultiCL \
test.SharedClasses.SCM23.MultiThread \
test.SharedClasses.SCM23.MultiThreadMultiCL
SHARED_CLASSES_TESTS:=$(filter-out $(EXCLUDE),$(SHARED_CLASSES_TESTS))

JLM_TESTS:=test.TestIBMJlmLocal \
test.TestIBMJlmRemoteClassAuth \
test.TestIBMJlmRemoteClassNoAuth \
test.TestIBMJlmRemoteMemoryAuth \
test.TestIBMJlmRemoteMemoryNoAuth 
JLM_TESTS:=$(filter-out $(EXCLUDE),$(JLM_TESTS))

# Time-based load test targets
LOAD_TESTS_5m:= test.HeapHogLoadTest_5m \
test.ObjectTreeLoadTest_5m \
test.DaaLoadTest_daa1_5m \
test.DaaLoadTest_daa2_5m \
test.DaaLoadTest_daa3_5m \
test.DaaLoadTest_daaAll_5m

TEST_TARGETS:=test.list \
test.help \
$(DAA_TESTS) \
$(GC_TESTS) \
$(SHARED_CLASSES_TESTS) \
$(JLM_TESTS) \
$(LOAD_TESTS_5m)

TEST_TARGETS:=$(filter-out $(EXCLUDE),$(TEST_TARGETS))

.PHONY: build configure clean refresh_source test $(TEST_TARGETS)

# Targets to configure and build
build: refresh_source
	echo Running target $@
	$(BUILD_COMMAND) build
configure:
	echo Running target $@
	$(BUILD_COMMAND) configure
clean:
	echo Running target $@
	$(BUILD_COMMAND) clean
refresh_source:
ifneq ($(OPENJ9_SYSTEMTEST_ROOT),$(OPENJ9_SYSTEMTEST_TARGET_ROOT))
	echo Running target $@
ifeq ($(WINDOWS),1)
	xcopy /D /E /Y /I $(OPENJ9_SYSTEMTEST_ROOT) $(OPENJ9_SYSTEMTEST_TARGET_ROOT)
endif
endif

test: $(TEST_TARGETS)
test.daa: $(DAA_TESTS)
test.gc: $(GC_TESTS)
test.SharedClasses: $(SHARED_CLASSES_TESTS)
test.jlm: $(JLM_TESTS)

test.list:
	echo Running target $@
	$(STF_COMMAND) -list $(LOG)
	echo Target $@ completed
test.help:
	echo Running target $@
	$(STF_COMMAND) -test=DaaLoadTest -help $(LOG)
	echo Target $@ completed
test.DaaLoadTest_daa1:
	echo Running target $@
	$(STF_COMMAND) -test=DaaLoadTest -test-args="workload=daa1" $(LOG)
	echo Target $@ completed
test.DaaLoadTest_daa2:
	echo Running target $@
	$(STF_COMMAND) -test=DaaLoadTest -test-args="workload=daa2" $(LOG)
	echo Target $@ completed
test.DaaLoadTest_daa3:
	echo Running target $@
	$(STF_COMMAND) -test=DaaLoadTest -test-args="workload=daa3" $(LOG)
	echo Target $@ completed
test.DaaLoadTest_daaAll:
	echo Running target $@
	$(STF_COMMAND) -test=DaaLoadTest -test-args="workload=daaAll" $(LOG)
	echo Target $@ completed
test.HeapHogLoadTest:
	echo Running target $@
	$(STF_COMMAND) -test=HeapHogLoadTest -java-args="-Xdisableexcessivegc" $(LOG)
	echo Target $@ completed
test.ObjectTreeLoadTest:
	echo Running target $@
	$(STF_COMMAND) -test=ObjectTreeLoadTest -java-args="-Xnoclassgc" $(LOG)
	echo Target $@ completed
test.SharedClassesWorkload:
	echo Running target $@
	$(STF_COMMAND) -test=SharedClassesWorkload $(LOG)
	echo Target $@ completed
test.SharedClassesAPI:
	echo Running target $@
	$(STF_COMMAND) -test=SharedClassesAPI $(LOG)
	echo Target $@ completed
test.SharedClassesWorkloadTest_Softmx_Increase:
	echo Running target $@
	$(STF_COMMAND) -test=SharedClassesWorkloadTest_Softmx_Increase $(LOG)
	echo Target $@ completed
test.SharedClassesWorkloadTest_Softmx_IncreaseDecrease:
	echo Running target $@
	$(STF_COMMAND) -test=SharedClassesWorkloadTest_Softmx_IncreaseDecrease $(LOG)
	echo Target $@ completed
test.SharedClassesWorkloadTest_Softmx_Increase_JitAot:
	echo Running target $@
	$(STF_COMMAND) -test=SharedClassesWorkloadTest_Softmx_Increase_JitAot $(LOG)
	echo Target $@ completed
test.SharedClasses.SCM01.SingleCL:
	echo Running target $@
	$(STF_COMMAND) -test=SharedClasses -test-args="sharedClassMode=SCM01,sharedClassTest=SingleCL" $(LOG)
	echo Target $@ completed
test.SharedClasses.SCM23.SingleCL:
	echo Running target $@
	$(STF_COMMAND) -test=SharedClasses -test-args="sharedClassMode=SCM23,sharedClassTest=SingleCL" $(LOG)
	echo Target $@ completed
test.SharedClasses.SCM01.MultiCL:
	echo Running target $@
	$(STF_COMMAND) -test=SharedClasses -test-args="sharedClassMode=SCM01,sharedClassTest=MultiCL" $(LOG)
	echo Target $@ completed
test.SharedClasses.SCM23.MultiCL:
	echo Running target $@
	$(STF_COMMAND) -test=SharedClasses -test-args="sharedClassMode=SCM23,sharedClassTest=MultiCL" $(LOG)
	echo Target $@ completed
test.SharedClasses.SCM01.MultiThread:
	echo Running target $@
	$(STF_COMMAND) -test=SharedClasses -test-args="sharedClassMode=SCM01,sharedClassTest=MultiThread" $(LOG)
	echo Target $@ completed
test.SharedClasses.SCM23.MultiThread:
	echo Running target $@
	$(STF_COMMAND) -test=SharedClasses -test-args="sharedClassMode=SCM23,sharedClassTest=MultiThread" $(LOG)
	echo Target $@ completed
test.SharedClasses.SCM01.MultiThreadMultiCL:
	echo Running target $@
	$(STF_COMMAND) -test=SharedClasses -test-args="sharedClassMode=SCM01,sharedClassTest=MultiThreadMultiCL" $(LOG)
	echo Target $@ completed
test.SharedClasses.SCM23.MultiThreadMultiCL:
	echo Running target $@
	$(STF_COMMAND) -test=SharedClasses -test-args="sharedClassMode=SCM23,sharedClassTest=MultiThreadMultiCL" $(LOG)
	echo Target $@ completed
test.TestIBMJlmLocal:
	echo Running target $@
	$(STF_COMMAND) -test=TestIBMJlmLocal $(LOG)
	echo Target $@ completed
test.TestIBMJlmRemoteClassAuth:
	echo Running target $@
	$(STF_COMMAND) -test=TestIBMJlmRemoteClassAuth $(LOG)
	echo Target $@ completed
test.TestIBMJlmRemoteClassNoAuth:
	echo Running target $@
	$(STF_COMMAND) -test=TestIBMJlmRemoteClassNoAuth $(LOG)
	echo Target $@ completed
test.TestIBMJlmRemoteMemoryAuth:
	echo Running target $@
	$(STF_COMMAND) -test=TestIBMJlmRemoteMemoryAuth $(LOG)
	echo Target $@ completed
test.TestIBMJlmRemoteMemoryNoAuth:
	echo Running target $@
	$(STF_COMMAND) -test=TestIBMJlmRemoteMemoryNoAuth $(LOG)
	echo Target $@ completed
test.DaaLoadTest_daa1_5m:
	echo Running target $@
	$(STF_COMMAND) -test=DaaLoadTest -test-args="workload=daa1,timeLimit=5m" $(LOG)
	echo Target $@ completed
test.DaaLoadTest_daa2_5m:
	echo Running target $@
	$(STF_COMMAND) -test=DaaLoadTest -test-args="workload=daa2,timeLimit=5m" $(LOG)
	echo Target $@ completed
test.DaaLoadTest_daa3_5m:
	echo Running target $@
	$(STF_COMMAND) -test=DaaLoadTest -test-args="workload=daa3,timeLimit=5m" $(LOG)
	echo Target $@ completed
test.DaaLoadTest_daaAll_5m:
	echo Running target $@
	$(STF_COMMAND) -test=DaaLoadTest -test-args="workload=daaAll,timeLimit=5m" $(LOG)
	echo Target $@ completed
test.HeapHogLoadTest_5m:
	echo Running target $@
	$(STF_COMMAND) -test=HeapHogLoadTest -java-args="-Xdisableexcessivegc" -test-args="timeLimit=5m" $(LOG)
	echo Target $@ completed
test.ObjectTreeLoadTest_5m:
	echo Running target $@
	$(STF_COMMAND) -test=ObjectTreeLoadTest -java-args="-Xnoclassgc" -test-args="timeLimit=5m" $(LOG)
	echo Target $@ completed
	
help:
	@echo make or make build: Builds openj9-systemtest projects
	@echo make test: Runs all openj9-systemtest tests
	@echo make $(TEST_TARGETS): Runs all openj9-systemtest tests
	@echo make test.xxxx: Runs individual test xxxx
	