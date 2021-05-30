@file:Suppress("unused")

package com.pschlup.ta.indicators

/**
 * Simple linear regression slope indicator.
 * {@see http://introcs.cs.princeton.edu/java/97data/LinearRegression.java.html}
 */
fun Indicator.linearRegressionSlope(length: Int = 20): Indicator {
  return Indicator { index ->
    // First pass: compute xBar and yBar.
    var sumX = 0.0
    var sumY = 0.0
    for (i in index until index + length) {
      // index grows in reverse direction so "i" is negative
      sumX -= i
      sumY += this[i]
    }
    val xBar = sumX / length
    val yBar = sumY / length

    // Second pass: compute slope.
    var xxBar = 0.0
    var xyBar = 0.0
    for (i in index until index + length) {
      // index grows in reverse direction so "i" is negative
      val dX = -i - xBar
      val dY = this[i] - yBar
      xxBar += dX * dX
      xyBar += dX * dY
    }

    // Returns the slope only.
    return@Indicator xyBar / xxBar
  }
}
