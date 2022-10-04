package com.alchemy.sdk.core.util


class ProxyRetrofitQueryMap(m: Map<String, Any> = emptyMap()) : HashMap<String, Any>(m) {

    override val entries: MutableSet<MutableMap.MutableEntry<String, Any>>
        get() {
            val originSet: Set<Map.Entry<String?, Any?>> = super.entries
            val newSet: MutableSet<MutableMap.MutableEntry<String, Any>> = LinkedHashSet()

            for (entry in originSet) {
                val entryKey =
                    entry.key ?: throw IllegalArgumentException("Query map contained null key")
                when (val entryValue = entry.value) {
                    null -> {
                        throw IllegalArgumentException(
                            "Query map contained null value for key '$entryKey'."
                        )
                    }
                    is List<*> -> {
                        for (arrayValue in entryValue) {
                            if (arrayValue != null) { // Skip null values
                                val newEntry = object : MutableMap.MutableEntry<String, Any> {
                                    override val key: String
                                        get() = entryKey
                                    override val value: Any
                                        get() = arrayValue

                                    override fun setValue(newValue: Any): Any {
                                        return value
                                    }
                                }
                                newSet.add(newEntry)
                            }
                        }
                    }
                    else -> {
                        val newEntry = object : MutableMap.MutableEntry<String, Any> {
                            override val key: String
                                get() = entryKey
                            override val value: Any
                                get() = entryValue

                            override fun setValue(newValue: Any): Any {
                                return value
                            }
                        }
                        newSet.add(newEntry)
                    }
                }
            }
            return newSet
        }
}