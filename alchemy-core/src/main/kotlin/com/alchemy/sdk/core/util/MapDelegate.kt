package com.alchemy.sdk.core.util

open class QueryMapObject : Map<String, String> {
    protected var queryData = hashMapOf<String, String>()

    override val entries: Set<Map.Entry<String, String>>
        get() = queryData.entries
    override val keys: Set<String>
        get() = queryData.keys
    override val size: Int
        get() = queryData.size
    override val values: Collection<String>
        get() = queryData.values

    override fun containsKey(key: String) = queryData.containsKey(key)

    override fun containsValue(value: String) = queryData.containsValue(value)

    override fun get(key: String) = queryData[key]

    override fun isEmpty() = queryData.isEmpty()
}