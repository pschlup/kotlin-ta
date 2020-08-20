package com.pschlup.ta.backtest;

import com.pschlup.ta.indicators.Indicator;
import com.pschlup.ta.strategy.Strategy;
import com.pschlup.ta.timeseries.Bar;
import com.pschlup.ta.timeseries.TimeFrame;
import com.pschlup.ta.timeseries.TimeSeriesManager;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.List;

/** Specifies the configuration for a back test run. */
@Getter
@Builder
@SuppressWarnings("UnusedAssignment")
public class BackTestSpec {

  @Builder.Default
  private TradeType tradeType = TradeType.LONG;

  /**
   * The base time frame at which the back tester runs.
   * <p>This is typically the time frame of the fastest indicator used in signals.
   **/
  @NonNull
  private TimeFrame runTimeFrame;

  /**
   * The strategy to back test.
   */
  @NonNull
  private StrategyFactory strategyFactory;

  /**
   * The input bars to use in backtesting. Must be in the same time frame as the base time frame, or faster.
   **/
  @NonNull
  private List<Bar> inputBars;

  /** The stop loss level */
  @NonNull
  private StopLossFactory stopLoss;

  @Builder.Default
  private boolean trailingStop = false;

  /** Level at which we exit 50% of the position. */
  @Nullable
  private Indicator takeProfitIndicator;

  /** The % of the account to risk on each trade. 0.01 = 1% */
  @Builder.Default
  private double betSize = 0.02;

  /** The initial account balance, in counter currency amount. */
  @Builder.Default
  private double startingBalance = 10_000;

  /** The average fee % charged by the exchange in each trade. */
  @Builder.Default
  private double feePerTrade = 0.001;

  @Builder.Default
  int pyramidingLimit = 1;

  public interface StopLossFactory {
    Indicator buildIndicator(TimeSeriesManager timeSeriesManager);
  }

  public interface StrategyFactory {
    Strategy buildStrategy(TimeSeriesManager timeSeriesManager);
  }
}
