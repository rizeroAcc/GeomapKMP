package com.rizero.geomapkmp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.rizero.shared_core_datasource.api.AuthRemoteDatasource
import com.rizero.shared_core_datasource.local.SessionLocalDatasource
import com.rizero.shared_core_network.api.AuthAPI
import org.koin.java.KoinJavaComponent.inject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        //tODO УБРАТЬ, ЭТО ДЛЯ ПРОВЕРКИ ЧТО KOIN РАБОТАЕТ
        val authAPI : AuthAPI by inject(AuthAPI::class.java)
        val authRemoteDatasource : AuthRemoteDatasource by inject(AuthRemoteDatasource::class.java)
        val authLocalDatasource : SessionLocalDatasource by inject(SessionLocalDatasource::class.java)
        Log.d("koin", authAPI.toString())
        Log.d("koin",authLocalDatasource.toString())
        Log.d("koin",authRemoteDatasource.toString())
        setContent {
            App()

        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}