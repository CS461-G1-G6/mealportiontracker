package com.cs461.g6.mealportiontracker.home

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cs461.g6.mealportiontracker.R
import com.cs461.g6.mealportiontracker.core.FirebaseAuthUtil
import com.cs461.g6.mealportiontracker.core.SessionManager
import com.cs461.g6.mealportiontracker.theme.MealTheme
import com.cs461.g6.mealportiontracker.theme.mealColors
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.util.Locale


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

var mealHistories = mutableListOf<MealEntry>()

class MealHistory : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up any necessary NavHostController and SessionManager here if needed
        setContent {
            // You can pass your NavHostController and SessionManager to ScreenStats
//            val navController = rememberNavController()
//            val sessionManager: SessionManager = remember { SessionManager(this) }

            MealTheme {
                // Display the ScreenHistory composable within the ComposeView
                ScreenHistory()
            }
        }
        }
    }



@Composable
fun ScreenHistory() {

    val currentUser = FirebaseAuthUtil.getCurrentUser()
    Log.i("------------------", "Loaded ScreenHistory")

    LaunchedEffect(key1 = currentUser) {
        if (currentUser != null ) {
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


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MealEntryCard(meal: MealEntry) {

    val openDialog = remember { mutableStateOf(false) }

    Card(
        onClick = { openDialog.value = true }, // Set dialog open state to true when card is clicked
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        elevation = 10.dp,
        backgroundColor = mealColors.background
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                    .padding(10.dp)
        ){
            Text(text = meal.date,
                color = Color.Gray,
                fontSize = 12.sp)
            Icon(
                painter = painterResource(id = R.drawable.ic_right),
                tint = mealColors.secondary,
                contentDescription = "More Info",
                )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(20.dp,30.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_restaurant),
                        contentDescription = "Meal Icon",
                        modifier = Modifier.size(17.dp)
                    )
                    Text(
                        text = " " + meal.name.toTitleCase(),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                    )
                }

                Text(text = "${meal.calories} kcals")
//                Text(text = "Protein: ${meal.proteins}")
//                Text(text = "Fat: ${meal.fats}")
//                Text(text = "Carbohydrates: ${meal.carbo}")
            }



        }

    }
    if (openDialog.value) {
        AlertDialog(
            backgroundColor = mealColors.background,
            onDismissRequest = { openDialog.value = false },
            title = {
                Text(text = " " + meal.name.toTitleCase(),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp)
            },
            text = {
                /* Display meal details */
//                Row(horizontalArrangement = Arrangement.Center){
//
//                    if (meal.imageUrl != "") {
//                        AsyncImage(
//                            model = ImageRequest.Builder(LocalContext.current)
//                                .data(meal.imageUrl)
//                                .crossfade(true)
//                                .build(),
//                            contentDescription = "",
//                            contentScale = ContentScale.Crop,
//                            modifier = Modifier
//                                .size(60.dp)
//                                .clip(RoundedCornerShape(10.dp)),
//                        )
//                    }
//                }
                Column {
                    Text(text = "${meal.calories} kcals")

                    Spacer(modifier = Modifier.size(15.dp))

                    Text(text = "Macronutrients (in grams): ", fontWeight = FontWeight.W500)
                    Spacer(modifier = Modifier.size(2.dp))
                    Text(text = "Protein: ${meal.proteins}")
                    Text(text = "Fat: ${meal.fats}")
                    Text(text = "Carbohydrates: ${meal.carbo}")
                }
            },
            confirmButton = {
                Button(
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = mealColors.secondary,
                        contentColor = Color.White),
                    onClick = { openDialog.value = false }

                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_exit),
                        contentDescription = "Exit",
                    )
                }
            },
            modifier = Modifier.padding(5.dp)
        )

    }
}




fun String.toTitleCase(): String {
    return split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}
