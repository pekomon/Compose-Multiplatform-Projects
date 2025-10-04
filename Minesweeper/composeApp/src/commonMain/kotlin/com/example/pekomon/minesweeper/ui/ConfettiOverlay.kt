package com.example.pekomon.minesweeper.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun ConfettiOverlay(
    modifier: Modifier = Modifier,
    particleCount: Int = 120,
    animationDurationMillis: Int = 4_000,
    colors: List<Color> = emptyList(),
) {
    if (particleCount <= 0 || animationDurationMillis <= 0) {
        return
    }

    val palette = if (colors.isEmpty()) defaultConfettiColors() else colors
    val particles =
        remember(palette, particleCount, animationDurationMillis) {
            createParticles(palette, particleCount, animationDurationMillis)
        }
    val transition = rememberInfiniteTransition(label = "confetti-transition")
    val animationDurations =
        remember(particles, animationDurationMillis) {
            particles.map { it.durationMillis(animationDurationMillis) }
        }
    val animations =
        particles.mapIndexed { index, particle ->
            val duration = animationDurations[index]
            transition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec =
                    infiniteRepeatable(
                        animation = tween(durationMillis = duration, easing = LinearEasing),
                        initialStartOffset = StartOffset(particle.startDelayMillis),
                    ),
                label = "confetti-progress-$index",
            )
        }

    val density = LocalDensity.current
    Canvas(modifier = modifier) {
        drawConfettiParticles(
            particles = particles,
            animations = animations,
            density = density,
        )
    }
}

@Composable
private fun defaultConfettiColors(): List<Color> =
    listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
    )

@Immutable
private data class ConfettiParticle(
    val color: Color,
    val startFractionX: Float,
    val swayAmplitude: Float,
    val swayFrequency: Float,
    val swayPhase: Float,
    val size: Dp,
    val rotationSpeed: Float,
    val speedMultiplier: Float,
    val startDelayMillis: Int,
)

private fun createParticles(
    palette: List<Color>,
    count: Int,
    baseDurationMillis: Int,
): List<ConfettiParticle> {
    val random = Random
    return List(count) {
        ConfettiParticle(
            color = palette[random.nextInt(palette.size)],
            startFractionX = random.nextFloat(),
            swayAmplitude = random.nextFloatInRange(0.05f, 0.3f),
            swayFrequency = random.nextFloatInRange(0.8f, 1.8f),
            swayPhase = random.nextFloatInRange(0f, (2f * PI).toFloat()),
            size = random.nextFloatInRange(8f, 14f).dp,
            rotationSpeed = random.nextFloatInRange(0.6f, 2.2f),
            speedMultiplier = random.nextFloatInRange(0.8f, 1.4f),
            startDelayMillis = random.nextInt(baseDurationMillis),
        )
    }
}

private fun ConfettiParticle.durationMillis(baseDuration: Int): Int {
    val scaled = (baseDuration * speedMultiplier).roundToInt()
    return scaled.coerceAtLeast(1)
}

private fun DrawScope.drawConfettiParticles(
    particles: List<ConfettiParticle>,
    animations: List<State<Float>>,
    density: Density,
) {
    val width = size.width
    val height = size.height
    particles.forEachIndexed { index, particle ->
        val progress = animations[index].value
        val sizePx = with(density) { particle.size.toPx() }
        val confettiHeight = sizePx * 0.45f
        val y = height * progress - confettiHeight
        val x = width * particle.xFraction(progress)
        val rotation = particle.rotationDegrees(progress)
        rotate(degrees = rotation, pivot = Offset(x, y + confettiHeight / 2f)) {
            drawRoundRect(
                color = particle.color,
                topLeft = Offset(x - sizePx / 2f, y),
                size = Size(width = sizePx, height = confettiHeight),
                cornerRadius = CornerRadius(confettiHeight / 2f, confettiHeight / 2f),
            )
        }
    }
}

private fun ConfettiParticle.xFraction(progress: Float): Float {
    val oscillation = sin((progress * swayFrequency + swayPhase) * PI * 2f).toFloat()
    return wrap01(startFractionX + swayAmplitude * oscillation)
}

private fun ConfettiParticle.rotationDegrees(progress: Float): Float = (progress * 360f * rotationSpeed) % 360f

private fun wrap01(value: Float): Float {
    var result = value % 1f
    if (result < 0f) {
        result += 1f
    }
    return result
}

private fun Random.nextFloatInRange(
    min: Float,
    max: Float,
): Float {
    require(min <= max) { "min must be <= max" }
    return min + nextFloat() * (max - min)
}
