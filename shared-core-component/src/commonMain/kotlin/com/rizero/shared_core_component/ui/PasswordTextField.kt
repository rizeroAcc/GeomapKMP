package com.rizero.shared_core_component.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.shared_core_component.theme.AppTheme
import geomapkmp.shared_core_component.generated.resources.Res
import geomapkmp.shared_core_component.generated.resources.visibility_invisible
import geomapkmp.shared_core_component.generated.resources.visibility_visible
import org.jetbrains.compose.resources.painterResource

object PasswordTextFieldDefaults {
    val DefaultHeight = 70.dp
    val DefaultWidth = 360.dp
    val DefaultCornerRadius = 16.dp
    val DefaultTextPadding = 16.dp
}

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (newValue: String) -> Unit,
    supportingText: String? = null,
    modifier: Modifier = Modifier,
    backgroundColor: Color = AppTheme.Colors.TextFieldBackgroundColor,
    textFontSize: TextUnit = 18.sp,
    supportingTextFontSize: TextUnit = 14.sp,
    placeholder: String = "",
) {

    var passwordVisible by remember { mutableStateOf(false) }
    val defaultModifier = Modifier
        .width(PasswordTextFieldDefaults.DefaultWidth)
        .height(PasswordTextFieldDefaults.DefaultHeight)
    Column(modifier.then(defaultModifier)) {
        if (supportingText != null) {
            Text(
                text = supportingText,
                fontSize = supportingTextFontSize,
            )
        }

        // Основное поле
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 4.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(PasswordTextFieldDefaults.DefaultCornerRadius))
                .background(backgroundColor)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .padding(start = 16.dp, end = 8.dp)
                    .weight(1f),
                textStyle = TextStyle(fontSize = textFontSize),
                singleLine = true,
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                fontSize = textFontSize,
                                color = Color.Gray
                            )
                        }
                        innerTextField()
                    }
                }
            )

            // Иконка видимости пароля
            IconButton(
                onClick = {
                    passwordVisible = !passwordVisible
                },
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(36.dp),
            ) {
                Icon(
                    painter = if (passwordVisible) {
                        painterResource(Res.drawable.visibility_visible)
                    } else {
                        painterResource(Res.drawable.visibility_invisible)
                    },
                    contentDescription = if (passwordVisible) {
                        "Скрыть пароль"
                    } else {
                        "Показать пароль"
                    },
                    modifier = Modifier.size(30.dp),
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewPasswordTextField(){
    PasswordTextField(
        value = "pass",
        onValueChange = {

        },
        supportingText = "password"
    )
}