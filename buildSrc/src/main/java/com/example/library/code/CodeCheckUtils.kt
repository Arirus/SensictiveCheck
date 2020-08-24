package com.example.library.code

import org.apache.bcel.classfile.JavaClass

class CodeCheckUtils {
    companion object {
        fun search(javaClass: JavaClass, list: List<Map<String, Any>>) {
            val packageName = javaClass.packageName
            javaClass.methods?.forEach { method ->
                list.forEach { classMethodCode ->
//                    classMethodCode.methods.forEach {
                    val compareContent = classMethodCode["className"] as String
                    takeIf { method.code.toString().contains(compareContent) }?.run {
//                        classMethodCode.plus()
                        println("$compareContent exist")

                    }
//                    }
                }
            }
        }
    }
}