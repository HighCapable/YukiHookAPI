enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://api.xposed.info/") {
            content { 
                includeGroup("de.robv.android.xposed")
            }
        }
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
    rootProject { common { isEnabled = false } }
    projects(":samples") { common { isEnabled = false } }
    projects(":samples:demo-app", ":samples:demo-module", ":yukihookapi-stub") { android { isEnabled = false } }
    projects(":yukihookapi-core", ":yukihookapi-ksp-xposed") {
        android { className = rootProject.name }
        jvm { className = rootProject.name }
    }
}
rootProject.name = "YukiHookAPI"
include(":samples:demo-app", ":samples:demo-module")
include(":yukihookapi-core", ":yukihookapi-ksp-xposed", ":yukihookapi-stub")