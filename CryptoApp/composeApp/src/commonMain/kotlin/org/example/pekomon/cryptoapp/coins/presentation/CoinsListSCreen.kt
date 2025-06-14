package org.example.pekomon.cryptoapp.coins.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import org.example.pekomon.cryptoapp.coins.presentation.components.CoinChartDialog
import org.example.pekomon.cryptoapp.theme.LocalCryptoAppColorsPalette
import org.koin.compose.viewmodel.koinViewModel

// TODO: Separate in separate files

@Composable
fun CoinsListScreen(
    onCoinClicked: (String) -> Unit
) {
    val viewModel = koinViewModel<CoinsListViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    CoinsListContent(
        state = state,
        onCoinLongPressed = { coinId -> viewModel.onCoinLongPressed(coinId) },
        onCoinClicked = onCoinClicked,
        onDismissChart = { viewModel.dismissChart()},
    )
}

@Composable
private fun CoinsListContent(
    state: CoinsState,
    onDismissChart: () -> Unit,
    onCoinLongPressed: (String) -> Unit,
    onCoinClicked: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (state.chartState != null) {
            CoinChartDialog(
                uiChartState = state.chartState,
                onDismiss = onDismissChart
            )
        }

        CoinsList(
            coins = state.coins,
            onCoinLongPressed = onCoinLongPressed,
            onCoinClicked = onCoinClicked
        )
    }
}

@Composable
private fun CoinsList(
    coins: List<UiCoinListItem>,
    onCoinLongPressed: (String) -> Unit,
    onCoinClicked: (String) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(
                    text = "🔥 Top Coins:",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    modifier = Modifier.padding(16.dp)
                )
            }
            items(coins) { coin ->
                CoinListItem(
                    coin = coin,
                    onCoinLongPressed = onCoinLongPressed,
                    onCoinClicked = onCoinClicked
                )
            }
        }
    }
}

@Composable
private fun CoinListItem(
    coin: UiCoinListItem,
    onCoinLongPressed: (String) -> Unit,
    onCoinClicked: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onLongClick = { onCoinLongPressed(coin.id) },
                onClick = { onCoinClicked(coin.id) }
            )
            .padding(16.dp)
    ) {
        AsyncImage(
            model = coin.iconUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(end = 4.dp)
                .clip(CircleShape)
                .size(40.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = coin.name,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = coin.symbol,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = coin.formattedPrice,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = coin.formattedChange,
                color = if (coin.isPositive) {
                    LocalCryptoAppColorsPalette.current.profitGreen
                } else {
                    LocalCryptoAppColorsPalette.current.lossRed
                },
                fontSize = MaterialTheme.typography.titleSmall.fontSize
            )
        }
    }
}

