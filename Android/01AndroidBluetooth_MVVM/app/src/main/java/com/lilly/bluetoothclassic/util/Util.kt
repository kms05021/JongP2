package com.lilly.bluetoothclassic.util

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.lilly.bluetoothclassic.MyApplication

class Util {
    companion object : AppCompatActivity() {
        private var telNumber : String = "01000000000"
        private val telNumberPreference = MyApplication.applicationContext().getSharedPreferences("telNumber", MODE_PRIVATE)

        fun showNotification(msg: String) {
            Toast.makeText(MyApplication.applicationContext(), msg, Toast.LENGTH_SHORT).show()
        }

        fun setTelNumber(_telNumber: String) {
            telNumberPreference.edit {
                putString("telNumber", _telNumber)
            }
            telNumber = _telNumber
        }

        fun getTelNumber(): String {
            telNumber = telNumberPreference.getString("telNumber", "01000000000").toString()
            return telNumber
        }
    }
}