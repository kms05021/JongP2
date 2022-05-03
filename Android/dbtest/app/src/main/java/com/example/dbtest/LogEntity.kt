package com.example.dbtest

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.time.LocalDate
import java.time.LocalTime

@Entity(primaryKeys = ["date", "time"])
data class LogEntity(
    val date: LocalDate,
    val time: LocalTime,
    @ColumnInfo val level: Int
)
