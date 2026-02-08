package com.rizero.shared_core_data.model

import kotlinx.serialization.Serializable

@Serializable
data class Token(
    val value : String,
    val expireAt : Long,
)
