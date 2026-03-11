package com.rizero.feature_project_select.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.feature_project_select.store.AddProjectDialogStore
import com.rizero.feature_project_select.ProjectSelectorState
import com.rizero.feature_project_select.component.AddProjectDialogComponent
import com.rizero.feature_project_select.component.MockAddProjectDialogComponent
import com.rizero.shared_core_component.theme.AppTheme
import com.rizero.shared_core_component.ui.DefaultTextField
import com.rizero.shared_core_component.ui.TwoSegmentSwitch
import com.rizero.shared_core_component.ui.TwoSegmentSwitchPosition

@Composable
fun CreateProjectDialog(
    addProjectDialogComponent: AddProjectDialogComponent
){
    val state by addProjectDialogComponent.stateFlow.collectAsState()
    Column(
        modifier = Modifier
            .background(
                color = AppTheme.Colors.CardBackgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .height(310.dp)
            .width(340.dp)
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .border(
                    border = BorderStroke(1.dp, color = Color.Black),
                    shape = RoundedCornerShape(16.dp)
                )
                .width(220.dp)
                .height(44.dp)
        ){
            Text(
                text = "Добавить проект",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }
        TwoSegmentSwitch(
            position =
                if (state.projectSelectorState == ProjectSelectorState.NEW_PROJECT) {
                    TwoSegmentSwitchPosition.LEFT
                }else{
                    TwoSegmentSwitchPosition.RIGHT
                }
            ,
            onPositionChange = { position->
                when(position) {
                    TwoSegmentSwitchPosition.LEFT -> addProjectDialogComponent.onSelectorStateChanged(
                        selectorState = ProjectSelectorState.NEW_PROJECT
                    )
                    TwoSegmentSwitchPosition.RIGHT -> addProjectDialogComponent.onSelectorStateChanged(
                        selectorState = ProjectSelectorState.JOIN_PROJECT
                    )
                }

            },
            contentLeft = {
                Text("Новый")
            },
            contentRight = {
                Text("Присоединиться")
            },
            modifier = Modifier
                .padding(top = 20.dp)
                .width(290.dp)
                .height(40.dp),
        )

        Crossfade(
            targetState = state.projectSelectorState,
            animationSpec = tween(500)
        ) { fadeState->
            when(fadeState) {
                ProjectSelectorState.NEW_PROJECT -> {
                    DefaultTextField(
                        value = state.newProjectName,
                        onValueChange = addProjectDialogComponent::onNewProjectNameChanged,
                        placeholder = "Название проекта",
                        supportingText = "Название проекта",
                        modifier =
                            Modifier
                                .padding(top = 8.dp)
                                .size(296.dp,72.dp)
                    )
                }
                ProjectSelectorState.JOIN_PROJECT -> {
                    DefaultTextField(
                        value = state.joinCode,
                        onValueChange = addProjectDialogComponent::onJoinCodeChanged,
                        placeholder = "Код приглашения",
                        supportingText = "Код приглашения",
                        modifier =
                            Modifier
                                .padding(top = 8.dp)
                                .size(296.dp,72.dp)
                    )
                }
            }
        }



        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = if(state.isLoading)
                    Color.Gray
                else
                    AppTheme.Colors.DefaultButtonColor
            ),
            onClick = {
                addProjectDialogComponent.createProject()
            },
            modifier = Modifier
                .padding(top = 18.dp)
                .size(220.dp,60.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp)
                )
            }else{
                Text(
                    text = when (state.projectSelectorState) {
                        ProjectSelectorState.NEW_PROJECT -> "Создать"
                        ProjectSelectorState.JOIN_PROJECT -> "Присоединиться"
                    },
                    fontSize = 20.sp
                )
            }
        }
    }
}




@Preview
@Composable
fun CreateProjectDialogNewPreview(){
    CreateProjectDialog(
        MockAddProjectDialogComponent(
            state = AddProjectDialogStore.State(
                projectSelectorState = ProjectSelectorState.NEW_PROJECT,
                newProjectName = "Имя проекта",
                joinCode = "sgdt4y3df"
            )
        )
    )
}

@Preview
@Composable
fun CreateProjectDialogJoinPreview(){
    CreateProjectDialog(
        MockAddProjectDialogComponent(
            state = AddProjectDialogStore.State(
                projectSelectorState = ProjectSelectorState.JOIN_PROJECT,
                newProjectName = "Имя проекта",
                joinCode = "sgdt4y3df",
                isLoading = true
            )
        )
    )
}