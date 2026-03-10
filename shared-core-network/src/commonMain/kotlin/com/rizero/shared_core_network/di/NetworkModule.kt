package com.rizero.shared_core_network.di

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.rizero.shared_core_network")
@Configuration
class  NetworkModule{

    @Single
    fun provideClient() : HttpClient{
        return provideHttpClient {
            expectSuccess = false
            install(ContentNegotiation){
                json(Json{
                    ignoreUnknownKeys = true
                })
            }
            install(Logging) {
                logger = object : io.ktor.client.plugins.logging.Logger {
                    override fun log(message: String) {
                        Logger.i("Ktor") { message }  // ← теперь через Kermit!
                    }
                }
                level = LogLevel.ALL
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 10_000L
                connectTimeoutMillis = 10_000L
                socketTimeoutMillis = 10_000L
            }
            defaultRequest {
                host = provideBaseUrl()
                contentType(ContentType.Application.Json)
            }
        }
    }
}

expect fun provideHttpClient(config : HttpClientConfig<*>.() -> Unit) : HttpClient
expect fun provideBaseUrl() : String