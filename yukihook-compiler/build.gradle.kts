plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.maven.publish)
}

group = gropify.project.groupName
version = gropify.project.yukihook.bom.version

dependencies {
    compileOnly(libs.symbol.processing.api)
    ksp(libs.auto.service.ksp)
    implementation(libs.auto.service.annotations)

    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)

    testImplementation(libs.junit)
    testImplementation(libs.symbol.processing.api)
}