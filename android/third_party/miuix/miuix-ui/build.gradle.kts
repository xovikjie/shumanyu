// Copyright 2026, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0
// Android-only build integration for ShuManYu; component sources are unchanged.
plugins {
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    jvmToolchain(21)
    android {
        namespace = "top.yukonga.miuix.kmp.ui"
        compileSdk = 37
        minSdk = 24
    }
    sourceSets {
        commonMain {
            dependencies {
                api("org.jetbrains.compose.foundation:foundation:1.12.2")
                api(project(":miuix-core"))
                api(project(":miuix-squircle"))
                implementation("androidx.navigationevent:navigationevent-compose:1.2.0")
                implementation("org.jetbrains.compose.material3:material3-window-size-class:1.12.0-alpha03")
                implementation("com.materialkolor:material-color-utilities:6.0.0")
            }
        }
    }
}
