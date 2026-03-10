package com.rizero.shared_core_network.api

import com.mapprjct.model.datatype.Username
import com.mapprjct.model.dto.UserCredentials
import com.mapprjct.model.request.auth.RegistrationRequest
import com.mapprjct.model.request.auth.SignInRequest
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.annotation.Single

interface AuthAPI {
    suspend fun signIn(credentials: UserCredentials) : HttpResponse
    suspend fun signOut(token : String) : HttpResponse
    suspend fun signUp(username : Username, credentials: UserCredentials) : HttpResponse
    suspend fun validateToken(token: String): HttpResponse
    suspend fun refreshToken(token: String) : HttpResponse
}

@Single
class DefaultAuthAPI(val client : HttpClient) : AuthAPI {
    /**
     * @throws io.ktor.client.network.sockets.ConnectTimeoutException подключение не удалось за 10с
     * @throws HttpRequestTimeoutException между запросом и ответом прошло 10с
     * @throws io.ktor.network.sockets.SocketTimeoutException чтение данных прервано за 10с
     * */
    override suspend fun signIn(credentials: UserCredentials) : HttpResponse {
        val request = SignInRequest(
            phone = credentials.phone,
            password = credentials.password
        )
        return client.post("/signin") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }
    override suspend fun signUp(username : Username, credentials: UserCredentials) : HttpResponse{
        val request = RegistrationRequest(
            phone = credentials.phone,
            username = username,
            password = credentials.password
        )
        return client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    override suspend fun signOut(token : String) : HttpResponse {
        return client.post("/logout") {
            headers.append("Authorization", token)
        }
    }

    override suspend fun validateToken(token: String): HttpResponse {
        return client.get("/validate_token") {
            headers.append("Authorization", token)
        }
    }

    override suspend fun refreshToken(token: String) : HttpResponse{
        return client.post("/refresh_token") {
            headers.append("Authorization", token)
        }
    }

}