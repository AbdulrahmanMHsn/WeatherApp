package amhsn.weatherapp.utils.worker

import amhsn.weatherapp.R
import amhsn.weatherapp.utils.PrefHelper
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.example.myapplication.network.RetrofitClient
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.concurrent.TimeUnit


class AlarmWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    private lateinit var inputTime: Data
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
//                68.3963,
//                36.9419,
                PrefHelper.getLatitude(applicationContext)!!.toDouble(),
                PrefHelper.getLongitude(applicationContext)!!.toDouble(),
                lang
            )
                .execute()

            withContext(Dispatchers.Main) {

                if (response.isSuccessful) {
                    if (response.body()?.alerts != null) {
                        for (i in response.body()!!.alerts!!) {
//                            Log.i(
//                                TAG,
//                                "getResponseFromApi: gggggggggggggggg" + i.start
//                            )
                            if (System.currentTimeMillis() >= i.start * 1000 && System.currentTimeMillis() <= i.end * 1000) {
//                            inputTime = Data.Builder().putString("event", i.event)
//                                .putString("description", i.description).build()

//                            if (i.start * 1000 >= System.currentTimeMillis()) {
//                                val delay = (i.start * 1000) - System.currentTimeMillis()
//                                Log.i(TAG, "getResponseFromApi: delay"+delay)
//                                setOneTimeWorkRequest(delay)
//                            }
//                                val intent = Intent(applicationContext, DialogActivity::class.java)
//                                intent.putExtra("sender_name",i.sender_name)
//                                intent.putExtra("event",i.event)
//                                intent.putExtra("description",i.description)
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                                applicationContext.startActivity(intent)
                                makeStatusNotification(
                                    i.event,
                                    i.description,
                                    applicationContext
                                )
                            }
                        }
                    } else {
                        Log.i(TAG, "getResponseFromApi: alerts is null")
                    }
                } else {
                    Log.d("TAG", "getCurrentWeather: ${response.body()}")
                }
            }
        }
    }

    companion object {
        private const val TAG = "AlarmWorker"
    }

    fun makeStatusNotification(
        body: String,
        title: String,
        context: Context
    ) {

        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val name = VERBOSE_NOTIFICATION_CHANNEL_NAME
            val description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description

            // Add the channel
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            notificationManager?.createNotificationChannel(channel)
        }


        // Create the notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(body)
            .setContentText(title)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)


        // Show the notification
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }

    /*
* A function use to call WorkManager
* */
    private fun setOneTimeWorkRequest(delay: Long) {
        val workManager: WorkManager = WorkManager.getInstance(applicationContext)
        val uploadRequest = OneTimeWorkRequest.Builder(NotifyWorker::class.java)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputTime)
            .addTag("OneTime_WorkRequest")
            .build()
        workManager.enqueue(uploadRequest)
    }

}