package com.rizero.feature_project_select.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.window.Dialog
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.rizero.feature_project_select.store.AddProjectDialogStore
import com.rizero.feature_project_select.store.ProjectListStore
import com.rizero.feature_project_select.component.MockAddProjectDialogComponent
import com.rizero.feature_project_select.component.MockProjectSelectComponent
import com.rizero.feature_project_select.component.ProjectSelectComponent
import com.rizero.feature_project_select.ui.component.ProjectCard
import com.rizero.shared_core_component.decompose.MockIconButtonTopBarComponent
import com.rizero.shared_core_component.decompose.ui.ProfileTopAppBar
import com.rizero.shared_core_component.theme.AppTheme
import com.rizero.shared_core_data.model.Project
import geomapkmp.feature_project_select.generated.resources.Res
import geomapkmp.feature_project_select.generated.resources.add
import io.github.lmbotero.pulltorefresh.ui.PullToRefreshLayout
import io.github.lmbotero.pulltorefresh.util.RefreshStateEvent
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProjectSelectionScreen(projectSelectionComponent: ProjectSelectComponent){
    val state by projectSelectionComponent.stateFlow.collectAsState()

    val addProjectDialog by projectSelectionComponent.addProjectDialog.subscribeAsState()
    val topBarComponent = projectSelectionComponent.topBarComponent

    Scaffold(
        topBar = {
            ProfileTopAppBar(topBarComponent)
        },
        floatingActionButton = {
            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = Color.Blue),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 4.dp, pressedElevation = 2.dp
                )
            ) {
                IconButton(onClick = projectSelectionComponent::openAddProjectDialog) {
                    Image(
                        painter = painterResource(Res.drawable.add), contentDescription = ""
                    )
                }
            }
        }
    ) { innerPadding->

        PullToRefreshLayout(
            refreshStateEvent = RefreshStateEvent(
                isRefreshing = state.isProjectListLoading,
                onRefresh = projectSelectionComponent::refreshProjectList
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.projectList.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Text(
                        text = "Список проектов пуст",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }else{
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = AppTheme.Colors.DefaultPageBackgroundColor),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(top = 8.dp, start = 20.dp, end = 20.dp)
                ) {
                    items(state.projectList.size) {itemIndex ->
                        ProjectCard(state.projectList[itemIndex])
                    }
                }
            }
        }
    }
    //todo watch better way
    addProjectDialog.child?.instance?.let { dialogComponent ->
        Dialog(onDismissRequest = projectSelectionComponent::closeAddProjectDialog
        ) {
            CreateProjectDialog(dialogComponent)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ProjectSelectionScreenWithEmptyListPreview(){
    ProjectSelectionScreen(
        MockProjectSelectComponent(
            state = ProjectListStore.State(),
            topBarComponent = MockIconButtonTopBarComponent("Проекты")
        )
    )
}

@Composable
@Preview(showBackground = true)
fun ProjectSelectionScreenPreview(){
    ProjectSelectionScreen(
        MockProjectSelectComponent(
            state = ProjectListStore.State(
                projectList = listOf(
                    Project(
                        name = "project 1",
                        id = "1",
                        membersCount = 2,
                        serverID = "gdfsa",
                        role = 2
                    ),
                    Project(
                        name = "project 2",
                        id = "2",
                        membersCount = 1,
                        serverID = null,
                        role = 1
                    ),
                    Project(
                        name = "project 3",
                        id = "3",
                        membersCount = 5,
                        serverID = null,
                        role = 3
                    )
                )
            ),
            topBarComponent = MockIconButtonTopBarComponent("Проекты")
//        dialogComponent = MockAddProjectDialogComponent(
//            state = AddProjectDialogStore.State()
//        )
        )
    )
}

@Composable
@Preview(showBackground = true)
fun ProjectSelectionScreenWithDialogPreview(){
    ProjectSelectionScreen(MockProjectSelectComponent(
        state = ProjectListStore.State(
            projectList = listOf(
                Project(
                    name = "project 1",
                    id = "1",
                    membersCount = 2,
                    serverID = "gdfsa",
                    role = 2
                ),
                Project(
                    name = "project 2",
                    id = "2",
                    membersCount = 1,
                    serverID = null,
                    role = 1
                ),
                Project(
                    name = "project 3",
                    id = "3",
                    membersCount = 5,
                    serverID = null,
                    role = 3
                )
            )
        ),
        topBarComponent = MockIconButtonTopBarComponent("Проекты"),
        dialogComponent = MockAddProjectDialogComponent(
            state = AddProjectDialogStore.State()
        )
    ))
}