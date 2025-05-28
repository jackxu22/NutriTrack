package com.xuJinghao_34680535.nutritrack.ui.loginactivity

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
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xuJinghao_34680535.nutritrack.data.patient.PatientsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ForgotPasswordScreen(
    patientsViewModel: PatientsViewModel,
    onPasswordResetSuccess: () -> Unit,
    onSwitchToLogin: () -> Unit
) {
    val context = LocalContext.current

    // Observe LiveData for selected patient and unregistered user IDs
    val patient by patientsViewModel.patient.observeAsState(initial = null)
    val registeredUserIds by patientsViewModel.registeredUserIds.observeAsState(initial = emptyList())

    // Form states of user input
    var selectedUserId by rememberSaveable { mutableStateOf("") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    // Error states
    var userIdError by remember { mutableStateOf(false) }
    var phoneNumberError by remember { mutableStateOf(false) }
    var newPasswordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }

    // Password visibility toggles
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // General error message to show
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
            text = "Reset Password",
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // User ID dropdown (only registered user)
        UserIdDropdown(
            userIds = registeredUserIds,
            selectedUserId = selectedUserId,
            onUserIdSelected = { newUserId ->
                selectedUserId = newUserId
                userIdError = newUserId.isEmpty()
                if (newUserId.isNotEmpty()) {
                    patientsViewModel.getPatientByPatientId(newUserId)
                }
            },
            isError = userIdError,
            label = "My ID (Provided by your Clinician)"
        )

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

        // Phone number input
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it
                phoneNumberError = !isValidPhoneNumber(it)
            },
            label = {
                Text(
                    text = "Phone Number",
                    fontSize = 14.sp
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = phoneNumberError,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Enter your phone number") }
        )

        if (phoneNumberError) {
            Text(
                text = "Phone number must be 11 digits",
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // New password input
        OutlinedTextField(
            value = newPassword,
            onValueChange = {
                newPassword = it
                newPasswordError = !isValidPassword(it)
            },
            label = {
                Text(
                    text = "New Password",
                    fontSize = 14.sp
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = newPasswordError,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Enter your new password") },
            trailingIcon = {
                val icon = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                val description = if (newPasswordVisible) "Hide password" else "Show password"

                IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                    Icon(imageVector = icon, contentDescription = description)
                }
            }
        )

        if (newPasswordError) {
            Text(
                text = "Password must be at least 8 chars with letters and numbers",
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm password input
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                confirmPasswordError = it.isEmpty() || it != newPassword
            },
            label = {
                Text(
                    text = "Confirm New Password",
                    fontSize = 14.sp
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = confirmPasswordError,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Enter your new password again") },
            trailingIcon = {
                val icon = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                val description = if (confirmPasswordVisible) "Hide password" else "Show password"

                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(imageVector = icon, contentDescription = description)
                }
            }
        )

        if (confirmPasswordError) {
            Text(
                text = if (confirmPassword.isEmpty()) "Please enter password again" else "Two passwords are inconsistent",
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        // General error message if applicable
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

        // Submit button
        val scope = rememberCoroutineScope()
        Button(
            onClick = {
                // Validate inputs
                userIdError = selectedUserId.isEmpty()
                phoneNumberError = !isValidPhoneNumber(phoneNumber)
                newPasswordError = !isValidPassword(newPassword)
                confirmPasswordError = confirmPassword.isEmpty() || confirmPassword != newPassword

                // If all valid, proceed
                if (!userIdError && !phoneNumberError && !newPasswordError && !confirmPasswordError) {
                    scope.launch(Dispatchers.IO) {
                        if (patient == null) {
                            patientsViewModel.getPatientByPatientId(selectedUserId)
                        }

                        // Verify reset password
                        withContext(Dispatchers.Main) {
                            if (patient == null) {
                                errorMessage = "Invalid user ID"
                            } else if (patient?.phoneNumber != phoneNumber) {
                                errorMessage = "Phone number doesn't match"
                                phoneNumber = ""
                                newPassword = ""
                                confirmPassword = ""
                            } else if (patient?.password?.isEmpty() == true) {
                                errorMessage = "This user is already registered"
                                selectedUserId = ""
                                phoneNumber = ""
                                newPassword = ""
                                confirmPassword = ""
                            } else {
                                // Reset password successful
                                errorMessage = null
                                val hashedPassword = hashPassword(newPassword)
                                patientsViewModel.updatePatient(
                                    patient!!.copy(
                                        password = hashedPassword
                                    )
                                )
                                Toast.makeText(context, "Password reset successful！", Toast.LENGTH_SHORT).show()
                                selectedUserId = ""
                                phoneNumber = ""
                                newPassword = ""
                                confirmPassword = ""
                                onPasswordResetSuccess()
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Text("Reset Password")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Back to login screen
        Button(
            onClick = { onSwitchToLogin() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Text("Back to Login")
        }
    }
}