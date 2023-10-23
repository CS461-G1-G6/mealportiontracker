package com.cs461.g6.mealportiontracker.core

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cs461.g6.mealportiontracker.accounts.AccountNavigationActivity
import com.cs461.g6.mealportiontracker.home.HomeNavigationActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        startActivity(Intent(this, AccountNavigationActivity::class.java))
        startActivity(Intent(this, HomeNavigationActivity::class.java))

    }
}
