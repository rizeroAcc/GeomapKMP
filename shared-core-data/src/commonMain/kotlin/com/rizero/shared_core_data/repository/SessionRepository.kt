package com.rizero.shared_core_data.repository

import com.mapprjct.model.datatype.Password
import com.mapprjct.model.datatype.RussiaPhoneNumber
import com.mapprjct.model.datatype.Username
import com.mapprjct.model.dto.User
import com.mapprjct.model.dto.UserCredentials
import com.rizero.shared_core_data.exceptions.LogInError
import com.rizero.shared_core_data.exceptions.LogOutError
import com.rizero.shared_core_data.exceptions.RegistrationError
import com.rizero.shared_core_data.model.Session
import com.rizero.shared_core_data.model.Token
import com.rizero.shared_core_data.model.UserModel
import com.rizero.shared_core_datasource.remote.AuthRemoteDatasource
import com.rizero.shared_core_datasource.exception.auth.SignInError
import com.rizero.shared_core_datasource.exception.auth.SignOutError
import com.rizero.shared_core_datasource.exception.auth.SignUpError
import com.rizero.shared_core_datasource.local.SessionLocalDatasource
import com.rizero.shared_core_utils.Either
import com.rizero.shared_core_utils.fold
import org.koin.core.annotation.Single

@Single
class SessionRepository(
    private val sessionLocalDatasource: SessionLocalDatasource,
    private val authRemoteDatasource: AuthRemoteDatasource,
) {
    suspend fun logInUser(phone : String, password : String) : Either<Session, LogInError> {
        val credentials = UserCredentials(RussiaPhoneNumber(phone), Password(password))
        return authRemoteDatasource.signIn(credentials).fold(
            onSuccess = { userSession->
                sessionLocalDatasource.saveCurrentSession(userSession)
                Either.success(Session(
                    user = UserModel.fromUserDTO(userSession.user),
                    token = Token(
                        value = userSession.tokenData.first,
                        expireAt = userSession.tokenData.second
                    )
                ))
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

    suspend fun registerUser(phone: String, password: String, username: String) : Either<UserModel, RegistrationError>{
        val userCredentials = UserCredentials(phone = RussiaPhoneNumber(phone), password = Password(password))
        return authRemoteDatasource.signUp(
            username = Username(username),
            credentials = userCredentials
        ).fold(
            onSuccess = { user->
                Either.success(UserModel.fromUserDTO(user))
            },
            onNetworkError = {
                Either.failure(RegistrationError.ConnectionError())
            },
            onFailure = { error ->
                when(error){
                    is SignUpError.InternalServerError,
                    is SignUpError.UnexpectedServerError -> Either.failure(RegistrationError.ServerError())
                    is SignUpError.UserAlreadyRegistered -> Either.failure(RegistrationError.UserAlreadyExists(phone))
                }
            }
        )
    }
    suspend fun logOutUser() : Either<Unit, LogOutError>{
        val currentSession = getCurrentSession() ?: return Either.success(Unit)
        return authRemoteDatasource.logOut(currentSession.token.value).fold(
            onSuccess = {
                sessionLocalDatasource.clearCurrentSession()
                Either.success(Unit)
            },
            onNetworkError = {
                Either.failure(LogOutError.ConnectionError())
            },
            onFailure = { error ->
                when(error){
                    is SignOutError.InternalServerError -> Either.failure(LogOutError.ServerError())
                    is SignOutError.SessionAlreadyNotActive -> Either.success(Unit)
                    is SignOutError.UnexpectedServerResponse -> Either.failure(LogOutError.UnexpectedResponse())
                }
            }
        )
    }

    suspend fun clearActiveSession() {
        sessionLocalDatasource.clearCurrentSession()
    }

    suspend fun refreshSession(){TODO("Пока не написано на сервере")}

    suspend fun getCurrentSession() : Session?{
        return sessionLocalDatasource.getCurrentSession()?.let {
            Session.fromUserSession(it)
        }
    }

}