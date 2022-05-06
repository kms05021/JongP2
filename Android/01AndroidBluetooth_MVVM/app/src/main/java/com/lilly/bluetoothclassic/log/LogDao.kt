package com.lilly.bluetoothclassic.log

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.time.LocalDate
import java.time.LocalTime

@Dao
interface LogDao {
    @Insert
    fun insertLog(logEntity: LogEntity)

    @Query("select * from LogEntity where date = :date")
    fun getByDate(date: LocalDate): List<LogEntity>

    @Query("select * from LogEntity where level = :level and date = :startDate and time >= '18:00:00' union select * from LogEntity where level = :level and date = :endDate and time <= '18:00:00'")
    fun getByLevelAndDateAndTime(level: Int, startDate: LocalDate, endDate: LocalDate) : List<LogEntity>

    @Query("select * from LogEntity where level = :level and date >= :startMonth and date <= :endMonth")
    fun getByLevelAndMonth(level: Int, startMonth: LocalDate, endMonth : LocalDate) : List<LogEntity>
}