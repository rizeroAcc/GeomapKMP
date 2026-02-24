package com.rizero.geomapkmp

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceAll
import com.rizero.geomapkmp.flow.authentication.AuthenticationFlowComponent
import com.rizero.geomapkmp.flow.project.ProjectFlowComponent
import kotlinx.serialization.Serializable

class RootComponent(
    componentContext: ComponentContext,
    private val authenticationFlowComponentFactory: AuthenticationFlowComponent.ComponentFactory,
    private val projectFlowComponentFactory: ProjectFlowComponent.ComponentFactory
) : ComponentContext by componentContext{
    private val navigation = StackNavigation<ScreenConfig>()
    val stack = childStack(
        source = navigation,
        serializer = ScreenConfig.serializer(),
        initialConfiguration = ScreenConfig.Authentication,
        handleBackButton = true,
        childFactory = ::createChild
    )

    fun startProjectFlow(){
        navigation.replaceAll(ScreenConfig.Project)
    }
    fun resetUserSession(){
        navigation.replaceAll(ScreenConfig.Authentication)
    }
    fun createChild(
        config: ScreenConfig,
        componentContext: ComponentContext
    ) : Child{
        return when(config){
            ScreenConfig.Authentication -> Child.Authentication(
                authenticationFlowComponentFactory(
                    componentContext = componentContext,
                    onAuthorizationComplete = ::startProjectFlow
                )
            )
            ScreenConfig.Project -> Child.Project(
                projectFlowComponentFactory(
                    componentContext,
                    logOutCallback = ::resetUserSession
                )
            )
        }
    }

    @Serializable
    sealed interface Child{
        class Authentication(val flow: AuthenticationFlowComponent) : Child
        class Project(val flow: ProjectFlowComponent) : Child
    }

    @Serializable
    sealed interface ScreenConfig{
        data object Authentication : ScreenConfig
        data object Project : ScreenConfig
    }

}