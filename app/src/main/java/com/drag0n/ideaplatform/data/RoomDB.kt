package com.drag0n.ideaplatform.data

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Item::class],
    version = 1,

)
abstract class RoomDB: RoomDatabase()
{

    abstract fun CourseDao(): CourseDao

    companion object {
        @Volatile
        private var INSTANCE: RoomDB? = null

        fun getDatabase(context: Context): RoomDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomDB::class.java,
                    "your_database_name"
                ).createFromAsset("data.db") // Указываем файл из assets
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}