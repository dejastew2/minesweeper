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

import com.mineSweeper.gui.*;
import java.util.*;

// a utility class for a minefield
public abstract class MineFieldUtil {
   // random object for random placement of mines
   private static Random rand = new Random();

   // the maximum number of combinations
   private static int MAX_COMBINATIONS = 500;

   // tried unknowns - so we don't keep trying the
   //  same combination over and over
   private static HashMap triedUnknowns = new HashMap();

   // status variables
   public static final int EMPTY_SQUARE       =  0;
   public static final int MINE               = -1;
   public static final int FLAGGED_MINE       = -2;
   public static final int FLAGGED_MINE_ERROR = -3;
   public static final int UNKNOWN            = -4;// must be the last variable

   // constructs a new minefield
   public static int[][] getNewMineField() {
      int[][] mineField =
         new int[MineSweeperConfig.HEIGHT][MineSweeperConfig.WIDTH];
      MineFieldUtil.markMines(mineField);
      MineFieldUtil.markMineNumbers(mineField);

      return mineField;
   }

   // marks the mines in the minefield
   private static void markMines(int[][] mineField) {
      // make sure there's no null values
      assert(mineField != null);

      int height = mineField.length;
      int width  = mineField[0].length;

      // construct an array
      int[] randNums = new int[height * width];
      for (int i = 0; i < randNums.length; i++) {
         randNums[i] = i;
      }

      // scramble the array
      MineFieldUtil.scrambleArray(randNums);

      // mark the mines
      for (int i = 0; i < MineSweeperConfig.MINES; i++) {
         mineField[(randNums[i] / width)]
                  [(randNums[i] % width)] = MineFieldUtil.MINE;
      }
   }

   // takes an int array and rearranges all the slots randomly
   private static void scrambleArray(int[] array) {
      // make sure there's no null values
      assert(array != null);

      int temp, swapNdx;

      for (int i = 0; i < array.length; i++) {
         swapNdx = MineFieldUtil.rand.nextInt(array.length);
         temp    = array[swapNdx];
         array[swapNdx] = array[i];
         array[i] = temp;
      }
   }

   // generates the numbers next to mines
   private static void markMineNumbers(int[][] mineField) {
      // make sure there's no null values
      assert(mineField != null);

      for (int i = 0; i < mineField.length; i++) {
         for (int j = 0; j < mineField[0].length; j++) {
            if (mineField[i][j] == MineFieldUtil.MINE) { // if a bomb is found
               // mark the mine numbers
               for (int k = i - 1; k <= i + 1; k++) {
                  for (int m = j - 1; m <= j + 1; m++) {
                     if (k < 0 || k >= mineField.length ||
                         m < 0 || m >= mineField[0].length) {
                        continue;
                     }

                     if (mineField[k][m] != MineFieldUtil.MINE) {
                        mineField[k][m]++;
                     }
                  }
               }
            }
         }
      }
   }



