package org.example.pekomon.cryptoapp.portfolio.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.pekomon.cryptoapp.theme.LocalCryptoAppColorsPalette
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PortfolioScreen(
    onCoinItemClicked: (String) -> Unit,
    onDiscoverCoinsClicked: () -> Unit
) {
    val portfolioViewModel = koinViewModel<PortfolioViewModel>()
    val state by portfolioViewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                color = LocalCryptoAppColorsPalette.current.profitGreen,
                modifier = Modifier.size(32.dp)
            )
        }
    } else {
        PortfolioContent(
            state = state,
            onCoinItemClicked = {}, // TODO
            onDiscoverCoinsClicked = {} // TODO
        )
    }
}

@Composable
private fun PortfolioContent(
    state: PortfolioState,
    onCoinItemClicked: (String) -> Unit,
    onDiscoverCoinsClicked: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.inversePrimary)
    ) {
        PortfolioBalanceSection(
            portfolioValue = state.portfolioValue,
            cashBalance = state.cashBalance,
            showBuyButton = state.showBuyButton,
            onBuyButtonClicked = onDiscoverCoinsClicked
        )

    }
}

@Composable
private fun PortfolioBalanceSection(
    portfolioValue: String,
    cashBalance: String,
    showBuyButton: Boolean,
    onBuyButtonClicked: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxHeight(0.3f) // drawn from hat
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.inversePrimary)
            .padding(32.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Total value:", // TODO: Localize
                color = MaterialTheme.colorScheme.primary,
                fontSize = MaterialTheme.typography.titleSmall.fontSize
            )
            Text(
                text = portfolioValue,
                color = MaterialTheme.colorScheme.primary,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize
            )
            Row {
                Text(
                    text = "Cash balance:", // TODO: Localize
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = cashBalance,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize
                )
            }
            if (showBuyButton) {
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = onBuyButtonClicked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LocalCryptoAppColorsPalette.current.profitGreen
                    ),
                    contentPadding = PaddingValues(horizontal = 64.dp)
                ) {
                    Text(
                        text = "Buy coin",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
