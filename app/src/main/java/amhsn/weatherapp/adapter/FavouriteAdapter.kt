package amhsn.weatherapp.adapter

import amhsn.weatherapp.R
import amhsn.weatherapp.databinding.ItemFavouriteBinding
import amhsn.weatherapp.pojo.Favourite
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView

class FavouriteAdapter : RecyclerView.Adapter<FavouriteAdapter.FavouriteHolder>() {

    private var arrayList: List<Favourite> = ArrayList()
    private lateinit var bindingAdapter: ItemFavouriteBinding
    private lateinit var mListener: OnItemClickListener


    interface OnItemClickListener {
        fun onItemClick(position: Int)
//        fun onItemChecked(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteHolder {
        bindingAdapter = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_favourite,
            parent,
            false
        )

        return FavouriteHolder(
            bindingAdapter,mListener
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
//
//        holder.binding.itemFavTxtVwCity.setOnClickListener {
////            Toast.makeText(it, "${position}", Toast.LENGTH_SHORT).show()
//            Log.i("makeText", "onBindViewHolder: "+position)
////            Navigation.findNavController(it).navigate(R.id.action_favouriteFragment_to_mapFragment)
//
//        }

//        holder.binding.itemFavTxtVwCity.setOnClickListener({ view ->
//            if (mListener != null) {
//                val position: Int = holder.getAdapterPosition()
//                if (position != RecyclerView.NO_POSITION) {
//                    mListener.onItemClick(position)
//                }
//            }
//        })

//        holder.binding.itemFavTxtVwCity.setOnClickListener({ view ->
//            if (mListener != null) {
//                if (position != RecyclerView.NO_POSITION) {
//                    mListener!!.onItemClick(position)
//                }
//            }
//        })

    }


    fun setList(list: List<Favourite>) {
        arrayList = list
        notifyDataSetChanged()
    }


    class FavouriteHolder(var binding: ItemFavouriteBinding,var listener: OnItemClickListener) : RecyclerView.ViewHolder(binding.root)


}