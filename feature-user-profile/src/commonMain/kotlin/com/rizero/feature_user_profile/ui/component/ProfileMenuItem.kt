package com.rizero.feature_user_profile.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.shared_core_component.theme.AppTheme
import geomapkmp.feature_user_profile.generated.resources.Res
import geomapkmp.feature_user_profile.generated.resources.arrow_right
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProfileMenuItem(
    itemText : String,
    onItemClick : () -> Unit,
    modifier: Modifier = Modifier
){
    Box(
        modifier = Modifier
            .then(modifier)
            .clickable(
                onClick = {
                    onItemClick()
                },
                indication = ripple(),
                interactionSource = null,
            )
            .padding(vertical = 4.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = itemText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Image(
                painter = painterResource(Res.drawable.arrow_right),
                modifier = Modifier.size(20.dp),
                contentDescription = ""
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ProfileMenuItemPreview(){
    Column(
        modifier = Modifier.border(
            color = AppTheme.Colors.LightBlue,
            shape = RoundedCornerShape(16.dp),
            width = 1.dp
        )
    ) {
        ProfileMenuItem(
            itemText = "Изменить пароль",
            onItemClick = {},
            modifier = Modifier
                .padding(top = 8.dp)
                .height(40.dp)
        )
        ProfileMenuItem(
            itemText = "Редактировать имя пользователя",
            onItemClick = {},
            modifier = Modifier.height(40.dp)
        )
    }
}