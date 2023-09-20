plugins {
    autowire(libs.plugins.kotlin.jvm)
    autowire(libs.plugins.kotlin.ksp)
    autowire(libs.plugins.maven.publish)
}

group = property.project.groupName

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    sourceSets.getByName("main") {
        kotlin.srcDir("src/api/kotlin")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(com.google.auto.service.auto.service.annotations)
    compileOnly(com.google.devtools.ksp.symbol.processing.api)
    ksp(dev.zacsweers.autoservice.auto.service.ksp)
}

mavenPublishing {
    coordinates(property.project.groupName, property.project.yukihookapi.ksp.xposed.moduleName, property.project.yukihookapi.ksp.xposed.version)
    pom {
        name = property.project.name
        description = property.project.description
        url = property.project.url
        licenses {
            license {
                name = property.project.licence.name
                url = property.project.licence.url
                distribution = property.project.licence.url
            }
        }
        developers {
            developer {
                id = property.project.developer.id
                name = property.project.developer.name
                email = property.project.developer.email
            }
        }
        scm {
            url = property.maven.publish.scm.url
            connection = property.maven.publish.scm.connection
            developerConnection = property.maven.publish.scm.developerConnection
        }
    }
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.S01)
    signAllPublications()
}