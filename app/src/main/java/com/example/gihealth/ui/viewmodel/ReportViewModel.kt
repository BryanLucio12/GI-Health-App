package com.example.gihealth.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.gihealth.data.SymptomEntity
import com.example.gihealth.data.UserInfoEntity
import com.example.gihealth.models.PdfQuestionnaireAnswers

class ReportViewModel(application: Application) : AndroidViewModel(application) {


    var symptoms: List<SymptomEntity> = emptyList()
    var todayStressRating: Int? = null
    var weeklyAvgStressRating: Double? = null

    var userInfoSnapshot: UserInfoEntity? = null

    var answers: PdfQuestionnaireAnswers? = null

    var todayAbdominalPain: Int? = null
    var weeklyAvgAbdominalPain: Double? = null

}