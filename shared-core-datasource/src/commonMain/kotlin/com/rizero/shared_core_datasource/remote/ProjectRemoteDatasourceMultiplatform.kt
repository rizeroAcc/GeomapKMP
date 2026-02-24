package com.rizero.shared_core_datasource.remote

import com.mapprjct.model.dto.Project
import com.mapprjct.model.dto.ProjectMembership
import com.mapprjct.model.response.project.CreateProjectResponse
import com.mapprjct.model.response.project.GetAllUserProjectsResponse
import com.rizero.shared_core_datasource.exception.project.GetAllUserProjectsError
import com.rizero.shared_core_datasource.exception.project.RegisterProjectError
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
    override suspend fun registerNewProject(projectName : String, userSession: UserSession) : NetworkResult<Project, RegisterProjectError> {
        return defaultNetworkCall< CreateProjectResponse, Project , RegisterProjectError>(
            call = {
                projectAPI.createProject(
                    projectName = projectName,
                    session = userSession
                )
            },
            onRequestSuccess = { createProjectResponse,_ ->
                createProjectResponse.project
            },
            onRequestFailure = { code, response ->
                when(code){
                    HttpStatusCode.InternalServerError -> {
                        val errorResponse = response.bodySafely<ErrorResponse>()
                        RegisterProjectError.InternalServerError(
                            errorResponse?.message
                        )
                    }
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
                    else -> GetAllUserProjectsError.UnexpectedServerResponse()
                }
            }
        )
    }
}