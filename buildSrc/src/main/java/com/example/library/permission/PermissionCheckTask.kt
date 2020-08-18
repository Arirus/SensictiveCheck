package com.example.library.permission

import com.example.library.model.PackagePermission
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*

@CacheableTask
open class PermissionCheckTask() : DefaultTask() {

    init {
        group = "check"
        description = "Check Manifest file desc `use-permission`"
    }

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    var sources: FileCollection? = null

    @get:Input
    var checkPermissions: List<String>? = null

    @get:Input
    var filter: List<String>? = null

    @TaskAction
    fun start() {
        val listManifest = mutableListOf<PackagePermission>()
        sources?.forEach { file ->
            if (PermissionUtil.isManifest(file.absolutePath)) {
//                                                listManifest.add(PermissionUtil.getManifestData(file))
                listManifest.add(PermissionParser.parsePermission(file))
            }
        }

        println("listManifest size:${listManifest.size}")
        listManifest.forEach { it ->
            println("package:" + it.packageName)
            it.permissions.forEach {
                println("permission:" + it.name)
            }
        }
    }
}

