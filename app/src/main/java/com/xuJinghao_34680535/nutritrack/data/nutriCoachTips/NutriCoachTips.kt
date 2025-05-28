package com.xuJinghao_34680535.nutritrack.data.nutriCoachTips

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.xuJinghao_34680535.nutritrack.data.patient.Patient

@Entity(
    tableName = "nutri_coach_tips",
    foreignKeys = [ForeignKey(
        entity = Patient::class,
        parentColumns = ["userId"],
        childColumns = ["patientId"],
        onDelete = ForeignKey.CASCADE // If a Patient is deleted, all related NutriCoachTips will be deleted automatically
    )]
)
data class NutriCoachTips(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientId: String, // The patient ID associated with the tip
    val tip: String,
    val timestamp: Long
)