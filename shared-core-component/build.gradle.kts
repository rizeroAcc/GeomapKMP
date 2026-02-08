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


    }

    val xcfName = "shared-core-componentKit"

    jvm()
    iosX64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
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

dependencies {
    androidRuntimeClasspath("org.jetbrains.compose.ui:ui-tooling:1.10.0")
}