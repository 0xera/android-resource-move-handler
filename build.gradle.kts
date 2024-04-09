enum class Version(val studioVersion: String, val versionName: String) {
    Hedgehog("2023.1.1.2", "Hedgehog"),
    Iguana("2023.2.1.1", "Iguana"),
    Jellyfish("2023.3.1.1", "Jellyfish"),
    Koala("2024.1.1.1", "Koala"),
}

val currentVersion = Version.Koala

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.21"
    id("org.jetbrains.intellij") version "1.17.2"
}

group = "com.zeroxera.android.res"
version = "0.1.1-${currentVersion.versionName}"

repositories {
    mavenCentral()
}

intellij {
    version.set(currentVersion.studioVersion)
    type.set("AI")
    downloadSources.set(true)

    plugins.set(listOf("android", "org.jetbrains.kotlin", "java"))
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_17.toString()
        targetCompatibility = JavaVersion.VERSION_17.toString()
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    runIde {
        val idePath = project.properties["studio.path"]?.toString().orEmpty()
        if (idePath.isNotEmpty()) {
            ideDir.set(file(idePath))
        }
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
