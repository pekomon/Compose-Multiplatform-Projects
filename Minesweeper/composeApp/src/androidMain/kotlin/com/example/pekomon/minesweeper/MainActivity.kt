package com.example.pekomon.minesweeper

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.pekomon.minesweeper.ui.theme.AndroidMinesweeperTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val supportsSystemSplash = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        val splashScreen = if (supportsSystemSplash) installSplashScreen() else null

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen?.let { screen ->
            var keepSplashVisible = true
            screen.setKeepOnScreenCondition { keepSplashVisible }
            lifecycleScope.launch {
                delay(260)
                keepSplashVisible = false
            }
        }

        setContent {
            AndroidMinesweeperTheme {
                LegacySplashHost(showLegacySplash = !supportsSystemSplash) {
                    App()
                }
            }
        }
    }
}

@Composable
private fun LegacySplashHost(
    showLegacySplash: Boolean,
    content: @Composable () -> Unit,
) {
    var showSplash by remember { mutableStateOf(showLegacySplash) }

    LaunchedEffect(showLegacySplash) {
        if (!showLegacySplash) {
            showSplash = false
            return@LaunchedEffect
        }
        delay(300)
        showSplash = false
    }

    if (showSplash) {
        LegacySplash()
    } else {
        content()
    }
}

@Composable
private fun LegacySplash() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.surface,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Minesweeper",
                style = MaterialTheme.typography.h4,
                color = MaterialTheme.colors.onSurface,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview
@Composable
fun GameScreenAndroidPreview() {
    AndroidMinesweeperTheme {
        App()
    }
}
