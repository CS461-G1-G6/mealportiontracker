package com.cs461.g6.mealportiontracker.foodimageprocessing

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.cs461.g6.mealportiontracker.home.mealColors
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.FirebaseApp
import android.net.Uri
import androidx.compose.foundation.layout.Spacer
import com.cs461.g6.mealportiontracker.core.FirebaseAuthUtil
import com.cs461.g6.mealportiontracker.home.Statistics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

data class FoodInfo(
    val name: String,
    val calories: Int,
    val proteins: Int,
    val carbo: Int,
    val fats: Int
)

data class FoodInfoWithDate(
    val name: String,
    val calories: Int,
    val proteins: Int,
    val carbo: Int,
    val fats: Int,
    val date: String,
    val imageUrl: String,
    val userId: String
)

class FoodImageProcessingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        val imageUri = intent.getStringExtra("imageUri")
        if (imageUri != null) {
            setContent {
                App(imageUri)
            }
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
                Image(
                    painter = rememberImagePainter(data = imageUri),
                    contentDescription = null, // Set a meaningful content description
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))

                FoodInfoRow("Food Name", foodInfo!!.name)
                FoodInfoRow("Calories (Per 100g)", foodInfo!!.calories.toString())
                FoodInfoRow("Proteins (Per 100g)", foodInfo!!.proteins.toString())
                FoodInfoRow("Carbohydrates (Per 100g)", foodInfo!!.carbo.toString())
                FoodInfoRow("Fats (Per 100g)", foodInfo!!.fats.toString())
            }

            // Add your button here
            Button(
                onClick = {
                    if (foodInfo != null) {
                        addFoodInfoToFirebase(context, foodInfo!!, imageUri)

                    } else {
                        mToast(context, "No Food information")
                    }
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
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
//        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label: ",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
//            fontSize = 16.sp
        )
        Text(
            text = value,
            color = Color.Black,
//            fontSize = 16.sp
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

private fun addFoodInfoToFirebase(context: Context, foodInfo: FoodInfo, imageUri: String) {
    val storageReference = FirebaseStorage.getInstance().reference
    val databaseReference = FirebaseDatabase.getInstance().getReference("meal_histories")

    // Generate a new unique key for the data
    val foodInfoKey = databaseReference.push().key

    // Format the date in "dd/MM/yyyy" format
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = dateFormat.format(Date())

    val currentUser = FirebaseAuthUtil.getCurrentUser() // Get the currently signed-in user

    if (currentUser != null) {
        if (foodInfoKey != null) {
            // First, upload the image to Firebase Storage
            val imageRef = storageReference.child("images/$foodInfoKey.jpg")
            val uploadTask = imageRef.putFile(Uri.parse(imageUri))

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                // Continue with the task to get the download URL
                imageRef.downloadUrl
            }.addOnCompleteListener { downloadUrlTask ->
                if (downloadUrlTask.isSuccessful) {
                    val downloadUri = downloadUrlTask.result

                    if (downloadUri != null) {
                        // Now, create a FoodInfoWithDate object with the image URL
                        val foodInfoWithDate = FoodInfoWithDate(
                            name = foodInfo.name,
                            calories = foodInfo.calories,
                            proteins = foodInfo.proteins,
                            carbo = foodInfo.carbo,
                            fats = foodInfo.fats,
                            date = date,
                            imageUrl = downloadUri.toString(),
                            userId = currentUser.uid // Include the user's ID
                        )

                        // Add the FoodInfoWithDate object to the database under the generated key
                        databaseReference.child(foodInfoKey).setValue(foodInfoWithDate)
                            .addOnCompleteListener { saveTask ->
                                if (saveTask.isSuccessful) {
                                    // Data was successfully saved to the database
                                    // You can add any further logic here if needed
                                    mToast(context, "Food information added to the database!")
                                    val intent = Intent(context, Statistics::class.java)
                                    context.startActivity(intent)
                                } else {
                                    // Handle database save failure
                                    val saveException = saveTask.exception
                                    mToast(context, "Failed to save food information: ${saveException?.message}")
                                }
                            }
                    } else {
                        mToast(context, "Download URL is null.")
                    }
                } else {
                    // Handle failure to get the image URL
                    mToast(context, "Failed to get image URL: ${downloadUrlTask.exception?.message}")
                }
            }
        } else {
            mToast(context, "Failed to generate a unique key for data.")
        }
    } else {
        mToast(context, "User is not authenticated.")
    }
}





fun mToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
