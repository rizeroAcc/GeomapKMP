import org.jetbrains.compose.resources.ResourcesExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    androidLibrary {
        namespace = "com.rizero.shared_core_component"
        compileSdk = 36
        minSdk = 26

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        androidResources {
            enable = true
        }
    }

    val xcfName = "shared-core-componentKit"

    jvm()
    iosX64 {}

    iosArm64 {}

    iosSimulatorArm64 {}
    targets
        .filterIsInstance<KotlinNativeTarget>()
        .filter { it.konanTarget.family == Family.IOS }
        .forEach {
            it.binaries.framework {
                export(libs.decompose.core)
                baseName = xcfName
            }
        }
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.bundles.compose.multiplatform)

                implementation(libs.compose.uiToolingPreview)

                implementation(libs.decompose.core)
                implementation(libs.decompose.extensions.compose)
            }
        }

        iosMain {
            dependencies {
            }
        }

        androidMain {
            dependencies {
                implementation(libs.compose.components.resources)
                implementation(libs.bundles.compose.multiplatform.tooling)
            }
        }
        jvmMain{
            dependencies{
                implementation(libs.bundles.compose.multiplatform.tooling)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.testExt.junit)
            }
        }
    }
}