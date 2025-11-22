package com.example.gihealth.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

//managing symptoms and communicating with database
class SymptomViewModel(application: Application) : AndroidViewModel(application) {

    //access dao from db
    private val symptomDao = SymptomDatabase.getDatabase(application).symptomDao()

    //backing the state flow to hold the list of symptoms
    private val _symptoms = MutableStateFlow<List<SymptomEntity>>(emptyList())
    val symptoms: StateFlow<List<SymptomEntity>> = _symptoms

    init {
        fetchAllSymptoms()
    }

    //fetch all symptoms from tb
    fun fetchAllSymptoms() {
        viewModelScope.launch {
            symptomDao.getAllSymptoms().collect { list ->
                _symptoms.value = list
            }
        }
    }
//add new symptom func
    fun addSymptom(name: String, severity: Int, timeLength: Int) {
        val symptom = SymptomEntity(
            name = name,
            severity = severity,
            timestamp = System.currentTimeMillis(),
            timeLength = timeLength
        )

        viewModelScope.launch {
            symptomDao.insert(symptom)
            fetchAllSymptoms()  // refresh list same as journal
        }
    }

    fun deleteSymptom(symptom: SymptomEntity) {
        viewModelScope.launch {
            symptomDao.delete(symptom)
            fetchAllSymptoms()  // refresh like journal
        }
    }
}
