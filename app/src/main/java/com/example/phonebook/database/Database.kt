package com.example.phonebook.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PhoneBookDbModel::class, TagDbModel::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun phoneBookDao(): PhoneBookDao
    abstract fun tagDao(): TagDao

    companion object {
        private const val DATABASE_NAME = "phone-book-database"
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            var instance = INSTANCE
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build()

                INSTANCE = instance
            }

            return instance
        }
    }
}

