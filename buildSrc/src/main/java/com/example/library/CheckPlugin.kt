package com.example.library

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.example.library.model.SensitiveCheckExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class CheckPlugin : Plugin<Project> {


    lateinit var mConfig: SensitiveCheckExtension

    private val SensitiveCheck = "SensitiveCheck"


    override fun apply(project: Project) {

        mConfig = project.extensions.create(
            "sensitivecheckConfig",
            SensitiveCheckExtension::class.java
        )

//        project.afterEvaluate {
        project.plugins.filterIsInstance<AppPlugin>().forEach { _ ->
            project.extensions.findByType(AppExtension::class.java)?.run {
                applicationVariants.all { variant ->


                    var genTask = project.tasks.findByName(variant.name + SensitiveCheck)
                    if (genTask != null) return@all

                    val outputFileName = "sensitive_result.txt"
                    val compileJavaTask =
                        project.tasks.findByName("compile${variant.name.capitalize()}JavaWithJavac")

                    genTask = project.tasks.create(variant.name + SensitiveCheck) {
                        it.doLast {
                            println("sensitive_check = ${mConfig.checkSwitch}")
                        }
                    }

                    val compileJavaTaskDependsOn =
                        compileJavaTask?.taskDependencies?.getDependencies(compileJavaTask)

//                    compileJavaTaskDependsOn?.all { task -> print(task.name) }

                    compileJavaTaskDependsOn?.let { tasls ->
                        tasls.forEach { print(it.name) }
                    }
                    genTask.dependsOn(compileJavaTaskDependsOn)

                    compileJavaTask?.dependsOn(genTask)
//                    project.tasks.register(variant.name + SensitiveCheck)

                }
            }
        }
//        }

    }


}