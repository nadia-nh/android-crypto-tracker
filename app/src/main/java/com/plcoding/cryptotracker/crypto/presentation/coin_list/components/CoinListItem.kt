package com.plcoding.cryptotracker.crypto.presentation.coin_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.plcoding.cryptotracker.crypto.domain.Coin
import com.plcoding.cryptotracker.crypto.presentation.models.UICoin
import com.plcoding.cryptotracker.crypto.presentation.models.toUICoin
import com.plcoding.cryptotracker.ui.theme.CryptoTrackerTheme

@Composable
fun CoinListItem(
    modifier: Modifier = Modifier,
    uiCoin: UICoin,
    onItemClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .clickable(onClick = onItemClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = uiCoin.iconRes),
            contentDescription = uiCoin.name,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(85.dp)
        )
    }
}

@Preview
@PreviewLightDark
@PreviewDynamicColors
@Composable
fun CoinListItemPreview() {
    CryptoTrackerTheme() {
        CoinListItem(
            modifier = Modifier.background(
                MaterialTheme.colorScheme.primaryContainer
            ),
            uiCoin = previewCoin.toUICoin(),
        )
    }
}

internal val previewCoin = Coin(
    id = "bitcoin",
    rank = 1,
    name = "Bitcoin",
    symbol = "BTC",
    marketCapUsd = 124.56,
    priceUsd = 123.45,
    changePercent24Hs = 12.34
)
