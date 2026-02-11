package com.rizero.shared_core_component.decompose.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.rizero.shared_core_component.decompose.IconButtonTopBarComponent
import com.rizero.shared_core_component.decompose.MockIconButtonTopBarComponent
import com.rizero.shared_core_component.theme.AppTheme
import geomapkmp.shared_core_component.generated.resources.Res
import geomapkmp.shared_core_component.generated.resources.account_circle
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProfileTopAppBar(oneButtonTopBarComponent: IconButtonTopBarComponent){
    val headerText by oneButtonTopBarComponent.headerText.subscribeAsState()
    Box(modifier = Modifier
        .background(color = AppTheme.Colors.TopAppBarColor)
        .padding(top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding())
        .fillMaxWidth(),
        contentAlignment = Alignment.CenterStart,
    ){
        Row(horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = headerText,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }
        Row(
            horizontalArrangement = Arrangement.Start
        ){
            IconButton(
                onClick = oneButtonTopBarComponent::onButtonClicked,
                modifier = Modifier.padding(start = 12.dp )
            ) {
                Image(
                    painter = painterResource(Res.drawable.account_circle),
                    contentDescription = "",
                    modifier = Modifier.size(60.dp)
                )
            }
        }
    }
}

@Composable
@Preview
fun ProfileTopAppBarPreview(){
    ProfileTopAppBar(MockIconButtonTopBarComponent(
        headerText = "Page header"
    ))
}