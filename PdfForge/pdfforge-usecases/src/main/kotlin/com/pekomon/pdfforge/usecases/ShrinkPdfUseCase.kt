package com.pekomon.pdfforge.usecases

import com.pekomon.pdfforge.domain.IoError
import com.pekomon.pdfforge.domain.PdfForgeError
import com.pekomon.pdfforge.domain.PdfPaths
import com.pekomon.pdfforge.domain.PdfProgressEvent
import com.pekomon.pdfforge.domain.ShrinkOptions
import com.pekomon.pdfforge.domain.ShrinkResult

sealed class UseCaseResult<out T> {
    data class Success<T>(val value: T) : UseCaseResult<T>()
    data class Failure(val error: PdfForgeError) : UseCaseResult<Nothing>()
}

class ShrinkPdfUseCase(
    private val shrinker: PdfShrinker,
) {
    fun execute(
        paths: PdfPaths,
        options: ShrinkOptions = ShrinkOptions(),
        onProgress: (PdfProgressEvent) -> Unit = {},
    ): UseCaseResult<ShrinkResult> {
        onProgress(PdfProgressEvent.Started())
        return try {
            val result = shrinker.shrink(paths, options, onProgress)
            onProgress(
                PdfProgressEvent.Completed(
                    outputPath = result.outputPath,
                    beforeBytes = result.beforeBytes,
                    afterBytes = result.afterBytes,
                ),
            )
            UseCaseResult.Success(result)
        } catch (error: Exception) {
            System.err.println("Shrink failed: ${error.message}")
            val wrapped = IoError(
                userMessage = "Could not shrink PDF.",
                technicalMessage = error.message ?: "Unknown error during shrink.",
                cause = error,
            )
            onProgress(PdfProgressEvent.Failed(wrapped))
            UseCaseResult.Failure(wrapped)
        }
    }
}
