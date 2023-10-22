package com.cs461.g6.mealportiontracker.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.lightColors
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cs461.g6.mealportiontracker.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class HomeNavigationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MealTheme{
                App()
            }
        }
    }

}


val mealColors = lightColors(
    primary = Color(0xFFFF9C29),
    primaryVariant = Color(0xFFF8694D),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFFA1C44D),
    secondaryVariant = Color(0xFFFFD966),
    onSecondary = Color(0xFF000000),
    background = Color(0xFFFDF4DD),
    onBackground = Color(0xFF000000),
    surface = Color(0xFFFFDF92),
    onSurface = Color(0xFF000000),
    error = Color(0xFFB00020)
)



@Composable
fun MealTheme(children: @Composable () -> Unit) {
    MaterialTheme(colors = mealColors, content = children)
}

@Composable
fun App(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AppScreen.valueOf(
        backStackEntry?.destination?.route ?: AppScreen.ScreenA.name
    )
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        // --------------- TOP BAR
        topBar = {
            MyTopAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        },
        // --------------- BOTTOM NAVIGATION
        bottomBar = {
            MyBottomNavBar(scope, scaffoldState, navController)
        },

        // --------------- FLOATING BUTTON
        floatingActionButton = {
            MyBottomNavBarFAB()
        },

        // --------------- SNACKBAR HOST
        snackbarHost = {
            // reuse default SnackbarHost to have default animation and timing handling
            SnackbarHost(it) { snackBarData ->
                // custom snackbar with the custom border
                MySnackbar(data = snackBarData)
            }
        },

        ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding) // #1
        ) {
            // or you can directly pass the modifier(#1) to AppNavHost(..)
            AppNavHost(navController)
        }
    }
}

// ---------------------------- Main App's Top App Bar
@Composable
private fun MyTopAppBar(
    currentScreen: AppScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Text(text = currentScreen.title)
        },
        modifier = modifier,
        navigationIcon = if (canNavigateBack) {
            {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        } else null,
    )
}


@Composable
fun MySnackbar(data: SnackbarData) {
    Card(shape = RoundedCornerShape(4.dp), modifier = Modifier.padding(8.dp)) {
        Snackbar(
            content = {
                Text(
                    text = "Hello, World!"
                    )

            }, action = {
                if (data.actionLabel != null) {
                    Text(text = data.actionLabel.toString(), color = Color.Yellow)
                }
            }
        )
    }
}

@Composable
fun MyBottomNavBar(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    navController: NavHostController
) {
    val listItems = listOf("Profile", "History", "Stats", "Settings")
    var selectedIndex by remember { mutableStateOf(0) }

    BottomNavigation {
        listItems.forEachIndexed { index, label ->
            BottomNavigationItem(
                unselectedContentColor = mealColors.surface,
                icon = {
                    when (label) {
                        "Profile" -> Icon(
                            imageVector = Icons.Filled.Face,
                            contentDescription = null
                        )

                        "History" -> Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = null
                        )

                        "Stats" -> Icon(
                            painter = painterResource(id = R.drawable.ic_piechart),
                            contentDescription = "Stats"
                        )


                        "Settings" -> Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = null
                        )
                    }
                },
                label = {
                    Text(text = label)
                },
                selected = selectedIndex == index,
                onClick = {
                    selectedIndex = index
                    when (index) {
                        0 -> navController.navigate(AppScreen.ScreenProfile.name)
                        1 -> navController.navigate(AppScreen.ScreenManualInput.name)
                        //1 -> navController.navigate(AppScreen.ScreenHistory.name)
                        2 -> navController.navigate(AppScreen.ScreenStats.name)
                        3 -> navController.navigate(AppScreen.ScreenSettings.name)
                    }
                    scope.launch {
                        val result = scaffoldState.snackbarHostState.showSnackbar(
                            message = "Clicked$index, $label",
                            actionLabel = "OK"
                        )
                        when (result) {
                            SnackbarResult.ActionPerformed -> {
                                //Do Something
                            }

                            SnackbarResult.Dismissed -> {
                                //Do Something
                            }
                        }
                    }
                },
                alwaysShowLabel = true
            )
        }
    }
}

@Composable
fun MyBottomNavBarFAB() {
    FloatingActionButton(
        onClick = {
                  // Go to Camera
        },
        contentColor = Color.White
    ) {
        IconButton(onClick = {}) {
            Icon(
                painter = painterResource(id = R.drawable.ic_camera),
                contentDescription = "Custom Icon"
            )
        }
    }
}


// ---------------------------- Manages the navigation between pages
@Composable
private fun AppNavHost(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        // ---------------------------- The first screen to load
        startDestination = AppScreen.ScreenProfile.name,
    ) {

        composable(route = AppScreen.ScreenProfile.name) {
            ScreenProfile()
        }

        /*composable(route = AppScreen.ScreenHistory.name) {
            ScreenHistory()
        }*/

        composable(route = AppScreen.ScreenManualInput.name) {
            ScreenManualInput()
        }

        composable(route = AppScreen.ScreenStats.name) {
            ScreenStats()
        }

        composable(route = AppScreen.ScreenSettings.name) {
            ScreenSettings()
        }
    }
}





