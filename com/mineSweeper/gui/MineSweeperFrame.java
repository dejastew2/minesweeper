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

import com.mineSweeper.menus.*;
import com.mineSweeper.util.*;
import com.mineSweeper.jessFunctions.*;
import com.mineSweeper.dialogs.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.*;
import java.util.*;
import java.beans.beancontext.*;
import jess.*;

// the MineSweeper application frame
public class MineSweeperFrame extends JFrame
      implements ActionListener, ComponentListener, WindowListener {
   private MineFieldPanel mFieldPanel;
   private MineTitlePanel mtitlePanel;
   private MineSweeperMenu msMenu;
   private JFileChooser fileChooser;
   private final static int MINE_ICON_SIZE = 18;
   private final static String APP_TITLE = "Minesweeper";
   public final static File CONFIG_FILE =
      new File("../config/minesweeper.cfg");
   private final static String JESS_FILE =
      new String("../jess/minesweeper.clp");
   private Rete rete;
   public static boolean USE_RETE_ENGINE = false;

   // constructs a new MineSweeper frame
   public MineSweeperFrame() {
      // create the window
      super(MineSweeperFrame.APP_TITLE);

      // set the look and feel
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch (Exception e) {
         System.out.println(e);
      }

      // construct a new rete engine
      this.rete = new Rete();

      // setup the window
      this.addWindowListener(this);
      this.addComponentListener(this);
      this.fileChooser = new JFileChooser("../testCases");
      this.setUpMineSweeperFrame(null);
   }

   // resets the rete engine
   public void resetReteEngine() {
      try {
         // reset the rete engine
         this.rete.reset();

         // create the MineSquare deftemplate
         Deftemplate mineSquareTemplate =
            new Deftemplate("MineSquare",
                            "a square in the mine field", this.rete);
         mineSquareTemplate.addSlot(
            "id", new Value(0, RU.INTEGER), "INTEGER");
         mineSquareTemplate.addSlot(
            "row", new Value(0, RU.INTEGER), "INTEGER");
         mineSquareTemplate.addSlot(
            "col", new Value(0, RU.INTEGER), "INTEGER");
         mineSquareTemplate.addSlot(
            "status", new Value(0, RU.INTEGER), "INTEGER");
         mineSquareTemplate.addMultiSlot(
            "surroundingUnknowns", new Value(new ValueVector(0), RU.LIST));
         mineSquareTemplate.addSlot(
            "numSurroundingFlags", new Value(0, RU.INTEGER), "INTEGER");

         // add the deftemplate to the rete engine
         this.rete.addDeftemplate(mineSquareTemplate);

         // create the EnvironmentVariables deftemplate
         Deftemplate minefieldVariablesTemplate =
            new Deftemplate("MinefieldVariables",
                            "the minefield variables", this.rete);
         minefieldVariablesTemplate.addSlot(
            "mineHeight", new Value(0, RU.INTEGER), "INTEGER");
         minefieldVariablesTemplate.addSlot(
            "mineWidth", new Value(0, RU.INTEGER), "INTEGER");
         minefieldVariablesTemplate.addSlot(
            "mineSquaresKnown", new Value(0, RU.INTEGER), "INTEGER");
         minefieldVariablesTemplate.addSlot(
            "numberMines", new Value(0, RU.INTEGER), "INTEGER");

         // add the deftemplate to the rete engine
         this.rete.addDeftemplate(minefieldVariablesTemplate);

         // add the defglobals to the rete engine
         this.rete.addDefglobal(
            new Defglobal("*EMPTY_SQUARE*",
                          new Value(MineFieldUtil.EMPTY_SQUARE, RU.INTEGER)));
         this.rete.addDefglobal(
            new Defglobal("*FLAGGED_MINE*",
                          new Value(MineFieldUtil.FLAGGED_MINE, RU.INTEGER)));
         this.rete.addDefglobal(
            new Defglobal("*UNKNOWN*",
                          new Value(MineFieldUtil.UNKNOWN, RU.INTEGER)));
      }
      catch (Exception e) {
         e.printStackTrace(System.out);
      }
   }

   // sets up the MineSweeper frame
   private void setUpMineSweeperFrame(int[][] mineField) {
      // construct the minefield
      if (mineField == null) {
         mineField = MineFieldUtil.getNewMineField();
      }

      // reset the engine
      this.resetReteEngine();

      // don't show window set
      this.setVisible(false);

      // remove all components first
      this.getContentPane().removeAll();

      // set up the window
      this.setSize(MineSweeperConfig.WIDTH * MINE_ICON_SIZE,
                   MineSweeperConfig.HEIGHT * MINE_ICON_SIZE);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.setIconImage(
         Toolkit.getDefaultToolkit().createImage(
            "../images/MineSweeperApplicationIcon.gif"));

      // construct the menu bar
      this.msMenu = new MineSweeperMenu(this);
      this.setJMenuBar(this.msMenu);

      // construct the title panel
      this.mtitlePanel = new MineTitlePanel(this, MineSweeperConfig.MINES);

      // construct the minefield panelS
      this.mFieldPanel =
         new MineFieldPanel(
            mineField, this.mtitlePanel, this.rete);

      // add the mine field and title panel to the MineSweeperFrame
      JPanel mineSweeperPanel = new JPanel();
      mineSweeperPanel.setLayout(new BorderLayout());
      mineSweeperPanel.add(this.mtitlePanel, BorderLayout.NORTH);
      mineSweeperPanel.add(this.mFieldPanel, BorderLayout.CENTER);
      mineSweeperPanel.setBorder(
         new CompoundBorder(
            new BevelBorder(BevelBorder.RAISED), new EmptyBorder(6, 6, 6, 6)));
      this.getContentPane().add(mineSweeperPanel);

      // pack the frame
      this.pack();

      // set location
      Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
      if (MineSweeperConfig.X_POS == -1 || MineSweeperConfig.Y_POS == -1) {
         this.setLocation(((screenDimension.width - this.getWidth()) / 2 ),
                          ((screenDimension.height - this.getHeight()) / 2 ));
      }
      else {
         this.setLocation(MineSweeperConfig.X_POS, MineSweeperConfig.Y_POS);
      }

      // display the window
      this.setVisible(true);

      try {
         // add user functions
         this.rete.addUserfunction(new RevealSquare(this.mFieldPanel));
         this.rete.addUserfunction(new FlagSquare(this.mFieldPanel));

         // assert rules
         rete.executeCommand("(batch " + MineSweeperFrame.JESS_FILE + ")");

         // run the engine
         rete.run();
      }
      catch (Exception e) {
         e.printStackTrace(System.out);
      }
   }

   // whenever the user hits an option from the file menu
   public void actionPerformed(ActionEvent e) {
      // make sure there's no null values
      assert(e != null);

      String event = e.getActionCommand();

      if (event.equals("Open Mine Field...")) { // file ->open mine field
         int openResult = fileChooser.showOpenDialog(this);
         if (openResult == JFileChooser.APPROVE_OPTION) {
            File openFile = fileChooser.getSelectedFile();
            int[][] mineField = FileUtil.openMineField(openFile);
            if (mineField == null) {
               JOptionPane.showMessageDialog(this,
                  "Error opening mine field" +
                     " or invalid minefield configuration.",
                  "File Operation Error",
                  JOptionPane.ERROR_MESSAGE);
            }
            else {
               this.setUpMineSweeperFrame(mineField);
               JOptionPane.showMessageDialog(this,
                  "Successfully loaded minefield.",
                  "File Operation Success",
                  JOptionPane.INFORMATION_MESSAGE);
            }
         }
      }
      else if (event.equals("Save Mine Field...")) { // file ->save mine field
         int saveResult = fileChooser.showSaveDialog(this);
         if (saveResult == JFileChooser.APPROVE_OPTION) {
            File saveFile = fileChooser.getSelectedFile();
            if (!FileUtil.saveMineField(
                           saveFile, this.mFieldPanel.toString())) {
               JOptionPane.showMessageDialog(this,
                  "Error saving mine field.",
                  "File Operation Error",
                  JOptionPane.ERROR_MESSAGE);

            }
            else {
               JOptionPane.showMessageDialog(this,
                  "Successfully saved minefield.",
                  "File Operation Success",
                  JOptionPane.INFORMATION_MESSAGE);
            }
         }
      }
      else if (event.equals("Use Rete Engine")) { // tools->use rete engine
         if (MineSweeperFrame.USE_RETE_ENGINE) {
            MineSweeperFrame.USE_RETE_ENGINE = false;
            this.mFieldPanel.haltReteEngine();
            System.out.println("Rete engine halted");
         }
         else {
            MineSweeperFrame.USE_RETE_ENGINE = true;
            MineButton[][] mineFieldButtons =
               this.mFieldPanel.getMineFieldButtons();
            for (int i = 0; i < mineFieldButtons.length; i++) {
               for (int j = 0; j < mineFieldButtons[0].length; j++) {
                  // reset buttons
                  mineFieldButtons[i][j].updateReteEngine();
               }
            }

            this.mFieldPanel.runReteEngine();
            System.out.println("Rete engine running");
         }
      }
      else if (event.equals("Display Facts")) { // tools->display facts
         try {
            this.rete.executeCommand("(facts)");
         }
         catch(Exception e2) {
            e2.printStackTrace(System.out);
         }
      }
      else if (event.equals("Try Combinations")) { // tools->Try Combinations
         MineFieldUtil.determinePossMineFieldCombos(this.mFieldPanel);
      }
      else if (event.equals("Beginner")) { // game->beginner
         if (MineSweeperConfig.DIFFICULTY != MineSweeperConfig.BEGINNER) {
            MineSweeperConfig.setDefaultSetup(MineSweeperConfig.BEGINNER);
            this.setUpMineSweeperFrame(null);
         }
         else {
            this.resetMineFieldAndTitle();
         }
         this.msMenu.setMenuCheckboxItems(MineSweeperConfig.DIFFICULTY);
      }
      else if (event.equals("Intermediate")) { // game->intermediate
         if (MineSweeperConfig.DIFFICULTY != MineSweeperConfig.INTERMEDIATE) {
            MineSweeperConfig.setDefaultSetup(MineSweeperConfig.INTERMEDIATE);
            this.setUpMineSweeperFrame(null);
         }
         else {
            this.resetMineFieldAndTitle();
         }
         this.msMenu.setMenuCheckboxItems(MineSweeperConfig.DIFFICULTY);
      }
      else if (event.equals("Expert")) { // game->expert
         if (MineSweeperConfig.DIFFICULTY != MineSweeperConfig.EXPERT) {
            MineSweeperConfig.setDefaultSetup(MineSweeperConfig.EXPERT);
            this.setUpMineSweeperFrame(null);
         }
         else {
            this.resetMineFieldAndTitle();
         }
         this.msMenu.setMenuCheckboxItems(MineSweeperConfig.DIFFICULTY);
      }
      else if (event.equals("Custom...")) { // game->Custom
         CustomMineFieldDialog customDialog =
            new CustomMineFieldDialog(
               this, MineSweeperConfig.HEIGHT, MineSweeperConfig.WIDTH,
               MineSweeperConfig.MINES);
         if (customDialog.getOkButtonPressed()) {
            this.setUpMineSweeperFrame(null);
         }
         this.msMenu.setMenuCheckboxItems(MineSweeperConfig.DIFFICULTY);
      }
      else if (event.equals("Best Times...")) { // game->Best Times
         new BestTimesDialog(this);
         return;
      }
      else if (event.equals("Exit")) { // game->exit
         FileUtil.saveMineSweeperConfiguration(MineSweeperFrame.CONFIG_FILE);
         System.exit(0);
      }
      else if (event.equals("About")) { // help->about
         JOptionPane.showMessageDialog(this,
            "Minesweeper 1.0\n Written by Hafeez Jaffer",
            "About Minesweeper",
            JOptionPane.INFORMATION_MESSAGE);
         return;
      }
      else { // smile button clicked or game->new
         this.resetMineFieldAndTitle();
      }
   }

   // reset the mine field and title panel
   public void resetMineFieldAndTitle() {
      // start a new game
      this.mFieldPanel.resetMineField(MineFieldUtil.getNewMineField());

      // reset the clock
      this.mtitlePanel.resetTitlePanel();
   }

   // invoked when the Window is set to be the active Window
   public void windowActivated(WindowEvent e) {
   }

   // invoked when a window has been closed as the result of
   //  calling dispose on the window.
   public void windowClosed(WindowEvent e) {
    FileUtil.saveMineSweeperConfiguration(MineSweeperFrame.CONFIG_FILE);
   }

   // invoked when the user attempts to close the window from
   //  the window's system menu
   public void windowClosing(WindowEvent e) {
    FileUtil.saveMineSweeperConfiguration(MineSweeperFrame.CONFIG_FILE);
   }

   // invoked when a Window is no longer the active Window
   public void windowDeactivated(WindowEvent e) {
   }

   // invoked when a window is changed from a minimized to a normal state
   public void windowDeiconified(WindowEvent e) {
   }

   // invoked when a window is changed from a normal to a minimized state
   public void windowIconified(WindowEvent e) {
   }

   // invoked the first time a window is made visible
   public void windowOpened(WindowEvent e) {
   }

   // invoked when the component has been made invisible
   public void componentHidden(ComponentEvent e) {
   }

   // invoked when the component's position changes
   public void componentMoved(ComponentEvent e) {
      if (this.isVisible()) {
         MineSweeperConfig.X_POS = this.getX();
         MineSweeperConfig.Y_POS = this.getY();
      }
   }

   // invoked when the component's size changes
   public void componentResized(ComponentEvent e) {
      if (this.isVisible()) {
         MineSweeperConfig.X_POS = this.getX();
         MineSweeperConfig.Y_POS = this.getY();
      }
   }

   // invoked when the component has been made visible
   public void componentShown(ComponentEvent e) {
   }

}