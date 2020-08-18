package com.example.library

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.example.library.model.SensitiveCheckExtension
import com.example.library.permission.PermissionCheckTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class CheckPlugin : Plugin<Project> {

    companion object {
        const val SensitiveCodeCheck = "SensitiveCodeCheck"
        const val ManifestPermissionCheck = "ManifestPermissionCheck"
    }


    lateinit var mConfig: SensitiveCheckExtension


    override fun apply(project: Project) {

        mConfig = project.extensions.create(
            "sensitivecheckConfig",
            SensitiveCheckExtension::class.java
        )

//        project.afterEvaluate {
        project.plugins.filterIsInstance<AppPlugin>().forEach { _ ->
            project.extensions.findByType(AppExtension::class.java)?.run {
                applicationVariants.all { variant ->

                    var manifestCheckTask =
                        project.tasks.findByName(variant.name + ManifestPermissionCheck)
                    if (manifestCheckTask == null) {
                        val processManifestTask =
                            project.tasks.findByName("process${variant.name.capitalize()}Manifest")

                        manifestCheckTask =
                            project.tasks.create(
                                variant.name + ManifestPermissionCheck,
                                PermissionCheckTask::class.java
                            ) {
                                it.sources = processManifestTask?.inputs?.files
                                it.checkPermissions = mConfig.checkPermission
                                it.filter = mConfig.filterPackages
                            }

                        project.afterEvaluate {
//                            manifestCheckTask?.dependsOn(
//                                processManifestTask?.taskDependencies?.getDependencies(
//                                    processManifestTask
//                                )
//                            )
                            processManifestTask?.dependsOn(manifestCheckTask)
                        }
                    }

//                        var CodeCheckTask =
//                            project.tasks.findByName(variant.name + SensitiveCodeCheck)
//                        if (CodeCheckTask != null) return@all
//
//                        val outputFileName = "sensitive_result.txt"
//
//
//                        val compileJavaTask =
//                            project.tasks.findByName("compile${variant.name.capitalize()}JavaWithJavac")
//
//                        CodeCheckTask =
//                            project.tasks.create(variant.name + SensitiveCodeCheck).doLast {
//                                println("sensitive_check = ${mConfig.checkSwitch}")
//
//                            }
//
//                        CodeCheckTask.dependsOn(
//                            compileJavaTask?.taskDependencies?.getDependencies(
//                                compileJavaTask
//                            )
//                        )
//
//                        compileJavaTask?.dependsOn(CodeCheckTask)

                }
            }
//            }
        }

    }


}