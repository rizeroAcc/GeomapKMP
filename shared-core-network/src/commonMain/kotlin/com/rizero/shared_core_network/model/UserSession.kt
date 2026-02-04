package com.rizero.shared_core_network.model

import com.mapprjct.model.dto.User
import kotlinx.serialization.Serializable

/**
 * @property tokenData represent token with them expiration
 * */
@Serializable
data class UserSession(
    val user : User,
    val tokenData : Pair<String,Long>
)