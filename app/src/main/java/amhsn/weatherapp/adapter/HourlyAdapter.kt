package amhsn.weatherapp.adapter

import amhsn.weatherapp.R
import amhsn.weatherapp.databinding.ItemHourlyBinding
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView

class HourlyAdapter : RecyclerView.Adapter<HourlyAdapter.MovieHolder>() {

    private var arrayList: List<String> = ArrayList()
    private lateinit var bindingAdapter: ItemHourlyBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
        Log.i("adapter", "onBindViewHolder: ssss")

        bindingAdapter = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_hourly, parent, false);

        return MovieHolder(
            bindingAdapter
        )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: MovieHolder, position: Int) {
        Log.i("adapter", "onBindViewHolder: asdsadsadsa")
        if (arrayList.isEmpty()) {
            Log.i("adapter", "onBindViewHolder: ssss")
            return;
        }

//        var path:String = "https://image.tmdb.org/t/p/w500" + arrayList.get(position).backdrop_path;
//
//        Log.i("adapter", "onBindViewHolder: sssssssssssssssssssssss")
//        holder.photo?.let {
//            Glide.with(it)
//                .load(path)
//                .into(it)
//        }
//
//        holder.title!!.setText(arrayList.get(position).title)
//
//        holder.layout.setOnClickListener{
//
//            var bundle: Bundle = Bundle();
//            bundle.putString("PHOTO",path)
//            bundle.putString("TITLE",arrayList.get(position).title)
//
//            Navigation.findNavController(it).navigate(R.id.action_listFragment_to_itemFragment,bundle)
//
//        }

        bindingAdapter.hourlyTxtVwTime.text = "Abdo"

    }


    fun setList(movieList: List<String>){
        arrayList = movieList
        notifyDataSetChanged()
    }

    class MovieHolder(val binding: ItemHourlyBinding) : RecyclerView.ViewHolder(binding.root)


}