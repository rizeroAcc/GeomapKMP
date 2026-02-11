package com.rizero.geomapkmp.flow.project

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.pushNew
import com.rizero.feature_project_select.component.ProjectSelectComponent
import com.rizero.feature_user_profile.component.DefaultUserProfileComponent
import com.rizero.feature_user_profile.component.UserProfileComponent
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Single

class ProjectFlowComponent(
    componentContext: ComponentContext,
    private val projectSelectionComponentFactory: ProjectSelectComponent.Factory,
    private val userProfileComponentFactory: UserProfileComponent.Factory
) : ComponentContext by componentContext {

    val navigation = StackNavigation<ScreenConfig>()
    val stack = childStack(
        source = navigation,
        serializer = ScreenConfig.serializer(),
        initialConfiguration = ScreenConfig.ProjectSelection,
        handleBackButton = true,
        childFactory = ::createChild
    )

    fun createChild(
        screenConfig: ScreenConfig,
        componentContext: ComponentContext
    ) = when(screenConfig) {
        ScreenConfig.ProjectSelection -> Child.ProjectSelectionPage(
            projectSelectionComponentFactory(
                componentContext,
                onProfileIconClick = ::openUserProfile
            )
        )
        ScreenConfig.UserProfile -> Child.UserProfile(
            userProfileComponentFactory(
                componentContext,
                backNavigateCallback = ::navigateBack
            )
        )
    }

    fun openUserProfile(){
        navigation.pushNew(ScreenConfig.UserProfile)
    }

    fun navigateBack(){
        navigation.pop()
    }

    @Serializable
    sealed interface ScreenConfig{
        @Serializable
        data object ProjectSelection : ScreenConfig
        @Serializable
        data object UserProfile : ScreenConfig
    }

    sealed interface Child{
        class ProjectSelectionPage(val projectSelectionComponent : ProjectSelectComponent) : Child
        class UserProfile(val userProfileComponent: UserProfileComponent) : Child
    }

    @Factory
    class ComponentFactory(
        private val projectSelectionComponentFactory: ProjectSelectComponent.Factory,
        private val userProfileComponentFactory: UserProfileComponent.Factory,
    ){
        operator fun invoke(componentContext : ComponentContext) : ProjectFlowComponent = ProjectFlowComponent(
            componentContext = componentContext,
            projectSelectionComponentFactory = projectSelectionComponentFactory,
            userProfileComponentFactory = userProfileComponentFactory,
        )
    }
}