package com.rizero.shared_core_data.repository

import com.mapprjct.model.datatype.Role
import com.mapprjct.model.datatype.StringUUID
import com.mapprjct.model.dto.ProjectMembership
import com.rizero.shared_core_data.exceptions.ProjectRegistrationError
import com.rizero.shared_core_data.exceptions.LoadUserProjectsError
import com.rizero.shared_core_data.exceptions.LoadUserProjectsError.*
import com.rizero.shared_core_data.model.Project
import com.rizero.shared_core_data.model.Session
import com.rizero.shared_core_data.model.toEntity
import com.rizero.shared_core_data.model.toUserSession
import com.rizero.shared_core_database.entity.ProjectMembershipEntity
import com.rizero.shared_core_database.model.UserMembershipInProject
import com.rizero.shared_core_datasource.exception.project.GetAllUserProjectsError
import com.rizero.shared_core_datasource.exception.project.RegisterProjectError
import com.rizero.shared_core_datasource.exception.project.RegisterProjectListError
import com.rizero.shared_core_datasource.local.ProjectLocalDatasource
import com.rizero.shared_core_datasource.remote.ProjectRemoteDatasource
import com.rizero.shared_core_utils.Either
import com.rizero.shared_core_utils.fold
import org.koin.core.annotation.Single

@Single
class ProjectRepository(
    val projectRemoteDatasource: ProjectRemoteDatasource,
    val projectLocalDatasource: ProjectLocalDatasource,
) {
    suspend fun getCachedUserProjects(userPhone: String) : List<Project> {
        return projectLocalDatasource
            .getAllUserProjects(userPhone)
            .map { membershipInProject -> Project.fromMembershipEntity(membershipInProject) }
    }
    suspend fun loadProjectsFromNetwork(session: Session) : Either<List<Project>, LoadUserProjectsError> {
        return projectRemoteDatasource.getAllUserProjects(session.toUserSession()).fold(
            onSuccess = { projectMemberships ->
                Either.success(projectMemberships.map { Project.fromMembershipDTO(it) })
            },
            onNetworkError = { Either.failure(ConnectionError())},
            onFailure = { error->
                when(error){
                    is GetAllUserProjectsError.InternalServerError,
                    is GetAllUserProjectsError.UnexpectedServerResponse ->
                        Either.failure(ServerError())
                    is GetAllUserProjectsError.Unauthorized ->
                        Either.failure(Unauthorized())
                }
            }
        )
    }

    suspend fun createUnregisteredProject(projectName : String, session: Session) : Project{
        val createdProjectEntity = projectLocalDatasource.createUnregisteredProject(
            projectName = projectName,
            userPhone = session.user.phone
        )
        return Project.fromEntity(createdProjectEntity, Role.Owner.toInt())
    }

    suspend fun registerProject(project: Project, session: Session) : Either<Project, ProjectRegistrationError>{
        return projectRemoteDatasource.registerNewProject(project.name, session.toUserSession()).fold(
            onSuccess = { registrationResult ->
                projectLocalDatasource.updateServerIDAfterProjectRegistration(
                    projectID = project.id,
                    serverProjectID = registrationResult.project.projectID.value
                )
                Either.success(project.copy(serverID = registrationResult.project.projectID.value))
            },
            onNetworkError = {
                Either.failure(ProjectRegistrationError.ConnectionError())
            },
            onFailure = { error ->
                when(error){
                    is RegisterProjectError.InternalServerError,
                    is RegisterProjectError.UnexpectedServerResponse ->
                        Either.failure(ProjectRegistrationError.ServerError())
                    is RegisterProjectError.Unauthorized ->
                        Either.failure(ProjectRegistrationError.Unauthorized())
                }
            }
        )
    }

    suspend fun registerProjects(projects : List<Project>,session: Session) : Either<List<Project>, ProjectRegistrationError>{
        val projectsDto = projects.map { com.mapprjct.model.dto.Project(StringUUID(it.id),it.name,it.membersCount) }
        return projectRemoteDatasource.registerNewProjectList(projectsDto, session = session.toUserSession()).fold(
            onSuccess = { registeredProjects->
                for (regResult in registeredProjects){
                    updateProjectServerID(
                        projectID = regResult.oldID!!.value,
                        serverProjectID = regResult.project.projectID.value
                    )
                }
                Either.success(registeredProjects.map { Project.fromRegistrationResult(it) })
            },
            onNetworkError = {
                Either.failure(ProjectRegistrationError.ConnectionError())
            },
            onFailure = { error ->
                when(error){
                    is RegisterProjectListError.UnexpectedServerResponse,
                    is RegisterProjectListError.InternalServerError ->
                        Either.failure(ProjectRegistrationError.ServerError())
                    is RegisterProjectListError.Unauthorized ->
                        Either.failure(ProjectRegistrationError.Unauthorized())
                }

            }
        )
    }



    suspend fun updateProjectServerID(projectID : String, serverProjectID : String){
        projectLocalDatasource.updateServerIDAfterProjectRegistration(
            projectID = projectID,
            serverProjectID = serverProjectID
        )
    }

    suspend fun saveReceivedProjects(serverProjects : List<Project>, userPhone : String) : List<Project> {
        val cachedProjects = getCachedUserProjects(userPhone)
        val cachedProjectsServerID = cachedProjects.map { it.serverID }
        val uncachedProjects = serverProjects.filter { it.serverID !in cachedProjectsServerID}
        projectLocalDatasource.saveRegisteredProjectList(
            projectsAndMemberships = uncachedProjects.map {
                it.toEntity() to ProjectMembershipEntity(
                    userPhone = userPhone,
                    projectID = it.id,
                    role = it.role.toShort()
                )
            }
        )
        return cachedProjects + uncachedProjects
    }

}
