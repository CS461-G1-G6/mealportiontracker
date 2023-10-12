package com.cs461.g6.mealportiontracker.core

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cs461.g6.mealportiontracker.R
import com.cs461.g6.mealportiontracker.home.ComposeNavigationActivity
import com.cs461.g6.mealportiontracker.samples.animation.Animation1Activity
import com.cs461.g6.mealportiontracker.samples.animation.Animation2Activity
import com.cs461.g6.mealportiontracker.samples.animation.ListAnimationActivity
import com.cs461.g6.mealportiontracker.samples.animation.TextAnimationActivity
import com.cs461.g6.mealportiontracker.samples.customview.CustomViewActivity
import com.cs461.g6.mealportiontracker.samples.customview.CustomViewPaintActivity
import com.cs461.g6.mealportiontracker.samples.customview.MeasuringScaleActivity
import com.cs461.g6.mealportiontracker.samples.customview.ZoomableActivity
import com.cs461.g6.mealportiontracker.samples.image.ImageActivity
import com.cs461.g6.mealportiontracker.samples.interop.ComposeInClassicAndroidActivity
import com.cs461.g6.mealportiontracker.samples.layout.ConstraintLayoutActivity
import com.cs461.g6.mealportiontracker.samples.layout.LayoutModifierActivity
import com.cs461.g6.mealportiontracker.samples.layout.ViewLayoutConfigurationsActivity
import com.cs461.g6.mealportiontracker.samples.material.AlertDialogActivity
import com.cs461.g6.mealportiontracker.samples.material.BottomNavigationActivity
import com.cs461.g6.mealportiontracker.samples.material.ButtonActivity
import com.cs461.g6.mealportiontracker.samples.material.DrawerAppActivity
import com.cs461.g6.mealportiontracker.samples.material.FixedActionButtonActivity
import com.cs461.g6.mealportiontracker.samples.material.FlowRowActivity
import com.cs461.g6.mealportiontracker.samples.material.MaterialActivity
import com.cs461.g6.mealportiontracker.samples.material.ShadowActivity
import com.cs461.g6.mealportiontracker.samples.scrollers.HorizontalScrollableActivity
import com.cs461.g6.mealportiontracker.samples.scrollers.VerticalScrollableActivity
import com.cs461.g6.mealportiontracker.samples.stack.StackActivity
import com.cs461.g6.mealportiontracker.samples.state.ProcessDeathActivity
import com.cs461.g6.mealportiontracker.samples.state.StateActivity
import com.cs461.g6.mealportiontracker.samples.state.backpress.BackPressActivity
import com.cs461.g6.mealportiontracker.samples.state.coroutine.CoroutineFlowActivity
import com.cs461.g6.mealportiontracker.samples.state.livedata.LiveDataActivity
import com.cs461.g6.mealportiontracker.samples.text.CustomTextActivity
import com.cs461.g6.mealportiontracker.samples.text.SimpleTextActivity
import com.cs461.g6.mealportiontracker.samples.text.TextFieldActivity
import com.cs461.g6.mealportiontracker.theme.DarkModeActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* TODO: Different starting screens if
            1. (Onboarding) First time opening app
            2. (Not Logged In) Sign-in / Sign-in as guest
            3. (Logged In) Home Screen
       */

        startActivity(Intent(this, ComposeNavigationActivity::class.java))
        // For debugging / viewing demos of components only
        setContentView(R.layout.activity_main)
    }

    fun startSimpleTextExample(view: View) {
        startActivity(Intent(this, SimpleTextActivity::class.java))
    }

    fun startCustomTextExample(view: View) {
        startActivity(Intent(this, CustomTextActivity::class.java))
    }

    fun startVerticalScrollableExample(view: View) {
        startActivity(Intent(this, VerticalScrollableActivity::class.java))
    }

    fun startHorizontalScrollableExample(view: View) {
        startActivity(Intent(this, HorizontalScrollableActivity::class.java))
    }

    fun starLoadImageExample(view: View) {
        startActivity(Intent(this, ImageActivity::class.java))
    }

    fun startAlertDialogExample(view: View) {
        startActivity(Intent(this, AlertDialogActivity::class.java))
    }

    fun startDrawerExample(view: View) {
        startActivity(Intent(this, DrawerAppActivity::class.java))
    }

    fun startButtonsExample(view: View) {
        startActivity(Intent(this, ButtonActivity::class.java))
    }

    fun startStateExample(view: View) {
        startActivity(Intent(this, StateActivity::class.java))
    }

    fun startCustomViewExample(view: View) {
        startActivity(Intent(this, CustomViewActivity::class.java))
    }

    fun startCustomViewPaintExample(view: View) {
        startActivity(Intent(this, CustomViewPaintActivity::class.java))
    }

    fun startAutofillTextExample(view: View) {
        startActivity(Intent(this, TextFieldActivity::class.java))
    }

    fun startStackExample(view: View) {
        startActivity(Intent(this, StackActivity::class.java))
    }

    fun startViewAlignExample(view: View) {
        startActivity(Intent(this, ViewLayoutConfigurationsActivity::class.java))
    }

    fun startMaterialDesignExample(view: View) {
        startActivity(Intent(this, MaterialActivity::class.java))
    }

    fun startFixedActionButtonExample(view: View) {
        startActivity(Intent(this, FixedActionButtonActivity::class.java))
    }

    fun startConstraintLayoutExample(view: View) {
        startActivity(Intent(this, ConstraintLayoutActivity::class.java))
    }

    fun startBottomNavigationExample(view: View) {
        startActivity(Intent(this, BottomNavigationActivity::class.java))
    }

    fun startAnimation1Example(view: View) {
        startActivity(Intent(this, Animation1Activity::class.java))
    }

    fun startAnimation2Example(view: View) {
        startActivity(Intent(this, Animation2Activity::class.java))
    }

    fun startTextInlineContentExample(view: View) {
        startActivity(Intent(this, TextAnimationActivity::class.java))
    }

    fun startListAnimation(view: View) {
        startActivity(Intent(this, ListAnimationActivity::class.java))
    }

    fun startThemeExample(view: View) {
        startActivity(Intent(this, DarkModeActivity::class.java))
    }

    fun startLayoutModifierExample(view: View) {
        startActivity(Intent(this, LayoutModifierActivity::class.java))
    }

    fun startProcessDeathExample(view: View) {
        startActivity(Intent(this, ProcessDeathActivity::class.java))
    }

    fun startLiveDataExample(view: View) {
        startActivity(Intent(this, LiveDataActivity::class.java))
    }

    fun startFlowRowExample(view: View) {
        startActivity(Intent(this, FlowRowActivity::class.java))
    }

    fun startShadowExample(view: View) {
        startActivity(Intent(this, ShadowActivity::class.java))
    }

    fun startCoroutineFlowExample(view: View) {
        startActivity(Intent(this, CoroutineFlowActivity::class.java))
    }

    fun startComposeWithClassicAndroidExample(view: View) {
        startActivity(Intent(this, ComposeInClassicAndroidActivity::class.java))
    }

    fun startMeasuringScaleExample(view: View) {
        startActivity(Intent(this, MeasuringScaleActivity::class.java))
    }

    fun startBackPressExample(view: View) {
        startActivity(Intent(this, BackPressActivity::class.java))
    }

    fun startZoomableExample(view: View) {
        startActivity(Intent(this, ZoomableActivity::class.java))
    }

    fun startComposeNavigationExample(view: View) {
        startActivity(Intent(this, ComposeNavigationActivity::class.java))
    }
}
