package com.rizero.feature_user_profile.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import com.rizero.shared_core_component.decompose.DefaultOneButtonTopBarComponent
import com.rizero.shared_core_data.repository.SessionRepository
import com.rizero.shared_core_data.repository.UserRepository
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Single

class DefaultUserProfileComponent(
    val logOutDialogFactory : LogOutDialogComponent.Factory,
    val sessionRepository: SessionRepository,
    val userRepository: UserRepository,
    componentContext: ComponentContext,
    val backNavigateCallback : () -> Unit,
    val onUserLogOut : () -> Unit,
) : UserProfileComponent, ComponentContext by componentContext{

    override val topBarComponent = DefaultOneButtonTopBarComponent(
        componentContext = childContext("Profile top bar"),
        headerText = "Профиль",
        onButtonClickedCallback = backNavigateCallback
    )

    private val logOutDialogNav = SlotNavigation<LogOutDialogConfig>()
    override val logOutDialog: Value<ChildSlot<*, LogOutDialogComponent>> = childSlot(
        source = logOutDialogNav,
        serializer = LogOutDialogConfig.serializer(),
        handleBackButton = true
    ){ _,childComponentContext->
        logOutDialogFactory(
            onCancel = { logOutDialogNav.dismiss() },
            onLoggedOut = { onUserLogOut() },
            componentContext = childComponentContext
        )
    }

    override fun openLogOutDialog() {
        logOutDialogNav.activate(LogOutDialogConfig("Log out dialog"))
    }


    @Serializable
    private data class LogOutDialogConfig(val tag : String){}
    @Single
    class ComponentFactory(
        val logOutDialogFactory: LogOutDialogComponent.Factory,
        val sessionRepository: SessionRepository,
        val userRepository: UserRepository
    ) : UserProfileComponent.Factory{
        override fun invoke(
            componentContext: ComponentContext,
            backNavigateCallback: () -> Unit,
            onUserLogOut: () -> Unit,
        ): UserProfileComponent = DefaultUserProfileComponent(
            logOutDialogFactory = logOutDialogFactory,
            sessionRepository = sessionRepository,
            userRepository = userRepository,
            componentContext = componentContext,
            backNavigateCallback = backNavigateCallback,
            onUserLogOut = onUserLogOut,
        )
    }
}