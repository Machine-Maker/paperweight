plugins {
    `config-kotlin`
    `config-publish`
}

dependencies {
    shade(projects.paperweightLib)
    shade(projects.paperweightCommonTasks)
}

gradlePlugin {
    plugins.all {
        description = "Gradle plugin for generating code for the Paper API"
        implementationClass = "io.papermc.paperweight.generator.PaperweightGenerator"
    }
}
