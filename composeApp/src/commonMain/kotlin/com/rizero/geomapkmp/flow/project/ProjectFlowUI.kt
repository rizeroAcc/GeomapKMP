package com.rizero.geomapkmp.flow.project

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.rizero.feature_project_select.ui.ProjectSelectionScreen
import com.rizero.feature_user_profile.ui.UserProfileScreen

@Composable
fun HomeFlowUI(projectFlowComponent: ProjectFlowComponent){
    val stack by projectFlowComponent.stack.subscribeAsState()
    Children(
        stack = stack,
        modifier = Modifier
            .fillMaxSize()
    ) { child ->
        when(val instance = child.instance){
            is ProjectFlowComponent.Child.ProjectSelectionPage -> ProjectSelectionScreen(
                instance.projectSelectionComponent
            )
            is ProjectFlowComponent.Child.UserProfile -> UserProfileScreen(
                instance.userProfileComponent
            )
        }
    }
}