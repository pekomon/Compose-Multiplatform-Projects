package com.pekomon.pdfforge.cli

import com.pekomon.pdfforge.domain.PdfPaths
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
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.system.exitProcess

private const val BytesInKb = 1024.0
private const val BytesInMb = 1024.0 * 1024.0

fun main(args: Array<String>) {
    if (args.isEmpty() || args.first() in setOf("-h", "--help")) {
        printHelp()
        return
    }

    val command = args.first()
    val rest = args.drop(1)
    when (command) {
        "shrink" -> runShrink(rest)
        "sign" -> runSign(rest)
        else -> {
            System.err.println("Unknown command: $command")
            printHelp()
            exitProcess(2)
        }
    }
}

private fun runShrink(args: List<String>) {
    if (args.size < 2) {
        System.err.println("shrink requires input and output paths")
        printShrinkHelp()
        exitProcess(2)
    }
    val input = Path(args[0])
    val output = Path(args[1])
    val preset = readPreset(args.drop(2))

    if (!input.exists()) {
        System.err.println("Input file does not exist: $input")
        exitProcess(2)
    }

    val useCase = ShrinkPdfUseCase(PdfBoxPdfShrinker())
    val result = useCase.execute(
        PdfPaths(input, output),
        ShrinkOptions(preset = preset),
    )

    when (result) {
        is UseCaseResult.Success -> {
            val before = formatBytes(result.value.beforeBytes)
            val after = formatBytes(result.value.afterBytes)
            println("Created: $output ($before -> $after)")
        }
        is UseCaseResult.Failure -> {
            System.err.println(result.error.userMessage)
            exitProcess(1)
        }
    }
}

private fun runSign(args: List<String>) {
    if (args.size < 2) {
        System.err.println("sign requires input and output paths")
        printSignHelp()
        exitProcess(2)
    }
    val input = Path(args[0])
    val output = Path(args[1])
    val options = args.drop(2)

    if (!input.exists()) {
        System.err.println("Input file does not exist: $input")
        exitProcess(2)
    }

    val p12Path = optionValue(options, "--p12")?.let { Path(it) }
    if (p12Path == null || !p12Path.exists()) {
        System.err.println("Missing or invalid --p12 path")
        printSignHelp()
        exitProcess(2)
    }

    val password = readPassword(options)
    val visibleSignature = hasFlag(options, "--visible")
    val visiblePage = optionValue(options, "--page")?.toIntOrNull() ?: 1
    val visibleStyle = parseVisibleStyle(optionValue(options, "--visible-style"))
    val visiblePosition = parseVisiblePosition(optionValue(options, "--visible-pos"))
    val useCase = SignPdfUseCase(PdfBoxPdfSigner())
    val result = useCase.execute(
        PdfPaths(input, output),
        p12Path,
        password,
        SignOptions(
            visibleSignature = visibleSignature,
            visibleSignaturePage = visiblePage,
            visibleSignatureStyle = visibleStyle,
            visibleSignaturePosition = visiblePosition,
        ),
    )
    password.fill('\u0000')

    when (result) {
        is UseCaseResult.Success -> {
            println("Created: $output")
        }
        is UseCaseResult.Failure -> {
            System.err.println(result.error.userMessage)
            exitProcess(1)
        }
    }
}

private fun readPreset(args: List<String>): ShrinkPreset {
    val value = optionValue(args, "--preset")?.lowercase() ?: return ShrinkPreset.Medium
    return when (value) {
        "none" -> ShrinkPreset.None
        "low" -> ShrinkPreset.High
        "medium" -> ShrinkPreset.Medium
        "high" -> ShrinkPreset.Aggressive
        "aggressive" -> ShrinkPreset.Aggressive
        else -> {
            System.err.println("Unknown preset: $value (use low, medium, high)")
            exitProcess(2)
        }
    }
}

private fun readPassword(args: List<String>): CharArray {
    val passSpec = optionValue(args, "--pass")
        ?: optionValue(args, "--password")
        ?: run {
            System.err.println("Missing --pass (use env:VAR)")
            exitProcess(2)
        }
    if (!passSpec.startsWith("env:")) {
        System.err.println("--pass must use env:VAR format")
        exitProcess(2)
    }
    val envName = passSpec.removePrefix("env:")
    val value = System.getenv(envName) ?: run {
        System.err.println("Environment variable not set: $envName")
        exitProcess(2)
    }
    return value.toCharArray()
}

private fun optionValue(args: List<String>, key: String): String? {
    val index = args.indexOf(key)
    if (index == -1) return null
    if (index + 1 >= args.size) return null
    return args[index + 1]
}

private fun hasFlag(args: List<String>, key: String): Boolean {
    return args.contains(key)
}

private fun parseVisibleStyle(value: String?): VisibleSignatureStyle {
    val normalized = value?.lowercase() ?: return VisibleSignatureStyle.Compact
    return when (normalized) {
        "compact" -> VisibleSignatureStyle.Compact
        "detailed" -> VisibleSignatureStyle.Detailed
        else -> {
            System.err.println("Unknown visible style: $value (use compact or detailed)")
            exitProcess(2)
        }
    }
}

private fun parseVisiblePosition(value: String?): VisibleSignaturePosition {
    val normalized = value?.lowercase() ?: return VisibleSignaturePosition.BottomRight
    return when (normalized) {
        "bottom-right" -> VisibleSignaturePosition.BottomRight
        "bottom-left" -> VisibleSignaturePosition.BottomLeft
        "top-right" -> VisibleSignaturePosition.TopRight
        "top-left" -> VisibleSignaturePosition.TopLeft
        else -> {
            System.err.println(
                "Unknown visible position: $value (use bottom-right, bottom-left, top-right, top-left)",
            )
            exitProcess(2)
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

private fun printHelp() {
    println(
        """
        |PdfForge CLI
        |
        |Usage:
        |  pdfforge shrink <input.pdf> <output.pdf> --preset {none|low|medium|high}
        |  pdfforge sign <input.pdf> <output.pdf> --p12 <cert.p12> --pass env:VAR [--visible]
        |    [--page N] [--visible-style {compact|detailed}] [--visible-pos {bottom-right|bottom-left|top-right|top-left}]
        |
        |Commands:
        |  shrink   Shrink a PDF by recompressing images
        |  sign     Sign a PDF with a PKCS#12 certificate
        |""".trimMargin(),
    )
}

private fun printShrinkHelp() {
    println("pdfforge shrink <input.pdf> <output.pdf> --preset {none|low|medium|high}")
}

private fun printSignHelp() {
    println(
        "pdfforge sign <input.pdf> <output.pdf> --p12 <cert.p12> --pass env:VAR " +
            "[--visible] [--page N] [--visible-style {compact|detailed}] " +
            "[--visible-pos {bottom-right|bottom-left|top-right|top-left}]",
    )
}
