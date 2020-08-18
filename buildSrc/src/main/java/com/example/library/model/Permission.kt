package com.example.library.model

data class PackagePermission(
    var packageName: String? = null,
    var permissions: MutableList<PermissionModel> = mutableListOf()
)

data class PermissionModel(
    var name: String? = null,
    var isDanger:Boolean? = false
)