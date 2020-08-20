package com.pschlup.ta.indicators;

import com.pschlup.ta.timeseries.Bar;
import com.pschlup.ta.timeseries.CsvBarSource;
import com.pschlup.ta.timeseries.TimeSeries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

class AverageTrueRangeTest {

  private TimeSeries timeSeries;

  @BeforeEach
  void setup() throws IOException {
    List<Bar> bars = CsvBarSource.readCsvData(
        AverageTrueRangeTest.class.getResourceAsStream("/test_chart_data.csv"));
    this.timeSeries = TimeSeries.of(bars);
  }

  @Test
  void shouldComputeValue() {
    AverageTrueRange atr = AverageTrueRange.of(timeSeries).withLength(14);
    assertThat(atr.valueAt(0))
        .isWithin(0.0001).of(49.49144);
    assertThat(atr.valueAt(1))
        .isWithin(0.0001).of(49.43149);
  }
}
