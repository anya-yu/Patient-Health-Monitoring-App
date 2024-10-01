package com.orbitalsonic.camraxtesting

import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class SymptomInputActivity : AppCompatActivity() {

    private lateinit var symptomSpinner: Spinner
    private lateinit var ratingBar: RatingBar
    private lateinit var uploadButton: Button
    private lateinit var symptomDatabase: SymptomDatabase
    private var heartRate = 0 // Replace with actual value passed from previous activity
    private var respiratoryRate = 0 // Replace with actual value passed from previous activity
    private val symptomsMap = mutableMapOf<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.symptoms_activity)

        // Initialize UI components
        symptomSpinner = findViewById(R.id.symptomSpinner)
        ratingBar = findViewById(R.id.symptomRatingBar)
        uploadButton = findViewById(R.id.uploadSymptomsBtn)

        // Initialize Room database
        symptomDatabase = SymptomDatabase.getDatabase(this)

        // List of symptoms
        val symptomsList = listOf(
            "nausea", "headache", "diarrhea", "sore throat", "fever",
            "muscle ache", "loss of smell or taste", "cough", "shortness of breath", "feeling tired"
        )

        // Set listener for rating changes
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            val selectedSymptom = symptomsList[symptomSpinner.selectedItemPosition]
            symptomsMap[selectedSymptom] = rating.toInt()
        }

        // Set default values for symptoms
        for (symptom in symptomsList) {
            symptomsMap[symptom] = 0
        }

        // Set click listener for upload button
        uploadButton.setOnClickListener {
            uploadSymptoms()
        }
    }

    private fun uploadSymptoms() {
        // Retrieve all symptom ratings and create SymptomData object
        val symptomData = SymptomData(
            heartRate = heartRate,
            respiratoryRate = respiratoryRate,
            nausea = symptomsMap["nausea"] ?: 0,
            headache = symptomsMap["headache"] ?: 0,
            diarrhea = symptomsMap["diarrhea"] ?: 0,
            soreThroat = symptomsMap["sore throat"] ?: 0,
            fever = symptomsMap["fever"] ?: 0,
            muscleAche = symptomsMap["muscle ache"] ?: 0,
            lossOfSmellTaste = symptomsMap["loss of smell or taste"] ?: 0,
            cough = symptomsMap["cough"] ?: 0,
            shortnessOfBreath = symptomsMap["shortness of breath"] ?: 0,
            feelingTired = symptomsMap["feeling tired"] ?: 0
        )

        // Insert data into the Room database
        lifecycleScope.launch {
            symptomDatabase.symptomDao().insert(symptomData)
            Toast.makeText(this@SymptomInputActivity, "Symptoms uploaded successfully", Toast.LENGTH_SHORT).show()
        }
    }
}
