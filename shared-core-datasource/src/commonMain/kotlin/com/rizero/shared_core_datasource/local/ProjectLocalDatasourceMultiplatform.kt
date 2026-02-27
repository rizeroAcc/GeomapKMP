package com.rizero.shared_core_datasource.local

import com.mapprjct.model.datatype.Role
import com.mapprjct.model.dto.Project
import com.rizero.shared_core_database.dao.ProjectDAO
import com.rizero.shared_core_database.dao.UserDAO
import com.rizero.shared_core_database.entity.ProjectEntity
import com.rizero.shared_core_database.entity.ProjectMembershipEntity
import com.rizero.shared_core_database.model.UserMembershipInProject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ProjectLocalDatasourceMultiplatform(
    val projectDAO: ProjectDAO,
    val userDAO: UserDAO
) : ProjectLocalDatasource {
    override suspend fun getAllUserProjects(userPhone : String) : List<UserMembershipInProject>{
        return userDAO.getAllUserMemberships(userPhone)
    }
    override suspend fun updateServerIDAfterProjectRegistration(projectID : String, serverProjectID : String){
        projectDAO.updateAfterRegistrationOnServer(projectID, serverProjectID = serverProjectID)
    }
    override suspend fun createNewProject(projectName : String, userPhone: String){
        projectDAO.saveNewProject(projectName,userPhone)
    }
    override suspend fun saveRegisteredProject(projectID : String, projectName: String, membersCount : Int, role: Role, userPhone: String){
        projectDAO.saveRegisteredOnServerProject(
            project = ProjectEntity(
                projectID = projectID,
                serverProjectID = projectID,
                name = projectName,
                membersCount = membersCount
            ),
            membership = ProjectMembershipEntity(
                userPhone = userPhone,
                projectID = projectID,
                role = role.toShort()
            )
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun saveRegisteredProjectList(projects: List<ProjectEntity>) {
        projectDAO.insertAll(projects)
    }
}