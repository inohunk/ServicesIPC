package ru.hunkel.serviceipc.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import ru.hunkel.servicesipc.IPasswordGenerator
import ru.hunkel.servicesipc.R
import ru.hunkel.servicesipc.services.PasswordGeneratorService

const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    lateinit var service: IPasswordGenerator

    private val serviceConnection = object : ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {

            Log.i(TAG,"service disconnected")

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            this@MainActivity.service = IPasswordGenerator.Stub.asInterface(service)
            Log.i(TAG,"service connected")

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        generate_button.setOnClickListener {
            val length= password_length_edit.text.toString().toInt()

            Toast.makeText(this,service.generatePasswordWithFixedLenght(length),Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()

        if (!super.bindService(Intent(this,PasswordGeneratorService::class.java),serviceConnection, Context.BIND_AUTO_CREATE)){
            Log.i(TAG,"Failed to bind service")
        }
    }
}
