package com.cs461.g6.mealportiontracker.home

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.cs461.g6.mealportiontracker.core.FirebaseAuthUtil
import com.cs461.g6.mealportiontracker.utils.SessionManager
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.cs461.g6.mealportiontracker.foodimageprocessing.CameraXPreviewActivity
import kotlin.math.cos
import kotlin.math.sin

val myRedColor = Color(0xFFE74C3C)
val myGreenColor = Color(0xFF228B22)
val myBlueColor = Color(0xFF3498DB)

data class StatisticsData(
    val totalCalories: Int,
    val totalFats: Int,
    val totalProteins: Int,
    val totalCarbo: Int
)

data class FoodInfoWithDate(
    val name: String = "",
    val calories: Int = 0,
    val proteins: Int = 0,
    val carbo: Int = 0,
    val fats: Int = 0,
    val date: String = "",
    val imageUrl: String = "",
    val userId: String = ""
)

class Statistics : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up any necessary NavHostController and SessionManager here if needed

        setContent {
            // You can pass your NavHostController and SessionManager to ScreenStats
            val navController: NavHostController = remember { NavHostController(this) }
            val sessionManager: SessionManager = remember { SessionManager(this) }

            // Display the ScreenStats composable within the ComposeView
            ScreenStats(sessionManager, navController)
        }
    }
}

@Composable
fun ScreenStats(sessionManager: SessionManager, navController: NavHostController) {

    var totalCalories by remember { mutableStateOf(0) }
    var totalFats by remember { mutableStateOf(0) }
    var totalProteins by remember { mutableStateOf(0) }
    var totalCarbo by remember { mutableStateOf(0) }
    var recommendedCalories by remember { mutableStateOf(0) }


    val currentUser = FirebaseAuthUtil.getCurrentUser()

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = dateFormat.format(Date())

    val context = LocalContext.current

    if (currentUser == null) {
        // Display "No records" when there is no currentUser
        Text(
            text = "No records",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            modifier = Modifier.padding(16.dp)
        )
    } else {
        // Display statistics when currentUser is available
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = dateFormat.format(Date())

        // Fetch user-specific meal histories and update totals
        LaunchedEffect(currentUser) {
            fetchUserMealHistories(currentUser.uid, date) { mealHistories ->
                val (calories, fats, proteins, carbo) = calculateTotalStatistics(mealHistories)
                totalCalories = calories
                totalFats = fats
                totalProteins = proteins
                totalCarbo = carbo
            }
            fetchUserRecommendedCalories(currentUser.uid) { calories ->
                recommendedCalories = calories
            }

        }
    }

    if (totalCalories > 0 || totalFats > 0 || totalProteins > 0 || totalCarbo > 0) {
        val total = totalFats.toFloat() + totalProteins.toFloat() + totalCarbo.toFloat()
        val fatPercentage = (totalFats.toFloat() / total) * 100
        val proteinPercentage = (totalProteins.toFloat() / total) * 100
        val carboPercentage = (totalCarbo.toFloat() / total) * 100

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Calories Breakdown",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Recommended Calories:")
                    }
                    append(" $recommendedCalories")
                }
            )

            Spacer(modifier = Modifier.height(25.dp))

            // Display the pie chart
            PieChart(
                fatPercentage = fatPercentage,
                proteinPercentage = proteinPercentage,
                carboPercentage = carboPercentage,
                modifier = Modifier.size(180.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                PieChartLegend()
            }

            Spacer(modifier = Modifier.height(25.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Calories", fontWeight = FontWeight.Bold)
                    Text("$totalCalories")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Fats (per 100g)", fontWeight = FontWeight.Bold)
                    Text("$totalFats g")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Proteins (per 100g)", fontWeight = FontWeight.Bold)
                    Text("$totalProteins g")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Carbohydrates (per 100g)", fontWeight = FontWeight.Bold)
                    Text("$totalCarbo g")
                }
            }
        }
    } else {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "No records found",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                modifier = Modifier.padding(16.dp)
            )

            Button(
                onClick = {
                    val intent = Intent(context, CameraXPreviewActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier.padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = mealColors.primary // Set the background color to the primary color
                )
            ) {
                Text("Capture your meal", color = mealColors.onPrimary)
            }
        }
    }
}

