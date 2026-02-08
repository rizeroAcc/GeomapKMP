package com.rizero.shared_core_datasource.api

import com.mapprjct.model.dto.User
import com.mapprjct.model.dto.UserCredentials
import com.mapprjct.model.response.auth.RegistrationResponse
import com.mapprjct.model.response.auth.SignInResponse
import com.rizero.shared_core_datasource.exception.auth.SignInError
import com.rizero.shared_core_datasource.exception.auth.SignUpError
import com.rizero.shared_core_network.api.AuthAPI
import com.rizero.shared_core_network.model.ErrorResponse
import com.rizero.shared_core_network.model.UserSession
import com.rizero.shared_core_utils.NetworkResult
import com.rizero.shared_core_utils.bodySafely
import com.rizero.shared_core_utils.defaultNetworkCall
import io.ktor.http.HttpStatusCode
import org.koin.core.annotation.Single

@Single
class AuthRemoteDatasourceMultiplatform(
    val authAPI: AuthAPI
) : AuthRemoteDatasource {
    /**
     * @throws ConnectionException - if network interrupted
     * @throws SignInError - if request failed
     * */
    override suspend fun signIn(credentials: UserCredentials) : NetworkResult<UserSession, SignInError> {
        return defaultNetworkCall<SignInResponse, UserSession, SignInError>(
            call = { authAPI.signIn(credentials) },
            onRequestSuccess = { responseBody,response->
                UserSession(
                    user = responseBody.user,
                    tokenData = response.headers["Authorization"]!! to responseBody.tokenExpiration
                )
            },
            onRequestFailure = { statusCode, response->
                when(statusCode){
                    HttpStatusCode.InternalServerError -> {
                        val errorResponse = response.bodySafely<ErrorResponse>()
                        SignInError.InternalServerError(errorResponse?.detailedMessage)
                    }
                    HttpStatusCode.Unauthorized -> {
                        SignInError.InvalidCredentialsError()
                    }
                    else -> SignInError.UnexpectedServerResponse()
                }
            }
        )
    }
    override suspend fun signUp(username : String, credentials: UserCredentials) : NetworkResult<User, SignUpError>{
        return defaultNetworkCall<RegistrationResponse, User, SignUpError>(
            call = {
                authAPI.signUp(username, credentials)
            },
            onRequestSuccess = { response,_->
                response.user
            },
            onRequestFailure = { statusCode,_->
                when(statusCode){
                    HttpStatusCode.InternalServerError -> SignUpError.InternalServerError()
                    HttpStatusCode.Conflict -> SignUpError.UserAlreadyRegistered()
                    else -> SignUpError.UnexpectedServerError()
                }
            }
        )
    }
    override suspend fun logOut(token : String) : NetworkResult<Unit, Throwable>{
        TODO()
    }
}