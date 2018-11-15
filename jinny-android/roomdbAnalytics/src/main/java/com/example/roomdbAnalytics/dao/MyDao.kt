package com.example.roomdbAnalytics.dao

import android.arch.persistence.room.*
import com.example.roomdbAnalytics.model.DefaultAnalytics

@Dao
interface MyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDefaultAnalytics(default: DefaultAnalytics?)

    @Query("DELETE FROM analytics")
    fun deleteDefaultAnalytics()

    @Query("SELECT * FROM analytics")
    fun queryDefaultAnalytics(): List<DefaultAnalytics>?
}