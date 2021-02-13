package amhsn.weatherapp.adapter

import amhsn.weatherapp.R
import amhsn.weatherapp.databinding.ItemDailyBinding
import amhsn.weatherapp.databinding.ItemHourlyBinding
import amhsn.weatherapp.network.response.Daily
import amhsn.weatherapp.network.response.Hourly
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NextDayAdapter : RecyclerView.Adapter<NextDayAdapter.MovieHolder>() {

    private var arrayList: List<Daily> = ArrayList()
    private lateinit var bindingAdapter: ItemDailyBinding
    private var currentCalendar = Calendar.getInstance()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
        bindingAdapter = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_daily,
            parent,
            false
        )

        return MovieHolder(
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


    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onBindViewHolder(holder: MovieHolder, position: Int) {

        if (arrayList.isEmpty()) return

        val item = arrayList.get(position)

        val path = "http://openweathermap.org/img/w/${item.weather.get(0).icon}.png"


        holder.binding.hourlyImgIconWeather.let {
            Glide.with(it)
                .load(path)
                .into(it)
        }

//        if (currentCalendar.timeInMillis == (item.dt).toLong() * 1000) {
//            holder.binding.hourlyTxtVwTime.text = "Todat"
//        } else {

            val date =
                SimpleDateFormat("MMM dd", Locale.ENGLISH).format(Date((item.dt).toLong() * 1000))

            holder.binding.hourlyTxtVwTime.text = date
//        }

        holder.binding.hourlyTxtVwTemp.text = item.temp.day.toString()
        holder.binding.hourlyTxtVwDesc.text = item.weather.get(0).description
//        holder.binding.hourlyTxtVwDesc.text = item.weather.get(0).id.toString()

    }


    fun setList(list: List<Daily>) {
        arrayList = list
        notifyDataSetChanged()
    }


    class MovieHolder(var binding: ItemDailyBinding) : RecyclerView.ViewHolder(binding.root)


//    class MovieHolder(view: View) : RecyclerView.ViewHolder(view) {
//
//        val time = view.findViewById<TextView>(R.id.hourly_txtVw_time)
//
//    }

}