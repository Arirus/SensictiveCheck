package com.example.library.code

import com.example.library.model.ClassMethodCode
import org.apache.bcel.classfile.JavaClass

class CodeCheckUtils {
    companion object {
        fun search(javaClass: JavaClass, list: List<ClassMethodCode>) {
            val packageName = javaClass.packageName
            javaClass.methods?.forEach { method ->
                list.forEach { classMethodCode ->
                    classMethodCode.methods.forEach {
                        val compareContent = "${classMethodCode.className}.${it}"
                        takeIf { method.code.toString().contains(compareContent) }?.run {
                            classMethodCode.plus()
                        }
                    }
                }
            }
        }
    }
}