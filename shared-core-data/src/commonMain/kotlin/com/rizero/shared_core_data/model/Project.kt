package com.rizero.shared_core_data.model

import com.mapprjct.model.datatype.Role
import com.mapprjct.model.datatype.StringUUID
import com.rizero.shared_core_database.entity.ProjectEntity

data class Project(
    val name : String,
    val id : String,
    val serverID : String?,
    val membersCount : Int,
    val role : Int,
){
    companion object {
        fun fromEntity(projectEntity: ProjectEntity, role: Int) : Project = Project(
            name = projectEntity.name,
            id = projectEntity.projectID,
            serverID = projectEntity.serverProjectID,
            membersCount = projectEntity.membersCount,
            role = role
        )
    }
}

fun Project.toEntity() = ProjectEntity(
    projectID = id,
    serverProjectID = serverID,
    name = name,
    membersCount = membersCount
)

fun Project.toDTO() = com.mapprjct.model.dto.Project(
    projectID = StringUUID(serverID ?: id),
    name = name,
    membersCount = membersCount
)