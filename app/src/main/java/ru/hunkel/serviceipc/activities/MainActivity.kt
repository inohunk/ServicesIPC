package ru.hunkel.serviceipc.activities

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import ru.hunkel.servicesipc.IPasswordGenerator
import ru.hunkel.servicesipc.R
import ru.hunkel.servicesipc.services.LocationService
import ru.hunkel.servicesipc.services.PasswordGeneratorService

const val REQUEST_CODE_PERMISSIONS = 0

class MainActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName
    var service: IPasswordGenerator? = null
    var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            service = null
            Log.i(TAG, "service disconnected")

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            this@MainActivity.service = IPasswordGenerator.Stub.asInterface(service)
            isBound = true
            Log.i(TAG, "service connected")

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        generate_button.setOnClickListener {
            onGenerateClick()
        }

        start_stop_button.setOnClickListener {
            onStartStopClicked()
        }
        updateUI()
    }

    private fun onGenerateClick() {

        val passwordLength = password_length_edit.text.toString()

        if (passwordLength.isEmpty()) {
            Toast.makeText(this, service?.generatePassword(), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                this,
                service?.generatePasswordWithFixedLenght(passwordLength.toInt()),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun onStartStopClicked() {
        val intent = Intent(this, PasswordGeneratorService::class.java)

        isBound = if (isBound) {
            stopLocationService()
            unbindService(serviceConnection)
            false
        } else {
            acceptPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
            bindService(
                intent,
                serviceConnection,
                Context.BIND_AUTO_CREATE
            )
        }
        updateUI()
    }

    private fun startLocationService() {
        startService(Intent(this, LocationService::class.java))

    }

    private fun stopLocationService() {
        stopService(Intent(this, LocationService::class.java))

    }

    private fun acceptPermissions(permissions: Array<String>) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    startLocationService()
                } else {
                    //TODO make check for all permission requests
                    if (shouldShowRequestPermissionRationale(permission)) {
                        Toast.makeText(this, "Location permission needed for tracking", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            requestPermissions(permissions, REQUEST_CODE_PERMISSIONS)
        } else {
            //TODO do something cos the permission is already accepted on API<23
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_PERMISSIONS -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationService()
                } else {
                    Toast.makeText(this, "Location permission needed for tracking", Toast.LENGTH_SHORT).show()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        }

    }

    private fun updateUI() {
        start_stop_button.text = if (isBound) "STOP" else "START"
        generate_button.isEnabled = isBound
    }
}
