@file:Suppress("MemberVisibilityCanBePrivate")

package com.pschlup.ta.timeseries

import com.opencsv.CSVReader
import java.io.*
import java.time.Duration
import java.time.Instant

/**
 * A simple source of time series bars, useful for initialization and backtesting.
 *
 * The header (first row) is ignored. Extra columns are ignored.
 *
 * The file format should be:
 * ISO_timestamp;open_price;high_price;low_price;close_price
 */
object CsvBarSource {
  fun readCsvData(fileName: String): List<Bar> = readCsvData(FileReader(File(fileName)))

  fun readCsvData(input: InputStream): List<Bar> = readCsvData(InputStreamReader(input))

  fun readCsvData(reader: Reader): List<Bar> =
    CSVReader(reader).use { csvReader ->
      val rows = csvReader.readAll()
      // Detects the timeframe by peeking at some consecutive rows.
      val timeFrame = TimeFrame.of(rows[3].openTime - rows[4].openTime)
      rows
        .drop(1) // Skips headers
        .map { row: Array<String> ->
          Bar(
            timeFrame = timeFrame,
            openTime = row.openTime,
            open = row.open,
            high = row.high,
            low = row.low,
            close = row.close,
            volume = row.volume,
          )
        }
    }
}

private operator fun Instant.minus(otherInstant: Instant) = Duration.between(this, otherInstant)

// Maps row columns to OHLCV values

private val Array<String>.openTime: Instant get() = Instant.parse(this[0])
private val Array<String>.open: Double get() = this[1].toDouble()
private val Array<String>.high: Double get() = this[2].toDouble()
private val Array<String>.low: Double get() = this[3].toDouble()
private val Array<String>.close: Double get() = this[4].toDouble()
private val Array<String>.volume: Double get() = this[5].toDouble()
