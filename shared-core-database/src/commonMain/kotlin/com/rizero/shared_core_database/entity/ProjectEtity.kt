package com.rizero.shared_core_database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "projects"
)
data class ProjectEntity(
    @PrimaryKey
    @ColumnInfo(index = true)
    val projectID : String,
    val name : String,
    val createdOnServer : Boolean
) {
}