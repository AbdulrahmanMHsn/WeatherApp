package amhsn.weatherapp.ui.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import amhsn.weatherapp.R
import amhsn.weatherapp.utils.PrefHelper
import androidx.navigation.Navigation


class SplashFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(PrefHelper.getLatitude(requireContext()) != 0.0){
            Navigation.findNavController(view).navigate(R.id.action_splashFragment_to_home2)
        }else{
            Navigation.findNavController(view).navigate(R.id.action_splashFragment_to_locationServiceFragment)
        }
    }




}