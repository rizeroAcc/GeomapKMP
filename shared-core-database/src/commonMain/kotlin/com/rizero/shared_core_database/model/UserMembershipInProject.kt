package com.rizero.shared_core_database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.rizero.shared_core_database.entity.ProjectEntity

data class UserMembershipInProject(
    @ColumnInfo(name = "user_phone")
    val userPhone : String,
    val role : Short,
    @Embedded val project: ProjectEntity,
) {
}