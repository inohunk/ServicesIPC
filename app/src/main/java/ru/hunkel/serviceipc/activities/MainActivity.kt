package ru.hunkel.serviceipc.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import ru.hunkel.servicesipc.IPasswordGenerator
import ru.hunkel.servicesipc.R
import ru.hunkel.servicesipc.services.PasswordGeneratorService

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
            unbindService(serviceConnection)
            false
        } else {
            bindService(
                intent,
                serviceConnection,
                Context.BIND_AUTO_CREATE
            )
        }
        updateUI()
    }

    private fun updateUI() {
        start_stop_button.text = if (isBound) "STOP" else "START"
        generate_button.isEnabled = isBound
    }
}
