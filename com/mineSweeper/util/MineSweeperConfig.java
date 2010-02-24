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


// keeps track of the MineSweeper configuration
public abstract class MineSweeperConfig {
   public static final String[] LEVEL_STRINGS =
      {"beginner", "intermediate", "expert"};

   public static final int BEGINNER     = 0;
   public static final int INTERMEDIATE = 1;
   public static final int EXPERT       = 2;
   public static final int CUSTOM       = 3;

   public static int DIFFICULTY = MineSweeperConfig.BEGINNER;
   public static int HEIGHT     = 8;
   public static int WIDTH      = 8;
   public static int MINES      = 10;
   public static int MARK       = -1;
   public static int COLOR      = -1;
   public static int X_POS      = -1;
   public static int Y_POS      = -1;
   public static int TIME1      = 999;
   public static int TIME2      = 999;
   public static int TIME3      = 999;
   public static String NAME1   = "Anonymous";
   public static String NAME2   = "Anonymous";
   public static String NAME3   = "Anonymous";

   // resets configuration based on the setup type
   //  (i.e. BEGINNER, INTERMEDIATE, EXPERT)
   public static void setDefaultSetup(int setup) {
      if (setup == MineSweeperConfig.BEGINNER) {
         MineSweeperConfig.DIFFICULTY = MineSweeperConfig.BEGINNER;
         MineSweeperConfig.HEIGHT     = 8;
         MineSweeperConfig.WIDTH      = 8;
         MineSweeperConfig.MINES      = 10;
      }
      else if (setup == MineSweeperConfig.INTERMEDIATE) {
         MineSweeperConfig.DIFFICULTY = MineSweeperConfig.INTERMEDIATE;
         MineSweeperConfig.HEIGHT = 16;
         MineSweeperConfig.WIDTH  = 16;
         MineSweeperConfig.MINES  = 40;
      }
      else if (setup == MineSweeperConfig.EXPERT) {
         MineSweeperConfig.DIFFICULTY = MineSweeperConfig.EXPERT;
         MineSweeperConfig.HEIGHT = 16;
         MineSweeperConfig.WIDTH  = 30;
         MineSweeperConfig.MINES  = 99;
      }
   }

   // sets a custom setup
   public static void setCustomSetup(int theHeight, int theWidth,
                                     int theMines) {
      MineSweeperConfig.DIFFICULTY = MineSweeperConfig.CUSTOM;
      MineSweeperConfig.HEIGHT = theHeight;
      MineSweeperConfig.WIDTH  = theWidth;
      MineSweeperConfig.MINES  = theMines;
   }

   // determines whether the passed in time
   //  has beaten the current fastest times record
   public static boolean isFastestTime(int time) {
      if (MineSweeperConfig.DIFFICULTY == MineSweeperConfig.BEGINNER) {
         if (time < MineSweeperConfig.TIME1) {
            return true;
         }
      }
      else if (MineSweeperConfig.DIFFICULTY == MineSweeperConfig.INTERMEDIATE) {
         if (time < MineSweeperConfig.TIME2) {
            return true;
         }
      }
      else if (MineSweeperConfig.DIFFICULTY == MineSweeperConfig.EXPERT) {
         if (time < MineSweeperConfig.TIME3) {
            return true;
         }
      }

      return false;
   }

   // sets the fastest time
   public static void setFastestTime(int time, String name) {
      // make sure there's no null values
      assert(name != null);

      if (MineSweeperConfig.DIFFICULTY == MineSweeperConfig.BEGINNER) {
         MineSweeperConfig.TIME1 = time;
         MineSweeperConfig.NAME1 = name;
      }
      else if (MineSweeperConfig.DIFFICULTY == MineSweeperConfig.INTERMEDIATE) {
         MineSweeperConfig.TIME2 = time;
         MineSweeperConfig.NAME2 = name;
      }
      else if (MineSweeperConfig.DIFFICULTY == MineSweeperConfig.EXPERT) {
         MineSweeperConfig.TIME3 = time;
         MineSweeperConfig.NAME3 = name;

      }
   }

   // resets the fastest times data
   public static void resetScores() {
      MineSweeperConfig.TIME1 = 999;
      MineSweeperConfig.TIME2 = 999;
      MineSweeperConfig.TIME3 = 999;
      MineSweeperConfig.NAME1 = "Anonymous";
      MineSweeperConfig.NAME2 = "Anonymous";
      MineSweeperConfig.NAME3 = "Anonymous";
   }

}