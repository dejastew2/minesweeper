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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// This class extends the DigitsPanel class and adds a timer.
// The counter digits can either count up or down
public class TimerDigitsPanel extends DigitsPanel implements ActionListener {
   // how often to update the display
   private static final int DEFAULT_TIMER_DELAY = 1000; // one second

   // whether the clock has been started
   private boolean clockStarted;

   // the timer
   private Timer timer;

   // whether to count up or down
   private boolean ifCountUp;

   // the starting time
   private long startingTime;

   private TimerDigitsPanel() {
   }

   // constructor:
   // theDigits    - initial digits
   // theNumDigits - how many slots
   // theIfCountUp - whether to count up or down
   public TimerDigitsPanel(int theDigits, int theNumDigits,
                           boolean theIfCountUp) {
      super(theDigits, theNumDigits);
      this.clockStarted = false;
      this.ifCountUp    = theIfCountUp;
      this.timer        = new Timer(TimerDigitsPanel.DEFAULT_TIMER_DELAY,
                                    this);
      this.startingTime = System.currentTimeMillis();
   }

   // returns the timer delay
   public int getTimerDelay() {
      return this.timer.getDelay();
   }

   // sets the timer delay
   public void setTimerDelay(int theTimerDelay) {
      this.timer.setDelay(theTimerDelay);
   }

   // returns whether we're counting up or down
   public boolean getIfCountUp() {
      return this.ifCountUp;
   }

   // sets whether we're counting up or down
   public void setIfCountUp(boolean theIfCountUp) {
      this.ifCountUp = theIfCountUp;
   }

   // start the clock
   public void setClockStarted(boolean ifClockStarted) {
      // no change in clock status
      if (ifClockStarted != this.clockStarted) {
         // change clock status
         this.clockStarted = ifClockStarted;

         // update the clock
         this.updateClock();
         if (this.clockStarted) {
            // start the clock
            this.timer.start();
         }
         else {
            // stop the clock
            this.timer.stop();
         }
      }
   }

   // reset the clock
   public void reset() {
      // stop the clock
      this.setClockStarted(false);
      this.startingTime = System.currentTimeMillis();

      // reset the digits
      super.reset();
   }

   // returns whether the clock has started
   public boolean getClockStarted() {
      return this.clockStarted;
   }

   // timer calls this method every time it executes
   public void actionPerformed(ActionEvent evt) {
      this.updateClock();
   }

   // determines the number of seconds for the clock
   public void updateClock() {
      if (this.clockStarted) {
         if (this.ifCountUp) {
            this.setDigits(
               (int)(System.currentTimeMillis() - this.startingTime)/1000);
         }
         else {
            this.setDigits(
               (int)(this.startingTime - System.currentTimeMillis())/1000);
         }

         // stop clock if digits maxed out
         if (this.getIfDigitsMaxedOut()) {
            this.setClockStarted(false);
         }

         this.repaint();
      }
   }

}