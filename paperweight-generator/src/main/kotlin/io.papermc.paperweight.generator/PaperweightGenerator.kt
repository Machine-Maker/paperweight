package io.papermc.paperweight.generator

import io.papermc.paperweight.DownloadService
import io.papermc.paperweight.common.taskcontainers.VanillaTasks
import io.papermc.paperweight.util.*
import io.papermc.paperweight.util.constants.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.*

class PaperweightGenerator : Plugin<Project> {
    override fun apply(target: Project) {
        Git.checkForGit()

        val ext = target.extensions.create(PAPERWEIGHT_EXTENSION, PaperweightGeneratorExtension::class)

        target.gradle.sharedServices.registerIfAbsent("download", DownloadService::class) {}

        val tasks = VanillaTasks(target)

        target.configurations.create(PARAM_MAPPINGS_CONFIG)
        target.configurations.create(REMAPPER_CONFIG)

        target.afterEvaluate {
            target.repositories {
                maven(ext.paramMappingsRepo) {
                    name = PARAM_MAPPINGS_REPO_NAME
                    content { onlyForConfigurations(PARAM_MAPPINGS_CONFIG) }
                }
                maven(ext.remapRepo) {
                    name = REMAPPER_REPO_NAME
                    content { onlyForConfigurations(REMAPPER_CONFIG) }
                }
            }

            plugins.apply("java")

            configurations.named(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME) {
                withDependencies {
                    dependencies {
                        add(create(files(tasks.fixJar.flatMap { it.outputJar })))
                        add(create(files(tasks.extractFromBundler.map { it.serverLibraryJars.asFileTree })))
                    }
                }
            }
        }
    }
}
