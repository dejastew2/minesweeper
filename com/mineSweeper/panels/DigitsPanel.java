package com.mineSweeper.panels;

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
import java.awt.*;
import javax.swing.*;
import java.util.*;

// a digits panel
public class DigitsPanel extends JPanel {
   public final static double LOG_10 = Math.log(10);

   // the current digits displayed in the panel
   protected int digits;

   // the number of slots allocated for the number
   protected int numDigits;

   // the original digits
   protected int originalDigits;

   protected DigitsPanel() {
   }

   // constructs a new digits panel
   public DigitsPanel(int theDigits, int theNumDigits) {
      this.digits    = theDigits;
      this.numDigits = theNumDigits;
      this.originalDigits    = theDigits;

      this.setPreferredSize(
         new Dimension(
            ImageUtil.DIGIT_ICON_WIDTH * this.numDigits + 10,
            ImageUtil.DIGIT_ICON_HEIGHT * 2));

      this.setMinimumSize(
         new Dimension(
            ImageUtil.DIGIT_ICON_WIDTH * this.numDigits + 10,
            ImageUtil.DIGIT_ICON_HEIGHT * 2));

      this.setMaximumSize(
         new Dimension(
            ImageUtil.DIGIT_ICON_WIDTH * this.numDigits + 10,
            ImageUtil.DIGIT_ICON_HEIGHT * 2));
   }

   // returns the displayed digits
   public int getDigits() {
      return this.digits;
   }

   // sets the digits
   public void setDigits(int theDigits) {
      this.digits = theDigits;
      this.repaint();
   }

   // increments the digits
   public void increment() {
      if (!this.getIfDigitsMaxedOut()) {
         this.digits++;
         this.repaint();
      }
   }

   // returns whether the number has overflowed the digits panel
   //  i.e. 1000 can't fit in three slots
   public boolean getIfDigitsMaxedOut() {
      return (DigitsPanel.getDigitsLength(this.digits + 1) > this.numDigits);
   }

   // decrements the digits
   public void decrement() {
      this.digits--;
      this.repaint();
   }

   // resets the digits
   public void reset() {
      this.digits    = this.originalDigits;
      this.repaint();
   }

   // paints the digits
   public void paintComponent(Graphics g) {
      Graphics2D g2D = (Graphics2D)g;
      super.paintComponent(g);
      int[] numberArray = this.getNumberArray(this.digits);

      for (int i = 0; i < this.numDigits; i++) {
         g2D.drawImage(
            ImageUtil.DIGIT_IMAGES[numberArray[i]],
            ImageUtil.DIGIT_ICON_WIDTH * i + 5, 5,
            ImageUtil.DIGIT_ICON_WIDTH, ImageUtil.DIGIT_ICON_HEIGHT, this);
      }
   }

   // converts a number into an array of ints
   private int[] getNumberArray(int number) {
      int[] numberArray = new int[this.numDigits];

      boolean numberNegative = (number < 0);
      if (numberNegative) {
         number *= -1;
      }

      for (int i = this.numDigits - 1; i >= 0; i--) {
         numberArray[i] = (number % 10);
         number /= 10;
      }

      if (numberNegative) {
         numberArray[0] = ImageUtil.NEGATIVE_SIGN_INDEX;
      }

      return numberArray;
   }

   // returns the number of digits
   public static int getDigitsLength(int number) {
      return Integer.toString(number).length();
   }

}