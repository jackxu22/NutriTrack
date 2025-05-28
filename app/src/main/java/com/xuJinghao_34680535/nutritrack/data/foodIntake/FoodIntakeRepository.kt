package com.xuJinghao_34680535.nutritrack.data.foodIntake

import android.content.Context
import com.xuJinghao_34680535.nutritrack.data.AppDatabase

class FoodIntakeRepository(context: Context) {
    private val foodIntakeDao = AppDatabase.getDatabase(context).foodIntakeDao()

    // Inserts a new FoodIntake record into the database
    suspend fun insert(foodIntake: FoodIntake) {
        foodIntakeDao.insert(foodIntake)
    }

    // Fetches the FoodIntake data for a specific patient based on their patientId
    suspend fun getFoodIntakesByPatientId(patientId: String): List<FoodIntake> =
        foodIntakeDao.getFoodIntakesByPatientId(patientId)

    // Resets the "isSelected" field for all food categories of a specific patient
    suspend fun resetIsSelected(patientId: String) {
        foodIntakeDao.resetIsSelected(patientId)
    }

    // Updates the "isSelected" status for a specific food category of a patient
    suspend fun setCategorySelected(patientId: String, category: String) {
        foodIntakeDao.updateIsSelected(patientId, category)
    }
}

