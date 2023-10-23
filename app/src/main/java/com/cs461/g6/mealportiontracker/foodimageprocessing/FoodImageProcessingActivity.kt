package com.cs461.g6.mealportiontracker.foodimageprocessing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.*
import kotlin.concurrent.thread


class FoodImageProcessingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App(imageUri = intent.getStringExtra("imageUri")!!)
        }
    }
}

@Composable
fun App(imageUri: String) {
    val context = LocalContext.current
    var result by remember {
        mutableStateOf("")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (result.isNotEmpty()) {
            val resultWithCalories = "$result Calories"

            Text(
                text = resultWithCalories,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center),
                fontWeight = FontWeight.Black,
                fontSize = 30.sp
            )
        } else {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

    LaunchedEffect(key1 = Unit) {
        processFoodImage(context, imageUri) { resultText ->
            result = resultText
        }
    }

}

private fun processFoodImage(
    context: Context,
    imageUri: String,
    resultText: (result: String) -> Unit
) {
    Glide.with(context)
        .asBitmap()
        .load(imageUri)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(
                resource: Bitmap,
                transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
            ) {

                thread {
                    try {
                        val resizedBitmap = Bitmap.createScaledBitmap(resource, 224, 224, false)
                        val moduleFileAbsoluteFilePath = assetFilePath(context, "model.pt")?.let {
                            File(
                                it
                            ).absolutePath
                        }
                        val module = Module.load(moduleFileAbsoluteFilePath)
                        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
                            resizedBitmap,
                            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                            TensorImageUtils.TORCHVISION_NORM_STD_RGB,
                        )
                        val outputTensor =
                            module.forward(IValue.from(inputTensor)).toTuple()[0].toTensor()
                        val scores = outputTensor.dataAsFloatArray

                        var maxScore = -Float.MAX_VALUE
                        var maxScoreIdx = -1
                        for (i in scores.indices) {
                            if (scores[i] > maxScore) {
                                maxScore = scores[i]
                                maxScoreIdx = i
                            }
                        }

                        Log.v("ai result", maxScoreIdx.toString())
                        resultText(getFoodNameAndCaloriesFromModelResult(context, maxScoreIdx).uppercase())
                    } catch (error: Exception) {
                        error.localizedMessage?.let { Log.e("AI error", it) }
                    }

                }
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        })
}

private fun getFoodNameAndCaloriesFromModelResult(context: Context, score: Int): String {
    lateinit var jsonString: String
    try {
        jsonString = context.assets.open("food_and_calories.json")
            .bufferedReader()
            .use { it.readText() }
    } catch (ioException: IOException) {
        ioException.localizedMessage?.let { Log.e("error", it) }
    }

    val listFoodType = object : TypeToken<ArrayList<String>>() {}.type
    val foodList: ArrayList<String> = Gson().fromJson(jsonString, listFoodType)
    return foodList[score]
}

private fun assetFilePath(context: Context, assetName: String): String? {
    val file = File(context.filesDir, assetName)
    if (file.exists() && file.length() > 0) {
        return file.absolutePath
    }
    try {
        context.assets.open(assetName).use { `is` ->
            FileOutputStream(file).use { os ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (`is`.read(buffer).also { read = it } != -1) {
                    os.write(buffer, 0, read)
                }
                os.flush()
            }
            return file.absolutePath
        }
    } catch (e: IOException) {
        Log.e(
            "Error",
            "Error process asset $assetName to file path"
        )
    }
    return null
}