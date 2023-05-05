package com.example.phonebook.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phonebook.domain.model.PhoneBookModel
import com.example.phonebook.ui.theme.Montserrat
import com.example.phonebook.util.fromHex

@ExperimentalMaterialApi
@Composable
fun Phone(
    modifier: Modifier = Modifier,
    phone: PhoneBookModel,
    onPhoneClick: (PhoneBookModel) -> Unit = {},
    onPhoneCheckedChange: (PhoneBookModel) -> Unit = {},
    isSelected: Boolean
) {
    val background = if (isSelected)
        Color.LightGray
    else
        MaterialTheme.colors.surface

    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        backgroundColor = background
    ) {
        ListItem(
            text = { Text(text = phone.name, maxLines = 1,fontFamily = Montserrat,fontSize = 21.sp,) },
            secondaryText = {
                Text(text = phone.phone, maxLines = 1,fontFamily = Montserrat,fontSize = 16.sp,)
            },
            icon = {
                PhonesColor(
                    color = Color.fromHex(phone.tag.hex),
                    size = 45.dp,
                    border = 1.dp
                )
            },
            modifier = Modifier.clickable {
                onPhoneClick.invoke(phone)
            }
        )
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
private fun PhonePreview() {
    Phone(phone = PhoneBookModel(1, "Apple", "1234567890"), isSelected = true)
}