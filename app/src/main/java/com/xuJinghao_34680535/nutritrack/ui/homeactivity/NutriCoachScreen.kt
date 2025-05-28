package com.xuJinghao_34680535.nutritrack.ui.homeactivity

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.xuJinghao_34680535.nutritrack.data.AuthManager
import com.xuJinghao_34680535.nutritrack.data.ai.GenAIViewModel
import com.xuJinghao_34680535.nutritrack.data.ai.UiState
import com.xuJinghao_34680535.nutritrack.data.foodIntake.FoodIntakeViewModel
import com.xuJinghao_34680535.nutritrack.data.fruit.FruitViewModel
import com.xuJinghao_34680535.nutritrack.data.nutriCoachTips.NutriCoachTipsViewModel
import com.xuJinghao_34680535.nutritrack.data.patient.PatientsViewModel

@Composable
fun NutriCoachScreen(
    patientsViewModel: PatientsViewModel,
    nutriCoachTipsViewModel: NutriCoachTipsViewModel,
    foodIntakeViewModel: FoodIntakeViewModel,
    fruitViewModel: FruitViewModel,
    genAiViewModel: GenAIViewModel = viewModel()
) {
    val loggedInUserId = AuthManager.getUserId().toString()

    // Observe the list of all nutrition tips
    val allTips by nutriCoachTipsViewModel.allTips.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    // Fetch food intake data for the logged-in user
    LaunchedEffect(true) {
        foodIntakeViewModel.getFoodIntakesByPatientId(loggedInUserId)
    }

    val fruitInfo by fruitViewModel.fruitInfo.observeAsState()
    val errorMessage by fruitViewModel.errorMessage.observeAsState("")
    val isLoading by fruitViewModel.isLoading.observeAsState(false)
    var fruitNameInput by rememberSaveable { mutableStateOf("") }
    val imageUrl by remember { mutableStateOf("https://picsum.photos/400/300") }

    // Observe food intake data from the database
    val foodIntakes by foodIntakeViewModel.foodIntakes.observeAsState(initial = emptyList())
    val selectedFood = foodIntakes
        .filter { it.response }
        .map { it.category }

    // Observe patient data for the logged-in user
    val patient by patientsViewModel.patient.observeAsState(initial = null)
    // Fetch patient data for the logged-in user
    LaunchedEffect(true) {
        patientsViewModel.getPatientByPatientId(loggedInUserId)
    }
    // Get the fruit variations score from patient data
    val fruitVariationsScore = patient?.fruitVariationsScore ?: 0f
    val fruitServeSize = patient?.fruitServeSize ?: 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        // Fruits Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                // Title
                "NutriCoach",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            // Conditional display based on fruitVariationsScore and fruitServeSize
            if (fruitVariationsScore >= 5 && fruitServeSize >= 2) {
                // Display for optimal fruit intake score
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Your fruit intake score is optimal!",
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Display a random image for optimal score
                    Image(
                        painter = rememberAsyncImagePainter(model = imageUrl),
                        contentDescription = "Random Image",
                        modifier = Modifier
                            .width(300.dp)
                            .height(200.dp)
                    )
                }
            } else {
                // Display for non-optimal fruit intake score with fruit search
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Fruit name",
                        textAlign = TextAlign.Left,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Row for fruit name input and search button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = fruitNameInput,
                            onValueChange = { fruitNameInput = it },
                            label = { Text("Enter fruit name") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Button(
                            onClick = {
                                fruitViewModel.fetchFruitInfo(fruitNameInput)
                            },
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search Icon",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Details")
                        }
                    }
                    // Display error message if any
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 16.sp
                        )
                    }
                    // Display fruit information if available
                    fruitInfo?.let { info ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp), // 缩小上下 padding
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {

                                // Display each nutrition fact using RowItem
                                RowItem(label = "Family", value = info.family)
                                RowItem(label = "Calories", value = "${info.nutritions.calories}")
                                RowItem(label = "Fat", value = "${info.nutritions.fat} g")
                                RowItem(label = "Sugar", value = "${info.nutritions.sugar} g")
                                RowItem(
                                    label = "Carbohydrates",
                                    value = "${info.nutritions.carbohydrates} g"
                                )
                                RowItem(label = "Protein", value = "${info.nutritions.protein} g")
                            }
                        }
                    }
                }
            }
            // AI-generated tips Section
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Observe the UI state for AI-generated message
                val uiState by genAiViewModel.uiState.collectAsState()
                // Handle different UI states
                when (uiState) {
                    is UiState.Loading -> {
                        // Display a loading indicator
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 12.dp)
                        )
                    }
                    is UiState.Initial -> {
                        // Display nothing or a placeholder for the initial state
                        Text(
                            text = "Press a button to generate a motivational message",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        )
                    }
                    else -> {
                        // Handle Success and Error states
                        var textColor = MaterialTheme.colorScheme.onSurface
                        var result = ""
                        when (uiState) {
                            is UiState.Error -> {
                                textColor = MaterialTheme.colorScheme.error
                                result = (uiState as UiState.Error).errorMessage
                            }
                            is UiState.Success -> {
                                textColor = MaterialTheme.colorScheme.onSurface
                                result = (uiState as UiState.Success).outputText
                                // Save generated message to database
                                LaunchedEffect(result) {
                                    if (result.isNotEmpty()) {
                                        nutriCoachTipsViewModel.insertTip(loggedInUserId, result)
                                    }
                                }
                            }
                            else -> {} // Initial and Loading are already handled
                        }
                        // Display the result text with scrolling support
                        Text(
                            text = result,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            color = textColor,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        )
                    }
                }

                // Buttons for motivational message and tips history
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Button to generate a motivational message
                    Button(
                        onClick = {
                            genAiViewModel.getMotivationalMessage(patient, selectedFood)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = "AI Tip", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Motivational Message")
                    }

                    // Button to show all nutrition tips
                    Button(
                        onClick = {
                            nutriCoachTipsViewModel.getAllTips(loggedInUserId)
                            showDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Show Tips", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Show All Tips")
                    }

                    // Dialog to display historical nutrition tips
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = { Text("Nutrition Tips History") },
                            text = {
                                Column(
                                    modifier = Modifier
                                        .heightIn(max = 300.dp)
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    if (!allTips.isEmpty()) {
                                        allTips.forEach { tip ->
                                            Text(
                                                text = "• ${tip.tip}",
                                                modifier = Modifier.fillMaxWidth(),
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = { showDialog = false }) {
                                    Text("Close")
                                }
                            }
                        )
                    }
                }
            }

        }
    }
}

// Composable function to display a labeled value in a row
@Composable
fun RowItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "$label:", fontSize = 12.sp)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}


