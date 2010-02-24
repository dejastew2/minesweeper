package com.mineSweeper.gui;

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

import com.mineSweeper.util.*;
import jess.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;

public class MineButton extends JButton {
   // number mine squares known
   public static int MINE_SQUARES_KNOWN = 0;
   // the number mine squares known fact
   public static Fact numMineSquaresKnownFact;

   // the unique id of the button
   private int id;

   // button row
   private int row;

   // button column
   private int col;

   // button status
   private int status;

   // the button's current status (initially unknown, status when revealed)
   private int curStatus;

   // the surrounding unknowns
   private ValueVector surroundingUnknowns;

   // all surrounding mine buttons
   private MineButton[] surroundingMineButtons;

   // the number of surrounding flags
   private int numSurroundingFlags;

   // a reference to the minefield
   private MineButton[][] mineFieldButtons;

   // the rete engine
   private Rete rete;

   // the context of the rete engine
   private Context reteContext;

   // the minebutton fact
   private Fact mineButtonFact;

   // a reference to the mine field panel
   private MineFieldPanel mfPanel;

   // the colors for the numbers
   private static Color BLUE       = new Color(0, 0, 255);
   private static Color DARK_GREEN = new Color(0, 128, 0);
   private static Color RED        = new Color(255, 0, 0);
   private static Color PURPLE     = new Color(128, 0, 128);
   private static Color BROWN      = new Color(128, 0, 0);
   private static Color CYAN       = new Color(0, 128, 128);
   private static Color BLACK      = new Color(0, 0, 0);
   private static Color GRAY       = new Color(128, 128, 128);

   public static Color[] NUMBER_COLORS =
      {MineButton.BLUE,  MineButton.DARK_GREEN, MineButton.RED,
      MineButton.PURPLE,  MineButton.BROWN, MineButton.CYAN,
      MineButton.BLACK,  MineButton.GRAY};

   // the icons for the mine buttons
   private static ImageIcon SCALED_MINE_IMAGE =
      new ImageIcon(ImageUtil.MINE_IMAGE);
   private static ImageIcon SCALED_FLAG_IMAGE =
      new ImageIcon(ImageUtil.FLAG_IMAGE);

   private MineButton() {
   }

   // constructs a new mine button
   public MineButton(MineFieldPanel theMfPanel, Rete theRete,
                     MineButton[][] theMineFieldButtons,
                     int theRow, int theCol) {
      // make sure there's no null values
      assert(theMfPanel != null && theRete != null &&
             theMineFieldButtons!= null);

      this.id   = MineSweeperConfig.WIDTH*theRow + theCol;
      this.col  = theCol;
      this.row  = theRow;
      this.numSurroundingFlags = 0;
      this.rete = theRete;
      this.reteContext = this.rete.getGlobalContext();
      this.mineFieldButtons = theMineFieldButtons;
      this.setBorder(BorderFactory.createRaisedBevelBorder());
      this.setFocusPainted(false);
      this.setPreferredSize(new Dimension(16, 16));
      this.mfPanel = theMfPanel;
      this.curStatus = MineFieldUtil.UNKNOWN;
      try {
         this.mineButtonFact = new Fact("MineSquare", this.rete);
         this.mineButtonFact.setSlotValue("id",  new Value(this.id, RU.INTEGER));
         this.mineButtonFact.setSlotValue("row", new Value(this.row, RU.INTEGER));
         this.mineButtonFact.setSlotValue("col", new Value(this.col, RU.INTEGER));
         this.rete.assertFact(this.mineButtonFact);
      }
      catch (Exception e) {
         e.printStackTrace(System.out);
      }
   }

   // after constructed, must call initialize.
   //  This calculates the surrounding unknowns and mine buttons
   public void initialize() {
      this.surroundingUnknowns = new ValueVector();

      ArrayList surroundingMineButtonsVector = new ArrayList();

      for (int i = this.row - 1; i <= this.row + 1; i++) {
         for (int j = this.col - 1; j <= this.col + 1; j++) {
            if (i < 0 || i >= this.mineFieldButtons.length ||
                j < 0 || j >= this.mineFieldButtons[0].length ||
                (i == this.row && j == this.col)) {
               continue;
            }
            surroundingMineButtonsVector.add(this.mineFieldButtons[i][j]);
            try {
               this.surroundingUnknowns.add(
                  new Value(this.mineFieldButtons[i][j].getId(),
                            RU.INTEGER));
            }
            catch (Exception e) {
               e.printStackTrace(System.out);
            }
         }
      }
      this.surroundingMineButtons =
         (MineButton[])surroundingMineButtonsVector.toArray(
            new MineButton[surroundingMineButtonsVector.size()]);

      // update the mine button
      this.updateReteEngine();
   }

