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
        namespace = "top.yukonga.miuix.kmp.preference"
        compileSdk = 37
        minSdk = 24
    }
    sourceSets {
        commonMain {
            dependencies {
                api("org.jetbrains.compose.foundation:foundation:1.12.2")
                api(project(":miuix-ui"))
            }
        }
    }
}
