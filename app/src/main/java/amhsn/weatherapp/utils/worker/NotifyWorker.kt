package amhsn.weatherapp.utils.worker

import amhsn.weatherapp.DialogActivity
import amhsn.weatherapp.R
import amhsn.weatherapp.database.AlarmDao
import amhsn.weatherapp.database.AppDatabase
import amhsn.weatherapp.network.response.ResponseAPIWeather
import amhsn.weatherapp.utils.PrefHelper
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
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
import java.lang.Math.round


val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence =
    "Verbose WorkManager Notifications"
val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
    "Shows notifications whenever work starts"
val NOTIFICATION_TITLE: CharSequence = "WorkRequest Starting"
val CHANNEL_ID = "VERBOSE_NOTIFICATION"
val NOTIFICATION_ID = 1

class NotifyWorker(var context: Context, params: WorkerParameters) : Worker(context, params) {
    private var type: String? = ""
    var alarmDao: AlarmDao = AppDatabase.getDatabase(applicationContext).getAlarmDaoInstance()


    override fun doWork(): Result {
        try {

            val time = inputData.getLong("time", 0)

            type = inputData.getString("type")
            Log.i("timetype", "doWork: " + type)
            getResponseFromApi(time)

            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    fun getResponseFromApi(time: Long) {
        GlobalScope.launch(Dispatchers.IO) {

            val lang: String = PrefHelper.getLocalLanguage(applicationContext).toString()
            val response = RetrofitClient.getApiService(applicationContext).getWeather(
                PrefHelper.getLatitude(applicationContext)!!.toDouble(),
                PrefHelper.getLongitude(applicationContext)!!.toDouble(),
//                68.3963,
//                36.9419,
                lang
            ).execute()

            withContext(Dispatchers.Main) {

                if (response.isSuccessful) {
                    if (response.body()?.alerts == null) {
                        if (type.equals("notify")) {
                            makeStatusNotification(
                                "There is no an alert",
                                "Have a nice day",
                                applicationContext
                            )
                        } else {
                            val i = Intent(applicationContext, DialogActivity::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            applicationContext.startActivity(i)
                        }
                    } else {
                        for (i in response.body()!!.alerts!!.indices) {
//                            if (i % 2 != 0) {
                            if (time >= response.body()!!.alerts!![i].start * 1000 && time <= response.body()!!.alerts!![i].end * 1000) {
                                if (type.equals("notify")) {
                                    makeStatusNotification(
                                        response.body()!!.alerts!![i].event,
                                        response.body()!!.alerts!![i].description,
                                        applicationContext
                                    )
                                } else {
                                    val intent =
                                        Intent(applicationContext, DialogActivity::class.java)
                                    intent.putExtra(
                                        "sender_name",
                                        response.body()!!.alerts!![i].sender_name
                                    )
                                    intent.putExtra(
                                        "event",
                                        response.body()!!.alerts!![i].event
                                    )
                                    intent.putExtra(
                                        "description",
                                        response.body()!!.alerts!![i].description
                                    )
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    applicationContext.startActivity(intent)
                                }
                                break
                            }
//                            }
//                            else{
//                                if (type.equals("notify")) {
//                                    makeStatusNotification(
//                                        "There is no an alert",
//                                        "Have a nice day",
//                                        applicationContext
//                                    )
//                                } else {
//                                    val i = Intent(applicationContext, DialogActivity::class.java)
//                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                                    applicationContext.startActivity(i)
//                                }
//                            }
                        }
                    }

                    deleteAlarm(time)
                } else {
                    Log.d("TAG", "getCurrentWeather: ${response.body()}")
                }
            }
        }
    }


//    fun makeStatusNotification(body: String, title: String, context: Context) {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = VERBOSE_NOTIFICATION_CHANNEL_NAME
//            val description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            val channel = NotificationChannel(CHANNEL_ID, name, importance)
//            channel.description = description
//
//            // Add the channel
//            val notificationManager =
//                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
//
//            notificationManager?.createNotificationChannel(channel)
//        }
//
//        // Create the notification
//        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
//            .setContentTitle(body)
//            .setSmallIcon(R.mipmap.ic_launcher)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
//
//        // Show the notification
//        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
//    }

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
        NotificationManagerCompat.from(context).notify(1, builder.build())
    }

    fun deleteAlarm(id: Long) {
        GlobalScope.launch {
            Dispatchers.IO
            alarmDao.deleteAlarm(id)
        }
    }
}