package com.xuJinghao_34680535.nutritrack.data.nutriCoachTips

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NutriCoachTipsViewModel(context: Context) : ViewModel() {

    private val nutriCoachTipsRepository: NutriCoachTipsRepository = NutriCoachTipsRepository(context)

    // List of all Tips
    private val _allTips = MutableStateFlow<List<NutriCoachTips>>(emptyList())
    val allTips: StateFlow<List<NutriCoachTips>> = _allTips.asStateFlow()

    // Fetch all NutriCoach tips for a specific patient ID
    fun getAllTips(patientId: String) {
        viewModelScope.launch {
            nutriCoachTipsRepository.getAllTips(patientId).collect { tips ->
                _allTips.value = tips
            }
        }
    }

    // Insert a new tip into the database for the given patient
    fun insertTip(patientId: String, tipContent: String) {
        val tip = NutriCoachTips(
            patientId = patientId,
            tip = tipContent,
            timestamp = System.currentTimeMillis()
        )
        viewModelScope.launch {
            nutriCoachTipsRepository.insertTip(tip)
        }
    }

    class NutriCoachTipsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            NutriCoachTipsViewModel(context) as T
    }
}
