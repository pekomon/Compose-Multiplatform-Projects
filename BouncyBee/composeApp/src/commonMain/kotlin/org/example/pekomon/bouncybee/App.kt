package org.example.pekomon.bouncybee

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bouncybee.composeapp.generated.resources.Res
import bouncybee.composeapp.generated.resources.background
import bouncybee.composeapp.generated.resources.bee_sprite
import bouncybee.composeapp.generated.resources.moving_background
import bouncybee.composeapp.generated.resources.pipe
import bouncybee.composeapp.generated.resources.pipe_cap
import com.stevdza_san.sprite.component.drawSpriteView
import com.stevdza_san.sprite.domain.SpriteSheet
import com.stevdza_san.sprite.domain.SpriteSpec
import com.stevdza_san.sprite.domain.rememberSpriteState
import kotlinx.coroutines.launch
import org.example.pekomon.bouncybee.domain.Bee
import org.example.pekomon.bouncybee.domain.Game
import org.example.pekomon.bouncybee.domain.GameStatus
import org.example.pekomon.bouncybee.ui.orange
import org.example.pekomon.bouncybee.util.ChewyFontFamily
import org.example.pekomon.bouncybee.util.Platform
import org.example.pekomon.bouncybee.util.getPlatform
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private const val BEE_FRAME_SIZE = 80
private const val PIPE_CAP_HEIGHT = 50f

@Composable
@Preview
fun App() {
    MaterialTheme {
        val platform = remember { getPlatform() }
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

        DisposableEffect(Unit) {
            onDispose {
                spriteState.stop()
                spriteState.cleanup()
                game?.cleanUp()
            }
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

        val scope = rememberCoroutineScope()

        val backGroundOffsetX = remember { Animatable(0f) }
        var imageWidth by remember { mutableStateOf(0) }
        val pipeImage = imageResource(Res.drawable.pipe)
        val pipeCapImage = imageResource(Res.drawable.pipe_cap)

        LaunchedEffect(game?.status) {
            while (game?.status == GameStatus.Started) {
                backGroundOffsetX.animateTo(
                    targetValue = -imageWidth.toFloat(),
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = when (platform) {
                                Platform.Android -> 4000
                                Platform.iOS -> 4000
                                Platform.Web -> 8000
                                Platform.Desktop -> 7000
                            },
                            easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Restart
                    )
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                painter = painterResource(Res.drawable.background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Image(
                painter = painterResource(Res.drawable.moving_background),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged {
                        imageWidth = it.width
                    }
                    .offset {
                        IntOffset(
                            x = backGroundOffsetX.value.toInt(),
                            y = 0
                        )
                    }
            )
            Image(
                painter = painterResource(Res.drawable.moving_background),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .fillMaxSize()
                    .offset {
                        IntOffset(
                            x = backGroundOffsetX.value.toInt() + imageWidth,
                            y = 0
                        )
                    }
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
                            platform = platform,
                            screenWidth = screenWidth,
                            screenHeight = screenHeight
                        )
                    }
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (game?.status == GameStatus.Started) {
                        game?.jump()
                    }
                }
        ) {
            game?.let { g->
                rotate(
                    degrees = animatedBeeAngle,
                    pivot = Offset(
                        x = g.bee.x - g.beeRadius,
                        y = g.bee.y - g.beeRadius
                    )
                ) {
                    drawSpriteView(
                        spriteState = spriteState,
                        spriteSpec = spriteSpec,
                        currentFrame = currentFrame,
                        image = sheetImage,
                        offset = IntOffset(
                            x = g.bee.x.toInt() - g.beeRadius.toInt(),
                            y = g.bee.y.toInt() - g.beeRadius.toInt()
                        ),
                        spriteFlip = null
                    )
                }
                g.pipePairs.forEach { pipePair ->
//                    drawRect(
//                        color = Color.Blue,
//                        topLeft = Offset(
//                            x = pipePair.x - g.pipeWidth / 2,
//                            y = 0f
//                        ),
//                        size = Size(g.pipeWidth, pipePair.topHeight)
//                    )
//                    drawRect(
//                        color = Color.Red,
//                        topLeft = Offset(
//                            x = pipePair.x - g.pipeWidth / 2,
//                            y = pipePair.y + g.pipeGapSize / 2
//                        ),
//                        size = Size(g.pipeWidth, pipePair.bottomHeight)
//                    )
                    drawImage(
                        image = pipeImage,
                        dstOffset = IntOffset(
                            x = (pipePair.x -(g.pipeWidth /2)).toInt(),
                            y = 0
                        ),
                        dstSize = IntSize(
                            width = g.pipeWidth.toInt(),
                            height = (pipePair.topHeight - PIPE_CAP_HEIGHT).toInt()
                        )
                    )
                    drawImage(
                        image = pipeCapImage,
                        dstOffset = IntOffset(
                            x = (pipePair.x - g.pipeWidth / 2).toInt(),
                            y = (pipePair.topHeight - PIPE_CAP_HEIGHT).toInt()
                        ),
                        dstSize = IntSize(
                            width = g.pipeWidth.toInt(),
                            height = PIPE_CAP_HEIGHT.toInt()
                        )
                    )
                    drawImage(
                        image = pipeCapImage,
                        dstOffset = IntOffset(
                            x = (pipePair.x - g.pipeWidth / 2).toInt(),
                            y = (pipePair.y + g.pipeGapSize / 2).toInt()
                        ),
                        dstSize = IntSize(
                            width = g.pipeWidth.toInt(),
                            height = PIPE_CAP_HEIGHT.toInt()
                        )
                    )
                    drawImage(
                        image = pipeImage,
                        dstOffset = IntOffset(
                            x = (pipePair.x - g.pipeWidth / 2).toInt(),
                            y = (pipePair.y + g.pipeGapSize / 2 + PIPE_CAP_HEIGHT).toInt()
                        ),
                        dstSize = IntSize(
                            width = g.pipeWidth.toInt(),
                            height = (pipePair.bottomHeight - PIPE_CAP_HEIGHT).toInt()
                        )
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
                text = "Best: ${game?.highScore}",
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.displaySmall.fontSize,
                fontFamily = ChewyFontFamily()
            )
            Text(
                text = "${game?.currentScore}",
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.displaySmall.fontSize,
                fontFamily = ChewyFontFamily()
            )
        }

        if (game?.status == GameStatus.Idle) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    modifier = Modifier.height(56.dp),
                    shape = RoundedCornerShape(size = 20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = orange
                    ),
                    onClick = {
                        game?.start()
                        spriteState.start()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Start",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontFamily = ChewyFontFamily()
                    )
                }
            }
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
                Text(
                    text = "Score: ${game?.currentScore}",
                    color = Color.White,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontFamily = ChewyFontFamily()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    modifier = Modifier.height(56.dp),
                    shape = RoundedCornerShape(size = 20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = orange
                    ),
                    onClick = {
                        game?.restart()
                        spriteState.start()
                        scope.launch {
                            backGroundOffsetX.snapTo(0f)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Restart",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontFamily = ChewyFontFamily()
                    )
                }
            }
        }
    }
}