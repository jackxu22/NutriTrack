package com.xuJinghao_34680535.nutritrack.ui.loginactivity

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xuJinghao_34680535.nutritrack.data.AuthManager
import com.xuJinghao_34680535.nutritrack.data.patient.PatientsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(
    patientsViewModel: PatientsViewModel,
    onLoginSuccess: (String) -> Unit,
    onSwitchToRegister: () -> Unit,
    onSwitchToForgotPassword: () -> Unit
) {
    val context = LocalContext.current

    // Observe LiveData for the selected patient and list of registered user IDs
    val patient by patientsViewModel.patient.observeAsState(initial = null)
    val userIds by patientsViewModel.registeredUserIds.observeAsState(initial = emptyList())

    // State variables for selected user ID and password
    var selectedUserId by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    // Validation and UI state flags
    var userIdError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // SharedPreferences used to persist login session
    val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = "Login",
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown for selecting registered user ID
        UserIdDropdown(
            userIds = userIds,
            selectedUserId = selectedUserId,
            onUserIdSelected = { newUserId ->
                selectedUserId = newUserId
                userIdError = newUserId.isEmpty()
                if (newUserId.isNotEmpty()) {
                    patientsViewModel.getPatientByPatientId(newUserId) // Fetch patient info from DB
                }
            },
            isError = userIdError,
            label = "My ID (Provided by your Clinician)"
        )

        // Show error if no user ID selected
        if (userIdError) {
            Text(
                text = "Please select user ID",
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Password input field with visibility toggle
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text(
                    text = "Password",
                    fontSize = 14.sp
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = passwordError,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Enter your password") },
            trailingIcon = {
                // Toggle for password visibility
                val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = description)
                }
            }
        )

        // Show error for invalid password
        if (passwordError) {
            Text(
                text = "Enter your password",
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        // Display any other login errors
        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Instructional text
        Text(
            text = "This app is only for pre-registered users. Please enter your ID and password" +
                    " or Register to claim your account on your first visit.",
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        // Login button
        val scope = rememberCoroutineScope()
        Button(
            onClick = {
                userIdError = selectedUserId.isEmpty()
                passwordError = password.isEmpty()

                // Validate and process login
                if (!userIdError && !passwordError) {
                    scope.launch(Dispatchers.IO) {
                        if (patient == null) {
                            patientsViewModel.getPatientByPatientId(selectedUserId)
                        }
                        // Verify login
                        withContext(Dispatchers.Main) {
                            if (selectedUserId.isEmpty()) {
                                errorMessage = "Invalid user ID"
                            } else if (password.isBlank()) {
                                errorMessage = "Password cannot be empty"
                            } else if (patient == null) {
                                errorMessage = "Invalid user"
                            } else if (patient?.password.isNullOrEmpty()) {
                                errorMessage = "This user is not registered, please register first"
                            } else {
                                val hashedPassword = hashPassword(password)
                                if (patient?.password != hashedPassword) {
                                    errorMessage = "Wrong password"
                                    password = ""
                                } else {
                                    errorMessage = null
                                    // Save login session and notify success
                                    AuthManager.login(patient!!.userId)
                                    with(sharedPreferences.edit()) {
                                        putString("logged_in_user_id", patient!!.userId)
                                        apply()
                                    }
                                    Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess(selectedUserId)
                                }
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        // Button to switch to registration screen
        Button(
            onClick = { onSwitchToRegister() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        // Button to switch to forgot password screen
        Button(
            onClick = { onSwitchToForgotPassword() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Forgot password?")
        }
    }
}