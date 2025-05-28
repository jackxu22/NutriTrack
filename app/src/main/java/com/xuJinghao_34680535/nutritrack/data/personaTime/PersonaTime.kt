package com.xuJinghao_34680535.nutritrack.data.personaTime

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import com.xuJinghao_34680535.nutritrack.data.patient.Patient

// Defines the 'persona_time' table in the Room database
@Entity(
    tableName = "persona_time",
    foreignKeys = [ForeignKey(
        entity = Patient::class,
        parentColumns = ["userId"],
        childColumns = ["patientId"],
        onDelete = ForeignKey.CASCADE // Deletes this row if the patient is deleted
    )],
    indices = [Index(value = ["patientId"], unique = true)]

)
data class PersonaTime(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientId: String, // Foreign key referring to Patient.userId
    val persona: String,
    val biggestMealTime: String,
    val sleepTime: String,
    val wakeUpTime: String
    )

