package com.rizero.feature_user_profile.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.feature_user_profile.component.MockUserProfileComponent
import com.rizero.feature_user_profile.component.UserProfileComponent
import com.rizero.shared_core_component.decompose.ui.BackButtonTopAppBar
import com.rizero.shared_core_component.theme.AppTheme
import geomapkmp.feature_user_profile.generated.resources.Res
import geomapkmp.feature_user_profile.generated.resources.account_circle
import geomapkmp.feature_user_profile.generated.resources.arrow_backx
import org.jetbrains.compose.resources.painterResource

@Composable
fun UserProfileScreen(userProfileComponent: UserProfileComponent){
    val tobBarPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
    val topBarComponent = userProfileComponent.topBarComponent
    Scaffold(
        topBar = {
            BackButtonTopAppBar(topBarComponent)
        },
    ){ innerPadding->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .background(color = AppTheme.Colors.DefaultPageBackgroundColor)
                .padding(top = 32.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            IconButton(
                modifier = Modifier.size(180.dp),
                onClick = {}
            ) {
                Image(
                    modifier = Modifier.size(180.dp),
                    painter = painterResource(Res.drawable.account_circle),
                    contentDescription = ""
                )
            }
            Row(horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "Username",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(200.dp)
                    .padding(top = 20.dp)
                    .border(
                        color = AppTheme.Colors.LightBlue,
                        shape = RoundedCornerShape(16.dp),
                        width = 1.dp
                    )
            ) {
                ProfileMenuItem(
                    itemText = "Изменить пароль",
                    modifier = Modifier.padding(top = 8.dp).height(40.dp)
                )
                ProfileMenuItem(
                    itemText = "Редактировать имя пользователя",
                    modifier = Modifier.height(40.dp)
                )
            }
        }
    }
}

@Composable
@Preview
fun UserProfileScreenPreview(){
    UserProfileScreen(MockUserProfileComponent())
}