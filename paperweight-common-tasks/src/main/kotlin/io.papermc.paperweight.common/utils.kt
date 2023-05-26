package io.papermc.paperweight.common

import io.papermc.paperweight.util.constants.*
import org.gradle.api.Project

val Project.commonExt: CommonPaperweightCoreExtension
    get() = extensions.getByName(PAPERWEIGHT_EXTENSION) as CommonPaperweightCoreExtension
