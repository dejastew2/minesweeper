import java.util.*;

public class ComboTest2
{

   public static void main(String[] args) {
      char[] vertices = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm'};
      int numCombos = 7;
      char[] curCombo = new char[numCombos];
      int[] comboIndxs = new int[numCombos];
      int lastComboIndx = comboIndxs.length - 1;
      comboIndxs[0] = 0;

      // verify data
      assert (numCombos <= vertices.length);

      // generate initial combo
      int curComboIndx = 1;
      int comboCount = 0;
      for (int i = 0; curComboIndx < numCombos; i++) {
         if (i == comboIndxs[0]) {
            i++;
         }
         comboIndxs[curComboIndx++] = i;
      }

      boolean comboExists = true;
      while (comboExists) {
         // generate a combo
         for (int j = 0; j < numCombos; j++) {
            curCombo[j] = vertices[comboIndxs[j]];
         }

         // print current combo
         System.out.print(++comboCount + ": ");
         ComboTest.printCurCombo(curCombo);

         // generate new combo
         comboExists = false;

         // first try to increment the last marker
         int nextIndx = comboIndxs[lastComboIndx] + 1;
         if (nextIndx != comboIndxs[0] && nextIndx < vertices.length) {
            comboIndxs[lastComboIndx]++;
            comboExists = true;
         }
         else if (nextIndx == comboIndxs[0] &&
                  (nextIndx + 1) < vertices.length) {
            comboIndxs[lastComboIndx] += 2;
            comboExists = true;
         }
         else { // if we can't, go backwards
            for (int m = lastComboIndx - 1; m > 0; m--) {
               if (((comboIndxs[m] + 1) == comboIndxs[m + 1]) ||
                   ((comboIndxs[m] + 1) == comboIndxs[0] &&
                    (comboIndxs[m] + 2) == comboIndxs[m + 1])
                  ) {
                  continue;
               }

               comboIndxs[m] +=
                  (((comboIndxs[m] + 1) == comboIndxs[0]) ? 2 : 1);

               // then go forward
               for (int u = m + 1; u < comboIndxs.length; u++) {
                  comboIndxs[u] = comboIndxs[u - 1] + 1;
                  if (comboIndxs[u] == comboIndxs[0]) {
                     comboIndxs[u]++;
                  }
               }

               comboExists = true;
               break;
            }
         }
      }
   }

   public static void printCurCombo(char[] curCombo) {
      for (int i = 0; i < curCombo.length; i++) {
         System.out.print(curCombo[i]);
      }
      System.out.println();
   }

}