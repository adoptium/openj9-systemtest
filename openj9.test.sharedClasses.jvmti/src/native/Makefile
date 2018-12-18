#################################################################################
# Copyright (c) 2017, 2018 IBM Corp.
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
# [2] http://openjdk.java.net/legal/assembly-exception.html
#
# SPDX-License-Identifier: EPL-2.0 OR Apache-2.0 OR GPL-2.0 WITH Classpath-exception-2.0 OR LicenseRef-GPL-2.0 WITH Assembly-exception
#################################################################################

#
# Usage: gmake [target] [PLATFORM=platform] [OUTDIR=destdir] [JAVA_HOME=javadir] [SRCDIR=srcdir]
#        target -   The make target valid values for this makefile are build and clean
#                   If no target is supplied the build target is used.
#        platform - The platform to build for.  The valid values for this argument are
#                   aix_ppc-32, aix_ppc-64, linux_390-31, linux_390-64, linux_ppc-32
#                   linux_ppc-64, linux_x86-32, linux_x86-64, win_x86-32, win_x86-64
#                   zos_390-31, zos_390-64,osx_x86-64
#        outdir  - The directory where a platform directory will be created and the lib will be place
#        javadir  - The java.home property directory (ie c:\sdk\jre)
#        srcdir   - Where the source files are located
#

# Assign OUTDIR to a temporary variable so we can change forward slashes to backslashes on Windows.
# We can't change OUTDIR directly because it is supplied on the command line and changes in makefiles are therefore ignored.

_OUTDIR = $(OUTDIR)
.SUFFIXES: .c
WIN=0

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

DESTDIR=$(OUTDIR)/$(PLATFORM)
OBJDIR=$(OUTDIR)/$(PLATFORM)

D=/
P=:

###
# Check platform is set to a single valid value
###
VALID_PLATFORMS?=aix_ppc-32,aix_ppc-64,linux_390-31,linux_390-64,linux_ppc-32,linux_ppc-64,linux_x86-32,linux_x86-64,win_x86-32,win_x86-64,zos_390-31,zos_390-64,linux_ppcle-64,linux_arm-32,osx_x86-64
ifndef PLATFORM
  $(error "The variable PLATFORM needs to be defined")
endif
space:=$(space) $(space)
comma:=,
ifneq "1" "$(words $(subst $(comma), ,$(PLATFORM)))"
  $(error "The variable PLATFORM should only contain one of $(VALID_PLATFORMS)")
endif
ifneq "" "$(filter-out $(subst $(comma), ,$(VALID_PLATFORMS)),$(PLATFORM))"
  $(error "The variable PLATFORM=$(PLATFORM) is not one of $(VALID_PLATFORMS)")
endif

ifeq ($(PLATFORM),linux_ppc-64)
    CFLAGS:=-D_JNI_IMPLEMENTATION_ -D_TRIVIAL_AGENT -O0 -g3 -pedantic -c -Wall -std=c99 -fPIC -fno-omit-frame-pointer -static-libgcc -o $(OBJDIR)/sharedClasses$(OSUFFIX) -m64 -DOS64 -mcpu=powerpc64
    LFLAGS:=-m64 -shared $(LFLAGS) -o
    IFLAGS=-I. -I$(JAVA_HOME)/../include -I$(JAVA_HOME)/../include/linux -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -I/usr/include
endif

ifeq ($(PLATFORM),linux_ppcle-64)
    CFLAGS:=-D_JNI_IMPLEMENTATION_ -D_TRIVIAL_AGENT -O0 -g3 -pedantic -c -Wall -std=c99 -fPIC -fno-omit-frame-pointer -static-libgcc -o $(OBJDIR)/sharedClasses$(OSUFFIX) -m64 -DOS64 -mcpu=powerpc64
    LFLAGS:=-m64 -shared $(LFLAGS) -o
    IFLAGS=-I. -I$(JAVA_HOME)/../include -I$(JAVA_HOME)/../include/linux -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -I/usr/include
endif

