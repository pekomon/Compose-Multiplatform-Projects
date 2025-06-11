package org.example.pekomon.cryptoapp.coins.presentation.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.example.pekomon.cryptoapp.coins.presentation.components.PerformanceChart
import org.example.pekomon.cryptoapp.theme.CryptoAppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview


@Preview
@Composable
fun PerformanceChartPreview() {
    CryptoAppTheme {
        PerformanceChart(
            nodes = listOf(4.0, 4.4, 3.0, 5.7, 2.6, 6.0, 7.0, 5.0),
            profitColor = Color.Green,
            lossColor = Color.Red
        )
    }
}

@Preview
@Composable
fun PerformanceChartPreview2() {
    CryptoAppTheme {
        PerformanceChart(
            nodes = listOf(4.0, 3.9999, 2.0, 9.99, -0.001),
            profitColor = Color.Green,
            lossColor = Color.Red
        )
    }
}

@Preview
@Composable
fun PerformanceChartPreview3() {
    CryptoAppTheme {
        PerformanceChart(
            nodes = listOf(4.0, 4.000000001, 4.0),
            profitColor = Color.Green,
            lossColor = Color.Red
        )
    }
}