package com.xuJinghao_34680535.nutritrack.ui.homeactivity

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.xuJinghao_34680535.nutritrack.data.patient.PatientsViewModel
import com.xuJinghao_34680535.nutritrack.data.AuthManager

@Composable
fun InsightsScreen(
    patientsViewModel: PatientsViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val loggedInUserId = AuthManager.getUserId().toString()
    val patient by patientsViewModel.patient.observeAsState(initial = null)
    LaunchedEffect(true) {
        patientsViewModel.getPatientByPatientId(loggedInUserId)
    }
    // Get scores from patient
    val maxTotalScore = 100
    val totalScore = patient?.totalScore ?: 0f

    // List of food categories with scores
    val foodCategories = listOf(
        FoodCategory("Discretionary Foods", patient?.discretionaryScore ?: 0f, 10),
        FoodCategory("Vegetables", patient?.vegetableScore ?: 0f, 10),
        FoodCategory("Fruits", patient?.fruitScore ?: 0f, 10),
        FoodCategory("Grains & Cereals", patient?.grainScore ?: 0f, 5),
        FoodCategory("Whole Grains", patient?.wholeGrainScore ?: 0f, 5),
        FoodCategory("Meat & Alternatives", patient?.meatScore ?: 0f, 10),
        FoodCategory("Dairy", patient?.dairyScore ?: 0f, 10),
        FoodCategory("Sodium", patient?.sodiumScore ?: 0f, 10),
        FoodCategory("Alcohol", patient?.alcoholScore ?: 0f, 5),
        FoodCategory("Water", patient?.waterScore ?: 0f, 5),
        FoodCategory("Sugar", patient?.sugarScore ?: 0f, 10),
        FoodCategory("Saturated Fats", patient?.saturatedFatScore ?: 0f, 5),
        FoodCategory("Unsaturated Fats", patient?.unsaturatedFatScore ?: 0f, 5),
    )

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
            ) {
            // Title
            Text(
                text = "Insights: Food Score",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Display progress bars for each food category
            foodCategories.forEach { category ->
                FoodCategoryRow(category = category)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Total score section
            Text(
                text = "Total Food Quality Score",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar and total score
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { (totalScore.toFloat() / maxTotalScore.toFloat()) },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp),
                    color = Color(0xFF6A1B9A),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$totalScore/$maxTotalScore",
                    fontSize = 12.sp,
                    modifier = Modifier.width(70.dp),
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "text/plain"
                        val shareText = "Hi, I just got a HEIFA score of $totalScore!"
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
                        context.startActivity(
                            Intent.createChooser(
                                shareIntent,
                                "Share text via"
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
                    modifier = Modifier.width(200.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Share with someone", color = Color.White, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        navController.navigate("nutrition") { // Navigate to NutriCoachScreen
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
                    modifier = Modifier.width(200.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = "Improve diet",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Improve my diet!", color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}
