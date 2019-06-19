package ru.hunkel.servicesipc.services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.util.Log

class TestService : Service() {

    private var mHandler: ServiceHandler? = null


    /*
        INNER CLASSES
    */
    companion object {
        private class ServiceHandler : Handler() {
            override fun handleMessage(msg: Message?) {
                //TODO make message handling
            }
        }
    }

    private inner class ServiceThread : Thread() {

        init {
            start()
        }

        override fun run() {
            Looper.prepare()
            mHandler = ServiceHandler()
            Looper.loop()
            Log.d("T","looped")
        }


    }

    override fun onCreate() {
        super.onCreate()

        val looper = ServiceThread()

    }
    override fun onBind(intent: Intent?): IBinder? {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return null
    }
}