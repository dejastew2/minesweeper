package com.mineSweeper.util;

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
import java.awt.image.*;
import javax.swing.*;

// this class contains methods to read images from files
public abstract class ImageUtil {
   private static Toolkit toolkit = Toolkit.getDefaultToolkit();
   private static Image SMILEYS_IMAGE;
   private static ImageProducer SMILEYS_IMAGE_PRODUCER;
   private static final int NUM_FACE_IMAGES = 4;
   private static final int FACE_ICON_SIZE = 17;
   public static Image[] FACE_IMAGES = new Image[ImageUtil.NUM_FACE_IMAGES];
   public static Image MINE_IMAGE;
   public static Image FLAG_IMAGE;
   private static Image DIGIT_IMAGE;
   public static ImageIcon OPEN_IMAGE;
   public static ImageIcon SAVE_IMAGE;
   private static ImageProducer DIGITS_IMAGE_PRODUCER;
   public static final int NEGATIVE_SIGN_INDEX = 10;
   private static final int NUM_DIGIT_IMAGES = 11;
   public static final int DIGIT_ICON_WIDTH  = 13;
   public static final int DIGIT_ICON_HEIGHT = 24;
   public static Image[] DIGIT_IMAGES = new Image[ImageUtil.NUM_DIGIT_IMAGES];

   static {
      MediaTracker mt = new MediaTracker(new JPanel());

      // read the file->open image
      ImageUtil.OPEN_IMAGE =
         new ImageIcon(ImageUtil.toolkit.getImage("../images/open.gif"));

      // read the file->save image
      ImageUtil.SAVE_IMAGE =
         new ImageIcon(ImageUtil.toolkit.getImage("../images/save.gif"));

      // read the smiley faces images
      ImageUtil.SMILEYS_IMAGE =
         ImageUtil.toolkit.getImage("../images/smileys.gif");
      mt.addImage(ImageUtil.SMILEYS_IMAGE, 0);

      // read the mine image
      ImageUtil.MINE_IMAGE =
         ImageUtil.toolkit.getImage("../images/transMine.gif");
      mt.addImage(ImageUtil.MINE_IMAGE, 0);

      // read the flag image
      ImageUtil.FLAG_IMAGE =
         ImageUtil.toolkit.getImage("../images/flag.gif");
      mt.addImage(ImageUtil.FLAG_IMAGE, 0);

      // read the digits for the digits panel
      ImageUtil.DIGIT_IMAGE =
         ImageUtil.toolkit.getImage("../images/digits.gif");
      mt.addImage(ImageUtil.DIGIT_IMAGE, 0);

      // wait for images to get loaded
      try {
         mt.waitForID(0);
      }
      catch (Exception e) {
         e.printStackTrace(System.out);
      }

      ImageUtil.SMILEYS_IMAGE_PRODUCER = ImageUtil.SMILEYS_IMAGE.getSource();
      ImageUtil.DIGITS_IMAGE_PRODUCER  = ImageUtil.DIGIT_IMAGE.getSource();

      // get face images
      for (int i = 0; i < ImageUtil.NUM_FACE_IMAGES; i++) {
         ImageUtil.FACE_IMAGES[i] =
            ImageUtil.toolkit.createImage(
               new FilteredImageSource(
                  ImageUtil.SMILEYS_IMAGE_PRODUCER,
                  new CropImageFilter(ImageUtil.FACE_ICON_SIZE * i, 0,
                                      ImageUtil.FACE_ICON_SIZE,
                                      ImageUtil.FACE_ICON_SIZE)));
         mt.addImage(ImageUtil.FACE_IMAGES[i], 1);
      }

      // get digits
      for (int i = 0; i < ImageUtil.NUM_DIGIT_IMAGES; i++) {
         ImageUtil.DIGIT_IMAGES[i] =
            ImageUtil.toolkit.createImage(
               new FilteredImageSource(
                  ImageUtil.DIGITS_IMAGE_PRODUCER,
                  new CropImageFilter(ImageUtil.DIGIT_ICON_WIDTH * i, 0,
                                      ImageUtil.DIGIT_ICON_WIDTH,
                                      ImageUtil.DIGIT_ICON_HEIGHT)));
         mt.addImage(ImageUtil.DIGIT_IMAGES[i], 1);
      }

      // wait for images to get loaded
      try {
         mt.waitForID(1);
      }
      catch (Exception e) {
         e.printStackTrace(System.out);
      }
   }

}