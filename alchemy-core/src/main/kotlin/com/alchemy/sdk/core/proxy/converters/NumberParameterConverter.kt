package com.alchemy.sdk.core.proxy.converters

import com.alchemy.sdk.core.proxy.ParameterConverter
import com.alchemy.sdk.core.util.HexString

object NumberParameterConverter : ParameterConverter<Number, String> {
    override suspend fun convert(data: Number) = when(data) {
        is Int -> HexString.from(data.toInt()).withoutLeadingZero()
        else -> throw IllegalArgumentException("Only Int is supported for now")
    }
}