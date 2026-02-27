package com.rizero.shared_core_database.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.rizero.shared_core_database.AppDatabase
import com.rizero.shared_core_database.dao.ProjectDAO
import com.rizero.shared_core_database.dao.UserDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.rizero.shared_core_database")
@Configuration
class DatabaseModule {
    @Single
    fun provideAppDatabase() : AppDatabase {
        //todo убрать fallback на релизе
        return getDatabaseBuilder()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .fallbackToDestructiveMigration(true)
            .build()
    }
    @Single
    fun provideUserDAO(database: AppDatabase) : UserDAO {
        return database.userDao()
    }
    @Single
    fun provideProjectDAO(database: AppDatabase) : ProjectDAO {
        return database.projectDao()
    }
}

expect fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>