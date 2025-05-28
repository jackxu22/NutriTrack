package com.xuJinghao_34680535.nutritrack.ui.homeactivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.xuJinghao_34680535.nutritrack.data.ai.GenAIViewModel
import com.xuJinghao_34680535.nutritrack.data.ai.UiState
import com.xuJinghao_34680535.nutritrack.data.patient.PatientsViewModel

@Composable
fun AdminScreen(
    patientsViewModel: PatientsViewModel,
    navController: NavController,
    genAiViewModel: GenAIViewModel = viewModel()
) {
    // Observe average HEIFA scores for male and female patients
    val averageHeifaScoreMale by patientsViewModel.averageHeifaScoreMale.observeAsState(initial = null)
    val averageHeifaScoreFemale by patientsViewModel.averageHeifaScoreFemale.observeAsState(initial = null)

    // Load average HEIFA scores and all patient data when the composable is first composed
    LaunchedEffect(true) {
        patientsViewModel.loadAverageHeifaScores()
        patientsViewModel.getAllPatientsData()
    }

    // Observe the list of all patients
    val patients by patientsViewModel.allPatients.observeAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Text("Clinician Dashboard", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        // Display average HEIFA scores
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Average HEIFA (Male):  $averageHeifaScoreMale",
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 4.dp),
            )
            Text(
                text = "Average HEIFA (Female):  $averageHeifaScoreFemale",
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Button to trigger AI pattern analysis
        Button(
            onClick = {
                genAiViewModel.getPatterns(patients)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Find Data Pattern")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Observe the UI state for AI-generated insights
        val uiState by genAiViewModel.uiState.collectAsState()

        // Display GenAI insights section
        Text(
            text = "GenAI Insights:",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

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
                // Display a placeholder for the initial state
                Text(
                    text = "Press 'Find Data Pattern' to generate insights",
                    fontSize = 14.sp,
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
                    }
                    else -> {} // Initial and Loading are already handled
                }
                // Format and display the result text
                val formattedMessage = formatMessageWithBoldText(result)
                Text(
                    text = formattedMessage,
                    fontSize = 14.sp,
                    color = textColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
            }
        }

        // Button to navigate back to settings screen
        Button(
            onClick = {
                navController.navigate("settings")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Done")
        }
    }
}

// Function to format a message with bold text for specific patterns (e.g., text between **)
fun formatMessageWithBoldText(message: String): AnnotatedString {
    return buildAnnotatedString {
        // Regular expression to match text between ** for bold formatting
        val boldRegex = "\\*\\*(.*?)\\*\\*".toRegex()
        val matches = boldRegex.findAll(message)
        var lastEnd = 0
        for (match in matches) {
            // Append regular text before the bold section
            append(message.substring(lastEnd, match.range.first))

            // Append bold text with specified style
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(match.groupValues[1])
            }
            lastEnd = match.range.last + 1
        }
        // Append any remaining regular text after the last bold section
        append(message.substring(lastEnd))
    }
}