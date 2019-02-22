Neverball Portable Launcher
=================================
Copyright 2004-2007 John T. Haller
Copyright 2008 Travis Carrico

Website: http://PortableApps.com/NeverballPortable

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.


ABOUT Neverball PORTABLE
==========================
The Neverball Portable Launcher allows you to run Neverball from a removable drive whose letter changes as you move it to another computer.  The program can be entirely self-contained on the drive and then used on any Windows computer.


LICENSE
=======
This code is released under the GPL.  The full code is included with this package as NeverballPortable.nsi.


INSTALLATION / DIRECTORY STRUCTURE
==================================
By default, the program expects the following directory structure:

-\ <--- Directory with NeverballPortable.exe
	+\App\
		+\Neverball\
	+\Data\
		+\settings\


It can be used in other directory configurations by including the NeverballPortable.ini file in the same directory as NeverballPortable.exe and configuring it as details in the INI file section below.


NeverballPortable.INI CONFIGURATION
====================================
The Neverball Portable Launcher will look for an ini file called NeverballPortable.ini (read the previous section for details on placement).  If you are happy with the default options, it is not necessary, though.  The INI file is formatted as follows:

[NeverballPortable]
NeverballDirectory=App\Neverball
NeverballExecutable=Neverball.exe
AdditionalParameters=
DisableSplashScreen=false

The NeverballDirectory entry should be set to the *relative* path to the appropriate directories from the current directory.  All must be a subdirectory (or multiple subdirectories) of the directory containing NeverballPortable.exe.  The default entries for these are described in the installation section above.

The NeverballExecutable entry allows you to set the Neverball Portable Launcher to use an alternate EXE call to launch Neverball.  This is helpful if you are using a machine that is set to deny Neverball.exe from running.  You'll need to rename the Neverball.exe file and then enter the name you gave it on the NeverballExecutable= line of the INI.

The AdditionalParameters entry allows you to pass additional commandline parameter entries to the executable.  Whatever you enter here will be appended to the call to the exe.

The DisableSplashScreen entry allows you to run the Launcher without the splash screen showing up.  The default is false.




PROGRAM HISTORY / ABOUT THE AUTHORS
===================================
This launcher is loosely based on the Firefox Portable launcher and contains some ideas suggested by mai9 and tracon from the mozillaZine forums.