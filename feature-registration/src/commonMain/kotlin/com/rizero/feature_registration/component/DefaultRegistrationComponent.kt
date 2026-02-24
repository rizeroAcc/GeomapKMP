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
import com.rizero.shared_core_data.model.UserModel
import com.rizero.shared_core_data.repository.SessionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

class DefaultRegistrationComponent (
    componentContext : ComponentContext,
    private val storeFactory: StoreFactory = DefaultStoreFactory(),
    private val sessionRepository: SessionRepository,
    val onRegistrationCompleted : (user : UserModel) -> Unit,
    val navigateBack : ()->Unit
) : RegistrationComponent, ComponentContext by componentContext {

    private val backCallback = BackCallback {
        navigateBack()
    }
    private val componentScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    val store = instanceKeeper.getStore {
        RegistrationStoreFactory(
            storeFactory = storeFactory,
            sessionRepository = sessionRepository
        ).create()
    }

    init {
        lifecycle.doOnDestroy {
            componentScope.cancel()
            backHandler.unregister(callback = backCallback)
        }
        componentScope.launch {
            store.labels.collect { label ->
                when(label){
                    is RegistrationStore.Label.RegistrationComplete -> {
                        onRegistrationCompleted(
                            label.registeredUser
                        )
                    }
                }
            }
        }
        backHandler.register(backCallback)
    }



    @OptIn(ExperimentalCoroutinesApi::class)
    override val state = store.stateFlow

    override fun onRegisterClick() {
        store.accept(RegistrationStore.Intent.PerformRegister)
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
        navigateBack()
    }

    @Factory
    class ComponentFactory(val sessionRepository: SessionRepository) : RegistrationComponent.Factory{
        override fun invoke(
            componentContext: ComponentContext,
            onRegistrationCompleted: (user : UserModel) -> Unit,
            navigateBack: () -> Unit
        ): RegistrationComponent = DefaultRegistrationComponent(
            sessionRepository = sessionRepository,
            componentContext = componentContext,
            onRegistrationCompleted = onRegistrationCompleted,
            navigateBack = navigateBack,
        )

    }
}