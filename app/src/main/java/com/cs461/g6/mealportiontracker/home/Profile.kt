package com.cs461.g6.mealportiontracker.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cs461.g6.mealportiontracker.R

// TODO: Configure this to work with Firebase, must also configure pages under accounts

data class User(
    val userId: String,
    val username: String,
    val email: String,
    val imageUrl: String
)

val dummyUser = User(
    userId = "123",
    username = "AJ",
    email = "aj@example.com",
    imageUrl = "https://imgur.com/a/fRL8VXzhttps://imgur.com/a/fRL8VXz"
)

class Profile(
    val user: User
) {

    /* You can add functions here to handle user's details.
    For the Firebase related functions, best place would be your ViewModel.
    ViewModel will handle fetching and updates to your user data*/

}

@Composable
fun ScreenProfile() {

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.landscape),
            contentDescription = "User Profile Picture",
            modifier = Modifier
                .size(100.dp)
                .clip(RectangleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Username: ${dummyUser.username}")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Email: ${dummyUser.email}")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "UserId: ${dummyUser.userId}")
        Spacer(modifier = Modifier.height(16.dp))
    }
}
