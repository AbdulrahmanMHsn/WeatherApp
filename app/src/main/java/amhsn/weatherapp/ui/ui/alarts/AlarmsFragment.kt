package amhsn.weatherapp.ui.ui.alarts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import amhsn.weatherapp.R
import amhsn.weatherapp.adapter.AlarmAdapter
import amhsn.weatherapp.adapter.FavouriteAdapter
import amhsn.weatherapp.databinding.FragmentAlarmsBinding
import amhsn.weatherapp.viewmodel.AlarmViewModel
import amhsn.weatherapp.viewmodel.WeatherViewModel
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager

class AlarmsFragment : Fragment() {

    private lateinit var adapter: AlarmAdapter
    private lateinit var binding: FragmentAlarmsBinding
    private lateinit var viewModel: AlarmViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AlarmViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alarms, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        viewModel.getAlarmLocalDataSource().observe(viewLifecycleOwner, Observer {
            adapter.setList(it)
        })

        binding.fabBtnAlarm.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_alarmsFragment_to_addAlarmFragment)
        }

    }

    private fun initRecyclerView() {
        binding.containerAlarm.setHasFixedSize(true)
        binding.containerAlarm.layoutManager = LinearLayoutManager(requireActivity())
        adapter = AlarmAdapter()
        binding.containerAlarm.adapter = adapter
    }


}