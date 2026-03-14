package com.rizero.geomapkmp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.rizero.feature_project_select.component.ProjectSelectComponent
import com.rizero.geomapkmp.flow.authentication.AuthenticationFlowComponent
import com.rizero.geomapkmp.flow.project.ProjectFlowComponent
import com.rizero.geomapkmp.flow.work.WorkFlowComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import java.awt.Dimension

fun main() = application {
    //todo use when maplibre correctly draw with other composables
//    System.setProperty("compose.interop.blending", "true")
//    System.setProperty("skiko.rendering.useNewRendering", "true")
//    System.setProperty("compose.swing.render.on.graphics", "true")
    initKoin {  }
    val lifecycle = LifecycleRegistry()

    val authenticationFlowComponentFactory : AuthenticationFlowComponent.ComponentFactory by inject(
        AuthenticationFlowComponent.ComponentFactory::class.java
    )
    val projectFlowComponentFactory : ProjectFlowComponent.ComponentFactory by inject(
        ProjectFlowComponent.ComponentFactory::class.java
    )
    val workflowComponentFactory : WorkFlowComponent.ComponentFactory by inject(
        WorkFlowComponent.ComponentFactory::class.java
    )

    val rootComponent = RootComponent(
        componentContext = DefaultComponentContext(
            lifecycle = lifecycle
        ),
        authenticationFlowComponentFactory = authenticationFlowComponentFactory,
        projectFlowComponentFactory = projectFlowComponentFactory,
        workFlowComponentFactory = workflowComponentFactory,
    )
    Window(
        onCloseRequest = ::exitApplication,
        title = "GeomapKMP",
    ) {
        this.window.minimumSize = Dimension(800,600)
        RootUI(rootComponent)
    }
}