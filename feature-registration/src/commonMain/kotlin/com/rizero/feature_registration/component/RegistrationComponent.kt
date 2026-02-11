package com.rizero.feature_registration.component

import com.arkivanov.decompose.ComponentContext
import com.rizero.feature_registration.RegistrationStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface RegistrationComponent {
    val state : StateFlow<RegistrationStore.State>
    fun onRegisterClick()
    fun onPhoneChanged(newPhone : String)
    fun onUsernameChanged(newUsername : String)
    fun onPasswordChanged(newPassword : String)
    fun onRepeatedPasswordChanged(newRepeatedPassword : String)
    fun onBackDispatch()

    fun interface Factory{
        operator fun invoke(
            componentContext : ComponentContext,
            onRegistrationCompleted : ()->Unit,
            onBackClick : ()->Unit,
        ) : RegistrationComponent
    }
}

class MockRegistrationComponent(state : RegistrationStore.State) : RegistrationComponent{
    override val state: StateFlow<RegistrationStore.State> = MutableStateFlow(
        state
    )
    override fun onRegisterClick() = Unit
    override fun onPhoneChanged(newPhone: String) = Unit
    override fun onUsernameChanged(newUsername: String) = Unit
    override fun onPasswordChanged(newPassword: String) = Unit
    override fun onRepeatedPasswordChanged(newRepeatedPassword: String) = Unit
    override fun onBackDispatch() = Unit
}