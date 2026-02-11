package com.rizero.geomapkmp

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.rizero.geomapkmp.flow.authentication.AuthenticationFlowUI
import com.rizero.geomapkmp.flow.project.HomeFlowUI

@Composable
fun RootUI(rootComponent: RootComponent){
    val stack by rootComponent.stack.subscribeAsState()
    Children(
        stack = stack,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
    ) { child->
        when(val instance = child.instance){
            is RootComponent.Child.Authentication -> AuthenticationFlowUI(instance.flow)
            is RootComponent.Child.Project -> HomeFlowUI(instance.flow)
        }
    }
}