package com.mineSweeper.ui;

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


import javax.swing.*;
import javax.swing.text.*;

import java.awt.Toolkit;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
// this class extends JTextField so that there can only// be whole numbers in the textField
public class WholeNumberField extends JTextField {
   private Toolkit toolkit;
   private NumberFormat integerFormatter;

   public WholeNumberField(int value, int columns) {
      super(columns);
      toolkit = Toolkit.getDefaultToolkit();
      integerFormatter = NumberFormat.getNumberInstance(Locale.US);
      integerFormatter.setParseIntegerOnly(true);
      setValue(value);
   }

   public int getValue() {
      int retVal = 0;
      try {
         retVal = integerFormatter.parse(getText()).intValue();
      }
      catch (ParseException e) {
         // this should never happen because insertString allows
         // only properly formatted data to get in the field.
         e.printStackTrace(System.out);
      }

      return retVal;
   }

   public void setValue(int value) {
      setText(integerFormatter.format(value));
   }

   protected Document createDefaultModel() {
      return new WholeNumberDocument();
   }

   protected class WholeNumberDocument extends PlainDocument {
      public void insertString(int offs, String str, AttributeSet a)
         throws BadLocationException {
         // make sure there's no null values
         assert(str != null);

         char[] source = str.toCharArray();
         char[] result = new char[source.length];
         int j = 0;

         for (int i = 0; i < result.length; i++) {
            if (Character.isDigit(source[i])) {
               result[j++] = source[i];
            }
            else {               toolkit.beep();
            }
         }

         super.insertString(offs, new String(result, 0, j), a);
      }
   }
}
