plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.maven.publish)
}

group = gropify.project.groupName
version = gropify.project.yukihookapi.ksp.xposed.version

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    sourceSets.getByName("main") {
        kotlin.srcDir("src/api/kotlin")
    }
}

dependencies {
    implementation(libs.auto.service.annotations)
    compileOnly(libs.symbol.processing.api)
    ksp(libs.auto.service.ksp)
}

mavenPublishing {
    coordinates(
        groupId = group.toString(),
        artifactId = gropify.project.yukihookapi.ksp.xposed.moduleName,
        version = version.toString()
    )
}