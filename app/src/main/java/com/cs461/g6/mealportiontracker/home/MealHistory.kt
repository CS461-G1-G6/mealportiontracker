package com.cs461.g6.mealportiontracker.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class MealEntry(
    val date: String,
    val protein: String,
    val vegetable: String,
    val fat: String,
    val carbohydrates: String
)

// dummy list
val mealList = listOf(
    MealEntry(
        date = "10-07-2021",
        protein = "25g",
        vegetable = "100g",
        fat = "15g",
        carbohydrates = "50g"
    ),
)

@Preview
@Composable
fun ScreenHistory(
) {

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // List here
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(mealList) { meal ->
                MealEntryCard(meal)
            }
        }
    }
}

@Composable
fun MealEntryCard(meal: MealEntry) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
//        shape = RoundedCornerShape(15.dp),
        elevation = 10.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = "Date: ${meal.date}",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
                Text(
                    text = "Protein: ${meal.protein}"
                )
                Text(text = "Vegetable: ${meal.vegetable}")
                Text(text = "Fat: ${meal.fat}")
                Text(text = "Carbohydrates: ${meal.carbohydrates}")
            }
        }
    }
}