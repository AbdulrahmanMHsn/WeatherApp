package amhsn.weatherapp.repo

import amhsn.weatherapp.database.AppDatabase
import amhsn.weatherapp.database.FavouriteDao
import amhsn.weatherapp.database.WeatherDao
import amhsn.weatherapp.network.response.ResponseAPIWeather
import amhsn.weatherapp.pojo.Favourite
import amhsn.weatherapp.utils.PrefHelper
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class WeatherRepo(val context: Context) {

    val mutableListWeather = MutableLiveData<ResponseAPIWeather>()
    var favouriteDao: FavouriteDao = AppDatabase.getDatabase(context).getFavouriteDaoInstance()
    var weatherDao: WeatherDao = AppDatabase.getDatabase(context).getWeatherDaoInstance()

    fun fetchRemoteDataSource(
        lat: Double,
        lon: Double,
        contex: Context
    ): MutableLiveData<ResponseAPIWeather> {


        GlobalScope.launch {
            Dispatchers.IO

            try {
                val unit: String = PrefHelper.getUnitTemp(contex).toString()
                val lang: String = PrefHelper.getLocalLanguage(contex).toString()
                val response = RetrofitClient.getApiService(contex)
                    .getWeather(lat = lat, lon = lon, units = unit, lang = lang).execute()

                // Check if response was successful.
                withContext(Dispatchers.Main) {

                    if (response.isSuccessful) {

                        mutableListWeather.value = response.body()


                        response.body()?.let {
                                val weather = ResponseAPIWeather(
                                    1,
                                    it.lat,
                                    it.lon,
                                    it.timezone,
                                    it.timezone_offset,
                                    it.current,
                                    it.hourly,
                                    it.daily,
                                    it.alerts
                                )
                                insertWeather(weather)
                            // lastUpdate
                            val currentCalendar = Calendar.getInstance()
                            val time = currentCalendar.timeInMillis
                            PrefHelper.setLastUpdate(time,context)
                        }

                        Log.i(TAG, "getDataApis: Response is successful ${mutableListWeather.value!!.lat}")
                    } else {
                        Log.i(TAG, "getDataApis: Response is not successful")
                    }
                }
            } catch (e: Exception) {
                Log.i(TAG, "getDataApis: " + e.message)
            }
        }

        return mutableListWeather
    }


    fun getLocalDataSource(): LiveData<List<Favourite>> {
        return favouriteDao.getAllFavourite()
    }


    fun getWeatherLocalDataSource(): LiveData<ResponseAPIWeather> {
        return weatherDao.getAllWeather()
    }


    fun insertFavourite(favourite: Favourite) {
        GlobalScope.launch {
            Dispatchers.IO
            favouriteDao.insertFavourite(favourite)
        }
    }


    fun insertWeather(weather: ResponseAPIWeather) {
        GlobalScope.launch {
            Dispatchers.IO
            weatherDao.insertWeather(weather)
        }
    }


    fun updateWeather(weather: ResponseAPIWeather) {
        GlobalScope.launch {
            Dispatchers.IO
            weatherDao.updateWeather(weather)
        }
    }


    fun deleteAllWeather() {
        GlobalScope.launch {
            Dispatchers.IO
            weatherDao.deleteAllWeather()
        }
    }


    fun deleteFavourite(favourite: Favourite) {
        GlobalScope.launch {
            Dispatchers.IO
            favouriteDao.deleteFavourite(favourite)
        }
    }

    companion object {
        private const val TAG = "WeatherRepo"
    }

}