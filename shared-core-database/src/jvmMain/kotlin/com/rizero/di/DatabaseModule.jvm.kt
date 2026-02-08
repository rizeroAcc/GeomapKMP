package com.rizero.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.rizero.shared_core_database.AppDatabase
import java.io.File
import java.nio.file.Paths

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val home = System.getProperty("user.home")
    val dir = Paths.get(home, ".myapp", "datastore").toFile()
    dir.mkdirs()  // создаём директорию, если нет
    return Room.databaseBuilder<AppDatabase>(
        name = dir.resolve("geomap_app.db").absolutePath,
    )
}