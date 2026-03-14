package com.rizero.geomapkmp.flow.work

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.rizero.feature_project_mapview.ui.MapScreen
import com.rizero.feature_project_select.ui.ProjectSelectionScreen
import com.rizero.feature_user_profile.ui.UserProfileScreen
import com.rizero.geomapkmp.flow.project.ProjectFlowComponent

@Composable
fun WorkflowUI(workFlowComponent: WorkFlowComponent){
    val stack by workFlowComponent.stack.subscribeAsState()
    Children(
        stack = stack,
        modifier = Modifier
            .fillMaxSize()
    ) { child ->
        when(val instance = child.instance){
            is WorkFlowComponent.Child.Map -> {
                MapScreen(instance.mapComponent)
            }
        }
    }
}