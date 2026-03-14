package com.rizero.geomapkmp

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.errorhandler.onDecomposeError
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceAll
import com.rizero.geomapkmp.RootComponent.Child.*
import com.rizero.geomapkmp.flow.authentication.AuthenticationFlowComponent
import com.rizero.geomapkmp.flow.project.ProjectFlowComponent
import com.rizero.geomapkmp.flow.work.WorkFlowComponent
import com.rizero.shared_core_data.model.Session
import kotlinx.serialization.Serializable

class RootComponent(
    componentContext: ComponentContext,
    private val authenticationFlowComponentFactory: AuthenticationFlowComponent.ComponentFactory,
    private val projectFlowComponentFactory: ProjectFlowComponent.ComponentFactory,
    private val workFlowComponentFactory: WorkFlowComponent.ComponentFactory,
) : ComponentContext by componentContext{
    private val navigation = StackNavigation<ScreenConfig>()
    val stack = childStack(
        source = navigation,
        serializer = ScreenConfig.serializer(),
        initialConfiguration = ScreenConfig.Authentication,
        handleBackButton = true,
        childFactory = ::createChild
    )

    fun startWorkFlow(project: com.rizero.shared_core_data.model.Project){
        navigation.replaceAll(ScreenConfig.Work(project))
    }
    fun startProjectFlow(session: Session){
        navigation.replaceAll(ScreenConfig.Project(session))
    }
    fun resetToAuthenticationFlow(skipInitial : Boolean = false){
        navigation.replaceAll(ScreenConfig.Authentication)
    }
    fun createChild(
        config: ScreenConfig,
        componentContext: ComponentContext
    ) : Child{
        return when(config){
            is ScreenConfig.Authentication -> Authentication(
                authenticationFlowComponentFactory(
                    componentContext = componentContext,
                    onAuthorizationComplete = ::startProjectFlow
                )
            )
            is ScreenConfig.Project -> {
                Project(
                    projectFlowComponentFactory(
                        componentContext,
                        session = config.session,
                        logOutCallback = ::resetToAuthenticationFlow,
                        onSessionExpiredCallback = {
                            //todo решить чето с сессией
                        },
                        onProjectSelected = { project ->
                            startWorkFlow(project)
                        }
                    )
                )
            }

            is ScreenConfig.Work -> {
                Work(workFlowComponentFactory(
                    componentContext = componentContext,
                    project = config.project
                ))
            }
        }
    }

    @Serializable
    sealed interface Child{
        class Authentication(val flow: AuthenticationFlowComponent) : Child
        class Project(val flow: ProjectFlowComponent) : Child

        class  Work(val flow : WorkFlowComponent) : Child
    }

    @Serializable
    sealed interface ScreenConfig{
        data object Authentication : ScreenConfig
        data class Project(
            val session: Session
        ) : ScreenConfig

        data class Work(
            val project: com.rizero.shared_core_data.model.Project
        ) : ScreenConfig
    }

}