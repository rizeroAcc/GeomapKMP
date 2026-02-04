package com.rizero.shared_core_network.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val message : String,
    val detailedMessage: String,
    val timestamp : Long,
    val errorID : String,
)