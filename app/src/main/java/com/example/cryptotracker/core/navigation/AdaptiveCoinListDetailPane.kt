package com.example.cryptotracker.core.navigation

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cryptotracker.core.presentation.util.ObserveAsEvents
import com.example.cryptotracker.core.presentation.util.toString
import com.example.cryptotracker.crypto.presentation.coin_detail.CoinDetailScreen
import com.example.cryptotracker.crypto.presentation.coin_list.CoinListAction
import com.example.cryptotracker.crypto.presentation.coin_list.CoinListEvent
import com.example.cryptotracker.crypto.presentation.coin_list.CoinListScreen
import com.example.cryptotracker.crypto.presentation.coin_list.CoinListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AdaptiveCoinListDetailPane(
    modifier: Modifier = Modifier,
    viewModel: CoinListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val events = viewModel.events
    val context = LocalContext.current

    ObserveAsEvents(events) { event ->
        handleEvent(event, context)
    }

    val navigator = rememberListDetailPaneScaffoldNavigator<Any>()
    val scope = rememberCoroutineScope()
    NavigableListDetailPaneScaffold(
        navigator = navigator,
        modifier = modifier,
        listPane = {
            AnimatedPane {
                CoinListScreen(
                    state = state,
                    onAction = { action ->
                        onAction(action, viewModel, navigator, scope)
                    }
                )
            }
        },
        detailPane = {
            AnimatedPane {
                CoinDetailScreen(
                    state = state
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun onAction(
    action: CoinListAction,
    viewModel: CoinListViewModel,
    navigator: ThreePaneScaffoldNavigator<Any>,
    scope: CoroutineScope
) {
    viewModel.onAction(action)

    when (action) {
        is CoinListAction.onCoinClick -> {
            scope.launch {
                navigator.navigateTo(
                    pane = ListDetailPaneScaffoldRole.Detail
                )
            }
        }
        else -> {}
    }
}
private fun handleEvent(
    event: CoinListEvent,
    context: Context
) {
    when (event) {
        is CoinListEvent.Error -> {
            val message = event.error.toString(context)
            Toast
                .makeText(context, message, Toast.LENGTH_SHORT)
                .show()
        }
    }
}
