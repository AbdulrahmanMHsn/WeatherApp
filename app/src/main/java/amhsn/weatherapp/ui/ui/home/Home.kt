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
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.solver.state.State
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import java.lang.Math.round
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class Home : Fragment() {

    private var OVER_RELAY_PERMISSION_CODE: Int = 10001
    private lateinit var mProgress: Dialog

    // declaration vars
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: WeatherViewModel


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

//    override fun onAttach(context: Context) {
//        val context: Context =
//            ContextWrapper.changeLang(context, Locale(PrefHelper.getLocalLanguage(context)))
//        super.onAttach(context)
//    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        mProgress = Dialogs.createProgressBarDialog(context, "")
        // definition NetworkConnection
        val networkConnection = NetworkConnection(requireContext())
        networkConnection.observe(viewLifecycleOwner, Observer {
            if (it) {
                getRemoteDataSource()
                getCompleteAddress(
                    PrefHelper.getLatitude(requireContext())!!.toDouble(),
                    PrefHelper.getLongitude(requireContext())!!.toDouble()
                )
                binding.centerHome.txtVwLastUpdate.visibility = View.GONE
            } else {
                PrefHelper.getAddress(requireContext())
                Toast.makeText(context, getString(R.string.internet), Toast.LENGTH_SHORT).show()
                getLocalDataSource()
                // lastUpdate
                val time = PrefHelper.getLastUpdate(requireContext())
                val date =
                    SimpleDateFormat("E dd MMM yyyy hh:mm a", Locale.ENGLISH).format(Date((time!!)))
                binding.centerHome.txtVwLastUpdate.visibility = View.VISIBLE
                binding.centerHome.txtVwLastUpdate.text = "${getString(R.string.last_update)} $date"
            }
            binding.centerHome.txtVwCity.text = PrefHelper.getAddress(requireContext())
        })

        return binding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        onBackPressed()
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
    private fun setAdapters(weather: ResponseAPIWeather) {
        val adapter = ViewPagerAdapter(requireActivity().supportFragmentManager)
        adapter.addFragment(HourlyFragment(weather.hourly), getString(R.string.today))
        adapter.addFragment(NextDays(weather.daily), getString(R.string.next_days))
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
                setAdapters(it)
                val pathImg =
                    "http://openweathermap.org/img/wn/${it.current.weather!!.get(0).icon}.png"

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

                binding.centerHome.txtVwDesc.text = it.current.weather!!.get(0).description

                binding.centerHome.txtVwValueHumidity.text =
                    it.current.humidity.toString() + " %"

                binding.centerHome.txtVwValuePressure.text =
                    it.current.pressure.toString() + " " + getString(R.string.hpa)

                binding.centerHome.txtVwTemp.text =
                    round(it.current.temp).toInt().toString() + "\u00b0"

                if (PrefHelper.getUnitWind(requireContext()).equals("m/s")) {
                    binding.centerHome.txtVwValueSpeed.text =
                        it.current.humidity.toString() + " " + getString(R.string.m_s)
                } else {
                    binding.centerHome.txtVwValueSpeed.text =
                        it.current.humidity.toString() + " " + getString(R.string.km_h)
                }


//                if (PrefHelper.getUnitTemp(requireContext()).equals("default")) {
//                    binding.centerHome.txtVwTemp.text
//                    round(it.current.temp).toInt().toString() + "\u00b0 K"
//                } else if (PrefHelper.getUnitTemp(requireContext()).equals("metric")) {
//                    binding.centerHome.txtVwTemp.text
//                    round(it.current.temp).toInt().toString() + "\u00b0 C"
//                } else if (PrefHelper.getUnitTemp(requireContext()).equals("imperial")) {
//                    binding.centerHome.txtVwTemp.text
//                    round(it.current.temp).toInt().toString() + "\u00b0 F"
//                }


                binding.centerHome.txtVwTempFeels.text =
                    getString(R.string.likes) + " " + round(it.current.feels_like).toInt()
                        .toString() + "\u00b0"

                binding.centerHome.imgWeatherIcon.let {
                    Glide.with(it)
                        .load(pathImg)
                        .into(it)
                }

                mProgress.dismiss()

                if (PrefHelper.getIsFirst(requireContext())!!.equals(true)) {
                    showDialogAlart()
                }

//                binding.swiperefresh.isRefreshing = false
            })
    }


    private fun getLocalDataSource() {
        mProgress.show()
        viewModel.getWeatherLocalDataSource().observe(requireActivity(),
            Observer {

                Log.i("ONONONON", "getLocalDataSource: " + it)


                setAdapters(it)
                val pathImg =
                    "http://openweathermap.org/img/wn/${it.current.weather!!.get(0).icon}.png"
//
                val date = SimpleDateFormat(
                    "E dd MMM yyyy hh:mm a",
                    Locale.ENGLISH
                ).format(Date((it.current.dt).toLong() * 1000))
//
//                // set data in view
                binding.centerHome.txtVwCity.text = PrefHelper.getAddress(requireContext())
                binding.centerHome.txtVwDate.text = date
//
                binding.centerHome.txtVwDesc.text = it.current.weather!!.get(0).description

                binding.centerHome.txtVwValueHumidity.text =
                    it.current.humidity.toString() + " %"

                binding.centerHome.txtVwValuePressure.text =
                    it.current.pressure.toString() + " " + getString(R.string.hpa)

                binding.centerHome.txtVwTemp.text =
                    round(it.current.temp).toInt().toString() + "\u00b0"

                if (PrefHelper.getUnitWind(requireContext()).equals("m/s")) {
                    binding.centerHome.txtVwValueSpeed.text =
                        it.current.humidity.toString() + " " + getString(R.string.m_s)
                } else {
                    binding.centerHome.txtVwValueSpeed.text =
                        it.current.humidity.toString() + " " + getString(R.string.km_h)
                }


//                if (PrefHelper.getUnitTemp(requireContext()).equals("default")) {
//                    binding.centerHome.txtVwTemp.text
//                    round(it.current.temp).toInt().toString() + "\u00b0 K"
//                } else if (PrefHelper.getUnitTemp(requireContext()).equals("metric")) {
//                    binding.centerHome.txtVwTemp.text
//                    round(it.current.temp).toInt().toString() + "\u00b0 C"
//                } else if (PrefHelper.getUnitTemp(requireContext()).equals("imperial")) {
//                    binding.centerHome.txtVwTemp.text
//                    round(it.current.temp).toInt().toString() + "\u00b0 F"
//                }


                binding.centerHome.txtVwTempFeels.text =
                    getString(R.string.likes) + " " + round(it.current.feels_like).toInt()
                        .toString() + "\u00b0"

                binding.centerHome.imgWeatherIcon.let {
                    Glide.with(it)
                        .load(pathImg)
                        .into(it)
                }

                mProgress.dismiss()

                if (PrefHelper.getIsFirst(requireContext())!!.equals(true)) {
                    showDialogAlart()
                }

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

            val address = addresses[0].getAddressLine(0)
            val splitAddress = address.split(",")
            val newAddress = splitAddress[0]
            Log.i("TAGaddress", "getCompleteAddress: " + address)
            val city = addresses[1]!!.locality
            val state: String = addresses[0]!!.getAdminArea()
            val splitState = state.split(" ")
            val newState = splitState[0]

            result = address
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
//            getPermission()
            PrefHelper.setEnableNotification(true, requireContext())
            setPeriodicWorkRequest()
            dialog.dismiss()
        }

        btnNo.setOnClickListener {
            PrefHelper.setEnableNotification(false, requireContext())
            dialog.dismiss()
        }

        PrefHelper.setIsFirst(false, requireContext())

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }


    private fun setPeriodicWorkRequest() {

        val constraints: Constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val periodicWorkRequest =
            PeriodicWorkRequest.Builder(AlarmWorker::class.java, 15, TimeUnit.MINUTES)
                .addTag("AlarmWorkerMANAGER_PeriodicWorkRequest")
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance(requireContext()).enqueue(periodicWorkRequest)
    }

    private fun onBackPressed() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
    }


}