plugins {
    kotlin("jvm")
}

dependencies {
    implementation(projects.pdfforgeDomain)
    implementation(projects.pdfforgeUsecases)
    implementation(libs.pdfbox)
    implementation(libs.bouncyCastle.bcprov)
    implementation(libs.bouncyCastle.bcpkix)
    testImplementation(kotlin("test"))
}
