plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.kotlin.ksp)
}

room {
    schemaDirectory("$projectDir/schemas")
}

kotlin {
    androidLibrary {
        namespace = "com.rizero.shared_core_database"
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

    val xcfName = "shared-core-databaseKit"

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
                implementation(libs.room.runtime)
                implementation(libs.sqlite.bundled)
                implementation(libs.kotlin.stdlib)
                implementation(libs.bundles.koin.annotations)
            }
        }
        iosMain {
            dependencies {
            }
        }
        androidMain {
            dependencies {
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
    add("kspAndroid", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspJvm",libs.room.compiler)
}