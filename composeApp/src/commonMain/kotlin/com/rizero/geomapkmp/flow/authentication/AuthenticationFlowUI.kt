package com.rizero.geomapkmp.flow.authentication

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.rizero.feature_authorization.ui.AuthorizationScreen
import com.rizero.feature_authorization.ui.InitialSessionLoadScreen
import com.rizero.feature_registration.ui.RegistrationScreen

@Composable
fun AuthenticationFlowUI(authenticationFlowComponent: AuthenticationFlowComponent){
    val stack by authenticationFlowComponent.stack.subscribeAsState()
    Children(
        stack = stack,
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) { child ->
        when(val instance = child.instance){
            is AuthenticationFlowComponent.Child.Authorization -> AuthorizationScreen(instance.authorizationComponent)
            is AuthenticationFlowComponent.Child.Registration -> RegistrationScreen(instance.registrationComponent)
            is AuthenticationFlowComponent.Child.InitialLoadPage -> InitialSessionLoadScreen(instance.initialSessionLoadComponent)
        }
    }
}