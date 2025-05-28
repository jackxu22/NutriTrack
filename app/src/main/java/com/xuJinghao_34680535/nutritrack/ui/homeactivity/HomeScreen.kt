package com.xuJinghao_34680535.nutritrack.ui.homeactivity

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.xuJinghao_34680535.nutritrack.R
import com.xuJinghao_34680535.nutritrack.data.patient.PatientsViewModel
import com.xuJinghao_34680535.nutritrack.data.AuthManager

@Composable
fun HomeScreen(
    patientsViewModel: PatientsViewModel,
    navController: NavHostController,
    onEditClick: () -> Unit
) {
    val loggedInUserId = AuthManager.getUserId().toString()

    // Observe patient LiveData
    val patient by patientsViewModel.patient.observeAsState(initial = null)
    // Fetch patient data when screen is first composed
    LaunchedEffect(true) {
        patientsViewModel.getPatientByPatientId(loggedInUserId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Greeting section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = "Hello,",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = "${patient?.name}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Description and edit button row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "You've already filled in your Food Intake Questionnaire," +
                        " but you can change details here:",
                fontSize = 12.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            Button(
                onClick = onEditClick,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(start = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Edit",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        // Food image
        Image(
            painter = painterResource(id = R.drawable.food_image),
            contentDescription = "Food Picture",
            modifier = Modifier.size(180.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Score display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Score",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "See all scores >",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier
                    .clickable {
                        navController.navigate("insights") {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                    .padding(start = 8.dp)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = "UpArrow",
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Your Food Quality score",
                    fontSize = 14.sp,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${patient?.totalScore ?: 0f}/100",
                fontSize = 16.sp,
                color = Color.Green
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Score explanation
        Text(
            text = "What is the Food Quality Score?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Your Food Quality Score provides a snapshot of how well your eating patterns" +
                    " align with established food guidelines for improvement in your diet.\n\n" +
                    "This personalized measurement considers various food groups including" +
                    " vegetables, fruits, whole grains, and proteins" +
                    " to give you practical insights for making healthier food choices.",
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
