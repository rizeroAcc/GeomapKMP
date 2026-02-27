package com.rizero.shared_core_database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "project_user_membership",
    primaryKeys = ["user_phone","project_id"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
            parentColumns = ["phone"],
            childColumns = ["user_phone"]
        ),
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["project_id"],
            childColumns = ["project_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index("user_phone","project_id", name = "user_project_index", unique = true)
    ]
)
class ProjectMembershipEntity(
    @ColumnInfo(name = "user_phone") val userPhone : String,
    @ColumnInfo(name = "project_id") val projectID : String,
    val role : Short
) {

}