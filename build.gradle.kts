import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")

    kotlin("plugin.serialization") version "2.0.20"
}

group = "com.ironmeddie"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    // network
//    val ktor_version="3.0.0"
//    implementation("io.ktor:ktor-client-core:$ktor_version")
//    implementation("io.ktor:ktor-client-cio:$ktor_version")
//    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
//    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
//    implementation("io.ktor:ktor-serialization-kotlinx-protobuf:$ktor_version")
//    implementation("io.ktor", "ktor-client-core", ktor_version)
//    implementation("io.ktor", "ktor-client-serialization", ktor_version)
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")


    // fcm API
    implementation(platform("com.google.cloud:libraries-bom:26.75.0"))

    implementation("com.google.cloud:google-cloud-storage")
    implementation("com.google.firebase:firebase-admin:9.2.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.19.0")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "SupportHelper"
            packageVersion = "1.0.1"
        }
    }
}
