package com.example.pekomon.minesweeper.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.pekomon.minesweeper.ui.theme.cellBorderColor

@Composable
internal fun CellContainer(
    backgroundColor: Color,
    cornerRadius: Dp,
    modifier: Modifier = Modifier,
    interactionModifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier =
            modifier
                .background(backgroundColor, shape)
                .border(1.dp, cellBorderColor(), shape)
                .then(interactionModifier),
        contentAlignment = contentAlignment,
        content = content,
    )
}
