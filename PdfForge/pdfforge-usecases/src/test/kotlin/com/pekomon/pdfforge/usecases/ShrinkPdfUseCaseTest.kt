package com.pekomon.pdfforge.usecases

import com.pekomon.pdfforge.domain.PdfPaths
import com.pekomon.pdfforge.domain.PdfProgressEvent
import com.pekomon.pdfforge.domain.ShrinkOptions
import com.pekomon.pdfforge.domain.ShrinkResult
import kotlin.io.path.createTempDirectory
import kotlin.io.path.readBytes
import kotlin.io.path.writeBytes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ShrinkPdfUseCaseTest {
    @Test
    fun shrink_stub_copies_input_to_output() {
        val tempDir = createTempDirectory("pdfforge-test")
        val input = tempDir.resolve("input.pdf")
        val output = tempDir.resolve("output.pdf")
        val payload = "stub-pdf-content".encodeToByteArray()
        input.writeBytes(payload)

        val shrinker = object : PdfShrinker {
            override fun shrink(
                paths: PdfPaths,
                options: ShrinkOptions,
                onProgress: (PdfProgressEvent) -> Unit,
            ): ShrinkResult {
                output.writeBytes(input.readBytes())
                return ShrinkResult(
                    outputPath = output,
                    beforeBytes = payload.size.toLong(),
                    afterBytes = output.readBytes().size.toLong(),
                )
            }
        }

        val useCase = ShrinkPdfUseCase(shrinker)
        val result = useCase.execute(PdfPaths(input, output))

        assertTrue(result is UseCaseResult.Success)
        assertEquals(payload.size.toLong(), output.readBytes().size.toLong())
    }
}
