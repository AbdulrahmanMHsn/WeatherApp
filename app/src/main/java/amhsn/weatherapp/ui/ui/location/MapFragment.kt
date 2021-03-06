package amhsn.weatherapp.ui.ui.location

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import amhsn.weatherapp.R
import amhsn.weatherapp.databinding.FragmentMapBinding
import amhsn.weatherapp.pojo.Favourite
import amhsn.weatherapp.utils.Dialogs
import amhsn.weatherapp.utils.PrefHelper
import amhsn.weatherapp.viewmodel.WeatherViewModel
import android.app.Dialog
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*


class MapFragment : Fragment() {

    private lateinit var mProgress: Dialog
    private var title: String? = null
    private lateinit var binding: FragmentMapBinding
    private lateinit var googleMap: GoogleMap

    //    private var listFav: List<Favourite> = ArrayList()
    private lateinit var viewModel: WeatherViewModel
    private var latlng: LatLng = LatLng(0.0, 0.0)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)
        mProgress = Dialogs.createProgressBarDialog(context, "")
//        mProgress.show()
//        viewModel.getLocalDataSource().observe(viewLifecycleOwner, Observer {
//            listFav = it
//        })

        val mapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { it ->
            googleMap = it
            it.setOnMapClickListener {
                val marker = MarkerOptions()
                marker.position(it)
                marker.title("${it.latitude}, ${it.longitude}")
                latlng = it
                googleMap.clear()
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 10f))
                googleMap.addMarker(marker)
            }
        }


        if (arguments != null) {
            title = requireArguments().getString("settings").toString()
        }



        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.btnFind.setOnClickListener {
            mProgress.show()
            if (latlng.latitude == 0.0) {
                mProgress.show()
                Toast.makeText(context, "choose a location", Toast.LENGTH_SHORT).show()
                mProgress.dismiss()
            } else {
                mProgress.show()
                if (!title.equals(null)) {
                    mProgress.dismiss()
                    PrefHelper.setLatLng(latlng.latitude, latlng.longitude, requireContext())
                    Navigation.findNavController(it).popBackStack()
                } else {
                    val favourite = Favourite()
                    favourite.lat = latlng.latitude
                    favourite.lon = latlng.longitude
                    favourite.city = getCity(latlng.latitude, latlng.longitude).toString()
                    favourite.country = getCountry(latlng.latitude, latlng.longitude).toString()
                    viewModel.insertFavourite(favourite)
                    Toast.makeText(context, "Added location", Toast.LENGTH_SHORT).show()
                    mProgress.dismiss()
                    Navigation.findNavController(it).popBackStack()

//                    else {
//                        val favourite = Favourite()
//                        favourite.lat = latlng.latitude
//                        favourite.lon = latlng.longitude
//                        favourite.city = getCity(latlng.latitude, latlng.longitude).toString()
//                        favourite.country = getCountry(latlng.latitude, latlng.longitude).toString()
//                        if (listFav.contains(favourite)) {
//                            Toast.makeText(
//                                context,
//                                "The location was added before the previous",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            mProgress.dismiss()
//                        } else {
//                            viewModel.insertFavourite(favourite)
//                            Toast.makeText(context, "Added location", Toast.LENGTH_SHORT).show()
//                            Navigation.findNavController(it).popBackStack()
//                            mProgress.dismiss()
//                        }
//                        mProgress.dismiss()
//                    }
                }
            }
        }

    }

    /*
* A function use to get full address from LatLng
* */
    private fun getCity(lat: Double, lon: Double): String? {
        var result: String? = null
        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lon, 1)

            val city = addresses[0]!!.getAddressLine(0)

            result = city

            Log.w("getCompleteAddress", result)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("getCompleteAddress", "Canont get Address!")
        }
        return result
    }


    private fun getCountry(lat: Double, lon: Double): String? {
        var result: String? = null
        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lon, 1)

            val country = addresses[0]!!.countryName

            result = country

            Log.w("getCompleteAddress", result)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("getCompleteAddress", "Canont get Address!")
        }
        return result
    }


}