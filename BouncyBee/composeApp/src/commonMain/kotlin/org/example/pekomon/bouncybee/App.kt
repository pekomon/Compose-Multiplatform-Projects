package org.example.pekomon.bouncybee

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bouncybee.composeapp.generated.resources.Res
import bouncybee.composeapp.generated.resources.background
import bouncybee.composeapp.generated.resources.bee_sprite
import com.stevdza_san.sprite.component.drawSpriteView
import com.stevdza_san.sprite.domain.SpriteSheet
import com.stevdza_san.sprite.domain.SpriteSpec
import com.stevdza_san.sprite.domain.rememberSpriteState
import org.example.pekomon.bouncybee.domain.Bee
import org.example.pekomon.bouncybee.domain.Game
import org.example.pekomon.bouncybee.domain.GameStatus
import org.example.pekomon.bouncybee.util.ChewyFontFamily
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private const val BEE_FRAME_SIZE = 80

@Composable
@Preview
fun App() {
    MaterialTheme {

        var screenWidth by remember { mutableStateOf(0) }
        var screenHeight by remember { mutableStateOf(0) }
        var game by remember { mutableStateOf<Game?>(null)}

        val spriteState = rememberSpriteState(
            // Specs bee_sprite.png
            totalFrames = 9,
            framesPerRow = 3
        )

        val spriteSpec = remember {
            SpriteSpec(
                screenWidth = screenWidth.toFloat(),
                default = SpriteSheet(
                    frameWidth = BEE_FRAME_SIZE,
                    frameHeight = BEE_FRAME_SIZE,
                    image = Res.drawable.bee_sprite,
                )
            )
        }

        val currentFrame by spriteState.currentFrame.collectAsStateWithLifecycle()
        val sheetImage = spriteSpec.imageBitmap
        val animatedBeeAngle by animateFloatAsState(
            targetValue = game?.let {
                 when {
                    it.beeVelocity > it.beeVelocity / 1.1 -> 30f
                    else -> 0f
                }
            } ?: 0f
        )

        LaunchedEffect(Unit) {
            game?.start()
            spriteState.start()
        }

        LaunchedEffect(game?.status) {
            while (game?.status == GameStatus.Started) {
                withFrameMillis {
                    game?.updateGameProgress()
                }
            }
            if (game?.status == GameStatus.Over) {
                spriteState.stop()
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
                rotate(
                    degrees = animatedBeeAngle,
                    pivot = Offset(
                        x = it.bee.x,
                        y = it.bee.y
                    )
                ) {
                    drawSpriteView(
                        spriteState = spriteState,
                        spriteSpec = spriteSpec,
                        currentFrame = currentFrame,
                        image = sheetImage,
                        offset = IntOffset(
                            x = it.bee.x.toInt(),
                            y = it.bee.y.toInt()
                        ),
                        spriteFlip = null
                    )
                }
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

        if (game?.status == GameStatus.Over) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.5f)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Game Over",
                    color = Color.Gray,
                    fontSize = MaterialTheme.typography.displayMedium.fontSize,
                    fontWeight = FontWeight.Bold,
                    fontFamily = ChewyFontFamily()
                )
            }
        }
    }
}