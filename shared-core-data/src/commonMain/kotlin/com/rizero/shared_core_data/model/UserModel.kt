package com.rizero.shared_core_data.model

import com.mapprjct.model.dto.User
import com.rizero.shared_core_database.entity.UserEntity

data class UserModel(
    val phone : String,
    val username : String,
    val avatarFilename : String?,
){
    companion object {
        fun fromUserDTO(userDTO: User) : UserModel {
            return UserModel(
                phone = userDTO.phone,
                username = userDTO.username,
                avatarFilename = userDTO.avatarFilename,
            )
        }
    }
}

fun UserModel.toEntity() = UserEntity(
    phone = this.phone,
    username = this.username,
    avatarPath = this.avatarFilename
)
