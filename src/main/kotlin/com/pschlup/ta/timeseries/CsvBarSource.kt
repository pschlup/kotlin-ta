@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.pschlup.ta.timeseries

import com.opencsv.CSVReader
import java.io.*
import java.time.Duration
import java.time.Instant

/**
 * A simple source of time series bars, useful for initialization and backtesting.
 *
 * The file format should strictly be:
 *    ISO_timestamp,open_price,high_price,low_price,close_price
 *
 * The header (first row) and extra columns are ignored.
 *
 * For example:
 *    "date","open","high","low","close","volume"
 *    "2019-04-19T18:35:00Z","5268.85093244","5270.56328683","5268.85093244","5270.56328683","552.51720638"
 *    "2019-04-19T18:40:00Z","5268.03011026","5277.2151644","5268.03011026","5276.31485716","2358.48058912"
 */
fun readCsvBars(fileName: String): List<Bar> = readCsvBars(FileReader(File(fileName)))
fun readCsvBars(input: InputStream): List<Bar> = readCsvBars(InputStreamReader(input))
fun readCsvBars(reader: Reader): List<Bar> =
  CSVReader(reader).use { csvReader ->
    val rows = csvReader.readAll()
    // Detects the timeframe by peeking at some consecutive rows.
    val timeFrame = TimeFrame.of(duration = rows[3].openTime - rows[2].openTime)
    rows
      .drop(1) // Skips headers
      .map { row ->
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

private operator fun Instant.minus(otherInstant: Instant) = Duration.between(this, otherInstant).abs()

// Maps row columns to OHLCV values

private val Array<String>.openTime: Instant get() = Instant.parse(this[0])
private val Array<String>.open: Double get() = this[1].toDouble()
private val Array<String>.high: Double get() = this[2].toDouble()
private val Array<String>.low: Double get() = this[3].toDouble()
private val Array<String>.close: Double get() = this[4].toDouble()
private val Array<String>.volume: Double get() = this[5].toDouble()
