package amhsn.weatherapp.ui.ui.favourite

import amhsn.weatherapp.R
import amhsn.weatherapp.adapter.FavouriteAdapter
import amhsn.weatherapp.databinding.FragmentFavouriteBinding
import amhsn.weatherapp.viewmodel.WeatherViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager


class FavouriteFragment : Fragment(), FavouriteAdapter.OnItemClickListener {

    private lateinit var binding: FragmentFavouriteBinding
    private lateinit var adapter: FavouriteAdapter
    private lateinit var mView: View
    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
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

        viewModel.getLocalDataSource().observe(viewLifecycleOwner, Observer {
            adapter.setList(it)
        })


        binding.customSearch.linearLayout.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_favouriteFragment_to_mapFragment)
        }
    }

    private fun initRecyclerView() {
        binding.favouriteContainer.setHasFixedSize(true)
        binding.favouriteContainer.layoutManager = LinearLayoutManager(requireActivity())
        adapter = FavouriteAdapter(this)
        binding.favouriteContainer.adapter = adapter
    }

    override fun onItemDeleteClick(position: Int) {
        adapter.getFavouriteAtPosition(position)?.let { viewModel.deleteFavourite(it) }
        Toast.makeText(getContext(), position.toString(), Toast.LENGTH_SHORT).show()
    }

}