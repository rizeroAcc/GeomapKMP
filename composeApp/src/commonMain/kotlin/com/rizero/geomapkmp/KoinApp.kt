package com.rizero.geomapkmp

import com.rizero.di.DatabaseModule
import com.rizero.shared_core_datasource.di.DatasourceModule
import com.rizero.shared_core_network.di.NetworkModule
import org.koin.core.annotation.KoinApplication
import org.koin.dsl.KoinAppDeclaration

@KoinApplication(
    modules = [
        NetworkModule::class,
        DatasourceModule::class,
        DatabaseModule::class,
    ]
)
object KoinInstance