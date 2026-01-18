package com.pekomon.pdfforge.infra

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory
import java.awt.image.BufferedImage
import java.nio.file.Path
import kotlin.random.Random

object TestPdfFactory {
    fun createScannedLikePdf(
        output: Path,
        pages: Int = 2,
        imagePx: Int = 2500,
    ) {
        PDDocument().use { document ->
            repeat(pages) {
                val page = PDPage(PDRectangle(imagePx.toFloat(), imagePx.toFloat()))
                document.addPage(page)
                val image = createNoiseImage(imagePx, imagePx)
                val pdImage = LosslessFactory.createFromImage(document, image)
                PDPageContentStream(document, page).use { contentStream ->
                    contentStream.drawImage(
                        pdImage,
                        0f,
                        0f,
                        page.mediaBox.width,
                        page.mediaBox.height,
                    )
                }
            }
            document.save(output.toFile())
        }
    }

    private fun createNoiseImage(width: Int, height: Int): BufferedImage {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val random = Random(0)
        val pixels = IntArray(width * height)
        for (i in pixels.indices) {
            val gray = random.nextInt(256)
            pixels[i] = (0xFF shl 24) or (gray shl 16) or (gray shl 8) or gray
        }
        image.setRGB(0, 0, width, height, pixels, 0, width)
        return image
    }
}
