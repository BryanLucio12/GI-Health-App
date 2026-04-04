package com.example.gihealth.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.gihealth.data.BloodworkDatabase
import com.example.gihealth.data.BloodworkEntity
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BloodworkViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = BloodworkDatabase.getDatabase(application).bloodworkDao()

    private val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

    private val _todayBloodwork = MutableLiveData<List<BloodworkEntity>>(emptyList())
    val todayBloodwork: LiveData<List<BloodworkEntity>> = _todayBloodwork

    private val _allBloodwork = MutableLiveData<List<BloodworkEntity>>(emptyList())
    val allBloodwork: LiveData<List<BloodworkEntity>> = _allBloodwork

    init {
        refreshToday()
        refreshAllBloodwork()
    }

    private fun todayString(): String = LocalDate.now().format(dateFormatter)

    fun insertBloodwork(date: String, b12Level: Double?, crpLevel: Double?) {
        viewModelScope.launch {
            dao.insert(
                BloodworkEntity(
                    date = date,
                    b12Level = b12Level,
                    crpLevel = crpLevel
                )
            )
            refreshToday()
            refreshAllBloodwork()
        }
    }

    fun refreshToday() {
        viewModelScope.launch {
            val today = todayString()
            _todayBloodwork.value = dao.getBloodworkForDate(today)
        }
    }

    fun refreshAllBloodwork() {
        viewModelScope.launch {
            _allBloodwork.value = dao.getAllBloodwork()
        }
    }

    fun debugOpenDb() {
        viewModelScope.launch {
            val entries = dao.getAllBloodwork()
            Log.d("BloodworkVM", "rows=${entries.size}")
            entries.forEach {
                Log.d(
                    "BloodworkVM",
                    "date=${it.date}, b12=${it.b12Level}, crp=${it.crpLevel}"
                )
            }
        }
    }
}