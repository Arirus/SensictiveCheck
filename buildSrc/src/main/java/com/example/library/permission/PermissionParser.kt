package com.example.library.permission

import com.android.utils.XmlUtils
import com.android.xml.AndroidManifest
import com.example.library.model.PackagePermission
import com.example.library.model.PermissionModel
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import java.io.File
import javax.xml.parsers.SAXParserFactory

object PermissionParser {

    private val sParserFactory: SAXParserFactory = SAXParserFactory.newInstance()


    fun parsePermission(file: File): PackagePermission {
        val parser = XmlUtils.createSaxParser(sParserFactory)
        val permission = PackagePermission()
        parser.parse(file, MyHandler(permission))
        return permission
    }


    class MyHandler(private val permissions: PackagePermission) : DefaultHandler() {

        @Throws(SAXException::class)
        override fun startElement(
            uri: String?, localName: String?, qName: String?,
            attributes: Attributes?
        ) {

            when (qName) {
                AndroidManifest.NODE_MANIFEST ->
                    takeIf {
                        attributes?.getLocalName(1) == AndroidManifest.ATTRIBUTE_PACKAGE
                    }?.run {
                        permissions.packageName = attributes?.getValue(1)
                    }

                AndroidManifest.NODE_USES_PERMISSION ->
                    takeIf {
                        attributes?.getLocalName(0) == "android:name"
                    }?.run {
                        permissions.permissions.add(PermissionModel(attributes?.getValue(0)))
                    }
            }

//            if (qName != AndroidManifest.NODE_USES_PERMISSION || attributes?.getLocalName(0) != "android:name") return
//            permissions.add(PermissionModel(attributes.getValue(0)))
        }
    }
}