package amhsn.weatherapp.ui.ui.alarts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import amhsn.weatherapp.R
import amhsn.weatherapp.databinding.FragmentAddAlarmBinding
import amhsn.weatherapp.pojo.CustomAlarm
import amhsn.weatherapp.utils.PrefHelper
import amhsn.weatherapp.utils.worker.NotifyWorker
import amhsn.weatherapp.viewmodel.AlarmViewModel
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.work.*
import java.util.*
import java.util.concurrent.TimeUnit


class AddAlarmFragment : Fragment() {

    private lateinit var idAlarm: String
    private val OVER_RELAY_PERMISSION_CODE = 10001
    // declaration vars
    private lateinit var inputTime: Data
    private lateinit var binding: FragmentAddAlarmBinding
    private lateinit var viewModel: AlarmViewModel
    private var type = "notify"


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


        binding.radioGroupType.setOnCheckedChangeListener { group, checkedId ->

            if (checkedId == R.id.radioNotify) {
                type = "notify"
            } else {
                type = "alarm"
            }
        }

//        binding.radioGroupSound.setOnCheckedChangeListener { group, checkedId ->
//
//            if(checkedId == R.id.radioDefault){
//                sound = "default"
//            }else{
//                sound = "noSound"
//            }
//        }

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

            if(type.equals("alarm")){
                getPermission()
                if (customTime > currentTime) {
                    val delay = customTime - currentTime
                    inputTime =
                        Data.Builder().putLong("time", customTime).putString("type", type).build()
                    setOneTimeWorkRequest(delay)
                    val customAlarm = CustomAlarm()
                    customAlarm.timestamp = customTime
                    customAlarm.id = customTime
                    customAlarm.isTurn = true
                    customAlarm.idAlarm = idAlarm
                    viewModel.insertAlarm(customAlarm)
                    Navigation.findNavController(it).popBackStack()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.date_expired),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                if (customTime > currentTime) {
                    val delay = customTime - currentTime
                    inputTime =
                        Data.Builder().putLong("time", customTime).putString("type", type).build()
                    setOneTimeWorkRequest(delay)
                    val customAlarm = CustomAlarm()
                    customAlarm.timestamp = customTime
                    customAlarm.id = customTime
                    customAlarm.isTurn = true
                    customAlarm.idAlarm = idAlarm
                    viewModel.insertAlarm(customAlarm)
                    Navigation.findNavController(it).popBackStack()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.date_expired),
                        Toast.LENGTH_SHORT
                    ).show()
                }
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
            .addTag("OneTime_WorkRequest")
            .build()
        idAlarm = uploadRequest.id.toString()
        workManager.enqueue(uploadRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVER_RELAY_PERMISSION_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this.requireContext())) {
                Toast.makeText(this.requireContext(), "Permission denied by the user.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this.requireContext())) {
            android.app.AlertDialog.Builder(this.requireContext())
                .setMessage("Allow weather wizard to display over other apps.")
                .setPositiveButton("Yes") { dialog, which ->
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION
                    )
                    startActivityForResult(
                        intent,
                        OVER_RELAY_PERMISSION_CODE
                    )
                }
                .setNegativeButton("No") { dialog, which ->
                    Toast.makeText(
                        this.requireContext(),
                        "The Application must have this permission for the alert functionality.",
                        Toast.LENGTH_SHORT
                    ).show()
                }.create().show()
        }
    }

}