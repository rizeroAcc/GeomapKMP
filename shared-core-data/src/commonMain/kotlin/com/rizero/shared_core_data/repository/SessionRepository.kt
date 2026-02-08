package com.rizero.shared_core_data.repository

import com.mapprjct.model.dto.UserCredentials
import com.rizero.shared_core_data.exceptions.LogInError
import com.rizero.shared_core_data.model.Session
import com.rizero.shared_core_data.model.UserModel
import com.rizero.shared_core_datasource.api.AuthRemoteDatasource
import com.rizero.shared_core_datasource.exception.auth.SignInError
import com.rizero.shared_core_datasource.local.SessionLocalDatasource
import com.rizero.shared_core_utils.Either
import com.rizero.shared_core_utils.fold
import org.koin.core.annotation.Single

@Single
class SessionRepository(
    private val sessionLocalDatasource: SessionLocalDatasource,
    private val authRemoteDatasource: AuthRemoteDatasource,
) {
    suspend fun logInUser(phone : String, password : String) : Either<UserModel, LogInError> {
        val credentials = UserCredentials(phone,password)
        return authRemoteDatasource.signIn(credentials).fold(
            onSuccess = { userSession->
                sessionLocalDatasource.saveCurrentSession(userSession)
                Either.success(UserModel.fromUserDTO(userSession.user))
            },
            onNetworkError = { Either.failure(LogInError.ConnectionError())},
            onFailure = { error ->
                when(error) {
                    is SignInError.InternalServerError -> Either.failure(LogInError.ServerError())
                    is SignInError.InvalidCredentialsError -> Either.failure(LogInError.InvalidCredentials())
                    is SignInError.UnexpectedServerResponse -> Either.failure(LogInError.UnexpectedResponse())
                }
            }
        )
    }

    suspend fun RegisterUser(){TODO()}
    suspend fun logOutUser(){TODO()}
    suspend fun refreshSession(){TODO()}

    suspend fun getCurrentSession() : Session?{
        return sessionLocalDatasource.getCurrentSession()?.let {
            Session.fromUserSession(it)
        }
    }

}