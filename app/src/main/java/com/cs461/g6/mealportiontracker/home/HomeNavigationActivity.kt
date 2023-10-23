package com.cs461.g6.mealportiontracker.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.cs461.g6.mealportiontracker.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.cs461.g6.mealportiontracker.foodimageprocessing.CameraXPreviewActivity
import com.cs461.g6.mealportiontracker.theme.MealTheme
import com.cs461.g6.mealportiontracker.utils.SessionManager

class HomeNavigationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val sessionManager = SessionManager(this)
            MealTheme{
                App(sessionManager)
            }
        }
    }

}


@Composable
fun App(sessionManager: SessionManager,
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
            AppNavHost(sessionManager, navController)
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
    var selectedIndex by remember { mutableIntStateOf(0) }

    BottomNavigation {
        listItems.forEachIndexed { index, label ->
            BottomNavigationItem(
                unselectedContentColor =   Color(0x66FFFFFF),
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
                        1 -> navController.navigate(AppScreen.ScreenHistory.name)
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
    val context = LocalContext.current
    FloatingActionButton(
        onClick = {

        },
        contentColor = Color.White
    ) {
        IconButton(onClick = {
            val intent = Intent(context, CameraXPreviewActivity::class.java)
            context.startActivity(intent)
        }) {
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
    sessionManager: SessionManager,
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        // ---------------------------- The first screen to load
        startDestination = AppScreen.ScreenProfile.name,
    ) {

        composable(route = AppScreen.ScreenProfile.name) {
            ScreenProfile(sessionManager, navController)
        }

        composable(route = AppScreen.ScreenHistory.name) {
            ScreenHistory()
        }

        composable(route = AppScreen.ScreenStats.name) {
            ScreenStats()
        }

        composable(route = AppScreen.ScreenSettings.name) {
            ScreenSettings()
        }
    }
}





