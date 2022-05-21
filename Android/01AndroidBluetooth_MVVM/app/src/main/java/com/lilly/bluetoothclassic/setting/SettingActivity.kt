package com.lilly.bluetoothclassic.setting

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.lilly.bluetoothclassic.MyApplication
import com.lilly.bluetoothclassic.R
import com.lilly.bluetoothclassic.databinding.ActivityLogBinding
import com.lilly.bluetoothclassic.databinding.ActivitySettingBinding
import com.lilly.bluetoothclassic.ui.MainActivity
import com.lilly.bluetoothclassic.util.*

class SettingActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.telNumberEditText.setText(Util.getTelNumber())

        binding.submitButton.setOnClickListener {
            Util.setTelNumber(binding.telNumberEditText.text.toString())
            Util.showNotification("설정이 완료되었습니다.")
        }
    }
}