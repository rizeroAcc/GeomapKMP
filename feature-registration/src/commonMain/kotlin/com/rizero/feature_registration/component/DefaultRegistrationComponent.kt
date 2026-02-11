package com.rizero.feature_registration.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.rizero.feature_registration.RegistrationStore
import com.rizero.feature_registration.RegistrationStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Single

class DefaultRegistrationComponent (
    componentContext : ComponentContext,
    private val storeFactory: StoreFactory = DefaultStoreFactory(),
    val onRegistrationCompleted : ()->Unit,
    val onBackButtonAction : ()->Unit
) : RegistrationComponent, ComponentContext by componentContext {

    private val backCallback = BackCallback {
        onBackButtonAction()
    }
    private val componentScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    val store = instanceKeeper.getStore {
        RegistrationStoreFactory(storeFactory).create()
    }

    init {
        lifecycle.doOnDestroy {
            componentScope.cancel()
            backHandler.unregister(callback = backCallback)
        }
        componentScope.launch {
            store.labels.collect { label ->
                when(label){
                    RegistrationStore.Label.RegistrationComplete -> {
                        onRegistrationCompleted()
                    }
                }
            }
        }
        backHandler.register(backCallback)
    }



    @OptIn(ExperimentalCoroutinesApi::class)
    override val state = store.stateFlow

    override fun onRegisterClick() {
        store.accept(RegistrationStore.Intent.Register)
    }

    override fun onPhoneChanged(newPhone : String) {
        store.accept(RegistrationStore.Intent.PhoneChanged(newPhone))
    }

    override fun onUsernameChanged(newUsername: String) {
        store.accept(RegistrationStore.Intent.UsernameChanged(newUsername))
    }

    override fun onPasswordChanged(newPassword: String) {
        store.accept(RegistrationStore.Intent.PasswordChanged(newPassword))
    }

    override fun onRepeatedPasswordChanged(newRepeatedPassword: String) {
        store.accept(RegistrationStore.Intent.RepeatedPasswordChanged(newRepeatedPassword))
    }

    override fun onBackDispatch() {
        onBackButtonAction()
    }

    @Factory
    class ComponentFactory() : RegistrationComponent.Factory{
        override fun invoke(
            componentContext: ComponentContext,
            onRegistrationCompleted: () -> Unit,
            onBackClick: () -> Unit
        ): RegistrationComponent = DefaultRegistrationComponent(
            componentContext = componentContext,
            onRegistrationCompleted = onRegistrationCompleted,
            onBackButtonAction = onBackClick,
        )

    }
}