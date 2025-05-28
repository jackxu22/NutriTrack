package com.xuJinghao_34680535.nutritrack.data.patient

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PatientDao {
    // Insert a list of patients into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(patients: List<Patient>)

    // Retrieve a patient by their userId
    @Query("SELECT * FROM patients WHERE userId = :userId")
    suspend fun getPatient(userId: String): Patient?

    // Calculate the average totalScore (HEIFA score) for patients of a specific sex
    @Query("SELECT AVG(totalScore) FROM patients WHERE sex = :sex")
    suspend fun getAverageHeifaScoreBySex(sex: String): Float

    // Count the total number of patients stored in the database
    @Query("SELECT COUNT(*) FROM patients")
    suspend fun getPatientCount(): Int

    // Retrieve all patients from the database
    @Query("SELECT * FROM patients")
    suspend fun getAllPatients(): List<Patient>

    // Update a specific patient's record in the database
    @Update
    suspend fun updatePatient(patient: Patient)
}