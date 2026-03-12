package com.rizero.shared_core_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.rizero.shared_core_database.entity.ProjectMembershipEntity
import com.rizero.shared_core_database.model.UserMembershipInProject

@Dao
interface MembershipDAO {
    @Insert
    suspend fun insertMembership(projectMembership: ProjectMembershipEntity)
    @Insert
    suspend fun insertMemberships(projectMemberships : List<ProjectMembershipEntity>)
    @Transaction
    @Query("""
        SELECT * FROM project_user_membership 
        INNER JOIN projects ON projects.project_id = project_user_membership.project_id
        WHERE user_phone = :phone
    """)
    suspend fun findAllUserMemberships(phone: String) : List<UserMembershipInProject>
}