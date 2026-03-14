package com.rizero.shared_core_component.decompose

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

interface OneButtonTopBarComponent {
    val headerText : Value<String>
    fun onButtonClicked()
}

class MockOneButtonTopBarComponent(headerText : String) : OneButtonTopBarComponent{
    override val headerText: Value<String> = MutableValue(headerText)
    override fun onButtonClicked() = Unit
}