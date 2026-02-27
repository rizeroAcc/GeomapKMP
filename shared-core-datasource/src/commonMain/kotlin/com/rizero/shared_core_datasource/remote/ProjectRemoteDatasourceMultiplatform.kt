package com.rizero.shared_core_datasource.remote

import com.mapprjct.model.dto.Project
import com.mapprjct.model.dto.ProjectMembership
import com.mapprjct.model.dto.ProjectRegistrationResult
import com.mapprjct.model.dto.UnregisteredProject
import com.mapprjct.model.response.project.GetAllUserProjectsResponse
import com.mapprjct.model.response.project.RegisterProjectListResponse
import com.mapprjct.model.response.project.RegisterProjectResponse
import com.rizero.shared_core_datasource.exception.project.GetAllUserProjectsError
import com.rizero.shared_core_datasource.exception.project.RegisterProjectError
import com.rizero.shared_core_datasource.exception.project.RegisterProjectListError
import com.rizero.shared_core_network.api.ProjectAPI
import com.rizero.shared_core_network.model.ErrorResponse
import com.rizero.shared_core_network.model.UserSession
import com.rizero.shared_core_utils.NetworkResult
import com.rizero.shared_core_utils.bodySafely
import com.rizero.shared_core_utils.defaultNetworkCall
import io.ktor.http.HttpStatusCode

class ProjectRemoteDatasourceMultiplatform(
    val projectAPI: ProjectAPI
) : ProjectRemoteDatasource {
    override suspend fun registerNewProject(projectName : String, userSession: UserSession) : NetworkResult<ProjectRegistrationResult, RegisterProjectError> {
        return defaultNetworkCall< RegisterProjectResponse, ProjectRegistrationResult , RegisterProjectError>(
            call = {
                projectAPI.createProject(
                    projectName = projectName,
                    session = userSession
                )
            },
            onRequestSuccess = { createProjectResponse,_ ->
                createProjectResponse.registrationResult
            },
            onRequestFailure = { code, response ->
                when(code){
                    HttpStatusCode.InternalServerError -> {
                        val errorResponse = response.bodySafely<ErrorResponse>()
                        RegisterProjectError.InternalServerError(
                            errorResponse?.message
                        )
                    }
                    HttpStatusCode.Unauthorized -> RegisterProjectError.Unauthorized()
                    else -> RegisterProjectError.UnexpectedServerResponse()
                }
            }
        )
    }
    override suspend fun getAllUserProjects(userSession: UserSession) : NetworkResult<List<ProjectMembership>, GetAllUserProjectsError>{
        return defaultNetworkCall<GetAllUserProjectsResponse,List<ProjectMembership>, GetAllUserProjectsError>(
            call = {
                projectAPI.getAllUserProjects(userSession)
            },
            onRequestSuccess = { getAllProjectsResponse,_->
                getAllProjectsResponse.result
            },
            onRequestFailure = { code,response ->
                when(code){
                    HttpStatusCode.InternalServerError -> {
                        val errorResponse = response.bodySafely<ErrorResponse>()
                        GetAllUserProjectsError.InternalServerError(
                            errorResponse?.message
                        )
                    }
                    HttpStatusCode.Unauthorized ->
                        GetAllUserProjectsError.Unauthorized()
                    else -> GetAllUserProjectsError.UnexpectedServerResponse()
                }
            }
        )
    }

    override suspend fun registerNewProjectList(projects: List<Project>, session: UserSession): NetworkResult<List<ProjectRegistrationResult>, RegisterProjectListError> {
        return defaultNetworkCall<RegisterProjectListResponse,List<ProjectRegistrationResult>, RegisterProjectListError>(
            call = {
                val projectsToRegister = projects.map { UnregisteredProject(it.projectID,it.name) }
                projectAPI.createProjectList(projectsToRegister,session)
            },
            onRequestSuccess = {rBody,r->
                rBody.registeredProjects
            },
            onRequestFailure = { code,response->
                when(code){
                    HttpStatusCode.InternalServerError -> {
                        val errorResponse = response.bodySafely<ErrorResponse>()
                        RegisterProjectListError.InternalServerError(
                            errorResponse?.message
                        )
                    }
                    HttpStatusCode.Unauthorized-> RegisterProjectListError.Unauthorized()
                    else -> RegisterProjectListError.UnexpectedServerResponse()
                }
            }
        )
    }
}