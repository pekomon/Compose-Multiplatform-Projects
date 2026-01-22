package com.pekomon.pdfforge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.pekomon.pdfforge.domain.PdfPaths
import com.pekomon.pdfforge.domain.PdfProgressEvent
import com.pekomon.pdfforge.domain.ShrinkOptions
import com.pekomon.pdfforge.domain.ShrinkPreset
import com.pekomon.pdfforge.domain.SignOptions
import com.pekomon.pdfforge.infra.PdfBoxPdfShrinker
import com.pekomon.pdfforge.infra.PdfBoxPdfSigner
import com.pekomon.pdfforge.usecases.ShrinkPdfUseCase
import com.pekomon.pdfforge.usecases.SignPdfUseCase
import com.pekomon.pdfforge.usecases.UseCaseResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.Desktop
import java.io.File
import java.io.FilenameFilter
import java.time.Instant
import kotlin.io.path.Path
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.pathString

private const val BytesInKb = 1024.0
private const val BytesInMb = 1024.0 * 1024.0

@Composable
fun App() {
    MaterialTheme {
        val shrinkUseCase = remember { ShrinkPdfUseCase(PdfBoxPdfShrinker()) }
        val signUseCase = remember { SignPdfUseCase(PdfBoxPdfSigner()) }
        val coroutineScope = rememberCoroutineScope()
        var selectedPdf by remember { mutableStateOf<File?>(null) }
        var selectedP12 by remember { mutableStateOf<File?>(null) }
        var password by remember { mutableStateOf("") }
        var statusMessage by remember { mutableStateOf("Select a PDF to begin.") }
        var selectedPreset by remember { mutableStateOf(ShrinkPreset.Medium) }
        var lastOutputFile by remember { mutableStateOf<File?>(null) }
        var isBusy by remember { mutableStateOf(false) }
        var logLines by remember { mutableStateOf(listOf<String>()) }

        val updateStatus: (String) -> Unit = { message ->
            coroutineScope.launch {
                logLines = (logLines + "[${Instant.now()}] $message").takeLast(6)
                statusMessage = message
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("PdfForge", style = MaterialTheme.typography.headlineMedium)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Input PDF")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = {
                        selectedPdf = pickPdfFile()
                        statusMessage = if (selectedPdf != null) "Ready." else "Select a PDF to begin."
                    }) {
                        Text("Choose PDF")
                    }
                    val name = selectedPdf?.name ?: "No file selected"
                    Text(name, modifier = Modifier.weight(1f))
                }
                selectedPdf?.let { file ->
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

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Signing Certificate")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = {
                        selectedP12 = pickP12File()
                    }) {
                        Text("Choose .p12/.pfx")
                    }
                    val certName = selectedP12?.name ?: "No certificate selected"
                    Text(certName, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    enabled = selectedPdf != null && !isBusy,
                    onClick = {
                        val input = selectedPdf ?: return@Button
                        val inputPath = Path(input.absolutePath)
                        val outputPath = inputPath.resolveSibling("${inputPath.nameWithoutExtension}_compressed.pdf")
                        isBusy = true
                        coroutineScope.launch {
                            val result = withContext(Dispatchers.IO) {
                                shrinkUseCase.execute(
                                    PdfPaths(inputPath, outputPath),
                                    ShrinkOptions(selectedPreset),
                                ) { event ->
                                    val message = when (event) {
                                        is PdfProgressEvent.Started -> "Shrinking..."
                                        is PdfProgressEvent.PageProcessed ->
                                            "Processing page ${event.pageIndex}/${event.totalPages}"
                                        is PdfProgressEvent.Completed -> "Finalizing shrink..."
                                        is PdfProgressEvent.Failed -> event.error.userMessage
                                    }
                                    updateStatus(message)
                                }
                            }
                            statusMessage = when (result) {
                                is UseCaseResult.Success -> {
                                    lastOutputFile = outputPath.toFile()
                                    "Created: ${outputPath.pathString} (${formatBytes(result.value.beforeBytes)} → ${formatBytes(result.value.afterBytes)})"
                                }
                                is UseCaseResult.Failure -> result.error.userMessage
                            }
                            isBusy = false
                        }
                    },
                ) {
                    Text("Shrink")
                }
                Button(
                    enabled = selectedPdf != null && selectedP12 != null && password.isNotBlank() && !isBusy,
                    onClick = {
                        val input = selectedPdf ?: return@Button
                        val cert = selectedP12 ?: return@Button
                        val passwordChars = password.toCharArray()
                        val inputPath = Path(input.absolutePath)
                        val outputPath = inputPath.resolveSibling("${inputPath.nameWithoutExtension}_signed.pdf")
                        isBusy = true
                        coroutineScope.launch {
                            val result = withContext(Dispatchers.IO) {
                                signUseCase.execute(
                                    PdfPaths(inputPath, outputPath),
                                    Path(cert.absolutePath),
                                    passwordChars,
                                    SignOptions(),
                                ) { event ->
                                    val message = when (event) {
                                        is PdfProgressEvent.Started -> "Signing..."
                                        is PdfProgressEvent.PageProcessed ->
                                            "Preparing page ${event.pageIndex}/${event.totalPages}"
                                        is PdfProgressEvent.Completed -> "Finalizing signature..."
                                        is PdfProgressEvent.Failed -> event.error.userMessage
                                    }
                                    updateStatus(message)
                                }
                            }
                            passwordChars.fill('\u0000')
                            statusMessage = when (result) {
                                is UseCaseResult.Success -> {
                                    lastOutputFile = outputPath.toFile()
                                    "Created: ${outputPath.pathString}"
                                }
                                is UseCaseResult.Failure -> result.error.userMessage
                            }
                            isBusy = false
                        }
                    },
                ) {
                    Text("Sign")
                }
            }

            if (isBusy) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            Text(statusMessage)

            lastOutputFile?.let { file ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Output: ${file.name}", modifier = Modifier.weight(1f))
                    OutlinedButton(onClick = { revealInFinder(file) }) {
                        Text("Reveal in Finder")
                    }
                }
            }

            if (logLines.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Recent activity", style = MaterialTheme.typography.titleSmall)
                    logLines.forEach { line ->
                        Text(line, style = MaterialTheme.typography.bodySmall)
                    }
                }
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
    dialog.filenameFilter = FilenameFilter { _, name -> name.endsWith(".pdf", ignoreCase = true) }
    dialog.isVisible = true
    val directory = dialog.directory ?: return null
    val filename = dialog.file ?: return null
    return File(directory, filename)
}

private fun pickP12File(): File? {
    val dialog = java.awt.FileDialog(null as java.awt.Frame?, "Select Certificate", java.awt.FileDialog.LOAD)
    dialog.filenameFilter = FilenameFilter { _, name ->
        name.endsWith(".p12", ignoreCase = true) || name.endsWith(".pfx", ignoreCase = true)
    }
    dialog.isVisible = true
    val directory = dialog.directory ?: return null
    val filename = dialog.file ?: return null
    return File(directory, filename)
}

private fun revealInFinder(file: File) {
    val osName = System.getProperty("os.name").lowercase()
    if (osName.contains("mac")) {
        Runtime.getRuntime().exec(arrayOf("open", "-R", file.absolutePath))
        return
    }
    if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().open(file.parentFile)
    }
}
