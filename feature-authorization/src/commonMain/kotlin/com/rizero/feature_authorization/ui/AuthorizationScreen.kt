package com.rizero.feature_authorization.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.feature_authorization.AuthorizationStore
import com.rizero.feature_authorization.component.AuthorizationComponent
import com.rizero.feature_authorization.component.MockAuthorizationComponent
import com.rizero.shared_core_component.theme.AppTheme
import com.rizero.shared_core_component.ui.DefaultTextField
import com.rizero.shared_core_component.ui.InputType
import com.rizero.shared_core_component.ui.PasswordTextField
import geomapkmp.feature_authorization.generated.resources.AppLogo
import geomapkmp.feature_authorization.generated.resources.Res
import org.jetbrains.compose.resources.painterResource
//TODO Вынести в stringResources
@Composable
fun AuthorizationScreen(authorizationComponent: AuthorizationComponent){
    val screenState by authorizationComponent.stateFlow.collectAsState()
    Box(modifier = Modifier.fillMaxSize()){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.AppLogo),
                contentDescription = "Application logo",
                modifier = Modifier.size(200.dp)
            )
            DefaultTextField(
                inputType = InputType.Phone,
                placeholder = "Phone number",
                value = screenState.phoneNumber,
                supportingText = "Phone number",
                onValueChange = { newValue->
                    authorizationComponent.onLoginChanged(newValue)
                },
                modifier = Modifier.padding(top = 8.dp)
            )
            PasswordTextField(
                placeholder = "password",
                value = screenState.password,
                supportingText = "Password",
                onValueChange = { newValue->
                    authorizationComponent.onPasswordChanged(newValue)
                },
                modifier = Modifier.padding(top = 8.dp)
            )

            Column(
                modifier = Modifier.padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row() {
                    Text(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        text = "Нет аккаунта?"
                    )
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .clickable(
                                interactionSource = null,
                                onClick = authorizationComponent::onRegistrationClick,
                                indication = ripple()
                            ),
                        fontSize = 16.sp,
                        color = Color.Blue,
                        fontWeight = FontWeight.Bold,
                        text = "Зарегистрироваться"
                    )
                }
                ErrorText(screenState.error)
            }

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppTheme.Colors.DefaultButtonColor
                ),
                modifier = Modifier
                    .padding(top = 80.dp)
                    .widthIn(min = 120.dp, max = 220.dp)
                    .height(60.dp)
                    .fillMaxWidth()
                ,
                onClick = authorizationComponent::onLogInClick,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 0.dp,
                ),
                enabled = !screenState.authorizationInProcess
            ){
                Row(verticalAlignment = Alignment.CenterVertically){
                    Text("Войти")
                    if (screenState.authorizationInProcess){
                        HorizontalDivider(modifier = Modifier.width(12.dp))
                        CircularProgressIndicator(modifier = Modifier.size(40.dp))
                    }
                }
            }
        }

    }
}

@Composable
fun ErrorText(error : AuthorizationStore.AuthorizationError?){
    Text(
        modifier = Modifier.padding(top = 8.dp),
        text = error?.let {
            when(error){
                AuthorizationStore.AuthorizationError.IncorrectPasswordInput -> ""
                AuthorizationStore.AuthorizationError.IncorrectPhoneInput -> "Incorrect phone number format"
                AuthorizationStore.AuthorizationError.InvalidCredentials -> "Incorrect phone or password"
                AuthorizationStore.AuthorizationError.NetworkUnavailable -> "Network unavailable"
                AuthorizationStore.AuthorizationError.ServerError -> "Server unavailable. Try later."
            }
        } ?: "",
        color = Color.Red,
        fontSize = 16.sp
    )
}

@Composable
@Preview(showBackground = true)
fun AuthorizationScreenPreview(){
    AuthorizationScreen(MockAuthorizationComponent(
        state = AuthorizationStore.State(
            phoneNumber = "89036559989",
            password = "password",
        )
    )
    )
}

@Composable
@Preview(showBackground = true)
fun AuthorizationScreenWithErrorPreview(){
    AuthorizationScreen(
        MockAuthorizationComponent(
            state = AuthorizationStore.State(
                phoneNumber = "89036559989",
                password = "password",
                error = AuthorizationStore.AuthorizationError.ServerError,
            )
        )
    )
}