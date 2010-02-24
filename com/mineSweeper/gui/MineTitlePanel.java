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

import com.mineSweeper.dialogs.*;
import com.mineSweeper.util.*;
import com.mineSweeper.panels.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.*;
import java.lang.*;

// the title panel contains:
//  - the mines remaining counter
//  - the timer
//  - the smile icon
public class MineTitlePanel extends JPanel implements MouseListener {
   private final int DEFAULT_WIDTH  = 130;
   private final int DEFAULT_HEIGHT = 44;
   private static boolean mousePressed = false;
   private static Toolkit toolkit = Toolkit.getDefaultToolkit();
   private JButton smileButton;
   private DigitsPanel dPanel;
   private TimerDigitsPanel tdPanel;
   private MineSweeperFrame msFrame;

   // get the smile images
   public static ImageIcon FACE_SMILE   =
      new ImageIcon(ImageUtil.FACE_IMAGES[0]);
   public static ImageIcon FACE_WORRIED =
      new ImageIcon(ImageUtil.FACE_IMAGES[1]);
   public static ImageIcon FACE_COOL    =
      new ImageIcon(ImageUtil.FACE_IMAGES[2]);
   public static ImageIcon FACE_DEAD    =
      new ImageIcon(ImageUtil.FACE_IMAGES[3]);

   private MineTitlePanel() {
   }

   // returns the smile button
   public JButton getSmileButton() {
      return this.smileButton;
   }

   // constructs a new MineTitlePanel
   public MineTitlePanel(MineSweeperFrame theMsFrame, int numMines) {
      // make sure there's no null values
      assert(theMsFrame != null);

      this.msFrame = theMsFrame;
      // set border, preferred size, and layout
      this.setBorder(
         new CompoundBorder(
            new EmptyBorder(0, 0, 6, 0),new BevelBorder(BevelBorder.LOWERED) ));
      this.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
      this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

      // digits panel
      this.dPanel  =
         new DigitsPanel(numMines, 1 + DigitsPanel.getDigitsLength(numMines));

      // timer panel
      this.tdPanel =
         new TimerDigitsPanel(0, 1 + DigitsPanel.getDigitsLength(numMines),
                              true);

      // smile button
      this.smileButton = new JButton(MineTitlePanel.FACE_SMILE);
      this.smileButton.setFocusPainted(false);
      this.smileButton.setBorder(BorderFactory.createRaisedBevelBorder());
      this.smileButton.setPreferredSize(new Dimension(27, 27));
      this.smileButton.setMinimumSize(new Dimension(27, 27));
      this.smileButton.setMaximumSize(new Dimension(27, 27));
      this.smileButton.addMouseListener(this);
      this.smileButton.addActionListener(msFrame);

      // add the components to the title panel and create glue for placement
      this.add(this.dPanel);
      this.add(Box.createHorizontalGlue());
      this.add(smileButton);
      this.add(Box.createHorizontalGlue());
      this.add(this.tdPanel);
   }

   // start the clock
   public void startClock(boolean startClock) {
      // start counting from 1
      if (this.tdPanel.getDigits() == 0) {
         this.tdPanel.increment();
      }

      // call start/stop clock
      this.tdPanel.setClockStarted(startClock);
   }

   // decrement the number of remaining mines
   public void decrementMinesRemaining() {
      this.dPanel.decrement();
   }

   // increment the number of remaining mines
   public void incrementMinesRemaining() {
      this.dPanel.increment();
   }

   // get the mines remaining
   public int getMinesRemaining() {
      return this.dPanel.getDigits();
   }

   // resets the title panel
   public void resetTitlePanel() {
      this.dPanel.reset();
      this.tdPanel.reset();
      this.smileButton.setIcon(new ImageIcon(ImageUtil.FACE_IMAGES[0]));
   }

   // set the title panel to game over
   public void gameOver(boolean hasWon) {
      // stop clock
      this.startClock(false);

      // set the appropriate icon for the smile button
      this.smileButton.setIcon(
         (hasWon ? MineTitlePanel.FACE_COOL : MineTitlePanel.FACE_DEAD));

      // check to see if a time record has been broken
      if (hasWon) {
         // reset the digits panel
         this.dPanel.setDigits(0);

         int curTime = this.tdPanel.getDigits();
         if (MineSweeperConfig.isFastestTime(curTime)) {
            // get the user's name
            new FastestTimeDialog(this.msFrame, curTime);

            // show the updated high scores
            new BestTimesDialog(this.msFrame);
         }
      }
   }

   // invoked when the mouse button has been
   //  clicked (pressed and released) on a component
   public void mouseClicked(MouseEvent e) {
   }

   // invoked when the mouse enters a component
   public void mouseEntered(MouseEvent e) {
      if (MineTitlePanel.mousePressed) {
         this.smileButton.setBorder(BorderFactory.createLoweredBevelBorder());
      }
   }

   // invoked when the mouse exits a component
   public void mouseExited(MouseEvent e) {
      if (MineTitlePanel.mousePressed) {
         this.smileButton.setBorder(BorderFactory.createRaisedBevelBorder());
      }
   }

   // invoked when a mouse button has been pressed on a component.
   public void mousePressed(MouseEvent e) {
      MineTitlePanel.mousePressed = true;
      this.smileButton.setBorder(BorderFactory.createLoweredBevelBorder());
   }

   // invoked when a mouse button has been released on a component.
   public void mouseReleased(MouseEvent e) {
      MineTitlePanel.mousePressed = false;
      this.smileButton.setBorder(BorderFactory.createRaisedBevelBorder());
   }

}