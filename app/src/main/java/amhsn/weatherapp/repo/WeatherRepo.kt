package amhsn.weatherapp.repo

import amhsn.weatherapp.database.AppDatabase
import amhsn.weatherapp.database.FavouriteDao
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

class WeatherRepo(val context: Context) {

    val mutableListWeather = MutableLiveData<ResponseAPIWeather>()
    var favouriteDao: FavouriteDao = AppDatabase.getDatabase(context).getFavouriteDaoInstance()


    fun fetchRemoteDataSource(lat: Double, lon: Double,contex:Context): MutableLiveData<ResponseAPIWeather> {

        GlobalScope.launch {
            Dispatchers.IO
            
            try {
                val unit:String = PrefHelper.getUnitTemp(contex).toString()
                val lang:String = PrefHelper.getLocalLanguage(contex).toString()
                val response = RetrofitClient.getApiService(contex)
                    .getWeather(lat = lat,lon =  lon,units = unit,lang = lang).execute()

                // Check if response was successful.
                withContext(Dispatchers.Main) {

                    if (response.isSuccessful) {

                        mutableListWeather.value = response.body()

                        Log.i(TAG, "getDataApis: Response is successful ${mutableListWeather.value!!.lat}")
                    }else{
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

    fun insertFavourite(favourite: Favourite) {
        GlobalScope.launch {
            Dispatchers.IO
            favouriteDao.insertFavourite(favourite)
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