package com.xuJinghao_34680535.nutritrack.data.patient

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PatientsViewModel(context: Context) : ViewModel() {
    private val repository = PatientsRepository(context)

    // Current selected patient
    private val _patient = MutableLiveData<Patient?>()
    val patient: LiveData<Patient?> get() = _patient

    // List of unregistered user IDs (patients with no password)
    private val _unregisteredUserIds = MutableLiveData<List<String>>(emptyList())
    val unregisteredUserIds: LiveData<List<String>> get() = _unregisteredUserIds

    // List of registered user IDs (patients with password)
    private val _registeredUserIds = MutableLiveData<List<String>>(emptyList())
    val registeredUserIds: LiveData<List<String>> get() = _registeredUserIds

    // Average HEIFA score for male patients
    private val _averageHeifaScoreMale = MutableLiveData<Float>()
    val averageHeifaScoreMale: LiveData<Float> get() = _averageHeifaScoreMale

    // Average HEIFA score for female patients
    private val _averageHeifaScoreFemale = MutableLiveData<Float>()
    val averageHeifaScoreFemale: LiveData<Float> get() = _averageHeifaScoreFemale

    // List of all patients
    private val _allPatients = MutableLiveData<List<Patient>>(emptyList())
    val allPatients: LiveData<List<Patient>> get() = _allPatients

    // Load CSV data (if needed) and update user ID lists when ViewModel is created
    init {
        loadCsvDataIfNeeded()
        loadUnregisteredUserIds()
        loadRegisteredUserIds()
    }

    // Load initial CSV data into database if needed
    fun loadCsvDataIfNeeded() {
        viewModelScope.launch {
            repository.loadCsvDataIfNeeded()
        }
    }

    // Get a patient by userId and update LiveData
    fun getPatientByPatientId(id: String) {
        viewModelScope.launch {
            val result = repository.getPatientByPatientId(id)  // or repository.getPatientById
            _patient.postValue(result)
        }
    }

    // Get list of all patients
    suspend fun getAllPatients(): List<Patient> {
        return withContext(Dispatchers.IO) {
            repository.getAllPatients()
        }
    }

    // Load all patients and update LiveData
    fun getAllPatientsData() {
        viewModelScope.launch {
            val result = repository.getAllPatients()
            _allPatients.postValue(result)
        }
    }

    // Update a patient's information in the database
    fun updatePatient(updatedPatient: Patient) {
        viewModelScope.launch {
            repository.updatePatient(updatedPatient)
            _patient.postValue(updatedPatient)
            loadUnregisteredUserIds() // Refresh user ID lists
            loadRegisteredUserIds()
        }
    }

    // Load list of user IDs that are not registered (password is empty)
    private fun loadUnregisteredUserIds() {
        viewModelScope.launch {
            try {
                val patients = getAllPatients()
                val filteredIds = patients
                    .filter { it.password.isNullOrEmpty() }
                    .map { it.userId }
                _unregisteredUserIds.postValue(filteredIds)
            } catch (_: Exception) {
                _unregisteredUserIds.postValue(emptyList())
            }
        }
    }

    // Load list of user IDs that are registered (password exists)
    private fun loadRegisteredUserIds() {
        viewModelScope.launch {
            try {
                val patients = getAllPatients()
                val filteredIds = patients
                    .filter { !it.password.isNullOrEmpty() }
                    .map { it.userId }
                _registeredUserIds.postValue(filteredIds)
            } catch (_: Exception) {
                _registeredUserIds.postValue(emptyList())
            }
        }
    }

    // Load average HEIFA scores by gender
    fun loadAverageHeifaScores() {
        viewModelScope.launch {
            val maleScore = repository.getAverageHeifaScoreBySex("Male")
            val femaleScore = repository.getAverageHeifaScoreBySex("Female")
            _averageHeifaScoreMale.postValue(maleScore)
            _averageHeifaScoreFemale.postValue(femaleScore)
        }
    }

    class PatientsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PatientsViewModel(context) as T
    }
}