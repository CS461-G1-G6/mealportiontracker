package com.cs461.g6.mealportiontracker.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp

data class MealEntry(
    val name: String,  // Placeholder until we get actual name from firebase
    val image: ImageBitmap, // Placeholder until we get actual image from firebase
    val date: String, // Placeholder until we get actual date from firebase
    val protein: String, // Placeholder until we get actual protein value from firebase
    val vegetable: String, // Placeholder until we get actual vegetable value from firebase
    val fat: String, // Placeholder until we get actual fat value from firebase
    val carbohydrates: String // Placeholder until we get actual carbohydrate value from firebase
)

val mealList = listOf(
    MealEntry(
        name = "Kaya Toast",
        image = ImageBitmap(100, 100),
        date = "10-07-2021",
        protein = "25g",
        vegetable = "100g",
        fat = "15g",
        carbohydrates = "50g"
    ),
    MealEntry(
        name = "Laksa",
        image = ImageBitmap(100, 100),
        date = "10-01-2021",
        protein = "256g",
        vegetable = "600g",
        fat = "155g",
        carbohydrates = "530g"
    ),
    // add more entries as required
)



@Composable
fun ScreenHistory(
) {
    val pageName = "Your Meal History"

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = pageName,
            style = MaterialTheme.typography.h5
        )

        // Here's your list:
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(mealList) { meal ->
                // MealEntryCard is a separate Composable, see below
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
        shape = RoundedCornerShape(15.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            // This will be changed to load the actual image from firebase
            Image(
                bitmap = meal.image,
                contentDescription = "Meal Image",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)

            )
            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = meal.name, // here it would be data from firebase
                    modifier = Modifier.padding(vertical = 2.dp)
                )
                Text(
                    text = "Date: ${meal.date}", // here it would be data from firebase
                    modifier = Modifier.padding(vertical = 2.dp)
                )
                Text(
                    text = "Protein: ${meal.protein}"
                    // Rest of the details for each meal entry (vegetable, fat & carbohydrates) will be similar to this
                )
                Text(text = "Vegetable: ${meal.vegetable}")
                Text(text = "Fat: ${meal.fat}")
                Text(text = "Carbohydrates: ${meal.carbohydrates}")
            }
        }
    }
}
