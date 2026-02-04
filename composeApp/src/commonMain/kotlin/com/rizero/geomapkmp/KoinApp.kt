package com.rizero.geomapkmp

import com.rizero.shared_core_network.di.NetworkModule
import org.koin.core.annotation.KoinApplication
import org.koin.dsl.KoinAppDeclaration

@KoinApplication(
    modules = [NetworkModule::class]
)
object KoinInstance