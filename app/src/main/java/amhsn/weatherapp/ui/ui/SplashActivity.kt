package amhsn.weatherapp.ui.ui

import amhsn.weatherapp.R
import amhsn.weatherapp.utils.PrefHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.Navigation

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        @Suppress("DEPRECATION")
        Handler().postDelayed({
            Log.i("TAG", "onViewCreated: "+ PrefHelper.getLatitude(this)!!)
            if(PrefHelper.getLatitude(this) != 0.0){
//            findNavController().navigate(R.id.action_splashFragment_to_home2)
            }else{
//                Navigation.findNavController(view).navigate(R.id.splash_to_locationService)
            }
        }, 3000)
    }
}