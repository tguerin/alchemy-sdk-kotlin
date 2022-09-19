package com.alchemy.sdk.core

import com.alchemy.sdk.core.api.CoreApi
import com.alchemy.sdk.core.model.Address

class Core(private val coreApi: CoreApi) : CoreApi by coreApi {

    fun getAccounts(): Result<List<Address>> = Result.success(emptyList())

}