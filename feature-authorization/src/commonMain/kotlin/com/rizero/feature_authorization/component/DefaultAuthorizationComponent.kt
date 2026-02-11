package com.rizero.feature_authorization.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.rizero.feature_authorization.AuthorizationStore
import com.rizero.feature_authorization.AuthorizationStoreFactory
import com.rizero.shared_core_data.repository.SessionRepository
import com.rizero.shared_core_data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

class DefaultAuthorizationComponent(
    componentContext : ComponentContext,
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository,
    private val storeFactory: StoreFactory = DefaultStoreFactory(),
    private val _onRegistrationClick : ()-> Unit,
    private val onAuthorizationComplete : () -> Unit,
) : AuthorizationComponent,ComponentContext by componentContext {
    val scope = coroutineScope(Dispatchers.Main)

    init {
        scope.launch {
            labels.collect { label->
                when (label) {
                    is AuthorizationStore.Label.SuccessfulLogIn -> {
                        onAuthorizationComplete()
                    }
                }
            }
        }
    }
    private val store : AuthorizationStore = instanceKeeper.getStore {
        AuthorizationStoreFactory(
            storeFactory = storeFactory,
            sessionRepository = sessionRepository,
            userRepository = userRepository
        ).create()
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    override val stateFlow = store.stateFlow(lifecycle)
    override val labels = store.labels
    override fun onLoginChanged(newLogin : String){
        store.accept(AuthorizationStore.Intent.ChangePhone(newLogin))
    }
    override fun onPasswordChanged(newPassword : String){
        store.accept(AuthorizationStore.Intent.ChangePassword(newPassword))
    }


    override fun onRegistrationClick() {
        _onRegistrationClick()
    }

    override fun onLogInClick() {
        store.accept(AuthorizationStore.Intent.Authorize)
    }

    @Factory
    class ComponentFactory(
        private val sessionRepository: SessionRepository,
        private val userRepository: UserRepository,
    ) : AuthorizationComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            onAuthorizationComplete: () -> Unit,
            onRegistrationClick: () -> Unit
        ): AuthorizationComponent = DefaultAuthorizationComponent(
            componentContext = componentContext,
            sessionRepository = sessionRepository,
            userRepository = userRepository,
            _onRegistrationClick = onRegistrationClick,
            onAuthorizationComplete = onAuthorizationComplete,
        )
    }
}