package com.example.library.code

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.example.library.model.SensitiveCheckExtension
import org.apache.bcel.classfile.ClassParser
import org.apache.commons.codec.digest.DigestUtils
import java.io.FileInputStream

class CodeCheckTransform(val config: SensitiveCheckExtension) : Transform() {

    override fun getName(): String = "CodeCheck"

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> =
        TransformManager.CONTENT_CLASS

    override fun isIncremental(): Boolean = false

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> =
        TransformManager.SCOPE_FULL_PROJECT

    override fun transform(transformInvocation: TransformInvocation?) {

        transformInvocation?.inputs?.forEach { it ->
            it.directoryInputs?.forEach { it ->


                println(it.file.path)

                val dst = transformInvocation.outputProvider.getContentLocation(
                    it.name, it.contentTypes, it.scopes, Format.DIRECTORY
                )



                if (it.file.isDirectory && config.checkCallMethodMapList != null) {
                    val walk = it.file.walkTopDown()
                    walk.filter { it.isFile }.forEach {
                        val javaClass = ClassParser(FileInputStream(it), it.name).parse()
                        println(javaClass.className)
                        CodeCheckUtils.search(javaClass, config.checkCallMethodMapList!!)

                    }
                }



                FileUtils.copyDirectory(it.file, dst)

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

        config?.checkCallMethodMapList?.forEach {
            println(it.className + " " + it.counter)
        }


    }

}