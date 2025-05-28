package com.xuJinghao_34680535.nutritrack.ui.questionnaireactivity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xuJinghao_34680535.nutritrack.ui.theme.NutriTrackTheme
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import java.util.Calendar
import androidx.compose.foundation.Image
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xuJinghao_34680535.nutritrack.R
import com.xuJinghao_34680535.nutritrack.data.AuthManager
import com.xuJinghao_34680535.nutritrack.data.foodIntake.FoodIntakeViewModel
import com.xuJinghao_34680535.nutritrack.data.personaTime.PersonaTimeViewModel
import com.xuJinghao_34680535.nutritrack.ui.homeactivity.HomeActivity
import com.xuJinghao_34680535.nutritrack.ui.loginactivity.LoginActivity

class QuestionnaireActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutriTrackTheme {
                QuestionnaireScreen()
            }
        }
    }
}

@Composable
fun QuestionnaireScreen() {
    // Retrieve the currently authenticated user's ID as patientId
    val patientId = AuthManager.getUserId().toString()
    val context = LocalContext.current

    val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    // Initialize FoodIntake ViewModel
    val foodIntakeViewModel: FoodIntakeViewModel = viewModel(
        factory = FoodIntakeViewModel.FoodIntakeViewModelFactory(context)
    )
    // Load the list of food intakes associated with the current patientId
    LaunchedEffect(patientId) {
        foodIntakeViewModel.getFoodIntakesByPatientId(patientId)
    }

    // State to track which food categories are selected by the user
    val selectedFoods = rememberSaveable { mutableStateOf<Set<String>>(emptySet()) }

    // Observe foodIntakes LiveData from the ViewModel
    val foodIntakes by foodIntakeViewModel.foodIntakes.observeAsState(initial = emptyList())

    // Initialize selectedFoods once when foodIntakes is first loaded
    LaunchedEffect(foodIntakes) {
        if (selectedFoods.value.isEmpty()) {
            selectedFoods.value = foodIntakes
                .filter { it.response } // Only include items marked as selected/true
                .map { it.category } // Extract the category field
                .toSet() // Store as a set for quick lookup
        }
    }

    // Initialize PersonaTime ViewModel
    val personaTimeViewModel: PersonaTimeViewModel = viewModel(
        factory = PersonaTimeViewModel.PersonaTimeViewModelFactory(context)
    )
    // Load persona time data for the current patient ID
    LaunchedEffect(true) {
        personaTimeViewModel.getPersonaTimeByPatientId(patientId)
    }
    // Observe persona time LiveData from ViewModel
    val personaTime by personaTimeViewModel.personaTime.observeAsState()
    // Initialize state variables for persona selection and time settings
    var selectedPersona by remember { mutableStateOf("") }
    var biggestMealTime by remember { mutableStateOf("00:00") }
    var sleepTime by remember { mutableStateOf("00:00") }
    var wakeUpTime by remember { mutableStateOf("00:00") }
    // Update UI state variables when persona time data is loaded
    LaunchedEffect(personaTime) {
        personaTime?.let {
            selectedPersona = it.persona
            biggestMealTime = it.biggestMealTime
            sleepTime = it.sleepTime
            wakeUpTime = it.wakeUpTime
        }
    }

    // Manage dialog visibility state
    var showPersonaDialog by remember { mutableStateOf<String?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Mapping of persona names to image resources for the information dialogs
    val personaImages = mapOf(
        "Health Devotee" to R.drawable.persona_1,
        "Mindful Eater" to R.drawable.persona_2,
        "Wellness Striver" to R.drawable.persona_3,
        "Balance Seeker" to R.drawable.persona_4,
        "Health Procrastinator" to R.drawable.persona_5,
        "Food Carefree" to R.drawable.persona_6
    )

    // Mapping of persona names to descriptions
    val personaDescriptions = mapOf(
        "Health Devotee" to "I'm passionate about healthy eating and it's a big part in my life. I use social media to share my healthy lifestyle, get new recipes/exercise ideas. I may even buy superfoods or follow a particular type of diet. I like to think I am super healthy.",
        "Mindful Eater" to "I’m health-conscious and being healthy and eating healthy is important to me. Although health means different things to different people, I make conscious lifestyle decisions about eating based on what I believe healthy means. I look for new recipes and healthy eating information on social media.\n",
        "Wellness Striver" to "I aspire to be healthy (but struggle sometimes). Healthy eating is hard work! I’ve tried to improve my diet, but always find things that make it difficult to stick with the changes. Sometimes I notice recipe ideas or healthy eating hacks, and if it seems easy enough, I’ll give it a go.\n",
        "Balance Seeker" to "I try and live a balanced lifestyle, and I think that all foods are okay in moderation. I shouldn’t have to feel guilty about eating a piece of cake now and again. I get all sorts of inspiration from social media like finding out about new restaurants, fun recipes and sometimes healthy eating tips.\n",
        "Health Procrastinator" to "I’m contemplating healthy eating but it’s not a priority for me right now. I know the basics about what it means to be healthy, but it doesn’t seem relevant to me right now. I have taken a few steps to be healthier but I am not motivated to make it a high priority because I have too many other things going on in my life.\n",
        "Food Carefree" to "I’m not bothered about healthy eating. I don’t really see the point and I don’t think about it. I don’t really notice healthy eating tips or recipes and I don’t care what I eat."
    )

    // Define static lists for food categories and personas
    val foodCategories = listOf(
        "Fruits", "Vegetables", "Grains",
        "Red Meat", "Seafood", "Poultry",
        "Fish", "Eggs", "Nuts/Seeds"
    )
    val personas = listOf(
        "Health Devotee", "Mindful Eater", "Wellness Striver",
        "Balance Seeker", "Health Procrastinator", "Food Carefree"
    )

    // Main layout column with scrolling
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
            .imePadding()
    ) {
        // Space for status bar
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
        )
        // Header row with back button and title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Back button
            IconButton(
                onClick = {
                    showLogoutDialog = true
                },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            // Logout confirmation dialog
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text("Confirm Logout") },
                    text = { Text("Are you sure you want to log out?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                // Perform logout operation
                                with(sharedPreferences.edit()) {
                                    putString("logged_in_user_id", null)
                                    apply()
                                }
                                AuthManager.logout()
                                Toast.makeText(context, "Log out successful!", Toast.LENGTH_SHORT)
                                    .show()

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

            // Title
            Text(
                text = "Food Intake Questionnaire",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(align = Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )

            // Spacer for symmetry
            Spacer(modifier = Modifier.size(24.dp))
        }

        // Food categories section
        Text(
            text = "Tick all the food categories you CAN eat",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Display food categories in 3 rows
        foodCategories.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowItems.forEach { category ->
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedFoods.value.contains(category),
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    selectedFoods.value = selectedFoods.value + category
                                } else {
                                    selectedFoods.value = selectedFoods.value - category
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(0.dp))
                        Text(
                            text = category,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Persona selection section
        Text(
            text = "Your Persona",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        Text(
            text = "People can be broadly classified into 6 different types based on their eating preferences. Click on each button below to find out the different types, and select the type that best fits you!",
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        // Display persona buttons in 3 rows
        personas.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowItems.forEach { persona ->
                    Button(
                        onClick = { showPersonaDialog = persona },
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(horizontal = 2.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6A4FAF)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = persona,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Persona dropdown
        Text(
            text = "Which persona best fits you?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        PersonaDropdown(
            personas = personas,
            selectedPersona = selectedPersona,
            onPersonaSelected = { selectedPersona = it }
        )

        // Time selection section
        Text(
            text = "Timings",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
        )
        TimePickerRow(
            label = "What time of day (approx.) do you normally eat your biggest meal?",
            selectedTime = biggestMealTime,
            onTimeSelected = { biggestMealTime = it }
        )
        TimePickerRow(
            label = "What time of day (approx.) do you go to sleep at night?",
            selectedTime = sleepTime,
            onTimeSelected = { sleepTime = it }
        )
        TimePickerRow(
            label = "What time of day (approx.) do you wake up in the morning?",
            selectedTime = wakeUpTime,
            onTimeSelected = { wakeUpTime = it }
        )

        // Save button
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                // Validation logic to ensure all required fields are filled correctly
                if (selectedFoods.value.isEmpty()) {
                    Toast.makeText(context, "Please select at least one food", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (selectedPersona.isBlank()) {
                    Toast.makeText(context, "Persona cannot be empty", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (biggestMealTime == sleepTime || biggestMealTime == wakeUpTime || sleepTime == wakeUpTime) {
                    Toast.makeText(context, "Meal time, sleep time and wakeup time cannot be the same", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // Determine whether biggestMealTime is between sleepTime and wakeUpTime (i.e. sleep time period)
                fun isInSleepTime(meal: String, sleep: String, wake: String): Boolean {
                    val mealHour = meal.split(":")[0].toInt()
                    val sleepHour = sleep.split(":")[0].toInt()
                    val wakeHour = wake.split(":")[0].toInt()

                    return if (sleepHour < wakeHour) {
                        mealHour in sleepHour until wakeHour
                    } else {
                        mealHour >= sleepHour || mealHour < wakeHour
                    }
                }

                if (isInSleepTime(biggestMealTime, sleepTime, wakeUpTime)) {
                    Toast.makeText(context, "The biggestMealTime cannot be during sleeping time", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // Save all user selections to the database via ViewModels
                foodIntakeViewModel.updateFoodSelections(patientId, selectedFoods.value)
                personaTimeViewModel.updatePersonaTimeByPatientId(
                    patientId = patientId,
                    persona = selectedPersona,
                    biggestMealTime = biggestMealTime,
                    sleepTime = sleepTime,
                    wakeUpTime = wakeUpTime)
                // Mark questionnaire as completed in SharedPreferences
                with(sharedPreferences.edit()) {
                    putBoolean("questionnaire_completed_$patientId", true)
                    apply()
                }
                // Navigate to HomeActivity after successful save
                context.startActivity(Intent(context, HomeActivity::class.java))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6A4FAF)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = "Save",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Text("Save")
        }

        // Space for navigation bar
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .windowInsetsBottomHeight(WindowInsets.navigationBars)
        )
    }

    // Persona dialog
    showPersonaDialog?.let { persona ->
        AlertDialog(
            onDismissRequest = { showPersonaDialog = null },
            title = {
                Text(
                    text = persona,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Persona image
                    Image(
                        painter = painterResource(id = personaImages[persona]!!),
                        contentDescription = "$persona Icon",
                        modifier = Modifier
                            .size(100.dp)
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Persona description
                    Text(
                        text = personaDescriptions[persona]!!,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    // Dismiss button
                    Button(
                        onClick = {
                            showPersonaDialog = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6A4FAF)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Dismiss", color = Color.White, fontSize = 14.sp)
                    }
                }
            },
            // Empty Composable block
            confirmButton = {
            },
            dismissButton = {
            },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun PersonaDropdown(
    personas: List<String>,
    selectedPersona: String,
    onPersonaSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Dropdown container
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedPersona,
            onValueChange = {},
            readOnly = true,
            label = { Text("Select option") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown"
                    )
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            personas.forEach { persona ->
                DropdownMenuItem(
                    text = { Text(persona) },
                    onClick = {
                        onPersonaSelected(persona)
                        expanded = false
                    }
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun TimePickerRow(
    label: String,
    selectedTime: String,
    onTimeSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    // Time picker row layout
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .clickable {
                    TimePickerDialog(
                        context,
                        { _, selectedHour, selectedMinute ->
                            val time = String.format("%02d:%02d", selectedHour, selectedMinute)
                            onTimeSelected(time)
                        },
                        hour,
                        minute,
                        true
                    ).show()
                }
        ) {
            Icon(
                imageVector = Icons.Filled.AccessTime,
                contentDescription = "Time Picker",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = selectedTime,
                fontSize = 14.sp
            )
        }
    }
}