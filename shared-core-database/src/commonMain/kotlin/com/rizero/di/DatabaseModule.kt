package com.rizero.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.rizero.shared_core_database.AppDatabase
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
    fun provideAppDatabase(builder: RoomDatabase.Builder<AppDatabase>) : AppDatabase {
        return builder
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}

expect fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>