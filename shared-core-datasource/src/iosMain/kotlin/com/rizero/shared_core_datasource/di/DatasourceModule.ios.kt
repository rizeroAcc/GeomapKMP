package com.rizero.shared_core_datasource.di

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual fun getSessionDatastorePath(): String {
    val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null
    )
    val path = requireNotNull(documentDirectory?.path)
    return "$path/auth_datastore.preferences_pb"
}