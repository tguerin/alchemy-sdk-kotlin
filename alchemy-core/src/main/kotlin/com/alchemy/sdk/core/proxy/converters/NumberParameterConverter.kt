package com.alchemy.sdk.core.proxy.converters

import com.alchemy.sdk.core.proxy.ParameterConverter
import com.alchemy.sdk.core.util.HexString.Companion.hexString

object NumberParameterConverter : ParameterConverter<Number, String> {
    override suspend fun convert(data: Number) = data.hexString.withoutLeadingZero()
}