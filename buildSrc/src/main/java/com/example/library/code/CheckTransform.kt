package com.example.library.code

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import jdk.internal.org.objectweb.asm.ClassReader
import jdk.internal.org.objectweb.asm.ClassReader.EXPAND_FRAMES
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.artifacts.transform.CacheableTransform

class CheckTransform : Transform() {

    override fun getName(): String {
        return "dsdsd"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun isIncremental(): Boolean {
        return false;
    }


    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun transform(transformInvocation: TransformInvocation?) {

        transformInvocation?.inputs?.forEach { it ->
            println("开始打印类")
            it.directoryInputs?.forEach { it ->

                val walk = it.file.walkTopDown()
                walk.filter { it.isFile }.forEach {
                    println(it.name + " "+it.path)

                }

                val dest = transformInvocation.outputProvider.getContentLocation(
                    it.name,
                    it.contentTypes,
                    it.scopes,
                    Format.DIRECTORY
                )
                FileUtils.copyDirectory(it.file, dest)

            }

            println("结束打印类")

            println("开始打印Jar包")

            it.jarInputs?.forEach {
                println(it.name + " " + it.file)

                var jarName = it.name
                val md5Name = DigestUtils.md5Hex(it.file.absolutePath)
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length - 4)
                }


                val dest = transformInvocation.outputProvider.getContentLocation(
                    jarName + md5Name,
                    it.contentTypes,
                    it.scopes,
                    Format.JAR
                )
            }
            println("结束打印Jar包")
        }

    }
}