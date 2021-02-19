package amhsn.weatherapp.ui.ui.location

import amhsn.weatherapp.R
import amhsn.weatherapp.databinding.FragmentLocationServiceBinding
import amhsn.weatherapp.ui.ui.local.ContextWrapper
import amhsn.weatherapp.utils.Dialogs
import amhsn.weatherapp.utils.LocationHelper
import amhsn.weatherapp.utils.PrefHelper
import amhsn.weatherapp.viewmodel.LocationViewModel
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController


class LocationServiceFragment : Fragment() {

    private lateinit var binding: FragmentLocationServiceBinding
    private lateinit var mProgress: Dialog
    private lateinit var locationViewModel: LocationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let { ContextWrapper.setLocale(it, PrefHelper.getLocalLanguage(requireContext())) }
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_location_service, container, false)

        mProgress = Dialogs.createProgressBarDialog(context, "")

        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.btnGetLocation.setOnClickListener {

            if (LocationHelper.checkGPS()) {
                mProgress.show()
                locationViewModel.getLocationData()
                    .observe(viewLifecycleOwner, Observer {
                        mProgress.cancel()
                        PrefHelper.setLatLng(it.latitude,it.longitude,requireContext())
                        findNavController().navigate(R.id.locationService_to_home)

                    })
            } else {
                LocationHelper.openGPS()
            }
        }
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

}