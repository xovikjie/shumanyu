pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ShuManYu"
include(":app")

// Miuix main sources pinned in third_party/miuix/UPSTREAM.md.
include(":miuix-core")
project(":miuix-core").projectDir = file("third_party/miuix/miuix-core")
include(":miuix-shader")
project(":miuix-shader").projectDir = file("third_party/miuix/miuix-shader")
include(":miuix-squircle")
project(":miuix-squircle").projectDir = file("third_party/miuix/miuix-squircle")
include(":miuix-ui")
project(":miuix-ui").projectDir = file("third_party/miuix/miuix-ui")
include(":miuix-preference")
project(":miuix-preference").projectDir = file("third_party/miuix/miuix-preference")
include(":miuix-icons")
project(":miuix-icons").projectDir = file("third_party/miuix/miuix-icons")
