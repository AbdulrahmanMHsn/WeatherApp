package amhsn.weatherapp.adapter

import amhsn.weatherapp.R
import amhsn.weatherapp.databinding.ItemAlarmBinding
import amhsn.weatherapp.databinding.ItemFavouriteBinding
import amhsn.weatherapp.pojo.CustomAlarm
import amhsn.weatherapp.pojo.Favourite
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AlarmAdapter() :
    RecyclerView.Adapter<AlarmAdapter.AlarmHolder>() {

    private var arrayList: List<CustomAlarm> = ArrayList()
    private lateinit var bindingAdapter: ItemAlarmBinding
    private lateinit var view: View
//    private var mListener: OnItemClickListener = listener
//
//    interface OnItemClickListener {
//        fun onItemDeleteClick(position: Int)
//    }

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

    }


    fun setList(list: List<CustomAlarm>) {
        arrayList = list
        notifyDataSetChanged()
    }

    fun getAlarmAtPosition(position: Int): CustomAlarm? {
        return arrayList.get(position)
    }


    class AlarmHolder(var itemBinding: ItemAlarmBinding) :
        RecyclerView.ViewHolder(itemBinding.root)


}