package com.alchemy.sdk

import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfFile

actual object Resource {
    actual fun readText(resourceName: String): String {
        // split based on "." and "/". We want to strip the leading ./ and
        // split the extension
        val pathParts = resourceName.split("[.|/]".toRegex())
        // pathParts looks like
        // [, , test_case_input_one, bin]
        val offset = if (pathParts.size == 2) 0 else 2
        val path = NSBundle.mainBundle.pathForResource("resources/${pathParts[0 + offset]}", pathParts[1 + offset])
        return NSString.stringWithContentsOfFile(path!!, NSUTF8StringEncoding, null) ?: error("Couldn't read file")
    }
}