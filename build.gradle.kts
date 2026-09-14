import com.android.build.api.dsl.CommonExtension
import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.JavaPlatform
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SourcesJar
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.engine.plugins.DokkaHtmlPluginParameters
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.kotlin.dokka) apply false
    alias(libs.plugins.maven.publish) apply false
}

val androidApplicationPluginId = libs.plugins.android.application.get().pluginId
val androidLibraryPluginId = libs.plugins.android.library.get().pluginId
val dokkaPluginId = libs.plugins.kotlin.dokka.get().pluginId
val mavenPublishPluginId = libs.plugins.maven.publish.get().pluginId

allprojects {
    fun Project.configureAndroidJvm() {
        configure<CommonExtension> {
            compileOptions.sourceCompatibility = JavaVersion.VERSION_17
            compileOptions.targetCompatibility = JavaVersion.VERSION_17
        }
    }

    plugins.withId(androidLibraryPluginId) {
        configureAndroidJvm()
    }

    plugins.withId(androidApplicationPluginId) {
        configureAndroidJvm()
    }

    plugins.withId("java") {
        configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }

    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
            freeCompilerArgs.addAll(
                "-Xno-param-assertions",
                "-Xno-call-assertions",
                "-Xno-receiver-assertions"
            )
        }
    }
}

libraryProjects {
    plugins.withId(mavenPublishPluginId) {
        configure<PublishingExtension> {
            repositories {
                val repositoryDir = gradle.gradleUserHomeDir
                    .resolve("highcapable-maven-repository")
                    .resolve("repository")

                maven {
                    name = "HighCapableMavenReleases"
                    url = repositoryDir.resolve("releases").toURI()
                }
                maven {
                    name = "HighCapableMavenSnapShots"
                    url = repositoryDir.resolve("snapshots").toURI()
                }
            }
        }

        configure<MavenPublishBaseExtension> {
            coordinates(artifactId = project.name)
            when (project.name) {
                Libraries.YUKIHOOKAPI_BOM -> configure(JavaPlatform())
                Libraries.YUKIHOOKAPI_COMPILER -> configure(KotlinJvm(JavadocJar.Javadoc(), SourcesJar.Sources()))
                else -> configure(AndroidSingleVariantLibrary(JavadocJar.None(), SourcesJar.Sources()))
            }
        }
    }

    plugins.withId(dokkaPluginId) {
        configure<DokkaExtension> {
            dokkaPublications.named("html") {
                outputDirectory.set(layout.buildDirectory.dir("dokka/html"))
            }
            pluginsConfiguration.withType<DokkaHtmlPluginParameters>().configureEach {
                footerMessage.set("YukiHookAPI | Apache-2.0 License | Copyright (C) 2019 HighCapable")
            }
        }

        tasks.register("publishKDoc") {
            group = "documentation"
            dependsOn("dokkaGeneratePublicationHtml")

            doLast {
                val docsDir = rootProject.projectDir
                    .resolve("docs-source")
                    .resolve("dist")
                    .resolve("KDoc")
                    .resolve(project.name)

                if (docsDir.exists()) docsDir.deleteRecursively() else docsDir.mkdirs()
                layout.buildDirectory.dir("dokka/html").get().asFile.copyRecursively(docsDir)
            }
        }
    }
}

registerAggregatePublishTask(
    name = "publishBomToMavenLocal",
    description = "Publishes the API BOM and all API modules to the local Maven cache.",
    taskName = "publishToMavenLocal",
    projectNames = Libraries.apiLibs
)

registerAggregatePublishTask(
    name = "publishBomToMavenCentral",
    description = "Publishes the API BOM and all API modules to the Maven Central repository.",
    taskName = "publishMavenPublicationToMavenCentralRepository",
    projectNames = Libraries.apiLibs
)

registerAggregatePublishTask(
    name = "publishBomToHighCapableMavenReleases",
    description = "Publishes the API BOM and all API modules to the HighCapableMavenReleases repository.",
    taskName = "publishAllPublicationsToHighCapableMavenReleasesRepository",
    projectNames = Libraries.apiLibs
)

registerAggregatePublishTask(
    name = "publishBomToHighCapableMavenSnapShots",
    description = "Publishes the API BOM and all API modules to the HighCapableMavenSnapShots repository.",
    taskName = "publishAllPublicationsToHighCapableMavenSnapShotsRepository",
    projectNames = Libraries.apiLibs
)

fun registerAggregatePublishTask(name: String, description: String, taskName: String, projectNames: List<String>) {
    tasks.register(name) {
        this.group = "publishing"
        this.description = description
        dependsOn(projectNames.map { ":$it:$taskName" })
    }
}

fun libraryProjects(action: Action<in Project>) {
    allprojects { if (Libraries.apiLibs.contains(name)) action.execute(this) }
}

object Libraries {
    const val YUKIHOOKAPI_BOM = "yukihookapi-bom"
    const val YUKIHOOKAPI_CORE = "yukihookapi-core"
    const val YUKIHOOKAPI_COMPILER = "yukihookapi-compiler"
    const val YUKIHOOKAPI_RUNTIME_LIBXPOSED = "yukihookapi-runtime-libxposed"
    const val YUKIHOOKAPI_RUNTIME_XPOSED82 = "yukihookapi-runtime-xposed82"

    val apiLibs = listOf(
        YUKIHOOKAPI_BOM,
        YUKIHOOKAPI_CORE,
        YUKIHOOKAPI_COMPILER,
        YUKIHOOKAPI_RUNTIME_LIBXPOSED,
        YUKIHOOKAPI_RUNTIME_XPOSED82
    )
}