package com.example.roomdbAnalytics.dao

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration
import android.content.Context
import com.example.roomdbAnalytics.model.DefaultAnalytics
import kotlin.jvm.java
import android.arch.persistence.db.SupportSQLiteDatabase


@Database(entities = [(DefaultAnalytics::class)], version = 1)
abstract class RoomDB : RoomDatabase() {

    abstract fun myDao(): MyDao

    companion object {

        @Volatile
        private var roomDB: RoomDB? = null

        fun newInstance(context: Context): RoomDB? {
            if (roomDB == null) {
                synchronized(RoomDB::class) {
                    roomDB = Room.databaseBuilder(context, RoomDB::class.java, "analytics.db")
                            .fallbackToDestructiveMigration()
//                            .addMigrations(MIGRATION_1_2)
                            .build()
                }
            }
            return roomDB
        }

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE analytics "
                        + " ADD COLUMN last_update INTEGER")
            }
        }

        fun destroyInstance() {
            roomDB = null
        }
    }
}