import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family

plugins {
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {

    androidLibrary {
        namespace = "com.rizero.geomapkmp"
        compileSdk = libs.versions.android.compileSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }

        androidResources {
            enable = true
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    )

    targets
        .filterIsInstance<KotlinNativeTarget>()
        .filter { it.konanTarget.family == Family.IOS }
        .forEach {
            it.binaries.framework {
                export(libs.decompose.core)
                baseName = "Geomap"
                isStatic = true
            }
        }

    jvm()
    
    sourceSets {
        commonMain.dependencies {
            implementation(projects.sharedCoreNetwork)
            implementation(projects.sharedCoreDatasource)
            implementation(projects.sharedCoreDatabase)
            implementation(projects.sharedCoreComponent)
            implementation(projects.sharedCoreData)

            implementation(projects.featureAuthorization)
            implementation(projects.featureRegistration)
            implementation(projects.featureUserProfile)
            implementation(projects.featureProjectSelect)

            implementation(libs.decompose.core)
            implementation(libs.decompose.extensions.compose)

            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.bundles.compose.multiplatform)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.bundles.koin.annotations)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
        androidMain.dependencies{
            implementation(libs.bundles.koin.annotations)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "com.rizero.geomapkmp.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.rizero.geomapkmp"
            packageVersion = "1.0.0"
        }
    }
}
