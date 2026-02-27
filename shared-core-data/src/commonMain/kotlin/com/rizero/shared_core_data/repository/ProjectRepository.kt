package com.rizero.shared_core_data.repository

import com.mapprjct.model.datatype.Role
import com.mapprjct.model.datatype.StringUUID
import com.rizero.shared_core_data.exceptions.ServerLoadFailure
import com.rizero.shared_core_data.exceptions.ServerLoadFailure.*
import com.rizero.shared_core_data.model.Project
import com.rizero.shared_core_data.model.Session
import com.rizero.shared_core_data.model.toUserSession
import com.rizero.shared_core_database.entity.ProjectEntity
import com.rizero.shared_core_datasource.exception.project.GetAllUserProjectsError
import com.rizero.shared_core_datasource.local.ProjectLocalDatasource
import com.rizero.shared_core_datasource.remote.ProjectRemoteDatasource
import com.rizero.shared_core_utils.Either
import com.rizero.shared_core_utils.fold
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single

@Single
class ProjectRepository(
    val projectRemoteDatasource: ProjectRemoteDatasource,
    val projectLocalDatasource: ProjectLocalDatasource,
) {
    suspend fun getCachedUserProjects(session: Session) : List<Project> {
        return projectLocalDatasource
            .getAllUserProjects(session.user.phone)
            .map { membershipInProject ->
                Project(
                    name = membershipInProject.project.name,
                    id = membershipInProject.project.projectID,
                    serverID = membershipInProject.project.serverProjectID,
                    membersCount = membershipInProject.project.membersCount,
                    role = membershipInProject.role.toInt()
                )
            }
    }
    suspend fun loadProjectsFromNetwork(session: Session) : Either<List<Project>, ServerLoadFailure> {
        return projectRemoteDatasource.getAllUserProjects(session.toUserSession()).fold(
            onSuccess = { projectMemberships ->
                Either.success(projectMemberships.map { Project(
                    name = it.project.name,
                    id = it.project.projectID.value,
                    serverID = it.project.projectID.value,
                    membersCount = it.project.membersCount,
                    role = it.role.toShort().toInt()
                ) })
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

    suspend fun registerProjects(projects : List<Project>,session: Session) : Either<List<Project>, ServerLoadFailure>{
        val projectsDto = projects.map { com.mapprjct.model.dto.Project(StringUUID(it.id),it.name,it.membersCount) }
        return projectRemoteDatasource.registerNewProjectList(projectsDto, session = session.toUserSession()).fold(
            onSuccess = { registeredProjects->
                Either.success(registeredProjects.map {
                    Project(
                        name = it.project.name,
                        id = it.oldID!!.value,
                        serverID = it.project.projectID.value,
                        membersCount = it.project.membersCount,
                        role = Role.Owner.toShort().toInt()
                    )
                })
            },
            onNetworkError = {
                Either.failure(TODO())
            },
            onFailure = {
                Either.failure(TODO())
            }
        )
    }

    suspend fun syncProjects(cachedProjects : List<Project>, serverProjects : List<Project>, session: Session) : Flow<ProjectSyncState> = flow {
        //todo проверить, может разбить на методы
        emit(ProjectSyncState.SyncStarted())

        //save received projects
        val registeredProjectsID = cachedProjects.filter { it.serverID != null }.map { it.serverID }
        val newProjects = serverProjects.filter { it.serverID !in registeredProjectsID}
        //cache
        projectLocalDatasource.saveRegisteredProjectList(newProjects
            .map {
                ProjectEntity(
                    projectID = it.id,
                    serverProjectID = it.serverID,
                    name = it.name,
                    membersCount = it.membersCount
                )
            }
        )
        val newCachedList = ArrayList(cachedProjects)
        newCachedList.addAll(newProjects)
        emit(ProjectSyncState.ReceivedSaved(newCachedList))

        //register projects
        val userSession = session.toUserSession()


        val syncedProjects = ArrayList<Project>()
        var registeredCount = 0

        val notRegisteredProjects = cachedProjects.filter { it.serverID == null }

        emit(ProjectSyncState.InProcess(registeredCount,notRegisteredProjects.size))
        for (projectToRegister in notRegisteredProjects){
            projectRemoteDatasource.registerNewProject(projectToRegister.name,userSession).fold(
                onSuccess = { registrationResult->
                    projectLocalDatasource.updateServerIDAfterProjectRegistration(projectToRegister.id, serverProjectID = registrationResult.project.projectID.value)
                    registeredCount += 1
                    syncedProjects.add(Project(
                        name = registrationResult.project.name,
                        id = registrationResult.project.projectID.value,
                        serverID = registrationResult.project.projectID.value,
                        membersCount = registrationResult.project.membersCount,
                        role = Role.Owner.toShort().toInt()
                    ))
                    emit(ProjectSyncState.InProcess(registeredCount,notRegisteredProjects.size))
                },
                onNetworkError = { error ->
                    emit(ProjectSyncState.SyncError(error, syncedProjects = syncedProjects))
                    return@flow
                },
                onFailure = { error ->
                    emit(ProjectSyncState.SyncError(error, syncedProjects = syncedProjects))
                    return@flow
                }
            )
        }
        newCachedList.addAll(syncedProjects)
        emit(ProjectSyncState.Synced(newCachedList))
    }
}

sealed interface ProjectSyncState{
    class SyncStarted() : ProjectSyncState
    class ReceivedSaved(val newCachedProjects : List<Project>) : ProjectSyncState
    class InProcess(val registered : Int, val total : Int) : ProjectSyncState
    class Synced(val refreshedProjects : List<Project>) : ProjectSyncState
    class SyncError(val error : Throwable, val syncedProjects : List<Project>) : ProjectSyncState
}