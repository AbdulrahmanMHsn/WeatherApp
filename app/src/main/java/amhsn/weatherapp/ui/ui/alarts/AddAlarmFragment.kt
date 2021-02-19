package amhsn.weatherapp.ui.ui.alarts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import amhsn.weatherapp.R
import amhsn.weatherapp.databinding.FragmentAddAlarmBinding
import amhsn.weatherapp.pojo.CustomAlarm
import amhsn.weatherapp.viewmodel.AlarmViewModel
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import java.util.*


class AddAlarmFragment : Fragment() {

    private lateinit var binding: FragmentAddAlarmBinding
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_alarm, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.doneFab.setOnClickListener {
            val customCalendar = Calendar.getInstance()
            customCalendar.set(
                binding.dateP.year,
                binding.dateP.month,
                binding.dateP.dayOfMonth,
                binding.timeP.hour,
                binding.timeP.minute,
                0
            )
            val customTime = customCalendar.timeInMillis
            val currentTime = System.currentTimeMillis()
            Log.i("customTime", "userInterface: " + customTime)
            if (customTime > currentTime) {
//                val data = Data.Builder().putInt(NOTIFICATION_ID, 0).build()
                val delay = customTime - currentTime
                val customAlarm = CustomAlarm()
                customAlarm.timestamp =customTime
                viewModel.insertAlarm(customAlarm)
                Navigation.findNavController(it).popBackStack()
            }
        }
    }


}