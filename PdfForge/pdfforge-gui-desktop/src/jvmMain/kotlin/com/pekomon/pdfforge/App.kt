package com.pekomon.pdfforge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pekomon.pdfforge.domain.PdfPaths
import com.pekomon.pdfforge.domain.PdfProgressEvent
import com.pekomon.pdfforge.domain.ShrinkOptions
import com.pekomon.pdfforge.domain.ShrinkPreset
import com.pekomon.pdfforge.domain.SignOptions
import com.pekomon.pdfforge.domain.VisibleSignaturePosition
import com.pekomon.pdfforge.domain.VisibleSignatureStyle
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
import java.security.KeyStore
import java.nio.file.Path
import kotlin.io.path.Path as pathOf
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.pathString

private const val BytesInKb = 1024.0
private const val BytesInMb = 1024.0 * 1024.0

private enum class PrimaryAction {
    Disabled,
    ShrinkOnly,
    SignOnly,
    ShrinkAndSign,
}

@Composable
fun App() {
    MaterialTheme {
        val shrinkUseCase = remember { ShrinkPdfUseCase(PdfBoxPdfShrinker()) }
        val signUseCase = remember { SignPdfUseCase(PdfBoxPdfSigner()) }
        val coroutineScope = rememberCoroutineScope()
        val scrollState = rememberScrollState()
        var selectedPdf by remember { mutableStateOf<File?>(null) }
        var selectedP12 by remember { mutableStateOf<File?>(null) }
        var passwordCache by remember { mutableStateOf("") }
        var statusMessage by remember { mutableStateOf("Select a PDF to begin.") }
        var selectedPreset by remember { mutableStateOf(ShrinkPreset.Medium) }
        var lastOutputFile by remember { mutableStateOf<File?>(null) }
        var isBusy by remember { mutableStateOf(false) }
        var visibleSignature by remember { mutableStateOf(false) }
        var visibleSignatureStyle by remember { mutableStateOf(VisibleSignatureStyle.Compact) }
        var visibleSignaturePosition by remember { mutableStateOf(VisibleSignaturePosition.BottomRight) }
        var visibleSignaturePageInput by remember { mutableStateOf("1") }
        var compressionMenuExpanded by remember { mutableStateOf(false) }
        var positionMenuExpanded by remember { mutableStateOf(false) }
        val signatureOptionsRequester = remember { BringIntoViewRequester() }

        var passwordDialogVisible by remember { mutableStateOf(false) }
        var passwordInput by remember { mutableStateOf("") }
        var passwordError by remember { mutableStateOf<String?>(null) }
        var pendingCert by remember { mutableStateOf<File?>(null) }

        val updateStatus: (String) -> Unit = { message ->
            coroutineScope.launch {
                statusMessage = message
            }
        }

        val hasPdf = selectedPdf != null
        val hasSigning = selectedP12 != null && passwordCache.isNotBlank()
        val wantsShrink = selectedPreset != ShrinkPreset.None
        val primaryAction = when {
            !hasPdf -> PrimaryAction.Disabled
            wantsShrink && hasSigning -> PrimaryAction.ShrinkAndSign
            wantsShrink -> PrimaryAction.ShrinkOnly
            hasSigning -> PrimaryAction.SignOnly
            else -> PrimaryAction.Disabled
        }
        val canRunPrimary = primaryAction != PrimaryAction.Disabled && !isBusy
        val inputsEnabled = !isBusy

        if (passwordDialogVisible && pendingCert != null) {
            AlertDialog(
                onDismissRequest = {
                    passwordDialogVisible = false
                    pendingCert = null
                    passwordInput = ""
                    passwordError = null
                },
                title = { Text("Certificate password") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text("Password") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            isError = passwordError != null,
                        )
                        passwordError?.let { Text(it) }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val cert = pendingCert ?: return@TextButton
                        val isValid = validateP12Password(cert, passwordInput)
                        if (isValid) {
                            selectedP12 = cert
                            passwordCache = passwordInput
                            statusMessage = "Certificate loaded."
                            passwordDialogVisible = false
                            pendingCert = null
                            passwordInput = ""
                            passwordError = null
                        } else {
                            selectedP12 = null
                            passwordCache = ""
                            passwordError = "Invalid password."
                            statusMessage = "Invalid certificate password."
                        }
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        passwordDialogVisible = false
                        pendingCert = null
                        passwordInput = ""
                        passwordError = null
                    }) {
                        Text("Cancel")
                    }
                },
            )
        }

        Scaffold(
            bottomBar = {
                Surface(tonalElevation = 4.dp) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        if (isBusy) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                enabled = canRunPrimary,
                                onClick = {
                                    val input = selectedPdf ?: return@Button
                                    val cert = selectedP12
                                    val inputPath = pathOf(input.absolutePath)
                                    val compressedPath =
                                        inputPath.resolveSibling("${inputPath.nameWithoutExtension}_compressed.pdf")
                                    val signedPath =
                                        compressedPath.resolveSibling("${compressedPath.nameWithoutExtension}_signed.pdf")
                                    val directSignedPath =
                                        inputPath.resolveSibling("${inputPath.nameWithoutExtension}_signed.pdf")
                                    val visiblePage = visibleSignaturePageInput.toIntOrNull()?.coerceAtLeast(1) ?: 1
                                    isBusy = true
                                    coroutineScope.launch {
                                        when (primaryAction) {
                                            PrimaryAction.ShrinkOnly -> {
                                                val result = runShrink(
                                                    shrinkUseCase,
                                                    inputPath,
                                                    compressedPath,
                                                    selectedPreset,
                                                    updateStatus,
                                                )
                                                statusMessage = when (result) {
                                                    is UseCaseResult.Success -> {
                                                        lastOutputFile = compressedPath.toFile()
                                                        "Created: ${compressedPath.pathString} " +
                                                            "(${formatBytes(result.value.beforeBytes)} -> ${formatBytes(result.value.afterBytes)})"
                                                    }
                                                    is UseCaseResult.Failure -> result.error.userMessage
                                                }
                                            }
                                            PrimaryAction.SignOnly -> {
                                                val result = runSign(
                                                    signUseCase,
                                                    inputPath,
                                                    directSignedPath,
                                                    cert,
                                                    passwordCache,
                                                    visibleSignature,
                                                    visiblePage,
                                                    visibleSignatureStyle,
                                                    visibleSignaturePosition,
                                                    updateStatus,
                                                )
                                                statusMessage = when (result) {
                                                    is UseCaseResult.Success -> {
                                                        lastOutputFile = directSignedPath.toFile()
                                                        "Created: ${directSignedPath.pathString}"
                                                    }
                                                    is UseCaseResult.Failure -> result.error.userMessage
                                                }
                                            }
                                            PrimaryAction.ShrinkAndSign -> {
                                                val shrinkResult = runShrink(
                                                    shrinkUseCase,
                                                    inputPath,
                                                    compressedPath,
                                                    selectedPreset,
                                                    updateStatus,
                                                )
                                                if (shrinkResult is UseCaseResult.Success) {
                                                    val signResult = runSign(
                                                        signUseCase,
                                                        compressedPath,
                                                        signedPath,
                                                        cert,
                                                        passwordCache,
                                                        visibleSignature,
                                                        visiblePage,
                                                        visibleSignatureStyle,
                                                        visibleSignaturePosition,
                                                        updateStatus,
                                                    )
                                                    statusMessage = when (signResult) {
                                                        is UseCaseResult.Success -> {
                                                            lastOutputFile = signedPath.toFile()
                                                            "Created: ${signedPath.pathString}"
                                                        }
                                                        is UseCaseResult.Failure -> signResult.error.userMessage
                                                    }
                                                } else if (shrinkResult is UseCaseResult.Failure) {
                                                    statusMessage = shrinkResult.error.userMessage
                                                }
                                            }
                                            PrimaryAction.Disabled -> Unit
                                        }
                                        isBusy = false
                                    }
                                },
                            ) {
                                Text(primaryActionLabel(primaryAction))
                            }
                            if (lastOutputFile != null) {
                                FilledTonalButton(onClick = { revealInFinder(lastOutputFile!!) }) {
                                    Text("Reveal in Finder")
                                }
                            }
                        }
                        Text(statusMessage, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
        ) { padding ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
            ) {
                val isWide = maxWidth >= 760.dp
                LaunchedEffect(visibleSignature, isWide) {
                    if (visibleSignature && !isWide) {
                        signatureOptionsRequester.bringIntoView()
                    }
                }
                Column(
                    modifier = Modifier.fillMaxWidth().verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    val inputSection: @Composable ColumnScope.() -> Unit = {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                enabled = inputsEnabled,
                                onClick = {
                                    selectedPdf = pickPdfFile()
                                    statusMessage = if (selectedPdf != null) "Ready." else "Select a PDF to begin."
                                },
                            ) {
                                Text("Choose PDF")
                            }
                            val name = selectedPdf?.name ?: "No file selected"
                            Text(
                                name,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        selectedPdf?.let { file ->
                            Text("Size: ${formatBytes(file.length())}")
                        }
                    }
                    val compressionSection: @Composable ColumnScope.() -> Unit = {
                        BoxWithConstraints {
                            OutlinedButton(
                                enabled = inputsEnabled,
                                onClick = { compressionMenuExpanded = true },
                            ) {
                                Text("Preset: ${shrinkPresetLabel(selectedPreset)}")
                            }
                            DropdownMenu(
                                expanded = compressionMenuExpanded,
                                onDismissRequest = { compressionMenuExpanded = false },
                            ) {
                                ShrinkPreset.entries.forEach { preset ->
                                    DropdownMenuItem(
                                        text = { Text(shrinkPresetLabel(preset)) },
                                        onClick = {
                                            selectedPreset = preset
                                            compressionMenuExpanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }
                    val signingSection: @Composable ColumnScope.() -> Unit = {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                enabled = inputsEnabled,
                                onClick = {
                                    val picked = pickP12File()
                                    if (picked != null) {
                                        pendingCert = picked
                                        passwordInput = ""
                                        passwordError = null
                                        passwordDialogVisible = true
                                        selectedP12 = null
                                        passwordCache = ""
                                    }
                                },
                            ) {
                                Text("Choose .p12/.pfx")
                            }
                            val certName = selectedP12?.name ?: "No certificate selected"
                            Text(
                                certName,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Checkbox(
                                checked = visibleSignature,
                                onCheckedChange = { visibleSignature = it },
                                enabled = inputsEnabled,
                            )
                            Text("Visible signature")
                        }
                        if (visibleSignature) {
                            if (isWide) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .bringIntoViewRequester(signatureOptionsRequester),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Text("Style", style = MaterialTheme.typography.labelMedium)
                                    SingleChoiceSegmentedButtonRow {
                                        SegmentedButton(
                                            selected = visibleSignatureStyle == VisibleSignatureStyle.Compact,
                                            onClick = { visibleSignatureStyle = VisibleSignatureStyle.Compact },
                                            enabled = inputsEnabled,
                                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                                        ) {
                                            Text("Compact")
                                        }
                                        SegmentedButton(
                                            selected = visibleSignatureStyle == VisibleSignatureStyle.Detailed,
                                            onClick = { visibleSignatureStyle = VisibleSignatureStyle.Detailed },
                                            enabled = inputsEnabled,
                                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                                        ) {
                                            Text("Detailed")
                                        }
                                    }
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text("Position", style = MaterialTheme.typography.labelMedium)
                                        BoxWithConstraints {
                                            OutlinedButton(
                                                enabled = inputsEnabled,
                                                onClick = { positionMenuExpanded = true },
                                            ) {
                                                Text(visiblePositionLabel(visibleSignaturePosition))
                                            }
                                            DropdownMenu(
                                                expanded = positionMenuExpanded,
                                                onDismissRequest = { positionMenuExpanded = false },
                                            ) {
                                                VisibleSignaturePosition.entries.forEach { position ->
                                                    DropdownMenuItem(
                                                        text = { Text(visiblePositionLabel(position)) },
                                                        onClick = {
                                                            visibleSignaturePosition = position
                                                            positionMenuExpanded = false
                                                        },
                                                    )
                                                }
                                            }
                                        }
                                        OutlinedTextField(
                                            value = visibleSignaturePageInput,
                                            onValueChange = { input ->
                                                visibleSignaturePageInput = input.filter { it.isDigit() }
                                            },
                                            label = { Text("Page") },
                                            singleLine = true,
                                            enabled = inputsEnabled,
                                            modifier = Modifier.width(110.dp),
                                        )
                                    }
                                }
                            } else {
                                Column(
                                    modifier = Modifier.bringIntoViewRequester(signatureOptionsRequester),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Text("Style", style = MaterialTheme.typography.labelMedium)
                                    SingleChoiceSegmentedButtonRow {
                                        SegmentedButton(
                                            selected = visibleSignatureStyle == VisibleSignatureStyle.Compact,
                                            onClick = { visibleSignatureStyle = VisibleSignatureStyle.Compact },
                                            enabled = inputsEnabled,
                                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                                        ) {
                                            Text("Compact")
                                        }
                                        SegmentedButton(
                                            selected = visibleSignatureStyle == VisibleSignatureStyle.Detailed,
                                            onClick = { visibleSignatureStyle = VisibleSignatureStyle.Detailed },
                                            enabled = inputsEnabled,
                                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                                        ) {
                                            Text("Detailed")
                                        }
                                    }
                                    Text("Position", style = MaterialTheme.typography.labelMedium)
                                    BoxWithConstraints {
                                        OutlinedButton(
                                            enabled = inputsEnabled,
                                            onClick = { positionMenuExpanded = true },
                                        ) {
                                            Text(visiblePositionLabel(visibleSignaturePosition))
                                        }
                                        DropdownMenu(
                                            expanded = positionMenuExpanded,
                                            onDismissRequest = { positionMenuExpanded = false },
                                        ) {
                                            VisibleSignaturePosition.entries.forEach { position ->
                                                DropdownMenuItem(
                                                    text = { Text(visiblePositionLabel(position)) },
                                                    onClick = {
                                                        visibleSignaturePosition = position
                                                        positionMenuExpanded = false
                                                    },
                                                )
                                            }
                                        }
                                    }
                                    OutlinedTextField(
                                        value = visibleSignaturePageInput,
                                        onValueChange = { input ->
                                            visibleSignaturePageInput = input.filter { it.isDigit() }
                                        },
                                        label = { Text("Page") },
                                        singleLine = true,
                                        enabled = inputsEnabled,
                                        modifier = Modifier.width(120.dp),
                                    )
                                }
                            }
                        }
                    }

                    if (isWide) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                SectionCard("Input PDF", inputSection)
                                SectionCard("Compression", compressionSection)
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                SectionCard("Signing", signingSection)
                            }
                        }
                    } else {
                        SectionCard("Input PDF", inputSection)
                        SectionCard("Compression", compressionSection)
                        SectionCard("Signing", signingSection)
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun PresetButton(label: String, selected: Boolean, enabled: Boolean, onClick: () -> Unit) {
    if (selected) {
        Button(onClick = onClick, enabled = enabled) { Text(label) }
    } else {
        OutlinedButton(onClick = onClick, enabled = enabled) { Text(label) }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            content()
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

private fun validateP12Password(certFile: File, password: String): Boolean {
    return try {
        val keyStore = KeyStore.getInstance("PKCS12")
        certFile.inputStream().use { input ->
            keyStore.load(input, password.toCharArray())
        }
        true
    } catch (_: Exception) {
        false
    }
}

private fun primaryActionLabel(action: PrimaryAction): String {
    return when (action) {
        PrimaryAction.Disabled -> "Select input"
        PrimaryAction.ShrinkOnly -> "Shrink"
        PrimaryAction.SignOnly -> "Sign"
        PrimaryAction.ShrinkAndSign -> "Shrink & Sign"
    }
}

private fun shrinkPresetLabel(preset: ShrinkPreset): String {
    return when (preset) {
        ShrinkPreset.None -> "None"
        ShrinkPreset.High -> "Low"
        ShrinkPreset.Medium -> "Medium"
        ShrinkPreset.Aggressive -> "High"
    }
}

private fun visiblePositionLabel(position: VisibleSignaturePosition): String {
    return when (position) {
        VisibleSignaturePosition.BottomRight -> "Bottom right"
        VisibleSignaturePosition.BottomLeft -> "Bottom left"
        VisibleSignaturePosition.TopRight -> "Top right"
        VisibleSignaturePosition.TopLeft -> "Top left"
    }
}

private suspend fun runShrink(
    useCase: ShrinkPdfUseCase,
    input: Path,
    output: Path,
    preset: ShrinkPreset,
    updateStatus: (String) -> Unit,
): UseCaseResult<com.pekomon.pdfforge.domain.ShrinkResult> {
    return withContext(Dispatchers.IO) {
        useCase.execute(
            PdfPaths(input, output),
            ShrinkOptions(preset),
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
}

private suspend fun runSign(
    useCase: SignPdfUseCase,
    input: Path,
    output: Path,
    certFile: File?,
    password: String,
    visibleSignature: Boolean,
    visibleSignaturePage: Int,
    visibleSignatureStyle: VisibleSignatureStyle,
    visibleSignaturePosition: VisibleSignaturePosition,
    updateStatus: (String) -> Unit,
): UseCaseResult<com.pekomon.pdfforge.domain.SignResult> {
    val cert = certFile ?: return UseCaseResult.Failure(
        com.pekomon.pdfforge.domain.InvalidInputError(
            userMessage = "Certificate not selected.",
            technicalMessage = "Sign called without certificate.",
        ),
    )
    val passwordChars = password.toCharArray()
    return withContext(Dispatchers.IO) {
        val result = useCase.execute(
            PdfPaths(input, output),
            pathOf(cert.absolutePath),
            passwordChars,
            SignOptions(
                visibleSignature = visibleSignature,
                visibleSignaturePage = visibleSignaturePage,
                visibleSignatureStyle = visibleSignatureStyle,
                visibleSignaturePosition = visibleSignaturePosition,
            ),
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
        passwordChars.fill('\u0000')
        result
    }
}
