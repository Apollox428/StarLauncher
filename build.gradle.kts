import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "com.apollox"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://jitpack.io")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://maven.google.com")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation("net.java.dev.jna:jna-platform:5.14.0")
    implementation("com.github.alexfacciorusso:windows-registry-ktx:6a9b0b89d2")
    implementation("com.konyaco:fluent:0.0.1-dev.8")
    implementation("com.konyaco:fluent-icons-extended:0.0.1-dev.8")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.pty4j:pty4j:0.13.0")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Exe)
            packageName = "DreamLauncher"
            packageVersion = "1.0.0"
        }
    }
}
