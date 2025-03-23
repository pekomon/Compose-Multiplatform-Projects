package org.example.pekomon.bouncybee

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bouncybee.composeapp.generated.resources.Res
import bouncybee.composeapp.generated.resources.background
import org.example.pekomon.bouncybee.domain.Bee
import org.example.pekomon.bouncybee.domain.Game
import org.example.pekomon.bouncybee.domain.GameStatus
import org.example.pekomon.bouncybee.util.ChewyFontFamily
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {

        var screenWidth by remember { mutableStateOf(0) }
        var screenHeight by remember { mutableStateOf(0) }
        var game by remember { mutableStateOf<Game?>(null)}

        /*var game = remember(screenWidth, screenHeight) {
            Game(
                screenWidth = screenWidth,
                screenHeight = screenHeight
            )
        }
        */
        val bee: Bee? = remember { null }

        LaunchedEffect(Unit) {
            game?.start()
        }

        LaunchedEffect(Unit) {
            while (game?.status == GameStatus.Started) {
                withFrameMillis {
                    game?.updateGameProgress()
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(Res.drawable.background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    val newSize = it.size
                    if (newSize.width <= 0f || newSize.height <= 0) {
                        return@onGloballyPositioned
                    }
                    if (it.size.width != screenWidth || it.size.height != screenHeight) {
                        screenWidth = it.size.width
                        screenHeight = it.size.height
                        game = Game(
                            screenWidth = screenWidth,
                            screenHeight = screenHeight
                        )
                    }
                }
                .clickable {
                    if (game?.status == GameStatus.Started) {
                        game?.jump()
                    }
                }
        ) {
            game?.let {
            drawCircle(
                color = Color.Red,
                radius = it.bee.radius,
                center = Offset(
                    x = it.bee.x,
                    y = it.bee.y
                )
            )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Best: 0",
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.displaySmall.fontSize,
                fontFamily = ChewyFontFamily()
            )
            Text(
                text = "0",
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.displaySmall.fontSize,
                fontFamily = ChewyFontFamily()
            )
        }
    }
}