package ru.hunkel.servicesipc.services

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import android.util.Log
import androidx.core.app.ActivityCompat

class LocationService : Service(), LocationListener {

    /*
        VARIABLES
    */
    private val TAG = this.javaClass.simpleName

    private lateinit var mLocationManager: LocationManager
    private var mLooper: ServiceLooper? = null

    /*
        INNER CLASSES
    */

    private class ServiceHandler(looper: Looper) : Handler(looper) {

        private val TAG = javaClass.simpleName

        override fun handleMessage(msg: Message?) {
//            super.handleMessage(msg)

            Log.i(TAG, "handling")

            if (msg != null) {
                val location = msg.obj as Location
                Log.i(TAG, location.provider)
            }
        }
    }

    private inner class ServiceLooper : Thread("test-thread") {

        private lateinit var mHandler: Handler
        private var mLooper: Looper? = null

        init {
            start()
        }

        override fun run() {

            Looper.prepare()

            mHandler = ServiceHandler(Looper.myLooper()!!)
            mLooper = Looper.myLooper()

            Looper.loop()
        }

        fun getLooper(): Looper {

            return mLooper!!
        }
    }


    /*
        LIFECYCLE
    */

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d(TAG, "onStartCommand")
        mLooper = ServiceLooper()

        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        //TODO rewrite
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //TODO do something  if service don't have permissions
        }

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0F, this, mLooper?.getLooper())

        Log.d(TAG,"service started")
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService()

        Log.d(TAG, "onDestroy")

    }

    /*
        BINDING INTERFACE
     */
    override fun onBind(intent: Intent?): IBinder? {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return null
    }


    /*
        LOCATION SERVICE
     */
    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            Log.i(TAG, location.latitude.toString())
            Log.i(TAG, location.longitude.toString())
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        if (provider != null) {
            Log.i(TAG, "$provider status changed")
        }

    }

    override fun onProviderEnabled(provider: String?) {
        if (provider != null) {
            Log.i(TAG, "$provider enabled")
        }
    }

    override fun onProviderDisabled(provider: String?) {
        if (provider != null) {
            Log.i(TAG, "$provider disabled")
        }
    }

    private fun stopService() {
        mLocationManager.removeUpdates(this)
    }
}