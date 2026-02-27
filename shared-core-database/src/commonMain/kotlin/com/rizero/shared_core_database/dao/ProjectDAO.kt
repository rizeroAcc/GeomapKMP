package com.rizero.shared_core_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.rizero.shared_core_database.entity.ProjectEntity
import com.rizero.shared_core_database.entity.ProjectMembershipEntity
import com.rizero.shared_core_database.model.UserMembershipInProject
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Dao
interface ProjectDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(project : ProjectEntity)
    @Insert
    suspend fun insertMembership(projectMembership: ProjectMembershipEntity)

    @Insert
    suspend fun insertAll(projects : List<ProjectEntity>)

    @OptIn(ExperimentalUuidApi::class)
    @Transaction
    suspend fun saveNewProject(projectName : String, userPhone : String) {
        val generatedUUID = Uuid.random().toHexDashString()
        insert(ProjectEntity(
            projectID = generatedUUID,
            serverProjectID = null,
            name = projectName,
            membersCount = 1
        ))
        insertMembership(ProjectMembershipEntity(
            userPhone = userPhone,
            projectID = generatedUUID,
            role = 1 //Owner role
        ))
    }

    @Transaction
    suspend fun saveRegisteredOnServerProject(project: ProjectEntity, membership: ProjectMembershipEntity){
        insert(project)
        insertMembership(membership)
    }


    @Query("""
        SELECT * FROM projects WHERE projectID = :projectID
    """)
    suspend fun findByID(projectID : String)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Query(
        """
            UPDATE projects SET serverProjectID = :serverProjectID WHERE projectID = :projectID
        """
    )
    suspend fun updateAfterRegistrationOnServer(projectID: String, serverProjectID : String)
}