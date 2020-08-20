package com.pschlup.ta.indicators;

import com.pschlup.ta.helpers.MockTimeSeries;
import com.pschlup.ta.timeseries.UnstablePeriodException;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LinearRegressionSlopeTest {

  @Test
  void shouldComputeSlopeOf1() {
    Indicator source = MockTimeSeries.of(new double[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}).closePrice();
    LinearRegressionSlope slope = LinearRegressionSlope.of(source).withLength(10);
    assertThat(slope.valueAt(0)).isEqualTo(1.);
  }

  @Test
  void shouldComputeSlopeOf2() {
    Indicator source = MockTimeSeries.of(new double[] {0, 2, 4, 6, 8, 10, 12, 14, 16, 18}).closePrice();
    LinearRegressionSlope slope = LinearRegressionSlope.of(source).withLength(10);
    assertThat(slope.valueAt(0)).isEqualTo(2.);
  }

  @Test
  void shouldComputeSlopeOfHalf() {
    Indicator source = MockTimeSeries.of(new double[] {0, 0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4}).closePrice();
    LinearRegressionSlope slope = LinearRegressionSlope.of(source).withLength(8);
    assertThat(slope.valueAt(0)).isEqualTo(.5);
  }

  @Test
  void shouldComputeSlopeOfZero() {
    Indicator source = MockTimeSeries.of(new double[] {1, 2, 3, 2, 1, 2, 3, 2, 1}).closePrice();
    LinearRegressionSlope slope = LinearRegressionSlope.of(source).withLength(9);
    assertThat(slope.valueAt(0)).isEqualTo(0.);
  }

  @Test
  void shouldThrowIfUnstable() {
    Indicator source = MockTimeSeries.of(new double[] {1, 2, 3}).closePrice();
    LinearRegressionSlope slope = LinearRegressionSlope.of(source).withLength(20);
    assertThrows(UnstablePeriodException.class, () -> slope.valueAt(0));
  }
}
