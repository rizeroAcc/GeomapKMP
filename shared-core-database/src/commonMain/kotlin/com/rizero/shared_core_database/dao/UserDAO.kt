package com.rizero.shared_core_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rizero.shared_core_database.entity.UserEntity
import com.rizero.shared_core_database.model.UserMembershipInProject


@Dao
interface UserDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user : UserEntity)

    @Query("""
        SELECT * FROM users WHERE phone = :phone
    """)
    suspend fun getUser(phone : String) : UserEntity?

    @Transaction
    @Query("""
        SELECT * FROM project_user_membership 
        INNER JOIN projects ON projects.project_id = project_user_membership.project_id
        WHERE user_phone = :phone
    """)
    suspend fun getAllUserMemberships(phone: String) : List<UserMembershipInProject>
}