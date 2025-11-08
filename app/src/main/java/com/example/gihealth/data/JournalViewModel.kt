package com.example.gihealth.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
//viewmodel for daily journal entry
class JournalViewModel(application: Application) : AndroidViewModel(application) {
    private val journalDao = JournalDatabase.getDatabase(application).journalDao()


    private val _journalEntries = MutableStateFlow<List<JournalEntity>>(emptyList())
    val journalEntries: StateFlow<List<JournalEntity>> = _journalEntries

    init {
        fetchAllJournals()
    }

    fun fetchAllJournals() {
        viewModelScope.launch {
            _journalEntries.value = journalDao.getAllJournals()
        }
    }

    fun addJournalEntry(text: String) {
        val date = java.text.SimpleDateFormat("MMMM d, yyyy", java.util.Locale.getDefault())
            .format(java.util.Date())
        val entry = JournalEntity(date = date, entry = text)
        viewModelScope.launch {
            journalDao.insert(entry)
            fetchAllJournals() // refresh list
        }
    }

    fun deleteJournalEntry(entry: JournalEntity) {
        viewModelScope.launch {
            journalDao.delete(entry)
            fetchAllJournals() // refresh list
        }
    }
}
