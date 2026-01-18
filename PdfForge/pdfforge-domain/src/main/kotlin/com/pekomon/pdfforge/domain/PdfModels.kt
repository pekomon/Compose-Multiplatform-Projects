package com.pekomon.pdfforge.domain

import java.nio.file.Path

data class PdfPaths(
    val input: Path,
    val output: Path,
)

enum class ShrinkPreset {
    High,
    Medium,
    Aggressive,
}

data class ShrinkOptions(
    val preset: ShrinkPreset = ShrinkPreset.Medium,
)

data class ShrinkResult(
    val outputPath: Path,
    val beforeBytes: Long,
    val afterBytes: Long,
)

sealed class PdfForgeError(
    open val userMessage: String,
    open val technicalMessage: String,
    open val cause: Throwable? = null,
)

class InvalidInputError(
    override val userMessage: String,
    override val technicalMessage: String,
    override val cause: Throwable? = null,
) : PdfForgeError(userMessage, technicalMessage, cause)

class IoError(
    override val userMessage: String,
    override val technicalMessage: String,
    override val cause: Throwable? = null,
) : PdfForgeError(userMessage, technicalMessage, cause)

sealed class PdfProgressEvent {
    data class Started(val totalSteps: Int? = null) : PdfProgressEvent()
    data class PageProcessed(val pageIndex: Int, val totalPages: Int) : PdfProgressEvent()
    data class Completed(val outputPath: Path, val beforeBytes: Long, val afterBytes: Long) : PdfProgressEvent()
    data class Failed(val error: PdfForgeError) : PdfProgressEvent()
}
