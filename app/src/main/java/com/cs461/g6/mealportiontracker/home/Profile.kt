package com.cs461.g6.mealportiontracker.home

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.cs461.g6.mealportiontracker.accounts.AppScreen
import com.cs461.g6.mealportiontracker.core.FirebaseAuthUtil
import com.cs461.g6.mealportiontracker.core.FoodItem
import com.cs461.g6.mealportiontracker.foodimageprocessing.mToast
import com.cs461.g6.mealportiontracker.utils.SessionManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

var RefreshButtonClickFlag = false

data class UserAuth(
    val userId: String,
    val email: String,
    val password: String
)

data class User(
    val userId: String,
    val email: String,
    val weight: Float?,
    val height:Float?,
    val recommendedCalories:Float,
    val age: Number?,
    val gender: String,
    val activity:Float?
)


fun readCsv(context: Context, fileName: String): List<FoodItem> {
    val result: MutableList<FoodItem> = mutableListOf()

    try {
        val inputStream = context.assets.open(fileName)
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line: String?

        while (reader.readLine().also { line = it } != null) {
            // Process each line of the CSV file and add it to the result list
            val foodItemProperties = line!!.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*\$)".toRegex())
                .map { it.trim('"') } // Remove quotes from components
            if (foodItemProperties.size >= 5) {
                val name = foodItemProperties[0]
                val calories = foodItemProperties[1].toDoubleOrNull() ?: 0.0
                val proteins = foodItemProperties[2].trim()
                val carbs = foodItemProperties[3].trim()
                val fats = foodItemProperties[4].trim()
                val foodItem = FoodItem(name, calories, proteins, carbs, fats)
                result.add(foodItem)
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return result
}


private fun fetchUserProfile(sessionManager: SessionManager, callback: (User?) -> Unit) {
    val databaseReference = FirebaseDatabase.getInstance().getReference("users")
    databaseReference
        .orderByKey()
        .equalTo(sessionManager.getUserId())
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var user_profile: User? = null
                for (datas in snapshot.children) {
                    user_profile = User(
                        userId = datas.child("userId").getValue(String::class.java) ?: "",
                        email = datas.child("email").getValue(String::class.java) ?: "",
                        weight = datas.child("weight").getValue(Float::class.java),
                        height = datas.child("height").getValue(Float::class.java),
                        recommendedCalories = datas.child("recommended_calories").getValue(Float::class.java) ?: 0.0f,
                        age = datas.child("age").getValue(Long::class.java)?.toInt() ?: 0,
                        gender = datas.child("gender").getValue(String::class.java) ?: "",
                        activity = datas.child("activity").getValue(Float::class.java)
                    )
                }
                callback(user_profile)
            }
            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
}

@Composable
fun activityRadioButton(
    options: List<Pair<Double, String>>,
    selectedActivity: Double,
    onOptionSelected: (Double) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        options.forEach { (value, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (selectedActivity == value),
                        onClick = {
                            onOptionSelected(value)
                        }
                    )
                    .padding(horizontal = 16.dp)
            ) {
                RadioButton(
                    selected = (selectedActivity == value),
                    onClick = { onOptionSelected(value) }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.body1.merge()
                )
            }
        }
    }
}


