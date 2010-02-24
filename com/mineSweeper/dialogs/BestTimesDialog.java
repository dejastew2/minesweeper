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

import com.mineSweeper.gui.*;
import com.mineSweeper.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// displays a dialog box containing the best MineSweepers
public class BestTimesDialog extends JDialog implements ActionListener {
   // labels for the names and times
   private JLabel beginnerTimeLabel;
   private JLabel beginnerNameLabel;
   private JLabel intermediateTimeLabel;
   private JLabel intermediateNameLabel;
   private JLabel expertTimeLabel;
   private JLabel expertNameLabel;

   // reset and ok button
   private JButton resetScoresButton;
   private JButton okButton;

   private BestTimesDialog() {
   }

   public BestTimesDialog(MineSweeperFrame msFrame) {
      super(msFrame, "Fastest Mine Sweepers", true);
      this.setDefaultLookAndFeelDecorated(false);

      // set the size
      this.setSize(310, 146);

      // set the location
      this.setLocation(msFrame.getX() + (msFrame.getWidth() / 4),
                       msFrame.getY() + (msFrame.getHeight() / 4));

      // create a best times panel
      JPanel bestTimesPanel = new JPanel(new GridLayout(5, 1));

      // beginner panel
      JPanel beginnerPanel = new JPanel(new GridLayout(1, 3));
      beginnerPanel.add(new JLabel("Beginner"));
      this.beginnerTimeLabel =
         new JLabel(MineSweeperConfig.TIME1 + " seconds");
      this.beginnerNameLabel = new JLabel(MineSweeperConfig.NAME1);
      beginnerPanel.add(this.beginnerTimeLabel);
      beginnerPanel.add(this.beginnerNameLabel);

      // intermediate panel
      JPanel intermediatePanel = new JPanel(new GridLayout(1, 3));
      intermediatePanel.add(new JLabel("Intermediate"));
      this.intermediateTimeLabel =
         new JLabel(MineSweeperConfig.TIME2 + " seconds");
      this.intermediateNameLabel = new JLabel(MineSweeperConfig.NAME2);
      intermediatePanel.add(this.intermediateTimeLabel);
      intermediatePanel.add(this.intermediateNameLabel);

      // expert panel
      JPanel expertPanel = new JPanel(new GridLayout(1, 3));
      expertPanel.add(new JLabel("Expert"));
      this.expertTimeLabel =
         new JLabel(MineSweeperConfig.TIME3 + " seconds");
      this.expertNameLabel = new JLabel(MineSweeperConfig.NAME3);
      expertPanel.add(this.expertTimeLabel);
      expertPanel.add(this.expertNameLabel);

      // empty panel
      JPanel emptyPanel = new JPanel();
      emptyPanel.add(new JLabel(""));

      // button panel
      JPanel buttonPanel = new JPanel(new GridLayout(1, 3));

      // reset scores button
      this.resetScoresButton = new JButton("Reset Scores");
      this.resetScoresButton.addActionListener(this);
      buttonPanel.add(this.resetScoresButton);

      // empty label
      buttonPanel.add(new JLabel(""));

      // ok button
      this.okButton = new JButton("OK");
      this.okButton.addActionListener(this);
      buttonPanel.add(this.okButton);

      // put panels on bestTimesPanel
      bestTimesPanel.add(beginnerPanel);
      bestTimesPanel.add(intermediatePanel);
      bestTimesPanel.add(expertPanel);
      bestTimesPanel.add(emptyPanel);
      bestTimesPanel.add(buttonPanel);

      // add bestTimesPanel to content pane
      this.getContentPane().add(bestTimesPanel);

      // set the focus traversal policy
      this.setFocusTraversalPolicy(new ModFocusPolicy());

      // display the dialog
      this.setVisible(true);
   }

   // executed when the user presses the ok button, reset button,
   //  or closes the dialog box
   public void actionPerformed(ActionEvent e) {
      // make sure there's no null values
      assert(e != null);

      String event = e.getActionCommand();

      if (event.equals("OK")) {
         this.setVisible(false);
      }
      else if (event.equals("Reset Scores")) {
         MineSweeperConfig.resetScores();
         this.beginnerTimeLabel.setText("999 seconds");
         this.intermediateTimeLabel.setText("999 seconds");
         this.expertTimeLabel.setText("999 seconds");
         this.beginnerNameLabel.setText("Anonymous");
         this.intermediateNameLabel.setText("Anonymous");
         this.expertNameLabel.setText("Anonymous");

         // repaint the dialog
         this.repaint();
      }
   }

   // the focus policy (stupid JAVA hack)
   private class ModFocusPolicy extends LayoutFocusTraversalPolicy {
      public Component getInitialComponent(Window window) {
         return BestTimesDialog.this.okButton;
      }
   }

}