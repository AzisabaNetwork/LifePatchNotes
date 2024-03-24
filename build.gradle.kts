plugins {
    kotlin("jvm") version "1.9.23"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "net.azisaba"
version = "1.0.0-SNAPSHOT"

java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
}

dependencies {
    implementation("dev.kord:kord-core:0.13.1")
    implementation("org.slf4j:slf4j-simple:2.0.1")
    //noinspection VulnerableLibrariesLocal
    compileOnly("com.destroystokyo.paper:paper-api:1.15.2-R0.1-SNAPSHOT")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks {
    test {
        useJUnitPlatform()
    }

    shadowJar {
        //relocate("dev.kord", "net.azisaba.lifepatchnotes.libs.dev.kord")
        relocate("com.charleskorn.kaml", "net.azisaba.lifepatchnotes.libs.com.charleskorn.kaml")
        relocate("kotlin.", "net.azisaba.lifepatchnotes.libs.kotlin.")
        relocate("io.ktor", "net.azisaba.lifepatchnotes.libs.io.ktor")
        //relocate("io.github", "net.azisaba.lifepatchnotes.libs.io.github")
        relocate("mu", "net.azisaba.lifepatchnotes.libs.mu")
        exclude("org/slf4j/**")
        relocate("co.touchlab", "net.azisaba.lifepatchnotes.libs.co.touchlab")
        relocate("org.jetbrains", "net.azisaba.lifepatchnotes.libs.org.jetbrains")
        relocate("org.intellij", "net.azisaba.lifepatchnotes.libs.org.intellij")
    }
}

kotlin {
    jvmToolchain(8)
}
