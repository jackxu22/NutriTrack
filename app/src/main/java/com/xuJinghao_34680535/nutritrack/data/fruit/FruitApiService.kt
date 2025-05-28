package com.xuJinghao_34680535.nutritrack.data.fruit

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Response

// Define the API request interface
interface APIService {

    // Fetches detailed information about a specific fruit.
    @GET("api/fruit/{name}")
    suspend fun getFruitByName(
        @Path("name") name: String // Use the @Path annotation to replace {name} in the URL
    ): Response<FruitResponse>

    // Companion object for creating Retrofit instance
    companion object {
        // Base URL for the API
        var BASE_URL = "https://www.fruityvice.com/"

        // Creates an instance of APIService.
        fun create(): APIService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())  // Use Gson to parse JSON
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(APIService::class.java)
        }
    }
}
