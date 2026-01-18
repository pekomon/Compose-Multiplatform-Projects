package com.pekomon.pdfforge.infra

import com.pekomon.pdfforge.domain.PdfPaths
import com.pekomon.pdfforge.domain.ShrinkOptions
import com.pekomon.pdfforge.domain.ShrinkPreset
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertTrue

class PdfBoxPdfShrinkerTest {
    @Test
    fun shrink_scanned_like_pdf_reduces_size() {
        val tempDir = createTempDirectory("pdfforge-shrink-test")
        val input = tempDir.resolve("input.pdf")
        val output = tempDir.resolve("output.pdf")
        TestPdfFactory.createScannedLikePdf(input)

        val shrinker = PdfBoxPdfShrinker()
        val result = shrinker.shrink(
            PdfPaths(input, output),
            ShrinkOptions(preset = ShrinkPreset.Aggressive),
        ) {}

        assertTrue(result.afterBytes < result.beforeBytes)
    }
}
