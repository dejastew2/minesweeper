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

import com.mineSweeper.ui.*;
import com.mineSweeper.util.*;
import jess.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.text.*;

// a panel that keeps all the minefield buttons
public class MineFieldPanel extends JPanel implements ComponentListener,
                                           MouseListener {
   // the array of minefield buttons
   private MineButton[][] mineFieldButtons;

   // a reference to the title panel
   private MineTitlePanel mtPanel;

   // a reference to the smile button
   private JButton smileButton;

   // the rete engine
   private Rete rete;

   // mouse status variables
   private static boolean FIRST_BUTTON_PRESSED = false;
   private static boolean TWO_BUTTONS_PRESSED  = false;
   private static MineButton curButtonPressed;
   private static int TWO_BUTTON_MASK =
      (InputEvent.BUTTON1_DOWN_MASK | InputEvent.BUTTON3_DOWN_MASK);
   private boolean gameOver;

   private MineFieldPanel() {
   }

   // constructs a new MineFieldPanel
   public MineFieldPanel(int[][] theMineField, MineTitlePanel theMtPanel,
                         Rete theRete) {
      super(new GridLayout(MineSweeperConfig.HEIGHT, MineSweeperConfig.WIDTH));

      // make sure there's no null values
      assert(theMineField != null && theMtPanel != null && theRete != null);

      this.mtPanel = theMtPanel;
      this.smileButton = theMtPanel.getSmileButton();
      MineButton.MINE_SQUARES_KNOWN = 0;
      this.gameOver = false;
      this.setBorder(new BevelBorder(BevelBorder.LOWERED));
      this.rete = theRete;

      // construct the mine field buttons
      this.mineFieldButtons =
         new MineButton[MineSweeperConfig.HEIGHT][MineSweeperConfig.WIDTH];
      for (int i = 0; i < MineSweeperConfig.HEIGHT; i++) {
         for (int j = 0; j < MineSweeperConfig.WIDTH; j++) {
            this.mineFieldButtons[i][j] =
               new MineButton(this, this.rete, this.mineFieldButtons, i, j);
            this.mineFieldButtons[i][j].addMouseListener(this);
            this.mineFieldButtons[i][j].setStatus(theMineField[i][j]);
            this.add(this.mineFieldButtons[i][j]);
         }
      }

      // update the mine field buttons surrounding squares
      for (int i = 0; i < MineSweeperConfig.HEIGHT; i++) {
         for (int j = 0; j < MineSweeperConfig.WIDTH; j++) {
            this.mineFieldButtons[i][j].initialize();
         }
      }

      // assert the number mine squares known fact
	//CHANGED by Andrew Tsui, Cory White, and Peter Cunningham
      //Previously, the following fact was never asserted into the system.
      //In addition, the following code would attempt to 'modify' the fact
      //if the stored fact was not null.  However, before this code is called,
      //the rete engine is reset, thus removing all facts from the system.  This
      //resulted in an exception being thrown when the code tried to modify the fact
      //that no longer existed in the system.  The fact is no recreated every time.
      try {
        MineButton.numMineSquaresKnownFact =
           new Fact("MinefieldVariables", this.rete);
        MineButton.numMineSquaresKnownFact.setSlotValue(
           "numberMines", new Value(MineSweeperConfig.MINES, RU.INTEGER));
        MineButton.numMineSquaresKnownFact.setSlotValue(
           "mineHeight", new Value(MineSweeperConfig.HEIGHT, RU.INTEGER));
        MineButton.numMineSquaresKnownFact.setSlotValue(
           "mineWidth", new Value(MineSweeperConfig.WIDTH, RU.INTEGER));
        rete.assertFact(MineButton.numMineSquaresKnownFact);
      }
      catch (Exception e) {
         e.printStackTrace(System.out);
      }

      // construct a component listener
      this.addComponentListener(this);
   }

   // returns the minefield buttons
   public MineButton[][] getMineFieldButtons() {
      return this.mineFieldButtons;
   }

   // outputs the minefield as a string
   public String toString() {
      StringBuffer retrStr = new StringBuffer();
      DecimalFormat df = new DecimalFormat(" #;-");

      for (int i = 0; i < mineFieldButtons.length; i++) {
         for (int j = 0; j < mineFieldButtons[0].length; j++) {
            retrStr.append(
               " " + df.format(mineFieldButtons[i][j].getStatus()));
         }
         retrStr.append("\n");
      }

      return retrStr.toString();
   }

   // sets game over
   public void setGameOver(int buttonRow, int buttonCol, boolean flagMines) {
      // game over
      this.gameOver = true;

      // stop clock
      this.mtPanel.gameOver(flagMines);

      // game over
      this.showAllMines(buttonRow, buttonCol, flagMines);
   }

   // invoked when the component has been made invisible
   public void componentHidden(ComponentEvent e) {
   }

   // invoked when the component's position changes
   public void componentMoved(ComponentEvent e) {
   }

   // invoked when the component's size changes
   public void componentResized(ComponentEvent e) {
      MineButton.updateScaledImages(
         this.mineFieldButtons[0][0].getWidth(),
         this.mineFieldButtons[0][0].getHeight());

      for (int i = 0; i < this.mineFieldButtons.length; i++) {
         for (int j = 0; j < this.mineFieldButtons[0].length; j++) {
            this.mineFieldButtons[i][j].updateMineButton();
         }
      }
   }

   // invoked when the component has been made visible
   public void componentShown(ComponentEvent e) {
   }

   // invoked when the mouse button has been
   //  clicked (pressed and released) on a component
   public void mouseClicked(MouseEvent e) {
      // make sure there's no null values
      assert(e != null);

      if (this.gameOver) {
         return;
      }

      MineButton mButton = (MineButton)e.getSource();
      try {
         Context reteContext = this.rete.getGlobalContext();

         System.out.print("id=" + mButton.getId());
         System.out.print("row=" + mButton.getRow() +
                          ",col=" + mButton.getCol());
         System.out.print(",status=");
         System.out.print(
            mButton.getMineButtonFact().getSlotValue("status").intValue(
               reteContext));
         System.out.print(",surroundingUnknowns=");
         System.out.print(
            mButton.getMineButtonFact().getSlotValue(
               "surroundingUnknowns").listValue(
                  reteContext).toStringWithParens());
         System.out.print(",numSurroundingFlags=");
         System.out.println(
            mButton.getMineButtonFact().getSlotValue(
               "numSurroundingFlags").intValue(reteContext));
         System.out.println("Mine Squares known: " +
                            MineButton.MINE_SQUARES_KNOWN);
      }
      catch(Exception e2) {
         e2.printStackTrace(System.out);
      }

      // first mouse button pressed
      if (this.FIRST_BUTTON_PRESSED) {
         this.curButtonPressed = mButton;
         this.mouseReleased(e);
      }
   }

   // invoked when the mouse enters a component
   public void mouseEntered(MouseEvent e) {
      // make sure there's no null values
      assert(e != null);

      if (this.gameOver) {
         return;
      }

      MineButton mButton = (MineButton)e.getSource();
      int mButtonRow = mButton.getRow();
      int mButtonCol = mButton.getCol();

      this.curButtonPressed = mButton;
      if (MineFieldPanel.FIRST_BUTTON_PRESSED) {
         if (mButton.getCurStatus() == MineFieldUtil.UNKNOWN) {
            mButton.setBorder(BorderFactory.createLoweredBevelBorder());
         }
      }

      if (MineFieldPanel.TWO_BUTTONS_PRESSED) {
         this.showProximity(mButtonRow, mButtonCol, true, false);
      }
   }

   // invoked when the mouse exits a component
   public void mouseExited(MouseEvent e) {
      // make sure there's no null values
      assert(e != null);

      if (this.gameOver) {
         return;
      }

      MineButton mButton = (MineButton)e.getSource();
      int mButtonRow = mButton.getRow();
      int mButtonCol = mButton.getCol();

      this.curButtonPressed = null;

      if (MineFieldPanel.FIRST_BUTTON_PRESSED) {
         if (mButton.getCurStatus() == MineFieldUtil.UNKNOWN) {
            mButton.setBorder(BorderFactory.createRaisedBevelBorder());
         }
      }

      if (MineFieldPanel.TWO_BUTTONS_PRESSED) {
         this.showProximity(mButtonRow, mButtonCol, false, false);
      }
   }

   // invoked when a mouse button has been pressed on a component.
   public void mousePressed(MouseEvent e) {
      // make sure there's no null values
      assert(e != null);

      if (this.gameOver) {
         return;
      }

      MineButton mButton = (MineButton)e.getSource();
      // check if two mouse buttons were pressed
      if ((e.getModifiersEx() & MineFieldPanel.TWO_BUTTON_MASK)
            == MineFieldPanel.TWO_BUTTON_MASK) {
         this.TWO_BUTTONS_PRESSED = true;
         this.showProximity(mButton.getRow(), mButton.getCol(), true, false);
         MineFieldPanel.FIRST_BUTTON_PRESSED = false;
      }
      // if the second mouse button is pressed
      else if ((e.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK)
            == InputEvent.BUTTON3_DOWN_MASK) {
         // flag the square
         this.flagMineSquare(mButton.getRow(), mButton.getCol(), false);

         return;
      }
      else { // first button pressed
         MineFieldPanel.FIRST_BUTTON_PRESSED = true;
         if (mButton.getCurStatus() == MineFieldUtil.UNKNOWN) {
            mButton.setBorder(BorderFactory.createLoweredBevelBorder());
            this.curButtonPressed = mButton;
         }
      }

      // set the smile icon to worried
      this.smileButton.setIcon(MineTitlePanel.FACE_WORRIED);
   }

   // invoked when a mouse button has been released on a component.
   public void mouseReleased(MouseEvent e) {
      if (this.gameOver) {
         return;
      }

      MineButton mButton = this.curButtonPressed;

      // set smily icon image back to normal
      this.smileButton.setIcon(MineTitlePanel.FACE_SMILE);

      // if the mouse has moved off the application
      if (this.curButtonPressed == null) {
         MineFieldPanel.FIRST_BUTTON_PRESSED = false;
         MineFieldPanel.TWO_BUTTONS_PRESSED  = false;
         return;
      }

      this.pushMineSquare(mButton.getRow(), mButton.getCol(), false);
   }

   // flags a square
   public void flagMineSquare(int buttonRow, int buttonCol,
                              boolean usingReteEngine) {
      MineButton mButton = this.mineFieldButtons[buttonRow][buttonCol];
      int curButtonStatus = mButton.getCurStatus();

      // halt the rete engine
      this.haltReteEngine();

      if (curButtonStatus == MineFieldUtil.UNKNOWN) {
         mButton.setCurStatus(MineFieldUtil.FLAGGED_MINE);
         this.mtPanel.decrementMinesRemaining();
      }
      else if (!usingReteEngine &&
               curButtonStatus == MineFieldUtil.FLAGGED_MINE) {
         mButton.setCurStatus(MineFieldUtil.UNKNOWN);
         this.mtPanel.incrementMinesRemaining();
      }

      // run the rete engine
      this.runReteEngine();
   }

   // reveals a square
   public void pushMineSquare(int buttonRow, int buttonCol,
                              boolean usingReteEngine) {
      if (this.gameOver) {
         return;
      }

      MineButton mButton = this.mineFieldButtons[buttonRow][buttonCol];

      int curButtonStatus = mButton.getCurStatus();
      // if two buttons are pressed
      if (MineFieldPanel.TWO_BUTTONS_PRESSED) {
         // show proximity
         this.showProximity(buttonRow, buttonCol, false,
                            curButtonStatus > 0);

         // update the status of the two mouse buttons
         MineFieldPanel.FIRST_BUTTON_PRESSED = false;
         MineFieldPanel.TWO_BUTTONS_PRESSED  = false;

         return;
      }

      // if first button is not pressed, don't do anything
      if (!MineFieldPanel.FIRST_BUTTON_PRESSED && !usingReteEngine) {
         return;
      }
      // set the status of the first mouse button
      MineFieldPanel.FIRST_BUTTON_PRESSED = false;

      // make sure clock is started
      this.mtPanel.startClock(true);

      if (curButtonStatus == MineFieldUtil.UNKNOWN) {
         // halt rete engine
         this.haltReteEngine();
         mButton.reveal();

         int buttonStatus = mButton.getStatus();
         if (buttonStatus == MineFieldUtil.MINE) { // mine
            this.setGameOver(buttonRow, buttonCol, false);
            return;
         }
         else { // not a mine (a number or no mine at all)
            if (buttonStatus == MineFieldUtil.EMPTY_SQUARE) {
               this.clearNonMineArea(buttonRow, buttonCol);
            }
         }

         // run rete engine
         this.runReteEngine();
      }
   }

   // called when the user reveals a square that is empty
   //  (i.e. not a number, mine, etc.)
   // this method recursively calls itself until all empty squares
   //  are bordered by non empty squares
   private void clearNonMineArea(int buttonRow, int buttonCol) {
      // reveal the mine button
      this.mineFieldButtons[buttonRow][buttonCol].reveal();

      // get neighbors
      MineButton[] surroundingMineButtons =
       this.mineFieldButtons[buttonRow][buttonCol].getSurroundingMineButtons();

      // reveal its neighbors
      int row;
      int col;
      for (int i = 0; i < surroundingMineButtons.length; i++) {
         if (surroundingMineButtons[i].getCurStatus() ==
                  MineFieldUtil.UNKNOWN) {
            row = surroundingMineButtons[i].getRow();
            col = surroundingMineButtons[i].getCol();

            if (surroundingMineButtons[i].getStatus() ==
                  MineFieldUtil.EMPTY_SQUARE) { // empty square
               this.clearNonMineArea(row, col);
            }
            else { // number or mine
               surroundingMineButtons[i].reveal();
               if (surroundingMineButtons[i].getStatus() ==
                     MineFieldUtil.MINE) {
                     this.setGameOver(row, col, false);
                  return;
               }
            }
         }
      }
   }

   // called when the game is over.
   //  All mines are revealed if the user lost
   //  All mines are flagged if the user won
   private void showAllMines(int buttonRow, int buttonCol, boolean flagMines) {
      // set the clicked on mine's background color to red
      if (!flagMines) {
         this.mineFieldButtons[buttonRow][buttonCol].setBackground(Color.RED);
      }

      for (int i = 0; i < this.mineFieldButtons.length; i++) {
         for (int j = 0; j < this.mineFieldButtons[0].length; j++) {
            int curButtonStatus = this.mineFieldButtons[i][j].getStatus();
            if (curButtonStatus == MineFieldUtil.MINE) {
               int curButtonCurStatus =
                  this.mineFieldButtons[i][j].getCurStatus();

               if (curButtonCurStatus == MineFieldUtil.UNKNOWN) {
                  if (flagMines) {
                     this.mineFieldButtons[i][j].setCurStatus(
                        MineFieldUtil.FLAGGED_MINE);
                  }
                  else {
                     this.mineFieldButtons[i][j].reveal();
                  }
               }
            }
         }
      }
   }

   // runs the rete engine
   public void runReteEngine() {
      final com.mineSweeper.ui.SwingWorker reteWorker = new com.mineSweeper.ui.SwingWorker() {
         public Object construct() {
            try {
               // run the rete engine
               MineFieldPanel.this.rete.runUntilHalt();
            }
            catch (Exception e) {
               e.printStackTrace(System.out);
            }

            return null;
         }
      };

      // star the rete worker thread
      reteWorker.start();
   }

   // reset the rete engine
   public void resetReteEngine() {
      // reset the rete engine
      try {
         this.rete.halt();
         this.rete.reset();
      }
      catch (Exception e) {
         e.printStackTrace(System.out);
      }
   }

   // halt the rete engine
   public void haltReteEngine() {
      // halt the rete engine
      try {
         this.rete.halt();
      }
      catch (Exception e) {
         e.printStackTrace(System.out);
      }
   }

   // reveals the proximity if the user clicks on a numbered square
   // shows the proximity if the user clicks on an unknown square
   private void showProximity(int buttonRow, int buttonCol,
                              boolean show, boolean revealProximity) {
      int buttonStatus =
         this.mineFieldButtons[buttonRow][buttonCol].getCurStatus();

      int curButtonStatus = 0;
      int numFlags = 0;
      for (int i = buttonRow - 1; i <= buttonRow + 1; i++) {
         for (int j = buttonCol - 1; j <= buttonCol + 1; j++) {
            if (i < 0 || i >= mineFieldButtons.length ||
                j < 0 || j >= mineFieldButtons[0].length) {
               continue;
            }

            curButtonStatus = this.mineFieldButtons[i][j].getCurStatus();

            if (curButtonStatus == MineFieldUtil.UNKNOWN) {
               this.mineFieldButtons[i][j].setBorder(
                  (show ? BorderFactory.createLoweredBevelBorder()
                        : BorderFactory.createRaisedBevelBorder()));
            }
            else if (curButtonStatus == MineFieldUtil.FLAGGED_MINE) {
               numFlags++;
            }
         }
      }

      if (revealProximity && numFlags >= buttonStatus) {
         this.clearNonMineArea(buttonRow, buttonCol);
      }
   }

   // resets the minefield
   public void resetMineField(int[][] theMineField) {
      // make sure there's no null values
      assert(theMineField != null);

      // reset the rete engine
      this.resetReteEngine();

      // reset mouse buttons
      MineFieldPanel.FIRST_BUTTON_PRESSED = false;
      MineFieldPanel.TWO_BUTTONS_PRESSED  = false;

      for (int i = 0; i < this.mineFieldButtons.length; i++) {
         for (int j = 0; j < this.mineFieldButtons[0].length; j++) {
            // reset buttons
            this.mineFieldButtons[i][j].resetButton(theMineField[i][j]);
         }
      }
      MineButton.MINE_SQUARES_KNOWN = 0;

      // update the rete engine
      //CHANGED by Andrew Tsui, Cory White, and Peter Cunninghame
      //Previously, this code tried to modify the fact (instead of assert)
      //However, the Rete engine is reset above, so no facts exist in the
      //system.  This caused exceptions to be thrown, and the fact never
      //put into the system.  The code was changed to reasert the fact.
      try {
         this.rete.assertFact(MineButton.numMineSquaresKnownFact);
      }
      catch (Exception e) {
         e.printStackTrace(System.out);
      }
      this.gameOver = false;

      // run the rete engine
      this.runReteEngine();
   }

}