ifeq ($(PLATFORM),linux_390-31)
    CFLAGS:=-D_JNI_IMPLEMENTATION_ -D_TRIVIAL_AGENT -O0 -g3 -pedantic -c -Wall -std=c99 -fPIC -fno-omit-frame-pointer -static-libgcc -o $(OBJDIR)/sharedClasses$(OSUFFIX) -m31
    LFLAGS:=-m31 -shared $(LFLAGS) -o
    IFLAGS=-I. -I$(JAVA_HOME)/../include -I$(JAVA_HOME)/../include/linux -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -I/usr/include
endif

ifeq ($(PLATFORM),linux_390-64)
    CFLAGS=-D_JNI_IMPLEMENTATION_ -D_TRIVIAL_AGENT -O0 -g3 -pedantic -c -Wall -std=c99 -fPIC -fno-omit-frame-pointer -static-libgcc -DOS64 -m64 -o $(OBJDIR)/sharedClasses$(OSUFFIX)
    LFLAGS=-m64 -shared -o
    IFLAGS=-I. -I$(JAVA_HOME)/../include -I$(JAVA_HOME)/../include/linux -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -I/usr/include
endif

ifeq ($(PLATFORM),zos_390-31)
    CC=c89
    LD=c89
    CFLAGS=-W c,exportall -D_JNI_IMPLEMENTATION -D_TRIVIAL_AGENT -W "c,langlvl(extended)" -W "c,float(ieee)" -W "c,convlit(ISO8859-1)" -W "c,xplink,dll" -W "l,xplink,dll" -DZOS -c -o $(OBJDIR)/sharedClasses$(OSUFFIX)
    LFLAGS=-W l,XPLINK,dll -o 
    IFLAGS=-I. -I$(JAVA_HOME)/../include -I$(JAVA_HOME)/../include/zos -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/zos -I/usr/include
endif

ifeq ($(PLATFORM),zos_390-64)
    CC=c89
    LD=c89
    CFLAGS=-Wc,lp64,warn64 -W c,exportall -W c,exportall -D_JNI_IMPLEMENTATION -D_TRIVIAL_AGENT -W "c,langlvl(extended)" -W "c,float(ieee)" -W "c,c,convlit(ISO8859-1)" -DZOS -c -o $(OBJDIR)/sharedClasses$(OSUFFIX)
    LFLAGS=-Wl,lp64,amode=64 -W l,XPLINK,dll -o
    IFLAGS=-I. -I$(JAVA_HOME)/../include -I$(JAVA_HOME)/../include/zos -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/zos -I/usr/include
endif

ifeq ($(PLATFORM),aix_ppc-64)
    CC=xlC
    LD=xlC
    CFLAGS=-D_JNI_IMPLEMENTATION -D_TRIVIAL_AGENT -qnooptimize -g -qlanglvl=stdc99 -q64 -c -o $(OBJDIR)/sharedClasses$(OSUFFIX)
    LFLAGS=-G -q64 -o
    IFLAGS=-I. -I$(JAVA_HOME)/../include -I$(JAVA_HOME)/../include/aix -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/aix -I/usr/include
endif

ifeq ($(PLATFORM),aix_ppc-32)
    CC=xlC
    LD=xlC
    CFLAGS=-D_JNI_IMPLEMENTATION -D_TRIVIAL_AGENT -qnooptimize -g -qlanglvl=stdc99 -q32 -c -o $(OBJDIR)/sharedClasses$(OSUFFIX)
    LFLAGS=-G -q32 -o
    IFLAGS=-I. -I$(JAVA_HOME)/../include -I$(JAVA_HOME)/../include/aix -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/aix -I/usr/include
endif

ifeq ($(PLATFORM),linux_x86-32)
    CFLAGS:=-D_JNI_IMPLEMENTATION_ -D_TRIVIAL_AGENT -O0 -g3 -pedantic -c -Wall -std=c99 -fPIC -fno-omit-frame-pointer -static-libgcc -o $(OBJDIR)/sharedClasses$(OSUFFIX) -m32
    LFLAGS:=-m32 $(LFLAGS)
    IFLAGS=-I. -I$(JAVA_HOME)/../include -I$(JAVA_HOME)/../include/linux -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -I/usr/include
endif

