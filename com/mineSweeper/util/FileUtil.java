package com.mineSweeper.util;

/*
MineSweeper Version 1.0
Copyright (C) 2004, Hafeez Jaffer

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
*/


import java.util.*;
import java.io.*;

// contains the file i/o methods to read/save values to files
public abstract class FileUtil {

   // pass this method a string and it will return the int value
   //  i.e. myVariable=3 returns 3
   public static int getValueInt(String value) {
      return Integer.parseInt(
         value.substring(
            value.indexOf('=') + 1, value.length()));
   }

   // pass this method a string and it will return the String value
   //  i.e. myVariable="Hafeez" returns "Hafeez"
   public static String getValueString(String value) {
      return value.substring(
               value.indexOf('=') + 1, value.length());
   }

   // reads the MineSweeper configuration from the config file
   public static boolean setMineSweeperConfiguration(File fileName) {
      // make sure there are no null values
      assert(fileName != null);

      BufferedReader in = null;

      try {
         // read variables
         in = new BufferedReader(new FileReader(fileName));
      }
      catch (Exception e) {
         e.printStackTrace(System.out);
      }
      try {
         String curLine = in.readLine();
         // Read the lines in
         while (curLine != null) {
            if (curLine.trim().startsWith("Difficulty")) {
               MineSweeperConfig.DIFFICULTY = FileUtil.getValueInt(curLine);
            }
            else if (curLine.trim().startsWith("Height")) {
               MineSweeperConfig.HEIGHT = FileUtil.getValueInt(curLine);
            }
            else if (curLine.trim().startsWith("Width")) {
               MineSweeperConfig.WIDTH = FileUtil.getValueInt(curLine);
            }
            else if (curLine.trim().startsWith("Mines")) {
               MineSweeperConfig.MINES = FileUtil.getValueInt(curLine);
            }
            else if (curLine.trim().startsWith("Mark")) {
               MineSweeperConfig.MARK = FileUtil.getValueInt(curLine);
            }
            else if (curLine.trim().startsWith("Color")) {
               MineSweeperConfig.COLOR = FileUtil.getValueInt(curLine);
            }
            else if (curLine.trim().startsWith("Xpos")) {
               MineSweeperConfig.X_POS = FileUtil.getValueInt(curLine);
            }
            else if (curLine.trim().startsWith("Ypos")) {
               MineSweeperConfig.Y_POS = FileUtil.getValueInt(curLine);
            }
            else if (curLine.trim().startsWith("Time1")) {
               MineSweeperConfig.TIME1 = FileUtil.getValueInt(curLine);
            }
            else if (curLine.trim().startsWith("Time2")) {
               MineSweeperConfig.TIME2 = FileUtil.getValueInt(curLine);
            }
            else if (curLine.trim().startsWith("Time3")) {
               MineSweeperConfig.TIME3 = FileUtil.getValueInt(curLine);
            }
            else if (curLine.trim().startsWith("Name1")) {
               MineSweeperConfig.NAME1 = FileUtil.getValueString(curLine);
            }
            else if (curLine.trim().startsWith("Name2")) {
               MineSweeperConfig.NAME2 = FileUtil.getValueString(curLine);
            }
            else if (curLine.trim().startsWith("Name3")) {
               MineSweeperConfig.NAME3 = FileUtil.getValueString(curLine);
            }

            // read the next line
            curLine = in.readLine();
         }
         // close the file
         in.close();
         return true;
      }
      catch (Exception e1) {
         e1.printStackTrace(System.out);

         // close the file
         try {
            in.close();
         }
         catch (Exception e2) {
            e2.printStackTrace(System.out);
         }
      }

      return false;
   }

