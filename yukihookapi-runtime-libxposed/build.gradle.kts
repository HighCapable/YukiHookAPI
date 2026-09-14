plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.maven.publish)
}

group = gropify.project.groupName
version = gropify.project.yukihookapi.bom.version

android {
    namespace = gropify.project.yukihookapi.runtime.libxposed.namespace
    compileSdk = gropify.project.android.compileSdk

    defaultConfig {
        minSdk = gropify.project.android.modern.minSdk
        consumerProguardFiles("consumer-rules.pro")
    }
    testOptions.unitTests.isReturnDefaultValues = true
}

dependencies {
    compileOnly(libs.libxposed.api)
    implementation(libs.libxposed.service)

    implementation(projects.yukihookapiCore)
    implementation(libs.androidx.annotation)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.lifecycle.runtime)

    testImplementation(platform(libs.kavaref.bom))
    testImplementation(libs.kavaref.core)
    testImplementation(libs.kavaref.extension)
}