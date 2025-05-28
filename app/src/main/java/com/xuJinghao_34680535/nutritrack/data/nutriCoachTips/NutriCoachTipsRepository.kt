package com.xuJinghao_34680535.nutritrack.data.nutriCoachTips

import android.content.Context
import com.xuJinghao_34680535.nutritrack.data.AppDatabase
import kotlinx.coroutines.flow.Flow

class NutriCoachTipsRepository(context: Context) {

    private val nutriCoachTipsDao = AppDatabase.getDatabase(context).NutriCoachTipsDao()

    // Insert a new NutriCoachTip into the database
    suspend fun insertTip(tip: NutriCoachTips) {
        nutriCoachTipsDao.insert(tip)
    }

    // Fetch all NutriCoachTips for a specific userId as a Flow of a list
    fun getAllTips(userId: String): Flow<List<NutriCoachTips>> {
        return nutriCoachTipsDao.getAllTips(userId)
    }
}
