package com.rizero.geomapkmp.flow.authentication

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceAll
import com.rizero.feature_authorization.component.AuthorizationComponent
import com.rizero.feature_authorization.component.InitialSessionLoadComponent
import com.rizero.feature_registration.component.RegistrationComponent
import com.rizero.geomapkmp.flow.authentication.AuthenticationFlowComponent.Child.*
import com.rizero.shared_core_data.model.Session
import com.rizero.shared_core_data.model.UserModel
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Factory

class AuthenticationFlowComponent (
    componentContext: ComponentContext,
    private val initialSessionLoadComponentFactory: InitialSessionLoadComponent.Factory,
    private val authorizationComponentFactory: AuthorizationComponent.Factory,
    private val registrationComponentFactory: RegistrationComponent.Factory,
    private val authorizationCompleteCallback : (session : Session) ->Unit
) : ComponentContext by componentContext{
    private val navigation = StackNavigation<ScreenConfig>()
    val stack = childStack(
        source = navigation,
        serializer = ScreenConfig.serializer(),
        initialConfiguration = ScreenConfig.InitialLoad,
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
    fun onAuthorizationComplete(session: Session){
        authorizationCompleteCallback(session)
    }

    fun openAuthorization(oldSession: Session? = null){
        navigation.replaceAll(ScreenConfig.Authorization(oldSession?.user?.phone))
    }

    fun createChild(
        config: ScreenConfig,
        newComponentContext: ComponentContext
    ) = when(config){
        is ScreenConfig.Authorization -> Authorization(
            authorizationComponentFactory(
                componentContext = newComponentContext,
                authorizationCompleteCallback = { session->
                    onAuthorizationComplete(session)
                },
                navigateToRegistration = ::onRegistrationClick,
                userPhone = config.presavedUserPhone
            )
        )
        is ScreenConfig.Registration -> Registration(
            registrationComponentFactory(
                componentContext = newComponentContext,
                onRegistrationCompleted = { registeredUser->
                    onRegistrationComplete(registeredUser)
                },
                navigateBack = ::onRegistrationBackClick,
            )
        )

        ScreenConfig.InitialLoad -> InitialLoadPage(
            initialSessionLoadComponentFactory(
                componentContext = newComponentContext,
                authorizedCallback = { session, isRefreshed ->
                    onAuthorizationComplete(session)
                },
                onOfflineContinue = { session ->
                    onAuthorizationComplete(session)
                },
                navigateToAuthorization = { session ->
                    openAuthorization(session)
                }
            )
        )
    }

    @Serializable
    sealed interface ScreenConfig {
        @Serializable
        data object InitialLoad : ScreenConfig
        @Serializable
        data class Authorization(
            val presavedUserPhone : String? = null
        ) : ScreenConfig
        @Serializable
        data object Registration : ScreenConfig
    }

    sealed interface Child {
        class InitialLoadPage(val initialSessionLoadComponent: InitialSessionLoadComponent) : Child
        class Authorization(val authorizationComponent: AuthorizationComponent) : Child
        class Registration(val registrationComponent: RegistrationComponent) : Child
    }

    @Factory
    class ComponentFactory(
        private val initialSessionLoadComponentFactory: InitialSessionLoadComponent.Factory,
        private val authorizationComponentFactory: AuthorizationComponent.Factory,
        private val registrationComponentFactory: RegistrationComponent.Factory,
    ){
        operator fun invoke(
            componentContext: ComponentContext,
            onAuthorizationComplete: (session : Session) -> Unit
        ) : AuthenticationFlowComponent = AuthenticationFlowComponent(
            componentContext = componentContext,
            authorizationComponentFactory = authorizationComponentFactory,
            registrationComponentFactory = registrationComponentFactory,
            initialSessionLoadComponentFactory = initialSessionLoadComponentFactory,
            authorizationCompleteCallback = onAuthorizationComplete
        )
    }
}