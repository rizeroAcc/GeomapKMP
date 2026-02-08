package com.rizero.shared_core_datasource.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.rizero.shared_core_network.model.UserSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single
class SessionLocalDatasourceMultiplatform(
    val sessionDatastore : DataStore<Preferences>
) : SessionLocalDatasource {

    private val json = Json { encodeDefaults = true }

    private companion object {
        private val AUTH_DATA_KEY = stringPreferencesKey("auth_data")
    }

    override suspend fun saveCurrentSession(session: UserSession) {
        sessionDatastore.edit { preferences ->
            val jsonData = json.encodeToString(session)
            preferences[AUTH_DATA_KEY] = jsonData
        }
    }

    override suspend fun clearCurrentSession() {
        sessionDatastore.edit { preferences ->
            preferences.remove(AUTH_DATA_KEY)
        }
    }

    override suspend fun getCurrentSession(): UserSession? {
        val data = sessionDatastore.data.map { preferences ->
            preferences[AUTH_DATA_KEY]
        }.first()
        return data?.let {
            json.decodeFromString<UserSession>(data)
        }
    }
}