package com.rizero.feature_authorization.component

import com.arkivanov.decompose.ComponentContext
import com.rizero.feature_authorization.AuthorizationStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface AuthorizationComponent {
    val stateFlow : StateFlow<AuthorizationStore.State>
    val labels : Flow<AuthorizationStore.Label>
    fun onLoginChanged(newLogin : String)
    fun onPasswordChanged(newPassword : String)
    fun onRegistrationClick()
    fun onLogInClick()

    fun interface Factory {
        operator fun invoke( // 2
            componentContext: ComponentContext,
            onAuthorizationComplete: () -> Unit,
            onRegistrationClick: () -> Unit,
        ): AuthorizationComponent
    }

}

class MockAuthorizationComponent(val state : AuthorizationStore.State): AuthorizationComponent {
    override val stateFlow = MutableStateFlow(
        state
    )
    override val labels: StateFlow<AuthorizationStore.Label>
        get() = MutableStateFlow(
            AuthorizationStore.Label.SuccessfulLogIn
        )
    override fun onLoginChanged(newLogin: String) {}
    override fun onPasswordChanged(newPassword: String) {}
    override fun onRegistrationClick() {}
    override fun onLogInClick() {}
}