   // updates the minebutton fact in the rete engine
   public void updateReteEngine() {
      if (MineSweeperFrame.USE_RETE_ENGINE) {
         try {
            // retract the fact
            this.rete.retract(this.mineButtonFact);

            // set the appropriate slot values
            this.mineButtonFact.setSlotValue("status",
               new Value(this.curStatus, RU.INTEGER));
            this.mineButtonFact.setSlotValue("surroundingUnknowns",
               new Value((ValueVector)this.surroundingUnknowns.clone(),
               RU.LIST));
            this.mineButtonFact.setSlotValue("numSurroundingFlags",
               new Value(this.numSurroundingFlags, RU.INTEGER));

            // assert the fact
            this.rete.assertFact(this.mineButtonFact);
         }
         catch (Exception e) {
            e.printStackTrace(System.out);
         }
      }
   }

   // resets the button
   public void resetButton(int theStatus) {
      // reset all variables
      this.setStatus(theStatus);
      this.curStatus = MineFieldUtil.UNKNOWN;
      this.numSurroundingFlags = 0;

      // initialize the button
      this.initialize();

      // update the mine button image
      this.updateMineButton();
   }

   // sets the current status of the button
   public synchronized void setCurStatus(int theCurStatus) {
      int prevStatus = this.curStatus;

      // update the button current status
      this.curStatus = theCurStatus;

      // increase the number of mine squares known if neccessary
      if (this.curStatus >= 0) {
         // increase the number of mine squares known
         MineButton.MINE_SQUARES_KNOWN++;

         if (MineButton.MINE_SQUARES_KNOWN ==
            (this.mineFieldButtons.length * this.mineFieldButtons[0].length)
             - MineSweeperConfig.MINES) {
            this.mfPanel.setGameOver(0, 0, true);
         }
      }

      // update the surroundingFlag and surroundingUnknownCount
      this.updateFlagCountAndUnknowns(prevStatus);

      // update the rete engine fact
      this.updateReteEngine();

      // update the mine button image
      this.updateMineButton();
   }

   // returns the current status of the button
   public synchronized int getCurStatus() {
      return this.curStatus;
   }

   // returns the status of the button
   public int getStatus() {
      return this.status;
   }

   // sets the status of the button
   public void setStatus(int theStatus) {
      this.status = theStatus;
   }

   // returns the button column
   public int getCol() {
      return this.col;
   }

   // returns the button row
   public int getRow() {
      return this.row;
   }

   // returns the button id
   public int getId() {
      return this.id;
   }

   // returns the button fact
   public Fact getMineButtonFact() {
      return this.mineButtonFact;
   }

   // returns the number of surrounding flags
   public int getNumSurroundingFlags() {
      return this.numSurroundingFlags;
   }

   // returns the surrounding unknowns
   public ValueVector getSurroundingUnknowns() {
      return this.surroundingUnknowns;
   }

   // returns all the surrounding mine buttons
   public MineButton[] getSurroundingMineButtons() {
      return this.surroundingMineButtons;
   }

   // updates the flag and unknown count
   private void updateFlagCountAndUnknowns(int prevStatus) {
      int curButtonStatus;
      int numUnknowns = 0;
      int numFlags    = 0;

      for (int i = 0; i < this.surroundingMineButtons.length; i++) {

         curButtonStatus = this.surroundingMineButtons[i].getCurStatus();

         if (prevStatus == MineFieldUtil.UNKNOWN && // UNKNOWN -> FLAG
             this.curStatus == MineFieldUtil.FLAGGED_MINE) {
            this.surroundingMineButtons[i].modifyNumSurroudingFlags(
               true, this.id);
         }
         else if (prevStatus == MineFieldUtil.FLAGGED_MINE && // FLAG -> UNKNOWN
                  this.curStatus == MineFieldUtil.UNKNOWN) {
            this.surroundingMineButtons[i].modifyNumSurroudingFlags(
               false, this.id);
         }
         else if (prevStatus == MineFieldUtil.UNKNOWN && // UNKNOWN -> number
            this.curStatus >= 0) {
            this.surroundingMineButtons[i].modifySurroundingUnknowns(
               this.id, false);
         }
         else { // either a mine or reset the button
         }

         if (curButtonStatus == MineFieldUtil.UNKNOWN) {
            numUnknowns++;
         }
         else if (curButtonStatus == MineFieldUtil.FLAGGED_MINE) {
            numFlags++;
         }
      }

      this.numSurroundingFlags = numFlags;
   }

