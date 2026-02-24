package com.example.cryptotracker.crypto.presentation.coin_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptotracker.R
import com.example.cryptotracker.crypto.presentation.coin_detail.components.InfoCard
import com.example.cryptotracker.crypto.presentation.coin_list.CoinListState
import com.example.cryptotracker.crypto.presentation.coin_list.components.previewCoin
import com.example.cryptotracker.crypto.presentation.models.toUICoin
import com.example.cryptotracker.ui.theme.CryptoTrackerTheme

@Composable
fun CoinDetailScreen(
    modifier: Modifier = Modifier,
    state: CoinListState,
) {
    val contentColor = if (isSystemInDarkTheme()) {
        Color.White
    } else {
        Color.Black
    }

    if (state.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (state.selectedCoin != null) {
        val coin = state.selectedCoin
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(
                    id = coin.iconRes
                ),
                contentDescription = coin.name,
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = coin.name,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = contentColor
            )

            Text(
                text = coin.symbol,
                fontSize = 20.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                color = contentColor
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                InfoCard(
                    title = stringResource(
                        id = R.string.market_cap
                    ),
                    icon = ImageVector.vectorResource(
                      id = R.drawable.stock
                    ),
                    text = "$" + coin.marketCapUsd.formatted,
                    contentColor = contentColor
                )

                InfoCard(
                    title = stringResource(
                        id = R.string.price
                    ),
                    icon = ImageVector.vectorResource(
                        id = R.drawable.dollar
                    ),
                    text = "$" + coin.priceUsd.formatted,
                    contentColor = contentColor
                )

                val isPositive = coin.changePercent24Hs.value > 0
                val iconId = if (isPositive) {
                    R.drawable.trending
                } else {
                    R.drawable.trending_down
                }
                val text = if (isPositive) "+" else ""
                InfoCard(
                    title = stringResource(
                        id = R.string.change_last_24h
                    ),
                    icon = ImageVector.vectorResource(
                        id = iconId
                    ),
                    text = text + coin.changePercent24Hs.formatted + "%",
                    contentColor = contentColor
                )
            }
        }
    }
}

@Preview
@PreviewLightDark
@PreviewDynamicColors
@Composable
private fun CoinDetailScreenPreview() {
    CryptoTrackerTheme {
        CoinDetailScreen(
            modifier = Modifier.background(
                MaterialTheme.colorScheme.background
            ),
            state = CoinListState(
                selectedCoin = previewCoin.toUICoin()
            )
        )
    }
}
