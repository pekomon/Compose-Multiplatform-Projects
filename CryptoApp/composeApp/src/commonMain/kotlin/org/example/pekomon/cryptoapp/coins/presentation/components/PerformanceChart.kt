package org.example.pekomon.cryptoapp.coins.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import org.example.pekomon.cryptoapp.theme.CryptoAppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PerformanceChart(
    modifier: Modifier = Modifier,
    nodes: List<Double>,
    profitColor: Color,
    lossColor: Color
) {
    if (nodes.isEmpty()) return

    val max = nodes.maxOrNull() ?: return
    val min = nodes.minOrNull() ?: return

    val lineColor = if (nodes.last() > nodes.first()) profitColor else lossColor

    Canvas(
        modifier = Modifier.fillMaxSize(),
    ) {
        val path = Path()
        nodes.forEachIndexed { index, node ->
            // TODO: Potential divide-by-zeros here...
            val x = index * (size.width / (nodes.size -1 ))
            val y = size.height * (1 - ((node - min)  / (max - min)).toFloat())

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 2.0f)
        )
    }
}
