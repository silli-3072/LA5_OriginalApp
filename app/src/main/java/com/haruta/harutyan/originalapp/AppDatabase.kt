package com.haruta.harutyan.originalapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Location::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    // AppDatabaseにDaoを追加
    abstract fun locationDao(): LocationDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, "database",
                    ).allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE
        }
    }
}