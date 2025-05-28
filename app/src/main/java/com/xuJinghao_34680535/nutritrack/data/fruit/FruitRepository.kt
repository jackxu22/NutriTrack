package com.xuJinghao_34680535.nutritrack.data.fruit

// Repository class for handling fruit data operations.
class FruitRepository () {

    // Create an instance of the API service for making network requests.
    private val apiService = APIService.Companion.create()

    // Fetches the information of a fruit by its name.
    suspend fun getFruitByName(name: String): FruitResponse? {
        return try {
            // Perform the API call to get the fruit information.
            val response = apiService.getFruitByName(name)

            // Check if the response is successful and return the body (fruit data).
            if (response.isSuccessful) {
                response.body()
            } else {
                // In case of failure, return null.
                null
            }
        } catch (_: Exception) {
            // Handle exceptions (e.g., network errors) and return null.
            null
        }
    }
}