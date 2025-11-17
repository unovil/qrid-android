import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java-library")
    id(libs.plugins.jetbrains.kotlin.jvm.get().pluginId)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}
