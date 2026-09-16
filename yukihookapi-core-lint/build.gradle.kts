plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = gropify.project.groupName

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "Lint-Registry-V2" to gropify.project.yukihookapi.core.lint.registry.v2.clazz
        )
    }
}

dependencies {
    compileOnly(libs.kotlin.stdlib)
    compileOnly(libs.android.lint.api)
    compileOnly(libs.android.lint.checks)
}