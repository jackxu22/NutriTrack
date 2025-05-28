package com.xuJinghao_34680535.nutritrack.data.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.xuJinghao_34680535.nutritrack.BuildConfig
import com.xuJinghao_34680535.nutritrack.data.patient.Patient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GenAIViewModel : ViewModel() {

    // Mutable state flow to hold the UI state
    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    // Expose the UI state as a StateFlow for observation
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Initialize the Generative AI model with model name and API key
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GENAI_API_KEY,
    )

    // Function to generate a motivational message to improve a patient's fruit intake
    fun getMotivationalMessage(patient: Patient?, selectedFoods: List<String>) {

        // Set UI state to Loading
        _uiState.value = UiState.Loading

        // Build the prompt for the AI model
        val prompt = buildString {
            append("Generate a short encouraging message for a patient to improve their fruit intake.\n")
            append("Current HEIFA fruit score is : ${patient?.fruitScore} and the full score is 10 points.\n")
            append("Current food intake selections are : ${selectedFoods}, this is the food selected by the current patient that he or she like to eat\n")
            append("And current patient's name is : ${patient?.name}\n")
            append("Based on this, generate a personalized message to help improve their fruit intake.")
        }

        // Launch a coroutine in the IO dispatcher for network operations
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Generate content using the AI model
                val response = generativeModel.generateContent(
                    content { text(prompt) }
                )
                // Update UI state with the response or a fallback message
                withContext(Dispatchers.Main) {
                    response.text?.let { output ->
                        _uiState.value = UiState.Success(output)
                    } ?: run {
                        _uiState.value = UiState.Error("No message received.")
                    }
                }
            } catch (e: Exception) {
                // Handle errors and update UI state with the error details
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    // Function to identify patterns in patient HEIFA score data
    fun getPatterns(patient: List<Patient>?) {
        // Set UI state to Loading
        _uiState.value = UiState.Loading

        // Build the prompt for the AI model to analyze patient data
        val prompt = buildString {
            append("This is all the data of all the patient about HEIFA scores.\n")
            append(patient)
            append("Based on this, generate 3 interesting patterns from the data about HEIFA.\n")
            append("You should only focus on \"Score\" in the patient data.\n")
            append("This is an very easy example for reference only :\n")
            append("Users with high vegetable scores also scored high in fruit\n")
            append("Females showed greater dietary variety\n")
            append("Write it in the form of 1:...2:...,3:...\n")
        }

        // Launch a coroutine in the IO dispatcher for network operations
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Generate content using the AI model
                val response = generativeModel.generateContent(
                    content { text(prompt) }
                )
                // Update UI state with the response or a fallback message
                withContext(Dispatchers.Main) {
                    response.text?.let { output ->
                        _uiState.value = UiState.Success(output)
                    } ?: run {
                        _uiState.value = UiState.Error("No message received.")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}