ifeq ($(PLATFORM),linux_x86-64)
    CFLAGS=-fPIC -fno-omit-frame-pointer -static-libgcc -O0 -g3 -pedantic -Wall -std=c99 -c -fPIC -D_JNI_IMPLEMENTATION_ -D_TRIVIAL_AGENT -DOS64 -o $(OBJDIR)/sharedClasses$(OSUFFIX)
    LFLAGS=-shared -o 
    IFLAGS=-I. -I$(JAVA_HOME)/../include -I$(JAVA_HOME)/../include/linux -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -I/usr/include
endif

ifeq ($(PLATFORM),osx_x86-64)
    CFLAGS=-fPIC -fno-omit-frame-pointer -O0 -g3 -pedantic -Wall -std=c99 -c -D_JNI_IMPLEMENTATION_ -D_TRIVIAL_AGENT -DOS64 -o $(OBJDIR)/sharedClasses$(OSUFFIX)
    LFLAGS=-shared -o
    IFLAGS=-I. -I$(JAVA_HOME)/include -I$(JAVA_HOME)/../include -I$(JAVA_HOME)/include/darwin -I$(JAVA_HOME)/../include/darwin -I/usr/include
endif

ifeq ($(PLATFORM),win_x86-32)
    WIN=1
endif

ifeq ($(PLATFORM),win_x86-64)
    WIN=1
endif
 
ifeq ($(WIN),1)

    # It is important that make runs cmd.exe on Windows rather than /usr/bin/sh
    # which would be selected by make instead if we are running under cygwin
    SHELL=cmd.exe

    DESTDIR=$(OUTDIR)\$(PLATFORM)

    # mkdir in Windows won't work if the path contains forward slashes
    _OUTDIR := $(subst /,\,$(_OUTDIR))
    DESTDIR := $(subst /,\,$(DESTDIR))
    OBJDIR := $(subst /,\,$(OBJDIR))
    # and let's escape backslashes in case one is stripped by the shell
    _OUTDIR := $(subst \,\\,$(_OUTDIR))
    DESTDIR := $(subst \,\\,$(DESTDIR))
    OBJDIR := $(subst \,\\,$(OBJDIR))

    CC=cl
    LD=link
    
    PREFIX=
    SUFFIX=.dll
    OSUFFIX=.obj
    ESUFFIX=.exe

    MV=move
    # mkdir exits non-zero on Windows if the directory already exists
    MKDIR=-mkdir
    RMDIR=rmdir
    RM=del /Q
    
    D=\\
    P=;

    # This creates a file win_x86-32.pdb or win_x86-64.pdb in the same directory as the .obj files
    CFLAGS=/DWIN32 /D_WINDOWS -Gy /LD /Zi /Odi /c /RTC1 /Fo"$(OBJDIR)/sharedClasses$(OSUFFIX)" /Fd"$(OBJDIR)"
    LFLAGS=/NOLOGO /DLL /INCREMENTAL:NO /NODEFAULTLIB:LIBCMTD /OUT:
	# Cater for JAVA_HOME being set either to the jre dir or the parent directory
    IFLAGS=/I. /I"../../../lib/native/jvmti" /I"$(JAVA_HOME)/../include" /I"$(JAVA_HOME)/../include/win32" /I"$(JAVA_HOME)/include" /I"$(JAVA_HOME)/include/win32"

endif

.PHONY: $(OUTDIR) $(DESTDIR)

build: $(_OUTDIR) $(DESTDIR) $(DESTDIR)/$(PREFIX)sharedClasses$(SUFFIX)

######################
# Build shared library
######################

$(DESTDIR)/$(PREFIX)sharedClasses$(SUFFIX) : $(OBJDIR)/sharedClasses$(OSUFFIX)
	$(LD) $(LFLAGS)$ @ $<
ifneq ($(WIN),1)
	$(CHMOD) 755 $@
endif

######################
# Build an object file
######################

$(OBJDIR)/sharedClasses$(OSUFFIX): $(SRCDIR)/sharedClasses.c
	$(CC) $(CFLAGS) $(IFLAGS) $<
ifneq ($(WIN),1)
	$(CHMOD) 755 $@
endif

$(DESTDIR):
	$(MKDIR) $(DESTDIR)
ifneq ($(WIN),1)
	$(CHMOD) 755 $(DESTDIR)
endif

$(_OUTDIR):
	$(MKDIR) $(_OUTDIR)
ifneq ($(WIN),1)
	$(CHMOD) 755 $(_OUTDIR)
endif
