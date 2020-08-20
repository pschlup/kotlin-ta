package com.pschlup.ta.backtest;

import com.pschlup.ta.indicators.Indicator;
import com.pschlup.ta.strategy.Strategy;
import com.pschlup.ta.strategy.Strategy.SignalType;
import com.pschlup.ta.timeseries.*;

import java.util.List;

public class BackTester {

  public static BackTestReport run(BackTestSpec spec) {
    List<Bar> inputBars = spec.getInputBars();
    TimeFrame inputTimeFrame = inputBars.get(0).getTimeFrame();

    TimeSeriesManager timeSeriesManager = new TimeSeriesManager(inputTimeFrame);
    Strategy strategy = spec.getStrategyFactory().buildStrategy(timeSeriesManager);
    Indicator stopLoss = spec.getStopLoss().buildIndicator(timeSeriesManager);
    TimeSeries runTimeSeries = timeSeriesManager.getTimeSeries(spec.getRunTimeFrame());

    TradeHistory tradeHistory = new TradeHistory(spec.getStartingBalance(), spec.getFeePerTrade());

    for (Bar inputBar : inputBars) {
      timeSeriesManager.addBar(inputBar);

      double currentPrice = inputBar.getClose();

      // Closes stopped trades
      tradeHistory.closeStoppedTrades(currentPrice);

      // Updates trade history equity, drawdown.
      tradeHistory.updateEquity(currentPrice);

      // Avoids trading before the next close.
      Bar currentBar = runTimeSeries.barAt(0);
      // TODO: improve detection of new bars in the run time series.
      if (!currentBar.getCloseTime().equals(inputBar.getCloseTime())) {
        continue;
      }

      try {
        // Moves trailing stops.
        if (spec.isTrailingStop()) {
          tradeHistory.trailStops(stopLoss.latestValue());
        }

        SignalType signal = strategy.getSignal();

        if (signal == SignalType.EXIT) {

          tradeHistory.exitActiveTrades(currentPrice);

        } else if ((signal == SignalType.ENTRY)
            && (tradeHistory.getActiveTradeCount() < spec.getPyramidingLimit())
            && tradeHistory.getBalance() > 0) {

          // Calculates 1R risk value.
          double stopLossPrice = stopLoss.latestValue();
          double risk = currentPrice - stopLossPrice;

          // Computes risk factor. Risks max betSize% of the account on each trade.
          double maxBalanceRisk = tradeHistory.getBalance() * spec.getBetSize();
          double amount = maxBalanceRisk / risk;

          // Avoids spending more than the available balance.
          if (amount > tradeHistory.getBalance() / currentPrice) {
            amount = tradeHistory.getBalance() / currentPrice;
          }

          tradeHistory.add(
              TradeRecord.builder()
                  .type(spec.getTradeType())
                  .timestamp(currentBar.getCloseTime())
                  .entryPrice(currentPrice)
                  .amount(amount)
                  .stopLossPrice(stopLossPrice)
                  .trailingStopDistance(currentPrice - stopLossPrice)
                  .build());
        }
      } catch(UnstablePeriodException e) {
        // Ignores signals during unstable period.
      }
    }

    // Closes trades that remain open in the end.
    double lastPrice = runTimeSeries.getLatestBar().map(Bar::getClose).orElse(Double.NaN);
    tradeHistory.exitActiveTrades(lastPrice);

    double startPrice = runTimeSeries.getOldestBar().map(Bar::getOpen).orElse(Double.NaN);
    return BackTestReport.builder()
        .trades(tradeHistory.getTrades())
        .finalBalance(tradeHistory.getBalance())
        .maxDrawDown(tradeHistory.getMaxDrawDown())
        .startPrice(startPrice)
        .endPrice(lastPrice)
        .spec(spec)
        .build();
  }
}
