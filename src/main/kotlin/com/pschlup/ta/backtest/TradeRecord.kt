package com.pschlup.ta.backtest

import com.pschlup.ta.backtest.TradeType.LONG
import com.pschlup.ta.backtest.TradeType.SHORT
import java.time.Instant

enum class CloseReason {
  EXIT, STOP_LOSS
}

/** A simulated trade record. */
data class TradeRecord(
  val type: TradeType = LONG,
  val timestamp: Instant? = null,
  val entryPrice: Double = 0.0,
  val amount: Double = 0.0,
  val trailingStopDistance: Double = 0.0,
  var stopLossPrice: Double = 0.0,
  var closeReason: CloseReason? = null,
  var exitPrice: Double? = null,
) {

  val isProfitable: Boolean
    get() = requireNotNull(exitPrice).let { exitPrice ->
      when (type) {
        LONG -> exitPrice > entryPrice
        SHORT -> exitPrice < entryPrice
      }
    }

  val profitLoss: Double
    get() = requireNotNull(exitPrice).let { exitPrice ->
      when (type) {
        LONG -> amount * (exitPrice - entryPrice)
        SHORT -> amount * (entryPrice - exitPrice)
      }
    }

  fun getUnrealizedProfitLoss(currentPrice: Double): Double =
    when (type) {
      LONG -> amount * (currentPrice - entryPrice)
      SHORT -> amount * (entryPrice - currentPrice)
    }

  fun updateTrailingStop(currentPrice: Double) {
    when (type) {
      LONG -> {
        if (currentPrice - trailingStopDistance > stopLossPrice) {
          stopLossPrice = currentPrice - trailingStopDistance
        }
      }
      SHORT -> {
        if (currentPrice + trailingStopDistance < stopLossPrice) {
          stopLossPrice = currentPrice + trailingStopDistance
        }
      }
    }
  }

  fun shouldStop(currentPrice: Double): Boolean {
    require(isOpen)
    return when (type) {
      LONG -> currentPrice < stopLossPrice
      SHORT -> currentPrice > stopLossPrice
    }
  }

  fun exit(exitPrice: Double) {
    require(isOpen)
    closeReason = CloseReason.EXIT
    this.exitPrice = exitPrice
  }

  fun stop(exitPrice: Double) {
    require(isOpen)
    closeReason = CloseReason.STOP_LOSS
    this.exitPrice = exitPrice
  }

  val isOpen: Boolean
    get() = closeReason == null
}
