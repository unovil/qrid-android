import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.ksp)
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
}

android {
    namespace = "com.unovil.tardyscan.core.domain"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:data"))

    implementation(libs.hilt)
    ksp(libs.hilt.android.compiler)
    implementation(libs.supabase.postgrest.kt)
    implementation(libs.supabase.storage.kt)
    implementation(libs.fernet.java8)
    implementation(libs.argon2kt)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}