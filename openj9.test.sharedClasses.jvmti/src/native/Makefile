#################################################################################
# Copyright (c) 2017, 2024 IBM Corp.
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
# SPDX-License-Identifier: EPL-2.0 OR Apache-2.0 OR GPL-2.0-only WITH Classpath-exception-2.0 OR GPL-2.0-only WITH OpenJDK-assembly-exception-1.0
#################################################################################

#
# Usage: gmake [target] [OUTDIR=destdir] [JAVA_HOME=javadir] [SRCDIR=srcdir]
#        target -   The make target valid values for this makefile are build and clean.
#                   If no target is supplied the build target is used.
#        platform - The platform to build for.  The valid values for this argument are
#                   aix_ppc-32, aix_ppc-64, linux_390-32, linux_390-64, linux_ppc-32
#                   linux_ppc-64, linux_x86-32, linux_x86-64, win_x86-32, win_x86-64
#                   zos_390-32, zos_390-64, osx_x86-64.
#        outdir  - The directory where a platform directory will be created and the lib will be placed.
#        javadir  - The java.home property directory (ie c:\sdk\jre).
#        srcdir   - Where the source files are located.
#

# Assign OUTDIR, JAVA_HOME and SRCDIR to temporary variables so we can change to forward slashes or backslashes
# as required on Windows.
# We can't change the variables directly because they may be supplied on the command line and changes in makefiles
# would therefore be ignored.

###
# Figure out current platform.
###
OS:=$(shell uname -s | tr "[:upper:]" "[:lower:]")

_OUTDIR:=$(OUTDIR)
_JAVA_HOME:=$(JAVA_HOME)
_SRCDIR:=$(SRCDIR)

.SUFFIXES: .c

CC=gcc
LD=gcc

PREFIX=lib
SUFFIX=.so
OSUFFIX=.o

MV=mv
MKDIR=mkdir -p
RMDIR=rm -rf
RM=rm -rf
CHMOD=-chmod
D=/
P=:

ifneq (,$(findstring cygwin,$(OS)))
    OS:=win
endif

ifneq (,$(findstring darwin,$(OS)))
    OS:=osx
endif

ifeq ($(OS),os/390)
    OS:=zos
endif

ifneq (,$(findstring win,$(OS)))
    OS:=win
endif

$(info OS is $(OS))

DESTDIR=$(OUTDIR)/$(OS)
OBJDIR=$(OUTDIR)/$(OS)

# AIX requires bitmode value to be passed into compiler flags.
ifeq ($(OS),aix)
    BitMode:=$(shell getconf KERNEL_BITMODE)
endif

CFLAGS:=-D_JNI_IMPLEMENTATION_ -D_TRIVIAL_AGENT -O0 -g3 -pedantic -c -Wall -std=c99 -fPIC -fno-omit-frame-pointer -static-libgcc -o $(OBJDIR)/sharedClasses$(OSUFFIX)
LFLAGS:=-shared $(LFLAGS) -o
IFLAGS=-I. -I$(JAVA_HOME)/../include -I$(JAVA_HOME)/../include/linux -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -I/usr/include

ifeq ($(OS),zos)
    CC=c89
    LD=c89
    CFLAGS=-W c,exportall -D_JNI_IMPLEMENTATION -D_TRIVIAL_AGENT -W "c,langlvl(extended)" -W "c,float(ieee)" -W "c,convlit(ISO8859-1)" -W "c,xplink,dll" -W "l,xplink,dll" -DZOS -Wc,"xplink,rostring,FLOAT(IEEE,FOLD,AFP),enum(4)" -Wc,lp64 -c -o $(OBJDIR)/sharedClasses$(OSUFFIX)
    LFLAGS=-Wl,xplink,dll -Wl,lp64 -o
    IFLAGS=-I. -I$(JAVA_HOME)/../include -I$(JAVA_HOME)/../include/zos -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/zos -I/usr/include
endif

ifeq ($(OS),aix)
    CC=xlC
    LD=xlC
    CFLAGS=-D_JNI_IMPLEMENTATION -D_TRIVIAL_AGENT -qnooptimize -g -qlanglvl=stdc99 -q$(BitMode) -c -o $(OBJDIR)/sharedClasses$(OSUFFIX)
    LFLAGS=-G -q$(BitMode) -o
    IFLAGS=-I. -I$(JAVA_HOME)/../include -I$(JAVA_HOME)/../include/aix -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/aix -I/usr/include
endif

