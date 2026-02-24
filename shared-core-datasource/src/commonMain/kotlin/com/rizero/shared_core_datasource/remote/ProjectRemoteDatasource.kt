package com.rizero.shared_core_datasource.remote

import com.mapprjct.model.dto.Project
import com.mapprjct.model.dto.ProjectMembership
import com.rizero.shared_core_datasource.exception.project.GetAllUserProjectsError
import com.rizero.shared_core_datasource.exception.project.RegisterProjectError
import com.rizero.shared_core_network.model.UserSession
import com.rizero.shared_core_utils.NetworkResult

interface ProjectRemoteDatasource {
    suspend fun registerNewProject(projectName : String, userSession: UserSession) : NetworkResult<Project, RegisterProjectError>
    suspend fun getAllUserProjects(userSession: UserSession) : NetworkResult<List<ProjectMembership>, GetAllUserProjectsError>
}