package amhsn.weatherapp.adapter

import amhsn.weatherapp.R
import amhsn.weatherapp.databinding.ItemHourlyBinding
import amhsn.weatherapp.network.response.Daily
import amhsn.weatherapp.network.response.Hourly
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HourlyAdapter : RecyclerView.Adapter<HourlyAdapter.MovieHolder>() {

    private var arrayList: List<Hourly> = ArrayList()
    private lateinit var bindingAdapter: ItemHourlyBinding
    private var currentCalendar = Calendar.getInstance()
    private lateinit var view: View


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
        view = parent
        bindingAdapter = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_hourly,
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


    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: MovieHolder, position: Int) {

        if (arrayList.isEmpty()) return

        val item = arrayList.get(position)
//
        val path = "http://openweathermap.org/img/w/${item.weather.get(0).icon}.png"


//
        holder.binding.hourlyImgIconWeather.let {
            Glide.with(it)
                .load(path)
                .into(it)
        }

        if (currentCalendar.timeInMillis >= (item.dt).toLong() * 1000) {
            holder.binding.hourlyTxtVwTime.text = "Now"
        } else {

            val date = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date((item.dt).toLong() * 1000))

            holder.binding.hourlyTxtVwTime.text = date
        }

        holder.binding.hourlyTxtVwTemp.text = item.temp.toString()
        holder.binding.hourlyTxtVwDesc.text = item.weather.get(0).description




    }


    fun setList(list: List<Hourly>) {
        arrayList = list
        notifyDataSetChanged()
    }



    class MovieHolder(var binding: ItemHourlyBinding) : RecyclerView.ViewHolder(binding.root)


//    class MovieHolder(view: View) : RecyclerView.ViewHolder(view) {
//
//        val time = view.findViewById<TextView>(R.id.hourly_txtVw_time)
//
//    }

}