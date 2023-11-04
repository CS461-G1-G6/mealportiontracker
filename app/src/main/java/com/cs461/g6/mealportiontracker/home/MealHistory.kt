package com.cs461.g6.mealportiontracker.home

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.cs461.g6.mealportiontracker.core.FirebaseAuthUtil
import com.cs461.g6.mealportiontracker.core.SessionManager
import com.cs461.g6.mealportiontracker.theme.MealTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

//data class MealEntry(
//    val date: String,
//    val protein: String,
//    val vegetable: String,
//    val fat: String,
//    val carbohydrates: String
//)

data class MealEntry(
    val name: String = "",
    val calories: Float = 0.0f,
    val proteins: Float = 0.0f,
    val carbo: Float = 0.0f,
    val fats: Float = 0.0f,
    val date: String = "",
    val imageUrl: String = "",
    val userId: String = ""
)

// dummy list
//val mealList = listOf(
//    MealEntry(
//        date = "10-07-2021",
//        protein = "25g",
//        vegetable = "100g",
//        fat = "15g",
//        carbohydrates = "50g"
//    ),
//)

var mealHistories = mutableListOf<MealEntry>()

class MealHistory : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up any necessary NavHostController and SessionManager here if needed
        setContent {
            // You can pass your NavHostController and SessionManager to ScreenStats
            val navController: NavHostController = remember { NavHostController(this) }
            val sessionManager: SessionManager = remember { SessionManager(this) }

            MealTheme {
                // Display the ScreenStats composable within the ComposeView
                ScreenHistory(sessionManager, navController)
            }
        }
    }
}


@Composable
fun ScreenHistory(sessionManager: SessionManager, navController: NavHostController) {

    val currentUser = FirebaseAuthUtil.getCurrentUser()

    LaunchedEffect(key1 = currentUser) {
        if (currentUser != null) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("meal_histories")
            val userQuery: Query = databaseReference.orderByChild("userId").equalTo(currentUser.uid)

            userQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val temp = mutableListOf<MealEntry>()
                    for (dataSnapshot in snapshot.children) {
                        val meal = dataSnapshot.getValue(MealEntry::class.java)
                        Log.d("Test", meal.toString())
                        meal?.let {
                            temp.add(it)
                        }
                    }
                    mealHistories = temp
//                    callback(mealHistories)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database query error
//                    callback(emptyList())
                }
            })
        }
    }


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
            items(mealHistories) { meal ->
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
                if (meal.imageUrl != "") {
                    AsyncImage(
                        model = meal.imageUrl,
                        contentDescription = "",
                        modifier = Modifier
                            .height(100.dp)
                            .width(100.dp)
                    )
                }
                Text(
                    text = meal.name,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
                Text(text = "Posted on: ${meal.date}")
                Text(text = "Calories: ${meal.calories}")
                Text(text = "Protein: ${meal.proteins}")
                Text(text = "Fat: ${meal.fats}")
                Text(text = "Carbohydrates: ${meal.carbo}")

            }
        }
    }
}