package com.example.phonebook.database

import com.example.phonebook.domain.model.NEW_PHONE_ID
import com.example.phonebook.domain.model.PhoneBookModel
import com.example.phonebook.domain.model.TagModel

class DbMapper {
    fun mapPhones(
        phoneBookDbModels: List<PhoneBookDbModel>,
        tagDbModels: Map<Long, TagDbModel>
    ): List<PhoneBookModel> = phoneBookDbModels.map {
        val tagDbModel = tagDbModels[it.tagId]
            ?: throw RuntimeException("Tag for tagId: ${it.tagId} was not found. Make sure that all tags are passed to this method")

        mapPhone(it, tagDbModel)
    }

    // convert NoteDbModel to NoteModel
    fun mapPhone(phoneBookDbModel: PhoneBookDbModel, tagDbModel: TagDbModel): PhoneBookModel {
        val tag = mapTag(tagDbModel)
        return with(phoneBookDbModel) { PhoneBookModel(id, name, phone, content, tag) }
    }

    // convert list of ColorDdModels to list of ColorModels
    fun mapTags(tagDbModels: List<TagDbModel>): List<TagModel> =
        tagDbModels.map { mapTag(it) }

    // convert ColorDbModel to ColorModel
    fun mapTag(tagDbModel: TagDbModel): TagModel =
        with(tagDbModel) { TagModel(id, name, hex) }

    // convert NoteModel back to NoteDbModel
    fun mapDbPhone(phoneB: PhoneBookModel): PhoneBookDbModel =
        with(phoneB) {
            if (id == NEW_PHONE_ID)
                PhoneBookDbModel(
                    name = name,
                    phone = phone,
                    content = content,
                    tagId = tag.id,
                    isInTrash = false
                )
            else
                PhoneBookDbModel(id, name, phone, content ,tag.id, false)
        }
}