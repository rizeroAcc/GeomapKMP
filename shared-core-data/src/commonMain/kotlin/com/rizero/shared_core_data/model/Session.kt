package com.rizero.shared_core_data.model

import com.rizero.shared_core_network.model.UserSession

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