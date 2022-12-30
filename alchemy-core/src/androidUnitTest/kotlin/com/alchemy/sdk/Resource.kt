package com.alchemy.sdk

import java.io.File

actual object Resource {

    private const val RESOURCE_PATH = "src/commonTest/resources"

    actual fun readText(resourceName: String): String {
        return File("$RESOURCE_PATH/$resourceName").readText()
    }
}