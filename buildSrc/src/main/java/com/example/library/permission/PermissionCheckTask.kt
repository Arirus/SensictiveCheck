package com.example.library.permission

import com.android.build.gradle.api.ApplicationVariant
import com.example.library.model.PackagePermission
import com.example.library.model.SensitiveCheckExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*

@CacheableTask
open class PermissionCheckTask() : DefaultTask() {
    fun Project.hasTask(name: String) = this.tasks.findByName(name) != null

    companion object {
        const val TASK_SUFFIX = "ManifestPermissionCheck"

        fun register(
            project: Project,
            variant: ApplicationVariant,
            config: SensitiveCheckExtension
        ) {

            val processManifestTask =
                project.tasks.findByName("process${variant.name.capitalize()}Manifest")

            val provider = project.tasks.register(
                variant.name + TASK_SUFFIX,
                PermissionCheckTask::class.java
            ) {
                it.sources = processManifestTask?.inputs?.files
                it.dangerPermissions = config.checkPermission
                it.packageFilter = config.filterPackages
            }
            project.afterEvaluate {
                processManifestTask?.dependsOn(provider.get())
            }

        }

    }

    init {
        group = "check"
        description = "Check Manifest file `use-permission` item"
    }

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    var sources: FileCollection? = null

    @get:Input
    var dangerPermissions: List<String>? = null

    @get:Input
    var packageFilter: List<String>? = null

    @TaskAction
    fun start() {
        val listManifest = mutableListOf<PackagePermission>()
        sources?.forEach { file ->
            takeIf { PermissionUtil.isManifest(file.absolutePath) }?.run {
                listManifest.add(
                    PermissionUtil.traversalDangerPermission(
                        PermissionParser.parsePermission(file, packageFilter), dangerPermissions
                    )
                )
            }
        }

        println("listManifest size:${listManifest.size}")
        listManifest
            .filter { it.permissions.size > 0 }
            .forEach { it ->
                println("package:" + it.packageName)
                it.permissions.forEach {
                    println("permission:${it.name}  isDanger:${it.isDanger}")
                }
            }
    }
}

