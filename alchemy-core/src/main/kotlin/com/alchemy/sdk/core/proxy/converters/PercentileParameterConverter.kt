package com.alchemy.sdk.core.proxy.converters

import com.alchemy.sdk.core.model.Percentile
import com.alchemy.sdk.core.proxy.ParameterConverter

object PercentileParameterConverter : ParameterConverter<Percentile, Float> {
    override suspend fun convert(data: Percentile): Float = data.value.toFloat()
}