   // determine possible minefield combinations to see whether there is only
   //  one possibility (used near the end of the game)
   public static void determinePossMineFieldCombos(MineFieldPanel mfPanel) {
      MineButton[][] mineFieldButtons = mfPanel.getMineFieldButtons();
      int mineFieldHeight = mineFieldButtons.length;
      int mineFieldWidth  = mineFieldButtons[0].length;
      int[][] mineFieldButtonsClone = new int[mineFieldHeight][mineFieldWidth];
      int[] unknowns;
      int numMinesRemaining;

      System.out.print("Trying all possibilities...");

      // clear tried unknowns hashmap
      MineFieldUtil.triedUnknowns.clear();

      // create a clone of the minebuttons
      synchronized (mineFieldButtons) {
         ArrayList unknownsArrayList = new ArrayList();

         int numFlaggedMines = 0;
         for (int i = 0; i < mineFieldHeight; i++) {
            for (int j = 0; j < mineFieldWidth; j++) {
               mineFieldButtonsClone[i][j] =
                  mineFieldButtons[i][j].getCurStatus();

               if (mineFieldButtonsClone[i][j] == MineFieldUtil.UNKNOWN) {
                  unknownsArrayList.add(
                     new Integer(mineFieldButtons[i][j].getId()));
               }
               else if (mineFieldButtonsClone[i][j] ==
                           MineFieldUtil.FLAGGED_MINE) {
                  numFlaggedMines++;
               }
            }
         }
         // calculate the mines remaining
         numMinesRemaining = MineSweeperConfig.MINES - numFlaggedMines;

         // get the unknowns into an int array (optimization)
         int numUnknowns = unknownsArrayList.size();
         unknowns = new int[numUnknowns];
         for (int q = numUnknowns - 1; q >= 0; q--) {
            unknowns[q] = ((Integer)unknownsArrayList.get(q)).intValue();
         }
      }

      TreeMap unknownsTreeMap = new TreeMap();
      for (int k = 0; k < unknowns.length; k++) {
         if (unknownsTreeMap.containsKey(new Integer(unknowns[k]))) {
            continue;
         }

         int[] possibleMineLocations =
            MineFieldUtil.tryCombos(mineFieldButtonsClone, unknowns,
                                    numMinesRemaining, mineFieldWidth, k);

         if (possibleMineLocations == null) { // cannot be a mine here
            mineFieldButtons[(unknowns[k] / mineFieldWidth)]
                            [(unknowns[k] % mineFieldWidth)].reveal();

            return;
         }
         else {
            // if it's a possible mine location, keep searching
            //  (add possible mine locations to treemap)
            for (int p = 0; p < possibleMineLocations.length; p++) {
               unknownsTreeMap.put(
                  new Integer(possibleMineLocations[p]), null);
            }
         }
      }

      System.out.println("Done");
   }

// OPTIMIZE THIS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
   public static boolean testFlaggedMineFieldValidity(int[][] mineField) {
      // see if the flagged mines violate any revealed numbers
      for (int i = 0; i < mineField.length; i++) {
         for (int j = 0; j < mineField[0].length; j++) {
            if (mineField[i][j] >= 0) { // if a number is found
               // count the number of surrounding mines
               int numSurroundingMines = 0;
               for (int k = i - 1; k <= i + 1; k++) {
                  for (int m = j - 1; m <= j + 1; m++) {
                     if (k < 0 || k >= mineField.length ||
                         m < 0 || m >= mineField[0].length) {
                        continue;
                     }

                     // test for mine, flagged mine, or flagged mine error
                     if (mineField[k][m] == MineFieldUtil.MINE ||
                         mineField[k][m] == MineFieldUtil.FLAGGED_MINE ||
                         mineField[k][m] == MineFieldUtil.FLAGGED_MINE_ERROR) {
                        numSurroundingMines++;
                     }
                  }
               }

               // test to see if there's a violation
               if (numSurroundingMines != mineField[i][j]) {
                  return false;
               }
            }
            else if (mineField[i][j] < 0) { // mine
               if (mineField[i][j] < MineFieldUtil.UNKNOWN) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   public static int[] tryCombos(int[][] mineFieldButtonsClone, int[] unknowns,
                                 int numMinesRemaining, int mineFieldWidth,
                                 int initialUnknownIndx) {
      int[] comboIndxs = new int[numMinesRemaining];
      int lastComboIndx = comboIndxs.length - 1;

      // verify data
      assert (mineFieldButtonsClone != null && unknowns != null &&
              numMinesRemaining <= unknowns.length &&
              initialUnknownIndx < unknowns.length);

      // initialize the first starting unknown
      comboIndxs[0] = initialUnknownIndx;

      // generate initial combo indexes
      int curComboIndx = 1;
      for (int i = 0; curComboIndx < numMinesRemaining; i++) {
         if (i == comboIndxs[0]) {
            i++;
         }
         comboIndxs[curComboIndx++] = i;
      }

      int[] curCombo   = new int[numMinesRemaining];
      boolean comboExists = true;
      while (comboExists) {
         // generate a combo
         for (int j = 0; j < numMinesRemaining; j++) {
            curCombo[j] = unknowns[comboIndxs[j]];
         }

         String curComboString = intArrayToString(curCombo);
         if (!MineFieldUtil.triedUnknowns.containsKey(curComboString)) {
            // add it to the unknowns hashmap
            MineFieldUtil.triedUnknowns.put(curComboString, null);

            // test current combo
            // mark the unknown squares in the minefield as flagged mines
            for (int r = 0; r < curCombo.length; r++) {
               mineFieldButtonsClone[(curCombo[r] / mineFieldWidth)]
                                    [(curCombo[r] % mineFieldWidth)]
                                    = MineFieldUtil.FLAGGED_MINE;
            }

            // test the mine field configuration
            boolean mineFieldValid =
               MineFieldUtil.testFlaggedMineFieldValidity(mineFieldButtonsClone);


            // mark the unknown squares in the minefield as flagged mines
            for (int r = 0; r < curCombo.length; r++) {
               mineFieldButtonsClone[(curCombo[r] / mineFieldWidth)]
                                    [(curCombo[r] % mineFieldWidth)]
                                    = MineFieldUtil.UNKNOWN;
            }

            // if the marked mines was a possible combination, return
            if (mineFieldValid) {
               // return the marked unknowns
               return curCombo;
            }
         }

         // generate new combo
         comboExists = false;

         // first try to increment the last marker
         int nextIndx = comboIndxs[lastComboIndx] + 1;
         if (nextIndx != comboIndxs[0] && nextIndx < unknowns.length) {
            comboIndxs[lastComboIndx]++;
            comboExists = true;
         }
         else if (nextIndx == comboIndxs[0] &&
                  (nextIndx + 1) < unknowns.length) {
            comboIndxs[lastComboIndx] += 2;
            comboExists = true;
         }
         else { // if we can't, go backwards
            for (int m = lastComboIndx - 1; m > 0; m--) {
               if (((comboIndxs[m] + 1) == comboIndxs[m + 1]) ||
                   ((comboIndxs[m] + 1) == comboIndxs[0] &&
                    (comboIndxs[m] + 2) == comboIndxs[m + 1])
                  ) {
                  continue;
               }

               comboIndxs[m] +=
                  (((comboIndxs[m] + 1) == comboIndxs[0]) ? 2 : 1);

               // then go forward
               for (int u = m + 1; u < comboIndxs.length; u++) {
                  comboIndxs[u] = comboIndxs[u - 1] + 1;
                  if (comboIndxs[u] == comboIndxs[0]) {
                     comboIndxs[u]++;
                  }
               }

               comboExists = true;
               break;
            }
         }
      }

      return null;
   }

   public static String intArrayToString(int[] array) {
      StringBuffer strBuf = new StringBuffer();

      for (int i = 0; i < array.length; i++) {
         strBuf.append(array[i] + "-");
      }

      return strBuf.toString();
   }

}