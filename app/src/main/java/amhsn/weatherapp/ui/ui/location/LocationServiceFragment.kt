package amhsn.weatherapp.ui.ui.location

import amhsn.weatherapp.R
import amhsn.weatherapp.databinding.FragmentLocationServiceBinding
import amhsn.weatherapp.utils.LocationHelper
import amhsn.weatherapp.utils.PrefHelper
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation


class LocationServiceFragment : Fragment() {

    private lateinit var binding: FragmentLocationServiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocationHelper.init(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_location_service, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.btnGetLocation.setOnClickListener {

            LocationHelper.getLocation()
            Log.i("latitude", "onActivityCreated: "+PrefHelper.getLatitude(requireContext()))
//            if(PrefHelper.setLatLng(location.latitude,location.longitude,requireContext())){
                Navigation.findNavController(it).navigate(R.id.action_locationServiceFragment_to_home2)
//            }else{
//                Toast.makeText(context,"Not found location",Toast.LENGTH_SHORT).show()
//            }
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
//        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

}