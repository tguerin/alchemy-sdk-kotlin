package com.alchemy.sdk.util


class ProxyRetrofitQueryMap(m: Map<String, Any> = emptyMap()) : HashMap<String, Any>(m) {

    override val entries: MutableSet<MutableMap.MutableEntry<String, Any>>
        get() {
            val originSet: Set<Map.Entry<String, Any>> = super.entries
            val newSet: MutableSet<MutableMap.MutableEntry<String, Any>> = LinkedHashSet()
            for (entry in originSet) {
                val entryKey = entry.key
                when (val entryValue = entry.value) {
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