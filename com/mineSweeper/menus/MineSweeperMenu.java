package com.mineSweeper.menus;

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
import javax.swing.*;
import java.awt.event.*;

// the menu for MineSweeper
public class MineSweeperMenu extends JMenuBar {
   // JMenuCheckBox items for the difficulty of the game
   private JCheckBoxMenuItem gameBeginnerMenuItem;
   private JCheckBoxMenuItem gameIntermediateMenuItem;
   private JCheckBoxMenuItem gameExpertMenuItem;
   private JCheckBoxMenuItem gameCustomMenuItem;

   private MineSweeperMenu() {
   }

   // determines the appropriate menu item to add a checkbox
   public void setMenuCheckboxItems(int gameType) {
      this.gameBeginnerMenuItem.setState(
         gameType == MineSweeperConfig.BEGINNER);
      this.gameIntermediateMenuItem.setState(
         gameType == MineSweeperConfig.INTERMEDIATE);
      this.gameExpertMenuItem.setState(
         gameType == MineSweeperConfig.EXPERT);
      this.gameCustomMenuItem.setState(
         gameType == MineSweeperConfig.CUSTOM);
   }

   // creates a new MineSweeperMenu
   public MineSweeperMenu(MineSweeperFrame msFrame) {
      // make sure there's no null values
      assert(msFrame != null);

      // create the file menu
      JMenu fileMenu = new JMenu("File");
      fileMenu.setMnemonic(KeyEvent.VK_F);
      this.add(fileMenu);

      // file->Open Mine Field
      JMenuItem fileOpenMineFieldMenuItem =
         new JMenuItem("Open Mine Field...", ImageUtil.OPEN_IMAGE);
      fileOpenMineFieldMenuItem.setAccelerator(KeyStroke.getKeyStroke(
              KeyEvent.VK_O, ActionEvent.ALT_MASK));
      fileOpenMineFieldMenuItem.setMnemonic('o');
      fileOpenMineFieldMenuItem.addActionListener(msFrame);
      fileMenu.add(fileOpenMineFieldMenuItem);

      // separator
      fileMenu.addSeparator();

      // file->Save Mine Field
      JMenuItem fileSaveMineFieldMenuItem =
         new JMenuItem("Save Mine Field...", ImageUtil.SAVE_IMAGE);
      fileSaveMineFieldMenuItem.setAccelerator(KeyStroke.getKeyStroke(
              KeyEvent.VK_S, ActionEvent.ALT_MASK));
      fileSaveMineFieldMenuItem.setMnemonic('s');
      fileSaveMineFieldMenuItem.addActionListener(msFrame);
      fileMenu.add(fileSaveMineFieldMenuItem);

      // create the game menu
      JMenu gameMenu = new JMenu("Game");
      gameMenu.setMnemonic(KeyEvent.VK_G);
      this.add(gameMenu);

      // game->New
      JMenuItem gameNewMenuItem = new JMenuItem("New");
      gameNewMenuItem.setAccelerator(KeyStroke.getKeyStroke(
              KeyEvent.VK_F2, 0));
      gameNewMenuItem.setMnemonic('n');
      gameNewMenuItem.addActionListener(msFrame);
      gameMenu.add(gameNewMenuItem);

      // separator
      gameMenu.addSeparator();

      // game->Beginner
      this.gameBeginnerMenuItem = new JCheckBoxMenuItem("Beginner");
      this.gameBeginnerMenuItem.setAccelerator(KeyStroke.getKeyStroke(
              KeyEvent.VK_B, ActionEvent.ALT_MASK));
      this.gameBeginnerMenuItem.setMnemonic('b');
      this.gameBeginnerMenuItem.addActionListener(msFrame);
      gameMenu.add(this.gameBeginnerMenuItem);

      // game->Intermediate
      this.gameIntermediateMenuItem = new JCheckBoxMenuItem("Intermediate");
      this.gameIntermediateMenuItem.setAccelerator(KeyStroke.getKeyStroke(
              KeyEvent.VK_I, ActionEvent.ALT_MASK));
      this.gameIntermediateMenuItem.setMnemonic('i');
      this.gameIntermediateMenuItem.addActionListener(msFrame);
      gameMenu.add(this.gameIntermediateMenuItem);

      // game->Expert
      this.gameExpertMenuItem = new JCheckBoxMenuItem("Expert");
      this.gameExpertMenuItem.setAccelerator(KeyStroke.getKeyStroke(
              KeyEvent.VK_E, ActionEvent.ALT_MASK));
      this.gameExpertMenuItem.setMnemonic('e');
      this.gameExpertMenuItem.addActionListener(msFrame);
      gameMenu.add(this.gameExpertMenuItem);

      // game->Custom
      gameCustomMenuItem = new JCheckBoxMenuItem("Custom...");
      this.gameCustomMenuItem.setAccelerator(KeyStroke.getKeyStroke(
              KeyEvent.VK_C, ActionEvent.ALT_MASK));
      this.gameCustomMenuItem.setMnemonic('c');
      this.gameCustomMenuItem.addActionListener(msFrame);
      gameMenu.add(this.gameCustomMenuItem);

      // separator
      gameMenu.addSeparator();

      // game->Best Times...
      JMenuItem gameBestTimesMenuItem = new JMenuItem("Best Times...");
      gameBestTimesMenuItem.setAccelerator(KeyStroke.getKeyStroke(
              KeyEvent.VK_T, ActionEvent.ALT_MASK));
      gameBestTimesMenuItem.setMnemonic('t');
      gameBestTimesMenuItem.addActionListener(msFrame);
      gameMenu.add(gameBestTimesMenuItem);

      // separator
      gameMenu.addSeparator();

      // game->Exit
      JMenuItem gameExitMenuItem = new JMenuItem("Exit");
      gameExitMenuItem.setAccelerator(KeyStroke.getKeyStroke(
              KeyEvent.VK_X, ActionEvent.ALT_MASK));
      gameExitMenuItem.setMnemonic('x');
      gameExitMenuItem.addActionListener(msFrame);
      gameMenu.add(gameExitMenuItem);

      // create the tools menu
      JMenu toolsMenu = new JMenu("Tools");
      toolsMenu.setMnemonic(KeyEvent.VK_T);
      this.add(toolsMenu);

      // tools->Use Rete Engine
      JCheckBoxMenuItem toolsUseReteMenuItem =
         new JCheckBoxMenuItem("Use Rete Engine");
      toolsUseReteMenuItem.setAccelerator(KeyStroke.getKeyStroke(
              KeyEvent.VK_R, ActionEvent.ALT_MASK));
      toolsUseReteMenuItem.setMnemonic('R');
      toolsUseReteMenuItem.addActionListener(msFrame);
      toolsUseReteMenuItem.setState(MineSweeperFrame.USE_RETE_ENGINE);
      toolsMenu.add(toolsUseReteMenuItem);

      // tools->Display Facts
      JMenuItem toolsDisplayFactsMenuItem =
         new JMenuItem("Display Facts");
      toolsDisplayFactsMenuItem.setAccelerator(KeyStroke.getKeyStroke(
              KeyEvent.VK_D, ActionEvent.ALT_MASK));
      toolsDisplayFactsMenuItem.setMnemonic('D');
      toolsDisplayFactsMenuItem.addActionListener(msFrame);
      toolsMenu.add(toolsDisplayFactsMenuItem);

      // tools->Try Combinations
      JMenuItem toolsTryCombinationsMenuItem =
         new JMenuItem("Try Combinations");
      toolsTryCombinationsMenuItem.setAccelerator(KeyStroke.getKeyStroke(
              KeyEvent.VK_Y, ActionEvent.ALT_MASK));
      toolsTryCombinationsMenuItem.setMnemonic('Y');
      toolsTryCombinationsMenuItem.addActionListener(msFrame);
      toolsMenu.add(toolsTryCombinationsMenuItem);

      // create the help menu
      JMenu helpMenu = new JMenu("Help");
      helpMenu.setMnemonic(KeyEvent.VK_H);
      this.add(helpMenu);

      // help->about
      JMenuItem helpAboutMenuItem = new JMenuItem("About");
      helpAboutMenuItem.setAccelerator(KeyStroke.getKeyStroke(
              KeyEvent.VK_A, ActionEvent.ALT_MASK));
      helpAboutMenuItem.setMnemonic('a');
      helpAboutMenuItem.addActionListener(msFrame);
      helpMenu.add(helpAboutMenuItem);

      // set menu options
      this.setMenuCheckboxItems(MineSweeperConfig.DIFFICULTY);
   }

}