package com.example.aeroback.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Voo::class, Reserva::class],
    version = 1,
    exportSchema = false
)
abstract class AeroBackDatabase : RoomDatabase() {
    abstract fun vooDao(): VooDao
    abstract fun reservaDao(): ReservaDao

    companion object {
        @Volatile
        private var INSTANCE: AeroBackDatabase? = null

        fun getDatabase(context: Context): AeroBackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AeroBackDatabase::class.java,
                    "aeroback_room_v1.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
