package ru.hunkel.servicesipc.services

import android.Manifest
import android.annotation.SuppressLint
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
    private var mLastLocation: Location? = null

    /*
        INNER CLASSES
    */
    /**
     * Location service implementation
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
            if (interval in 1..999) {
                mGpsInterval = interval * 1000
            }
        }

        override fun getTrack(): Location? {
            return mLastLocation
        }

        @SuppressLint("MissingPermission")
        override fun punch(controlPoint: Int):Long{
            mLocationManager!!.requestSingleUpdate(LocationManager.GPS_PROVIDER,this@LocationService,mServiceLooper?.looper)
            return mLastLocation!!.time
        }
    }

    private class ServiceHandlerWithLooper(looper: Looper) : Handler(looper) {

        private val TAG = javaClass.simpleName

        override fun handleMessage(msg: Message?) {
            //TODO write message handling
        }
    }

    /**
     * The class creates thread and associates looper and handler with new thread
     */
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
        Log.d(TAG, "onDestroy")
        stopGpsTracking()
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
                        "\tspeed: ${location.speed}\n" +
                        "\tinterval: $mGpsInterval\n"
            )
            mLastLocation = location
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
            Log.d(TAG, "looper is null")
        } else {
            Log.d(TAG, "looper is active")
        }

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
        Log.d(TAG, "tracking started in ${Thread.currentThread().name}")
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