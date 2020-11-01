/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package so.madprogrammer;

import java.awt.*;

public class ScaleUtil {
  public static double getScaleFactor(int iMasterSize, int iTargetSize) {
    return (double) iTargetSize / (double) iMasterSize;
  }

  public static double getScaleFactorToFit(Dimension original, Dimension toFit) {
    double dScale = 1d;

    if (original != null && toFit != null) {
      double dScaleWidth = getScaleFactor(original.width, toFit.width);
      double dScaleHeight = getScaleFactor(original.height, toFit.height);

      dScale = Math.min(dScaleHeight, dScaleWidth);
    }

    return dScale;
  }

  public static double getScaleFactorToFill(Dimension masterSize, Dimension targetSize) {
    double dScaleWidth = getScaleFactor(masterSize.width, targetSize.width);
    double dScaleHeight = getScaleFactor(masterSize.height, targetSize.height);

    return Math.max(dScaleHeight, dScaleWidth);
  }
}
