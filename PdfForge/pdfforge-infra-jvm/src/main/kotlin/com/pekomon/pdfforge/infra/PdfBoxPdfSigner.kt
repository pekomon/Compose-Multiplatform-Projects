package com.pekomon.pdfforge.infra

import com.pekomon.pdfforge.domain.PdfPaths
import com.pekomon.pdfforge.domain.PdfProgressEvent
import com.pekomon.pdfforge.domain.SignOptions
import com.pekomon.pdfforge.domain.SignResult
import com.pekomon.pdfforge.usecases.PdfSigner
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface
import org.bouncycastle.cert.jcajce.JcaCertStore
import org.bouncycastle.cms.CMSProcessableByteArray
import org.bouncycastle.cms.CMSSignedDataGenerator
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder
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
            document.addSignature(signature, signer)
            Files.newOutputStream(paths.output).use { output ->
                document.saveIncremental(output)
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

private fun <T> java.util.Enumeration<T>.toList(): List<T> {
    val list = mutableListOf<T>()
    while (hasMoreElements()) {
        list.add(nextElement())
    }
    return list
}
