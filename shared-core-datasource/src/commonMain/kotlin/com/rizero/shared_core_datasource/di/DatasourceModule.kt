package com.rizero.shared_core_datasource.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.rizero.shared_core_datasource")
@Configuration
class DatasourceModule{
    @Single
    fun createSessionDataStore(): DataStore<Preferences> =
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { getSessionDatastorePath().toPath() }
        )
}

expect fun getSessionDatastorePath() : String