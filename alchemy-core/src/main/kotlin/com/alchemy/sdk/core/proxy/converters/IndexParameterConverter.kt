package com.alchemy.sdk.core.proxy.converters

import com.alchemy.sdk.core.model.Index
import com.alchemy.sdk.core.proxy.ParameterConverter
import com.alchemy.sdk.core.util.HexString

object IndexParameterConverter : ParameterConverter<Index, String> {
    override suspend fun convert(data: Index) = HexString.from(data.value).withoutLeadingZero()
}