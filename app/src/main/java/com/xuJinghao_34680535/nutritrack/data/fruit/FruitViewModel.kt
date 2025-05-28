package com.xuJinghao_34680535.nutritrack.data.fruit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * ViewModel to handle fruit information data and survive configuration changes
 */

class FruitViewModel() :ViewModel() {

    // Create instance of repository
    private val fruitRepository = FruitRepository()

    // LiveData to hold fruit information
    private val _fruitInfo = MutableLiveData<FruitResponse?>(null)
    val fruitInfo: LiveData<FruitResponse?> = _fruitInfo

    // LiveData to hold error messages
    private val _errorMessage = MutableLiveData<String>("")
    val errorMessage: LiveData<String> = _errorMessage

    // LiveData to track loading state
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * Fetch fruit information from API
     * @param fruitName The name of the fruit to fetch
     * @return Boolean indicating whether request is valid and initiated
     */
    fun fetchFruitInfo(fruitName: String) {
        // Input validation
        if (fruitName.isEmpty()) {
            _errorMessage.postValue("Please enter a fruit name")
            clearFruitInfo()
            return
        } else if (!fruitName.matches(Regex("^[a-zA-Z]+$"))) {
            _errorMessage.postValue("Fruit name must contain only letters")
            clearFruitInfo()
            return
        }

        // Set loading state
        _isLoading.postValue(true)
        _errorMessage.postValue("")

        // Launch coroutine to fetch data
        viewModelScope.launch {
            try {
                val result = fruitRepository.getFruitByName(fruitName.lowercase())
                if (result != null) {
                    _fruitInfo.postValue(result)
                    _errorMessage.postValue("")
                } else {
                    clearFruitInfo()
                    _errorMessage.postValue("Failed to fetch fruit data")
                }
            } catch (e: Exception) {
                clearFruitInfo()
                _errorMessage.postValue("Error: ${e.message ?: "Unknown error occurred"}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    /**
     * Clear fruit information data
     */
    fun clearFruitInfo() {
        _fruitInfo.postValue(null)
    }

    class FruitViewModelFactory() : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            FruitViewModel() as T
    }
}