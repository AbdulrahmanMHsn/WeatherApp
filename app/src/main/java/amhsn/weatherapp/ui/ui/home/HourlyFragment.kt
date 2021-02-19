package amhsn.weatherapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import amhsn.weatherapp.R
import amhsn.weatherapp.adapter.HourlyAdapter
import amhsn.weatherapp.databinding.FragmentHourlyBinding
import amhsn.weatherapp.network.response.Hourly
import amhsn.weatherapp.viewmodel.WeatherViewModel
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager


class HourlyFragment() : Fragment() {

    private lateinit var binding: FragmentHourlyBinding
    private lateinit var adapter: HourlyAdapter
    private lateinit var viewModel: WeatherViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        getRemoteDataSource()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_hourly, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()



//        val list: List<String> = mutableListOf(
//            "asdas",
//            "asdsa",
//            "sadsa",
//            "sadsa",
//            "sadsa",
//            "sadsa",
//            "sadsa",
//            "sadsa",        "sadsa",        "sadsa", "sadsa",
//            "sadsa"
//        )


    }


    private fun initRecyclerView() {
        binding.hourlyContainer.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(requireContext())
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.hourlyContainer.layoutManager = mLayoutManager
        binding.hourlyContainer.itemAnimator = DefaultItemAnimator()
        adapter = HourlyAdapter()
        binding.hourlyContainer.adapter = adapter
//        adapter.setList(list)
    }

    fun getRemoteDataSource() {
        viewModel.getRemoteDataSource(30.052966803697707, 31.2111345601925,requireContext())
            .observe(requireActivity(), Observer {
                adapter.setList(it.hourly)
            })
    }


}