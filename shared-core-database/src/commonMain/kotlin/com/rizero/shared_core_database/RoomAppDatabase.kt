package com.rizero.shared_core_database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.rizero.shared_core_database.entity.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = true
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {

}

// The Room compiler generates the `actual` implementations.
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}