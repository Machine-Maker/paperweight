/*
 * paperweight is a Gradle plugin for the PaperMC project.
 *
 * Copyright (c) 2023 Kyle Wood (DenWav)
 *                    Contributors
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 only, no later versions.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 */

package io.papermc.paperweight.common.taskcontainers

import io.papermc.paperweight.common.*
import io.papermc.paperweight.tasks.*
import io.papermc.paperweight.util.*
import org.gradle.api.Project
import org.gradle.api.tasks.TaskContainer
import org.gradle.kotlin.dsl.*

@Suppress("MemberVisibilityCanBePrivate")
open class GeneralTasks(
    project: Project,
    tasks: TaskContainer = project.tasks,
    extension: CommonPaperweightCoreExtension = project.commonExt,
) : InitialTasks(project) {

    val filterVanillaJar by tasks.registering<FilterJar> {
        inputJar.set(extractFromBundler.flatMap { it.serverJar })
        includes.set(extension.vanillaJarIncludes)
    }
}
