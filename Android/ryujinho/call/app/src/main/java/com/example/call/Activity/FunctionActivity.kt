package com.example.call.Activity

import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.call.Actions
import com.example.call.AppService
import com.example.call.R
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission

class FunctionActivity: AppCompatActivity() {
    val mContext = this

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val callButton = findViewById<Button>(R.id.callButton)
        val vibeButton = findViewById<Button>(R.id.vibeButton)
        val vibeCancelButton = findViewById<Button>(R.id.vibeCancelButton)
        val alarmButton = findViewById<Button>(R.id.alarmButton)
        val alarmStopButton = findViewById<Button>(R.id.alarmStopButton)
        val startForeButton = findViewById<Button>(R.id.startForeButton)
        val stopForeButton = findViewById<Button>(R.id.stopForeButton)

        startForeButton.setOnClickListener {
            val intent = Intent(this@FunctionActivity, AppService::class.java)
            intent.action = Actions.START_FOREGROUND
            startService(intent)
        }

        stopForeButton.setOnClickListener {
            val intent = Intent(this@FunctionActivity, AppService::class.java)
            intent.action = Actions.STOP_FOREGROUND
            startService(intent)
        }

        callButton.setOnClickListener { call() }

        vibeButton.setOnClickListener {
            startVibration()
            Thread.sleep(5000)
            stopVibration()
        }

        vibeCancelButton.setOnClickListener { stopVibration() }

        alarmButton.setOnClickListener { alarm() }
    }

    private fun call() {
        val telNumber = "01087602581"

        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                val myUri = Uri.parse("tel:$telNumber")
                val myIntent = Intent(Intent.ACTION_CALL, myUri)
                startActivity(myIntent)
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(mContext, "전화 연결 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        TedPermission.create()
            .setPermissionListener(permissionListener)
            .setDeniedMessage("권한을 열어!")
            .setPermissions(android.Manifest.permission.CALL_PHONE)
            .check()
    }

    private fun startVibration() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.O -> {
                val pattern = longArrayOf(100, 200, 100, 200, 100, 200)
                vibrator.vibrate(pattern, 0)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                val timing = longArrayOf(100, 200, 100, 200, 100, 200)
                val amplitudes = intArrayOf(0, 200, 0, 200, 0, 200)
                val effect = VibrationEffect.createWaveform(timing, amplitudes, 0)
                vibrator.vibrate(effect)
            }
            else -> {
                val vibratorManager: VibratorManager by lazy {
                    getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                }
                val timing = longArrayOf(100, 200, 100, 200, 100, 200)
                val amplitudes = intArrayOf(0, 200, 0, 200, 0, 200)
                val vibrationEffect = VibrationEffect.createWaveform(timing, amplitudes, 0)
                val combinedVibration = CombinedVibration.createParallel(vibrationEffect)
                vibratorManager.vibrate(combinedVibration)
            }
        }
    }

    private fun stopVibration() {
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.S -> {
                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.cancel()
            }
            else -> {
                val vibratorManager: VibratorManager by lazy {
                    getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                }
                vibratorManager.cancel()
            }
        }
    }

    private fun alarm() {
        val uriRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val ringtone = RingtoneManager.getRingtone(this, uriRingtone)
        ringtone.play()
    }
}