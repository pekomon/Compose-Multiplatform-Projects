package com.pekomon.pdfforge.usecases

import com.pekomon.pdfforge.domain.IoError
import com.pekomon.pdfforge.domain.PdfPaths
import com.pekomon.pdfforge.domain.PdfProgressEvent
import com.pekomon.pdfforge.domain.SignOptions
import com.pekomon.pdfforge.domain.SignResult
import java.nio.file.Path

class SignPdfUseCase(
    private val signer: PdfSigner,
) {
    fun execute(
        paths: PdfPaths,
        p12Path: Path,
        password: CharArray,
        options: SignOptions = SignOptions(),
        onProgress: (PdfProgressEvent) -> Unit = {},
    ): UseCaseResult<SignResult> {
        onProgress(PdfProgressEvent.Started())
        return try {
            val result = signer.signWithP12(paths, p12Path, password, options, onProgress)
            onProgress(
                PdfProgressEvent.Completed(
                    outputPath = result.outputPath,
                    beforeBytes = result.beforeBytes,
                    afterBytes = result.afterBytes,
                ),
            )
            UseCaseResult.Success(result)
        } catch (error: Exception) {
            System.err.println("Sign failed: ${error.message}")
            val wrapped = IoError(
                userMessage = "Could not sign PDF.",
                technicalMessage = error.message ?: "Unknown error during signing.",
                cause = error,
            )
            onProgress(PdfProgressEvent.Failed(wrapped))
            UseCaseResult.Failure(wrapped)
        }
    }
}
