package com.rizero.shared_core_data.model

import com.mapprjct.model.datatype.Role
import com.mapprjct.model.datatype.StringUUID
import com.mapprjct.model.dto.ProjectMembership
import com.mapprjct.model.dto.ProjectRegistrationResult
import com.rizero.shared_core_database.entity.ProjectEntity
import com.rizero.shared_core_database.model.UserMembershipInProject

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
        fun fromMembershipEntity(projectMembership: UserMembershipInProject) : Project = Project(
            name = projectMembership.project.name,
            id = projectMembership.project.projectID,
            serverID = projectMembership.project.serverProjectID,
            membersCount = projectMembership.project.membersCount,
            role = projectMembership.role.toInt()
        )
        fun fromMembershipDTO(projectMembership: ProjectMembership) : Project = Project(
            name = projectMembership.project.name,
            id = projectMembership.project.projectID.value,
            serverID = projectMembership.project.projectID.value,
            membersCount = projectMembership.project.membersCount,
            role = projectMembership.role.toInt()
        )
        /**
         * @throws NullPointerException if registration result doesn't contain old project ID
         * */
        fun fromRegistrationResult(projectRegistrationResult: ProjectRegistrationResult) = Project(
            name = projectRegistrationResult.project.name,
            id = projectRegistrationResult.oldID!!.value,
            serverID = projectRegistrationResult.project.projectID.value,
            membersCount = projectRegistrationResult.project.membersCount,
            role = Role.Owner.toInt()
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