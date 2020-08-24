package com.example.library.code

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.example.library.model.SensitiveCheckExtension
import org.apache.bcel.classfile.ClassParser
import org.apache.commons.codec.digest.DigestUtils
import java.io.File
import java.io.FileInputStream

class CodeCheckTransform(val config: SensitiveCheckExtension) : Transform() {

    init {
        println("CodeCheckTransform Constractor")
    }

    override fun getName(): String = "CodeCheck"

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> =
        kotlin.run {
//            println("CodeCheckTransform getInputTypes")
            TransformManager.CONTENT_CLASS
        }

    override fun isIncremental(): Boolean = kotlin.run {
        println("CodeCheckTransform isIncremental")
        true
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> = kotlin.run {
//        println("CodeCheckTransform getScopes")
        TransformManager.SCOPE_FULL_PROJECT
    }

    private val printFile = { file: File ->
        val javaClass = ClassParser(FileInputStream(file), file.name).parse()
        println(javaClass.className)
    }


    private val isClassFile = { file: File ->
        file.exists() && file.isFile && file.name.endsWith(".class")
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        transformInvocation?.inputs?.forEach { transInput ->

            // jar 包处理
            transInput.jarInputs?.forEach {
                var jarName = it.name
                val md5Name = DigestUtils.md5Hex(it.file.getAbsolutePath())

                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length - 4)
                }

                val src = it.file
                val dest = transformInvocation.outputProvider.getContentLocation(
                    jarName + md5Name,
                    it.contentTypes, it.scopes, Format.JAR
                )

                FileUtils.copyFile(src, dest)

            }


            //源码处理
            if (!transformInvocation.isIncremental)
                transInput.directoryInputs?.forEach { dirInput ->

                    val sorDirPath = dirInput.file.absolutePath

                    val dst = transformInvocation.outputProvider.getContentLocation(
                        dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY
                    )

                    if (dirInput.file.isDirectory) {
                        val walk = dirInput.file.walkTopDown()
                        walk.filter(isClassFile).forEach(printFile)
                    } else if (isClassFile(dirInput.file)) {
                        printFile(dirInput.file)
                    }

                    FileUtils.copyDirectory(dirInput.file, dst)
                }
            else
                transInput.directoryInputs?.forEach { dirInput ->
                    val dst = transformInvocation.outputProvider.getContentLocation(
                        dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY
                    )

                    val dstPath = dst.absolutePath

//                    println("dstPath $dstPath")

                    val srcPath = dirInput.file.absolutePath

//                    println("srcPath $srcPath")


                    dirInput.changedFiles.filter { it.value != Status.NOTCHANGED }
                        .forEach { (file, staus) ->

                            val fileName = file.absolutePath.substring(srcPath.length)

//                            println("fileName $fileName  status $staus")

                            when (staus) {
                                Status.ADDED,
                                Status.CHANGED -> {
                                    if (isClassFile(file)) {
                                        printFile(file)
                                        file.copyTo(File(dstPath + fileName),true)
                                    }
                                }

                                Status.REMOVED -> {
//                                    println("deleteIfExists ${dstPath + fileName}")
                                    FileUtils.deleteRecursivelyIfExists(File(dstPath + fileName))
                                }
                            }
                        }

                }

        }

    }


    fun transform1(transformInvocation: TransformInvocation?) {


        transformInvocation?.inputs?.forEach { it ->

            if (!transformInvocation.isIncremental) {
                it.directoryInputs?.forEach { it ->

                    println(it.file.path)

                    val dst = transformInvocation.outputProvider.getContentLocation(
                        it.name, it.contentTypes, it.scopes, Format.DIRECTORY
                    )

                    if (it.file.isDirectory) {
                        val walk = it.file.walkTopDown()
                        walk.filter { it.isFile && it.name.endsWith(".class") }.forEach {
                            val javaClass = ClassParser(FileInputStream(it), it.name).parse()
                            println(javaClass.className)
//                            CodeCheckUtils.search(javaClass, config.checkCallMethodMapList!!)
                        }
                    } else if (it.file.isFile && it.name.endsWith(".class")) {
                        val javaClass = ClassParser(FileInputStream(it.file), it.name).parse()
                        println(javaClass.className)
//                        CodeCheckUtils.search(javaClass, config.checkCallMethodMapList!!)
                    }


                    FileUtils.copyDirectory(it.file, dst)

                }
            }

//            if (!transformInvocation.isIncremental) {

//            }


            if (transformInvocation.isIncremental) {

                it.directoryInputs?.forEach { dirInput ->
                    val dst = transformInvocation.outputProvider.getContentLocation(
                        dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY
                    )

                    val sourceParentPatch = dirInput.file.absolutePath

                    dirInput.changedFiles.filter { it.value != Status.NOTCHANGED }
                        .forEach { (file, status) ->

                            when (status) {
                                Status.CHANGED, Status.ADDED -> {
                                    if (file.isDirectory) {
                                        val walk = file.walkTopDown()
                                        walk.filter { it.isFile && it.name.endsWith(".class") }
                                            .forEach {
                                                val javaClass = ClassParser(
                                                    FileInputStream(it),
                                                    it.name
                                                ).parse()
                                                println(javaClass.className)
                                            }

                                        val dstPatch = dst.absolutePath + file.path.substringAfter(
                                            sourceParentPatch
                                        )
                                        val lastIndex = dstPatch.indexOfLast { c -> c == '/' }

                                        println(
                                            "dstPath111 ${File(
                                                dstPatch.substring(
                                                    0,
                                                    lastIndex
                                                )
                                            )}"
                                        )

                                        if (File(dstPatch.substring(0, lastIndex)).exists()) {
                                            File(dstPatch.substring(0, lastIndex)).createNewFile()
                                        }

                                        FileUtils.copyDirectory(
                                            file, File(dstPatch.substring(0, lastIndex))
                                        )

                                    } else if (file.isFile) {
                                        val javaClass = ClassParser(
                                            FileInputStream(file),
                                            file.name
                                        ).parse()
                                        println(javaClass.className)
                                    }


                                    val dstPatch = dst.absolutePath + file.path.substringAfter(
                                        sourceParentPatch
                                    )
                                    val lastIndex = dstPatch.indexOfLast { c -> c == '/' }

                                    println("dstPath222 ${File(dstPatch.substring(0, lastIndex))}")


                                    if (File(dstPatch.substring(0, lastIndex)).exists()) {
                                        File(dstPatch.substring(0, lastIndex)).createNewFile()
                                    }

                                    FileUtils.copyFileToDirectory(
                                        file, File(dstPatch.substring(0, lastIndex))
                                    )

//                                    FileUtils.copyFileToDirectory(
//                                        file,
//                                        File(
//                                            File(
//                                                dst.absolutePath + file.path.substringAfter(
//                                                    sourceParentPatch
//                                                )
//                                            ).parent
//                                        )
//                                    )

                                }
                                Status.REMOVED -> {
//                                    println()

//                                    val dstPatch = dst.absolutePath + file.path.substringAfter(
//                                        sourceParentPatch
//                                    )
//                                    val lastIndex = dstPatch.indexOfLast { c -> c == '/' }
//
//                                    println("dstPath ${File(dstPatch.substring(0, lastIndex))}")

                                    println("$file + $status")

                                    FileUtils.deleteIfExists(
                                        File(
                                            dst.absolutePath + file.path.substringAfter(
                                                sourceParentPatch
                                            )
                                        )
                                    )
                                }
                            }
                        }

                }


            }




            it.jarInputs?.forEach {
                var jarName = it.name
                val md5Name = DigestUtils.md5Hex(it.file.getAbsolutePath())

//                println(it.file.path)

                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length - 4)
                }

                val src = it.file
                val dest = transformInvocation.outputProvider.getContentLocation(
                    jarName + md5Name,
                    it.contentTypes, it.scopes, Format.JAR
                )

                FileUtils.copyFile(src, dest)

            }

        }


    }


}