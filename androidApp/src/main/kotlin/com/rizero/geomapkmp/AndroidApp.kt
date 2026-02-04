package com.rizero.geomapkmp

import android.app.Application
import org.koin.plugin.module.dsl.startKoin


class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin<KoinInstance> {

        }
    }
}