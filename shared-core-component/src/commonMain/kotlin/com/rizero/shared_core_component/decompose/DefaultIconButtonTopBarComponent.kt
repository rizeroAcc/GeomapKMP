package com.rizero.shared_core_component.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class DefaultIconButtonTopBarComponent(
    val componentContext : ComponentContext,
    headerText : String,
    val onButtonClickedCallback : () -> Unit,
) : IconButtonTopBarComponent, ComponentContext by componentContext {
    private val headerTextHolder = MutableValue(headerText)

    override val headerText : Value<String> = headerTextHolder

    fun setHeaderText(newHeaderText : String) {
        headerTextHolder.value = newHeaderText
    }

    override fun onButtonClicked() {
        onButtonClickedCallback()
    }
}