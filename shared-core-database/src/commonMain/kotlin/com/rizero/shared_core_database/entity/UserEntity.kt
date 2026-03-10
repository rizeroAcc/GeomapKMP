package com.rizero.shared_core_database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
)
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(index = true)
    val phone : String,
    val username : String,
    @ColumnInfo(name = "avatar_path")
    val avatarPath : String?,
)

