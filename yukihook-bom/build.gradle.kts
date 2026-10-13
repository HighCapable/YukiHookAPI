plugins {
    `java-platform`
    alias(libs.plugins.maven.publish)
}

group = gropify.project.groupName
version = gropify.project.yukihook.bom.version

dependencies {
    constraints {
        api(projects.yukihookCore)
        api(projects.yukihookCompiler)
        api(projects.yukihookExtensionKavaref)
        api(projects.yukihookRuntimeLibxposed)
        api(projects.yukihookRuntimeXposed82)
    }
}