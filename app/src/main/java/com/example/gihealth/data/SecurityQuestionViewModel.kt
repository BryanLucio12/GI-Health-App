package com.example.gihealth.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SecurityQuestionViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = SecurityQuestionDatabase
        .getDatabase(application)
        .securityQuestionDao()

    fun saveSecurityQuestions(list: List<SecurityQuestionEntity>) {
        viewModelScope.launch {
            dao.deleteAll()
            list.forEach { dao.insert(it) }
        }
    }

    suspend fun getSecurityQuestions(): List<SecurityQuestionEntity> {
        return dao.getAllQuestions()
    }
}