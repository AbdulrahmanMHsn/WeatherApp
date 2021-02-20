package amhsn.weatherapp.utils.worker

import amhsn.weatherapp.utils.PrefHelper
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.myapplication.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception


class AlarmWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        try {
            getResponseFromApi()
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }

    fun getResponseFromApi() {
        GlobalScope.launch(Dispatchers.IO) {

            val lang: String = PrefHelper.getLocalLanguage(applicationContext).toString()
            val response = RetrofitClient.getApiService(applicationContext).getWeather(
                PrefHelper.getLatitude(applicationContext)!!.toDouble(),
                PrefHelper.getLongitude(applicationContext)!!.toDouble(),
                lang
            )
                .execute()

            withContext(Dispatchers.Main) {

                if (response.isSuccessful) {

                } else {
                    Log.d("TAG", "getCurrentWeather: ${response.body()}")
                }
            }
        }
    }

}