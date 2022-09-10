package com.alchemy.sdk.core.proxy.converters

import com.alchemy.sdk.core.model.StoragePosition
import com.alchemy.sdk.core.proxy.ParameterConverter
import com.alchemy.sdk.core.util.HexString

object StoragePositionParameterConverter : ParameterConverter<StoragePosition, String> {
    override suspend fun convert(data: StoragePosition) = HexString.from(data.position).toString()
}