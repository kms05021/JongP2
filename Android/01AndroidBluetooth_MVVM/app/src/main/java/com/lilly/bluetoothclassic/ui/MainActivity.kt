package com.lilly.bluetoothclassic.ui

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.lilly.bluetoothclassic.R
import com.lilly.bluetoothclassic.databinding.ActivityMainBinding
import com.lilly.bluetoothclassic.log.LogActivity
import com.lilly.bluetoothclassic.util.*
import com.lilly.bluetoothclassic.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.lilly.bluetoothclassic.log.LogDB
import com.lilly.bluetoothclassic.log.LogEntity
import androidx.room.Room
import com.lilly.bluetoothclassic.setting.SettingActivity
import java.time.LocalDate
import java.time.LocalTime

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModel<MainViewModel>()
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                viewModel.onClickConnect()
            }
        }

    lateinit var db: LogDB
    var mBluetoothAdapter: BluetoothAdapter? = null
    var recv: String = ""
    var mediaplayer: MediaPlayer? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mContext: Context = this

        // view model binding
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel

        // Permission 요청
        if (!hasPermissions(this, PERMISSIONS)) {
            requestPermissions(PERMISSIONS, REQUEST_ALL_PERMISSION)
        }
        db = Room.databaseBuilder(this, LogDB::class.java, "LogDB").allowMainThreadQueries().build()
        initObserving()
    }

    private fun initObserving() {
        // Progress
        viewModel.inProgress.observe(this) {
            if (it.getContentIfNotHandled() == true) {
                viewModel.inProgressView.set(true)
            } else {
                viewModel.inProgressView.set(false)
            }
        }

        // Progress text
        viewModel.progressState.observe(this) {
            viewModel.txtProgress.set(it)
        }

        // Bluetooth On 요청
        viewModel.requestBleOn.observe(this) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startForResult.launch(enableBtIntent)

        }

        // Bluetooth Connect/Disconnect Event
        viewModel.connected.observe(this) {
            if (it != null) {
                val btnStart = findViewById<ImageButton>(R.id.btn_start)
                if (it) {
                    viewModel.setInProgress(false)
                    viewModel.btnConnected.set(true)
                    btnStart.isEnabled = true
                    Util.showNotification("디바이스와 연결되었습니다.")
                } else {
                    viewModel.setInProgress(false)
                    viewModel.btnConnected.set(false)
                    viewModel.onStart.set(false)
                    btnStart.isEnabled = false
                    Util.showNotification("디바이스와 연결이 해제되었습니다.")
                }
            }
        }

        // Bluetooth Connect Error
        viewModel.connectError.observe(this) {
            Util.showNotification("블루투스 연결에 실패했습니다.")
            viewModel.setInProgress(false)
        }

        // Data Receive
        viewModel.putTxt.observe(this) {
            if (it != null) {
                recv += it
                sv_read_data.fullScroll(View.FOCUS_DOWN)
                viewModel.txtRead.set(recv)

                Log.d("LOG", it)
                if (it.length >= 6) {
                    when (it.substring(0, 6)) {
                        "LEVEL1" -> {
                            db.getDao().insertLog(LogEntity(LocalDate.now(), LocalTime.now(), 1))
                            Log.d("DB", "LEVEL1 SUCCESS")
                            startVibration()
                        }
                        "LEVEL2" -> {
                            db.getDao().insertLog(LogEntity(LocalDate.now(), LocalTime.now(), 2))
                            Log.d("DB", "LEVEL2 SUCCESS")
                            startSound()
                        }
                        "LEVEL3" -> {
                            db.getDao().insertLog(LogEntity(LocalDate.now(), LocalTime.now(), 3))
                            Log.d("DB", "LEVEL3 SUCCESS")
                            call()
                        }
                    }
                }

            }
        }
    }

    private fun hasPermissions(context: Context?, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (context?.let { ActivityCompat.checkSelfPermission(it, permission) }
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun call() {
        val telNumber = Util.getTelNumber()

        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                val myUri = Uri.parse("tel:$telNumber")
                val myIntent = Intent(Intent.ACTION_CALL, myUri)
                startActivity(myIntent)
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Util.showNotification("전화 연결 권한이 거부되었습니다.")
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
            /*else -> {
                val vibratorManager: VibratorManager by lazy {
                    getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                }
                val timing = longArrayOf(100, 200, 100, 200, 100, 200)
                val amplitudes = intArrayOf(0, 200, 0, 200, 0, 200)
                val vibrationEffect = VibrationEffect.createWaveform(timing, amplitudes, 0)
                val combinedVibration = CombinedVibration.createParallel(vibrationEffect)
                vibratorManager.vibrate(combinedVibration)
            }*/
        }
        Thread.sleep(5000)
        stopVibration()
    }

    private fun stopVibration() {
//        when {
//            Build.VERSION.SDK_INT < Build.VERSION_CODES.S -> {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.cancel()
//            }
        /*else -> {
            val vibratorManager: VibratorManager by lazy {
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            }
            vibratorManager.cancel()
        }*/
//        }
    }

    private fun startSound() {
        mediaplayer = MediaPlayer.create(this, R.raw.ambulance_siren)
        mediaplayer?.isLooping = true
        mediaplayer?.start()
        Thread.sleep(5000)  // sound n초간 재생
        stopSound()
    }

    private fun stopSound() {
        mediaplayer?.stop()
        mediaplayer?.reset()
    }

    // Permission check
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ALL_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show()
                } else {
                    requestPermissions(permissions, REQUEST_ALL_PERMISSION)
                    Toast.makeText(this, "Permissions must be granted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.txtRead.set("here you can see the message come")
    }

    override fun onPause() {
        super.onPause()
        viewModel.unregisterReceiver()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        viewModel.setInProgress(false)
    }

    fun onClickMethod(v: View) {
        when (v.id) {
            R.id.logIcon -> {
                val nextIntent = Intent(this, LogActivity::class.java)
                startActivity(nextIntent)
            }

            R.id.settingIcon -> {
                val nextIntent = Intent(this, SettingActivity::class.java)
                startActivity(nextIntent)
            }
        }
    }

}