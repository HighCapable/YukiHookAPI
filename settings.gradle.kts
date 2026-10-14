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

    projects(":yukihook-bom") {
        jvm {
            isEnabled = false
        }
    }
    projects(
        ":yukihook-core",
        ":yukihook-compiler",
        ":yukihook-extension-kavaref",
        ":yukihook-runtime-xposed82",
        ":yukihook-runtime-libxposed",
        ":yukihook-core-lint",
        ":yukihook-extension-kavaref-lint"
    ) {
        sourceCode {
            className = rootProject.name
        }
    }
    projects(":yukihook-runtime-stub") {
        android {
            isEnabled = false
        }
    }
}

rootProject.name = "YukiHook"

include(":samples:demo-app", ":samples:demo-module-legacy", ":samples:demo-module-modern")
include(":yukihook-bom")
include(":yukihook-core", ":yukihook-compiler", ":yukihook-runtime-stub")
include(":yukihook-extension-kavaref")
include(":yukihook-runtime-xposed82", ":yukihook-runtime-libxposed")
include(":yukihook-core-lint", ":yukihook-extension-kavaref-lint")