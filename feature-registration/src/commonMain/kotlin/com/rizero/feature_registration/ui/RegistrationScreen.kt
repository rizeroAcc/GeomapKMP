package com.rizero.feature_registration.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rizero.feature_registration.RegistrationStore
import com.rizero.feature_registration.component.MockRegistrationComponent
import com.rizero.feature_registration.component.RegistrationComponent
import com.rizero.shared_core_component.theme.AppTheme
import com.rizero.shared_core_component.ui.DefaultTextField
import com.rizero.shared_core_component.ui.InputType
import com.rizero.shared_core_component.ui.PasswordTextField
import geomapkmp.feature_registration.generated.resources.AppLogo
import geomapkmp.feature_registration.generated.resources.Res
import geomapkmp.feature_registration.generated.resources.arrow_backx
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun RegistrationScreen(registrationComponent: RegistrationComponent){
    val screenState by registrationComponent.state.collectAsState()
    Box(modifier = Modifier.fillMaxSize()){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = registrationComponent::onBackDispatch
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_backx),
                        contentDescription = "Back button",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            Image(
                painter = painterResource(Res.drawable.AppLogo),
                contentDescription = "Application logo",
                modifier = Modifier.size(250.dp)
            )
            DefaultTextField(
                inputType = InputType.Phone,
                placeholder = "Phone number",
                value = screenState.phone,
                supportingText = "Phone number",
                onValueChange = { newPhone->
                    registrationComponent.onPhoneChanged(newPhone)
                },
                modifier = Modifier.padding(top = 8.dp)
            )
            DefaultTextField(
                placeholder = "Username",
                value = screenState.username,
                supportingText = "Username",
                onValueChange = { newUsername->
                    registrationComponent.onUsernameChanged(newUsername)
                },
                modifier = Modifier.padding(top = 8.dp)
            )
            PasswordTextField(
                placeholder = "Password",
                value = screenState.password,
                supportingText = "Password",
                onValueChange = { newPassword->
                    registrationComponent.onPasswordChanged(newPassword)
                },
                modifier = Modifier.padding(top = 8.dp)
            )
            PasswordTextField(
                placeholder = "Repeated password",
                value = screenState.repeatedPassword,
                supportingText = "Repeated password",
                onValueChange = { newRepeatedPassword->
                    registrationComponent.onRepeatedPasswordChanged(newRepeatedPassword)
                },
                modifier = Modifier.padding(top = 8.dp)
            )

            screenState.error?.let {
                Text(
                    modifier = Modifier.padding(top = 12.dp),
                    text = getErrorText(it),
                    color = Color.Red
                )
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
                onClick = registrationComponent::onRegisterClick,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 0.dp,
                ),
                enabled = !screenState.performingRegistration
            ){
                Row(verticalAlignment = Alignment.CenterVertically){
                    Text("Войти")
                    if (screenState.performingRegistration){
                        HorizontalDivider(modifier = Modifier.width(12.dp))
                        CircularProgressIndicator(modifier = Modifier.size(40.dp))
                    }
                }
            }
        }
    }
}

private fun getErrorText(error: RegistrationStore.RegistrationError) : String{
    //TODO move to string resources
    return when(error){
        RegistrationStore.RegistrationError.InvalidPassword.InvalidLength ->
            "Password must have length at least 8 symbols"
        RegistrationStore.RegistrationError.InvalidPassword.PasswordBlank ->
            "Password must not be blank"
        RegistrationStore.RegistrationError.InvalidPassword.RepeatedPasswordNotMatches ->
            "Password and repeated password must match"
        RegistrationStore.RegistrationError.InvalidPhoneFormat.InvalidPhoneLength ->
            "Invalid phone length"
        RegistrationStore.RegistrationError.InvalidPhoneFormat.PhoneIsNotRussian ->
            "Phone must match russian standart "
        RegistrationStore.RegistrationError.InvalidUsername.BlankUsername ->
            "Username must not be blank"
        RegistrationStore.RegistrationError.NetworkError ->
            "Network unavailable"
        RegistrationStore.RegistrationError.ServerError ->
            "Server error. Try later"
        RegistrationStore.RegistrationError.UserAlreadyRegistered ->
            "User with entered phone already registered"
    }
}

@Composable
@Preview(showBackground = true)
fun RegistrationScreenPreview(){
    RegistrationScreen(
        registrationComponent = MockRegistrationComponent(
            state = RegistrationStore.State(
                error = RegistrationStore.RegistrationError.NetworkError
            )
        )
    )

}