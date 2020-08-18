package com.example.library.code

import com.android.build.gradle.api.ApplicationVariant
import com.example.library.model.SensitiveCheckExtension
import com.example.library.permission.PermissionCheckTask
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*

open class CodeCheckTask : DefaultTask() {

    companion object{
        const val TASK_SUFFIX = "SensitiveCodeCheck"

        fun register(
            project: Project,
            variant: ApplicationVariant,
            config: SensitiveCheckExtension
        ) {
            val compileJavaTask =
                project.tasks.findByName("compile${variant.name.capitalize()}JavaWithJavac")

            val provider = project.tasks.register(
                variant.name + TASK_SUFFIX,
                CodeCheckTask::class.java
            ) {
                it.sources = compileJavaTask?.inputs?.files
                it.packageFilter = config.filterPackages
            }
            project.afterEvaluate {
                compileJavaTask?.dependsOn(provider.get())
            }

        }

    }

    init {
        group = "check"
        description = "Check Code Sensitive Class and Method"
    }

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    var sources: FileCollection? = null

    @get:Input
    var packageFilter: List<String>? = null


    @TaskAction
    fun start() {
        sources?.forEach { file ->
            println(file.absolutePath)
        }


    }

}