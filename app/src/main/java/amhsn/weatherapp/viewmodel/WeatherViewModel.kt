package amhsn.weatherapp.viewmodel

import amhsn.weatherapp.network.response.ResponseAPIWeather
import amhsn.weatherapp.repo.WeatherRepo
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherViewModel : ViewModel() {

    private var movieRepo: WeatherRepo = WeatherRepo()
    lateinit var weather: LiveData<List<ResponseAPIWeather>>

    fun getRemoteDataSource(lat: Double, lon: Double): MutableLiveData<ResponseAPIWeather> {
        return movieRepo.fetchRemoteDataSource(lat,lon)
    }

    companion object {
        private const val TAG = "WeatherViewModel"
    }
}