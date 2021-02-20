package amhsn.weatherapp.ui.home

import amhsn.weatherapp.R
import amhsn.weatherapp.adapter.ViewPagerAdapter
import amhsn.weatherapp.databinding.FragmentHomeBinding
import amhsn.weatherapp.network.response.ResponseAPIWeather
import amhsn.weatherapp.ui.ui.local.ContextWrapper
import amhsn.weatherapp.utils.Dialogs
import amhsn.weatherapp.utils.NetworkConnection
import amhsn.weatherapp.utils.PrefHelper
import amhsn.weatherapp.utils.worker.AlarmWorker
import amhsn.weatherapp.viewmodel.WeatherViewModel
import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import java.lang.Math.round
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class Home : Fragment() {

    private lateinit var mProgress: Dialog

    // declaration vars
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: WeatherViewModel
    private var list: List<ResponseAPIWeather> = ArrayList()


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
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        mProgress = Dialogs.createProgressBarDialog(context, "")

        getRemoteDataSource()
        setAdapters()

        return binding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (PrefHelper.getIsFirst(requireContext())!!.equals(true)) {
            showDialogAlart()
        }

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
                Toast.makeText(context, getString(R.string.internet), Toast.LENGTH_SHORT).show()
            }
            binding.centerHome.txtVwCity.text = PrefHelper.getAddress(requireContext())
        })


//        setupRefreshLayout()

    }

    /*
    * A function use to setup swipe refresh
    * */
    @SuppressLint("SetTextI18n")
    private fun setupRefreshLayout() {
//        binding.swiperefresh.setColorSchemeResources(R.color.blue_dark)
//        binding.swiperefresh.setOnRefreshListener {
//            getRemoteDataSource()
//
//            // lastUpdate
//            val currentCalendar = Calendar.getInstance()
//            val time = currentCalendar.timeInMillis
//            val date = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date((time)))
//            binding.centerHome.txtVwLastUpdate.visibility = View.VISIBLE
//            binding.centerHome.txtVwLastUpdate.text = "${getString(R.string.last_update)} $date"
//        }
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
            pager.isSaveEnabled = false
            tabLayout.setupWithViewPager(pager)
        }
    }


    /*
    * A function use to observer on data from viewModel
    * */
    @SuppressLint("SetTextI18n")
    private fun getRemoteDataSource() {
        mProgress.show()
        viewModel.getRemoteDataSource(
            PrefHelper.getLatitude(requireContext())!!.toDouble(),
            PrefHelper.getLongitude(requireContext())!!.toDouble(),
            requireContext()
        ).observe(requireActivity(),
            Observer {

                list = listOf(it)
                setAdapters()
                val pathImg =
                    "http://openweathermap.org/img/wn/${it.current.weather.get(0).icon}.png"

                val date = SimpleDateFormat(
                    "E dd MMM yyyy hh:mm a",
                    Locale.ENGLISH
                ).format(Date((it.current.dt).toLong() * 1000))

                // set data in view
                binding.centerHome.txtVwCity.text = getCompleteAddress(
                    PrefHelper.getLatitude(requireContext())!!.toDouble(),
                    PrefHelper.getLongitude(requireContext())!!.toDouble()
                )
                binding.centerHome.txtVwDate.text = date

                binding.centerHome.txtVwDesc.text = it.current.weather.get(0).description

                binding.centerHome.txtVwValueHumidity.text =
                    it.current.humidity.toString() + " %"

                binding.centerHome.txtVwValuePressure.text =
                    it.current.pressure.toString() + " hPa"

                binding.centerHome.txtVwValueSpeed.text =
                    it.current.humidity.toString() + " " + getString(R.string.m_s)

                binding.centerHome.txtVwTemp.text =
                    round(it.current.temp).toInt().toString() + "\u00b0"


                binding.centerHome.txtVwTempFeels.text =
                    "Feels like " + round(it.current.feels_like).toInt().toString() + "\u00b0"

                binding.centerHome.imgWeatherIcon.let {
                    Glide.with(it)
                        .load(pathImg)
                        .into(it)
                }

                mProgress.dismiss()

//                binding.swiperefresh.isRefreshing = false
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

    private fun showDialogAlart() {
        val dialog = Dialog(requireContext(), R.style.Theme_Dialog)
        dialog.setContentView(R.layout.dialog_show_alart)
        val btnYes = dialog.findViewById<Button>(R.id.dialog_btnYes)
        val btnNo = dialog.findViewById<Button>(R.id.dialog_btnNo)

        btnYes.setOnClickListener {
            PrefHelper.setEnableShowDialogAlert(true, requireContext())
            setPeriodicWorkRequest()
            dialog.dismiss()
        }

        btnNo.setOnClickListener {
            PrefHelper.setEnableShowDialogAlert(false, requireContext())
            dialog.dismiss()
        }

        PrefHelper.setIsFirst(false, requireContext())

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }


    private fun setPeriodicWorkRequest() {
        val periodicWorkRequest =
            PeriodicWorkRequest.Builder(AlarmWorker::class.java, 1, TimeUnit.HOURS)
                .addTag("AlarmWorkerMANAGER_PeriodicWorkRequest")
                .build()
        WorkManager.getInstance(requireContext()).enqueue(periodicWorkRequest)
    }
}