@Composable
fun ScreenProfile(sessionManager: SessionManager, navController: NavHostController) {
    val mContext = LocalContext.current
    var user_profile by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val context: Context = mContext
    val fileName = "food_nutrition.csv"
    val foodItems = readCsv(context, fileName)
    val recommendedFoodList = mutableListOf<FoodItem>()

    var showRecommendedFoods by remember { mutableStateOf(true) }
    var refreshed by remember { mutableStateOf(false) }

    for (foodItem in foodItems) {
        recommendedFoodList.add(foodItem)
    }

    LaunchedEffect(isLoading) {
        fetchUserProfile(sessionManager) { fetchedUser ->
            if (fetchedUser != null) {
                user_profile = fetchedUser
                isLoading = false
            } else {
                isLoading = false
                mToast(mContext, "Error retrieving profile information from database")
            }
        }
    }

    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.size(50.dp))
    } else if (user_profile != null) {
        var height by remember { mutableStateOf(user_profile?.height ?: 0.0f) }
        var weight by remember { mutableStateOf(user_profile?.weight ?: 0.0f) }
        var recommendedCalories by remember { mutableStateOf(user_profile?.recommendedCalories ?: 0.0f) }
        var age by remember { mutableStateOf(user_profile?.age ?: 0) }
        val radioOptions = listOf("M", "F")
        val activityOptions = listOf(
            1.2 to "Sedentary (little or no exercise)",
            1.375 to "Lightly active (light exercise/sports 1-3 days/week)",
            1.55 to "Moderately active (moderate exercise/sports 3-5 days/week)",
            1.725 to "Very active (hard exercise/sports 6-7 days a week)",
            1.9 to "Extra active (very hard exercise/sports & physical job or 2x training)"
        )
        var (selectedOption, onOptionSelected) = remember { mutableStateOf(user_profile?.gender ?: "M") }
        Log.i("activee", user_profile?.activity.toString())
        var selectedActivity by remember { mutableStateOf((user_profile?.activity as? Double) ?: 1.2) }

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        )
        {
            Text(text = "Email: ${sessionManager.getUserEmail()}", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(36.dp))
            Text(text = "Calories Recommender", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text="Your Gender:", fontSize = 18.sp)
            radioOptions.forEach { text ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = {
                                onOptionSelected(text)
                            }
                        )
                        .padding(horizontal = 16.dp)
                ) {
                    RadioButton(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text) },
                        modifier = Modifier.align(Alignment.CenterVertically) // Align RadioButton content vertically
                    )
                    Spacer(modifier = Modifier.width(5.dp)) // Add spacing between RadioButton and Text
                    Text(
                        text = text,
                        style = MaterialTheme.typography.body1.merge(),
                        modifier = Modifier.align(Alignment.CenterVertically) // Align Text content vertically
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = if (age.toInt() > 0) age.toString() else "",
                onValueChange = { newAgeText ->
                    if (newAgeText.isEmpty()) {
                        age = 0 // Set age to 0 when the field is empty
                    } else if (newAgeText.matches(Regex("^[1-9]\\d*$"))) {
                        val newAge = newAgeText.toInt()
                        age = newAge
                    }
                },
                label = { Text("Current Age: " + (if (age.toInt() > 0) age.toString() else "")) },
                placeholder = { Text(if (age.toInt() > 0) age.toString() else "Enter Age") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = if (weight > 0) weight.toString() + " kg" else "",
                onValueChange = { newWeightText ->
                    val newWeight = newWeightText.removeSuffix(" kg").toFloatOrNull()
                    if (newWeight != null) {
                        weight = newWeight
                    }
                },
                label = { Text("Current Weight: " + (if (weight != null && weight > 0) weight.toString() else "") + " kg") },
                placeholder = { Text(if (weight != null && weight > 0) weight.toString() + " kg" else "Enter Weight in cm") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = if (height > 0) height.toString() + " cm" else "",
                onValueChange = { newHeightText ->
                    val newHeight = newHeightText.removeSuffix(" cm").toFloatOrNull()
                    if (newHeight != null) {
                        height = newHeight
                    }
                },
                label = { Text("Current Height: " + (if (height != null && height > 0) height.toString() else "") + " cm") },
                placeholder = { Text(if (height != null && height > 0) height.toString() + " cm" else "Enter Height in cm") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text="How Active are you:", fontSize = 18.sp)
            activityRadioButton(
                options = activityOptions,
                selectedActivity = selectedActivity
            ) { selectedOption ->
                Log.i("selectedoption", selectedOption.toString())
                selectedActivity = selectedOption
            }
            Spacer(modifier = Modifier.height(18.dp))
            Button(onClick = {
                var haveError = false;
                if(height <= 0.0) {
                    mToast(mContext, "Please enter valid height")
                    haveError = true
                }
                if(weight <= 0.0) {
                    mToast(mContext, "Please enter valid weight")
                    haveError = true
                }
                if(age.toInt() <= 0) {
                    mToast(mContext, "Please enter valid age")
                    haveError = true
                }
                var recommendedCaloriesDb:Float = 0.0f
                if (selectedOption == "M") {
                    recommendedCaloriesDb = (((13.397*weight) + (4.799*height) - (5.677*age.toFloat()) + 88.362)* selectedActivity.toFloat()).toFloat()
                } else {
                    recommendedCaloriesDb = (((9.247*weight) + (3.098*height) - (4.330*age.toFloat()) + 447.593)* selectedActivity.toFloat()).toFloat()
                }
                if (!haveError) {
                    val updateData: MutableMap<String, Any> = HashMap()
                    updateData["weight"] = weight
                    updateData["height"] = height
                    updateData["age"] = age
                    updateData["gender"] = selectedOption
                    updateData["activity"] = selectedActivity
                    updateData["recommended_calories"] = recommendedCaloriesDb
                    val databaseReference = FirebaseDatabase.getInstance().getReference("users")
                    databaseReference.child(sessionManager.getUserId().toString()).updateChildren(
                        updateData
                    ) { databaseError, databaseReference ->
                        if (databaseError != null) {
                            mToast(mContext, "An error has occured while updating information")
                        } else {
                            recommendedCalories = recommendedCaloriesDb
                            mToast(mContext, "Successfully Updated Information")
                            RefreshButtonClickFlag = true;
                        }
                    }
                }
            }) {
                Text("Update Information")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Recommended Calories: " + recommendedCalories.toString(), fontSize = 18.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Recommended Food", fontSize = 18.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            if (showRecommendedFoods ) {
                RecommendedFoodListRow(recommendedFoodList, recommendedCalories)

            }
            Button(
                onClick = {
                    RefreshButtonClickFlag = true;
                    refreshed = true
                    showRecommendedFoods = false


                }
            ) {
                Text("Refresh Recommended Foods")
            }
            Spacer(modifier = Modifier.height(30.dp))
            // SignOut Button
            Button(onClick = {
                // Sign out user from Firebase and clear session
                FirebaseAuthUtil.signOut()
                sessionManager.clearUserData()
                navController.navigate(AppScreen.ScreenLogin.name) {
                    // Clear the back stack to prevent going back to the profile screen
                    popUpTo(navController.graph.startDestinationRoute!!) {
                        inclusive = false
                    }
                }
            }, modifier = Modifier.align(Alignment.Start)) {
                Text("Sign Out")
            }
        }
    }
    if(RefreshButtonClickFlag)
    {
        RefreshButtonClickFlag = false
        mToast(mContext, "Please wait...")
        refreshed = false;
        showRecommendedFoods = true;
    }
}

@Composable
fun RecommendedFoodListRow(items: List<FoodItem>, calorieLimit: Float) {
    val randomFoods = mutableListOf<FoodItem>()
    var remainingCalories = calorieLimit
    val shuffledList = items.shuffled()
    var beverageCount = 0

    Log.i("recommended list", "pressed")

    for (food in shuffledList) {
        if (food.calories > 0 && food.calories <= remainingCalories) {
            if (food.name.contains("beverage", ignoreCase = true)) {
                if (beverageCount < 1) {
                    randomFoods.add(food)
                    beverageCount++
                    remainingCalories -= food.calories.toFloat()
                }
            } else {
                randomFoods.add(food)
                remainingCalories -= food.calories.toFloat()
            }
        }
        if (remainingCalories <= 0) {
            break
        }
    }

    var caloriesUsed = calorieLimit - remainingCalories

    Column {
        randomFoods.forEach { item ->
            FoodItemRow(item)
        }
        Column(
            modifier = Modifier.fillMaxWidth(),  // This line ensures that the "Total Calories" text is centered within the available width.
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Calories: " + caloriesUsed.toString(),
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FoodItemRow(item: FoodItem) {
    Text(
        text = item.name,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(3.dp))
    Text(
        text = "Calories: " + item.calories.toString(),
    )
    Spacer(modifier = Modifier.height(8.dp))
}