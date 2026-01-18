package com.pekomon.pdfforge.infra

import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Path
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.SecureRandom
import java.security.Security
import java.security.cert.X509Certificate
import java.util.Date

object TestCertificateFactory {
    fun createSelfSignedP12(
        output: Path,
        password: CharArray,
    ): X509Certificate {
        ensureBouncyCastle()
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048, SecureRandom())
        val keyPair = keyPairGenerator.generateKeyPair()

        val now = Date()
        val until = Date(now.time + 365L * 24 * 60 * 60 * 1000)
        val subject = X500Name("CN=PdfForge Test")
        val certificateBuilder = JcaX509v3CertificateBuilder(
            subject,
            BigInteger.valueOf(now.time),
            now,
            until,
            subject,
            keyPair.public,
        )
        val signer = JcaContentSignerBuilder("SHA256withRSA").build(keyPair.private)
        val certificate = JcaX509CertificateConverter()
            .setProvider("BC")
            .getCertificate(certificateBuilder.build(signer))

        val keyStore = KeyStore.getInstance("PKCS12")
        keyStore.load(null, null)
        keyStore.setKeyEntry("pdfforge", keyPair.private, password, arrayOf(certificate))
        Files.newOutputStream(output).use { outputStream ->
            keyStore.store(outputStream, password)
        }

        return certificate
    }

    private fun ensureBouncyCastle() {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(BouncyCastleProvider())
        }
    }
}