   // reveals the mine button
   public void reveal() {
      // do not reveal a mine that's already been revealed
      if (this.curStatus >= 0) {
         return;
      }

      // reveal itself
      if (this.curStatus == MineFieldUtil.FLAGGED_MINE) {
         // do not reveal flagged mines
         if (this.status != MineFieldUtil.MINE) {
            this.setCurStatus(MineFieldUtil.FLAGGED_MINE_ERROR);
         }
      }
      else {
         this.setCurStatus(this.status);
      }
   }

   // modifies the surrounding unknowns
   private void modifySurroundingUnknowns(int theMineId, boolean toBeAdded) {
      try {
         if (toBeAdded) {
            this.surroundingUnknowns.add(new Value(theMineId, RU.INTEGER));
         }
         else {
            int numSurroundingUnknowns = this.surroundingUnknowns.size();

            for (int i = 0; i < numSurroundingUnknowns; i++) {
               if (this.surroundingUnknowns.get(i).intValue(this.reteContext)
                    == theMineId) {
                  this.surroundingUnknowns.remove(i);
                  break;
               }
            }
         }

         // update the rete engine
         this.updateReteEngine();
      }
      catch (Exception e) {
         e.printStackTrace(System.out);
      }
   }

   // modifies the number of surrounding flags
   private void modifyNumSurroudingFlags(boolean increment, int theMineId) {
      if (increment) {
         this.numSurroundingFlags++;
         this.modifySurroundingUnknowns(theMineId, false);
      }
      else {
         this.numSurroundingFlags--;
         this.modifySurroundingUnknowns(theMineId, true);
      }
   }

   // update the mine button (called when the mine field panel is resized)
   public void updateMineButton() {
      if (this.curStatus == MineFieldUtil.MINE) {
         this.setBorder(BorderFactory.createLoweredBevelBorder());
         this.setIcon(MineButton.SCALED_MINE_IMAGE);
      }
      else if (this.curStatus == MineFieldUtil.FLAGGED_MINE) {
         this.setIcon(MineButton.SCALED_FLAG_IMAGE);
      }
      else if (this.curStatus == MineFieldUtil.FLAGGED_MINE_ERROR) {
         this.setIcon(MineButton.SCALED_MINE_IMAGE);
      }
      else if (this.curStatus == MineFieldUtil.UNKNOWN){
         this.setIcon(null);
         this.setText("");
         this.setBorder(
            BorderFactory.createRaisedBevelBorder());
         this.setBackground(Color.LIGHT_GRAY);

      }
      else if (this.curStatus == MineFieldUtil.EMPTY_SQUARE) {
         this.setIcon(null);
         this.setText("");
         this.setBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY.darker(), 1));
         this.setBackground(Color.LIGHT_GRAY);
      }
      else { // a number
         this.setForeground(
            MineButton.NUMBER_COLORS[this.curStatus - 1]);
         this.setFont(new Font("Courier", Font.BOLD, this.getHeight() - 1));
         this.setText(Integer.toString(this.curStatus));
         this.setBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY.darker(), 1));
      }
   }

   // the paint component - responsible for drawing the button
   public void paintComponent(Graphics g) {
      super.paintComponent(g);

      Graphics2D g2 = (Graphics2D)g;

      if (this.curStatus == MineFieldUtil.FLAGGED_MINE_ERROR) {
         g.setColor(Color.RED);
         Line2D.Float redLine1 =
            new Line2D.Float(0, 0, this.getWidth(), this.getHeight());
         Line2D.Float redLine2 =
            new Line2D.Float(0, this.getHeight(), this.getWidth(), 0);
         g2.setStroke(new BasicStroke(2));
         g2.draw(redLine1);
         g2.draw(redLine2);
      }
   }

   // updates the scaled images to use for the button
   static void updateScaledImages(int buttonWidth, int buttonHeight) {
      MineButton.SCALED_MINE_IMAGE =
         new ImageIcon(
            ImageUtil.MINE_IMAGE.getScaledInstance(
               buttonWidth, buttonHeight, Image.SCALE_SMOOTH));

      MineButton.SCALED_FLAG_IMAGE =
         new ImageIcon(
            ImageUtil.FLAG_IMAGE.getScaledInstance(
               buttonWidth, buttonHeight, Image.SCALE_SMOOTH));
   }

}