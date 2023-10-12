package com.cs461.g6.mealportiontracker.home

/**
 * title can also come from StringRes
 *
 * enum class Screen(@StringRes title: Int) { ... }
 */
enum class AppScreen(val title: String) {
    ScreenA(title = "Screen A"),
    ScreenB(title = "Screen B"),
    ScreenC(title = "Screen C"),

    //Bottom Navigation Tabs
    ScreenProfile(title = "Profile"),
    ScreenHistory(title = "Your Meal History"),
    ScreenDash(title = ""),
    ScreenLog(title = ""),
    ScreenAddMeal(title = ""),


//    Screen(title = ""),
}
