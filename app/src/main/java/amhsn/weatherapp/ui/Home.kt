package amhsn.weatherapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import amhsn.weatherapp.R
import amhsn.weatherapp.adapter.ViewPagerAdapter
import amhsn.weatherapp.databinding.FragmentHomeBinding
import amhsn.weatherapp.network.response.ResponseAPIWeather
import amhsn.weatherapp.viewmodel.WeatherViewModel
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.myapplication.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Home : Fragment() {

    private var moviesList: List<ResponseAPIWeather> = ArrayList()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        getRemoteDataSource()



        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setAdapters()



    }

    private fun setAdapters() {
        val adapter = ViewPagerAdapter(requireActivity().supportFragmentManager)
        adapter.addFragment(HourlyFragment(), "Today")
        adapter.addFragment(NextDays(), "This Week")
        binding.bottomHome.apply {
            pager.adapter = adapter
            tabLayout.setupWithViewPager(pager)

        }
    }


    fun getRemoteDataSource() {
        viewModel.getRemoteDataSource(30.008537, 31.170177).observe(viewLifecycleOwner, Observer {
            binding.centerHome.viewModelCurrent = it
        })
    }


    companion object {
        private const val TAG = "Home"
    }


}