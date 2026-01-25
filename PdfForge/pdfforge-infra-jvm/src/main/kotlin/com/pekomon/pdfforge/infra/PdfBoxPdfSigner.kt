package com.pekomon.pdfforge.infra

import com.pekomon.pdfforge.domain.PdfPaths
import com.pekomon.pdfforge.domain.PdfProgressEvent
import com.pekomon.pdfforge.domain.SignOptions
import com.pekomon.pdfforge.domain.SignResult
import com.pekomon.pdfforge.domain.VisibleSignaturePosition
import com.pekomon.pdfforge.domain.VisibleSignatureStyle
import com.pekomon.pdfforge.usecases.PdfSigner
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSigProperties
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSignDesigner
import org.bouncycastle.cert.jcajce.JcaCertStore
import org.bouncycastle.cms.CMSProcessableByteArray
import org.bouncycastle.cms.CMSSignedDataGenerator
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.X509Certificate
import java.util.Calendar
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class PdfBoxPdfSigner : PdfSigner {
    override fun signWithP12(
        paths: PdfPaths,
        p12Path: Path,
        password: CharArray,
        options: SignOptions,
        onProgress: (PdfProgressEvent) -> Unit,
    ): SignResult {
        val beforeBytes = Files.size(paths.input)
        val keyMaterial = loadKeyMaterial(p12Path, password)
        val signDate = Calendar.getInstance()
        val signature = PDSignature().apply {
            setFilter(PDSignature.FILTER_ADOBE_PPKLITE)
            setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED)
            name = keyMaterial.certificate.subjectX500Principal.name
            reason = options.reason
            location = options.location
            this.signDate = signDate
        }

        val signer = CmsSignature(keyMaterial.privateKey, keyMaterial.certificateChain)
        Loader.loadPDF(paths.input.toFile()).use { document ->
            if (options.visibleSignature) {
                SignatureOptions().use { sigOptions ->
                    val pageIndex = visiblePageIndex(document, options.visibleSignaturePage)
                    val visibleProps = buildVisibleSignature(
                        document,
                        keyMaterial.certificate.subjectX500Principal.name,
                        options,
                        pageIndex,
                        signDate,
                    )
                    sigOptions.setVisualSignature(visibleProps)
                    sigOptions.setPage(pageIndex)
                    document.addSignature(signature, signer, sigOptions)
                    Files.newOutputStream(paths.output).use { output ->
                        document.saveIncremental(output)
                    }
                }
            } else {
                document.addSignature(signature, signer)
                Files.newOutputStream(paths.output).use { output ->
                    document.saveIncremental(output)
                }
            }
        }

        val afterBytes = Files.size(paths.output)
        return SignResult(
            outputPath = paths.output,
            beforeBytes = beforeBytes,
            afterBytes = afterBytes,
        )
    }

    private fun loadKeyMaterial(p12Path: Path, password: CharArray): KeyMaterial {
        val keyStore = KeyStore.getInstance("PKCS12")
        Files.newInputStream(p12Path).use { input ->
            keyStore.load(input, password)
        }
        val alias = keyStore.aliases().toList().firstOrNull()
            ?: error("No aliases found in PKCS#12 file")
        val key = keyStore.getKey(alias, password) as? PrivateKey
            ?: error("No private key found in PKCS#12 file")
        val certChain = keyStore.getCertificateChain(alias)
            ?.map { it as X509Certificate }
            ?: error("No certificate chain found in PKCS#12 file")
        return KeyMaterial(key, certChain.first(), certChain)
    }

    private data class KeyMaterial(
        val privateKey: PrivateKey,
        val certificate: X509Certificate,
        val certificateChain: List<X509Certificate>,
    )

    private class CmsSignature(
        private val privateKey: PrivateKey,
        private val certificateChain: List<X509Certificate>,
    ) : SignatureInterface {
        override fun sign(content: InputStream): ByteArray {
            val contentBytes = content.readBytes()
            val generator = CMSSignedDataGenerator()
            val signerInfo = JcaSignerInfoGeneratorBuilder(
                JcaDigestCalculatorProviderBuilder().build(),
            ).build(JcaContentSignerBuilder("SHA256withRSA").build(privateKey), certificateChain.first())
            generator.addSignerInfoGenerator(signerInfo)
            generator.addCertificates(JcaCertStore(certificateChain))
            val cmsData = generator.generate(CMSProcessableByteArray(contentBytes), false)
            return cmsData.encoded
        }
    }
}

private const val VisibleSignatureScale = 2f
private const val VisibleSignatureMargin = 36f
private const val CompactSignatureWidth = 200f
private const val CompactSignatureHeight = 60f
private const val DetailedSignatureWidth = 260f
private const val DetailedSignatureHeight = 90f
private val SignatureTimestampFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm XXX")

