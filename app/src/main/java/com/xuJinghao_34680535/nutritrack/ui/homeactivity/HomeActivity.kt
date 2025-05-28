package com.xuJinghao_34680535.nutritrack.ui.homeactivity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xuJinghao_34680535.nutritrack.ui.theme.NutriTrackTheme
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.xuJinghao_34680535.nutritrack.data.foodIntake.FoodIntakeViewModel
import com.xuJinghao_34680535.nutritrack.data.fruit.FruitViewModel
import com.xuJinghao_34680535.nutritrack.data.nutriCoachTips.NutriCoachTipsViewModel
import com.xuJinghao_34680535.nutritrack.data.nutriCoachTips.NutriCoachTipsViewModel.NutriCoachTipsViewModelFactory
import com.xuJinghao_34680535.nutritrack.ui.questionnaireactivity.QuestionnaireActivity
import com.xuJinghao_34680535.nutritrack.data.patient.PatientsViewModel
import com.xuJinghao_34680535.nutritrack.data.patient.PatientsViewModel.PatientsViewModelFactory
import kotlin.getValue

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Initialize ViewModel for patients
        val patientsViewModel: PatientsViewModel by viewModels {
            PatientsViewModelFactory(this)
        }
        // Initialize ViewModel for nutrition coach tips
        val nutriCoachTipsViewModel: NutriCoachTipsViewModel by viewModels {
            NutriCoachTipsViewModelFactory(this)
        }
        // Initialize ViewModel for food intake data
        val foodIntakeViewModel: FoodIntakeViewModel by viewModels {
            FoodIntakeViewModel.FoodIntakeViewModelFactory(this)
        }
        // Initialize ViewModel for fruit API
        val fruitViewModel: FruitViewModel by viewModels {
            FruitViewModel.FruitViewModelFactory()
        }
        setContent {
            NutriTrackTheme {
                val navController = rememberNavController() // Create navigation controller
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(navController = navController) // Add bottom navigation bar
                    }
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        // Home screen composable
                        composable("home") {
                            HomeScreen(
                                patientsViewModel,
                                navController = navController,
                                onEditClick = {
                                    // Starts the Questionnaire screen when edit is clicked
                                    startActivity(Intent(this@HomeActivity, QuestionnaireActivity::class.java))
                                }
                            )
                        }
                        // Insights screen composable
                        composable("insights") {
                            InsightsScreen(
                                patientsViewModel,
                                navController = navController)
                        }
                        // NutriCoach screen composable
                        composable("nutrition") {
                            NutriCoachScreen(patientsViewModel,nutriCoachTipsViewModel,foodIntakeViewModel,fruitViewModel)
                        }
                        // Settings screen composable
                        composable("settings") {
                            SettingsScreen(
                                patientsViewModel,
                                navController = navController)
                        }
                        // Admin screen composable
                        composable("admin") {
                            AdminScreen(
                                patientsViewModel,
                                navController = navController)
                        }
                    }
                }
            }
        }
    }
}

// Data class for food category scores
data class FoodCategory(
    val name: String,
    val score: Float, // Current score
    var maxScore: Int // Maximum possible score
)

@Composable
fun FoodCategoryRow(category: FoodCategory) {
    // Display a single food category with progress bar
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = category.name,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold
        )
        LinearProgressIndicator(
            progress = { category.score.toFloat() / category.maxScore.toFloat() },
            modifier = Modifier
                .width(120.dp)
                .height(8.dp),
            color = Color(0xFF6A1B9A),
        )

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${category.score}/${category.maxScore}",
            fontSize = 12.sp,
            modifier = Modifier.width(50.dp),
            textAlign = TextAlign.End
        )
    }
}


@Composable
fun BottomNavigationBar(navController: NavHostController) {
    // Define navigation items with routes, icons, and labels
    val items = listOf(
        "home" to Pair(Icons.Default.Home, "Home"),
        "insights" to Pair(Icons.Default.Insights, "Insights"),
        "nutrition" to Pair(Icons.Default.Restaurant, "NutriCoach"),
        "settings" to Pair(Icons.Default.Settings, "Settings")
    )
    var selectedItem by remember { mutableIntStateOf(0) }

    // Bottom navigation bar using Material 3
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { index, (route, iconLabelPair) ->
            val (icon, label) = iconLabelPair
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label
                    )
                },
                label = { Text(label) },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )
        }
    }
}
