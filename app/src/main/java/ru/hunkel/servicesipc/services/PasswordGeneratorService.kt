package ru.hunkel.servicesipc.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import ru.hunkel.servicesipc.services.impl.IPasswordGeneratorImpl

class PasswordGeneratorService : Service() {

    private val TAG = this::class.java.simpleName
    lateinit var service: IPasswordGeneratorImpl

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onCreate")
        service = IPasswordGeneratorImpl()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind")
        return service
    }

    override fun onRebind(intent: Intent?) {
        Log.d(TAG, "onReBind")
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }
}
