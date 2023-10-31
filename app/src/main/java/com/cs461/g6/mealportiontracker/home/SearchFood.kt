package com.cs461.g6.mealportiontracker.home

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Context.MODE_APPEND
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cs461.g6.mealportiontracker.core.FirebaseAuthUtil
import com.cs461.g6.mealportiontracker.home.ui.theme.SearchBarComposeTheme
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.PrintStream
import java.text.SimpleDateFormat
import java.util.*

class SearchFoodActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            SearchBarComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScreenSearchFood(navController, viewModel = viewModel)
                }
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.N)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScreenSearchFood(navController: NavHostController, viewModel: MainViewModel) {

    //Collecting states from ViewModel
    val searchText by viewModel.searchText.collectAsState()
    val filteredFoodItemList by viewModel.filteredFoodItemList.collectAsState()

    var query by remember { mutableStateOf(searchText) }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            SearchBar(
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
                    navController.navigate(AppScreen.ScreenInput.name)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(start = 16.dp, end = 16.dp)
        ) {
            // LazyColumn with filtered items
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                items(filteredFoodItemList) { foodItem ->
                    FoodItemRow(foodItem = foodItem, onAddButtonClick = {
                        // Handle adding the food item into the database here

                        //Need to ask
                        val userId = FirebaseAuthUtil.getCurrentUser()!!.uid

                        val protein = foodItem.proteins.replace("[^\\d.]".toRegex(), "").toDoubleOrNull() ?: 0.0

                        val carbs = foodItem.carbs.replace("[^\\d.]".toRegex(), "").toDoubleOrNull() ?: 0.0

                        val fat = foodItem.fats.replace("[^\\d.]".toRegex(), "").toDoubleOrNull() ?: 0.0

                        val currentTimeMillis = System.currentTimeMillis()
                        val sdf = SimpleDateFormat("d MMMM yyyy 'at' HH:mm:ss 'UTC'Z", Locale.getDefault())
                        val timeZone = TimeZone.getTimeZone("GMT+8") // Set your desired time zone
                        sdf.timeZone = timeZone

                        val formattedDate = sdf.format(currentTimeMillis)

                        val date: Date = sdf.parse(formattedDate) // Parse the formatted date string to Date object
                        val timestamp: Timestamp = Timestamp(date) // Convert Date object to Timestamp object

                        FirebaseAuthUtil.addMealHistory(userId, foodItem.name, foodItem.calories, protein, carbs, fat, timestamp)
                            .thenAccept { success ->
                                if (success) {
                                    Toast.makeText(
                                        context,
                                        "The food has been successfully added",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "The food could not be added. Please try again later.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                    })
                }
            }
        }
    }
}

@Composable
fun SearchBar(
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
            TextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    onQueryChange(it)
                },
                placeholder = { Text("Search food...") },
                modifier = Modifier
                    .weight(1f) // Take up remaining space
                    .padding(end = 8.dp), // Add padding to the end
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = bgColor,
                    disabledLabelColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(20.dp),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = iconColor
                    )
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = {
                            searchText = ""
                            onQueryChange("")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = iconColor
                            )
                        }
                    }
                }
            )
            // Add button next to the search bar
            Button(
                onClick = { onAddClick() }, // Call the provided onAddClick lambda when the button is clicked
                modifier = Modifier.padding(start = 8.dp, top = 3.dp) // Add padding to the start of the button
            ) {
                Text("Add")
            }
        }
    }
}

@Composable
fun FoodItemRow(foodItem: FoodItem, onAddButtonClick: () -> Unit) {
    val addColor: Color = Color(170,164,184)
    val circleColor: Color = Color(228,228,236)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Bold heading
        Text(
            text = foodItem.name,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp, // You can adjust the font size as needed
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )

        // Row containing Calories and Add button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Calories text
            Text(
                text = "Calories: ${foodItem.calories}",
                fontSize = 14.sp, // You can adjust the font size as needed
            )

            // Add icon aligned to the right end and vertically centered
            IconButton(
                onClick = { onAddButtonClick() },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = addColor,
                    modifier = Modifier
                        .size(48.dp) // Set size
                        .background(circleColor, CircleShape) // Set background color and shape
                        .padding(8.dp) // Set padding
                        .clip(CircleShape) // Clip the icon to a circle shape
                )
            }
        }

        // Proteins, Carbs, and Fats text
        Text(
            text = "Proteins: ${foodItem.proteins}, Carbs: ${foodItem.carbs}, Fats: ${foodItem.fats}",
            fontSize = 14.sp, // You can adjust the font size as needed
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(5.dp))

    }
}

