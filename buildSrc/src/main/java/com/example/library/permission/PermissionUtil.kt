package com.example.library.permission

import com.android.ide.common.xml.AndroidManifestParser
import com.android.ide.common.xml.ManifestData
import com.example.library.model.PackagePermission
import java.io.File

class PermissionUtil {
    companion object {

        const val FILE_SUFFIX = "AndroidManifest.xml"

        fun isManifest(path: String) = path.endsWith(FILE_SUFFIX)

        fun getManifestData(file: File): ManifestData = AndroidManifestParser.parse(file.toPath())

        fun traversalDangerPermission(packagePermission : PackagePermission, dangerList:List<String>?):PackagePermission{
            dangerList?.let {
                packagePermission.permissions.forEach {
                    it.isDanger =  dangerList.indexOf(it.name)!=-1
                }
            }
            return packagePermission
        }

    }

}