package amhsn.weatherapp.ui.ui.favourite

import amhsn.weatherapp.R
import amhsn.weatherapp.adapter.FavouriteAdapter
import amhsn.weatherapp.databinding.FragmentFavouriteBinding
import amhsn.weatherapp.pojo.Favourite
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager


class FavouriteFragment : Fragment() ,FavouriteAdapter.OnItemClickListener{

    private lateinit var binding: FragmentFavouriteBinding
    private lateinit var adapter: FavouriteAdapter
    private lateinit var mView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView = view
        initRecyclerView()

        val lis = mutableListOf<Favourite>()
        lis.add(Favourite("Faisal", "Egypt", 30.0397, 31.2056))
        lis.add(Favourite("Faisal", "Egypt", 30.0397, 31.2056))
        lis.add(Favourite("Faisal", "Egypt", 30.0397, 31.2056))
        lis.add(Favourite("Faisal", "Egypt", 30.0397, 31.2056))

        adapter.setList(lis)
        adapter.setOnItemClickListener(this)

//        deleteItemBySwabbing();
//        adapter.setOnItemClickListener()
    }


    private fun initRecyclerView() {
        binding.favouriteContainer.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(requireContext())
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.favouriteContainer.layoutManager = mLayoutManager
        binding.favouriteContainer.itemAnimator = DefaultItemAnimator()
        adapter = FavouriteAdapter()
        binding.favouriteContainer.adapter = adapter
    }

    override fun onItemClick(position: Int) {
//        Log.i("makeText", "onBindViewHolder: " + position)
        Toast.makeText(context,"${position}",Toast.LENGTH_SHORT).show()
        Navigation.findNavController(mView).navigate(R.id.action_favouriteFragment_to_mapFragment)
    }


}