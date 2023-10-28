package com.cs461.g6.mealportiontracker.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScreenManualInput() {
    var ingredients by remember { mutableStateOf(listOf(Pair("", ""))) }
    val scrollState = rememberScrollState()

    fun addNewEntry() {
        ingredients = ingredients.toMutableList().apply {
            add(Pair("", ""))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize() // Takes the full available space
            .padding(16.dp) // Adds padding around the content
            .verticalScroll(scrollState) // Enables vertical scrolling

    ) {
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
}

@Preview
@Composable
fun PreviewManualInputScreen() {
   ScreenManualInput()
}
