package com.cs461.g6.mealportiontracker.accounts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarData
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ComposeNavigationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App()
            // TODO: Change to Onboarding, Register / Login Navigation
        }
    }
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
            MyBottomNavBar(scope,scaffoldState)
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
fun MySnackbar(data: SnackbarData){
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

@Composable
fun MyBottomNavBar(
    scope: CoroutineScope,
    scaffoldState:ScaffoldState
) {
    val listItems = listOf("Profile", "History", "Dash", "Log")
    var selectedIndex by remember { mutableStateOf(0) }

    BottomNavigation {
        listItems.forEachIndexed { index, label ->
            BottomNavigationItem(
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
                        "Dash" -> Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null
                        )
                        "Log" -> Icon(
                            imageVector = Icons.Filled.List,
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
                    scope.launch {
                        val result = scaffoldState.snackbarHostState.showSnackbar(
                            message = "Clicked$index, $label",
                            actionLabel = "OK",
                            duration = SnackbarDuration.Short
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

        },
        backgroundColor = Color.Yellow
    ) {
        IconButton(onClick = {}) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
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
        startDestination = AppScreen.ScreenA.name,
    ) {
        // ---------------------------- Add screens here as needed
//        composable(route = AppScreen.ScreenB.name) {
//            ScreenB(
//                onBackClick = { navController.navigateUp() },
//                onNextClick = { navController.navigate(AppScreen.ScreenC.name) },
//            )
//        }
        composable(route = AppScreen.ScreenA.name) {
            ScreenA(
                onNextClick = { navController.navigate(AppScreen.ScreenB.name) }
            )
        }

        composable(route = AppScreen.ScreenB.name) {
            ScreenB(
                onBackClick = { navController.navigateUp() },
                onNextClick = { navController.navigate(AppScreen.ScreenC.name) },
            )
        }

        composable(route = AppScreen.ScreenC.name) {
            ScreenC(
                onBackClick = { navController.navigateUp() },
                onResetClick = {
                    navController.popBackStack(
                        route = AppScreen.ScreenA.name,
                        inclusive = false
                    )
                }
            )
        }
    }
}

// ---------------------------- Pages here
@Composable
private fun ScreenA(
    onNextClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Screen A",
            style = MaterialTheme.typography.h5
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNextClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Navigate to Screen B")
        }
    }
}

@Composable
private fun ScreenB(
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Screen B",
            style = MaterialTheme.typography.h5
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Navigate to Screen A")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onNextClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Navigate to Screen C")
        }

    }
}

@Composable
private fun ScreenC(
    onBackClick: () -> Unit,
    onResetClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Screen C",
            style = MaterialTheme.typography.h5
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Navigate to Screen B")
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = onResetClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Navigate to Screen A (PopupTo with Inclusive)")
        }
    }
}
