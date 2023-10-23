package com.cs461.g6.mealportiontracker.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class Statistics {
}

@Composable
fun ScreenStats(
) {
    val buttonTxt = "User Statistics"
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = buttonTxt,
            style = MaterialTheme.typography.h5
        )

        Spacer(modifier = Modifier.height(16.dp))

    }
}