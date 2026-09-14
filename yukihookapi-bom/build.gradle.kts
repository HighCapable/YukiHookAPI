plugins {
    `java-platform`
    alias(libs.plugins.maven.publish)
}

group = gropify.project.groupName
version = gropify.project.yukihookapi.bom.version

dependencies {
    constraints {
        api(projects.yukihookapiCore)
        api(projects.yukihookapiCompiler)
        api(projects.yukihookapiRuntimeLibxposed)
        api(projects.yukihookapiRuntimeXposed82)
    }
}