package amhsn.weatherapp.adapter

import amhsn.weatherapp.R
import amhsn.weatherapp.databinding.ItemAlarmBinding
import amhsn.weatherapp.pojo.CustomAlarm
import amhsn.weatherapp.utils.worker.NotifyWorker
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class AlarmAdapter(var listener: OnItemClickListener) :
    RecyclerView.Adapter<AlarmAdapter.AlarmHolder>() {

    private var arrayList: List<CustomAlarm> = ArrayList()
    private lateinit var bindingAdapter: ItemAlarmBinding
    private lateinit var view: View

    interface OnItemClickListener {
        fun onClickDelete(position: Int)
        fun onChangerListener(position: Int,checked:Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmHolder {

        bindingAdapter = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_alarm,
            parent,
            false
        )

        view = parent
        return AlarmHolder(
            bindingAdapter
        )
    }


    override fun getItemCount(): Int {
        return if (!arrayList.isEmpty()) {
            arrayList.size
        } else {
            0
        }
    }


    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: AlarmHolder, position: Int) {

        if (arrayList.isEmpty()) return

        val item = arrayList.get(position)

        val time = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date((item.timestamp)))
        val date = SimpleDateFormat("E dd MMM", Locale.ENGLISH).format(Date((item.timestamp)))


        holder.itemBinding.alarmTime.text = time
        holder.itemBinding.alarmDate.text = date
        holder.itemBinding.alarmSwitch.isChecked = item.isTurn

        holder.itemBinding.delete.setOnClickListener {
            if (position != RecyclerView.NO_POSITION) {
                listener.onClickDelete(position)
            }
        }

//        holder.itemBinding.alarmSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
//
//            if (isChecked) {
//                if (item.timestamp > System.currentTimeMillis()) {
//                    val delay = item.timestamp - System.currentTimeMillis()
//                    val inputTime = Data.Builder().putLong("time", item.timestamp).build()
//                    setOneTimeWorkRequest(delay, inputTime,buttonView.context)
//                } else {
//                    Toast.makeText(buttonView.context, "The Time is expired", Toast.LENGTH_SHORT)
//                        .show()
//                }
//            }
//
//        }

        holder.itemBinding.alarmSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (position != RecyclerView.NO_POSITION) {
                listener.onChangerListener(position,isChecked)
            }
        }

    }


    fun setList(list: List<CustomAlarm>) {
        arrayList = list
        notifyDataSetChanged()
    }


    fun getAlarmAtPosition(position: Int): CustomAlarm? {
        return arrayList.get(position)
    }

    /*
     * A function use to call WorkManager
     * */
    private fun setOneTimeWorkRequest(delay: Long, inputTime: Data, context: Context) {
        val workManager: WorkManager = WorkManager.getInstance(context)
        val uploadRequest = OneTimeWorkRequest.Builder(NotifyWorker::class.java)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputTime)
            .build()
        workManager.enqueue(uploadRequest)
    }

    class AlarmHolder(var itemBinding: ItemAlarmBinding) :
        RecyclerView.ViewHolder(itemBinding.root)


}