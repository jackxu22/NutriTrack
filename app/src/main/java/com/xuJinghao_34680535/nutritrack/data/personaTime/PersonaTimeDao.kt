package com.xuJinghao_34680535.nutritrack.data.personaTime

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PersonaTimeDao {
    // Insert a new PersonaTime record
    @Insert
    suspend fun insert(personaTime: PersonaTime)

    // Get the PersonaTime for a specific patient ID
    @Query("SELECT * FROM persona_time WHERE patientId = :patientId LIMIT 1")
    suspend fun getPersonaTimeByPatientId(patientId: String): PersonaTime?

    // Update PersonaTime fields for a specific patient ID
    @Query("""
        UPDATE persona_time 
        SET persona = :persona, 
            biggestMealTime = :biggestMealTime,
            sleepTime = :sleepTime, 
            wakeUpTime = :wakeUpTime 
        WHERE patientId = :patientId
    """)
    suspend fun updatePersonaTime(
        patientId: String,
        persona: String,
        biggestMealTime: String,
        sleepTime: String,
        wakeUpTime: String
    )

}