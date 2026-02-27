package com.rizero.shared_core_data.model

import com.mapprjct.model.datatype.RussiaPhoneNumber
import com.mapprjct.model.datatype.Username
import com.mapprjct.model.dto.User
import com.rizero.shared_core_database.entity.UserEntity
import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
    val phone : String,
    val username : String,
    val avatarFilename : String?,
){
    companion object {
        fun fromUserDTO(userDTO: User) : UserModel {
            return UserModel(
                phone = userDTO.phone.value,
                username = userDTO.username.value,
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

fun UserModel.toDto() = User(
    phone = RussiaPhoneNumber(this.phone),
    username = Username(this.username),
    avatarFilename = this.avatarFilename
)
