package com.cs461.g6.mealportiontracker.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.material.TextFieldDefaults

@Composable
fun ScreenManualInput() {
    var ingredients by remember { mutableStateOf(listOf(Pair("", ""))) }

    fun addNewEntry() {
        ingredients = ingredients.toMutableList().apply {
            add(Pair("", ""))
        }
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally, // Center the content horizontally
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "What is in your meal?",
            modifier = Modifier
                .padding(start = 18.dp)
                .fillMaxWidth(),
            style = LocalTextStyle.current.copy(fontSize = 16.sp),
            color = LocalContentColor.current.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Display existing ingredients and quantities
        ingredients.forEachIndexed { index, pair ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, // Center the content horizontally
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${index + 1}. Ingredient",
                    modifier = Modifier
                        .padding(start = 18.dp)
                        .fillMaxWidth(),
                    style = LocalTextStyle.current.copy(fontSize = 16.sp),
                    color = LocalContentColor.current.copy(alpha = 0.7f)
                )
                TextField(
                    value = pair.first,
                    onValueChange = {
                        val updatedIngredients = ingredients.toMutableList()
                        updatedIngredients[index] = Pair(it, pair.second)
                        ingredients = updatedIngredients
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent
                    ),
                    placeholder = { Text("Enter Ingredient Name") },
                    modifier = Modifier
                        .width(320.dp) // Set the width of the TextField
                )

                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    text = "Quantity",
                    modifier = Modifier
                        .padding(start = 35.dp)
                        .fillMaxWidth(),
                    style = LocalTextStyle.current.copy(fontSize = 16.sp),
                    color = LocalContentColor.current.copy(alpha = 0.7f)
                )

                TextField(
                    value = pair.second,
                    onValueChange = {
                        val updatedIngredients = ingredients.toMutableList()
                        updatedIngredients[index] = Pair(pair.first, it)
                        ingredients = updatedIngredients
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent
                    ),
                    placeholder = { Text("Enter Ingredient Wuantity") },
                    modifier = Modifier.width(320.dp) // Set the width of the TextField
                )
            }

            Spacer(modifier = Modifier.height(50.dp))
        }

        // Button to add a new pair of text fields
        TextButton(
            onClick = { addNewEntry() }
        ) {
            Text("+ Add Ingredient", color = Color(179, 179, 179))
        }

        Spacer(modifier = Modifier.height(100.dp))

        Button(onClick = { /* Handle Registration */ }) {
            Text("Submit")
        }
    }
}

@Preview
@Composable
fun PreviewManualInputScreen() {
   ScreenManualInput()
}
