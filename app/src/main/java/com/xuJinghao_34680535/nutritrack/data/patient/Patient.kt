package com.xuJinghao_34680535.nutritrack.data.patient

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey val userId: String,
    val phoneNumber: String,
    val sex: String,
    val totalScore: Float,
    val discretionaryScore: Float,
    val vegetableScore: Float,
    val fruitScore: Float,
    val fruitVariationsScore : Float,
    val fruitServeSize : Float,
    val grainScore: Float,
    val wholeGrainScore: Float,
    val meatScore: Float,
    val dairyScore: Float,
    val sodiumScore: Float,
    val alcoholScore: Float,
    val waterScore: Float,
    val sugarScore: Float,
    val saturatedFatScore: Float,
    val unsaturatedFatScore: Float,
    val name: String?,
    val password: String?
)