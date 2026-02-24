package com.rizero.shared_core_database.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class UserProjectsMembership(
    @Embedded val user : UserEntity,
    @Relation(
        parentColumn = "phone",
        entityColumn = "userPhone",
        associateBy = Junction(ProjectMembership::class)
    )
    val memberships :List<ProjectMembership>
) {
}