package com.example.phonebook.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.phonebook.domain.model.PhoneBookModel
import com.example.phonebook.domain.model.TagModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Repository (
    private val phoneBookDao: PhoneBookDao,
    private val tagDao: TagDao,
    private val dbMapper: DbMapper
    ) {
        // Working Phones
        private val phonesNotInTrashLiveData: MutableLiveData<List<PhoneBookModel>> by lazy {
            MutableLiveData<List<PhoneBookModel>>()
        }

        fun getAllPhonesNotInTrash(): LiveData<List<PhoneBookModel>> = phonesNotInTrashLiveData

        // Deleted Phones
        private val phonesInTrashLiveData: MutableLiveData<List<PhoneBookModel>> by lazy {
            MutableLiveData<List<PhoneBookModel>>()
        }

        fun getAllPhonesInTrash(): LiveData<List<PhoneBookModel>> = phonesInTrashLiveData

        init {
            initDatabase(this::updatePhonesLiveData)
        }

        /**
         * Populates database with colors if it is empty.
         */
        private fun initDatabase(postInitAction: () -> Unit) {
            GlobalScope.launch {
                // Prepopulate tags
                val tags = TagDbModel.DEFAULT_TAGS.toTypedArray()
                val dbTags = tagDao.getAllSync()
                if (dbTags.isNullOrEmpty()) {
                    tagDao.insertAll(*tags)
                }

                // Prepopulate phones
                val phones = PhoneBookDbModel.DEFAULT_PHONES.toTypedArray()
                val dbPhones = phoneBookDao.getAllSync()
                if (dbPhones.isNullOrEmpty()) {
                    phoneBookDao.insertAll(*phones)
                }

                postInitAction.invoke()
            }
        }

        // get list of working notes or deleted notes
        private fun getAllPhonesDependingOnTrashStateSync(inTrash: Boolean): List<PhoneBookModel> {
            val tagDbModels: Map<Long, TagDbModel> = tagDao.getAllSync().map { it.id to it }.toMap()
            val dbPhones: List<PhoneBookDbModel> =
                phoneBookDao.getAllSync().filter { it.isInTrash == inTrash }
            return dbMapper.mapPhones(dbPhones, tagDbModels)
        }

        fun insertPhone(note: PhoneBookModel) {
            phoneBookDao.insert(dbMapper.mapDbPhone(note))
            updatePhonesLiveData()
        }

        fun deletePhones(phoneIds: List<Long>) {
            phoneBookDao.delete(phoneIds)
            updatePhonesLiveData()
        }

        fun moveNoteToTrash(phoneId: Long) {
            val dbPhone = phoneBookDao.findByIdSync(phoneId)
            val newDbNote = dbPhone.copy(isInTrash = true)
            phoneBookDao.insert(newDbNote)
            updatePhonesLiveData()
        }

        fun restoreNotesFromTrash(phoneIds: List<Long>) {
            val dbPhonesInTrash = phoneBookDao.getPhoneByIdsSync(phoneIds)
            dbPhonesInTrash.forEach {
                val newDbNote = it.copy(isInTrash = false)
                phoneBookDao.insert(newDbNote)
            }
            updatePhonesLiveData()
        }

        fun getAllTags(): LiveData<List<TagModel>> =
            Transformations.map(tagDao.getAll()) { dbMapper.mapTags(it) }

        private fun updatePhonesLiveData() {
            phonesNotInTrashLiveData.postValue(getAllPhonesDependingOnTrashStateSync(false))
            phonesInTrashLiveData.postValue(getAllPhonesDependingOnTrashStateSync(true))
        }
}