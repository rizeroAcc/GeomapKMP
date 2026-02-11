package com.rizero.shared_core_component.decompose

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

interface IconButtonTopBarComponent {
    val headerText : Value<String>
    fun onButtonClicked()
}

class MockIconButtonTopBarComponent(headerText : String) : IconButtonTopBarComponent{
    override val headerText: Value<String> = MutableValue(headerText)
    override fun onButtonClicked() = Unit
}