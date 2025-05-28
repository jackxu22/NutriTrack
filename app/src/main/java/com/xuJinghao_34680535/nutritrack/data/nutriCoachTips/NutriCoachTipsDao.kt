package com.xuJinghao_34680535.nutritrack.data.nutriCoachTips

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NutriCoachTipsDao {
    // Insert a new NutriCoachTip into the database
    @Insert
    suspend fun insert(tip: NutriCoachTips)

    // Get all NutriCoachTips for a specific userId, ordered by timestamp in descending order
    @Query("SELECT * FROM nutri_coach_tips WHERE patientId = :userId ORDER BY timestamp DESC")
    fun getAllTips(userId: String): Flow<List<NutriCoachTips>>
}