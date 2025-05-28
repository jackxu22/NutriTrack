package com.xuJinghao_34680535.nutritrack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.xuJinghao_34680535.nutritrack.data.patient.Patient
import com.xuJinghao_34680535.nutritrack.data.patient.PatientDao
import com.xuJinghao_34680535.nutritrack.data.foodIntake.FoodIntake
import com.xuJinghao_34680535.nutritrack.data.foodIntake.FoodIntakeDao
import com.xuJinghao_34680535.nutritrack.data.nutriCoachTips.NutriCoachTips
import com.xuJinghao_34680535.nutritrack.data.nutriCoachTips.NutriCoachTipsDao
import com.xuJinghao_34680535.nutritrack.data.personaTime.PersonaTime
import com.xuJinghao_34680535.nutritrack.data.personaTime.PersonaTimeDao

// Room database class that defines the database configuration and serves as the main access point
@Database(
    entities = [Patient::class, FoodIntake::class, PersonaTime::class, NutriCoachTips::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun foodIntakeDao(): FoodIntakeDao
    abstract fun personaTimeDao(): PersonaTimeDao
    abstract fun NutriCoachTipsDao(): NutriCoachTipsDao

    // Companion object to manage the singleton instance of the database
    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        // Function to get or create the singleton instance of the database
        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                // Build the Room database with the specified context and database name
                Room.databaseBuilder(context, AppDatabase::class.java, "nutritrack_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}