package com.pekomon.pdfforge.infra

import com.pekomon.pdfforge.domain.PdfPaths
import com.pekomon.pdfforge.domain.PdfProgressEvent
import com.pekomon.pdfforge.domain.ShrinkOptions
import com.pekomon.pdfforge.domain.ShrinkResult
import com.pekomon.pdfforge.usecases.PdfShrinker
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class JvmFileCopyPdfShrinker : PdfShrinker {
    override fun shrink(
        paths: PdfPaths,
        options: ShrinkOptions,
        onProgress: (PdfProgressEvent) -> Unit,
    ): ShrinkResult {
        val beforeSize = Files.size(paths.input)
        Files.copy(paths.input, paths.output, StandardCopyOption.REPLACE_EXISTING)
        val afterSize = Files.size(paths.output)
        return ShrinkResult(
            outputPath = paths.output,
            beforeBytes = beforeSize,
            afterBytes = afterSize,
        )
    }
}
