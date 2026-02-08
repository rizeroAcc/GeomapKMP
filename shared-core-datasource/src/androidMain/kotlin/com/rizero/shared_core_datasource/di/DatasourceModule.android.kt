package com.rizero.shared_core_datasource.di

import android.content.Context
import org.koin.java.KoinJavaComponent.inject

actual fun getSessionDatastorePath(): String {
    val context: Context by inject(Context::class.java)
    return context.filesDir
        .resolve("auth_datastore.preferences_pb")
        .absolutePath
}