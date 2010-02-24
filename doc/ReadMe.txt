MineSweeper Version 1.0 ReadMe File
Copyright (C) 2004, Hafeez Jaffer

License Information:
   This program is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License
   as published by the Free Software Foundation; either version 2
   of the License, or any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

About the author:
   Hafeez Jaffer is currently a computer science masters student in Cal Poly State University, San Luis Obispo.

Description:
   This program is an implementation of MineSweeper, a game created by Robert Donner and Curt Johnson. It ships with every version of Microsoft (TM) Windows operating system. While the original game was programmed in C++, this version is developed in JAVA and has a Rete engine to determine which squares are mines.

Files and Directories:
   .source                  - contains a list of all the source code files (for compiling)
   ./build                  - the location for the compiled java files
   ./com                    - the source code for MineSweeper
   ./config/minesweeper.cfg - the MineSweeper configuration
   ./doc/License.txt        - the license information regarding this program
   ./doc/ReadMe.txt         - the ReadMe file
   ./images                 - the icons and images used by MineSweeper
   ./jess/minesweeper.clp   - the Jess file loaded by MineSweeper (contains all the rules to determine mine locations)
   ./testCases              - test mine fields used to test MineSweeper
   ./testCasePics           - test mine field pictures

Compilation:
   External dependencies:
   This program requires the Jess language component to be installed. The jar file can be downloaded from the following location: http://herzberg.ca.sandia.gov/jess/
   Once downloaded, the jar file must be included in the CLASSPATH environment variable. Next, just execute the compile.bat file or type:
   "%JAVA_HOME%\bin\javac -source 1.4 -d build @source"
   Note: there must exist a JAVA_HOME variable that points to the installed JAVA directory
   
Operating Systems:
   This program has been tested on the following operating systems:
      - Windows 95
      - Windows 98
      - Windows NT 4.0
      - Windows 2000
      
   * This program does not visually look good with WindowsXP. The reason is because the button theme for XP has been changed. Therefore, the following argument must be used with the JAVA command: -Dswing.noxp=true

Execution:
   Simply run the run.bat file or type:
   "%JAVA_HOME%\bin\java -Dswing.noxp=true -enableassertions StartMinesweeper"