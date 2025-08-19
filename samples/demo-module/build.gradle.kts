plugins {
    autowire(libs.plugins.android.application)
    autowire(libs.plugins.kotlin.android)
    autowire(libs.plugins.kotlin.ksp)
}

android {
    namespace = property.project.samples.demo.module.packageName
    compileSdk = property.project.android.compileSdk

    defaultConfig {
        applicationId = property.project.samples.demo.module.packageName
        minSdk = property.project.android.minSdk
        targetSdk = property.project.android.targetSdk
        versionName = property.project.samples.demo.module.versionName
        versionCode = property.project.samples.demo.module.versionCode
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
    compileOnly(de.robv.android.xposed.api)
    implementation(projects.yukihookapiCore)
    ksp(projects.yukihookapiKspXposed)
    ksp(com.highcapable.hikage.hikage.compiler)
    implementation(com.highcapable.kavaref.kavaref.core)
    implementation(com.highcapable.kavaref.kavaref.extension)
    implementation(com.highcapable.hikage.hikage.core)
    implementation(com.highcapable.hikage.hikage.extension)
    implementation(com.highcapable.hikage.hikage.widget.androidx)
    implementation(com.highcapable.hikage.hikage.widget.material)
    implementation(com.highcapable.betterandroid.ui.component)
    implementation(com.highcapable.betterandroid.ui.extension)
    implementation(com.highcapable.betterandroid.system.extension)
    implementation(androidx.preference.preference.ktx)
    implementation(androidx.core.core.ktx)
    implementation(androidx.appcompat.appcompat)
    implementation(androidx.lifecycle.lifecycle.viewmodel.ktx)
    implementation(com.google.android.material.material)
    implementation(androidx.constraintlayout.constraintlayout)
    testImplementation(junit.junit)
    androidTestImplementation(androidx.test.ext.junit)
    androidTestImplementation(androidx.test.espresso.espresso.core)
}