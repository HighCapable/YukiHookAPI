plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.hikage)
}

android {
    namespace = gropify.project.samples.demo.module.modern.packageName
    compileSdk = gropify.project.android.compileSdk

    defaultConfig {
        applicationId = gropify.project.samples.demo.module.modern.packageName
        minSdk = gropify.project.android.modern.minSdk
        targetSdk = gropify.project.android.targetSdk
        versionName = gropify.project.samples.demo.module.modern.versionName
        versionCode = gropify.project.samples.demo.module.modern.versionCode
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = providers.gradleProperty("yukiMinify").map { it.toBoolean() }.orElse(false).get()
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
    lint { checkReleaseBuilds = false }
    androidResources.additionalParameters += listOf("--allow-reserved-package-id", "--package-id", "0x64")
}

dependencies {
    compileOnly(libs.libxposed.api)
    implementation(projects.yukihookRuntimeLibxposed)
    implementation(projects.yukihookCore)
    implementation(projects.yukihookExtensionKavaref)
    ksp(projects.yukihookCompiler)

    implementation(platform(libs.kavaref.bom))
    implementation(libs.kavaref.core)
    implementation(libs.kavaref.android)
    implementation(libs.kavaref.extension)

    implementation(platform(libs.hikage.bom))
    implementation(libs.hikage.core)
    implementation(libs.hikage.runtime)
    implementation(libs.hikage.runtime.attribute)
    implementation(libs.hikage.extension)
    implementation(libs.hikage.widget.androidx)
    implementation(libs.hikage.widget.material)

    implementation(platform(libs.betterandroid.android.bom))
    implementation(libs.betterandroid.ui.component)
    implementation(libs.betterandroid.ui.extension)
    implementation(libs.betterandroid.system.extension)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.material)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}