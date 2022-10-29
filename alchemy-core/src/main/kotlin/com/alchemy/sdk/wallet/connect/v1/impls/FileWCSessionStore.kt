package com.alchemy.sdk.wallet.connect.v1.impls

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.alchemy.sdk.wallet.connect.v1.nullOnThrow
import java.io.File
import java.util.concurrent.ConcurrentHashMap

@Suppress("UNCHECKED_CAST")
class FileWCSessionStore(
    private val storageFile: File,
    gson: Gson
) : WCSessionStore {

    private val adapter = gson.getAdapter(
        TypeToken.getParameterized(
            Map::class.java,
            String::class.java,
            WCSessionStore.State::class.java
        ) as TypeToken<Map<String, WCSessionStore.State>>
    )

    private val currentStates: MutableMap<String, WCSessionStore.State> = ConcurrentHashMap()

    init {
        val storeContent = storageFile.readText()
        nullOnThrow { adapter.fromJson(storeContent) }?.let {
            currentStates.putAll(it)
        }
    }

    override fun load(id: String): WCSessionStore.State? = currentStates[id]

    override fun store(id: String, state: WCSessionStore.State) {
        currentStates[id] = state
        writeToFile()
    }

    override fun remove(id: String) {
        currentStates.remove(id)
        writeToFile()
    }

    override fun list(): List<WCSessionStore.State> = currentStates.values.toList()

    private fun writeToFile() {
        storageFile.writeText(adapter.toJson(currentStates))
    }

}
