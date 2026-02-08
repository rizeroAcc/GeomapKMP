package com.rizero.shared_core_datasource.di

import java.nio.file.Paths

actual fun getSessionDatastorePath(): String {
    val home = System.getProperty("user.home")
    val dir = Paths.get(home, ".myapp", "datastore").toFile()
    dir.mkdirs()  // создаём директорию, если нет

    return dir.resolve("auth_datastore.preferences_pb").absolutePath
}