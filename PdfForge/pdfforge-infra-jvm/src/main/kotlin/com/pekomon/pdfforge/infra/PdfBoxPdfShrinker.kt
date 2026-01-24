package com.pekomon.pdfforge.infra

import com.pekomon.pdfforge.domain.PdfPaths
import com.pekomon.pdfforge.domain.PdfProgressEvent
import com.pekomon.pdfforge.domain.ShrinkOptions
import com.pekomon.pdfforge.domain.ShrinkPreset
import com.pekomon.pdfforge.domain.ShrinkResult
import com.pekomon.pdfforge.usecases.PdfShrinker
import org.apache.pdfbox.Loader
import org.apache.pdfbox.cos.COSBase
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDResources
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.nio.file.Files

class PdfBoxPdfShrinker : PdfShrinker {
    override fun shrink(
        paths: PdfPaths,
        options: ShrinkOptions,
        onProgress: (PdfProgressEvent) -> Unit,
    ): ShrinkResult {
        val beforeBytes = Files.size(paths.input)
        if (options.preset == ShrinkPreset.None) {
            Files.copy(paths.input, paths.output, java.nio.file.StandardCopyOption.REPLACE_EXISTING)
            val afterBytes = Files.size(paths.output)
            return ShrinkResult(
                outputPath = paths.output,
                beforeBytes = beforeBytes,
                afterBytes = afterBytes,
            )
        }
        val preset = presetConfig(options.preset)
        Loader.loadPDF(paths.input.toFile()).use { document ->
            val processed = mutableSetOf<COSBase>()
            val totalPages = document.numberOfPages
            for (pageIndex in 0 until totalPages) {
                val page = document.getPage(pageIndex)
                page.resources?.let { resources ->
                    processResources(document, resources, preset, processed)
                }
                onProgress(PdfProgressEvent.PageProcessed(pageIndex + 1, totalPages))
            }
            document.save(paths.output.toFile())
        }
        val afterBytes = Files.size(paths.output)
        return ShrinkResult(
            outputPath = paths.output,
            beforeBytes = beforeBytes,
            afterBytes = afterBytes,
        )
    }

    private fun processResources(
        document: PDDocument,
        resources: PDResources,
        preset: PresetConfig,
        processed: MutableSet<COSBase>,
    ) {
        for (name in resources.xObjectNames) {
            val xObject = resources.getXObject(name)
            when (xObject) {
                is PDFormXObject -> xObject.resources?.let {
                    processResources(document, it, preset, processed)
                }
                is PDImageXObject -> {
                    val cosObject = xObject.cosObject ?: continue
                    if (!processed.add(cosObject)) continue
                    val image = xObject.image ?: continue
                    val recompressed = recompressImage(document, image, preset)
                    resources.put(name, recompressed)
                }
            }
        }
    }

    private fun recompressImage(
        document: PDDocument,
        source: BufferedImage,
        preset: PresetConfig,
    ): PDImageXObject {
        val resized = resizeIfNeeded(source, preset.maxImagePx)
        val rgb = ensureRgb(resized)
        return JPEGFactory.createFromImage(document, rgb, preset.jpegQuality)
    }

    private fun resizeIfNeeded(image: BufferedImage, maxDimension: Int): BufferedImage {
        val width = image.width
        val height = image.height
        val largest = maxOf(width, height)
        if (largest <= maxDimension) {
            return image
        }
        val scale = maxDimension.toDouble() / largest.toDouble()
        val targetWidth = (width * scale).toInt().coerceAtLeast(1)
        val targetHeight = (height * scale).toInt().coerceAtLeast(1)
        val resized = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB)
        val g2d = resized.createGraphics()
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        g2d.drawImage(image, 0, 0, targetWidth, targetHeight, null)
        g2d.dispose()
        return resized
    }

    private fun ensureRgb(image: BufferedImage): BufferedImage {
        if (image.type == BufferedImage.TYPE_INT_RGB && !image.colorModel.hasAlpha()) {
            return image
        }
        val rgb = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
        val g2d: Graphics2D = rgb.createGraphics()
        g2d.color = Color.WHITE
        g2d.fillRect(0, 0, rgb.width, rgb.height)
        g2d.drawImage(image, 0, 0, null)
        g2d.dispose()
        return rgb
    }

    private fun presetConfig(preset: ShrinkPreset): PresetConfig {
        return when (preset) {
            ShrinkPreset.None -> PresetConfig(jpegQuality = 1.0f, maxImagePx = Int.MAX_VALUE)
            ShrinkPreset.High -> PresetConfig(jpegQuality = 0.85f, maxImagePx = 2400)
            ShrinkPreset.Medium -> PresetConfig(jpegQuality = 0.70f, maxImagePx = 2000)
            ShrinkPreset.Aggressive -> PresetConfig(jpegQuality = 0.55f, maxImagePx = 1600)
        }
    }

    private data class PresetConfig(
        val jpegQuality: Float,
        val maxImagePx: Int,
    )
}
