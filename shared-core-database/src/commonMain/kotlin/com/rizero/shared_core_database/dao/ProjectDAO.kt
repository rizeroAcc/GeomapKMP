package com.rizero.shared_core_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.OnConflictStrategy.Companion.IGNORE
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
    @Insert(onConflict = IGNORE)
    suspend fun insertAll(projects : List<ProjectEntity>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateProject(project: ProjectEntity)

    @Update
    suspend fun updateProjects(projects : List<ProjectEntity>)

    @Query(
        """
            UPDATE projects SET server_project_id = :serverProjectID WHERE project_id = :projectID
        """
    )
    suspend fun updateAfterRegistrationOnServer(projectID: String, serverProjectID : String)

    @Query("""
        SELECT * FROM projects WHERE project_id = :projectID
    """)
    suspend fun findByID(projectID : String) : ProjectEntity


}