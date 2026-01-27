package com.example.gihealth.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateListOf
import com.example.gihealth.data.CustomSymptomEntity
import com.example.gihealth.data.SymptomDatabase
import kotlinx.coroutines.launch

data class SelectedSymptom(
    val name: String,
    val severity: Int = 0,
    val note: String = ""
)

class LogSymptomsViewModel(application: Application) : AndroidViewModel(application) {

    private val customSymptomDao =
        SymptomDatabase.getDatabase(application).customSymptomDao()

    private val _customSymptoms = mutableStateListOf<String>()
    val customSymptoms: List<String> = _customSymptoms

    private val _selected = mutableStateListOf<SelectedSymptom>()
    val selected: List<SelectedSymptom> = _selected

    init {
        viewModelScope.launch {
            customSymptomDao.getAllCustomSymptoms().collect { list ->
                _customSymptoms.clear()
                _customSymptoms.addAll(list.map { it.name })
            }
        }
    }

    fun addCustomSymptom(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return

        viewModelScope.launch {
            customSymptomDao.insert(CustomSymptomEntity(trimmed))
        }

        // ALWAYS ensure it is selected
        val existing = _selected.find { it.name == trimmed }
        if (existing == null) {
            _selected.add(
                SelectedSymptom(
                    name = trimmed,
                    severity = 0,
                    note = ""
                )
            )
        }
    }

    fun toggleSymptom(name: String) {
        val existing = _selected.find { it.name == name }
        if (existing != null) {
            _selected.remove(existing)
        } else {
            _selected.add(SelectedSymptom(name))
        }
    }

    fun updateSeverity(name: String, severity: Int) {
        val index = _selected.indexOfFirst { it.name == name }
        if (index != -1) {
            _selected[index] = _selected[index].copy(severity = severity)
        }
    }

    fun updateNote(name: String, note: String) {
        val index = _selected.indexOfFirst { it.name == name }
        if (index != -1) {
            _selected[index] = _selected[index].copy(note = note)
        }
    }

    fun clear() {
        _selected.clear()
    }
}