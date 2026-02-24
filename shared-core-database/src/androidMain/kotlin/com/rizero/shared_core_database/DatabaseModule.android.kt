package com.rizero.shared_core_database.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rizero.shared_core_database.AppDatabase
import org.koin.java.KoinJavaComponent.inject

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val appContext : Context by inject(Context::class.java)
    val dbFile = appContext.getDatabasePath("geomap_app.db")
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}