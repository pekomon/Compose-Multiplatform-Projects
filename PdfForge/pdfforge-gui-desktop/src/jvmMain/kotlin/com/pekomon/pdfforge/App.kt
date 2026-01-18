package com.pekomon.pdfforge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pekomon.pdfforge.domain.PdfPaths
import com.pekomon.pdfforge.domain.PdfProgressEvent
import com.pekomon.pdfforge.domain.ShrinkOptions
import com.pekomon.pdfforge.domain.ShrinkPreset
import com.pekomon.pdfforge.infra.PdfBoxPdfShrinker
import com.pekomon.pdfforge.usecases.ShrinkPdfUseCase
import com.pekomon.pdfforge.usecases.UseCaseResult
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.pathString

private const val BytesInKb = 1024.0
private const val BytesInMb = 1024.0 * 1024.0

@Composable
fun App() {
    MaterialTheme {
        val shrinkUseCase = remember { ShrinkPdfUseCase(PdfBoxPdfShrinker()) }
        var selectedFile by remember { mutableStateOf<File?>(null) }
        var statusMessage by remember { mutableStateOf("Select a PDF to begin.") }
        var selectedPreset by remember { mutableStateOf(ShrinkPreset.Medium) }

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("PdfForge", style = MaterialTheme.typography.headlineMedium)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Input PDF")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = {
                        selectedFile = pickPdfFile()
                        statusMessage = if (selectedFile != null) "Ready to shrink." else "Select a PDF to begin."
                    }) {
                        Text("Choose PDF")
                    }
                    val name = selectedFile?.name ?: "No file selected"
                    Text(name, modifier = Modifier.weight(1f))
                }
                selectedFile?.let { file ->
                    Text("Size: ${formatBytes(file.length())}")
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Preset")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShrinkPreset.values().forEach { preset ->
                        if (preset == selectedPreset) {
                            Button(onClick = { selectedPreset = preset }) {
                                Text(preset.name)
                            }
                        } else {
                            OutlinedButton(onClick = { selectedPreset = preset }) {
                                Text(preset.name)
                            }
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    enabled = selectedFile != null,
                    onClick = {
                        val input = selectedFile ?: return@Button
                        val inputPath = Path(input.absolutePath)
                        val outputPath = inputPath.resolveSibling("${inputPath.nameWithoutExtension}_compressed.pdf")
                        val result = shrinkUseCase.execute(
                            PdfPaths(inputPath, outputPath),
                            ShrinkOptions(selectedPreset),
                        ) { event ->
                            statusMessage = when (event) {
                                is PdfProgressEvent.Started -> "Shrinking..."
                                is PdfProgressEvent.PageProcessed ->
                                    "Processing page ${event.pageIndex}/${event.totalPages}"
                                is PdfProgressEvent.Completed -> "Finalizing..."
                                is PdfProgressEvent.Failed -> event.error.userMessage
                            }
                        }
                        statusMessage = when (result) {
                            is UseCaseResult.Success ->
                                "Created: ${outputPath.pathString} (${formatBytes(result.value.beforeBytes)} → ${formatBytes(result.value.afterBytes)})"
                            is UseCaseResult.Failure -> result.error.userMessage
                        }
                    },
                ) {
                    Text("Shrink")
                }
                Spacer(Modifier.width(8.dp))
                Text(statusMessage, modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

private fun formatBytes(bytes: Long): String {
    return when {
        bytes >= BytesInMb -> String.format("%.2f MB", bytes / BytesInMb)
        bytes >= BytesInKb -> String.format("%.1f KB", bytes / BytesInKb)
        else -> "$bytes B"
    }
}

private fun pickPdfFile(): File? {
    val dialog = java.awt.FileDialog(null as java.awt.Frame?, "Select PDF", java.awt.FileDialog.LOAD)
    dialog.isVisible = true
    val directory = dialog.directory ?: return null
    val filename = dialog.file ?: return null
    return File(directory, filename)
}
