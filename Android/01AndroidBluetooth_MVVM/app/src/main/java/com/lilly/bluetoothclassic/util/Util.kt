package com.lilly.bluetoothclassic.util

import android.widget.Toast
import com.lilly.bluetoothclassic.MyApplication

class Util {
    companion object{
        private var telNumber = "01000000000"

        fun showNotification(msg: String) {
            Toast.makeText(MyApplication.applicationContext(), msg, Toast.LENGTH_SHORT).show()
        }

        fun setTelNumber(_telNumber: String) {
            telNumber = _telNumber
        }

        fun getTelNumber(): String {
            return telNumber
        }
    }
}