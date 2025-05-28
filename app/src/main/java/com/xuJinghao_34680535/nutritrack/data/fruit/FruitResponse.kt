package com.xuJinghao_34680535.nutritrack.data.fruit

// Represents the response for fruit data, including fruit details and its nutrition information
data class FruitResponse(
    val name: String,
    val id: Int,
    val family: String,
    val order: String,
    val genus: String,
    val nutritions: Nutritions
)

// Represents the nutritional contents of the fruit
data class Nutritions(
    val calories: Double,
    val fat: Double,
    val sugar: Double,
    val carbohydrates: Double,
    val protein: Double
)
