package amhsn.weatherapp.ui.ui.alarts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import amhsn.weatherapp.R
import amhsn.weatherapp.databinding.FragmentAddAlarmBinding
import amhsn.weatherapp.pojo.CustomAlarm
import amhsn.weatherapp.utils.worker.NotifyWorker
import amhsn.weatherapp.viewmodel.AlarmViewModel
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.work.*
import java.util.*
import java.util.concurrent.TimeUnit


class AddAlarmFragment : Fragment() {

    private lateinit var idAlarm: String

    // declaration vars
    private lateinit var inputTime: Data
    private lateinit var binding: FragmentAddAlarmBinding
    private lateinit var viewModel: AlarmViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // create an object AlarmViewModel
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

            if (customTime > currentTime) {
                val delay = customTime - currentTime
                inputTime = Data.Builder().putLong("time", customTime).build()
                setOneTimeWorkRequest(delay)
                val customAlarm = CustomAlarm()
                customAlarm.timestamp = customTime
                customAlarm.isTurn = true
                customAlarm.idAlarm = idAlarm
                viewModel.insertAlarm(customAlarm)
                Navigation.findNavController(it).popBackStack()
            }
        }
    }


    /*
    * A function use to call WorkManager
    * */
    private fun setOneTimeWorkRequest(delay: Long) {
        val workManager: WorkManager = WorkManager.getInstance(requireContext())
        val uploadRequest = OneTimeWorkRequest.Builder(NotifyWorker::class.java)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputTime)
            .build()
        idAlarm = uploadRequest.id.toString()
        workManager.enqueue(uploadRequest)
    }


}