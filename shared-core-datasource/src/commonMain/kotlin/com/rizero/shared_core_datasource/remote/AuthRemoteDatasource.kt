package com.rizero.shared_core_datasource.remote

import com.mapprjct.model.datatype.Username
import com.mapprjct.model.dto.User
import com.mapprjct.model.dto.UserCredentials
import com.rizero.shared_core_datasource.exception.auth.RefreshTokenError
import com.rizero.shared_core_datasource.exception.auth.SignInError
import com.rizero.shared_core_datasource.exception.auth.SignOutError
import com.rizero.shared_core_datasource.exception.auth.SignUpError
import com.rizero.shared_core_network.model.UserSession
import com.rizero.shared_core_utils.NetworkResult

interface AuthRemoteDatasource {
    suspend fun signIn(credentials: UserCredentials) : NetworkResult<UserSession, SignInError>
    suspend fun signUp(username : Username, credentials: UserCredentials) : NetworkResult<User, SignUpError>
    suspend fun logOut(token : String) : NetworkResult<Unit, SignOutError>
    /**
     * Call validate route
     * @return [NetworkResult.Success] with boolean value that indicates Response Code equals 200
     * */
    suspend fun checkTokenValid(token : String) : NetworkResult<Boolean, Nothing>

    suspend fun refreshSession(token: String) : NetworkResult<Pair<String,Long>, RefreshTokenError>
}