import com.mineSweeper.gui.*;
import com.mineSweeper.util.*;
import java.io.*;

public class StartMinesweeper {

   public static void main(String[] args) {
      // get minesweeper configuration
      if (!FileUtil.setMineSweeperConfiguration(
            MineSweeperFrame.CONFIG_FILE)) {
         // if we can't read the configuration from a file,
         //  read it off the commandline
         if (args.length == 3) {
            MineSweeperConfig.HEIGHT = Integer.parseInt(args[1]);
            MineSweeperConfig.WIDTH  = Integer.parseInt(args[0]);
            MineSweeperConfig.MINES  = Integer.parseInt(args[2]);
         }
         else if (args.length == 2) {
            MineSweeperConfig.HEIGHT = Integer.parseInt(args[1]);
            MineSweeperConfig.WIDTH  = Integer.parseInt(args[0]);
            MineSweeperConfig.MINES  =
               MineSweeperConfig.WIDTH * MineSweeperConfig.HEIGHT -
               MineSweeperConfig.WIDTH - MineSweeperConfig.HEIGHT + 1;
         }
         else {
            System.out.println("Minesweeper command line arguments:\n" +
                               "<height> <width> <number of mines>");
            MineSweeperConfig.HEIGHT = 8;
            MineSweeperConfig.WIDTH  = 8;
            MineSweeperConfig.MINES  = 10;
         }
      }

      // construct a new minesweeper frame
      new MineSweeperFrame();
   }

}