package com.rizero.feature_user_profile.component

import com.arkivanov.decompose.ComponentContext
import com.rizero.shared_core_component.decompose.IconButtonTopBarComponent
import com.rizero.shared_core_component.decompose.MockIconButtonTopBarComponent

interface UserProfileComponent {
    val topBarComponent : IconButtonTopBarComponent

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            backNavigateCallback : () -> Unit,
        ) : UserProfileComponent
    }
}

class MockUserProfileComponent() : UserProfileComponent{
    override val topBarComponent: IconButtonTopBarComponent
        get() = MockIconButtonTopBarComponent("Профиль")
}