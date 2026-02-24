package com.rizero.shared_core_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rizero.shared_core_database.entity.UserEntity
import com.rizero.shared_core_database.entity.UserProjectsMembership

@Dao
interface UserDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user : UserEntity)

    @Query("""
        SELECT * FROM users WHERE phone = :phone
    """)
    suspend fun getUser(phone : String) : UserEntity?

    @Query("""
        SELECT * FROM users WHERE phone = :phone
    """)
    suspend fun getAllUserMemberships(phone: String) : UserProjectsMembership
}