plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.dokka)
    alias(libs.plugins.maven.publish)
}

group = gropify.project.groupName
version = gropify.project.yukihook.bom.version

android {
    namespace = gropify.project.yukihook.runtime.xposed82.namespace
    compileSdk = gropify.project.android.compileSdk

    defaultConfig {
        minSdk = gropify.project.android.minSdk
        consumerProguardFiles("consumer-rules.pro")
    }
    testOptions.unitTests.isReturnDefaultValues = true
}

dokka {
    // Link the Xposed API 82 types used in the public API to the Javadoc of Xposed API.
    dokkaSourceSets.configureEach {
        val module = gropify.project.third.party.xposed.framework.api.moduleName
        externalDocumentationLinks.register(module) {
            url("${gropify.project.third.party.xposed.framework.javadoc.url}/")
            packageListUrl("${gropify.project.third.party.xposed.framework.javadoc.url}/package-list")
        }
    }
}

dependencies {
    compileOnly(libs.xposed.framework.api)

    compileOnly(projects.yukihookRuntimeStub)
    implementation(projects.yukihookCore)

    implementation(platform(libs.kavaref.bom))
    implementation(libs.kavaref.core)
    implementation(libs.kavaref.android)
    implementation(libs.kavaref.extension)

    implementation(libs.androidx.annotation)

    testImplementation(libs.junit)
}