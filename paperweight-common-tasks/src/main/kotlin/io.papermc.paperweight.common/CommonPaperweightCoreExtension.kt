package io.papermc.paperweight.common

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.*

abstract class CommonPaperweightCoreExtension(objects: ObjectFactory) {

    val minecraftVersion: Property<String> = objects.property()

    val vanillaJarIncludes: ListProperty<String> = objects.listProperty<String>().convention(
        listOf("/*.class", "/net/minecraft/**", "/com/mojang/math/**")
    )
}
