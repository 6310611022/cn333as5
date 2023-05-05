package com.example.phonebook.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phonebook.domain.model.NEW_PHONE_ID
import com.example.phonebook.domain.model.PhoneBookModel
import com.example.phonebook.domain.model.TagModel
import com.example.phonebook.routing.MyPhonesRouter
import com.example.phonebook.routing.Screen
import com.example.phonebook.ui.components.PhonesColor
import com.example.phonebook.util.fromHex
import com.example.phonebook.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import com.example.phonebook.ui.theme.Montserrat


@ExperimentalMaterialApi
@Composable
fun SavePhoneScreen(viewModel: MainViewModel) {
    val phoneEntry by viewModel.phoneEntry.observeAsState(PhoneBookModel())

    val tags: List<TagModel> by viewModel.tags.observeAsState(listOf())

    val bottomDrawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)

    val coroutineScope = rememberCoroutineScope()

    val movePhoneToTrashDialogShownState = rememberSaveable { mutableStateOf(false) }

    BackHandler {
        if (bottomDrawerState.isOpen) {
            coroutineScope.launch { bottomDrawerState.close() }
        } else {
            MyPhonesRouter.navigateTo(Screen.Phones)
        }
    }

    Scaffold(
        topBar = {
            val isEditingMode: Boolean = phoneEntry.id != NEW_PHONE_ID
            SaveNoteTopAppBar(
                isEditingMode = isEditingMode,
                onBackClick = { MyPhonesRouter.navigateTo(Screen.Phones) },
                onSavePhoneClick = { viewModel.saveNote(phoneEntry) },
                onOpenColorPickerClick = {
                    coroutineScope.launch { bottomDrawerState.open() }
                },
                onDeletePhoneClick = {
                    movePhoneToTrashDialogShownState.value = true
                }
            )
        }
    ) {
        BottomDrawer(
            drawerState = bottomDrawerState,
            drawerContent = {
                ColorPicker(
                    colors = tags,
                    onColorSelect = { color ->
                        viewModel.onNoteEntryChange(phoneEntry.copy(tag = color))
                    }
                )
            }
        ) {
            SaveNoteContent(
                phone = phoneEntry,
                onPhoneChange = { updatePhoneEntry ->
                    viewModel.onNoteEntryChange(updatePhoneEntry)
                }
            )
        }

        if (movePhoneToTrashDialogShownState.value) {
            AlertDialog(
                onDismissRequest = {
                    movePhoneToTrashDialogShownState.value = false
                },
                title = {
                    Text(text = "Move note to the trash?",
                    fontFamily = Montserrat)

                },
                text = {
                    Text(text =
                        "Are you sure you want to " +
                                "move this note to the trash?",
                        fontFamily = Montserrat
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.moveNoteToTrash(phoneEntry)
                    }) {
                        Text(text="Confirm",
                            fontFamily = Montserrat,)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        movePhoneToTrashDialogShownState.value = false
                    }) {
                        Text(text = "Dismiss",
                            fontFamily = Montserrat,)
                    }
                }
            )
        }
    }
}

@Composable
fun SaveNoteTopAppBar(
    isEditingMode: Boolean,
    onBackClick: () -> Unit,
    onSavePhoneClick: () -> Unit,
    onOpenColorPickerClick: () -> Unit,
    onDeletePhoneClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Save Contact",
                color = MaterialTheme.colors.onPrimary,
                fontFamily = Montserrat,
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        },
        actions = {
            IconButton(onClick = onSavePhoneClick) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Save Phone Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }

            IconButton(onClick = onOpenColorPickerClick) {
                Icon(
                    painter = painterResource(id = com.example.phonebook.R.drawable.tag),
                    contentDescription = "Open Tag Picker Button",
                    tint = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.size(size = 22.dp)
                )
            }

            if (isEditingMode) {
                IconButton(onClick = onDeletePhoneClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Phone Button",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    )
}

@Composable
private fun SaveNoteContent(
    phone: PhoneBookModel,
    onPhoneChange: (PhoneBookModel) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(20.dp))
    {
        TextTitle()

        Spacer(modifier = Modifier.height(20.dp))

        ContentTextField(
            label = "Name",
            text = phone.name,
            onTextChange = { newName ->
                onPhoneChange.invoke(phone.copy(name = newName))
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        ContentTextField(
            modifier = Modifier
                .heightIn(max = 240.dp)
                .padding(top = 16.dp),
            label = "Phone",
            text = phone.phone,
            onTextChange = { newPhone ->
                onPhoneChange.invoke(phone.copy(phone = newPhone))
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        ContentTextField(
            modifier = Modifier
                .heightIn(max = 240.dp)
                .padding(top = 16.dp),
            label = "Email",
            text = phone.email,
            onTextChange = { newEmail ->
                onPhoneChange.invoke(phone.copy(phone = newEmail))
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        ContentTextField(
            modifier = Modifier
                .heightIn(max = 240.dp)
                .padding(top = 16.dp),
            label = "Note",
            text = phone.content,
            onTextChange = { newNote ->
                onPhoneChange.invoke(phone.copy(phone = newNote))
            }
        )

        Spacer(modifier = Modifier.height(40.dp))

        TextTag()

        PickedColor(color = phone.tag)
    }
}
@Composable
private fun TextTitle() {
    Row(
        Modifier
            .padding(top = 16.dp)
    ) {
        Text(
            text = "New Contact",
            fontFamily = Montserrat,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            textAlign = TextAlign.Center,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFA72F1D),
            textDecoration = TextDecoration.Underline
        )
    }
}

@Composable
private fun TextTag() {
    Row(
        Modifier
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Tag",
            fontFamily = Montserrat,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFC55241)
        )
    }
}

@Composable
private fun ContentTextField(
    modifier: Modifier = Modifier,
    label: String,
    text: String,
    onTextChange: (String) -> Unit
) {
    TextField(
        value = text,
        singleLine = true,
        onValueChange = onTextChange,
        label = { Text(label,
            fontFamily = Montserrat,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFC56869))},
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface
        )
    )
}

@Composable
private fun PickedColor(color: TagModel) {
    Row(
        Modifier
            .padding(8.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = color.name,
            fontFamily = Montserrat,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            fontSize = 20.sp,
        )
        PhonesColor(
            color = Color.fromHex(color.hex),
            size = 40.dp,
            border = 1.dp,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
private fun ColorPicker(
    colors: List<TagModel>,
    onColorSelect: (TagModel) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Tag picker",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp),
            fontFamily = Montserrat
        )
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(colors.size) { itemIndex ->
                val color = colors[itemIndex]
                ColorItem(
                    color = color,
                    onColorSelect = onColorSelect
                )
            }
        }
    }
}

@Composable
fun ColorItem(
    color: TagModel,
    onColorSelect: (TagModel) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    onColorSelect(color)
                }
            )
    ) {
        PhonesColor(
            modifier = Modifier.padding(10.dp),
            color = Color.fromHex(color.hex),
            size = 80.dp,
            border = 2.dp
        )
        Text(
            text = color.name,
            fontSize = 22.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterVertically),
            fontFamily = Montserrat
        )
    }
}

@Preview
@Composable
fun ColorItemPreview() {
    ColorItem(TagModel.DEFAULT) {}
}

@Preview
@Composable
fun ColorPickerPreview() {
    ColorPicker(
        colors = listOf(
            TagModel.DEFAULT,
            TagModel.DEFAULT,
            TagModel.DEFAULT
        )
    ) { }
}

@Preview
@Composable
fun PickedColorPreview() {
    PickedColor(TagModel.DEFAULT)
}
