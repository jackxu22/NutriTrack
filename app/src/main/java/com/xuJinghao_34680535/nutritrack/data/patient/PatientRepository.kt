package com.xuJinghao_34680535.nutritrack.data.patient

import android.content.Context
import com.xuJinghao_34680535.nutritrack.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class PatientsRepository(private val context: Context) {
    private val patientDao = AppDatabase.getDatabase(context).patientDao()

    // Retrieve a patient from the database using userId
    suspend fun getPatientByPatientId(patientId: String): Patient? =
        patientDao.getPatient(patientId)

    // Calculate the average HEIFA score based on sex (Male/Female)
    suspend fun getAverageHeifaScoreBySex(sex: String): Float {
        return patientDao.getAverageHeifaScoreBySex(sex)
    }

    // Get all patients stored in the database
    suspend fun getAllPatients(): List<Patient> {
        return patientDao.getAllPatients()
    }

    // Update a patient's data in the database
    suspend fun updatePatient(patient: Patient) {
        patientDao.updatePatient(patient)
    }

    // Load patient data from CSV file into the database (only if not already loaded)
    suspend fun loadCsvDataIfNeeded() {
        withContext(Dispatchers.IO) {
            // Check if the database already contains patient data
            val patientCount = patientDao.getPatientCount()
            if (patientCount > 0) return@withContext // Skip loading if data already exists

            // Open the CSV file from the assets folder
            val inputStream = context.assets.open("data.csv")
            val reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
            val patients = mutableListOf<Patient>()

            // Skip the header line of the CSV
            reader.readLine()

            // Read each line from the CSV file
            reader.forEachLine { line ->
                val values = line.split(",")
                val sex = values[2].trim()
                val columnOffset = if (sex == "Male") 0 else 1

                val userId = values[1].trim()
                val phoneNumber = values[0].trim()
                val totalScore = values[3 + columnOffset].trim().toFloatOrNull() ?: 0f

                // Construct a Patient object with all HEIFA component scores
                val patient = Patient(
                    userId = userId,
                    phoneNumber = phoneNumber,
                    sex = sex,
                    totalScore = totalScore,
                    discretionaryScore = values[5 + columnOffset].trim().toFloatOrNull() ?: 0f,
                    vegetableScore = values[8 + columnOffset].trim().toFloatOrNull() ?: 0f,
                    fruitScore = values[19 + columnOffset].trim().toFloatOrNull() ?: 0f,
                    fruitVariationsScore = values[22].trim().toFloatOrNull() ?: 0f,
                    fruitServeSize = values[21].trim().toFloatOrNull() ?: 0f,
                    grainScore = values[29 + columnOffset].trim().toFloatOrNull() ?: 0f,
                    wholeGrainScore = values[33 + columnOffset].trim().toFloatOrNull() ?: 0f,
                    meatScore = values[36 + columnOffset].trim().toFloatOrNull() ?: 0f,
                    dairyScore = values[40 + columnOffset].trim().toFloatOrNull() ?: 0f,
                    sodiumScore = values[43 + columnOffset].trim().toFloatOrNull() ?: 0f,
                    alcoholScore = values[46 + columnOffset].trim().toFloatOrNull() ?: 0f,
                    waterScore = values[49 + columnOffset].trim().toFloatOrNull() ?: 0f,
                    sugarScore = values[54 + columnOffset].trim().toFloatOrNull() ?: 0f,
                    saturatedFatScore = values[57 + columnOffset].trim().toFloatOrNull() ?: 0f,
                    unsaturatedFatScore = values[60 + columnOffset].trim().toFloatOrNull() ?: 0f,
                    name = null,
                    password = null
                )
                patients.add(patient)

            }

            // Insert all parsed patients into the database
            patientDao.insertAll(patients)

            // Close resources
            reader.close()
            inputStream.close()
        }
    }
}