package com.example.yogaadminmvvm.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.yogaadminmvvm.data.local.dao.YogaClassDao
import com.example.yogaadminmvvm.data.local.dao.YogaCourseDao
import com.example.yogaadminmvvm.data.local.entities.YogaClassEntity
import com.example.yogaadminmvvm.data.local.entities.YogaCourseEntity
import com.example.yogaadminmvvm.utils.Converters // Assuming your Converters class is here

@Database(entities = [YogaCourseEntity::class, YogaClassEntity::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class) // Added this to handle YogaType
abstract class AppDatabase : RoomDatabase() {

    abstract fun yogaCourseDao(): YogaCourseDao
    abstract fun yogaClassDao(): YogaClassDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "yoga_admin_database" // You can change the database name if you like
                )
                // Wipes and rebuilds instead of migrating if no Migration object.
                // Migration is not covered by this scope.
                .fallbackToDestructiveMigration() // Be cautious with this in production
                .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

