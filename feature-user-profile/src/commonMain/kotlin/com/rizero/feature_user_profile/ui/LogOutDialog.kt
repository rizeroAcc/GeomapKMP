package com.rizero.feature_user_profile.ui

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.feature_user_profile.component.LogOutDialogComponent
import com.rizero.feature_user_profile.component.MockkLogOutDialogComponent
import com.rizero.feature_user_profile.store.LogOutDialogStore
import com.rizero.shared_core_component.theme.AppTheme
import geomapkmp.feature_user_profile.generated.resources.Cancel
import geomapkmp.feature_user_profile.generated.resources.Exit
import geomapkmp.feature_user_profile.generated.resources.LocalLogOutQuestion
import geomapkmp.feature_user_profile.generated.resources.LogOutQuestion
import geomapkmp.feature_user_profile.generated.resources.Res
import org.jetbrains.compose.resources.stringResource


@Composable
fun LogOutDialog(logOutDialogComponent: LogOutDialogComponent){
    val state by logOutDialogComponent.stateFlow.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(
                color = AppTheme.Colors.LightBlue,
                shape = RoundedCornerShape(12.dp)
            )
            .width(380.dp)
            .heightIn(80.dp,200.dp)
    ) {
        Text(
            text = if (state.suggestLocalLogOut)
                stringResource(Res.string.LocalLogOutQuestion)
            else
                stringResource(Res.string.LogOutQuestion)
            ,
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
                modifier = Modifier.padding(start = 20.dp).width(140.dp).height(60.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 2.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppTheme.Colors.DefaultButtonColor),
                onClick = {
                    logOutDialogComponent.cancel()
                }
            ){
                Text(stringResource(Res.string.Cancel))
            }
            Button(
                modifier = Modifier.padding(end = 20.dp).width(140.dp).height(60.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 2.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppTheme.Colors.DefaultButtonColor),
                onClick = {
                    if (state.suggestLocalLogOut){
                        logOutDialogComponent.performLocalLogOut()
                    }else{
                        logOutDialogComponent.performLogOut()
                    }
                }
            ){
                Text(stringResource(Res.string.Exit))
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun LogOutDialogPreview(){
    val component = MockkLogOutDialogComponent()
    LogOutDialog(component)
}