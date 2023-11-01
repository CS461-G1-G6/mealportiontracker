package com.cs461.g6.mealportiontracker.home

enum class AppScreen(val title: String) {
    //Bottom Navigation Tabs
    ScreenProfile(title = "Your Profile"),
    ScreenHistory(title = "Your Meal History"),
    ScreenStats(title = "Your Calories Breakdown"),
    ScreenForums(title = "Forums"),
    ScreenInput(title = "Log a Meal"),

    ScreenSearch(title = "Search Food")

}
