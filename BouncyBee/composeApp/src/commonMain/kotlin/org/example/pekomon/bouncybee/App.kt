package org.example.pekomon.bouncybee

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bouncybee.composeapp.generated.resources.Res
import bouncybee.composeapp.generated.resources.background
import org.example.pekomon.bouncybee.util.ChewyFontFamily
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
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