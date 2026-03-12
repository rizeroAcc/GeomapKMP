package com.rizero.shared_core_datasource.local


import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import com.mapprjct.model.datatype.Role
import com.rizero.shared_core_database.AppDatabase
import com.rizero.shared_core_database.dao.MembershipDAO
import com.rizero.shared_core_database.dao.ProjectDAO
import com.rizero.shared_core_database.entity.ProjectEntity
import com.rizero.shared_core_database.entity.ProjectMembershipEntity
import com.rizero.shared_core_database.model.UserMembershipInProject
import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Single
class ProjectLocalDatasourceMultiplatform(
    val database: AppDatabase,
    val projectDAO: ProjectDAO,
    val membershipDAO: MembershipDAO,
) : ProjectLocalDatasource {
    override suspend fun getAllUserProjects(userPhone : String) : List<UserMembershipInProject>{
        return membershipDAO.findAllUserMemberships(userPhone)
    }
    override suspend fun updateServerIDAfterProjectRegistration(projectID : String, serverProjectID : String){
        projectDAO.updateAfterRegistrationOnServer(projectID, serverProjectID = serverProjectID)
    }
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun createUnregisteredProject(projectName : String, userPhone: String) : ProjectEntity{
        val generatedUUID = Uuid.random().toHexDashString()
        val newProject = ProjectEntity(
            projectID = generatedUUID,
            serverProjectID = null,
            name = projectName,
            membersCount = 1
        )
        database.useWriterConnection { transactor ->
            transactor.immediateTransaction {
                projectDAO.insert(newProject)
                membershipDAO.insertMembership(ProjectMembershipEntity(
                    userPhone = userPhone,
                    projectID = generatedUUID,
                    role = Role.Owner.toShort() //Owner role
                ))
            }
        }
        return newProject
    }
    override suspend fun saveRegisteredProject(project: ProjectEntity,role: Role, userPhone: String){
        val membershipInProject = ProjectMembershipEntity(
            userPhone = userPhone,
            projectID = project.projectID,
            role = role.toShort()
        )
        database.useWriterConnection{ transactor ->
            transactor.immediateTransaction {
                projectDAO.insert(project)
                membershipDAO.insertMembership(membershipInProject)
            }
        }
    }
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun saveRegisteredProjectList(projectsAndMemberships: List<Pair<ProjectEntity,ProjectMembershipEntity>>) {
        database.useWriterConnection { transactor ->
            transactor.immediateTransaction {
                projectDAO.insertAll(projectsAndMemberships.map { it.first })
                membershipDAO.insertMemberships(projectsAndMemberships.map { it.second })
            }
        }
    }
}