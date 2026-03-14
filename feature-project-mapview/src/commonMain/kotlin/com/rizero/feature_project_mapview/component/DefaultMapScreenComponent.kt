package com.rizero.feature_project_mapview.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.rizero.shared_core_component.decompose.DefaultOneButtonTopBarComponent
import com.rizero.shared_core_component.decompose.OneButtonTopBarComponent

class DefaultMapScreenComponent(
    componentContext: ComponentContext,
    val onHeaderClick : ()->Unit
) : MapScreenComponent, ComponentContext by componentContext{
    override val topBarComponent = DefaultOneButtonTopBarComponent(
        componentContext = childContext("Top map app bar"),
        headerText = "Нет активного задания", //todo придумать че делать чтобы утащить в ресурсы
        onButtonClickedCallback = onHeaderClick
    )
}