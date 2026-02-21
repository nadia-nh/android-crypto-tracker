package com.example.cryptotracker.crypto.presentation.coin_list

import androidx.lifecycle.ViewModel
import com.example.cryptotracker.crypto.domain.CoinDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CoinListViewModel(): ViewModel() {
    private val _state = MutableStateFlow(CoinListState())
    val state = _state.asStateFlow()
}
