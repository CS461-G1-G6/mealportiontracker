package com.cs461.g6.mealportiontracker.accounts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
fun LoginScreen() {
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
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* Handle Login */ }) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height((16.dp)))
        Button(onClick = { } ) {
            Text("Register")
        }
    }
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
        OutlinedTextField(value = "", onValueChange = {}, label = { Text(text = "Confirm Password") })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* Handle Registration */
        // show snackbar for user feedback
            }) {
            Text("Register")
        }
    }
}

@Composable
fun AccountSnackbar(data: SnackbarData){
    Card(shape = RoundedCornerShape(4.dp), modifier = Modifier.padding(8.dp)) {
        Snackbar(
            content = {
                Text(text = data.message)
            }, action = {
                if (data.actionLabel != null) {
                    Text(text = data.actionLabel.toString(),color = Color.Yellow)
                }
            }
        )
    }
}