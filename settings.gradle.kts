pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"

    id("com.autonomousapps.build-health") version "3.4.1"
    id("com.android.application") version "8.10.1" apply false
    id("com.android.library") version "8.10.1" apply false
    id("org.jetbrains.kotlin.android") version "2.1.10" apply false
}

rootProject.name = "Tardy Scanner"
include(":app")
include(":core")
include(":core:common")
include(":core:ui")
include(":core:data")
include(":core:domain")
include(":core:designsystem")
include(":core:datastore")
include(":core:model")
include(":feature:history")
include(":feature:scan")
include(":feature:onboarding")
include(":feature:settings")
