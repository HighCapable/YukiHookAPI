enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("com.highcapable.sweetdependency") version "1.0.4"
    id("com.highcapable.sweetproperty") version "1.0.8"
}

sweetProperty {
    global {
        sourcesCode {
            includeKeys("^project\\..*\$".toRegex())
            isEnableRestrictedAccess = true
        }
    }
    rootProject { all { isEnable = false } }
    project(":samples:app", ":samples:module") { sourcesCode { isEnable = false } }
}

rootProject.name = "YukiHook"

include(":samples:app", ":samples:module")
include(":yukihook-core", ":yukihook-api-rovo89", ":yukihook-api-libxposed", ":yukihook-api-helper")