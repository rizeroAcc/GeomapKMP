package com.rizero.shared_core_component.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizero.shared_core_component.theme.AppTheme

object DefaultTextFieldDefaults {
    val DefaultHeight = 70.dp
    val DefaultWidth = 360.dp
    val DefaultCornerRadius = 16.dp
    val DefaultTextPadding = 16.dp
}

enum class InputType {
    Text,
    Phone,
}

@Composable
fun DefaultTextField(
    value : String,
    onValueChange : (newValue : String) -> Unit,
    modifier: Modifier = Modifier,
    inputType: InputType = InputType.Text,
    supportingText : String? = null,
    placeholder : String = "",
    backgroundColor: Color = AppTheme.Colors.TextFieldBackgroundColor,
    textFontSize : TextUnit = 18.sp,
    supportingTextFontSize : TextUnit = 14.sp
){
    val defaultModifier = Modifier
        .width(DefaultTextFieldDefaults.DefaultWidth)
        .height(DefaultTextFieldDefaults.DefaultHeight)


    Column(modifier = modifier.then(defaultModifier)) {
        if (supportingText!=null) {
            Text(
                text = supportingText,
                fontSize = supportingTextFontSize
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxSize()
                .clip(RoundedCornerShape(DefaultTextFieldDefaults.DefaultCornerRadius))
                .background(color = backgroundColor)
        ){
            BasicTextField(
                value = value,
                onValueChange = { newValue->
                    if (inputType == InputType.Phone){
                        val filtered = newValue.filter { it.isDigit() || it == '+' }
                        onValueChange(filtered)
                    }else if (inputType == InputType.Text){
                        onValueChange(newValue)
                    }

                },
                modifier = Modifier.padding(start = DefaultTextFieldDefaults.DefaultTextPadding),
                textStyle = TextStyle(fontSize = textFontSize),
                singleLine = true,
                keyboardOptions = if (inputType == InputType.Text)
                    KeyboardOptions.Default
                else
                    KeyboardOptions(keyboardType = KeyboardType.Phone),
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
        }
    }
}

@Preview
@Composable
fun PreviewDefaultTextField(){
    DefaultTextField(
        value = "some login",
        onValueChange = {},
        supportingText = "login"
    )
}

@Preview
@Composable
fun PreviewDefaultTextFieldPlaceholder(){
    DefaultTextField(
        value = "",
        placeholder = "login",
        onValueChange = {},
        supportingText = "login"
    )
}

@Preview
@Composable
fun PreviewDefaultTextFieldWithoutSupportingText(){
    DefaultTextField(
        value = "some login",
        onValueChange = {},
    )
}