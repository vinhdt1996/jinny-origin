package com.example.roomdbAnalytics.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "analytics")
data class DefaultAnalytics(var event_type: Int?,
                            var event_name: String?,
                            var description: String?) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var device_id: String? = null
    var user_id: String? = null
}