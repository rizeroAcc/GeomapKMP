package com.rizero.feature_user_profile.component

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.rizero.shared_core_component.decompose.OneButtonTopBarComponent
import com.rizero.shared_core_component.decompose.MockOneButtonTopBarComponent

interface UserProfileComponent {
    val topBarComponent : OneButtonTopBarComponent
    val logOutDialog : Value<ChildSlot<*, LogOutDialogComponent>>

    fun openLogOutDialog()

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            backNavigateCallback : () -> Unit,
            onUserLogOut: () -> Unit,
        ) : UserProfileComponent
    }
}

class MockUserProfileComponent(
    val logOutComponent : LogOutDialogComponent? = null
) : UserProfileComponent{
    override val topBarComponent: OneButtonTopBarComponent
        get() = MockOneButtonTopBarComponent("Профиль")
    override val logOutDialog: Value<ChildSlot<*, LogOutDialogComponent>>
        get()  = MutableValue(
            ChildSlot(
                logOutComponent?.let {
                    Child.Created(Any(),it)
                }
            )
        )

    override fun openLogOutDialog() = Unit
}