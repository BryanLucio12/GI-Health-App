package com.example.gihealth.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// viewmodel for daily journal entry
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

    fun addJournalEntry(text: String, date: LocalDate) {
        val dateString = date.format(
            DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())
        )

        val entry = JournalEntity(date = dateString, entry = text)

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

