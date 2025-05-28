package com.xuJinghao_34680535.nutritrack.data.foodIntake

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import com.xuJinghao_34680535.nutritrack.data.patient.Patient

@Entity(
    tableName = "food_intake",
    foreignKeys = [ForeignKey(
        entity = Patient::class,
        parentColumns = ["userId"],
        childColumns = ["patientId"],
        onDelete = ForeignKey.CASCADE // If a Patient is deleted, all related FoodIntake will be deleted automatically
    )]
)
data class FoodIntake(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientId: String, // The patient ID associated with the FoodIntake
    val category: String,
    val response: Boolean
)
