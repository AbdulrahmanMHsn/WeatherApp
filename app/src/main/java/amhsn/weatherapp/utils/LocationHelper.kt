package amhsn.weatherapp.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.gms.location.*

object LocationHelper {

    // vars location

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
        locationRequest.interval = 5000
    }

    fun getLocation() {

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
            return
        }

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    for (location in locationResult.locations) {
                        if (location != null) {
                            isWorkedLocationCallback = true
                            PrefHelper.setLatLng(location.latitude,location.longitude, context.applicationContext)
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

//    fun getLocation(): Location {
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
//        } else {
//
//            locationCallback = object : LocationCallback() {
//                override fun onLocationResult(locationResult: LocationResult) {
//                    for (location in locationResult.locations) {
//                        if (location != null) {
//                            isWorkedLocationCallback = true
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
//        }
//
//        return mLocation
//    }

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


}