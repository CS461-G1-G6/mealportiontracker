package com.cs461.g6.mealportiontracker.core

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.concurrent.CompletableFuture

object FirebaseAuthUtil {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    data class MealHistory(
        val user_id: String,
        val meal_history_id: Int,
        val food_name: String,
        val calories: Double,
        val protein: Double,
        val carbohydrates: Double,
        val fat: Double,
        val date: Timestamp
    )

    fun registerUserWithEmailAndPassword(email: String, password: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
    }

    fun loginUserWithEmailAndPassword(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun addMealHistory(
        userId: String,
        food: String,
        calories: Double,
        protein: Double,
        carbohydrates: Double,
        fat: Double,
        date: Timestamp
    ): CompletableFuture<Boolean> {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        val dbMealHistory: CollectionReference = db.collection("meal_history")

        val future = CompletableFuture<Boolean>()

        dbMealHistory.orderBy("meal_history_id", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                val mealHistoryId: Int = if (!documents.isEmpty()) {
                    val document = documents.documents[0]
                    document.getLong("meal_history_id")!!.toInt() + 1
                } else {
                    1
                }

                // Create a new MealHistory object
                val mealHistory = MealHistory(
                    userId,
                    mealHistoryId,
                    food,
                    calories,
                    protein,
                    carbohydrates,
                    fat,
                    date
                )

                dbMealHistory.add(mealHistory)
                    .addOnSuccessListener {
                        future.complete(true) // Operation succeeded, set CompletableFuture to true
                    }
                    .addOnFailureListener { e ->
                        future.complete(false) // Operation failed, set CompletableFuture to false
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreQuery", "Error getting documents: $exception")
                future.complete(false) // Operation failed, set CompletableFuture to false in case of an error
            }

        return future
    }



    fun signOut() {
        auth.signOut()
    }
}