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
import com.mineSweeper.ui.*;
import com.mineSweeper.gui.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// dialog for creating a custom minefield
public class CustomMineFieldDialog extends JDialog implements ActionListener {
   // the height of the minefield
   WholeNumberField heightField;

   // the width of the minefield
   WholeNumberField widthField;

   // the number of mines in the minefield
   WholeNumberField minesField;

   // whether the ok button was pressed
   private boolean okButtonPressed;

   private CustomMineFieldDialog() {
   }

   // constructs a new custom minefield dialog
   public CustomMineFieldDialog(MineSweeperFrame msFrame,
                                int defaultHeight, int defaultWidth,
                                int defaultMines) {
      super(msFrame, "Custom Field", true);
      this.setDefaultLookAndFeelDecorated(false);

      // set the size
      this.setSize(129, 171);

      // set the location
      this.setLocation(msFrame.getX() + (msFrame.getWidth() / 4),
                       msFrame.getY() + (msFrame.getHeight() / 4));

      // create a config panel
      JPanel configPanel = new JPanel(new GridLayout(4, 1));

      // ok button
      final JButton okButton = new JButton("OK");
      this.okButtonPressed = false;
      okButton.addActionListener(this);

      // height panel
      JPanel heightPanel = new JPanel();
      heightPanel.add(new JLabel("Height"));
      this.heightField = new WholeNumberField(defaultHeight, 5) {
         public void processKeyEvent(KeyEvent e) {
            super.processKeyEvent(e);

            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
               okButton.doClick();
            }
         }
      };
      heightPanel.add(this.heightField);

      // width panel
      JPanel widthPanel = new JPanel();
      widthPanel.add(new JLabel("Width"));
      this.widthField = new WholeNumberField(defaultWidth, 5) {
         public void processKeyEvent(KeyEvent e) {
            super.processKeyEvent(e);

            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
               okButton.doClick();
            }
         }
      };
      widthPanel.add(this.widthField);

      // mines panel
      JPanel minesPanel = new JPanel();
      minesPanel.add(new JLabel("Mines"));
      this.minesField = new WholeNumberField(defaultMines, 5) {
         public void processKeyEvent(KeyEvent e) {
            super.processKeyEvent(e);

            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
               okButton.doClick();
            }
         }
      };
      minesPanel.add(this.minesField);

      // add the panels to the config panel
      configPanel.add(heightPanel);
      configPanel.add(widthPanel);
      configPanel.add(minesPanel);
      configPanel.add(okButton);

      // add the config panel to the content pane
      this.getContentPane().add(configPanel);

      // display the dialog
      this.setVisible(true);
   }

   // determines whether the ok button was pressed
   public boolean getOkButtonPressed() {
      return this.okButtonPressed;
   }

   // when the user either presses ok or closes the dialog box
   public void actionPerformed(ActionEvent e) {
      // make sure there's no null values
      assert(e != null);

      String event = e.getActionCommand();

      if (event.equals("OK")) {
         int newHeight = this.heightField.getValue();
         int newWidth  = this.widthField.getValue();
         int newMines  = this.minesField.getValue();

         if (newHeight <= 0 || newWidth <= 0) {
            JOptionPane.showMessageDialog(this,
               "Custom mine field height and width must be greater than 0.",
               "Custom Mine Field Dialog Error",
               JOptionPane.ERROR_MESSAGE);
         }
         else if (newMines >= (newHeight*newWidth)) {
            JOptionPane.showMessageDialog(this,
               "Number of mines must be less than the size of the mine field.",
               "Custom Mine Field Dialog Error",
               JOptionPane.ERROR_MESSAGE);
         }
         else {
            this.okButtonPressed = true;

            MineSweeperConfig.setCustomSetup(
               newHeight, newWidth, newMines);
            this.setVisible(false);
         }
      }
   }

}