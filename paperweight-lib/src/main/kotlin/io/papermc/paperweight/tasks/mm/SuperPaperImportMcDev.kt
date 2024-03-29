package io.papermc.paperweight.tasks.mm

import io.papermc.paperweight.PaperweightException
import io.papermc.paperweight.tasks.*
import io.papermc.paperweight.util.*
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.*
import kotlin.streams.asSequence
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

abstract class SuperPaperImportMcDev : BaseTask() {

    @get:InputDirectory
    abstract val spigotMcDevSrc: DirectoryProperty

    @get:Input
    abstract val backupFiles: ListProperty<String>

    @get:InputDirectory
    abstract val paperMcDevSrc: DirectoryProperty

    @get:InputDirectory
    abstract val mcLibrariesDir: DirectoryProperty

    @get:InputDirectory
    abstract val spigotLibrariesDir: DirectoryProperty

    @get:InputDirectory
    abstract val perFilePatchDir: DirectoryProperty

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val spigotRecompiledClasses: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun run() {
        val basePatchDirFile = outputDir.path.resolve("src/main/java")
        Files.walk(paperMcDevSrc.path).use {
            it.asSequence().filter { file -> file.isRegularFile() && file.name == "package-info.java" }
                .forEach { file ->
                    val javaName = javaFileName(paperMcDevSrc.path, file)
                    val out = basePatchDirFile.resolve(javaName)
                    out.parent.createDirectories()
                    file.copyTo(out, true)
                }
        }
        val perFilePatches = perFilePatchDir.path
        val patchList = Files.walk(perFilePatches).use { it.asSequence().filter { file -> file.isRegularFile() }.toSet() }
        val extras = mutableSetOf<String>();
        if (patchList.isNotEmpty()) {
            val spigotRecompiles = Files.readString(spigotRecompiledClasses.path).split("\n")
            // Copy in patch targets
            for (file in patchList) {
                val javaName = javaFileName(perFilePatches, file)
                val out = basePatchDirFile.resolve(javaName)
                val useSpigot = spigotRecompiles.contains(javaName.removeSuffix(".java"))
                val sourcePath = if (useSpigot || backupFiles.get().contains(javaName)) {
                    spigotMcDevSrc.path.resolve(javaName)
                } else {
                    paperMcDevSrc.path.resolve(javaName)
                }
                if (sourcePath.notExists()) {
                    extras.add(javaName)
                } else {
                    out.parent.createDirectories()
                    sourcePath.copyTo(out, true)
                }
            }
        }
        val libFiles = listOf(spigotLibrariesDir.path, mcLibrariesDir.path).flatMap { it.listDirectoryEntries("*-sources.jar") }
        val imports = findNeededLibraryImports(extras, libFiles)
        for ((libraryFileName, importFilePath) in imports) {
            val libFile = libFiles.firstOrNull { it.name == libraryFileName }
                ?: throw PaperweightException("Failed to find library: $libraryFileName for class $importFilePath")

            listOf(basePatchDirFile).mapNotNull { it.resolve(importFilePath) }
                .filter { it.notExists() }
                .forEach { outputFile ->
                    outputFile.parent.createDirectories()

                    libFile.openZip().use { zipFile ->
                        val libEntry = zipFile.getPath(importFilePath)
                        libEntry.copyTo(outputFile)
                    }
                }
        }
    }

    private fun findNeededLibraryImports(patchLines: Set<String>, libFiles: List<Path>): Set<LibraryImport> {
        val knownImportMap = findPossibleLibraryImports(libFiles)
            .associateBy { it.importFilePath }
        val prefix = "+++ b/src/main/java/"
        return patchLines.map { it.substringAfter(prefix) }
            .mapNotNull { knownImportMap[it] }
            .toSet()
    }

    private fun findPossibleLibraryImports(libFiles: List<Path>): Collection<LibraryImport> {
        val found = hashSetOf<LibraryImport>()
        val suffix = ".java"
        libFiles.map { libFile ->
            libFile.openZip().use { zipFile ->
                zipFile.walk()
                    .filter { it.isRegularFile() && it.name.endsWith(suffix) }
                    .map { sourceFile ->
                        LibraryImport(libFile.name, sourceFile.toString().substring(1))
                    }
                    .forEach(found::add)
            }
        }
        return found
    }
    private data class LibraryImport(val libraryFileName: String, val importFilePath: String)

}
