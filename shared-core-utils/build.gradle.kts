plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.android.lint)
}

kotlin {
    androidLibrary {
        namespace = "com.rizero.shared_core_utils"
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

    val xcfName = "shared-core-utilsKit"

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
                implementation(libs.ktor.client.core)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {

            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.testExt.junit)
            }
        }

        iosMain {
            dependencies {

            }
        }
    }

}