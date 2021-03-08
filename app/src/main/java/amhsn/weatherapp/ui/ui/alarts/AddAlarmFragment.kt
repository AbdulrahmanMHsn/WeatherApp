package amhsn.weatherapp.ui.ui.alarts

import amhsn.weatherapp.R
import amhsn.weatherapp.databinding.FragmentAddAlarmBinding
import amhsn.weatherapp.pojo.CustomAlarm
import amhsn.weatherapp.utils.worker.NotifyWorker
import amhsn.weatherapp.viewmodel.AlarmViewModel
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.work.*
import java.util.*
import java.util.concurrent.TimeUnit


@Suppress("DEPRECATION")
class AddAlarmFragment : Fragment() {

    private lateinit var idAlarm: String
    private val OVER_RELAY_PERMISSION_CODE = 10001

    // declaration vars
    private lateinit var inputTime: Data
    private lateinit var binding: FragmentAddAlarmBinding
    private lateinit var viewModel: AlarmViewModel
    private lateinit var mView: View
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
        mView = container!!.rootView
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.radioGroupType.setOnCheckedChangeListener { group, checkedId ->

            if (checkedId == R.id.radioNotify) {
                type = "notify"
            } else {
                type = "alarm"
                getPermission(requireContext())
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

            if (type.equals("alarm")) {

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
//                    getPermission()
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
        val constraints: Constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val workManager: WorkManager = WorkManager.getInstance(requireContext())
        val uploadRequest = OneTimeWorkRequest.Builder(NotifyWorker::class.java)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputTime)
            .setConstraints(constraints)
            .addTag("OneTime_WorkRequest")
            .build()
        idAlarm = uploadRequest.id.toString()
        workManager.enqueue(uploadRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVER_RELAY_PERMISSION_CODE) {
            Toast.makeText(
                this.requireContext(),
                "Permission denied by the user.",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun onBackPressed() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Navigation.findNavController(mView).popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
    }


//    private fun getPermission() {
//        if (!Settings.canDrawOverlays(this.requireContext())) {
//         AlertDialog.Builder(this.requireContext())
//                .setMessage("Allow weather wizard to display over other apps.")
//                .setPositiveButton("Yes") { dialog, which ->
//
//                }
//                .setNegativeButton("No") { dialog, which ->
//                    Toast.makeText(
//                       requireContext(),
//                        "The Application must have this permission for the alert functionality.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }.create().show()
//        }
//    }


    fun getPermission(context: Context) {
        if (!Settings.canDrawOverlays(this.requireContext())) {
            val builder1 = android.app.AlertDialog.Builder(context)
            builder1.setTitle("Allow weather wizard to display over other apps.")
            builder1.setCancelable(false)
            builder1.setPositiveButton(
                "Ok"
            ) { dialog, which ->
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + requireActivity().applicationContext.packageName)
                )
                requireActivity().startActivityForResult(
                    intent,
                    OVER_RELAY_PERMISSION_CODE
                )
            }
            builder1.setNegativeButton(
                "CANCEL"
            ) { dialog, which ->
                dialog.cancel()
            }

            val dialog = builder1.create()
            dialog.show()
        }
    }

}