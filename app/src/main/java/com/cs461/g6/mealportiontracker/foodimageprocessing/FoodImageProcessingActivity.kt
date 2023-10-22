package com.cs461.g6.mealportiontracker.foodimageprocessing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.concurrent.thread
import com.cs461.g6.mealportiontracker.home.mealColors

data class FoodInfo(
    val name: String,
    val calories: Int,
    val proteins: Int,
    val carbo: Int,
    val fats: Int
)

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
    var foodInfo by remember { mutableStateOf<FoodInfo?>(null) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (foodInfo != null) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FoodInfoRow("Food Name", foodInfo!!.name)
                FoodInfoRow("Calories", foodInfo!!.calories.toString())
                FoodInfoRow("Proteins", foodInfo!!.proteins.toString())
                FoodInfoRow("Carbo", foodInfo!!.carbo.toString())
                FoodInfoRow("Fats", foodInfo!!.fats.toString())
            }

            // Add your button here
            Button(
                onClick = {
                    // Handle the button click event
                    // You can add the logic to add the food item to a list, for example
                },
                modifier = Modifier.padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = mealColors.primary // Set the background color to the primary color
                )
            ) {
                Text("Add Food", color = mealColors.onPrimary)

            }

        } else {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }

    LaunchedEffect(key1 = Unit) {
        processFoodImage(context, imageUri) { resultFoodInfo ->
            foodInfo = resultFoodInfo
        }
    }
}

@Composable
fun FoodInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label: ",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Text(
            text = value,
            color = Color.Black,
            fontSize = 20.sp
        )
    }
}

private fun processFoodImage(
    context: Context,
    imageUri: String,
    resultFoodInfo: (FoodInfo) -> Unit
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
                            File(it).absolutePath
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
                        val foodInfo = getFoodNameAndCaloriesFromModelResult(context, maxScoreIdx)
                        resultFoodInfo(foodInfo)
                    } catch (error: Exception) {
                        error.localizedMessage?.let { Log.e("AI error", it) }
                    }
                }
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        })
}

private fun getFoodNameAndCaloriesFromModelResult(context: Context, score: Int): FoodInfo {
    lateinit var jsonString: String
    try {
        jsonString = context.assets.open("food_and_calories.json")
            .bufferedReader()
            .use { it.readText() }
    } catch (ioException: IOException) {
        ioException.localizedMessage?.let { Log.e("error", it) }
    }

    val listType = object : TypeToken<List<String>>() {}.type
    val foodList: List<String> = Gson().fromJson(jsonString, listType)
    val foodData = foodList[score].split(": ")
    val foodName = foodData[0]
    val nutrientValues = foodData[1].split(", ").map { it.toInt() }

    if (nutrientValues.size == 4) {
        val calories = nutrientValues[0]
        val proteins = nutrientValues[1]
        val carbo = nutrientValues[2]
        val fats = nutrientValues[3]

        return FoodInfo(foodName, calories, proteins, carbo, fats)
    } else {
        // Handle invalid data in the JSON
        return FoodInfo("", 0, 0, 0, 0)
    }
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
            "Error processing asset $assetName to a file path"
        )
    }
    return null
}
