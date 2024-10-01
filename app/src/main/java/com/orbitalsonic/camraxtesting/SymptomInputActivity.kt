package com.orbitalsonic.camraxtesting

import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SymptomInputActivity : AppCompatActivity() {

    private lateinit var symptomSpinner: Spinner
    private lateinit var ratingBar: RatingBar
    private lateinit var uploadButton: Button
    private lateinit var symptomDao: SymptomDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.symptoms_activity)

        // Initialize UI elements
        symptomSpinner = findViewById(R.id.symptomSpinner)
        ratingBar = findViewById(R.id.symptomRatingBar)
        uploadButton = findViewById(R.id.uploadSymptomsBtn)

        // Initialize database (Room)
        val db = SymptomDatabase.getInstance(applicationContext)
        symptomDao = db.symptomDao()

        uploadButton.setOnClickListener {
            // Get the selected symptom and rating
            val selectedSymptom = symptomSpinner.selectedItem.toString()
            val symptomRating = ratingBar.rating.toInt()

            // Insert the symptom and rating into the database
            saveSymptomData(selectedSymptom, symptomRating)
        }
    }

    private fun saveSymptomData(symptom: String, rating: Int) {
        val heartRate = intent.getIntExtra("heart_rate", 0)
        val respRate = intent.getIntExtra("resp_rate", 0)

        val symptomData = SymptomData(
            symptom = symptom,
            rating = rating,
            heartRate = heartRate,
            respiratoryRate = respRate
        )

        GlobalScope.launch {
            symptomDao.insertSymptomData(symptomData)
            runOnUiThread {
                Toast.makeText(this@SymptomInputActivity, "Symptoms uploaded", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
