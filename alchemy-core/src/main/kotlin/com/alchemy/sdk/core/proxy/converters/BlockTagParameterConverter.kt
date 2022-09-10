package com.alchemy.sdk.core.proxy.converters

import com.alchemy.sdk.core.model.BlockTag
import com.alchemy.sdk.core.proxy.ParameterConverter

object BlockTagParameterConverter : ParameterConverter<BlockTag, String> {
    override suspend fun convert(data: BlockTag) = data.value
}