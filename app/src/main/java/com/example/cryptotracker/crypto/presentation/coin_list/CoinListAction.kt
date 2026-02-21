package com.example.cryptotracker.crypto.presentation.coin_list

import com.example.cryptotracker.crypto.presentation.models.UICoin

sealed interface CoinListAction {
    data class onCoinClick(val uiCoin: UICoin): CoinListAction
    data object OnRefresh: CoinListAction
}
