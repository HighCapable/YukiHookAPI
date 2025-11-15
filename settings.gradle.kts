enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

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
        maven("https://jitpack.io")
        maven("https://api.xposed.info/")
    }
}

plugins {
    id("com.highcapable.gropify") version "1.0.1"
}

gropify {
    global {
        android {
            includeKeys("^project\\..*\$".toRegex())
            isRestrictedAccessEnabled = true
        }
    }

    rootProject {
        common {
            isEnabled = false
        }
    }

    projects(":samples:app", ":samples:module") {
        android {
            isEnabled = false
        }
    }
}

rootProject.name = "YukiHook"

include(":samples:app", ":samples:module")
include(":yukihook-core", ":yukihook-api-rovo89", ":yukihook-api-libxposed", ":yukihook-api-helper")