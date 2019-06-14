package ru.hunkel.servicesipc.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import ru.hunkel.servicesipc.services.impl.IPasswordGeneratorImpl

class PasswordGeneratorService : Service() {

    lateinit var service: IPasswordGeneratorImpl

    override fun onCreate() {
        super.onCreate()
        service = IPasswordGeneratorImpl()
    }

    override fun onBind(intent: Intent): IBinder {
        return service
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
