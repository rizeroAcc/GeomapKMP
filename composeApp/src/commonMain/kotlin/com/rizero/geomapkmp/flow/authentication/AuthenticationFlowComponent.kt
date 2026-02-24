package com.rizero.geomapkmp.flow.authentication

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceAll
import com.rizero.feature_authorization.component.AuthorizationComponent
import com.rizero.feature_registration.component.RegistrationComponent
import com.rizero.shared_core_data.model.UserModel
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Factory

class AuthenticationFlowComponent (
    componentContext: ComponentContext,
    private val authorizationComponentFactory: AuthorizationComponent.Factory,
    private val registrationComponentFactory: RegistrationComponent.Factory,
    private val onAuthorizationComplete : () ->Unit
) : ComponentContext by componentContext{
    private val navigation = StackNavigation<ScreenConfig>()
    val stack = childStack(
        source = navigation,
        serializer = ScreenConfig.serializer(),
        initialConfiguration = ScreenConfig.Authorization(),
        handleBackButton = true,
        childFactory = ::createChild
    )

    fun onRegistrationClick(){
        navigation.pushNew(configuration = ScreenConfig.Registration)
    }
    fun onRegistrationBackClick(){
        navigation.pop()
    }

    fun onRegistrationComplete(user : UserModel){
        navigation.replaceAll(ScreenConfig.Authorization(user.phone))
    }
    fun _onAuthorizationComplete(){
        onAuthorizationComplete()
    }
    fun createChild(
        config: ScreenConfig,
        newComponentContext: ComponentContext
    ) = when(config){
        is ScreenConfig.Authorization -> Child.Authorization(
            authorizationComponentFactory(
                componentContext = newComponentContext,
                authorizationCompleteCallback = ::_onAuthorizationComplete,
                navigateToRegistration = ::onRegistrationClick,
                userPhone = config.presavedUserPhone
            )
        )
        is ScreenConfig.Registration -> Child.Registration(
            registrationComponentFactory(
                componentContext = newComponentContext,
                onRegistrationCompleted = { registeredUser->
                    onRegistrationComplete(registeredUser)
                },
                navigateBack = ::onRegistrationBackClick,
            )
        )
    }

    @Serializable
    sealed interface ScreenConfig {
        @Serializable
        data class Authorization(
            val presavedUserPhone : String? = null
        ) : ScreenConfig
        @Serializable
        data object Registration : ScreenConfig
    }

    sealed interface Child {
        class Authorization(val authorizationComponent: AuthorizationComponent) : Child
        class Registration(val registrationComponent: RegistrationComponent) : Child
    }

    @Factory
    class ComponentFactory(
        private val authorizationComponentFactory: AuthorizationComponent.Factory,
        private val registrationComponentFactory: RegistrationComponent.Factory,
    ){
        operator fun invoke(
            componentContext: ComponentContext,
            onAuthorizationComplete: () -> Unit
        ) : AuthenticationFlowComponent = AuthenticationFlowComponent(
            componentContext = componentContext,
            authorizationComponentFactory = authorizationComponentFactory,
            registrationComponentFactory = registrationComponentFactory,
            onAuthorizationComplete = onAuthorizationComplete
        )
    }
}