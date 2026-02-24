package com.rizero.shared_core_database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.rizero.shared_core_database.dao.UserDAO
import com.rizero.shared_core_database.entity.ProjectEntity
import com.rizero.shared_core_database.entity.ProjectMembership
import com.rizero.shared_core_database.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ProjectEntity::class,
        ProjectMembership::class,
               ],
    version = 2,
    exportSchema = true
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao() : UserDAO
}

// The Room compiler generates the `actual` implementations.
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}