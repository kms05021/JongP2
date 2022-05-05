package com.lilly.bluetoothclassic.log

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [LogEntity::class], version = 1)
@TypeConverters(TimeConverter::class)
abstract class LogDB: RoomDatabase() {
    abstract fun getDao() : LogDao
}