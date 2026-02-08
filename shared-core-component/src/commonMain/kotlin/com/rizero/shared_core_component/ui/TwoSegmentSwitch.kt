package com.rizero.shared_core_component.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rizero.shared_core_component.theme.AppTheme

enum class TwoSegmentSwitchPosition{
    LEFT,
    RIGHT,
}

@Composable
fun TwoSegmentSwitch(
    position : TwoSegmentSwitchPosition,
    onPositionChange : (position : TwoSegmentSwitchPosition) -> Unit,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier,
    contentLeft : @Composable ()-> Unit,
    contentRight : @Composable () -> Unit,
){

    val animatedOffset by animateFloatAsState(
        targetValue = when (position){
            TwoSegmentSwitchPosition.LEFT -> 0f
            TwoSegmentSwitchPosition.RIGHT -> 0.5f
        },
        animationSpec = tween(300)
    )

    Box(
        modifier = Modifier
            .then(modifier)
            .background(
                color = AppTheme.Colors.InfoSpotColor,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = RoundedCornerShape(16.dp)
            )
            .drawWithContent(
                onDraw = {
                    drawRoundRect(
                        brush = SolidColor(Color.Yellow),
                        cornerRadius = CornerRadius(size.height),
                        size = Size(width = size.width/2, height = size.height),
                        topLeft = Offset(x = animatedOffset*size.width, y = 0f)
                    )
                    drawContent()
                }
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable(
                        onClick = {
                            onPositionChange(TwoSegmentSwitchPosition.LEFT)
                        },
                        indication = ripple(),
                        interactionSource = null
                    ),
                contentAlignment = Alignment.Center
            ) {
                contentLeft()
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable(
                        onClick = {
                            onPositionChange(TwoSegmentSwitchPosition.RIGHT)
                        },
                        indication = ripple(),
                        interactionSource = null
                    ),
                contentAlignment = Alignment.Center
            ) {
                contentRight()
            }
        }
    }
}

@Composable
@Preview
fun TwoSegmentSwitchPreview(){
    TwoSegmentSwitch(
        position = TwoSegmentSwitchPosition.LEFT,
        onPositionChange = {},
        contentLeft = {
            Text("Новый")
        },
        contentRight = {

        },
        modifier = Modifier
            .padding(10.dp)
            .width(300.dp)
            .height(40.dp)

    )
}