package amhsn.weatherapp.ui.home

import amhsn.weatherapp.R
import amhsn.weatherapp.adapter.ViewPagerAdapter
import amhsn.weatherapp.databinding.FragmentHomeBinding
import amhsn.weatherapp.utils.PrefHelper
import amhsn.weatherapp.viewmodel.WeatherViewModel
import android.annotation.SuppressLint
import android.app.Dialog
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import java.lang.Math.round
import java.text.SimpleDateFormat
import java.util.*


class Home : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: WeatherViewModel
    private lateinit var address: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        address = getCompleteAddress(PrefHelper.getLatitude(requireContext())!!.toDouble(), PrefHelper.getLongitude(requireContext())!!.toDouble())

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
//        showDialog()
        getRemoteDataSource()
        return binding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.i("LocationTAG", "onActivityCreated: sadsa : "+PrefHelper.getLatitude(requireContext()) + "  "+PrefHelper.getLongitude(requireContext()))
//        binding.centerHome.txtVwCity.text = getCompleteAddress(PrefHelper.getLatitude(requireContext())!!.toDouble(), PrefHelper.getLongitude(requireContext())!!.toDouble())

        binding.swiperefresh.setColorSchemeResources(R.color.blue_dark);
        binding.swiperefresh.setOnRefreshListener {
            getRemoteDataSource()

            // lastUpdate
            val currentCalendar = Calendar.getInstance()
            val time = currentCalendar.timeInMillis
            val date = SimpleDateFormat(
                "hh:mm a",
                Locale.ENGLISH
            ).format(Date((time)))

            binding.centerHome.txtVwLastUpdate.visibility = View.VISIBLE
            binding.centerHome.txtVwLastUpdate.text = "Last Update: $date"
        }


        setAdapters()
    }

    private fun setAdapters() {
        val adapter = ViewPagerAdapter(requireActivity().supportFragmentManager)
        adapter.addFragment(HourlyFragment(), "Today")
        adapter.addFragment(NextDays(), "Next 7 days")
        binding.bottomHome.apply {
            pager.adapter = adapter
            tabLayout.setupWithViewPager(pager)
        }
    }


    @SuppressLint("SetTextI18n")
    fun getRemoteDataSource() {
        Toast.makeText(requireContext(),"sadsa "+PrefHelper.getLatitude(requireContext())!!.toDouble(),Toast.LENGTH_SHORT).show()
        viewModel.getRemoteDataSource(PrefHelper.getLatitude(requireContext())!!.toDouble(), PrefHelper.getLongitude(requireContext())!!.toDouble(), requireContext())
            .observe(requireActivity(), Observer {
//                binding.centerHome.viewModelCurrent = it
//
//Toast.makeText(requireContext(),"sadsa",Toast.LENGTH_SHORT).show()
                val pathImg = "http://openweathermap.org/img/wn/${it.current.weather.get(0).icon}.png"
//


                binding.centerHome.txtVwDesc.text = it.current.weather.get(0).description
                binding.centerHome.txtVwValueHumidity.text = it.current.humidity.toString() + " %"
                binding.centerHome.txtVwValuePressure.text = it.current.pressure.toString() + " hPa"
                binding.centerHome.txtVwValueSpeed.text = it.current.humidity.toString() + " m/s"

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


    fun getCompleteAddress(lat: Double, lon: Double): String {
        Toast.makeText(context, "a", Toast.LENGTH_SHORT).show()
        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(context, Locale.getDefault())

        addresses = geocoder.getFromLocation(
            lat,
            lon,
            1
        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


//        val address: String = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        val city: String = addresses[0].getLocality()
        val state: String = addresses[0].getAdminArea()
//        val country: String = addresses[0].getCountryName()

        val splitState = state.split(" ")
        val newState = splitState[0]

        val result = "${city}, ${newState}"

        return result
    }

    fun showDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_location_service)
//        val text = dialog.findViewById(R.id.text_dialog) as TextView
//        text.text = msg
//        val dialogButton: Button = dialog.findViewById(R.id.btn_dialog) as Button
//        dialogButton.setOnClickListener(View.OnClickListener { dialog.dismiss() })
        dialog.show()
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity?)!!.getSupportActionBar()!!.title = address

    }


}