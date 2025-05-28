package com.xuJinghao_34680535.nutritrack.data.personaTime

import android.content.Context
import com.xuJinghao_34680535.nutritrack.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PersonaTimeRepository(context: Context) {
    private val personaTimeDao = AppDatabase.getDatabase(context).personaTimeDao()

    // Update PersonaTime data by patient ID
    suspend fun updatePersonaTimeByPatientId(
        patientId: String,
        persona: String,
        biggestMealTime: String,
        sleepTime: String,
        wakeUpTime: String
    ) {
        withContext(Dispatchers.IO) {
            personaTimeDao.updatePersonaTime(patientId, persona, biggestMealTime, sleepTime, wakeUpTime)
        }
    }

    // Insert new PersonaTime record
    suspend fun insertPersonaTime(personaTime: PersonaTime) {
        withContext(Dispatchers.IO) {
            personaTimeDao.insert(personaTime)
        }
    }

    // Get PersonaTime by patient ID
    suspend fun getPersonaTimeByPatientId(patientId: String): PersonaTime? {
        return personaTimeDao.getPersonaTimeByPatientId(patientId)
    }
}