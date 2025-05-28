package com.xuJinghao_34680535.nutritrack.data.personaTime

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PersonaTimeViewModel(context: Context) : ViewModel() {
    private val repository = PersonaTimeRepository(context)

    private val _personaTime = MutableLiveData<PersonaTime?>()
    val personaTime: LiveData<PersonaTime?> get() = _personaTime

    // Updates persona time data for a given patient ID
    fun updatePersonaTimeByPatientId(
        patientId: String,
        persona: String,
        biggestMealTime: String,
        sleepTime: String,
        wakeUpTime: String
    ) {
        viewModelScope.launch {
            repository.updatePersonaTimeByPatientId(patientId, persona, biggestMealTime, sleepTime, wakeUpTime)
        }
    }

    // Retrieves persona time data for a given patient ID
    fun getPersonaTimeByPatientId(patientId: String) {
        viewModelScope.launch {
            val result = repository.getPersonaTimeByPatientId(patientId)
            if (result == null) {
                // If no data exists in the database, initialize with default values
                initializePersonaTime(patientId)
                // Fetch the initialized data
                val initializedResult = repository.getPersonaTimeByPatientId(patientId)
                _personaTime.postValue(initializedResult)
            } else {
                // Update LiveData with the retrieved data
                _personaTime.postValue(result)
            }
        }
    }

    // Initializes default persona time data for a given patient ID
    fun initializePersonaTime(patientId: String) {
        val initialPersonaTime = PersonaTime(
            patientId = patientId,
            persona = "",
            biggestMealTime = "00:00",
            sleepTime = "00:00",
            wakeUpTime = "00:00"
        )
        viewModelScope.launch {
            repository.insertPersonaTime(initialPersonaTime)
        }
    }

    // Factory class for creating PersonaTimeViewModel instances
    class PersonaTimeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PersonaTimeViewModel(context) as T
    }
}