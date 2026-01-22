PdfForge is a Kotlin/JVM desktop app plus CLI for shrinking and signing PDFs.

## Modules
- `pdfforge-domain`: domain models and errors
- `pdfforge-usecases`: use cases + port contracts
- `pdfforge-infra-jvm`: PDFBox/BouncyCastle implementations
- `pdfforge-gui-desktop`: Compose Desktop GUI
- `pdfforge-cli`: command-line interface

## Build
```shell
./gradlew build
```

## Run the GUI (Desktop)
```shell
./gradlew :pdfforge-gui-desktop:run
```

## Use the CLI
```shell
./gradlew :pdfforge-cli:run --args="shrink input.pdf output.pdf --preset medium"
./gradlew :pdfforge-cli:run --args="sign input.pdf output.pdf --p12 cert.p12 --pass env:P12_PASS"
```

### CLI help
```shell
./gradlew :pdfforge-cli:run --args="--help"
```

## Create a test certificate (.p12)
```shell
openssl req -x509 -newkey rsa:2048 -keyout pdfforge.key -out pdfforge.crt -days 365 -nodes -subj "/CN=PdfForge Test"
openssl pkcs12 -export -out pdfforge.p12 -inkey pdfforge.key -in pdfforge.crt -name "PdfForge Test"
```

## Notes
- Self-signed certificates will show as untrusted in Acrobat/Reader.
- Signatures are currently invisible (no on-page stamp).
