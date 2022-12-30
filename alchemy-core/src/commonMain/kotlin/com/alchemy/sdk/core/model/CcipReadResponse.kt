package com.alchemy.sdk.core.model

import com.alchemy.sdk.util.HexString
import kotlinx.serialization.Serializable

@Serializable
class CcipReadResponse(val data: HexString? = null)