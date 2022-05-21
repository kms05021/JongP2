package com.lilly.bluetoothclassic.log

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class TimeConverter {
    @TypeConverter
    fun dateToString(date: LocalDate) : String {
        return date.toString()
    }

    @TypeConverter
    fun timeToString(time: LocalTime) : String {
        return time.toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun stringToDate(value: String) : LocalDate {
        return LocalDate.parse(value, DateTimeFormatter.ISO_DATE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun stringToTime(value: String) : LocalTime {
        return LocalTime.parse(value, DateTimeFormatter.ISO_TIME)
    }
}