ifeq ($(OS),osx)
    SUFFIX=.dylib
    CFLAGS=-fPIC -fno-omit-frame-pointer -O0 -g3 -pedantic -Wall -std=c99 -c -D_JNI_IMPLEMENTATION_ -D_TRIVIAL_AGENT -DOS64 -o $(OBJDIR)/sharedClasses$(OSUFFIX)
    LFLAGS=-shared -o
    IFLAGS=-I. -I$(JAVA_HOME)/include -I$(JAVA_HOME)/../include -I$(JAVA_HOME)/include/darwin -I$(JAVA_HOME)/../include/darwin -I/usr/include
endif

ifeq ($(OS),win)
    # Environment variable OSTYPE is set to cygwin if running under cygwin.
    # Set our own macro to indicate we're running under cygwin.
    # Also Windows commands will need to be prefixed with cmd /c to run them in a Windows shell
    ifndef CYGWIN
      OSTYPE?=$(shell echo $$OSTYPE)
      ifeq ($(OSTYPE),cygwin)
        CYGWIN:=1
      else
        CYGWIN:=0
      endif
    endif

    ifeq (0,$(CYGWIN))
      # mkdir in Windows won't work if the path contains forward slashes
      _OUTDIR := $(subst /,\,$(_OUTDIR))
      _SRCDIR := $(subst /,\,$(_SRCDIR))
      DESTDIR := $(subst /,\,$(DESTDIR))
      OBJDIR := $(subst /,\,$(OBJDIR))
      # and let's escape backslashes in case one is stripped by the shell
      _OUTDIR := $(subst /,\,$(_OUTDIR))
      _SRCDIR := $(subst /,\,$(_SRCDIR))
      DESTDIR := $(subst \,\\,$(DESTDIR))
      OBJDIR := $(subst \,\\,$(OBJDIR))
      MV:=move
      # mkdir exits non-zero on Windows if the directory already exists
      MKDIR:=-mkdir
      RMDIR:=rmdir
      RM:=del /Q
      D:=\\
      P:=;
    else
      # cygwin works with forward slashes
      _OUTDIR := $(subst \,/,$(_OUTDIR))
      _SRCDIR := $(subst \,/,$(_SRCDIR))
      _JAVA_HOME := $(subst \,/,$(_JAVA_HOME))
      DESTDIR := $(subst \,/,$(DESTDIR))
      OBJDIR := $(subst \,/,$(OBJDIR))
      # Prefix Windows commands with cmd /c to run them in a Windows shell
      CMD_PREFIX:=cmd /c
    endif

    CC=cl
    LD=link

    PREFIX=
    SUFFIX=.dll
    OSUFFIX=.obj
    ESUFFIX=.exe

    CFLAGS=/DWIN32 /D_WINDOWS -Gy /LD /Zi /Odi /c /RTC1 /Fo"$(OBJDIR)/sharedClasses$(OSUFFIX)" /Fd"$(OBJDIR)"
    LFLAGS=/NOLOGO /DLL /INCREMENTAL:NO /NODEFAULTLIB:LIBCMTD /OUT:
    # Cater for JAVA_HOME being set either to the jre dir or the parent directory.
    IFLAGS=/I. /I"../../../lib/native/jvmti" /I"$(_JAVA_HOME)/../include" /I"$(_JAVA_HOME)/../include/win32" /I"$(_JAVA_HOME)/include" /I"$(_JAVA_HOME)/include/win32"
endif

build: $(_OUTDIR) $(DESTDIR) $(DESTDIR)/$(PREFIX)sharedClasses$(SUFFIX)

#######################
# Build shared library.
#######################

$(DESTDIR)/$(PREFIX)sharedClasses$(SUFFIX) : $(OBJDIR)/sharedClasses$(OSUFFIX)
	$(CMD_PREFIX) $(LD) $(LFLAGS)$@ $<
ifneq ($(OS), win)
	$(CHMOD) 755 $@
endif

#######################
# Build an object file.
#######################

$(OBJDIR)/sharedClasses$(OSUFFIX): $(_SRCDIR)/sharedClasses.c
	$(CMD_PREFIX) $(CC) $(CFLAGS) $(IFLAGS) $<
ifneq ($(OS),win)
	$(CHMOD) 755 $@
endif

$(DESTDIR):
	$(MKDIR) $(DESTDIR)
ifneq ($(OS),win)
	$(CHMOD) 755 $(DESTDIR)
endif

$(_OUTDIR):
	$(MKDIR) $(_OUTDIR)
ifneq ($(OS),win)
	$(CHMOD) 755 $(_OUTDIR)
endif
