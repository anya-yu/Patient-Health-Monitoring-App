package com.orbitalsonic.camraxtesting

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SymptomDao {
    @Insert
    suspend fun insert(symptomData: SymptomData)
}

