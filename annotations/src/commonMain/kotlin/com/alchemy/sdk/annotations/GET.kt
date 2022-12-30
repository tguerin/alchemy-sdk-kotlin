package com.alchemy.sdk.annotations

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(
    AnnotationRetention.SOURCE
)
annotation class GET(
    /**
     * A relative or absolute path, or full URL of the endpoint.
     */
    val value: String = ""
)