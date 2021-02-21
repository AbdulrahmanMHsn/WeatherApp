package amhsn.weatherapp.ui.ui.favourite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import amhsn.weatherapp.R
import amhsn.weatherapp.adapter.ViewPagerAdapter
import amhsn.weatherapp.databinding.FragmentFavouriteDetailsBinding
import amhsn.weatherapp.network.response.ResponseAPIWeather
import amhsn.weatherapp.ui.home.HourlyFragment
import amhsn.weatherapp.ui.home.NextDays
import amhsn.weatherapp.ui.ui.local.ContextWrapper
import amhsn.weatherapp.utils.PrefHelper
import amhsn.weatherapp.viewmodel.WeatherViewModel
import android.annotation.SuppressLint
import android.location.Geocoder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*


class FavouriteDetailsFragment : Fragment() {

    private lateinit var binding: FragmentFavouriteDetailsBinding
    private lateinit var viewModel: WeatherViewModel
    private var lat: Double = 0.0
    private var lon: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            ContextWrapper.setLocale(
                it,
                PrefHelper.getLocalLanguage(requireContext())
            )
        }
        // initialization view model
        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        if (!requireArguments().isEmpty) {
            lat = requireArguments().getDouble("lat")
            lon = requireArguments().getDouble("lon")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite_details, container, false)
        getRemoteDataSource()

        return binding.root
    }


    /*
     * A function use to setup viewPager
     * */
    private fun setAdapters(weather:ResponseAPIWeather) {
        val adapter = ViewPagerAdapter(requireActivity().supportFragmentManager)
        adapter.addFragment(HourlyFragment(weather.hourly), getString(R.string.today))
        adapter.addFragment(NextDays(weather.daily), getString(R.string.next_days))
        binding.bottomHome.apply {
            pager.adapter = adapter
            tabLayout.setupWithViewPager(pager)
        }
    }


    /*
    * A function use to observer on data from viewModel
    * */
    @SuppressLint("SetTextI18n")
    private fun getRemoteDataSource() {

        viewModel.getRemoteDataSource(
            lat,
            lon,
            requireContext()
        ).observe(requireActivity(), androidx.lifecycle.Observer {

            setAdapters(it)

            val pathImg = "http://openweathermap.org/img/wn/${it.current.weather!!.get(0).icon}.png"

            val date = SimpleDateFormat(
                "E dd MMM yyyy hh:mm a",
                Locale.ENGLISH
            ).format(Date((it.current.dt).toLong() * 1000))

            // set data in view
            binding.centerHome.txtVwCity.text = getCompleteAddress(
                lat,
                lon
            )

            binding.centerHome.txtVwDate.text = date

            binding.centerHome.txtVwDesc.text = it.current.weather!!.get(0).description

            binding.centerHome.txtVwValueHumidity.text =
                it.current.humidity.toString() + " %"

            binding.centerHome.txtVwValuePressure.text =
                it.current.pressure.toString() + " hPa"

            binding.centerHome.txtVwValueSpeed.text =
                it.current.humidity.toString() + " " + getString(R.string.m_s)

            binding.centerHome.txtVwTemp.text =
                Math.round(it.current.temp).toInt().toString() + "\u00b0"

            binding.centerHome.imgWeatherIcon.let {
                Glide.with(it)
                    .load(pathImg)
                    .into(it)
            }

        })
    }


    /*
    * A function use to get full address from LatLng
    * */
    private fun getCompleteAddress(lat: Double, lon: Double): String? {
        var result: String? = null
        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lon, 5)

            val city = addresses[1]!!.locality
            val state: String = addresses[0]!!.getAdminArea()
            val splitState = state.split(" ")
            val newState = splitState[0]

            result = city + ", " + newState
            PrefHelper.setAddress(result, requireContext())

            Log.w("getCompleteAddress", result)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("getCompleteAddress", "Canont get Address!")
        }
        return result
    }


    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

}