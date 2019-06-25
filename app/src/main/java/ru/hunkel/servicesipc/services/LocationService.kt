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
import ru.hunkel.servicesipc.ILocationService
import utils.LOCATION_SERVICE_TRACKING_OFF
import utils.LOCATION_SERVICE_TRACKING_ON
import kotlin.system.exitProcess

class LocationService : Service(), LocationListener {

    /*
        VARIABLES
    */
    private val TAG = this.javaClass.simpleName

    private var mLocationManager: LocationManager? = null
    private var mServiceLooper: ServiceLooper? = null
    private var mServiceHandler: ServiceHandlerWithLooper? = null

    private var mIsTracking = false
    private var mGpsInterval = 1L //milliseconds

    /*
        INNER CLASSES
    */

    private inner class LocationServiceImpl : ILocationService.Stub() {
        override fun startTracking() {
            startGpsTracking()
            printServiceInfo()
        }

        override fun stopTracking() {
            stopGpsTracking()
            printServiceInfo()
        }

        override fun getTrackingState(): Int {
            return if (mIsTracking) LOCATION_SERVICE_TRACKING_ON else LOCATION_SERVICE_TRACKING_OFF
        }

        override fun setTrackingSettings(interval: Long) {
            if (interval in 1..999){
                mGpsInterval = interval*1000
            }
        }
    }

    private class ServiceHandlerWithLooper(looper: Looper) : Handler(looper) {

        private val TAG = javaClass.simpleName

        override fun handleMessage(msg: Message?) {
            //TODO write message handling
        }
    }

    private inner class ServiceLooper : HandlerThread("test-thread") {

        private var mHandler: ServiceHandlerWithLooper? = null
        private lateinit var mLooper: Looper

        init {
            start()
        }

        override fun onLooperPrepared() {
            mLooper = looper
            mHandler = ServiceHandlerWithLooper(mLooper)

        }

        fun sendTask(location: Location) {
            mHandler!!.obtainMessage().apply {
                obj = location
            }.sendToTarget()
        }
    }

    /*
        LIFECYCLE
    */

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        Log.d(TAG, "service started")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d(TAG, "onStartCommand")
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        stopGpsTracking()
        Log.d(TAG, "onDestroy")
        exitProcess(1)

    }

    /*
        BINDING INTERFACE
     */
    override fun onBind(intent: Intent?): IBinder? {
        return LocationServiceImpl()
    }

    /*
        LOCATION SERVICE
     */
    override fun onLocationChanged(location: Location?) {
        Log.d("$TAG-THREAD-LOCATION", Thread.currentThread().name)
        if (location != null) {
            Log.i(TAG, "handling in thread: ${Thread.currentThread().name}")
            Log.i(
                TAG, "GPS INFO:\n" +
                        "\tprovider: ${location.provider}\n" +
                        "\tlatitude: ${location.latitude}\n" +
                        "\tlongitude: ${location.longitude}\n" +
                        "\taccuracy: ${location.accuracy}\n" +
                        "\tspeed: ${location.speed}\n"+
                        "\tinterval: ${mGpsInterval}\n"

            )
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

    /*
        FUNCTIONS
     */
    private fun startGpsTracking() {
        if (mIsTracking) return

        mServiceLooper = ServiceLooper()

        if (mServiceLooper?.looper == null) {
            Log.d(TAG, "test looper is null")
        } else {
            Log.d(TAG, "test looper active")
        }

        Log.d("$TAG-THREAD", Thread.currentThread().name)

        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                this@LocationService,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //TODO do something  if passwordService don't have permissions
        }

        mLocationManager?.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            mGpsInterval,
            0F,
            this@LocationService,
            mServiceLooper!!.looper
        )
        mIsTracking = true

    }

    private fun stopGpsTracking() {
        if (mIsTracking) {
            removeGpsUpdates()
            mServiceLooper?.looper?.thread?.interrupt()
            mServiceLooper?.interrupt()
            mLocationManager = null
            mServiceLooper = null
            mServiceHandler = null
            mIsTracking = false
        }
    }

    private fun removeGpsUpdates() {
        if (mIsTracking) {
            mLocationManager?.removeUpdates(this)
        }
    }

    private fun printServiceInfo() {
        Log.d(TAG, "tracking: $mIsTracking")

    }
}