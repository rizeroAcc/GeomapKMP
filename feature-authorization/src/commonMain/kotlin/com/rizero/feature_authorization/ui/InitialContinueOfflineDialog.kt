package com.rizero.feature_authorization.ui

import geomapkmp.feature_authorization.generated.resources.Authorize
import geomapkmp.feature_authorization.generated.resources.ContinueOffline
import geomapkmp.feature_authorization.generated.resources.Res
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.feature_authorization.component.MockOfflineContinueDialogComponent
import com.rizero.feature_authorization.component.OfflineContinueDialogComponent

import com.rizero.shared_core_component.theme.AppTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun InitialOfflineContinueDialog(offlineContinueComponent: OfflineContinueDialogComponent){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(
                color = AppTheme.Colors.LightBlue,
                shape = RoundedCornerShape(12.dp)
            )
            .width(380.dp)
            .heightIn(80.dp,280.dp)
    ) {
        Text(
            text = getDialogTextFromCause(offlineContinueComponent.cause),
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 24.dp, horizontal = 32.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                modifier = Modifier.padding(start = 12.dp).width(160.dp).height(60.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 2.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppTheme.Colors.DefaultButtonColor),
                onClick = {
                    offlineContinueComponent.tryAuthorize()
                }
            ){
                Text(
                    text = stringResource(Res.string.Authorize),
                    textAlign = TextAlign.Center
                )
            }
            Button(
                modifier = Modifier.padding(end = 12.dp).width(160.dp).height(60.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 2.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppTheme.Colors.DefaultButtonColor),
                onClick = {
                    offlineContinueComponent.continueOffline()
                }
            ){
                Text(
                    text = stringResource(Res.string.ContinueOffline),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

fun getDialogTextFromCause(cause: OfflineContinueDialogComponent.ErrorCause) : String = when(cause) {
    //TODO MIGRATE TO STRING RESOURCES
    OfflineContinueDialogComponent.ErrorCause.TOKEN_NOT_VALID ->
        "Сессия не действительна. Авторизуйтесь или продолжите оффлайн."
    OfflineContinueDialogComponent.ErrorCause.NETWORK_UNAVAILABLE ->
        "Не удалось проверить действительность сессии.\n Сеть не доступна. Продолжить оффлайн?"
}

@Composable
@Preview(showBackground = true)
fun InitialOfflineContinueDialogPreview(){
    InitialOfflineContinueDialog(
        offlineContinueComponent = MockOfflineContinueDialogComponent()
    )
}