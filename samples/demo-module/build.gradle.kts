plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = gropify.project.samples.demo.module.packageName
    compileSdk = gropify.project.android.compileSdk

    defaultConfig {
        applicationId = gropify.project.samples.demo.module.packageName
        minSdk = gropify.project.android.minSdk
        targetSdk = gropify.project.android.targetSdk
        versionName = gropify.project.samples.demo.module.versionName
        versionCode = gropify.project.samples.demo.module.versionCode
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
    lint { checkReleaseBuilds = false }
    androidResources.additionalParameters += listOf("--allow-reserved-package-id", "--package-id", "0x64")
}

dependencies {
    compileOnly(libs.rovo89.xposed.api)
    implementation(projects.yukihookapiCore)
    ksp(projects.yukihookapiKspXposed)
    ksp(libs.hikage.compiler)
    implementation(libs.kavaref.core)
    implementation(libs.kavaref.extension)
    implementation(libs.hikage.core)
    implementation(libs.hikage.extension)
    implementation(libs.hikage.widget.androidx)
    implementation(libs.hikage.widget.material)
    implementation(libs.betterandroid.ui.component)
    implementation(libs.betterandroid.ui.extension)
    implementation(libs.betterandroid.system.extension)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}