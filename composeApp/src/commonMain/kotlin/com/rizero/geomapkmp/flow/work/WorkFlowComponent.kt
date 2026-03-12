package com.rizero.geomapkmp.flow.work

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.rizero.feature_project_mapview.component.MapScreenComponent
import com.rizero.geomapkmp.flow.work.WorkFlowComponent.ScreenConfig
import com.rizero.shared_core_data.model.Project
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Single

class WorkFlowComponent(
    val project: Project,
    componentContext: ComponentContext,
) : ComponentContext by componentContext{

    val navigation = StackNavigation<ScreenConfig>()
    val stack = childStack(
        source = navigation,
        serializer = ScreenConfig.serializer(),
        initialConfiguration = ScreenConfig.Map(project),
        handleBackButton = true,
        childFactory = ::createChild
    )

    fun createChild(
        screenConfig: ScreenConfig,
        componentContext: ComponentContext
    ) = when(screenConfig){
        is ScreenConfig.Map -> Child.Map(
            //todo make normal
            object : MapScreenComponent{}
        )
    }

    @Single
    class ComponentFactory(){
        operator fun invoke(
            componentContext: ComponentContext,
            project: Project
        ) : WorkFlowComponent =
            WorkFlowComponent(
                componentContext = componentContext,
                project = project
            )
    }

    sealed interface Child {
        class Map(val mapComponent : MapScreenComponent) : Child
    }

    @Serializable
    sealed interface ScreenConfig{
        data class Map(val project: Project) : ScreenConfig
    }
}