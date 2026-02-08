package com.rizero.shared_core_datasource.local

import com.rizero.shared_core_network.model.UserSession

interface SessionLocalDatasource {
    suspend fun saveCurrentSession(session: UserSession)
    suspend fun clearCurrentSession()
    suspend fun getCurrentSession() : UserSession?
}