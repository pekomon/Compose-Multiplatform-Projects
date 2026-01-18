package com.pekomon.pdfforge.usecases

import com.pekomon.pdfforge.domain.PdfPaths
import com.pekomon.pdfforge.domain.PdfProgressEvent
import com.pekomon.pdfforge.domain.ShrinkOptions
import com.pekomon.pdfforge.domain.ShrinkResult

interface PdfShrinker {
    fun shrink(
        paths: PdfPaths,
        options: ShrinkOptions,
        onProgress: (PdfProgressEvent) -> Unit,
    ): ShrinkResult
}
