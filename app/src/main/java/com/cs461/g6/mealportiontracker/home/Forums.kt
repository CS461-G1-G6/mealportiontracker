package com.cs461.g6.mealportiontracker.home

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.cs461.g6.mealportiontracker.core.FirebaseAuthUtil
import com.cs461.g6.mealportiontracker.core.SessionManager
import com.cs461.g6.mealportiontracker.theme.MealTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

data class Post(
    val userId: String = "",
    val postDate: String = "",
    val title: String = "",
    val body: String = ""
)

var postList = mutableListOf<Post>()

class Forums : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up any necessary NavHostController and SessionManager here if needed
        setContent {
            // You can pass your NavHostController and SessionManager to ScreenStats
            val navController: NavHostController = remember { NavHostController(this) }
            val sessionManager: SessionManager = remember { SessionManager(this) }

            MealTheme {
                // Display the ScreenStats composable within the ComposeView
                ScreenForums(navController, viewModel = viewModel, sessionManager)
            }
        }
    }
}
@Composable
fun ScreenForums(navController: NavHostController,
                 viewModel: MainViewModel,
                 sessionManager: SessionManager) {

    val currentUser = FirebaseAuthUtil.getCurrentUser()
    val searchText by viewModel.searchText.collectAsState()
    var query by remember { mutableStateOf(searchText) }
    val loading = remember { mutableStateOf(true) }

    LaunchedEffect(key1 = currentUser) {
        if (currentUser != null) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("forum_posts")
            val postQuery: Query = databaseReference.orderByChild("postDate")

            postQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val temp = mutableListOf<Post>()
                    for (dataSnapshot in snapshot.children) {
                        val post = dataSnapshot.getValue(Post::class.java)
                        Log.d("Test", post.toString())
                        post?.let {
                            temp.add(it)
                        }
                    }
                    postList = temp
                    loading.value = false
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database query error
//                    callback(emptyList())
                }
            })
        }
    }
    if (!loading.value) {
        Scaffold(
            topBar = {
                SearchPostBar(
                    query = query,
                    onQueryChange = {
                        query = it
                        viewModel.onSearchTextChange(it)
                    },
                    onSearch = {
                        viewModel.onSearchTextChange(query)
                    },
                    onAddClick = {
                        //val intent = Intent(context, ManualInputActivity::class.java)
                        //context.startActivity(intent)
                        navController.navigate(AppScreen.ScreenAddPost.name)
                    }
                )
            }
        ) { innerPadding ->
            Column(
                //            verticalArrangement = Arrangement.Center,
                //            horizontalAlignment = Alignment.CenterHorizontally,
                //            modifier = Modifier
                //                .fillMaxSize()
                //                .padding(16.dp)
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(postList) { post ->
                        PostEntryCard(post)
                    }
                }
            }
        }
    }
}

@Composable
fun SearchPostBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onAddClick: () -> Unit
) {
    var searchText by remember { mutableStateOf(query) }
    val bgColor: Color = Color(244, 240, 236)
    val iconColor: Color = Color(169, 169, 169)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
        ) {
            Button(
                onClick = { onAddClick() }, // Call the provided onAddClick lambda when the button is clicked
                modifier = Modifier.padding(
                    start = 8.dp,
                    top = 3.dp
                ) // Add padding to the start of the button
            ) {
                Text("Create Post")
            }
        }
    }
}

@Composable
fun PostEntryCard(post: Post) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
//        shape = RoundedCornerShape(15.dp),
        elevation = 10.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = post.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(text = "Posted on: ${post.postDate}", modifier = Modifier.padding(vertical = 2.dp))
                Text(text = post.body)
            }
        }
    }
}