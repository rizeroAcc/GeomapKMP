package com.rizero.geomapkmp

import com.rizero.di.DatabaseModule
import com.rizero.feature_authorization.di.FeatureAuthorizationModule
import com.rizero.feature_project_select.di.FeatureProjectSelectModule
import com.rizero.feature_registration.di.FeatureRegistrationModule
import com.rizero.feature_user_profile.di.FeatureUserProfileModule
import com.rizero.geomapkmp.di.FlowModule
import com.rizero.shared_core_data.RepositoryModule
import com.rizero.shared_core_datasource.di.DatasourceModule
import com.rizero.shared_core_network.di.NetworkModule
import org.koin.core.annotation.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.plugin.module.dsl.startKoin

@KoinApplication(
    modules = [
        NetworkModule::class,
        DatasourceModule::class,
        DatabaseModule::class,
        RepositoryModule::class,

        FeatureAuthorizationModule::class,
        FeatureProjectSelectModule::class,
        FeatureUserProfileModule::class,
        FeatureRegistrationModule::class,

        FlowModule::class
    ]
)
object KoinInstance

fun initKoin(cfg: KoinAppDeclaration? = null) {
    startKoin<KoinInstance> {
        cfg?.invoke(this)
    }
}