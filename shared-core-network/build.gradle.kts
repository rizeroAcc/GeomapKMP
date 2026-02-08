plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.koin.compiler)
}

kotlin {
    androidLibrary {
        namespace = "com.rizero.shared_core_network"
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
    val xcfName = "shared-core-networkKit"

    iosX64 {
        binaries.framework { baseName = xcfName }
    }

    iosArm64 {
        binaries.framework { baseName = xcfName }
    }

    iosSimulatorArm64 {
        binaries.framework { baseName = xcfName }
    }
    jvm()

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.sharedCoreUtils)
                api("com.rizero:shared-dto")
                implementation(libs.kotlin.stdlib)
                implementation(libs.bundles.ktor.multiplatform)
                implementation(libs.bundles.koin.annotations)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.ktor.client.okhttp)
                implementation(libs.okhttp.logging.interceptor)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.ktor.client.okhttp)
                implementation(libs.okhttp.logging.interceptor)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.ktor.client.darwin)
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

