plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.dokka)
    alias(libs.plugins.maven.publish)
}

group = gropify.project.groupName
version = gropify.project.yukihook.bom.version

android {
    namespace = gropify.project.yukihook.extension.kavaref.namespace
    compileSdk = gropify.project.android.compileSdk

    defaultConfig {
        minSdk = gropify.project.android.minSdk
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    lintPublish(projects.yukihookExtensionKavarefLint)

    implementation(projects.yukihookCore)
    implementation(libs.hiddenapibypass)

    implementation(platform(libs.kavaref.bom))
    implementation(libs.kavaref.core)
    implementation(libs.kavaref.android)
    implementation(libs.kavaref.extension)

    implementation(platform(libs.betterandroid.android.bom))
    implementation(libs.betterandroid.system.extension)
}