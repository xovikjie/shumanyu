import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

val releaseSigning = Properties().apply {
    rootProject.file("signing.properties").takeIf { it.isFile }?.inputStream()?.use { load(it) }
}
fun signingValue(key: String, envName: String): String? =
    providers.environmentVariable(envName).orNull ?: releaseSigning.getProperty(key)
val releaseKeyPath = signingValue("storeFile", "SHUMANYU_KEYSTORE")
val releaseStorePassword = signingValue("storePassword", "SHUMANYU_STORE_PASSWORD")
val releaseKeyPassword = signingValue("keyPassword", "SHUMANYU_KEY_PASSWORD") ?: releaseStorePassword
val hasReleaseSigning = !releaseKeyPath.isNullOrBlank() && !releaseStorePassword.isNullOrBlank() &&
    rootProject.file(requireNotNull(releaseKeyPath)).isFile

android {
    namespace = "com.shumanyu.app"
    compileSdk = 37
    defaultConfig {
        applicationId = "com.shumanyu.app"
        minSdk = 26
        targetSdk = 37
        versionCode = 3
        versionName = "1.0.2"
    }
    signingConfigs {
        if (hasReleaseSigning) create("release") {
            storeFile = rootProject.file(requireNotNull(releaseKeyPath))
            storePassword = releaseStorePassword
            keyAlias = signingValue("keyAlias", "SHUMANYU_KEY_ALIAS") ?: "shumanyu"
            keyPassword = releaseKeyPassword
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = false
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            if (hasReleaseSigning) signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation("org.jsoup:jsoup:1.18.3")
    implementation("org.mozilla:rhino:1.8.1")
    implementation("com.jayway.jsonpath:json-path:2.9.0")
    testImplementation("org.json:json:20240303")
    implementation("io.github.kyant0:backdrop:2.0.1")
    testImplementation("junit:junit:4.13.2")
    implementation("androidx.activity:activity-compose:1.12.4")
    implementation("androidx.navigationevent:navigationevent-android:1.2.0")
    implementation("androidx.navigationevent:navigationevent-compose-android:1.2.0")
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation(project(":miuix-ui"))
    implementation(project(":miuix-preference"))
    implementation(project(":miuix-icons"))
}
