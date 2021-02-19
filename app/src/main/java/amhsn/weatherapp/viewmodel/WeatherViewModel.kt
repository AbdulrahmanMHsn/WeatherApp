package amhsn.weatherapp.viewmodel

import amhsn.weatherapp.network.response.ResponseAPIWeather
import amhsn.weatherapp.network.response.Weather
import amhsn.weatherapp.pojo.Favourite
import amhsn.weatherapp.repo.WeatherRepo
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private var weatherRepo: WeatherRepo = WeatherRepo(application)

    fun getRemoteDataSource(lat: Double, lon: Double,context: Context): MutableLiveData<ResponseAPIWeather> {
        return weatherRepo.fetchRemoteDataSource(lat,lon,context)
    }

    fun getLocalDataSource(): LiveData<List<Favourite>> {
        return weatherRepo.getLocalDataSource()
    }

    fun insertFavourite(favourite: Favourite) {
        weatherRepo.insertFavourite(favourite)
    }


    fun deleteFavourite(favourite: Favourite) {
        weatherRepo.deleteFavourite(favourite)
    }

    companion object {
        private const val TAG = "WeatherViewModel"
    }
}