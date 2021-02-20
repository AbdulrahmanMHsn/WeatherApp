package amhsn.weatherapp.ui.ui.location

import amhsn.weatherapp.R
import amhsn.weatherapp.databinding.FragmentLocationServiceBinding
import amhsn.weatherapp.ui.ui.local.ContextWrapper
import amhsn.weatherapp.utils.Dialogs
import amhsn.weatherapp.utils.LocationHelper
import amhsn.weatherapp.utils.NetworkConnection
import amhsn.weatherapp.utils.PrefHelper
import amhsn.weatherapp.viewmodel.LocationViewModel
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.google.android.gms.location.LocationListener


class LocationServiceFragment : Fragment() {

    private lateinit var binding: FragmentLocationServiceBinding
    private lateinit var mProgress: Dialog
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var locationManager: LocationManager
    private var isConnected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            ContextWrapper.setLocale(
                it,
                PrefHelper.getLocalLanguage(requireContext())
            )
        }
        LocationHelper.init(requireContext())
        LocationHelper.getLocation()
        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)

        if (!LocationHelper.checkGPS()) {
            LocationHelper.openGPS()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_location_service, container, false)

        mProgress = Dialogs.createProgressBarDialog(context, "")

        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // definition NetworkConnection
        val networkConnection = NetworkConnection(requireContext())

        networkConnection.observe(viewLifecycleOwner, Observer {
            isConnected = it
        })


        binding.btnGetLocation.setOnClickListener {
            val view = it
            if (isConnected) {
                if (LocationHelper.checkGPS()) {
                    mProgress.show()
                    locationViewModel.getLocationData()
                        .observe(viewLifecycleOwner, Observer {
                            mProgress.cancel()
                            PrefHelper.setLatLng(it.latitude, it.longitude, requireContext())
                            PrefHelper.setIsFirst(true,requireContext())
                            Navigation.findNavController(view)
                                .navigate(R.id.locationService_to_home)
//                                Naviagtion.findNavController().navigate(R.id.locationService_to_home)

                        })
                    PrefHelper.setEnableCurrentLocation(true,requireContext())
                } else {
                    LocationHelper.openGPS()
                }
            } else {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
//                    locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListenerGPS)
            }
        }
    }

    val locationListenerGPS = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val latitude = location.latitude
            val longitude = location.longitude
            val msg =
                "New Latitude: " + latitude + "New Longitude: " + longitude
//                            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show()
        }

        fun onStatusChanged(
            provider: String?,
            status: Int,
            extras: Bundle?
        ) {
        }

        fun onProviderEnabled(provider: String?) {}
        fun onProviderDisabled(provider: String?) {}
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        LocationHelper.onRequestPermissionsResultLocation(requestCode, permissions, grantResults)
    }

    override fun onPause() {
        super.onPause()
        LocationHelper.stopLocation()
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }


    private fun isLocationEnabled() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Enable Location")
            alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.")
            alertDialog.setPositiveButton(
                "Location Settings",
                DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                })
            alertDialog.setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
            val alert: AlertDialog = alertDialog.create()
            alert.show()
        } else {
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Confirm Location")
            alertDialog.setMessage("Your Location is enabled, please enjoy")
            alertDialog.setNegativeButton(
                "Back to interface",
                DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
            val alert: AlertDialog = alertDialog.create()
            alert.show()
        }
    }

}