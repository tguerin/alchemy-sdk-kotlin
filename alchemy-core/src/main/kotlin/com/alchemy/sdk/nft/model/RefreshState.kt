package com.alchemy.sdk.nft.model

import com.alchemy.sdk.nft.adapter.KRefreshStateSerializer
import kotlinx.serialization.Serializable

@Serializable(with = KRefreshStateSerializer::class)
enum class RefreshState(val value: String) {
    AlreadyQueued("already_queued"),
    DoesNotExist("does_not_exist"),
    Finished("finished"),
    InProgress("in_progress"),
    Queued("queued"),
    QueueFailed("queue_failed"),
    Unknown("unknown")
}