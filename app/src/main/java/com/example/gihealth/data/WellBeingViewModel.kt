package com.example.gihealth.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WellBeingViewModel(application: Application) : AndroidViewModel(application) {

    private val wellBeingDao = WellBeingDatabase.getDatabase(application).wellBeingDao()

    private val _entries = MutableLiveData<List<WellBeingEntity>>()
    val entries: LiveData<List<WellBeingEntity>> get() = _entries

    init {
        loadEntries()
    }

    fun loadEntries() {
        viewModelScope.launch {
            _entries.value = wellBeingDao.getAllEntries()
        }
    }

    fun insertEntry(entry: WellBeingEntity) {
        viewModelScope.launch {
            wellBeingDao.insert(entry)
            loadEntries()   // refresh list after adding
        }
    }

    fun deleteEntry(entry: WellBeingEntity) {
        viewModelScope.launch {
            wellBeingDao.delete(entry)
            loadEntries()   // refresh list after deleting
        }
    }
}

