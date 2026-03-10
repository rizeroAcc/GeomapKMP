package com.rizero.shared_core_datasource.remote

import com.mapprjct.model.datatype.Username
import com.mapprjct.model.dto.User
import com.mapprjct.model.dto.UserCredentials
import com.mapprjct.model.response.auth.RefreshTokenResponse
import com.mapprjct.model.response.auth.RegistrationResponse
import com.mapprjct.model.response.auth.SignInResponse
import com.rizero.shared_core_datasource.exception.auth.RefreshTokenError
import com.rizero.shared_core_datasource.exception.auth.SignInError
import com.rizero.shared_core_datasource.exception.auth.SignOutError
import com.rizero.shared_core_datasource.exception.auth.SignUpError
import com.rizero.shared_core_network.api.AuthAPI
import com.rizero.shared_core_network.model.ErrorResponse
import com.rizero.shared_core_network.model.UserSession
import com.rizero.shared_core_utils.NetworkResult
import com.rizero.shared_core_utils.bodySafely
import com.rizero.shared_core_utils.defaultNetworkCall
import com.rizero.shared_core_utils.exceptions.ConnectionException
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.CancellationException
import kotlinx.io.IOException
import org.koin.core.annotation.Single

@Single
class AuthRemoteDatasourceMultiplatform(
    val authAPI: AuthAPI
) : AuthRemoteDatasource {
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
                        SignInError.InternalServerError(errorResponse?.message)
                    }
                    HttpStatusCode.Unauthorized -> {
                        SignInError.InvalidCredentialsError()
                    }
                    else -> SignInError.UnexpectedServerResponse()
                }
            }
        )
    }
    override suspend fun signUp(username : Username, credentials: UserCredentials) : NetworkResult<User, SignUpError>{
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
    override suspend fun logOut(token : String) : NetworkResult<Unit, SignOutError>{
        return defaultNetworkCall<Unit,Unit, SignOutError>(
            call = {
                authAPI.signOut(token)
            },
            onRequestSuccess = { _,_ -> },
            onRequestFailure = { status, _ ->
                when(status){
                    HttpStatusCode.InternalServerError -> SignOutError.InternalServerError()
                    HttpStatusCode.NotFound -> SignOutError.SessionAlreadyNotActive()
                    else -> SignOutError.UnexpectedServerResponse()
                }
            }
        )
    }

    override suspend fun checkTokenValid(token: String): NetworkResult<Boolean, Nothing> {
        return try {
            NetworkResult.Success(authAPI.validateToken(token).status == HttpStatusCode.OK)
        }catch(e : CancellationException) {
            throw e
        }catch (e : HttpRequestTimeoutException){
            NetworkResult.Failure.NetworkError(ConnectionException(e))
        }catch (e : ConnectTimeoutException){
            NetworkResult.Failure.NetworkError(ConnectionException(e))
        }catch (e : SocketTimeoutException){
            NetworkResult.Failure.NetworkError(ConnectionException(e))
        }catch (e : IOException){
            NetworkResult.Failure.NetworkError(ConnectionException(e))
        }
    }

    override suspend fun refreshSession(token: String) : NetworkResult<Pair<String,Long>, RefreshTokenError>{
        return defaultNetworkCall<RefreshTokenResponse,Pair<String,Long>,RefreshTokenError>(
            call = {
                authAPI.refreshToken(token)
            },
            onRequestSuccess = { body,response ->
                response.headers["Authorization"]!! to body.tokenExpireAt
            },
            onRequestFailure = { status, response ->
                when(status){
                    HttpStatusCode.Unauthorized -> RefreshTokenError.Unauthorized()
                    else -> RefreshTokenError.ServerError()
                }
            }
        )
    }
}