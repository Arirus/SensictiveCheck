package com.example.library.model

data class ClassMethodCode(val className: String, val methods: List<String>) {
    var counter = 0;

    fun plus() = counter++
}