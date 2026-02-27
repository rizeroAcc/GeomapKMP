package com.rizero.shared_core_data.repository

import com.rizero.shared_core_data.model.Project
import com.rizero.shared_core_data.model.Session
import com.rizero.shared_core_data.model.UserModel
import com.rizero.shared_core_data.model.toEntity
import com.rizero.shared_core_data.model.toUserSession
import com.rizero.shared_core_database.dao.UserDAO
import com.rizero.shared_core_datasource.local.ProjectLocalDatasource
import com.rizero.shared_core_datasource.remote.ProjectRemoteDatasource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

@Single
class UserRepository(
    val userDAO: UserDAO,
) {
    suspend fun saveUser(userModel: UserModel){
        userDAO.insertUser(userModel.toEntity())
    }
}