package amhsn.weatherapp.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener


object LocationHelper {

    // vars location
    private const val TAG = "LocationHelper"
    private lateinit var mSettingsClient: SettingsClient
    private lateinit var mLocationSettingsRequest: LocationSettingsRequest
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private const val LOCATION_REQUEST_CODE = 1
    private var isWorkedLocationCallback = false
    private lateinit var context: Context
    private var mLocation: Location = Location("")

    fun init(context: Context) {
        this.context = context
        mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context.applicationContext)
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
    }

//    fun getLocation() {
//
//        if (ActivityCompat.checkSelfPermission(
//                context.applicationContext,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                context.applicationContext,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                context as Activity,
//                arrayOf(
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                ),
//                LOCATION_REQUEST_CODE
//            )
//            return
//        }
//
//            locationCallback = object : LocationCallback() {
//                override fun onLocationResult(locationResult: LocationResult) {
//                    for (location in locationResult.locations) {
//                        if (location != null) {
//                            isWorkedLocationCallback = true
//                            PrefHelper.setLatLng(location.latitude,location.longitude, context.applicationContext)
//                            mLocation = location
//                            Log.i(
//                                "TAGTAGTAGTAG",
//                                "onLocationResult: " + location.latitude + "   " + location.latitude
//                            )
//
//                        }
//                    }
//                }
//            }
//            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
//
//    }

    fun getLocation(): Location {

        if (ActivityCompat.checkSelfPermission(
                context.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_REQUEST_CODE
            )
        } else {

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    for (location in locationResult.locations) {
                        if (location != null) {
                            isWorkedLocationCallback = true
                            mLocation = location

                            Log.i(
                                "TAGTAGTAGTAG",
                                "onLocationResult: " + location.latitude + "   " + location.latitude
                            )

                        }
                    }
                }
            }
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }

        return mLocation
    }

    fun onRequestPermissionsResultLocation(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            getLocation()
        }
    }

    fun stopLocation() {
        if (isWorkedLocationCallback) {
            isWorkedLocationCallback = false
            mFusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }


    fun checkGPS(): Boolean {
        val locationManager =
            context.getSystemService(LOCATION_SERVICE) as LocationManager?

        if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true
        }

        return false
    }


    fun openGPS() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        mLocationSettingsRequest = builder.build()
        mSettingsClient = LocationServices.getSettingsClient(context)
        mSettingsClient
            .checkLocationSettings(mLocationSettingsRequest)
            .addOnSuccessListener(
                context as Activity,
                OnSuccessListener<LocationSettingsResponse?> {
                    getLocation()
                })
            .addOnFailureListener(context as Activity, OnFailureListener { e ->
                val statusCode = (e as ApiException).statusCode
                when (statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the
                            // result in onActivityResult().
                            val rae = e as ResolvableApiException
                            rae.startResolutionForResult(
                                context as Activity?,
                                100
                            )
                        } catch (sie: SendIntentException) {
                            Log.i(
                                TAG, "PendingIntent unable to execute request."
                            )
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        val errorMessage =
                            "Location settings are inadequate, and cannot be " +
                                    "fixed here. Fix in Settings."
                        Log.e(TAG, errorMessage)
                    }
                }
            })
    }


}

