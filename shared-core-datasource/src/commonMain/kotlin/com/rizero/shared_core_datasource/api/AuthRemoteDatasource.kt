package com.rizero.shared_core_datasource.api

import com.mapprjct.model.dto.User
import com.mapprjct.model.dto.UserCredentials
import com.rizero.shared_core_datasource.exception.auth.SignInError
import com.rizero.shared_core_datasource.exception.auth.SignUpError
import com.rizero.shared_core_network.model.UserSession
import com.rizero.shared_core_utils.NetworkResult

interface AuthRemoteDatasource {
    suspend fun signIn(credentials: UserCredentials) : NetworkResult<UserSession, SignInError>
    suspend fun signUp(username : String, credentials: UserCredentials) : NetworkResult<User, SignUpError>
    suspend fun logOut(token : String) : NetworkResult<Unit, Throwable>
}