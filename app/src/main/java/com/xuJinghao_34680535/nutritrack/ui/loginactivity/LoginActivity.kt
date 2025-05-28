package com.xuJinghao_34680535.nutritrack.ui.loginactivity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.xuJinghao_34680535.nutritrack.ui.theme.NutriTrackTheme
import com.xuJinghao_34680535.nutritrack.data.AuthManager
import com.xuJinghao_34680535.nutritrack.ui.questionnaireactivity.QuestionnaireActivity
import com.xuJinghao_34680535.nutritrack.data.patient.PatientsViewModel
import com.xuJinghao_34680535.nutritrack.data.patient.PatientsViewModel.PatientsViewModelFactory
import com.xuJinghao_34680535.nutritrack.ui.homeactivity.HomeActivity
import java.security.MessageDigest

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Retrieve stored login status from SharedPreferences
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getString("logged_in_user_id", null) != null

        // ViewModel to access patient data
        val patientsViewModel: PatientsViewModel by viewModels {
            PatientsViewModelFactory(this)
        }

        if (isLoggedIn) {
            // If already logged in, check questionnaire completion status
            val loggedInUserId = sharedPreferences.getString("logged_in_user_id", null).toString()
            AuthManager.login(loggedInUserId) // Set current logged-in user in AuthManager

            // Check if questionnaire is already completed for this user
            val isQuestionnaireCompleted = sharedPreferences.getBoolean("questionnaire_completed_$loggedInUserId", false)

            if (isQuestionnaireCompleted) {
                // If questionnaire is completed, navigate directly to HomeActivity
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                // If questionnaire is not completed, navigate to QuestionnaireActivity
                startActivity(Intent(this, QuestionnaireActivity::class.java))
            }
            finish()
            return
        }

        // If not logged in, show login or register screen
        setContent {
            NutriTrackTheme {
                val navController = rememberNavController()

                Scaffold { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        // Login Screen
                        composable("login") {
                            LoginScreen(
                                patientsViewModel,
                                onLoginSuccess = { userId ->
                                    val isQuestionnaireCompleted = sharedPreferences.getBoolean("questionnaire_completed_$userId", false)

                                    if (isQuestionnaireCompleted) {
                                        // If questionnaire is completed, navigate directly to HomeActivity
                                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                                    } else {
                                        // If questionnaire is not completed, navigate to QuestionnaireActivity
                                        startActivity(Intent(this@LoginActivity, QuestionnaireActivity::class.java))
                                    }
                                    finish()
                                },
                                onSwitchToRegister = {
                                    navController.navigate("register")
                                },
                                onSwitchToForgotPassword = {
                                    navController.navigate("forgotPassword")
                                }
                            )
                        }

                        // Register Screen
                        composable("register") {
                            RegisterScreen(
                                patientsViewModel,
                                onRegisterSuccess = {
                                    // Back to Login Screen
                                    navController.navigate("login")
                                },
                                onSwitchToLogin = {
                                    // Back to Login Screen
                                    navController.navigate("login")
                                }
                            )
                        }

                        // Forgot Password Screen
                        composable("forgotPassword") {
                            ForgotPasswordScreen(
                                patientsViewModel,
                                onPasswordResetSuccess = {
                                    // Back to Login Screen
                                    navController.navigate("login")
                                },
                                onSwitchToLogin = {
                                    // Back to Login Screen
                                    navController.navigate("login")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserIdDropdown(
    userIds: List<String>,
    selectedUserId: String,
    onUserIdSelected: (String) -> Unit,
    isError: Boolean,
    label: String = "User ID"
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        // Display selected user ID in a read-only text field
        OutlinedTextField(
            value = selectedUserId,
            onValueChange = {},
            readOnly = true,
            label = {
                Text(
                    text = label,
                    fontSize = 14.sp
                )
            },
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            trailingIcon = {
                // Dropdown arrow icon
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Drop down"
                    )
                }
            }
        )
        // Dropdown menu with user ID options
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (userIds.isEmpty()) {
                // Show message when no users are available
                DropdownMenuItem(
                    text = { Text("No users available") },
                    onClick = { expanded = false }
                )
            } else {
                // Show list of available user IDs
                userIds.forEach { userId ->
                    DropdownMenuItem(
                        text = { Text(userId) },
                        onClick = {
                            onUserIdSelected(userId)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

// Validates that the phone number has exactly 11 digits
fun isValidPhoneNumber(phoneNumber: String): Boolean {
    return phoneNumber.matches(Regex("\\d{11}"))
}

// Hashes the given password using SHA-256 algorithm
fun hashPassword(password: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}

// Checks if the password is valid:
// At least 8 characters long, and contains both letters and digits
fun isValidPassword(password: String): Boolean {
    val hasLetter = password.any { it.isLetter() }
    val hasDigit = password.any { it.isDigit() }
    return password.length > 7 && hasLetter && hasDigit
}