private fun fetchUserMealHistories(userId: String, date: String, callback: (List<FoodInfoWithDate>) -> Unit) {
    val databaseReference = FirebaseDatabase.getInstance().getReference("meal_histories")
    val userQuery: Query = databaseReference.orderByChild("userId").equalTo(userId)

    userQuery.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val mealHistories = mutableListOf<FoodInfoWithDate>()
            for (dataSnapshot in snapshot.children) {
                val foodInfoWithDate = dataSnapshot.getValue(FoodInfoWithDate::class.java)
                foodInfoWithDate?.let {
                    Log.d("FoodInfoWithDate", "name: ${it.name}, calories: ${it.calories}, proteins: ${it.proteins}, carbo: ${it.carbo}, fats: ${it.fats}")
                    if (it.date == date) { // Check if the date matches the current date
                        Log.d("FoodInfoWithDate", "name: ${it.name}, calories: ${it.calories}, proteins: ${it.proteins}, carbo: ${it.carbo}, fats: ${it.fats}")
                        mealHistories.add(it)
                    }
                }
            }
            callback(mealHistories)
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle database query error
            callback(emptyList())
        }
    })
}


private fun calculateTotalStatistics(mealHistories: List<FoodInfoWithDate>): StatisticsData {
    var totalCalories = 0
    var totalFats = 0
    var totalProteins = 0
    var totalCarbo = 0

    for (meal in mealHistories) {
        totalCalories += meal.calories
        totalFats += meal.fats
        totalProteins += meal.proteins
        totalCarbo += meal.carbo
    }

    return StatisticsData(totalCalories, totalFats, totalProteins, totalCarbo)
}

@Composable
fun PieChart(fatPercentage: Float, proteinPercentage: Float, carboPercentage: Float, modifier: Modifier) {
    Canvas(modifier = modifier) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.minDimension / 2
        val startAngle = 0f

        // Draw the fat segment
        val fatSweepAngle = (fatPercentage / 100) * 360
        drawArc(
            color = myRedColor,
            startAngle = startAngle,
            sweepAngle = fatSweepAngle,
            useCenter = true,
            style = Fill
        )

        // Draw the protein segment
        val proteinSweepAngle = (proteinPercentage / 100) * 360
        drawArc(
            color = myGreenColor,
            startAngle = startAngle + fatSweepAngle,
            sweepAngle = proteinSweepAngle,
            useCenter = true,
            style = Fill
        )

        // Draw the carbohydrate segment
        val carboSweepAngle = (carboPercentage / 100) * 360
        drawArc(
            color = myBlueColor,
            startAngle = startAngle + fatSweepAngle + proteinSweepAngle,
            sweepAngle = carboSweepAngle,
            useCenter = true,
            style = Fill
        )

        // Update the protein percentage label
        drawPercentageLabel(centerX, centerY, radius, startAngle, fatSweepAngle, "${fatPercentage.toInt()}%")
        drawPercentageLabel(centerX, centerY, radius, startAngle + fatSweepAngle, proteinSweepAngle, "${proteinPercentage.toInt()}%")
        drawPercentageLabel(centerX, centerY, radius, startAngle + fatSweepAngle + proteinSweepAngle, carboSweepAngle, "${carboPercentage.toInt()}%")

    }
}

private fun DrawScope.drawPercentageLabel(centerX: Float, centerY: Float, radius: Float, startAngle: Float, sweepAngle: Float, label: String) {
    val angle = startAngle + sweepAngle / 2
    val textX = centerX + (radius / 1.5f) * cos(Math.toRadians(angle.toDouble()).toFloat())
    val textY = centerY + (radius / 1.5f) * sin(Math.toRadians(angle.toDouble()).toFloat())
    val fontSize = 35f
    val paint = Paint().apply {
        color = Color.Black.toArgb()
        textAlign = android.graphics.Paint.Align.CENTER
        textSize = fontSize
    }
    drawIntoCanvas {
        it.nativeCanvas.drawText(label, textX, textY, paint)
    }
}


fun fetchUserRecommendedCalories(userId: String, callback: (Int) -> Unit) {
    val databaseReference = FirebaseDatabase.getInstance().getReference("users")

    databaseReference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val recommendedCalories = snapshot.child("recommended_calories").getValue(Int::class.java)
            recommendedCalories?.let {
                callback(it)
            } ?: run {

                callback(0)
            }
        }

        override fun onCancelled(error: DatabaseError) {

            callback(0)
        }
    })
}

@Composable
fun PieChartLegend() {
    Row(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        PieChartLegendItem(myRedColor, "Fats")
        Spacer(modifier = Modifier.width(16.dp))
        PieChartLegendItem(myGreenColor, "Proteins")
        Spacer(modifier = Modifier.width(16.dp))
        PieChartLegendItem(myBlueColor, "Carbohydrates")
        Spacer(modifier = Modifier.width(16.dp))
    }
}

@Composable
fun PieChartLegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(16.dp, 16.dp)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = TextStyle(fontWeight = FontWeight.Bold))
    }
}




