package com.alchemy.sdk.core.proxy.converters

import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.core.proxy.ParameterConverter

object AddressParamConverter : ParameterConverter<Address, String> {
    override suspend fun convert(data: Address) = data.value.data
}