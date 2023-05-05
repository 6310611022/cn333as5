package com.example.phonebook.domain.model

const val NEW_PHONE_ID = -1L

data class PhoneBookModel (
    val id: Long = NEW_PHONE_ID,
    val name: String = "",
    val phone: String = "",
    val content: String = "",
    val tag: TagModel = TagModel.DEFAULT
)

