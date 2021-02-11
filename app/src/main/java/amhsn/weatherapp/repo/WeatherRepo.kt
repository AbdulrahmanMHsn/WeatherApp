package amhsn.weatherapp.repo

import amhsn.weatherapp.network.response.ResponseAPIWeather
import amhsn.weatherapp.utils.PrefHelper
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherRepo {

    val mutableListWeather = MutableLiveData<ResponseAPIWeather>()

    fun fetchRemoteDataSource(lat: Double, lon: Double,contex:Context): MutableLiveData<ResponseAPIWeather> {

        GlobalScope.launch {
            Dispatchers.IO
            
            try {
                val unit:String = PrefHelper.getUnitTemp(contex).toString()
                val response = RetrofitClient.getApiService(contex)
                    .getWeather(lat = lat, lon = lon,units = unit ,appid = "249932fc39de527f614b962d93598099").execute()

                // Check if response was successful.
                withContext(Dispatchers.Main) {

                    if (response.isSuccessful) {

                        mutableListWeather.value = response.body()

                        Log.i(Companion.TAG, "getDataApis: Response not successful ${mutableListWeather.value!!.lat}")
                    }else{
                        Log.i(Companion.TAG, "getDataApis: Response is not successful")
                    }
                }
            } catch (e: Exception) {
                Log.i(Companion.TAG, "getDataApis: " + e.message)
            }
        }

        return mutableListWeather
    }

    companion object {
        private const val TAG = "WeatherRepo"
    }


}