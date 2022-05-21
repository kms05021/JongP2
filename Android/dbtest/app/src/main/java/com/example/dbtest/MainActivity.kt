package com.example.dbtest

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.room.Room
import java.time.LocalDate
import java.time.LocalTime

class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var db: LogDB
    lateinit var textLevel1 : TextView
    lateinit var textLevel2 : TextView
    lateinit var textLevel3 : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(this, LogDB::class.java, "LogDB").allowMainThreadQueries().build()

        textLevel1 = findViewById(R.id.textLevel1)
        textLevel2 = findViewById(R.id.textLevel2)
        textLevel3 = findViewById(R.id.textLevel3)
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v : View?){
        val onlyDate: LocalDate = LocalDate.now()
        val onlyTime: LocalTime = LocalTime.now()

        when(v?.id) {
            R.id.btnLevel1 -> {
                db.getDao().insertLog(LogEntity(onlyDate, onlyTime, 1))
            }
            R.id.btnLevel2 -> {
                db.getDao().insertLog(LogEntity(onlyDate, onlyTime, 2))
            }
            R.id.btnLevel3 -> {
                db.getDao().insertLog(LogEntity(onlyDate, onlyTime, 3))
            }
            R.id.btnUpdate -> {
                var list1: List<LogEntity> = db.getDao().getByLevelAndDateAndTime(1, onlyDate.minusDays(1), onlyDate)
                var list2: List<LogEntity> = db.getDao().getByLevelAndDateAndTime(2, onlyDate.minusDays(1), onlyDate)
                var list3: List<LogEntity> = db.getDao().getByLevelAndDateAndTime(3, onlyDate.minusDays(1), onlyDate)

                textLevel1.text = list1.size.toString()
                textLevel2.text = list2.size.toString()
                textLevel3.text = list3.size.toString()
            }
        }
    }
}