package amhsn.weatherapp.viewmodel

import amhsn.weatherapp.network.response.ResponseAPIWeather
import amhsn.weatherapp.network.response.Weather
import amhsn.weatherapp.repo.WeatherRepo
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherViewModel : ViewModel() {

    private var movieRepo: WeatherRepo = WeatherRepo()
    lateinit var weather: LiveData<List<ResponseAPIWeather>>

    fun getRemoteDataSource(lat: Double, lon: Double,context: Context): MutableLiveData<ResponseAPIWeather> {
        return movieRepo.fetchRemoteDataSource(lat,lon,context)
    }

    companion object {
        private const val TAG = "WeatherViewModel"
    }
}