plugins {
    id(libs.plugins.android.application.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.secrets)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "com.unovil.tardyscan"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.unovil.tardyscan"
        minSdk = 26
        targetSdk = 35
        versionCode = 5
        versionName = "1.1.1"

        testInstrumentationRunner = "com.unovil.tardyscan.HiltTestRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            merges += "META-INF/LICENSE.md"
            merges += "META-INF/LICENSE-notice.md"
        }
    }
}

secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "local.defaults.properties"
}

dependencies {

    implementation(project(":core:domain"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:datastore"))
    implementation(project(":core:ui"))
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(project(":core:common"))

    implementation(project(":feature:history"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:scan"))
    implementation(project(":feature:settings"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.mockk.agent)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // scanning
    implementation(libs.barcode.scanning)
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    implementation(libs.accompanist.permissions)
    implementation(libs.fernet.java8)

    // supabase
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.auth.kt)
    implementation(libs.supabase.postgrest.kt)
    implementation(libs.supabase.storage.kt)
    implementation(libs.ktor.client.android)
    implementation(libs.hilt)
    ksp(libs.hilt.android.compiler)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.kotlinx.datetime)

    // hashing
    implementation(libs.argon2kt)

    // testing supabase
    testImplementation(libs.multiplatform.settings.test)
    testImplementation(platform(libs.kotlin.faker.bom))
    testImplementation(libs.kotlin.faker)
    testImplementation(libs.kotlin.faker.edu)
    testImplementation(project(":app"))

    // load bearing hilt dependency
    implementation(libs.javapoet)
}
