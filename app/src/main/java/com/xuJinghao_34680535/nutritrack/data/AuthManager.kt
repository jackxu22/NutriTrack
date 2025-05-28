package com.xuJinghao_34680535.nutritrack.data


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

/**
  */
object AuthManager {
    val _userId: MutableState<String?> = mutableStateOf(null)


    fun login(userId: String) {
        _userId.value = userId
    }

    fun logout() {
        _userId.value = null
    }

    fun getUserId(): String? {
        return _userId.value
    }
}

