package com.pschlup.ta.timeseries;

import static java.lang.Double.parseDouble;
import static java.util.stream.Collectors.toList;

import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * A simple source of time series bars, useful for initialization and backtesting.
 * <p>
 *   The header (first row) is ignored. Extra columns are ignored.
 * <p>
 *   The file format should be:
 *   ISO_timestamp;open_price;high_price;low_price;close_price
 */
@SuppressWarnings("unused, WeakerAccess")
public class CsvBarSource {

  public static List<Bar> readCsvData(String fileName) throws IOException {
    return CsvBarSource.readCsvData(new FileReader(new File(fileName)));
  }

  public static List<Bar> readCsvData(InputStream input) throws IOException {
    return CsvBarSource.readCsvData(new InputStreamReader(input));
  }

  public static List<Bar> readCsvData(Reader reader) throws IOException {
    try (CSVReader csvReader = new CSVReader(reader)) {
      List<String[]> rows = csvReader.readAll();
      // Detects the timeframe by peeking at some consecutive rows.
      TimeFrame timeFrame = TimeFrame.of(
          Duration.between(Instant.parse(rows.get(3)[0]), Instant.parse(rows.get(4)[0])));
      return rows.stream()
          // Skips headers
          .skip(1)
          .map(row -> new Bar(
              timeFrame,
              Instant.parse(row[0]),
              parseDouble(row[1]),  // open
              parseDouble(row[2]),  // high
              parseDouble(row[3]),  // low
              parseDouble(row[4]),  // close
              parseDouble(row[5]))) // volume
          .collect(toList());
    }
  }
}
