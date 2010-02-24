package com.mineSweeper.dialogs;

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
import com.mineSweeper.gui.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// dialog that appears when the user has broken a fastest time record
public class FastestTimeDialog extends JDialog {
   private FastestTimeDialog() {
   }

   public FastestTimeDialog(MineSweeperFrame msFrame, final int time) {
      super(msFrame, "Congratulations", true);

      // set the size
      this.setSize(158, 168);

      // set the location
      this.setLocation(msFrame.getX() + (msFrame.getWidth() / 4),
                       msFrame.getY() + (msFrame.getHeight() / 4));

      // create a fastest times panel
      JPanel fastestTimesPanel = new JPanel(new GridLayout(8, 1));

      // create a fastest times label
      JLabel fastestTimeLabel1 =
         new JLabel("You have the fastest time");

      JLabel fastestTimeLabel2 =
         new JLabel(
            " for " +
            MineSweeperConfig.LEVEL_STRINGS[MineSweeperConfig.DIFFICULTY] +
            " level.");

      JLabel fastestTimeLabel3 =
         new JLabel("Please enter your name.");

      // create an ok button
      JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
      final JButton okButton = new JButton("OK");

      // create a name field
      final JTextField nameField = new JTextField("Anonymous") {
         public void processKeyEvent(KeyEvent e) {
            super.processKeyEvent(e);

            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
               okButton.doClick();
            }
         }
      };

      // ok button listener
      okButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            String event = e.getActionCommand();

            if (event.equals("OK")) {
               MineSweeperConfig.setFastestTime(time, nameField.getText());
               setVisible(false);
            }
         }
      });
      // add the ok button to the button panel
      buttonPanel.add(new JLabel(""));
      buttonPanel.add(okButton);
      buttonPanel.add(new JLabel(""));

      // add the components to the fastestTimesPanel
      fastestTimesPanel.add(fastestTimeLabel1);
      fastestTimesPanel.add(fastestTimeLabel2);
      fastestTimesPanel.add(fastestTimeLabel3);
      fastestTimesPanel.add(new JLabel(""));
      fastestTimesPanel.add(nameField);
      fastestTimesPanel.add(new JLabel(""));
      fastestTimesPanel.add(buttonPanel);
      fastestTimesPanel.add(new JLabel(""));

      this.getContentPane().add(fastestTimesPanel);

      // display the dialog
      this.setVisible(true);
   }

}