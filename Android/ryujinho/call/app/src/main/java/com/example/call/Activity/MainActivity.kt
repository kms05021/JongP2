package com.example.call.Activity

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.call.R
import com.example.call.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModel<MainViewModel>()
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                viewModel.onClickConnect()
            }
        }

    var mBluetoothAdapter: BluetoothAdapter? = null
    var recv: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // view model binding
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel

        // Permission 요청
        if (!hasPermissions(this, PERMISSIONS)) {
            requestPermissions(PERMISSIONS, REQUEST_ALL_PERMISSION)
        }

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
                if (it) {
                    viewModel.setInProgress(false)
                    viewModel.btnConnected.set(true)
                    Util.showNotification("디바이스와 연결되었습니다.")
                } else {
                    viewModel.setInProgress(false)
                    viewModel.btnConnected.set(false)
                    Util.showNotification("디바이스와 연결이 해제되었습니다.")
                }
            }
        }

        // Bluetooth Connect Error
        viewModel.connectError.observe(this) {
            Util.showNotification("Connect Error. Please check the device")
            viewModel.setInProgress(false)
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

}