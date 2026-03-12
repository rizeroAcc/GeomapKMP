rootProject.name = "GeomapKMP"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("org.jetbrains.kotlin.multiplatform") version "2.3.0"
        id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

//todo move dto to mavenLocal repository. Not necessary
includeBuild("C:\\Users\\Admin\\Desktop\\server\\shared-dto"){
    name = "server-shared-dto"
    dependencySubstitution {
        substitute(module("com.rizero:shared-dto"))
            .using(project(":"))
    }
}

include(":androidApp")
include(":composeApp")
include(":shared-core-network")
include(":shared-core-datasource")
include(":shared-core-utils")
include(":shared-core-database")
include(":shared-core-data")
include(":feature-authorization")
include(":shared-core-component")
include(":feature-project-select")
include(":feature-registration")
include(":feature-user-profile")
include(":feature-project-mapview")
