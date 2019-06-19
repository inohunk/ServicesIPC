package ru.hunkel.servicesipc.services

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import java.util.*

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
        private val TAG = this.javaClass.simpleName
        init {
            start()
        }

        override fun run() {
            Looper.prepare()
            mHandler = ServiceHandler()
            Log.d(TAG, "looped")
            Looper.loop()
        }
    }

    private class NumGenerateTask : TimerTask() {
        private val TAG = this.javaClass.simpleName

        override fun run() {
            val randomNum = kotlin.random.Random.nextInt(0,100).toString()
            Log.i(TAG, randomNum)
        }
    }

    /*
        FUNCTIONS
    */
    override fun onCreate() {
        super.onCreate()
        val looper = ServiceThread()

        val task = NumGenerateTask()

        with(Timer()){
            schedule(task,1000L)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mHandler?.looper?.quitSafely()
        } else {
            mHandler?.looper?.quit()
        }
        if (!Thread.currentThread().isInterrupted) {
            Thread.currentThread().interrupt()
    }
        System.exit(1)
    }
}