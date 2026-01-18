package com.pekomon.pdfforge.usecases

import com.pekomon.pdfforge.domain.PdfPaths
import com.pekomon.pdfforge.domain.PdfProgressEvent
import com.pekomon.pdfforge.domain.SignOptions
import com.pekomon.pdfforge.domain.SignResult
import java.nio.file.Path

interface PdfSigner {
    fun signWithP12(
        paths: PdfPaths,
        p12Path: Path,
        password: CharArray,
        options: SignOptions,
        onProgress: (PdfProgressEvent) -> Unit,
    ): SignResult
}
