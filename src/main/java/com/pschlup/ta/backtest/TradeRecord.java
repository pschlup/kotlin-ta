package com.pschlup.ta.backtest;

import com.google.common.base.Preconditions;
import java.time.Instant;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** A simulated trade record */
@Getter
@Setter
@Builder
@SuppressWarnings({"unused", "WeakerAccess"})
public class TradeRecord {

  enum CloseReason {
    EXIT, STOP_LOSS
  }

  @Builder.Default
  private final TradeType type = TradeType.LONG;

  private final Instant timestamp;
  private final double entryPrice;
  private final double amount;
  private final double trailingStopDistance;

  private double stopLossPrice;

  @Nullable
  private CloseReason closeReason;

  @Nullable
  private Double exitPrice;

  boolean isProfitable() {
    Preconditions.checkNotNull(this.exitPrice);
    return this.type == TradeType.LONG
        ? this.exitPrice > this.entryPrice
        : this.exitPrice < this.entryPrice;
  }

  double getProfitLoss() {
    Preconditions.checkNotNull(this.exitPrice);
    return this.type == TradeType.LONG
        ? amount * (this.exitPrice - this.entryPrice)
        : amount * (this.entryPrice - this.exitPrice);
  }

  double getUnrealizedProfitLoss(double currentPrice) {
    return this.type == TradeType.LONG
        ? amount * (currentPrice - this.entryPrice)
        : amount * (this.entryPrice - currentPrice);
  }

  void updateTrailingStop(double currentPrice) {
    if (this.type == TradeType.LONG) {
      if (currentPrice - trailingStopDistance > stopLossPrice) {
        stopLossPrice = currentPrice - trailingStopDistance;
      }
    } else {
      if (currentPrice + trailingStopDistance < stopLossPrice) {
        stopLossPrice = currentPrice + trailingStopDistance;
      }
    }
  }

  boolean shouldStop(double currentPrice) {
    Preconditions.checkState(this.closeReason == null);
    if (this.type == TradeType.SHORT) {
      return currentPrice > this.stopLossPrice;
    } else {
      return currentPrice < this.stopLossPrice;
    }
  }

  void exit(double exitPrice) {
    Preconditions.checkState(this.closeReason == null);
    this.closeReason = CloseReason.EXIT;
    this.exitPrice = exitPrice;
  }

  void stop(double exitPrice) {
    Preconditions.checkState(this.closeReason == null);
    this.closeReason = CloseReason.STOP_LOSS;
    this.exitPrice = exitPrice;
  }

  boolean isOpen() {
    return this.closeReason == null;
  }
}
