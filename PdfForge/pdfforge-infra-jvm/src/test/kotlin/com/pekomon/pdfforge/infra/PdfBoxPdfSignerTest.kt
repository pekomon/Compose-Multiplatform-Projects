package com.pekomon.pdfforge.infra

import com.pekomon.pdfforge.domain.PdfPaths
import com.pekomon.pdfforge.domain.SignOptions
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField
import org.bouncycastle.cms.CMSProcessableByteArray
import org.bouncycastle.cms.CMSSignedData
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder
import org.bouncycastle.cert.X509CertificateHolder
import org.bouncycastle.util.Selector
import java.nio.file.Files
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertTrue

class PdfBoxPdfSignerTest {
    @Test
    fun sign_creates_signature_and_verifies() {
        val tempDir = createTempDirectory("pdfforge-sign-test")
        val input = tempDir.resolve("input.pdf")
        val output = tempDir.resolve("signed.pdf")
        val p12 = tempDir.resolve("signing.p12")
        val password = "secret".toCharArray()

        TestPdfFactory.createTextPdf(input)
        TestCertificateFactory.createSelfSignedP12(p12, password)

        val signer = PdfBoxPdfSigner()
        val result = signer.signWithP12(
            PdfPaths(input, output),
            p12,
            password,
            SignOptions(reason = "Test", location = "Unit"),
        ) {}

        assertTrue(result.afterBytes > 0)

        Loader.loadPDF(output.toFile()).use { document ->
            val signatures = document.signatureDictionaries
            assertTrue(signatures.isNotEmpty())
            val signature = signatures.first()

            val signedContent = Files.newInputStream(output).use { stream ->
                signature.getSignedContent(stream)
            }
            val signatureBytes = Files.newInputStream(output).use { stream ->
                signature.getContents(stream)
            }

            val cms = CMSSignedData(CMSProcessableByteArray(signedContent), signatureBytes)
            val signerInfo = cms.signerInfos.signers.first()
            @Suppress("UNCHECKED_CAST")
            val selector = signerInfo.sid as Selector<X509CertificateHolder>
            val certs = cms.certificates.getMatches(selector)
            val certHolder = certs.iterator().next() as X509CertificateHolder
            val verifier = JcaSimpleSignerInfoVerifierBuilder().build(certHolder)

            assertTrue(signerInfo.verify(verifier))
        }
    }

    @Test
    fun sign_with_visible_signature_creates_widget() {
        val tempDir = createTempDirectory("pdfforge-visible-sign-test")
        val input = tempDir.resolve("input.pdf")
        val output = tempDir.resolve("signed.pdf")
        val p12 = tempDir.resolve("signing.p12")
        val password = "secret".toCharArray()

        TestPdfFactory.createTextPdf(input)
        TestCertificateFactory.createSelfSignedP12(p12, password)

        val signer = PdfBoxPdfSigner()
        signer.signWithP12(
            PdfPaths(input, output),
            p12,
            password,
            SignOptions(visibleSignature = true, visibleSignaturePage = 1),
        ) {}

        Loader.loadPDF(output.toFile()).use { document ->
            val acroForm = requireNotNull(document.documentCatalog.acroForm)
            val signatureFields = acroForm.fields.filterIsInstance<PDSignatureField>()
            val widget = signatureFields.first().widgets.first()
            val rect = widget.rectangle
            assertTrue(rect.width > 0 && rect.height > 0)
        }
    }
}
