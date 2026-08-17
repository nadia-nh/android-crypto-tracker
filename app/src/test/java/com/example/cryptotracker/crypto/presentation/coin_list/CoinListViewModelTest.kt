package com.example.cryptotracker.crypto.presentation.coin_list

import android.app.Application
import com.example.cryptotracker.core.domain.util.NetworkError
import com.example.cryptotracker.core.domain.util.Result
import com.example.cryptotracker.crypto.domain.Coin
import com.example.cryptotracker.crypto.domain.CoinDataSource
import com.example.cryptotracker.crypto.domain.CoinPrice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.ZonedDateTime

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = Application::class)
class CoinListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadCoinsPopulatesCoinsOnSuccess() = runTest(testDispatcher) {
        val viewModel = CoinListViewModel(FakeCoinDataSource(getCoinsResult = Result.Success(listOf(btcCoin))))
        val job = collectState(viewModel)
        advanceUntilIdle()
        job.cancel()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(1, state.coins.size)
        assertEquals("Bitcoin", state.coins.first().name)
    }

    @Test
    fun loadCoinsErrorClearsLoadingAndEmitsErrorEvent() = runTest(testDispatcher) {
        val viewModel = CoinListViewModel(
            FakeCoinDataSource(getCoinsResult = Result.Error(NetworkError.NO_INTERNET_CONNECTION))
        )

        val events = mutableListOf<CoinListEvent>()
        val eventJob = launch { viewModel.events.collect { events.add(it) } }
        val stateJob = collectState(viewModel)
        advanceUntilIdle()

        stateJob.cancel()
        eventJob.cancel()

        assertFalse(viewModel.state.value.isLoading)
        assertTrue(viewModel.state.value.coins.isEmpty())
        assertEquals(1, events.size)
        assertEquals(NetworkError.NO_INTERNET_CONNECTION, (events[0] as CoinListEvent.Error).error)
    }

    @Test
    fun onCoinClickSetsSelectedCoin() = runTest(testDispatcher) {
        val viewModel = CoinListViewModel(FakeCoinDataSource(getCoinsResult = Result.Success(listOf(btcCoin))))
        val job = collectState(viewModel)
        advanceUntilIdle()

        val uiCoin = viewModel.state.value.coins.first()
        viewModel.onAction(CoinListAction.onCoinClick(uiCoin))
        advanceUntilIdle()
        job.cancel()

        assertEquals(uiCoin.id, viewModel.state.value.selectedCoin?.id)
    }

    @Test
    fun onCoinClickFetchesHistoryAndPopulatesCoinPriceHistory() = runTest(testDispatcher) {
        val history = listOf(
            CoinPrice(priceUsd = 50_000.0, dateTime = ZonedDateTime.now().minusHours(6)),
            CoinPrice(priceUsd = 51_000.0, dateTime = ZonedDateTime.now())
        )
        val viewModel = CoinListViewModel(
            FakeCoinDataSource(
                getCoinsResult = Result.Success(listOf(btcCoin)),
                getCoinHistoryResult = Result.Success(history)
            )
        )
        val job = collectState(viewModel)
        advanceUntilIdle()

        viewModel.onAction(CoinListAction.onCoinClick(viewModel.state.value.coins.first()))
        advanceUntilIdle()
        job.cancel()

        assertEquals(2, viewModel.state.value.selectedCoin?.coinPriceHistory?.size)
    }

    @Test
    fun onCoinClickHistoryErrorEmitsErrorEvent() = runTest(testDispatcher) {
        val viewModel = CoinListViewModel(
            FakeCoinDataSource(
                getCoinsResult = Result.Success(listOf(btcCoin)),
                getCoinHistoryResult = Result.Error(NetworkError.SERVER_ERROR)
            )
        )

        val events = mutableListOf<CoinListEvent>()
        val eventJob = launch { viewModel.events.collect { events.add(it) } }
        val stateJob = collectState(viewModel)
        advanceUntilIdle()

        viewModel.onAction(CoinListAction.onCoinClick(viewModel.state.value.coins.first()))
        advanceUntilIdle()

        stateJob.cancel()
        eventJob.cancel()

        assertEquals(1, events.size)
        assertEquals(NetworkError.SERVER_ERROR, (events[0] as CoinListEvent.Error).error)
    }

    @Test
    fun onRefreshReloadsCoinList() = runTest(testDispatcher) {
        val viewModel = CoinListViewModel(FakeCoinDataSource(getCoinsResult = Result.Success(listOf(btcCoin))))
        val job = collectState(viewModel)
        advanceUntilIdle()

        viewModel.onAction(CoinListAction.OnRefresh)
        advanceUntilIdle()
        job.cancel()

        assertFalse(viewModel.state.value.isLoading)
        assertEquals(1, viewModel.state.value.coins.size)
    }

    // region helpers

    private fun CoroutineScope.collectState(viewModel: CoinListViewModel): Job =
        launch { viewModel.state.collect { } }

    private val btcCoin = Coin(
        id = "bitcoin",
        rank = 1,
        name = "Bitcoin",
        symbol = "BTC",
        marketCapUsd = 1_000_000.0,
        priceUsd = 50_000.0,
        changePercent24Hs = 2.5
    )

    private class FakeCoinDataSource(
        private val getCoinsResult: Result<List<Coin>, NetworkError> = Result.Success(emptyList()),
        private val getCoinHistoryResult: Result<List<CoinPrice>, NetworkError> = Result.Success(emptyList())
    ) : CoinDataSource {
        override suspend fun getCoins() = getCoinsResult
        override suspend fun getCoinHistory(coinId: String, start: ZonedDateTime, end: ZonedDateTime) =
            getCoinHistoryResult
    }

    // endregion
}
