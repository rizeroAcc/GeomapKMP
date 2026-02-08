package com.rizero.shared_core_data.repository

import com.rizero.shared_core_data.model.Project
import kotlinx.coroutines.delay
import org.koin.core.annotation.Single

@Single
class ProjectRepository {
    suspend fun getAllUserProjects(userPhone : String) : List<Project>{
        delay(1500)
        return listOf(
            Project(
                name = "project 1",
                id = "1",
                membersCount = 2,
                syncStatus = 2,
                role = 2
            ),
            Project(
                name = "project 2",
                id = "2",
                membersCount = 1,
                syncStatus = 3,
                role = 1
            ),
            Project(
                name = "project 3",
                id = "3",
                membersCount = 5,
                syncStatus = 1,
                role = 3
            )
        )
    }
}