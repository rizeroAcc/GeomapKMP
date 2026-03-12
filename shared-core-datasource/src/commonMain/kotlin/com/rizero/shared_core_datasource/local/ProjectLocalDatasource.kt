package com.rizero.shared_core_datasource.local

import com.mapprjct.model.datatype.Role
import com.rizero.shared_core_database.entity.ProjectEntity
import com.rizero.shared_core_database.entity.ProjectMembershipEntity
import com.rizero.shared_core_database.model.UserMembershipInProject

interface ProjectLocalDatasource {
    suspend fun getAllUserProjects(userPhone : String) : List<UserMembershipInProject>
    suspend fun updateServerIDAfterProjectRegistration(projectID : String, serverProjectID : String)
    suspend fun createUnregisteredProject(projectName : String, userPhone: String) : ProjectEntity
    suspend fun saveRegisteredProject(project: ProjectEntity,role: Role, userPhone: String)
    suspend fun saveRegisteredProjectList(projectsAndMemberships: List<Pair<ProjectEntity,ProjectMembershipEntity>>)
}