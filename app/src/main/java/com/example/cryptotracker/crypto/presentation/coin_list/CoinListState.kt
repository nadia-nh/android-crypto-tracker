package com.example.cryptotracker.crypto.presentation.coin_list

import androidx.compose.runtime.Immutable
import com.example.cryptotracker.crypto.presentation.models.UICoin

@Immutable
data class CoinListState(
    val isLoading: Boolean = false,
    val coins: List<UICoin> = emptyList(),
    val selectedCoin: UICoin? = null
)
