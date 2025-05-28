package com.xuJinghao_34680535.nutritrack.data.foodIntake

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FoodIntakeDao {
    // Inserts a new FoodIntake record into the database
    @Insert
    suspend fun insert(foodIntake: FoodIntake)

    // Retrieves all FoodIntake records for a specific patient
    @Query("SELECT * FROM food_intake WHERE patientId = :patientId")
    suspend fun getFoodIntakesByPatientId(patientId: String): List<FoodIntake>

    // Resets the 'response' field to false for all food categories associated with a specific patient
    @Query("UPDATE food_intake SET response = false WHERE patientId = :patientId")
    suspend fun resetIsSelected(patientId: String)

    // Updates the 'response' field to true for a specific food category of a patient
    @Query("UPDATE food_intake SET response = true WHERE patientId = :patientId AND category = :category")
    suspend fun updateIsSelected(patientId: String, category: String)

}