data class FoodItem(
    val name: String,
    val calories: Double,
    val proteins: String,
    val carbs: String,
    val fats: String
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    //second state the text typed by the user
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    // Third state: the original list of food items from the CSV file
    private val _originalFoodItemList = MutableStateFlow<List<FoodItem>>(emptyList())
    //val originalFoodItemList = _originalFoodItemList.asStateFlow()

    // Fourth state: the filtered list of food items based on search text
    private val _filteredFoodItemList = MutableStateFlow<List<FoodItem>>(emptyList())
    val filteredFoodItemList = _filteredFoodItemList.asStateFlow()

    init {
        val context: Context = application.applicationContext
        val fileName = "food_nutrition.csv"

        val foodItems: List<FoodItem>

        if(isFilePresent(context, fileName)){
            // File exists in internal storage, read data from it
            foodItems = readInternalCsv(context, fileName)
        } else {
            // Read CSV file from assets folder and populate food list during initialization
            val inputStream: InputStream = context.assets.open("food_nutrition.csv")
            foodItems = readCsv(inputStream)

            // Write CSV data to the file in the app's data folder
            try {
                val outStream = PrintStream(context.openFileOutput(fileName, MODE_APPEND))

                // Iterate through foodItems and write them to the file
                for (foodItem in foodItems) {
                    val line = "\"${foodItem.name}\",${foodItem.calories},${foodItem.proteins},${foodItem.carbs},${foodItem.fats}"
                    outStream.println(line)
                }

                // Close the file
                outStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle the exception (e.g., show an error message to the user)
            }
        }
        _originalFoodItemList.value = foodItems
        _filteredFoodItemList.value = foodItems  // Initialize filtered list with all food

    }

    fun isFilePresent(context: Context, fileName: String): Boolean {
        val file = File(context.filesDir, fileName)
        return file.exists()
    }

    private fun parseCsvLine(line: String): FoodItem? {
        val foodItemProperties = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*\$)".toRegex())
            .map { it.trim('"') } // Remove quotes from components

        return if (foodItemProperties.size >= 5) {
            val name = foodItemProperties[0]
            val calories = foodItemProperties[1].toDoubleOrNull() ?: 0.0
            val proteins = foodItemProperties[2].trim()
            val carbs = foodItemProperties[3].trim()
            val fats = foodItemProperties[4].trim()
            FoodItem(name, calories, proteins, carbs, fats)
        } else {
            null
        }
    }

    fun readInternalCsv(context: Context, fileName: String): List<FoodItem> {
        val result: MutableList<FoodItem> = mutableListOf()

        try {
            val file = File(context.filesDir, fileName)
            val fileContents = file.readText()

            val lines = fileContents.split("\n") // Split fileContents into lines
            for (line in lines) {
                val foodItem = parseCsvLine(line)
                foodItem?.let { result.add(it) }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle IOException (e.g., show an error message to the user)
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle other exceptions (e.g., show an error message to the user)
        }

        return result
    }

    fun readCsv(inputStream: InputStream): List<FoodItem> {
        val result: MutableList<FoodItem> = mutableListOf()
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line: String?
        try {
            while (reader.readLine().also { line = it } != null) {
                val foodItem = parseCsvLine(line!!)
                foodItem?.let { result.add(it) }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return result
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text

        // Filter the food based on the search text and update the filteredFoodItemList
        _filteredFoodItemList.value = if (text.isBlank()) {
            _originalFoodItemList.value // If the search text is empty, show all food
        } else {
            _originalFoodItemList.value.filter { foodItem ->
                foodItem.name.uppercase().contains(text.trim().uppercase())
            }
        }
    }

}
