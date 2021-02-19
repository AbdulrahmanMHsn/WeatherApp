package amhsn.weatherapp.ui.ui.location

import amhsn.weatherapp.R
import amhsn.weatherapp.databinding.FragmentLocationBinding
import amhsn.weatherapp.utils.LocationHelper
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*


class LocationFragment : Fragment() {

    // vars location
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var binding: FragmentLocationBinding
    private var isWorkedLocationCallback = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
//        locationRequest = LocationRequest.create()
//        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        locationRequest.interval = 0
        LocationHelper.init(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_location, container, false)


        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.add.setOnClickListener {
            LocationHelper.getLocation()
        }

    }

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    private fun getLocation() {

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_REQUEST_CODE)
            return
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location != null) {
                        isWorkedLocationCallback = true
                    }
                }
            }
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        LocationHelper.onRequestPermissionsResultLocation(requestCode,permissions,grantResults)
//        if (requestCode == LOCATION_REQUEST_CODE) {
//            getLocation()
//        }
    }

    override fun onPause() {
        super.onPause()
//        if (isWorkedLocationCallback) {
//            isWorkedLocationCallback = false
//            mFusedLocationClient.removeLocationUpdates(locationCallback)
//        }

        LocationHelper.stopLocation()
    }


}