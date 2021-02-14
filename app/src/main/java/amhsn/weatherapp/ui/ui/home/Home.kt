package amhsn.weatherapp.ui.home

import amhsn.weatherapp.R
import amhsn.weatherapp.adapter.ViewPagerAdapter
import amhsn.weatherapp.databinding.FragmentHomeBinding
import amhsn.weatherapp.utils.NetworkConnection
import amhsn.weatherapp.utils.PrefHelper
import amhsn.weatherapp.viewmodel.WeatherViewModel
import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import java.lang.Math.round
import java.text.SimpleDateFormat
import java.util.*


class Home : Fragment() {

    // declaration vars
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: WeatherViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // initialization view model
        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        setAdapters()
        getRemoteDataSource()

        return binding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // definition NetworkConnection
        val networkConnection = NetworkConnection(requireContext())
        networkConnection.observe(viewLifecycleOwner, Observer {
            if (it) {
                getCompleteAddress(
                    PrefHelper.getLatitude(requireContext())!!.toDouble(),
                    PrefHelper.getLongitude(requireContext())!!.toDouble()
                )
            } else {
                PrefHelper.getAddress(requireContext())
                Toast.makeText(context, "Not Connected", Toast.LENGTH_SHORT).show()
            }
            binding.centerHome.txtVwCity.text = PrefHelper.getAddress(requireContext())
        })


        setupRefreshLayout()

    }

    /*
    * A function use to setup swipe refresh
    * */
    @SuppressLint("SetTextI18n")
    private fun setupRefreshLayout(){
        binding.swiperefresh.setColorSchemeResources(R.color.blue_dark)
        binding.swiperefresh.setOnRefreshListener {
            getRemoteDataSource()

            // lastUpdate
            val currentCalendar = Calendar.getInstance()
            val time = currentCalendar.timeInMillis
            val date = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date((time)))
            binding.centerHome.txtVwLastUpdate.visibility = View.VISIBLE
            binding.centerHome.txtVwLastUpdate.text = "${getString(R.string.last_update)} $date"
        }
    }


    /*
    * A function use to setup viewPager
    * */
    private fun setAdapters() {
        val adapter = ViewPagerAdapter(requireActivity().supportFragmentManager)
        adapter.addFragment(HourlyFragment(), getString(R.string.today))
        adapter.addFragment(NextDays(), getString(R.string.next_days))
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
            PrefHelper.getLatitude(requireContext())!!.toDouble(),
            PrefHelper.getLongitude(requireContext())!!.toDouble(),
            requireContext()
        ).observe(requireActivity(),
            Observer {

                val pathImg =
                    "http://openweathermap.org/img/wn/${it.current.weather.get(0).icon}.png"

                val date = SimpleDateFormat(
                    "E dd MMM yyyy hh:mm a",
                    Locale.ENGLISH
                ).format(Date((it.current.dt).toLong() * 1000))

                // set data in view
                binding.centerHome.txtVwDate.text = date

                binding.centerHome.txtVwDesc.text = it.current.weather.get(0).description

                binding.centerHome.txtVwValueHumidity.text = it.current.humidity.toString() + " %"

                binding.centerHome.txtVwValuePressure.text = it.current.pressure.toString() + " hPa"

                binding.centerHome.txtVwValueSpeed.text =
                    it.current.humidity.toString() + " " + getString(R.string.m_s)

                binding.centerHome.txtVwTemp.text =
                    round(it.current.temp).toInt().toString() + "\u00b0"

                binding.centerHome.imgWeatherIcon.let {
                    Glide.with(it)
                        .load(pathImg)
                        .into(it)
                }

                binding.swiperefresh.isRefreshing = false
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
            val city1 = addresses[2]!!.locality
            val state: String = addresses[0]!!.getAdminArea()
            val splitState = state.split(" ")
            val newState = splitState[0]

            result = "${city} ${city1} ${newState}"
            PrefHelper.setAddress(result,requireContext())

            Log.w("getCompleteAddress", result)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("getCompleteAddress", "Canont get Address!")
        }
        return result
    }
}