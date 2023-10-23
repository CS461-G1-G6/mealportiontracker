package com.cs461.g6.mealportiontracker.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.cs461.g6.mealportiontracker.R
import com.cs461.g6.mealportiontracker.accounts.AppScreen
import com.cs461.g6.mealportiontracker.core.FirebaseAuthUtil
import com.cs461.g6.mealportiontracker.utils.SessionManager

data class UserAuth(
    val userId: String,
    val email: String,
    val password: String
)

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
fun ScreenProfile(sessionManager: SessionManager, navController: NavHostController) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.user_profile),
            contentDescription = "User Profile Picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Username: ${dummyUser.username}")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Email: ${sessionManager.getUserEmail()}")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "UserId: ${dummyUser.userId}")
        Spacer(modifier = Modifier.height(16.dp))

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

// This composable displays user's image, name, email and edit button
@Composable
private fun UserDetails(context: Context) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // User's image
        Image(
            modifier = Modifier
                .size(72.dp)
                .clip(shape = CircleShape),
            painter = painterResource(id = R.drawable.user_profile),
            contentDescription = "Your Image"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(weight = 3f, fill = false)
                    .padding(start = 16.dp)
            ) {

                // User's name
                Text(
                    text = "Victoria Steele",
                    style = TextStyle(
                        fontSize = 22.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                // User's email
                Text(
                    text = "email123@email.com",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray,
                        letterSpacing = (0.8).sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Edit button
            IconButton(
                modifier = Modifier
                    .weight(weight = 1f, fill = false),
                onClick = {
                    Toast.makeText(context, "Edit Button", Toast.LENGTH_SHORT).show()
                }) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit Details",
                )
            }

        }
    }
}
