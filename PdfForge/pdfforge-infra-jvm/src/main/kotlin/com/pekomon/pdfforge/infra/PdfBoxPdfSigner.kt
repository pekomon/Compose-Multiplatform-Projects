package com.pekomon.pdfforge.infra

import com.pekomon.pdfforge.domain.PdfPaths
import com.pekomon.pdfforge.domain.PdfProgressEvent
import com.pekomon.pdfforge.domain.SignOptions
import com.pekomon.pdfforge.domain.SignResult
import com.pekomon.pdfforge.usecases.PdfSigner
import org.apache.pdfbox.Loader
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
        val signature = PDSignature().apply {
            setFilter(PDSignature.FILTER_ADOBE_PPKLITE)
            setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED)
            name = keyMaterial.certificate.subjectX500Principal.name
            reason = options.reason
            location = options.location
            signDate = Calendar.getInstance()
        }

        val signer = CmsSignature(keyMaterial.privateKey, keyMaterial.certificateChain)
        Loader.loadPDF(paths.input.toFile()).use { document ->
            if (options.visibleSignature) {
                SignatureOptions().use { sigOptions ->
                    val pageNumber = options.visibleSignaturePage.coerceAtLeast(1)
                    val visibleProps = buildVisibleSignature(
                        document,
                        signature,
                        keyMaterial.certificate.subjectX500Principal.name,
                        options,
                        pageNumber,
                    )
                    sigOptions.setVisualSignature(visibleProps)
                    sigOptions.setPage(pageNumber - 1)
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

private const val VisibleSignatureWidth = 200f
private const val VisibleSignatureHeight = 60f
private const val VisibleSignatureX = 36f
private const val VisibleSignatureY = 36f
private const val VisibleSignatureScale = 2f

private fun buildVisibleSignature(
    document: org.apache.pdfbox.pdmodel.PDDocument,
    @Suppress("UNUSED_PARAMETER") signature: PDSignature,
    signerName: String,
    options: SignOptions,
    pageNumber: Int,
): PDVisibleSigProperties {
    val image = createSignatureImage(signerName, options)
    val designer = PDVisibleSignDesigner(document, image, pageNumber)
        .xAxis(VisibleSignatureX)
        .yAxis(VisibleSignatureY)
        .width(VisibleSignatureWidth)
        .height(VisibleSignatureHeight)
        .signatureFieldName("Signature1")

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
): BufferedImage {
    val width = (VisibleSignatureWidth * VisibleSignatureScale).toInt().coerceAtLeast(1)
    val height = (VisibleSignatureHeight * VisibleSignatureScale).toInt().coerceAtLeast(1)
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val g = image.createGraphics()
    drawSignatureImage(g, width, height, signerName, options)
    g.dispose()
    return image
}

private fun drawSignatureImage(
    g: Graphics2D,
    width: Int,
    height: Int,
    signerName: String,
    options: SignOptions,
) {
    g.color = Color.WHITE
    g.fillRect(0, 0, width, height)
    g.color = Color.BLACK
    g.drawRect(0, 0, width - 1, height - 1)
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
    g.font = Font("SansSerif", Font.PLAIN, 12 * VisibleSignatureScale.toInt())
    val lines = buildList {
        add("Signed by: $signerName")
        options.reason?.let { add("Reason: $it") }
        options.location?.let { add("Location: $it") }
    }
    var y = 16 * VisibleSignatureScale
    lines.forEach { line ->
        g.drawString(line, 8f, y)
        y += 14 * VisibleSignatureScale
    }
}

private fun <T> java.util.Enumeration<T>.toList(): List<T> {
    val list = mutableListOf<T>()
    while (hasMoreElements()) {
        list.add(nextElement())
    }
    return list
}
