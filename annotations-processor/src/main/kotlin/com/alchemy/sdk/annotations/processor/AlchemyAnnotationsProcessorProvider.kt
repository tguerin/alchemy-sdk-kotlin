package com.alchemy.sdk.annotations.processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class AlchemyAnnotationsProcessorProvider : SymbolProcessorProvider {

    private val symbolProcessorProviders = listOf(JsonRpcProcessorProvider(), RestProcessorProvider())
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return CompositeSymbolProcessor(
            *symbolProcessorProviders.map { it.create(environment) }.toTypedArray()
        )
    }

}