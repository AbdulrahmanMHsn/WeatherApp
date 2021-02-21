package amhsn.weatherapp.database

import amhsn.weatherapp.network.response.ResponseAPIWeather
import amhsn.weatherapp.pojo.CustomAlarm
import amhsn.weatherapp.pojo.Favourite
import android.content.Context
import androidx.room.*

@Database(entities = [Favourite::class, CustomAlarm::class,ResponseAPIWeather::class], version = 2)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getFavouriteDaoInstance(): FavouriteDao
    abstract fun getAlarmDaoInstance(): AlarmDao
    abstract fun getWeatherDaoInstance(): WeatherDao

    companion object {
        var instance: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            instance ?: synchronized(this)
            {
                val roomInstance =
                    Room.databaseBuilder(context, AppDatabase::class.java, "WeatherDB").build()
                instance = roomInstance
            }
            return instance!!
        }
    }

}