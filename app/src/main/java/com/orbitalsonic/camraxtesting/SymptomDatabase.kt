package com.orbitalsonic.camraxtesting

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(entities = [SymptomData::class], version = 3, exportSchema = false)
abstract class SymptomDatabase : RoomDatabase() {

    // Access to DAO
    abstract fun symptomDao(): SymptomDao

    companion object {
        @Volatile
        private var INSTANCE: SymptomDatabase? = null

        // Define migration from version 1 to version 3
        val MIGRATION_1_3 = object : Migration(1, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Example: Adding new columns or modifying schema (adjust accordingly)
                // database.execSQL("ALTER TABLE symptom_data ADD COLUMN new_column_name INTEGER NOT NULL DEFAULT 0")
                // If no structural changes occurred, you can leave this empty or add a no-op
            }
        }

        // Function to get database instance
        fun getDatabase(context: Context): SymptomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SymptomDatabase::class.java,
                    "symptom_database"
                )
                    // Add the migration strategy here if needed
                    .addMigrations(MIGRATION_1_3)
                    // Uncomment the line below to fallback to destructive migration (use in development only)
                    //.fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
