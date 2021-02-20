package amhsn.weatherapp.utils.worker

import amhsn.weatherapp.R
import amhsn.weatherapp.database.AlarmDao
import amhsn.weatherapp.database.AppDatabase
import amhsn.weatherapp.pojo.CustomAlarm
import amhsn.weatherapp.utils.PrefHelper
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.myapplication.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception


val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence =
    "Verbose WorkManager Notifications"
val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
    "Shows notifications whenever work starts"
val NOTIFICATION_TITLE: CharSequence = "WorkRequest Starting"
val CHANNEL_ID = "VERBOSE_NOTIFICATION"
val NOTIFICATION_ID = 1

class NotifyWorker(var context: Context, params: WorkerParameters) : Worker(context, params) {
//    var alarmDao: AlarmDao = AppDatabase.getDatabase(applicationContext).getAlarmDaoInstance()

    override fun doWork(): Result {
        try {
            Log.i("doWork", "doWork: ")
            val time = inputData.getLong("time", 0)

            getResponseFromApi(time)

            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }

    fun getResponseFromApi(time: Long) {
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
                    outerLoop@ for (i in response.body()!!.hourly) {
                        for (j in response.body()!!.hourly) {
                            if (time <= i.dt * 1000 && time >= j.dt * 1000) {
                                makeStatusNotification(
                                    response.body()!!.current.weather[0].description,
                                    response.body()!!.current.temp.toString(),
                                    applicationContext
                                )

                                break@outerLoop
                            }
                        }
                    }
//                    val alarm = CustomAlarm()
//                    alarm.id = time
//                    deleteAlarm(alarm)
                } else {
                    Log.d("TAG", "getCurrentWeather: ${response.body()}")
                }
            }
        }
    }

    fun makeStatusNotification(body: String, title: String, context: Context) {

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
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))

        // Show the notification
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }

//    fun deleteAlarm(alarm: CustomAlarm) {
//        GlobalScope.launch {
//            Dispatchers.IO
//            alarmDao.deleteAlarm(alarm)
//        }
//    }
}