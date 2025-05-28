package com.xuJinghao_34680535.nutritrack.ui.mainactivity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xuJinghao_34680535.nutritrack.ui.theme.NutriTrackTheme
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xuJinghao_34680535.nutritrack.R
import com.xuJinghao_34680535.nutritrack.data.patient.PatientsViewModel
import com.xuJinghao_34680535.nutritrack.data.patient.PatientsViewModel.PatientsViewModelFactory
import com.xuJinghao_34680535.nutritrack.ui.loginactivity.LoginActivity


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NutriTrackTheme {
                WelcomeScreen()
            }
        }
    }
}

@Composable
fun WelcomeScreen() {
    val context = LocalContext.current
    // Init for loading data from csv (first time)
    val patientsViewModel: PatientsViewModel = viewModel(
        factory = PatientsViewModelFactory(context)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            // App name
            Text(
                text = "NutriTrack",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "NutriTrack Logo",
                modifier = Modifier.size(120.dp)
            )

            // Disclaimer text
            Text(
                text = "This app provides general health and nutrition information " +
                        "for educational purposes only. It is not intended as " +
                        "medical advice, diagnosis, or treatment. Always consult a " +
                        "qualified healthcare professional before making any " +
                        "changes to your diet, exercise, or health regimen.\n\n" +
                        "Use this app at your own risk.\n\n" +
                        "If you'd like to see an Accredited Practicing Dietitian (APD), " +
                        "please visit the Monash Nutrition/Dietetics Clinic " +
                        "(discounted rates for students):\n" +
                        "https://www.monash.edu/medicine/scs/nutrition/clinics/nutrition",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(16.dp)
            )

            // Login button, navigates to LoginActivity when clicked
            Button(
                onClick = {
                    context.startActivity(Intent(context, LoginActivity::class.java))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "Login",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Student name and ID at the bottom
            Text(
                text = "Designed with ❤️ by Xu Jinghao (34680535)",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}