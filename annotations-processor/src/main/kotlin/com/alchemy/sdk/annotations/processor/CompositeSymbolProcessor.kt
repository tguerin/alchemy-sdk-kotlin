package com.alchemy.sdk.annotations.processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated

class CompositeSymbolProcessor(
    vararg symbolProcessors: SymbolProcessor
) : SymbolProcessor {

    private val processors = symbolProcessors.asList()
    override fun process(resolver: Resolver): List<KSAnnotated> {
        return processors.map { processor -> processor.process(resolver) }.flatten()
    }
}