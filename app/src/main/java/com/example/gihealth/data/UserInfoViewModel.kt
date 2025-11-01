package com.example.gihealth.data


import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


// viewmodel for user info and pin
class UserInfoViewModel(application: Application) : AndroidViewModel(application) {


    private val userInfoDao = UserInfoDatabase
        .getDatabase(application)
        .UserInfoDao()


    // liveData to observe current user info
    private val _userInfo = MutableLiveData<UserInfoEntity?>()
    val userInfo: LiveData<UserInfoEntity?> = _userInfo


    init {
        loadUserInfo()
    }


    // loads existing user from database
    private fun loadUserInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            val info = userInfoDao.getUserInfo()
            _userInfo.postValue(info)
        }
    }


    // saving user info from usersetupscreen
    fun saveUserInfo(
        name: String,
        age: Int,
        bloodType: String,
        weight: Float,
        gender: String,
        disease: String,
        triggers: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentInfo = userInfoDao.getUserInfo()
            val updatedInfo = UserInfoEntity(
                id = currentInfo?.id ?: 0,  //if id exists keep it
                name = name,
                age = age,
                bloodType = bloodType,
                weight = weight,
                gender = gender,
                disease = disease,
                triggers = triggers,
                pin = currentInfo?.pin ?: 0 // save pin if set
            )
            userInfoDao.insert(updatedInfo)
            _userInfo.postValue(updatedInfo)
        }
    }


    // saving pin from create pin screen
    fun saveUserPin(pin: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentInfo = userInfoDao.getUserInfo()
            val updatedInfo = currentInfo?.copy(pin = pin)
                ?: UserInfoEntity(
                    name = "",
                    age = 0,
                    bloodType = "",
                    weight = 0f,
                    gender = "",
                    disease = "",
                    triggers = "",
                    pin = pin
                )
            userInfoDao.insert(updatedInfo)
            _userInfo.postValue(updatedInfo)
        }
    }


    // verify pin
    fun verifyPin(enteredPin: Int): Boolean {
        return _userInfo.value?.pin == enteredPin
    }
}










