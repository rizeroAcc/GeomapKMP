package com.rizero.shared_core_network.api

import com.mapprjct.model.datatype.Role
import com.mapprjct.model.datatype.StringUUID
import com.mapprjct.model.request.project.CreateInvitationRequest
import com.mapprjct.model.request.project.CreateProjectRequest
import com.mapprjct.model.request.project.JoinProjectRequest
import com.rizero.shared_core_network.model.UserSession
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import org.koin.core.annotation.Single

interface ProjectAPI {
    suspend fun createProject(projectName : String, session: UserSession) : HttpResponse
    suspend fun getAllUserProjects(session: UserSession) : HttpResponse
    suspend fun joinProject(inviteCode : StringUUID,session: UserSession) : HttpResponse
    suspend fun inviteToProject(projectID : StringUUID, role: Role ,session: UserSession) : HttpResponse
}

@Single
class DefaultProjectAPI(val client: HttpClient) : ProjectAPI {
    override suspend fun createProject(projectName : String, session: UserSession) : HttpResponse{
        val request = CreateProjectRequest(
            projectName = projectName
        )
        return client.post("/projects") {
            headers.append("Authorization",session.tokenData.first)
            setBody(request)
        }
    }
    override suspend fun getAllUserProjects(session: UserSession) : HttpResponse {
        return client.get("/projects/all") {
            headers.append("Authorization", session.tokenData.first)
        }
    }
    override suspend fun joinProject(inviteCode : StringUUID, session: UserSession) : HttpResponse{
        val request = JoinProjectRequest(
            inviteCode = inviteCode.value
        )
        return client.post("/projects/join") {
            headers.append("Authorization", session.tokenData.first)
            setBody(request)
        }
    }
    override suspend fun inviteToProject(projectID : StringUUID, role: Role, session: UserSession) : HttpResponse {
        val request = CreateInvitationRequest(
            projectID = projectID.value,
            role = role.toShort()
        )
        return client.post("/projects/invite") {
            headers.append("Authorization", session.tokenData.first)
            setBody(request)
        }
    }
}