   // saves the MineSweeper configuration to a file
   public static boolean saveMineSweeperConfiguration(File fileName) {
      // make sure there are no null values
      assert(fileName != null);

      PrintWriter pos = null;

      // write this information to the file
      try {
         pos = new PrintWriter(new FileOutputStream(fileName, false), true);
      }
      catch (Exception e) {
         e.printStackTrace(System.out);
      }
      try {
         // write values
         pos.println("[Minesweeper]");

         pos.println("Difficulty=" + MineSweeperConfig.DIFFICULTY);
         pos.println("Height=" + MineSweeperConfig.HEIGHT);
         pos.println("Width=" + MineSweeperConfig.WIDTH);
         pos.println("Mines=" + MineSweeperConfig.MINES);
         pos.println("Mark=" + MineSweeperConfig.MARK);
         pos.println("Color=" + MineSweeperConfig.COLOR);
         pos.println("Xpos=" + MineSweeperConfig.X_POS);
         pos.println("Ypos=" + MineSweeperConfig.Y_POS);
         pos.println("Time1=" + MineSweeperConfig.TIME1);
         pos.println("Time2=" + MineSweeperConfig.TIME2);
         pos.println("Time3=" + MineSweeperConfig.TIME3);
         pos.println("Name1=" + MineSweeperConfig.NAME1);
         pos.println("Name2=" + MineSweeperConfig.NAME2);
         pos.println("Name3=" + MineSweeperConfig.NAME3);

         // close the file
         pos.close();
         return true;
      }
      catch (Exception e1) {
         e1.printStackTrace(System.out);

         // close the file
         try {
            pos.close();
         }
         catch (Exception e2) {
            e2.printStackTrace(System.out);
         }
      }
      return false;
   }

   // opens a saved MineSweeper field
   public static int[][] openMineField(File fileName) {
      // make sure there are no null values
      assert(fileName != null);

      BufferedReader in = null;

      try {
         // read variable
         in = new BufferedReader(new FileReader(fileName));
      }
      catch (Exception e) {
         e.printStackTrace(System.out);
      }
      try {
         String curLine = in.readLine();
         ArrayList mineFieldCol = new ArrayList();

         // read the lines in
         while (curLine != null) {
            // read the current minefield row
            StringTokenizer st = new StringTokenizer(curLine);

            ArrayList mineFieldRow = new ArrayList();
            while (st.hasMoreTokens()) {
                mineFieldRow.add(st.nextToken());
            }
            // don't count returns at the end of the file
            if (mineFieldRow.size() > 0) {
               mineFieldCol.add(mineFieldRow);
            }

            // read the next line
            curLine = in.readLine();
         }

         // close the file
         in.close();

         // construct the mine field
         int mineFieldHeight = mineFieldCol.size();
         int mineFieldWidth  = ((ArrayList)mineFieldCol.get(0)).size();
         int[][] mineField = new int[mineFieldHeight][mineFieldWidth];

         int numMines = 0;
         for (int i = 0; i < mineFieldHeight; i++) {
            // get the row
            ArrayList curMinefieldRow = (ArrayList)mineFieldCol.get(i);
            if (curMinefieldRow.size() != mineFieldWidth) {
               return null;
            }
            for (int j = 0; j < mineFieldWidth; j++) {
               mineField[i][j] =
                  (Integer.parseInt((String)curMinefieldRow.get(j)));
               // increment mine count if mine is found
               if (mineField[i][j] == MineFieldUtil.MINE) {
                  numMines++;
               }
            }
         }

         // test to make sure that the mine field is valid
         if (MineFieldUtil.testFlaggedMineFieldValidity(mineField)) {
            MineSweeperConfig.DIFFICULTY = MineSweeperConfig.CUSTOM;
            MineSweeperConfig.HEIGHT     = mineFieldHeight;
            MineSweeperConfig.WIDTH      = mineFieldWidth;
            MineSweeperConfig.MINES      = numMines;

            return mineField;
         }
      }
      catch (Exception e1) {
         e1.printStackTrace(System.out);

         // close the file
         try {
            in.close();
         }
         catch (Exception e2) {
            e2.printStackTrace(System.out);
         }
      }

      return null;
   }

   // saves a MineSweeper field
   public static boolean saveMineField(File fileName, String mineField) {
      // make sure there are no null values
      assert(fileName != null);

      PrintWriter pos = null;

      // write this information to the file
      try {
         pos = new PrintWriter(new FileOutputStream(fileName, false), true);
      }
      catch (Exception e) {
         e.printStackTrace(System.out);
      }
      try { // write values
         // write the minefield
         pos.println(mineField);

         // close the file
         pos.close();

         return true;
      }
      catch (Exception e1) {
         e1.printStackTrace(System.out);

         // close the file
         try {
            pos.close();
         }
         catch (Exception e2) {
            e2.printStackTrace(System.out);
         }
      }
      return false;
   }

}