package com.rizero.shared_core_component.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ColoredCircle(color: Color = Color.Red, size: Dp = 50.dp){
    Box(
        modifier = Modifier.size(size).background(color = color, shape = CircleShape)
    )
}