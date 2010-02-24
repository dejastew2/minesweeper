package com.mineSweeper.jessFunctions;

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
import jess.*;

// flags a square in the minefield (called from the Jess code)
public class FlagSquare implements Userfunction {
   MineFieldPanel mfPanel;

   public FlagSquare(MineFieldPanel theMfPanel) {
      // make sure there's no null values
      assert(theMfPanel != null);

      this.mfPanel = theMfPanel;
   }

   // the name of the jess function
   public String getName() {
      return "flag-square";
   }

   // called from the jess code
   public Value call(ValueVector vv, Context context) throws JessException {
      // make sure there's no null values
      assert(vv != null && context != null);

      int mineButtonId = vv.get(1).intValue(context);

      this.mfPanel.flagMineSquare(
         mineButtonId / MineSweeperConfig.WIDTH,
         mineButtonId % MineSweeperConfig.WIDTH, true);

      return new Value(true);
   }

}
