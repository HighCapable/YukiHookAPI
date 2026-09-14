plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.maven.publish)
}

group = gropify.project.groupName
version = gropify.project.yukihookapi.bom.version

android {
    namespace = gropify.project.yukihookapi.runtime.xposed82.namespace
    compileSdk = gropify.project.android.compileSdk

    defaultConfig {
        minSdk = gropify.project.android.minSdk
        consumerProguardFiles("consumer-rules.pro")
    }
    testOptions.unitTests.isReturnDefaultValues = true
}

dependencies {
    compileOnly(libs.rovo89.xposed.api)

    compileOnly(projects.yukihookapiRuntimeStub)
    implementation(projects.yukihookapiCore)

    implementation(platform(libs.kavaref.bom))
    implementation(libs.kavaref.core)
    implementation(libs.kavaref.android)
    implementation(libs.kavaref.extension)

    implementation(libs.androidx.annotation)

    testImplementation(libs.junit)
}