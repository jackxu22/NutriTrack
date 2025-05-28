package com.xuJinghao_34680535.nutritrack.data.foodIntake

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FoodIntakeViewModel(context: Context) : ViewModel() {

    private val foodIntakeRepository: FoodIntakeRepository = FoodIntakeRepository(context)

    // Use MutableLiveData to store the list of FoodIntake data
    private val _foodIntakes = MutableLiveData<List<FoodIntake>>()
    val foodIntakes: LiveData<List<FoodIntake>> get() = _foodIntakes

    // Fetches the FoodIntake data for a specific patient by their patient ID.
    fun getFoodIntakesByPatientId(patientId: String) {
        viewModelScope.launch {
            val intakes = foodIntakeRepository.getFoodIntakesByPatientId(patientId)
            _foodIntakes.postValue(intakes)
        }
    }

    // Initializes the FoodIntake data for a patient by creating records for all food categories.
    fun initializeFoodIntakes(patientId: String, foodCategories: List<String>) {
        viewModelScope.launch {
            foodCategories.forEach { category ->
                val foodIntake = FoodIntake(
                    patientId = patientId,
                    category = category,
                    response = false  // Initial state is false, meaning not selected
                )
                foodIntakeRepository.insert(foodIntake)
            }
        }
    }

    // Updates the food selections for a specific patient.
    fun updateFoodSelections(patientId: String, selectedCategories: Set<String>) {
        viewModelScope.launch {
            // Reset the selection state for the patient
            foodIntakeRepository.resetIsSelected(patientId)
            // Set the selected categories for the patient
            selectedCategories.forEach { category ->
                foodIntakeRepository.setCategorySelected(patientId, category)
            }
        }
    }

    class FoodIntakeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            FoodIntakeViewModel(context) as T
    }
}