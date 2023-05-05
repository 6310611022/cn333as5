package com.example.phonebook.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phonebook.database.AppDatabase
import com.example.phonebook.database.DbMapper
import com.example.phonebook.database.Repository
import com.example.phonebook.domain.model.PhoneBookModel
import com.example.phonebook.domain.model.TagModel
import com.example.phonebook.routing.MyPhonesRouter
import com.example.phonebook.routing.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : ViewModel() {
    val phonesNotInTrash: LiveData<List<PhoneBookModel>> by lazy {
        repository.getAllPhonesNotInTrash()
    }

    private var _phoneEntry = MutableLiveData(PhoneBookModel())

    val phoneEntry: LiveData<PhoneBookModel> = _phoneEntry

    val tags: LiveData<List<TagModel>> by lazy {
        repository.getAllTags()
    }

    val phonesInTrash by lazy { repository.getAllPhonesInTrash() }

    private var _selectedPhones = MutableLiveData<List<PhoneBookModel>>(listOf())

    val selectedPhones: LiveData<List<PhoneBookModel>> = _selectedPhones

    private val repository: Repository

    init {
        val db = AppDatabase.getInstance(application)
        repository = Repository(db.phoneBookDao(), db.tagDao(), DbMapper())
    }

    fun onCreateNewNoteClick() {
        _phoneEntry.value = PhoneBookModel()
        MyPhonesRouter.navigateTo(Screen.SavePhone)
    }

    fun onNoteClick(phone: PhoneBookModel) {
        _phoneEntry.value = phone
        MyPhonesRouter.navigateTo(Screen.SavePhone)
    }

    fun onPhoneCheckedChange(phone: PhoneBookModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.insertPhone(phone)
        }
    }

    fun onNoteSelected(phone: PhoneBookModel) {
        _selectedPhones.value = _selectedPhones.value!!.toMutableList().apply {
            if (contains(phone)) {
                remove(phone)
            } else {
                add(phone)
            }
        }
    }

    fun restoreNotes(notes: List<PhoneBookModel>) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.restoreNotesFromTrash(notes.map { it.id })
            withContext(Dispatchers.Main) {
                _selectedPhones.value = listOf()
            }
        }
    }

    fun permanentlyDeleteNotes(notes: List<PhoneBookModel>) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.deletePhones(notes.map { it.id })
            withContext(Dispatchers.Main) {
                _selectedPhones.value = listOf()
            }
        }
    }

    fun onNoteEntryChange(phone: PhoneBookModel) {
        _phoneEntry.value = phone
    }

    fun saveNote(phone: PhoneBookModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.insertPhone(phone)

            withContext(Dispatchers.Main) {
                MyPhonesRouter.navigateTo(Screen.Phones)

                _phoneEntry.value = PhoneBookModel()
            }
        }
    }

    fun moveNoteToTrash(phone: PhoneBookModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.moveNoteToTrash(phone.id)

            withContext(Dispatchers.Main) {
                MyPhonesRouter.navigateTo(Screen.Phones)
            }
        }
    }
}