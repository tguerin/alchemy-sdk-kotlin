package com.alchemy.sdk.util

open class QueryMapObject : Map<String, Any> {
    protected var queryData = ProxyQueryMap()

    override val entries: Set<Map.Entry<String, Any>>
        get() = queryData.entries
    override val keys: Set<String>
        get() = queryData.keys
    override val size: Int
        get() = queryData.size
    override val values: Collection<Any>
        get() = queryData.values

    override fun containsKey(key: String) = queryData.containsKey(key)

    override fun containsValue(value: Any) = queryData.containsValue(value)

    override fun get(key: String) = queryData[key]

    override fun isEmpty() = queryData.isEmpty()
}