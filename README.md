# talib - Kotlin Technical Analysis library

A TA (Technical Analysis) and backtesting library for idiomatic Kotlin which supports multi-timeframe analysis.

## Demo

Run backtest demo:
```bash
$ mvn
```

Which should output a report similar to this:
```
----------------------------------
Finished backtest with 306 trades
----------------------------------
Pyramiding limit   : 2
Account risk/trade : 2.0%
----------------------------------
Profitability      : 270.98%
Buy-and-hold       : 366.08%
Vs buy-and-hold    : -25.98%
Win rate           : 37.58%
Max drawdown       : 35.92%
Risk/reward ratio  : 2.82
Sortino ratio      : 7.54
----------------------------------
Start balance      : $20’000.00
Profit/loss        : $54’195.38
Final balance      : $61’521.90
----------------------------------
```

Refer to the `BackTestDemo.kt` file to see how the demo works.

## Usage

### Time series
Time series are managed by the TimeSeriesManager class. It has an input time frame that should match your input
time frame or at least the fastest time frame you intend to use in your analysis.

```kotlin
// 
val timeSeriesManager = TimeSeriesManager(inputTimeFrame = TimeFrame.M15)

// Obtain a TimeSeries for a specific time frame:
val oneHourSeries = timeSeriesManager.h1
val fifteenMinuteSeries = timeSeriesManager.m15
```

A simple helper for reading chart bars (candlesticks) from CSV files is included:

```kotlin
val bars = CsvBarSource.readCsvData("chart_data_BTC_USDT_p5_730d.csv")

for (val bar in bars) {
  timeSeriesManager += bar
  // Do something after each bar
}
```

Each added chart bar affects all time series generated from the same TimeSeriesManager.

### Indicators

A few basic indicators are provided. These can be combined in multiple ways:

```kotlin
val closePrice = oneHourSeries.closePrice
val ema15 = closePrice.exponentialMovingAverage(length = 15)
val ema30 = closePrice.exponentialMovingAverage(length = 30)
val rsi = closePrice.rsi() // Standard RSI with length 14
```

A couple of stop-loss indicators are also provided:

```kotlin
val fixedStopPosition =  oneHourSeries.volatilityStop(length = 10, atrMultiplier = 3.0)
```

Indicators are dynamic and calculated on-demand, i.e. their values change as new data is added
to the time series. You can obtain the value of an indicator at each chart bar by using it's index:

```kotlin
val currentPrice = closePrice.latestValue // Same as closePrice[0]
val previousPrice = closePrice[1] // The closing price 1 bar ago
```

Note that the bar indices depend on the time frame where the indicator is operating. In the examples above `closePrice[1]`
corresponds to the price 1 hour before because the indicator is based on the one-hour time frame.

### Signals

Indicators can be combined to extract signals:

```kotlin
val crossOver = ema15 crossedOver ema30
val crossUnder = ema15 crossedUnder ema30
val rsiIsHigh = rsi isOver 70.0
val rsiIsLow = rsi isUnder 30.0
```

Signals can be further combined using boolean operations:

```kotlin
val entrySignal = (ema15 crossedOver ema30) and (rsi isOver 70.0)
val exitSignal = (ema15 crossedUnder ema30) and (rsi isUnder 30.0)
```

Like Indicators, signals are dynamic and are recalculated at each bar position.

```kotlin
val shouldEnterTrade: Boolean = entrySignal.latestValue // Same as entrySignal[0]
val shouldExitTrade: Boolean = entrySignal.latestValue // Same as entrySignal[0]
```
