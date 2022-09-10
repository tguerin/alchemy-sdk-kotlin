package com.alchemy.sdk.core.proxy.converters

import com.alchemy.sdk.core.proxy.ParameterConverter
import com.alchemy.sdk.core.util.HexString

object HexStringParameterConverter : ParameterConverter<HexString, String> {
    override suspend fun convert(data: HexString) = data.toString()
}