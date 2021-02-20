package amhsn.weatherapp.adapter

import amhsn.weatherapp.R
import amhsn.weatherapp.databinding.ItemFavouriteBinding
import amhsn.weatherapp.pojo.Favourite
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

class FavouriteAdapter(var listener: OnItemClickListener) :
    RecyclerView.Adapter<FavouriteAdapter.FavouriteHolder>() {

    private var arrayList: List<Favourite> = ArrayList()
    private lateinit var bindingAdapter: ItemFavouriteBinding
    private lateinit var view: View
    private var mListener: OnItemClickListener = listener

    interface OnItemClickListener {
        fun onItemDeleteClick(position: Int)
        fun onItemClickListener(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteHolder {

        bindingAdapter = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_favourite,
            parent,
            false
        )

        view = parent
        return FavouriteHolder(
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
    override fun onBindViewHolder(holder: FavouriteHolder, position: Int) {

        if (arrayList.isEmpty()) return

        val item = arrayList.get(position)

        holder.itemBinding.itemFavTxtVwCity.text = item.city
        holder.itemBinding.itemFavTxtVwCountry.text = item.country

//        holder.itemBinding.layoutItemFav.setOnClickListener {
//            val bundle = Bundle()
//            bundle.putDouble("lat", item.lat)
//            bundle.putDouble("lon", item.lon)
//            findNavController(it).navigate(
//                R.id.action_favouriteFragment_to_favouriteDetailsFragment,
//                bundle
//            )
//        }

        holder.itemBinding.layoutItemFav.setOnClickListener {
            if (position != RecyclerView.NO_POSITION) {
                mListener.onItemClickListener(position)
            }
        }

        holder.itemBinding.imageDelete.setOnClickListener {
            if (position != RecyclerView.NO_POSITION) {
                mListener.onItemDeleteClick(position)
            }
        }

    }


    fun setList(list: List<Favourite>) {
        arrayList = list
        notifyDataSetChanged()
    }

    fun getFavouriteAtPosition(position: Int): Favourite? {
        return arrayList.get(position)
    }


    class FavouriteHolder(var itemBinding: ItemFavouriteBinding) :
        RecyclerView.ViewHolder(itemBinding.root)


}