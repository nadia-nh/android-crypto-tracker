package com.example.cryptotracker.crypto.presentation.models

import android.icu.text.NumberFormat
import androidx.annotation.DrawableRes
import com.example.cryptotracker.crypto.domain.Coin
import com.example.cryptotracker.core.presentation.util.getDrawableIdForCoin
import com.example.cryptotracker.crypto.presentation.coin_detail.DataPoint
import java.util.Locale

data class UICoin(
    val id: String,
    val rank: Int,
    val name: String,
    val symbol: String,
    val marketCapUsd: DisplayableNumber,
    val priceUsd: DisplayableNumber,
    val changePercent24Hs: DisplayableNumber,
    val coinPriceHistory: List<DataPoint> = emptyList(),
    @param:DrawableRes val iconRes: Int
)

data class DisplayableNumber(
    val value: Double,
    val formatted: String
)

fun Coin.toUICoin(): UICoin =
    UICoin(
        id = id,
        name = name,
        symbol = symbol,
        rank = rank,
        marketCapUsd = marketCapUsd.toDisplayableNumber(),
        priceUsd = priceUsd.toDisplayableNumber(),
        changePercent24Hs = changePercent24Hs.toDisplayableNumber(),
        iconRes = getDrawableIdForCoin(symbol)
    )

fun List<Coin?>.toUICoins(): List<UICoin> =
    mapNotNull { it?.toUICoin() }

private fun Double.toDisplayableNumber(): DisplayableNumber {
    val formatter = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }
    return DisplayableNumber(
        value = this,
        formatted = formatter.format(this)
    )
}

