package com.rizero.shared_core_database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.util.TableInfo

@Entity(
    tableName = "project_user_membership",
    primaryKeys = ["userPhone","projectID"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
            parentColumns = ["phone"],
            childColumns = ["userPhone"]
        ),
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["projectID"],
            childColumns = ["projectID"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        )
    ]
)
class ProjectMembership(
    val userPhone : String,
    val projectID : String,
    val role : Short
) {

}