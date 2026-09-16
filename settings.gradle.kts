enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://raw.githubusercontent.com/HighCapable/maven-repository/main/repository/releases")
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
        maven("https://raw.githubusercontent.com/HighCapable/maven-repository/main/repository/releases")
    }
}

plugins {
    id("com.highcapable.gropify") version "1.0.2"
}

gropify {
    global {
        android {
            includeKeys("^project\\..*$".toRegex())
            isRestrictedAccessEnabled = true
        }
    }

    rootProject {
        common {
            isEnabled = false
        }
    }

    projects(":samples") {
        common {
            isEnabled = false
        }
    }
    projects(
        ":samples:demo-app",
        ":samples:demo-module-legacy",
        ":samples:demo-module-modern"
    ) {
        android {
            className = "Demo"
        }
    }

    projects(":yukihookapi-bom") {
        jvm {
            isEnabled = false
        }
    }
    projects(
        ":yukihookapi-core",
        ":yukihookapi-core-lint",
        ":yukihookapi-compiler"
    ) {
        sourceCode {
            className = rootProject.name
        }
    }
    projects(
        ":yukihookapi-runtime-stub",
        ":yukihookapi-runtime-xposed82",
        ":yukihookapi-runtime-libxposed"
    ) {
        android {
            isEnabled = false
        }
    }
}

rootProject.name = "YukiHookAPI"

include(":samples:demo-app", ":samples:demo-module-legacy", ":samples:demo-module-modern")
include(":yukihookapi-bom")
include(":yukihookapi-core", ":yukihookapi-compiler", ":yukihookapi-runtime-stub")
include(":yukihookapi-core-lint")
include(":yukihookapi-runtime-xposed82", ":yukihookapi-runtime-libxposed")