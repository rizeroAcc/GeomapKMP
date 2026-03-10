package com.rizero.feature_authorization.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.value.getValue
import com.rizero.feature_authorization.SessionLoadStore
import com.rizero.feature_authorization.component.InitialSessionLoadComponent
import com.rizero.feature_authorization.component.MockInitialSessionLoadComponent
import com.rizero.feature_authorization.component.MockOfflineContinueDialogComponent
import com.rizero.shared_core_component.theme.AppTheme

@Composable
fun InitialSessionLoadScreen(loadComponent: InitialSessionLoadComponent){
    val state by loadComponent.stateFlow.collectAsState()
    val continueOfflineDialog by loadComponent.continueOfflineDialog.subscribeAsState()
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(color = AppTheme.Colors.DefaultPageBackgroundColor)
            .fillMaxSize(),
    ) {
        if (state.loadingStatus !is SessionLoadStore.State.LoadingStatus.LoadingFinished) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(16.dp)
                    .size(200.dp)
            )
        }
        Text(
            text = getTextFromLoadingStage(state.loadingStatus),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
    continueOfflineDialog.child?.let {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ){
            InitialOfflineContinueDialog(it.instance)
        }
    }
}

fun getTextFromLoadingStage(stage: SessionLoadStore.State.LoadingStatus) : String{
    //todo migrate to resources
    return when(stage) {
        SessionLoadStore.State.LoadingStatus.LoadingCachedSession -> "Поиск сохраненной сессии..."
        SessionLoadStore.State.LoadingStatus.CheckingSessionValid -> "Проверка актуальности сессии..."
        is SessionLoadStore.State.LoadingStatus.RefreshingSession -> "Обновление сессии..."
        SessionLoadStore.State.LoadingStatus.LoadingFinished.CachedSessionInvalid -> "Сохраненная сессия не действительна"
        SessionLoadStore.State.LoadingStatus.LoadingFinished.NetworkErrorDuringRefreshing -> "Не удалось обновить сессию"
        SessionLoadStore.State.LoadingStatus.LoadingFinished.NetworkErrorDuringValidating -> "Не удалось проверить актуальность сессии"
        SessionLoadStore.State.LoadingStatus.LoadingFinished.NoCachedSession -> "Сохраненная сессия не найдена"
        is SessionLoadStore.State.LoadingStatus.LoadingFinished.Success -> "Сохраненная сессия действительна"

    }
}

@Composable
@Preview
fun InitialSessionLoadScreenPreview(){
    InitialSessionLoadScreen(MockInitialSessionLoadComponent())
}

@Composable
@Preview
fun InitialSessionLoadScreenWithDialogPreview(){
    InitialSessionLoadScreen(MockInitialSessionLoadComponent(
        MockOfflineContinueDialogComponent()
    ))
}