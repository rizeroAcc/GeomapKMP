package com.rizero.geomapkmp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.defaultComponentContext
import com.rizero.geomapkmp.flow.authentication.AuthenticationFlowComponent
import com.rizero.geomapkmp.flow.project.ProjectFlowComponent
import org.koin.java.KoinJavaComponent.inject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val authenticationFlowComponentFactory : AuthenticationFlowComponent.ComponentFactory by inject(
            AuthenticationFlowComponent.ComponentFactory::class.java
        )
        val projectFlowComponentFactory : ProjectFlowComponent.ComponentFactory by inject(
            ProjectFlowComponent.ComponentFactory::class.java
        )
        setContent {
            RootUI(
                RootComponent(
                    componentContext = defaultComponentContext(),
                    authenticationFlowComponentFactory = authenticationFlowComponentFactory,
                    projectFlowComponentFactory = projectFlowComponentFactory
                )
            )
        }
    }
}