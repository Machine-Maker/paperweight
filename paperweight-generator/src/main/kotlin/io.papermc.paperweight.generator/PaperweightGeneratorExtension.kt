package io.papermc.paperweight.generator

import io.papermc.paperweight.common.CommonPaperweightCoreExtension
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.*

open class PaperweightGeneratorExtension(objects: ObjectFactory) : CommonPaperweightCoreExtension(objects) {

    val paramMappingsRepo: Property<String> = objects.property()
    val remapRepo: Property<String> = objects.property()
}
