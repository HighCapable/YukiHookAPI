import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SourcesJar

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.maven.publish)
}

group = gropify.project.groupName
version = gropify.project.yukihookapi.core.version

android {
    namespace = gropify.project.groupName
    compileSdk = gropify.project.android.compileSdk

    defaultConfig {
        minSdk = gropify.project.android.minSdk
        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    lint { checkReleaseBuilds = false }
}

dependencies {
    compileOnly(libs.rovo89.xposed.api)
    compileOnly(projects.yukihookapiStub)
    implementation(libs.hiddenapibypass)
    implementation(libs.kavaref.core)
    implementation(libs.kavaref.extension)
    implementation(libs.betterandroid.ui.extension)
    implementation(libs.betterandroid.system.extension)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.preference.ktx)
}

mavenPublishing {
    configure(AndroidSingleVariantLibrary(JavadocJar.None(), SourcesJar.Sources()))
    coordinates(
        groupId = group.toString(),
        artifactId = gropify.project.yukihookapi.core.moduleName,
        version = version.toString()
    )
}