package com.alchemy.sdk.wallet.connect.v1

//Map functions that throw exceptions into optional types
fun <T> nullOnThrow(func: () -> T): T? = try {
    func.invoke()
} catch (e: Exception) {
    null
}
