package com.orbitalsonic.camraxtesting

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "symptom_data")
data class SymptomData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val symptom: String,
    val rating: Int,
    val heartRate: Int,
    val respiratoryRate: Int
)
