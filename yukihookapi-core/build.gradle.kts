import com.vanniktech.maven.publish.AndroidSingleVariantLibrary

plugins {
    autowire(libs.plugins.android.library)
    autowire(libs.plugins.kotlin.android)
    autowire(libs.plugins.maven.publish)
}

group = property.project.groupName
version = property.project.yukihookapi.core.version

android {
    namespace = property.project.groupName
    compileSdk = property.project.android.compileSdk

    defaultConfig {
        minSdk = property.project.android.minSdk
        consumerProguardFiles("consumer-rules.pro")
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
    lint { checkReleaseBuilds = false }
}

dependencies {
    compileOnly(de.robv.android.xposed.api)
    compileOnly(projects.yukihookapiStub)
    implementation(org.lsposed.hiddenapibypass.hiddenapibypass)
    implementation(com.highcapable.kavaref.kavaref.core)
    implementation(com.highcapable.kavaref.kavaref.extension)
    implementation(com.highcapable.betterandroid.ui.extension)
    implementation(com.highcapable.betterandroid.system.extension)
    implementation(androidx.core.core.ktx)
    implementation(androidx.appcompat.appcompat)
    implementation(androidx.preference.preference.ktx)
}

mavenPublishing {
    configure(AndroidSingleVariantLibrary(publishJavadocJar = false))
    coordinates(
        groupId = group.toString(),
        artifactId = property.project.yukihookapi.core.moduleName,
        version = version.toString()
    )
}