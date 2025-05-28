package com.xuJinghao_34680535.nutritrack.ui.homeactivity

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.xuJinghao_34680535.nutritrack.data.AuthManager
import com.xuJinghao_34680535.nutritrack.data.patient.PatientsViewModel
import com.xuJinghao_34680535.nutritrack.ui.loginactivity.LoginActivity

@Composable
fun SettingsScreen(
    patientsViewModel: PatientsViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val loggedInUserId = AuthManager.getUserId().toString()
    val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    var showClinicianDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val patient by patientsViewModel.patient.observeAsState(initial = null)
    LaunchedEffect(true) {
        patientsViewModel.getPatientByPatientId(loggedInUserId)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        ) {
        Text(
            // Title
            "Setting",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // User information section
            Text("ACCOUNT", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            // Display user details
            UserDetailItem(icon = Icons.Default.Person, label = patient?.name.toString())
            UserDetailItem(icon = Icons.Default.Phone, label = patient?.phoneNumber.toString())
            UserDetailItem(icon = Icons.Default.Badge, label = loggedInUserId)

            Spacer(modifier = Modifier.height(24.dp))

            // Settings options section
            Text("OTHER SETTINGS", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            // Logout settings item
            SettingsItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                label = "Logout",
                onClick = {
                    showLogoutDialog = true
                }
            )
            // Logout confirmation dialog
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text("Confirm Logout") },
                    text = { Text("Are you sure you want to log out?") },
                    confirmButton = {
                        TextButton(onClick = {
                            // Perform logout operation
                            with(sharedPreferences.edit()) {
                                putString("logged_in_user_id", null)
                                apply()
                            }
                            AuthManager.logout()
                            Toast.makeText(context, "Log out successful!", Toast.LENGTH_SHORT).show()

                            // Navigate to the login screen after logout
                            context.startActivity(Intent(context, LoginActivity::class.java))
                            showLogoutDialog = false
                        },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E44AD))
                        ) {
                            Text("Confirm", color = Color.White)
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showLogoutDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                        ) {
                            Text("Cancel", color = Color.Black)
                        }
                    }
                )
            }

            // Clinician login settings item
            SettingsItem(
                icon = Icons.Default.Person,
                label = "Clinician Login",
                onClick = {
                    showClinicianDialog = true
                }
            )

            // Clinician login dialog
            if (showClinicianDialog) {
                Dialog(onDismissRequest = { showClinicianDialog = false }) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        ClinicianLoginContent(onDismiss = { showClinicianDialog = false }, navController)
                    }
                }
            }
        }
    }
}

// Composable function to display a user detail item with an icon and label
@Composable
fun UserDetailItem(icon: ImageVector, label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Display the icon
        Icon(icon, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        // Display the label
        Text(text = label, fontSize = 16.sp)
    }
}

// Composable function to display a clickable settings item with an icon, label, and chevron
@Composable
fun SettingsItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF8E44AD), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
    }
}

// Composable function for the clinician login dialog content
@Composable
fun ClinicianLoginContent(onDismiss: () -> Unit, navController: NavController) {
    var key by remember { mutableStateOf("") }
    val context = LocalContext.current
    var keyVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dialog title
        Text("Clinician Login", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // Text field for entering clinician key
        OutlinedTextField(
            value = key,
            onValueChange = { key = it },
            label = { Text("Clinician Key") },
            placeholder = { Text("Enter your clinician key") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (keyVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                // Toggle key visibility
                val image = if (keyVisible)
                    Icons.Default.Visibility
                else Icons.Default.VisibilityOff

                IconButton(onClick = { keyVisible = !keyVisible }) {
                    Icon(imageVector = image, contentDescription = if (keyVisible) "Hide password" else "Show password")
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Button to submit clinician login
        Button(
            onClick = {
                if (key == "dollar-entry-apples") {
                    // Validate clinician key
                    Toast.makeText(context, "Clinician login successful", Toast.LENGTH_SHORT).show()
                    onDismiss()
                    // Navigate to admin screen on successful login
                    navController.navigate("admin")
                } else {
                    Toast.makeText(context, "Invalid key", Toast.LENGTH_SHORT).show()
                }
                key = ""
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Clinician Login")
        }
    }
}