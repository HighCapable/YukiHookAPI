enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
plugins {
    id("com.highcapable.sweetdependency") version "1.0.1"
    id("com.highcapable.sweetproperty") version "1.0.2"
}
sweetProperty {
    global {
        sourcesCode {
            includeKeys("^project\\..*\$".toRegex())
            isEnableRestrictedAccess = true
        }
    }
    rootProject { all { isEnable = false } }
    project("samples") { all { isEnable = false } }
    project("samples:demo-app") { sourcesCode { isEnable = false } }
    project("samples:demo-module") { sourcesCode { isEnable = false } }
    project("yukihookapi-core") { sourcesCode { className = rootProject.name } }
    project("yukihookapi-ksp-xposed") { sourcesCode { className = rootProject.name } }
    project("yukihookapi-stub") { sourcesCode { isEnable = false } }
}
rootProject.name = "YukiHookAPI"
include(":samples:demo-app", ":samples:demo-module")
include(":yukihookapi-core", ":yukihookapi-ksp-xposed", ":yukihookapi-stub")