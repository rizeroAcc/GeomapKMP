package com.rizero.shared_core_network.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.logging.HttpLoggingInterceptor


actual fun provideHttpClient(config : HttpClientConfig<*>.() -> Unit): HttpClient {

    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.setLevel(level = HttpLoggingInterceptor.Level.BODY)

    return HttpClient(
        engineFactory = OkHttp,
    ){
        config()
        engine {
            addInterceptor(loggingInterceptor)
        }
    }
}