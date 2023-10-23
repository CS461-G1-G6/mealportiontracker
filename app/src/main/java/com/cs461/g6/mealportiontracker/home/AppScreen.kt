package com.cs461.g6.mealportiontracker.home

enum class AppScreen(val title: String) {
    ScreenA(title = "Screen A"),
    ScreenB(title = "Screen B"),
    ScreenC(title = "Screen C"),
    ScreenD(title = "Screen C"),

    //Bottom Navigation Tabs
    ScreenProfile(title = "Profile"),
    ScreenHistory(title = "Your Meal History"),
    ScreenStats(title = "Your Progress"),
    ScreenManual(title = "Log a Meal"),

    ScreenDash(title = ""),
    ScreenLog(title = ""),
    ScreenAddMeal(title = ""),
}
