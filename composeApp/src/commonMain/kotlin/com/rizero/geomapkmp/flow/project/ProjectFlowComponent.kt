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
import com.rizero.shared_core_data.model.Project
import com.rizero.shared_core_data.model.Session
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Single

class ProjectFlowComponent(
    componentContext: ComponentContext,
    val session: Session,
    val logOutCallback : ()-> Unit,
    val onSessionExpiredCallback : (Session)-> Unit,
    private val onProjectSelected : (Project)-> Unit,
    private val projectSelectionComponentFactory: ProjectSelectComponent.Factory,
    private val userProfileComponentFactory: UserProfileComponent.Factory,
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
                session = session,
                componentContext,
                onProfileIconClick = ::openUserProfile,
                onSessionExpired =  onSessionExpiredCallback,
                onProjectSelected = onProjectSelected,
            )
        )
        ScreenConfig.UserProfile -> Child.UserProfile(
            userProfileComponentFactory(
                componentContext,
                backNavigateCallback = ::navigateBack,
                onUserLogOut = logOutCallback
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

    @Single
    class ComponentFactory(
        private val projectSelectionComponentFactory: ProjectSelectComponent.Factory,
        private val userProfileComponentFactory: UserProfileComponent.Factory,
    ){
        operator fun invoke(
            componentContext : ComponentContext,
            session: Session,
            logOutCallback : ()-> Unit,
            onSessionExpiredCallback: (Session) -> Unit,
            onProjectSelected: (Project) -> Unit,
        ) : ProjectFlowComponent = ProjectFlowComponent(
            componentContext = componentContext,
            session = session,
            projectSelectionComponentFactory = projectSelectionComponentFactory,
            userProfileComponentFactory = userProfileComponentFactory,
            logOutCallback = logOutCallback,
            onSessionExpiredCallback = onSessionExpiredCallback,
            onProjectSelected = onProjectSelected,
        )
    }
}