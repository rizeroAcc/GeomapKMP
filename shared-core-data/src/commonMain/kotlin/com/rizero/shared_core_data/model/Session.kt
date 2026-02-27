package com.rizero.shared_core_data.model

import com.rizero.shared_core_network.model.UserSession
import kotlinx.serialization.Serializable

@Serializable
data class Session (
    val user : UserModel,
    val token: Token,
){
    companion object{
        fun fromUserSession(userSession: UserSession) : Session = Session(
            user = UserModel.fromUserDTO(userSession.user),
            token = Token(
                value = userSession.tokenData.first,
                expireAt = userSession.tokenData.second
            )
        )
    }
}

fun Session.toUserSession() = UserSession(
    user = this.user.toDto(),
    tokenData = this.token.value to this.token.expireAt
)