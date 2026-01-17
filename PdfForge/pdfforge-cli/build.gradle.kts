plugins {
    kotlin("jvm")
    application
}

application {
    mainClass.set("com.pekomon.pdfforge.cli.MainKt")
}

dependencies {
    implementation(projects.pdfforgeUsecases)
    implementation(projects.pdfforgeInfraJvm)
}
