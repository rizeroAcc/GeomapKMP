package com.rizero.geomapkmp.flow.authentication

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.rizero.feature_authorization.component.AuthorizationComponent
import com.rizero.feature_registration.component.RegistrationComponent
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Single

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
        initialConfiguration = ScreenConfig.Authorization,
        handleBackButton = true,
        childFactory = ::createChild
    )

    fun onRegistrationClick(){
        navigation.pushNew(configuration = ScreenConfig.Registration)
    }
    fun onRegistrationBackClick(){
        navigation.pop()
    }

    fun onRegistrationComplete(){
        navigation.pop()
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
                onAuthorizationComplete = ::_onAuthorizationComplete,
                onRegistrationClick = ::onRegistrationClick,
            )
        )
        is ScreenConfig.Registration -> Child.Registration(
            registrationComponentFactory(
                componentContext = newComponentContext,
                onRegistrationCompleted = ::onRegistrationComplete,
                onBackClick = ::onRegistrationBackClick,
            )
        )
    }

    @Serializable
    sealed interface ScreenConfig {
        @Serializable
        data object Authorization : ScreenConfig
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