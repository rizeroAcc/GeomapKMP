package com.rizero.shared_core_data.repository

import com.rizero.shared_core_data.model.UserModel
import com.rizero.shared_core_data.model.toEntity
import com.rizero.shared_core_database.dao.UserDAO
import org.koin.core.annotation.Single

@Single
class UserRepository(val userDAO: UserDAO) {
    suspend fun saveUser(userModel: UserModel){
        userDAO.insertUser(userModel.toEntity())
    }
}