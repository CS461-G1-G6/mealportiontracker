package com.cs461.g6.mealportiontracker.accounts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarData
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.cs461.g6.mealportiontracker.core.FirebaseAuthUtil
import com.cs461.g6.mealportiontracker.foodimageprocessing.CameraXPreviewActivity
import com.cs461.g6.mealportiontracker.home.AppScreen
import com.cs461.g6.mealportiontracker.home.User
import com.cs461.g6.mealportiontracker.utils.SessionManager
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase


data class UserAuth(
    val userId: String,
    val email: String,
    val password: String
)

class UserLogin {
}

//@Composable
//fun LoginScreen(onNavigateToRegister: () -> Unit) {
//    Column(
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        OutlinedTextField(value = "", onValueChange = {}, label = { Text(text = "Email") })
//        Spacer(modifier = Modifier.height(8.dp))
//        OutlinedTextField(value = "", onValueChange = {}, label = { Text(text = "Password") })
//        Spacer(modifier = Modifier.height(16.dp))
//        Button(onClick = { /* Handle Login */ }) {
//            Text("Login")
//        }
//        Spacer(modifier = Modifier.height((16.dp)))
//        Button(onClick = onNavigateToRegister) {
//            Text("Register")
//        }
//    }
//}

@Composable
fun LoginScreen(navController: NavHostController, sessionManager: SessionManager) {
    var isLoading by remember { mutableStateOf(false) } // To control the visibility of the progress dialog
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val mContext = LocalContext.current

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { newEmail -> email = newEmail },
            label = { Text(text = "Email") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { newPassword -> password = newPassword },
            label = { Text(text = "Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()

        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            isLoading = true // Show the progress dialog
            FirebaseAuthUtil.loginUserWithEmailAndPassword(email.toLowerCase().trim(), password.trim())
                .addOnCompleteListener { task ->
                    isLoading = false // Hide the progress dialog

                    if (task.isSuccessful) {
                        // Login successful
                        val user = FirebaseAuthUtil.getCurrentUser()
                        val userId = FirebaseAuthUtil.getCurrentUser()!!.uid
                        mToast(mContext, "Login Successful!")
                        sessionManager.saveUserData(user!!.uid, user!!.email ?: "", password, true)

                        navController.navigate(AppScreen.ScreenProfile.name)

//                        For testing
//                        navController.navigate(AppScreen.ScreenStats.name)
//                        val intent = Intent(mContext, CameraXPreviewActivity::class.java)
//                        mContext.startActivity(intent)


                    } else {
                        /*val exception = task.exception
                        mToast(mContext, "Login Failed: ${exception?.message}")*/

                        // Login failed
                        val exception = task.exception
                        var errorMessage = "Login failed. Please try again later."
                        // Check the type of exception and customize the error message accordingly
                        when (exception) {
                            is FirebaseAuthInvalidUserException -> {
                                errorMessage = "Invalid email address. Please enter a valid email."
                            }
                            is FirebaseAuthInvalidCredentialsException -> {
                                errorMessage = "Invalid password. Please enter the correct password."
                            }
                            is FirebaseAuthEmailException -> {
                                errorMessage = "Email address is not registered. Please sign up first."
                            }
                        }

                        mToast(mContext, errorMessage)

                    }
                }


            /*FirebaseAuthUtil.loginUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    isLoading = false // Hide the progress dialog

                    if (task.isSuccessful) {
                        val user = FirebaseAuthUtil.getCurrentUser()

                    } else {
                        val exception = task.exception
                        // Handle login failure, e.g., show an error message
                    }
                }*/
        }) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            isLoading = true // Show the progress dialog

            FirebaseAuthUtil.registerUserWithEmailAndPassword(email.trim(), password)
                .addOnCompleteListener { task ->
                    isLoading = false // Hide the progress dialog

                    if (task.isSuccessful) {
                        val firebaseUser = FirebaseAuthUtil.getCurrentUser()
                        val userId = firebaseUser?.uid
                        val userEmail = firebaseUser?.email

                        // Save user data to Realtime Firebase database
                        if (userId != null && userEmail != null) {
                            val databaseReference = FirebaseDatabase.getInstance().getReference("users")
                            val user = UserAuth(userId, userEmail.lowercase().trim(), password) // Assuming you have a User data class

                            databaseReference.child(userId).setValue(user)
                                .addOnCompleteListener { saveTask ->
                                    if (saveTask.isSuccessful) {
                                        sessionManager.saveUserData(user.userId, user.email ?: "", password, true)
                                        // Registration and data save were successful
                                        // Save user data to session
                                        mToast(mContext, "Registration Successful!")
                                        navController.navigate(AppScreen.ScreenProfile.name)
                                    } else {
                                        // Handle database save failure
                                        val saveException = saveTask.exception
                                        mToast(mContext, "Failed to save user data: ${saveException?.message}")
                                    }
                                }
                        }

                    } else {
                        /*val exception = task.exception
                        mToast(mContext, "Registration failed: ${exception?.message}")*/


                        // Handle registration failure
                        val exception = task.exception
                        var errorMessage = "Registration failed. Please try again later."
                        // Check the type of exception and customize the error message accordingly
                        when (exception) {
                            is FirebaseAuthWeakPasswordException -> {
                                errorMessage = "Weak password. Please choose a stronger password."
                            }
                            is FirebaseAuthInvalidCredentialsException -> {
                                errorMessage = "Invalid email format. Please enter a valid email address."
                            }
                            is FirebaseAuthUserCollisionException -> {
                                errorMessage = "Email address is already in use. Please use a different email."
                            }
                        }

                        mToast(mContext, errorMessage)

                    }
                }

            /*
                        FirebaseAuthUtil.registerUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                isLoading = false // Hide the progress dialog

                                if (task.isSuccessful) {
                                    val user = FirebaseAuthUtil.getCurrentUser()
                                } else {
                                    val exception = task.exception
                                    // Handle registration failure, e.g., show an error message
                                }
                            }*/
        }) {
            Text("Register")
        }

        // Progress Dialog
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(50.dp))
        }
    }
}

private fun mToast(context: Context, message: String){
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

@Composable
fun RegisterScreen() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(value = "", onValueChange = {}, label = { Text(text = "Email") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = "", onValueChange = {}, label = { Text(text = "Password") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text(text = "Confirm Password") })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* Handle Registration */
            // show snackbar for user feedback
        }) {
            Text("Register")
        }
    }
}

@Composable
fun AccountSnackbar(data: SnackbarData) {
    Card(shape = RoundedCornerShape(4.dp), modifier = Modifier.padding(8.dp)) {
        Snackbar(
            content = {
                Text(text = data.message)
            }, action = {
                if (data.actionLabel != null) {
                    Text(text = data.actionLabel.toString(), color = Color.Yellow)
                }
            }
        )
    }
}