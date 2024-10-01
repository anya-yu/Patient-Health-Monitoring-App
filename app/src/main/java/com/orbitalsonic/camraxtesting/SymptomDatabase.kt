package com.orbitalsonic.camraxtesting

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SymptomData::class], version = 1)
abstract class SymptomDatabase : RoomDatabase() {

    abstract fun symptomDao(): SymptomDao

    companion object {
        @Volatile
        private var INSTANCE: SymptomDatabase? = null

        fun getInstance(context: Context): SymptomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SymptomDatabase::class.java,
                    "symptom_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
