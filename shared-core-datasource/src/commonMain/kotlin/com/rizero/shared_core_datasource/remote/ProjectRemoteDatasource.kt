package com.rizero.shared_core_datasource.remote

import com.mapprjct.model.dto.Project
import com.mapprjct.model.dto.ProjectMembership
import com.mapprjct.model.dto.ProjectRegistrationResult
import com.rizero.shared_core_datasource.exception.project.GetAllUserProjectsError
import com.rizero.shared_core_datasource.exception.project.RegisterProjectError
import com.rizero.shared_core_datasource.exception.project.RegisterProjectListError
import com.rizero.shared_core_network.model.UserSession
import com.rizero.shared_core_utils.NetworkResult

interface ProjectRemoteDatasource {
    suspend fun registerNewProject(projectName : String, userSession: UserSession) : NetworkResult<ProjectRegistrationResult, RegisterProjectError>
    suspend fun getAllUserProjects(userSession: UserSession) : NetworkResult<List<ProjectMembership>, GetAllUserProjectsError>

    suspend fun registerNewProjectList(projects: List<Project>, session: UserSession) : NetworkResult<List<ProjectRegistrationResult>, RegisterProjectListError>
}