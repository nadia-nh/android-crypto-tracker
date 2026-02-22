package com.example.cryptotracker.crypto.presentation.coin_list

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.example.cryptotracker.core.presentation.util.toString
import com.example.cryptotracker.crypto.presentation.coin_list.components.CoinListItem
import com.example.cryptotracker.crypto.presentation.coin_list.components.previewCoin
import com.example.cryptotracker.crypto.presentation.models.toUICoin
import com.example.cryptotracker.ui.theme.CryptoTrackerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.withContext

@Composable
fun CoinListScreen (
    modifier: Modifier = Modifier,
    state: CoinListState,
    events: Flow<CoinListEvent> = emptyFlow(),
)
{
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(key1 = lifecycleOwner.lifecycle) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(
            Lifecycle.State.STARTED) {
            // Ensure events don't get lost
            withContext(Dispatchers.Main.immediate) {
                events.collect { event ->
                    handleEvent(event, context)
                }
            }
        }
    }

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {

        LazyColumn(
            modifier = modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.coins) { coin ->
                CoinListItem(
                    modifier = Modifier.fillMaxWidth(),
                    uiCoin = coin,
                    onItemClick = { },
                )
                HorizontalDivider()
            }
        }
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

@Preview
@PreviewLightDark
@PreviewDynamicColors
@Composable
fun CoinListScreenPreview() {
    val coins = (1 .. 10).map {
        previewCoin.toUICoin().copy(id = it.toString())
    }
    CryptoTrackerTheme() {
        CoinListScreen(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background),
            state = CoinListState(
                coins = coins
            )
        )
    }
}
