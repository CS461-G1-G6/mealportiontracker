package com.cs461.g6.mealportiontracker.accounts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.cs461.g6.mealportiontracker.home.ScreenProfile
import com.cs461.g6.mealportiontracker.utils.SessionManager
import kotlinx.coroutines.delay

class AccountNavigationActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sessionManager = SessionManager(this)

        val initialRoute = if (sessionManager.getIsUserLoggedIn()) {
            com.cs461.g6.mealportiontracker.home.AppScreen.ScreenProfile.name
        } else {
            AppScreen.ScreenSplash.name
        }

        setContent {
            App(initialRoute, sessionManager)
            // TODO: Change to Onboarding, Register / Login Navigation
        }
    }
}

@Composable
fun App(initialRoute: String,
        sessionManager: SessionManager,
        navController: NavHostController = rememberNavController()) {

    val backStackEntry by navController.currentBackStackEntryAsState()
    backStackEntry?.destination?.route ?: initialRoute
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        // --------------- SNACKBAR HOST
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding) // #1
        ) {
            // or you can directly pass the modifier(#1) to AppNavHost(..)
            AppNavHost(navController, sessionManager)
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

// ---------------------------- Manages the navigation between pages
@Composable
private fun AppNavHost(navController: NavHostController, sessionManager: SessionManager) {
    NavHost(
        navController = navController,
        // ---------------------------- The first screen to load
        startDestination = AppScreen.ScreenSplash.name,
    ) {
        composable(route = com.cs461.g6.mealportiontracker.home.AppScreen.ScreenProfile.name) {
            ScreenProfile(sessionManager, navController)
        }
        composable(route = AppScreen.ScreenSplash.name) {
            LaunchedEffect(Unit) {
                delay(2000) // Show SplashScreen for 2 seconds
                if (sessionManager.getIsUserLoggedIn()) {
                    navController.navigate(com.cs461.g6.mealportiontracker.home.AppScreen.ScreenProfile.name) {
                        popUpTo(com.cs461.g6.mealportiontracker.home.AppScreen.ScreenProfile.name) { inclusive = true }
                    }
                } else {
                    navController.navigate(AppScreen.ScreenLogin.name) {
                        popUpTo(AppScreen.ScreenLogin.name) { inclusive = true }
                    }
                }
                /*navController.navigate(AppScreen.ScreenLogin.name) {
                    popUpTo(AppScreen.ScreenLogin.name) { inclusive = true }
                }*/
            }
        }

        composable(route = AppScreen.ScreenLogin.name) {
            LoginScreen(navController, sessionManager)
        }

        composable(route = com.cs461.g6.mealportiontracker.home.AppScreen.ScreenProfile.name) {
            ScreenProfile(sessionManager, navController)
        }

        // Add other composables/routes here

    }
}


