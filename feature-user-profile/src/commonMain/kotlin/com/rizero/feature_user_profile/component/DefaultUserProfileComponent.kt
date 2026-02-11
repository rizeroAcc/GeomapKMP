package com.rizero.feature_user_profile.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.rizero.shared_core_component.decompose.DefaultIconButtonTopBarComponent
import com.rizero.shared_core_data.repository.SessionRepository
import com.rizero.shared_core_data.repository.UserRepository
import org.koin.core.annotation.Single

class DefaultUserProfileComponent(
    val sessionRepository: SessionRepository,
    val userRepository: UserRepository,
    componentContext: ComponentContext,
    val backNavigateCallback : () -> Unit,
) : UserProfileComponent, ComponentContext by componentContext{

    override val topBarComponent = DefaultIconButtonTopBarComponent(
        componentContext = childContext("Profile top bar"),
        headerText = "Профиль",
        onButtonClickedCallback = backNavigateCallback
    )



    @Single
    class ComponentFactory(
        val sessionRepository: SessionRepository,
        val userRepository: UserRepository
    ) : UserProfileComponent.Factory{
        override fun invoke(
            componentContext: ComponentContext,
            backNavigateCallback: () -> Unit
        ): UserProfileComponent = DefaultUserProfileComponent(
            sessionRepository = sessionRepository,
            userRepository = userRepository,
            componentContext = componentContext,
            backNavigateCallback = backNavigateCallback
        )
    }
}