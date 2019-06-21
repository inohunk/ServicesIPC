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
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import ru.hunkel.servicesipc.ILocationService
import ru.hunkel.servicesipc.IPasswordGenerator
import ru.hunkel.servicesipc.R
import ru.hunkel.servicesipc.services.LocationService
import ru.hunkel.servicesipc.services.PasswordGeneratorService
import utils.LOCATION_SERVICE_TRACKING_ON

const val REQUEST_CODE_PERMISSIONS = 0

class MainActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName

    var passwordService: IPasswordGenerator? = null
    var passwordServiceBounded = false
    private val passwordServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            passwordServiceBounded = false
            passwordService = null
            Log.i(TAG, "passwordService disconnected")

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            this@MainActivity.passwordService = IPasswordGenerator.Stub.asInterface(service)
            passwordServiceBounded = true
            Log.i(TAG, "passwordService connected")

        }
    }

    var locationService: ILocationService? = null
    var locationServiceBounded = false
    private val locationServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            locationServiceBounded = false
            locationService = null
            Log.i(TAG, "locationService disconnected")
            updateUI()
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            this@MainActivity.locationService = ILocationService.Stub.asInterface(service)
            Log.i(TAG, "locationService connected")
            updateUI()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        generate_button.setOnClickListener {
            onGenerateClick()
            updateUI()
        }

        start_stop_button.setOnClickListener {
            onStartStopClicked()
            updateUI()
        }
        start_stop_tracking_button.setOnClickListener {
            onStartStopTrackingClicked()
            updateUI()
        }
        updateUI()
    }

    private fun onGenerateClick() {

        val passwordLength = password_length_edit.text.toString()

        if (passwordLength.isEmpty()) {
            Toast.makeText(this, passwordService?.generatePassword(), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                this,
                passwordService?.generatePasswordWithFixedLenght(passwordLength.toInt()),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun onStartStopClicked() {
        val passwordServiceIntent = Intent(this, PasswordGeneratorService::class.java)

        passwordServiceBounded = if (passwordServiceBounded) {
            stopLocationService()
            unbindService(passwordServiceConnection)
            false
        } else {
            acceptPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))

            bindService(
                passwordServiceIntent,
                passwordServiceConnection,
                Context.BIND_AUTO_CREATE
            )
        }
        updateUI()
    }

    private fun onStartStopTrackingClicked() {
        val isTrackingNow = locationService?.trackingState == LOCATION_SERVICE_TRACKING_ON

        if (isTrackingNow) {
            locationService?.stopTracking()
        } else {
            locationService?.startTracking()
        }
    }

    private fun startLocationService() {
        val locationServiceIntent = Intent(this, LocationService::class.java)

        startService(Intent(this, LocationService::class.java))
        bindService(
            locationServiceIntent,
            locationServiceConnection,
            Context.BIND_AUTO_CREATE
        )
        locationServiceBounded = true
    }

    private fun stopLocationService() {
        if (locationServiceBounded) {
            unbindService(locationServiceConnection)
            locationServiceBounded = false
        }
        stopService(Intent(this, LocationService::class.java))
    }

    private fun acceptPermissions(permissions: Array<String>) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (
                    (checkSelfPermission((permission)) != PackageManager.PERMISSION_GRANTED)
                    and
                    (shouldShowRequestPermissionRationale(permission))
                ) {
                    Toast.makeText(this, "Location permission needed for tracking", Toast.LENGTH_SHORT).show()
                }
            }
            requestPermissions(permissions, REQUEST_CODE_PERMISSIONS)
        } else {
            startLocationService()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_PERMISSIONS -> {
                var index = 0

                for (permission in permissions) {
                    if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                        when (permission) {
                            Manifest.permission.ACCESS_FINE_LOCATION -> {
                                startLocationService()
                            }
                        }
                        index++
                    } else {
                        Toast.makeText(this, "The app need a permissions", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun updateUI() {
        start_stop_button.text = if (passwordServiceBounded) "STOP" else "START"
        generate_button.isEnabled = passwordServiceBounded

        if (locationServiceBounded) {
            start_stop_tracking_button.visibility = View.VISIBLE
            if ((locationService?.trackingState == LOCATION_SERVICE_TRACKING_ON)) {
                start_stop_tracking_button.text = "TRACKING: ON"
            } else {
                start_stop_tracking_button.text = "TRACKING: OFF"
            }
        } else {
            start_stop_tracking_button.visibility = View.INVISIBLE
        }
    }
}