private fun buildVisibleSignature(
    document: PDDocument,
    signerName: String,
    options: SignOptions,
    pageIndex: Int,
    signDate: Calendar,
): PDVisibleSigProperties {
    val pageNumber = pageIndex + 1
    val image = createSignatureImage(signerName, options, signDate)
    val page = document.getPage(pageIndex)
    val layout = signatureLayout(options.visibleSignatureStyle)
    val placement = signaturePlacement(page, layout, options.visibleSignaturePosition)
    val designer = PDVisibleSignDesigner(document, image, pageNumber)
        .xAxis(placement.x)
        .yAxis(placement.y)
        .width(layout.width)
        .height(layout.height)
        .signatureFieldName("Signature$pageNumber")

    return PDVisibleSigProperties()
        .signerName(signerName)
        .signerLocation(options.location ?: "")
        .signatureReason(options.reason ?: "")
        .page(pageNumber)
        .visualSignEnabled(true)
        .setPdVisibleSignature(designer)
        .apply { buildSignature() }
}

private fun createSignatureImage(
    signerName: String,
    options: SignOptions,
    signDate: Calendar,
): BufferedImage {
    val layout = signatureLayout(options.visibleSignatureStyle)
    val width = (layout.width * VisibleSignatureScale).toInt().coerceAtLeast(1)
    val height = (layout.height * VisibleSignatureScale).toInt().coerceAtLeast(1)
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val g = image.createGraphics()
    drawSignatureImage(g, width, height, signerName, options, signDate)
    g.dispose()
    return image
}

private fun drawSignatureImage(
    g: Graphics2D,
    width: Int,
    height: Int,
    signerName: String,
    options: SignOptions,
    signDate: Calendar,
) {
    val layout = signatureLayout(options.visibleSignatureStyle)
    g.color = Color.WHITE
    g.fillRect(0, 0, width, height)
    g.color = Color.BLACK
    g.drawRect(0, 0, width - 1, height - 1)
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
    g.font = Font("SansSerif", Font.PLAIN, (layout.fontSize * VisibleSignatureScale).toInt())
    val lines = buildSignatureLines(signerName, options, signDate)
    val lineHeight = layout.lineHeight * VisibleSignatureScale
    var y = layout.paddingY * VisibleSignatureScale
    lines.forEach { line ->
        g.drawString(line, layout.paddingX * VisibleSignatureScale, y)
        y += lineHeight
    }
}

private fun buildSignatureLines(
    signerName: String,
    options: SignOptions,
    signDate: Calendar,
): List<String> {
    val timestamp = SignatureTimestampFormat.format(signDate.toInstant().atZone(ZoneId.systemDefault()))
    return when (options.visibleSignatureStyle) {
        VisibleSignatureStyle.Compact -> buildList {
            add("Digitally signed by $signerName")
            add("Date: $timestamp")
        }
        VisibleSignatureStyle.Detailed -> buildList {
            add("Signed by: $signerName")
            add("Date: $timestamp")
            options.reason?.takeIf { it.isNotBlank() }?.let { add("Reason: $it") }
            options.location?.takeIf { it.isNotBlank() }?.let { add("Location: $it") }
        }
    }
}

private data class SignatureLayout(
    val width: Float,
    val height: Float,
    val fontSize: Int,
    val lineHeight: Float,
    val paddingX: Float,
    val paddingY: Float,
)

private data class SignaturePlacement(
    val x: Float,
    val y: Float,
)

private fun signatureLayout(style: VisibleSignatureStyle): SignatureLayout {
    return when (style) {
        VisibleSignatureStyle.Compact -> SignatureLayout(
            width = CompactSignatureWidth,
            height = CompactSignatureHeight,
            fontSize = 12,
            lineHeight = 14f,
            paddingX = 8f,
            paddingY = 18f,
        )
        VisibleSignatureStyle.Detailed -> SignatureLayout(
            width = DetailedSignatureWidth,
            height = DetailedSignatureHeight,
            fontSize = 10,
            lineHeight = 12f,
            paddingX = 8f,
            paddingY = 18f,
        )
    }
}

private fun signaturePlacement(
    page: PDPage,
    layout: SignatureLayout,
    position: VisibleSignaturePosition,
): SignaturePlacement {
    val pageBox = page.mediaBox
    val pageWidth = pageBox.width
    val pageHeight = pageBox.height
    val maxX = (pageWidth - layout.width).coerceAtLeast(0f)
    val maxY = (pageHeight - layout.height).coerceAtLeast(0f)
    val rawX = when (position) {
        VisibleSignaturePosition.BottomLeft, VisibleSignaturePosition.TopLeft -> VisibleSignatureMargin
        VisibleSignaturePosition.BottomRight, VisibleSignaturePosition.TopRight ->
            pageWidth - layout.width - VisibleSignatureMargin
    }
    val rawY = when (position) {
        VisibleSignaturePosition.BottomLeft, VisibleSignaturePosition.BottomRight ->
            pageHeight - layout.height - VisibleSignatureMargin
        VisibleSignaturePosition.TopLeft, VisibleSignaturePosition.TopRight -> VisibleSignatureMargin
    }
    return SignaturePlacement(
        x = rawX.coerceIn(0f, maxX),
        y = rawY.coerceIn(0f, maxY),
    )
}

private fun visiblePageIndex(document: PDDocument, pageNumber: Int): Int {
    val totalPages = document.numberOfPages
    if (totalPages <= 1) {
        return 0
    }
    return (pageNumber - 1).coerceIn(0, totalPages - 1)
}

private fun <T> java.util.Enumeration<T>.toList(): List<T> {
    val list = mutableListOf<T>()
    while (hasMoreElements()) {
        list.add(nextElement())
    }
    return list
}
