package com.rizero.shared_core_database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "projects"
)
data class ProjectEntity(
    @PrimaryKey
    @ColumnInfo(name = "project_id",index = true)
    val projectID : String,
    @ColumnInfo(name = "server_project_id")
    val serverProjectID : String? = null,
    val name : String,
    @ColumnInfo(name = "members_count")
    val membersCount : Int
) {
}