package com.cs461.g6.mealportiontracker.home

enum class AppScreen(val title: String) {
    //Bottom Navigation Tabs
    ScreenProfile(title = "Your Profile"),
    ScreenHistory(title = "Your Meal History"),
    ScreenStats(title = "Your Meal Statistics"),
    ScreenForums(title = "Forums"),
    ScreenAddMeal(title = "Log a Meal")
}
