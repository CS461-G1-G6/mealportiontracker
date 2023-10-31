package com.cs461.g6.mealportiontracker.home

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.cs461.g6.mealportiontracker.accounts.AppScreen
import com.cs461.g6.mealportiontracker.core.FirebaseAuthUtil
import com.cs461.g6.mealportiontracker.foodimageprocessing.mToast
import com.cs461.g6.mealportiontracker.core.SessionManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


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

val dummyUser = User(
    userId = "123",
    email = "aj@example.com",
    weight = 0.0f,
    height = 0.0f,
    recommendedCalories = 0.0f,
    age = 20,
    gender = "F",
    activity = 1.2f
)

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
            1.375 to "lightly active (light exercise/sports 1-3 days/week)",
            1.55 to "moderately active (moderate exercise/sports 3-5 days/week)",
            1.725 to "very active (hard exercise/sports 6-7 days a week)",
            1.9 to "extra active (very hard exercise/sports & physical job or 2x training)"
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
            Text(text = "Email: ${sessionManager.getUserEmail()}")
            Spacer(modifier = Modifier.height(36.dp))
            Text(text = "Calories Recommender", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text="Your Gender:")
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
            Text(text="How Active are you:")
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
                        }
                    }
                }
            }) {
                Text("Update Information")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Recommended Calories: " + recommendedCalories.toString(), fontSize = 24.sp)
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
            }) {
                Text("Sign Out")
            }
        }
    }
}

