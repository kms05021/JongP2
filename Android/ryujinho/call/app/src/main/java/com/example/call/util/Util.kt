package com.lilly.bluetoothclassic.util

import android.widget.Toast
import com.example.call.MyApplication

class Util {
    companion object{
        fun showNotification(msg: String) {
            Toast.makeText(MyApplication.applicationContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }
}