plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.dokka)
    alias(libs.plugins.maven.publish)
}

group = gropify.project.groupName
version = gropify.project.yukihook.bom.version

android {
    namespace = gropify.project.yukihook.runtime.libxposed.namespace
    compileSdk = gropify.project.android.compileSdk

    defaultConfig {
        minSdk = gropify.project.android.modern.minSdk
        consumerProguardFiles("consumer-rules.pro")
    }
    testOptions.unitTests.isReturnDefaultValues = true
}

dokka {
    // Link the libxposed API types used in the public API to the Javadoc of libxposed.
    dokkaSourceSets.configureEach {
        val module = gropify.project.third.party.libxposed.api.moduleName
        externalDocumentationLinks.register(module) {
            url("${gropify.project.third.party.libxposed.javadoc.url}/$module/")
            packageListUrl("${gropify.project.third.party.libxposed.javadoc.url}/$module/element-list")
        }
    }
}

dependencies {
    compileOnly(libs.libxposed.api)
    compileOnly(libs.libxposed.service.api)
    implementation(libs.libxposed.service)

    implementation(projects.yukihookCore)
    implementation(libs.androidx.annotation)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.lifecycle.runtime)

    testImplementation(projects.yukihookExtensionKavaref)
    testImplementation(platform(libs.kavaref.bom))
    testImplementation(libs.kavaref.core)
    testImplementation(libs.kavaref.extension)
}