package amhsn.weatherapp.ui.ui.alarts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import amhsn.weatherapp.R
import amhsn.weatherapp.adapter.AlarmAdapter
import amhsn.weatherapp.databinding.FragmentAlarmsBinding
import amhsn.weatherapp.pojo.CustomAlarm
import amhsn.weatherapp.utils.worker.NotifyWorker
import amhsn.weatherapp.viewmodel.AlarmViewModel
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.util.*
import java.util.concurrent.TimeUnit


class AlarmsFragment : Fragment(), AlarmAdapter.OnItemClickListener {

    private lateinit var idAlarm: String
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
        adapter = AlarmAdapter(this)
        binding.containerAlarm.adapter = adapter
    }


    override fun onClickDelete(position: Int) {
        val item = adapter.getAlarmAtPosition(position)
//        adapter.getAlarmAtPosition(position)?.let { viewModel.deleteAlarm(it) }
        viewModel.deleteAlarm(item!!.timestamp)
        WorkManager.getInstance(requireContext()).cancelWorkById((UUID.fromString(item.idAlarm)))
    }


    override fun onChangerListener(position: Int, checked: Boolean) {
        val item = adapter.getAlarmAtPosition(position)
        if (checked) {
            if (item!!.timestamp > System.currentTimeMillis()) {
                val delay = item.timestamp - System.currentTimeMillis()
                val inputTime = Data.Builder().putLong("time", item.timestamp).putLong("id",item.id).build()
                setOneTimeWorkRequest(delay, inputTime)
                val obj = CustomAlarm()
                obj.id = item.id
                obj.idAlarm = idAlarm
                obj.timestamp = item.timestamp
                obj.isTurn = true
                viewModel.updateAlarm(obj)
            } else {
                Toast.makeText(context, getString(R.string.alarm_expired), Toast.LENGTH_SHORT).show()
                adapter.notifyDataSetChanged()
            }
        } else {
            val obj = CustomAlarm()
            obj.id = item!!.id
            obj.timestamp = item.timestamp
            obj.isTurn = false
            obj.idAlarm = item.idAlarm
            viewModel.updateAlarm(obj)
            WorkManager.getInstance(requireContext()).cancelWorkById((UUID.fromString(item.idAlarm)))
            Toast.makeText(context, getString(R.string.cancel_alarm), Toast.LENGTH_SHORT).show()
        }
    }


    /*
     * A function use to call WorkManager
     * */
    private fun setOneTimeWorkRequest(delay: Long, inputTime: Data) {
        val workManager: WorkManager = WorkManager.getInstance(requireContext())
        val uploadRequest = OneTimeWorkRequest.Builder(NotifyWorker::class.java)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputTime)
            .build()
        idAlarm = uploadRequest.id.toString()
        workManager.enqueue(uploadRequest)
    }


}