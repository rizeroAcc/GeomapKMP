import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {

    androidLibrary {
        namespace = "com.rizero.feature_project_mapview"
        compileSdk {
            version = release(36)
        }
        minSdk = 26

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
        androidResources{
            enable = true
        }
    }

    val xcfName = "feature-project-mapviewKit"

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    jvm()

    targets
        .filterIsInstance<KotlinNativeTarget>()
        .filter { it.konanTarget.family == Family.IOS }
        .forEach {
            it.binaries.framework {
                export(libs.decompose.core)
                baseName = xcfName
            }
        }

    fun detectTarget(): String {
        val hostOs = when (val os = System.getProperty("os.name").lowercase()) {
            "mac os x" -> "macos"
            else -> os.split(" ").first()
        }
        val hostArch = when (val arch = System.getProperty("os.arch").lowercase()) {
            "x86_64" -> "amd64"
            "arm64" -> "aarch64"
            else -> arch
        }
        val renderer = when (hostOs) {
            "macos" -> "metal"
            else -> "opengl"
        }
        return "${hostOs}-${hostArch}-${renderer}"
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlin.stdlib)
                implementation(libs.bundles.mvikotlin.corutines)
                implementation(libs.decompose.core)
                implementation(libs.decompose.extensions.compose)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.essenty.lifecycle.coroutines)

                implementation(libs.bundles.koin.annotations)
                implementation(libs.bundles.compose.multiplatform)
                implementation(libs.compose.uiToolingPreview)

                implementation(libs.maplibre.compose)

                implementation(projects.sharedCoreData)
                implementation(projects.sharedCoreComponent)
            }
        }

        jvmMain{
            dependencies{
                implementation(libs.bundles.compose.multiplatform.tooling)
                implementation(compose.desktop.currentOs)
                implementation(libs.maplibre.compose)
                runtimeOnly("org.maplibre.compose:maplibre-native-bindings-jni:0.12.1") {
                    capabilities {
                        requireCapability("org.maplibre.compose:maplibre-native-bindings-jni-${detectTarget()}")
                    }
                }
            }
        }

        androidMain {
            dependencies {
                implementation(libs.bundles.compose.multiplatform.tooling)
            }
        }

        iosMain {
            dependencies {
                //TODO Configure maplibre for ios
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.testExt.junit)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }

}