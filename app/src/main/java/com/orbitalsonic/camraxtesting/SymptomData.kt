package com.orbitalsonic.camraxtesting

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "symptom_data")
data class SymptomData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val heartRate: Int,
    val respiratoryRate: Int,
    val nausea: Int = 0,
    val headache: Int = 0,
    val diarrhea: Int = 0,
    val soreThroat: Int = 0,
    val fever: Int = 0,
    val muscleAche: Int = 0,
    val lossOfSmellTaste: Int = 0,
    val cough: Int = 0,
    val shortnessOfBreath: Int = 0,
    val feelingTired: Int = 0
)
